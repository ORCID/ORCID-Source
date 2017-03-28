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
package org.orcid.integration.blackbox.api.v2.rc4.tests;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.blackbox.api.v2.rc4.BlackBoxBaseRC4;
import org.orcid.integration.blackbox.api.v2.rc4.MemberV2ApiClientImpl;
import org.orcid.jaxb.model.common_rc4.Title;
import org.orcid.jaxb.model.common_rc4.TranslatedTitle;
import org.orcid.jaxb.model.common_rc4.Url;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.error_rc4.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc4.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc4.WorkSummary;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_rc4.BulkElement;
import org.orcid.jaxb.model.record_rc4.CitationType;
import org.orcid.jaxb.model.record_rc4.ExternalID;
import org.orcid.jaxb.model.record_rc4.ExternalIDs;
import org.orcid.jaxb.model.record_rc4.Relationship;
import org.orcid.jaxb.model.record_rc4.Work;
import org.orcid.jaxb.model.record_rc4.WorkBulk;
import org.orcid.jaxb.model.record_rc4.WorkTitle;
import org.orcid.jaxb.model.record_rc4.WorkType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class WorksTest extends BlackBoxBaseRC4 {
    @Resource(name = "memberV2ApiClient_rc4")
    private MemberV2ApiClientImpl memberV2ApiClient;        
    
    @Test
    public void createViewUpdateAndDeleteWork() throws JSONException, InterruptedException, URISyntaxException {
        showMyOrcidPage();
    	changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        long time = System.currentTimeMillis();
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc4/samples/work-2.0_rc4.xml", Work.class);
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
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc4/" + this.getUser1OrcidId() + "/work/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Work gotWork = getResponse.getEntity(Work.class);
        
        assertEquals("common:title", gotWork.getWorkTitle().getTitle().getContent());
        assertEquals("work:citation-value", gotWork.getWorkCitation().getCitation());
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
        assertEquals("work:citation-value", gotAfterUpdateWork.getWorkCitation().getCitation());
        assertEquals(CitationType.FORMATTED_UNSPECIFIED, gotAfterUpdateWork.getWorkCitation().getWorkCitationType());
        
        ClientResponse deleteResponse = memberV2ApiClient.deleteWorkXml(this.getUser1OrcidId(), gotWork.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
        
    @Test
    public void testWorksWithPartOfRelationshipDontGetGrouped () throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        String accessTokenForClient1 = getAccessToken();
        String accessTokenForClient2 = getAccessToken(getUser1OrcidId(), getUser1Password(), getScopes(ScopePathType.ACTIVITIES_UPDATE, ScopePathType.ACTIVITIES_READ_LIMITED), getClient2ClientId(), getClient2ClientSecret(), getClient2RedirectUri());
        
        Work work1 = (Work) unmarshallFromPath("/record_2.0_rc4/samples/work-2.0_rc4.xml", Work.class);
        work1.setPutCode(null);
        work1.setVisibility(Visibility.PUBLIC);
        work1.getExternalIdentifiers().getExternalIdentifier().clear();
        org.orcid.jaxb.model.record_rc4.WorkTitle title1 = new org.orcid.jaxb.model.record_rc4.WorkTitle();
        title1.setTitle(new Title("Work # 1" + time));
        work1.setWorkTitle(title1);
        ExternalID wExtId1 = new ExternalID();
        wExtId1.setValue("Work Id " + time);
        wExtId1.setType(WorkExternalIdentifierType.AGR.value());
        wExtId1.setRelationship(Relationship.SELF);
        wExtId1.setUrl(new Url("http://orcid.org/work#1"));
        work1.getExternalIdentifiers().getExternalIdentifier().clear();
        work1.getExternalIdentifiers().getExternalIdentifier().add(wExtId1);

        Work work2 = (Work) unmarshallFromPath("/record_2.0_rc4/samples/work-2.0_rc4.xml", Work.class);
        work2.setPutCode(null);
        work2.setVisibility(Visibility.PUBLIC);
        org.orcid.jaxb.model.record_rc4.WorkTitle title2 = new org.orcid.jaxb.model.record_rc4.WorkTitle();
        title2.setTitle(new Title("Work # 2" + time));
        work2.setWorkTitle(title2);
        work2.getExternalIdentifiers().getExternalIdentifier().clear();
        ExternalID wExtId2 = new ExternalID();
        wExtId2.setValue("Work Id " + time);
        wExtId2.setType(WorkExternalIdentifierType.AGR.value());
        wExtId2.setRelationship(Relationship.PART_OF);
        wExtId2.setUrl(new Url("http://orcid.org/work#2"));
        work2.getExternalIdentifiers().getExternalIdentifier().clear();
        work2.getExternalIdentifiers().getExternalIdentifier().add(wExtId2);
        
        Work work3 = (Work) unmarshallFromPath("/record_2.0_rc4/samples/work-2.0_rc4.xml", Work.class);
        work3.setPutCode(null);
        work3.setVisibility(Visibility.PUBLIC);
        org.orcid.jaxb.model.record_rc4.WorkTitle title3 = new org.orcid.jaxb.model.record_rc4.WorkTitle();
        title3.setTitle(new Title("Work # 3" + time));
        work3.setWorkTitle(title3);        
        work3.getExternalIdentifiers().getExternalIdentifier().clear();
        ExternalID wExtId3 = new ExternalID();
        wExtId3.setValue("Work Id " + time);
        wExtId3.setType(WorkExternalIdentifierType.AGR.value());
        wExtId3.setRelationship(Relationship.SELF);
        wExtId3.setUrl(new Url("http://orcid.org/work#3"));
        work3.getExternalIdentifiers().getExternalIdentifier().clear();
        work3.getExternalIdentifiers().getExternalIdentifier().add(wExtId3);
        
        //Add the three works
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), work1, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        Long putCode1 = getPutCodeFromResponse(postResponse);
        
        postResponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), work2, accessTokenForClient1);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        Long putCode2 = getPutCodeFromResponse(postResponse);
        
        postResponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), work3, accessTokenForClient2);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        Long putCode3 = getPutCodeFromResponse(postResponse);
        
        ClientResponse activitiesResponse = memberV2ApiClient.viewActivities(this.getUser1OrcidId(), accessTokenForClient1);
        assertEquals(Response.Status.OK.getStatusCode(), activitiesResponse.getStatus());
        ActivitiesSummary activities = activitiesResponse.getEntity(ActivitiesSummary.class);
        assertNotNull(activities);
        assertFalse(activities.getWorks().getWorkGroup().isEmpty());
        
        WorkGroup work1Group = null; 
        WorkGroup work2Group = null;
        WorkGroup work3Group = null;
        
        boolean work1found = false;
        boolean work2found = false;
        boolean work3found = false;
                                
        for(WorkGroup group : activities.getWorks().getWorkGroup()) {
            if(group.getIdentifiers().getExternalIdentifier() == null || group.getIdentifiers().getExternalIdentifier().isEmpty()) {
                for(WorkSummary summary : group.getWorkSummary()) {
                    String title = summary.getTitle().getTitle().getContent(); 
                    if (("Work # 2" + time).equals(title)) {
                        work2found = true;
                        work2Group = group;
                    }
                }
            } else {
                for(ExternalID id : group.getIdentifiers().getExternalIdentifier()) {
                    //If it is the ID is the one we are looking for
                    if(id.getValue().equals("Work Id " + time)) {                    
                        for(WorkSummary summary : group.getWorkSummary()) {
                            String title = summary.getTitle().getTitle().getContent(); 
                            if(("Work # 1" + time).equals(title)) {
                                work1found = true;
                                work1Group = group;
                            } else if(("Work # 3" + time).equals(title)) {
                                work3found = true;
                                work3Group = group;
                            }
                        }
                    }
                }
            }            
        }
        
        assertTrue(work1found);
        assertTrue(work2found);
        assertTrue(work3found);
        //Check that work # 1 and Work # 3 are in the same work
        assertEquals(work1Group, work3Group);
        //Check that work # 2 is not in the same group than group # 1
        assertThat(work2Group, not(work1Group));
        
        //Remove all created works
        ClientResponse deleteResponse = memberV2ApiClient.deleteWorkXml(this.getUser1OrcidId(), putCode1, accessTokenForClient1);
        assertNotNull(deleteResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        
        deleteResponse = memberV2ApiClient.deleteWorkXml(this.getUser1OrcidId(), putCode2, accessTokenForClient1);
        assertNotNull(deleteResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
        
        deleteResponse = memberV2ApiClient.deleteWorkXml(this.getUser1OrcidId(), putCode3, accessTokenForClient2);
        assertNotNull(deleteResponse);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
    
    @Test
    public void testUpdateWorkWithProfileCreationTokenWhenClaimedAndNotSource() throws JSONException, InterruptedException, URISyntaxException {
        long time = System.currentTimeMillis();
        Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc4/samples/work-2.0_rc4.xml", Work.class);
        workToCreate.setPutCode(null);
        workToCreate.setVisibility(Visibility.PUBLIC);
        workToCreate.getExternalIdentifiers().getExternalIdentifier().clear();
        ExternalID wExtId = new ExternalID();
        wExtId.setValue("Work Id " + time);
        wExtId.setType(WorkExternalIdentifierType.AGR.value());
        wExtId.setRelationship(Relationship.SELF);
        workToCreate.getExternalIdentifiers().getExternalIdentifier().add(wExtId);
        String accessToken = getAccessToken();
        ClientResponse postResponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), workToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc4/" + this.getUser1OrcidId() + "/work/\\d+"));
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Work gotWork = getResponse.getEntity(Work.class);
        assertEquals("common:title", gotWork.getWorkTitle().getTitle().getContent());
        gotWork.getWorkTitle().getTitle().setContent("updated title");
        String profileCreateToken = oauthHelper.getClientCredentialsAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), ScopePathType.ORCID_PROFILE_CREATE);
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), profileCreateToken, gotWork);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        ClientResponse getAfterUpdateResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getAfterUpdateResponse.getStatus());
        Work gotAfterUpdateWork = getAfterUpdateResponse.getEntity(Work.class);
        assertEquals("common:title", gotAfterUpdateWork.getWorkTitle().getTitle().getContent());
        ClientResponse deleteResponse = memberV2ApiClient.deleteWorkXml(this.getUser1OrcidId(), gotWork.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
    
    @Test
    public void testCreateBulkWork() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();        
        WorkBulk bulk = createBulk(10, null);
        ClientResponse postResponse = memberV2ApiClient.createWorksJson(this.getUser1OrcidId(), bulk, accessToken);
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
        ClientResponse postResponse = memberV2ApiClient.createWorksJson(this.getUser1OrcidId(), bulk, accessToken);
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
    public void testThreeWithInvalidTypeAllOthersAreFine() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        WorkBulk bulk = createBulk(10, null);
        //Work 3: no type
        Work work3 = (Work)bulk.getBulk().get(3);
        work3.setWorkType(null);
        bulk.getBulk().set(3, work3);
        
        //Work 5: empty title
        Work work5 = (Work)bulk.getBulk().get(5);
        work5.getWorkTitle().getTitle().setContent(null);
        bulk.getBulk().set(5, work5);
        
        //Work 7: translated title language code empty
        Work work7 = (Work)bulk.getBulk().get(7);
        work7.getWorkTitle().getTranslatedTitle().setLanguageCode(null);
        bulk.getBulk().set(7, work7);
        
        ClientResponse postResponse = memberV2ApiClient.createWorksJson(this.getUser1OrcidId(), bulk, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.OK.getStatusCode(), postResponse.getStatus());  
        
        bulk = postResponse.getEntity(WorkBulk.class); 
        assertNotNull(bulk);
        assertNotNull(bulk.getBulk());
        
        for(int i = 0; i < bulk.getBulk().size(); i++) {
            BulkElement element = bulk.getBulk().get(i);
            if(i == 3 || i == 5 || i == 7) {
                assertTrue(OrcidError.class.isAssignableFrom(element.getClass()));
                OrcidError error = (OrcidError) element;
                switch(i) {
                case 3: 
                    assertEquals(Integer.valueOf(9037), error.getErrorCode());
                    assertTrue(error.getDeveloperMessage().startsWith("Invalid work type"));
                    break;
                case 5: 
                    assertEquals(Integer.valueOf(9022), error.getErrorCode());
                    break;
                case 7: 
                    assertEquals(Integer.valueOf(9037), error.getErrorCode());
                    assertTrue(error.getDeveloperMessage().startsWith("Invalid translated title"));
                    break;
                }
            } else {
                assertTrue(Work.class.isAssignableFrom(element.getClass()));
                Work work = (Work) element;
                assertNotNull(work.getPutCode());
                memberV2ApiClient.deleteWorkXml(this.getUser1OrcidId(), work.getPutCode(), accessToken);
            }
        }
    }
    
    @Test
    public void testCantAddMoreThan1000WorksAtATime() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        WorkBulk bulk = createBulk(1001, null);
        ClientResponse postResponse = memberV2ApiClient.createWorksJson(this.getUser1OrcidId(), bulk, accessToken);
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
        Title wTitle = new Title(title);
        workTitle.setTranslatedTitle(new TranslatedTitle(title, "en"));
        workTitle.setTitle(wTitle);
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
