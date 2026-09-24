package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.adapter.MockedMapStructAdapters;
import org.orcid.core.adapter.mapstruct.v3.impl.JpaJaxbClientAdapterImpl;
import org.orcid.core.manager.v3.read_only.ClientManagerReadOnly;
import org.orcid.core.manager.v3.read_only.impl.ClientManagerReadOnlyImpl;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.client.Client;
import org.orcid.jaxb.model.v3.release.client.ClientRedirectUri;
import org.orcid.jaxb.model.v3.release.client.ClientSummary;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientAuthorisedGrantTypeEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientGrantedAuthorityEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.ClientResourceIdEntity;
import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.orcid.persistence.jpa.entities.CustomEmailEntity;
import org.orcid.persistence.jpa.entities.EmailType;
import org.orcid.persistence.jpa.entities.keys.*;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * The v3 twin of org.orcid.core.manager.ClientManagerReadOnlyTest, and the same reasoning
 * applies: the implementation is a two line delegation over a DAO and an adapter, so it is
 * built here by hand instead of pulled from a Spring context.
 *
 * <p>
 * The adapter is REAL, over the real Orika facade from {@link MockedMapStructAdapters}.
 * Every assertion compares a fully populated Client / ClientSummary against an expected
 * object, which is an assertion about the Orika mapping configuration and nothing else;
 * mocking JpaJaxbClientAdapter would mean stubbing it with the expected object and comparing
 * that object with itself. Only the DAO is faked.
 *
 * <p>
 * Silent runner, and NOT for the reason the shared getLastModified stub suggests. Mockito's
 * unnecessary-stubbing check groups stubbings by source location and counts a location as used
 * when any one test method used it -- see
 * {@code UnusedStubbingsFinder.getUnusedStubbingsByLocation}, whose javadoc calls out exactly
 * this case -- so a stub declared in {@code @Before} and reached by only some of the tests is
 * never reported. getLastModified is reached by getClientTest and getSummaryTest, so it is fine.
 * What the strict runner WOULD report is the two stubs in the {@link MockedMapStructAdapters}
 * constructor, sourceNameCacheManager.retrieve and clientDetailsEntityCacheManager.retrieve:
 * they are created while the runner's listener is attached, and the client mapping registers no
 * source converters, so neither is ever called. Every test built on that factory runs Silent for
 * this reason.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class ClientManagerReadOnlyTest {

    private final MockedMapStructAdapters adapters = new MockedMapStructAdapters();

    @Mock
    private ClientDetailsDao daoMock;

    private ClientManagerReadOnly clientManagerReadOnly;

    @Before
    public void before() {
        JpaJaxbClientAdapterImpl adapter = adapters.get(JpaJaxbClientAdapterImpl.class);

        ClientManagerReadOnlyImpl impl = new ClientManagerReadOnlyImpl();
        impl.setClientDetailsDao(daoMock);
        ReflectionTestUtils.setField(impl, "jpaJaxbClientAdapter", adapter);
        clientManagerReadOnly = impl;

        when(daoMock.getLastModified(anyString())).thenReturn(new Date());
    }

    @Test
    public void getClientTest() throws IllegalAccessException {
        String seed = RandomStringUtils.randomAlphanumeric(30);
        when(daoMock.findByClientId(anyString(), anyLong())).thenReturn(getClientDetailsEntity(seed));
        Client client = clientManagerReadOnly.get(seed);
        assertEquals(getClient(seed), client);
    }

    @Test
    public void getClientsTest() throws IllegalAccessException {
        String seed1 = RandomStringUtils.randomAlphanumeric(30);
        String seed2 = RandomStringUtils.randomAlphanumeric(30);
        String seed3 = RandomStringUtils.randomAlphanumeric(30);
        List<ClientDetailsEntity> clients = new ArrayList<ClientDetailsEntity>();
        clients.add(getClientDetailsEntity(seed1));
        clients.add(getClientDetailsEntity(seed2));
        clients.add(getClientDetailsEntity(seed3));
        when(daoMock.findByGroupId(anyString())).thenReturn(clients);
        Set<Client> results = clientManagerReadOnly.getClients("anything");
        assertEquals(3, results.size());
        for (Client client : results) {
            if (client.getId().equals(seed1)) {
                assertEquals(getClient(seed1), client);
            } else if (client.getId().equals(seed2)) {
                assertEquals(getClient(seed2), client);
            } else if (client.getId().equals(seed3)) {
                assertEquals(getClient(seed3), client);
            } else {
                fail("Unknown id " + client.getId());
            }
        }
    }

    @Test
    public void getSummaryTest() throws IllegalAccessException {
        String seed = RandomStringUtils.randomAlphanumeric(30);
        when(daoMock.findByClientId(anyString(), anyLong())).thenReturn(getClientDetailsEntity(seed));
        ClientSummary summary = clientManagerReadOnly.getSummary(seed);
        assertEquals(getClientSummary(seed), summary);
    }

    private ClientSummary getClientSummary(String randomString) {
        ClientSummary summary = new ClientSummary();
        summary.setDescription("description " + randomString);
        summary.setName("client-name " + randomString);
        return summary;
    }

    private Client getClient(String randomString) {
        Client client = new Client();
        client.setAllowAutoDeprecate(true);        
        client.setPersistentTokensEnabled(true);
        client.setClientType(ClientType.CREATOR);
        client.setDescription("description " + randomString);        
        client.setGroupProfileId("group-profile-id " + randomString);
        client.setId(randomString);
        client.setName("client-name " + randomString);        
        client.setWebsite("client-website " + randomString);
        client.setAuthenticationProviderId("authentication-provider-id " + randomString);

        Set<ClientRedirectUri> clientRedirectUris = new HashSet<ClientRedirectUri>();
        ClientRedirectUri rUri1 = new ClientRedirectUri();
        Set<ScopePathType> scopes1 = new HashSet<ScopePathType>();
        scopes1.add(ScopePathType.ACTIVITIES_READ_LIMITED);
        rUri1.setPredefinedClientScopes(scopes1);
        rUri1.setRedirectUri("redirect-uri-1 " + randomString);
        rUri1.setRedirectUriType("type-1 " + randomString);
        rUri1.setUriActType("uri-act-type-1 " + randomString);
        rUri1.setUriGeoArea("uri-geo-area-1 " + randomString);
        ClientRedirectUri rUri2 = new ClientRedirectUri();
        Set<ScopePathType> scopes2 = new HashSet<ScopePathType>();
        scopes2.add(ScopePathType.ACTIVITIES_UPDATE);
        rUri2.setPredefinedClientScopes(scopes2);
        rUri2.setRedirectUri("redirect-uri-2 " + randomString);
        rUri2.setRedirectUriType("type-2 " + randomString);
        rUri2.setUriActType("uri-act-type-2 " + randomString);
        rUri2.setUriGeoArea("uri-geo-area-2 " + randomString);
        ClientRedirectUri rUri3 = new ClientRedirectUri();
        Set<ScopePathType> scopes3 = new HashSet<ScopePathType>();
        scopes3.add(ScopePathType.AFFILIATIONS_CREATE);
        rUri3.setPredefinedClientScopes(scopes3);
        rUri3.setRedirectUri("redirect-uri-3 " + randomString);
        rUri3.setRedirectUriType("type-3 " + randomString);
        rUri3.setUriActType("uri-act-type-3 " + randomString);
        rUri3.setUriGeoArea("uri-geo-area-3 " + randomString);
        clientRedirectUris.add(rUri1);
        clientRedirectUris.add(rUri2);
        clientRedirectUris.add(rUri3);
        client.setClientRedirectUris(clientRedirectUris);
        return client;
    }

    private ClientDetailsEntity getClientDetailsEntity(String randomString) throws IllegalAccessException {
        Date now = new Date();
        ClientDetailsEntity entity = new ClientDetailsEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(entity, now); 
        entity.setAllowAutoDeprecate(true);
        entity.setAuthenticationProviderId("authentication-provider-id " + randomString);
        entity.setClientDescription("description " + randomString);
        entity.setClientName("client-name " + randomString);
        entity.setClientType(ClientType.CREATOR.name());
        entity.setClientWebsite("client-website " + randomString);
        entity.setEmailAccessReason("email-access-reason " + randomString);
        entity.setGroupProfileId("group-profile-id " + randomString);
        entity.setId(randomString);
        entity.setPersistentTokensEnabled(true);
        entity.setClientSecretForJpa("secret-1 " + randomString);
        entity.setClientSecretForJpa("secret-2 " + randomString);
        entity.setClientSecretForJpa("secret-3 " + randomString);

        HashSet<ClientAuthorisedGrantTypeEntity> clientAuthorisedGrantTypeEntities = new HashSet<ClientAuthorisedGrantTypeEntity>();
        ClientAuthorisedGrantTypeEntity cagt1 = new ClientAuthorisedGrantTypeEntity();
        cagt1.setClientId(randomString);
        cagt1.setGrantType("grant-type-1 " + randomString);
        DateFieldsOnBaseEntityUtils.setDateFields(cagt1, now);
        ClientAuthorisedGrantTypeEntity cagt2 = new ClientAuthorisedGrantTypeEntity();
        cagt2.setClientId(randomString);
        cagt2.setGrantType("grant-type-2 " + randomString);
        DateFieldsOnBaseEntityUtils.setDateFields(cagt2, now);
        ClientAuthorisedGrantTypeEntity cagt3 = new ClientAuthorisedGrantTypeEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cagt3, now);
        cagt3.setClientId(randomString);
        cagt3.setGrantType("grant-type-3 " + randomString);
        clientAuthorisedGrantTypeEntities.add(cagt1);
        clientAuthorisedGrantTypeEntities.add(cagt2);
        clientAuthorisedGrantTypeEntities.add(cagt3);
        entity.setClientAuthorizedGrantTypes(clientAuthorisedGrantTypeEntities);

        List<ClientGrantedAuthorityEntity> clientGrantedAuthorityEntities = new ArrayList<ClientGrantedAuthorityEntity>();
        ClientGrantedAuthorityEntity cga1 = new ClientGrantedAuthorityEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cga1, now);
        cga1.setClientId(randomString);
        cga1.setAuthority("authority-1 " + randomString);

        ClientGrantedAuthorityEntity cga2 = new ClientGrantedAuthorityEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cga2, now);
        cga2.setClientId(randomString);
        cga2.setAuthority("authority-2 " + randomString);
        
        ClientGrantedAuthorityEntity cga3 = new ClientGrantedAuthorityEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cga3, now);
        cga3.setClientId(randomString);
        cga3.setAuthority("authority-3 " + randomString);
        
        clientGrantedAuthorityEntities.add(cga1);
        clientGrantedAuthorityEntities.add(cga2);
        clientGrantedAuthorityEntities.add(cga3);
        entity.setClientGrantedAuthorities(clientGrantedAuthorityEntities);

        SortedSet<ClientRedirectUriEntity> clientRegisteredRedirectUris = new TreeSet<ClientRedirectUriEntity>();
        ClientRedirectUriEntity rUri1 = new ClientRedirectUriEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(rUri1, now);
        rUri1.setClientId(randomString);
        rUri1.setRedirectUri("redirect-uri-1 " + randomString);
        rUri1.setRedirectUriType("type-1 " + randomString);
        rUri1.setPredefinedClientScope(ScopePathType.ACTIVITIES_READ_LIMITED.value());
        rUri1.setUriActType("uri-act-type-1 " + randomString);
        rUri1.setUriGeoArea("uri-geo-area-1 " + randomString);
        ClientRedirectUriEntity rUri2 = new ClientRedirectUriEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(rUri2, now);
        rUri2.setClientId(randomString);
        rUri2.setRedirectUri("redirect-uri-2 " + randomString);
        rUri2.setRedirectUriType("type-2 " + randomString);
        rUri2.setPredefinedClientScope(ScopePathType.ACTIVITIES_UPDATE.value());
        rUri2.setUriActType("uri-act-type-2 " + randomString);
        rUri2.setUriGeoArea("uri-geo-area-2 " + randomString);
        ClientRedirectUriEntity rUri3 = new ClientRedirectUriEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(rUri3, now);
        rUri3.setClientId(randomString);
        rUri3.setRedirectUri("redirect-uri-3 " + randomString);
        rUri3.setRedirectUriType("type-3 " + randomString);
        rUri3.setPredefinedClientScope(ScopePathType.AFFILIATIONS_CREATE.value());
        rUri3.setUriActType("uri-act-type-3 " + randomString);
        rUri3.setUriGeoArea("uri-geo-area-3 " + randomString);
        clientRegisteredRedirectUris.add(rUri1);
        clientRegisteredRedirectUris.add(rUri2);
        clientRegisteredRedirectUris.add(rUri3);
        entity.setClientRegisteredRedirectUris(clientRegisteredRedirectUris);

        Set<ClientResourceIdEntity> clientResourceIds = new HashSet<ClientResourceIdEntity>();
        ClientResourceIdEntity cri1 = new ClientResourceIdEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cri1, now);
        cri1.setClientId("id");
        cri1.setResourceId("resource-id-1");
        ClientResourceIdEntity cri2 = new ClientResourceIdEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cri2, now);
        cri2.setClientId("id");
        cri2.setResourceId("resource-id-2");
        ClientResourceIdEntity cri3 = new ClientResourceIdEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cri3, now);
        cri3.setClientId("id");
        cri3.setResourceId("resource-id-3");
        clientResourceIds.add(cri1);
        clientResourceIds.add(cri2);
        clientResourceIds.add(cri3);
        entity.setClientResourceIds(clientResourceIds);

        Set<ClientScopeEntity> clientScopes = new HashSet<ClientScopeEntity>();
        ClientScopeEntity cs1 = new ClientScopeEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cs1, now);
        cs1.setClientId(randomString);
        cs1.setScopeType("scope-1 " + randomString);

        ClientScopeEntity cs2 = new ClientScopeEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cs2, now);
        cs2.setClientId(randomString);
        cs2.setScopeType("scope-2 " + randomString);

        ClientScopeEntity cs3 = new ClientScopeEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cs3, now);
        cs3.setClientId(randomString);
        cs3.setScopeType("scope-3 " + randomString);
        clientScopes.add(cs1);
        clientScopes.add(cs2);
        clientScopes.add(cs3);
        entity.setClientScopes(clientScopes);

        return entity;
    }
}
