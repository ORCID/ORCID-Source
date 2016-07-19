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
package org.orcid.core.oauth;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-oauth2-common-config.xml" })
public class OrcidClientCredentialEndPointDelegatorTest extends DBUnitTest {

    private static final String CLIENT_ID_1 = "APP-5555555555555555";
    private static final String CLIENT_ID_2 = "APP-5555555555555556";
    private static final String USER_ORCID = "0000-0000-0000-0001";
    
    @Resource
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/SubjectEntityData.xml",
                "/data/SecurityQuestionEntityData.xml"));
    }
    
    private OrcidOauth2AuthoriziationCodeDetail createAuthorizationCode(String value, String clientId, String redirectUri, String ... scopes) {
        OrcidOauth2AuthoriziationCodeDetail authorizationCode = new OrcidOauth2AuthoriziationCodeDetail();
        authorizationCode.setId(value);
        authorizationCode.setApproved(true);
        authorizationCode.setScopes(new HashSet<String>(Arrays.asList(scopes)));
        authorizationCode.setClientDetailsEntity(new ClientDetailsEntity(clientId));
        authorizationCode.setPersistent(true);
        authorizationCode.setProfileEntity(new ProfileEntity(USER_ORCID));
        authorizationCode.setRedirectUri(redirectUri);
        authorizationCode.setResourceIds(new HashSet<String>(Arrays.asList("orcid")));
        orcidOauth2AuthoriziationCodeDetailDao.persist(authorizationCode);
        return authorizationCode;
    }
    
    @Test
    public void generateAccessTokenTest() {
        fail();
    }
    
    @Test
    public void generateClientCredentialsAccessTokenTest() {
        fail();
    }
        
    @Test
    public void generateRefreshTokenTest() {
     fail();   
    }
    
    @Test
    public void generateClientCredentialsAccessTokenWithInvalidTokenTest() {
        fail();
    }
    
    @Test
    public void generateRefreshTokenWithExpiredTokenTest() {
        fail();
    }
}
