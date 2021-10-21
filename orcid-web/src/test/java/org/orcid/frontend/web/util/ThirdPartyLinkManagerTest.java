package org.orcid.frontend.web.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.persistence.dao.ClientRedirectDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.pojo.ajaxForm.ImportWizzardClientForm;

public class ThirdPartyLinkManagerTest {

    @Mock
    private ClientRedirectDao clientRedirectDaoReadOnly;

    @Mock
    private LocaleManager localeManager;
    
    @InjectMocks
    private ThirdPartyLinkManager thirdPartyLinkManager;
    
    private static final ClientDetailsEntity client = new ClientDetailsEntity("APP-00001");
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        Mockito.doReturn("Articles").when(localeManager).resolveMessage(Mockito.eq("workspace.works.import_wizzard.work_type.articles"), Mockito.any(Locale.class));
        Mockito.doReturn("Books").when(localeManager).resolveMessage(Mockito.eq("workspace.works.import_wizzard.work_type.books"), Mockito.any(Locale.class));
        Mockito.doReturn("Data").when(localeManager).resolveMessage(Mockito.eq("workspace.works.import_wizzard.work_type.data"), Mockito.any(Locale.class));
        Mockito.doReturn("Student Publications").when(localeManager).resolveMessage(Mockito.eq("workspace.works.import_wizzard.work_type.student_publications"), Mockito.any(Locale.class));
        
        Mockito.doReturn("Africa").when(localeManager).resolveMessage(Mockito.eq("workspace.works.import_wizzard.geo_area.africa"), Mockito.any(Locale.class));
        Mockito.doReturn("Asia").when(localeManager).resolveMessage(Mockito.eq("workspace.works.import_wizzard.geo_area.asia"), Mockito.any(Locale.class));
        Mockito.doReturn("Australia").when(localeManager).resolveMessage(Mockito.eq("workspace.works.import_wizzard.geo_area.australia"), Mockito.any(Locale.class));
        Mockito.doReturn("Europe").when(localeManager).resolveMessage(Mockito.eq("workspace.works.import_wizzard.geo_area.europe"), Mockito.any(Locale.class));
        Mockito.doReturn("Global").when(localeManager).resolveMessage(Mockito.eq("workspace.works.import_wizzard.geo_area.global"), Mockito.any(Locale.class));
        Mockito.doReturn("North America").when(localeManager).resolveMessage(Mockito.eq("workspace.works.import_wizzard.geo_area.north_america"), Mockito.any(Locale.class));
        Mockito.doReturn("South America").when(localeManager).resolveMessage(Mockito.eq("workspace.works.import_wizzard.geo_area.south_america"), Mockito.any(Locale.class));
    }
    
    private void initRedirectUriData(Boolean isWorksWizzard, RedirectUriType rut) {
        ClientRedirectUriEntity r1 = new ClientRedirectUriEntity();
        r1.setClientDetailsEntity(client);
        r1.setPredefinedClientScope("/read-limited /activities/update");
        r1.setRedirectUri("https://test.orcid.org/ruri/1");
        r1.setRedirectUriType("import-works-wizard");
        
        ClientRedirectUriEntity r2 = new ClientRedirectUriEntity();
        r2.setClientDetailsEntity(client);
        r2.setPredefinedClientScope("/activities/update /activities/create /authenticate");
        r2.setRedirectUri("https://test.orcid.org/ruri/2");
        r2.setRedirectUriType("import-works-wizard");
        
        if(isWorksWizzard) {
            r1.setUriActType("{\"import-works-wizard\":[\"Articles\",\"Books\"]}");
            r1.setUriGeoArea("{\"import-works-wizard\":[\"Global\"]}");
            
            r2.setUriActType("{\"import-works-wizard\":[\"Articles\",\"Books\", \"Student Publications\"]}");
            r2.setUriGeoArea("{\"import-works-wizard\":[\"Europe\",\"North America\"]}");
        }
        
        List<ClientRedirectUriEntity> rUris = Arrays.asList(r1, r2);
        
        Mockito.doReturn(rUris).when(clientRedirectDaoReadOnly).findClientDetailsWithRedirectScope(Mockito.eq(rut.value()));
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
    
}
