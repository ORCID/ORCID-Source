package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.v3.release.record.Record;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PublicRecordApiControllerTest {

    private static final String ORCID = "0000-0000-0000-0001";

    @Mock
    private RecordManagerReadOnly recordManagerReadOnly;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private PublicRecordApiController controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testViewRecord() throws Exception {
        Record record = new Record();
        when(recordManagerReadOnly.getPublicRecord(eq(ORCID), anyBoolean())).thenReturn(record);

        String result = controller.viewRecord(request, ORCID);

        assertNotNull(result);
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(request).setAttribute(SourceEntityUtils.DO_NOT_POPULATE_SOURCES, true);
        verify(recordManagerReadOnly).getPublicRecord(ORCID, false);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule());
        String expectedJson = mapper.writeValueAsString(record);
        assertEquals(expectedJson, result);
    }

    @Test(expected = SecurityException.class)
    public void testViewRecordSecurityException() throws Exception {
        doThrow(new SecurityException()).when(orcidSecurityManager).checkProfile(ORCID);
        controller.viewRecord(request, ORCID);
    }

}
