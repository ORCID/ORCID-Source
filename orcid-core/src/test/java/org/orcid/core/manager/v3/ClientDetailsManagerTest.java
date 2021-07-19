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
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.common.OrcidType;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.test.util.ReflectionTestUtils;

public class ClientDetailsManagerTest {
    
    @Mock
    private ClientDetailsDao clientDetailsDao;
    
    @Mock
    private ProfileEntityManager profileEntityManager;
    
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
    
    @Test
    public void testConvertPublicClientToMember() {
        ProfileEntity premiumGroup = new ProfileEntity();
        premiumGroup.setOrcidType(OrcidType.GROUP.name());
        premiumGroup.setGroupType(MemberType.PREMIUM.name());
        
        ProfileEntity basicGroup = new ProfileEntity();
        basicGroup.setOrcidType(OrcidType.GROUP.name());
        basicGroup.setGroupType(MemberType.BASIC.name());
        
        Mockito.doNothing().when(clientDetailsDao).convertPublicClientToMember(Mockito.eq("client"), Mockito.anyString(), Mockito.anyString());
        Mockito.when(profileEntityManager.findByOrcid(Mockito.eq("premium"))).thenReturn(premiumGroup);
        Mockito.when(profileEntityManager.findByOrcid(Mockito.eq("basic"))).thenReturn(basicGroup);
        
        clientDetailsManager.convertPublicClientToMember("client", "basic");
        Mockito.verify(clientDetailsDao).convertPublicClientToMember(Mockito.eq("client"), Mockito.eq("basic"), Mockito.eq(ClientType.UPDATER.name()));
        
        clientDetailsManager.convertPublicClientToMember("client", "premium");        
        Mockito.verify(clientDetailsDao).convertPublicClientToMember(Mockito.eq("client"), Mockito.eq("premium"), Mockito.eq(ClientType.PREMIUM_UPDATER.name()));
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
