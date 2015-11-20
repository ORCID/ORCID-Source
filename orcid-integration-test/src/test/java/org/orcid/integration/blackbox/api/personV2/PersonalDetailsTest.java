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

import org.junit.Test;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.api.memberV2.MemberV2ApiClientImpl;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.BlackBoxBase;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;
import org.springframework.beans.factory.annotation.Value;

import com.sun.jersey.api.client.ClientResponse;

public class PersonalDetailsTest extends BlackBoxBase {
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
    public void testGetWithPublicAPI() {
        ClientResponse getAllResponse = publicV2ApiClient.viewResearcherUrlsXML(user1OrcidId);
        assertNotNull(getAllResponse);
        PersonalDetails personalDetails = getAllResponse.getEntity(PersonalDetails.class);
        assertNotNull(personalDetails);
        assertNotNull(personalDetails.getBiography());
        //TODO
    }
    
}
