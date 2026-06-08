package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.frontend.sms.SmsPocRequest;
import org.orcid.frontend.sms.SmsPocResponse;
import org.orcid.frontend.sms.SmsPocService;

public class SmsPocControllerTest {

    @Mock
    private SmsPocService smsPocService;

    @InjectMocks
    private SmsPocController controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendDelegatesToSmsPocService() {
        SmsPocRequest request = new SmsPocRequest();
        request.setPhoneNumber("+50688888888");
        request.setMessage("ORCID SMS POC test");
        SmsPocResponse expected = SmsPocResponse.success("aws", "message-id", "+50688888888", "SENT");
        when(smsPocService.send(request)).thenReturn(expected);

        SmsPocResponse response = controller.send(request);

        assertTrue(response.isSuccess());
        assertEquals("aws", response.getProvider());
        verify(smsPocService).send(request);
    }
}
