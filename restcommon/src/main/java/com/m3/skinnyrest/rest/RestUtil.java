package com.m3.skinnyrest.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

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
import com.m3.skinnyrest.rest.RestHandlerDetail.RestParameter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.net.httpserver.HttpServer;

public class RestUtil {
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
            RestHandlerDetail handler = retval.addHandler(server, mthd.getName(), op, mpath, rsc);
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
                                prmdefault = null;
                            }
                        }
                        if (prmdefault == null || "NONE".equalsIgnoreCase(prmdefault))
                            prmdefault = null;
                        handler.addParameter(prmtype, prmname, prmdefault);
                    }
                }
            }
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

    public static Map<String, Object> parseUrlPath(String mthpath, RestHandlerDetail handlerdetail) {
		// TODO Auto-generated method stub
        List<RestParameter> parmstrm = handlerdetail.pathParameterStream().collect(Collectors.toList());
        int prmssz = parmstrm.size();
        int prmix = 0;
        Map<String, Object> result = null;
        String declpath = handlerdetail.path();
        int dpix = declpath.indexOf("{");
        while (dpix > 0) {
            int pthix = mthpath.indexOf("/", dpix);
            if (pthix < 0) pthix = mthpath.length() - 1;
            String value = mthpath.substring(dpix, pthix);
            dpix++;
            int dpix2 = declpath.indexOf("}", dpix);
//            String nm2 = declpath.substring(dpix, dpix2);
            if (prmix >= prmssz) // exhausted path parameters; skip the rest for now TODO error out
                break;
            RestParameter prm = parmstrm.get(prmix);
            String name = prm.name();
            if (result == null) {
                result = new HashMap<String, Object>();
            }
            // For now everything is a String
            // TODO Handle other data types
            result.put(name, value);
            dpix = declpath.indexOf("{", dpix2);
        }
        return result;
    }
}
