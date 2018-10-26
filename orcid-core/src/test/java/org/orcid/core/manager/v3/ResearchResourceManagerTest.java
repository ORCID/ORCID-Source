package org.orcid.core.manager.v3;

import static org.junit.Assert.*;
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
import org.orcid.core.exception.InvalidDisambiguatedOrgException;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.manager.v3.read_only.ResearchResourceManagerReadOnly;
import org.orcid.jaxb.model.v3.rc2.common.Day;
import org.orcid.jaxb.model.v3.rc2.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.rc2.common.FuzzyDate;
import org.orcid.jaxb.model.v3.rc2.common.Iso3166Country;
import org.orcid.jaxb.model.v3.rc2.common.Month;
import org.orcid.jaxb.model.v3.rc2.common.Organization;
import org.orcid.jaxb.model.v3.rc2.common.OrganizationAddress;
import org.orcid.jaxb.model.v3.rc2.common.Source;
import org.orcid.jaxb.model.v3.rc2.common.Title;
import org.orcid.jaxb.model.v3.rc2.common.TranslatedTitle;
import org.orcid.jaxb.model.v3.rc2.common.Url;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.common.Year;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc2.record.Relationship;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResourceItem;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResourceProposal;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResourceTitle;
import org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResources;
import org.orcid.persistence.dao.ResearchResourceDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.TargetProxyHelper;

public class ResearchResourceManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/OrgsEntityData.xml", "/data/ResearchResourceEntityData.xml");
    
    private static String USER_ORCID = "0000-0000-0000-0003";
    private static String OTHER_USER_ORCID = "4444-4444-4444-4446";
    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
    
    @Mock
    private SourceManager sourceManager;
    
    @Resource(name = "researchResourceManagerV3")
    private ResearchResourceManager researchResourceManager;
    
    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;

    @Resource(name = "researchResourceManagerReadOnlyV3")
    private ResearchResourceManagerReadOnly researchResourceManagerReadOnly;
    
    @Resource
    private ResearchResourceDao ResearchResourceDao;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "sourceManager", sourceManager);
        TargetProxyHelper.injectIntoProxy(researchResourceManager, "sourceManager", sourceManager);
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }
    
    @Test
    public void testView(){
        ResearchResource r = researchResourceManagerReadOnly.getResearchResource(OTHER_USER_ORCID, 1l);
        assertEquals("the title",r.getProposal().getTitle().getTitle().getContent());
        assertEquals(Long.valueOf(1l),r.getPutCode());
        assertEquals(2,r.getResourceItems().size());
        assertEquals("the resource name1",r.getResourceItems().get(0).getResourceName());
    }

    @Test
    public void testViewList(){
        List<ResearchResource> r = researchResourceManagerReadOnly.findResearchResources(OTHER_USER_ORCID);
        assertEquals(3,r.size());
    }

    @Test
    public void testViewSummary(){
        ResearchResourceSummary r = researchResourceManagerReadOnly.getResearchResourceSummary(OTHER_USER_ORCID, 2l);
        assertEquals("the title2",r.getProposal().getTitle().getTitle().getContent());
        assertEquals("2",r.getDisplayIndex());
        assertEquals(Long.valueOf(2l),r.getPutCode());
    }
    
    @Test
    public void testUpdateToMaxDisplay(){
        ResearchResourceSummary r = researchResourceManagerReadOnly.getResearchResourceSummary(OTHER_USER_ORCID, 1l);
        assertEquals("the title",r.getProposal().getTitle().getTitle().getContent());
        assertEquals("1",r.getDisplayIndex());
        assertEquals(Long.valueOf(1l),r.getPutCode());
        
        researchResourceManager.updateToMaxDisplay(OTHER_USER_ORCID, 1l);
        r = researchResourceManagerReadOnly.getResearchResourceSummary(OTHER_USER_ORCID, 1l);
        assertEquals("4",r.getDisplayIndex());
    }
    
    @Test
    public void testViewSummaries(){
        List<ResearchResourceSummary> r = researchResourceManagerReadOnly.getResearchResourceSummaryList(OTHER_USER_ORCID);
        assertEquals(3,r.size());
        ResearchResources rr = researchResourceManagerReadOnly.groupResearchResources(r, true);
        assertEquals(2,rr.getResearchResourceGroup().size()); 
    }
    
    @Test(expected = InvalidDisambiguatedOrgException.class)
    public void testCreateWithoutDisambiguatedOrg(){
        when(sourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));                
        researchResourceManager.createResearchResource(USER_ORCID, generateResearchResourceWithoutDisambiguatedOrg("title1","id1"), true);
    }

    @Test
    public void testCreateWithoutDisambiguatedOrg2(){
        when(sourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));                
        researchResourceManager.createResearchResource(USER_ORCID, generateResearchResourceWithoutDisambiguatedOrg("title1","id2"), false);
    }

    @Test(expected = ActivityIdentifierValidationException.class)
    public void testCreateWithoutExternalIdentifiers(){
        when(sourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));                
        researchResourceManager.createResearchResource(USER_ORCID, generateResearchResourceWithoutExternalID("title1","id2"), false);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testCreate(){
        when(sourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));                
        ResearchResource rr1 = researchResourceManager.createResearchResource(USER_ORCID, generateResearchResource("title2","id2"), true);
        assertNotNull(rr1.getCreatedDate());
        assertNotNull(rr1.getLastModifiedDate());
        assertNotNull(rr1.getPutCode());
        assertNotNull(rr1.getSource());
        assertEquals(CLIENT_1_ID,rr1.getSource().retrieveSourcePath());
        assertNotNull(rr1.getVisibility());
        assertEquals(Visibility.PUBLIC,rr1.getVisibility());
        assertNotNull(rr1.getProposal());
        assertEquals("title2",rr1.getProposal().getTitle().getTitle().getContent());
        assertNotNull(rr1.getProposal().getHosts());
        assertEquals(2,rr1.getProposal().getHosts().getOrganization().size());
        assertNotNull(rr1.getProposal().getHosts().getOrganization().get(0));
        assertEquals("orgName",rr1.getProposal().getHosts().getOrganization().get(0).getName());
        assertNotNull(rr1.getProposal().getStartDate());
        assertEquals("2011",rr1.getProposal().getStartDate().getYear().getValue());
        assertNotNull(rr1.getProposal().getEndDate());
        assertEquals("2012",rr1.getProposal().getEndDate().getYear().getValue());
        assertEquals(1,rr1.getProposal().getExternalIdentifiers().getExternalIdentifier().size());
        
        ResearchResource rr2 = researchResourceManager.createResearchResource(USER_ORCID, generateResearchResourceWithItems("title4","id4"), true);
        assertNotNull(rr2.getResourceItems());
        assertEquals(2,rr2.getResourceItems().size());
        assertNotNull(rr2.getResourceItems().get(0));
        assertNotNull(rr2.getResourceItems().get(0).getHosts());
        assertNotNull(rr2.getResourceItems().get(0).getHosts().getOrganization().get(0));
        
        ResearchResourceSummary rs1 = researchResourceManagerReadOnly.getResearchResourceSummary(USER_ORCID, rr1.getPutCode());
        assertNotNull(rs1);
        ResearchResourceSummary rs2 = researchResourceManagerReadOnly.getResearchResourceSummary(USER_ORCID, rr2.getPutCode());
        assertNotNull(rs2);
        ResearchResource rrr1 = researchResourceManagerReadOnly.getResearchResource(USER_ORCID, rr1.getPutCode());
        assertNotNull(rrr1);
        ResearchResource rrr2 = researchResourceManagerReadOnly.getResearchResource(USER_ORCID, rr2.getPutCode());
        assertNotNull(rrr2);
    }
    
    @Test
    public void testUpdate(){
        when(sourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));                
        ResearchResource r1 = researchResourceManager.createResearchResource(USER_ORCID,generateResearchResourceWithItems("title5","id5"),true);
        ResearchResource r2 = generateResearchResourceWithItems("title5","id5");
        r2.setPutCode(r1.getPutCode());
        r2.getProposal().setTitle(new ResearchResourceTitle());
        r2.getProposal().getTitle().setTitle(new Title("changedTitle"));
        
        Organization org1 = new Organization();
        org1.setName("changedOrg");
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        org1.setAddress(address);
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("abc456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        org1.setDisambiguatedOrganization(disambiguatedOrg);
        
        r2.getProposal().getHosts().setOrganization(new ArrayList<Organization>());
        r2.getProposal().getHosts().getOrganization().add(org1);
        
        r2.getResourceItems().get(0).getHosts().getOrganization().set(0, org1);
        assertEquals("title5-item1",r2.getResourceItems().get(0).getResourceName());
        r2.getResourceItems().get(0).setResourceName("changedResourceName");
        assertEquals("changedResourceName",r2.getResourceItems().get(0).getResourceName());
        
        ResearchResource r3 = researchResourceManager.updateResearchResource(USER_ORCID, r2, true);
        assertEquals("changedTitle",r3.getProposal().getTitle().getTitle().getContent());
        assertEquals(1,r3.getProposal().getHosts().getOrganization().size());
        assertEquals("changedOrg",r3.getProposal().getHosts().getOrganization().get(0).getName());
        assertEquals("abc456",r3.getProposal().getHosts().getOrganization().get(0).getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
        assertEquals(2,r3.getResourceItems().get(0).getHosts().getOrganization().size());
        assertEquals("changedOrg",r3.getResourceItems().get(0).getHosts().getOrganization().get(0).getName());
        assertEquals("changedResourceName",r3.getResourceItems().get(0).getResourceName());
        
        ResearchResource r4 = researchResourceManager.getResearchResource(USER_ORCID, r1.getPutCode());
        assertEquals("changedTitle",r4.getProposal().getTitle().getTitle().getContent());
        assertEquals(1,r4.getProposal().getHosts().getOrganization().size());
        assertEquals("changedOrg",r4.getProposal().getHosts().getOrganization().get(0).getName());
        assertEquals("abc456",r4.getProposal().getHosts().getOrganization().get(0).getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
        assertEquals(2,r4.getResourceItems().get(0).getHosts().getOrganization().size());
        assertEquals("changedOrg",r4.getResourceItems().get(0).getHosts().getOrganization().get(0).getName());
        assertEquals("changedResourceName",r4.getResourceItems().get(0).getResourceName());
    }

    @Test(expected = javax.persistence.NoResultException.class)
    public void testDelete(){
        when(sourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));                
        ResearchResource rr1 = researchResourceManager.createResearchResource(USER_ORCID, generateResearchResource("title6","id6"), true);
        researchResourceManager.checkSourceAndRemoveResearchResource(USER_ORCID, rr1.getPutCode());
        ResearchResource rr2 = researchResourceManager.getResearchResource(USER_ORCID, rr1.getPutCode());
    }
    
    @Test
    public void testUpdateOrgDoesntUpdateOthers(){
        when(sourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));                
        ResearchResource r1 = researchResourceManager.createResearchResource(USER_ORCID,generateResearchResourceWithItems("title6","id6"),true);
        ResearchResource r2 = researchResourceManager.createResearchResource(USER_ORCID,generateResearchResourceWithItems("title7","id7"),true);
        
        //update orgName does not affect the other
        r1.getProposal().getHosts().getOrganization().get(0).setName("changedOrg");
        ResearchResource r3 = researchResourceManager.updateResearchResource(USER_ORCID, r1, true);        
        ResearchResource r4 = researchResourceManager.getResearchResource(USER_ORCID, r2.getPutCode());
        assertEquals("changedOrg",r3.getProposal().getHosts().getOrganization().get(0).getName());
        assertEquals("orgName",r4.getProposal().getHosts().getOrganization().get(0).getName());
        
        //update disambiguated does not update the other
        r1.getProposal().getHosts().getOrganization().get(0).getDisambiguatedOrganization().setDisambiguatedOrganizationIdentifier("abc456");
        ResearchResource r5 = researchResourceManager.updateResearchResource(USER_ORCID, r1, true);
        ResearchResource r6 = researchResourceManager.getResearchResource(USER_ORCID, r2.getPutCode());
        assertEquals("abc456",r5.getProposal().getHosts().getOrganization().get(0).getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
        assertEquals("def456",r6.getProposal().getHosts().getOrganization().get(0).getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   
    public ResearchResource generateResearchResourceWithoutDisambiguatedOrg(String title, String extIdValue){
        ResearchResource rr = generateResearchResource(title,extIdValue);
        for (Organization o : rr.getProposal().getHosts().getOrganization())
            o.setDisambiguatedOrganization(null);
        return rr;
    }
    
    public ResearchResource generateResearchResourceWithoutExternalID(String title, String extIdValue){
        ResearchResource rr = generateResearchResource(title,extIdValue);
        rr.getProposal().setExternalIdentifiers(null);
        return rr;
    }
    public ResearchResource generateResearchResource(String title, String extIdValue){
        ResearchResource rr = new ResearchResource();
        ResearchResourceProposal rp = new ResearchResourceProposal();
        rr.setProposal(rp);
        rp.setUrl(new Url("http://blah.com"));
        rp.setTitle(new ResearchResourceTitle());
        rp.getTitle().setTitle(new Title(title));
        rp.getTitle().setTranslatedTitle(new TranslatedTitle("translatedTitle","EN"));
        
        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://orcid.org"));        
        extId.setValue(extIdValue);               
        extIds.getExternalIdentifier().add(extId);
        rp.setExternalIdentifiers(extIds);
        rp.setEndDate(new FuzzyDate(new Year(2012),new Month(1),new Day(1)));
        rp.setStartDate(new FuzzyDate(new Year(2011),new Month(1),new Day(1)));
        Organization org1 = new Organization();
        org1.setName("orgName");
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        org1.setAddress(address);
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("def456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        org1.setDisambiguatedOrganization(disambiguatedOrg);
        Organization org2 = new Organization();
        org2.setName("orgName2");
        org2.setAddress(address);
        DisambiguatedOrganization disambiguatedOrg2 = new DisambiguatedOrganization();
        disambiguatedOrg2.setDisambiguatedOrganizationIdentifier("def456");
        disambiguatedOrg2.setDisambiguationSource("WDB");
        org2.setDisambiguatedOrganization(disambiguatedOrg2);
        rp.getHosts().getOrganization().add(org1);
        rp.getHosts().getOrganization().add(org2);
        
        return rr;
    }
    
    public ResearchResource generateResearchResourceWithItems(String title, String extIdValue){
        ResearchResource rr = generateResearchResource(title,extIdValue);
        rr.getResourceItems().add(generateResearchResourceItem(title+"-item1", extIdValue+"item1"));
        rr.getResourceItems().add(generateResearchResourceItem(title+"-item2", extIdValue+"item2"));
        return rr;
    }
    
    public ResearchResourceItem generateResearchResourceItem(String title, String extIdValue){
        ResearchResourceItem ri1 = new ResearchResourceItem();
        ri1.setResourceName(title);
        ri1.setResourceType("infrastrutures");
        ri1.setUrl(new Url("http://orcid.org")); 
        
        Organization org1 = new Organization();
        org1.setName("orgName");
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        org1.setAddress(address);
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("def456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        org1.setDisambiguatedOrganization(disambiguatedOrg);
        Organization org2 = new Organization();
        org2.setName("orgName2");
        org2.setAddress(address);
        DisambiguatedOrganization disambiguatedOrg2 = new DisambiguatedOrganization();
        disambiguatedOrg2.setDisambiguatedOrganizationIdentifier("def456");
        disambiguatedOrg2.setDisambiguationSource("WDB");
        org2.setDisambiguatedOrganization(disambiguatedOrg2);
        ri1.getHosts().getOrganization().add(org1);
        ri1.getHosts().getOrganization().add(org2);
        
        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://orcid.org"));        
        extId.setValue(extIdValue);               
        extIds.getExternalIdentifier().add(extId);
        ri1.setExternalIdentifiers(extIds);
        
        return ri1;
    }
    

    /*
    
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
    
    @Test
    public void testGetAll() {
        String orcid = "0000-0000-0000-0003"; 
        List<Funding> elements = profileFundingManager.getFundingList(orcid);
        assertNotNull(elements);
        assertEquals(5, elements.size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false;
        
        for(Funding element : elements) {
            if(10 == element.getPutCode()) {
                found1 = true;
            } else if(11 == element.getPutCode()) {
                found2 = true;
            } else if(12 == element.getPutCode()) {
                found3 = true;
            } else if(13 == element.getPutCode()) {
                found4 = true;
            } else if(14 == element.getPutCode()) {
                found5 = true;
            } else {
                fail("Invalid put code found: " + element.getPutCode());
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        assertTrue(found5);        
    }
    
    @Test
    public void testGetPublic() {
        String orcid = "0000-0000-0000-0003"; 
        List<FundingSummary> elements = profileFundingManager.getFundingSummaryList(orcid);
        assertNotNull(elements);
        assertEquals(5, elements.size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false;
        
        for(FundingSummary element : elements) {
            if(10 == element.getPutCode()) {
                found1 = true;
            } else if(11 == element.getPutCode()) {
                found2 = true;
            } else if(12 == element.getPutCode()) {
                found3 = true;
            } else if(13 == element.getPutCode()) {
                found4 = true;
            } else if(14 == element.getPutCode()) {
                found5 = true;
            } else {
                fail("Invalid put code found: " + element.getPutCode());
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        assertTrue(found5); 
    }
    
    @Test
    public void nonGroupableIdsGenerateEmptyIdsListTest() {
        FundingSummary s1 = getFundingSummary("Element 1", "ext-id-1", Visibility.PUBLIC);
        FundingSummary s2 = getFundingSummary("Element 2", "ext-id-2", Visibility.LIMITED);
        FundingSummary s3 = getFundingSummary("Element 3", "ext-id-3", Visibility.PRIVATE);
        
        // s1 will be a part of identifier, so, it will go in its own group
        s1.getExternalIdentifiers().getExternalIdentifier().get(0).setRelationship(Relationship.PART_OF);
        
        List<FundingSummary> fundingList = Arrays.asList(s1, s2, s3);
        
        Fundings fundings = profileFundingManager.groupFundings(fundingList, false);
        assertNotNull(fundings);
        assertEquals(3, fundings.getFundingGroup().size());
        boolean foundEmptyGroup = false;
        boolean found2 = false;
        boolean found3 = false;
        for(FundingGroup group : fundings.getFundingGroup()) {
            assertEquals(1, group.getFundingSummary().size());
            assertNotNull(group.getIdentifiers().getExternalIdentifier());
            if(group.getIdentifiers().getExternalIdentifier().isEmpty()) {
                assertEquals("Element 1", group.getFundingSummary().get(0).getTitle().getTitle().getContent());
                assertEquals("ext-id-1", group.getFundingSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                foundEmptyGroup = true;
            } else {
                assertEquals(1, group.getIdentifiers().getExternalIdentifier().size());
                assertThat(group.getIdentifiers().getExternalIdentifier().get(0).getValue(), anyOf(is("ext-id-2"), is("ext-id-3")));
                if(group.getIdentifiers().getExternalIdentifier().get(0).getValue().equals("ext-id-2")) {
                    assertEquals("Element 2", group.getFundingSummary().get(0).getTitle().getTitle().getContent());
                    assertEquals("ext-id-2", group.getFundingSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                    found2 = true;
                } else if(group.getIdentifiers().getExternalIdentifier().get(0).getValue().equals("ext-id-3")) {
                    assertEquals("Element 3", group.getFundingSummary().get(0).getTitle().getTitle().getContent());
                    assertEquals("ext-id-3", group.getFundingSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                    found3 = true;
                } else {
                    fail("Invalid ext id found " + group.getIdentifiers().getExternalIdentifier().get(0).getValue());
                }
            }
        }
        assertTrue(foundEmptyGroup);
        assertTrue(found2);
        assertTrue(found3);
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
        
        Organization org = new Organization();
        org.setName("org-name");
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        org.setAddress(address);
        summary.setOrganization(org);
        
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
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("abc456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        org.setDisambiguatedOrganization(disambiguatedOrg);
        funding.setOrganization(org);
        funding.setVisibility(Visibility.PUBLIC);
        funding.setType(FundingType.AWARD);
        return funding;
    }
    */
}
