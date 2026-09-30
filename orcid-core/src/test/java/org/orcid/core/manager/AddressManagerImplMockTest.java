package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.adapter.JpaJaxbAddressAdapter;
import org.orcid.core.aop.ProfileLastModifiedAspect;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.impl.AddressManagerImpl;
import org.orcid.core.utils.Actors;
import org.orcid.jaxb.model.common_v2.Country;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.jpa.entities.AddressEntity;

/**
 * The two rules {@link AddressManagerImpl} enforces on an API write, on mocks.
 *
 * <p>
 * A mocked sibling of the DBUnit {@code AddressManagerTest}, which is left
 * alone: it proves the create-time privacy rules against real fixtures and is a
 * database test. What is proved here is what a stubbed delegator can no longer
 * prove — that the source guard runs <em>before</em> the write, and that an API
 * update cannot move an address to another visibility.
 *
 * <p>
 * {@code PersonValidator} is static and therefore real in every test below, so
 * the visibility repair and the mismatch throw are the production ones.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddressManagerImplMockTest {

    private static final String ORCID = Actors.USER_A;

    private static final Long PUT_CODE = 1L;

    @InjectMocks
    private AddressManagerImpl addressManager = new AddressManagerImpl();

    @Mock
    private AddressDao addressDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private SourceManager sourceManager;

    @Mock
    private JpaJaxbAddressAdapter adapter;

    @Mock
    private ProfileLastModifiedAspect profileLastModifiedAspect;


    private static AddressEntity storedAddress(Visibility visibility, String clientSourceId) {
        AddressEntity entity = new AddressEntity();
        entity.setId(PUT_CODE);
        entity.setOrcid(ORCID);
        entity.setVisibility(visibility.name());
        entity.setClientSourceId(clientSourceId);
        return entity;
    }

    private static Address address(Visibility visibility) {
        Address address = new Address();
        address.setPutCode(PUT_CODE);
        address.setCountry(new Country(Iso3166Country.CR));
        address.setVisibility(visibility);
        return address;
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }

    // ------------------------------------------------- P1, the source guard

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(updatedEntity)}
     * ({@code AddressManagerImpl.java:56}) below {@code addressDao.merge(updatedEntity)}
     * ({@code :81}), or deleting it. The {@code never()} is the half that pins the
     * ordering — asserting only the exception leaves a guard that fires after the
     * row has already been written.
     */
    @Test
    public void updateRefusesAnAddressSourcedByAnotherClient() {
        AddressEntity stored = storedAddress(Visibility.PUBLIC, Actors.CLIENT_B);
        when(addressDao.getAddress(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            addressManager.updateAddress(ORCID, PUT_CODE, address(Visibility.PUBLIC), true);
            fail("a client must not update an address another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(addressDao, never()).merge(any(AddressEntity.class));
    }

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(entity)}
     * ({@code AddressManagerImpl.java:123}) below {@code addressDao.remove(entity)}
     * ({@code :126}), or deleting it.
     */
    @Test
    public void deleteRefusesAnAddressSourcedByAnotherClient() {
        AddressEntity stored = storedAddress(Visibility.PUBLIC, Actors.CLIENT_B);
        when(addressDao.getAddress(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            addressManager.deleteAddress(ORCID, PUT_CODE);
            fail("a client must not delete an address another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(addressDao, never()).remove(any(AddressEntity.class));
    }

    // ----------------------------------------------- P4, the stored visibility

    /**
     * Mutation caught: deleting the {@code PersonValidator.validateAddress(...)}
     * call at {@code AddressManagerImpl.java:60}, which is the only thing that
     * stops an API update from silently changing an address's visibility.
     */
    @Test
    public void apiUpdateWithADifferentVisibilityIsRefused() {
        when(addressDao.getAddress(ORCID, PUT_CODE)).thenReturn(storedAddress(Visibility.PUBLIC, Actors.CLIENT_A));

        try {
            addressManager.updateAddress(ORCID, PUT_CODE, address(Visibility.PRIVATE), true);
            fail("an API update must not move a public address to private");
        } catch (VisibilityMismatchException expected) {
            // the API renders this as 9035
        }

        verify(addressDao, never()).merge(any(AddressEntity.class));
    }

    /**
     * Mutation caught: passing {@code null} instead of {@code originalVisibility}
     * at {@code AddressManagerImpl.java:60} (the value read at {@code :47}). The
     * address that reaches the adapter must carry the stored visibility, because
     * the update mapper maps whatever it is given onto the entity and a null
     * really nulls the column.
     */
    @Test
    public void apiUpdateWithNoVisibilityKeepsTheStoredOne() {
        AddressEntity stored = storedAddress(Visibility.PUBLIC, Actors.CLIENT_A);
        when(addressDao.getAddress(ORCID, PUT_CODE)).thenReturn(stored);

        addressManager.updateAddress(ORCID, PUT_CODE, address(null), true);

        ArgumentCaptor<Address> submitted = ArgumentCaptor.forClass(Address.class);
        verify(adapter).toAddressEntity(submitted.capture(), same(stored));
        assertEquals(Visibility.PUBLIC, submitted.getValue().getVisibility());
    }
}
