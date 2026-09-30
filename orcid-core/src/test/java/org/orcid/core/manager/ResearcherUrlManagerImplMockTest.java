package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
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
import org.orcid.core.adapter.JpaJaxbResearcherUrlAdapter;
import org.orcid.core.aop.ProfileLastModifiedAspect;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.impl.ResearcherUrlManagerImpl;
import org.orcid.core.utils.Actors;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;

/**
 * The source guard on {@link ResearcherUrlManagerImpl}, on mocks.
 *
 * <p>
 * A mocked sibling of the DBUnit {@code ResearcherUrlManagerTest}, which is left
 * alone.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResearcherUrlManagerImplMockTest {

    private static final String ORCID = Actors.USER_A;

    private static final Long PUT_CODE = 1L;

    @InjectMocks
    private ResearcherUrlManagerImpl researcherUrlManager = new ResearcherUrlManagerImpl();

    @Mock
    private ResearcherUrlDao researcherUrlDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private SourceManager sourceManager;

    @Mock
    private JpaJaxbResearcherUrlAdapter jpaJaxbResearcherUrlAdapter;

    @Mock
    private ProfileLastModifiedAspect profileLastModifiedAspect;


    private static ResearcherUrlEntity storedResearcherUrl(Visibility visibility, String clientSourceId) {
        ResearcherUrlEntity entity = new ResearcherUrlEntity();
        entity.setId(PUT_CODE);
        entity.setOrcid(ORCID);
        entity.setUrl("http://orcid.org");
        entity.setUrlName("ORCID Site");
        entity.setVisibility(visibility.name());
        entity.setClientSourceId(clientSourceId);
        return entity;
    }

    private static ResearcherUrl researcherUrl(Visibility visibility) {
        ResearcherUrl researcherUrl = new ResearcherUrl();
        researcherUrl.setPutCode(PUT_CODE);
        researcherUrl.setUrl(new Url("http://orcid.org"));
        researcherUrl.setUrlName("ORCID Site");
        researcherUrl.setVisibility(visibility);
        return researcherUrl;
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(updatedResearcherUrlEntity)}
     * ({@code ResearcherUrlManagerImpl.java:153}) below
     * {@code researcherUrlDao.merge(updatedResearcherUrlEntity)}, or deleting it.
     */
    @Test
    public void updateRefusesAResearcherUrlSourcedByAnotherClient() {
        ResearcherUrlEntity stored = storedResearcherUrl(Visibility.PUBLIC, Actors.CLIENT_B);
        when(researcherUrlDao.getResearcherUrl(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            researcherUrlManager.updateResearcherUrl(ORCID, researcherUrl(Visibility.PUBLIC), true);
            fail("a client must not update a researcher url another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(researcherUrlDao, never()).merge(any(ResearcherUrlEntity.class));
    }

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(toDelete)}
     * ({@code ResearcherUrlManagerImpl.java:48}) below
     * {@code researcherUrlDao.deleteResearcherUrl(orcid, id)}, or deleting it.
     * The delete is inside a {@code try} that swallows every exception, so
     * without the {@code never()} a guard moved below it would still leave the
     * call returning quietly.
     */
    @Test
    public void deleteRefusesAResearcherUrlSourcedByAnotherClient() {
        ResearcherUrlEntity stored = storedResearcherUrl(Visibility.PUBLIC, Actors.CLIENT_B);
        when(researcherUrlDao.getResearcherUrl(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            researcherUrlManager.deleteResearcherUrl(ORCID, PUT_CODE, true);
            fail("a client must not delete a researcher url another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(researcherUrlDao, never()).deleteResearcherUrl(anyString(), anyLong());
    }
}
