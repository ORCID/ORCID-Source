package org.orcid.frontend.web.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.exception.OrcidCoreExceptionMapper;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.test.TargetProxyHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class PublicRecordApiExceptionHandlerTest {

    private static final String DEPRECATED_ORCID = "0000-0000-0000-0001";
    private static final String PRIMARY_ORCID = "0009-0004-3164-3380";

    private PublicRecordApiExceptionHandler handler;

    @Mock
    private OrcidCoreExceptionMapper orcidCoreExceptionMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        handler = new PublicRecordApiExceptionHandler();
        TargetProxyHelper.injectIntoProxy(handler, "orcidCoreExceptionMapper", orcidCoreExceptionMapper);
    }

    @Test
    public void handleThrowable_deprecatedGet_setsLocationAndPrimaryHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(80);
        request.setMethod("GET");
        request.setRequestURI("/" + DEPRECATED_ORCID + "/record");

        Map<String, String> params = new HashMap<>();
        params.put(OrcidDeprecatedException.ORCID, PRIMARY_ORCID);
        OrcidDeprecatedException exception = new OrcidDeprecatedException(params);

        OrcidError orcidError = Mockito.mock(OrcidError.class);
        when(orcidCoreExceptionMapper.getOrcidError(eq(exception), eq(OrcidCoreExceptionMapper.V3))).thenReturn(orcidError);

        ResponseEntity<Object> response = handler.handleThrowable(exception, request);

        assertEquals(301, response.getStatusCodeValue());
        assertEquals(PRIMARY_ORCID, response.getHeaders().getFirst("x-orcid-primary"));
        assertEquals("http://localhost/" + PRIMARY_ORCID + "/record", response.getHeaders().getFirst("location"));
    }

    @Test
    public void handleThrowable_deprecatedWithoutPrimaryParam_doesNotSetHeaders() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(80);
        request.setMethod("GET");
        request.setRequestURI("/" + DEPRECATED_ORCID + "/record");

        OrcidDeprecatedException exception = new OrcidDeprecatedException(Collections.emptyMap());

        OrcidError payload = Mockito.mock(OrcidError.class);
        when(orcidCoreExceptionMapper.getOrcidError(eq(exception), eq(OrcidCoreExceptionMapper.V3))).thenReturn(payload);

        ResponseEntity<Object> response = handler.handleThrowable(exception, request);

        assertEquals(301, response.getStatusCodeValue());
        assertNull(response.getHeaders().getFirst("x-orcid-primary"));
        assertNull(response.getHeaders().getFirst("location"));
        assertEquals(payload, response.getBody());
    }
}

