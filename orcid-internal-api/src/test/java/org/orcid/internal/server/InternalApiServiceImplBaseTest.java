package org.orcid.internal.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.internal.server.delegator.InternalApiServiceDelegator;

@RunWith(MockitoJUnitRunner.class)
public class InternalApiServiceImplBaseTest {

    @Mock
    private InternalApiServiceDelegator serviceDelegator;

    private InternalApiServiceImplBase service;

    @Before
    public void before() {
        service = new InternalApiServiceImplBase();
        service.setServiceDelegator(serviceDelegator);
    }

    @Test
    @SuppressWarnings("resource")
    public void viewStatusTextDelegates() {
        Response expected = mock(Response.class);
        when(expected.getEntity()).thenReturn("OK I am here");
        when(serviceDelegator.viewStatusText()).thenReturn(expected);

        Response actual = service.viewStatusText();

        assertNotNull(actual);
        assertEquals("OK I am here", String.valueOf(actual.getEntity()));
        verify(serviceDelegator).viewStatusText();
    }
}