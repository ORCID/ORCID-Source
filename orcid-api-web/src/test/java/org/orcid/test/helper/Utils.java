package org.orcid.test.helper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.orcid.jaxb.model.common_v2.Country;
import org.orcid.jaxb.model.common_v2.Filterable;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.LastModifiedDate;
import org.orcid.jaxb.model.common_v2.Organization;
import org.orcid.jaxb.model.common_v2.OrganizationAddress;
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.common_v2.VisibilityType;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_v2.ActivitiesContainer;
import org.orcid.jaxb.model.record_v2.Activity;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.FundingTitle;
import org.orcid.jaxb.model.record_v2.FundingType;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.OtherNames;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.PeerReviewType;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.PersonalDetails;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.jaxb.model.record_v2.Role;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkTitle;
import org.orcid.jaxb.model.record_v2.WorkType;

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

    public static Education getEducation() {
        Education education = new Education();
        education.setDepartmentName("My department name");
        education.setRoleTitle("My Role");
        education.setOrganization(getOrganization());
        return education;
    }

    public static Employment getEmployment() {
        Employment employment = new Employment();
        employment.setDepartmentName("My department name");
        employment.setRoleTitle("My Role");
        employment.setOrganization(getOrganization());
        return employment;
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
        peerReview.setSubjectType(WorkType.DATA_SET);
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
        newRecord.setGroupId("issn:1234-5678");
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
    
    public static GroupIdRecord getNonIssnGroupIdRecord() {
        GroupIdRecord newRecord = new GroupIdRecord();
        newRecord.setGroupId("publons:errrmmmmm");
        newRecord.setName("TestGroup5");
        newRecord.setDescription("TestDescription5");
        newRecord.setType("publisher");
        return newRecord;
    }
}
