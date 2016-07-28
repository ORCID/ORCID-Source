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
package org.orcid.integration.blackbox.api.v2.rc2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.web.account.PublicProfileVisibilityTest;
import org.orcid.jaxb.model.common_rc3.Country;
import org.orcid.jaxb.model.common_rc3.Iso3166Country;
import org.orcid.jaxb.model.common_rc3.Visibility;
import org.orcid.jaxb.model.error_rc1.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Addresses;
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
public class AddressTest extends BlackBoxBaseRC2 {
    protected static Map<String, String> accessTokens = new HashMap<String, String>();
    @Resource(name = "memberV2ApiClient_rc2")
    private MemberV2ApiClientImpl memberV2ApiClient;
    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient;
    
    @Test
    public void testGetAddressWithMembersAPI() throws InterruptedException, JSONException {
        //set up
        signin();
        showMyOrcidPage();
        openEditCountryModal();
        deleteAllCountriesInCountryModal();
        saveEditCountryModal();
        openEditCountryModal();
        setCountryInCountryModal("US");
        markAllPublicInCountryModal();
        saveEditCountryModal();
        
        //test
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient.viewAddresses(getUser1OrcidId(), accessToken);
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
    public void testCreateGetUpdateAndDeleteAddress() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(webDriver, Visibility.PUBLIC);
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);

        Address address = new Address();       
        address.setCountry(new Country(Iso3166Country.CR));
        address.setVisibility(Visibility.PUBLIC);
        //Create
        ClientResponse response = memberV2ApiClient.createAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        
        //Get all and verify
        response = memberV2ApiClient.viewAddresses(getUser1OrcidId(), accessToken);
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
        response = memberV2ApiClient.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        address = response.getEntity(Address.class);
        assertNotNull(address);
        assertNotNull(address.getSource());
        assertEquals(getClient1ClientId(), address.getSource().retrieveSourcePath());
        assertNotNull(address.getCountry());
        assertNotNull(address.getCountry().getValue());        
        assertEquals(Iso3166Country.CR, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC, address.getVisibility());                
        assertNotNull(address.getDisplayIndex());
        Long originalDisplayIndex = address.getDisplayIndex();
        
        //Save the original visibility
        Visibility originalVisibility = address.getVisibility();
        Visibility updatedVisibility = Visibility.LIMITED;
        
        //Verify you cant update the visibility
        address.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient.updateAddress(getUser1OrcidId(), address, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        address.setVisibility(originalVisibility);        
        
        //Update 
        address.getCountry().setValue(Iso3166Country.PA);
        response = memberV2ApiClient.updateAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        Address updatedAddress = response.getEntity(Address.class);
        assertNotNull(updatedAddress);
        assertNotNull(updatedAddress.getCountry());
        assertEquals(Iso3166Country.PA, updatedAddress.getCountry().getValue());
        assertEquals(address.getPutCode(), updatedAddress.getPutCode());
        assertEquals(originalDisplayIndex, updatedAddress.getDisplayIndex());        
        
        //Delete
        response = memberV2ApiClient.deleteAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        //Check it was deleted
        testGetAddressWithMembersAPI();
    }

    @Test
    public void testGetAddressWithPublicAPI() throws InterruptedException, JSONException {
        //set up
        signin();
        showMyOrcidPage();
        openEditCountryModal();
        deleteAllCountriesInCountryModal();
        saveEditCountryModal();
        openEditCountryModal();
        setCountryInCountryModal("US");
        markAllPublicInCountryModal();
        saveEditCountryModal();
        
        // test read public works
        ClientResponse response = publicV2ApiClient.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Addresses addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        assertEquals(1, addresses.getAddress().size());
        assertEquals(Visibility.PUBLIC, addresses.getAddress().get(0).getVisibility());
        assertEquals(Iso3166Country.US, addresses.getAddress().get(0).getCountry().getValue());      
    }
    
    @Test
    public void testInvalidPutCodeReturns404() throws InterruptedException, JSONException {
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);
        
        Address address = new Address();       
        address.setCountry(new Country(Iso3166Country.CR));
        address.setVisibility(Visibility.PUBLIC);
        address.setPutCode(1234567890L);
        
        ClientResponse response = memberV2ApiClient.updateAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
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
