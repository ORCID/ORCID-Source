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
package org.orcid.integration.blackbox.api.v2.rc2.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.blackbox.api.v2.rc2.BlackBoxBaseRC2;
import org.orcid.integration.blackbox.api.v2.rc2.MemberV2ApiClientImpl;
import org.orcid.jaxb.model.common_rc2.Url;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.error_rc2.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.CitationType;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.Relationship;
import org.orcid.jaxb.model.record_rc2.Work;
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
public class WorksTest extends BlackBoxBaseRC2 {
    @Resource(name = "memberV2ApiClient_rc2")
    private MemberV2ApiClientImpl memberV2ApiClient;
    
    @Test
    public void createViewUpdateAndDeleteWork() throws JSONException, InterruptedException, URISyntaxException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        long time = System.currentTimeMillis();
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc2/samples/work-2.0_rc2.xml", Work.class);
        workToCreate.setPutCode(null);
        workToCreate.getExternalIdentifiers().getExternalIdentifier().clear();
        
        ExternalID wExtId = new ExternalID();
        wExtId.setValue("Work Id " + time);
        wExtId.setType("agr");
        wExtId.setRelationship(Relationship.SELF);
        wExtId.setUrl(new Url("http://test.orcid.org/" + time));
        
        workToCreate.getExternalIdentifiers().getExternalIdentifier().add(wExtId);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), workToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc2/" + this.getUser1OrcidId() + "/work/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Work gotWork = getResponse.getEntity(Work.class);
        
        assertEquals("common:title", gotWork.getWorkTitle().getTitle().getContent());
        assertEquals("work:citation", gotWork.getWorkCitation().getCitation());
        assertEquals(CitationType.FORMATTED_UNSPECIFIED, gotWork.getWorkCitation().getWorkCitationType());
        
        gotWork.getWorkTitle().getTitle().setContent("updated title");
        
        //Save the original visibility
        Visibility originalVisibility = gotWork.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE.equals(originalVisibility) ? Visibility.LIMITED : Visibility.PRIVATE;
        
        //Verify you cant update the visibility
        gotWork.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotWork);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        gotWork.setVisibility(originalVisibility);
        putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotWork);
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Work gotAfterUpdateWork = getAfterUpdateResponse.getEntity(Work.class);
        
        assertEquals("updated title", gotAfterUpdateWork.getWorkTitle().getTitle().getContent());
        assertEquals("work:citation", gotAfterUpdateWork.getWorkCitation().getCitation());
        assertEquals(CitationType.FORMATTED_UNSPECIFIED, gotAfterUpdateWork.getWorkCitation().getWorkCitationType());
        
        ClientResponse deleteResponse = memberV2ApiClient.deleteWorkXml(this.getUser1OrcidId(), gotWork.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    private String getAccessToken() throws InterruptedException, JSONException {                
        return getAccessToken(getScopes(ScopePathType.ACTIVITIES_UPDATE, ScopePathType.ACTIVITIES_READ_LIMITED));
    }    
}
