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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.common_rc2.Iso3166Country;
import org.orcid.jaxb.model.common_rc2.Organization;
import org.orcid.jaxb.model.common_rc2.OrganizationAddress;
import org.orcid.jaxb.model.common_rc2.Title;
import org.orcid.jaxb.model.common_rc2.Url;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.ExternalIDs;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.PeerReviewType;
import org.orcid.jaxb.model.record_rc2.Relationship;
import org.orcid.jaxb.model.record_rc2.Role;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class PeerReviewManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml");
    
    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
    private String claimedOrcid = "0000-0000-0000-0002";
    private String unclaimedOrcid = "0000-0000-0000-0001";
    
    @Mock
    private SourceManager sourceManager;
    
    @Resource 
    private PeerReviewManager peerReviewManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        peerReviewManager.setSourceManager(sourceManager);
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }
    
    @Test
    public void testAddFundingToUnclaimedRecordPreserveFundingVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));   
        PeerReview peer = getPeerReview();
        
        peer = peerReviewManager.createPeerReview(unclaimedOrcid, peer, true);
        peer = peerReviewManager.getPeerReview(unclaimedOrcid, peer.getPutCode());
        
        assertNotNull(peer);
        assertEquals(Visibility.PUBLIC, peer.getVisibility());        
    }
    
    @Test
    public void testAddFundingToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));                
        PeerReview peer = getPeerReview();
        
        peer = peerReviewManager.createPeerReview(claimedOrcid, peer, true);
        peer = peerReviewManager.getPeerReview(claimedOrcid, peer.getPutCode());
        
        assertNotNull(peer);
        assertEquals(Visibility.LIMITED, peer.getVisibility());       
    }
    
    private PeerReview getPeerReview() {
        PeerReview peerReview = new PeerReview();
        peerReview.setRole(Role.CHAIR);
        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://orcid.org"));
        extId.setValue("ext-id-value");
        extIds.getExternalIdentifier().add(extId);
        peerReview.setExternalIdentifiers(extIds);        
        peerReview.setSubjectContainerName(new Title("Peer review title"));
        peerReview.setSubjectExternalIdentifier(extId);
        
        Organization org = new Organization();
        org.setName("org-name");
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        org.setAddress(address);
        
        peerReview.setOrganization(org);
        peerReview.setType(PeerReviewType.EVALUATION);
        peerReview.setVisibility(Visibility.PUBLIC);
        
        return peerReview;
    }
}
