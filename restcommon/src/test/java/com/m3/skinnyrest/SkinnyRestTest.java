package com.m3.skinnyrest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
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
        RestHandlerDetail handlerdetail2 = resourcedetail.addHandler(null, "updateFromJson", "POST", "/updatewithjson");
        handlerdetail2.addParameter(RestParamType.BODY, "data", null);
        RestHandlerDetail handlerdetail3 = resourcedetail.addHandler(null, "createFromJson", "PUT", "/createwithjson");
        handlerdetail3.addParameter(RestParamType.BODY, "data", null);
        RestHandlerDetail handlerdetail4 = resourcedetail.addHandler(null, "getFromPathAndQuery", "GET", "/getwithparams");
        handlerdetail4.addParameter(RestParamType.PATH, "name", null);
        handlerdetail4.addParameter(RestParamType.QUERY, "type", null);
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

    @SuppressWarnings("rawtypes")
    @Test
    void updatePostWithJsonBodySucceeds() {
        HttpExchange exchange = Mockito.mock(HttpExchange.class, 
                withSettings().lenient().defaultAnswer(RETURNS_SMART_NULLS));
        URI requestURI = mockRequestURI("/updatewithjson");
        Headers headers = Mockito.mock(Headers.class, 
                withSettings().lenient().defaultAnswer(RETURNS_SMART_NULLS));
        when(headers.isEmpty()).thenReturn(false);
        when(headers.containsKey(RestUtil.HEADER_CONTENT_TYPE)).thenReturn(true);
        List<String> contentTypeLst = new ArrayList<String>();
        contentTypeLst.add("application/json");
        when(headers.get(RestUtil.HEADER_CONTENT_TYPE)).thenReturn(contentTypeLst);
        when(exchange.getRequestURI()).thenReturn(requestURI);
        when(exchange.getRequestHeaders()).thenReturn(headers);
        when(exchange.getRequestMethod()).thenReturn("POST");
        // build the body and return a Stream to it
        StringBuilder bodysb = new StringBuilder();
        bodysb.append(System.lineSeparator());
        bodysb.append("{");
        bodysb.append(System.lineSeparator());
        bodysb.append("\"number\": ");
        bodysb.append("\"2315\",");
        bodysb.append(System.lineSeparator());
        bodysb.append("\"street\": ");
        bodysb.append("\"Alhambra Ln\"");
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
    void createPutWithJsonBodySucceeds() {
        HttpExchange exchange = Mockito.mock(HttpExchange.class, 
                withSettings().lenient().defaultAnswer(RETURNS_SMART_NULLS));
        URI requestURI = mockRequestURI("/createwithjson");
        Headers headers = Mockito.mock(Headers.class, 
                withSettings().lenient().defaultAnswer(RETURNS_SMART_NULLS));
        when(headers.isEmpty()).thenReturn(false);
        when(headers.containsKey(RestUtil.HEADER_CONTENT_TYPE)).thenReturn(true);
        List<String> contentTypeLst = new ArrayList<String>();
        contentTypeLst.add("application/json");
        when(headers.get(RestUtil.HEADER_CONTENT_TYPE)).thenReturn(contentTypeLst);
        when(exchange.getRequestURI()).thenReturn(requestURI);
        when(exchange.getRequestHeaders()).thenReturn(headers);
        when(exchange.getRequestMethod()).thenReturn("PUT");
        // build the body and return a Stream to it
        StringBuilder bodysb = new StringBuilder();
        bodysb.append(System.lineSeparator());
        bodysb.append("{");
        bodysb.append(System.lineSeparator());
        bodysb.append("\"name\": ");
        bodysb.append("\"johnsmith\",");
        bodysb.append(System.lineSeparator());
        bodysb.append("\"type\": ");
        bodysb.append("\"home\",");
        bodysb.append(System.lineSeparator());
        bodysb.append("\"number\": ");
        bodysb.append("\"2315\",");
        bodysb.append(System.lineSeparator());
        bodysb.append("\"street\": ");
        bodysb.append("\"Alhambra Ln\"");
        bodysb.append(System.lineSeparator());
        bodysb.append("\"city\": ");
        bodysb.append("\"Lost Angels\"");
        bodysb.append(System.lineSeparator());
        bodysb.append("\"state\": ");
        bodysb.append("\"Cali Fornia\"");
        bodysb.append(System.lineSeparator());
        bodysb.append("\"postcode\": ");
        bodysb.append("\"90210\",");
        bodysb.append(System.lineSeparator());
        bodysb.append("\"country\": ");
        bodysb.append("\"UF\",");
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
            } else if ("updateFromJson".equals(handlerdetail.name())) {
                result = new StringResultEntity(updateFromJson(reqdata));
            } else if ("createFromJson".equals(handlerdetail.name())) {
                result = new StringResultEntity(createFromJson(reqdata));
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

        String updateFromJson(String data) {
            if (data == null || data.isBlank()) {
        	    throw new IllegalArgumentException("ERROR: Invalid parameters");
            }
            String number = null;
            String street = null;
            String key = null;
            int inobject = 0;
            int inarray = 0;
            try (InputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)); 
                    InputStreamReader isr = new InputStreamReader(is)) {
            	JsonReader parser = new JsonReader(isr);
            	while (parser.hasNext()) {
            	    JsonToken token = parser.peek();
            	    switch (token) {
            	    case BEGIN_OBJECT:
            	        parser.beginObject();
            	        inobject++;
            	        break;
            	    case END_OBJECT:
            	        parser.endObject();
            	        // TODO ensure it is > 0 before doing this else it is an error
            	        inobject--;
            	        break;
            	    case BEGIN_ARRAY:
            	        parser.beginArray();
            	        inarray++;
            	        break;
            	    case END_ARRAY:
        			    parser.endArray();
            	        // TODO ensure it is > 0 before doing this else it is an error
            	        inarray--;
            	        break;
            	    case NAME:
            	        if (inarray > 0) parser.nextName(); // discard the name
            	        if (inobject != 1) parser.nextName(); // discard the name
            	        key = parser.nextName();
            	        break;
            	    case BOOLEAN:
            	        if (inarray > 0) parser.skipValue(); // discard the value
            	        if (inobject != 1) parser.skipValue(); // discard the value
            	        _LOG.warn("Unknown boolean item found with key=[" + key + "]");
    					parser.skipValue();
            	        break;
            	    case NUMBER:
            	        if (inarray > 0) parser.skipValue(); // discard the value
            	        if (inobject != 1) parser.skipValue(); // discard the value
            	        _LOG.warn("Unknown number item found with key=[" + key + "]");
    					parser.skipValue();
            	        break;
            	    case STRING:
            	        if (inarray > 0) parser.skipValue(); // discard the value
            	        if (inobject != 1) parser.skipValue(); // discard the value
            	        String value = parser.nextString();
            	        if ("number".equalsIgnoreCase(key)) number = value;
            	        else if ("street".equalsIgnoreCase(key)) street = value;
            	        else _LOG.warn("Unknown data found with key=["+ key + "] value=[" + value + "]");
            	        break;
            	    case NULL:
            	        if (inarray > 0) parser.nextNull(); // discard the value
            	        if (inobject != 1) parser.nextNull(); // discard the value
            	        _LOG.warn("Null value for key=[" + key + "]");
            	        parser.nextNull(); // consume anyway
            	        break;
            	    case END_DOCUMENT:
            	        _LOG.warn("End of Document Reached");
            	        break;
            	    default:
            	        _LOG.warn("This part should never execute");
            	        break;
            	    }
            	}
            	parser.close();
            } catch (IOException ioe) {
                throw new IllegalStateException("ERROR: Reading and or parsing Json body", ioe);
            }
        	if (number == null || number.isBlank() || street == null || street.isBlank()) {
        	    throw new IllegalArgumentException("ERROR: Invalid parameters");
        	}
        	return "SUCCESS";
        }

        String createFromJson(String data) {
            if (data == null || data.isBlank()) {
        	    throw new IllegalArgumentException("ERROR: Invalid parameters");
            }
            String name = null;
            String type = null;
            String number = null;
            String street = null;
            String city = null;
            String state = null;
            String postcode = null;
            String country = null;
            String key = null;
            int inobject = 0;
            int inarray = 0;
            try (InputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)); 
                    InputStreamReader isr = new InputStreamReader(is)) {
            	JsonReader parser = new JsonReader(isr);
            	while (parser.hasNext()) {
            	    JsonToken token = parser.peek();
            	    switch (token) {
            	    case BEGIN_OBJECT:
            	        parser.beginObject();
            	        inobject++;
            	        break;
            	    case END_OBJECT:
            	        parser.endObject();
            	        // TODO ensure it is > 0 before doing this else it is an error
            	        inobject--;
            	        break;
            	    case BEGIN_ARRAY:
            	        parser.beginArray();
            	        inarray++;
            	        break;
            	    case END_ARRAY:
        			    parser.endArray();
            	        // TODO ensure it is > 0 before doing this else it is an error
            	        inarray--;
            	        break;
            	    case NAME:
            	        if (inarray > 0) parser.nextName(); // discard the name
            	        if (inobject != 1) parser.nextName(); // discard the name
            	        key = parser.nextName();
            	        break;
            	    case BOOLEAN:
            	        if (inarray > 0) parser.skipValue(); // discard the value
            	        if (inobject != 1) parser.skipValue(); // discard the value
            	        _LOG.warn("Unknown boolean item found with key=[" + key + "]");
    					parser.skipValue();
            	        break;
            	    case NUMBER:
            	        if (inarray > 0) parser.skipValue(); // discard the value
            	        if (inobject != 1) parser.skipValue(); // discard the value
            	        _LOG.warn("Unknown number item found with key=[" + key + "]");
    					parser.skipValue();
            	        break;
            	    case STRING:
            	        if (inarray > 0) parser.skipValue(); // discard the value
            	        if (inobject != 1) parser.skipValue(); // discard the value
            	        String value = parser.nextString();
            	        if ("name".equalsIgnoreCase(key)) name = value;
            	        else if ("type".equalsIgnoreCase(key)) type = value;
            	        else if ("number".equalsIgnoreCase(key)) number = value;
            	        else if ("street".equalsIgnoreCase(key)) street = value;
            	        else if ("city".equalsIgnoreCase(key)) city = value;
            	        else if ("state".equalsIgnoreCase(key)) state = value;
            	        else if ("postcode".equalsIgnoreCase(key)) postcode = value;
            	        else if ("country".equalsIgnoreCase(key)) country = value;
            	        else _LOG.warn("Unknown data found with key=["+ key + "] value=[" + value + "]");
            	        break;
            	    case NULL:
            	        if (inarray > 0) parser.nextNull(); // discard the value
            	        if (inobject != 1) parser.nextNull(); // discard the value
            	        _LOG.warn("Null value for key=[" + key + "]");
            	        parser.nextNull(); // consume anyway
            	        break;
            	    case END_DOCUMENT:
            	        _LOG.warn("End of Document Reached");
            	        break;
            	    default:
            	        _LOG.warn("This part should never execute");
            	        break;
            	    }
            	}
            	parser.close();
            } catch (IOException ioe) {
                throw new IllegalStateException("ERROR: Reading and or parsing Json body", ioe);
            }
        	if (name == null || name.isBlank() || type == null || type.isBlank()
        			 || number == null || number.isBlank() || street == null || street.isBlank()
        			 || city == null || city.isBlank() || state == null || state.isBlank()
        			 || postcode == null || postcode.isBlank() || country == null || postcode.isBlank()) {
        	    throw new IllegalArgumentException("ERROR: Invalid parameters");
        	}
        	return "SUCCESS";
        }
    }

}
