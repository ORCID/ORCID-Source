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

import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.common_rc3.Title;
import org.orcid.jaxb.model.message.ScopePathType;
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
        WorkBulk bulk = createBulk();
        Long time1 = System.currentTimeMillis();
        ClientResponse postResponse = memberV2ApiClient.createWorks(this.getUser1OrcidId(), bulk, accessToken);
        Long time2 = System.currentTimeMillis();
        System.out.println("Difference: " + ((time2 - time1)/1000));
        assertNotNull(postResponse);
        assertEquals(Response.Status.OK.getStatusCode(), postResponse.getStatus());
        
        WorkBulk bulk2 = createBulk();
        Long time3 = System.currentTimeMillis();
        ClientResponse postResponse2 = memberV2ApiClient.createWorks(this.getUser1OrcidId(), bulk2, accessToken);
        Long time4 = System.currentTimeMillis();
        System.out.println("Difference: " + ((time4 - time3)/1000));
    }
    
    private WorkBulk createBulk() {
        WorkBulk bulk = new WorkBulk();
        Long time = System.currentTimeMillis();
        for(int i = 0; i < 100; i++) {
            Work work = new Work();
            WorkTitle title = new WorkTitle();
            title.setTitle(new Title("Work title # " + i + "-" + time));
            work.setWorkTitle(title);
            work.setWorkType(WorkType.BOOK);
            ExternalID extId = new ExternalID();
            extId.setRelationship(Relationship.SELF);
            extId.setValue("work-ext-id-" + i + "-" + time);
            extId.setType("artistic-performance");
            ExternalIDs extIds = new ExternalIDs();
            extIds.getExternalIdentifier().add(extId);
            work.setWorkExternalIdentifiers(extIds);
        }
        return bulk;
    }
    
    private String getAccessToken() throws InterruptedException, JSONException {                
        return getAccessToken(getScopes(ScopePathType.ACTIVITIES_UPDATE, ScopePathType.ACTIVITIES_READ_LIMITED));
    }    
}
