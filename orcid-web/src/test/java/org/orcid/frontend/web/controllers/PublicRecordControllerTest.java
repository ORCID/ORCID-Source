package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.read_only.ExternalIdentifierManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.frontend.web.pagination.Page;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.FamilyName;
import org.orcid.jaxb.model.v3.release.record.GivenNames;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.PeerReviewMinimizedSummary;
import org.orcid.pojo.ajaxForm.ActivityExternalIdentifier;
import org.orcid.pojo.ajaxForm.AffiliationForm;
import org.orcid.pojo.ajaxForm.AffiliationGroupContainer;
import org.orcid.pojo.ajaxForm.AffiliationGroupForm;
import org.orcid.pojo.ajaxForm.FundingForm;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.WorkForm;
import org.orcid.pojo.grouping.FundingGroup;
import org.orcid.pojo.grouping.WorkGroup;
import org.orcid.pojo.summary.RecordSummary;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@ContextConfiguration(locations = { "classpath:test-frontend-web-servlet.xml" })
public class PublicRecordControllerTest {

    private final String ORCID = "0000-0000-0000-0000";
    private final String CLIENT1 = "APP-0000";

    public PublicRecordController controller = new PublicRecordController();

    @Mock
    private RecordNameManagerReadOnly recordNameManagerReadOnlyMock;

    @Mock
    private PublicProfileController publicProfileControllerMock;

    @Mock
    private PeerReviewManagerReadOnly peerReviewManagerReadOnlyMock;

    @Mock
    private ExternalIdentifierManagerReadOnly externalIdentifierManagerReadOnlyMock;

    @Mock
    private RecordManagerReadOnly recordManagerReadOnlyMock;
    
    @Mock
    private ProfileEntityCacheManager profileEntityCacheManagerMock;
    
    @Mock
    private ProfileEntity profileEntityMock;

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
        ReflectionTestUtils.setField(controller, "recordNameManagerReadOnly", recordNameManagerReadOnlyMock);

        // Set external identifiers
        PersonExternalIdentifiers personExternalIdentifiers = getPersonExternalIdentifiers();
        Mockito.when(externalIdentifierManagerReadOnlyMock.getPublicExternalIdentifiers(Mockito.eq(ORCID))).thenReturn(personExternalIdentifiers);
        ReflectionTestUtils.setField(controller, "externalIdentifierManagerReadOnly", externalIdentifierManagerReadOnlyMock);        
        
        // Set affiliations
        AffiliationGroupContainer agc = new AffiliationGroupContainer();
        Map<AffiliationType, List<AffiliationGroupForm>> affiliationGroups = new HashMap<AffiliationType, List<AffiliationGroupForm>>();

        affiliationGroups.put(AffiliationType.DISTINCTION, getAffiliationGroup(AffiliationType.DISTINCTION));
        affiliationGroups.put(AffiliationType.EDUCATION, getAffiliationGroup(AffiliationType.EDUCATION));
        affiliationGroups.put(AffiliationType.EMPLOYMENT, getAffiliationGroup(AffiliationType.EMPLOYMENT));
        affiliationGroups.put(AffiliationType.INVITED_POSITION, getAffiliationGroup(AffiliationType.INVITED_POSITION));
        affiliationGroups.put(AffiliationType.MEMBERSHIP, getAffiliationGroup(AffiliationType.MEMBERSHIP));
        affiliationGroups.put(AffiliationType.QUALIFICATION, getAffiliationGroup(AffiliationType.QUALIFICATION));
        affiliationGroups.put(AffiliationType.SERVICE, getAffiliationGroup(AffiliationType.SERVICE));
        agc.setAffiliationGroups(affiliationGroups);

        Mockito.when(publicProfileControllerMock.getGroupedAffiliations(Mockito.eq(ORCID))).thenReturn(agc);

        // Set works
        Page<org.orcid.pojo.grouping.WorkGroup> works = getWorkGroups();
        Mockito.when(publicProfileControllerMock.getAllWorkGroupsJson(Mockito.eq(ORCID), Mockito.eq("date"), Mockito.eq(true))).thenReturn(works);

        // Set fundings
        List<FundingGroup> fundings = getFundingGroups();
        Mockito.when(publicProfileControllerMock.getFundingsJson(Mockito.eq(ORCID), Mockito.eq("date"), Mockito.eq(true))).thenReturn(fundings);
        ReflectionTestUtils.setField(controller, "publicProfileController", publicProfileControllerMock);

        // Set peer reviews
        List<PeerReviewMinimizedSummary> peerReviews = getPeerReviewSummaryList();
        Mockito.when(peerReviewManagerReadOnlyMock.getPeerReviewMinimizedSummaryList(Mockito.eq(ORCID), Mockito.eq(true))).thenReturn(peerReviews);
        ReflectionTestUtils.setField(controller, "peerReviewManagerReadOnly", peerReviewManagerReadOnlyMock);

        // Set metadata
        OrcidIdentifier oi = new OrcidIdentifier();
        oi.setUri("https://test.orcid.org/0000-0000-0000-0000");
        Mockito.when(recordManagerReadOnlyMock.getOrcidIdentifier(Mockito.eq(ORCID))).thenReturn(oi);
        ReflectionTestUtils.setField(controller, "recordManagerReadOnly", recordManagerReadOnlyMock);
        
        Mockito.when(profileEntityMock.getDateCreated()).thenReturn(new Date(2024, 1, 1));
        Mockito.when(profileEntityMock.getLastModified()).thenReturn(new Date(2024, 1, 1));
        Mockito.when(profileEntityCacheManagerMock.retrieve(ORCID)).thenReturn(profileEntityMock);
        ReflectionTestUtils.setField(controller, "profileEntityCacheManager", profileEntityCacheManagerMock);
    }

    @Test
    public void getSummaryTest() {
        RecordSummary summary = controller.getSummary(ORCID);
        assertNotNull(summary);
        assertEquals(3, summary.getEmploymentAffiliationsCount());
        assertEquals(3, summary.getEmploymentAffiliations().size());
        boolean found0 = false, found1 = false, found2 = false;
        for(int i = 0; i < 3; i++ ) {
            String name = summary.getEmploymentAffiliations().get(i).getOrganizationName();
            switch(name) {
            case "EMPLOYMENT-0":
                found0 = true;
                break;
            case "EMPLOYMENT-1":
                found1 = true;
                break;
            case "EMPLOYMENT-2":
                found2 = true;
                break;
            }
        }
        assertTrue(found0);
        assertTrue(found1);
        assertTrue(found2);
        
        assertEquals(1, summary.getExternalIdentifiers().size());
        assertEquals("0", summary.getExternalIdentifiers().get(0).getId());
        assertEquals("0000", summary.getExternalIdentifiers().get(0).getReference());
        
        assertEquals("ORCID Test Credit Name", summary.getName());
        assertEquals("https://test.orcid.org/0000-0000-0000-0000", summary.getOrcid());
        
        //TODO
        fail();
        summary.getPeerReviewPublicationGrants();
        summary.getPeerReviewsTotal();
        summary.getProfessionalActivities();
        summary.getProfessionalActivitiesCount();
        summary.getSelfAssertedFunds();
        summary.getSelfAssertedPeerReviews();
        summary.getSelfAssertedWorks();
        summary.getStatus();
        summary.getValidatedFunds();
        summary.getValidatedWorks();
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

    private List<AffiliationGroupForm> getAffiliationGroup(AffiliationType affiliationType) {
        List<AffiliationGroupForm> affiliationGroupForms = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            AffiliationGroupForm agf = new AffiliationGroupForm();
            List<AffiliationForm> l = new ArrayList<AffiliationForm>();
            AffiliationForm af1 = getAffiliationForm(affiliationType, affiliationType.name() + "-" + i, Long.valueOf(i));
            l.add(af1);
            agf.setDefaultAffiliation(af1);
            agf.setAffiliations(l);
            agf.setActivePutCode(1L);
            agf.setActiveVisibility(Visibility.PUBLIC.name());
            agf.setAffiliationType(affiliationType);
            affiliationGroupForms.add(i, agf);
        }
        return affiliationGroupForms;
    }

    private AffiliationForm getAffiliationForm(AffiliationType affiliationType, String affiliationName, Long putCode) {
        AffiliationForm f = new AffiliationForm();
        f.setVisibility(new org.orcid.pojo.ajaxForm.Visibility());
        f.setAffiliationType(Text.valueOf(affiliationType.name()));
        f.setAffiliationName(Text.valueOf(affiliationName));
        f.setPutCode(Text.valueOf(putCode));
        org.orcid.pojo.ajaxForm.Date endDate = new org.orcid.pojo.ajaxForm.Date();
        endDate.setYear("2012");
        f.setEndDate(endDate);
        return f;
    }

    private Page<org.orcid.pojo.grouping.WorkGroup> getWorkGroups() {
        Page<WorkGroup> works = new Page<WorkGroup>();
        for (int i = 0; i < 3; i++) {
            WorkGroup wg = new WorkGroup();
            wg.setActivePutCode(1L);
            wg.setActiveVisibility(Visibility.PUBLIC.name());

            ActivityExternalIdentifier aei = new ActivityExternalIdentifier();
            aei.setRelationship(Text.valueOf(Relationship.SELF.name()));
            aei.setExternalIdentifierId(Text.valueOf("001"));
            aei.setExternalIdentifierType(Text.valueOf("DOI"));

            // Add 4 works per group
            // First source is the client
            // Second source is the user
            // Third source is a client through OBO
            // Fourth source is the user through OBO
            for (int j = 0; j < 4; j++) {
                WorkForm wf = new WorkForm();
                wf.setTitle(Text.valueOf("Work-" + j));
                switch (j) {
                case 0:
                    wf.setSource(CLIENT1);
                    break;
                case 1:
                    wf.setSource(ORCID);
                    break;
                case 2:
                    wf.setAssertionOriginClientId(CLIENT1);
                    break;
                case 3:
                    wf.setAssertionOriginOrcid(ORCID);
                    break;
                }
                wg.addWork(wf);
            }

            works.addWorkGroup(wg);
        }
        return works;
    }

    private List<FundingGroup> getFundingGroups() {
        List<FundingGroup> fundings = new ArrayList<FundingGroup>();

        for (int i = 0; i < 3; i++) {
            FundingGroup fg = new FundingGroup();
            fg.setFundings(new ArrayList<FundingForm>());
            // Add 4 funding per group
            // First source is the client
            // Second source is the user
            // Third source is a client through OBO
            // Fourth source is the user through OBO
            for (int j = 0; j < 4; j++) {
                FundingForm ff = new FundingForm();
                switch (j) {
                case 0:
                    ff.setSource(CLIENT1);
                    break;
                case 1:
                    ff.setSource(ORCID);
                    break;
                case 2:
                    ff.setAssertionOriginClientId(CLIENT1);
                    break;
                case 3:
                    ff.setAssertionOriginOrcid(ORCID);
                    break;
                }
                fg.getFundings().add(ff);
            }
            fundings.add(i, fg);
        }

        return fundings;
    }

    private List<PeerReviewMinimizedSummary> getPeerReviewSummaryList() {
        List<PeerReviewMinimizedSummary> peerReviews = new ArrayList<PeerReviewMinimizedSummary>();
        // Add 4 peer reviews per group
        // First source is the client
        // Second source is the user
        // Third source is a client through OBO
        // Fourth source is the user through OBO
        for (int j = 0; j < 4; j++) {
            PeerReviewMinimizedSummary pr = new PeerReviewMinimizedSummary(ORCID, null, null, null, null, null, 0);
            switch (j) {
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
}
