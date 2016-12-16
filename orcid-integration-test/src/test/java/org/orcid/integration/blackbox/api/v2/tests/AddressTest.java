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
package org.orcid.integration.blackbox.api.v2.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.members.MemberV2ApiClientImpl;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.api.v2.rc4.BlackBoxBaseRC4;
import org.orcid.jaxb.model.common_rc4.Country;
import org.orcid.jaxb.model.common_rc4.Iso3166Country;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.error_rc4.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc4.Address;
import org.orcid.jaxb.model.record_rc4.Addresses;
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
public class AddressTest extends BlackBoxBaseRC4 {
    
	@Resource(name = "memberV2ApiClient_rc2")
    private MemberV2ApiClientImpl memberV2ApiClient_rc2;
    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient_rc2;
	
	@Resource(name = "memberV2ApiClient_rc3")
    private MemberV2ApiClientImpl memberV2ApiClient_rc3;
    @Resource(name = "publicV2ApiClient_rc3")
    private PublicV2ApiClientImpl publicV2ApiClient_rc3;
	
	@Resource(name = "memberV2ApiClient_rc4")
    private org.orcid.integration.api.members.MemberV2ApiClientImpl memberV2ApiClient_rc4;
    @Resource(name = "publicV2ApiClient_rc4")
    private PublicV2ApiClientImpl publicV2ApiClient_rc4;
    
    @BeforeClass
    public static void setup(){
        signin();
        showMyOrcidPage();
        openEditAddressModal();  
        deleteAddresses();
        createAddress(Iso3166Country.US.name());
        changeAddressVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        saveEditAddressModal();                
    }
    
    @AfterClass
    public static void after() {  
        showMyOrcidPage();
        openEditAddressModal();
        deleteAddresses();
        saveEditAddressModal();
        signout();
    }
    
    /**
     * 	 	---------
     *  	--     --
     *   	-- RC2 --
     *    	--     --
     *     	---------
     * 
     * */
    
    @Test
    public void testGetAddressWithMembersAPI_rc2() throws InterruptedException, JSONException {
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        saveEditAddressModal();
        
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient_rc2.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Addresses addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        
        boolean found = false;
        
        for(Address address : addresses.getAddress()) {
            assertEquals(Visibility.LIMITED, address.getVisibility());
            if(Iso3166Country.US.equals(address.getCountry().getValue())) {
                found = true;
                break;
            }            
        }
        
        assertTrue(found);
    }
    
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Test
    public void testCreateGetUpdateAndDeleteAddress_rc2() throws InterruptedException, JSONException {        
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        saveEditAddressModal();
        
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        Address address = new Address();       
        address.setCountry(new Country(Iso3166Country.CR));
                
        //Create
        ClientResponse response = memberV2ApiClient_rc2.createAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        
        //Get all and verify
        response = memberV2ApiClient_rc2.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Addresses addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());        
                        
        boolean foundCR = false;
        boolean foundUS = false;
        
        for(Address add : addresses.getAddress()) {
            if(add.getCountry().getValue().equals(Iso3166Country.CR)) {
                assertEquals(add.getVisibility(), Visibility.LIMITED);
                foundCR = true;
            } else if(add.getCountry().getValue().equals(Iso3166Country.US)) {
                assertEquals(add.getVisibility(), Visibility.PUBLIC);
                foundUS = true;
            }
        }
        
        assertTrue(foundCR);
        assertTrue(foundUS);
               
        //Get it
        response = memberV2ApiClient_rc2.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        address = response.getEntity(Address.class);
        assertNotNull(address);
        assertNotNull(address.getSource());
        assertEquals(getClient1ClientId(), address.getSource().retrieveSourcePath());
        assertNotNull(address.getCountry());
        assertNotNull(address.getCountry().getValue());        
        assertEquals(Iso3166Country.CR, address.getCountry().getValue());
        assertEquals(Visibility.LIMITED, address.getVisibility());                
        assertNotNull(address.getDisplayIndex());
        Long originalDisplayIndex = address.getDisplayIndex();
        
        //Save the original visibility
        Visibility originalVisibility = address.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE;
        
        //Verify you can't update the visibility
        address.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient_rc2.updateAddress(getUser1OrcidId(), address, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        address.setVisibility(originalVisibility);        
        
        //Update 
        address.getCountry().setValue(Iso3166Country.PA);
        response = memberV2ApiClient_rc2.updateAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_rc2.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        Address updatedAddress = response.getEntity(Address.class);
        assertNotNull(updatedAddress);
        assertNotNull(updatedAddress.getCountry());
        assertEquals(Iso3166Country.PA, updatedAddress.getCountry().getValue());
        assertEquals(address.getPutCode(), updatedAddress.getPutCode());
        assertEquals(originalDisplayIndex, updatedAddress.getDisplayIndex());        
        
        //Delete
        response = memberV2ApiClient_rc2.deleteAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());        
    }
    
    @Test
    public void testGetAddressWithPublicAPI_rc2() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
                
        ClientResponse response = publicV2ApiClient_rc2.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Addresses addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        
        boolean found = false;
        
        for(Address add : addresses.getAddress()) {
            assertEquals(Visibility.PUBLIC, add.getVisibility());
            if(add.getCountry().getValue().equals(Iso3166Country.US)) {
                found = true;
                break;
            }
        }             
        
        assertTrue(found);
        signin();
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        saveEditAddressModal();
        
        response = publicV2ApiClient_rc2.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNull(addresses.getAddress());                   
    }
    
    @Test
    public void testInvalidPutCodeReturns404_rc2() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        Address address = new Address();       
        address.setCountry(new Country(Iso3166Country.CR));
        address.setVisibility(Visibility.PUBLIC);
        address.setPutCode(1234567890L);
        
        ClientResponse response = memberV2ApiClient_rc2.updateAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
    
    /**
     * 	 	---------
     *  	--     --
     *   	-- RC3 --
     *    	--     --
     *     	---------
     * 
     * */
    
    @Test
    public void testGetAddressWithMembersAPI_rc3() throws InterruptedException, JSONException {
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        saveEditAddressModal();
        
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient_rc3.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Addresses addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        
        boolean found = false;
        
        for(Address address : addresses.getAddress()) {
            assertEquals(Visibility.LIMITED, address.getVisibility());
            if(Iso3166Country.US.equals(address.getCountry().getValue())) {
                found = true;
                break;
            }            
        }
        
        assertTrue(found);
    }
    
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Test
    public void testCreateGetUpdateAndDeleteAddress_rc3() throws InterruptedException, JSONException {        
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        saveEditAddressModal();
        
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        Address address = new Address();       
        address.setCountry(new Country(Iso3166Country.CR));
                
        //Create
        ClientResponse response = memberV2ApiClient_rc3.createAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        
        //Get all and verify
        response = memberV2ApiClient_rc3.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Addresses addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());        
                        
        boolean foundCR = false;
        boolean foundUS = false;
        
        for(Address add : addresses.getAddress()) {
            if(add.getCountry().getValue().equals(Iso3166Country.CR)) {
                assertEquals(add.getVisibility(), Visibility.LIMITED);
                foundCR = true;
            } else if(add.getCountry().getValue().equals(Iso3166Country.US)) {
                assertEquals(add.getVisibility(), Visibility.PUBLIC);
                foundUS = true;
            }
        }
        
        assertTrue(foundCR);
        assertTrue(foundUS);
               
        //Get it
        response = memberV2ApiClient_rc3.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        address = response.getEntity(Address.class);
        assertNotNull(address);
        assertNotNull(address.getSource());
        assertEquals(getClient1ClientId(), address.getSource().retrieveSourcePath());
        assertNotNull(address.getCountry());
        assertNotNull(address.getCountry().getValue());        
        assertEquals(Iso3166Country.CR, address.getCountry().getValue());
        assertEquals(Visibility.LIMITED, address.getVisibility());                
        assertNotNull(address.getDisplayIndex());
        Long originalDisplayIndex = address.getDisplayIndex();
        
        //Save the original visibility
        Visibility originalVisibility = address.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE;
        
        //Verify you can't update the visibility
        address.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient_rc3.updateAddress(getUser1OrcidId(), address, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        address.setVisibility(originalVisibility);        
        
        //Update 
        address.getCountry().setValue(Iso3166Country.PA);
        response = memberV2ApiClient_rc3.updateAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_rc3.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        Address updatedAddress = response.getEntity(Address.class);
        assertNotNull(updatedAddress);
        assertNotNull(updatedAddress.getCountry());
        assertEquals(Iso3166Country.PA, updatedAddress.getCountry().getValue());
        assertEquals(address.getPutCode(), updatedAddress.getPutCode());
        assertEquals(originalDisplayIndex, updatedAddress.getDisplayIndex());        
        
        //Delete
        response = memberV2ApiClient_rc3.deleteAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());        
    }
    
    @Test
    public void testGetAddressWithPublicAPI_rc3() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
                
        ClientResponse response = publicV2ApiClient_rc3.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Addresses addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        
        boolean found = false;
        
        for(Address add : addresses.getAddress()) {
            assertEquals(Visibility.PUBLIC, add.getVisibility());
            if(add.getCountry().getValue().equals(Iso3166Country.US)) {
                found = true;
                break;
            }
        }             
        
        assertTrue(found);
        signin();
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        saveEditAddressModal();
        
        response = publicV2ApiClient_rc3.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNull(addresses.getAddress());                   
    }
    
    @Test
    public void testInvalidPutCodeReturns404_rc3() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        Address address = new Address();       
        address.setCountry(new Country(Iso3166Country.CR));
        address.setVisibility(Visibility.PUBLIC);
        address.setPutCode(1234567890L);
        
        ClientResponse response = memberV2ApiClient_rc3.updateAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
    
    /**
     * 	 	---------
     *  	--     --
     *   	-- RC4 --
     *    	--     --
     *     	---------
     * 
     * */
    
    @Test
    public void testGetAddressWithMembersAPI_rc4() throws InterruptedException, JSONException {
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        saveEditAddressModal();
        
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient_rc4.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Addresses addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        
        boolean found = false;
        
        for(Address address : addresses.getAddress()) {
            assertEquals(Visibility.LIMITED, address.getVisibility());
            if(Iso3166Country.US.equals(address.getCountry().getValue())) {
                found = true;
                break;
            }            
        }
        
        assertTrue(found);
    }
    
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Test
    public void testCreateGetUpdateAndDeleteAddress_rc4() throws InterruptedException, JSONException {        
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
        saveEditAddressModal();
        
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        Address address = new Address();       
        address.setCountry(new Country(Iso3166Country.CR));
                
        //Create
        ClientResponse response = memberV2ApiClient_rc4.createAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        
        //Get all and verify
        response = memberV2ApiClient_rc4.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Addresses addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());        
                        
        boolean foundCR = false;
        boolean foundUS = false;
        
        for(Address add : addresses.getAddress()) {
            if(add.getCountry().getValue().equals(Iso3166Country.CR)) {
                assertEquals(add.getVisibility(), Visibility.LIMITED);
                foundCR = true;
            } else if(add.getCountry().getValue().equals(Iso3166Country.US)) {
                assertEquals(add.getVisibility(), Visibility.PUBLIC);
                foundUS = true;
            }
        }
        
        assertTrue(foundCR);
        assertTrue(foundUS);
               
        //Get it
        response = memberV2ApiClient_rc4.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        address = response.getEntity(Address.class);
        assertNotNull(address);
        assertNotNull(address.getSource());
        assertEquals(getClient1ClientId(), address.getSource().retrieveSourcePath());
        assertNotNull(address.getCountry());
        assertNotNull(address.getCountry().getValue());        
        assertEquals(Iso3166Country.CR, address.getCountry().getValue());
        assertEquals(Visibility.LIMITED, address.getVisibility());                
        assertNotNull(address.getDisplayIndex());
        Long originalDisplayIndex = address.getDisplayIndex();
        
        //Save the original visibility
        Visibility originalVisibility = address.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE;
        
        //Verify you can't update the visibility
        address.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient_rc4.updateAddress(getUser1OrcidId(), address, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        address.setVisibility(originalVisibility);        
        
        //Update 
        address.getCountry().setValue(Iso3166Country.PA);
        response = memberV2ApiClient_rc4.updateAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_rc4.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        Address updatedAddress = response.getEntity(Address.class);
        assertNotNull(updatedAddress);
        assertNotNull(updatedAddress.getCountry());
        assertEquals(Iso3166Country.PA, updatedAddress.getCountry().getValue());
        assertEquals(address.getPutCode(), updatedAddress.getPutCode());
        assertEquals(originalDisplayIndex, updatedAddress.getDisplayIndex());        
        
        //Delete
        response = memberV2ApiClient_rc4.deleteAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());        
    }
    
    @Test
    public void testGetAddressWithPublicAPI_rc4() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
                
        ClientResponse response = publicV2ApiClient_rc4.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Addresses addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        
        boolean found = false;
        
        for(Address add : addresses.getAddress()) {
            assertEquals(Visibility.PUBLIC, add.getVisibility());
            if(add.getCountry().getValue().equals(Iso3166Country.US)) {
                found = true;
                break;
            }
        }             
        
        assertTrue(found);
        signin();
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
        saveEditAddressModal();
        
        response = publicV2ApiClient_rc4.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        addresses = response.getEntity(Addresses.class);
        assertNotNull(addresses);
        assertNull(addresses.getAddress());                   
    }
    
    @Test
    public void testInvalidPutCodeReturns404_rc4() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        
        Address address = new Address();       
        address.setCountry(new Country(Iso3166Country.CR));
        address.setVisibility(Visibility.PUBLIC);
        address.setPutCode(1234567890L);
        
        ClientResponse response = memberV2ApiClient_rc4.updateAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    public String getAccessToken() throws InterruptedException, JSONException {
        List<String> scopes = getScopes(ScopePathType.PERSON_UPDATE, ScopePathType.READ_LIMITED);        
        return getAccessToken(getUser1OrcidId(), getUser1Password(), scopes, getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
    }
}
