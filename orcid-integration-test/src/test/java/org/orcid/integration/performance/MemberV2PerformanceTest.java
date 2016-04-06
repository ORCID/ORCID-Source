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
package org.orcid.integration.performance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.time.StopWatch;
import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.blackbox.api.v2.rc2.BlackBoxBaseRC2;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc2.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc2.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc2.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc2.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc2.WorkSummary;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.ExternalIDType;
import org.orcid.jaxb.model.record_rc2.Relationship;
import org.orcid.jaxb.model.record_rc2.Work;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Will Simpson
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-memberV2-context.xml" })
public class MemberV2PerformanceTest extends BlackBoxBaseRC2 {

    protected static Map<String, String> accessTokens = new HashMap<String, String>();

    @BeforeClass
    public static void beforeClass() {
        revokeApplicationsAccess();
    }

    @AfterClass
    public static void afterClass() {
        revokeApplicationsAccess();
    }

    @After
    public void after() throws JSONException, InterruptedException, URISyntaxException {
        cleanActivities();
    }

    @Test
    public void createManyWorks() throws JSONException, InterruptedException, URISyntaxException {
        cleanActivities();
        int numWorks = 1000;
        // Amount of linear increase allowed
        float scalingFactor = 1.5f;
        int numInitialSample = numWorks / 10;
        long initialSampleTime = 0;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 1; i <= numWorks; i++) {
            StopWatch singleWorkStopWatch = new StopWatch();
            singleWorkStopWatch.start();
            long time = System.currentTimeMillis();
            Work workToCreate = (Work) unmarshallFromPath("/record_2.0_rc2/samples/work-2.0_rc2.xml", Work.class);
            workToCreate.setPutCode(null);
            workToCreate.getExternalIdentifiers().getExternalIdentifier().clear();
            ExternalID wExtId = new ExternalID();
            wExtId.setValue("Work Id " + i + " " + time);
            wExtId.setType(ExternalIDType.AGR.value());
            wExtId.setRelationship(Relationship.SELF);
            workToCreate.getExternalIdentifiers().getExternalIdentifier().add(wExtId);
            String accessToken = getAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), this.getClient1RedirectUri());

            ClientResponse postResponse = memberV2ApiClient.createWorkXml(this.getUser1OrcidId(), workToCreate, accessToken);
            stopWatch.split();
            long splitTime = stopWatch.getSplitTime();
            System.out.println("Split time: " + splitTime);
            if (i == numInitialSample) {
                initialSampleTime = splitTime;
            } else if (i > numInitialSample) {
                float maxTime = (initialSampleTime / numInitialSample) * scalingFactor * i;
                System.out.println("Max time: " + maxTime);
                assertTrue("Split time = " + splitTime + ", but max allowed time = " + maxTime + ", when num added = " + i, splitTime <= maxTime);
            }
            assertNotNull(postResponse);
            assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
            String locationPath = postResponse.getLocation().getPath();
            assertTrue("Location header path should match pattern, but was " + locationPath,
                    locationPath.matches(".*/v2.0_rc2/" + this.getUser1OrcidId() + "/work/\\d+"));
            ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
            assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
            Work gotWork = getResponse.getEntity(Work.class);
            assertEquals("common:title", gotWork.getWorkTitle().getTitle().getContent());
            System.out.println("Time for single work = " + singleWorkStopWatch);
        }
    }

    private void cleanActivities(String token) throws JSONException, InterruptedException, URISyntaxException {
        // Remove all activities
        ClientResponse activitiesResponse = memberV2ApiClient.viewActivities(this.getUser1OrcidId(), token);
        assertNotNull(activitiesResponse);
        ActivitiesSummary summary = activitiesResponse.getEntity(ActivitiesSummary.class);
        assertNotNull(summary);
        if (summary.getEducations() != null && !summary.getEducations().getSummaries().isEmpty()) {
            for (EducationSummary education : summary.getEducations().getSummaries()) {
                memberV2ApiClient.deleteEducationXml(this.getUser1OrcidId(), education.getPutCode(), token);
            }
        }

        if (summary.getEmployments() != null && !summary.getEmployments().getSummaries().isEmpty()) {
            for (EmploymentSummary employment : summary.getEmployments().getSummaries()) {
                memberV2ApiClient.deleteEmploymentXml(this.getUser1OrcidId(), employment.getPutCode(), token);
            }
        }

        if (summary.getFundings() != null && !summary.getFundings().getFundingGroup().isEmpty()) {
            for (FundingGroup group : summary.getFundings().getFundingGroup()) {
                for (FundingSummary funding : group.getFundingSummary()) {
                    memberV2ApiClient.deleteFundingXml(this.getUser1OrcidId(), funding.getPutCode(), token);
                }
            }
        }

        if (summary.getWorks() != null && !summary.getWorks().getWorkGroup().isEmpty()) {
            for (WorkGroup group : summary.getWorks().getWorkGroup()) {
                for (WorkSummary work : group.getWorkSummary()) {
                    memberV2ApiClient.deleteWorkXml(this.getUser1OrcidId(), work.getPutCode(), token);
                }
            }
        }

        if (summary.getPeerReviews() != null && !summary.getPeerReviews().getPeerReviewGroup().isEmpty()) {
            for (PeerReviewGroup group : summary.getPeerReviews().getPeerReviewGroup()) {
                for (PeerReviewSummary peerReview : group.getPeerReviewSummary()) {
                    memberV2ApiClient.deletePeerReviewXml(this.getUser1OrcidId(), peerReview.getPutCode(), token);
                }
            }
        }
    }

    private String getAccessToken(String clientId, String clientSecret, String clientRedirectUri) throws InterruptedException, JSONException {
        if (accessTokens.containsKey(clientId)) {
            return accessTokens.get(clientId);
        }

        String accessToken = super.getAccessToken(ScopePathType.ACTIVITIES_UPDATE.value() + " " + ScopePathType.ACTIVITIES_READ_LIMITED.value(), clientId, clientSecret,
                clientRedirectUri);
        accessTokens.put(clientId, accessToken);
        return accessToken;
    }

    private void cleanActivities() throws JSONException, InterruptedException, URISyntaxException {
        for (String token : accessTokens.values()) {
            cleanActivities(token);
        }
    }
}
