package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.manager.v3.OrgManager;
import org.orcid.pojo.OrgDisambiguated;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.Silent.class)
public class OrgControllerTest {

    @Mock
    private OrgDisambiguatedManager orgDisambiguatedManager;

    /** Declared on the controller but not reached by the endpoint under test. */
    @Mock
    private OrgManager orgManager;

    @InjectMocks
    private OrgController orgController = new OrgController();

    @Test
    public void testFindBySourceTypeAndSourceId() {
        when(orgDisambiguatedManager.findInDB("abc456", "WDB")).thenReturn(orgDisambiguated());
        when(orgDisambiguatedManager.findInDB("no", "no")).thenReturn(null);

        ResponseEntity<OrgDisambiguated> o = orgController.getDisambiguatedOrg("WDB", "abc456");

        assertEquals("abc456", o.getBody().getSourceId());
        assertEquals("WDB", o.getBody().getSourceType());
        assertEquals("London", o.getBody().getCity());
        assertEquals("An Institution", o.getBody().getValue());
        assertEquals("GB", o.getBody().getCountry());
        assertEquals(200, o.getStatusCodeValue());

        ResponseEntity<OrgDisambiguated> o2 = orgController.getDisambiguatedOrg("no", "no");
        assertEquals(404, o2.getStatusCodeValue());
        assertNull(o2.getBody());
    }

    private OrgDisambiguated orgDisambiguated() {
        OrgDisambiguated org = new OrgDisambiguated();
        org.setSourceId("abc456");
        org.setSourceType("WDB");
        org.setCity("London");
        org.setValue("An Institution");
        org.setCountry("GB");
        return org;
    }
}
