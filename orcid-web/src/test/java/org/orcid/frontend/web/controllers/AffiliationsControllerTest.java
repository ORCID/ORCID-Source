package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.pojo.OrgDisambiguated;
import org.springframework.test.util.ReflectionTestUtils;

public class AffiliationsControllerTest {
    
    @Mock
    private OrgDisambiguatedManager orgDisambiguatedManager;
    
    @InjectMocks
    private AffiliationsController affiliationsController;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testSearchDisambiguated() {
        OrgDisambiguatedManager mockOrgDisambiguatedManager = Mockito.mock(OrgDisambiguatedManager.class);
        OrgDisambiguatedManager oldOrgDisambiguatedManager = (OrgDisambiguatedManager) ReflectionTestUtils.getField(affiliationsController, "orgDisambiguatedManager");
        ReflectionTestUtils.setField(affiliationsController, "orgDisambiguatedManager", mockOrgDisambiguatedManager);
        
        Mockito.when(mockOrgDisambiguatedManager.searchOrgsFromSolr(Mockito.eq("search"), Mockito.eq(0), Mockito.eq(0), Mockito.eq(false))).thenReturn(getListOfMixedOrgsDiambiguated());
        
        List<Map<String, String>> results = affiliationsController.searchDisambiguated("search", 0);
        assertEquals(4, results.size());
        assertEquals("first", results.get(0).get("value"));
        assertEquals(OrgDisambiguatedSourceType.FUNDREF.name(), results.get(0).get("sourceType"));
        assertEquals("second", results.get(1).get("value"));
        assertEquals(OrgDisambiguatedSourceType.RINGGOLD.name(), results.get(1).get("sourceType"));
        assertEquals("third", results.get(2).get("value"));
        assertEquals(OrgDisambiguatedSourceType.GRID.name(), results.get(2).get("sourceType"));
        assertEquals("fourth", results.get(3).get("value"));
        assertEquals(OrgDisambiguatedSourceType.LEI.name(), results.get(3).get("sourceType"));
        
        ReflectionTestUtils.setField(affiliationsController, "orgDisambiguatedManager", oldOrgDisambiguatedManager);
    }

    private List<OrgDisambiguated> getListOfMixedOrgsDiambiguated() {
        OrgDisambiguated first = new OrgDisambiguated();
        first.setValue("first");
        first.setSourceType(OrgDisambiguatedSourceType.FUNDREF.name());
        
        OrgDisambiguated second = new OrgDisambiguated();
        second.setValue("second");
        second.setSourceType(OrgDisambiguatedSourceType.RINGGOLD.name());
        
        OrgDisambiguated third = new OrgDisambiguated();
        third.setValue("third");
        third.setSourceType(OrgDisambiguatedSourceType.GRID.name());
        
        OrgDisambiguated fourth = new OrgDisambiguated();
        fourth.setValue("fourth");
        fourth.setSourceType(OrgDisambiguatedSourceType.LEI.name());
        
        return Arrays.asList(first, second, third, fourth);
    }

}
