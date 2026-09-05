package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
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
import org.orcid.core.manager.v3.impl.OtherNameManagerImpl;
import org.orcid.core.utils.Actors;
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;

/**
 * The v3 other-name delete refuses a client that is not the source of the other
 * name.
 *
 * <p>
 * {@code OtherNameManagerImpl.deleteOtherName} reads the other name, then calls
 * {@code orcidSecurityManager.checkSourceAndThrow(otherNameEntity)} -- guarded
 * by the {@code checkSource} flag, which the API passes as {@code true} --
 * <em>before</em> {@code otherNameDao.deleteOtherName}.
 *
 * <p>
 * The rule used to be proved only by the real-chain
 * {@code MemberV3ApiServiceDelegator_OtherNamesTest}, which now stubs the
 * manager and so cannot see the guard.
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
public class OtherNameManagerImplMockTest {

    private static final Long OTHER_NAME_ID = 50L;

    @Mock
    private OtherNameDao otherNameDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @InjectMocks
    private OtherNameManagerImpl otherNameManager = new OtherNameManagerImpl();

    /**
     * Catches: deleting
     * {@code orcidSecurityManager.checkSourceAndThrow(otherNameEntity)} at
     * {@code manager/v3/impl/OtherNameManagerImpl.java:52}, and moving it below
     * {@code otherNameDao.deleteOtherName} at {@code :56}.
     */
    @Test
    public void deleteOtherNameRefusesOneSourcedByAnotherClient() {
        OtherNameEntity otherName = sourcedByAnotherClient();
        when(otherNameDao.getOtherName(Actors.USER_A, OTHER_NAME_ID)).thenReturn(otherName);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSourceAndThrow(otherName);

        try {
            otherNameManager.deleteOtherName(Actors.USER_A, OTHER_NAME_ID, true);
            fail("a client must not be able to delete an other name another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(otherNameDao, never()).deleteOtherName(any(OtherNameEntity.class));
    }

    /**
     * Catches: replacing the {@code if (checkSource)} condition at
     * {@code manager/v3/impl/OtherNameManagerImpl.java:51} with a literal
     * {@code true}, which would make the record holder's own UI delete fail.
     */
    @Test
    public void deleteOtherNameWithoutTheSourceCheckSkipsTheGuard() {
        OtherNameEntity otherName = sourcedByAnotherClient();
        when(otherNameDao.getOtherName(Actors.USER_A, OTHER_NAME_ID)).thenReturn(otherName);

        assertTrue(otherNameManager.deleteOtherName(Actors.USER_A, OTHER_NAME_ID, false));

        verify(orcidSecurityManager, never()).checkSourceAndThrow(any(SourceAwareEntity.class));
        verify(otherNameDao).deleteOtherName(otherName);
    }

    private static OtherNameEntity sourcedByAnotherClient() {
        OtherNameEntity otherName = new OtherNameEntity();
        otherName.setId(OTHER_NAME_ID);
        otherName.setClientSourceId(Actors.CLIENT_B);
        return otherName;
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }
}
