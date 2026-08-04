package org.orcid.core.manager.v3;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import jakarta.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.orcid.core.manager.v3.impl.GivenPermissionToManagerImpl;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.jpa.entities.GivenPermissionByEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@RunWith(MockitoJUnitRunner.class)
public class GivenPermissionToManagerTest {

    private static final String GIVER = "0000-0000-0000-0006";
    private static final String RECEIVER = "0000-0000-0000-0003";

    @Mock
    private GivenPermissionToDao givenPermissionToDao;

    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private ProfileEntityManager profileEntityManager;

    @InjectMocks
    private GivenPermissionToManagerImpl givenPermissionToManager;

    @Before
    public void setUp() {
        when(transactionTemplate.execute(any())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                TransactionCallbackWithoutResult callback = invocation.getArgument(0);
                callback.doInTransaction(null);
                return null;
            }
        });
    }

    @Test
    public void testRemove() {
        givenPermissionToManager.remove(GIVER, RECEIVER);

        verify(givenPermissionToDao).remove(GIVER, RECEIVER);
        verify(profileEntityManager).updateLastModifed(GIVER);
        verify(profileEntityManager).updateLastModifed(RECEIVER);
    }

    @Test
    public void testRemove_Nulls() {
        givenPermissionToManager.remove(null, null);

        verify(givenPermissionToDao).remove(null, null);
        verify(profileEntityManager, times(2)).updateLastModifed(null);
    }

    @Test
    public void testCreate_New() {
        when(givenPermissionToDao.findByGiverAndReceiverOrcid(GIVER, RECEIVER)).thenReturn(null);

        givenPermissionToManager.create(GIVER, RECEIVER);

        verify(givenPermissionToDao).merge(any(GivenPermissionToEntity.class));
        verify(notificationManager).sendNotificationToAddedDelegate(GIVER, RECEIVER);
        verify(notificationManager).sendNotificationToUserGrantingPermission(GIVER, RECEIVER);
        verify(profileEntityManager).updateLastModifed(GIVER);
        verify(profileEntityManager).updateLastModifed(RECEIVER);
    }

    @Test
    public void testCreate_Existing() {
        when(givenPermissionToDao.findByGiverAndReceiverOrcid(GIVER, RECEIVER)).thenReturn(new GivenPermissionToEntity());

        givenPermissionToManager.create(GIVER, RECEIVER);

        verify(givenPermissionToDao, never()).merge(any(GivenPermissionToEntity.class));
        verify(notificationManager, never()).sendNotificationToAddedDelegate(any(), any());
    }

    @Test
    public void testCreate_SameGiverAndReceiver() {
        // Technically nothing stops this in the current code except maybe the DAO check if it exists
        when(givenPermissionToDao.findByGiverAndReceiverOrcid(GIVER, GIVER)).thenReturn(null);

        givenPermissionToManager.create(GIVER, GIVER);

        verify(givenPermissionToDao).merge(any(GivenPermissionToEntity.class));
        verify(notificationManager).sendNotificationToAddedDelegate(GIVER, GIVER);
        verify(profileEntityManager, times(2)).updateLastModifed(GIVER);
    }

    @Test
    public void testCreate_Nulls() {
        when(givenPermissionToDao.findByGiverAndReceiverOrcid(null, null)).thenReturn(null);
        givenPermissionToManager.create(null, null);
        verify(givenPermissionToDao).merge(any(GivenPermissionToEntity.class));
    }

    @Test
    public void testRemoveAllForProfile() {
        GivenPermissionToEntity given1 = new GivenPermissionToEntity();
        given1.setGiver("orcid");
        given1.setReceiver("receiver1");

        GivenPermissionByEntity received1 = new GivenPermissionByEntity();
        received1.setGiver("giver1");
        received1.setReceiver("orcid");

        when(givenPermissionToDao.findByGiver("orcid")).thenReturn(Arrays.asList(given1));
        when(givenPermissionToDao.findByReceiver("orcid")).thenReturn(Arrays.asList(received1));

        givenPermissionToManager.removeAllForProfile("orcid");

        verify(givenPermissionToDao, times(1)).remove("orcid", "receiver1");
        verify(givenPermissionToDao, times(1)).remove("giver1", "orcid");
        
        // verify updateLastModifed called for all involved
        verify(profileEntityManager, times(2)).updateLastModifed("orcid");
        verify(profileEntityManager, times(1)).updateLastModifed("receiver1");
        verify(profileEntityManager, times(1)).updateLastModifed("giver1");
    }

    @Test
    public void testRemoveAllForProfile_Empty() {
        when(givenPermissionToDao.findByGiver("orcid")).thenReturn(Arrays.asList());
        when(givenPermissionToDao.findByReceiver("orcid")).thenReturn(Arrays.asList());

        givenPermissionToManager.removeAllForProfile("orcid");

        verify(givenPermissionToDao, never()).remove(any(), any());
        verify(profileEntityManager, never()).updateLastModifed(any());
    }

    @Test
    public void testRemoveAllForProfile_Null() {
        when(givenPermissionToDao.findByGiver(null)).thenReturn(Arrays.asList());
        when(givenPermissionToDao.findByReceiver(null)).thenReturn(Arrays.asList());

        givenPermissionToManager.removeAllForProfile(null);

        verify(givenPermissionToDao, never()).remove(any(), any());
    }
}
