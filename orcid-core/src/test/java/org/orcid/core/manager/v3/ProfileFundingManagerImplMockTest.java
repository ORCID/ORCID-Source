package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
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
import org.orcid.core.manager.v3.impl.ProfileFundingManagerImpl;
import org.orcid.core.utils.Actors;
import org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;

/**
 * The v3 funding delete refuses a client that is not the source of the funding.
 *
 * <p>
 * {@code ProfileFundingManagerImpl.checkSourceAndDelete} reads the funding, then
 * calls {@code orcidSecurityManager.checkSourceAndThrow(pfe)} <em>before</em>
 * {@code profileFundingDao.removeProfileFunding}.
 *
 * <p>
 * The rule used to be proved only by the real-chain member API delegator test
 * {@code MemberV3ApiServiceDelegator_FundingTest}; that test now runs against a
 * mocked manager, so the guard line could be deleted with both stages green.
 * The {@code never()} assertions below are what tie the guard to the delete.
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
public class ProfileFundingManagerImplMockTest {

    private static final Long FUNDING_ID = 10L;

    @Mock
    private ProfileFundingDao profileFundingDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private NotificationManager notificationManager;

    @InjectMocks
    private ProfileFundingManagerImpl profileFundingManager = new ProfileFundingManagerImpl();

    /**
     * Catches: deleting {@code orcidSecurityManager.checkSourceAndThrow(pfe)} at
     * {@code manager/v3/impl/ProfileFundingManagerImpl.java:238}, and moving it
     * below {@code profileFundingDao.removeProfileFunding} at {@code :239}.
     */
    @Test
    public void checkSourceAndDeleteRefusesAFundingSourcedByAnotherClient() {
        ProfileFundingEntity funding = new ProfileFundingEntity();
        funding.setId(FUNDING_ID);
        funding.setClientSourceId(Actors.CLIENT_B);
        when(profileFundingDao.getProfileFunding(Actors.USER_A, FUNDING_ID)).thenReturn(funding);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSourceAndThrow(funding);

        try {
            profileFundingManager.checkSourceAndDelete(Actors.USER_A, FUNDING_ID);
            fail("a client must not be able to delete a funding another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(profileFundingDao, never()).removeProfileFunding(anyString(), anyLong());
        verify(notificationManager, never()).sendAmendEmail(anyString(), any(AmendedSection.class), anyCollection());
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }
}
