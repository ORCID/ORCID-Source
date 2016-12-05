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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.junit.Assert.assertThat;
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
import org.orcid.jaxb.model.common_rc4.Iso3166Country;
import org.orcid.jaxb.model.common_rc4.Organization;
import org.orcid.jaxb.model.common_rc4.OrganizationAddress;
import org.orcid.jaxb.model.common_rc4.Title;
import org.orcid.jaxb.model.common_rc4.Url;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.record.summary_rc4.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc4.Fundings;
import org.orcid.jaxb.model.record_rc4.ExternalID;
import org.orcid.jaxb.model.record_rc4.ExternalIDs;
import org.orcid.jaxb.model.record_rc4.Funding;
import org.orcid.jaxb.model.record_rc4.FundingTitle;
import org.orcid.jaxb.model.record_rc4.FundingType;
import org.orcid.jaxb.model.record_rc4.Relationship;
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
    
    @Test
    public void testGroupFundings() {
        /**
         * They should be grouped as
         * 
         * Group 1: Funding 1 + Funding 4
         * Group 2: Funding 2 + Funding 5
         * Group 3: Funding 3
         * Group 4: Funding 6
         * */
        FundingSummary s1 = getFundingSummary("Funding 1", "ext-id-1", Visibility.PUBLIC);
        FundingSummary s2 = getFundingSummary("Funding 2", "ext-id-2", Visibility.LIMITED);
        FundingSummary s3 = getFundingSummary("Funding 3", "ext-id-3", Visibility.PRIVATE);
        FundingSummary s4 = getFundingSummary("Funding 4", "ext-id-1", Visibility.PRIVATE);
        FundingSummary s5 = getFundingSummary("Funding 5", "ext-id-2", Visibility.PUBLIC);
        FundingSummary s6 = getFundingSummary("Funding 6", "ext-id-4", Visibility.PRIVATE);
        
        List<FundingSummary> fundingList1 = Arrays.asList(s1, s2, s3, s4, s5, s6); 
        
        Fundings fundings1 = profileFundingManager.groupFundings(fundingList1, false);
        assertNotNull(fundings1);
        assertEquals(4, fundings1.getFundingGroup().size());
        //Group 1 have all with ext-id-1
        assertEquals(2, fundings1.getFundingGroup().get(0).getFundingSummary().size());
        assertEquals(1, fundings1.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-1", fundings1.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        //Group 2 have all with ext-id-2
        assertEquals(2, fundings1.getFundingGroup().get(1).getFundingSummary().size());
        assertEquals(1, fundings1.getFundingGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-2", fundings1.getFundingGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        //Group 3 have ext-id-3
        assertEquals(1, fundings1.getFundingGroup().get(2).getFundingSummary().size());
        assertEquals(1, fundings1.getFundingGroup().get(2).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-3", fundings1.getFundingGroup().get(2).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        //Group 4 have ext-id-4
        assertEquals(1, fundings1.getFundingGroup().get(3).getFundingSummary().size());
        assertEquals(1, fundings1.getFundingGroup().get(3).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-4", fundings1.getFundingGroup().get(3).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        FundingSummary s7 = getFundingSummary("Funding 7", "ext-id-4", Visibility.PRIVATE);
        //Add ext-id-3 to work 7, so, it join group 3 and group 4 in a single group
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://orcid.org"));        
        extId.setValue("ext-id-3"); 
        s7.getExternalIdentifiers().getExternalIdentifier().add(extId);
        
        /**
         * Now, they should be grouped as
         * 
         * Group 1: Funding 1 + Funding 4
         * Group 2: Funding 2 + Funding 5
         * Group 3: Funding 3 + Funding 6 + Funding 7
         * */
        List<FundingSummary> fundingsList2 = Arrays.asList(s1, s2, s3, s4, s5, s6, s7);
        
        Fundings fundings2 = profileFundingManager.groupFundings(fundingsList2, false);
        assertNotNull(fundings2);
        assertEquals(3, fundings2.getFundingGroup().size());
        //Group 1 have all with ext-id-1
        assertEquals(2, fundings2.getFundingGroup().get(0).getFundingSummary().size());
        assertEquals(1, fundings2.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-1", fundings2.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        //Group 2 have all with ext-id-2
        assertEquals(2, fundings2.getFundingGroup().get(1).getFundingSummary().size());
        assertEquals(1, fundings2.getFundingGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-2", fundings2.getFundingGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        //Group 3 have all with ext-id-3 and ext-id-4
        assertEquals(3, fundings2.getFundingGroup().get(2).getFundingSummary().size());
        assertEquals(2, fundings2.getFundingGroup().get(2).getIdentifiers().getExternalIdentifier().size());
        assertThat(fundings2.getFundingGroup().get(2).getIdentifiers().getExternalIdentifier().get(0).getValue(), anyOf(is("ext-id-3"), is("ext-id-4")));
        assertThat(fundings2.getFundingGroup().get(2).getIdentifiers().getExternalIdentifier().get(1).getValue(), anyOf(is("ext-id-3"), is("ext-id-4")));
    }
    
    @Test
    public void testGroupFundings_groupOnlyPublicFundings1() {
        FundingSummary s1 = getFundingSummary("Public 1", "ext-id-1", Visibility.PUBLIC);
        FundingSummary s2 = getFundingSummary("Limited 1", "ext-id-2", Visibility.LIMITED);
        FundingSummary s3 = getFundingSummary("Private 1", "ext-id-3", Visibility.PRIVATE);
        FundingSummary s4 = getFundingSummary("Public 2", "ext-id-4", Visibility.PUBLIC);
        FundingSummary s5 = getFundingSummary("Limited 2", "ext-id-5", Visibility.LIMITED);
        FundingSummary s6 = getFundingSummary("Private 2", "ext-id-6", Visibility.PRIVATE);
        FundingSummary s7 = getFundingSummary("Public 3", "ext-id-7", Visibility.PUBLIC);
        FundingSummary s8 = getFundingSummary("Limited 3", "ext-id-8", Visibility.LIMITED);
        FundingSummary s9 = getFundingSummary("Private 3", "ext-id-9", Visibility.PRIVATE);
        
        List<FundingSummary> fundingList = Arrays.asList(s1, s2, s3, s4, s5, s6, s7, s8, s9);
        
        /**
         * They should be grouped as
         * 
         * Group 1: Public 1
         * Group 2: Public 2
         * Group 3: Public 3
         * */
        Fundings fundings = profileFundingManager.groupFundings(fundingList, true);
        assertNotNull(fundings);
        assertEquals(3, fundings.getFundingGroup().size());
        assertEquals(1, fundings.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals(1, fundings.getFundingGroup().get(0).getFundingSummary().size());
        assertEquals("ext-id-1", fundings.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("Public 1", fundings.getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTitle().getContent());
        assertEquals(1, fundings.getFundingGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        assertEquals(1, fundings.getFundingGroup().get(1).getFundingSummary().size());
        assertEquals("ext-id-4", fundings.getFundingGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("Public 2", fundings.getFundingGroup().get(1).getFundingSummary().get(0).getTitle().getTitle().getContent());
        assertEquals(1, fundings.getFundingGroup().get(2).getIdentifiers().getExternalIdentifier().size());
        assertEquals(1, fundings.getFundingGroup().get(2).getFundingSummary().size());
        assertEquals("ext-id-7", fundings.getFundingGroup().get(2).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("Public 3", fundings.getFundingGroup().get(2).getFundingSummary().get(0).getTitle().getTitle().getContent());
    }
    
    @Test
    public void testGroupFundings_groupOnlyPublicFundings2() {
        FundingSummary s1 = getFundingSummary("Public 1", "ext-id-1", Visibility.PUBLIC);
        FundingSummary s2 = getFundingSummary("Limited 1", "ext-id-1", Visibility.LIMITED);
        FundingSummary s3 = getFundingSummary("Private 1", "ext-id-1", Visibility.PRIVATE);
        FundingSummary s4 = getFundingSummary("Public 2", "ext-id-1", Visibility.PUBLIC);
        FundingSummary s5 = getFundingSummary("Limited 2", "ext-id-1", Visibility.LIMITED);
        FundingSummary s6 = getFundingSummary("Private 2", "ext-id-1", Visibility.PRIVATE);
        FundingSummary s7 = getFundingSummary("Public 3", "ext-id-2", Visibility.PUBLIC);
        FundingSummary s8 = getFundingSummary("Limited 3", "ext-id-2", Visibility.LIMITED);
        FundingSummary s9 = getFundingSummary("Private 3", "ext-id-2", Visibility.PRIVATE);        
        
        List<FundingSummary> fundingList = Arrays.asList(s1, s2, s3, s4, s5, s6, s7, s8, s9);
        
        /**
         * They should be grouped as
         * 
         * Group 1: Public 1 + Public 2
         * Group 2: Public 3
         * */
        Fundings fundings = profileFundingManager.groupFundings(fundingList, true);
        assertNotNull(fundings);
        assertEquals(2, fundings.getFundingGroup().size());
        assertEquals(1, fundings.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-1", fundings.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(2, fundings.getFundingGroup().get(0).getFundingSummary().size());
        assertThat(fundings.getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTitle().getContent(), anyOf(is("Public 1"), is("Public 2")));
        assertThat(fundings.getFundingGroup().get(0).getFundingSummary().get(1).getTitle().getTitle().getContent(), anyOf(is("Public 1"), is("Public 2")));
        assertEquals(1, fundings.getFundingGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-2", fundings.getFundingGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(1, fundings.getFundingGroup().get(1).getFundingSummary().size());
        assertEquals("Public 3", fundings.getFundingGroup().get(1).getFundingSummary().get(0).getTitle().getTitle().getContent());
    }
    
    private FundingSummary getFundingSummary(String titleValue, String extIdValue, Visibility visibility) {
        FundingSummary summary = new FundingSummary();
        FundingTitle fundingTitle = new FundingTitle();
        fundingTitle.setTitle(new Title(titleValue));
        summary.setTitle(fundingTitle);        
        summary.setVisibility(visibility);        
        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://orcid.org"));        
        extId.setValue(extIdValue);               
        extIds.getExternalIdentifier().add(extId);
        summary.setExternalIdentifiers(extIds);
        return summary;
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
