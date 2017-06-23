/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.client_v2.Client;
import org.orcid.jaxb.model.client_v2.ClientRedirectUri;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.TargetProxyHelper;

public class ClientManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml");
    
    private static final String MEMBER_ID="5555-5555-5555-5558";
    
    @Resource
    private ClientManager clientManager;

    @Resource
    private ClientDetailsDao clientDetailsDao;
    
    @Mock
    private SourceManager sourceManager;
    
    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Mock
    private EncryptionManager encryptionManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }    
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }
    
    @Before
    public void before() {        
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(clientManager, "sourceManager", sourceManager);
        when(sourceManager.retrieveSourceOrcid()).thenReturn(MEMBER_ID);
        
        TargetProxyHelper.injectIntoProxy(clientManager, "profileEntityCacheManager", profileEntityCacheManager);
        ProfileEntity member = new ProfileEntity(MEMBER_ID);
        member.setGroupType(MemberType.PREMIUM);
        when(profileEntityCacheManager.retrieve(MEMBER_ID)).thenReturn(member);        
        
        TargetProxyHelper.injectIntoProxy(clientManager, "encryptionManager", encryptionManager);
        when(encryptionManager.encryptForInternalUse(anyString())).thenReturn("encrypted-value");
    }
    
    @Test
    public void createClientTest() {
        String seed = RandomStringUtils.randomAlphanumeric(30);
        Client client = getClient(seed, MEMBER_ID);
        assertFalse(client.getId().startsWith("APP-"));
        client = clientManager.create(client);
        assertTrue(client.getId().startsWith("APP-"));
        ClientDetailsEntity newEntity = clientDetailsDao.find(client.getId());
        assertNotNull(newEntity);
        
    }

    @Test
    public void edit() {
    }

    @Test
    public void resetClientSecret() {
    }
    
    private Client getClient(String randomString, String memberId) {
        Client client = new Client();
        client.setAllowAutoDeprecate(true);
        client.setAuthenticationProviderId("authentication-provider-id " + randomString);
        client.setClientType(ClientType.CREATOR);
        client.setDescription("description " + randomString);
        client.setEmailAccessReason("email-access-reason " + randomString);
        client.setGroupProfileId(memberId);
        client.setId(randomString);
        client.setName("client-name " + randomString);
        client.setPersistentTokensEnabled(true);
        client.setWebsite("client-website " + randomString);

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
        Set<String> scopes = new HashSet<String>();
        scopes.add("scope-type-1 " + randomString);
        scopes.add("scope-type-2 " + randomString);
        scopes.add("scope-type-3 " + randomString);
        client.setClientScopes(scopes);
        return client;
    }
}
