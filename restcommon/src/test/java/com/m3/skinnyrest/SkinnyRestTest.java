package com.m3.skinnyrest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m3.skinnyrest.annotations.Path;
import com.m3.skinnyrest.rest.RestHandlerDetail;
import com.m3.skinnyrest.rest.RestHandlerDetail.RestParamType;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.m3.skinnyrest.rest.RestResourceDetail;
import com.m3.skinnyrest.rest.RestUtil;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class SkinnyRestTest {
    private static final String FORM_DATA_PARM_START = "Content-Disposition";
    private static final String FORM_DATA_PARM_PREFIX = FORM_DATA_PARM_START + ": form-data; name=\"";

    private static RestResourceDetail resourcedetail;

    private TestSkinnyResource service;

    @BeforeAll
    static void setupBeforeClass() throws Exception {
        resourcedetail = new RestResourceDetail("testskinny", "/skinnytest");
        RestHandlerDetail handlerdetail1 = resourcedetail.addHandler(null, "updateFromForm", "POST", "/updatewithparams");
        handlerdetail1.addParameter(RestParamType.FORM, "number", null);
        handlerdetail1.addParameter(RestParamType.FORM, "street", null);
    }

    @BeforeEach
    void setUp() throws Exception {
        service = new TestSkinnyResource(resourcedetail);
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @SuppressWarnings("rawtypes")
	@Test
    void updatePostWithFormMultipartParametersSucceeds() {
        HttpExchange exchange = Mockito.mock(HttpExchange.class, 
                withSettings().lenient().defaultAnswer(RETURNS_SMART_NULLS));
        URI requestURI = mockRequestURI("/updatewithparams");
        Headers headers = Mockito.mock(Headers.class, 
                withSettings().lenient().defaultAnswer(RETURNS_SMART_NULLS));
        when(headers.isEmpty()).thenReturn(false);
        when(headers.containsKey(RestUtil.HEADER_CONTENT_TYPE)).thenReturn(true);
        List<String> contentTypeLst = new ArrayList<String>();
        contentTypeLst.add(RestUtil.CONTENT_TYPE_MULTIPART_FORM);
        contentTypeLst.add("boundary=abracadabrasometestboundary");
        when(headers.get(RestUtil.HEADER_CONTENT_TYPE)).thenReturn(contentTypeLst);
        when(exchange.getRequestURI()).thenReturn(requestURI);
        when(exchange.getRequestHeaders()).thenReturn(headers);
        when(exchange.getRequestMethod()).thenReturn("POST");
        // build the body and return a Stream to it
        String boundary = "--abracadabrasometestboundary";
        StringBuilder bodysb = new StringBuilder();
        bodysb.append(System.lineSeparator());
        bodysb.append(boundary);
        bodysb.append(System.lineSeparator());
        bodysb.append(FORM_DATA_PARM_PREFIX);
        bodysb.append("number\"");
        bodysb.append(System.lineSeparator());
        bodysb.append(System.lineSeparator());
        bodysb.append("2315");
        bodysb.append(System.lineSeparator());
        bodysb.append(boundary);
        bodysb.append(System.lineSeparator());
        bodysb.append(FORM_DATA_PARM_PREFIX);
        bodysb.append("street\"");
        bodysb.append(System.lineSeparator());
        bodysb.append(System.lineSeparator());
        bodysb.append("Alhambra Ln");
        bodysb.append(System.lineSeparator());
        bodysb.append(boundary);
        bodysb.append(System.lineSeparator());
        InputStream is = new ByteArrayInputStream(bodysb.toString().getBytes(StandardCharsets.UTF_8));
        when(exchange.getRequestBody()).thenReturn(is);
        try {
            doAnswer((Answer) invocation -> {
                Integer respcode = (Integer)invocation.getArgument(0);
                Integer expcode = Integer.valueOf(200);
                Integer bodysize = (Integer)invocation.getArgument(1);
                assertEquals(expcode, respcode);
                assertTrue(bodysize > 0);
                return null;
            }).when(exchange).sendResponseHeaders(anyInt(), anyInt());
        } catch (IOException ioe) {
            // IGNORE
        }
        OutputStream os = Mockito.mock(OutputStream.class, 
                withSettings().lenient().defaultAnswer(RETURNS_SMART_NULLS));
        try {
            doNothing().when(os).write((byte[])notNull());
        } catch (IOException ioe) {
            // IGNORE
        }
        when(exchange.getResponseBody()).thenReturn(os);
        service.handleResource(exchange);
        // TODO Use argument captor and verify various things within
    }

    @SuppressWarnings("rawtypes")
	@Test
    void updatePostWithFormUrlEncodeParametersSucceeds() {
        HttpExchange exchange = Mockito.mock(HttpExchange.class, 
                withSettings().lenient().defaultAnswer(RETURNS_SMART_NULLS));
        URI requestURI = mockRequestURI("/updatewithparams");
        Headers headers = Mockito.mock(Headers.class, 
                withSettings().lenient().defaultAnswer(RETURNS_SMART_NULLS));
        when(headers.isEmpty()).thenReturn(false);
        when(headers.containsKey(RestUtil.HEADER_CONTENT_TYPE)).thenReturn(true);
        List<String> contentTypeLst = new ArrayList<String>();
        contentTypeLst.add(RestUtil.CONTENT_TYPE_FORM_URL_ENCODED);
        when(headers.get(RestUtil.HEADER_CONTENT_TYPE)).thenReturn(contentTypeLst);
        when(exchange.getRequestURI()).thenReturn(requestURI);
        when(exchange.getRequestHeaders()).thenReturn(headers);
        when(exchange.getRequestMethod()).thenReturn("POST");
        // build the body and return a Stream to it
        StringBuilder bodysb = new StringBuilder();
        bodysb.append(System.lineSeparator());
        bodysb.append("number=2315&");
        bodysb.append("street=Alhambra_Ln");
        bodysb.append(System.lineSeparator());
        InputStream is = new ByteArrayInputStream(bodysb.toString().getBytes(StandardCharsets.UTF_8));
        when(exchange.getRequestBody()).thenReturn(is);
        try {
            doAnswer((Answer) invocation -> {
                Integer respcode = (Integer)invocation.getArgument(0);
                Integer expcode = Integer.valueOf(200);
                Integer bodysize = (Integer)invocation.getArgument(1);
                assertEquals(expcode, respcode);
                assertTrue(bodysize > 0);
                return null;
            }).when(exchange).sendResponseHeaders(anyInt(), anyInt());
        } catch (IOException ioe) {
            // IGNORE
        }
        OutputStream os = Mockito.mock(OutputStream.class, 
                withSettings().lenient().defaultAnswer(RETURNS_SMART_NULLS));
        try {
            doNothing().when(os).write((byte[])notNull());
        } catch (IOException ioe) {
            // IGNORE
        }
        when(exchange.getResponseBody()).thenReturn(os);
        service.handleResource(exchange);
        // TODO Use argument captor and verify various things within
    }

//    @SuppressWarnings("rawtypes")
//	@Test
//    void updatePostWithJsonBodySucceeds() {
//        HttpExchange exchange = Mockito.mock(HttpExchange.class, 
//                withSettings().lenient().defaultAnswer(RETURNS_SMART_NULLS));
//        URI requestURI = mockRequestURI("/updatewithparams");
//        Headers headers = Mockito.mock(Headers.class, 
//                withSettings().lenient().defaultAnswer(RETURNS_SMART_NULLS));
//        when(headers.isEmpty()).thenReturn(false);
//        when(headers.containsKey(RestUtil.HEADER_CONTENT_TYPE)).thenReturn(true);
//        List<String> contentTypeLst = new ArrayList<String>();
//        contentTypeLst.add(RestUtil.CONTENT_TYPE_MULTIPART_FORM);
//        contentTypeLst.add("boundary=abracadabrasometestboundary");
//        when(headers.get(RestUtil.HEADER_CONTENT_TYPE)).thenReturn(contentTypeLst);
//        when(exchange.getRequestURI()).thenReturn(requestURI);
//        when(exchange.getRequestHeaders()).thenReturn(headers);
//        when(exchange.getRequestMethod()).thenReturn("POST");
//        // build the body and return a Stream to it
//        String boundary = "--abracadabrasometestboundary";
//        StringBuilder bodysb = new StringBuilder();
//        bodysb.append(System.lineSeparator());
//        bodysb.append(boundary);
//        bodysb.append(System.lineSeparator());
//        bodysb.append(FORM_DATA_PARM_PREFIX);
//        bodysb.append("number\"");
//        bodysb.append(System.lineSeparator());
//        bodysb.append(System.lineSeparator());
//        bodysb.append("2315");
//        bodysb.append(System.lineSeparator());
//        bodysb.append(boundary);
//        bodysb.append(System.lineSeparator());
//        bodysb.append(FORM_DATA_PARM_PREFIX);
//        bodysb.append("street\"");
//        bodysb.append(System.lineSeparator());
//        bodysb.append(System.lineSeparator());
//        bodysb.append("Alhambra Ln");
//        bodysb.append(System.lineSeparator());
//        bodysb.append(boundary);
//        bodysb.append(System.lineSeparator());
//        InputStream is = new ByteArrayInputStream(bodysb.toString().getBytes(StandardCharsets.UTF_8));
//        when(exchange.getRequestBody()).thenReturn(is);
//        try {
//            doAnswer((Answer) invocation -> {
//                Integer respcode = (Integer)invocation.getArgument(0);
//                Integer expcode = Integer.valueOf(200);
//                Integer bodysize = (Integer)invocation.getArgument(1);
//                assertEquals(expcode, respcode);
//                assertTrue(bodysize > 0);
//                return null;
//            }).when(exchange).sendResponseHeaders(anyInt(), anyInt());
//        } catch (IOException ioe) {
//            // IGNORE
//        }
//        OutputStream os = Mockito.mock(OutputStream.class, 
//                withSettings().lenient().defaultAnswer(RETURNS_SMART_NULLS));
//        try {
//            doNothing().when(os).write((byte[])notNull());
//        } catch (IOException ioe) {
//            // IGNORE
//        }
//        when(exchange.getResponseBody()).thenReturn(os);
//        service.handleResource(exchange);
//        // TODO Use argument captor and verify various things within
//    }

    // Looks like URI is a final class and cannot be mocked
    // So need to create a real non-absolute, non-opaque URI with the corresponding path
    private URI mockRequestURI(String methodPath) {
        String fullpath = "/skinnytest" + methodPath;
        URI requestURI = URI.create(fullpath);
        return requestURI;
    }

    @Path("/skinnytest")
    static class TestSkinnyResource implements SkinnyResource {
        private static final Logger _LOG = LoggerFactory.getLogger(TestSkinnyResource.class);

        private final RestResourceDetail _resourcedetail;

        public TestSkinnyResource(RestResourceDetail detail) {
            _resourcedetail = detail;
        }

        @Override
        public RestEntity callRealMethod(RestHandlerDetail handlerdetail, String mthd, String mthpath,
                                Map<String, String> formParams, Map<String, String> qryparams, String reqdata, String contentType) {
            RestEntity result = null;
            if ("updateFromForm".equals(handlerdetail.name())) {
            	result = new StringResultEntity(updateFromForm(formParams.get("number"), formParams.get("street")));
            }
            return result;
        }

        @Override
        public RestResourceDetail getResourceDetail() { return _resourcedetail; }

        @Override
        public Logger getLogger() { return _LOG; }

        String updateFromForm(String number, String street) {
        	if (number == null || number.isBlank() || street == null || street.isBlank()) {
        	    throw new IllegalArgumentException("ERROR: Invalid parameters");
        	}
        	return "SUCCESS";
        }
    }

}
