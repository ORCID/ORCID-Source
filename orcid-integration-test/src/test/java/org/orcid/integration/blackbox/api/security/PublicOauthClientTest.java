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
package org.orcid.integration.blackbox.api.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.orcid.api.common.OauthAuthorizationPageHelper;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-publicV2-context.xml" })
public class PublicOauthClientTest extends BlackBoxBaseV2Release {

    @Test
    public void testAuthenticateIsTheOnlyScopeThatWorksForPublicClient() throws JSONException, InterruptedException {
        String clientId = getPublicClientId();
        String clientRedirectUri = getPublicClientRedirectUri();
        String userId = getUser1OrcidId();
        String password = getUser1Password();
        WebDriver webDriver = getWebDriver();
        for (ScopePathType scope : ScopePathType.values()) {
            if (ScopePathType.AUTHENTICATE.equals(scope)) {
                String authCode = getAuthorizationCode(clientId, clientRedirectUri, scope.value(), userId, password, true);
                assertFalse(PojoUtil.isEmpty(authCode));
            } else {
                String authorizationPageUrl = String.format(OauthAuthorizationPageHelper.authorizationScreenUrl, getWebBaseUrl(), clientId, scope.value(), clientRedirectUri);
                webDriver.get(authorizationPageUrl);
                String authCodeUrl = webDriver.getCurrentUrl();
                assertFalse(PojoUtil.isEmpty(authCodeUrl));
                assertTrue(authCodeUrl.contains("error=invalid_scope"));
            }
        }
    }

}
