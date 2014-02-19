package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.impl.OrcidSSOManagerImpl;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class OrcidSSOManagerImplTest extends BaseTest {
    
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml",
            "/data/ProfileWorksEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml");
    
    private String orcid1 = "4444-4444-4444-444X"; 
    
    @Resource
    OrcidSSOManagerImpl ssoManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES, null);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles, null);
    }
    
    @Test
    @Rollback(true)
    @Transactional
    public void testCreateOrcidClientGroup() {
        HashSet<String> uris = new HashSet<String>();
        uris.add("http://1.com");
        uris.add("http://2.com");
        ClientDetailsEntity clientDetails = ssoManager.grantSSOAccess(orcid1, uris);
        assertNotNull(clientDetails);
        assertNotNull(clientDetails.getAuthorizedGrantTypes());
        assertTrue(clientDetails.getAuthorizedGrantTypes().contains("authorization_code"));
        assertNotNull(clientDetails.getClientRegisteredRedirectUris());
        assertEquals(clientDetails.getClientRegisteredRedirectUris().size(), 2);
        for(ClientRedirectUriEntity redirectUri : clientDetails.getClientRegisteredRedirectUris()) {
            assertTrue(redirectUri.getRedirectUri().equals("http://1.com") || redirectUri.getRedirectUri().equals("http://2.com"));
            assertTrue(redirectUri.getRedirectUriType().equals(RedirectUriType.SSO_AUTHENTICATION.value()));
        }
        clientDetails.getAuthorities();
        clientDetails.getClientAuthorizedGrantTypes();
        clientDetails.getClientGrantedAuthorities();
        
    }
}