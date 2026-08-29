package org.orcid.api.identifiers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import jakarta.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.api.identifiers.delegator.IdentifierApiServiceDelegator;
import org.orcid.pojo.IdentifierType;
import org.springframework.test.util.ReflectionTestUtils;

public class IdentifierApiServiceDelegatorTest {

    private IdentifierApiServiceImpl service;

    @Mock
    private IdentifierApiServiceDelegator serviceDelegator;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        service = new IdentifierApiServiceImpl();
        ReflectionTestUtils.setField(service, "serviceDelegator", serviceDelegator);
    }

    @SuppressWarnings("unchecked")
    private List<IdentifierType> getTypes(Response response) {
        return (List<IdentifierType>) response.getEntity();
    }
    
    @Test
    public void testviewIdentifierTypes() {
        IdentifierType doi = new IdentifierType();
        doi.setName("doi");
        doi.setDescription("doi: Digital object identifier");
        List<IdentifierType> mockedTypes = Collections.singletonList(doi);
        when(serviceDelegator.getIdentifierTypes("en")).thenReturn(Response.ok(mockedTypes).build());

        try (Response r = service.viewIdentifierTypes(null)) {
            assertEquals(200, r.getStatus());
            List<IdentifierType> types = getTypes(r);
            boolean found = false;
            for (IdentifierType t : types) {
                if (t.getName().equals("doi")) {
                    assertEquals("doi: Digital object identifier", t.getDescription());
                    found = true;
                }
            }
            assertTrue("no description for DOI found", found);
        }
    }
    
    @Test
    public void testviewIdentifierTypesWithLocale() {
        IdentifierType doi = new IdentifierType();
        doi.setName("doi");
        doi.setDescription("doi: Identificador de objeto digital");
        List<IdentifierType> mockedTypes = Collections.singletonList(doi);
        when(serviceDelegator.getIdentifierTypes("es")).thenReturn(Response.ok(mockedTypes).build());

        try (Response r = service.viewIdentifierTypes("es")) {
            assertEquals(200, r.getStatus());
            List<IdentifierType> types = getTypes(r);
            boolean found = false;
            for (IdentifierType t : types) {
                if (t.getName().equals("doi")) {
                    assertEquals("doi: Identificador de objeto digital", t.getDescription());
                    found = true;
                }
            }
            assertTrue("no description for DOI found", found);
        }
    }

    
}
