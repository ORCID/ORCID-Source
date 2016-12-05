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
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

public class OtherNameManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml");
    
    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
    private String claimedOrcid = "0000-0000-0000-0002";
    private String unclaimedOrcid = "0000-0000-0000-0001";
    
    @Mock
    private SourceManager sourceManager;
    
    @Resource 
    private OtherNameManager otherNameManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        otherNameManager.setSourceManager(sourceManager);
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }
    
    @Test
    public void testAddOtherNameToUnclaimedRecordPreserveOtherNameVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));   
        OtherName otherName = getOtherName();
        
        otherName = otherNameManager.createOtherName(unclaimedOrcid, otherName, true);
        otherName = otherNameManager.getOtherName(unclaimedOrcid, otherName.getPutCode());
        
        assertNotNull(otherName);
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());        
    }
    
    @Test
    public void testAddOtherNameToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));                
        OtherName otherName = getOtherName();
        
        otherName = otherNameManager.createOtherName(claimedOrcid, otherName, true);
        otherName = otherNameManager.getOtherName(claimedOrcid, otherName.getPutCode());
        
        assertNotNull(otherName);
        assertEquals(Visibility.LIMITED, otherName.getVisibility());       
    }
    
    @Test
    public void displayIndexIsSetTo_1_FromUI() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        OtherName otherName = getOtherName();
        otherName.setContent(otherName.getContent() + " fromUI");
        
        otherName = otherNameManager.createOtherName(claimedOrcid, otherName, false);
        otherName = otherNameManager.getOtherName(claimedOrcid, otherName.getPutCode());
        
        assertNotNull(otherName);
        assertEquals(Long.valueOf(1), otherName.getDisplayIndex());
    }
    
    @Test
    public void displayIndexIsSetTo_0_FromAPI() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        OtherName otherName = getOtherName();
        otherName.setContent(otherName.getContent() + " fromAPI");
        
        otherName = otherNameManager.createOtherName(claimedOrcid, otherName, true);
        otherName = otherNameManager.getOtherName(claimedOrcid, otherName.getPutCode());
        
        assertNotNull(otherName);
        assertEquals(Long.valueOf(0), otherName.getDisplayIndex());
    }
    
    private OtherName getOtherName() {
        OtherName otherName = new OtherName();
        otherName.setContent("other-name");
        otherName.setVisibility(Visibility.PUBLIC);
        return otherName;
    }
}
