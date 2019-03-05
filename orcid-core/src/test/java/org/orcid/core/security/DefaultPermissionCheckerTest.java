package org.orcid.core.security;

import static org.junit.Assert.fail;

import java.security.AccessControlException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.oauth.OrcidOAuth2Authentication;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.oauth.OrcidOauth2UserAuthentication;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Declan Newman (declan) Date: 27/04/2012
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class DefaultPermissionCheckerTest extends DBUnitTest {

    @Resource(name = "defaultPermissionChecker")
    private PermissionChecker defaultPermissionChecker;

    @Resource(name = "profileEntityManager")
    private ProfileEntityManager profileEntityManager;
    
    @Resource
    private OrcidOauth2TokenDetailService tokenDetailService; 

    @Resource(name = "profileEntityCacheManager")
    ProfileEntityCacheManager profileEntityCacheManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml","/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/Oauth2TokenDetailsData.xml", "/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml"));
    }

    @Test    
    @Rollback    
    @Transactional
    public void testCheckUserPermissionsAuthenticationScopesOrcidAndOrcidMessage() throws Exception {
        Set<String> resourceIds = new HashSet<String>(Arrays.asList("orcid"));
        HashSet<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(Arrays.asList(new SimpleGrantedAuthority("ROLE_CLIENT")));
        AuthorizationRequest request = new AuthorizationRequest("4444-4444-4444-4441", Arrays.asList("/orcid-bio/external-identifiers/create"));
        request.setAuthorities(grantedAuthorities);
        request.setResourceIds(resourceIds);
        ProfileEntity entity = profileEntityManager.findByOrcid("4444-4444-4444-4446");
        OrcidOauth2UserAuthentication oauth2UserAuthentication = new OrcidOauth2UserAuthentication(entity, true);
        OAuth2Authentication oAuth2Authentication = new OrcidOAuth2Authentication(request, oauth2UserAuthentication, "made-up-token");
        ScopePathType requiredScope = ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE;
        OrcidMessage orcidMessage = getOrcidMessage();
        String messageOrcid = orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath();
        defaultPermissionChecker.checkPermissions(oAuth2Authentication, requiredScope, messageOrcid, orcidMessage);
    }

    @Test(expected = AccessControlException.class)
    @Transactional
    @Rollback
    public void testCheckUserPermissionsAuthenticationScopesOrcidAndOrcidMessageWhenWrongUser() throws Exception {
        Set<String> resourceIds = new HashSet<String>(Arrays.asList("orcid"));
        HashSet<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(Arrays.asList(new SimpleGrantedAuthority("ROLE_CLIENT")));
        AuthorizationRequest request = new AuthorizationRequest("4444-4444-4444-4441", Arrays.asList("/orcid-bio/external-identifiers/create"));
        request.setAuthorities(grantedAuthorities);
        request.setResourceIds(resourceIds);
        ProfileEntity entity = profileEntityManager.findByOrcid("4444-4444-4444-4445");
        OrcidOauth2UserAuthentication oauth2UserAuthentication = new OrcidOauth2UserAuthentication(entity, true);
        OAuth2Authentication oAuth2Authentication = new OrcidOAuth2Authentication(request, oauth2UserAuthentication, "made-up-token");
        ScopePathType requiredScope = ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE;
        OrcidMessage orcidMessage = getOrcidMessage();
        String messageOrcid = orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath();
        defaultPermissionChecker.checkPermissions(oAuth2Authentication, requiredScope, messageOrcid, orcidMessage);
    }

    @Test
    @Transactional
    @Rollback
    public void testCheckClientPermissionsAuthenticationScopesOrcidAndOrcidMessage() throws Exception {
        Set<String> resourceIds = new HashSet<String>(Arrays.asList("orcid"));
        HashSet<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(Arrays.asList(new SimpleGrantedAuthority("ROLE_CLIENT")));
        AuthorizationRequest request = new AuthorizationRequest("APP-5555555555555555", Arrays.asList("/orcid-bio/external-identifiers/create"));
        request.setAuthorities(grantedAuthorities);
        request.setResourceIds(resourceIds);
        OAuth2Authentication oAuth2Authentication = new OrcidOAuth2Authentication(request, null, "made-up-token");
        ScopePathType requiredScope = ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE;
        OrcidMessage orcidMessage = getOrcidMessage();
        orcidMessage.getOrcidProfile().getOrcidIdentifier().setPath("4444-4444-4444-4447");
        String messageOrcid = orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath();
        defaultPermissionChecker.checkPermissions(oAuth2Authentication, requiredScope, messageOrcid, orcidMessage);
    }

    @Test
    public void testCheckPermissionsAuthenticationScopesAndOrcidMessage() throws Exception {
        Set<String> resourceIds = new HashSet<String>(Arrays.asList("orcid"));
        HashSet<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(Arrays.asList(new SimpleGrantedAuthority("ROLE_CLIENT")));
        AuthorizationRequest request = new AuthorizationRequest("4444-4444-4444-4441", Arrays.asList(ScopePathType.ORCID_WORKS_CREATE.value()));
        request.setAuthorities(grantedAuthorities);
        request.setResourceIds(resourceIds);
        OAuth2Authentication oAuth2Authentication = new OrcidOAuth2Authentication(request, null, "made-up-token");
        ScopePathType requiredScope = ScopePathType.ORCID_WORKS_CREATE;
        OrcidMessage orcidMessage = getOrcidMessage();
        defaultPermissionChecker.checkPermissions(oAuth2Authentication, requiredScope, orcidMessage);
    }

    @Test
    public void testCheckPermissionsAuthenticationScopePathTypesAndOrcid() throws Exception {
        Set<String> resourceIds = new HashSet<String>(Arrays.asList("orcid"));
        HashSet<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(Arrays.asList(new SimpleGrantedAuthority("ROLE_CLIENT")));
        AuthorizationRequest request = new AuthorizationRequest("4444-4444-4444-4441", Arrays.asList(ScopePathType.ORCID_BIO_READ_LIMITED.value()));
        request.setAuthorities(grantedAuthorities);
        request.setResourceIds(resourceIds);
        OAuth2Authentication oAuth2Authentication = new OrcidOAuth2Authentication(request, null, "made-up-token");
        ScopePathType requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED;
        defaultPermissionChecker.checkPermissions(oAuth2Authentication, requiredScope, "4444-4444-4444-4447");
    }
    
    @Test
    @Transactional
    @Rollback
    public void checkRemoveUserGrantWriteScopePastValitityForNonPersistentTokens() {
        OrcidOauth2TokenDetail token = tokenDetailService.findIgnoringDisabledByTokenValue("00000001-d80f-4afc-8f95-9b48d28aaadb");
        DefaultPermissionChecker customPermissionChecker = (DefaultPermissionChecker) defaultPermissionChecker;
        if(!customPermissionChecker.removeUserGrantWriteScopePastValitity(token))
            fail();
    }

    @Test
    @Transactional
    @Rollback
    public void checkRemoveUserGrantWriteScopePastValitityForPersistentTokens() {
        OrcidOauth2TokenDetail token = tokenDetailService.findIgnoringDisabledByTokenValue("00000002-d80f-4afc-8f95-9b48d28aaadb");
        DefaultPermissionChecker customPermissionChecker = (DefaultPermissionChecker) defaultPermissionChecker;
        if(customPermissionChecker.removeUserGrantWriteScopePastValitity(token))
            fail();
    }
    
    private OrcidMessage getOrcidMessage() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(OrcidMessage.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (OrcidMessage) unmarshaller.unmarshal(DefaultPermissionCheckerTest.class.getResourceAsStream("/orcid-full-message-no-visibility-latest.xml"));
    }
}
