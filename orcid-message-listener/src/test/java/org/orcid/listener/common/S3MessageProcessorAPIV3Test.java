package org.orcid.listener.common;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.record.History;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.listener.orcid.Orcid30Manager;
import org.orcid.listener.persistence.managers.ActivitiesStatusManager;
import org.orcid.listener.persistence.managers.Api30RecordStatusManager;
import org.orcid.listener.persistence.managers.RecordStatusManager;
import org.orcid.listener.persistence.util.APIVersion;
import org.orcid.listener.persistence.util.ActivityType;
import org.orcid.listener.persistence.util.AvailableBroker;
import org.orcid.listener.s3.S3Manager;
import org.orcid.listener.s3.S3MessageProcessorAPIV3;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.DateUtils;
import org.orcid.utils.listener.LastModifiedMessage;
import org.orcid.utils.listener.MessageConstants;
import org.springframework.test.context.ContextConfiguration;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-message-listener-test-context.xml" })
public class S3MessageProcessorAPIV3Test {

    private final String orcid = "0000-0000-0000-0000";

    private final Date dateNow = new Date(System.currentTimeMillis());    
    private final Date dateAfter = new Date(System.currentTimeMillis() + 1000);
    private final XMLGregorianCalendar after = DateUtils.convertToXMLGregorianCalendar(dateAfter);

    @Resource
    private S3MessageProcessorAPIV3 processor;

    @Mock
    private Orcid30Manager mock_orcid30ApiClient;

    @Mock
    private S3Manager mock_s3Manager;

    @Mock
    private Api30RecordStatusManager mock_api30RecordStatusManager;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(processor, "isV3IndexerEnabled", true);
        TargetProxyHelper.injectIntoProxy(processor, "orcid30ApiClient", mock_orcid30ApiClient);
        TargetProxyHelper.injectIntoProxy(processor, "api30RecordStatusManager", mock_api30RecordStatusManager);        
        TargetProxyHelper.injectIntoProxy(processor, "s3Manager", mock_s3Manager);
    }

    @Test
    public void v30RecordDeprecatedExceptionTest() throws Exception {
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenThrow(new DeprecatedRecordException(new OrcidError()));
        processSummaries(orcid);
        verify(mock_s3Manager, times(1)).clearV3Activities(eq(orcid));
        verify(mock_s3Manager, times(1)).uploadV3OrcidError(eq(orcid), any(OrcidError.class));
        final ArgumentCaptor<ArrayList> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());   
        
        final ArrayList argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void v30RecordLockedExceptionTest() throws Exception {
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenThrow(new LockedRecordException(new OrcidError()));
        processSummaries(orcid);
        verify(mock_s3Manager, times(1)).clearV3Activities(eq(orcid));
        verify(mock_s3Manager, times(1)).uploadV3OrcidError(eq(orcid), any(OrcidError.class));
        final ArgumentCaptor<ArrayList> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());   
        
        final ArrayList argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void recordSummary_AmazonClientExceptionTest() throws Exception {
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV2RecordSummary(eq(orcid), any(Record.class));

        try {
            processSummaries(orcid);
        } catch (AmazonClientException ace) {

        } catch (Exception e) {
            fail();
        }
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), any());
        verify(mock_s3Manager, times(0)).uploadV2OrcidError(any(), any());
        verify(mock_recordStatusManager, times(0)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
        verify(mock_recordStatusManager, times(1)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
    }

    @Test
    public void recordSummary_JAXBExceptionTest() throws Exception {
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        doThrow(new JAXBException("error")).when(mock_s3Manager).uploadV2RecordSummary(eq(orcid), any(Record.class));

        try {
            processSummaries(orcid);
        } catch (AmazonClientException ace) {
            fail();
        } catch (Exception e) {

        }

        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), any());
        verify(mock_s3Manager, times(0)).uploadV2OrcidError(any(), any());
        verify(mock_recordStatusManager, times(0)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
        verify(mock_recordStatusManager, times(1)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
    }

    @Test
    public void recordStatusMarkAsSentFor20Test() throws Exception {
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        processSummaries(orcid);
        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any(Record.class));
        verify(mock_recordStatusManager, times(1)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
    }

    @Test
    public void activities_DeprecatedExceptionTest() throws Exception {
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenThrow(new DeprecatedRecordException(new OrcidError()));
        processActivities(orcid);
        verify(mock_s3Manager, times(1)).clearV2Activities(eq(orcid));
        verify(mock_activitiesStatusManager, times(1)).markAllAsSent(eq(orcid));
    }

    @Test
    public void activities_LockedExceptionTest() throws Exception {
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenThrow(new LockedRecordException(new OrcidError()));
        processActivities(orcid);
        verify(mock_s3Manager, times(1)).clearV2Activities(eq(orcid));
        verify(mock_activitiesStatusManager, times(1)).markAllAsSent(eq(orcid));
    }

    @Test
    public void activities_AllSentTest() throws Exception {
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(any(), eq(APIVersion.V2))).thenReturn(getEmptyMapOfActivities());
        when(mock_orcid20ApiClient.fetchAffiliation(eq(orcid), eq(0L), eq(AffiliationType.EDUCATION))).thenReturn(getEducation());
        when(mock_orcid20ApiClient.fetchAffiliation(eq(orcid), eq(0L), eq(AffiliationType.EMPLOYMENT))).thenReturn(getEmployment());
        when(mock_orcid20ApiClient.fetchFunding(eq(orcid), eq(0L))).thenReturn(getFunding());
        when(mock_orcid20ApiClient.fetchPeerReview(eq(orcid), eq(0L))).thenReturn(getPeerReview());
        when(mock_orcid20ApiClient.fetchWork(eq(orcid), eq(0L))).thenReturn(getWork());

        processActivities(orcid);

        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEducation()));
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEmployment()));
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getFunding()));
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getPeerReview()));
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getWork()));
        verify(mock_s3Manager, times(0)).removeV2Activity(any(), any(), any());
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.WORKS));
    }

    @Test
    public void activities_EducationsFailTest() throws Exception {
        // Leave only educations
        Record r = getRecord();
        r.getActivitiesSummary().getEmployments().getSummaries().clear();
        r.getActivitiesSummary().getFundings().getFundingGroup().clear();
        r.getActivitiesSummary().getPeerReviews().getPeerReviewGroup().clear();
        r.getActivitiesSummary().getWorks().getWorkGroup().clear();

        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(any(), eq(APIVersion.V2))).thenReturn(getEmptyMapOfActivities());
        when(mock_orcid20ApiClient.fetchAffiliation(eq(orcid), eq(0L), eq(AffiliationType.EDUCATION))).thenReturn(getEducation());
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEducation()));

        try {
            processActivities(orcid);
        } catch (Exception e) {

        }

        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEducation()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEmployment()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getFunding()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getPeerReview()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getWork()));
        verify(mock_s3Manager, times(0)).removeV2Activity(any(), any(), any());
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_activitiesStatusManager, times(0)).markAsSent(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_activitiesStatusManager, times(1)).markAsFailed(eq(orcid), eq(ActivityType.EDUCATIONS));
    }

    @Test
    public void activities_EmploymentsFailTest() throws Exception {
        // Leave only employments
        Record r = getRecord();
        r.getActivitiesSummary().getEducations().getSummaries().clear();
        r.getActivitiesSummary().getFundings().getFundingGroup().clear();
        r.getActivitiesSummary().getPeerReviews().getPeerReviewGroup().clear();
        r.getActivitiesSummary().getWorks().getWorkGroup().clear();

        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(any(), eq(APIVersion.V2))).thenReturn(getEmptyMapOfActivities());
        when(mock_orcid20ApiClient.fetchAffiliation(eq(orcid), eq(0L), eq(AffiliationType.EMPLOYMENT))).thenReturn(getEmployment());
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEmployment()));

        try {
            processActivities(orcid);
        } catch (Exception e) {

        }

        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEducation()));
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEmployment()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getFunding()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getPeerReview()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getWork()));
        verify(mock_s3Manager, times(0)).removeV2Activity(any(), any(), any());
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_activitiesStatusManager, times(0)).markAsSent(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_activitiesStatusManager, times(1)).markAsFailed(eq(orcid), eq(ActivityType.EMPLOYMENTS));
    }

    @Test
    public void activities_FundingsFailTest() throws Exception {
        // Leave only fundings
        Record r = getRecord();
        r.getActivitiesSummary().getEducations().getSummaries().clear();
        r.getActivitiesSummary().getEmployments().getSummaries().clear();
        r.getActivitiesSummary().getPeerReviews().getPeerReviewGroup().clear();
        r.getActivitiesSummary().getWorks().getWorkGroup().clear();

        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(any(), eq(APIVersion.V2))).thenReturn(getEmptyMapOfActivities());
        when(mock_orcid20ApiClient.fetchFunding(eq(orcid), eq(0L))).thenReturn(getFunding());
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getFunding()));

        try {
            processActivities(orcid);
        } catch (Exception e) {

        }

        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEducation()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEmployment()));
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getFunding()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getPeerReview()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getWork()));
        verify(mock_s3Manager, times(0)).removeV2Activity(any(), any(), any());
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_activitiesStatusManager, times(0)).markAsSent(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_activitiesStatusManager, times(1)).markAsFailed(eq(orcid), eq(ActivityType.FUNDINGS));
    }

    @Test
    public void activities_PeerReviewsFailTest() throws Exception {
        // Leave only peer reviews
        Record r = getRecord();
        r.getActivitiesSummary().getEducations().getSummaries().clear();
        r.getActivitiesSummary().getEmployments().getSummaries().clear();
        r.getActivitiesSummary().getFundings().getFundingGroup().clear();
        r.getActivitiesSummary().getWorks().getWorkGroup().clear();

        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(any(), eq(APIVersion.V2))).thenReturn(getEmptyMapOfActivities());
        when(mock_orcid20ApiClient.fetchPeerReview(eq(orcid), eq(0L))).thenReturn(getPeerReview());
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getPeerReview()));

        try {
            processActivities(orcid);
        } catch (Exception e) {

        }

        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEducation()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEmployment()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getFunding()));
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getPeerReview()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getWork()));
        verify(mock_s3Manager, times(0)).removeV2Activity(any(), any(), any());
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_activitiesStatusManager, times(0)).markAsSent(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_activitiesStatusManager, times(1)).markAsFailed(eq(orcid), eq(ActivityType.PEER_REVIEWS));
    }

    @Test
    public void activities_WorksFailTest() throws Exception {
        // Leave only works
        Record r = getRecord();
        r.getActivitiesSummary().getEducations().getSummaries().clear();
        r.getActivitiesSummary().getEmployments().getSummaries().clear();
        r.getActivitiesSummary().getFundings().getFundingGroup().clear();
        r.getActivitiesSummary().getPeerReviews().getPeerReviewGroup().clear();

        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(any(), eq(APIVersion.V2))).thenReturn(getEmptyMapOfActivities());
        when(mock_orcid20ApiClient.fetchWork(eq(orcid), eq(0L))).thenReturn(getWork());
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getWork()));

        try {
            processActivities(orcid);
        } catch (Exception e) {

        }

        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEducation()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEmployment()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getFunding()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getPeerReview()));
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getWork()));
        verify(mock_s3Manager, times(0)).removeV2Activity(any(), any(), any());
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_activitiesStatusManager, times(0)).markAsSent(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_activitiesStatusManager, times(1)).markAsFailed(eq(orcid), eq(ActivityType.WORKS));
    }

    @Test
    public void deleteOneEducationTest() throws Exception {
        // Leave only educations
        Record r = getRecord();
        r.getActivitiesSummary().getEmployments().getSummaries().clear();
        r.getActivitiesSummary().getFundings().getFundingGroup().clear();
        r.getActivitiesSummary().getPeerReviews().getPeerReviewGroup().clear();
        r.getActivitiesSummary().getWorks().getWorkGroup().clear();

        Map<ActivityType, Map<String, S3ObjectSummary>> map = getEmptyMapOfActivities();
        map.get(ActivityType.EDUCATIONS).put(String.valueOf(1L), new S3ObjectSummary());

        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(any(), eq(APIVersion.V2))).thenReturn(map);
        when(mock_orcid20ApiClient.fetchAffiliation(eq(orcid), eq(0L), eq(AffiliationType.EDUCATION))).thenReturn(getEducation());

        try {
            processActivities(orcid);
        } catch (Exception e) {

        }

        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEducation()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEmployment()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getFunding()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getPeerReview()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getWork()));
        verify(mock_s3Manager, times(1)).removeV2Activity(eq(orcid), eq(String.valueOf(1L)), eq(ActivityType.EDUCATIONS));
        verify(mock_s3Manager, times(0)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.WORKS));
    }

    @Test
    public void deleteOneEmploymentTest() throws Exception {
        // Leave only employments
        Record r = getRecord();
        r.getActivitiesSummary().getEducations().getSummaries().clear();
        r.getActivitiesSummary().getFundings().getFundingGroup().clear();
        r.getActivitiesSummary().getPeerReviews().getPeerReviewGroup().clear();
        r.getActivitiesSummary().getWorks().getWorkGroup().clear();

        Map<ActivityType, Map<String, S3ObjectSummary>> map = getEmptyMapOfActivities();
        map.get(ActivityType.EMPLOYMENTS).put(String.valueOf(1L), new S3ObjectSummary());

        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(any(), eq(APIVersion.V2))).thenReturn(map);
        when(mock_orcid20ApiClient.fetchAffiliation(eq(orcid), eq(0L), eq(AffiliationType.EMPLOYMENT))).thenReturn(getEmployment());

        try {
            processActivities(orcid);
        } catch (Exception e) {

        }

        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEducation()));
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEmployment()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getFunding()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getPeerReview()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getWork()));
        verify(mock_s3Manager, times(1)).removeV2Activity(eq(orcid), eq(String.valueOf(1L)), eq(ActivityType.EMPLOYMENTS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_s3Manager, times(0)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.WORKS));
    }

    @Test
    public void deleteOneFundingTest() throws Exception {
        // Leave only fundings
        Record r = getRecord();
        r.getActivitiesSummary().getEducations().getSummaries().clear();
        r.getActivitiesSummary().getEmployments().getSummaries().clear();
        r.getActivitiesSummary().getPeerReviews().getPeerReviewGroup().clear();
        r.getActivitiesSummary().getWorks().getWorkGroup().clear();

        Map<ActivityType, Map<String, S3ObjectSummary>> map = getEmptyMapOfActivities();
        map.get(ActivityType.FUNDINGS).put(String.valueOf(1L), new S3ObjectSummary());

        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(any(), eq(APIVersion.V2))).thenReturn(map);
        when(mock_orcid20ApiClient.fetchFunding(eq(orcid), eq(0L))).thenReturn(getFunding());

        try {
            processActivities(orcid);
        } catch (Exception e) {

        }

        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEducation()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEmployment()));
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getFunding()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getPeerReview()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getWork()));
        verify(mock_s3Manager, times(1)).removeV2Activity(eq(orcid), eq(String.valueOf(1L)), eq(ActivityType.FUNDINGS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_s3Manager, times(0)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.WORKS));
    }

    @Test
    public void deleteOnePeerReviewTest() throws Exception {
        // Leave only peer reviews
        Record r = getRecord();
        r.getActivitiesSummary().getEducations().getSummaries().clear();
        r.getActivitiesSummary().getEmployments().getSummaries().clear();
        r.getActivitiesSummary().getFundings().getFundingGroup().clear();
        r.getActivitiesSummary().getWorks().getWorkGroup().clear();

        Map<ActivityType, Map<String, S3ObjectSummary>> map = getEmptyMapOfActivities();
        map.get(ActivityType.PEER_REVIEWS).put(String.valueOf(1L), new S3ObjectSummary());

        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(any(), eq(APIVersion.V2))).thenReturn(map);
        when(mock_orcid20ApiClient.fetchPeerReview(eq(orcid), eq(0L))).thenReturn(getPeerReview());

        try {
            processActivities(orcid);
        } catch (Exception e) {

        }

        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEducation()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEmployment()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getFunding()));
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getPeerReview()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getWork()));
        verify(mock_s3Manager, times(1)).removeV2Activity(eq(orcid), eq(String.valueOf(1L)), eq(ActivityType.PEER_REVIEWS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_s3Manager, times(0)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.WORKS));
    }

    @Test
    public void deleteOneWorkTest() throws Exception {
        // Leave only works
        Record r = getRecord();
        r.getActivitiesSummary().getEducations().getSummaries().clear();
        r.getActivitiesSummary().getEmployments().getSummaries().clear();
        r.getActivitiesSummary().getFundings().getFundingGroup().clear();
        r.getActivitiesSummary().getPeerReviews().getPeerReviewGroup().clear();

        Map<ActivityType, Map<String, S3ObjectSummary>> map = getEmptyMapOfActivities();
        map.get(ActivityType.WORKS).put(String.valueOf(1L), new S3ObjectSummary());

        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(any(), eq(APIVersion.V2))).thenReturn(map);
        when(mock_orcid20ApiClient.fetchWork(eq(orcid), eq(0L))).thenReturn(getWork());

        try {
            processActivities(orcid);
        } catch (Exception e) {

        }

        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEducation()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEmployment()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getFunding()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getPeerReview()));
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getWork()));
        verify(mock_s3Manager, times(1)).removeV2Activity(eq(orcid), eq(String.valueOf(1L)), eq(ActivityType.WORKS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_s3Manager, times(0)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.WORKS));
    }

    @Test
    public void uploadNothingTest() throws Exception {
        S3ObjectSummary s3Object = new S3ObjectSummary();
        s3Object.setLastModified(dateAfter);
        
        Map<ActivityType, Map<String, S3ObjectSummary>> map = getEmptyMapOfActivities();
        map.get(ActivityType.EDUCATIONS).put(String.valueOf(0L), s3Object);
        map.get(ActivityType.EMPLOYMENTS).put(String.valueOf(0L), s3Object);
        map.get(ActivityType.FUNDINGS).put(String.valueOf(0L), s3Object);
        map.get(ActivityType.PEER_REVIEWS).put(String.valueOf(0L), s3Object);
        map.get(ActivityType.WORKS).put(String.valueOf(0L), s3Object);
        
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(any(), eq(APIVersion.V2))).thenReturn(map);
        when(mock_orcid20ApiClient.fetchAffiliation(eq(orcid), eq(0L), eq(AffiliationType.EDUCATION))).thenReturn(getEducation());
        when(mock_orcid20ApiClient.fetchAffiliation(eq(orcid), eq(0L), eq(AffiliationType.EMPLOYMENT))).thenReturn(getEmployment());
        when(mock_orcid20ApiClient.fetchFunding(eq(orcid), eq(0L))).thenReturn(getFunding());
        when(mock_orcid20ApiClient.fetchPeerReview(eq(orcid), eq(0L))).thenReturn(getPeerReview());
        when(mock_orcid20ApiClient.fetchWork(eq(orcid), eq(0L))).thenReturn(getWork());
        
        processActivities(orcid);

        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEducation()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEmployment()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getFunding()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getPeerReview()));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getWork()));
        verify(mock_s3Manager, times(0)).removeV2Activity(any(), any(), any());
        verify(mock_s3Manager, times(0)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_s3Manager, times(0)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_s3Manager, times(0)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_s3Manager, times(0)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_s3Manager, times(0)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.WORKS));
    }

    @Test
    public void uploadAllDueLastModifiedChangeTest() throws Exception {
        S3ObjectSummary s3Object = new S3ObjectSummary();
        s3Object.setLastModified(dateNow);
        
        Map<ActivityType, Map<String, S3ObjectSummary>> map = getEmptyMapOfActivities();
        map.get(ActivityType.EDUCATIONS).put(String.valueOf(0L), s3Object);
        map.get(ActivityType.EMPLOYMENTS).put(String.valueOf(0L), s3Object);
        map.get(ActivityType.FUNDINGS).put(String.valueOf(0L), s3Object);
        map.get(ActivityType.PEER_REVIEWS).put(String.valueOf(0L), s3Object);
        map.get(ActivityType.WORKS).put(String.valueOf(0L), s3Object);
        
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(any(), eq(APIVersion.V2))).thenReturn(map);
        when(mock_orcid20ApiClient.fetchAffiliation(eq(orcid), eq(0L), eq(AffiliationType.EDUCATION))).thenReturn(getEducation());
        when(mock_orcid20ApiClient.fetchAffiliation(eq(orcid), eq(0L), eq(AffiliationType.EMPLOYMENT))).thenReturn(getEmployment());
        when(mock_orcid20ApiClient.fetchFunding(eq(orcid), eq(0L))).thenReturn(getFunding());
        when(mock_orcid20ApiClient.fetchPeerReview(eq(orcid), eq(0L))).thenReturn(getPeerReview());
        when(mock_orcid20ApiClient.fetchWork(eq(orcid), eq(0L))).thenReturn(getWork());
        
        processActivities(orcid);

        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEducation()));
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEmployment()));
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getFunding()));
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getPeerReview()));
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getWork()));
        verify(mock_s3Manager, times(0)).removeV2Activity(any(), any(), any());
        verify(mock_s3Manager, times(0)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_s3Manager, times(0)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_s3Manager, times(0)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_s3Manager, times(0)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_s3Manager, times(0)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_activitiesStatusManager, times(1)).markAsSent(eq(orcid), eq(ActivityType.WORKS));
    }
    
    private void processSummaries(String orcid) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        String date = String.valueOf(System.currentTimeMillis());
        map.put(MessageConstants.ORCID.value, orcid);
        map.put(MessageConstants.DATE.value, date);
        map.put(MessageConstants.TYPE.value, MessageConstants.TYPE_LAST_UPDATED.value);
        LastModifiedMessage message = new LastModifiedMessage(map);
        processor.update(message);
    }

    private void processActivities(String orcid) {
        Map<String, String> map = new HashMap<String, String>();
        String date = String.valueOf(System.currentTimeMillis());
        map.put(MessageConstants.ORCID.value, orcid);
        map.put(MessageConstants.DATE.value, date);
        map.put(MessageConstants.TYPE.value, MessageConstants.TYPE_LAST_UPDATED.value);
        LastModifiedMessage message = new LastModifiedMessage(map);
        processor.update20Activities(message);
    }

    private Record getRecord() {
        Record claimedRecord = new Record();
        History history = new History();
        history.setClaimed(true);
        claimedRecord.setHistory(history);
        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(new Educations(getEducationGroups()));        
        as.setEmployments(new Employments());
        as.getEmployments().getSummaries().add(getEmploymentSummary());
        as.setFundings(new Fundings());
        as.getFundings().getFundingGroup().add(getFundingGroup());
        as.setPeerReviews(new PeerReviews());
        as.getPeerReviews().getPeerReviewGroup().add(getPeerReviewGroup());
        as.setWorks(new Works());
        as.getWorks().getWorkGroup().add(getWorkGroup());
        claimedRecord.setActivitiesSummary(as);
        return claimedRecord;
    }

    private List<AffiliationGroup<DistinctionSummary>> getDistinctionGroups() {
        List<AffiliationGroup<DistinctionSummary>> list = new ArrayList<>();
        DistinctionSummary e = new DistinctionSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        AffiliationGroup group = new AffiliationGroup();
        group.getActivities().add(e);
        return list;
    }
    
    private List<AffiliationGroup<EducationSummary>> getEducationGroups() {
        List<AffiliationGroup<EducationSummary>> list = new ArrayList<>();
        EducationSummary e = new EducationSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        AffiliationGroup group = new AffiliationGroup();
        group.getActivities().add(e);
        return list;
    }

    private List<AffiliationGroup<EmploymentSummary>> getEmploymentGroups() {
        List<AffiliationGroup<EmploymentSummary>> list = new ArrayList<>();
        EmploymentSummary e = new EmploymentSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        AffiliationGroup group = new AffiliationGroup();
        group.getActivities().add(e);
        return list;
    }

    private List<AffiliationGroup<InvitedPositionSummary>> getInvitedPositionGroups() {
        List<AffiliationGroup<InvitedPositionSummary>> list = new ArrayList<>();
        InvitedPositionSummary e = new InvitedPositionSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        AffiliationGroup group = new AffiliationGroup();
        group.getActivities().add(e);
        return list;
    }
    
    private List<AffiliationGroup<MembershipSummary>> getMembershipGroups() {
        List<AffiliationGroup<MembershipSummary>> list = new ArrayList<>();
        MembershipSummary e = new MembershipSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        AffiliationGroup group = new AffiliationGroup();
        group.getActivities().add(e);
        return list;
    }
    
    private List<AffiliationGroup<QualificationSummary>> getQualificationGroups() {
        List<AffiliationGroup<QualificationSummary>> list = new ArrayList<>();
        QualificationSummary e = new QualificationSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        AffiliationGroup group = new AffiliationGroup();
        group.getActivities().add(e);
        return list;
    }
    
    private List<AffiliationGroup<ServiceSummary>> getServiceGroups() {
        List<AffiliationGroup<ServiceSummary>> list = new ArrayList<>();
        ServiceSummary e = new ServiceSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        AffiliationGroup group = new AffiliationGroup();
        group.getActivities().add(e);
        return list;
    }
    
    private FundingGroup getFundingGroup() {
        FundingGroup g = new FundingGroup();
        FundingSummary f = new FundingSummary();
        f.setPutCode(0L);
        f.setLastModifiedDate(new LastModifiedDate(after));
        g.getFundingSummary().add(f);
        return g;
    }

    private PeerReviewGroup getPeerReviewGroup() {
        PeerReviewGroup g = new PeerReviewGroup();
        PeerReviewSummary p = new PeerReviewSummary();
        p.setPutCode(0L);
        p.setLastModifiedDate(new LastModifiedDate(after));
        g.getPeerReviewSummary().add(p);
        return g;
    }

    private WorkGroup getWorkGroup() {
        WorkGroup g = new WorkGroup();
        WorkSummary w = new WorkSummary();
        w.setPutCode(0L);
        w.setLastModifiedDate(new LastModifiedDate(after));
        g.getWorkSummary().add(w);
        return g;
    }

    private Employment getEmployment() {
        Employment e = new Employment();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        return e;
    }

    private Education getEducation() {
        Education e = new Education();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        return e;
    }

    private Funding getFunding() {
        Funding f = new Funding();
        f.setPutCode(0L);
        f.setLastModifiedDate(new LastModifiedDate(after));
        return f;
    }

    private PeerReview getPeerReview() {
        PeerReview p = new PeerReview();
        p.setPutCode(0L);
        p.setLastModifiedDate(new LastModifiedDate(after));
        return p;
    }

    private Work getWork() {
        Work w = new Work();
        w.setPutCode(0L);
        w.setLastModifiedDate(new LastModifiedDate(after));
        return w;
    }

    private Map<ActivityType, Map<String, S3ObjectSummary>> getEmptyMapOfActivities() {
        Map<ActivityType, Map<String, S3ObjectSummary>> activitiesOnS3 = new HashMap<ActivityType, Map<String, S3ObjectSummary>>();

        Map<String, S3ObjectSummary> educations = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> employments = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> fundings = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> works = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> peerReviews = new HashMap<String, S3ObjectSummary>();

        activitiesOnS3.put(ActivityType.EDUCATIONS, educations);
        activitiesOnS3.put(ActivityType.EMPLOYMENTS, employments);
        activitiesOnS3.put(ActivityType.FUNDINGS, fundings);
        activitiesOnS3.put(ActivityType.WORKS, works);
        activitiesOnS3.put(ActivityType.PEER_REVIEWS, peerReviews);
        return activitiesOnS3;
    }
}