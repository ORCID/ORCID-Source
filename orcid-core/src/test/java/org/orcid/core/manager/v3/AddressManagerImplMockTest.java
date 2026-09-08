package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.v3.impl.AddressManagerImpl;
import org.orcid.core.utils.Actors;
import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.jpa.entities.AddressEntity;

/**
 * The v3 address delete refuses a client that is not the source of the address.
 *
 * <p>
 * {@code AddressManagerImpl.deleteAddress} reads the address, then calls
 * {@code orcidSecurityManager.checkSourceAndThrow(entity)} <em>before</em>
 * {@code addressDao.remove(entity)}. Unlike the other person elements this one
 * has no {@code checkSource} flag: the guard is unconditional.
 *
 * <p>
 * The rule used to be proved only by the real-chain
 * {@code MemberV3ApiServiceDelegator_AddressesTest}, which now stubs the manager
 * and so cannot see the guard.
 *
 * <p>
 * No actor is placed in the security context. At this layer the security manager
 * is a mock, so nothing under test reads {@code SecurityContextHolder}; the
 * fixture that carries the rule is the entity's source -- {@code Actors.CLIENT_B}
 * created it, and the client the guard refuses is the one acting. Setting an
 * actor here would also fail the class: {@code Actors.memberClient} builds a
 * stubbed token mock, and the strict Mockito runner reports stubbings nothing
 * reads as unnecessary.
 *
 * @see OrcidSecurityManager_SourceTest the other half of TESTING.md R2
 */
@RunWith(MockitoJUnitRunner.class)
public class AddressManagerImplMockTest {

    private static final Long ADDRESS_ID = 60L;

    @Mock
    private AddressDao addressDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @InjectMocks
    private AddressManagerImpl addressManager = new AddressManagerImpl();

    /**
     * Catches: deleting {@code orcidSecurityManager.checkSourceAndThrow(entity)}
     * at {@code manager/v3/impl/AddressManagerImpl.java:120}, and moving it below
     * {@code addressDao.remove(entity)} at {@code :123}.
     */
    @Test
    public void deleteAddressRefusesOneSourcedByAnotherClient() {
        AddressEntity address = new AddressEntity();
        address.setId(ADDRESS_ID);
        address.setClientSourceId(Actors.CLIENT_B);
        when(addressDao.getAddress(Actors.USER_A, ADDRESS_ID)).thenReturn(address);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSourceAndThrow(address);

        try {
            addressManager.deleteAddress(Actors.USER_A, ADDRESS_ID);
            fail("a client must not be able to delete an address another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(addressDao, never()).remove(any(AddressEntity.class));
        verify(addressDao, never()).remove(anyLong());
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }
}
