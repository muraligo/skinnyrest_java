package com.m3.skinnyrest.sample;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m3.skinnyrest.RestEntity;
import com.m3.skinnyrest.SkinnyResource;
import com.m3.skinnyrest.annotations.FormParam;
import com.m3.skinnyrest.annotations.HeaderParam;
import com.m3.skinnyrest.annotations.Name;
import com.m3.skinnyrest.annotations.POST;
import com.m3.skinnyrest.annotations.Path;
import com.m3.skinnyrest.rest.RestHandlerDetail;
import com.m3.skinnyrest.rest.RestResourceDetail;

@Name("store")
@Path("/store")
public class StoreResource implements SkinnyResource {
    private static Logger _LOG = LoggerFactory.getLogger(StoreResource.class);

    private final RestResourceDetail _resourcedetail;

    public StoreResource(RestResourceDetail resdetail) {
        _resourcedetail = resdetail;
    }

    @POST
    @Path("/add")
    void addInventory(@HeaderParam(value="request-id", defaultValue="NONE") String requestId, 
            @FormParam(value="name", defaultValue="NONE") String name, 
            @FormParam(value="quantity", defaultValue="NONE") Integer quantity) {
    	// TODO implement
    }

    @Override
    public RestEntity callRealMethod(RestHandlerDetail handlerdetail, String mthd, String mthpath,
                    Map<String, String> formParams, Map<String, String> qryparams, String reqdata, String contentType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RestResourceDetail getResourceDetail() {
        return _resourcedetail;
    }

    @Override
    public Logger getLogger() {
        return _LOG;
	}
}
