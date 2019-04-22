package org.orcid.api.common.util.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.orcid.jaxb.model.common.CitationType;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.v3.release.common.Contributor;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.common.Subtitle;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.TranslatedTitle;
import org.orcid.jaxb.model.v3.release.record.Citation;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.FundingContributor;
import org.orcid.jaxb.model.v3.release.record.FundingContributors;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.orcid.jaxb.model.v3.release.record.WorkContributors;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
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
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Services;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;

public class ActivityUtilsTest {

    private static final String ORCID = "0000-0000-0000-0000";

    @Test
    public void setPathToActivityTest() {
        Work w = new Work();
        w.setPutCode(123456L);
        assertNull(w.getPath());
        ActivityUtils.setPathToActivity(w, ORCID);
        assertEquals("/" + ORCID + "/work/123456", w.getPath());
    }

    @Test
    public void setPathToDistinctionsTest() {
        Distinctions x = getDistinctions();
        ActivityUtils.setPathToAffiliations(x, ORCID);
        assertEquals("/" + ORCID + "/distinction/123", x.retrieveGroups().iterator().next().getActivities().get(0).getPath());
    }
    
    @Test
    public void setPathToEducationsTest() {
        Educations x = getEducations();
        ActivityUtils.setPathToAffiliations(x, ORCID);
        assertEquals("/" + ORCID + "/education/123", x.retrieveGroups().iterator().next().getActivities().get(0).getPath());
    }

    @Test
    public void setPathToEmploymentsTest() {
        Employments x = getEmployments();
        ActivityUtils.setPathToAffiliations(x, ORCID);
        assertEquals("/" + ORCID + "/employment/123", x.retrieveGroups().iterator().next().getActivities().get(0).getPath());
    }

    @Test
    public void setPathToInvitedPositionsTest() {
        InvitedPositions x = getInvitedPositions();
        ActivityUtils.setPathToAffiliations(x, ORCID);
        assertEquals("/" + ORCID + "/invited-position/123", x.retrieveGroups().iterator().next().getActivities().get(0).getPath());
    }
    
    @Test
    public void setPathToMembershipsTest() {
        Memberships x = getMemberships();
        ActivityUtils.setPathToAffiliations(x, ORCID);
        assertEquals("/" + ORCID + "/membership/123", x.retrieveGroups().iterator().next().getActivities().get(0).getPath());
    }
    
    @Test
    public void setPathToQualificationTest() {
        Qualifications x = getQualifications();
        ActivityUtils.setPathToAffiliations(x, ORCID);
        assertEquals("/" + ORCID + "/qualification/123", x.retrieveGroups().iterator().next().getActivities().get(0).getPath());
    }
    
    @Test
    public void setPathToServicesTest() {
        Services x = getServices();
        ActivityUtils.setPathToAffiliations(x, ORCID);
        assertEquals("/" + ORCID + "/service/123", x.retrieveGroups().iterator().next().getActivities().get(0).getPath());
    }
    
    @Test
    public void setPathToFundingsTest() {
        Fundings x = getFundings();
        ActivityUtils.setPathToFundings(x, ORCID);
        assertEquals("/" + ORCID + "/funding/123", x.getFundingGroup().get(0).getFundingSummary().get(0).getPath());
    }

    @Test
    public void setPathToWorksTest() {
        Works x = getWorks();
        ActivityUtils.setPathToWorks(x, ORCID);
        assertEquals("/" + ORCID + "/work/123", x.getWorkGroup().get(0).getWorkSummary().get(0).getPath());
    }

    @Test
    public void setPathToPeerReviewsTest() {
        PeerReviews x = getPeerReviews();
        ActivityUtils.setPathToPeerReviews(x, ORCID);
        assertEquals("/" + ORCID + "/peer-review/123", x.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPath());
    }

    @Test
    public void setPathToActivitiesSummaryTest() {
        ActivitiesSummary x = new ActivitiesSummary();
        x.setDistinctions(getDistinctions());
        x.setEducations(getEducations());
        x.setEmployments(getEmployments());
        x.setInvitedPositions(getInvitedPositions());
        x.setFundings(getFundings());
        x.setMemberships(getMemberships());
        x.setQualifications(getQualifications());
        x.setServices(getServices());
        x.setWorks(getWorks());
        x.setPeerReviews(getPeerReviews());
        ActivityUtils.setPathToActivity(x, ORCID);
        assertEquals("/" + ORCID + "/activities", x.getPath());
        assertEquals("/" + ORCID + "/distinction/123", x.getDistinctions().retrieveGroups().iterator().next().getActivities().get(0).getPath());
        assertEquals("/" + ORCID + "/education/123", x.getEducations().retrieveGroups().iterator().next().getActivities().get(0).getPath());
        assertEquals("/" + ORCID + "/employment/123", x.getEmployments().retrieveGroups().iterator().next().getActivities().get(0).getPath());
        assertEquals("/" + ORCID + "/invited-position/123", x.getInvitedPositions().retrieveGroups().iterator().next().getActivities().get(0).getPath());
        assertEquals("/" + ORCID + "/funding/123", x.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPath());
        assertEquals("/" + ORCID + "/membership/123", x.getMemberships().retrieveGroups().iterator().next().getActivities().get(0).getPath());
        assertEquals("/" + ORCID + "/qualification/123", x.getQualifications().retrieveGroups().iterator().next().getActivities().get(0).getPath());
        assertEquals("/" + ORCID + "/service/123", x.getServices().retrieveGroups().iterator().next().getActivities().get(0).getPath());
        assertEquals("/" + ORCID + "/work/123", x.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPath());
        assertEquals("/" + ORCID + "/peer-review/123", x.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPath());
    }

    @Test
    public void cleanEmptyActivitiesSummaryTest() {
        // Test it cleans empty fields
        WorkSummary s = getEmptyWorkSummary();
        ActivitiesSummary x = new ActivitiesSummary();
        Works w = new Works();
        WorkGroup g = new WorkGroup();
        g.getWorkSummary().add(s);
        w.getWorkGroup().add(g);
        x.setWorks(w);
        assertNotNull(x.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle());
        ActivityUtils.cleanEmptyFields(x);
        assertNull(x.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle());

        // Test it doesn't remove non empty fields
        s = getEmptyWorkSummary();
        s.getTitle().getTranslatedTitle().setContent("test");
        s.getTitle().getTranslatedTitle().setLanguageCode("en");
        x = new ActivitiesSummary();
        w = new Works();
        g = new WorkGroup();
        g.getWorkSummary().add(s);
        w.getWorkGroup().add(g);
        x.setWorks(w);
        assertEquals("test", x.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle().getContent());
        assertEquals("en", x.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle().getLanguageCode());
        ActivityUtils.cleanEmptyFields(x);
        assertEquals("test", x.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle().getContent());
        assertEquals("en", x.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle().getLanguageCode());
    }

    @Test
    public void cleanWorksTest() {
        // Test it cleans empty fields
        WorkSummary s = getEmptyWorkSummary();
        Works w = new Works();
        WorkGroup g = new WorkGroup();
        g.getWorkSummary().add(s);
        w.getWorkGroup().add(g);
        assertNotNull(w.getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle());
        ActivityUtils.cleanEmptyFields(w);
        assertNull(w.getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle());
        
        // Test it doesn't remove non empty fields
        s = getEmptyWorkSummary();
        s.getTitle().getTranslatedTitle().setContent("test");
        s.getTitle().getTranslatedTitle().setLanguageCode("en");        
        w = new Works();
        g = new WorkGroup();
        g.getWorkSummary().add(s);
        w.getWorkGroup().add(g);
        assertEquals("test", w.getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle().getContent());
        assertEquals("en", w.getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle().getLanguageCode());
        ActivityUtils.cleanEmptyFields(w);
        assertEquals("test", w.getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle().getContent());
        assertEquals("en", w.getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle().getLanguageCode());                        
    }

    @Test
    public void cleanWorkSummaryTest() {
        // Test it cleans empty fields
        WorkSummary s = getEmptyWorkSummary();
        assertNotNull(s.getTitle().getTranslatedTitle());
        ActivityUtils.cleanEmptyFields(s);
        assertNull(s.getTitle().getTranslatedTitle());
        
        // Test it doesn't remove non empty fields
        s = getEmptyWorkSummary();
        s.getTitle().getTranslatedTitle().setContent("test");
        s.getTitle().getTranslatedTitle().setLanguageCode("en");        
        assertEquals("test", s.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", s.getTitle().getTranslatedTitle().getLanguageCode());
        ActivityUtils.cleanEmptyFields(s);
        assertEquals("test", s.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", s.getTitle().getTranslatedTitle().getLanguageCode());
    }

    @Test
    public void cleanWorkTest() {
        // Test it cleans empty fields
        Work w = getEmptyWork();
        assertNotNull(w.getWorkTitle().getTranslatedTitle());
        assertNotNull(w.getWorkCitation());
        assertNotNull(w.getWorkContributors().getContributor().get(0).getCreditName());
        ActivityUtils.cleanEmptyFields(w);
        assertNull(w.getWorkTitle().getTranslatedTitle());
        assertNull(w.getWorkCitation());
        assertNull(w.getWorkContributors().getContributor().get(0).getCreditName());
        
        // Test it doesn't remove non empty fields
        w = getEmptyWork();
        w.getWorkTitle().getTranslatedTitle().setContent("translated_title");
        w.getWorkTitle().getTranslatedTitle().setLanguageCode("en");
        w.getWorkCitation().setCitation("citation");
        w.getWorkCitation().setWorkCitationType(CitationType.BIBTEX);
        w.getWorkContributors().getContributor().get(0).getCreditName().setContent("credit_name");        
        assertEquals("translated_title", w.getWorkTitle().getTranslatedTitle().getContent());
        assertEquals("en", w.getWorkTitle().getTranslatedTitle().getLanguageCode());
        assertEquals("citation", w.getWorkCitation().getCitation());
        assertEquals(CitationType.BIBTEX, w.getWorkCitation().getWorkCitationType());
        assertEquals("credit_name", w.getWorkContributors().getContributor().get(0).getCreditName().getContent());
        ActivityUtils.cleanEmptyFields(w);
        assertEquals("translated_title", w.getWorkTitle().getTranslatedTitle().getContent());
        assertEquals("en", w.getWorkTitle().getTranslatedTitle().getLanguageCode());
        assertEquals("citation", w.getWorkCitation().getCitation());
        assertEquals(CitationType.BIBTEX, w.getWorkCitation().getWorkCitationType());
        assertEquals("credit_name", w.getWorkContributors().getContributor().get(0).getCreditName().getContent());
    }

    @Test
    public void cleanBulkElementTest() {
        // Test it cleans empty fields
        BulkElement b = getEmptyWork();
        assertNotNull(((Work) b).getWorkTitle().getTranslatedTitle());
        assertNotNull(((Work) b).getWorkCitation());
        assertNotNull(((Work) b).getWorkContributors().getContributor().get(0).getCreditName());
        ActivityUtils.cleanEmptyFields(b);
        assertNull(((Work) b).getWorkTitle().getTranslatedTitle());
        assertNull(((Work) b).getWorkCitation());
        assertNull(((Work) b).getWorkContributors().getContributor().get(0).getCreditName());
        
        // Test it doesn't remove non empty fields
        b = getEmptyWork();
        ((Work) b).getWorkTitle().getTranslatedTitle().setContent("translated_title");
        ((Work) b).getWorkTitle().getTranslatedTitle().setLanguageCode("en");
        ((Work) b).getWorkCitation().setCitation("citation");
        ((Work) b).getWorkCitation().setWorkCitationType(CitationType.BIBTEX);
        ((Work) b).getWorkContributors().getContributor().get(0).getCreditName().setContent("credit_name");       
        assertEquals("translated_title", ((Work) b).getWorkTitle().getTranslatedTitle().getContent());
        assertEquals("en", ((Work) b).getWorkTitle().getTranslatedTitle().getLanguageCode());
        assertEquals("citation", ((Work) b).getWorkCitation().getCitation());
        assertEquals(CitationType.BIBTEX, ((Work) b).getWorkCitation().getWorkCitationType());
        assertEquals("credit_name", ((Work) b).getWorkContributors().getContributor().get(0).getCreditName().getContent());
        ActivityUtils.cleanEmptyFields(b);
        assertEquals("translated_title", ((Work) b).getWorkTitle().getTranslatedTitle().getContent());
        assertEquals("en", ((Work) b).getWorkTitle().getTranslatedTitle().getLanguageCode());
        assertEquals("citation", ((Work) b).getWorkCitation().getCitation());
        assertEquals(CitationType.BIBTEX, ((Work) b).getWorkCitation().getWorkCitationType());
        assertEquals("credit_name", ((Work) b).getWorkContributors().getContributor().get(0).getCreditName().getContent());        
    }

    @Test
    public void cleanBulkWorkTest() {
        // Test it cleans empty fields
        WorkBulk bulk = new WorkBulk();
        Work w = getEmptyWork();
        bulk.getBulk().add(w);
        assertNotNull(((Work) bulk.getBulk().get(0)).getWorkTitle().getTranslatedTitle());
        assertNotNull(((Work) bulk.getBulk().get(0)).getWorkCitation());
        assertNotNull(((Work) bulk.getBulk().get(0)).getWorkContributors().getContributor().get(0).getCreditName());
        ActivityUtils.cleanEmptyFields(bulk);
        assertNull(((Work) bulk.getBulk().get(0)).getWorkTitle().getTranslatedTitle());
        assertNull(((Work) bulk.getBulk().get(0)).getWorkCitation());
        assertNull(((Work) bulk.getBulk().get(0)).getWorkContributors().getContributor().get(0).getCreditName());
        
        // Test it doesn't remove non empty fields
        bulk = new WorkBulk();
        w = getEmptyWork();        
        w.getWorkTitle().getTranslatedTitle().setContent("translated_title");
        w.getWorkTitle().getTranslatedTitle().setLanguageCode("en");
        w.getWorkCitation().setCitation("citation");
        w.getWorkCitation().setWorkCitationType(CitationType.BIBTEX);
        w.getWorkContributors().getContributor().get(0).getCreditName().setContent("credit_name");        
        bulk.getBulk().add(w);
        assertEquals("translated_title", ((Work) bulk.getBulk().get(0)).getWorkTitle().getTranslatedTitle().getContent());
        assertEquals("en", ((Work) bulk.getBulk().get(0)).getWorkTitle().getTranslatedTitle().getLanguageCode());
        assertEquals("citation", ((Work) bulk.getBulk().get(0)).getWorkCitation().getCitation());
        assertEquals(CitationType.BIBTEX, ((Work) bulk.getBulk().get(0)).getWorkCitation().getWorkCitationType());
        assertEquals("credit_name", ((Work) bulk.getBulk().get(0)).getWorkContributors().getContributor().get(0).getCreditName().getContent());
        ActivityUtils.cleanEmptyFields(bulk);
        assertEquals("translated_title", ((Work) bulk.getBulk().get(0)).getWorkTitle().getTranslatedTitle().getContent());
        assertEquals("en", ((Work) bulk.getBulk().get(0)).getWorkTitle().getTranslatedTitle().getLanguageCode());
        assertEquals("citation", ((Work) bulk.getBulk().get(0)).getWorkCitation().getCitation());
        assertEquals(CitationType.BIBTEX, ((Work) bulk.getBulk().get(0)).getWorkCitation().getWorkCitationType());
        assertEquals("credit_name", ((Work) bulk.getBulk().get(0)).getWorkContributors().getContributor().get(0).getCreditName().getContent());        
    }
    
    @Test
    public void cleanFundingTest() {
        // Test it cleans empty fields
        Funding f = getEmptyFunding();
        assertNotNull(f.getContributors().getContributor().get(0).getCreditName());
        assertNotNull(f.getContributors().getContributor().get(0).getCreditName().getContent());
        ActivityUtils.cleanEmptyFields(f);
        assertNull(f.getContributors().getContributor().get(0).getCreditName());
        
        // Test it doesn't remove non empty fields
        f = getEmptyFunding();
        f.getContributors().getContributor().get(0).getCreditName().setContent("test");
        assertNotNull(f.getContributors().getContributor().get(0).getCreditName());
        assertEquals("test", f.getContributors().getContributor().get(0).getCreditName().getContent());
        ActivityUtils.cleanEmptyFields(f);
        assertNotNull(f.getContributors().getContributor().get(0).getCreditName());
        assertEquals("test", f.getContributors().getContributor().get(0).getCreditName().getContent());        
    }

    private Educations getEducations() {
        Educations x = new Educations();
        EducationSummary e = new EducationSummary();
        e.setPutCode(123L);
        
        AffiliationGroup<EducationSummary> group = new AffiliationGroup<>();
        group.getActivities().add(e);
        x.retrieveGroups().add(group);
        return x;
    }

    private Employments getEmployments() {
        Employments x = new Employments();
        EmploymentSummary e = new EmploymentSummary();
        e.setPutCode(123L);
        
        AffiliationGroup<EmploymentSummary> group = new AffiliationGroup<>();
        group.getActivities().add(e);
        x.retrieveGroups().add(group);
        return x;
    }

    private Fundings getFundings() {
        Fundings x = new Fundings();
        FundingGroup g = new FundingGroup();
        FundingSummary e = new FundingSummary();
        e.setPutCode(123L);
        g.getFundingSummary().add(e);
        x.getFundingGroup().add(g);
        return x;
    }

    private PeerReviews getPeerReviews() {
        PeerReviews x = new PeerReviews();
        PeerReviewGroup g = new PeerReviewGroup();
        PeerReviewDuplicateGroup duplicateGroup = new PeerReviewDuplicateGroup();
        PeerReviewSummary e = new PeerReviewSummary();
        e.setPutCode(123L);
        duplicateGroup.getPeerReviewSummary().add(e);
        g.getPeerReviewGroup().add(duplicateGroup);
        x.getPeerReviewGroup().add(g);
        return x;
    }

    private Works getWorks() {
        Works x = new Works();
        WorkGroup g = new WorkGroup();
        WorkSummary e = new WorkSummary();
        e.setPutCode(123L);
        g.getWorkSummary().add(e);
        x.getWorkGroup().add(g);
        return x;
    }

    private WorkSummary getEmptyWorkSummary() {
        WorkSummary s = new WorkSummary();
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title(""));
        title.setSubtitle(new Subtitle(""));
        title.setTranslatedTitle(new TranslatedTitle(""));
        s.setTitle(title);
        return s;
    }

    private Work getEmptyWork() {
        Work w = new Work();
        // Title
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title(""));
        title.setSubtitle(new Subtitle(""));
        title.setTranslatedTitle(new TranslatedTitle(""));
        w.setWorkTitle(title);
        // Citation
        w.setWorkCitation(new Citation());
        WorkContributors wc = new WorkContributors();
        // Contributors
        Contributor c = new Contributor();
        c.setCreditName(new CreditName(""));
        wc.getContributor().add(c);
        w.setWorkContributors(wc);
        return w;
    }
    
    private Funding getEmptyFunding() {
        Funding f = new Funding();
        FundingContributors fcs = new FundingContributors();
        FundingContributor fc = new FundingContributor();
        fc.setCreditName(new CreditName(""));
        fcs.getContributor().add(fc);
        f.setContributors(fcs);
        return f;
    }
    
    private Distinctions getDistinctions() {
        Distinctions x = new Distinctions();
        DistinctionSummary e = new DistinctionSummary();
        e.setPutCode(123L);
        
        AffiliationGroup<DistinctionSummary> group = new AffiliationGroup<>();
        group.getActivities().add(e);
        x.retrieveGroups().add(group);
        return x;
    }
    
    private InvitedPositions getInvitedPositions() {
        InvitedPositions x = new InvitedPositions();
        InvitedPositionSummary e = new InvitedPositionSummary();
        e.setPutCode(123L);
        
        AffiliationGroup<InvitedPositionSummary> group = new AffiliationGroup<>();
        group.getActivities().add(e);
        x.retrieveGroups().add(group);
        return x;
    }
    
    private Memberships getMemberships() {
        Memberships x = new Memberships();
        MembershipSummary e = new MembershipSummary();
        e.setPutCode(123L);
        
        AffiliationGroup<MembershipSummary> group = new AffiliationGroup<>();
        group.getActivities().add(e);
        x.retrieveGroups().add(group);
        return x;
    }
    
    private Qualifications getQualifications() {
        Qualifications x = new Qualifications();
        QualificationSummary e = new QualificationSummary();
        e.setPutCode(123L);
        
        AffiliationGroup<QualificationSummary> group = new AffiliationGroup<>();
        group.getActivities().add(e);
        x.retrieveGroups().add(group);
        return x;
    }
    
    private Services getServices() {
        Services x = new Services();
        ServiceSummary e = new ServiceSummary();
        e.setPutCode(123L);
        
        AffiliationGroup<ServiceSummary> group = new AffiliationGroup<>();
        group.getActivities().add(e);
        x.retrieveGroups().add(group);
        return x;
    }
}
