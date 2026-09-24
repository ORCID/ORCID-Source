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
import org.orcid.core.adapter.v3.JpaJaxbWorkAdapter;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.v3.impl.WorkManagerImpl;
import org.orcid.core.utils.Actors;
import org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.WorkEntity;

/**
 * The v3 work delete refuses a client that is not the source of the work.
 *
 * <p>
 * {@code WorkManagerImpl.checkSourceAndRemoveWork} reads the work, then calls
 * {@code orcidSecurityManager.checkSourceAndThrow(workEntity)} <em>before</em>
 * {@code workDao.removeWork}. The guard is what stops member CLIENT_A from
 * deleting a work member CLIENT_B created on the same record.
 *
 * <p>
 * Until this class existed the rule was proved only by the member API delegator
 * test {@code MemberV3ApiServiceDelegator_WorksTest}, which now runs against a
 * mocked manager and therefore cannot see the guard at all: deleting
 * {@code orcidSecurityManager.checkSourceAndThrow(workEntity)} from
 * {@code manager/v3/impl/WorkManagerImpl.java:442} left both test stages green.
 *
 * <p>
 * The catch half proves the exception reaches the caller.
 * The {@code never()} half is what proves the guard runs <em>before</em> the
 * delete rather than after it, and is the point of the test.
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
 * @see OrcidSecurityManager_SourceTest the other half of TESTING.md R2 -- that
 *      the guard itself throws for this input
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkManagerImplMockTest {

    private static final Long WORK_ID = 11L;

    @Mock
    private WorkDao workDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private JpaJaxbWorkAdapter jpaJaxbWorkAdapter;

    @InjectMocks
    private WorkManagerImpl workManager = new WorkManagerImpl(100, 100);

    /**
     * Catches: deleting {@code orcidSecurityManager.checkSourceAndThrow(workEntity)}
     * at {@code manager/v3/impl/WorkManagerImpl.java:442}, and moving it below
     * {@code workDao.removeWork} at {@code :444}.
     */
    @Test
    public void checkSourceAndRemoveWorkRefusesAWorkSourcedByAnotherClient() {
        WorkEntity work = new WorkEntity();
        work.setId(WORK_ID);
        work.setOrcid(Actors.USER_A);
        work.setClientSourceId(Actors.CLIENT_B);
        when(workDao.getWork(Actors.USER_A, WORK_ID)).thenReturn(work);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSourceAndThrow(work);

        try {
            workManager.checkSourceAndRemoveWork(Actors.USER_A, WORK_ID);
            fail("a client must not be able to delete a work another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(workDao, never()).removeWork(anyString(), anyLong());
        verify(workDao, never()).flush();
        verify(notificationManager, never()).sendAmendEmail(anyString(), any(AmendedSection.class), anyCollection());
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }
}
