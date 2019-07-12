package com.m3.skinnyrest.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

import com.m3.skinnyrest.SkinnyResource;
import com.sun.net.httpserver.HttpServer;

public class RestResourceDetail {
    private final String _name;
    private final String _basepath;
    private final List<RestHandlerDetail> _handlers = new ArrayList<RestHandlerDetail>();
    private transient SkinnyResource _resource;

    public RestResourceDetail(String nm, String basep) {
        _name = nm;
        _basepath = basep;
    }

    public String getName() { return _name; }
    public String getBasePath() { return _basepath; }
    public List<RestHandlerDetail> getHandlers() { return _handlers; }
    public RestHandlerDetail addHandler(HttpServer server, String thename, String themethod, String thepath) {
        RestHandlerDetail handler = new RestHandlerDetail(server, thename, themethod, thepath, _basepath);
        _handlers.add(handler);
        return handler;
    }

    public List<RestHandlerDetail> findMatchingHandlers(String path, String mthd) {
        ArrayList<RestHandlerDetail> result = _handlers.stream().filter(h -> (h.path().equals(path) && h.method().equalsIgnoreCase(mthd)))
                                                       .collect(Collector.of(ArrayList::new, ArrayList::add, (left, right) -> { left.addAll(right); return left; }));
        return result;
    }

    public SkinnyResource getResource() { return _resource; }
    public void setResource(SkinnyResource obj) { _resource = obj; }
}
