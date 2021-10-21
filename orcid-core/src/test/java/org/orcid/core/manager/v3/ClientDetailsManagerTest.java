package org.orcid.core.manager.v3;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
import org.orcid.persistence.dao.ClientScopeDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.test.util.ReflectionTestUtils;

public class ClientDetailsManagerTest {
    
    @Mock
    private ClientDetailsDao clientDetailsDao;
    
    @Mock
    private ProfileEntityManager profileEntityManager;
    
    @Mock
    private ClientScopeDao clientScopeDao;
    
    @InjectMocks
    private ClientDetailsManagerImpl clientDetailsManager;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(clientDetailsManager, "clientScopeDao", clientScopeDao);
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

        // Old public clients doesn't have the '/read-public' scope 
        List<String> publicClientScopes1 = Arrays.asList("/authenticate","openid");
        List<String> publicClientScopes2 = Arrays.asList("/authenticate","openid", "/read-public");
        
        Set<String> updaterScopes = ClientType.getScopes(ClientType.UPDATER);
        Set<String> premiumUpdaterScopes = ClientType.getScopes(ClientType.PREMIUM_UPDATER);
        
        Mockito.doReturn(Boolean.FALSE).when(clientDetailsDao).convertPublicClientToMember(Mockito.eq("client-0"), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(Boolean.TRUE).when(clientDetailsDao).convertPublicClientToMember(Mockito.eq("client-1"), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(Boolean.TRUE).when(clientDetailsDao).convertPublicClientToMember(Mockito.eq("client-2"), Mockito.anyString(), Mockito.anyString());
        
        Mockito.doReturn(publicClientScopes1).when(clientScopeDao).getActiveScopes("client-1");
        Mockito.doReturn(publicClientScopes2).when(clientScopeDao).getActiveScopes("client-2");
        
        Mockito.when(profileEntityManager.findByOrcid(Mockito.eq("premium"))).thenReturn(premiumGroup);
        Mockito.when(profileEntityManager.findByOrcid(Mockito.eq("basic"))).thenReturn(basicGroup);
        
        clientDetailsManager.convertPublicClientToMember("client-0", "basic");
        Mockito.verify(clientDetailsDao, times(1)).convertPublicClientToMember(Mockito.eq("client-0"), Mockito.anyString(), Mockito.any());
        // Any other mock for client-0 should not be invoked since the conversion returned false according to the mock
        Mockito.verify(clientDetailsDao, never()).updateClientGrantedAuthority(Mockito.eq("client-0"), Mockito.anyString());
        
        // Test convert to basic client
        clientDetailsManager.convertPublicClientToMember("client-1", "basic");
        Mockito.verify(clientDetailsDao, times(1)).convertPublicClientToMember(Mockito.eq("client-1"), Mockito.eq("basic"), Mockito.eq(ClientType.UPDATER.name()));
        Mockito.verify(clientDetailsDao, times(1)).updateClientGrantedAuthority(Mockito.eq("client-1"), Mockito.eq("ROLE_CLIENT"));
        Mockito.verify(clientScopeDao, times(1)).getActiveScopes(Mockito.eq("client-1"));
        for(String scope:updaterScopes) {
            if(!publicClientScopes1.contains(scope)) {
                Mockito.verify(clientScopeDao, times(1)).insertClientScope(Mockito.eq("client-1"), Mockito.eq(scope));
            }
        }        
        
        // Test convert to premium client
        clientDetailsManager.convertPublicClientToMember("client-2", "premium");        
        Mockito.verify(clientDetailsDao, times(1)).convertPublicClientToMember(Mockito.eq("client-2"), Mockito.eq("premium"), Mockito.eq(ClientType.PREMIUM_UPDATER.name()));
        Mockito.verify(clientDetailsDao, times(1)).updateClientGrantedAuthority(Mockito.eq("client-2"), Mockito.eq("ROLE_CLIENT"));
        Mockito.verify(clientScopeDao, times(1)).getActiveScopes(Mockito.eq("client-2"));
        for(String scope:premiumUpdaterScopes) {            
            if(!publicClientScopes2.contains(scope)) {
                Mockito.verify(clientScopeDao, times(1)).insertClientScope(Mockito.eq("client-2"), Mockito.eq(scope));
            }
        }                
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
