package org.orcid.api.memberV2.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;

import java.lang.reflect.Method;
import java.util.Arrays;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.api.memberV2.server.delegator.MemberV2ApiServiceDelegator;
import org.springframework.test.util.ReflectionTestUtils;

public class MemberV2ApiServiceImplV2Test {

    @Mock
    @SuppressWarnings("rawtypes")
    private MemberV2ApiServiceDelegator serviceDelegator;

    private MemberV2ApiServiceImplV2_0 serviceV20;
    private MemberV2ApiServiceImplV2_1 serviceV21;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        serviceV20 = new MemberV2ApiServiceImplV2_0();
        serviceV21 = new MemberV2ApiServiceImplV2_1();
        ReflectionTestUtils.setField(serviceV20, "serviceDelegator", serviceDelegator);
        ReflectionTestUtils.setField(serviceV21, "serviceDelegator", serviceDelegator);
    }

    @Test
    public void statusMethodsProduceVendorJson() throws Exception {
        assertProduces(serviceV20, "viewStatusText", VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON);
        assertProduces(serviceV21, "viewStatusText", VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON);
    }

    @Test
    public void statusMethodsDelegateAndFlagMonitoring() {
        Response expected = Response.ok("ok").build();
        when(serviceDelegator.viewStatusText()).thenReturn(expected);

        Response actual20 = serviceV20.viewStatusText();
        Response actual21 = serviceV21.viewStatusText();

        assertNotNull(actual20);
        assertNotNull(actual21);
        assertEquals("ok", String.valueOf(actual20.getEntity()));
        assertEquals("ok", String.valueOf(actual21.getEntity()));
        verify(serviceDelegator, times(2)).viewStatusText();
    }

    private void assertProduces(Object service, String methodName, String... expectedProduces) throws Exception {
        Method method = service.getClass().getMethod(methodName);
        Produces produces = method.getAnnotation(Produces.class);

        assertNotNull(produces);
        for (String expectedProduce : expectedProduces) {
            assertTrue(Arrays.asList(produces.value()).contains(expectedProduce));
        }
    }
}