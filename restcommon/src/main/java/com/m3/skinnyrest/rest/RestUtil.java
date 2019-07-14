package com.m3.skinnyrest.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;

import com.m3.skinnyrest.SkinnyResource;
import com.m3.skinnyrest.annotations.CookieParam;
import com.m3.skinnyrest.annotations.DELETE;
import com.m3.skinnyrest.annotations.FormParam;
import com.m3.skinnyrest.annotations.GET;
import com.m3.skinnyrest.annotations.HEAD;
import com.m3.skinnyrest.annotations.HeaderParam;
import com.m3.skinnyrest.annotations.Name;
import com.m3.skinnyrest.annotations.OPTIONS;
import com.m3.skinnyrest.annotations.POST;
import com.m3.skinnyrest.annotations.PUT;
import com.m3.skinnyrest.annotations.Path;
import com.m3.skinnyrest.annotations.PathParam;
import com.m3.skinnyrest.annotations.QueryParam;
import com.m3.skinnyrest.rest.RestHandlerDetail.RestParamType;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.net.httpserver.HttpServer;

public class RestUtil {
	public static final String CONTENT_TYPE_FORM_URL_ENCODED = "application/ x-www-form-urlencoded";
    public static final String CONTENT_TYPE_MULTIPART_FORM = "multipart/form-data";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    public static Map<String, String> parseUrlQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<String, String>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx != -1)
                    params.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), 
                                URLDecoder.decode(pair.substring(idx+1), "UTF-8"));
            }
        }
        return params;
    }

    // This is a bit shaky. It doesn't handle continuation
    // lines, but our client shouldn't send any.
    // Read a line from the input stream, swallowing the final
    // \r\n sequence. Stops at the first \n, doesn't complain
    // if it wasn't preceded by '\r'.
    //
    public static String readLine(InputStream r) throws IOException {
        StringBuilder b = new StringBuilder();
        int c;
        while ((c = r.read()) != -1) {
            if (c == '\n') break;
            b.appendCodePoint(c);
        }
        if (c == -1 && b.length() == 0) {
            return null;
        }
        if (b.codePointAt(b.length() -1) == '\r') {
            b.delete(b.length() -1, b.length());
        }
        return b.toString();
    }

    public static String readAllFormDataParams(InputStream is, String boundary, Map<String, String> formParams)
            throws IOException {
        String reqdata;
        // skip till start boundary (or end)
        String bndrymarker = MULTIPART_BOUNDARY_PREFIX + boundary;
        while ((reqdata = readLine(is)) != null) {
            if (!reqdata.isBlank() && reqdata.strip().startsWith(bndrymarker)) {
                break;
            }
        }
        while ((reqdata = readAFormDataParam(is, bndrymarker, formParams)) != null) {
            if (reqdata.startsWith("ENDMULTIPART")) {
                reqdata = reqdata.substring("ENDMULTIPART ".length());
                break;
            }
        }
        return reqdata;
    }

    // read a single Form-Data parameter name and value contained within a single part of a multipart body with a boundary
    private static String readAFormDataParam(InputStream is, String boundaryMarker, Map<String, String> formParams) 
            throws IOException {
        String reqdata;
        reqdata = RestUtil.readLine(is);
        if (reqdata == null || reqdata.isBlank()) {
            return null;
        }
        reqdata = reqdata.strip();
        if (!reqdata.startsWith(FORM_DATA_PARM_START)) {
            // end of multipart segments
        	return "ENDMULTIPART " + reqdata;
        }
        String fieldname = reqdata.substring(FORM_DATA_PARM_PREFIX.length(), reqdata.lastIndexOf('\"'));
        if (fieldname != null) {
            StringBuilder sbfld = new StringBuilder();
            while ((reqdata = RestUtil.readLine(is)) != null) {
                if (!reqdata.isBlank() && reqdata.strip().startsWith(boundaryMarker)) {
                    break;
                }
                if (!reqdata.isBlank()) {
                    sbfld.append(reqdata.strip());
                }
            }
            if (sbfld.length() > 0) {
                formParams.put(fieldname, sbfld.toString());
            }
        }
        return reqdata;
    }

    @SafeVarargs
    public static ConcurrentMap<String, RestResourceDetail> registerRestResources(HttpServer server, Class<?>...clazzes) {
        ConcurrentMap<String, RestResourceDetail> restdetails = null;
        for (Class<?> clz : clazzes) {
            RestResourceDetail rrd = registerRestResource(server, clz);
            if (rrd != null) {
                if (restdetails == null) {
                    restdetails = new ConcurrentHashMap<String, RestResourceDetail>();
                }
                restdetails.put(rrd.getBasePath(), rrd);
            }
        }
        return restdetails;
    }

    private static RestResourceDetail registerRestResource(HttpServer server, Class<?> clz) {
        Annotation[] anns = clz.getAnnotations();
        String name = null;
        String basep = null;
        for (Annotation ann : anns) {
            if (ann instanceof Name) { // @Name exists
                Name annm = (Name)ann;
                name = annm.value();
            } else if (ann instanceof Path) { // @Path exists
                Path annpth = (Path)ann;
                basep = annpth.value();
            }
        }
        RestResourceDetail retval = new RestResourceDetail(name, basep);
        Method[] methods = clz.getMethods();
        for (Method mthd : methods) {
        	int mods = mthd.getModifiers();
        	if (Modifier.isPrivate(mods) || Modifier.isProtected(mods)) {
        	    continue;
        	}
            Annotation[] mtanns = mthd.getAnnotations();
            String op = null;
            String mpath = null;
            for (Annotation ann : mtanns) {
                if (ann instanceof GET) { // @GET handler
                    op = "GET";
                } else if (ann instanceof PUT) { // @PUT handler
                    op = "PUT";
                } else if (ann instanceof POST) { // @POST handler
                    op = "POST";
                } else if (ann instanceof DELETE) { // @DELETE handler
                    op = "DELETE";
                } else if (ann instanceof HEAD) { // @HEAD handler
                    op = "HEAD";
                } else if (ann instanceof OPTIONS) { // @OPTIONS handler
                    op = "OPTIONS";
                } else if (ann instanceof Path) { // @Path exists
                    Path annpth = (Path)ann;
                    mpath = annpth.value();
                }
            }
            RestHandlerDetail handler = retval.addHandler(server, mthd.getName(), op, mpath);
            Annotation[][] prmannss = mthd.getParameterAnnotations();
            if (prmannss.length > 0) {
                for (int ix = 0; ix < prmannss.length; ix++) {
                    Annotation[] prmanns = prmannss[ix];
                    if (prmanns.length > 0) {
                        RestParamType prmtype = null;
                        String prmname = null;
                        String prmdefault = null;
                        for (int jx = 0; jx < prmanns.length; jx++) {
                            if (prmanns[jx] instanceof PathParam) {
                                PathParam annprm = (PathParam)prmanns[jx];
                                prmtype = RestParamType.PATH;
                                prmname = annprm.value();
                                prmdefault = annprm.defaultValue();
                            } else if (prmanns[jx] instanceof QueryParam) {
                                QueryParam annprm = (QueryParam)prmanns[jx];
                                prmtype = RestParamType.QUERY;
                                prmname = annprm.value();
                                prmdefault = annprm.defaultValue();
                            } else if (prmanns[jx] instanceof HeaderParam) {
                                HeaderParam annprm = (HeaderParam)prmanns[jx];
                                prmtype = RestParamType.HEADER;
                                prmname = annprm.value();
                                prmdefault = annprm.defaultValue();
                            } else if (prmanns[jx] instanceof FormParam) {
                                FormParam annprm = (FormParam)prmanns[jx];
                                prmtype = RestParamType.FORM;
                                prmname = annprm.value();
                                prmdefault = annprm.defaultValue();
                            } else if (prmanns[jx] instanceof CookieParam) {
                                CookieParam annprm = (CookieParam)prmanns[jx];
                                prmtype = RestParamType.COOKIE;
                                prmname = annprm.value();
                                prmdefault = annprm.defaultValue();
                            }
                        }
                        handler.addParameter(prmtype, prmname, prmdefault);
                    }
                }
            }
        }
        Object obj = null;
        try {
            obj = clz.getDeclaredConstructor(RestResourceDetail.class).newInstance(retval);
        } catch (InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException ex) {
            throw new RuntimeException("Resource [" + clz.getSimpleName() + "] instantiation failed", ex);
        } catch (NoSuchMethodException nsme) {
            throw new RuntimeException("Resource [" + clz.getSimpleName() + "] does not have a constructor with RestResourceDetail as argument", nsme);
        }
        if (obj == null) {
            throw new RuntimeException("Resource [" + clz.getSimpleName() + "] instantiation failed");
        }
        if (!(obj instanceof SkinnyResource)) {
            throw new RuntimeException("Resource [" + clz.getSimpleName() + "] does not seem to be a valid Resource");
        }
        SkinnyResource rsc = (SkinnyResource)obj;
        retval.setResource(rsc);
        for (RestHandlerDetail h : retval.getHandlers()) {
            h.context().setHandler(rsc::handleResource);
        }
        return retval;
    }

    public static void formErrorResponse(HttpExchange exchange, RestResponseCode responseCode, String errorMessage, 
            Logger log) {
        String finalmsg = responseCode.baseMessage() + errorMessage;
    	try {
            exchange.sendResponseHeaders(responseCode.code(), finalmsg.length());
    	} catch (IOException ioe1) {
    	    log.error(finalmsg);
    	    return;
    	}
    	try (OutputStream os = exchange.getResponseBody()) {
    	    os.write(finalmsg.getBytes());
    	} catch (IOException ioe2) {
    	    log.error(finalmsg);
    	    return;
    	}
    	// should implicitly close request Input Stream if it was opened
    }

	public static void printRequestInfo(Logger log, HttpExchange exchange) {
        log.debug("-- headers --");
        Headers requestHeaders = exchange.getRequestHeaders();
        StringBuilder sb = new StringBuilder();
        requestHeaders.entrySet().forEach(v -> {
        	sb.append("\t");
            sb.append(v.getKey());
        	sb.append("=[");
        	v.getValue().forEach(arg -> sb.append(arg + "; "));
        	sb.append("]\n");
        });
        log.debug(sb.toString());

        HttpPrincipal principal = exchange.getPrincipal();
        log.debug("-- principal --\t" + ((principal != null) ? principal.toString() : ""));

        String requestMethod = exchange.getRequestMethod();
        log.debug("-- HTTP method --\t" + requestMethod);

        log.debug("-- query --");
        URI requestURI = exchange.getRequestURI();
        String query = requestURI.getQuery();
        log.debug("\t" + query);
    }

    private static final String MULTIPART_BOUNDARY_PREFIX = "--";
    private static final String FORM_DATA_PARM_START = "Content-Disposition";
    private static final String FORM_DATA_PARM_PREFIX = FORM_DATA_PARM_START + ": form-data; name=\"";
}
