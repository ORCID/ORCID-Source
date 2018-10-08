package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.jaxb.model.v3.rc2.common.Url;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrls;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.TargetProxyHelper;

public class ResearcherUrlManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml");

    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
    private String claimedOrcid = "0000-0000-0000-0002";
    private String unclaimedOrcid = "0000-0000-0000-0001";

    @Mock
    private SourceManager sourceManager;

    @Resource(name = "researcherUrlManagerV3")
    private ResearcherUrlManager researcherUrlManager;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        TargetProxyHelper.injectIntoProxy(researcherUrlManager, "sourceManager", sourceManager);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }

    @Test
    public void testAddResearcherUrlToUnclaimedRecordPreserveResearcherUrlVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        ResearcherUrl rUrl = getResearcherUrl();
        
        rUrl = researcherUrlManager.createResearcherUrl(unclaimedOrcid, rUrl, true);
        rUrl = researcherUrlManager.getResearcherUrl(unclaimedOrcid, rUrl.getPutCode());

        assertNotNull(rUrl);
        assertEquals(Visibility.PUBLIC, rUrl.getVisibility());
    }

    @Test
    public void testAddResearcherUrToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        ResearcherUrl rUrl = getResearcherUrl();
        
        rUrl = researcherUrlManager.createResearcherUrl(claimedOrcid, rUrl, true);
        rUrl = researcherUrlManager.getResearcherUrl(claimedOrcid, rUrl.getPutCode());

        assertNotNull(rUrl);
        assertEquals(Visibility.LIMITED, rUrl.getVisibility());
    }

    @Test
    public void displayIndexIsSetTo_1_FromUI() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));        
        ResearcherUrl rUrl = getResearcherUrl();
        rUrl.getUrl().setValue(rUrl.getUrl().getValue() + "/fromUI");
        
        rUrl = researcherUrlManager.createResearcherUrl(claimedOrcid, rUrl, false);
        rUrl = researcherUrlManager.getResearcherUrl(claimedOrcid, rUrl.getPutCode());
        
        assertNotNull(rUrl);
        assertEquals(Long.valueOf(1), rUrl.getDisplayIndex());
    }
    
    @Test
    public void displayIndexIsSetTo_0_FromAPI() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));        
        ResearcherUrl rUrl = getResearcherUrl();
        rUrl.getUrl().setValue(rUrl.getUrl().getValue() + "/fromAPI");
        
        rUrl = researcherUrlManager.createResearcherUrl(claimedOrcid, rUrl, true);
        rUrl = researcherUrlManager.getResearcherUrl(claimedOrcid, rUrl.getPutCode());
                
        assertNotNull(rUrl);
        assertEquals(Long.valueOf(0), rUrl.getDisplayIndex());
    }
    
    @Test
    public void getAllTest() {
        String orcid = "0000-0000-0000-0003";
        ResearcherUrls elements = researcherUrlManager.getResearcherUrls(orcid);
        assertNotNull(elements);
        assertNotNull(elements.getResearcherUrls());
        assertEquals(5, elements.getResearcherUrls().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false;
        for(ResearcherUrl element : elements.getResearcherUrls()) {
            if(13 == element.getPutCode()){
                found1 = true;
            } else if(14 == element.getPutCode()){
                found2 = true;
            } else if(15 == element.getPutCode()){
                found3 = true;
            } else if(16 == element.getPutCode()){
                found4 = true;
            } else if(17 == element.getPutCode()){
                found5 = true;
            } else {
                fail("Invalid element found: " + element.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        assertTrue(found5);
    }
    
    @Test
    public void getPublicTest() {
        String orcid = "0000-0000-0000-0003";        
        ResearcherUrls elements = researcherUrlManager.getPublicResearcherUrls(orcid);
        assertNotNull(elements);
        assertNotNull(elements.getResearcherUrls());
        assertEquals(1, elements.getResearcherUrls().size());
        assertEquals(Long.valueOf(13), elements.getResearcherUrls().get(0).getPutCode());
    }
    
    private ResearcherUrl getResearcherUrl() {
        ResearcherUrl rUrl = new ResearcherUrl();
        rUrl.setUrl(new Url("http://orcid.org"));
        rUrl.setUrlName("ORCID Site");
        rUrl.setVisibility(Visibility.PUBLIC);
        return rUrl;
    }
}
