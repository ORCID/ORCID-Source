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
import org.orcid.jaxb.model.common_rc2.Iso3166Country;
import org.orcid.jaxb.model.common_rc2.Organization;
import org.orcid.jaxb.model.common_rc2.OrganizationAddress;
import org.orcid.jaxb.model.common_rc2.Title;
import org.orcid.jaxb.model.common_rc2.Url;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.ExternalIDs;
import org.orcid.jaxb.model.record_rc2.Funding;
import org.orcid.jaxb.model.record_rc2.FundingTitle;
import org.orcid.jaxb.model.record_rc2.FundingType;
import org.orcid.jaxb.model.record_rc2.Relationship;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

public class ProfileFundingManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml");
    
    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
    private String claimedOrcid = "0000-0000-0000-0002";
    private String unclaimedOrcid = "0000-0000-0000-0001";
    
    @Mock
    private SourceManager sourceManager;
    
    @Resource 
    private ProfileFundingManager profileFundingManager;
    
    @Resource
    private ProfileFundingDao profileFundingDao;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        profileFundingManager.setSourceManager(sourceManager);
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
        Funding funding = getFunding(null);
        
        funding = profileFundingManager.createFunding(unclaimedOrcid, funding, true);
        funding = profileFundingManager.getFunding(unclaimedOrcid, funding.getPutCode());
        
        assertNotNull(funding);
        assertEquals("Funding title", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.PUBLIC, funding.getVisibility());        
    }
    
    @Test
    public void testAddFundingToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));                
        Funding funding = getFunding(null);
        
        funding = profileFundingManager.createFunding(claimedOrcid, funding, true);
        funding = profileFundingManager.getFunding(claimedOrcid, funding.getPutCode());
        
        assertNotNull(funding);
        assertEquals("Funding title", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.LIMITED, funding.getVisibility());        
    }
    
    @Test
    public void testAddMultipleModifiesIndexingStatus() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));                
        Funding f1 = getFunding("F1");
        f1 = profileFundingManager.createFunding(claimedOrcid, f1, true);
        
        Funding f2 = getFunding("F2");
        f2 = profileFundingManager.createFunding(claimedOrcid, f2, true);
        
        Funding f3 = getFunding("F3");
        f3 = profileFundingManager.createFunding(claimedOrcid, f3, true);
        
        ProfileFundingEntity entity1 = profileFundingDao.find(f1.getPutCode());
        ProfileFundingEntity entity2 = profileFundingDao.find(f2.getPutCode());
        ProfileFundingEntity entity3 = profileFundingDao.find(f3.getPutCode());
        
        assertNotNull(entity1.getDisplayIndex());
        assertNotNull(entity2.getDisplayIndex());
        assertNotNull(entity3.getDisplayIndex());
        assertEquals(Long.valueOf(0), entity3.getDisplayIndex());
        
        //Rollback all changes
        profileFundingDao.remove(entity1.getId());
        profileFundingDao.remove(entity2.getId());
        profileFundingDao.remove(entity3.getId());
    } 
    
    @Test
    public void displayIndexIsSetTo_1_FromUI() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        
        Funding f1 = getFunding("fromUI-1");
        f1 = profileFundingManager.createFunding(claimedOrcid, f1, false);
        ProfileFundingEntity f = profileFundingDao.find(f1.getPutCode());
        
        assertNotNull(f);
        assertEquals(Long.valueOf(1), f.getDisplayIndex());        
    }
    
    @Test
    public void displayIndexIsSetTo_0_FromAPI() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        
        Funding f1 = getFunding("fromAPI-1");
        f1 = profileFundingManager.createFunding(claimedOrcid, f1, true);
        ProfileFundingEntity f = profileFundingDao.find(f1.getPutCode());
        
        assertNotNull(f);
        assertEquals(Long.valueOf(0), f.getDisplayIndex());
    }
    
    private Funding getFunding(String grantNumber) {
        Funding funding = new Funding();
        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("grant_number");
        extId.setUrl(new Url("http://orcid.org"));
        if(grantNumber == null) {
            extId.setValue("ext-id-value");
        } else {
            extId.setValue(grantNumber);
        }
        
        extIds.getExternalIdentifier().add(extId);
        funding.setExternalIdentifiers(extIds);
        
        FundingTitle title = new FundingTitle();
        if(grantNumber == null) {
            title.setTitle(new Title("Funding title"));
        } else {
            title.setTitle(new Title("Funding title " + grantNumber));
        }        
        funding.setTitle(title);
        
        Organization org = new Organization();
        org.setName("org-name");
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        org.setAddress(address);
        funding.setOrganization(org);
        funding.setVisibility(Visibility.PUBLIC);
        funding.setType(FundingType.AWARD);
        return funding;
    }
}
