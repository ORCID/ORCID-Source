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
package org.orcid.core.oauth.service;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.jaxb.model.message.Orcid;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.test.DBUnitTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationRequestHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 24/04/2012
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class OrcidAuthorizationCodeServiceTest extends DBUnitTest {

    @Resource(name = "orcidAuthorizationCodeService")
    private AuthorizationCodeServices authorizationCodeServices;

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
    @Rollback
    @Transactional
    public void testCreateAuthorizationCodeWithValidClient() {
        AuthorizationRequestHolder request = getAuthorizationRequestHolder("4444-4444-4444-4441");
        String authorizationCode = authorizationCodeServices.createAuthorizationCode(request);
        assertNotNull(authorizationCode);
        AuthorizationRequestHolder authorizationRequestHolder = authorizationCodeServices.consumeAuthorizationCode(authorizationCode);
        assertNotNull(authorizationRequestHolder);
    }

    @Test(expected = InvalidGrantException.class)
    @Rollback
    @Transactional
    public void testConsumeNonExistentCode() {
        authorizationCodeServices.consumeAuthorizationCode("bodus-code!");
    }

    @Test(expected = IllegalArgumentException.class)
    @Rollback
    @Transactional
    public void testCreateAuthorizationCodeWithInvalidClient() {
        AuthorizationRequestHolder request = getAuthorizationRequestHolder("6444-4444-4444-4441");
        authorizationCodeServices.createAuthorizationCode(request);
    }

    public AuthorizationRequestHolder getAuthorizationRequestHolder(String clientId) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        Set<String> resourceIds = new HashSet<>();
        resourceIds.add("orcid");
        DefaultAuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest(clientId, Arrays.asList("a-scope"));
        authorizationRequest.setAuthorities(grantedAuthorities);
        authorizationRequest.setResourceIds(resourceIds);
        OrcidProfile profile = new OrcidProfile();
        profile.setOrcid(new Orcid("4444-4444-4444-4445"));
        OrcidProfileUserDetails details = new OrcidProfileUserDetails(profile);
        Authentication userAuthentication = new UsernamePasswordAuthenticationToken(details, "password");

        return new AuthorizationRequestHolder(authorizationRequest, userAuthentication);
    }
}
