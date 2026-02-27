package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Country;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_AddressesTest extends MemberV3ApiServiceDelegatorMockTest {

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewAddressesWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewAddresses(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewAddressWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewAddress(ORCID, 10L);
    }

    @Test
    public void testViewAddressesReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        when(addressManagerReadOnly.getAddresses(eq(ORCID))).thenReturn(new Addresses());
        Response r = serviceDelegator.viewAddresses(ORCID);
        Addresses element = (Addresses) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/address", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewAddressReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Address address = new Address();
        address.setPutCode(9L);
        when(addressManagerReadOnly.getAddress(eq(ORCID), eq(9L))).thenReturn(address);
        Response r = serviceDelegator.viewAddress(ORCID, 9L);
        Address element = (Address) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/address/9", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testReadPublicScope_Address() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<>());
        addresses.getAddress().add(createAddress(9L));
        addresses.getAddress().add(createAddress(10L));
        addresses.getAddress().add(createAddress(11L));
        
        when(addressManagerReadOnly.getAddresses(eq(ORCID))).thenReturn(addresses);
        
        // Public works
        Response r = serviceDelegator.viewAddresses(ORCID);
        assertNotNull(r);
        assertEquals(Addresses.class.getName(), r.getEntity().getClass().getName());
        Addresses a = (Addresses) r.getEntity();
        assertNotNull(a);
        assertEquals("/0000-0000-0000-0003/address", a.getPath());
        Utils.verifyLastModified(a.getLastModifiedDate());
        assertEquals(3, a.getAddress().size());
        boolean found9 = false, found10 = false, found11 = false;
        for (Address address : a.getAddress()) {
            if (address.getPutCode() == 9) {
                found9 = true;
            } else if (address.getPutCode() == 10) {
                found10 = true;
            } else if (address.getPutCode() == 11) {
                found11 = true;
            } else {
                fail("Invalid put code " + address.getPutCode());
            }

        }
        assertTrue(found9);
        assertTrue(found10);
        assertTrue(found11);

        when(addressManagerReadOnly.getAddress(eq(ORCID), eq(9L))).thenReturn(createAddress(9L));
        r = serviceDelegator.viewAddress(ORCID, 9L);
        assertNotNull(r);
        assertEquals(Address.class.getName(), r.getEntity().getClass().getName());

        // Limited where am the source should work
        when(addressManagerReadOnly.getAddress(eq(ORCID), eq(10L))).thenReturn(createAddress(10L));
        serviceDelegator.viewAddress(ORCID, 10L);

        // Limited am not the source should fail
        when(addressManagerReadOnly.getAddress(eq(ORCID), eq(12L))).thenReturn(createAddress(12L));
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Address.class), any(ScopePathType.class));
        try {
            serviceDelegator.viewAddress(ORCID, 12L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private where am the source should work
        when(addressManagerReadOnly.getAddress(eq(ORCID), eq(11L))).thenReturn(createAddress(11L));
        // Reset mock for success case
        serviceDelegator.viewAddress(ORCID, 11L);
        
        try {
            // Private am not the source should fail
            when(addressManagerReadOnly.getAddress(eq(ORCID), eq(13L))).thenReturn(createAddress(13L));
            doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Address.class), any(ScopePathType.class));
            serviceDelegator.viewAddress(ORCID, 13L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testViewAddresses() {
        String orcid = "4444-4444-4444-4447";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.PERSON_READ_LIMITED);
        
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<>());
        addresses.getAddress().add(createAddress(2L, Visibility.PUBLIC, orcid, null));
        addresses.getAddress().add(createAddress(3L, Visibility.LIMITED, null, "APP-5555555555555555"));
        addresses.getAddress().add(createAddress(4L, Visibility.PRIVATE, null, "APP-5555555555555555"));
        
        when(addressManagerReadOnly.getAddresses(eq(orcid))).thenReturn(addresses);
        
        Response response = serviceDelegator.viewAddresses(orcid);
        assertNotNull(response);
        Addresses result = (Addresses) response.getEntity();
        assertNotNull(result);
        assertEquals("/" + orcid + "/address", result.getPath());
        Utils.verifyLastModified(result.getLastModifiedDate());
        assertNotNull(result.getAddress());
        assertEquals(3, result.getAddress().size());

        for (Address address : result.getAddress()) {
            Utils.verifyLastModified(address.getLastModifiedDate());
            assertThat(address.getPutCode(), anyOf(is(2L), is(3L), is(4L)));
            assertThat(address.getCountry().getValue(), anyOf(is(Iso3166Country.CR), is(Iso3166Country.US)));
            if (address.getPutCode() == 2L) {
                assertEquals(Visibility.PUBLIC, address.getVisibility());
                assertEquals(orcid, address.getSource().retrieveSourcePath());
            } else if (address.getPutCode() == 3L) {
                assertEquals(Visibility.LIMITED, address.getVisibility());
                assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
            } else if (address.getPutCode() == 4L) {
                assertEquals(Visibility.PRIVATE, address.getVisibility());
                assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
            }
        }
    }

    @Test
    public void testViewPublicAddress() {
        String orcid = "4444-4444-4444-4447";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.PERSON_READ_LIMITED);
        
        Address address = createAddress(2L, Visibility.PUBLIC, orcid, null);
        when(addressManagerReadOnly.getAddress(eq(orcid), eq(2L))).thenReturn(address);
        
        Response response = serviceDelegator.viewAddress(orcid, 2L);
        assertNotNull(response);
        Address result = (Address) response.getEntity();
        assertNotNull(result);
        assertEquals("/" + orcid + "/address/2", result.getPath());
        Utils.verifyLastModified(result.getLastModifiedDate());
        assertEquals(Visibility.PUBLIC, result.getVisibility());
        assertEquals(orcid, result.getSource().retrieveSourcePath());
        assertEquals(Iso3166Country.US, result.getCountry().getValue());
    }

    @Test
    public void testViewLimitedAddress() {
        String orcid = "4444-4444-4444-4447";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.PERSON_READ_LIMITED);
        
        Address address = createAddress(3L, Visibility.LIMITED, null, "APP-5555555555555555");
        address.setCountry(new Country(Iso3166Country.CR));
        when(addressManagerReadOnly.getAddress(eq(orcid), eq(3L))).thenReturn(address);
        
        Response response = serviceDelegator.viewAddress(orcid, 3L);
        assertNotNull(response);
        Address result = (Address) response.getEntity();
        assertNotNull(result);
        assertEquals("/" + orcid + "/address/3", result.getPath());
        Utils.verifyLastModified(result.getLastModifiedDate());
        assertEquals(Visibility.LIMITED, result.getVisibility());
        assertEquals("APP-5555555555555555", result.getSource().retrieveSourcePath());
        assertEquals(Iso3166Country.CR, result.getCountry().getValue());
    }

    @Test
    public void testViewPrivateAddress() {
        String orcid = "4444-4444-4444-4447";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.PERSON_READ_LIMITED);
        
        Address address = createAddress(4L, Visibility.PRIVATE, null, "APP-5555555555555555");
        address.setCountry(new Country(Iso3166Country.CR));
        when(addressManagerReadOnly.getAddress(eq(orcid), eq(4L))).thenReturn(address);
        
        Response response = serviceDelegator.viewAddress(orcid, 4L);
        assertNotNull(response);
        Address result = (Address) response.getEntity();
        assertNotNull(result);
        assertEquals("/" + orcid + "/address/4", result.getPath());
        Utils.verifyLastModified(result.getLastModifiedDate());
        assertEquals(Visibility.PRIVATE, result.getVisibility());
        assertEquals("APP-5555555555555555", result.getSource().retrieveSourcePath());
        assertEquals(Iso3166Country.CR, result.getCountry().getValue());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateAddressWhereYouAreNotTheSource() {
        String orcid = "4444-4444-4444-4447";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.PERSON_READ_LIMITED);
        
        when(addressManagerReadOnly.getAddress(eq(orcid), eq(5L))).thenReturn(createAddress(5L));
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(eq(orcid), any(Address.class), any(ScopePathType.class));
        
        serviceDelegator.viewAddress(orcid, 5L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewAddressThatDontBelongToTheUser() {
        String orcid = "4444-4444-4444-4447";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.PERSON_READ_LIMITED);
        
        when(addressManagerReadOnly.getAddress(eq(orcid), eq(1L))).thenThrow(new NoResultException());
        
        serviceDelegator.viewAddress(orcid, 1L);
        fail();
    }

    @Test
    public void testAddAddress() {
        String orcid = "4444-4444-4444-4442";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        
        Address input = Utils.getAddress();
        Address saved = Utils.getAddress();
        saved.setPutCode(123L);
        saved.setVisibility(Visibility.LIMITED);
        Source s = new Source();
        s.setSourceClientId(new SourceClientId("APP-5555555555555555"));
        saved.setSource(s);
        
        when(addressManager.createAddress(eq(orcid), any(Address.class), anyBoolean())).thenReturn(saved);
        when(apiUtils.buildApiResponse(eq(orcid), anyString(), eq("123"), anyString())).thenReturn(Response.status(Response.Status.CREATED).entity("123").build());
        
        Response response = serviceDelegator.createAddress(orcid, input);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = 123L;

        when(addressManagerReadOnly.getAddress(eq(orcid), eq(putCode))).thenReturn(saved);
        response = serviceDelegator.viewAddress(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Address newAddress = (Address) response.getEntity();
        assertNotNull(newAddress);
        Utils.verifyLastModified(newAddress.getLastModifiedDate());
        assertEquals(Iso3166Country.ES, newAddress.getCountry().getValue());
        assertEquals(Visibility.LIMITED, newAddress.getVisibility());
        assertNotNull(newAddress.getSource());
        assertEquals("APP-5555555555555555", newAddress.getSource().retrieveSourcePath());
        
        // Remove it
        response = serviceDelegator.deleteAddress(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateAddress() {
        String orcid = "4444-4444-4444-4442";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        
        Address address = createAddress(1L, Visibility.PUBLIC, orcid, null);
        when(addressManagerReadOnly.getAddress(eq(orcid), eq(1L))).thenReturn(address);
        
        Response response = serviceDelegator.viewAddress(orcid, 1L);
        assertNotNull(response);
        Address result = (Address) response.getEntity();
        assertNotNull(result);
        
        result.getCountry().setValue(Iso3166Country.PA);
        when(addressManager.updateAddress(eq(orcid), eq(1L), any(Address.class), anyBoolean())).thenReturn(result);

        response = serviceDelegator.updateAddress(orcid, 1L, result);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Address updated = (Address) response.getEntity();
        assertEquals(Iso3166Country.PA, updated.getCountry().getValue());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateAddressYouAreNotTheSourceOf() {
        String orcid = "4444-4444-4444-4447";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        
        Address address = createAddress(2L, Visibility.PUBLIC, orcid, null);
        when(addressManagerReadOnly.getAddress(eq(orcid), eq(2L))).thenReturn(address);
        when(addressManager.updateAddress(eq(orcid), eq(2L), any(Address.class), anyBoolean())).thenThrow(new WrongSourceException(Collections.emptyMap()));
        
        address.getCountry().setValue(Iso3166Country.BR);

        serviceDelegator.updateAddress(orcid, 2L, address);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateAddressChangingVisibilityTest() {
        String orcid = "4444-4444-4444-4442";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        
        Address address = createAddress(1L, Visibility.PUBLIC, orcid, null);
        when(addressManagerReadOnly.getAddress(eq(orcid), eq(1L))).thenReturn(address);
        when(addressManager.updateAddress(eq(orcid), eq(1L), any(Address.class), anyBoolean())).thenThrow(new VisibilityMismatchException());
        
        address.setVisibility(Visibility.PRIVATE);

        serviceDelegator.updateAddress(orcid, 1L, address);
        fail();
    }

    @Test
    public void testUpdateAddressLeavingVisibilityNullTest() {
        String orcid = "4444-4444-4444-4442";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        
        Address address = createAddress(1L, Visibility.PUBLIC, orcid, null);
        when(addressManagerReadOnly.getAddress(eq(orcid), eq(1L))).thenReturn(address);
        when(addressManager.updateAddress(eq(orcid), eq(1L), any(Address.class), anyBoolean())).thenReturn(address);
        
        address.setVisibility(null);

        Response response = serviceDelegator.updateAddress(orcid, 1L, address);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Address result = (Address) response.getEntity();
        assertNotNull(result);
        assertEquals(Visibility.PUBLIC, result.getVisibility());
    }

    @Test
    public void testDeleteAddress() {
        String orcid = "4444-4444-4444-4499";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<>());
        addresses.getAddress().add(createAddress(100L));
        
        when(addressManagerReadOnly.getAddresses(eq(orcid))).thenReturn(addresses);
        when(addressManagerReadOnly.getAddresses(eq(orcid))).thenReturn(addresses, new Addresses()); // First call has it, second is empty
        
        Response response = serviceDelegator.viewAddresses(orcid);
        assertNotNull(response);
        Addresses result = (Addresses) response.getEntity();
        assertEquals(1, result.getAddress().size());
        
        Long putCode = result.getAddress().get(0).getPutCode();
        response = serviceDelegator.deleteAddress(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        response = serviceDelegator.viewAddresses(orcid);
        result = (Addresses) response.getEntity();
        assertTrue(result.getAddress() == null || result.getAddress().isEmpty());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteAddressYouAreNotTheSourceOf() {
        String orcid = "4444-4444-4444-4447";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.PERSON_UPDATE);
        
        when(addressManager.deleteAddress(eq(orcid), eq(5L))).thenThrow(new WrongSourceException(Collections.emptyMap()));
        
        serviceDelegator.deleteAddress(orcid, 5L);
        fail();
    }

    @Test
    public void testAddKosovoAddress() {
        String orcid = "4444-4444-4444-4442";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Address kosovo = Utils.getAddress();
        kosovo.setCountry(new Country(Iso3166Country.XK));
        
        Address saved = Utils.getAddress();
        saved.setPutCode(124L);
        saved.setCountry(new Country(Iso3166Country.XK));
        saved.setVisibility(Visibility.LIMITED);
        Source s2 = new Source();
        s2.setSourceClientId(new SourceClientId("APP-5555555555555555"));
        saved.setSource(s2);

        when(addressManager.createAddress(eq(orcid), any(Address.class), anyBoolean())).thenReturn(saved);
        when(apiUtils.buildApiResponse(eq(orcid), anyString(), eq("124"), anyString())).thenReturn(Response.status(Response.Status.CREATED).entity("124").build());
        
        Response response = serviceDelegator.createAddress(orcid, kosovo);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = 124L;

        when(addressManagerReadOnly.getAddress(eq(orcid), eq(putCode))).thenReturn(saved);
        response = serviceDelegator.viewAddress(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Address newAddress = (Address) response.getEntity();
        assertNotNull(newAddress);
        assertEquals(Iso3166Country.XK, newAddress.getCountry().getValue());
        
        // Remove it
        response = serviceDelegator.deleteAddress(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }
    
    private Address createAddress(Long putCode) {
        return createAddress(putCode, Visibility.PUBLIC, null, null);
    }
    
    private Address createAddress(Long putCode, Visibility visibility, String sourceOrcid, String sourceClientId) {
        Address a = new Address();
        a.setPutCode(putCode);
        a.setVisibility(visibility);
        a.setCountry(new Country(Iso3166Country.US));
        if (sourceOrcid != null || sourceClientId != null) {
            Source s = new Source();
            if (sourceOrcid != null) {
                s.setSourceOrcid(new SourceOrcid(sourceOrcid));
            }
            if (sourceClientId != null) {
                s.setSourceClientId(new SourceClientId(sourceClientId));
            }
            a.setSource(s);
        }
        return a;
    }
}
