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
package org.orcid.integration.api.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.orcid.api.common.WebDriverHelper;
import org.orcid.integration.api.helper.InitializeDataHelper;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.jaxb.model.clientgroup.GroupType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.pojo.ajaxForm.Group;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-oauth-orcid-api-client-context.xml" })
public class PerActivityAPIScope_Activities_Update_Test extends InitializeDataHelper {

    private String email;
    private String password;
    private OrcidProfile user;
    private Group member;
    private OrcidClient client;

    @Resource
    private OauthHelper oauthHelper;
    
    @Resource
    OrcidOauth2TokenDetailDao oauth2TokenDetailDao;

    @Before
    public void init() throws Exception {
        WebDriver webDriver = new FirefoxDriver();
        WebDriverHelper webDriverHelper = new WebDriverHelper(webDriver, webBaseUrl, getRedirectUri());
        oauthHelper.setWebDriverHelper(webDriverHelper);
        email = System.currentTimeMillis() + "@orcid-integration-test.com";
        password = String.valueOf(System.currentTimeMillis());
        user = createProfile(email, password);
        member = createMember(GroupType.BASIC);
        client = createClient(member.getGroupOrcid().getValue());
    }

    @After
    public void after() throws Exception {
        deleteClient(client.getClientId());
        deleteProfile(member.getGroupOrcid().getValue());
        deleteProfile(user.getOrcidId());
    }

    @Test
    public void createTokenTest() throws InterruptedException, JSONException {
        String accessToken = oauthHelper.obtainAccessToken(client.getClientId(), "/activities/update", email, password, getRedirectUri());
        assertFalse(PojoUtil.isEmpty(accessToken));
        OrcidOauth2TokenDetail tokenEntity = oauth2TokenDetailDao.findByTokenValue(accessToken);
        assertNotNull(tokenEntity);
        assertTrue(tokenEntity.isPersistent());
    }

}
