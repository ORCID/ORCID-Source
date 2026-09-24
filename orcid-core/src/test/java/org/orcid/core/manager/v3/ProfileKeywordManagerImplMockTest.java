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
import org.orcid.core.adapter.v3.JpaJaxbKeywordAdapter;
import org.orcid.core.aop.ProfileLastModifiedAspect;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.v3.impl.ProfileKeywordManagerImpl;
import org.orcid.core.utils.Actors;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.SourceAwareEntity;

/**
 * The v3 keyword write guards: a client that is not the source of a keyword may
 * neither delete it nor update it.
 *
 * <p>
 * {@code ProfileKeywordManagerImpl} is the only one of the nine v3 managers in
 * this family that guards <em>both</em> sides -- {@code deleteKeyword} calls
 * {@code orcidSecurityManager.checkSourceAndThrow(entity)} before
 * {@code profileKeywordDao.deleteProfileKeyword}, and {@code updateKeyword}
 * calls it again before {@code profileKeywordDao.merge}. The delete side used to
 * be proved by the real-chain
 * {@code MemberV3ApiServiceDelegator_KeywordsTest.testDeleteKeywordYouAreNotTheSourceOf};
 * the update side is reached by no test at any layer, in either stage.
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
public class ProfileKeywordManagerImplMockTest {

    private static final Long KEYWORD_ID = 80L;

    @Mock
    private ProfileKeywordDao profileKeywordDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private SourceManager sourceManager;

    @Mock
    private SourceEntityUtils sourceEntityUtils;

    @Mock
    private ProfileLastModifiedAspect profileLastModifiedAspect;

    @Mock
    private JpaJaxbKeywordAdapter adapter;

    @InjectMocks
    private ProfileKeywordManagerImpl profileKeywordManager = new ProfileKeywordManagerImpl();

    /**
     * Catches: deleting {@code orcidSecurityManager.checkSourceAndThrow(entity)}
     * at {@code manager/v3/impl/ProfileKeywordManagerImpl.java:51}, and moving it
     * below {@code profileKeywordDao.deleteProfileKeyword} at {@code :55}.
     */
    @Test
    public void deleteKeywordRefusesOneSourcedByAnotherClient() {
        ProfileKeywordEntity keyword = sourcedByAnotherClient();
        when(profileKeywordDao.getProfileKeyword(Actors.USER_A, KEYWORD_ID)).thenReturn(keyword);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSourceAndThrow(keyword);

        try {
            profileKeywordManager.deleteKeyword(Actors.USER_A, KEYWORD_ID, true);
            fail("a client must not be able to delete a keyword another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(profileKeywordDao, never()).deleteProfileKeyword(any(ProfileKeywordEntity.class));
    }

    /**
     * Catches: replacing the {@code if (checkSource)} condition at
     * {@code manager/v3/impl/ProfileKeywordManagerImpl.java:50} with a literal
     * {@code true}, which would make the record holder's own UI delete fail.
     */
    @Test
    public void deleteKeywordWithoutTheSourceCheckSkipsTheGuard() {
        ProfileKeywordEntity keyword = sourcedByAnotherClient();
        when(profileKeywordDao.getProfileKeyword(Actors.USER_A, KEYWORD_ID)).thenReturn(keyword);

        assertTrue(profileKeywordManager.deleteKeyword(Actors.USER_A, KEYWORD_ID, false));

        verify(orcidSecurityManager, never()).checkSourceAndThrow(any(SourceAwareEntity.class));
        verify(profileKeywordDao).deleteProfileKeyword(keyword);
    }

    /**
     * Catches: deleting
     * {@code orcidSecurityManager.checkSourceAndThrow(updatedEntity)} at
     * {@code manager/v3/impl/ProfileKeywordManagerImpl.java:114}, and moving it
     * below {@code profileKeywordDao.merge} at {@code :121}. This guard is the
     * one no test at any layer reached before: the member API delegator only
     * ever proved the delete side.
     */
    @Test
    public void updateKeywordRefusesOneSourcedByAnotherClient() {
        ProfileKeywordEntity stored = sourcedByAnotherClient();
        stored.setKeywordName("stored keyword");
        stored.setVisibility(Visibility.PUBLIC.name());
        when(profileKeywordDao.getProfileKeyword(Actors.USER_A, KEYWORD_ID)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSourceAndThrow(stored);

        Keyword submitted = new Keyword();
        submitted.setPutCode(KEYWORD_ID);
        submitted.setContent("keyword the acting client did not create");

        try {
            profileKeywordManager.updateKeyword(Actors.USER_A, KEYWORD_ID, submitted, true);
            fail("a client must not be able to update a keyword another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(adapter, never()).toProfileKeywordEntity(any(Keyword.class), any(ProfileKeywordEntity.class));
        verify(profileKeywordDao, never()).merge(any(ProfileKeywordEntity.class));
    }

    private static ProfileKeywordEntity sourcedByAnotherClient() {
        ProfileKeywordEntity keyword = new ProfileKeywordEntity();
        keyword.setId(KEYWORD_ID);
        keyword.setClientSourceId(Actors.CLIENT_B);
        return keyword;
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }
}
