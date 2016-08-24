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
package org.orcid.integration.blackbox.api.v2.rc3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.common_rc3.Title;
import org.orcid.jaxb.model.error_rc3.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc3.BulkElement;
import org.orcid.jaxb.model.record_rc3.ExternalID;
import org.orcid.jaxb.model.record_rc3.ExternalIDs;
import org.orcid.jaxb.model.record_rc3.Relationship;
import org.orcid.jaxb.model.record_rc3.Work;
import org.orcid.jaxb.model.record_rc3.WorkBulk;
import org.orcid.jaxb.model.record_rc3.WorkTitle;
import org.orcid.jaxb.model.record_rc3.WorkType;
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
public class WorksTest extends BlackBoxBaseRC3 {
    @Resource(name = "memberV2ApiClient_rc3")
    private MemberV2ApiClientImpl memberV2ApiClient;
    
    @Test
    public void testCreateBulkWork() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();        
        WorkBulk bulk = createBulk(10, null);
        ClientResponse postResponse = memberV2ApiClient.createWorks(this.getUser1OrcidId(), bulk, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.OK.getStatusCode(), postResponse.getStatus());  
        
        bulk = postResponse.getEntity(WorkBulk.class); 
        assertNotNull(bulk);
        assertNotNull(bulk.getBulk());
        //All elements might be ok
        for(BulkElement element : bulk.getBulk()) {
            assertTrue(Work.class.isAssignableFrom(element.getClass()));
            Work work = (Work) element;
            //Remove the work
            memberV2ApiClient.deleteWorkXml(this.getUser1OrcidId(), work.getPutCode(), accessToken);
        }
    }
    
    @Test
    public void testCreateBulkWithAllErrors() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        WorkBulk bulk = createBulk(10, "existing-ext-id-" + System.currentTimeMillis());
        ClientResponse postResponse = memberV2ApiClient.createWorks(this.getUser1OrcidId(), bulk, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.OK.getStatusCode(), postResponse.getStatus());  
        
        bulk = postResponse.getEntity(WorkBulk.class); 
        assertNotNull(bulk);
        assertNotNull(bulk.getBulk());
        boolean first = true;
        //All elements might be ok
        for(BulkElement element : bulk.getBulk()) {
            if(first) {
                assertTrue(Work.class.isAssignableFrom(element.getClass()));
                Work work = (Work) element;
                //Remove the work
                memberV2ApiClient.deleteWorkXml(this.getUser1OrcidId(), work.getPutCode(), accessToken);
                first = false;
            } else {
                assertTrue(OrcidError.class.isAssignableFrom(element.getClass()));
                OrcidError error = (OrcidError) element;
                assertEquals(Integer.valueOf(9021), error.getErrorCode());
            }
        }
    }
    
    @Test
    public void testCantAddMoreThan1000WorksAtATime() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        WorkBulk bulk = createBulk(1001, null);
        ClientResponse postResponse = memberV2ApiClient.createWorks(this.getUser1OrcidId(), bulk, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());  
        OrcidError error = postResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9006), error.getErrorCode());
    }
    
    private WorkBulk createBulk(int size, String extId) {
        WorkBulk bulk = new WorkBulk();
        Long time = System.currentTimeMillis();
        for(int i = 0; i < size; i++) {            
            if(extId == null) {
                bulk.getBulk().add(getWork("Work title #" + i + "-" + time, true, null));
            } else {
                bulk.getBulk().add(getWork("Work title #" + i + "-" + time, false, String.valueOf(time)));
            }            
        }
        return bulk;
    }
    
    private Work getWork(String title, boolean randomExtId, String extIdValue) {
        Long time = System.currentTimeMillis();
        Work work = new Work();
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title(title));
        work.setWorkTitle(workTitle);
        work.setWorkType(WorkType.BOOK);
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        if(randomExtId) {
            extId.setValue("work-ext-id-" + (Math.random() * 1000) + "-" + time);
        } else {
            extId.setValue("work-ext-id-" + extIdValue);
        }
        extId.setType("doi");
        ExternalIDs extIds = new ExternalIDs();
        extIds.getExternalIdentifier().add(extId);
        work.setWorkExternalIdentifiers(extIds);        
        return work;
    }
    
    private String getAccessToken() throws InterruptedException, JSONException {                
        return getAccessToken(getScopes(ScopePathType.ACTIVITIES_UPDATE, ScopePathType.ACTIVITIES_READ_LIMITED));
    }    
}
