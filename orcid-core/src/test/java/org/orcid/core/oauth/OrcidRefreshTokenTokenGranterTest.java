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

import java.util.Arrays;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-oauth2-common-config.xml" })
public class OrcidRefreshTokenTokenGranterTest extends DBUnitTest {

    private static final String CLIENT_ID_1 = "APP-5555555555555555";
    private static final String CLIENT_ID_2 = "APP-5555555555555556";
    private static final String USER_ORCID = "0000-0000-0000-0001";
    
    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;
    
    @Resource
    private OrcidRefreshTokenTokenGranter tokenGranter;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    } 
    
    private OrcidOauth2TokenDetail createToken(String clientId, String userOrcid, String tokenValue, String refreshTokenValue, Date expirationDate, String scopes, boolean disabled) {
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setApproved(true);
        token.setClientDetailsId(clientId);
        token.setDateCreated(new Date());
        token.setLastModified(new Date());
        token.setProfile(new ProfileEntity(userOrcid));
        token.setScope(scopes);
        token.setTokenDisabled(disabled);
        token.setTokenExpiration(expirationDate);
        token.setTokenType("bearer");
        token.setTokenValue(tokenValue);
        token.setRefreshTokenValue(refreshTokenValue);
        orcidOauth2TokenDetailService.saveOrUpdate(token);
        return token;
    }
    
    //TODO!!!!!!
    //1: Create token, create refresh, parent should be disabled, scopes should be equal
    //2: Create token, create refresh with narrower scopes, parent should be disabled, scopes should be narrower
    //3: Create token, create refresh without disabling parent token, parent should work, refresh should work
    //4: Create token, create refresh with narrower scopes and without disabling parent token, parent should work, refresh should have narrower scopes
    //5: Create token, try to create refresh token with invalid scopes, fail
    //6: Create token, try to create refresh token that expires after parent token, fail
    //7: Create token for client # 1, try to create a refresh token using client # 2, fail
    //8: Create token, try to create refresh token with invalid refresh value, fail
    //9: Create token, try to create refresh token with invalid parent token value, fail
}
