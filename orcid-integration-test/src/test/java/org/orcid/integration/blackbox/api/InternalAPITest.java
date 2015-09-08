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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-internal-api-context.xml" })
public class InternalAPITest {

    @Value("${org.orcid.web.testClient1.redirectUri}")
    private String redirectUri;
    @Value("${org.orcid.web.testClient1.clientId}")
    public String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    public String client1ClientSecret;
    
    @Resource
    private OauthHelper oauthHelper;

    @Test
    public void testGetTokenForInternalScopes() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", client1ClientId);
        params.add("client_secret", client1ClientSecret);
        params.add("grant_type", "client_credentials");
        params.add("scope", ScopePathType.INTERNAL_PERSON_LAST_MODIFIED.value());
        ClientResponse clientResponse = oauthHelper.getResponse(params, true);
        assertNotNull(clientResponse);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), clientResponse.getStatus());
    }
    
}
