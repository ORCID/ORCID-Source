package org.orcid.core.adapter.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.client.Client;
import org.orcid.jaxb.model.v3.release.client.ClientRedirectUri;
import org.orcid.jaxb.model.v3.release.client.ClientSummary;
import org.orcid.persistence.jpa.entities.ClientAuthorisedGrantTypeEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientGrantedAuthorityEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriStatus;
import org.orcid.persistence.jpa.entities.ClientResourceIdEntity;
import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.orcid.persistence.jpa.entities.CustomEmailEntity;
import org.orcid.persistence.jpa.entities.EmailType;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class JpaJaxbClientAdapterTest {
    @Resource
    private JpaJaxbClientAdapter adapter;

    @Test
    public void toClientTest() throws IllegalAccessException {
        ClientDetailsEntity entity = getClientDetailsEntity();
        Client client = adapter.toClient(entity);        
        assertEquals(getClient(), client);
    }

    @Test
    public void toClientSummaryTest() throws IllegalAccessException {
        ClientDetailsEntity entity = getClientDetailsEntity();
        ClientSummary summary = adapter.toClientSummary(entity);
        assertEquals(getClientSummary(), summary);
    }

    @Test
    public void toClientListTest() throws IllegalAccessException {
        ClientDetailsEntity entity1 = getClientDetailsEntity();
        List<ClientDetailsEntity> entities = new ArrayList<ClientDetailsEntity>();
        entities.add(entity1);
        Set<Client> clients = adapter.toClientList(entities);
        assertEquals(1, clients.size());
        for (Client client : clients) {            
            assertEquals(getClient(), client);
        }
    }

    @Test
    public void toEntityTest() throws IllegalAccessException {
        Client client = getClient();
        ClientDetailsEntity entity = adapter.toEntity(client);
        ClientDetailsEntity toCompare = getClientDetailsEntity();
        
        // Configuration values should be the default
        assertFalse(entity.isPersistentTokensEnabled());
        assertNull(entity.getAuthenticationProviderId());
        assertNull(entity.getDateCreated());
        assertNull(entity.getLastModified());
        assertEquals(toCompare.getClientDescription(), entity.getClientDescription());
        assertEquals(toCompare.getClientId(), entity.getClientId());
        assertEquals(toCompare.getClientName(), entity.getClientName());
        assertEquals(toCompare.getClientRegisteredRedirectUris(), entity.getClientRegisteredRedirectUris());        
        assertEquals(toCompare.getClientWebsite(), entity.getClientWebsite());
        assertEquals(toCompare.isAllowAutoDeprecate(), entity.isAllowAutoDeprecate());
        
        assertFalse(entity.isPersistentTokensEnabled());
        assertNull(entity.getClientType());
        assertNull(entity.getAuthenticationProviderId());
        assertNull(entity.getEmailAccessReason());
        assertNull(entity.getGroupProfileId());
        assertNull(entity.getClientSecrets());
        assertEquals(Collections.EMPTY_SET, entity.getClientAuthorizedGrantTypes());
        assertEquals(Collections.EMPTY_SET, entity.getClientResourceIds());
        assertEquals(Collections.EMPTY_SET, entity.getClientScopes());
        assertEquals(Collections.EMPTY_SET, entity.getCustomEmails());
        assertEquals(Collections.EMPTY_LIST, entity.getClientGrantedAuthorities());
    }

    @Test
    public void toEntity_withExistingEntityTest() throws IllegalAccessException {
        Client client = getClient();
        ClientDetailsEntity existingEntity = getClientDetailsEntity();        
        existingEntity = adapter.toEntity(client, existingEntity);
        assertEquals(getClientDetailsEntity(), existingEntity);
    }

    private Client getClient() {
        Client client = new Client();
        client.setAllowAutoDeprecate(true);
        client.setPersistentTokensEnabled(true);
        client.setClientType(ClientType.CREATOR);
        client.setDescription("description");
        client.setGroupProfileId("group-profile-id");
        client.setId("id");
        client.setName("client-name");
        client.setWebsite("client-website");
        client.setAuthenticationProviderId("authentication-provider-id");

        Set<ClientRedirectUri> clientRedirectUris = new HashSet<ClientRedirectUri>();
        ClientRedirectUri rUri1 = new ClientRedirectUri();
        Set<ScopePathType> scopes1 = new HashSet<ScopePathType>();
        scopes1.add(ScopePathType.ACTIVITIES_READ_LIMITED);
        rUri1.setPredefinedClientScopes(scopes1);
        rUri1.setRedirectUri("redirect-uri-1");
        rUri1.setRedirectUriType("type-1");
        rUri1.setUriActType("uri-act-type-1");
        rUri1.setUriGeoArea("uri-geo-area-1");
        rUri1.setStatus("OK");
        ClientRedirectUri rUri2 = new ClientRedirectUri();
        Set<ScopePathType> scopes2 = new HashSet<ScopePathType>();
        scopes2.add(ScopePathType.ACTIVITIES_UPDATE);
        rUri2.setPredefinedClientScopes(scopes2);
        rUri2.setRedirectUri("redirect-uri-2");
        rUri2.setRedirectUriType("type-2");
        rUri2.setUriActType("uri-act-type-2");
        rUri2.setUriGeoArea("uri-geo-area-2");
        rUri2.setStatus("OK");
        ClientRedirectUri rUri3 = new ClientRedirectUri();
        Set<ScopePathType> scopes3 = new HashSet<ScopePathType>();
        scopes3.add(ScopePathType.AFFILIATIONS_CREATE);
        rUri3.setPredefinedClientScopes(scopes3);
        rUri3.setRedirectUri("redirect-uri-3");
        rUri3.setRedirectUriType("type-3");
        rUri3.setUriActType("uri-act-type-3");
        rUri3.setUriGeoArea("uri-geo-area-3");
        rUri3.setStatus("RETIRED");
        clientRedirectUris.add(rUri1);
        clientRedirectUris.add(rUri2);
        clientRedirectUris.add(rUri3);
        client.setClientRedirectUris(clientRedirectUris);        
        return client;
    }

    private ClientSummary getClientSummary() {
        ClientSummary summary = new ClientSummary();
        summary.setDescription("description");
        summary.setName("client-name");
        return summary;
    }

    private ClientDetailsEntity getClientDetailsEntity() throws IllegalAccessException {
        Date now = new Date();
        Date date = DateUtils.convertToDate("2015-06-05T10:15:20");
        ClientDetailsEntity entity = new ClientDetailsEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(entity, date); 
        entity.setAllowAutoDeprecate(true);
        entity.setAuthenticationProviderId("authentication-provider-id");
        entity.setClientDescription("description");
        entity.setClientName("client-name");
        entity.setClientType(ClientType.CREATOR.name());
        entity.setClientWebsite("client-website");
        entity.setEmailAccessReason("email-access-reason");
        entity.setGroupProfileId("group-profile-id");
        entity.setId("id");
        entity.setPersistentTokensEnabled(true);
        entity.setClientSecretForJpa("secret-1");
        entity.setClientSecretForJpa("secret-2");
        entity.setClientSecretForJpa("secret-3");

        HashSet<ClientAuthorisedGrantTypeEntity> clientAuthorisedGrantTypeEntities = new HashSet<ClientAuthorisedGrantTypeEntity>();
        ClientAuthorisedGrantTypeEntity cagt1 = new ClientAuthorisedGrantTypeEntity();
        cagt1.setClientDetailsEntity(new ClientDetailsEntity("id"));
        DateFieldsOnBaseEntityUtils.setDateFields(cagt1, now);        
        cagt1.setGrantType("grant-type-1");        
        ClientAuthorisedGrantTypeEntity cagt2 = new ClientAuthorisedGrantTypeEntity();
        cagt2.setClientDetailsEntity(new ClientDetailsEntity("id"));
        DateFieldsOnBaseEntityUtils.setDateFields(cagt2, now);
        cagt2.setGrantType("grant-type-2");
        ClientAuthorisedGrantTypeEntity cagt3 = new ClientAuthorisedGrantTypeEntity();
        cagt3.setClientDetailsEntity(new ClientDetailsEntity("id"));
        DateFieldsOnBaseEntityUtils.setDateFields(cagt3, now);
        cagt3.setGrantType("grant-type-3");
        clientAuthorisedGrantTypeEntities.add(cagt1);
        clientAuthorisedGrantTypeEntities.add(cagt2);
        clientAuthorisedGrantTypeEntities.add(cagt3);
        entity.setClientAuthorizedGrantTypes(clientAuthorisedGrantTypeEntities);

        List<ClientGrantedAuthorityEntity> clientGrantedAuthorityEntities = new ArrayList<ClientGrantedAuthorityEntity>();
        ClientGrantedAuthorityEntity cga1 = new ClientGrantedAuthorityEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cga1, now); 
        cga1.setAuthority("authority-1");
        cga1.setClientDetailsEntity(new ClientDetailsEntity("id"));
        
        ClientGrantedAuthorityEntity cga2 = new ClientGrantedAuthorityEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cga2, now); 
        cga2.setAuthority("authority-2");
        cga2.setClientDetailsEntity(new ClientDetailsEntity("id"));
        
        ClientGrantedAuthorityEntity cga3 = new ClientGrantedAuthorityEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cga3, now); 
        cga3.setAuthority("authority-3");
        cga3.setClientDetailsEntity(new ClientDetailsEntity("id"));
        
        clientGrantedAuthorityEntities.add(cga1);
        clientGrantedAuthorityEntities.add(cga2);
        clientGrantedAuthorityEntities.add(cga3);
        entity.setClientGrantedAuthorities(clientGrantedAuthorityEntities);

        SortedSet<ClientRedirectUriEntity> clientRegisteredRedirectUris = new TreeSet<ClientRedirectUriEntity>();
        ClientRedirectUriEntity rUri1 = new ClientRedirectUriEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(rUri1, now); 
        rUri1.setClientDetailsEntity(new ClientDetailsEntity("id"));
        rUri1.setPredefinedClientScope(ScopePathType.ACTIVITIES_READ_LIMITED.value());
        rUri1.setRedirectUri("redirect-uri-1");
        rUri1.setRedirectUriType("type-1");
        rUri1.setUriActType("uri-act-type-1");
        rUri1.setUriGeoArea("uri-geo-area-1");
        rUri1.setStatus(ClientRedirectUriStatus.OK);
        
        ClientRedirectUriEntity rUri2 = new ClientRedirectUriEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(rUri2, now); 
        rUri2.setClientDetailsEntity(new ClientDetailsEntity("id"));
        rUri2.setPredefinedClientScope(ScopePathType.ACTIVITIES_UPDATE.value());
        rUri2.setRedirectUri("redirect-uri-2");
        rUri2.setRedirectUriType("type-2");
        rUri2.setUriActType("uri-act-type-2");
        rUri2.setUriGeoArea("uri-geo-area-2");
        rUri2.setStatus(ClientRedirectUriStatus.OK);
        
        ClientRedirectUriEntity rUri3 = new ClientRedirectUriEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(rUri3, now); 
        rUri3.setClientDetailsEntity(new ClientDetailsEntity("id"));
        rUri3.setPredefinedClientScope(ScopePathType.AFFILIATIONS_CREATE.value());
        rUri3.setRedirectUri("redirect-uri-3");
        rUri3.setRedirectUriType("type-3");
        rUri3.setUriActType("uri-act-type-3");
        rUri3.setUriGeoArea("uri-geo-area-3");
        rUri3.setStatus(ClientRedirectUriStatus.RETIRED);
        clientRegisteredRedirectUris.add(rUri1);
        clientRegisteredRedirectUris.add(rUri2);
        clientRegisteredRedirectUris.add(rUri3);
        entity.setClientRegisteredRedirectUris(clientRegisteredRedirectUris);

        Set<ClientResourceIdEntity> clientResourceIds = new HashSet<ClientResourceIdEntity>();
        ClientResourceIdEntity cri1 = new ClientResourceIdEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cri1, now); 
        cri1.setClientDetailsEntity(new ClientDetailsEntity("id"));
        cri1.setResourceId("resource-id-1");
        
        ClientResourceIdEntity cri2 = new ClientResourceIdEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cri2, now); 
        cri2.setClientDetailsEntity(new ClientDetailsEntity("id"));
        cri2.setResourceId("resource-id-2");
        
        ClientResourceIdEntity cri3 = new ClientResourceIdEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cri3, now); 
        cri3.setClientDetailsEntity(new ClientDetailsEntity("id"));
        cri3.setResourceId("resource-id-3");
        clientResourceIds.add(cri1);
        clientResourceIds.add(cri2);
        clientResourceIds.add(cri3);
        entity.setClientResourceIds(clientResourceIds);

        Set<ClientScopeEntity> clientScopes = new HashSet<ClientScopeEntity>();
        ClientScopeEntity cs1 = new ClientScopeEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cs1, now); 
        cs1.setClientDetailsEntity(new ClientDetailsEntity("id"));
        cs1.setScopeType("scope-type-1");
        
        ClientScopeEntity cs2 = new ClientScopeEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cs2, now); 
        cs2.setClientDetailsEntity(new ClientDetailsEntity("id"));
        cs2.setScopeType("scope-type-2");
        
        ClientScopeEntity cs3 = new ClientScopeEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(cs3, now); 
        cs3.setClientDetailsEntity(new ClientDetailsEntity("id"));
        cs3.setScopeType("scope-type-3");
        clientScopes.add(cs1);
        clientScopes.add(cs2);
        clientScopes.add(cs3);
        entity.setClientScopes(clientScopes);

        Set<CustomEmailEntity> customEmails = new HashSet<CustomEmailEntity>();
        CustomEmailEntity ce1 = new CustomEmailEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(ce1, now); 
        ce1.setClientDetailsEntity(new ClientDetailsEntity("id"));
        ce1.setContent("content-1");
        ce1.setEmailType(EmailType.ACCOUNT_DEPRECATED);
        ce1.setHtml(true);
        ce1.setSender("sender-1");
        ce1.setSubject("subject-1");
        CustomEmailEntity ce2 = new CustomEmailEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(ce2, now); 
        ce2.setClientDetailsEntity(new ClientDetailsEntity("id"));
        ce2.setContent("content-2");
        ce2.setEmailType(EmailType.ACCOUNT_DEPRECATED);
        ce2.setHtml(true);
        ce2.setSender("sender-2");
        ce2.setSubject("subject-2");
        CustomEmailEntity ce3 = new CustomEmailEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(ce3, now); 
        ce3.setClientDetailsEntity(new ClientDetailsEntity("id"));
        ce3.setContent("content-3");
        ce3.setEmailType(EmailType.ACCOUNT_DEPRECATED);
        ce3.setHtml(true);
        ce3.setSender("sender-3");
        ce3.setSubject("subject-3");
        customEmails.add(ce1);
        customEmails.add(ce2);
        customEmails.add(ce3);
        entity.setCustomEmails(customEmails);
        return entity;
    }
}
