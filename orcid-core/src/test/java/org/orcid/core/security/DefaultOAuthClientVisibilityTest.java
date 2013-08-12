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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.oauth.OrcidOAuth2Authentication;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.oauth.OrcidOauth2UserAuthentication;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class DefaultOAuthClientVisibilityTest extends BaseTest {

    @Resource(name = "defaultPermissionChecker")
    private PermissionChecker permissionChecker;

    @Mock
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;

    @Before
    public void mockDependencies() throws Exception {
        DefaultPermissionChecker defaultPermissionChecker = (DefaultPermissionChecker) permissionChecker;
        defaultPermissionChecker.setOrcidOauthTokenDetailService(orcidOauth2TokenDetailService);
    }

    @Test
    @Transactional
    @Rollback
    public void testCheckClientPermissionsAllowOnlyPublicAndLimitedVisibility() throws Exception {
        Set<String> resourceIds = new HashSet<String>(Arrays.asList("orcid"));
        HashSet<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(Arrays.asList(new SimpleGrantedAuthority("ROLE_CLIENT")));
        DefaultAuthorizationRequest request = new DefaultAuthorizationRequest("4444-4444-4444-4446", Arrays.asList("/orcid-bio/external-identifiers/create"));
        request.setAuthorities(grantedAuthorities);
        request.setResourceIds(resourceIds);
        ProfileEntity entity = new ProfileEntity("4444-4444-4444-4446");
        OrcidOauth2UserAuthentication oauth2UserAuthentication = new OrcidOauth2UserAuthentication(entity, true);
        // we care only that an OAuth client request results in the correct
        // visibilities
        OrcidOAuth2Authentication oAuth2Authentication = new OrcidOAuth2Authentication(request, oauth2UserAuthentication, "made-up-token");

        when(orcidOauth2TokenDetailService.findNonDisabledByTokenValue(any(String.class))).thenReturn(new OrcidOauth2TokenDetail());
        ScopePathType scopePathType = ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE;
        Set<Visibility> visibilitiesForClient = permissionChecker.obtainVisibilitiesForAuthentication(oAuth2Authentication, scopePathType, getOrcidMessage());
        assertTrue(visibilitiesForClient.size() == 3);
        assertTrue(visibilitiesForClient.contains(Visibility.LIMITED));
        assertTrue(visibilitiesForClient.contains(Visibility.REGISTERED_ONLY));
        assertTrue(visibilitiesForClient.contains(Visibility.PUBLIC));
    }

    private OrcidMessage getOrcidMessage() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(OrcidMessage.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (OrcidMessage) unmarshaller.unmarshal(DefaultPermissionCheckerTest.class.getResourceAsStream("/orcid-full-message-no-visibility-latest.xml"));
    }
}
