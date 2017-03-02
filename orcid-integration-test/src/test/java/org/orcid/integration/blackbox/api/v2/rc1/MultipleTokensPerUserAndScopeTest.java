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
package org.orcid.integration.blackbox.api.v2.rc1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc1.ActivitiesSummary;
import org.orcid.jaxb.model.record_rc1.Work;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class MultipleTokensPerUserAndScopeTest extends BlackBoxBaseRC1 {
    
    @Test
    public void useSameScopesGetDifferentTokensTest() throws InterruptedException, JSONException, URISyntaxException {
        List<String> scopes = getScopes(ScopePathType.READ_LIMITED);
        String token1 = getNonCachedAccessTokens(getUser1UserName(), getUser1Password(), scopes, getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        String token2 = getNonCachedAccessTokens(getUser1UserName(), getUser1Password(), scopes, getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());

        // Check the scopes are not null
        assertNotNull(token1);
        assertNotNull(token2);
        assertFalse(token1.equals(token2));

        // Check token 1 is working
        ClientResponse token1Response = memberV2ApiClient.viewActivities(this.getUser1OrcidId(), token1);
        assertNotNull(token1Response);
        assertEquals(Response.Status.OK.getStatusCode(), token1Response.getStatus());
        ActivitiesSummary token1Activities = token1Response.getEntity(ActivitiesSummary.class);
        assertNotNull(token1Activities);

        // Check token 2 is working
        ClientResponse token2Response = memberV2ApiClient.viewActivities(this.getUser1OrcidId(), token2);
        assertNotNull(token2Response);
        assertEquals(Response.Status.OK.getStatusCode(), token2Response.getStatus());
        ActivitiesSummary token2Activities = token2Response.getEntity(ActivitiesSummary.class);
        assertNotNull(token2Activities);

        assertTrue(token1Activities.equals(token2Activities));
        
        // Check tokens works just for his scopes
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc1/samples/work-2.0_rc1.xml", Work.class);
        workToCreate.setPutCode(null);
        workToCreate.getWorkTitle().getTitle().setContent("Title " + System.currentTimeMillis());
        
        ClientResponse token1AddWorkresponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), workToCreate, token1);
        assertNotNull(token1AddWorkresponse);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), token1AddWorkresponse.getStatus());
                
        ClientResponse token2AddWorkresponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), workToCreate, token2);
        assertNotNull(token2AddWorkresponse);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), token2AddWorkresponse.getStatus());  
        
        // Check a new token with other scope can add the work
        scopes.add(ScopePathType.ACTIVITIES_UPDATE.value());
        String token3 = getNonCachedAccessTokens(getUser1UserName(), getUser1Password(), scopes, getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(token3);
        assertFalse(token1.equals(token3));
        
        // Check token 3 is working
        ClientResponse token3Response = memberV2ApiClient.viewActivities(this.getUser1OrcidId(), token3);
        assertNotNull(token3Response);
        assertEquals(Response.Status.OK.getStatusCode(), token3Response.getStatus());
        ActivitiesSummary token3Activities = token3Response.getEntity(ActivitiesSummary.class);
        assertNotNull(token3Activities);
        
        assertTrue(token1Activities.equals(token3Activities));
        
        //Check that token 3 can add works
        ClientResponse token3AddWorkresponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), workToCreate, token3);
        assertNotNull(token3AddWorkresponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), token3AddWorkresponse.getStatus());
        
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(token3AddWorkresponse.getLocation(), token3);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Work gotWork = getResponse.getEntity(Work.class);
        assertNotNull(gotWork);
        assertNotNull(gotWork.getPutCode());
        
        ClientResponse deleteNewWork = memberV2ApiClient.deleteWorkXml(getUser1OrcidId(), gotWork.getPutCode(), token3);
        assertNotNull(deleteNewWork);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), deleteNewWork.getStatus());
    }          
}
