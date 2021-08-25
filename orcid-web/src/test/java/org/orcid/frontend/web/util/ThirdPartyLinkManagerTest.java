package org.orcid.frontend.web.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    }
    
    @Test
    public void findOrcidClientsWithPredefinedOauthScopeWorksImportTest() {
        ClientRedirectUriEntity r1 = new ClientRedirectUriEntity();
        r1.setClientDetailsEntity(client);
        r1.setPredefinedClientScope("/orcid-profile/read-limited /activities/update /orcid-works/create");
        r1.setRedirectUri("https://test.orcid.org/ruri/1");
        r1.setRedirectUriType("import-works-wizard");
        r1.setUriActType("{\"import-works-wizard\":[\"Articles\",\"Books\"]}");
        r1.setUriGeoArea("{\"import-works-wizard\":[\"Global\"]}");
        
        ClientRedirectUriEntity r2 = new ClientRedirectUriEntity();
        r2.setClientDetailsEntity(client);
        r2.setPredefinedClientScope("/orcid-profile/read-limited /activities/update /orcid-works/create");
        r2.setRedirectUri("https://test.orcid.org/ruri/2");
        r2.setRedirectUriType("import-works-wizard");
        r2.setUriActType("{\"import-works-wizard\":[\"Articles\",\"Books\", \"Student Publications\"]}");
        r2.setUriGeoArea("{\"import-works-wizard\":[\"Europe\",\"North America\"]}");
        
        List<ClientRedirectUriEntity> rUris = Arrays.asList(r1, r2);
        
        Mockito.doReturn(rUris).when(clientRedirectDaoReadOnly).findClientDetailsWithRedirectScope(Mockito.eq(RedirectUriType.IMPORT_WORKS_WIZARD.value()));
        
        List<ImportWizzardClientForm> forms = thirdPartyLinkManager.findOrcidClientsWithPredefinedOauthScopeWorksImport(Locale.ENGLISH);
        assertNotNull(forms);
        assertEquals(2, forms.size());
    }
    
    @Test
    public void findOrcidClientsWithPredefinedOauthScopeFundingImportTest() {
        
    }
    
    @Test
    public void findOrcidClientsWithPredefinedOauthScopeReadAccessTest() {
        
    }
    
    @Test
    public void findOrcidClientsWithPredefinedOauthScopePeerReviewImportTest() {
        
    }
    
}
