package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.pojo.PIDPojo;
import org.springframework.http.ResponseEntity;

/**
 * The controller is a two line wrapper around {@link PIDNormalizationService}.
 * What belongs here is that it copies both answers into the pojo, and that it
 * only asks for a URL when normalisation produced a value. The normalisation
 * rules themselves (what "ISSN: 1234-1234" becomes) belong to
 * PIDNormalizationService, which owns a component scan of every Normalizer and
 * cannot be assembled without a Spring context.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class PIDControllerTest {

    @Mock
    private PIDNormalizationService normService;

    @InjectMocks
    private PIDController controller = new PIDController();

    @Test
    public void testNorm() {
        when(normService.normalise("doi", "10.1/123")).thenReturn("10.1/123");
        when(normService.generateNormalisedURL("doi", "10.1/123")).thenReturn("https://doi.org/10.1/123");
        when(normService.normalise("issn", "ISSN: 1234-1234")).thenReturn("1234-1234");
        when(normService.generateNormalisedURL("issn", "ISSN: 1234-1234")).thenReturn("https://portal.issn.org/resource/ISSN/1234-1234");

        ResponseEntity<PIDPojo> norm = controller.getNormalized("doi", "10.1/123");
        assertEquals("https://doi.org/10.1/123", norm.getBody().getNormUrl());
        assertEquals("10.1/123", norm.getBody().getNormValue());
        assertEquals("doi", norm.getBody().getIdType());
        assertEquals("10.1/123", norm.getBody().getIdValue());

        ResponseEntity<PIDPojo> norm2 = controller.getNormalized("issn", "ISSN: 1234-1234");
        assertEquals("https://portal.issn.org/resource/ISSN/1234-1234", norm2.getBody().getNormUrl());
        assertEquals("1234-1234", norm2.getBody().getNormValue());
        assertEquals("issn", norm2.getBody().getIdType());
        assertEquals("ISSN: 1234-1234", norm2.getBody().getIdValue());
    }

    @Test
    public void testNormUnrecognisedValueDoesNotBuildAUrl() {
        when(normService.normalise("doi", "not a doi")).thenReturn("");

        ResponseEntity<PIDPojo> norm = controller.getNormalized("doi", "not a doi");

        assertEquals("", norm.getBody().getNormValue());
        assertEquals("", norm.getBody().getNormUrl());
        verify(normService, never()).generateNormalisedURL("doi", "not a doi");
    }
}
