package org.orcid.frontend.web.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.persistence.dao.ClientRedirectDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.pojo.ajaxForm.ImportWizzardClientForm;
import org.orcid.pojo.ajaxForm.SearchAndLinkWizardFormSummary;
import org.springframework.test.util.ReflectionTestUtils;

public class ThirdPartyLinkManagerTest {

    @Mock
    private ClientRedirectDao clientRedirectDaoReadOnly;

    @Mock
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Mock
    private LocaleManager localeManager;

    @Mock
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;

    @Mock
    private RedisClient redisClient;
    
    @InjectMocks
    private ThirdPartyLinkManager thirdPartyLinkManager;

    private static final String clientId = "APP-00001";
    private static final ClientDetailsEntity client = new ClientDetailsEntity(clientId);
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        Mockito.doReturn("Articles").when(localeManager).resolveMessage(eq("workspace.works.import_wizzard.work_type.articles"), Mockito.any(Locale.class));
        Mockito.doReturn("Books").when(localeManager).resolveMessage(eq("workspace.works.import_wizzard.work_type.books"), Mockito.any(Locale.class));
        Mockito.doReturn("Data").when(localeManager).resolveMessage(eq("workspace.works.import_wizzard.work_type.data"), Mockito.any(Locale.class));
        Mockito.doReturn("Student Publications").when(localeManager).resolveMessage(eq("workspace.works.import_wizzard.work_type.student_publications"), Mockito.any(Locale.class));
        
        Mockito.doReturn("Africa").when(localeManager).resolveMessage(eq("workspace.works.import_wizzard.geo_area.africa"), Mockito.any(Locale.class));
        Mockito.doReturn("Asia").when(localeManager).resolveMessage(eq("workspace.works.import_wizzard.geo_area.asia"), Mockito.any(Locale.class));
        Mockito.doReturn("Australia").when(localeManager).resolveMessage(eq("workspace.works.import_wizzard.geo_area.australia"), Mockito.any(Locale.class));
        Mockito.doReturn("Europe").when(localeManager).resolveMessage(eq("workspace.works.import_wizzard.geo_area.europe"), Mockito.any(Locale.class));
        Mockito.doReturn("Global").when(localeManager).resolveMessage(eq("workspace.works.import_wizzard.geo_area.global"), Mockito.any(Locale.class));
        Mockito.doReturn("North America").when(localeManager).resolveMessage(eq("workspace.works.import_wizzard.geo_area.north_america"), Mockito.any(Locale.class));
        Mockito.doReturn("South America").when(localeManager).resolveMessage(eq("workspace.works.import_wizzard.geo_area.south_america"), Mockito.any(Locale.class));
    }
    
    private void initRedirectUriData(Boolean isWorksWizzard, RedirectUriType rut) {
        ClientRedirectUriEntity r1 = new ClientRedirectUriEntity();
        r1.setClientId(clientId);
        r1.setRedirectUri("https://test.orcid.org/ruri/1");
        r1.setRedirectUriType("import-works-wizard");
        r1.setPredefinedClientScope("/read-limited /activities/update");
        r1.setRedirectUriMetadata("{}");
        
        ClientRedirectUriEntity r2 = new ClientRedirectUriEntity();
        r2.setClientId(clientId);
        r2.setRedirectUri("https://test.orcid.org/ruri/2");
        r2.setRedirectUriType("import-works-wizard");
        r2.setPredefinedClientScope("/activities/update /activities/create /authenticate");
        r2.setRedirectUriMetadata("{\"certified\":true}");
        
        if(isWorksWizzard) {
            r1.setUriActType("{\"import-works-wizard\":[\"Articles\",\"Books\"]}");
            r1.setUriGeoArea("{\"import-works-wizard\":[\"Global\"]}");
            
            r2.setUriActType("{\"import-works-wizard\":[\"Articles\",\"Books\", \"Student Publications\"]}");
            r2.setUriGeoArea("{\"import-works-wizard\":[\"Europe\",\"North America\"]}");
        }
        
        List<ClientRedirectUriEntity> rUris = Arrays.asList(r1, r2);
        
        Mockito.doReturn(rUris).when(clientRedirectDaoReadOnly).findClientDetailsWithRedirectScope(eq(rut.value()));

        ClientDetailsEntity clientDetailsMock = Mockito.mock(ClientDetailsEntity.class);
        Mockito.when(clientDetailsMock.getId()).thenReturn(clientId);
        Mockito.when(clientDetailsMock.getClientId()).thenReturn(clientId);
        Mockito.when(clientDetailsMock.getClientName()).thenReturn("ClientName");
        Mockito.when(clientDetailsMock.getClientDescription()).thenReturn("ClientDescription");
        Mockito.when(clientDetailsMock.getClientWebsite()).thenReturn("http://test.orcid.org");
        Mockito.when(clientDetailsEntityCacheManager.retrieve(eq(clientId))).thenReturn(clientDetailsMock);
    }

    private void initSearchAndLinkWizardData() {
        ClientRedirectUriEntity r1 = new ClientRedirectUriEntity();
        r1.setClientId("APP-00001");
        r1.setRedirectUri("https://test.orcid.org/search-link/1");
        r1.setRedirectUriType("import-works-wizard");
        r1.setPredefinedClientScope("/read-limited");
        r1.setRedirectUriMetadata("{}");

        ClientRedirectUriEntity r2 = new ClientRedirectUriEntity();
        r2.setClientId("APP-00002");
        r2.setRedirectUri("https://test.orcid.org/search-link/2");
        r2.setRedirectUriType("import-works-wizard");
        r2.setPredefinedClientScope("/activities/update");
        r2.setRedirectUriMetadata("{\"certified\":true}");

        Mockito.doReturn(Arrays.asList(r1, r2)).when(clientRedirectDaoReadOnly).findClientDetailsWithRedirectScope(eq(RedirectUriType.IMPORT_WORKS_WIZARD.value()));

        ClientDetailsEntity c1 = Mockito.mock(ClientDetailsEntity.class);
        Mockito.when(c1.getId()).thenReturn("APP-00001");
        Mockito.when(c1.getClientName()).thenReturn("Client One");
        Mockito.when(clientDetailsEntityCacheManager.retrieve(eq("APP-00001"))).thenReturn(c1);

        ClientDetailsEntity c2 = Mockito.mock(ClientDetailsEntity.class);
        Mockito.when(c2.getId()).thenReturn("APP-00002");
        Mockito.when(c2.getClientName()).thenReturn("Client Two");
        Mockito.when(clientDetailsEntityCacheManager.retrieve(eq("APP-00002"))).thenReturn(c2);
    }
    
    @Test
    public void findOrcidClientsWithPredefinedOauthScopeWorksImportTest() {
        initRedirectUriData(true, RedirectUriType.IMPORT_WORKS_WIZARD);
        
        List<ImportWizzardClientForm> forms = thirdPartyLinkManager.findOrcidClientsWithPredefinedOauthScopeWorksImport(Locale.ENGLISH);
        assertNotNull(forms);
        assertEquals(2, forms.size());
        
        ImportWizzardClientForm f1 = forms.get(0);
        assertEquals(2, f1.getActTypes().size());
        assertTrue(f1.getActTypes().contains("Articles"));
        assertTrue(f1.getActTypes().contains("Books"));
        assertEquals(1, f1.getGeoAreas().size());
        assertTrue(f1.getGeoAreas().contains("Global"));
        assertEquals("https://test.orcid.org/ruri/1", f1.getRedirectUri());
        assertEquals("/read-limited /activities/update", f1.getScopes());
        assertNull(f1.getRedirectUriMetadata());
        
        ImportWizzardClientForm f2 = forms.get(1);
        assertEquals(3, f2.getActTypes().size());
        assertTrue(f2.getActTypes().contains("Articles"));
        assertTrue(f2.getActTypes().contains("Books"));
        assertTrue(f2.getActTypes().contains("Student Publications"));        
        assertEquals(2, f2.getGeoAreas().size());
        assertTrue(f2.getGeoAreas().contains("Europe"));
        assertTrue(f2.getGeoAreas().contains("North America"));
        assertEquals("https://test.orcid.org/ruri/2", f2.getRedirectUri());
        assertEquals("/activities/update /activities/create /authenticate", f2.getScopes());
        assertNotNull(f2.getRedirectUriMetadata());
        assertTrue(f2.getRedirectUriMetadata().get("certified").asBoolean());       
    }
    
    @Test
    public void findOrcidClientsWithPredefinedOauthScopeFundingImportTest() {
        initRedirectUriData(false, RedirectUriType.IMPORT_FUNDING_WIZARD);
        
        List<ImportWizzardClientForm> forms = thirdPartyLinkManager.findOrcidClientsWithPredefinedOauthScopeFundingImport(Locale.ENGLISH);
        assertNotNull(forms);
        assertEquals(2, forms.size());
        
        ImportWizzardClientForm f1 = forms.get(0);
        assertEquals("https://test.orcid.org/ruri/1", f1.getRedirectUri());
        assertEquals("/read-limited /activities/update", f1.getScopes());
        
        ImportWizzardClientForm f2 = forms.get(1);
        assertEquals("https://test.orcid.org/ruri/2", f2.getRedirectUri());
        assertEquals("/activities/update /activities/create /authenticate", f2.getScopes());   
    }
    
    @Test
    public void findOrcidClientsWithPredefinedOauthScopeReadAccessTest() {
        initRedirectUriData(false, RedirectUriType.GRANT_READ_WIZARD);
        
        List<ImportWizzardClientForm> forms = thirdPartyLinkManager.findOrcidClientsWithPredefinedOauthScopeReadAccess(Locale.ENGLISH);
        assertNotNull(forms);
        assertEquals(2, forms.size());
        
        ImportWizzardClientForm f1 = forms.get(0);
        assertEquals("https://test.orcid.org/ruri/1", f1.getRedirectUri());
        assertEquals("/read-limited /activities/update", f1.getScopes());
        
        ImportWizzardClientForm f2 = forms.get(1);
        assertEquals("https://test.orcid.org/ruri/2", f2.getRedirectUri());
        assertEquals("/activities/update /activities/create /authenticate", f2.getScopes());
    }
    
    @Test
    public void findOrcidClientsWithPredefinedOauthScopePeerReviewImportTest() {
        initRedirectUriData(false, RedirectUriType.IMPORT_PEER_REVIEW_WIZARD);
        
        List<ImportWizzardClientForm> forms = thirdPartyLinkManager.findOrcidClientsWithPredefinedOauthScopePeerReviewImport(Locale.ENGLISH);
        assertNotNull(forms);
        assertEquals(2, forms.size());
        
        ImportWizzardClientForm f1 = forms.get(0);
        assertEquals("https://test.orcid.org/ruri/1", f1.getRedirectUri());
        assertEquals("/read-limited /activities/update", f1.getScopes());
        
        ImportWizzardClientForm f2 = forms.get(1);
        assertEquals("https://test.orcid.org/ruri/2", f2.getRedirectUri());
        assertEquals("/activities/update /activities/create /authenticate", f2.getScopes());
    }

    @Test
    public void findSearchAndLinkWizardClients_cacheMiss_buildsListAndSetsConnected() {
        initSearchAndLinkWizardData();
        Mockito.when(redisClient.get(eq("works-search-and-link-wizard-clients"))).thenReturn(null);

        Mockito.when(orcidOauth2TokenDetailService.doesClientKnowUser(eq("APP-00001"), eq("0000-0000-0000-0001"))).thenReturn(true);
        Mockito.when(orcidOauth2TokenDetailService.doesClientKnowUser(eq("APP-00002"), eq("0000-0000-0000-0001"))).thenReturn(false);

        List<SearchAndLinkWizardFormSummary> forms = thirdPartyLinkManager.findSearchAndLinkWizardClients("0000-0000-0000-0001");
        assertNotNull(forms);
        assertEquals(2, forms.size());

        SearchAndLinkWizardFormSummary f1 = forms.get(0);
        assertEquals("APP-00001", f1.getId());
        assertEquals("Client One", f1.getName());
        assertEquals("https://test.orcid.org/search-link/1", f1.getRedirectUri());
        assertEquals("/read-limited", f1.getScopes());
        assertNull(f1.getRedirectUriMetadata());
        assertTrue(f1.isConnected());

        SearchAndLinkWizardFormSummary f2 = forms.get(1);
        assertEquals("APP-00002", f2.getId());
        assertEquals("Client Two", f2.getName());
        assertEquals("https://test.orcid.org/search-link/2", f2.getRedirectUri());
        assertEquals("/activities/update", f2.getScopes());
        assertNotNull(f2.getRedirectUriMetadata());
        assertTrue(f2.getRedirectUriMetadata().get("certified").asBoolean());
        assertFalse(f2.isConnected());
    }

    @Test
    public void findSearchAndLinkWizardClients_cacheHit_usesCachedValueAndSkipsDao() {
        ReflectionTestUtils.setField(thirdPartyLinkManager, "worksSearchAndLinkWizardCacheTtl", 123);

        String cachedJson = "[{\"id\":\"APP-00001\",\"name\":\"Client One\",\"redirectUri\":\"https://test.orcid.org/search-link/1\",\"scopes\":\"/read-limited\",\"redirectUriMetadata\":{\"certified\":true}}]";
        Mockito.when(redisClient.get(eq("works-search-and-link-wizard-clients"))).thenReturn(cachedJson);
        Mockito.when(orcidOauth2TokenDetailService.doesClientKnowUser(eq("APP-00001"), eq("0000-0000-0000-0001"))).thenReturn(true);

        List<SearchAndLinkWizardFormSummary> forms = thirdPartyLinkManager.findSearchAndLinkWizardClients("0000-0000-0000-0001");
        assertNotNull(forms);
        assertEquals(1, forms.size());
        assertTrue(forms.get(0).isConnected());
        assertNotNull(forms.get(0).getRedirectUriMetadata());
        assertTrue(forms.get(0).getRedirectUriMetadata().get("certified").asBoolean());

        Mockito.verify(clientRedirectDaoReadOnly, Mockito.never()).findClientDetailsWithRedirectScope(Mockito.anyString());
        Mockito.verify(redisClient, Mockito.never()).set(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    public void findSearchAndLinkWizardClients_badCachedValue_rebuildsAndStores() {
        initSearchAndLinkWizardData();
        ReflectionTestUtils.setField(thirdPartyLinkManager, "worksSearchAndLinkWizardCacheTtl", 99);

        Mockito.when(redisClient.get(eq("works-search-and-link-wizard-clients"))).thenReturn("{bad json");
        Mockito.when(orcidOauth2TokenDetailService.doesClientKnowUser(eq("APP-00001"), eq("0000-0000-0000-0001"))).thenReturn(true);
        Mockito.when(orcidOauth2TokenDetailService.doesClientKnowUser(eq("APP-00002"), eq("0000-0000-0000-0001"))).thenReturn(false);

        List<SearchAndLinkWizardFormSummary> forms = thirdPartyLinkManager.findSearchAndLinkWizardClients("0000-0000-0000-0001");
        assertNotNull(forms);
        assertEquals(2, forms.size());

        Mockito.verify(redisClient).set(eq("works-search-and-link-wizard-clients"), Mockito.anyString(), eq(99));
    }

    @Test
    public void afterPropertiesSet_cacheEmpty_populatesCache() {
        Mockito.when(clientRedirectDaoReadOnly.findClientDetailsWithRedirectScope(eq(RedirectUriType.IMPORT_WORKS_WIZARD.value())))
                .thenReturn(Arrays.asList());
        ReflectionTestUtils.setField(thirdPartyLinkManager, "worksSearchAndLinkWizardCacheTtl", 3600);
        Mockito.when(redisClient.get(eq("works-search-and-link-wizard-clients"))).thenReturn(null);

        thirdPartyLinkManager.afterPropertiesSet();

        Mockito.verify(redisClient).get(eq("works-search-and-link-wizard-clients"));
        Mockito.verify(redisClient).set(eq("works-search-and-link-wizard-clients"), Mockito.anyString(), eq(3600));
    }

    @Test
    public void afterPropertiesSet_readsFromRedis() {
        Mockito.when(clientRedirectDaoReadOnly.findClientDetailsWithRedirectScope(eq(RedirectUriType.IMPORT_WORKS_WIZARD.value())))
                .thenReturn(Arrays.asList());
        Mockito.when(redisClient.get(eq("works-search-and-link-wizard-clients"))).thenReturn(null);

        thirdPartyLinkManager.afterPropertiesSet();

        // Cache enabled: initCache() reads from Redis (and populates when empty)
        Mockito.verify(redisClient).get(eq("works-search-and-link-wizard-clients"));
    }

    @Test
    public void afterPropertiesSet_cacheInvalid_rebuildsCache() {
        Mockito.when(clientRedirectDaoReadOnly.findClientDetailsWithRedirectScope(eq(RedirectUriType.IMPORT_WORKS_WIZARD.value())))
                .thenReturn(Arrays.asList());
        ReflectionTestUtils.setField(thirdPartyLinkManager, "worksSearchAndLinkWizardCacheTtl", 1800);
        Mockito.when(redisClient.get(eq("works-search-and-link-wizard-clients"))).thenReturn("{invalid json");

        thirdPartyLinkManager.afterPropertiesSet();

        Mockito.verify(redisClient).get(eq("works-search-and-link-wizard-clients"));
        Mockito.verify(redisClient).set(eq("works-search-and-link-wizard-clients"), Mockito.anyString(), eq(1800));
    }
    
}
