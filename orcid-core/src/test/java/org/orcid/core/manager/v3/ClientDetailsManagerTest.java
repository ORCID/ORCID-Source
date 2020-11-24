package org.orcid.core.manager.v3;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.exception.ClientAlreadyActiveException;
import org.orcid.core.exception.ClientAlreadyDeactivatedException;
import org.orcid.core.manager.v3.impl.ClientDetailsManagerImpl;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.springframework.test.util.ReflectionTestUtils;

public class ClientDetailsManagerTest {
    
    @Mock
    private ClientDetailsDao clientDetailsDao;
    
    @InjectMocks
    private ClientDetailsManagerImpl clientDetailsManager;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(clientDetailsManager, "clientDetailsDao", clientDetailsDao);
        ReflectionTestUtils.setField(clientDetailsManager, "clientDetailsDaoReadOnly", clientDetailsDao);
    }

    @Test
    public void testDeactivateClientDetails() throws ClientAlreadyDeactivatedException {
        Mockito.when(clientDetailsDao.find(Mockito.eq("client"))).thenReturn(getActiveClientDetails("client"));
        Mockito.when(clientDetailsDao.merge(Mockito.any(ClientDetailsEntity.class))).thenReturn(null);
        
        clientDetailsManager.deactivateClientDetails("client", "orcid");
        
        Mockito.verify(clientDetailsDao, Mockito.times(1)).find(Mockito.eq("client"));
        Mockito.verify(clientDetailsDao, Mockito.times(1)).deactivateClient(Mockito.eq("client"), Mockito.eq("orcid"));
    }
    
    @Test
    public void testActivateClientDetails() throws ClientAlreadyActiveException {
        Mockito.when(clientDetailsDao.find(Mockito.eq("client"))).thenReturn(getDeactivedClientDetails("client"));
        Mockito.when(clientDetailsDao.merge(Mockito.any(ClientDetailsEntity.class))).thenReturn(null);
        
        clientDetailsManager.activateClientDetails("client");
        
        Mockito.verify(clientDetailsDao, Mockito.times(1)).find(Mockito.eq("client"));
        Mockito.verify(clientDetailsDao, Mockito.times(1)).activateClient(Mockito.eq("client"));
    }
    
    @Test(expected = ClientAlreadyDeactivatedException.class)
    public void testDeactivateClientDetailsAlreadyDeactivated() throws ClientAlreadyDeactivatedException {
        Mockito.when(clientDetailsDao.find(Mockito.eq("client"))).thenReturn(getDeactivedClientDetails("client"));
        clientDetailsManager.deactivateClientDetails("client", "orcid");
    }
    
    @Test(expected = ClientAlreadyActiveException.class)
    public void testActivateClientDetailsAlreadyActive() throws ClientAlreadyActiveException {
        Mockito.when(clientDetailsDao.find(Mockito.eq("client"))).thenReturn(getActiveClientDetails("client"));
        clientDetailsManager.activateClientDetails("client");
    }
    
    private ClientDetailsEntity getDeactivedClientDetails(String id) {
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        clientDetails.setId(id);
        clientDetails.setDeactivatedDate(new Date());
        clientDetails.setDeactivatedBy("an-orcid");
        return clientDetails;
    }

    private ClientDetailsEntity getActiveClientDetails(String id) {
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        clientDetails.setId(id);
        return clientDetails;
    }

}
