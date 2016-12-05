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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
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
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.jaxb.model.common_rc4.Url;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc4.Relationship;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

public class ExternalIdentifierManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml");

    private static final String CLIENT_1_ID = "APP-5555555555555555";
    private static final String CLIENT_2_ID = "APP-5555555555555556";
    private String claimedOrcid = "0000-0000-0000-0002";
    private String unclaimedOrcid = "0000-0000-0000-0001";

    @Mock
    private SourceManager sourceManager;

    @Resource
    private ExternalIdentifierManager externalIdentifierManager;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        externalIdentifierManager.setSourceManager(sourceManager);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }

    @Test
    public void testAddExternalIdentifierToUnclaimedRecordPreserveExternalIdentifierVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        PersonExternalIdentifier extId = getExternalIdentifier();

        extId = externalIdentifierManager.createExternalIdentifier(unclaimedOrcid, extId, true);
        extId = externalIdentifierManager.getExternalIdentifier(unclaimedOrcid, extId.getPutCode());

        assertNotNull(extId);
        assertEquals(Visibility.PUBLIC, extId.getVisibility());
    }

    @Test
    public void testAddExternalIdentifierToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        PersonExternalIdentifier extId = getExternalIdentifier();

        extId = externalIdentifierManager.createExternalIdentifier(claimedOrcid, extId, true);
        extId = externalIdentifierManager.getExternalIdentifier(claimedOrcid, extId.getPutCode());

        assertNotNull(extId);
        assertEquals(Visibility.LIMITED, extId.getVisibility());
    }

    @Test
    public void testAddEqualsExternalIdentifiersFromDifferentSource() {
        PersonExternalIdentifier extId = getExternalIdentifier();
        extId.setType(extId.getType() + System.currentTimeMillis());
        // Create from client # 1
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        PersonExternalIdentifier extId1 = externalIdentifierManager.createExternalIdentifier(claimedOrcid, extId, true);
        assertNotNull(extId1);
        assertNotNull(extId1.getPutCode());
        
        // Create from client # 2
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_2_ID)));
        PersonExternalIdentifier extId2 = externalIdentifierManager.createExternalIdentifier(claimedOrcid, extId, true);
        assertNotNull(extId2);
        assertNotNull(extId2.getPutCode());
        
        // Verify both ext ids are not the same one
        assertNotEquals(extId1.getPutCode(), extId2.getPutCode());        
    }
    
    @Test(expected = OrcidDuplicatedElementException.class)
    public void testAddEqualsExternalIdentifiersFromSameSource() {
        PersonExternalIdentifier extId = getExternalIdentifier();
        extId.setType(extId.getType() + System.currentTimeMillis());
        // Create from client # 1
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        PersonExternalIdentifier extId1 = externalIdentifierManager.createExternalIdentifier(claimedOrcid, extId, true);
        assertNotNull(extId1);
        assertNotNull(extId1.getPutCode());
        
        // Remove the put code and the source
        extId1.setPutCode(null);
        extId1.setSource(null);
        externalIdentifierManager.createExternalIdentifier(claimedOrcid, extId, true);
        fail();
    }
    
    @Test
    public void displayIndexIsSetTo_1_FromUI() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        PersonExternalIdentifier extId = getExternalIdentifier();
        extId.setType(extId.getType() + System.currentTimeMillis());
        PersonExternalIdentifier extId1 = externalIdentifierManager.createExternalIdentifier(claimedOrcid, extId, false);
        extId1 = externalIdentifierManager.getExternalIdentifier(claimedOrcid, extId1.getPutCode());
        
        assertNotNull(extId1);
        assertEquals(Long.valueOf(1), extId1.getDisplayIndex());
    }
    
    @Test
    public void displayIndexIsSetTo_0_FromAPI() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        PersonExternalIdentifier extId = getExternalIdentifier();
        extId.setType(extId.getType() + System.currentTimeMillis());
        PersonExternalIdentifier extId1 = externalIdentifierManager.createExternalIdentifier(claimedOrcid, extId, true);
        extId1 = externalIdentifierManager.getExternalIdentifier(claimedOrcid, extId1.getPutCode());
        
        assertNotNull(extId1);
        assertEquals(Long.valueOf(0), extId1.getDisplayIndex());
    }
    
    private PersonExternalIdentifier getExternalIdentifier() {
        PersonExternalIdentifier extId = new PersonExternalIdentifier();
        extId.setRelationship(Relationship.SELF);
        extId.setType("person-ext-id-type");
        extId.setValue("person-ext-id-value");
        extId.setUrl(new Url("http://orcid.org"));
        extId.setVisibility(Visibility.PUBLIC);
        return extId;
    }
}
