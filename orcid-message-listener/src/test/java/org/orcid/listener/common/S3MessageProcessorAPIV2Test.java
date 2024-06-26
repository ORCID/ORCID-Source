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
import org.orcid.jaxb.model.common_v2.LastModifiedDate;
import org.orcid.jaxb.model.common_v2.OrcidIdentifier;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.Educations;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.Employments;
import org.orcid.jaxb.model.record.summary_v2.FundingGroup;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.History;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.listener.orcid.Orcid20Manager;
import org.orcid.listener.persistence.managers.Api20RecordStatusManager;
import org.orcid.listener.persistence.util.APIVersion;
import org.orcid.listener.persistence.util.ActivityType;
import org.orcid.listener.s3.S3Manager;
import org.orcid.listener.s3.S3MessageProcessorAPIV2;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.SerializationUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;

@SuppressWarnings("unchecked")
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-message-listener-test-context.xml" })
public class S3MessageProcessorAPIV2Test {

    private final String orcid = "0000-0000-0000-0000";

    private final Date dateNow = new Date(System.currentTimeMillis());
    private final Date dateAfter = new Date(System.currentTimeMillis() + 1000);
    private final XMLGregorianCalendar now = DateUtils.convertToXMLGregorianCalendar(dateNow);
    private final XMLGregorianCalendar after = DateUtils.convertToXMLGregorianCalendar(dateAfter);

    @Resource
    private S3MessageProcessorAPIV2 processor;

    @Mock
    private Orcid20Manager mock_orcid20ApiClient;

    @Mock
    private S3Manager mock_s3Manager;

    @Mock
    private Api20RecordStatusManager mock_api20RecordStatusManager;

    @Before
    public void before() throws LockedRecordException, DeprecatedRecordException, ExecutionException, IOException, InterruptedException {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(processor, "isV2IndexingEnabled", true);
        TargetProxyHelper.injectIntoProxy(processor, "orcid20ApiClient", mock_orcid20ApiClient);
        TargetProxyHelper.injectIntoProxy(processor, "api20RecordStatusManager", mock_api20RecordStatusManager);
        TargetProxyHelper.injectIntoProxy(processor, "s3Manager", mock_s3Manager);

        // Setup mocks
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getEmptyMapOfActivities());
        when(mock_orcid20ApiClient.fetchActivity(eq(orcid), eq(0L), eq("education"))).thenReturn(getEducation());
        when(mock_orcid20ApiClient.fetchActivity(eq(orcid), eq(0L), eq("employment"))).thenReturn(getEmployment());
        when(mock_orcid20ApiClient.fetchActivity(eq(orcid), eq(0L), eq("funding"))).thenReturn(getFunding());
        when(mock_orcid20ApiClient.fetchActivity(eq(orcid), eq(0L), eq("peer-review"))).thenReturn(getPeerReview());
        when(mock_orcid20ApiClient.fetchActivity(eq(orcid), eq(0L), eq("work"))).thenReturn(getWork());
    }

    @Test
    public void v20RecordDeprecatedExceptionTest() throws Exception {
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenThrow(new DeprecatedRecordException(new OrcidError()));
        when(mock_s3Manager.clearV2Activities(any())).thenReturn(true);
        process(orcid);
        verify(mock_s3Manager, times(1)).clearV2Activities(eq(orcid));
        verify(mock_s3Manager, times(1)).uploadV2OrcidError(eq(orcid), any(OrcidError.class));
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());

        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void v20RecordLockedExceptionTest() throws Exception {
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenThrow(new LockedRecordException(new OrcidError()));
        when(mock_s3Manager.clearV2Activities(any())).thenReturn(true);
        process(orcid);
        verify(mock_s3Manager, times(1)).clearV2Activities(eq(orcid));
        verify(mock_s3Manager, times(1)).uploadV2OrcidError(eq(orcid), any(OrcidError.class));
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());

        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void recordSummary_AmazonClientExceptionTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV2RecordSummary(eq(orcid), any(Record.class));
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV2Activity(eq(orcid), any(String.class), any(ActivityType.class), any(Date.class), any());
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }
        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(false), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.containsAll(Arrays.asList(ActivityType.EDUCATIONS, ActivityType.EMPLOYMENTS, ActivityType.FUNDINGS, ActivityType.PEER_REVIEWS, ActivityType.WORKS)));
    }

    @Test
    public void recordSummary_JAXBExceptionTest() throws Exception {
        doThrow(new JAXBException("error")).when(mock_s3Manager).uploadV2RecordSummary(eq(orcid), any(Record.class));
        doThrow(new JAXBException("error")).when(mock_s3Manager).uploadV2Activity(eq(orcid), any(String.class), any(ActivityType.class), any(Date.class), any());
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }
        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(false), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.containsAll(Arrays.asList(ActivityType.EDUCATIONS, ActivityType.EMPLOYMENTS, ActivityType.FUNDINGS, ActivityType.PEER_REVIEWS, ActivityType.WORKS)));
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
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }    

    @Test
    public void activities_EducationsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV2Activity(eq(orcid), any(String.class), eq(ActivityType.EDUCATIONS), any(Date.class), any());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.EDUCATIONS));
    }

    @Test
    public void activities_EmploymentsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV2Activity(eq(orcid), any(String.class), eq(ActivityType.EMPLOYMENTS), any(Date.class), any());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.EMPLOYMENTS));
    }

    @Test
    public void activities_FundingsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV2Activity(eq(orcid), any(String.class), eq(ActivityType.FUNDINGS), any(Date.class), any());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.FUNDINGS));
    }

    @Test
    public void activities_PeerReviewsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV2Activity(eq(orcid), any(String.class), eq(ActivityType.PEER_REVIEWS), any(Date.class), any());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.PEER_REVIEWS));
    }

    @Test
    public void activities_WorksFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV2Activity(eq(orcid), any(String.class), eq(ActivityType.WORKS), any(Date.class), any());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.WORKS));
    }
    

    @Test
    public void uploadNothingTest() throws Exception {
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities());    

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), any(), any(), any(), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
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
        Educations educations = new Educations();
        educations.getSummaries().add(s2);
        r.getActivitiesSummary().setEducations(educations);
        Education e2 = new Education();
        e2.setPutCode(1L);
        e2.setLastModifiedDate(new LastModifiedDate(now));

        byte [] e2ba = SerializationUtils.serialize(e2);
        
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities());
        when(mock_orcid20ApiClient.fetchActivity(eq(orcid), eq(1L), eq("education"))).thenReturn(e2ba);
        when(mock_s3Manager.removeV2Activity(eq(orcid), any(), eq(ActivityType.EDUCATIONS))).thenReturn(true);        
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq("1"), eq(ActivityType.EDUCATIONS), any(Date.class), eq(e2ba));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), any(), eq(ActivityType.EMPLOYMENTS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), any(), eq(ActivityType.FUNDINGS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), any(), eq(ActivityType.PEER_REVIEWS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), any(), eq(ActivityType.WORKS), any(Date.class), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeEducationsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.EDUCATIONS));
        when(mock_s3Manager.removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.EDUCATIONS))).thenReturn(true);
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.EDUCATIONS));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), not(eq("1")), any(), any(Date.class), any());        
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void removeEducationsFailTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.EDUCATIONS));
        when(mock_s3Manager.removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.EDUCATIONS))).thenReturn(false);
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.EDUCATIONS));
        verify(mock_s3Manager, times(0)).uploadV2Activity(eq(orcid), not(eq("1")), any(), any(Date.class), any());        
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captorOnFail = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captorOnFail.capture());
        final ArrayList<ActivityType> argumentOnFail = captorOnFail.getValue();
        assertNotNull(argumentOnFail);
        assertEquals(1, argumentOnFail.size());
        assertEquals(ActivityType.EDUCATIONS, argumentOnFail.get(0));
    }
    
    @Test
    public void uploadEducationsBecauseLastModifiedChangedTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getEducations().getSummaries().forEach(g -> {g.getLastModifiedDate().setValue(after);});
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq("0"), eq(ActivityType.EDUCATIONS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), not(eq(ActivityType.EDUCATIONS)), any(Date.class), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
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
        r.getActivitiesSummary().setEducations(new Educations());
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities());
        when(mock_s3Manager.clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EDUCATIONS))).thenReturn(true);
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EDUCATIONS));
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), any(), any(Date.class), any());        
        verify(mock_s3Manager, times(0)).uploadV2OrcidError(any(), any());      
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
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
        Employments employments = new Employments();
        employments.getSummaries().add(s2);
        r.getActivitiesSummary().setEmployments(employments);
        Employment e2 = new Employment();
        e2.setPutCode(1L);
        e2.setLastModifiedDate(new LastModifiedDate(now));

        byte [] e2ba = SerializationUtils.serialize(e2);
        
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities());
        when(mock_orcid20ApiClient.fetchActivity(eq(orcid), eq(1L), eq("employment"))).thenReturn(e2ba);
        when(mock_s3Manager.removeV2Activity(eq(orcid), any(), eq(ActivityType.EMPLOYMENTS))).thenReturn(true);
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq("1"), eq(ActivityType.EMPLOYMENTS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), not(eq(ActivityType.EMPLOYMENTS)), any(Date.class), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeEmploymentsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.EMPLOYMENTS));
        when(mock_s3Manager.removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.EMPLOYMENTS))).thenReturn(true);
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.EMPLOYMENTS));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void removeEmploymentsFailTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.EMPLOYMENTS));
        when(mock_s3Manager.removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.EMPLOYMENTS))).thenReturn(false);
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.EMPLOYMENTS));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captorOnFail = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captorOnFail.capture());
        final ArrayList<ActivityType> argumentOnFail = captorOnFail.getValue();
        assertNotNull(argumentOnFail);
        assertEquals(1, argumentOnFail.size());
        assertEquals(ActivityType.EMPLOYMENTS, argumentOnFail.get(0));
    }
    
    @Test
    public void uploadEmploymentsBecauseLastModifiedChangedTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getEmployments().getSummaries().forEach(g -> {g.getLastModifiedDate().setValue(after);});
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq("0"), eq(ActivityType.EMPLOYMENTS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), not(eq(ActivityType.EMPLOYMENTS)), any(Date.class), any());
        verify(mock_s3Manager, times(0)).removeV2Activity(eq(orcid), any(), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
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
        r.getActivitiesSummary().setEmployments(new Employments());
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities());
        when(mock_s3Manager.clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EMPLOYMENTS))).thenReturn(true);
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.EMPLOYMENTS));
        verify(mock_s3Manager, times(0)).uploadV2OrcidError(any(), any());      
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
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
        
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities());
        when(mock_orcid20ApiClient.fetchActivity(eq(orcid), eq(1L), eq("funding"))).thenReturn(e2ba);

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq("1"), eq(ActivityType.FUNDINGS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), not(eq(ActivityType.FUNDINGS)), any(Date.class), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeFundingsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.FUNDINGS));
        when(mock_s3Manager.removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.FUNDINGS))).thenReturn(true);        
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.FUNDINGS));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void removeFundingsFailTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.FUNDINGS));
        when(mock_s3Manager.removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.FUNDINGS))).thenReturn(false);        
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.FUNDINGS));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captorOnFail = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captorOnFail.capture());
        final ArrayList<ActivityType> argumentOnFail = captorOnFail.getValue();
        assertNotNull(argumentOnFail);
        assertEquals(1, argumentOnFail.size());
        assertEquals(ActivityType.FUNDINGS, argumentOnFail.get(0));
    }
    
    @Test
    public void uploadFundingsBecauseLastModifiedChangedTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getFundings().getFundingGroup().forEach(g -> {g.getFundingSummary().get(0).getLastModifiedDate().setValue(after);});
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq("0"), eq(ActivityType.FUNDINGS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), not(eq(ActivityType.FUNDINGS)), any(Date.class), any());
        verify(mock_s3Manager, times(0)).removeV2Activity(eq(orcid), any(), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
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
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities());
        when(mock_s3Manager.clearV2ActivitiesByType(eq(orcid), eq(ActivityType.FUNDINGS))).thenReturn(true);
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.FUNDINGS));
        verify(mock_s3Manager, times(0)).uploadV2OrcidError(any(), any());      
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
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
        group.getPeerReviewSummary().add(s2);
        r.getActivitiesSummary().getPeerReviews().getPeerReviewGroup().add(group);
        PeerReview e2 = new PeerReview();
        e2.setPutCode(1L);
        e2.setLastModifiedDate(new LastModifiedDate(now));

        byte [] e2ba = SerializationUtils.serialize(e2);
        
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities());
        when(mock_orcid20ApiClient.fetchActivity(eq(orcid), eq(1L), eq("peer-review"))).thenReturn(e2ba);

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq("1"), eq(ActivityType.PEER_REVIEWS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), not(eq(ActivityType.PEER_REVIEWS)), any(Date.class), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removePeerReviewsTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.PEER_REVIEWS));
        when(mock_s3Manager.removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.PEER_REVIEWS))).thenReturn(true);
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.PEER_REVIEWS));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void removePeerReviewsFailTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.PEER_REVIEWS));
        when(mock_s3Manager.removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.PEER_REVIEWS))).thenReturn(false);
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.PEER_REVIEWS));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captorOnFail = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captorOnFail.capture());
        final ArrayList<ActivityType> argumentOnFail = captorOnFail.getValue();
        assertNotNull(argumentOnFail);
        assertEquals(1, argumentOnFail.size());
        assertEquals(ActivityType.PEER_REVIEWS, argumentOnFail.get(0));
    }
    
    @Test
    public void uploadPeerReviewBecauseLastModifiedChangedTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getPeerReviews().getPeerReviewGroup().forEach(g -> {g.getPeerReviewSummary().get(0).getLastModifiedDate().setValue(after);});
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq("0"), eq(ActivityType.PEER_REVIEWS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), not(eq(ActivityType.PEER_REVIEWS)), any(Date.class), any());
        verify(mock_s3Manager, times(0)).removeV2Activity(eq(orcid), any(), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
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
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities());
        when(mock_s3Manager.clearV2ActivitiesByType(eq(orcid), eq(ActivityType.PEER_REVIEWS))).thenReturn(true);
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.PEER_REVIEWS));
        verify(mock_s3Manager, times(0)).uploadV2OrcidError(any(), any());      
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
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

        byte [] e2ba = SerializationUtils.serialize(e2);
        
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities());
        when(mock_orcid20ApiClient.fetchActivity(eq(orcid), eq(1L), eq("work"))).thenReturn(e2ba);

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq("1"), eq(ActivityType.WORKS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), not(eq(ActivityType.WORKS)), any(Date.class), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void removeWorksTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.WORKS));
        when(mock_s3Manager.removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.WORKS))).thenReturn(true);
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.WORKS));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    @Test
    public void removeWorksFailTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Remove one
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities_AddOneExtraOfType(ActivityType.WORKS));
        when(mock_s3Manager.removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.WORKS))).thenReturn(false);
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).removeV2Activity(eq(orcid), eq("1"), eq(ActivityType.WORKS));
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captorOnFail = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captorOnFail.capture());
        final ArrayList<ActivityType> argumentOnFail = captorOnFail.getValue();
        assertNotNull(argumentOnFail);
        assertEquals(1, argumentOnFail.size());
        assertEquals(ActivityType.WORKS, argumentOnFail.get(0));
    }
    
    @Test
    public void uploadWorksBecauseLastModifiedChangedTest() throws LockedRecordException, DeprecatedRecordException, ExecutionException, AmazonServiceException, JsonProcessingException,
            AmazonClientException, JAXBException {
        // Last modified changed
        Record r = getRecord();
        // Assumes there is only one element
        r.getActivitiesSummary().getWorks().getWorkGroup().forEach(g -> {g.getWorkSummary().get(0).getLastModifiedDate().setValue(after);});
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities());

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq("0"), eq(ActivityType.WORKS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), not(eq(ActivityType.WORKS)), any(Date.class), any());
        verify(mock_s3Manager, times(0)).removeV2Activity(eq(orcid), any(), any());
        verifyErrorAndClearWasntCalled();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
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
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(r);
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V2))).thenReturn(getMapOfActivities());
        when(mock_s3Manager.clearV2ActivitiesByType(eq(orcid), eq(ActivityType.WORKS))).thenReturn(true);
        
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(0)).uploadV2Activity(any(), any(), any(), any(Date.class), any());
        verify(mock_s3Manager, times(1)).clearV2ActivitiesByType(eq(orcid), eq(ActivityType.WORKS));
        verify(mock_s3Manager, times(0)).uploadV2OrcidError(any(), any());      
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api20RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }
    
    private void verifyUploadIsCalledForSummaryAndActivities() throws AmazonServiceException, JsonProcessingException, AmazonClientException, JAXBException {
        verify(mock_s3Manager, times(1)).uploadV2RecordSummary(eq(orcid), any());
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq("0"), eq(ActivityType.EDUCATIONS), any(Date.class), any());
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq("0"), eq(ActivityType.EMPLOYMENTS), any(Date.class), any());
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq("0"), eq(ActivityType.FUNDINGS), any(Date.class), any());
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq("0"), eq(ActivityType.PEER_REVIEWS), any(Date.class), any());
        verify(mock_s3Manager, times(1)).uploadV2Activity(eq(orcid), eq("0"), eq(ActivityType.WORKS), any(Date.class), any());
        verify(mock_s3Manager, times(0)).removeV2Activity(any(), any(), any());
    }

    private void verifyErrorAndClearWasntCalled() throws AmazonServiceException, JsonProcessingException, AmazonClientException, JAXBException {
        verify(mock_s3Manager, times(0)).uploadV2OrcidError(any(), any());
        verify(mock_s3Manager, times(0)).clearV2ActivitiesByType(any(), any());
    }

    private void process(String orcid) throws Exception {
        processor.update(orcid);
    }

    private Record getRecord() {
        Record claimedRecord = new Record();
        claimedRecord.setOrcidIdentifier(new OrcidIdentifier(orcid));
        History history = new History();
        history.setClaimed(true);
        claimedRecord.setHistory(history);
        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(new Educations(List.of(getEducationSummary())));
        as.setEmployments(new Employments(List.of(getEmploymentSummary())));
        as.getFundings().getFundingGroup().add(getFundingGroup());
        as.getPeerReviews().getPeerReviewGroup().add(getPeerReviewGroup());
        as.getWorks().getWorkGroup().add(getWorkGroup());
        claimedRecord.setActivitiesSummary(as);
        return claimedRecord;
    }
    
    private EducationSummary getEducationSummary() {
        EducationSummary e = new EducationSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(now));
        return e;
    }

    private EmploymentSummary getEmploymentSummary() {
        EmploymentSummary e = new EmploymentSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(now));
        return e;
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
        PeerReviewSummary p = new PeerReviewSummary();
        p.setPutCode(0L);
        p.setLastModifiedDate(new LastModifiedDate(now));
        g.getPeerReviewSummary().add(p);
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