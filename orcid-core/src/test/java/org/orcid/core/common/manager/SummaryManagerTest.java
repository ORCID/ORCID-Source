package org.orcid.core.common.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;

import com.oracle.truffle.api.profiles.Profile;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.common.manager.impl.SummaryManagerImpl;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.WorksCacheManager;
import org.orcid.core.manager.v3.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ExternalIdentifierManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileEmailDomainManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ResearchResourceManagerReadOnly;
import org.orcid.core.model.EmailDomains;
import org.orcid.core.model.ProfessionalActivity;
import org.orcid.core.model.RecordSummary;
import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.release.common.Organization;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.common.Year;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.FamilyName;
import org.orcid.jaxb.model.v3.release.record.FundingTitle;
import org.orcid.jaxb.model.v3.release.record.GivenNames;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResources;
import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.PeerReviewMinimizedSummary;
import org.orcid.pojo.summary.RecordSummaryPojo;
import org.orcid.utils.DateUtils;
import org.springframework.test.util.ReflectionTestUtils;

public class SummaryManagerTest {
    private final String ORCID = "0000-0000-0000-0000";
    private final String CLIENT1 = "APP-0000";
    private final String EMAIL_DOMAIN = "orcid.org";

    public SummaryManagerImpl manager = new SummaryManagerImpl();

    @Mock
    private AffiliationsManagerReadOnly affiliationsManagerReadOnlyMock;
    
    @Mock
    private RecordNameManagerReadOnly recordNameManagerReadOnlyMock;

    @Mock
    private PeerReviewManagerReadOnly peerReviewManagerReadOnlyMock;

    @Mock
    private ExternalIdentifierManagerReadOnly externalIdentifierManagerReadOnlyMock;

    @Mock
    private RecordManagerReadOnly recordManagerReadOnlyMock;
    
    @Mock
    private ProfileEntityCacheManager profileEntityCacheManagerMock;
    
    @Mock
    private ProfileFundingManagerReadOnly profileFundingManagerReadOnlyMock;
    
    @Mock
    private ProfileEntity profileEntityMock;
    
    @Mock
    private WorksCacheManager worksCacheManagerMock;
    
    @Mock
    private ResearchResourceManagerReadOnly researchResourceManagerReadOnlyMock;
    
    @Mock
    private ProfileEmailDomainManagerReadOnly profileEmailDomainManagerReadOnlyMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        XMLGregorianCalendar now = DateUtils.convertToXMLGregorianCalendar(new Date());

        // Set record Name
        Name n = new Name();
        n.setCreatedDate(new CreatedDate(now));
        n.setLastModifiedDate(new LastModifiedDate(now));
        n.setVisibility(Visibility.PUBLIC);
        n.setGivenNames(new GivenNames("ORCID"));
        n.setFamilyName(new FamilyName("Test"));
        n.setCreditName(new CreditName("ORCID Test Credit Name"));

        Mockito.when(recordNameManagerReadOnlyMock.getRecordName(Mockito.eq(ORCID))).thenReturn(n);
        ReflectionTestUtils.setField(manager, "recordNameManagerReadOnly", recordNameManagerReadOnlyMock);

        // Set external identifiers
        Mockito.when(externalIdentifierManagerReadOnlyMock.getPublicExternalIdentifiers(Mockito.eq(ORCID))).thenReturn(getPersonExternalIdentifiers());
        ReflectionTestUtils.setField(manager, "externalIdentifierManagerReadOnly", externalIdentifierManagerReadOnlyMock);        
        
        // Set affiliations
        Mockito.when(affiliationsManagerReadOnlyMock.getGroupedAffiliations(Mockito.eq(ORCID), Mockito.eq(true))).thenReturn(generateAffiliations());
        ReflectionTestUtils.setField(manager, "affiliationsManagerReadOnly", affiliationsManagerReadOnlyMock);    
        
        // Set works        
        Mockito.when(worksCacheManagerMock.getGroupedWorks(Mockito.eq(ORCID))).thenReturn(getWorkGroups());
        ReflectionTestUtils.setField(manager, "worksCacheManager", worksCacheManagerMock);    
        
        // Set fundings
        Fundings fundings = getFundings();
        Mockito.when(profileFundingManagerReadOnlyMock.getFundingSummaryList(Mockito.eq(ORCID))).thenReturn(new ArrayList<FundingSummary>());
        Mockito.when(profileFundingManagerReadOnlyMock.groupFundings(Mockito.anyList(), Mockito.eq(true))).thenReturn(fundings);
        ReflectionTestUtils.setField(manager, "profileFundingManagerReadOnly", profileFundingManagerReadOnlyMock);

        // Set peer reviews
        Mockito.when(peerReviewManagerReadOnlyMock.getPeerReviewMinimizedSummaryList(Mockito.eq(ORCID), Mockito.eq(true))).thenReturn(getPeerReviewSummaryList());
        ReflectionTestUtils.setField(manager, "peerReviewManagerReadOnly", peerReviewManagerReadOnlyMock);
        
        
        // Set ResearchResources
        ResearchResources researchResources = getResearchResources();
        Mockito.when(researchResourceManagerReadOnlyMock.getResearchResourceSummaryList(Mockito.eq(ORCID))).thenReturn(new ArrayList<ResearchResourceSummary>());
        Mockito.when(researchResourceManagerReadOnlyMock.groupResearchResources(Mockito.anyList(), Mockito.eq(true))).thenReturn(researchResources);
        ReflectionTestUtils.setField(manager, "researchResourceManagerReadOnly", researchResourceManagerReadOnlyMock);

        // Set EmailDomains
        List<ProfileEmailDomainEntity> emailDomains = getEmailDomains();
        Mockito.when(profileEmailDomainManagerReadOnlyMock.getPublicEmailDomains(Mockito.eq(ORCID))).thenReturn(emailDomains);
        ReflectionTestUtils.setField(manager, "profileEmailDomainManagerReadOnly", profileEmailDomainManagerReadOnlyMock);
        
        // Set metadata
        OrcidIdentifier oi = new OrcidIdentifier();
        oi.setUri("https://test.orcid.org/0000-0000-0000-0000");
        oi.setHost("test.orcid.org");
        Mockito.when(recordManagerReadOnlyMock.getOrcidIdentifier(Mockito.eq(ORCID))).thenReturn(oi);
        ReflectionTestUtils.setField(manager, "recordManagerReadOnly", recordManagerReadOnlyMock);
        
        Mockito.when(profileEntityMock.getDateCreated()).thenReturn(new Date(124, 0, 1));
        Mockito.when(profileEntityMock.getLastModified()).thenReturn(new Date(124, 11, 31));
        Mockito.when(profileEntityCacheManagerMock.retrieve(ORCID)).thenReturn(profileEntityMock);
        ReflectionTestUtils.setField(manager, "profileEntityCacheManager", profileEntityCacheManagerMock);
    }

    @Test
    public void generateAffiliationsSummaryTest() {
        RecordSummary rs = new RecordSummary();
        Map<AffiliationType, List<AffiliationGroup<AffiliationSummary>>> affiliations = generateAffiliations();
        Mockito.when(affiliationsManagerReadOnlyMock.getGroupedAffiliations(Mockito.eq(ORCID), Mockito.eq(true))).thenReturn(affiliations);
        
        manager.generateAffiliationsSummary(rs, ORCID);
        assertNotNull(rs.getEmployments());
        assertEquals(Integer.valueOf(3), rs.getEmployments().getCount());
        assertEquals(3, rs.getEmployments().getEmployments().size());
        
        
        assertEquals(3, rs.getProfessionalActivities().getProfessionalActivities().size());
        // 3 of every professional activity type
        assertEquals(Integer.valueOf(12), rs.getProfessionalActivities().getCount());
    }
    
    @Test
    public void generateAffiliationsSummary_EmptyTest() {
        RecordSummary rs = new RecordSummary();
        Map<AffiliationType, List<AffiliationGroup<AffiliationSummary>>> affiliations = generateAffiliations();
        affiliations.remove(AffiliationType.DISTINCTION);
        affiliations.remove(AffiliationType.EDUCATION);
        affiliations.remove(AffiliationType.EMPLOYMENT);
        affiliations.remove(AffiliationType.INVITED_POSITION);
        affiliations.remove(AffiliationType.MEMBERSHIP);
        affiliations.remove(AffiliationType.QUALIFICATION);
        affiliations.remove(AffiliationType.SERVICE);
        
        Mockito.when(affiliationsManagerReadOnlyMock.getGroupedAffiliations(Mockito.eq(ORCID), Mockito.eq(true))).thenReturn(affiliations);
        manager.generateAffiliationsSummary(rs, ORCID);
        assertEquals(Integer.valueOf(0), rs.getEmployments().getCount());
        assertNull(rs.getEmployments().getEmployments());
        assertEquals(Integer.valueOf(0), rs.getProfessionalActivities().getCount());
        assertNull(rs.getProfessionalActivities().getProfessionalActivities());        
    }
    
    @Test
    public void generateAffiliationsSummary_EmploymentOnlyTest() {
        RecordSummary rs = new RecordSummary();
        Map<AffiliationType, List<AffiliationGroup<AffiliationSummary>>> affiliations = generateAffiliations();
        affiliations.remove(AffiliationType.DISTINCTION);
        affiliations.remove(AffiliationType.EDUCATION);
        affiliations.remove(AffiliationType.INVITED_POSITION);
        affiliations.remove(AffiliationType.MEMBERSHIP);
        affiliations.remove(AffiliationType.QUALIFICATION);
        affiliations.remove(AffiliationType.SERVICE);
        
        Mockito.when(affiliationsManagerReadOnlyMock.getGroupedAffiliations(Mockito.eq(ORCID), Mockito.eq(true))).thenReturn(affiliations);
        manager.generateAffiliationsSummary(rs, ORCID);
        assertEquals(Integer.valueOf(3), rs.getEmployments().getCount());
        assertEquals(3, rs.getEmployments().getEmployments().size());
        assertEquals(Integer.valueOf(0), rs.getProfessionalActivities().getCount());
        assertNull(rs.getProfessionalActivities().getProfessionalActivities());
    }
    
    @Test
    public void generateAffiliationsSummary_EducationOnlyTest() {
        RecordSummary rs = new RecordSummary();
        Map<AffiliationType, List<AffiliationGroup<AffiliationSummary>>> affiliations = generateAffiliations();
        affiliations.remove(AffiliationType.DISTINCTION);
        affiliations.remove(AffiliationType.EMPLOYMENT);
        affiliations.remove(AffiliationType.INVITED_POSITION);
        affiliations.remove(AffiliationType.MEMBERSHIP);
        affiliations.remove(AffiliationType.QUALIFICATION);
        affiliations.remove(AffiliationType.SERVICE);
        
        Mockito.when(affiliationsManagerReadOnlyMock.getGroupedAffiliations(Mockito.eq(ORCID), Mockito.eq(true))).thenReturn(affiliations);
        manager.generateAffiliationsSummary(rs, ORCID);
        assertEquals(Integer.valueOf(0), rs.getEmployments().getCount());
        assertNull(rs.getEmployments().getEmployments());
        assertEquals(Integer.valueOf(0), rs.getProfessionalActivities().getCount());
        assertNull(rs.getProfessionalActivities().getProfessionalActivities());
    }
    
    @Test
    public void generateAffiliationsSummary_InvitedPositionOnlyTest() {
        RecordSummary rs = new RecordSummary();
        Map<AffiliationType, List<AffiliationGroup<AffiliationSummary>>> affiliations = generateAffiliations();
        affiliations.remove(AffiliationType.DISTINCTION);
        affiliations.remove(AffiliationType.EMPLOYMENT);
        affiliations.remove(AffiliationType.MEMBERSHIP);
        affiliations.remove(AffiliationType.QUALIFICATION);
        affiliations.remove(AffiliationType.SERVICE);
        
        Mockito.when(affiliationsManagerReadOnlyMock.getGroupedAffiliations(Mockito.eq(ORCID), Mockito.eq(true))).thenReturn(affiliations);
        manager.generateAffiliationsSummary(rs, ORCID);
        assertEquals(Integer.valueOf(0), rs.getEmployments().getCount());
        assertNull(rs.getEmployments().getEmployments());
        assertEquals(Integer.valueOf(3), rs.getProfessionalActivities().getCount());
        assertEquals(3, rs.getProfessionalActivities().getProfessionalActivities().size());
        for(ProfessionalActivity pa : rs.getProfessionalActivities().getProfessionalActivities()) {
            assertEquals(AffiliationType.INVITED_POSITION.value(), pa.getType());
        }
    }
    
    @Test
    public void generateAffiliationsSummary_ProfessionalActivitiesOnlyTest() {
        RecordSummary rs = new RecordSummary();
        Map<AffiliationType, List<AffiliationGroup<AffiliationSummary>>> affiliations = generateAffiliations();
        affiliations.remove(AffiliationType.EMPLOYMENT);
        
        Mockito.when(affiliationsManagerReadOnlyMock.getGroupedAffiliations(Mockito.eq(ORCID), Mockito.eq(true))).thenReturn(affiliations);
        manager.generateAffiliationsSummary(rs, ORCID);
        assertEquals(Integer.valueOf(0), rs.getEmployments().getCount());
        assertNull(rs.getEmployments().getEmployments());
        assertEquals(Integer.valueOf(12), rs.getProfessionalActivities().getCount());
        assertEquals(3, rs.getProfessionalActivities().getProfessionalActivities().size());
        for(ProfessionalActivity pa : rs.getProfessionalActivities().getProfessionalActivities()) {
            assertNotEquals(AffiliationType.EMPLOYMENT, pa.getType());
        }
    }

    @Test
    public void generateExternalIdentifiersSummaryTest() {
        RecordSummary rs = new RecordSummary();
        manager.generateExternalIdentifiersSummary(rs, ORCID);
        assertEquals(1, rs.getExternalIdentifiers().getExternalIdentifiers().size());
        assertEquals(Long.valueOf(0), rs.getExternalIdentifiers().getExternalIdentifiers().get(0).getPutCode());
        assertEquals("0000", rs.getExternalIdentifiers().getExternalIdentifiers().get(0).getExternalIdValue());
    }
    
    @Test
    public void generateExternalIdentifiersSummary_NullTest() {
        RecordSummary rs = new RecordSummary();
        Mockito.when(externalIdentifierManagerReadOnlyMock.getPublicExternalIdentifiers(Mockito.eq(ORCID))).thenReturn(null);
        
        manager.generateExternalIdentifiersSummary(rs, ORCID);        
        assertNull(rs.getExternalIdentifiers());
    }
    
    @Test
    public void generateExternalIdentifiersSummary_EmptyTest() {
        RecordSummary rs = new RecordSummary();
        Mockito.when(externalIdentifierManagerReadOnlyMock.getPublicExternalIdentifiers(Mockito.eq(ORCID))).thenReturn(new PersonExternalIdentifiers());
        
        manager.generateExternalIdentifiersSummary(rs, ORCID);
        assertNull(rs.getExternalIdentifiers());
    }
    
    @Test
    public void generateWorksSummaryTest() {
        RecordSummary rs = new RecordSummary();
        manager.generateWorksSummary(rs, ORCID);
        assertEquals(Integer.valueOf(0), rs.getWorks().getSelfAssertedCount());
        assertEquals(Integer.valueOf(3), rs.getWorks().getValidatedCount());
    }
    
    @Test
    public void generateWorksSummary_OboValidatedTest() {
        RecordSummary rs = new RecordSummary();
        Works works = getWorkGroups();
        Source s = new Source();
        s.setSourceClientId(new SourceClientId(CLIENT1));
        s.setAssertionOriginClientId(new SourceClientId(CLIENT1));
        for(WorkGroup wg : works.getWorkGroup()) {
            for(WorkSummary ws : wg.getWorkSummary()) {
                ws.setSource(s);
            }
        }
        Mockito.when(worksCacheManagerMock.getGroupedWorks(Mockito.eq(ORCID))).thenReturn(works);
        
        manager.generateWorksSummary(rs, ORCID);
        assertEquals(Integer.valueOf(0), rs.getWorks().getSelfAssertedCount());
        assertEquals(Integer.valueOf(3), rs.getWorks().getValidatedCount());
    }
    
    @Test
    public void generateWorksSummary_SelfAssertedOnlyTest() {
        RecordSummary rs = new RecordSummary();
        Works works = getWorkGroups();
        Source s = new Source();
        s.setSourceOrcid(new SourceOrcid(ORCID));
        for(WorkGroup wg : works.getWorkGroup()) {
            for(WorkSummary ws : wg.getWorkSummary()) {
                ws.setSource(s);
            }
        }
        Mockito.when(worksCacheManagerMock.getGroupedWorks(Mockito.eq(ORCID))).thenReturn(works);
        
        manager.generateWorksSummary(rs, ORCID);
        assertEquals(Integer.valueOf(3), rs.getWorks().getSelfAssertedCount());
        assertEquals(Integer.valueOf(0), rs.getWorks().getValidatedCount());
    }
    
    @Test
    public void generateWorksSummary_OboSelfAssertedOnlyTest() {
        RecordSummary rs = new RecordSummary();
        Works works = getWorkGroups();
        Source s = new Source();
        s.setSourceClientId(new SourceClientId(CLIENT1));
        s.setAssertionOriginOrcid(new SourceOrcid(ORCID));
        for(WorkGroup wg : works.getWorkGroup()) {
            for(WorkSummary ws : wg.getWorkSummary()) {
                ws.setSource(s);
            }
        }
        Mockito.when(worksCacheManagerMock.getGroupedWorks(Mockito.eq(ORCID))).thenReturn(works);
        
        manager.generateWorksSummary(rs, ORCID);
        assertEquals(Integer.valueOf(3), rs.getWorks().getSelfAssertedCount());
        assertEquals(Integer.valueOf(0), rs.getWorks().getValidatedCount());
    }
    
    @Test
    public void generateFundingSummaryTest() {
        RecordSummary rs = new RecordSummary();
        manager.generateFundingSummary(rs, ORCID);
        assertEquals(Integer.valueOf(0), rs.getFundings().getSelfAssertedCount());
        assertEquals(Integer.valueOf(3), rs.getFundings().getValidatedCount());
    }
    
    @Test
    public void generateFundingSummary_OboValidatedTest() {
        RecordSummary rs = new RecordSummary();
        Source s = new Source();
        s.setSourceClientId(new SourceClientId(CLIENT1));
        s.setAssertionOriginClientId(new SourceClientId(CLIENT1));
        Fundings fundings = getFundings();
        for(FundingGroup fg : fundings.getFundingGroup()) {
            for(FundingSummary fs : fg.getFundingSummary()) {
                fs.setSource(s);
            }
        }
        Mockito.when(profileFundingManagerReadOnlyMock.groupFundings(Mockito.anyList(), Mockito.eq(true))).thenReturn(fundings);
        
        manager.generateFundingSummary(rs, ORCID);
        assertEquals(Integer.valueOf(0), rs.getFundings().getSelfAssertedCount());
        assertEquals(Integer.valueOf(3), rs.getFundings().getValidatedCount());        
    }
    
    @Test
    public void generateFundingSummary_SelfAssertedTest() {
        RecordSummary rs = new RecordSummary();
        Source s = new Source();
        s.setSourceOrcid(new SourceOrcid(ORCID));
        Fundings fundings = getFundings();
        for(FundingGroup fg : fundings.getFundingGroup()) {
            for(FundingSummary fs : fg.getFundingSummary()) {
                fs.setSource(s);
            }
        }
        Mockito.when(profileFundingManagerReadOnlyMock.groupFundings(Mockito.anyList(), Mockito.eq(true))).thenReturn(fundings);
        
        manager.generateFundingSummary(rs, ORCID);
        assertEquals(Integer.valueOf(3), rs.getFundings().getSelfAssertedCount());
        assertEquals(Integer.valueOf(0), rs.getFundings().getValidatedCount());;        
    }
    
    @Test
    public void generateFundingSummary_OboSelfAssertedTest() {
        RecordSummary rs = new RecordSummary();
        Source s = new Source();
        s.setSourceClientId(new SourceClientId(CLIENT1));
        s.setAssertionOriginOrcid(new SourceOrcid(ORCID));
        Fundings fundings = getFundings();
        for(FundingGroup fg : fundings.getFundingGroup()) {
            for(FundingSummary fs : fg.getFundingSummary()) {
                fs.setSource(s);
            }
        }
        Mockito.when(profileFundingManagerReadOnlyMock.groupFundings(Mockito.anyList(), Mockito.eq(true))).thenReturn(fundings);
        
        manager.generateFundingSummary(rs, ORCID);
        assertEquals(Integer.valueOf(3), rs.getFundings().getSelfAssertedCount());
        assertEquals(Integer.valueOf(0), rs.getFundings().getValidatedCount());
    }
    
    @Test
    public void generatePeerReviewSummaryTest() {
        RecordSummary rs = new RecordSummary();
        manager.generatePeerReviewSummary(rs, ORCID);
        // Each peer review group have 1 self asserted peer review and 1 user obo asserted peer review
        // So, we have 3 groups = 6 self asserted peer reviews in total
        assertEquals(Integer.valueOf(2), rs.getPeerReviews().getSelfAssertedCount());
        assertEquals(Integer.valueOf(4), rs.getPeerReviews().getPeerReviewPublicationGrants());
        assertEquals(Integer.valueOf(16), rs.getPeerReviews().getTotal());
    }
    
    @Test
    public void generatePeerReviewSummary_OboValidatedTest() {
        RecordSummary rs = new RecordSummary();
        List<PeerReviewMinimizedSummary> peerReviews = getPeerReviewSummaryList();
        for(PeerReviewMinimizedSummary pr : peerReviews) {
            pr.setClientSourceId(CLIENT1);
            pr.setAssertionOriginSourceId(CLIENT1);
            pr.setSourceId(null);
        }
        Mockito.when(peerReviewManagerReadOnlyMock.getPeerReviewMinimizedSummaryList(Mockito.eq(ORCID), Mockito.eq(true))).thenReturn(peerReviews);
        
        manager.generatePeerReviewSummary(rs, ORCID);
        // Each peer review group have 1 self asserted peer review and 1 user obo asserted peer review
        // So, we have 3 groups = 6 self asserted peer reviews in total
        assertEquals(Integer.valueOf(0), rs.getPeerReviews().getSelfAssertedCount());
        assertEquals(Integer.valueOf(4), rs.getPeerReviews().getPeerReviewPublicationGrants());
        assertEquals(Integer.valueOf(16), rs.getPeerReviews().getTotal());      
    }
    
    @Test
    public void generatePeerReviewSummary_SelfAssertedTest() {
        RecordSummary rs = new RecordSummary();
        List<PeerReviewMinimizedSummary> peerReviews = getPeerReviewSummaryList();
        for(PeerReviewMinimizedSummary pr : peerReviews) {
            pr.setClientSourceId(null);
            pr.setAssertionOriginSourceId(null);
            pr.setSourceId(ORCID);
        }
        Mockito.when(peerReviewManagerReadOnlyMock.getPeerReviewMinimizedSummaryList(Mockito.eq(ORCID), Mockito.eq(true))).thenReturn(peerReviews);
        
        manager.generatePeerReviewSummary(rs, ORCID);
        // Each peer review group have 1 self asserted peer review and 1 user obo asserted peer review
        // So, we have 3 groups = 6 self asserted peer reviews in total
        assertEquals(Integer.valueOf(4), rs.getPeerReviews().getSelfAssertedCount());
        assertEquals(Integer.valueOf(4), rs.getPeerReviews().getPeerReviewPublicationGrants());
        assertEquals(Integer.valueOf(16), rs.getPeerReviews().getTotal());       
    }
    
    @Test
    public void generatePeerReviewSummary_Test() {
        RecordSummary rs = new RecordSummary();
        List<PeerReviewMinimizedSummary> peerReviews = getPeerReviewSummaryList();
        for(PeerReviewMinimizedSummary pr : peerReviews) {
            pr.setClientSourceId(CLIENT1);
            pr.setAssertionOriginSourceId(ORCID);
            pr.setSourceId(null);
        }
        Mockito.when(peerReviewManagerReadOnlyMock.getPeerReviewMinimizedSummaryList(Mockito.eq(ORCID), Mockito.eq(true))).thenReturn(peerReviews);
        
        manager.generatePeerReviewSummary(rs, ORCID);
        // Each peer review group have 1 self asserted peer review and 1 user obo asserted peer review
        // So, we have 3 groups = 6 self asserted peer reviews in total
        assertEquals(Integer.valueOf(4), rs.getPeerReviews().getSelfAssertedCount());
        assertEquals(Integer.valueOf(4), rs.getPeerReviews().getPeerReviewPublicationGrants());
        assertEquals(Integer.valueOf(16), rs.getPeerReviews().getTotal());        
    }
    
    /**
     * 
     * Summary Tests
     * 
     * */
    @Test
    public void getSummaryTest() {
        RecordSummary rs = manager.getRecordSummary(ORCID);
        assertEquals("test.orcid.org", rs.getOrcidIdentifier().getHost());
        assertEquals("https://test.orcid.org/" + ORCID, rs.getOrcidIdentifier().getUri());
        assertEquals(2024, rs.getCreatedDate().getValue().getYear());
        assertEquals(1, rs.getCreatedDate().getValue().getMonth());
        assertEquals(1, rs.getCreatedDate().getValue().getDay());
        
        
        assertEquals(2024, rs.getLastModifiedDate().getValue().getYear());
        assertEquals(12, rs.getLastModifiedDate().getValue().getMonth());
        assertEquals(31, rs.getLastModifiedDate().getValue().getDay());
        
        // Affiliations
        assertEquals(Integer.valueOf(3), rs.getEmployments().getCount());
        assertEquals(3, rs.getEmployments().getEmployments().size());
        assertEquals(Integer.valueOf(12), rs.getProfessionalActivities().getCount());
        assertEquals(3, rs.getProfessionalActivities().getProfessionalActivities().size()); 
        
        // External identifiers 
        assertEquals(1, rs.getExternalIdentifiers().getExternalIdentifiers().size());
        // Works
        assertEquals(Integer.valueOf(0), rs.getWorks().getSelfAssertedCount());
        assertEquals(Integer.valueOf(3), rs.getWorks().getValidatedCount());
        
        // Funding
        assertEquals(Integer.valueOf(0), rs.getFundings().getSelfAssertedCount());
        assertEquals(Integer.valueOf(3), rs.getFundings().getValidatedCount());
        
        // Peer review
        assertEquals(Integer.valueOf(2), rs.getPeerReviews().getSelfAssertedCount());
        assertEquals(Integer.valueOf(4), rs.getPeerReviews().getPeerReviewPublicationGrants());
        assertEquals(Integer.valueOf(16), rs.getPeerReviews().getTotal());

        // Email domains
        assertEquals("2024-12-20", rs.getEmailDomains().getEmailDomains().get(0).getVerificationDate().toString());
        assertEquals(1, rs.getEmailDomains().getEmailDomains().size());
    }       
    
    /**
     * 
     * POJO Tests
     * 
     * */
    
    @Test
    public void getSummaryPojoTest() {
        RecordSummaryPojo rs = manager.getRecordSummaryPojo(ORCID);
        assertEquals("https://test.orcid.org/" + ORCID, rs.getOrcid());
        assertEquals("2024-01-01", rs.getCreation());
        assertEquals("2024-12-31", rs.getLastModified());
        // Affiliations
        assertEquals(3, rs.getEmploymentAffiliations().size());
        assertEquals(3, rs.getEmploymentAffiliationsCount());
        assertEquals(3, rs.getProfessionalActivities().size());
        assertEquals(12, rs.getProfessionalActivitiesCount()); 
        // External identifiers 
        assertEquals(1, rs.getExternalIdentifiers().size());
        // Works
        assertEquals(0, rs.getSelfAssertedWorks());
        assertEquals(3, rs.getValidatedWorks());
        // Funding
        assertEquals(0, rs.getSelfAssertedFunds());
        assertEquals(3, rs.getValidatedFunds());
        // Peer review
        assertEquals(2, rs.getSelfAssertedPeerReviews());
        assertEquals(4, rs.getPeerReviewPublicationGrants());
        assertEquals(16, rs.getPeerReviewsTotal());
        // Email domain
        assertEquals(1, rs.getEmailDomains().size());
        assertEquals("2024-12-20", rs.getEmailDomains().get(0).getVerificationDate());

    }
    
    private PersonExternalIdentifiers getPersonExternalIdentifiers() {
        PersonExternalIdentifiers peis = new PersonExternalIdentifiers();
        PersonExternalIdentifier pei = new PersonExternalIdentifier();
        pei.setPutCode(0L);
        pei.setSource(new Source(ORCID));
        pei.setValue("0000");
        peis.getExternalIdentifiers().add(pei);
        return peis;
    }
    
    private ResearchResources  getResearchResources() {
        ResearchResources researchResources = new ResearchResources();
        
        return researchResources;
    }
    
    private List<ProfileEmailDomainEntity>  getEmailDomains() {
        List<ProfileEmailDomainEntity> emailDomains = new ArrayList<ProfileEmailDomainEntity>();
        ProfileEmailDomainEntity emailDomain = new ProfileEmailDomainEntity();
        emailDomain.setEmailDomain(EMAIL_DOMAIN);
        emailDomain.setOrcid(ORCID);
        emailDomain.setDateCreated(new Date(124, 11, 20));
        emailDomains.add(emailDomain);
        return emailDomains;
    }
    
     

    private List<AffiliationGroup<AffiliationSummary>> getAffiliations(AffiliationType affiliationType) {
        List<AffiliationGroup<AffiliationSummary>> affiliationGroups = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            AffiliationGroup<AffiliationSummary> group = new AffiliationGroup<AffiliationSummary>();
            
            for(int j = 0; j < 3; j++) {
                AffiliationSummary summary = getAffiliationSummary(affiliationType, affiliationType.name() + "-" + j, CLIENT1, Long.valueOf(j));
                group.getActivities().add(summary);
            }
            
            affiliationGroups.add(group);
        }
        return affiliationGroups;
    }

    private AffiliationSummary getAffiliationSummary(AffiliationType affiliationType, String affiliationName, String sourceId, Long putCode) {
        AffiliationSummary summary = null;
        switch(affiliationType) {
        case DISTINCTION:
            summary = new DistinctionSummary();
            break;
        case EDUCATION:
            summary = new EducationSummary();
            break;
        case EMPLOYMENT:
            summary = new EmploymentSummary();
            break;
        case INVITED_POSITION:
            summary = new InvitedPositionSummary();
            break;
        case MEMBERSHIP:
            summary = new MembershipSummary();
            break;
        case QUALIFICATION:
            summary = new QualificationSummary();
            break;
        case SERVICE:
            summary = new ServiceSummary();
            break;
        }
        summary.setVisibility(Visibility.PUBLIC);
        Organization org = new Organization();
        org.setName(affiliationName);
        summary.setOrganization(org);
        summary.setPutCode(putCode);   
        summary.setDisplayIndex(String.valueOf(putCode));
        summary.setEndDate(new FuzzyDate(new Year(2012), null, null));
        Source source = new Source();
        source.setSourceClientId(new SourceClientId(sourceId));
        summary.setSource(source);
        return summary;
    }

    private Works getWorkGroups() {
        Works works = new Works();
        for (int i = 0; i < 3; i++) {
            WorkGroup wg = new WorkGroup();
            
            // Add 4 works per group
            // First source is the client
            // Second source is the user
            // Third source is a client through OBO
            // Fourth source is the user through OBO
            for (int j = 0; j < 4; j++) {
                WorkSummary ws = new WorkSummary();
                ws.setVisibility(Visibility.PUBLIC);
                WorkTitle wt = new WorkTitle();
                wt.setTitle(new Title("Work-" + j));
                ws.setTitle(wt);
                Source source = new Source();
                ws.setSource(source);
                switch (j) {
                case 0:
                    source.setSourceClientId(new SourceClientId(CLIENT1));
                    break;
                case 1:
                    source.setSourceOrcid(new SourceOrcid(ORCID));
                    break;
                case 2:
                    source.setAssertionOriginClientId(new SourceClientId(CLIENT1));
                    break;
                case 3:
                    source.setAssertionOriginOrcid(new SourceOrcid(ORCID));
                    break;
                }
                wg.getWorkSummary().add(ws);
            }

            works.getWorkGroup().add(wg);
        }
        return works;
    }

    private Fundings getFundings() {
        Fundings fundings = new Fundings();

        for (int i = 0; i < 3; i++) {
            FundingGroup fg = new FundingGroup();
            
            // Add 4 funding per group
            // First source is the client
            // Second source is the user
            // Third source is a client through OBO
            // Fourth source is the user through OBO
            for (int j = 0; j < 4; j++) {
                FundingSummary fs = new FundingSummary();
                FundingTitle ft = new FundingTitle();
                ft.setTitle(new Title("Funding-" + j));
                fs.setTitle(ft);
                Source source = new Source();
                fs.setSource(source);
                switch (j) {
                case 0:
                    source.setSourceClientId(new SourceClientId(CLIENT1));
                    break;
                case 1:
                    source.setSourceOrcid(new SourceOrcid(ORCID));
                    break;
                case 2:
                    source.setAssertionOriginClientId(new SourceClientId(CLIENT1));
                    break;
                case 3:
                    source.setAssertionOriginOrcid(new SourceOrcid(ORCID));
                    break;
                }
                fg.getFundingSummary().add(fs);
            }

            fundings.getFundingGroup().add(fg);
        }

        return fundings;
    }

    private List<PeerReviewMinimizedSummary> getPeerReviewSummaryList() {
        List<PeerReviewMinimizedSummary> peerReviews = new ArrayList<PeerReviewMinimizedSummary>();
        for(int i = 0; i < 4; i++) {
            BigInteger groupId = BigInteger.valueOf(i);
            String groupIdValue = "group-id-" + i;
            // First source is the client
            // Second source is the user
            // Third source is a client through OBO
            // Fourth source is the user through OBO
            PeerReviewMinimizedSummary pr = new PeerReviewMinimizedSummary(ORCID, groupId, groupIdValue, BigInteger.valueOf(0), null, null, 0);            
            pr.addPutCode(BigInteger.valueOf(1));
            pr.addPutCode(BigInteger.valueOf(2));
            pr.addPutCode(BigInteger.valueOf(3));
            switch (i) {
            case 0:
                pr.setSourceId(CLIENT1);
                break;
            case 1:
                pr.setSourceId(ORCID);
                break;
            case 2:
                pr.setAssertionOriginSourceId(CLIENT1);
                break;
            case 3:
                pr.setAssertionOriginSourceId(ORCID);
                break;
            }
            peerReviews.add(pr);              
        }        
        return peerReviews;
    }
    
    private Map<AffiliationType, List<AffiliationGroup<AffiliationSummary>>> generateAffiliations() {
        Map<AffiliationType, List<AffiliationGroup<AffiliationSummary>>> affiliations = new HashMap<AffiliationType, List<AffiliationGroup<AffiliationSummary>>>();

        affiliations.put(AffiliationType.DISTINCTION, getAffiliations(AffiliationType.DISTINCTION));
        affiliations.put(AffiliationType.EDUCATION, getAffiliations(AffiliationType.EDUCATION));
        affiliations.put(AffiliationType.EMPLOYMENT, getAffiliations(AffiliationType.EMPLOYMENT));
        affiliations.put(AffiliationType.INVITED_POSITION, getAffiliations(AffiliationType.INVITED_POSITION));
        affiliations.put(AffiliationType.MEMBERSHIP, getAffiliations(AffiliationType.MEMBERSHIP));
        affiliations.put(AffiliationType.QUALIFICATION, getAffiliations(AffiliationType.QUALIFICATION));
        affiliations.put(AffiliationType.SERVICE, getAffiliations(AffiliationType.SERVICE)); 
        return affiliations;
    }
}
