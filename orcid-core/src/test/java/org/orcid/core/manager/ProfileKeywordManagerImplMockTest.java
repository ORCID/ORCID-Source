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
import org.orcid.core.adapter.JpaJaxbKeywordAdapter;
import org.orcid.core.aop.ProfileLastModifiedAspect;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.impl.ProfileKeywordManagerImpl;
import org.orcid.core.utils.Actors;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;

/**
 * The two rules {@link ProfileKeywordManagerImpl} enforces on an API write, on
 * mocks.
 *
 * <p>
 * A mocked sibling of the DBUnit {@code ProfileKeywordManagerTest}, which is
 * left alone. {@code PersonValidator} is static and therefore real here.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProfileKeywordManagerImplMockTest {

    private static final String ORCID = Actors.USER_A;

    private static final Long PUT_CODE = 1L;

    @InjectMocks
    private ProfileKeywordManagerImpl profileKeywordManager = new ProfileKeywordManagerImpl();

    @Mock
    private ProfileKeywordDao profileKeywordDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private SourceManager sourceManager;

    @Mock
    private JpaJaxbKeywordAdapter adapter;

    @Mock
    private ProfileLastModifiedAspect profileLastModifiedAspect;


    private static ProfileKeywordEntity storedKeyword(Visibility visibility, String clientSourceId) {
        ProfileKeywordEntity entity = new ProfileKeywordEntity();
        entity.setId(PUT_CODE);
        entity.setOrcid(ORCID);
        entity.setKeywordName("keyword-1");
        entity.setVisibility(visibility.name());
        entity.setClientSourceId(clientSourceId);
        return entity;
    }

    private static Keyword keyword(Visibility visibility) {
        Keyword keyword = new Keyword();
        keyword.setPutCode(PUT_CODE);
        keyword.setContent("keyword-1");
        keyword.setVisibility(visibility);
        return keyword;
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }

    // ------------------------------------------------- P1, the source guard

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(updatedEntity)}
     * ({@code ProfileKeywordManagerImpl.java:116}) below
     * {@code profileKeywordDao.merge(updatedEntity)}, or deleting it.
     */
    @Test
    public void updateRefusesAKeywordSourcedByAnotherClient() {
        ProfileKeywordEntity stored = storedKeyword(Visibility.PUBLIC, Actors.CLIENT_B);
        when(profileKeywordDao.getProfileKeyword(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            profileKeywordManager.updateKeyword(ORCID, PUT_CODE, keyword(Visibility.PUBLIC), true);
            fail("a client must not update a keyword another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(profileKeywordDao, never()).merge(any(ProfileKeywordEntity.class));
    }

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(entity)}
     * ({@code ProfileKeywordManagerImpl.java:44-46}) below
     * {@code profileKeywordDao.deleteProfileKeyword(entity)}, or deleting it.
     */
    @Test
    public void deleteRefusesAKeywordSourcedByAnotherClient() {
        ProfileKeywordEntity stored = storedKeyword(Visibility.PUBLIC, Actors.CLIENT_B);
        when(profileKeywordDao.getProfileKeyword(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            profileKeywordManager.deleteKeyword(ORCID, PUT_CODE, true);
            fail("a client must not delete a keyword another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(profileKeywordDao, never()).deleteProfileKeyword(any(ProfileKeywordEntity.class));
    }

    // ----------------------------------------------- P4, the stored visibility

    /**
     * Mutation caught: deleting the {@code PersonValidator.validateKeyword(...)}
     * call at {@code ProfileKeywordManagerImpl.java:104}.
     */
    @Test
    public void apiUpdateWithADifferentVisibilityIsRefused() {
        when(profileKeywordDao.getProfileKeyword(ORCID, PUT_CODE)).thenReturn(storedKeyword(Visibility.PUBLIC, Actors.CLIENT_A));

        try {
            profileKeywordManager.updateKeyword(ORCID, PUT_CODE, keyword(Visibility.PRIVATE), true);
            fail("an API update must not move a public keyword to private");
        } catch (VisibilityMismatchException expected) {
            // the API renders this as 9035
        }

        verify(profileKeywordDao, never()).merge(any(ProfileKeywordEntity.class));
    }

    /**
     * Mutation caught: passing {@code null} instead of {@code originalVisibility}
     * at {@code ProfileKeywordManagerImpl.java:104} (the value read at
     * {@code :97}). What reaches the adapter is what gets mapped onto the row.
     */
    @Test
    public void apiUpdateWithNoVisibilityKeepsTheStoredOne() {
        ProfileKeywordEntity stored = storedKeyword(Visibility.PUBLIC, Actors.CLIENT_A);
        when(profileKeywordDao.getProfileKeyword(ORCID, PUT_CODE)).thenReturn(stored);

        profileKeywordManager.updateKeyword(ORCID, PUT_CODE, keyword(null), true);

        ArgumentCaptor<Keyword> submitted = ArgumentCaptor.forClass(Keyword.class);
        verify(adapter).toProfileKeywordEntity(submitted.capture(), same(stored));
        assertEquals(Visibility.PUBLIC, submitted.getValue().getVisibility());
    }
}
