package com.m3.skinnyrest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.m3.skinnyrest.annotations.Path;
import com.m3.skinnyrest.rest.RestHandlerDetail;
import com.m3.skinnyrest.rest.RestHandlerDetail.RestParamType;
import com.m3.skinnyrest.rest.RestResourceDetail;
import com.m3.skinnyrest.rest.RestResponseCode;
import com.m3.skinnyrest.rest.RestUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public interface SkinnyResource {
    /**
     * Extracts parameters from a REST body.
     * Extracts query parameters
     * Renders a REST response body
     * 
     */
    default void handleResource(HttpExchange exchange) {
        URI requestURI = exchange.getRequestURI();
        String mthd = exchange.getRequestMethod();
        Headers hdrs = exchange.getRequestHeaders();
        String path = null;
        if (!requestURI.isAbsolute() || !requestURI.isOpaque()) {
            path = requestURI.getPath();
            path = (path != null && !path.isBlank()) ? path.toLowerCase() : "";
        }
        if (path == null || path.isBlank()) {
            RestUtil.formErrorResponse(exchange, RestResponseCode.BAD_REQUEST, 
                    "A non-blank base path is a must for every resource", getLogger());
            return;
        }
        Annotation[] anns = getClass().getAnnotations();
        String basep = null;
        for (Annotation ann : anns) {
            if (ann instanceof Path) { // @Path exists
                Path annpth = (Path)ann;
                basep = annpth.value();
            }
        }
        if (basep == null || basep.isBlank()) {
            RestUtil.formErrorResponse(exchange, RestResponseCode.BAD_REQUEST, 
                    "A non-blank base path is a must for every resource", getLogger());
            return;
        }
        int pthix = path.indexOf(basep);
        if (pthix < 0) {
            RestUtil.formErrorResponse(exchange, RestResponseCode.BAD_REQUEST, 
                    "This resource class only handles requests with its base path", getLogger());
            return;
        }
        pthix += basep.length();
        String mthpath = path.substring(pthix);
        RestResourceDetail rrd = getResourceDetail();
        List<RestHandlerDetail> handlerdetails = rrd.findMatchingHandlers(mthpath, mthd);
        if (handlerdetails == null || handlerdetails.isEmpty()) {
            RestUtil.formErrorResponse(exchange, RestResponseCode.NOT_ACCEPTABLE, 
                    "Unsupported path and method combination", getLogger());
            return;
        }
        List<String> contentTypeLst = null;
        String contentType = null;
        String boundary = null;
        if (hdrs.isEmpty() || !hdrs.containsKey(RestUtil.HEADER_CONTENT_TYPE)) {
            if (!"GET".equalsIgnoreCase(mthd) && !"DELETE".equalsIgnoreCase(mthd)) {
                RestUtil.formErrorResponse(exchange, RestResponseCode.BAD_REQUEST, 
                        "This kind of request must contain a Content-Type Header with at least a Content-Type", 
                        getLogger());
                return;
            }
        } else {
            contentTypeLst = hdrs.get(RestUtil.HEADER_CONTENT_TYPE);
        }
        if (contentTypeLst != null && !contentTypeLst.isEmpty()) {
            contentType = contentTypeLst.get(0); // first element must be the real content type
        }
        String reqdata = null;
        // At this time we do not support resolution via ContentType and AcceptType
        // so just take the first Handler
        RestHandlerDetail handlerdetail = handlerdetails.get(0);
        Map<String, String> formParams = null;
        if (handlerdetail.hasParameterOfType(RestParamType.FORM)) {
            if (contentTypeLst != null && !contentTypeLst.isEmpty()) {
                boundary = contentTypeLst.stream()
                        .filter(cts -> "boundary".equalsIgnoreCase(cts))
                        .findAny().orElse(null);
            }
            if (contentTypeLst == null || contentTypeLst.isEmpty() || contentType == null || 
                    !RestUtil.CONTENT_TYPE_MULTIPART_FORM.equalsIgnoreCase(contentType) || 
                    boundary == null || boundary.isBlank()) {
                RestUtil.formErrorResponse(exchange, RestResponseCode.BAD_REQUEST, 
                        "Methods with FORM parameters must have Content-Type Header of type multipart form body", 
                        getLogger());
                return;
            }
            formParams = new HashMap<String, String>();
            try (InputStream is = exchange.getRequestBody()) {
            	reqdata = RestUtil.readAllFormDataParams(is, boundary, formParams);
            } catch (IOException ioe1) {
                RestUtil.formErrorResponse(exchange, RestResponseCode.BAD_REQUEST, 
                        "Error extracting parameters from multipart form body", getLogger());
                return;
            }
        }
        String qrystr = null;
        pthix = path.indexOf("?");
        if (pthix > 0) {
            qrystr = path.substring(pthix+1);
        }
        Map<String, String> qryparams = null;
        if (qrystr != null && !qrystr.isBlank()) {
            try {
                qryparams = RestUtil.parseUrlQuery(qrystr);
            } catch (UnsupportedEncodingException e) {
                RestUtil.formErrorResponse(exchange, RestResponseCode.BAD_REQUEST, 
                        "Error extracting query parameters", getLogger());
                return;
            }
        }
        // TODO Extract Accept-Type and pass in
        // TODO Extract OAuth2 header fields and pass in
        // TODO extract remaining headers into a header map and pass in
        RestEntity result = null;
        try {
            result = callRealMethod(handlerdetail, mthd, mthpath, formParams, qryparams, reqdata, contentType);
        } catch (Throwable t) {
        	RestResponseCode httpstatus = null;
        	String errmessage = null;
            if (t instanceof IllegalArgumentException) {
                httpstatus = RestResponseCode.BAD_REQUEST;
                errmessage = "Missing or bad (or unusable) parameter";
                if (t.getCause() != null && !(t.getCause() instanceof IllegalArgumentException)) {
                    errmessage = errmessage + ". Underlying cause: [" + t.getCause().getMessage() + "]";
                }
            } else if (t instanceof IllegalStateException) {
                httpstatus = RestResponseCode.NOT_ACCEPTABLE;
                errmessage = t.getMessage();
            } else if (t instanceof RestException) { // this includes 404
                RestException rex = (RestException)t;
                httpstatus = RestResponseCode.forCode(rex.getStatusCode());
                errmessage = rex.getMessage();
                if (rex.getCause() != null && !(rex.getCause() instanceof RestException)) {
                    errmessage = errmessage + ". Underlying cause: [" + rex.getCause().getMessage() + "]";
                }
            }
            if (httpstatus == null) {
                httpstatus = RestResponseCode.INTERNAL_SERVER_ERROR;
            }
            if (errmessage == null) {
                errmessage = "Unknown Error";
            }
            RestUtil.formErrorResponse(exchange, httpstatus, errmessage, getLogger());
        }
        RestResponseCode respcode = (result != null) ? RestResponseCode.OK : 
                (("PUT".equalsIgnoreCase(mthd) || "DELETE".equalsIgnoreCase(mthd) || "POST".equalsIgnoreCase(mthd)) ? 
                        RestResponseCode.ACCEPTED : RestResponseCode.NO_CONTENT);
        // TODO handle intricate ACCEPTED and CREATED now
        // if we reach here it is success; return the result in the response body
        int msglength = -1;
        StringBuilder respsb = new StringBuilder();
        try {
            result.toJson(respsb);
        } catch (Throwable t) {
            // TODO handle exception of conversion to JSON
        }
        if (respsb.length() > 0) {
        	msglength = respsb.length();
        }
    	try {
            exchange.sendResponseHeaders(respcode.code(), msglength);
    	} catch (IOException ioe2) {
    	    getLogger().error("Error writing response to stream", ioe2);
    	    return;
    	}
    	try (OutputStream os = exchange.getResponseBody()) {
    	    os.write(respsb.toString().getBytes());
    	} catch (IOException ioe3) {
    	    getLogger().error("Error writing response to stream", ioe3);
    	    return;
    	}
    	// should implicitly close request Input Stream if it was opened
    }

    RestEntity callRealMethod(RestHandlerDetail handlerdetail, String mthd, String mthpath, Map<String, String> formParams,
                            Map<String, String> qryparams, String reqdata, String contentType);

	RestResourceDetail getResourceDetail();
	Logger getLogger();
}
