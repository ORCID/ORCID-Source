package org.orcid.frontend.web.controllers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.jena.ext.com.google.common.collect.Sets;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.v3.ResearchResourceManager;
import org.orcid.frontend.web.pagination.Page;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.jaxb.model.v3.rc1.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceGroup;
import org.orcid.pojo.ResearchResourceGroupPojo;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.collect.Lists;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml", "classpath:statistics-core-context.xml" })
public class ResearchResourceControllerTest extends BaseControllerTest{

    /*
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/WebhookEntityData.xml", "/data/RecordNameEntityData.xml");
     */
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/OrgsEntityData.xml", "/data/ResearchResourceEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml");

    private static String OTHER_USER_ORCID = "4444-4444-4444-4446";

    @Resource
    private ResearchResourcesController controller;
    
    @Resource(name = "researchResourceManagerV3")
    private ResearchResourceManager researchResourceManager;

    @Resource
    protected OrcidProfileManager orcidProfileManager;

    @Captor
    private ArgumentCaptor<List<Long>> idsCaptor;
    
    @Mock
    private HttpServletRequest servletRequest;
    
    @Before
    public void init() {
        orcidProfileManager.updateLastModifiedDate("4444-4444-4444-4446");
        assertNotNull(controller);
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Lists.reverse(DATA_FILES));
    }
    
    //Get the index of the group with 2 resources in it.
    public int getBigGroupIndex(Page<ResearchResourceGroupPojo> page){
        if (page.getWorkGroups().get(0).getResearchResources().size() == 2)
            return 0;
        else 
            return 1;
    }

    //Get the index of the group with 1 resources in it.
    public int getSmallGroupIndex(Page<ResearchResourceGroupPojo> page){
        if (page.getWorkGroups().get(0).getResearchResources().size() == 2)
            return 1;
        else 
            return 0;
    }

    @Test
    public void testReadPage(){
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        Page<ResearchResourceGroupPojo> page = controller.getresearchResourcePage(0);
        assertNotNull(page);
        assertEquals(2, page.getTotalGroups());
        assertEquals(2, page.getWorkGroups().size());
        
        //check we have one group of one and one group of two.
        Set<Integer> setI = new HashSet<Integer>();
        setI.add(page.getWorkGroups().get(0).getResearchResources().size());
        setI.add(page.getWorkGroups().get(1).getResearchResources().size());
        assertEquals(setI, Sets.newHashSet(1,2));
        
        int bg = getBigGroupIndex(page); //has 2 rr
        int sg = getSmallGroupIndex(page); // has 1 rr
        
        //grouped dude
        assertEquals("work:external-identifier-id#1",page.getWorkGroups().get(bg).getWorkExternalIdentifiers().get(0).getExternalIdentifierId().getValue());
        Set<String> titles = new HashSet<String>();
        titles.add(page.getWorkGroups().get(bg).getResearchResources().get(0).getProposal().getTitle().getTitle().getContent());
        titles.add(page.getWorkGroups().get(bg).getResearchResources().get(1).getProposal().getTitle().getTitle().getContent());
        assertEquals(titles, Sets.newHashSet("the title","the title2"));
        
        //in the big group, rr 1 has an org, 2 has higher display index (so is the default and should be first in the list).
        assertEquals(Long.valueOf(2l), page.getWorkGroups().get(bg).getResearchResources().get(0).getPutCode());
        assertEquals("An institution",page.getWorkGroups().get(bg).getResearchResources().get(1).getProposal().getHosts().getOrganization().get(0).getName());            
        assertEquals(page.getWorkGroups().get(bg).getResearchResources().get(0),page.getWorkGroups().get(bg).getDefaultActivity());
        
        //ungrouped dude
        assertEquals("work:external-identifier-id#2",page.getWorkGroups().get(sg).getWorkExternalIdentifiers().get(0).getExternalIdentifierId().getValue());
        assertEquals("the title3",page.getWorkGroups().get(sg).getResearchResources().get(0).getProposal().getTitle().getTitle().getContent());
        assertEquals("the title3",page.getWorkGroups().get(sg).getDefaultActivity().getProposal().getTitle().getTitle().getContent());        
        assertEquals(Long.valueOf(3),page.getWorkGroups().get(sg).getDefaultActivity().getPutCode());
        assertEquals("PUBLIC",page.getWorkGroups().get(sg).getDefaultActivity().getVisibility().name());
        assertEquals("3",page.getWorkGroups().get(sg).getDefaultActivity().getDisplayIndex());
        
        this.testDeleteOne();
    }
    
    @Test
    public void testReadOne(){
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        ResearchResource rr = controller.getResearchResource(1);
        
        assertEquals("work:external-identifier-id#1",rr.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("the title",rr.getProposal().getTitle().getTitle().getContent());
        assertEquals(Long.valueOf(1),rr.getPutCode());
        assertEquals("PUBLIC",rr.getVisibility().name());
        assertEquals(2,rr.getResourceItems().size());
        assertEquals("the resource name1",rr.getResourceItems().get(0).getName());
        assertEquals("An institution",rr.getResourceItems().get(0).getHosts().getOrganization().get(0).getName());
        assertEquals("Another Institution",rr.getResourceItems().get(0).getHosts().getOrganization().get(1).getName());
    }

    @Test
    public void testUpdateVis(){
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        
        controller.updateVisibilitys("2", "private");
        ResearchResource rrUpdated = controller.getResearchResource(2);
        assertEquals("PRIVATE",rrUpdated.getVisibility().name());
        
        controller.updateVisibilitys("2", "public");
        ResearchResource rrLast = controller.getResearchResource(2);
        assertEquals("PUBLIC",rrLast.getVisibility().name());
    }

    @Test
    public void testUpdateMax(){
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        
        Page<ResearchResourceGroupPojo> pageBefore = controller.getresearchResourcePage(0);
        int bgBefore = getBigGroupIndex(pageBefore);
        assertEquals(Long.valueOf(2l),pageBefore.getWorkGroups().get(bgBefore).getDefaultActivity().getPutCode());
        assertEquals("2",pageBefore.getWorkGroups().get(bgBefore).getDefaultActivity().getDisplayIndex());
        
        controller.updateToMaxDisplay(1l);
        Page<ResearchResourceGroupPojo> pageAfter = controller.getresearchResourcePage(0);
        int bgAfter = getBigGroupIndex(pageAfter);
        assertEquals("4",pageAfter.getWorkGroups().get(bgAfter).getDefaultActivity().getDisplayIndex());
        assertEquals(Long.valueOf(1l),pageAfter.getWorkGroups().get(bgAfter).getDefaultActivity().getPutCode());

        //set back
        controller.updateToMaxDisplay(2l);
        Page<ResearchResourceGroupPojo> pageLast = controller.getresearchResourcePage(0);
        int bgLast = getBigGroupIndex(pageLast);
        assertEquals(Long.valueOf(2l),pageLast.getWorkGroups().get(bgLast).getDefaultActivity().getPutCode());
    }

   
    //note, invoked by testReadPage to prevent ordering issues
    public void testDeleteOne(){
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
        controller.removeWork("3");
        try {
            ResearchResource rrDeleted = controller.getResearchResource(3);
            fail();
        } catch (NoResultException nre) {

        }
    }


}
