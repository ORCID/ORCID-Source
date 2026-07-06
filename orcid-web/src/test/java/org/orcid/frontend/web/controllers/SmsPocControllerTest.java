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
import org.orcid.frontend.sms.SmsVerificationCheckRequest;

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
    public void sendDelegatesToStartVerification() {
        SmsPocRequest request = new SmsPocRequest();
        request.setPhoneNumber("+50688888888");
        SmsPocResponse expected = SmsPocResponse.success("aws", "message-id", "+50688888888", "SENT");
        when(smsPocService.startVerification(request)).thenReturn(expected);

        SmsPocResponse response = controller.send(request);

        assertTrue(response.isSuccess());
        assertEquals("aws", response.getProvider());
        verify(smsPocService).startVerification(request);
    }

    @Test
    public void verifyDelegatesToCheckVerification() {
        SmsVerificationCheckRequest request = new SmsVerificationCheckRequest();
        request.setPhoneNumber("+50688888888");
        request.setCode("123456");
        SmsPocResponse expected = SmsPocResponse.verificationResult("aws", "message-id", "+50688888888", true, "VERIFIED");
        when(smsPocService.checkVerification(request)).thenReturn(expected);

        SmsPocResponse response = controller.verify(request);

        assertTrue(response.isSuccess());
        assertEquals(Boolean.TRUE, response.getVerified());
        verify(smsPocService).checkVerification(request);
    }
}
