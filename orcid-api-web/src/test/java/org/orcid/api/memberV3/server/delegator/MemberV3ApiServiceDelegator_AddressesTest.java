package org.orcid.api.memberV3.server.delegator;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import jakarta.persistence.NoResultException;
import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Country;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the address endpoints of the member V3 API.
 *
 * <p>
 * {@code checkAndFilter} is void and filters in place, so a mocked security
 * manager filters nothing; the visibility tables these tests used to exercise
 * are proved in orcid-core by {@code OrcidSecurityManager_generalTest}. What is
 * asserted here is the delegator's own contract, plus a {@code verify} that the
 * element was handed to the security manager with the right scope.
 */
public class MemberV3ApiServiceDelegator_AddressesTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final ScopePathType SCOPE = ScopePathType.ORCID_BIO_READ_LIMITED;

    private static final String USER_4442 = "4444-4444-4444-4442";
    private static final String USER_4447 = "4444-4444-4444-4447";
    private static final String USER_4499 = "4444-4444-4444-4499";

    private Address address(long putCode, Iso3166Country country, Visibility visibility, Source source) {
        Address element = new Address();
        element.setPutCode(putCode);
        element.setCountry(new Country(country));
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        element.setCreatedDate(created());
        return element;
    }

    private Addresses addresses(Address... elements) {
        Addresses container = new Addresses();
        container.setAddress(new ArrayList<>(Arrays.asList(elements)));
        return container;
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewAddressesWrongToken() {
        when(addressManagerReadOnly.getAddresses(ORCID)).thenReturn(addresses(address(9L, Iso3166Country.US, Visibility.PUBLIC, clientSource(CLIENT_1))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));

        serviceDelegator.viewAddresses(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewAddressWrongToken() {
        when(addressManagerReadOnly.getAddress(ORCID, 10L)).thenReturn(address(10L, Iso3166Country.US, Visibility.LIMITED, clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Address.class), eq(SCOPE));

        serviceDelegator.viewAddress(ORCID, 10L);
    }

    @Test
    public void testViewAddressesReadPublic() {
        when(addressManagerReadOnly.getAddresses(ORCID)).thenReturn(addresses(address(9L, Iso3166Country.US, Visibility.PUBLIC, clientSource(CLIENT_1))));

        Response r = serviceDelegator.viewAddresses(ORCID);
        Addresses element = (Addresses) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/address", element.getPath());
        assertEquals("/0000-0000-0000-0003/address/9", element.getAddress().get(0).getPath());
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testViewAddressReadPublic() {
        Address stored = address(9L, Iso3166Country.US, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(addressManagerReadOnly.getAddress(ORCID, 9L)).thenReturn(stored);

        Response r = serviceDelegator.viewAddress(ORCID, 9L);
        Address element = (Address) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/address/9", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testReadPublicScope_Address() {
        when(addressManagerReadOnly.getAddresses(ORCID)).thenReturn(addresses(
                address(9L, Iso3166Country.US, Visibility.PUBLIC, clientSource(CLIENT_1)),
                address(10L, Iso3166Country.CR, Visibility.LIMITED, clientSource(CLIENT_1)),
                address(11L, Iso3166Country.CR, Visibility.PRIVATE, clientSource(CLIENT_1))));
        when(addressManagerReadOnly.getAddress(ORCID, 9L)).thenReturn(address(9L, Iso3166Country.US, Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(addressManagerReadOnly.getAddress(ORCID, 10L)).thenReturn(address(10L, Iso3166Country.CR, Visibility.LIMITED, clientSource(CLIENT_1)));
        when(addressManagerReadOnly.getAddress(ORCID, 11L)).thenReturn(address(11L, Iso3166Country.CR, Visibility.PRIVATE, clientSource(CLIENT_1)));

        Address limitedOtherSource = address(12L, Iso3166Country.CR, Visibility.LIMITED, userSource(ORCID));
        Address privateOtherSource = address(13L, Iso3166Country.CR, Visibility.PRIVATE, userSource(ORCID));
        when(addressManagerReadOnly.getAddress(ORCID, 12L)).thenReturn(limitedOtherSource);
        when(addressManagerReadOnly.getAddress(ORCID, 13L)).thenReturn(privateOtherSource);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, limitedOtherSource, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, privateOtherSource, SCOPE);

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

        r = serviceDelegator.viewAddress(ORCID, 9L);
        assertNotNull(r);
        assertEquals(Address.class.getName(), r.getEntity().getClass().getName());

        // Limited where am the source should work
        serviceDelegator.viewAddress(ORCID, 10L);
        // Limited where am not the source of should fail
        try {
            serviceDelegator.viewAddress(ORCID, 12L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }
        // Private where am the source should work
        serviceDelegator.viewAddress(ORCID, 11L);
        // Private where am not the source of should fail
        try {
            serviceDelegator.viewAddress(ORCID, 13L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testViewAddresses() {
        when(addressManagerReadOnly.getAddresses(USER_4447)).thenReturn(addresses(
                address(2L, Iso3166Country.US, Visibility.PUBLIC, userSource(USER_4447)),
                address(3L, Iso3166Country.CR, Visibility.LIMITED, clientSource(CLIENT_1)),
                address(4L, Iso3166Country.CR, Visibility.PRIVATE, clientSource(CLIENT_1))));

        Response response = serviceDelegator.viewAddresses(USER_4447);
        assertNotNull(response);
        Addresses addresses = (Addresses) response.getEntity();
        assertNotNull(addresses);
        assertEquals("/4444-4444-4444-4447/address", addresses.getPath());
        Utils.verifyLastModified(addresses.getLastModifiedDate());
        assertNotNull(addresses.getAddress());
        assertEquals(3, addresses.getAddress().size());

        for (Address address : addresses.getAddress()) {
            Utils.verifyLastModified(address.getLastModifiedDate());
            assertThat(address.getPutCode(), anyOf(is(2L), is(3L), is(4L)));
            assertThat(address.getCountry().getValue(), anyOf(is(Iso3166Country.CR), is(Iso3166Country.US)));
            if (address.getPutCode() == 2L) {
                assertEquals(Visibility.PUBLIC, address.getVisibility());
                assertEquals(USER_4447, address.getSource().retrieveSourcePath());
            } else if (address.getPutCode() == 3L) {
                assertEquals(Visibility.LIMITED, address.getVisibility());
                assertEquals(CLIENT_1, address.getSource().retrieveSourcePath());
            } else if (address.getPutCode() == 4L) {
                assertEquals(Visibility.PRIVATE, address.getVisibility());
                assertEquals(CLIENT_1, address.getSource().retrieveSourcePath());
            }
        }
        verify(orcidSecurityManager).checkAndFilter(eq(USER_4447), anyList(), eq(SCOPE));
    }

    @Test
    public void testViewPublicAddress() {
        when(addressManagerReadOnly.getAddress(USER_4447, 2L)).thenReturn(address(2L, Iso3166Country.US, Visibility.PUBLIC, userSource(USER_4447)));

        Response response = serviceDelegator.viewAddress(USER_4447, 2L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals("/4444-4444-4444-4447/address/2", address.getPath());
        Utils.verifyLastModified(address.getLastModifiedDate());
        assertEquals(Visibility.PUBLIC, address.getVisibility());
        assertEquals(USER_4447, address.getSource().retrieveSourcePath());
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        verify(orcidSecurityManager).checkAndFilter(USER_4447, address, SCOPE);
    }

    @Test
    public void testViewLimitedAddress() {
        when(addressManagerReadOnly.getAddress(USER_4447, 3L)).thenReturn(address(3L, Iso3166Country.CR, Visibility.LIMITED, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewAddress(USER_4447, 3L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals("/4444-4444-4444-4447/address/3", address.getPath());
        Utils.verifyLastModified(address.getLastModifiedDate());
        assertEquals(Visibility.LIMITED, address.getVisibility());
        assertEquals(CLIENT_1, address.getSource().retrieveSourcePath());
        assertEquals(Iso3166Country.CR, address.getCountry().getValue());
        verify(orcidSecurityManager).checkAndFilter(USER_4447, address, SCOPE);
    }

    @Test
    public void testViewPrivateAddress() {
        when(addressManagerReadOnly.getAddress(USER_4447, 4L)).thenReturn(address(4L, Iso3166Country.CR, Visibility.PRIVATE, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewAddress(USER_4447, 4L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals("/4444-4444-4444-4447/address/4", address.getPath());
        Utils.verifyLastModified(address.getLastModifiedDate());
        assertEquals(Visibility.PRIVATE, address.getVisibility());
        assertEquals(CLIENT_1, address.getSource().retrieveSourcePath());
        assertEquals(Iso3166Country.CR, address.getCountry().getValue());
        verify(orcidSecurityManager).checkAndFilter(USER_4447, address, SCOPE);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateAddressWhereYouAreNotTheSource() {
        Address stored = address(5L, Iso3166Country.CR, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(addressManagerReadOnly.getAddress(USER_4447, 5L)).thenReturn(stored);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(USER_4447, stored, SCOPE);

        serviceDelegator.viewAddress(USER_4447, 5L);
        fail();
    }

    /**
     * The rule this proves -- that address 1 cannot be read through record 4447 --
     * lives in a SQL WHERE clause ({@code AddressDaoImpl.getAddress}), so with a
     * mocked manager only the pass-through survives here. The predicate itself is
     * proved by MemberV3ApiServiceDelegatorDatabaseRulesTest in the db-tests
     * stage.
     */
    @Test(expected = NoResultException.class)
    public void testViewAddressThatDontBelongToTheUser() {
        when(addressManagerReadOnly.getAddress(USER_4447, 1L)).thenThrow(new NoResultException());

        serviceDelegator.viewAddress(USER_4447, 1L);
        fail();
    }

    @Test
    public void testAddAddress() {
        Address created = address(1000L, Iso3166Country.ES, Visibility.LIMITED, clientSource(CLIENT_1));
        when(addressManager.createAddress(eq(USER_4442), any(Address.class), eq(true))).thenReturn(created);
        when(addressManagerReadOnly.getAddress(USER_4442, 1000L)).thenReturn(created);

        Address toCreate = Utils.getAddress();
        // Planted so that assertNull below proves clearSource ran, rather than
        // only proving the fixture never had a source to begin with.
        toCreate.setSource(clientSource(CLIENT_2));

        Response response = serviceDelegator.createAddress(USER_4442, toCreate);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = Utils.getPutCode(response);
        assertEquals(Long.valueOf(1000L), putCode);

        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4442, ScopePathType.ORCID_BIO_UPDATE);
        // A client supplied source must never reach the manager.
        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
        verify(addressManager).createAddress(eq(USER_4442), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());

        response = serviceDelegator.viewAddress(USER_4442, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Address newAddress = (Address) response.getEntity();
        assertNotNull(newAddress);
        Utils.verifyLastModified(newAddress.getLastModifiedDate());
        assertEquals(Iso3166Country.ES, newAddress.getCountry().getValue());
        assertEquals(Visibility.LIMITED, newAddress.getVisibility());
        assertNotNull(newAddress.getSource());
        assertEquals(CLIENT_1, newAddress.getSource().retrieveSourcePath());
        assertNotNull(newAddress.getCreatedDate());

        // Remove it
        response = serviceDelegator.deleteAddress(USER_4442, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(addressManager).deleteAddress(USER_4442, 1000L);
    }

    @Test
    public void testUpdateAddress() {
        when(addressManagerReadOnly.getAddress(USER_4442, 1L)).thenReturn(address(1L, Iso3166Country.US, Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(addressManager.updateAddress(eq(USER_4442), eq(1L), any(Address.class), eq(true)))
                .thenReturn(address(1L, Iso3166Country.PA, Visibility.PUBLIC, clientSource(CLIENT_1)))
                .thenReturn(address(1L, Iso3166Country.US, Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewAddress(USER_4442, 1L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        Utils.verifyLastModified(address.getLastModifiedDate());
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC, address.getVisibility());

        address.setCountry(new Country(Iso3166Country.PA));

        response = serviceDelegator.updateAddress(USER_4442, 1L, address);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        address = (Address) response.getEntity();
        assertEquals(Iso3166Country.PA, address.getCountry().getValue());
        assertEquals("/4444-4444-4444-4442/address/1", address.getPath());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4442, ScopePathType.ORCID_BIO_UPDATE);

        // Rollback
        address.setCountry(new Country(Iso3166Country.US));
        response = serviceDelegator.updateAddress(USER_4442, 1L, address);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        address = (Address) response.getEntity();
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC, address.getVisibility());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateAddressYouAreNotTheSourceOf() {
        when(addressManagerReadOnly.getAddress(USER_4447, 2L)).thenReturn(address(2L, Iso3166Country.US, Visibility.PUBLIC, userSource(USER_4447)));
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(addressManager).updateAddress(eq(USER_4447), eq(2L), any(Address.class), eq(true));

        Response response = serviceDelegator.viewAddress(USER_4447, 2L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC, address.getVisibility());
        assertEquals(USER_4447, address.getSource().retrieveSourcePath());

        address.setCountry(new Country(Iso3166Country.PA));

        serviceDelegator.updateAddress(USER_4447, 2L, address);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateAddressChangingVisibilityTest() {
        when(addressManagerReadOnly.getAddress(USER_4442, 1L)).thenReturn(address(1L, Iso3166Country.US, Visibility.PUBLIC, clientSource(CLIENT_1)));
        doThrow(new VisibilityMismatchException()).when(addressManager).updateAddress(eq(USER_4442), eq(1L), any(Address.class), eq(true));

        Response response = serviceDelegator.viewAddress(USER_4442, 1L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Visibility.PUBLIC, address.getVisibility());

        address.setVisibility(Visibility.PRIVATE);

        serviceDelegator.updateAddress(USER_4442, 1L, address);
        fail();
    }

    @Test
    public void testUpdateAddressLeavingVisibilityNullTest() {
        when(addressManagerReadOnly.getAddress(USER_4442, 1L)).thenReturn(address(1L, Iso3166Country.US, Visibility.PUBLIC, clientSource(CLIENT_1)));
        // Restoring the stored visibility is the manager's job and is proved
        // there; here the delegator must simply return what it produced.
        when(addressManager.updateAddress(eq(USER_4442), eq(1L), any(Address.class), eq(true)))
                .thenReturn(address(1L, Iso3166Country.US, Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewAddress(USER_4442, 1L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Visibility.PUBLIC, address.getVisibility());

        address.setVisibility(null);

        response = serviceDelegator.updateAddress(USER_4442, 1L, address);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Visibility.PUBLIC, address.getVisibility());
    }

    @Test
    public void testDeleteAddress() {
        when(addressManagerReadOnly.getAddresses(USER_4499))
                .thenReturn(addresses(address(30L, Iso3166Country.US, Visibility.PUBLIC, clientSource(CLIENT_1)))).thenReturn(addresses());

        Response response = serviceDelegator.viewAddresses(USER_4499);
        assertNotNull(response);
        Addresses addresses = (Addresses) response.getEntity();
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        assertEquals(1, addresses.getAddress().size());
        Long putCode = addresses.getAddress().get(0).getPutCode();

        response = serviceDelegator.deleteAddress(USER_4499, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4499, ScopePathType.ORCID_BIO_UPDATE);
        verify(addressManager).deleteAddress(USER_4499, 30L);

        response = serviceDelegator.viewAddresses(USER_4499);
        assertNotNull(response);
        addresses = (Addresses) response.getEntity();
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        assertTrue(addresses.getAddress().isEmpty());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteAddressYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(new HashMap<String, String>())).when(addressManager).deleteAddress(USER_4447, 5L);

        serviceDelegator.deleteAddress(USER_4447, 5L);
        fail();
    }

    @Test
    public void testAddKosovoAddress() {
        Address created = address(1001L, Iso3166Country.XK, Visibility.LIMITED, clientSource(CLIENT_1));
        when(addressManager.createAddress(eq(USER_4442), any(Address.class), eq(true))).thenReturn(created);
        when(addressManagerReadOnly.getAddress(USER_4442, 1001L)).thenReturn(created);

        Address kosovo = Utils.getAddress();
        kosovo.setCountry(new Country(Iso3166Country.XK));
        Response response = serviceDelegator.createAddress(USER_4442, kosovo);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = Utils.getPutCode(response);

        // XK must survive the round trip to the manager unchanged.
        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
        verify(addressManager).createAddress(eq(USER_4442), captor.capture(), eq(true));
        assertEquals(Iso3166Country.XK, captor.getValue().getCountry().getValue());

        response = serviceDelegator.viewAddress(USER_4442, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Address newAddress = (Address) response.getEntity();
        assertNotNull(newAddress);
        Utils.verifyLastModified(newAddress.getLastModifiedDate());
        assertEquals(Iso3166Country.XK, newAddress.getCountry().getValue());
        assertEquals(Visibility.LIMITED, newAddress.getVisibility());
        assertNotNull(newAddress.getSource());
        assertEquals(CLIENT_1, newAddress.getSource().retrieveSourcePath());
        assertNotNull(newAddress.getCreatedDate());

        // Remove it
        response = serviceDelegator.deleteAddress(USER_4442, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }
}
