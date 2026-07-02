package org.orcid.api.memberV3.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;

import java.lang.reflect.Method;
import java.util.Arrays;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.api.memberV3.server.delegator.MemberV3ApiServiceDelegator;
import org.springframework.test.util.ReflectionTestUtils;

public class MemberV3ApiServiceImplV3_0Test {

    @Mock
    @SuppressWarnings("rawtypes")
    private MemberV3ApiServiceDelegator serviceDelegator;

    @Mock
    private HttpServletRequest httpRequest;

    private MemberV3ApiServiceImplV3_0 service;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        service = new MemberV3ApiServiceImplV3_0();
        ReflectionTestUtils.setField(service, "serviceDelegator", serviceDelegator);
        ReflectionTestUtils.setField(service, "httpRequest", httpRequest);
    }

    @Test
    public void viewStatusSimpleProducesVendorJson() throws Exception {
        assertProduces("viewStatusSimple", VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON);
    }

    @Test
    public void viewStatusJsonProducesVendorJson() throws Exception {
        assertProduces("viewStatusJson", VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON);
    }

    @Test
    public void viewStatusSimpleDelegatesAndFlagsMonitoring() {
        Response expected = Response.ok("ok").build();
        when(serviceDelegator.viewStatusSimple()).thenReturn(expected);

        Response actual = service.viewStatusSimple();

        assertNotNull(actual);
        assertEquals("ok", String.valueOf(actual.getEntity()));
        verify(httpRequest).setAttribute("skipAccessLog", true);
        verify(httpRequest).setAttribute("isMonitoring", true);
        verify(serviceDelegator).viewStatusSimple();
    }

    @Test
    public void viewStatusJsonDelegatesAndFlagsMonitoring() {
        Response expected = Response.ok("ok").build();
        when(serviceDelegator.viewStatus()).thenReturn(expected);

        Response actual = service.viewStatusJson();

        assertNotNull(actual);
        assertEquals("ok", String.valueOf(actual.getEntity()));
        verify(httpRequest).setAttribute("skipAccessLog", true);
        verify(httpRequest).setAttribute("isMonitoring", true);
        verify(serviceDelegator).viewStatus();
    }

    private void assertProduces(String methodName, String... expectedProduces) throws Exception {
        Method method = MemberV3ApiServiceImplV3_0.class.getMethod(methodName);
        Produces produces = method.getAnnotation(Produces.class);

        assertNotNull(produces);
        for (String expectedProduce : expectedProduces) {
            assertTrue(Arrays.asList(produces.value()).contains(expectedProduce));
        }
    }
}