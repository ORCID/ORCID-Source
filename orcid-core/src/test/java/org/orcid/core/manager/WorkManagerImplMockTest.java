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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.adapter.JpaJaxbWorkAdapter;
import org.orcid.core.aop.ProfileLastModifiedAspect;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.impl.WorkManagerImpl;
import org.orcid.core.manager.validator.ActivityValidator;
import org.orcid.core.utils.Actors;
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkTitle;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.WorkEntity;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * The source guard on {@link WorkManagerImpl}, on mocks.
 *
 * <p>
 * A mocked sibling of the DBUnit {@code WorkManagerTest}, which is left alone.
 * That class covers the same-source happy path; what is missing without these
 * two tests is the refusal, and in particular that the refusal happens before
 * anything is written.
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkManagerImplMockTest {

    private static final String ORCID = Actors.USER_A;

    private static final Long PUT_CODE = 1L;

    @InjectMocks
    private WorkManagerImpl workManager = new WorkManagerImpl(100, 100);

    @Mock
    private WorkDao workDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private SourceManager sourceManager;

    @Mock
    private JpaJaxbWorkAdapter jpaJaxbWorkAdapter;

    @Mock
    private WorkEntityCacheManager workEntityCacheManager;

    @Mock
    private ActivityValidator activityValidator;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private LocaleManager localeManager;

    @Mock
    private ProfileLastModifiedAspect profileLastModifiedAspect;

    @Before
    public void before() {
        // @InjectMocks does not resolve @Value fields; WorkForm.valueOf needs it.
        ReflectionTestUtils.setField(workManager, "maxContributorsForUI", 50);
    }


    private static WorkEntity storedWorkEntity(Visibility visibility, String clientSourceId) {
        WorkEntity entity = new WorkEntity();
        entity.setId(PUT_CODE);
        entity.setOrcid(ORCID);
        entity.setVisibility(visibility.name());
        entity.setClientSourceId(clientSourceId);
        return entity;
    }

    private static Work work(String title) {
        Work work = new Work();
        work.setPutCode(PUT_CODE);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title(title));
        work.setWorkTitle(workTitle);
        work.setWorkType(WorkType.BOOK);
        work.setVisibility(Visibility.PUBLIC);
        return work;
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(workEntity)}
     * ({@code WorkManagerImpl.java:411}) below {@code workDao.merge(workEntity)},
     * or deleting it.
     *
     * <p>
     * The incoming title differs from the stored one on purpose: an update that
     * changes nothing returns early at {@code :384} and never reaches the guard.
     */
    @Test
    public void updateRefusesAWorkSourcedByAnotherClient() {
        WorkEntity stored = storedWorkEntity(Visibility.PUBLIC, Actors.CLIENT_B);
        when(workDao.getWork(ORCID, PUT_CODE)).thenReturn(stored);
        when(jpaJaxbWorkAdapter.toWork(stored)).thenReturn(work("The stored title"));
        when(localeManager.resolveMessage("apiError.9010.developerMessage")).thenReturn("Wrong source for ${activity}");
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            workManager.updateWork(ORCID, work("A different title"), true);
            fail("a client must not update a work another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(workDao, never()).merge(any(WorkEntity.class));
    }

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(workEntity)}
     * ({@code WorkManagerImpl.java:430}) below {@code workDao.removeWork(orcid, workId)},
     * or deleting it. The removal sits inside a {@code try} that swallows every
     * exception and returns {@code false}, so without the {@code never()} a guard
     * moved below it would leave the row deleted and the call merely reporting
     * failure.
     */
    @Test
    public void deleteRefusesAWorkSourcedByAnotherClient() {
        WorkEntity stored = storedWorkEntity(Visibility.PUBLIC, Actors.CLIENT_B);
        when(workDao.getWork(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            workManager.checkSourceAndRemoveWork(ORCID, PUT_CODE);
            fail("a client must not delete a work another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(workDao, never()).removeWork(anyString(), anyLong());
    }
}
