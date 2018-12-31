package org.orcid.test.helper.v3;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.v3.rc2.common.Country;
import org.orcid.jaxb.model.v3.rc2.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.rc2.common.Filterable;
import org.orcid.jaxb.model.v3.rc2.common.FuzzyDate;
import org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.rc2.common.Organization;
import org.orcid.jaxb.model.v3.rc2.common.OrganizationAddress;
import org.orcid.jaxb.model.v3.rc2.common.Title;
import org.orcid.jaxb.model.v3.rc2.common.Url;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.common.VisibilityType;
import org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.rc2.record.ActivitiesContainer;
import org.orcid.jaxb.model.v3.rc2.record.Activity;
import org.orcid.jaxb.model.v3.rc2.record.Address;
import org.orcid.jaxb.model.v3.rc2.record.Addresses;
import org.orcid.jaxb.model.v3.rc2.record.Affiliation;
import org.orcid.jaxb.model.v3.rc2.record.AffiliationType;
import org.orcid.jaxb.model.v3.rc2.record.Distinction;
import org.orcid.jaxb.model.v3.rc2.record.Education;
import org.orcid.jaxb.model.v3.rc2.record.Email;
import org.orcid.jaxb.model.v3.rc2.record.Emails;
import org.orcid.jaxb.model.v3.rc2.record.Employment;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc2.record.Funding;
import org.orcid.jaxb.model.v3.rc2.record.FundingTitle;
import org.orcid.jaxb.model.v3.rc2.record.FundingType;
import org.orcid.jaxb.model.v3.rc2.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc2.record.Keyword;
import org.orcid.jaxb.model.v3.rc2.record.Keywords;
import org.orcid.jaxb.model.v3.rc2.record.Membership;
import org.orcid.jaxb.model.v3.rc2.record.OtherName;
import org.orcid.jaxb.model.v3.rc2.record.OtherNames;
import org.orcid.jaxb.model.v3.rc2.record.PeerReview;
import org.orcid.jaxb.model.v3.rc2.record.PeerReviewSubjectType;
import org.orcid.jaxb.model.v3.rc2.record.PeerReviewType;
import org.orcid.jaxb.model.v3.rc2.record.Person;
import org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.rc2.record.PersonalDetails;
import org.orcid.jaxb.model.v3.rc2.record.Qualification;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.rc2.record.Role;
import org.orcid.jaxb.model.v3.rc2.record.Service;
import org.orcid.jaxb.model.v3.rc2.record.Work;
import org.orcid.jaxb.model.v3.rc2.record.WorkTitle;

public class Utils {
    public static void assertIsPublicOrSource(VisibilityType v, String sourceId) {
        if (v instanceof Filterable) {
            Filterable f = (Filterable) v;
            if (f.retrieveSourcePath().equals(sourceId)) {
                return;
            }
        }

        if (!Visibility.PUBLIC.equals(v.getVisibility())) {
            fail("Not public nor source");
        }
    }

    public static void assertIsPublicOrSource(ActivitiesContainer c, String sourceId) {
        Collection<? extends Activity> activities = c.retrieveActivities();
        for (Activity a : activities) {
            assertIsPublicOrSource(a, sourceId);
        }
    }

    public static void assertIsPublicOrSource(Addresses elements, String sourceId) {
        if (elements == null || elements.getAddress() == null) {
            return;
        }

        for (Address e : elements.getAddress()) {
            assertIsPublicOrSource(e, sourceId);
        }
    }

    public static void assertIsPublicOrSource(Keywords elements, String sourceId) {
        if (elements == null || elements.getKeywords() == null) {
            return;
        }

        for (Keyword e : elements.getKeywords()) {
            assertIsPublicOrSource(e, sourceId);
        }
    }

    public static void assertIsPublicOrSource(ResearcherUrls elements, String sourceId) {
        if (elements == null || elements.getResearcherUrls() == null) {
            return;
        }

        for (ResearcherUrl e : elements.getResearcherUrls()) {
            assertIsPublicOrSource(e, sourceId);
        }
    }

    public static void assertIsPublicOrSource(PersonExternalIdentifiers elements, String sourceId) {
        if (elements == null || elements.getExternalIdentifiers() == null) {
            return;
        }

        for (PersonExternalIdentifier e : elements.getExternalIdentifiers()) {
            assertIsPublicOrSource(e, sourceId);
        }
    }

    public static void assertIsPublicOrSource(Emails elements, String sourceId) {
        if (elements == null || elements.getEmails() == null) {
            return;
        }

        for (Email e : elements.getEmails()) {
            assertIsPublicOrSource(e, sourceId);
        }
    }

    public static void assertIsPublicOrSource(OtherNames elements, String sourceId) {
        if (elements == null || elements.getOtherNames() == null) {
            return;
        }

        for (OtherName e : elements.getOtherNames()) {
            assertIsPublicOrSource(e, sourceId);
        }
    }

    public static void assertIsPublicOrSource(PersonalDetails p, String sourceId) {
        if (p == null) {
            return;
        }

        assertIsPublicOrSource(p.getBiography(), sourceId);
        assertIsPublicOrSource(p.getOtherNames(), sourceId);
        assertIsPublicOrSource(p.getName(), sourceId);
    }

    public static void assertIsPublicOrSource(Person p, String sourceId) {
        if (p == null) {
            return;
        }
        assertIsPublicOrSource(p.getAddresses(), sourceId);
        assertIsPublicOrSource(p.getBiography(), sourceId);
        assertIsPublicOrSource(p.getEmails(), sourceId);
        assertIsPublicOrSource(p.getExternalIdentifiers(), sourceId);
        assertIsPublicOrSource(p.getKeywords(), sourceId);
        assertIsPublicOrSource(p.getName(), sourceId);
        assertIsPublicOrSource(p.getOtherNames(), sourceId);
        assertIsPublicOrSource(p.getResearcherUrls(), sourceId);
    }

    public static void verifyLastModified(LastModifiedDate l) {
        assertNotNull(l);
        assertNotNull(l.getValue());
    }

    public static Address getAddress() {
        Address address = new Address();
        address.setVisibility(Visibility.PUBLIC);
        address.setCountry(new Country(Iso3166Country.ES));
        return address;
    }

    public static Affiliation getAffiliation(AffiliationType type) {
        Affiliation a = null;
        switch(type) {
        case DISTINCTION:
            a = new Distinction();
            break;
        case EDUCATION:
            a = new Education();
            break;
        case EMPLOYMENT:
            a = new Employment();
            break;
        case INVITED_POSITION:
            a = new InvitedPosition();
            break;
        case MEMBERSHIP:
            a = new Membership();
            break;
        case QUALIFICATION:
            a = new Qualification();
            break;
        case SERVICE:
            a = new Service();
            break;
        }
        a.setDepartmentName("My department name");
        a.setRoleTitle("My Role");
        a.setOrganization(getOrganization());
        a.setStartDate(FuzzyDate.valueOf(2017, 1, 1));
        return a;
    }
    
    public static Work getWork(String title) {
        Work work = new Work();
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title(title));
        work.setWorkTitle(workTitle);
        work.setWorkType(WorkType.BOOK);
        work.setVisibility(Visibility.PUBLIC);
        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.PART_OF);
        extId.setType(WorkExternalIdentifierType.AGR.value());
        extId.setValue("ext-id-" + System.currentTimeMillis());
        extId.setUrl(new Url("http://thisIsANewUrl.com"));
        extIds.getExternalIdentifier().add(extId);
        work.setWorkExternalIdentifiers(extIds);
        return work;
    }

    public static PeerReview getPeerReview() {
        PeerReview peerReview = new PeerReview();
        ExternalIDs weis = new ExternalIDs();
        ExternalID wei1 = new ExternalID();
        wei1.setRelationship(Relationship.PART_OF);
        wei1.setUrl(new Url("http://myUrl.com"));
        wei1.setValue("work-external-identifier-id");
        wei1.setType(WorkExternalIdentifierType.DOI.value());
        weis.getExternalIdentifier().add(wei1);
        peerReview.setExternalIdentifiers(weis);
        peerReview.setGroupId("issn:0000003");
        peerReview.setOrganization(getOrganization());
        peerReview.setRole(Role.CHAIR);
        peerReview.setSubjectContainerName(new Title("subject-container-name"));
        peerReview.setSubjectExternalIdentifier(wei1);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("work-title"));
        peerReview.setSubjectName(workTitle);
        peerReview.setSubjectType(PeerReviewSubjectType.DATA_SET);
        peerReview.setType(PeerReviewType.EVALUATION);
        return peerReview;
    }

    public static Funding getFunding() {
        Funding newFunding = new Funding();
        FundingTitle title = new FundingTitle();
        title.setTitle(new Title("Public Funding # 2"));
        newFunding.setTitle(title);
        newFunding.setType(FundingType.AWARD);
        ExternalID fExtId = new ExternalID();
        fExtId.setRelationship(Relationship.PART_OF);
        fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER.value());
        fExtId.setUrl(new Url("http://fundingExtId.com"));
        fExtId.setValue("new-funding-ext-id");
        ExternalIDs fExtIds = new ExternalIDs();
        fExtIds.getExternalIdentifier().add(fExtId);
        newFunding.setExternalIdentifiers(fExtIds);
        newFunding.setOrganization(getOrganization());
        return newFunding;
    }

    public static Organization getOrganization() {
        Organization org = new Organization();
        org.setName("Org Name");
        OrganizationAddress add = new OrganizationAddress();
        add.setCity("city");
        add.setCountry(Iso3166Country.TT);
        org.setAddress(add);
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("abc456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        org.setDisambiguatedOrganization(disambiguatedOrg);
        return org;
    }

    public static PersonExternalIdentifier getPersonExternalIdentifier() {
        PersonExternalIdentifier newExtId = new PersonExternalIdentifier();
        newExtId.setType("new-common-name");
        newExtId.setValue("new-reference");
        newExtId.setUrl(new Url("http://newUrl.com"));
        newExtId.setVisibility(Visibility.LIMITED);
        return newExtId;
    }

    public static Keyword getKeyword() {
        Keyword keyword = new Keyword();
        keyword.setContent("New keyword");
        keyword.setVisibility(Visibility.LIMITED);
        return keyword;
    }

    public static OtherName getOtherName() {
        OtherName otherName = new OtherName();
        otherName.setContent("New Other Name");
        otherName.setVisibility(Visibility.LIMITED);
        return otherName;
    }

    public static ResearcherUrl getResearcherUrl() {
        ResearcherUrl rUrl = new ResearcherUrl();
        rUrl.setUrl(new Url("http://www.myRUrl.com"));
        rUrl.setUrlName("My researcher Url");
        rUrl.setVisibility(Visibility.LIMITED);
        return rUrl;
    }

    public static GroupIdRecord getGroupIdRecord() {
        GroupIdRecord newRecord = new GroupIdRecord();
        newRecord.setGroupId("issn:0000006");
        newRecord.setName("TestGroup5");
        newRecord.setDescription("TestDescription5");
        newRecord.setType("publisher");
        return newRecord;
    }

    public static Long getPutCode(Response response) {
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        return Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
    }
}
