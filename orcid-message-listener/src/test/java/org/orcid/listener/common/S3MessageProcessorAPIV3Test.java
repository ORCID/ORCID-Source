package org.orcid.listener.common;

import static org.junit.Assert.assertEquals;
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
import org.orcid.jaxb.model.v3.release.record.Activity;
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
import org.orcid.jaxb.model.v3.release.record.Works;
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
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositions;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Memberships;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
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
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;

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
    public void before() throws LockedRecordException, DeprecatedRecordException, ExecutionException {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(processor, "isV3IndexerEnabled", true);
        TargetProxyHelper.injectIntoProxy(processor, "orcid30ApiClient", mock_orcid30ApiClient);
        TargetProxyHelper.injectIntoProxy(processor, "api30RecordStatusManager", mock_api30RecordStatusManager);
        TargetProxyHelper.injectIntoProxy(processor, "s3Manager", mock_s3Manager);

        // Setup mocks
        when(mock_orcid30ApiClient.fetchPublicRecord(any())).thenReturn(getRecord());
        when(mock_s3Manager.searchActivities(eq(orcid), eq(APIVersion.V3))).thenReturn(getEmptyMapOfActivities());
        when(mock_orcid30ApiClient.fetchAffiliation(eq(orcid), eq(0L), eq(AffiliationType.DISTINCTION))).thenReturn(getDistinction());
        when(mock_orcid30ApiClient.fetchAffiliation(eq(orcid), eq(0L), eq(AffiliationType.EDUCATION))).thenReturn(getEducation());
        when(mock_orcid30ApiClient.fetchAffiliation(eq(orcid), eq(0L), eq(AffiliationType.EMPLOYMENT))).thenReturn(getEmployment());
        when(mock_orcid30ApiClient.fetchAffiliation(eq(orcid), eq(0L), eq(AffiliationType.INVITED_POSITION))).thenReturn(getInvitedPosition());
        when(mock_orcid30ApiClient.fetchAffiliation(eq(orcid), eq(0L), eq(AffiliationType.MEMBERSHIP))).thenReturn(getMembership());
        when(mock_orcid30ApiClient.fetchAffiliation(eq(orcid), eq(0L), eq(AffiliationType.QUALIFICATION))).thenReturn(getQualification());
        when(mock_orcid30ApiClient.fetchAffiliation(eq(orcid), eq(0L), eq(AffiliationType.SERVICE))).thenReturn(getService());
        when(mock_orcid30ApiClient.fetchFunding(eq(orcid), eq(0L))).thenReturn(getFunding());
        when(mock_orcid30ApiClient.fetchPeerReview(eq(orcid), eq(0L))).thenReturn(getPeerReview());
        when(mock_orcid30ApiClient.fetchResearchResource(eq(orcid), eq(0L))).thenReturn(getResearchResource());
        when(mock_orcid30ApiClient.fetchWork(eq(orcid), eq(0L))).thenReturn(getWork());
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
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), any(String.class), any(Activity.class));
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }
        verifyUploadIsCalledForSummaryAndActivities();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(false), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.containsAll(Arrays.asList(ActivityType.values())));
    }

    @Test
    public void recordSummary_JAXBExceptionTest() throws Exception {
        doThrow(new JAXBException("error")).when(mock_s3Manager).uploadV3RecordSummary(eq(orcid), any(Record.class));
        doThrow(new JAXBException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), any(String.class), any(Activity.class));
        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }
        verifyUploadIsCalledForSummaryAndActivities();
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
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertTrue(argument.isEmpty());
    }

    @Test
    public void activities_DistinctionsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getDistinction()));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.DISTINCTIONS));
    }

    @Test
    public void activities_EducationsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEducation()));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.EDUCATIONS));
    }
    
    @Test
    public void activities_EmploymentsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getEmployment()));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.EMPLOYMENTS));
    }
    
    @Test
    public void activities_FundingsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getFunding()));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.FUNDINGS));
    }
    
    @Test
    public void activities_InvitedPositionsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getInvitedPosition()));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.INVITED_POSITIONS));
    }
    
    @Test
    public void activities_MembershipsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getMembership()));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.MEMBERSHIP));
    }
    
    @Test
    public void activities_PeerReviewsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getPeerReview()));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.PEER_REVIEWS));
    }
    
    @Test
    public void activities_QualificationsFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getQualification()));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.QUALIFICATIONS));
    }
    
    @Test
    public void activities_ResearchResourcesFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getResearchResource()));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.RESEARCH_RESOURCES));
    }
    
    @Test
    public void activities_ServicesFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getService()));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.SERVICES));
    }
    
    @Test
    public void activities_WorksFailTest() throws Exception {
        doThrow(new AmazonClientException("error")).when(mock_s3Manager).uploadV3Activity(eq(orcid), eq(String.valueOf(0L)), eq((Activity) getWork()));

        try {
            process(orcid);
        } catch (Exception e) {
            fail();
        }

        verifyUploadIsCalledForSummaryAndActivities();
        final ArgumentCaptor<ArrayList<ActivityType>> captor = ArgumentCaptor.forClass(ArrayList.class);
        verify(mock_api30RecordStatusManager, times(1)).save(eq(orcid), eq(true), captor.capture());
        final ArrayList<ActivityType> argument = captor.getValue();
        assertNotNull(argument);
        assertEquals(1, argument.size());
        assertTrue(argument.contains(ActivityType.WORKS));
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
        e.setLastModifiedDate(new LastModifiedDate(after));
        AffiliationGroup<DistinctionSummary> group = new AffiliationGroup<DistinctionSummary>();
        group.getActivities().add(e);
        list.add(group);
        return list;
    }

    private List<AffiliationGroup<EducationSummary>> getEducationGroups() {
        List<AffiliationGroup<EducationSummary>> list = new ArrayList<>();
        EducationSummary e = new EducationSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        AffiliationGroup<EducationSummary> group = new AffiliationGroup<EducationSummary>();
        group.getActivities().add(e);
        list.add(group);
        return list;
    }

    private List<AffiliationGroup<EmploymentSummary>> getEmploymentGroups() {
        List<AffiliationGroup<EmploymentSummary>> list = new ArrayList<>();
        EmploymentSummary e = new EmploymentSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        AffiliationGroup<EmploymentSummary> group = new AffiliationGroup<EmploymentSummary>();
        group.getActivities().add(e);
        list.add(group);
        return list;
    }

    private List<AffiliationGroup<InvitedPositionSummary>> getInvitedPositionGroups() {
        List<AffiliationGroup<InvitedPositionSummary>> list = new ArrayList<>();
        InvitedPositionSummary e = new InvitedPositionSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        AffiliationGroup<InvitedPositionSummary> group = new AffiliationGroup<InvitedPositionSummary>();
        group.getActivities().add(e);
        list.add(group);
        return list;
    }

    private List<AffiliationGroup<MembershipSummary>> getMembershipGroups() {
        List<AffiliationGroup<MembershipSummary>> list = new ArrayList<>();
        MembershipSummary e = new MembershipSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        AffiliationGroup<MembershipSummary> group = new AffiliationGroup<MembershipSummary>();
        group.getActivities().add(e);
        list.add(group);
        return list;
    }

    private List<AffiliationGroup<QualificationSummary>> getQualificationGroups() {
        List<AffiliationGroup<QualificationSummary>> list = new ArrayList<>();
        QualificationSummary e = new QualificationSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        AffiliationGroup<QualificationSummary> group = new AffiliationGroup<QualificationSummary>();
        group.getActivities().add(e);
        list.add(group);
        return list;
    }

    private List<AffiliationGroup<ServiceSummary>> getServiceGroups() {
        List<AffiliationGroup<ServiceSummary>> list = new ArrayList<>();
        ServiceSummary e = new ServiceSummary();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        AffiliationGroup<ServiceSummary> group = new AffiliationGroup<ServiceSummary>();
        group.getActivities().add(e);
        list.add(group);
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
        PeerReviewDuplicateGroup dg = new PeerReviewDuplicateGroup();
        PeerReviewSummary p = new PeerReviewSummary();
        p.setPutCode(0L);
        p.setLastModifiedDate(new LastModifiedDate(after));
        dg.getPeerReviewSummary().add(p);
        g.getPeerReviewGroup().add(dg);
        return g;
    }

    private ResearchResourceGroup getResearchResourcesGroup() {
        ResearchResourceGroup g = new ResearchResourceGroup();
        ResearchResourceSummary p = new ResearchResourceSummary();
        p.setPutCode(0L);
        p.setLastModifiedDate(new LastModifiedDate(after));
        g.getResearchResourceSummary().add(p);
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

    private Distinction getDistinction() {
        Distinction e = new Distinction();
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

    private Employment getEmployment() {
        Employment e = new Employment();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        return e;
    }

    private InvitedPosition getInvitedPosition() {
        InvitedPosition e = new InvitedPosition();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        return e;
    }

    private Membership getMembership() {
        Membership e = new Membership();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        return e;
    }

    private Qualification getQualification() {
        Qualification e = new Qualification();
        e.setPutCode(0L);
        e.setLastModifiedDate(new LastModifiedDate(after));
        return e;
    }

    private Service getService() {
        Service e = new Service();
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

    private ResearchResource getResearchResource() {
        ResearchResource p = new ResearchResource();
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
}