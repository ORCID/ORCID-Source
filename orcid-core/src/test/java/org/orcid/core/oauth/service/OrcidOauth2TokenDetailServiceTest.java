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
package org.orcid.core.oauth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class OrcidOauth2TokenDetailServiceTest extends DBUnitTest {
    private static String CLIENT_ID = "APP-6666666666666666";
    private static String USER_ORCID = "0000-0000-0000-0001";
    
    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/OrcidOauth2AuthorisationDetailsData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/OrcidOauth2AuthorisationDetailsData.xml", "/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }  
    
    @Test
    public void dontGetExpiredTokensTest() {
        //Token # 1: expired
        createToken("expired-1", USER_ORCID, new Date(System.currentTimeMillis() - 1000), "/read-limited", false);
        //Token # 2: /activities/update
        createToken("active-1", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/activities/update", false);
        //Token # 3: disabled
        createToken("disabled-1", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/activities/update", true);
        //Token # 4: /read-limited
        createToken("active-2", USER_ORCID, new Date(System.currentTimeMillis() + 100000), "/read-limited", false);
        //Fetch all active tokens
        List<OrcidOauth2TokenDetail> activeTokens = orcidOauth2TokenDetailService.findByUserName(USER_ORCID);
        assertNotNull(activeTokens);
        assertEquals(2, activeTokens.size());
        assertThat(activeTokens.get(0).getScope(), anyOf(is("/activities/update"), is("/read-limited")));
        assertThat(activeTokens.get(1).getScope(), anyOf(is("/activities/update"), is("/read-limited")));
        
        //Find the id of the token with scope '/activities/update' and disable that token
        Long tokenToDisableId = null;
        for(OrcidOauth2TokenDetail token : activeTokens) {
            if("/activities/update".equals(token.getScope())) {
                tokenToDisableId = token.getId();
                break;
            }
        }
        
        assertNotNull(tokenToDisableId);
        //Disable that access token
        orcidOauth2TokenDetailService.disableAccessToken(tokenToDisableId, USER_ORCID);
        //Fetch the active tokens again, it should contain just one
        activeTokens = orcidOauth2TokenDetailService.findByUserName(USER_ORCID);
        assertNotNull(activeTokens);
        assertEquals(1, activeTokens.size());
        assertEquals("/read-limited", activeTokens.get(0).getScope());
        assertEquals("active-2", activeTokens.get(0).getTokenValue());
    }
    
    private OrcidOauth2TokenDetail createToken(String tokenValue, String userOrcid, Date expirationDate, String scopes, boolean disabled) {
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setApproved(true);
        token.setClientDetailsId(CLIENT_ID);
        token.setDateCreated(new Date());
        token.setLastModified(new Date());
        token.setProfile(new ProfileEntity(userOrcid));
        token.setScope(scopes);
        token.setTokenDisabled(disabled);
        token.setTokenExpiration(expirationDate);
        token.setTokenType("bearer");
        token.setTokenValue(tokenValue);
        orcidOauth2TokenDetailService.saveOrUpdate(token);
        return token;
    }
}
