/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.security;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.oauth.OrcidOauth2UserAuthentication;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 27/04/2012
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class DefaultPermissionCheckerTest extends DBUnitTest {

    @Resource(name = "defaultPermissionChecker")
    private PermissionChecker defaultPermissionChecker;

    @Resource(name = "profileEntityManager")
    private ProfileEntityManager profileEntityManager;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml"), null);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/SecurityQuestionEntityData.xml"), null);
    }

    @Test
    @Transactional
    @Rollback
    public void testCheckUserPermissionsAuthenticationScopesOrcidAndOrcidMessage() throws Exception {
        Collection<String> resourceIds = new HashSet<String>(Arrays.asList("orcid"));
        HashSet<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(Arrays.asList(new SimpleGrantedAuthority("ROLE_CLIENT")));
        AuthorizationRequest request = new AuthorizationRequest("4444-4444-4444-4441", Arrays.asList("/orcid-bio/external-identifiers/create"), grantedAuthorities,
                resourceIds);
        ProfileEntity entity = profileEntityManager.findByOrcid("4444-4444-4444-4441");
        OrcidOauth2UserAuthentication oauth2UserAuthentication = new OrcidOauth2UserAuthentication(entity, true);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(request, oauth2UserAuthentication);
        ScopePathType requiredScope = ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE;
        OrcidMessage orcidMessage = getOrcidMessage();
        String messageOrcid = orcidMessage.getOrcidProfile().getOrcid().getValue();
        defaultPermissionChecker.checkPermissions(oAuth2Authentication, requiredScope, messageOrcid, orcidMessage);
    }

    @Test
    @Transactional
    @Rollback
    public void testCheckClientPermissionsAuthenticationScopesOrcidAndOrcidMessage() throws Exception {
        Collection<String> resourceIds = new HashSet<String>(Arrays.asList("orcid"));
        HashSet<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(Arrays.asList(new SimpleGrantedAuthority("ROLE_CLIENT")));
        AuthorizationRequest request = new AuthorizationRequest("4444-4444-4444-4441", Arrays.asList("/orcid-bio/external-identifiers/create"), grantedAuthorities,
                resourceIds);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(request, null);
        ScopePathType requiredScope = ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE;
        OrcidMessage orcidMessage = getOrcidMessage();
        orcidMessage.getOrcidProfile().getOrcid().setValue("4444-4444-4444-4447");
        String messageOrcid = orcidMessage.getOrcidProfile().getOrcid().getValue();
        defaultPermissionChecker.checkPermissions(oAuth2Authentication, requiredScope, messageOrcid, orcidMessage);
    }

    @Test
    public void testCheckPermissionsAuthenticationScopesAndOrcidMessage() throws Exception {
        Collection<String> resourceIds = new HashSet<String>(Arrays.asList("orcid"));
        HashSet<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(Arrays.asList(new SimpleGrantedAuthority("ROLE_CLIENT")));
        AuthorizationRequest request = new AuthorizationRequest("4444-4444-4444-4441", Arrays.asList(ScopePathType.ORCID_WORKS_CREATE.value()), grantedAuthorities,
                resourceIds);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(request, null);
        ScopePathType requiredScope = ScopePathType.ORCID_WORKS_CREATE;
        OrcidMessage orcidMessage = getOrcidMessage();
        defaultPermissionChecker.checkPermissions(oAuth2Authentication, requiredScope, orcidMessage);
    }

    @Test
    public void testCheckPermissionsAuthenticationScopePathTypesAndOrcid() throws Exception {
        Collection<String> resourceIds = new HashSet<String>(Arrays.asList("orcid"));
        HashSet<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(Arrays.asList(new SimpleGrantedAuthority("ROLE_CLIENT")));
        AuthorizationRequest request = new AuthorizationRequest("4444-4444-4444-4441", Arrays.asList(ScopePathType.ORCID_BIO_READ_LIMITED.value()), grantedAuthorities,
                resourceIds);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(request, null);
        ScopePathType requiredScope = ScopePathType.ORCID_BIO_READ_LIMITED;
        defaultPermissionChecker.checkPermissions(oAuth2Authentication, requiredScope, "4444-4444-4444-4447");
    }

    private OrcidMessage getOrcidMessage() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(OrcidMessage.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (OrcidMessage) unmarshaller.unmarshal(DefaultPermissionCheckerTest.class.getResourceAsStream("/orcid-full-message-no-visibility-latest.xml"));
    }
}
