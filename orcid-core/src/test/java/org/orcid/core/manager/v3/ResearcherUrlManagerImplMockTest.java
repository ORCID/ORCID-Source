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
import org.orcid.core.manager.v3.impl.ResearcherUrlManagerImpl;
import org.orcid.core.utils.Actors;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;

/**
 * The v3 researcher URL delete refuses a client that is not the source of the
 * researcher URL.
 *
 * <p>
 * {@code ResearcherUrlManagerImpl.deleteResearcherUrl} reads the URL, then calls
 * {@code orcidSecurityManager.checkSourceAndThrow(toDelete)} -- guarded by the
 * {@code checkSource} flag, which the API passes as {@code true} and the
 * Registry's own UI as {@code false} -- <em>before</em>
 * {@code researcherUrlDao.deleteResearcherUrl}.
 *
 * <p>
 * The rule used to be proved only by the real-chain
 * {@code MemberV3ApiServiceDelegator_ResearcherUrlsTest}, which now stubs the
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
public class ResearcherUrlManagerImplMockTest {

    private static final Long RESEARCHER_URL_ID = 40L;

    @Mock
    private ResearcherUrlDao researcherUrlDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @InjectMocks
    private ResearcherUrlManagerImpl researcherUrlManager = new ResearcherUrlManagerImpl();

    /**
     * Catches: deleting
     * {@code orcidSecurityManager.checkSourceAndThrow(toDelete)} at
     * {@code manager/v3/impl/ResearcherUrlManagerImpl.java:55}, and moving it
     * below {@code researcherUrlDao.deleteResearcherUrl} at {@code :59}.
     */
    @Test
    public void deleteResearcherUrlRefusesOneSourcedByAnotherClient() {
        ResearcherUrlEntity researcherUrl = sourcedByAnotherClient();
        when(researcherUrlDao.getResearcherUrl(Actors.USER_A, RESEARCHER_URL_ID)).thenReturn(researcherUrl);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSourceAndThrow(researcherUrl);

        try {
            researcherUrlManager.deleteResearcherUrl(Actors.USER_A, RESEARCHER_URL_ID, true);
            fail("a client must not be able to delete a researcher url another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(researcherUrlDao, never()).deleteResearcherUrl(anyString(), anyLong());
    }

    /**
     * Catches: replacing the {@code if (checkSource)} condition at
     * {@code manager/v3/impl/ResearcherUrlManagerImpl.java:54} with a literal
     * {@code true}, which would make the record holder's own UI delete fail.
     */
    @Test
    public void deleteResearcherUrlWithoutTheSourceCheckSkipsTheGuard() {
        ResearcherUrlEntity researcherUrl = sourcedByAnotherClient();
        when(researcherUrlDao.getResearcherUrl(Actors.USER_A, RESEARCHER_URL_ID)).thenReturn(researcherUrl);

        assertTrue(researcherUrlManager.deleteResearcherUrl(Actors.USER_A, RESEARCHER_URL_ID, false));

        verify(orcidSecurityManager, never()).checkSourceAndThrow(any(SourceAwareEntity.class));
        verify(researcherUrlDao).deleteResearcherUrl(Actors.USER_A, RESEARCHER_URL_ID);
    }

    private static ResearcherUrlEntity sourcedByAnotherClient() {
        ResearcherUrlEntity researcherUrl = new ResearcherUrlEntity();
        researcherUrl.setId(RESEARCHER_URL_ID);
        researcherUrl.setClientSourceId(Actors.CLIENT_B);
        return researcherUrl;
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }
}
