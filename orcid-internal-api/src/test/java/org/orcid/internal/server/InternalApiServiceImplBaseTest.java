package org.orcid.internal.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.internal.server.delegator.InternalApiServiceDelegator;
import org.springframework.test.util.ReflectionTestUtils;

public class InternalApiServiceImplBaseTest {

    @Mock
    private InternalApiServiceDelegator serviceDelegator;

    private InternalApiServiceImplBase service;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        service = new InternalApiServiceImplBase();
        ReflectionTestUtils.setField(service, "serviceDelegator", serviceDelegator);
    }

    @Test
    public void viewStatusTextDelegates() {
        Response expected = Response.ok("OK I am here").build();
        when(serviceDelegator.viewStatusText()).thenReturn(expected);

        Response actual = service.viewStatusText();

        assertNotNull(actual);
        assertEquals("OK I am here", String.valueOf(actual.getEntity()));
        verify(serviceDelegator).viewStatusText();
    }
}