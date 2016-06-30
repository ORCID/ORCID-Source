/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import org.orcid.jaxb.model.common_rc2.Url;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

public class ResearcherUrlManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml");

    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
    private String claimedOrcid = "0000-0000-0000-0002";
    private String unclaimedOrcid = "0000-0000-0000-0001";

    @Mock
    private SourceManager sourceManager;

    @Resource
    private ResearcherUrlManager researcherUrlManager;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        researcherUrlManager.setSourceManager(sourceManager);
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
    
    private ResearcherUrl getResearcherUrl() {
        ResearcherUrl rUrl = new ResearcherUrl();
        rUrl.setUrl(new Url("http://orcid.org"));
        rUrl.setUrlName("ORCID Site");
        rUrl.setVisibility(Visibility.PUBLIC);
        return rUrl;
    }
}
