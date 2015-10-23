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
package org.orcid.integration.blackbox.api;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.memberV2.MemberV2ApiClientImpl;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.BlackBoxBase;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-publicV2-context.xml" })
public class LockedClientsTest extends BlackBoxBase {
    
    protected static Map<String, String> accessTokens = new HashMap<String, String>();
    
    //ADMIN USER DATA
    @Value("${org.orcid.web.adminUser.username}")
    public String adminUserName;
    @Value("${org.orcid.web.adminUser.password}")
    public String adminPassword;
    @Value("${org.orcid.web.testUser1.orcidId}")
    
    //USER DATA
    public String user1OrcidId;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1Username;
    
    //MEMBER DATA
    @Value("${org.orcid.web.locked.member.id}")
    public String memberId;
    //CLIENT DATA
    @Value("${org.orcid.web.locked.member.client.id}")
    public String lockedClientId;
    @Value("${org.orcid.web.locked.member.client.secret}")
    public String lockedClientSecret;
    @Value("${org.orcid.web.locked.member.client.ruri}")
    public String lockedClientRedirectUri;
    
    //USER WITH PUBLIC CLIENT DATA
    @Value("${org.orcid.web.locked.public.id}")
    public String lockedPublicMemberId;
    //PUBLIC CLIENT DATA
    @Value("${org.orcid.web.locked.public.client.id}")
    public String lockedPublicClientId;
    @Value("${org.orcid.web.locked.public.client.secret}")
    public String lockedPublicClientSecret;
    @Value("${org.orcid.web.locked.public.client.ruri}")
    public String lockedPublicClientRedirectUri;
    
    @Resource(name = "memberV2ApiClient_rc2")
    private MemberV2ApiClientImpl memberV2ApiClient;

    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient;
    
    @Test
    public void unlockAndTestClient() throws InterruptedException, JSONException {
        //Check if unlocked
        adminUnlockAccount(adminUserName, adminPassword, memberId);
        //String accessToken = getAccessToken(lockedClientId, lockedClientSecret);
    }
    
    @Test
    public void lockAndTestClient() {
        
    }
    
    @Test
    public void unlockAndTestPublicClient() {
        
    }
    
    @Test
    public void lockAndTestPublicClient() {
        
    }
    
    public String getAccessToken(String clientId, String clientSecret, String redirectUri) throws InterruptedException, JSONException {
        if (accessTokens.containsKey(clientId)) {
            return accessTokens.get(clientId);
        }

        String accessToken = super.getAccessToken(ScopePathType.PERSON_READ_LIMITED.value(), clientId, clientSecret, redirectUri);
        accessTokens.put(clientId, accessToken);
        return accessToken;
    }
}
