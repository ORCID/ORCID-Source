package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.NoResultException;
import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.jaxb.model.common_v2.Country;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.test.helper.Utils;

/**
 * The address endpoints of the member v2 delegator, on mocks.
 *
 * <p>
 * What the delegator itself does with an address is set its path, resolve its
 * source name, hand it to {@link org.orcid.core.manager.OrcidSecurityManager},
 * and wrap it in a response. That is what is asserted here. Every rule that used
 * to be asserted through the database is named at its call site: visibility
 * filtering belongs to {@code OrcidSecurityManager_generalTest}, the
 * source-ownership rule to {@code AddressManagerImpl}'s tests, and "an address
 * of another record is not readable" to the SQL in {@code AddressDaoImpl}.
 */
public class MemberV2ApiServiceDelegator_AddressesTest extends MemberV2ApiServiceDelegatorMockBase {

    private static final String OTHER_ORCID = "4444-4444-4444-4447";
    private static final String MY_ORCID = "4444-4444-4444-4442";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewAddressesWrongToken() {
        when(addressManagerReadOnly.getAddresses(ORCID)).thenReturn(addresses(address(9L, Visibility.PUBLIC, clientSource(CLIENT_1))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(),
                eq(ScopePathType.ORCID_BIO_READ_LIMITED));

        try {
            serviceDelegator.viewAddresses(ORCID);
        } finally {
            // the refusal has to stop the read, not merely be recorded
            verifyNoInteractions(sourceNameCacheManager);
        }
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewAddressWrongToken() {
        Address address = address(10L, Visibility.LIMITED, clientSource(CLIENT_1));
        when(addressManagerReadOnly.getAddress(ORCID, 10L)).thenReturn(address);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, address,
                ScopePathType.ORCID_BIO_READ_LIMITED);

        try {
            serviceDelegator.viewAddress(ORCID, 10L);
        } finally {
            assertNull("the element must not be decorated once the guard has refused", address.getPath());
        }
    }

    @Test
    public void testViewAddressesReadPublic() {
        Address address = address(9L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(addressManagerReadOnly.getAddresses(ORCID)).thenReturn(addresses(address));

        Response r = serviceDelegator.viewAddresses(ORCID);

        Addresses element = (Addresses) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/address", element.getPath());
        assertEquals("/0000-0000-0000-0003/address/9", element.getAddress().get(0).getPath());
        // Which elements survive is checkAndFilter's decision, and it edits the
        // list in place, so a mock cannot demonstrate it. All this layer owes is
        // calling the guard with the right list and scope.
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(ScopePathType.ORCID_BIO_READ_LIMITED));
    }

    @Test
    public void testViewAddressReadPublic() {
        Address address = address(9L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(addressManagerReadOnly.getAddress(ORCID, 9L)).thenReturn(address);

        Response r = serviceDelegator.viewAddress(ORCID, 9L);

        Address element = (Address) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/address/9", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, address, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @Test
    public void testReadPublicScope_Address() {
        // A read-public token reaches checkAndFilter, which raises
        // OrcidAccessControlException for anything the token may not be shown.
        // The refusal is stubbed per element, never with a blanket matcher: a
        // matcher that refused everything would also refuse 9, 10 and 11 and the
        // "should work" half of this test would prove nothing.
        Address nine = address(9L, Visibility.PUBLIC, clientSource(CLIENT_1));
        Address ten = address(10L, Visibility.LIMITED, clientSource(CLIENT_1));
        Address eleven = address(11L, Visibility.PRIVATE, clientSource(CLIENT_1));
        Address twelve = address(12L, Visibility.LIMITED, clientSource(CLIENT_2));
        Address thirteen = address(13L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(addressManagerReadOnly.getAddress(ORCID, 9L)).thenReturn(nine);
        when(addressManagerReadOnly.getAddress(ORCID, 10L)).thenReturn(ten);
        when(addressManagerReadOnly.getAddress(ORCID, 11L)).thenReturn(eleven);
        when(addressManagerReadOnly.getAddress(ORCID, 12L)).thenReturn(twelve);
        when(addressManagerReadOnly.getAddress(ORCID, 13L)).thenReturn(thirteen);
        when(addressManagerReadOnly.getAddresses(ORCID)).thenReturn(addresses(nine, ten, eleven));
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, twelve, ScopePathType.ORCID_BIO_READ_LIMITED);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, thirteen, ScopePathType.ORCID_BIO_READ_LIMITED);

        Response r = serviceDelegator.viewAddresses(ORCID);
        assertNotNull(r);
        assertEquals(Addresses.class.getName(), r.getEntity().getClass().getName());
        Addresses a = (Addresses) r.getEntity();
        assertNotNull(a);
        assertEquals("/0000-0000-0000-0003/address", a.getPath());
        Utils.verifyLastModified(a.getLastModifiedDate());
        assertEquals(3, a.getAddress().size());
        for (Address address : a.getAddress()) {
            assertEquals("/0000-0000-0000-0003/address/" + address.getPutCode(), address.getPath());
        }

        r = serviceDelegator.viewAddress(ORCID, 9L);
        assertNotNull(r);
        assertEquals(Address.class.getName(), r.getEntity().getClass().getName());

        // Limited where am the source should work
        serviceDelegator.viewAddress(ORCID, 10L);

        try {
            // Limited am not the source should fail
            serviceDelegator.viewAddress(ORCID, 12L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private where am the source should work
        serviceDelegator.viewAddress(ORCID, 11L);
        try {
            // Private am not the source should fail
            serviceDelegator.viewAddress(ORCID, 13L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testViewAddresses() {
        Addresses stored = addresses(address(2L, Visibility.PUBLIC, userSource(OTHER_ORCID)), address(3L, Visibility.LIMITED, clientSource(CLIENT_1)),
                address(4L, Visibility.PRIVATE, clientSource(CLIENT_1)));
        when(addressManagerReadOnly.getAddresses(OTHER_ORCID)).thenReturn(stored);

        Response response = serviceDelegator.viewAddresses(OTHER_ORCID);

        assertNotNull(response);
        Addresses addresses = (Addresses) response.getEntity();
        assertNotNull(addresses);
        assertEquals("/4444-4444-4444-4447/address", addresses.getPath());
        Utils.verifyLastModified(addresses.getLastModifiedDate());
        assertNotNull(addresses.getAddress());
        assertEquals(3, addresses.getAddress().size());
        for (Address address : addresses.getAddress()) {
            Utils.verifyLastModified(address.getLastModifiedDate());
            assertEquals("/4444-4444-4444-4447/address/" + address.getPutCode(), address.getPath());
        }
        assertEquals(CLIENT_1_NAME, addresses.getAddress().get(1).getSource().getSourceName().getContent());

        // checkAndFilter edits the collection it is given in place, so the
        // delegator must copy the cached list before handing it over.
        ArgumentCaptor<List<Address>> filtered = addressListCaptor();
        verify(orcidSecurityManager).checkAndFilter(eq(OTHER_ORCID), filtered.capture(), eq(ScopePathType.ORCID_BIO_READ_LIMITED));
        assertNotSame(stored.getAddress(), filtered.getValue());
    }

    @Test
    public void testViewPublicAddress() {
        assertViewAddressDecorated(2L, Visibility.PUBLIC, userSource(OTHER_ORCID));
    }

    @Test
    public void testViewLimitedAddress() {
        assertViewAddressDecorated(3L, Visibility.LIMITED, clientSource(CLIENT_1));
    }

    @Test
    public void testViewPrivateAddress() {
        assertViewAddressDecorated(4L, Visibility.PRIVATE, clientSource(CLIENT_1));
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateAddressWhereYouAreNotTheSource() {
        Address address = address(5L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(addressManagerReadOnly.getAddress(OTHER_ORCID, 5L)).thenReturn(address);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(OTHER_ORCID, address, ScopePathType.ORCID_BIO_READ_LIMITED);

        serviceDelegator.viewAddress(OTHER_ORCID, 5L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewAddressThatDontBelongToTheUser() {
        // AddressDaoImpl selects on (id, orcid) together, so an address that
        // belongs to another record is simply not found. That predicate is in
        // SQL; what belongs to the delegator is that it does not swallow the
        // miss, and does not consult the guard about an element it never got.
        when(addressManagerReadOnly.getAddress(OTHER_ORCID, 1L)).thenThrow(new NoResultException());

        try {
            serviceDelegator.viewAddress(OTHER_ORCID, 1L);
            fail();
        } finally {
            verifyNoInteractions(orcidSecurityManager);
        }
    }

    @Test
    public void testAddAddress() {
        Address created = address(100L, Visibility.LIMITED, clientSource(CLIENT_1));
        when(addressManager.createAddress(eq(MY_ORCID), any(Address.class), anyBoolean())).thenReturn(created);

        Response response = serviceDelegator.createAddress(MY_ORCID, Utils.getAddress());

        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(100), Utils.getPutCode(response));
        verify(orcidSecurityManager).checkClientAccessAndScopes(MY_ORCID, ScopePathType.ORCID_BIO_UPDATE);
        // a client may not choose its own source: the delegator clears it
        ArgumentCaptor<Address> submitted = ArgumentCaptor.forClass(Address.class);
        verify(addressManager).createAddress(eq(MY_ORCID), submitted.capture(), eq(true));
        assertNull(submitted.getValue().getSource());
        assertEquals(CLIENT_1_NAME, created.getSource().getSourceName().getContent());
    }

    @Test
    public void testUpdateAddress() {
        Address address = address(1L, Visibility.PUBLIC, clientSource(CLIENT_1));
        address.getCountry().setValue(Iso3166Country.PA);
        Address updated = address(1L, Visibility.PUBLIC, clientSource(CLIENT_1));
        updated.getCountry().setValue(Iso3166Country.PA);
        when(addressManager.updateAddress(eq(MY_ORCID), eq(1L), any(Address.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateAddress(MY_ORCID, 1L, address);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Address returned = (Address) response.getEntity();
        assertEquals(Iso3166Country.PA, returned.getCountry().getValue());
        assertEquals("/4444-4444-4444-4442/address/1", returned.getPath());
        verify(orcidSecurityManager).checkClientAccessAndScopes(MY_ORCID, ScopePathType.ORCID_BIO_UPDATE);
        ArgumentCaptor<Address> submitted = ArgumentCaptor.forClass(Address.class);
        verify(addressManager).updateAddress(eq(MY_ORCID), eq(1L), submitted.capture(), eq(true));
        assertNull(submitted.getValue().getSource());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateAddressYouAreNotTheSourceOf() {
        // AddressManagerImpl calls orcidSecurityManager.checkSource on the stored
        // entity, so the rule itself is proved in the manager's own tests. What
        // is provable at this boundary is that the delegator lets it out.
        Address address = address(2L, Visibility.PUBLIC, userSource(OTHER_ORCID));
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "address"))).when(addressManager).updateAddress(eq(OTHER_ORCID), eq(2L),
                any(Address.class), anyBoolean());

        serviceDelegator.updateAddress(OTHER_ORCID, 2L, address);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateAddressChangingVisibilityTest() {
        // Also thrown inside AddressManagerImpl, when the incoming visibility
        // differs from the stored one.
        Address address = address(1L, Visibility.PRIVATE, clientSource(CLIENT_1));
        doThrow(new VisibilityMismatchException()).when(addressManager).updateAddress(eq(MY_ORCID), eq(1L), any(Address.class), anyBoolean());

        serviceDelegator.updateAddress(MY_ORCID, 1L, address);
        fail();
    }

    @Test
    public void testUpdateAddressLeavingVisibilityNullTest() {
        Address address = address(1L, null, clientSource(CLIENT_1));
        Address updated = address(1L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(addressManager.updateAddress(eq(MY_ORCID), eq(1L), any(Address.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateAddress(MY_ORCID, 1L, address);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(Visibility.PUBLIC, ((Address) response.getEntity()).getVisibility());
        // The delegator passes the null visibility through untouched; keeping the
        // stored visibility is AddressManagerImpl's job and is asserted there.
        ArgumentCaptor<Address> submitted = ArgumentCaptor.forClass(Address.class);
        verify(addressManager).updateAddress(eq(MY_ORCID), eq(1L), submitted.capture(), eq(true));
        assertNull(submitted.getValue().getVisibility());
    }

    @Test
    public void testDeleteAddress() {
        Response response = serviceDelegator.deleteAddress("4444-4444-4444-4499", 6L);

        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes("4444-4444-4444-4499", ScopePathType.ORCID_BIO_UPDATE);
        verify(addressManager).deleteAddress("4444-4444-4444-4499", 6L);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteAddressYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "address"))).when(addressManager).deleteAddress(OTHER_ORCID, 5L);

        serviceDelegator.deleteAddress(OTHER_ORCID, 5L);
        fail();
    }

    // ------------------------------------------------------------- helpers

    private void assertViewAddressDecorated(long putCode, Visibility visibility, Source source) {
        Address address = address(putCode, visibility, source);
        when(addressManagerReadOnly.getAddress(OTHER_ORCID, putCode)).thenReturn(address);

        Response response = serviceDelegator.viewAddress(OTHER_ORCID, putCode);

        assertNotNull(response);
        Address returned = (Address) response.getEntity();
        assertNotNull(returned);
        assertEquals("/4444-4444-4444-4447/address/" + putCode, returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());
        assertEquals(visibility, returned.getVisibility());
        // Whether an element of this visibility may be shown to this token is
        // decided in OrcidSecurityManager_generalTest; here the guard must simply
        // be asked, with the element and the read scope for a person element.
        verify(orcidSecurityManager).checkAndFilter(OTHER_ORCID, address, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<Address>> addressListCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    private Address address(Long putCode, Visibility visibility, Source source) {
        Address address = new Address();
        address.setPutCode(putCode);
        address.setVisibility(visibility);
        address.setCountry(new Country(Iso3166Country.CR));
        address.setSource(source);
        address.setCreatedDate(createdDate());
        address.setLastModifiedDate(lastModified());
        return address;
    }

    private Addresses addresses(Address... elements) {
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<>(Arrays.asList(elements)));
        addresses.setLastModifiedDate(lastModified());
        return addresses;
    }
}
