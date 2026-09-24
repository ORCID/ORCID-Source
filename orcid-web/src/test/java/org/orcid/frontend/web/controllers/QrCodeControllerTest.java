package org.orcid.frontend.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class QrCodeControllerTest {

    private static final String ORCID = "0000-0000-0000-0001";
    private static final String BASE_URI = "https://orcid.org";

    @Spy
    @InjectMocks
    private QrCodeController controller;

    @Mock
    private UserDetails userDetails;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        doReturn(BASE_URI).when(controller).getBaseUri();
    }

    @Test
    public void testMyOrcidQrCode() {
        ModelAndView mav = controller.myOrcidQrCode();
        assertNotNull(mav);
        assertEquals("my_orcid_qr_code", mav.getViewName());
    }

    @Test
    public void testGenerateQrCodeAuthenticated() {
        when(userDetails.getUsername()).thenReturn(ORCID);
        doReturn(userDetails).when(controller).getCurrentUser();

        byte[] qrCode = controller.generateQrCode();
        assertNotNull(qrCode);
        assertTrue(qrCode.length > 0);
    }

    @Test
    public void testGenerateQrCodeUnauthenticated() {
        doReturn(null).when(controller).getCurrentUser();

        byte[] qrCode = controller.generateQrCode();
        assertNotNull(qrCode);
        assertEquals(0, qrCode.length);
    }

    @Test
    public void testQrCodeForDownloadAuthenticated() {
        when(userDetails.getUsername()).thenReturn(ORCID);
        doReturn(userDetails).when(controller).getCurrentUser();

        byte[] qrCode = controller.qrCodeForDownload();
        assertNotNull(qrCode);
        assertTrue(qrCode.length > 0);
    }

    @Test
    public void testQrCodeForDownloadUnauthenticated() {
        doReturn(null).when(controller).getCurrentUser();

        byte[] qrCode = controller.qrCodeForDownload();
        assertNotNull(qrCode);
        assertEquals(0, qrCode.length);
    }
}
