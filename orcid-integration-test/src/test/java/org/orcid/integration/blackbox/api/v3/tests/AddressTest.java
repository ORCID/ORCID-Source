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
package org.orcid.integration.blackbox.api.v3.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import org.orcid.integration.api.pub.PublicV3ApiClientImpl;
import org.orcid.integration.blackbox.api.v3.dev1.BlackBoxBaseV3_0_dev1;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class AddressTest extends BlackBoxBaseV3_0_dev1 {

    @Resource(name = "memberV3_0_dev1ApiClient")
    private org.orcid.integration.blackbox.api.v3.dev1.MemberV3Dev1ApiClientImpl memberV3Dev1ApiClient;
    
    @Resource(name = "publicV3_0_dev1ApiClient")
    private PublicV3ApiClientImpl publicV3ApiClientImpl;

    private static org.orcid.jaxb.model.v3.dev1.common.Visibility currentDefaultVisibility = null;
    
    @BeforeClass
    public static void setup() {
        signin();
        showMyOrcidPage();
        openEditAddressModal();
        deleteAddresses();
        createAddress(org.orcid.jaxb.model.common_v2.Iso3166Country.US.name());
        changeAddressVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC.name());
        saveEditAddressModal();        
    }

    @AfterClass
    public static void after() {
        showMyOrcidPage();
        openEditAddressModal();
        deleteAddresses();
        saveEditAddressModal();
        signout();
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC.name());
    }

    private void changeDefaultUserVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility v) {
        if(!v.equals(currentDefaultVisibility)) {
            changeDefaultUserVisibility(webDriver, v.name());
            currentDefaultVisibility = v;
        }
    }
    
    /**
     * --------- -- -- -- V2.1 -- -- -- ---------
     * 
     */
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Test
    public void testCreateGetUpdateAndDeleteAddress() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.LIMITED);
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.v3.dev1.record.Address address = new org.orcid.jaxb.model.v3.dev1.record.Address();
        address.setCountry(new org.orcid.jaxb.model.v3.dev1.common.Country(org.orcid.jaxb.model.v3.dev1.common.Iso3166Country.CR));

        // Create
        ClientResponse response = memberV3Dev1ApiClient.createAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Get all and verify
        response = memberV3Dev1ApiClient.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.v3.dev1.record.Addresses addresses = response.getEntity(org.orcid.jaxb.model.v3.dev1.record.Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());

        boolean foundCR = false;
        boolean foundUS = false;

        for (org.orcid.jaxb.model.v3.dev1.record.Address add : addresses.getAddress()) {
            if (add.getCountry().getValue().equals(org.orcid.jaxb.model.v3.dev1.common.Iso3166Country.CR)) {
                assertEquals(org.orcid.jaxb.model.v3.dev1.common.Visibility.LIMITED, add.getVisibility());
                foundCR = true;
            } else if (add.getCountry().getValue().equals(org.orcid.jaxb.model.v3.dev1.common.Iso3166Country.US)) {
                assertEquals(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC, add.getVisibility());
                foundUS = true;
            }
        }

        assertTrue(foundCR);
        assertTrue(foundUS);

        // Get it
        response = memberV3Dev1ApiClient.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        address = response.getEntity(org.orcid.jaxb.model.v3.dev1.record.Address.class);
        assertNotNull(address);
        assertNotNull(address.getSource());
        assertEquals(getClient1ClientId(), address.getSource().retrieveSourcePath());
        assertNotNull(address.getCountry());
        assertNotNull(address.getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.v3.dev1.common.Iso3166Country.CR, address.getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.v3.dev1.common.Visibility.LIMITED, address.getVisibility());
        assertNotNull(address.getDisplayIndex());
        Long originalDisplayIndex = address.getDisplayIndex();

        // Save the original visibility
        org.orcid.jaxb.model.v3.dev1.common.Visibility originalVisibility = address.getVisibility();
        org.orcid.jaxb.model.v3.dev1.common.Visibility updatedVisibility = org.orcid.jaxb.model.v3.dev1.common.Visibility.PRIVATE;

        // Verify you can't update the visibility
        address.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV3Dev1ApiClient.updateAddress(getUser1OrcidId(), address, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.v3.dev1.error.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.v3.dev1.error.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        address.setVisibility(originalVisibility);

        // Update
        address.getCountry().setValue(org.orcid.jaxb.model.v3.dev1.common.Iso3166Country.PA);
        response = memberV3Dev1ApiClient.updateAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV3Dev1ApiClient.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.v3.dev1.record.Address updatedAddress = response.getEntity(org.orcid.jaxb.model.v3.dev1.record.Address.class);
        assertNotNull(updatedAddress);
        assertNotNull(updatedAddress.getCountry());
        assertEquals(org.orcid.jaxb.model.v3.dev1.common.Iso3166Country.PA, updatedAddress.getCountry().getValue());
        assertEquals(address.getPutCode(), updatedAddress.getPutCode());
        assertEquals(originalDisplayIndex, updatedAddress.getDisplayIndex());

        // Delete
        response = memberV3Dev1ApiClient.deleteAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }
    
    /**
     * ---------------------- -- -- -- ALL -- -- --
     * ----------------------
     */
    @Test
    public void testGetAddressWithMembersAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        // SET THEM ALL TO LIMITED
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.LIMITED.name());
        saveEditAddressModal();

        ClientResponse response = memberV3Dev1ApiClient.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.v3.dev1.record.Addresses addresses = response.getEntity(org.orcid.jaxb.model.v3.dev1.record.Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        assertEquals(1, addresses.getAddress().size());
        assertEquals(org.orcid.jaxb.model.v3.dev1.common.Visibility.LIMITED, addresses.getAddress().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.v3.dev1.common.Iso3166Country.US, addresses.getAddress().get(0).getCountry().getValue());
        
        // SET THEM ALL TO PRIVATE
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.PRIVATE.name());
        saveEditAddressModal();

        response = memberV3Dev1ApiClient.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        addresses = response.getEntity(org.orcid.jaxb.model.v3.dev1.record.Addresses.class);
        assertNotNull(addresses);
        assertTrue(addresses.getAddress().isEmpty());
        
        // SET THEM ALL TO PUBLIC BEFORE FINISHING THE TEST
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC.name());
        saveEditAddressModal();
    }

    @Test
    public void testGetAddressWithPublicAPI() throws InterruptedException, JSONException {
        ClientResponse response = publicV3ApiClientImpl.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.v3.dev1.record.Addresses addresses = response.getEntity(org.orcid.jaxb.model.v3.dev1.record.Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());

        boolean found = false;

        for (org.orcid.jaxb.model.v3.dev1.record.Address add : addresses.getAddress()) {
            assertEquals(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC, add.getVisibility());
            if (add.getCountry().getValue().equals(org.orcid.jaxb.model.v3.dev1.common.Iso3166Country.US)) {
                found = true;                
            }
        }
        
        // SET THEM ALL TO PUBLIC BEFORE FINISHING THE TEST
        assertTrue(found);
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(org.orcid.jaxb.model.v3.dev1.common.Visibility.PUBLIC.name());
        saveEditAddressModal();
    }

    @Test
    public void testInvalidPutCodeReturns404() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.v3.dev1.record.Address address = new org.orcid.jaxb.model.v3.dev1.record.Address();
        address.setCountry(new org.orcid.jaxb.model.v3.dev1.common.Country(org.orcid.jaxb.model.v3.dev1.common.Iso3166Country.MX));
        address.setPutCode(1234567890L);

        ClientResponse response = memberV3Dev1ApiClient.updateAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    public String getAccessToken() throws InterruptedException, JSONException {
        List<String> scopes = getScopes(ScopePathType.PERSON_UPDATE, ScopePathType.READ_LIMITED);
        return getAccessToken(getUser1OrcidId(), getUser1Password(), scopes, getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
    }
}
