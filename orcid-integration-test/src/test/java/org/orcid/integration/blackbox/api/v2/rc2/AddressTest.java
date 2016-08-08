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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.jaxb.model.common_rc2.Country;
import org.orcid.jaxb.model.common_rc2.Iso3166Country;
import org.orcid.jaxb.model.common_rc2.Visibility;
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
    @Resource(name = "memberV2ApiClient_rc2")
    private MemberV2ApiClientImpl memberV2ApiClient;
    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient;
    
    @Test
    public void testGetAddressWithMembersAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        Country country = new Country();
        country.setValue(Iso3166Country.US);
        Long putCode = createAddress(country, getUser1OrcidId(), accessToken);
        
        signin();
        showMyOrcidPage();
        openEditCountryModal();
        changeAddressVisibility(Visibility.LIMITED);
        
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Addresses addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        
        boolean found = false;
        
        for(Address address : addresses.getAddress()) {
            if(Visibility.PRIVATE.equals(address.getVisibility())) {
                //If an address is private, check the source is this client
                assertEquals(getClient1ClientId(), address.getSource().retrieveSourcePath());
            }
            
            if(putCode.equals(address.getPutCode())) {
                assertEquals(Visibility.LIMITED, address.getVisibility());
                assertEquals(Iso3166Country.US, address.getCountry().getValue());
                assertEquals(getClient1ClientId(), address.getSource().retrieveSourcePath());
                found = true;
                break;
            }            
        }
        
        assertTrue(found);
        //Delete it
        deleteAddress(getUser1OrcidId(), putCode, accessToken);        
    }

    @SuppressWarnings({ "deprecation", "rawtypes" })
    @Test
    public void testCreateGetUpdateAndDeleteAddress() throws InterruptedException, JSONException {        
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        Address address = new Address();       
        address.setCountry(new Country(Iso3166Country.CR));
        changeDefaultUserVisibility(webDriver, Visibility.PUBLIC);
        
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
                        
        boolean foundCR = false;
        
        for(Address add : addresses.getAddress()) {
            assertEquals(Visibility.PUBLIC, add.getVisibility());
            assertNotNull(add.getCountry());
            assertNotNull(add.getCountry().getValue());
            if(add.getCountry().getValue().equals(Iso3166Country.CR)) {
                foundCR = true;
            }
        }
        
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
        
        //Verify you can't update the visibility
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
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        Country country = new Country();
        country.setValue(Iso3166Country.CR);
        Long putCode = createAddress(country, getUser1OrcidId(), accessToken);
        
        signin();
        showMyOrcidPage();
        openEditCountryModal();
        changeAddressVisibility(Visibility.PUBLIC);
        
        // test read public works
        ClientResponse response = publicV2ApiClient.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Addresses addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        
        boolean found = false;
        
        for(Address address : addresses.getAddress()) {
            assertEquals(Visibility.PUBLIC, address.getVisibility());
            if(putCode.equals(address.getPutCode())) {
                assertEquals(Iso3166Country.CR, address.getCountry().getValue());
                assertEquals(getClient1ClientId(), address.getSource().retrieveSourcePath());
                found = true;
                break;
            }
        }             
        
        assertTrue(found);
        showMyOrcidPage();
        openEditCountryModal();
        changeAddressVisibility(Visibility.LIMITED);
        
        response = publicV2ApiClient.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNull(addresses.getAddress());  
        
        deleteAddress(getUser1OrcidId(), putCode, accessToken);
    }
    
    @Test
    public void testInvalidPutCodeReturns404() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        Address address = new Address();       
        address.setCountry(new Country(Iso3166Country.CR));
        address.setVisibility(Visibility.PUBLIC);
        address.setPutCode(1234567890L);
        
        ClientResponse response = memberV2ApiClient.updateAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    public String getAccessToken() throws InterruptedException, JSONException {
        List<String> scopes = getScopes(ScopePathType.PERSON_UPDATE, ScopePathType.READ_LIMITED);        
        return getAccessToken(getUser1OrcidId(), getUser1Password(), scopes, getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
    }
}
