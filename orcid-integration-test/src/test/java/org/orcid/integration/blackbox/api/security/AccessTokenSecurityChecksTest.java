package org.orcid.integration.blackbox.api.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.JsonUtils;
import org.orcid.integration.api.helper.APIRequestType;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.blackbox.api.v12.T2OAuthAPIService;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.Work;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class AccessTokenSecurityChecksTest extends BlackBoxBaseV2Release {

    @Resource
    private OauthHelper oauthHelper;
    
    @Resource(name = "t2OAuthClient_1_2")
    protected T2OAuthAPIService<ClientResponse> t2OAuthClient_1_2; 
	    
    @SuppressWarnings("unchecked")
    @Test
    public void testInvalidTokenResponse() throws IOException {
    	ClientResponse response = memberV2ApiClient.viewPerson(getUser1OrcidId(), "invalid_token");
    	assertNotNull(response);
        assertEquals(ClientResponse.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        InputStream stream = response.getEntityInputStream();
        
        String result = null;
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(stream))) {
            result = buffer.lines().collect(Collectors.joining("\n"));
        }
        
        assertNotNull(result);
        HashMap<String, String> error = JsonUtils.readObjectFromJsonString(result, HashMap.class);
        assertNotNull(error);
        assertEquals("invalid_token", error.get("error"));
        assertEquals("Invalid access token: invalid_token", error.get("error_description"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testInvalidClientResponse() throws IOException {
    	MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", "APP-0000000000000000");
        params.add("client_secret", "clientSecret");
        params.add("grant_type", "client_credentials");
        params.add("scope", "/read-public");
        ClientResponse response = oauthHelper.getResponse(params, APIRequestType.MEMBER);        
        assertEquals(ClientResponse.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        String result = response.getEntity(String.class);
        assertNotNull(result);        
		HashMap<String, String> error = JsonUtils.readObjectFromJsonString(result, HashMap.class);
        assertNotNull(error);
        assertEquals("invalid_client", error.get("error"));
        assertEquals("Client not found: APP-0000000000000000", error.get("error_description"));
    }
           
    @Test
    public void testTokenIssuedForOneUserFailForOtherUsers_20API() throws JSONException, InterruptedException, URISyntaxException {
        String accessToken = getNonCachedAccessTokens(getUser2OrcidId(), getUser2Password(), getScopes(), getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        String orcid = getUser1OrcidId();
        Long putCode = 1L;

        Address address = (Address) unmarshallFromPath("/record_2.0/samples/read_samples/address-2.0.xml", Address.class);
        evaluateResponse(memberV2ApiClient.createAddress(orcid, address, accessToken));

        Education education = (Education) unmarshallFromPath("/record_2.0/samples/read_samples/education-2.0.xml", Education.class);
        evaluateResponse(memberV2ApiClient.createEducationJson(orcid, education, accessToken));
        evaluateResponse(memberV2ApiClient.createEducationXml(orcid, education, accessToken));

        Employment employment = (Employment) unmarshallFromPath("/record_2.0/samples/read_samples/employment-2.0.xml", Employment.class);
        evaluateResponse(memberV2ApiClient.createEmploymentJson(orcid, employment, accessToken));
        evaluateResponse(memberV2ApiClient.createEmploymentXml(orcid, employment, accessToken));

        PersonExternalIdentifier externalIdentifier = (PersonExternalIdentifier) unmarshallFromPath("/record_2.0/samples/read_samples/external-identifier-2.0.xml",
                PersonExternalIdentifier.class);
        evaluateResponse(memberV2ApiClient.createExternalIdentifier(orcid, externalIdentifier, accessToken));

        Funding funding = (Funding) unmarshallFromPath("/record_2.0/samples/read_samples/funding-2.0.xml", Funding.class);
        evaluateResponse(memberV2ApiClient.createFundingJson(orcid, funding, accessToken));
        evaluateResponse(memberV2ApiClient.createFundingXml(orcid, funding, accessToken));

        Keyword keyword = (Keyword) unmarshallFromPath("/record_2.0/samples/read_samples/keyword-2.0.xml", Keyword.class);
        evaluateResponse(memberV2ApiClient.createKeyword(orcid, keyword, accessToken));

        OtherName otherName = (OtherName) unmarshallFromPath("/record_2.0/samples/read_samples/other-name-2.0.xml", OtherName.class);
        evaluateResponse(memberV2ApiClient.createOtherName(orcid, otherName, accessToken));

        PeerReview peerReview = (PeerReview) unmarshallFromPath("/record_2.0/samples/read_samples/peer-review-2.0.xml", PeerReview.class);
        evaluateResponse(memberV2ApiClient.createPeerReviewJson(orcid, peerReview, accessToken));
        evaluateResponse(memberV2ApiClient.createPeerReviewXml(orcid, peerReview, accessToken));

        ResearcherUrl rUrl = (ResearcherUrl) unmarshallFromPath("/record_2.0/samples/read_samples/researcher-url-2.0.xml", ResearcherUrl.class);
        evaluateResponse(memberV2ApiClient.createResearcherUrls(orcid, rUrl, accessToken));

        Work work = (Work) unmarshallFromPath("/record_2.0/samples/read_samples/work-2.0.xml", Work.class);
        evaluateResponse(memberV2ApiClient.createWorkJson(orcid, work, accessToken));
        evaluateResponse(memberV2ApiClient.createWorkXml(orcid, work, accessToken));
        evaluateResponse(memberV2ApiClient.deleteAddress(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deleteEducationXml(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deleteEmploymentXml(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deleteExternalIdentifier(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deleteFundingXml(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deleteKeyword(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deleteOtherName(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deletePeerReviewXml(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deleteResearcherUrl(orcid, putCode, accessToken));
        evaluateResponse(memberV2ApiClient.deleteWorkXml(orcid, putCode, accessToken));

        address.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateAddress(orcid, address, accessToken));

        education.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateEducation(orcid, education, accessToken));

        employment.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateEmployment(orcid, employment, accessToken));

        externalIdentifier.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateExternalIdentifier(orcid, externalIdentifier, accessToken));

        funding.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateFunding(orcid, funding, accessToken));

        keyword.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateKeyword(orcid, keyword, accessToken));

        otherName.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateOtherName(orcid, otherName, accessToken));

        peerReview.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updatePeerReview(orcid, peerReview, accessToken));

        rUrl.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateResearcherUrls(orcid, rUrl, accessToken));

        work.setPutCode(putCode);
        evaluateResponse(memberV2ApiClient.updateWork(orcid, work, accessToken));
        
        evaluateResponse(memberV2ApiClient.getResearcherUrls(orcid, accessToken));                                
        evaluateResponse(memberV2ApiClient.viewAddresses(orcid, accessToken));
        evaluateResponse(memberV2ApiClient.viewExternalIdentifiers(orcid, accessToken));        
        evaluateResponse(memberV2ApiClient.viewKeywords(orcid, accessToken));                        
        evaluateResponse(memberV2ApiClient.viewOtherNames(orcid, accessToken));                
        evaluateResponse(memberV2ApiClient.viewBiography(orcid, accessToken));        
        evaluateResponse(memberV2ApiClient.viewPersonalDetailsXML(orcid, accessToken));        
        evaluateResponse(memberV2ApiClient.viewActivities(orcid, accessToken));                
        evaluateResponse(memberV2ApiClient.viewPerson(orcid, accessToken));                          
    }
    
    @Test
    public void invalidAuthorizationCodesFailTest() throws InterruptedException, JSONException {
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String clientSecret = getClient1ClientSecret();
        String userId = getUser1OrcidId();
        String password = getUser1Password();
        String scope = "/orcid-works/create";
        String authorizationCode = getAuthorizationCode(clientId, clientRedirectUri, scope, userId, password, true);
        assertNotNull(authorizationCode);
        ClientResponse tokenResponse = getAccessTokenResponse(clientId, clientSecret, clientRedirectUri, "invalid-authorization-code");
        assertEquals(400, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        assertEquals("invalid_grant", jsonObject.get("error"));
        assertEquals("Invalid authorization code: invalid-authorization-code", jsonObject.get("error_description"));
    }

    private List<String> getScopes() {
        return getScopes(ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE, ScopePathType.AFFILIATIONS_CREATE,
                ScopePathType.AFFILIATIONS_READ_LIMITED, ScopePathType.AFFILIATIONS_UPDATE, ScopePathType.AUTHENTICATE, ScopePathType.FUNDING_CREATE,
                ScopePathType.FUNDING_READ_LIMITED, ScopePathType.FUNDING_UPDATE, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE,
                ScopePathType.ORCID_BIO_READ_LIMITED, ScopePathType.ORCID_BIO_UPDATE, ScopePathType.ORCID_PROFILE_READ_LIMITED, ScopePathType.ORCID_WORKS_CREATE,
                ScopePathType.ORCID_WORKS_READ_LIMITED, ScopePathType.ORCID_WORKS_UPDATE, ScopePathType.PEER_REVIEW_CREATE, ScopePathType.PEER_REVIEW_READ_LIMITED);
    }

    private void evaluateResponse(ClientResponse response) {
        assertNotNull(response);
        assertEquals(ClientResponse.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        OrcidError error = response.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals("401 Unauthorized: The client application is not authorized for this ORCID record. Full validation error: Access token is for a different record", error.getDeveloperMessage());
        assertEquals(Integer.valueOf(9017), error.getErrorCode());
    }
    
    private void evaluateResponseOn12API(ClientResponse response) {
        assertNotNull(response);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
                
        OrcidMessage orcidMessage = response.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);        
        assertNotNull(orcidMessage.getErrorDesc());
        assertEquals("Security problem : You do not have the required permissions.", orcidMessage.getErrorDesc().getContent());
    }
    
    @Test
    public void resusedAuthorizationCodesFailTest() throws InterruptedException, JSONException {
        toggleFeature(getAdminUserName(), getAdminPassword(), Features.REVOKE_TOKEN_ON_CODE_REUSE, true);
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String clientSecret = getClient1ClientSecret();
        String userId = getUser1OrcidId();
        String password = getUser1Password();
        String scope = "/orcid-works/create";
        String authorizationCode = getAuthorizationCode(clientId, clientRedirectUri, scope, userId, password, true);
        assertNotNull(authorizationCode);
        ClientResponse tokenResponse = getAccessTokenResponse(clientId, clientSecret, clientRedirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String token = new JSONObject(tokenResponse.getEntity(String.class)).getString("access_token");
        
        ClientResponse response = memberV2ApiClient.viewPerson(getUser1OrcidId(), token);
        assertNotNull(response);
        assertEquals(200, response.getStatus());        
        
        ClientResponse tokenResponse2 = getAccessTokenResponse(clientId, clientSecret, clientRedirectUri, authorizationCode);
        assertEquals(400, tokenResponse2.getStatus());
        String body = tokenResponse2.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        assertEquals("invalid_grant", jsonObject.get("error"));
        assertEquals("Reused authorization code: "+authorizationCode, jsonObject.get("error_description"));
        
        //check token has been revoked
        ClientResponse response2 = memberV2ApiClient.viewPerson(getUser1OrcidId(), token);
        assertNotNull(response2);
        assertEquals(401, response2.getStatus()); 
        
        //check we get the old message if feature is off
        toggleFeature(getAdminUserName(), getAdminPassword(), Features.REVOKE_TOKEN_ON_CODE_REUSE, false);
        ClientResponse tokenResponse3 = getAccessTokenResponse(clientId, clientSecret, clientRedirectUri, authorizationCode);
        assertEquals(400, tokenResponse3.getStatus());
        String body3 = tokenResponse3.getEntity(String.class);
        JSONObject jsonObject3 = new JSONObject(body3);
        assertEquals("invalid_grant", jsonObject3.get("error"));
        assertEquals("Invalid authorization code: "+authorizationCode, jsonObject3.get("error_description"));
    }
    
}