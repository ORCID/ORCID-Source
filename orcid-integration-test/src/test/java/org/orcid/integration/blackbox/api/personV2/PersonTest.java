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
package org.orcid.integration.blackbox.api.personV2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.api.memberV2.MemberV2ApiClientImpl;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.BlackBoxBase;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.Biography;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-publicV2-context.xml" })
public class PersonTest extends BlackBoxBase {
    protected static Map<String, String> accessTokens = new HashMap<String, String>();

    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    private String webBaseUrl;
    @Value("${org.orcid.web.testClient1.redirectUri}")
    private String client1RedirectUri;
    @Value("${org.orcid.web.testClient1.clientId}")
    public String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    public String client1ClientSecret;            
    @Value("${org.orcid.web.testClient2.clientId}")
    public String client2ClientId;
    @Value("${org.orcid.web.testClient2.clientSecret}")
    public String client2ClientSecret;
    @Value("${org.orcid.web.testClient2.redirectUri}")
    protected String client2RedirectUri;    
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;
    @Value("${org.orcid.web.publicClient1.clientId}")
    public String publicClientId;
    @Value("${org.orcid.web.publicClient1.clientSecret}")
    public String publicClientSecret;
    
    @Resource(name = "memberV2ApiClient_rc2")
    private MemberV2ApiClientImpl memberV2ApiClient;

    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient;

    @Resource
    private OauthHelper oauthHelper;
    
    @Test
    public void testGetBioFromPublicAPI() throws Exception {
        String accessToken = getAccessToken(this.client1ClientId, this.client1ClientSecret, this.client1RedirectUri);
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient.viewBiography(this.user1OrcidId, accessToken);
        assertNotNull(response);
        Biography bio = response.getEntity(Biography.class);
        assertNotNull(bio);
        assertEquals("This is my bio", bio.getContent());
        assertEquals(Visibility.PUBLIC, bio.getVisibility());
    }
    
    @Test
    public void testGetBioFromMemberAPI() throws Exception {
        String accessToken = getAccessToken(this.client1ClientId, this.client1ClientSecret, this.client1RedirectUri);
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient.viewBiography(this.user1OrcidId, accessToken);
        assertNotNull(response);
        Biography bio = response.getEntity(Biography.class);
        assertNotNull(bio);
        assertEquals("This is my bio", bio.getContent());
        assertEquals(Visibility.PUBLIC, bio.getVisibility());
    }
    
    
    
    public String getAccessToken(String clientId, String clientSecret, String redirectUri) throws InterruptedException, JSONException {
        if (accessTokens.containsKey(clientId)) {
            return accessTokens.get(clientId);
        }

        String accessToken = super.getAccessToken(ScopePathType.PERSON_READ_LIMITED.value() + " " + ScopePathType.PERSON_UPDATE.value(), clientId, clientSecret, redirectUri);
        accessTokens.put(clientId, accessToken);
        return accessToken;
    }
}
