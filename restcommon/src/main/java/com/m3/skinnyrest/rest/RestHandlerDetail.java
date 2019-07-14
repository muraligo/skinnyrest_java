package com.m3.skinnyrest.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

public class RestHandlerDetail {
    private final String _name;
    private final String _method;
    private final String _path;
    private final String _fullpath;
    private final ConcurrentMap<String, RestParameter> _parameters = new ConcurrentHashMap<String, RestParameter>();
    private HttpContext _context;

    public RestHandlerDetail(HttpServer server, String thename, String themethod, String thepath, String basepath) {
        _name = thename;
        _method = themethod;
        _path = thepath;
        _fullpath = (basepath == null) ? thepath : basepath + ((!basepath.strip().endsWith("/") ? "/" : "") + thepath);
        if (server != null) {
            _context = server.createContext(_fullpath);
        }
    }

    public String name() { return _name; }
    public String method() { return _method; }
    public String path() { return _path; }
    public String fullPath() { return _fullpath; }
    public void addParameter(RestParamType thetype, String thename, String defaultvalue) {
        RestParameter parm = new RestParameter(thetype, thename);
        if (defaultvalue != null) {
            parm.setDefaultValue(defaultvalue);
        }
        _parameters.put(thename, parm);
    }
    public RestParameter findParameter(String thename) {
        RestParameter parm = _parameters.get(thename);
        return parm;
    }
    public HttpContext context() { return _context; }

    public boolean hasParameterOfType(RestParamType thetype) {
        List<RestParameter> params = new ArrayList<RestParameter>();
        params.addAll(_parameters.values());
        return params.stream().anyMatch(rp -> rp._type.equals(thetype));
    }

    class RestParameter {
        private final RestParamType _type;
        private final String _name;
        private String _defaultValue;

        RestParameter(RestParamType thetype, String thename) {
            _type = thetype;
            _name = thename;
        }

        RestParamType parameterType() { return _type; }
        String name() { return _name; }
        String defaultValue() { return _defaultValue; }
        void setDefaultValue(String value) {
            _defaultValue = value;
        }
    }

    public enum RestParamType {
        PATH, 
        QUERY, 
        HEADER, 
        FORM,
        COOKIE, 
        BODY
    }
}
