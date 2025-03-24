package org.orcid.core.cron;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.persistence.dao.ClientSecretDao;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class CleanOldClientKeysCronJobTest extends DBUnitTest {

    @InjectMocks
    private CleanOldClientKeysCronJob CleanUpJob;

    @Mock
    private ClientSecretDao mockClientSecretDao;

    @Mock
    private ClientDetailsDao mockClientDetailsDao;

    @Captor
    private ArgumentCaptor<String> condition;

    @Captor
    private ArgumentCaptor<List<String>> updateReturn;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetNoResults() {
        Mockito.when(mockClientSecretDao.getNonPrimaryKeys(Mockito.anyInt())).thenReturn(new ArrayList<ClientSecretEntity>());
        CleanUpJob.cleanOldClientKeys();
        Mockito.verify(mockClientSecretDao, Mockito.times(1)).getNonPrimaryKeys(Mockito.anyInt());
        Mockito.verify(mockClientSecretDao, Mockito.times(0)).removeWithCustomCondition(Mockito.anyString());
        Mockito.verify(mockClientDetailsDao, Mockito.times(0)).updateLastModifiedBulk(Mockito.anyList());
    }

    @Test
    public void testGetOneResult() {
        List<ClientSecretEntity> secretList = new ArrayList<ClientSecretEntity>();
        List<String> clientList = new ArrayList<String>();

        // populate list of clients for updateLastModifiedBulk
        String clientId = "clientId";
        clientList.add(clientId);

        // populate list of client secret entities for removeWithCustomCondition
        ClientDetailsEntity client = new ClientDetailsEntity(clientId, "clientName");
        ClientSecretEntity entity = new ClientSecretEntity();
        entity.setClientSecret("clientSecret");
        entity.setClientId(client.getClientId());
        secretList.add(entity);

        Mockito.when(mockClientSecretDao.getNonPrimaryKeys(100)).thenReturn(secretList);
        Mockito.when(mockClientSecretDao.removeWithCustomCondition(Mockito.anyString())).thenReturn(true);

        CleanUpJob.cleanOldClientKeys();
        Mockito.verify(mockClientSecretDao, Mockito.times(1)).getNonPrimaryKeys(100);
        Mockito.verify(mockClientSecretDao, Mockito.times(1)).removeWithCustomCondition(condition.capture());
        Mockito.verify(mockClientDetailsDao, Mockito.times(1)).updateLastModifiedBulk(updateReturn.capture());
        assertEquals(updateReturn.getValue(), clientList);
        assertEquals(condition.getValue(), "(client_details_id = 'clientId' and client_secret = 'clientSecret')");
    }

    @Test
    public void testGetMultipleResults() {
        List<ClientSecretEntity> secretList = new ArrayList<ClientSecretEntity>();
        List<String> clientList = new ArrayList<String>();

        // populate list of clients for updateLastModifiedBulk
        String clientId = "clientId";
        String clientIdTwo = "clientIdTwo";
        clientList.add(clientId);
        clientList.add(clientIdTwo);

        // populate list of client secret entities for removeWithCustomCondition
        ClientDetailsEntity client = new ClientDetailsEntity(clientId, "clientName");
        ClientDetailsEntity clientTwo = new ClientDetailsEntity(clientIdTwo, "clientNameTwo");
        ClientSecretEntity entityOne = new ClientSecretEntity();
        entityOne.setClientSecret("clientSecret");
        entityOne.setClientId(client.getClientId());
        ClientSecretEntity entityTwo = new ClientSecretEntity();
        entityTwo.setClientSecret("clientSecretTwo");
        entityTwo.setClientId(clientTwo.getClientId());
        secretList.add(entityOne);
        secretList.add(entityTwo);

        Mockito.when(mockClientSecretDao.getNonPrimaryKeys(100)).thenReturn(secretList);
        Mockito.when(mockClientSecretDao.removeWithCustomCondition(Mockito.anyString())).thenReturn(true);

        CleanUpJob.cleanOldClientKeys();
        Mockito.verify(mockClientSecretDao, Mockito.times(1)).getNonPrimaryKeys(100);
        Mockito.verify(mockClientSecretDao, Mockito.times(1)).removeWithCustomCondition(condition.capture());
        Mockito.verify(mockClientDetailsDao, Mockito.times(1)).updateLastModifiedBulk(updateReturn.capture());
        assertEquals(updateReturn.getValue(), clientList);
        assertEquals(condition.getValue(),
                "(client_details_id = 'clientId' and client_secret = 'clientSecret') or (client_details_id = 'clientIdTwo' and client_secret = 'clientSecretTwo')");
    }

}
