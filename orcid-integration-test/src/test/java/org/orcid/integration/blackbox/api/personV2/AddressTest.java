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
package org.orcid.integration.blackbox.api.personV2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.memberV2.MemberV2ApiClientImpl;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.BlackBoxBase;
import org.orcid.jaxb.model.common.Country;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Addresses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-publicV2-context.xml" })
public class AddressTest extends BlackBoxBase {
    protected static Map<String, String> accessTokens = new HashMap<String, String>();

    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;
    @Value("${org.orcid.web.testClient1.redirectUri}")
    private String client1RedirectUri;
    @Value("${org.orcid.web.testClient1.clientId}")
    public String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    public String client1ClientSecret;
    @Value("${org.orcid.web.testClient2.clientId}")
    public String client2ClientId;
    @Value("${org.orcid.web.testClient2.clientSecret}")
    public String client2ClientSecret;
    @Value("${org.orcid.web.testClient2.redirectUri}")
    protected String client2RedirectUri;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;

    @Resource(name = "memberV2ApiClient_rc2")
    private MemberV2ApiClientImpl memberV2ApiClient;

    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient;
    
    /**
     * PRECONDITIONS: The user should have one public address US
     * 
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test
    public void testGetAddressWithMembersAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken(this.client1ClientId, this.client1ClientSecret, this.client1RedirectUri);
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient.viewAddresses(user1OrcidId, accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Addresses addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        assertEquals(1, addresses.getAddress().size());
        assertEquals(Visibility.PUBLIC, addresses.getAddress().get(0).getVisibility());
        assertEquals(Iso3166Country.US, addresses.getAddress().get(0).getCountry().getValue());
    }

    @SuppressWarnings({ "deprecation", "rawtypes" })
    @Test
    public void testCreateGetUpdateAndDeleteKeyword() throws InterruptedException, JSONException {
        String accessToken = getAccessToken(this.client1ClientId, this.client1ClientSecret, this.client1RedirectUri);
        assertNotNull(accessToken);

        Address address = new Address();       
        address.setCountry(new Country(Iso3166Country.CR));
        address.setVisibility(Visibility.PUBLIC);
        //Create
        ClientResponse response = memberV2ApiClient.createAddress(user1OrcidId, address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        
        //Get all and verify
        response = memberV2ApiClient.viewAddresses(user1OrcidId, accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Addresses addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        assertEquals(2, addresses.getAddress().size());
                
        boolean foundUS = false;
        boolean foundCR = false;
        
        for(Address add : addresses.getAddress()) {
            assertEquals(Visibility.PUBLIC, add.getVisibility());
            assertNotNull(add.getCountry());
            assertNotNull(add.getCountry().getValue());
            if(add.getCountry().getValue().equals(Iso3166Country.US)) {
                foundUS = true;
            } else if(add.getCountry().getValue().equals(Iso3166Country.CR)) {
                foundCR = true;
            }
        }
        
        assertTrue(foundUS);
        assertTrue(foundCR);
               
        //Get it
        response = memberV2ApiClient.viewAddress(user1OrcidId, putCode, accessToken);
        assertNotNull(response);
        address = response.getEntity(Address.class);
        assertNotNull(address);
        assertNotNull(address.getSource());
        assertEquals(client1ClientId, address.getSource().retrieveSourcePath());
        assertNotNull(address.getCountry());
        assertNotNull(address.getCountry().getValue());        
        assertEquals(Iso3166Country.CR, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC, address.getVisibility());
        
        //Update 
        address.getCountry().setValue(Iso3166Country.PA);
        response = memberV2ApiClient.updateAddress(user1OrcidId, address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient.viewAddress(user1OrcidId, putCode, accessToken);
        assertNotNull(response);
        Address updatedAddress = response.getEntity(Address.class);
        assertNotNull(updatedAddress);
        assertNotNull(updatedAddress.getCountry());
        assertEquals(Iso3166Country.PA, updatedAddress.getCountry().getValue());
        assertEquals(address.getPutCode(), updatedAddress.getPutCode());
                
        //Delete
        response = memberV2ApiClient.deleteAddress(user1OrcidId, putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        //Check it was deleted
        testGetAddressWithMembersAPI();
    }

    /**
     * PRECONDITIONS: The user should have one public address US
     * 
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test
    public void testGetAddressWithPublicAPI() throws InterruptedException, JSONException {
        ClientResponse response = publicV2ApiClient.viewAddressesXML(user1OrcidId);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Addresses addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        assertEquals(1, addresses.getAddress().size());
        assertEquals(Visibility.PUBLIC, addresses.getAddress().get(0).getVisibility());
        assertEquals(Iso3166Country.US, addresses.getAddress().get(0).getCountry().getValue());
    }

    public String getAccessToken(String clientId, String clientSecret, String redirectUri) throws InterruptedException, JSONException {
        if (accessTokens.containsKey(clientId)) {
            return accessTokens.get(clientId);
        }

        String accessToken = super.getAccessToken(ScopePathType.PERSON_UPDATE.value() + " " + ScopePathType.READ_LIMITED.value(), clientId, clientSecret, redirectUri);
        accessTokens.put(clientId, accessToken);
        return accessToken;
    }
}
