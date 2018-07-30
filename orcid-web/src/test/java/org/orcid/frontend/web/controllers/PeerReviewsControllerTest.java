package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.pojo.grouping.PeerReviewGroup;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.collect.Lists;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml", "classpath:statistics-core-context.xml" })
public class PeerReviewsControllerTest extends BaseControllerTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
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
