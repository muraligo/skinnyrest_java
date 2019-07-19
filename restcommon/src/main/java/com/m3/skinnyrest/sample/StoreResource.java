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

    // In order to work properly these methods should be stateless
    @POST
    @Path("/add")
    void addInventory(@HeaderParam(value="request-id") String requestId, 
            @FormParam(value="name") String name, @FormParam(value="quantity") Integer quantity) {
    	// TODO implement
        // TODO for now ignore request-id
    }

    @Override
    public RestEntity callRealMethod(RestHandlerDetail handlerdetail, String mthd, String mthpath,
                    Map<String, String> formParams, Map<String, String> qryparams, String reqdata, String contentType) {
        RestEntity result = null;
        // as we call the real methods, we let the exceptions flow through
        // let calls to void methods return a null entity and let the caller default handling decide
        if ("addInventory".equals(handlerdetail.name())) {
            // TODO we need header param completed before we can pass this in. for now pass null
            String name = formParams.get("name");;
            Integer quantity = null;
            if (formParams.containsKey("quantity")) {
                String qtystr = formParams.get("quantity");
                if (qtystr != null && !qtystr.isBlank()) {
                    try {
                        quantity = Integer.valueOf(qtystr);
                    } catch (NumberFormatException nfe) {
                        getLogger().error("Numerical form parameter did not parse to a number", nfe);
                        quantity = null;
                    }
                }
            }
            addInventory(null, name, quantity);
        }
        return result;
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
