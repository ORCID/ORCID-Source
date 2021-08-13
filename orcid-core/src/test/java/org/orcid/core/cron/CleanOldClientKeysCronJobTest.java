package org.orcid.core.cron;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.orcid.core.cron.CleanOldClientKeysCronJob;
import org.orcid.persistence.dao.ClientSecretDao;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;

public class CleanOldClientKeysCronJobTest {
    
    @InjectMocks
    private CleanOldClientKeysCronJob CleanUpJob;
    
    @Mock
    private ClientSecretDao clientSecretDao;
    
    @Mock
    private ClientDetailsDao clientDetailsDao;
    
    @Captor
    private ArgumentCaptor<String> condition;
    
    
    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void testGetNoResults() {
        Mockito.when(clientSecretDao.getNonPrimaryKeys()).thenReturn(new ArrayList<ClientSecretEntity>());
        CleanUpJob.cleanOldClientKeys();
        Mockito.verify(clientSecretDao, Mockito.times(1)).getNonPrimaryKeys();
        Mockito.verify(clientSecretDao, Mockito.times(0)).removeWithCustomCondition(condition.capture());
        Mockito.verify(clientDetailsDao, Mockito.times(0)).updateLastModifiedBulk(Mockito.anyList());
    }
    
    @Test
    public void testGetOneResult() {
        List<ClientSecretEntity> list = new ArrayList<ClientSecretEntity>();
        ClientDetailsEntity client = new ClientDetailsEntity("anything", "anything");
        ClientSecretEntity entity = new ClientSecretEntity();
        entity.setClientSecret("anything");
        entity.setClientDetailsEntity(client);
        list.add(entity);
        Mockito.when(clientSecretDao.getNonPrimaryKeys()).thenReturn(list);
        Mockito.when(clientSecretDao.removeWithCustomCondition(Mockito.anyString())).thenReturn(true);
       // Mockito.doNothing().when(clientDetailsDao.updateLastModifiedBulk(Mockito.anyList()));
        CleanUpJob.cleanOldClientKeys();
        Mockito.verify(clientSecretDao, Mockito.times(1)).getNonPrimaryKeys();
        Mockito.verify(clientSecretDao, Mockito.times(1)).removeWithCustomCondition(condition.capture());
        Mockito.verify(clientDetailsDao, Mockito.times(1)).updateLastModifiedBulk(Mockito.anyList());
    }
    
    @Test
    public void testGetMultipleResults() {
        List<ClientSecretEntity> list = new ArrayList<ClientSecretEntity>();
        ClientDetailsEntity client = new ClientDetailsEntity("anything", "anything");
        ClientSecretEntity entityOne = new ClientSecretEntity();
        entityOne.setClientSecret("anything");
        entityOne.setClientDetailsEntity(client);
        ClientSecretEntity entityTwo = new ClientSecretEntity();
        entityTwo.setClientSecret("anything again");
        entityTwo.setClientDetailsEntity(client);
        list.add(entityOne);
        list.add(entityTwo);
        Mockito.when(clientSecretDao.getNonPrimaryKeys()).thenReturn(list);
        Mockito.when(clientSecretDao.removeWithCustomCondition(Mockito.anyString())).thenReturn(true);
       // Mockito.doNothing().when(clientDetailsDao.updateLastModifiedBulk(Mockito.anyList()));
        CleanUpJob.cleanOldClientKeys();
        Mockito.verify(clientSecretDao, Mockito.times(1)).getNonPrimaryKeys();
        Mockito.verify(clientSecretDao, Mockito.times(1)).removeWithCustomCondition(condition.capture());
        Mockito.verify(clientDetailsDao, Mockito.times(1)).updateLastModifiedBulk(Mockito.anyList());
    }
    
}
