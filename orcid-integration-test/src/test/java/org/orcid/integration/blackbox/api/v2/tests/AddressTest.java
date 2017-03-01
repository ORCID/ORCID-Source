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
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.integration.blackbox.api.v2.release.MemberV2ApiClientImpl;
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
public class AddressTest extends BlackBoxBaseV2Release {

    @Resource(name = "memberV2ApiClient_rc2")
    private org.orcid.integration.blackbox.api.v2.rc2.MemberV2ApiClientImpl memberV2ApiClient_rc2;
    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient_rc2;

    @Resource(name = "memberV2ApiClient_rc3")
    private org.orcid.integration.blackbox.api.v2.rc3.MemberV2ApiClientImpl memberV2ApiClient_rc3;
    @Resource(name = "publicV2ApiClient_rc3")
    private PublicV2ApiClientImpl publicV2ApiClient_rc3;

    @Resource(name = "memberV2ApiClient_rc4")
    private org.orcid.integration.blackbox.api.v2.rc4.MemberV2ApiClientImpl memberV2ApiClient_rc4;
    @Resource(name = "publicV2ApiClient_rc4")
    private PublicV2ApiClientImpl publicV2ApiClient_rc4;
    
    @Resource(name = "memberV2ApiClient")
    private MemberV2ApiClientImpl memberV2ApiClient_release;
    @Resource(name = "publicV2ApiClient")
    private PublicV2ApiClientImpl publicV2ApiClient_release;

    private static org.orcid.jaxb.model.common_v2.Visibility currentDefaultVisibility = null;
    
    @BeforeClass
    public static void setup() {
        signin();
        showMyOrcidPage();
        openEditAddressModal();
        deleteAddresses();
        createAddress(org.orcid.jaxb.model.common_v2.Iso3166Country.US.name());
        changeAddressVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        saveEditAddressModal();        
    }

    @AfterClass
    public static void after() {
        showMyOrcidPage();
        openEditAddressModal();
        deleteAddresses();
        saveEditAddressModal();
        signout();
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
    }

    private void changeDefaultUserVisibility(org.orcid.jaxb.model.common_v2.Visibility v) {
        if(!v.equals(currentDefaultVisibility)) {
            changeDefaultUserVisibility(webDriver, v);
            currentDefaultVisibility = v;
        }
    }
    
    /**
     * --------- -- -- -- RC2 -- -- -- ---------
     * 
     */
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Test
    public void testCreateGetUpdateAndDeleteAddress_rc2() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.record_rc2.Address address = new org.orcid.jaxb.model.record_rc2.Address();
        address.setCountry(new org.orcid.jaxb.model.common_rc2.Country(org.orcid.jaxb.model.common_rc2.Iso3166Country.CR));

        // Create
        ClientResponse response = memberV2ApiClient_rc2.createAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Get all and verify
        response = memberV2ApiClient_rc2.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc2.Addresses addresses = response.getEntity(org.orcid.jaxb.model.record_rc2.Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());

        boolean foundCR = false;
        boolean foundUS = false;

        for (org.orcid.jaxb.model.record_rc2.Address add : addresses.getAddress()) {
            if (add.getCountry().getValue().equals(org.orcid.jaxb.model.common_rc2.Iso3166Country.CR)) {
                assertEquals(add.getVisibility(), org.orcid.jaxb.model.common_rc2.Visibility.LIMITED);
                foundCR = true;
            } else if (add.getCountry().getValue().equals(org.orcid.jaxb.model.common_rc2.Iso3166Country.US)) {
                assertEquals(add.getVisibility(), org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC);
                foundUS = true;
            }
        }

        assertTrue(foundCR);
        assertTrue(foundUS);

        // Get it
        response = memberV2ApiClient_rc2.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        address = response.getEntity(org.orcid.jaxb.model.record_rc2.Address.class);
        assertNotNull(address);
        assertNotNull(address.getSource());
        assertEquals(getClient1ClientId(), address.getSource().retrieveSourcePath());
        assertNotNull(address.getCountry());
        assertNotNull(address.getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc2.Iso3166Country.CR, address.getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, address.getVisibility());
        assertNotNull(address.getDisplayIndex());
        Long originalDisplayIndex = address.getDisplayIndex();

        // Save the original visibility
        org.orcid.jaxb.model.common_rc2.Visibility originalVisibility = address.getVisibility();
        org.orcid.jaxb.model.common_rc2.Visibility updatedVisibility = org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE;

        // Verify you can't update the visibility
        address.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV2ApiClient_rc2.updateAddress(getUser1OrcidId(), address, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_rc2.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_rc2.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        address.setVisibility(originalVisibility);

        // Update
        address.getCountry().setValue(org.orcid.jaxb.model.common_rc2.Iso3166Country.PA);
        response = memberV2ApiClient_rc2.updateAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_rc2.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc2.Address updatedAddress = response.getEntity(org.orcid.jaxb.model.record_rc2.Address.class);
        assertNotNull(updatedAddress);
        assertNotNull(updatedAddress.getCountry());
        assertEquals(org.orcid.jaxb.model.common_rc2.Iso3166Country.PA, updatedAddress.getCountry().getValue());
        assertEquals(address.getPutCode(), updatedAddress.getPutCode());
        assertEquals(originalDisplayIndex, updatedAddress.getDisplayIndex());

        // Delete
        response = memberV2ApiClient_rc2.deleteAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    /**
     * --------- -- -- -- RC3 -- -- -- ---------
     * 
     */
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Test
    public void testCreateGetUpdateAndDeleteAddress_rc3() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.record_rc3.Address address = new org.orcid.jaxb.model.record_rc3.Address();
        address.setCountry(new org.orcid.jaxb.model.common_rc3.Country(org.orcid.jaxb.model.common_rc3.Iso3166Country.CR));

        // Create
        ClientResponse response = memberV2ApiClient_rc3.createAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Get all and verify
        response = memberV2ApiClient_rc3.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc3.Addresses addresses = response.getEntity(org.orcid.jaxb.model.record_rc3.Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());

        boolean foundCR = false;
        boolean foundUS = false;

        for (org.orcid.jaxb.model.record_rc3.Address add : addresses.getAddress()) {
            if (add.getCountry().getValue().equals(org.orcid.jaxb.model.common_rc3.Iso3166Country.CR)) {
                assertEquals(add.getVisibility(), org.orcid.jaxb.model.common_rc3.Visibility.LIMITED);
                foundCR = true;
            } else if (add.getCountry().getValue().equals(org.orcid.jaxb.model.common_rc3.Iso3166Country.US)) {
                assertEquals(add.getVisibility(), org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC);
                foundUS = true;
            }
        }

        assertTrue(foundCR);
        assertTrue(foundUS);

        // Get it
        response = memberV2ApiClient_rc3.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        address = response.getEntity(org.orcid.jaxb.model.record_rc3.Address.class);
        assertNotNull(address);
        assertNotNull(address.getSource());
        assertEquals(getClient1ClientId(), address.getSource().retrieveSourcePath());
        assertNotNull(address.getCountry());
        assertNotNull(address.getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc3.Iso3166Country.CR, address.getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, address.getVisibility());
        assertNotNull(address.getDisplayIndex());
        Long originalDisplayIndex = address.getDisplayIndex();

        // Save the original visibility
        org.orcid.jaxb.model.common_rc3.Visibility originalVisibility = address.getVisibility();
        org.orcid.jaxb.model.common_rc3.Visibility updatedVisibility = org.orcid.jaxb.model.common_rc3.Visibility.PRIVATE;

        // Verify you can't update the visibility
        address.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV2ApiClient_rc3.updateAddress(getUser1OrcidId(), address, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_rc3.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_rc3.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        address.setVisibility(originalVisibility);

        // Update
        address.getCountry().setValue(org.orcid.jaxb.model.common_rc3.Iso3166Country.PA);
        response = memberV2ApiClient_rc3.updateAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_rc3.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc3.Address updatedAddress = response.getEntity(org.orcid.jaxb.model.record_rc3.Address.class);
        assertNotNull(updatedAddress);
        assertNotNull(updatedAddress.getCountry());
        assertEquals(org.orcid.jaxb.model.common_rc3.Iso3166Country.PA, updatedAddress.getCountry().getValue());
        assertEquals(address.getPutCode(), updatedAddress.getPutCode());
        assertEquals(originalDisplayIndex, updatedAddress.getDisplayIndex());

        // Delete
        response = memberV2ApiClient_rc3.deleteAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    /**
     * --------- -- -- -- RC4 -- -- -- ---------
     * 
     */
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Test
    public void testCreateGetUpdateAndDeleteAddress_rc4() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.record_rc4.Address address = new org.orcid.jaxb.model.record_rc4.Address();
        address.setCountry(new org.orcid.jaxb.model.common_rc4.Country(org.orcid.jaxb.model.common_rc4.Iso3166Country.CR));

        // Create
        ClientResponse response = memberV2ApiClient_rc4.createAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Get all and verify
        response = memberV2ApiClient_rc4.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc4.Addresses addresses = response.getEntity(org.orcid.jaxb.model.record_rc4.Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());

        boolean foundCR = false;
        boolean foundUS = false;

        for (org.orcid.jaxb.model.record_rc4.Address add : addresses.getAddress()) {
            if (add.getCountry().getValue().equals(org.orcid.jaxb.model.common_rc4.Iso3166Country.CR)) {
                assertEquals(add.getVisibility(), org.orcid.jaxb.model.common_rc4.Visibility.LIMITED);
                foundCR = true;
            } else if (add.getCountry().getValue().equals(org.orcid.jaxb.model.common_rc4.Iso3166Country.US)) {
                assertEquals(add.getVisibility(), org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC);
                foundUS = true;
            }
        }

        assertTrue(foundCR);
        assertTrue(foundUS);

        // Get it
        response = memberV2ApiClient_rc4.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        address = response.getEntity(org.orcid.jaxb.model.record_rc4.Address.class);
        assertNotNull(address);
        assertNotNull(address.getSource());
        assertEquals(getClient1ClientId(), address.getSource().retrieveSourcePath());
        assertNotNull(address.getCountry());
        assertNotNull(address.getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc4.Iso3166Country.CR, address.getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, address.getVisibility());
        assertNotNull(address.getDisplayIndex());
        Long originalDisplayIndex = address.getDisplayIndex();

        // Save the original visibility
        org.orcid.jaxb.model.common_rc4.Visibility originalVisibility = address.getVisibility();
        org.orcid.jaxb.model.common_rc4.Visibility updatedVisibility = org.orcid.jaxb.model.common_rc4.Visibility.PRIVATE;

        // Verify you can't update the visibility
        address.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV2ApiClient_rc4.updateAddress(getUser1OrcidId(), address, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_rc4.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_rc4.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        address.setVisibility(originalVisibility);

        // Update
        address.getCountry().setValue(org.orcid.jaxb.model.common_rc4.Iso3166Country.PA);
        response = memberV2ApiClient_rc4.updateAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_rc4.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_rc4.Address updatedAddress = response.getEntity(org.orcid.jaxb.model.record_rc4.Address.class);
        assertNotNull(updatedAddress);
        assertNotNull(updatedAddress.getCountry());
        assertEquals(org.orcid.jaxb.model.common_rc4.Iso3166Country.PA, updatedAddress.getCountry().getValue());
        assertEquals(address.getPutCode(), updatedAddress.getPutCode());
        assertEquals(originalDisplayIndex, updatedAddress.getDisplayIndex());

        // Delete
        response = memberV2ApiClient_rc4.deleteAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    /**
     * --------- -- -- -- Release -- -- -- ---------
     * 
     */
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Test
    public void testCreateGetUpdateAndDeleteAddress_release() throws InterruptedException, JSONException {
        changeDefaultUserVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        org.orcid.jaxb.model.record_v2.Address address = new org.orcid.jaxb.model.record_v2.Address();
        address.setCountry(new org.orcid.jaxb.model.common_v2.Country(org.orcid.jaxb.model.common_v2.Iso3166Country.CR));

        // Create
        ClientResponse response = memberV2ApiClient_release.createAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        // Get all and verify
        response = memberV2ApiClient_release.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_v2.Addresses addresses = response.getEntity(org.orcid.jaxb.model.record_v2.Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());

        boolean foundCR = false;
        boolean foundUS = false;

        for (org.orcid.jaxb.model.record_v2.Address add : addresses.getAddress()) {
            if (add.getCountry().getValue().equals(org.orcid.jaxb.model.common_v2.Iso3166Country.CR)) {
                assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, add.getVisibility());
                foundCR = true;
            } else if (add.getCountry().getValue().equals(org.orcid.jaxb.model.common_v2.Iso3166Country.US)) {
                assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, add.getVisibility());
                foundUS = true;
            }
        }

        assertTrue(foundCR);
        assertTrue(foundUS);

        // Get it
        response = memberV2ApiClient_release.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        address = response.getEntity(org.orcid.jaxb.model.record_v2.Address.class);
        assertNotNull(address);
        assertNotNull(address.getSource());
        assertEquals(getClient1ClientId(), address.getSource().retrieveSourcePath());
        assertNotNull(address.getCountry());
        assertNotNull(address.getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.common_v2.Iso3166Country.CR, address.getCountry().getValue());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, address.getVisibility());
        assertNotNull(address.getDisplayIndex());
        Long originalDisplayIndex = address.getDisplayIndex();

        // Save the original visibility
        org.orcid.jaxb.model.common_v2.Visibility originalVisibility = address.getVisibility();
        org.orcid.jaxb.model.common_v2.Visibility updatedVisibility = org.orcid.jaxb.model.common_v2.Visibility.PRIVATE;

        // Verify you can't update the visibility
        address.setVisibility(updatedVisibility);
        ClientResponse putResponse = memberV2ApiClient_release.updateAddress(getUser1OrcidId(), address, accessToken);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        org.orcid.jaxb.model.error_v2.OrcidError error = putResponse.getEntity(org.orcid.jaxb.model.error_v2.OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());

        // Set the visibility again to the initial one
        address.setVisibility(originalVisibility);

        // Update
        address.getCountry().setValue(org.orcid.jaxb.model.common_v2.Iso3166Country.PA);
        response = memberV2ApiClient_release.updateAddress(getUser1OrcidId(), address, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient_release.viewAddress(getUser1OrcidId(), putCode, accessToken);
        assertNotNull(response);
        org.orcid.jaxb.model.record_v2.Address updatedAddress = response.getEntity(org.orcid.jaxb.model.record_v2.Address.class);
        assertNotNull(updatedAddress);
        assertNotNull(updatedAddress.getCountry());
        assertEquals(org.orcid.jaxb.model.common_v2.Iso3166Country.PA, updatedAddress.getCountry().getValue());
        assertEquals(address.getPutCode(), updatedAddress.getPutCode());
        assertEquals(originalDisplayIndex, updatedAddress.getDisplayIndex());

        // Delete
        response = memberV2ApiClient_release.deleteAddress(getUser1OrcidId(), putCode, accessToken);
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
        changeAddressVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        saveEditAddressModal();

        // RC2
        ClientResponse response = memberV2ApiClient_rc2.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc2.Addresses addresses_rc2 = response.getEntity(org.orcid.jaxb.model.record_rc2.Addresses.class);
        assertNotNull(addresses_rc2);
        assertNotNull(addresses_rc2.getAddress());
        assertEquals(1, addresses_rc2.getAddress().size());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED, addresses_rc2.getAddress().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_rc2.Iso3166Country.US, addresses_rc2.getAddress().get(0).getCountry().getValue());

        // RC3
        response = memberV2ApiClient_rc3.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc3.Addresses addresses_rc3 = response.getEntity(org.orcid.jaxb.model.record_rc3.Addresses.class);
        assertNotNull(addresses_rc3);
        assertNotNull(addresses_rc3.getAddress());
        assertEquals(1, addresses_rc3.getAddress().size());
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.LIMITED, addresses_rc3.getAddress().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_rc3.Iso3166Country.US, addresses_rc3.getAddress().get(0).getCountry().getValue());

        // RC4
        response = memberV2ApiClient_rc4.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc4.Addresses addresses_rc4 = response.getEntity(org.orcid.jaxb.model.record_rc4.Addresses.class);
        assertNotNull(addresses_rc4);
        assertNotNull(addresses_rc4.getAddress());
        assertEquals(1, addresses_rc4.getAddress().size());
        assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.LIMITED, addresses_rc4.getAddress().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_rc4.Iso3166Country.US, addresses_rc4.getAddress().get(0).getCountry().getValue());

        // Release        
        response = memberV2ApiClient_release.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_v2.Addresses addresses_v2 = response.getEntity(org.orcid.jaxb.model.record_v2.Addresses.class);
        assertNotNull(addresses_v2);
        assertNotNull(addresses_v2.getAddress());
        assertEquals(1, addresses_v2.getAddress().size());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, addresses_v2.getAddress().get(0).getVisibility());
        assertEquals(org.orcid.jaxb.model.common_v2.Iso3166Country.US, addresses_v2.getAddress().get(0).getCountry().getValue());
        
        // SET THEM ALL TO PRIVATE
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
        saveEditAddressModal();

        // RC2
        response = memberV2ApiClient_rc2.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        addresses_rc2 = response.getEntity(org.orcid.jaxb.model.record_rc2.Addresses.class);
        assertNotNull(addresses_rc2);
        assertNull(addresses_rc2.getAddress());

        // RC3
        response = memberV2ApiClient_rc3.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        addresses_rc3 = response.getEntity(org.orcid.jaxb.model.record_rc3.Addresses.class);
        assertNotNull(addresses_rc3);
        assertNull(addresses_rc3.getAddress());

        // RC4
        response = memberV2ApiClient_rc4.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        addresses_rc4 = response.getEntity(org.orcid.jaxb.model.record_rc4.Addresses.class);
        assertNotNull(addresses_rc4);
        assertNotNull(addresses_rc4.getAddress());
        assertTrue(addresses_rc4.getAddress().isEmpty());

        // Release
        response = memberV2ApiClient_release.viewAddresses(getUser1OrcidId(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        addresses_v2 = response.getEntity(org.orcid.jaxb.model.record_v2.Addresses.class);
        assertNotNull(addresses_v2);
        assertNotNull(addresses_v2.getAddress());
        assertTrue(addresses_v2.getAddress().isEmpty());
        
        // SET THEM ALL TO PUBLIC BEFORE FINISHING THE TEST
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        saveEditAddressModal();
    }

    @Test
    public void testGetAddressWithPublicAPI() throws InterruptedException, JSONException {
        // RC2
        ClientResponse response = publicV2ApiClient_rc2.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc2.Addresses addresses_rc2 = response.getEntity(org.orcid.jaxb.model.record_rc2.Addresses.class);
        assertNotNull(addresses_rc2);
        assertNotNull(addresses_rc2.getAddress());

        boolean found = false;

        for (org.orcid.jaxb.model.record_rc2.Address add : addresses_rc2.getAddress()) {
            assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC, add.getVisibility());
            if (add.getCountry().getValue().equals(org.orcid.jaxb.model.common_rc2.Iso3166Country.US)) {
                found = true;                
            }
        }

        // RC3
        response = publicV2ApiClient_rc3.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc3.Addresses addresses_rc3 = response.getEntity(org.orcid.jaxb.model.record_rc3.Addresses.class);
        assertNotNull(addresses_rc3);
        assertNotNull(addresses_rc3.getAddress());

        found = false;

        for (org.orcid.jaxb.model.record_rc3.Address add : addresses_rc3.getAddress()) {
            assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, add.getVisibility());
            if (add.getCountry().getValue().equals(org.orcid.jaxb.model.common_rc3.Iso3166Country.US)) {
                found = true;
            }
        }

        // RC4
        response = publicV2ApiClient_rc4.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_rc4.Addresses addresses_rc4 = response.getEntity(org.orcid.jaxb.model.record_rc4.Addresses.class);
        assertNotNull(addresses_rc4);
        assertNotNull(addresses_rc4.getAddress());

        found = false;

        for (org.orcid.jaxb.model.record_rc4.Address add : addresses_rc4.getAddress()) {
            assertEquals(org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC, add.getVisibility());
            if (add.getCountry().getValue().equals(org.orcid.jaxb.model.common_rc4.Iso3166Country.US)) {
                found = true;
            }
        }

        // Release
        response = publicV2ApiClient_release.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        org.orcid.jaxb.model.record_v2.Addresses addresses_v2 = response.getEntity(org.orcid.jaxb.model.record_v2.Addresses.class);
        assertNotNull(addresses_v2);
        assertNotNull(addresses_v2.getAddress());

        found = false;

        for (org.orcid.jaxb.model.record_v2.Address add : addresses_v2.getAddress()) {
            assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, add.getVisibility());
            if (add.getCountry().getValue().equals(org.orcid.jaxb.model.common_v2.Iso3166Country.US)) {
                found = true;
            }
        }        
        
        // SET THEM ALL TO LIMITED
        assertTrue(found);
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
        saveEditAddressModal();

        // RC2
        response = publicV2ApiClient_rc2.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        addresses_rc2 = response.getEntity(org.orcid.jaxb.model.record_rc2.Addresses.class);
        assertNotNull(addresses_rc2);
        assertNull(addresses_rc2.getAddress());

        // RC3
        response = publicV2ApiClient_rc3.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        addresses_rc3 = response.getEntity(org.orcid.jaxb.model.record_rc3.Addresses.class);
        assertNotNull(addresses_rc3);
        assertNull(addresses_rc3.getAddress());

        // RC4
        response = publicV2ApiClient_rc4.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        addresses_rc4 = response.getEntity(org.orcid.jaxb.model.record_rc4.Addresses.class);
        assertNotNull(addresses_rc4);
        assertNotNull(addresses_rc4.getAddress());
        assertTrue(addresses_rc4.getAddress().isEmpty());

        // Release
        response = publicV2ApiClient_release.viewAddressesXML(getUser1OrcidId());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        addresses_v2 = response.getEntity(org.orcid.jaxb.model.record_v2.Addresses.class);
        assertNotNull(addresses_v2);
        assertNotNull(addresses_v2.getAddress());
        assertTrue(addresses_v2.getAddress().isEmpty());
        
        // SET THEM ALL TO PUBLIC BEFORE FINISHING THE TEST
        assertTrue(found);
        showMyOrcidPage();
        openEditAddressModal();
        changeAddressVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        saveEditAddressModal();
    }

    @Test
    public void testInvalidPutCodeReturns404() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        assertNotNull(accessToken);

        // RC2
        org.orcid.jaxb.model.record_rc2.Address address_rc2 = new org.orcid.jaxb.model.record_rc2.Address();
        address_rc2.setCountry(new org.orcid.jaxb.model.common_rc2.Country(org.orcid.jaxb.model.common_rc2.Iso3166Country.MX));
        address_rc2.setPutCode(1234567890L);

        ClientResponse response = memberV2ApiClient_rc2.updateAddress(getUser1OrcidId(), address_rc2, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());

        // RC3
        org.orcid.jaxb.model.record_rc3.Address address_rc3 = new org.orcid.jaxb.model.record_rc3.Address();
        address_rc3.setCountry(new org.orcid.jaxb.model.common_rc3.Country(org.orcid.jaxb.model.common_rc3.Iso3166Country.MX));
        address_rc3.setPutCode(1234567890L);

        response = memberV2ApiClient_rc3.updateAddress(getUser1OrcidId(), address_rc3, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());

        // RC4
        org.orcid.jaxb.model.record_rc4.Address address_rc4 = new org.orcid.jaxb.model.record_rc4.Address();
        address_rc4.setCountry(new org.orcid.jaxb.model.common_rc4.Country(org.orcid.jaxb.model.common_rc4.Iso3166Country.MX));
        address_rc4.setPutCode(1234567890L);

        response = memberV2ApiClient_rc4.updateAddress(getUser1OrcidId(), address_rc4, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        
        // Release
        org.orcid.jaxb.model.record_v2.Address address_v2 = new org.orcid.jaxb.model.record_v2.Address();
        address_v2.setCountry(new org.orcid.jaxb.model.common_v2.Country(org.orcid.jaxb.model.common_v2.Iso3166Country.MX));
        address_v2.setPutCode(1234567890L);
        
        response = memberV2ApiClient_release.updateAddress(getUser1OrcidId(), address_v2, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        
    }

    public String getAccessToken() throws InterruptedException, JSONException {
        List<String> scopes = getScopes(ScopePathType.PERSON_UPDATE, ScopePathType.READ_LIMITED);
        return getAccessToken(getUser1OrcidId(), getUser1Password(), scopes, getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
    }
}
