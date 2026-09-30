package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.orcid.core.manager.v3.impl.ExternalIdentifierManagerImpl;
import org.orcid.core.utils.Actors;
import org.orcid.persistence.dao.ExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;

/**
 * The v3 external identifier delete refuses a client that is not the source of
 * the identifier.
 *
 * <p>
 * {@code ExternalIdentifierManagerImpl.deleteExternalIdentifier} reads the
 * identifier, returns {@code false} if there is none, then calls
 * {@code orcidSecurityManager.checkSourceAndThrow(extIdEntity)} -- guarded by
 * the {@code checkSource} flag, which the API passes as {@code true} --
 * <em>before</em> {@code externalIdentifierDao.removeExternalIdentifier}.
 *
 * <p>
 * The rule used to be proved only by the real-chain
 * {@code MemberV3ApiServiceDelegator_ExternalIdentifiersTest}, which now stubs
 * the manager and so cannot see the guard.
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
public class ExternalIdentifierManagerImplMockTest {

    private static final Long EXTERNAL_IDENTIFIER_ID = 70L;

    @Mock
    private ExternalIdentifierDao externalIdentifierDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @InjectMocks
    private ExternalIdentifierManagerImpl externalIdentifierManager = new ExternalIdentifierManagerImpl();

    /**
     * Catches: deleting
     * {@code orcidSecurityManager.checkSourceAndThrow(extIdEntity)} at
     * {@code manager/v3/impl/ExternalIdentifierManagerImpl.java:139}, and moving
     * it below {@code externalIdentifierDao.removeExternalIdentifier} at
     * {@code :142}.
     */
    @Test
    public void deleteExternalIdentifierRefusesOneSourcedByAnotherClient() {
        ExternalIdentifierEntity externalIdentifier = sourcedByAnotherClient();
        when(externalIdentifierDao.getExternalIdentifierEntity(Actors.USER_A, EXTERNAL_IDENTIFIER_ID)).thenReturn(externalIdentifier);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSourceAndThrow(externalIdentifier);

        try {
            externalIdentifierManager.deleteExternalIdentifier(Actors.USER_A, EXTERNAL_IDENTIFIER_ID, true);
            fail("a client must not be able to delete an external identifier another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(externalIdentifierDao, never()).removeExternalIdentifier(anyString(), anyLong());
    }

    /**
     * Catches: replacing the {@code if (checkSource)} condition at
     * {@code manager/v3/impl/ExternalIdentifierManagerImpl.java:138} with a
     * literal {@code true}, which would make the record holder's own UI delete
     * fail.
     */
    @Test
    public void deleteExternalIdentifierWithoutTheSourceCheckSkipsTheGuard() {
        ExternalIdentifierEntity externalIdentifier = sourcedByAnotherClient();
        when(externalIdentifierDao.getExternalIdentifierEntity(Actors.USER_A, EXTERNAL_IDENTIFIER_ID)).thenReturn(externalIdentifier);

        assertTrue(externalIdentifierManager.deleteExternalIdentifier(Actors.USER_A, EXTERNAL_IDENTIFIER_ID, false));

        verify(orcidSecurityManager, never()).checkSourceAndThrow(any(SourceAwareEntity.class));
        verify(externalIdentifierDao).removeExternalIdentifier(Actors.USER_A, EXTERNAL_IDENTIFIER_ID);
    }

    private static ExternalIdentifierEntity sourcedByAnotherClient() {
        ExternalIdentifierEntity externalIdentifier = new ExternalIdentifierEntity();
        externalIdentifier.setId(EXTERNAL_IDENTIFIER_ID);
        externalIdentifier.setClientSourceId(Actors.CLIENT_B);
        return externalIdentifier;
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }
}
