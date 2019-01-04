package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.exception.InvalidDisambiguatedOrgException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.v3.read_only.ResearchResourceManagerReadOnly;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.ResourceType;
import org.orcid.jaxb.model.v3.rc2.common.Day;
import org.orcid.jaxb.model.v3.rc2.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.rc2.common.FuzzyDate;
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
import org.orcid.jaxb.model.v3.rc2.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResourceItem;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResourceProposal;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResourceTitle;
import org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResources;
import org.orcid.persistence.dao.ResearchResourceDao;
import org.orcid.test.TargetProxyHelper;

public class ResearchResourceManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/OrgsEntityData.xml", "/data/ResearchResourceEntityData.xml");
    
    private static String USER_ORCID = "0000-0000-0000-0003";
    private static String OTHER_USER_ORCID = "4444-4444-4444-4446";
    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
    private static final String CLIENT_2_ID = "APP-5555555555555556";//obo
    private static final String CLIENT_3_ID = "4444-4444-4444-4498";//obo
    
    @Mock
    private SourceManager mockSourceManager;
    
    @Resource(name = "sourceManagerV3")
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
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "sourceManager", mockSourceManager);
        TargetProxyHelper.injectIntoProxy(researchResourceManager, "sourceManager", mockSourceManager);
    }
    
    @After
    public void after() {
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
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));                
        researchResourceManager.createResearchResource(USER_ORCID, generateResearchResourceWithoutDisambiguatedOrg("title1","id1"), true);
    }

    @Test
    public void testCreateWithoutDisambiguatedOrg2(){
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));                
        researchResourceManager.createResearchResource(USER_ORCID, generateResearchResourceWithoutDisambiguatedOrg("title1","id2"), false);
    }

    @Test(expected = ActivityIdentifierValidationException.class)
    public void testCreateWithoutExternalIdentifiers(){
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));                
        researchResourceManager.createResearchResource(USER_ORCID, generateResearchResourceWithoutExternalID("title1","id2"), false);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testCreate(){
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));                
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
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));                
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
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));                
        ResearchResource rr1 = researchResourceManager.createResearchResource(USER_ORCID, generateResearchResource("title6","id6"), true);
        researchResourceManager.checkSourceAndRemoveResearchResource(USER_ORCID, rr1.getPutCode());
        ResearchResource rr2 = researchResourceManager.getResearchResource(USER_ORCID, rr1.getPutCode());
    }
    
    @Test
    public void testUpdateOrgDoesntUpdateOthers(){
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));                
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
    
    @Test
    public void testAssertionOriginUpdate() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID, CLIENT_2_ID));                
        ResearchResource r1 = researchResourceManager.createResearchResource(USER_ORCID,generateResearchResourceWithItems("title7","id67"),true);
        
        assertEquals(r1.getSource().getSourceOrcid().getPath(),CLIENT_1_ID);
        assertEquals(r1.getSource().getSourceOrcid().getUri(),"https://testserver.orcid.org/"+CLIENT_1_ID);
        //assertEquals(r1.getSource().getSourceName().getContent(),"U. Test");
        assertEquals(r1.getSource().getAssertionOriginClientId().getPath(),CLIENT_2_ID);
        assertEquals(r1.getSource().getAssertionOriginClientId().getUri(),"https://testserver.orcid.org/client/"+CLIENT_2_ID);
        assertEquals(r1.getSource().getAssertionOriginName().getContent(),"Source Client 2");
        
        //make a duplicate
        ResearchResource r2 = generateResearchResourceWithItems("title7","id67");
        try {
            r2 = researchResourceManager.createResearchResource(USER_ORCID,r2,true);
            fail();
        }catch(OrcidDuplicatedActivityException e) {
            
        }
        
        //make a duplicate as a different assertion origin
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID, CLIENT_3_ID));                
        r2 = researchResourceManager.createResearchResource(USER_ORCID,r2,true);
        
        //wrong sources:
        r1.getProposal().getExternalIdentifiers().getExternalIdentifier().get(0).setValue("x");
        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID, CLIENT_3_ID));
            researchResourceManager.updateResearchResource(USER_ORCID, r1, true);
            fail();
        }catch(WrongSourceException e) {
        }
        
        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));  
            researchResourceManager.updateResearchResource(USER_ORCID, r1, true);
            fail();
        }catch(WrongSourceException e) {
            
        }
        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_2_ID));
            researchResourceManager.updateResearchResource(USER_ORCID, r1, true);
            fail();
        }catch(WrongSourceException e) {
            
        }
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
        rp.getTitle().setTranslatedTitle(new TranslatedTitle("translatedTitle","en"));
        
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
        ri1.setResourceType(ResourceType.valueOf("infrastrutures"));
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
    
}
