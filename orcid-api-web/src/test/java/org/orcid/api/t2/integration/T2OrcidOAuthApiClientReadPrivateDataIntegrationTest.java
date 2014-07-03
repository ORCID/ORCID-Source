package org.orcid.api.t2.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.FundingTitle;
import org.orcid.jaxb.model.message.FundingType;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.message.OrganizationAddress;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

@RunWith(SpringJUnit4ClassRunner.class)
public class T2OrcidOAuthApiClientReadPrivateDataIntegrationTest extends BaseT2OrcidOAuthApiClientIntegrationTest {

    @Resource
    private OrcidClientGroupManager orcidClientGroupManager;
        
    private String redirectUriString = "https://developers.google.com/oauthplayground";
    
    OrcidClientGroup group = null;
    OrcidClient readPrivateWorksClient = null;
    OrcidClient readPrivateFundingClient = null;
    OrcidClient readPrivateAffiliationsClient = null;
    OrcidClient cantReadPrivateDataClient = null;
    String userOrcid = null;
    
    String client1token = null;
    String client2token = null;
    String client3token = null;
    String client4token = null;
    
    /**
     * TODO Create integration tests that test the following:
     * 1. Can read his private works, but not other private works
     * 2. Can read his private affiliations, but not other private affiliations
     * 3. Can read his private funding, but not other pirvate funding
     * */
    
    
    
    @Override
    @Before
    public void createClientCredentialsAndAccessToken() {
        group = orcidClientDataHelper.createAndPersistGroupWithMultipleClients();
        
        assertNotNull(group);
        assertNotNull(group.getOrcidClient());
        assertEquals(4, group.getOrcidClient().size());
        
        readPrivateWorksClient = group.getOrcidClient().get(0);
        readPrivateFundingClient = group.getOrcidClient().get(1); 
        readPrivateAffiliationsClient = group.getOrcidClient().get(2); 
        cantReadPrivateDataClient = group.getOrcidClient().get(3);
        
        try{
            //Create the user profile
            clientId = cantReadPrivateDataClient.getClientId();
            clientSecret = cantReadPrivateDataClient.getClientSecret();
            client4token = createAccessTokenFromCredentials(ScopePathType.ORCID_PROFILE_CREATE.value() + ' ' + ScopePathType.ORCID_PROFILE_READ_LIMITED.value());
            OrcidMessage profile = orcidClientDataHelper.createFromXML(OrcidClientDataHelper.USER_TO_TEST_PRIVATE_DATA_VISIBILITY);
            ClientResponse clientResponse = oauthT2Client.createProfileXML(profile, client4token);
            // assign orcid any time it's created for use in tear-down
            userOrcid = orcidClientDataHelper.extractOrcidFromResponseCreated(clientResponse);            
            assertNotNull(this.accessToken);
            assertFalse(PojoUtil.isEmpty(userOrcid));

            //Organization used by funding and affiliation
            Organization organization = new Organization();
            organization.setName("Org Name");
            OrganizationAddress address = new OrganizationAddress();
            address.setCity("Some city");
            address.setCountry(Iso3166Country.CR);
            address.setRegion("Some region");
            organization.setAddress(address);
            
            //Add works to the new profile
            clientId = readPrivateWorksClient.getClientId();
            clientSecret = readPrivateWorksClient.getClientSecret();
            client1token = createAccessTokenFromCredentials(ScopePathType.ORCID_PROFILE_READ_LIMITED.value() + ' ' + ScopePathType.ORCID_WORKS_CREATE.value() + ' ' + ScopePathType.ORCID_WORKS_UPDATE.value() + ' ' + ScopePathType.ORCID_WORKS_READ_LIMITED.value());
            
            OrcidWorks orcidWorks = new OrcidWorks();
            OrcidWork orcidWork = orcidClientDataHelper.createWork("Private work for client1");
            orcidWork.setWorkType(WorkType.ARTISTIC_PERFORMANCE);
            orcidWorks.getOrcidWork().add(orcidWork);
            profile.getOrcidProfile().setOrcidWorks(orcidWorks);
            
            clientResponse = oauthT2Client.addWorksXml(userOrcid, profile, client1token);
            assertEquals(201, clientResponse.getStatus());
            
            //Add funding to the new profile
            clientId = readPrivateFundingClient.getClientId();
            clientSecret = readPrivateFundingClient.getClientSecret();
            client2token = createAccessTokenFromCredentials(ScopePathType.ORCID_PROFILE_READ_LIMITED.value() + ' ' + ScopePathType.FUNDING_CREATE.value() + ' ' + ScopePathType.FUNDING_UPDATE.value() + ' ' + ScopePathType.FUNDING_READ_LIMITED.value());
            
            FundingList fundingList = new FundingList();
            Funding funding = new Funding();
            funding.setType(FundingType.AWARD);
            FundingTitle title = new FundingTitle();
            title.setTitle(new Title("Private funding for client2"));
            funding.setTitle(title);            
            funding.setOrganization(organization);
            fundingList.getFundings().add(funding);
            profile.getOrcidProfile().setFundings(fundingList);
            
            clientResponse = oauthT2Client.addFundingXml(userOrcid, profile, client2token);
            assertEquals(201, clientResponse.getStatus());
            
            //Add affiliations to the new profile
            clientId = readPrivateAffiliationsClient.getClientId();
            clientSecret = readPrivateAffiliationsClient.getClientSecret();
            client3token = createAccessTokenFromCredentials(ScopePathType.ORCID_PROFILE_READ_LIMITED.value() + ' ' + ScopePathType.AFFILIATIONS_CREATE.value() + ' ' + ScopePathType.AFFILIATIONS_UPDATE.value() + ' ' + ScopePathType.AFFILIATIONS_READ_LIMITED.value());
            
            Affiliations affiliations = new Affiliations();
            Affiliation affiliation = new Affiliation();
            affiliation.setType(AffiliationType.EDUCATION);
            affiliation.setDepartmentName("Private affiliation for client3");
            affiliation.setOrganization(organization);
            affiliations.getAffiliation().add(affiliation);            
            profile.getOrcidProfile().setAffiliations(affiliations);
            
            clientResponse = oauthT2Client.addAffiliationsXml(userOrcid, profile, client3token);
            assertEquals(201, clientResponse.getStatus());
            
            
        } catch(Exception e) {
            fail();
        }
    }
    
    @After
    public void clearOrcid() throws Exception {
        // remove any client data if it exists
        orcidClientDataHelper.deleteClientId(readPrivateWorksClient.getClientId());
        orcidClientDataHelper.deleteClientId(readPrivateFundingClient.getClientId());
        orcidClientDataHelper.deleteClientId(readPrivateAffiliationsClient.getClientId());
        orcidClientDataHelper.deleteClientId(cantReadPrivateDataClient.getClientId());
        orcidClientDataHelper.deleteOrcidProfile(group.getGroupOrcid());
        
        readPrivateWorksClient = null;
        readPrivateFundingClient = null;
        readPrivateAffiliationsClient = null;
        cantReadPrivateDataClient = null;
        group = null;
    }
    
    @Test
    public void testViewPrivateWorks() {
        ClientResponse clientResponse = oauthT2Client.viewBioDetailsXml(userOrcid, client4token);
        assertNotNull(clientResponse);
        assertEquals(200, clientResponse.getStatus());        
        OrcidMessage orcidMessage = clientResponse.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
    }
}
