package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.pojo.OrgDisambiguated;
import org.orcid.pojo.grouping.PeerReviewGroup;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml", "classpath:statistics-core-context.xml" })
public class PeerReviewsControllerTest extends BaseControllerTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/OrgsEntityData.xml",
            "/data/OrgAffiliationEntityData.xml", "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml");

    @Resource
    protected OrcidProfileManager orcidProfileManager;

    @Resource
    protected PeerReviewsController peerReviewsController;
    
    @Resource
    private OrcidUserDetailsService orcidUserDetailsService;

    @Mock
    private HttpServletRequest servletRequest;

    @Before
    public void init() {
        orcidProfileManager.updateLastModifiedDate("4444-4444-4444-4446");
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Lists.reverse(DATA_FILES));
    }
    
    @Test
    public void testSearchDisambiguated() {
        OrgDisambiguatedManager mockOrgDisambiguatedManager = Mockito.mock(OrgDisambiguatedManager.class);
        OrgDisambiguatedManager oldOrgDisambiguatedManager = (OrgDisambiguatedManager) ReflectionTestUtils.getField(peerReviewsController, "orgDisambiguatedManager");
        ReflectionTestUtils.setField(peerReviewsController, "orgDisambiguatedManager", mockOrgDisambiguatedManager);
        
        Mockito.when(mockOrgDisambiguatedManager.searchOrgsFromSolr(Mockito.eq("search"), Mockito.eq(0), Mockito.eq(0), Mockito.eq(false))).thenReturn(getListOfMixedOrgsDiambiguated());
        
        List<Map<String, String>> results = peerReviewsController.searchDisambiguated("search", 0);
        assertEquals(4, results.size());
        assertEquals("first", results.get(0).get("value"));
        assertEquals("second", results.get(1).get("value"));
        assertEquals("third", results.get(2).get("value"));
        assertEquals("fourth", results.get(3).get("value"));
        
        ReflectionTestUtils.setField(peerReviewsController, "orgDisambiguatedManager", oldOrgDisambiguatedManager);
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
    
    @Test
    public void testGetPeerReviews() {
        List<PeerReviewGroup> groups = peerReviewsController.getPeerReviewsJson(true);
        assertNotNull(groups);
        assertEquals(4, groups.size());
        assertNotNull(groups.get(0).getPeerReviewDuplicateGroups());
        assertNotNull(groups.get(1).getPeerReviewDuplicateGroups());
        assertNotNull(groups.get(2).getPeerReviewDuplicateGroups());
        assertNotNull(groups.get(3).getPeerReviewDuplicateGroups());
        assertEquals(1, groups.get(0).getPeerReviewDuplicateGroups().size());
        assertEquals(1, groups.get(1).getPeerReviewDuplicateGroups().size());
        assertEquals(1, groups.get(2).getPeerReviewDuplicateGroups().size());
        assertEquals(1, groups.get(3).getPeerReviewDuplicateGroups().size());
        
        assertTrue(groups.get(0).getName().compareTo(groups.get(1).getName()) < 0);
        assertTrue(groups.get(1).getName().compareTo(groups.get(2).getName()) < 0);
        assertTrue(groups.get(2).getName().compareTo(groups.get(3).getName()) < 0);
        
        groups = peerReviewsController.getPeerReviewsJson(false);
        assertNotNull(groups);
        assertEquals(4, groups.size());
        assertNotNull(groups.get(0).getPeerReviewDuplicateGroups());
        assertNotNull(groups.get(1).getPeerReviewDuplicateGroups());
        assertNotNull(groups.get(2).getPeerReviewDuplicateGroups());
        assertNotNull(groups.get(3).getPeerReviewDuplicateGroups());
        assertEquals(1, groups.get(0).getPeerReviewDuplicateGroups().size());
        assertEquals(1, groups.get(1).getPeerReviewDuplicateGroups().size());
        assertEquals(1, groups.get(2).getPeerReviewDuplicateGroups().size());
        assertEquals(1, groups.get(3).getPeerReviewDuplicateGroups().size());
        
        assertTrue(groups.get(0).getName().compareTo(groups.get(1).getName()) > 0);
        assertTrue(groups.get(1).getName().compareTo(groups.get(2).getName()) > 0);
        assertTrue(groups.get(2).getName().compareTo(groups.get(3).getName()) > 0);
    }

}
