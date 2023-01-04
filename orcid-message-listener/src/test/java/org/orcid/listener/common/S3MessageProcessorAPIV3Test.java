package org.orcid.listener.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
import org.orcid.jaxb.model.v3.release.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.History;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Distinctions;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.jaxb.model.v3.release.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositions;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Memberships;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Qualifications;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceGroup;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Services;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.listener.orcid.Orcid30Manager;
import org.orcid.listener.persistence.managers.Api30RecordStatusManager;
import org.orcid.listener.persistence.util.APIVersion;
import org.orcid.listener.persistence.util.ActivityType;
import org.orcid.listener.s3.S3Manager;
import org.orcid.listener.s3.S3MessageProcessorAPIV3;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.DateUtils;
import org.orcid.utils.listener.LastModifiedMessage;
import org.orcid.utils.listener.MessageConstants;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.SerializationUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;

@SuppressWarnings("unchecked")
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-message-listener-test-context.xml" })
public class S3MessageProcessorAPIV3Test {

    private final String orcid = "0000-0000-0000-0000";

    private final Date dateNow = new Date(System.currentTimeMillis());
    private final Date dateAfter = new Date(System.currentTimeMillis() + 1000);
    private final XMLGregorianCalendar now = DateUtils.convertToXMLGregorianCalendar(dateNow);
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
    public void before() throws LockedRecordException, DeprecatedRecordException, ExecutionException, IOException, InterruptedException {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(processor, "isV3IndexerEnabled", true);
        TargetProxyHelper.injectIntoProxy(processor, "orcid30ApiClient", mock_orcid30ApiClient);
        TargetProxyHelper.injectIntoProxy(processor, "api30RecordStatusManager", mock_api30RecordStatusManager);
        TargetProxyHelper.injectIntoProxy(processor, "s3Manager", mock_s3Manager);

        // Setup mocks
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getEmptyMapOfActivities());
        when(mock_orcid30ApiClient.fetchActivity(eq(orcid), eq(0L), eq("distinction"))).thenReturn(getDistinction());
        when(mock_orcid30ApiClient.fetchActivity(eq(orcid), eq(0L), eq("education"))).thenReturn(getEducation());
        when(mock_orcid30ApiClient.fetchActivity(eq(orcid), eq(0L), eq("employment"))).thenReturn(getEmployment());
        when(mock_orcid30ApiClient.fetchActivity(eq(orcid), eq(0L), eq("invited-position"))).thenReturn(getInvitedPosition());
        when(mock_orcid30ApiClient.fetchActivity(eq(orcid), eq(0L), eq("membership"))).thenReturn(getMembership());
        when(mock_orcid30ApiClient.fetchActivity(eq(orcid), eq(0L), eq("qualification"))).thenReturn(getQualification());
        when(mock_orcid30ApiClient.fetchActivity(eq(orcid), eq(0L), eq("service"))).thenReturn(getService());
        when(mock_orcid30ApiClient.fetchActivity(eq(orcid), eq(0L), eq("funding"))).thenReturn(getFunding());
        when(mock_orcid30ApiClient.fetchActivity(eq(orcid), eq(0L), eq("peer-review"))).thenReturn(getPeerReview());
        when(mock_orcid30ApiClient.fetchActivity(eq(orcid), eq(0L), eq("research-resource"))).thenReturn(getResearchResource());
        when(mock_orcid30ApiClient.fetchActivity(eq(orcid), eq(0L), eq("work"))).thenReturn(getWork());
    }

    @Test
    public void v30RecordDeprecatedExceptionTest() throws Exception {
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenThrow(new DeprecatedRecordException(new OrcidError()));
        process(orcid);
        verify(mock_s3Manager, times(1)).clearV3Activities(eq(orcid));
        verify(mock_s3Manager, times(1)).uploadV3OrcidError(eq(orcid), any(OrcidError.class));
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());

        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void v30RecordLockedExceptionTest() throws Exception {
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenThrow(new LockedRecordException(new OrcidError()));
        process(orcid);
        verify(mock_s3Manager, times(1)).clearV3Activities(eq(orcid));
        verify(mock_s3Manager, times(1)).uploadV3OrcidError(eq(orcid), any(OrcidError.class));
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());

        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void recordSummary_AmazonClientExceptionTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3RecordSummary(eq(orcid), any(Record.class));
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), any(String.class), any(ActivityType.class), any(Date.class), any());
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }
        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(false), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.containsAll(Arrays.asList(ActivityType.values())));
    }

    @Test
    public void recordSummary_JAXBExceptionTest() throws Exception {
        doThrow(new JAXBException("error")).when(mock_s3Manager).uploadV3RecordSummary(eq(orcid), any(Record.class));
        doThrow(new JAXBException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), any(String.class), any(ActivityType.class), any(Date.class), any());
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }
        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(false), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.containsAll(Arrays.asList(ActivityType.values())));
    }

    @Test
    public void recordStatusMarkAllOkTest() throws Exception {
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void activities_DistinctionsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq(ActivityType.DISTINCTIONS), any(Date.class), any());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.DISTINCTIONS));
    }

    @Test
    public void activities_EducationsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq(ActivityType.EDUCATIONS), any(Date.class), any());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.EDUCATIONS));
    }

    @Test
    public void activities_EmploymentsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq(ActivityType.EMPLOYMENTS), any(Date.class), any());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.EMPLOYMENTS));
    }

    @Test
    public void activities_FundingsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq(ActivityType.FUNDINGS), any(Date.class), any());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.FUNDINGS));
    }

    @Test
    public void activities_InvitedPositionsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq(ActivityType.INVITED_POSITIONS), any(Date.class), any());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.INVITED_POSITIONS));
    }

    @Test
    public void activities_MembershipsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq(ActivityType.MEMBERSHIP), any(Date.class), any());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.MEMBERSHIP));
    }

    @Test
    public void activities_PeerReviewsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq(ActivityType.PEER_REVIEWS), any(Date.class), any());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.PEER_REVIEWS));
    }

    @Test
    public void activities_QualificationsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq(ActivityType.QUALIFICATIONS), any(Date.class), any());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.QUALIFICATIONS));
    }

    @Test
    public void activities_ResearchResourcesFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq(ActivityType.RESEARCH_RESOURCES), any(Date.class), any());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.RESEARCH_RESOURCES));
    }

    @Test
    public void activities_ServicesFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq(ActivityType.SERVICES), any(Date.class), any());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.SERVICES));
    }

    @Test
    public void activities_WorksFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq(ActivityType.WORKS), any(Date.class), any());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.WORKS));
    }
    

    @Test
    public void uploadNothingTest() throws Exception {
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());    

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(any(), any(), any(), any(Date.class), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void addDistinctionsTest() throws Exception {
        // Add one education more
        Record r = getRecord();
        DistinctionSummary s2 = new DistinctionSummary();
        s2.setPutCode(1L);
        s2.setLastModifiedDate(new LastModifiedDate(now));
        AffiliationGroup<DistinctionSummary> group = new AffiliationGroup<DistinctionSummary>();
        group.getActivities().add(s2);
        r.getActivitiesSummary().getDistinctions().retrieveGroups().add(group);
        Distinction e2 = new Distinction();
        e2.setPutCode(1L);
        e2.setLastModifiedDate(new LastModifiedDate(now));

        byte [] e2ba = SerializationUtils.serialize(e2);
        
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());
        when(mock_orcid30ApiClient.fetchActivity(eq(orcid), eq(1L), eq("education"))).thenReturn(e2ba);
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), eq("1"), eq(ActivityType.DISTINCTIONS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(any(), any(), not(eq(ActivityType.DISTINCTIONS)), any(Date.class), any());        
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeDistinctionsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.DISTINCTIONS));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).removeV3Activity(eq(orcid), eq("1"), eq(ActivityType.DISTINCTIONS));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void uploadDistinctionBecauseLastModifiedChangedTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getDistinctions().retrieveGroups().forEach(g -> {g.getActivities().get(0).getLastModifiedDate().setValue(after);});
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), eq("1"), eq(ActivityType.DISTINCTIONS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(any(), any(), not(eq(ActivityType.DISTINCTIONS)), any(Date.class), any());
        verify(mock_s3Manager, times(0)).removeV3Activity(eq(orcid), any(), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeAllDistinctionsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getDistinctions().retrieveGroups().clear();
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).clearV3ActivitiesByType(eq(orcid), eq(ActivityType.DISTINCTIONS));
        verify(mock_s3Manager, times(0)).uploadV3OrcidError(any(), any());      
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void addEducationsTest() throws Exception {
        // Add one education more
        Record r = getRecord();
        EducationSummary s2 = new EducationSummary();
        s2.setPutCode(1L);
        s2.setLastModifiedDate(new LastModifiedDate(now));
        AffiliationGroup<EducationSummary> group = new AffiliationGroup<EducationSummary>();
        group.getActivities().add(s2);
        r.getActivitiesSummary().getEducations().retrieveGroups().add(group);
        Education e2 = new Education();
        e2.setPutCode(1L);
        e2.setLastModifiedDate(new LastModifiedDate(now));

        byte [] e2ba = SerializationUtils.serialize(e2);
        
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());
        when(mock_orcid30ApiClient.fetchActivity(eq(orcid), eq(1L), eq("education"))).thenReturn(e2ba);

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), eq("1"), eq(ActivityType.EDUCATIONS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(any(), any(), not(eq(ActivityType.EDUCATIONS)), any(Date.class), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeEducationsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.EDUCATIONS));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).removeV3Activity(eq(orcid), eq("1"), eq(ActivityType.EDUCATIONS));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void uploadEducationsBecauseLastModifiedChangedTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getEducations().retrieveGroups().forEach(g -> {g.getActivities().get(0).getLastModifiedDate().setValue(after);});
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), eq("1"), eq(ActivityType.EDUCATIONS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(any(), any(), not(eq(ActivityType.EDUCATIONS)), any(Date.class), any());
        verify(mock_s3Manager, times(0)).removeV3Activity(eq(orcid), any(), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeAllEducationsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getEducations().retrieveGroups().clear();
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).clearV3ActivitiesByType(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_s3Manager, times(0)).uploadV3OrcidError(any(), any());      
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void addEmploymentsTest() throws Exception {
        // Add one education more
        Record r = getRecord();
        EmploymentSummary s2 = new EmploymentSummary();
        s2.setPutCode(1L);
        s2.setLastModifiedDate(new LastModifiedDate(now));
        AffiliationGroup<EmploymentSummary> group = new AffiliationGroup<EmploymentSummary>();
        group.getActivities().add(s2);
        r.getActivitiesSummary().getEmployments().retrieveGroups().add(group);
        Employment e2 = new Employment();
        e2.setPutCode(1L);
        e2.setLastModifiedDate(new LastModifiedDate(now));

        byte [] e2ba = SerializationUtils.serialize(e2);
        
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());
        when(mock_orcid30ApiClient.fetchActivity(eq(orcid), eq(1L), eq("employment"))).thenReturn(e2ba);

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), eq("1"), eq(ActivityType.EMPLOYMENTS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(any(), any(), not(eq(ActivityType.EMPLOYMENTS)), any(Date.class), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeEmploymentsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.EMPLOYMENTS));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).removeV3Activity(eq(orcid), eq("1"), eq(ActivityType.EMPLOYMENTS));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void uploadEmploymentsBecauseLastModifiedChangedTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getEmployments().retrieveGroups().forEach(g -> {g.getActivities().get(0).getLastModifiedDate().setValue(after);});
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), eq("1"), eq(ActivityType.EMPLOYMENTS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(any(), any(), not(eq(ActivityType.EMPLOYMENTS)), any(Date.class), any());
        verify(mock_s3Manager, times(0)).removeV3Activity(eq(orcid), any(), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeAllEmploymentsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getEmployments().retrieveGroups().clear();
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).clearV3ActivitiesByType(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_s3Manager, times(0)).uploadV3OrcidError(any(), any());      
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void addFundingsTest() throws Exception {
        // Add one education more
        Record r = getRecord();
        FundingSummary s2 = new FundingSummary();
        s2.setPutCode(1L);
        s2.setLastModifiedDate(new LastModifiedDate(now));
        FundingGroup group = new FundingGroup();
        group.getFundingSummary().add(s2);
        r.getActivitiesSummary().getFundings().getFundingGroup().add(group);
        Funding e2 = new Funding();
        e2.setPutCode(1L);
        e2.setLastModifiedDate(new LastModifiedDate(now));

        byte [] e2ba = SerializationUtils.serialize(e2);
        
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());
        when(mock_orcid30ApiClient.fetchActivity(eq(orcid), eq(1L), eq("funding"))).thenReturn(e2ba);

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), eq("1"), eq(ActivityType.FUNDINGS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(any(), any(), not(eq(ActivityType.FUNDINGS)), any(Date.class), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeFundingsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.FUNDINGS));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).removeV3Activity(eq(orcid), eq("1"), eq(ActivityType.FUNDINGS));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void uploadFundingsBecauseLastModifiedChangedTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getFundings().getFundingGroup().forEach(g -> {g.getFundingSummary().get(0).getLastModifiedDate().setValue(after);});
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), eq("1"), eq(ActivityType.FUNDINGS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(any(), any(), not(eq(ActivityType.FUNDINGS)), any(Date.class), any());
        verify(mock_s3Manager, times(0)).removeV3Activity(eq(orcid), any(), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeAllFundingsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getFundings().getFundingGroup().clear();
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(1)).clearV3ActivitiesByType(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_s3Manager, times(0)).uploadV3OrcidError(any(), any());      
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void addInvitedPositionsTest() throws Exception {
        // Add one education more
        Record r = getRecord();
        InvitedPositionSummary s2 = new InvitedPositionSummary();
        s2.setPutCode(1L);
        s2.setLastModifiedDate(new LastModifiedDate(now));
        AffiliationGroup<InvitedPositionSummary> group = new AffiliationGroup<InvitedPositionSummary>();
        group.getActivities().add(s2);
        r.getActivitiesSummary().getInvitedPositions().retrieveGroups().add(group);
        InvitedPosition e2 = new InvitedPosition();
        e2.setPutCode(1L);
        e2.setLastModifiedDate(new LastModifiedDate(now));

        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());
        when(mock_orcid30ApiClient.fetchAffiliation(eq(orcid), eq(1L), eq(AffiliationType.INVITED_POSITION))).thenReturn(e2);

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), eq("1"), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeInvitedPositionsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.INVITED_POSITIONS));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(1)).removeV3Activity(eq(orcid), eq("1"), eq(ActivityType.INVITED_POSITIONS));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void uploadInvitedPositionsBecauseLastModifiedChangedTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getInvitedPositions().retrieveGroups().forEach(g -> {g.getActivities().get(0).getLastModifiedDate().setValue(after);});
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(0)).removeV3Activity(eq(orcid), any(), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeAllInvitedPositionsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getInvitedPositions().retrieveGroups().clear();
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(1)).clearV3ActivitiesByType(eq(orcid), eq(ActivityType.INVITED_POSITIONS));
        verify(mock_s3Manager, times(0)).uploadV3OrcidError(any(), any());      
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void addMembershipsTest() throws Exception {
        // Add one education more
        Record r = getRecord();
        MembershipSummary s2 = new MembershipSummary();
        s2.setPutCode(1L);
        s2.setLastModifiedDate(new LastModifiedDate(now));
        AffiliationGroup<MembershipSummary> group = new AffiliationGroup<MembershipSummary>();
        group.getActivities().add(s2);
        r.getActivitiesSummary().getMemberships().retrieveGroups().add(group);
        Membership e2 = new Membership();
        e2.setPutCode(1L);
        e2.setLastModifiedDate(new LastModifiedDate(now));

        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());
        when(mock_orcid30ApiClient.fetchAffiliation(eq(orcid), eq(1L), eq(AffiliationType.MEMBERSHIP))).thenReturn(e2);

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), eq("1"), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeMembershipsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.MEMBERSHIP));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(1)).removeV3Activity(eq(orcid), eq("1"), eq(ActivityType.MEMBERSHIP));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void uploadMembershipsBecauseLastModifiedChangedTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getMemberships().retrieveGroups().forEach(g -> {g.getActivities().get(0).getLastModifiedDate().setValue(after);});
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(0)).removeV3Activity(eq(orcid), any(), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeAllMemebershipsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getMemberships().retrieveGroups().clear();
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(1)).clearV3ActivitiesByType(eq(orcid), eq(ActivityType.MEMBERSHIP));
        verify(mock_s3Manager, times(0)).uploadV3OrcidError(any(), any());      
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void addPeerReviewsTest() throws Exception {
        // Add one education more
        Record r = getRecord();
        PeerReviewSummary s2 = new PeerReviewSummary();
        s2.setPutCode(1L);
        s2.setLastModifiedDate(new LastModifiedDate(now));
        PeerReviewGroup group = new PeerReviewGroup();
        PeerReviewDuplicateGroup dg = new PeerReviewDuplicateGroup();
        dg.getPeerReviewSummary().add(s2);
        group.getPeerReviewGroup().add(dg);
        r.getActivitiesSummary().getPeerReviews().getPeerReviewGroup().add(group);
        PeerReview e2 = new PeerReview();
        e2.setPutCode(1L);
        e2.setLastModifiedDate(new LastModifiedDate(now));

        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());
        when(mock_orcid30ApiClient.fetchPeerReview(eq(orcid), eq(1L))).thenReturn(e2);

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), eq("1"), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removePeerReviewsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.PEER_REVIEWS));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(1)).removeV3Activity(eq(orcid), eq("1"), eq(ActivityType.PEER_REVIEWS));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void uploadPeerReviewBecauseLastModifiedChangedTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getPeerReviews().getPeerReviewGroup().forEach(g -> {g.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getLastModifiedDate().setValue(after);});
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(0)).removeV3Activity(eq(orcid), any(), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeAllPeerReviewsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getPeerReviews().getPeerReviewGroup().clear();
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(1)).clearV3ActivitiesByType(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_s3Manager, times(0)).uploadV3OrcidError(any(), any());      
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void addQualificationsTest() throws Exception {
        // Add one education more
        Record r = getRecord();
        QualificationSummary s2 = new QualificationSummary();
        s2.setPutCode(1L);
        s2.setLastModifiedDate(new LastModifiedDate(now));
        AffiliationGroup<QualificationSummary> group = new AffiliationGroup<QualificationSummary>();
        group.getActivities().add(s2);
        r.getActivitiesSummary().getQualifications().retrieveGroups().add(group);
        Qualification e2 = new Qualification();
        e2.setPutCode(1L);
        e2.setLastModifiedDate(new LastModifiedDate(now));

        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());
        when(mock_orcid30ApiClient.fetchAffiliation(eq(orcid), eq(1L), eq(AffiliationType.QUALIFICATION))).thenReturn(e2);

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), eq("1"), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeQualificationsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.QUALIFICATIONS));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(1)).removeV3Activity(eq(orcid), eq("1"), eq(ActivityType.QUALIFICATIONS));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void uploadQualificationsBecauseLastModifiedChangedTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getQualifications().retrieveGroups().forEach(g -> {g.getActivities().get(0).getLastModifiedDate().setValue(after);});
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(0)).removeV3Activity(eq(orcid), any(), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeAllQualificationsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getQualifications().retrieveGroups().clear();
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(1)).clearV3ActivitiesByType(eq(orcid), eq(ActivityType.QUALIFICATIONS));
        verify(mock_s3Manager, times(0)).uploadV3OrcidError(any(), any());      
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void addResearchResourcesTest() throws Exception {
        // Add one education more
        Record r = getRecord();
        ResearchResourceSummary s2 = new ResearchResourceSummary();
        s2.setPutCode(1L);
        s2.setLastModifiedDate(new LastModifiedDate(now));
        ResearchResourceGroup group = new ResearchResourceGroup();
        group.getResearchResourceSummary().add(s2);
        r.getActivitiesSummary().getResearchResources().getResearchResourceGroup().add(group);
        ResearchResource e2 = new ResearchResource();
        e2.setPutCode(1L);
        e2.setLastModifiedDate(new LastModifiedDate(now));

        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());
        when(mock_orcid30ApiClient.fetchResearchResource(eq(orcid), eq(1L))).thenReturn(e2);

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), eq("1"), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeResearchResourcesTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.RESEARCH_RESOURCES));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(1)).removeV3Activity(eq(orcid), eq("1"), eq(ActivityType.RESEARCH_RESOURCES));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void uploadResearchResourcesBecauseLastModifiedChangedTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getResearchResources().getResearchResourceGroup().forEach(g -> {g.getResearchResourceSummary().get(0).getLastModifiedDate().setValue(after);});
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(0)).removeV3Activity(eq(orcid), any(), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeAllResearchResourcesTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getResearchResources().getResearchResourceGroup().clear();
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(1)).clearV3ActivitiesByType(eq(orcid), eq(ActivityType.RESEARCH_RESOURCES));
        verify(mock_s3Manager, times(0)).uploadV3OrcidError(any(), any());      
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void addServicesTest() throws Exception {
        // Add one education more
        Record r = getRecord();
        ServiceSummary s2 = new ServiceSummary();
        s2.setPutCode(1L);
        s2.setLastModifiedDate(new LastModifiedDate(now));
        AffiliationGroup<ServiceSummary> group = new AffiliationGroup<ServiceSummary>();
        group.getActivities().add(s2);
        r.getActivitiesSummary().getServices().retrieveGroups().add(group);
        Service e2 = new Service();
        e2.setPutCode(1L);
        e2.setLastModifiedDate(new LastModifiedDate(now));

        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());
        when(mock_orcid30ApiClient.fetchAffiliation(eq(orcid), eq(1L), eq(AffiliationType.SERVICE))).thenReturn(e2);

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), eq("1"), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeServicesTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.SERVICES));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(1)).removeV3Activity(eq(orcid), eq("1"), eq(ActivityType.SERVICES));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void uploadServicesBecauseLastModifiedChangedTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getServices().retrieveGroups().forEach(g -> {g.getActivities().get(0).getLastModifiedDate().setValue(after);});
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(0)).removeV3Activity(eq(orcid), any(), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeAllServicesTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getServices().retrieveGroups().clear();
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(1)).clearV3ActivitiesByType(eq(orcid), eq(ActivityType.SERVICES));
        verify(mock_s3Manager, times(0)).uploadV3OrcidError(any(), any());      
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void addWorksTest() throws Exception {
        // Add one education more
        Record r = getRecord();
        WorkSummary s2 = new WorkSummary();
        s2.setPutCode(1L);
        s2.setLastModifiedDate(new LastModifiedDate(now));
        WorkGroup group = new WorkGroup();
        group.getWorkSummary().add(s2);
        r.getActivitiesSummary().getWorks().getWorkGroup().add(group);
        Work e2 = new Work();
        e2.setPutCode(1L);
        e2.setLastModifiedDate(new LastModifiedDate(now));

        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());
        when(mock_orcid30ApiClient.fetchWork(eq(orcid), eq(1L))).thenReturn(e2);

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), eq("1"), any(Work.class));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeWorksTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.WORKS));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(1)).removeV3Activity(eq(orcid), eq("1"), eq(ActivityType.WORKS));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void uploadWorksBecauseLastModifiedChangedTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getWorks().getWorkGroup().forEach(g -> {g.getWorkSummary().get(0).getLastModifiedDate().setValue(after);});
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(0)).removeV3Activity(eq(orcid), any(), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeAllWorksTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getWorks().getWorkGroup().clear();
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(0)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(1)).clearV3ActivitiesByType(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_s3Manager, times(0)).uploadV3OrcidError(any(), any());      
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    private void verifyUploadIsCalledForSummaryAndActivities() throws AmazonServiceException, JsonProcessingException, AmazonClientException, JAXBException {
        verify(mock_s3Manager, times(1)).uploadV3RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(Distinction.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(Education.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(Employment.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(Funding.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(InvitedPosition.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(Membership.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(PeerReview.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(Qualification.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(ResearchResource.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(Service.class));
        verify(mock_s3Manager, times(1)).uploadV3Activity(eq(orcid), any(), any(Work.class));
        verify(mock_s3Manager, times(0)).removeV3Activity(any(), any(), any());
    }

    private void verifyErrorAndClearWasntCalled() throws AmazonServiceException, JsonProcessingException, AmazonClientException, JAXBException {
        verify(mock_s3Manager, times(0)).uploadV3OrcidError(any(), any());
        verify(mock_s3Manager, times(0)).clearV3ActivitiesByType(any(), any());
    }

    private void process(String orcid) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        String date = String.valueOf(System.currentTimeMillis());
        map.put(MessageConstants.ORCID.value, orcid);
        map.put(MessageConstants.DATE.value, date);
        map.put(MessageConstants.TYPE.value, MessageConstants.TYPE_LAST_UPDATED.value);
        LastModifiedMessage message = new LastModifiedMessage(map);
        processor.update(message);
    }

    private Record getRecord() {
        Record claimedRecord = new Record();
        claimedRecord.setOrcidIdentifier(new OrcidIdentifier(orcid));
        History history = new History();
        history.setClaimed(true);
        claimedRecord.setHistory(history);
        ActivitiesSummary as = new ActivitiesSummary();
        as.setDistinctions(new Distinctions(getDistinctionGroups()));
        as.setEducations(new Educations(getEducationGroups()));
        as.setEmployments(new Employments(getEmploymentGroups()));
        as.setInvitedPositions(new InvitedPositions(getInvitedPositionGroups()));
        as.setMemberships(new Memberships(getMembershipGroups()));
        as.setQualifications(new Qualifications(getQualificationGroups()));
        as.setServices(new Services(getServiceGroups()));

        as.getFundings().getFundingGroup().add(getFundingGroup());
        as.getPeerReviews().getPeerReviewGroup().add(getPeerReviewGroup());
        as.getResearchResources().getResearchResourceGroup().add(getResearchResourcesGroup());
        as.getWorks().getWorkGroup().add(getWorkGroup());
        claimedRecord.setActivitiesSummary(as);
        return claimedRecord;
    }

    private List<AffiliationGroup<DistinctionSummary>> getDistinctionGroups() {
        List<AffiliationGroup<DistinctionSummary>> list = new ArrayList<>();
        DistinctionSummary e = new DistinctionSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(now));
        AffiliationGroup<DistinctionSummary> group = new AffiliationGroup<DistinctionSummary>();
        group.getActivities().add(e);
        list.add(group);
        return list;
    }

    private List<AffiliationGroup<EducationSummary>> getEducationGroups() {
        List<AffiliationGroup<EducationSummary>> list = new ArrayList<>();
        EducationSummary e = new EducationSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(now));
        AffiliationGroup<EducationSummary> group = new AffiliationGroup<EducationSummary>();
        group.getActivities().add(e);
        list.add(group);
        return list;
    }

    private List<AffiliationGroup<EmploymentSummary>> getEmploymentGroups() {
        List<AffiliationGroup<EmploymentSummary>> list = new ArrayList<>();
        EmploymentSummary e = new EmploymentSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(now));
        AffiliationGroup<EmploymentSummary> group = new AffiliationGroup<EmploymentSummary>();
        group.getActivities().add(e);
        list.add(group);
        return list;
    }

    private List<AffiliationGroup<InvitedPositionSummary>> getInvitedPositionGroups() {
        List<AffiliationGroup<InvitedPositionSummary>> list = new ArrayList<>();
        InvitedPositionSummary e = new InvitedPositionSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(now));
        AffiliationGroup<InvitedPositionSummary> group = new AffiliationGroup<InvitedPositionSummary>();
        group.getActivities().add(e);
        list.add(group);
        return list;
    }

    private List<AffiliationGroup<MembershipSummary>> getMembershipGroups() {
        List<AffiliationGroup<MembershipSummary>> list = new ArrayList<>();
        MembershipSummary e = new MembershipSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(now));
        AffiliationGroup<MembershipSummary> group = new AffiliationGroup<MembershipSummary>();
        group.getActivities().add(e);
        list.add(group);
        return list;
    }

    private List<AffiliationGroup<QualificationSummary>> getQualificationGroups() {
        List<AffiliationGroup<QualificationSummary>> list = new ArrayList<>();
        QualificationSummary e = new QualificationSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(now));
        AffiliationGroup<QualificationSummary> group = new AffiliationGroup<QualificationSummary>();
        group.getActivities().add(e);
        list.add(group);
        return list;
    }

    private List<AffiliationGroup<ServiceSummary>> getServiceGroups() {
        List<AffiliationGroup<ServiceSummary>> list = new ArrayList<>();
        ServiceSummary e = new ServiceSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(now));
        AffiliationGroup<ServiceSummary> group = new AffiliationGroup<ServiceSummary>();
        group.getActivities().add(e);
        list.add(group);
        return list;
    }

    private FundingGroup getFundingGroup() {
        FundingGroup g = new FundingGroup();
        FundingSummary f = new FundingSummary();
        f.setPutCode(0L);
        f.setLastModifiedDate(new LastModifiedDate(now));
        g.getFundingSummary().add(f);
        return g;
    }

    private PeerReviewGroup getPeerReviewGroup() {
        PeerReviewGroup g = new PeerReviewGroup();
        PeerReviewDuplicateGroup dg = new PeerReviewDuplicateGroup();
        PeerReviewSummary p = new PeerReviewSummary();
        p.setPutCode(0L);
        p.setLastModifiedDate(new LastModifiedDate(now));
        dg.getPeerReviewSummary().add(p);
        g.getPeerReviewGroup().add(dg);
        return g;
    }

    private ResearchResourceGroup getResearchResourcesGroup() {
        ResearchResourceGroup g = new ResearchResourceGroup();
        ResearchResourceSummary p = new ResearchResourceSummary();
        p.setPutCode(0L);
        p.setLastModifiedDate(new LastModifiedDate(now));
        g.getResearchResourceSummary().add(p);
        return g;
    }

    private WorkGroup getWorkGroup() {
        WorkGroup g = new WorkGroup();
        WorkSummary w = new WorkSummary();
        w.setPutCode(0L);
        w.setLastModifiedDate(new LastModifiedDate(now));
        g.getWorkSummary().add(w);
        return g;
    }

    private byte [] getDistinction() {
        Distinction e = new Distinction();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(now));
        return SerializationUtils.serialize(e);
    }

    private byte [] getEducation() {
        Education e = new Education();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(now));
        return SerializationUtils.serialize(e);
    }

    private byte [] getEmployment() {
        Employment e = new Employment();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(now));
        return SerializationUtils.serialize(e);
    }

    private byte [] getInvitedPosition() {
        InvitedPosition e = new InvitedPosition();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(now));
        return SerializationUtils.serialize(e);
    }

    private byte [] getMembership() {
        Membership e = new Membership();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(now));
        return SerializationUtils.serialize(e);
    }

    private byte [] getQualification() {
        Qualification e = new Qualification();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(now));
        return SerializationUtils.serialize(e);
    }

    private byte [] getService() {
        Service e = new Service();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(now));
        return SerializationUtils.serialize(e);
    }

    private byte [] getFunding() {
        Funding f = new Funding();
        f.setPutCode(0L);
        f.setLastModifiedDate(new LastModifiedDate(now));
        return SerializationUtils.serialize(f);
    }

    private byte [] getPeerReview() {
        PeerReview p = new PeerReview();
        p.setPutCode(0L);
        p.setLastModifiedDate(new LastModifiedDate(now));
        return SerializationUtils.serialize(p);
    }

    private byte [] getResearchResource() {
        ResearchResource p = new ResearchResource();
        p.setPutCode(0L);
        p.setLastModifiedDate(new LastModifiedDate(now));
        return SerializationUtils.serialize(p);
    }

    private byte [] getWork() {
        Work w = new Work();
        w.setPutCode(0L);
        w.setLastModifiedDate(new LastModifiedDate(now));
        return SerializationUtils.serialize(w);
    }

    private Map<ActivityType, Map<String, S3ObjectSummary>> getEmptyMapOfActivities() {
        Map<ActivityType, Map<String, S3ObjectSummary>> activitiesOnS3 = new HashMap<ActivityType, Map<String, S3ObjectSummary>>();

        Map<String, S3ObjectSummary> distinctions = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> educations = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> employments = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> invitedPositions = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> fundings = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> memberships = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> peerReviews = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> qualifications = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> researchResources = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> services = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> works = new HashMap<String, S3ObjectSummary>();

        activitiesOnS3.put(ActivityType.DISTINCTIONS, distinctions);
        activitiesOnS3.put(ActivityType.EDUCATIONS, educations);
        activitiesOnS3.put(ActivityType.EMPLOYMENTS, employments);
        activitiesOnS3.put(ActivityType.INVITED_POSITIONS, invitedPositions);
        activitiesOnS3.put(ActivityType.FUNDINGS, fundings);
        activitiesOnS3.put(ActivityType.MEMBERSHIP, memberships);
        activitiesOnS3.put(ActivityType.PEER_REVIEWS, peerReviews);
        activitiesOnS3.put(ActivityType.QUALIFICATIONS, qualifications);
        activitiesOnS3.put(ActivityType.RESEARCH_RESOURCES, researchResources);
        activitiesOnS3.put(ActivityType.SERVICES, services);
        activitiesOnS3.put(ActivityType.WORKS, works);
        return activitiesOnS3;
    }

    private Map<ActivityType, Map<String, S3ObjectSummary>> getMapOfActivities() {
        String putCode = String.valueOf(0L);
        S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
        s3ObjectSummary.setLastModified(dateNow);
        Map<ActivityType, Map<String, S3ObjectSummary>> activitiesOnS3 = getEmptyMapOfActivities();
        activitiesOnS3.get(ActivityType.DISTINCTIONS).put(putCode, s3ObjectSummary);
        activitiesOnS3.get(ActivityType.EDUCATIONS).put(putCode, s3ObjectSummary);
        activitiesOnS3.get(ActivityType.EMPLOYMENTS).put(putCode, s3ObjectSummary);
        activitiesOnS3.get(ActivityType.FUNDINGS).put(putCode, s3ObjectSummary);
        activitiesOnS3.get(ActivityType.INVITED_POSITIONS).put(putCode, s3ObjectSummary);
        activitiesOnS3.get(ActivityType.MEMBERSHIP).put(putCode, s3ObjectSummary);
        activitiesOnS3.get(ActivityType.PEER_REVIEWS).put(putCode, s3ObjectSummary);
        activitiesOnS3.get(ActivityType.QUALIFICATIONS).put(putCode, s3ObjectSummary);
        activitiesOnS3.get(ActivityType.RESEARCH_RESOURCES).put(putCode, s3ObjectSummary);
        activitiesOnS3.get(ActivityType.SERVICES).put(putCode, s3ObjectSummary);
        activitiesOnS3.get(ActivityType.WORKS).put(putCode, s3ObjectSummary);
        return activitiesOnS3;
    }

    private Map<ActivityType, Map<String, S3ObjectSummary>> getMapOfActivities_AddOneExtraOfType(ActivityType t) {
        String putCode = String.valueOf(1L);
        S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
        s3ObjectSummary.setLastModified(dateAfter);
        Map<ActivityType, Map<String, S3ObjectSummary>> activitiesOnS3 = getMapOfActivities();
        switch (t) {
        case DISTINCTIONS:
            activitiesOnS3.get(ActivityType.DISTINCTIONS).put(putCode, s3ObjectSummary);
            break;
        case EDUCATIONS:
            activitiesOnS3.get(ActivityType.EDUCATIONS).put(putCode, s3ObjectSummary);
            break;
        case EMPLOYMENTS:
            activitiesOnS3.get(ActivityType.EMPLOYMENTS).put(putCode, s3ObjectSummary);
            break;
        case FUNDINGS:
            activitiesOnS3.get(ActivityType.FUNDINGS).put(putCode, s3ObjectSummary);
            break;
        case INVITED_POSITIONS:
            activitiesOnS3.get(ActivityType.INVITED_POSITIONS).put(putCode, s3ObjectSummary);
            break;
        case MEMBERSHIP:
            activitiesOnS3.get(ActivityType.MEMBERSHIP).put(putCode, s3ObjectSummary);
            break;
        case PEER_REVIEWS:
            activitiesOnS3.get(ActivityType.PEER_REVIEWS).put(putCode, s3ObjectSummary);
            break;
        case QUALIFICATIONS:
            activitiesOnS3.get(ActivityType.QUALIFICATIONS).put(putCode, s3ObjectSummary);
            break;
        case RESEARCH_RESOURCES:
            activitiesOnS3.get(ActivityType.RESEARCH_RESOURCES).put(putCode, s3ObjectSummary);
            break;
        case SERVICES:
            activitiesOnS3.get(ActivityType.SERVICES).put(putCode, s3ObjectSummary);
            break;
        case WORKS:
            activitiesOnS3.get(ActivityType.WORKS).put(putCode, s3ObjectSummary);
            break;
        }
        return activitiesOnS3;
    }
}