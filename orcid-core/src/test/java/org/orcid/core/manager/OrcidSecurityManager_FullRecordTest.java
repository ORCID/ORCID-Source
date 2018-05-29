package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.jaxb.model.record_v2.Biography;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.OtherNames;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidSecurityManager_FullRecordTest extends OrcidSecurityManagerTestBase {

    // ---- RECORD ----
    @Test(expected = OrcidUnauthorizedException.class)
    public void testRecord_When_TokenForOtherUser() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_LIMITED);
        Record record = new Record();
        orcidSecurityManager.checkAndFilter(ORCID_2, record);
        fail();
    }

    @Test
    public void testRecord_When_AllPublic_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.PUBLIC);
        Biography bio = createBiography(Visibility.PUBLIC);

        Address a1 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Address a2 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Address a3 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e2 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e3 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        EducationSummary edu1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary edu2 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary edu3 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);

        FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(edu1, edu2, edu3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        Record record = new Record();
        record.setActivitiesSummary(as);
        record.setPerson(p);

        orcidSecurityManager.checkAndFilter(ORCID_1, record);
        assertNotNull(record);
        // Check person
        assertEquals(name, p.getName());
        assertEquals(bio, p.getBiography());
        // Check addresses
        assertEquals(3, p.getAddresses().getAddress().size());
        assertTrue(p.getAddresses().getAddress().contains(a1));
        assertTrue(p.getAddresses().getAddress().contains(a2));
        assertTrue(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(3, p.getEmails().getEmails().size());
        assertTrue(p.getEmails().getEmails().contains(e1));
        assertTrue(p.getEmails().getEmails().contains(e2));
        assertTrue(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(3, p.getKeywords().getKeywords().size());
        assertTrue(p.getKeywords().getKeywords().contains(k1));
        assertTrue(p.getKeywords().getKeywords().contains(k2));
        assertTrue(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
        // Check activities
        assertNotNull(as);
        // Check educations
        assertEquals(3, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(edu1));
        assertTrue(as.getEducations().getSummaries().contains(edu2));
        assertTrue(as.getEducations().getSummaries().contains(edu3));
        // Check employments
        assertEquals(3, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertTrue(as.getEmployments().getSummaries().contains(em3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(3, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(4, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(3, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(4, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testRecord_When_SomeLimited_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.PUBLIC);
        Biography bio = createBiography(Visibility.LIMITED);

        Address a1 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Address a2 = createAddress(Visibility.LIMITED, CLIENT_2);
        Address a3 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e2 = createEmail(Visibility.LIMITED, CLIENT_2);
        Email e3 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.LIMITED, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.LIMITED, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.LIMITED, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        EducationSummary edu1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary edu2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary edu3 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);

        FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(edu1, edu2, edu3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        Record record = new Record();
        record.setActivitiesSummary(as);
        record.setPerson(p);

        orcidSecurityManager.checkAndFilter(ORCID_1, record);
        assertNotNull(record);
        // Check person
        assertEquals(name, p.getName());
        assertNull(p.getBiography());
        // Check addresses
        assertEquals(2, p.getAddresses().getAddress().size());
        assertTrue(p.getAddresses().getAddress().contains(a1));
        assertFalse(p.getAddresses().getAddress().contains(a2));
        assertTrue(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(2, p.getEmails().getEmails().size());
        assertTrue(p.getEmails().getEmails().contains(e1));
        assertFalse(p.getEmails().getEmails().contains(e2));
        assertTrue(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(2, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(2, p.getKeywords().getKeywords().size());
        assertTrue(p.getKeywords().getKeywords().contains(k1));
        assertFalse(p.getKeywords().getKeywords().contains(k2));
        assertTrue(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(2, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertFalse(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(2, p.getResearcherUrls().getResearcherUrls().size());
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
        // Check activities
        assertNotNull(as);
        // Check educations
        assertEquals(2, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(edu1));
        assertFalse(as.getEducations().getSummaries().contains(edu2));
        assertTrue(as.getEducations().getSummaries().contains(edu3));
        // Check employments
        assertEquals(2, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertFalse(as.getEmployments().getSummaries().contains(em2));
        assertTrue(as.getEmployments().getSummaries().contains(em3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(2, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(3, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(2, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(2, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(3, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testRecord_When_SomePrivate_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.PRIVATE);
        Biography bio = createBiography(Visibility.PUBLIC);

        Address a1 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Address a2 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Address a3 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Email e2 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e3 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        EducationSummary edu1 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary edu2 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary edu3 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);

        FundingSummary f1 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(edu1, edu2, edu3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        Record record = new Record();
        record.setActivitiesSummary(as);
        record.setPerson(p);

        orcidSecurityManager.checkAndFilter(ORCID_1, record);
        assertNotNull(record);
        // Check person
        assertNull(p.getName());
        assertEquals(bio, p.getBiography());
        // Check addresses
        assertEquals(2, p.getAddresses().getAddress().size());
        assertFalse(p.getAddresses().getAddress().contains(a1));
        assertTrue(p.getAddresses().getAddress().contains(a2));
        assertTrue(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(2, p.getEmails().getEmails().size());
        assertFalse(p.getEmails().getEmails().contains(e1));
        assertTrue(p.getEmails().getEmails().contains(e2));
        assertTrue(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(2, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(2, p.getKeywords().getKeywords().size());
        assertFalse(p.getKeywords().getKeywords().contains(k1));
        assertTrue(p.getKeywords().getKeywords().contains(k2));
        assertTrue(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(2, p.getOtherNames().getOtherNames().size());
        assertFalse(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(2, p.getResearcherUrls().getResearcherUrls().size());
        assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
        // Check activities
        assertNotNull(as);
        // Check educations
        assertEquals(2, as.getEducations().getSummaries().size());
        assertFalse(as.getEducations().getSummaries().contains(edu1));
        assertTrue(as.getEducations().getSummaries().contains(edu2));
        assertTrue(as.getEducations().getSummaries().contains(edu3));
        // Check employments
        assertEquals(2, as.getEmployments().getSummaries().size());
        assertFalse(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertTrue(as.getEmployments().getSummaries().contains(em3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(2, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(3, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(2, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(2, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(3, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testRecord_When_AllPrivate_NoSource_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.PRIVATE);
        Biography bio = createBiography(Visibility.PRIVATE);

        Address a1 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Address a2 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Address a3 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Email e2 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        EducationSummary edu1 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary edu2 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary edu3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

        FundingSummary f1 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(edu1, edu2, edu3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        Record record = new Record();
        record.setActivitiesSummary(as);
        record.setPerson(p);

        orcidSecurityManager.checkAndFilter(ORCID_1, record);
        assertNotNull(record);
        // Check person
        assertNull(p.getName());
        assertNull(p.getBiography());
        // Check addresses
        assertEquals(0, p.getAddresses().getAddress().size());
        // Check emails
        assertEquals(0, p.getEmails().getEmails().size());
        // Check ext ids
        assertEquals(0, p.getExternalIdentifiers().getExternalIdentifiers().size());
        // Check keywords
        assertEquals(0, p.getKeywords().getKeywords().size());
        // Check other names
        assertEquals(0, p.getOtherNames().getOtherNames().size());
        // Check researcher urls
        assertEquals(0, p.getResearcherUrls().getResearcherUrls().size());
        // Check activities
        assertNotNull(as);
        // Check educations
        assertEquals(0, as.getEducations().getSummaries().size());
        // Check employments
        assertEquals(0, as.getEmployments().getSummaries().size());
        // Check fundings
        assertEquals(0, as.getFundings().getFundingGroup().size());
        // Check peer reviews
        assertEquals(0, as.getPeerReviews().getPeerReviewGroup().size());
        // Check works
        assertEquals(0, as.getWorks().getWorkGroup().size());
    }

    @Test
    public void testRecord_When_AllPrivate_Source_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        Name name = createName(Visibility.PRIVATE);
        Biography bio = createBiography(Visibility.PRIVATE);

        Address a1 = createAddress(Visibility.PRIVATE, CLIENT_1);
        Address a2 = createAddress(Visibility.PRIVATE, CLIENT_1);
        Address a3 = createAddress(Visibility.PRIVATE, CLIENT_1);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PRIVATE, CLIENT_1);
        Email e2 = createEmail(Visibility.PRIVATE, CLIENT_1);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_1);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PRIVATE, CLIENT_1);
        Keyword k2 = createKeyword(Visibility.PRIVATE, CLIENT_1);
        Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_1);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        EducationSummary edu1 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);
        EducationSummary edu2 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);
        EducationSummary edu3 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);

        FundingSummary f1 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(edu1, edu2, edu3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        Record record = new Record();
        record.setActivitiesSummary(as);
        record.setPerson(p);

        orcidSecurityManager.checkAndFilter(ORCID_1, record);
        assertNotNull(record);
        // Check person
        assertNull(p.getName());
        assertNull(p.getBiography());
        // Check addresses
        assertEquals(3, p.getAddresses().getAddress().size());
        assertTrue(p.getAddresses().getAddress().contains(a1));
        assertTrue(p.getAddresses().getAddress().contains(a2));
        assertTrue(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(3, p.getEmails().getEmails().size());
        assertTrue(p.getEmails().getEmails().contains(e1));
        assertTrue(p.getEmails().getEmails().contains(e2));
        assertTrue(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(3, p.getKeywords().getKeywords().size());
        assertTrue(p.getKeywords().getKeywords().contains(k1));
        assertTrue(p.getKeywords().getKeywords().contains(k2));
        assertTrue(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
        // Check activities
        assertNotNull(as);
        // Check educations
        assertEquals(3, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(edu1));
        assertTrue(as.getEducations().getSummaries().contains(edu2));
        assertTrue(as.getEducations().getSummaries().contains(edu3));
        // Check employments
        assertEquals(3, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertTrue(as.getEmployments().getSummaries().contains(em3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(3, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(4, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(3, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(4, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testRecord_When_AllPublic_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_LIMITED);
        Name name = createName(Visibility.PUBLIC);
        Biography bio = createBiography(Visibility.PUBLIC);

        Address a1 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Address a2 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Address a3 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e2 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e3 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        EducationSummary edu1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary edu2 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary edu3 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);

        FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(edu1, edu2, edu3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        Record record = new Record();
        record.setActivitiesSummary(as);
        record.setPerson(p);

        orcidSecurityManager.checkAndFilter(ORCID_1, record);
        assertNotNull(record);
        // Check person
        assertEquals(name, p.getName());
        assertEquals(bio, p.getBiography());
        // Check addresses
        assertEquals(3, p.getAddresses().getAddress().size());
        assertTrue(p.getAddresses().getAddress().contains(a1));
        assertTrue(p.getAddresses().getAddress().contains(a2));
        assertTrue(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(3, p.getEmails().getEmails().size());
        assertTrue(p.getEmails().getEmails().contains(e1));
        assertTrue(p.getEmails().getEmails().contains(e2));
        assertTrue(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(3, p.getKeywords().getKeywords().size());
        assertTrue(p.getKeywords().getKeywords().contains(k1));
        assertTrue(p.getKeywords().getKeywords().contains(k2));
        assertTrue(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
        // Check activities
        assertNotNull(as);
        // Check educations
        assertEquals(3, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(edu1));
        assertTrue(as.getEducations().getSummaries().contains(edu2));
        assertTrue(as.getEducations().getSummaries().contains(edu3));
        // Check employments
        assertEquals(3, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertTrue(as.getEmployments().getSummaries().contains(em3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(3, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(4, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(3, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(4, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testRecord_When_SomeLimited_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_LIMITED);
        Name name = createName(Visibility.PUBLIC);
        Biography bio = createBiography(Visibility.LIMITED);

        Address a1 = createAddress(Visibility.LIMITED, CLIENT_2);
        Address a2 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Address a3 = createAddress(Visibility.LIMITED, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.LIMITED, CLIENT_2);
        Email e2 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e3 = createEmail(Visibility.LIMITED, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.LIMITED, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.LIMITED, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.LIMITED, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.LIMITED, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.LIMITED, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.LIMITED, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        EducationSummary edu1 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary edu2 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary edu3 = createEducationSummary(Visibility.LIMITED, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);

        FundingSummary f1 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(edu1, edu2, edu3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        Record record = new Record();
        record.setActivitiesSummary(as);
        record.setPerson(p);

        orcidSecurityManager.checkAndFilter(ORCID_1, record);
        assertNotNull(record);
        // Check person
        assertEquals(name, p.getName());
        assertEquals(bio, p.getBiography());
        // Check addresses
        assertEquals(3, p.getAddresses().getAddress().size());
        assertTrue(p.getAddresses().getAddress().contains(a1));
        assertTrue(p.getAddresses().getAddress().contains(a2));
        assertTrue(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(3, p.getEmails().getEmails().size());
        assertTrue(p.getEmails().getEmails().contains(e1));
        assertTrue(p.getEmails().getEmails().contains(e2));
        assertTrue(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(3, p.getKeywords().getKeywords().size());
        assertTrue(p.getKeywords().getKeywords().contains(k1));
        assertTrue(p.getKeywords().getKeywords().contains(k2));
        assertTrue(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
        // Check activities
        assertNotNull(as);
        // Check educations
        assertEquals(3, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(edu1));
        assertTrue(as.getEducations().getSummaries().contains(edu2));
        assertTrue(as.getEducations().getSummaries().contains(edu3));
        // Check employments
        assertEquals(3, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertTrue(as.getEmployments().getSummaries().contains(em3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(3, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(4, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(3, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(4, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testRecord_When_SomePrivate_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_LIMITED);
        Name name = createName(Visibility.PRIVATE);
        Biography bio = createBiography(Visibility.PUBLIC);

        Address a1 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Address a2 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Address a3 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Email e2 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        EducationSummary edu1 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary edu2 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary edu3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

        FundingSummary f1 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(edu1, edu2, edu3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        Record record = new Record();
        record.setActivitiesSummary(as);
        record.setPerson(p);

        orcidSecurityManager.checkAndFilter(ORCID_1, record);
        assertNotNull(record);
        // Check person
        assertNull(p.getName());
        assertEquals(bio, p.getBiography());
        // Check addresses
        assertEquals(1, p.getAddresses().getAddress().size());
        assertFalse(p.getAddresses().getAddress().contains(a1));
        assertTrue(p.getAddresses().getAddress().contains(a2));
        assertFalse(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(1, p.getEmails().getEmails().size());
        assertFalse(p.getEmails().getEmails().contains(e1));
        assertTrue(p.getEmails().getEmails().contains(e2));
        assertFalse(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(1, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(1, p.getKeywords().getKeywords().size());
        assertFalse(p.getKeywords().getKeywords().contains(k1));
        assertTrue(p.getKeywords().getKeywords().contains(k2));
        assertFalse(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(1, p.getOtherNames().getOtherNames().size());
        assertFalse(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertFalse(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
        assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r3));
        // Check activities
        assertNotNull(as);
        // Check educations
        assertEquals(1, as.getEducations().getSummaries().size());
        assertFalse(as.getEducations().getSummaries().contains(edu1));
        assertTrue(as.getEducations().getSummaries().contains(edu2));
        assertFalse(as.getEducations().getSummaries().contains(edu3));
        // Check employments
        assertEquals(1, as.getEmployments().getSummaries().size());
        assertFalse(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertFalse(as.getEmployments().getSummaries().contains(em3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(1, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(2, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(1, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(2, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testRecord_When_AllPrivate_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_LIMITED);
        Name name = createName(Visibility.PRIVATE);
        Biography bio = createBiography(Visibility.PRIVATE);

        Address a1 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Address a2 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Address a3 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Email e2 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        EducationSummary edu1 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary edu2 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary edu3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

        FundingSummary f1 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(edu1, edu2, edu3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        Record record = new Record();
        record.setActivitiesSummary(as);
        record.setPerson(p);

        orcidSecurityManager.checkAndFilter(ORCID_1, record);
        assertNotNull(record);
        // Check person
        assertNull(p.getName());
        assertNull(p.getBiography());
        // Check addresses
        assertEquals(0, p.getAddresses().getAddress().size());
        // Check emails
        assertEquals(0, p.getEmails().getEmails().size());
        // Check ext ids
        assertEquals(0, p.getExternalIdentifiers().getExternalIdentifiers().size());
        // Check keywords
        assertEquals(0, p.getKeywords().getKeywords().size());
        // Check other names
        assertEquals(0, p.getOtherNames().getOtherNames().size());
        // Check researcher urls
        assertEquals(0, p.getResearcherUrls().getResearcherUrls().size());
        // Check activities
        assertNotNull(as);
        // Check educations
        assertEquals(0, as.getEducations().getSummaries().size());
        // Check employments
        assertEquals(0, as.getEmployments().getSummaries().size());
        // Check fundings
        assertEquals(0, as.getFundings().getFundingGroup().size());
        // Check peer reviews
        assertEquals(0, as.getPeerReviews().getPeerReviewGroup().size());
        // Check works
        assertEquals(0, as.getWorks().getWorkGroup().size());
    }

    @Test
    public void testRecord_When_AllPrivate_Source_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_LIMITED);
        Name name = createName(Visibility.PRIVATE);
        Biography bio = createBiography(Visibility.PRIVATE);

        Address a1 = createAddress(Visibility.PRIVATE, CLIENT_1);
        Address a2 = createAddress(Visibility.PRIVATE, CLIENT_1);
        Address a3 = createAddress(Visibility.PRIVATE, CLIENT_1);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PRIVATE, CLIENT_1);
        Email e2 = createEmail(Visibility.PRIVATE, CLIENT_1);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_1);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PRIVATE, CLIENT_1);
        Keyword k2 = createKeyword(Visibility.PRIVATE, CLIENT_1);
        Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_1);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
        ResearcherUrl r2 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        EducationSummary edu1 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);
        EducationSummary edu2 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);
        EducationSummary edu3 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);

        FundingSummary f1 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(edu1, edu2, edu3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        Record record = new Record();
        record.setActivitiesSummary(as);
        record.setPerson(p);

        orcidSecurityManager.checkAndFilter(ORCID_1, record);
        assertNotNull(record);
        // Check person
        assertNull(p.getName());
        assertNull(p.getBiography());
        // Check addresses
        assertEquals(3, p.getAddresses().getAddress().size());
        assertTrue(p.getAddresses().getAddress().contains(a1));
        assertTrue(p.getAddresses().getAddress().contains(a2));
        assertTrue(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(3, p.getEmails().getEmails().size());
        assertTrue(p.getEmails().getEmails().contains(e1));
        assertTrue(p.getEmails().getEmails().contains(e2));
        assertTrue(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(3, p.getKeywords().getKeywords().size());
        assertTrue(p.getKeywords().getKeywords().contains(k1));
        assertTrue(p.getKeywords().getKeywords().contains(k2));
        assertTrue(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertTrue(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
        // Check activities
        assertNotNull(as);
        // Check educations
        assertEquals(3, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(edu1));
        assertTrue(as.getEducations().getSummaries().contains(edu2));
        assertTrue(as.getEducations().getSummaries().contains(edu3));
        // Check employments
        assertEquals(3, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertTrue(as.getEmployments().getSummaries().contains(em3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(3, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(4, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(3, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(4, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testRecord_When_MixedVisibility_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_LIMITED);
        Name name = createName(Visibility.LIMITED);
        Biography bio = createBiography(Visibility.PRIVATE);

        Address a1 = createAddress(Visibility.PUBLIC, CLIENT_2);
        Address a2 = createAddress(Visibility.LIMITED, CLIENT_2);
        Address a3 = createAddress(Visibility.PRIVATE, CLIENT_2);
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

        Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
        Email e2 = createEmail(Visibility.LIMITED, CLIENT_2);
        Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

        Keyword k1 = createKeyword(Visibility.PUBLIC, CLIENT_2);
        Keyword k2 = createKeyword(Visibility.LIMITED, CLIENT_2);
        Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_2);
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

        OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
        OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
        OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

        PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
        PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.LIMITED, CLIENT_2);
        PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

        ResearcherUrl r1 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
        ResearcherUrl r2 = createResearcherUrl(Visibility.LIMITED, CLIENT_2);
        ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

        EducationSummary edu1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary edu2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary edu3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

        FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(edu1, edu2, edu3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));

        Person p = new Person();
        p.setBiography(bio);
        p.setName(name);
        p.setAddresses(addresses);
        p.setEmails(emails);
        p.setExternalIdentifiers(extIds);
        p.setKeywords(keywords);
        p.setOtherNames(otherNames);
        p.setResearcherUrls(researcherUrls);

        Record record = new Record();
        record.setActivitiesSummary(as);
        record.setPerson(p);

        orcidSecurityManager.checkAndFilter(ORCID_1, record);
        assertNotNull(record);
        // Check person
        assertEquals(name, p.getName());
        assertNull(p.getBiography());
        // Check addresses
        assertEquals(2, p.getAddresses().getAddress().size());
        assertTrue(p.getAddresses().getAddress().contains(a1));
        assertTrue(p.getAddresses().getAddress().contains(a2));
        assertFalse(p.getAddresses().getAddress().contains(a3));
        // Check emails
        assertEquals(2, p.getEmails().getEmails().size());
        assertTrue(p.getEmails().getEmails().contains(e1));
        assertTrue(p.getEmails().getEmails().contains(e2));
        assertFalse(p.getEmails().getEmails().contains(e3));
        // Check ext ids
        assertEquals(2, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
        assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
        // Check keywords
        assertEquals(2, p.getKeywords().getKeywords().size());
        assertTrue(p.getKeywords().getKeywords().contains(k1));
        assertTrue(p.getKeywords().getKeywords().contains(k2));
        assertFalse(p.getKeywords().getKeywords().contains(k3));
        // Check other names
        assertEquals(2, p.getOtherNames().getOtherNames().size());
        assertTrue(p.getOtherNames().getOtherNames().contains(o1));
        assertTrue(p.getOtherNames().getOtherNames().contains(o2));
        assertFalse(p.getOtherNames().getOtherNames().contains(o3));
        // Check researcher urls
        assertEquals(2, p.getResearcherUrls().getResearcherUrls().size());
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
        assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
        assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r3));
        // Check activities
        assertNotNull(as);
        // Check educations
        assertEquals(2, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(edu1));
        assertTrue(as.getEducations().getSummaries().contains(edu2));
        assertFalse(as.getEducations().getSummaries().contains(edu3));
        // Check employments
        assertEquals(2, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertFalse(as.getEmployments().getSummaries().contains(em3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(2, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(3, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(2, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(2, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(3, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testRecord_When_ReadLimitedToken_EmptyElement() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_LIMITED);
        Record record = new Record();
        orcidSecurityManager.checkAndFilter(ORCID_1, record);
        assertNotNull(record);
    }
}
