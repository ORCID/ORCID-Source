package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc1.common.Iso3166Country;
import org.orcid.jaxb.model.v3.rc1.common.Locale;
import org.orcid.jaxb.model.v3.rc1.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.rc1.common.OrcidType;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.record.Address;
import org.orcid.jaxb.model.v3.rc1.record.Addresses;
import org.orcid.jaxb.model.v3.rc1.record.Biography;
import org.orcid.jaxb.model.v3.rc1.record.Distinction;
import org.orcid.jaxb.model.v3.rc1.record.Education;
import org.orcid.jaxb.model.v3.rc1.record.Email;
import org.orcid.jaxb.model.v3.rc1.record.Emails;
import org.orcid.jaxb.model.v3.rc1.record.Employment;
import org.orcid.jaxb.model.v3.rc1.record.Funding;
import org.orcid.jaxb.model.v3.rc1.record.FundingType;
import org.orcid.jaxb.model.v3.rc1.record.History;
import org.orcid.jaxb.model.v3.rc1.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc1.record.Keyword;
import org.orcid.jaxb.model.v3.rc1.record.Keywords;
import org.orcid.jaxb.model.v3.rc1.record.Membership;
import org.orcid.jaxb.model.v3.rc1.record.OtherName;
import org.orcid.jaxb.model.v3.rc1.record.OtherNames;
import org.orcid.jaxb.model.v3.rc1.record.PeerReview;
import org.orcid.jaxb.model.v3.rc1.record.Person;
import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.rc1.record.Qualification;
import org.orcid.jaxb.model.v3.rc1.record.Record;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.rc1.record.Service;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.WorkBulk;
import org.orcid.jaxb.model.v3.rc1.record.WorkType;
import org.orcid.jaxb.model.v3.rc1.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.v3.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV3ApiServiceDelegator_ReadRecordTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV3ApiServiceDelegatorV3_0_rc1")
    protected MemberV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work, WorkBulk, Address, Keyword> serviceDelegator;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Test
    public void testViewRecordWrongScope() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response response = serviceDelegator.viewRecord(ORCID);
        // Verify everything inside is public
        Record record = (Record) response.getEntity();
        assertNotNull(record);
        assertEquals("/0000-0000-0000-0003", record.getPath());
        assertEquals("/0000-0000-0000-0003/activities", record.getActivitiesSummary().getPath());
        Utils.assertIsPublicOrSource(record.getActivitiesSummary(), SecurityContextTestUtils.DEFAULT_CLIENT_ID);
        assertEquals("/0000-0000-0000-0003/person", record.getPerson().getPath());
        Utils.assertIsPublicOrSource(record.getPerson(), SecurityContextTestUtils.DEFAULT_CLIENT_ID);
    }

    @Test
    public void testViewRecordReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewRecord(ORCID);
        Record record = (Record) r.getEntity();
        assertNotNull(record);
        assertEquals("/0000-0000-0000-0003", record.getPath());
        assertEquals("/0000-0000-0000-0003/activities", record.getActivitiesSummary().getPath());
        Utils.assertIsPublicOrSource(record.getActivitiesSummary(), "APP-5555555555555555");
        assertEquals("/0000-0000-0000-0003/person", record.getPerson().getPath());
        Utils.assertIsPublicOrSource(record.getPerson(), "APP-5555555555555555");
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewRecordWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewRecord(ORCID);
    }    

    @Test
    public void testReadPublicScope_Record() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response response = serviceDelegator.viewRecord(ORCID);
        assertNotNull(response);
        Record record = (Record) response.getEntity();
        assertEquals("/0000-0000-0000-0003", record.getPath());
        testPerson(record.getPerson(), ORCID);
        testActivities(record.getActivitiesSummary(), ORCID);
        assertNotNull(record.getHistory());
        assertEquals(OrcidType.USER, record.getOrcidType());
        assertNotNull(record.getPreferences());
        assertEquals(Locale.EN, record.getPreferences().getLocale());
        History history = record.getHistory();
        assertTrue(history.getClaimed());
        assertNotNull(history.getCompletionDate());
        assertEquals(CreationMethod.INTEGRATION_TEST, history.getCreationMethod());
        assertNull(history.getDeactivationDate());
        Utils.verifyLastModified(history.getLastModifiedDate());
        assertNotNull(history.getSource());
        assertEquals("APP-5555555555555555", history.getSource().retrieveSourcePath());
        assertNotNull(history.getSubmissionDate());
        assertNotNull(record.getOrcidIdentifier());
        OrcidIdentifier id = record.getOrcidIdentifier();
        assertEquals("0000-0000-0000-0003", id.getPath());
    }

    @Test
    public void testViewRecord() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewRecord(ORCID);
        assertNotNull(response);
        Record record = (Record) response.getEntity();
        assertNotNull(record);
        assertEquals("/0000-0000-0000-0003", record.getPath());
        assertNotNull(record.getHistory());
        assertEquals(OrcidType.USER, record.getOrcidType());
        assertNotNull(record.getPreferences());
        assertEquals(Locale.EN, record.getPreferences().getLocale());
        History history = record.getHistory();
        assertTrue(history.getClaimed());
        assertNotNull(history.getCompletionDate());
        assertEquals(CreationMethod.INTEGRATION_TEST, history.getCreationMethod());
        assertNull(history.getDeactivationDate());
        Utils.verifyLastModified(history.getLastModifiedDate());
        assertNotNull(history.getSource());
        assertEquals("APP-5555555555555555", history.getSource().retrieveSourcePath());
        assertNotNull(history.getSubmissionDate());
        assertNotNull(record.getOrcidIdentifier());
        OrcidIdentifier id = record.getOrcidIdentifier();
        assertEquals("0000-0000-0000-0003", id.getPath());
        // Validate person
        Person person = record.getPerson();
        assertNotNull(person);
        assertEquals("/0000-0000-0000-0003/person", person.getPath());
        Utils.verifyLastModified(person.getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/person", person.getPath());
        assertNotNull(person.getAddresses());
        Utils.verifyLastModified(person.getAddresses().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/address", person.getAddresses().getPath());
        assertEquals(4, person.getAddresses().getAddress().size());
        for (Address address : person.getAddresses().getAddress()) {
            Utils.verifyLastModified(address.getLastModifiedDate());
            if (address.getPutCode().equals(Long.valueOf(9))) {
                assertEquals(Iso3166Country.US, address.getCountry().getValue());
                assertEquals(Long.valueOf(0), address.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/address/9", address.getPath());
                assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), address.getVisibility().value());
            } else if (address.getPutCode().equals(Long.valueOf(10))) {
                assertEquals(Iso3166Country.CR, address.getCountry().getValue());
                assertEquals(Long.valueOf(1), address.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/address/10", address.getPath());
                assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), address.getVisibility().value());
            } else if (address.getPutCode().equals(Long.valueOf(11))) {
                assertEquals(Iso3166Country.GB, address.getCountry().getValue());
                assertEquals(Long.valueOf(2), address.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/address/11", address.getPath());
                assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), address.getVisibility().value());
            } else if (address.getPutCode().equals(Long.valueOf(12))) {
                assertEquals(Iso3166Country.MX, address.getCountry().getValue());
                assertEquals(Long.valueOf(3), address.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/address/12", address.getPath());
                assertEquals("0000-0000-0000-0003", address.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), address.getVisibility().value());
            } else {
                fail("Invalid address found with put code: " + address.getPutCode());
            }
        }

        assertNotNull(person.getBiography());
        Utils.verifyLastModified(person.getBiography().getLastModifiedDate());
        assertEquals(Visibility.PUBLIC.value(), person.getBiography().getVisibility().value());
        assertEquals("Biography for 0000-0000-0000-0003", person.getBiography().getContent());

        assertNotNull(person.getEmails());
        Utils.verifyLastModified(person.getEmails().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/email", person.getEmails().getPath());
        assertEquals(4, person.getEmails().getEmails().size());
        for (Email email : person.getEmails().getEmails()) {
            Utils.verifyLastModified(email.getLastModifiedDate());
            if (email.getEmail().equals("public_0000-0000-0000-0003@test.orcid.org")) {
                assertEquals("APP-5555555555555555", email.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), email.getVisibility().value());
            } else if (email.getEmail().equals("limited_0000-0000-0000-0003@test.orcid.org")) {
                assertEquals("APP-5555555555555555", email.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), email.getVisibility().value());
            } else if (email.getEmail().equals("private_0000-0000-0000-0003@test.orcid.org")) {
                assertEquals("APP-5555555555555555", email.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), email.getVisibility().value());
            } else if (email.getEmail().equals("self_limited_0000-0000-0000-0003@test.orcid.org")) {
                assertEquals("0000-0000-0000-0003", email.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), email.getVisibility().value());
            } else {
                fail("Invalid email found: " + email.getEmail());
            }
        }

        assertNotNull(person.getExternalIdentifiers());
        Utils.verifyLastModified(person.getExternalIdentifiers().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/external-identifiers", person.getExternalIdentifiers().getPath());
        assertEquals(4, person.getExternalIdentifiers().getExternalIdentifiers().size());
        for (PersonExternalIdentifier extId : person.getExternalIdentifiers().getExternalIdentifiers()) {
            Utils.verifyLastModified(extId.getLastModifiedDate());
            if (extId.getPutCode().equals(Long.valueOf(13))) {
                assertEquals(Long.valueOf(0), extId.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/external-identifiers/13", extId.getPath());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
                assertEquals("public_type", extId.getType());
                assertEquals("http://ext-id/public_ref", extId.getUrl().getValue());
                assertEquals("public_ref", extId.getValue());
                assertEquals(Visibility.PUBLIC.value(), extId.getVisibility().value());
            } else if (extId.getPutCode().equals(Long.valueOf(14))) {
                assertEquals(Long.valueOf(1), extId.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/external-identifiers/14", extId.getPath());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
                assertEquals("limited_type", extId.getType());
                assertEquals("http://ext-id/limited_ref", extId.getUrl().getValue());
                assertEquals("limited_ref", extId.getValue());
                assertEquals(Visibility.LIMITED.value(), extId.getVisibility().value());
            } else if (extId.getPutCode().equals(Long.valueOf(15))) {
                assertEquals(Long.valueOf(2), extId.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/external-identifiers/15", extId.getPath());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
                assertEquals("private_type", extId.getType());
                assertEquals("http://ext-id/private_ref", extId.getUrl().getValue());
                assertEquals("private_ref", extId.getValue());
                assertEquals(Visibility.PRIVATE.value(), extId.getVisibility().value());
            } else if (extId.getPutCode().equals(Long.valueOf(16))) {
                assertEquals(Long.valueOf(3), extId.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/external-identifiers/16", extId.getPath());
                assertEquals("0000-0000-0000-0003", extId.getSource().retrieveSourcePath());
                assertEquals("self_limited_type", extId.getType());
                assertEquals("http://ext-id/self/limited", extId.getUrl().getValue());
                assertEquals("self_limited_ref", extId.getValue());
                assertEquals(Visibility.LIMITED.value(), extId.getVisibility().value());
            } else {
                fail("Invalid external identifier found: " + extId.getPutCode());
            }
        }

        assertNotNull(person.getKeywords());
        Utils.verifyLastModified(person.getKeywords().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/keywords", person.getKeywords().getPath());
        assertEquals(4, person.getKeywords().getKeywords().size());
        for (Keyword keyword : person.getKeywords().getKeywords()) {
            Utils.verifyLastModified(keyword.getLastModifiedDate());
            if (keyword.getPutCode().equals(Long.valueOf(9))) {
                assertEquals("PUBLIC", keyword.getContent());
                assertEquals(Long.valueOf(0), keyword.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/keywords/9", keyword.getPath());
                assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), keyword.getVisibility().value());
            } else if (keyword.getPutCode().equals(Long.valueOf(10))) {
                assertEquals("LIMITED", keyword.getContent());
                assertEquals(Long.valueOf(1), keyword.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/keywords/10", keyword.getPath());
                assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), keyword.getVisibility().value());
            } else if (keyword.getPutCode().equals(Long.valueOf(11))) {
                assertEquals("PRIVATE", keyword.getContent());
                assertEquals(Long.valueOf(2), keyword.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/keywords/11", keyword.getPath());
                assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), keyword.getVisibility().value());
            } else if (keyword.getPutCode().equals(Long.valueOf(12))) {
                assertEquals("SELF LIMITED", keyword.getContent());
                assertEquals(Long.valueOf(3), keyword.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/keywords/12", keyword.getPath());
                assertEquals("0000-0000-0000-0003", keyword.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), keyword.getVisibility().value());
            } else {
                fail("Invalid keyword found: " + keyword.getPutCode());
            }
        }

        assertNotNull(person.getName());
        Utils.verifyLastModified(person.getName().getLastModifiedDate());
        assertEquals("Credit Name", person.getName().getCreditName().getContent());
        assertEquals("Family Name", person.getName().getFamilyName().getContent());
        assertEquals("Given Names", person.getName().getGivenNames().getContent());
        assertEquals(Visibility.PUBLIC.value(), person.getName().getVisibility().value());

        assertNotNull(person.getOtherNames());
        Utils.verifyLastModified(person.getOtherNames().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/other-names", person.getOtherNames().getPath());
        assertEquals(4, person.getOtherNames().getOtherNames().size());
        for (OtherName otherName : person.getOtherNames().getOtherNames()) {
            Utils.verifyLastModified(otherName.getLastModifiedDate());
            if (otherName.getPutCode().equals(Long.valueOf(13))) {
                assertEquals("Other Name PUBLIC", otherName.getContent());
                assertEquals(Long.valueOf(0), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/13", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), otherName.getVisibility().value());
            } else if (otherName.getPutCode().equals(Long.valueOf(14))) {
                assertEquals("Other Name LIMITED", otherName.getContent());
                assertEquals(Long.valueOf(1), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/14", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), otherName.getVisibility().value());
            } else if (otherName.getPutCode().equals(Long.valueOf(15))) {
                assertEquals("Other Name PRIVATE", otherName.getContent());
                assertEquals(Long.valueOf(2), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/15", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), otherName.getVisibility().value());
            } else if (otherName.getPutCode().equals(Long.valueOf(16))) {
                assertEquals("Other Name SELF LIMITED", otherName.getContent());
                assertEquals(Long.valueOf(3), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/16", otherName.getPath());
                assertEquals("0000-0000-0000-0003", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), otherName.getVisibility().value());
            } else {
                fail("Invalid other name found: " + otherName.getPutCode());
            }
        }

        assertNotNull(person.getResearcherUrls());
        Utils.verifyLastModified(person.getResearcherUrls().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/researcher-urls", person.getResearcherUrls().getPath());
        assertEquals(4, person.getResearcherUrls().getResearcherUrls().size());
        for (ResearcherUrl rUrl : person.getResearcherUrls().getResearcherUrls()) {
            Utils.verifyLastModified(rUrl.getLastModifiedDate());
            if (rUrl.getPutCode().equals(Long.valueOf(13))) {
                assertEquals(Long.valueOf(0), rUrl.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/researcher-urls/13", rUrl.getPath());
                assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://www.researcherurl.com?id=13", rUrl.getUrl().getValue());
                assertEquals("public_rurl", rUrl.getUrlName());
                assertEquals(Visibility.PUBLIC.value(), rUrl.getVisibility().value());
            } else if (rUrl.getPutCode().equals(Long.valueOf(14))) {
                assertEquals(Long.valueOf(1), rUrl.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/researcher-urls/14", rUrl.getPath());
                assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://www.researcherurl.com?id=14", rUrl.getUrl().getValue());
                assertEquals("limited_rurl", rUrl.getUrlName());
                assertEquals(Visibility.LIMITED.value(), rUrl.getVisibility().value());
            } else if (rUrl.getPutCode().equals(Long.valueOf(15))) {
                assertEquals(Long.valueOf(2), rUrl.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/researcher-urls/15", rUrl.getPath());
                assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://www.researcherurl.com?id=15", rUrl.getUrl().getValue());
                assertEquals("private_rurl", rUrl.getUrlName());
                assertEquals(Visibility.PRIVATE.value(), rUrl.getVisibility().value());
            } else if (rUrl.getPutCode().equals(Long.valueOf(16))) {
                assertEquals(Long.valueOf(3), rUrl.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/researcher-urls/16", rUrl.getPath());
                assertEquals("0000-0000-0000-0003", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://www.researcherurl.com?id=16", rUrl.getUrl().getValue());
                assertEquals("self_limited_rurl", rUrl.getUrlName());
                assertEquals(Visibility.LIMITED.value(), rUrl.getVisibility().value());
            } else {
                fail("Invalid researcher url found: " + rUrl.getPutCode());
            }
        }

        // Validate activities
        ActivitiesSummary activities = record.getActivitiesSummary();
        assertNotNull(activities);
        assertEquals("/0000-0000-0000-0003/activities", activities.getPath());
        Utils.verifyLastModified(activities.getLastModifiedDate());
        
        assertNotNull(activities.getDistinctions());
        Utils.verifyLastModified(activities.getDistinctions().getLastModifiedDate());
        assertEquals(3, activities.getDistinctions().retrieveGroups().size());
        
        for (AffiliationGroup<DistinctionSummary> group : activities.getDistinctions().retrieveGroups()) {
            for (DistinctionSummary element : group.getActivities()) {
                Utils.verifyLastModified(element.getLastModifiedDate());
                if (element.getPutCode().equals(Long.valueOf(27))) {
                    assertEquals("PUBLIC Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/distinction/27", element.getPath());
                    assertEquals("PUBLIC", element.getRoleTitle());
                    assertEquals("APP-5555555555555555", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.PUBLIC.value(), element.getVisibility().value());
                } else if (element.getPutCode().equals(Long.valueOf(28))) {
                    assertEquals("LIMITED Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/distinction/28", element.getPath());
                    assertEquals("LIMITED", element.getRoleTitle());
                    assertEquals("APP-5555555555555555", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.LIMITED.value(), element.getVisibility().value());
                } else if (element.getPutCode().equals(Long.valueOf(29))) {
                    assertEquals("PRIVATE Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/distinction/29", element.getPath());
                    assertEquals("PRIVATE", element.getRoleTitle());
                    assertEquals("APP-5555555555555555", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.PRIVATE.value(), element.getVisibility().value());
                } else if (element.getPutCode().equals(Long.valueOf(30))) {
                    assertEquals("SELF LIMITED Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/distinction/30", element.getPath());
                    assertEquals("SELF LIMITED", element.getRoleTitle());
                    assertEquals("0000-0000-0000-0003", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.LIMITED.value(), element.getVisibility().value());
                } else {
                    fail("Invalid distinction found: " + element.getPutCode());
                }
            }
        }
        
        assertNotNull(activities.getEducations());
        Utils.verifyLastModified(activities.getEducations().getLastModifiedDate());
        assertEquals(3, activities.getEducations().retrieveGroups().size());
        
        for (AffiliationGroup<EducationSummary> group : activities.getEducations().retrieveGroups()) {
            for (EducationSummary education : group.getActivities()) {
                Utils.verifyLastModified(education.getLastModifiedDate());
                assertNotNull(education.getStartDate());
                assertEquals("2016", education.getStartDate().getYear().getValue());
                assertEquals("04", education.getStartDate().getMonth().getValue());
                assertEquals("01", education.getStartDate().getDay().getValue());
                assertNotNull(education.getEndDate());
                assertEquals("2030", education.getEndDate().getYear().getValue());
                assertEquals("01", education.getEndDate().getMonth().getValue());
                assertEquals("01", education.getEndDate().getDay().getValue());
                if (education.getPutCode().equals(Long.valueOf(20))) {
                    assertEquals("PUBLIC Department", education.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/education/20", education.getPath());
                    assertEquals("PUBLIC", education.getRoleTitle());
                    assertEquals("APP-5555555555555555", education.getSource().retrieveSourcePath());
                    assertEquals(Visibility.PUBLIC.value(), education.getVisibility().value());
                } else if (education.getPutCode().equals(Long.valueOf(21))) {
                    assertEquals("LIMITED Department", education.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/education/21", education.getPath());
                    assertEquals("LIMITED", education.getRoleTitle());
                    assertEquals("APP-5555555555555555", education.getSource().retrieveSourcePath());
                    assertEquals(Visibility.LIMITED.value(), education.getVisibility().value());
                } else if (education.getPutCode().equals(Long.valueOf(22))) {
                    assertEquals("PRIVATE Department", education.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/education/22", education.getPath());
                    assertEquals("PRIVATE", education.getRoleTitle());
                    assertEquals("APP-5555555555555555", education.getSource().retrieveSourcePath());
                    assertEquals(Visibility.PRIVATE.value(), education.getVisibility().value());
                } else if (education.getPutCode().equals(Long.valueOf(25))) {
                    assertEquals("SELF LIMITED Department", education.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/education/25", education.getPath());
                    assertEquals("SELF LIMITED", education.getRoleTitle());
                    assertEquals("0000-0000-0000-0003", education.getSource().retrieveSourcePath());
                    assertEquals(Visibility.LIMITED.value(), education.getVisibility().value());
                } else {
                    fail("Invalid education found: " + education.getPutCode());
                }
            }
        }

        assertNotNull(activities.getEmployments());
        Utils.verifyLastModified(activities.getEmployments().getLastModifiedDate());
        assertEquals(3, activities.getEmployments().retrieveGroups().size());
        
        for (AffiliationGroup<EmploymentSummary> group : activities.getEmployments().retrieveGroups()) {
            for (EmploymentSummary employment : group.getActivities()) {
                Utils.verifyLastModified(employment.getLastModifiedDate());
                if (employment.getPutCode().equals(Long.valueOf(17))) {
                    assertEquals("PUBLIC Department", employment.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/employment/17", employment.getPath());
                    assertEquals("PUBLIC", employment.getRoleTitle());
                    assertEquals("APP-5555555555555555", employment.getSource().retrieveSourcePath());
                    assertEquals(Visibility.PUBLIC.value(), employment.getVisibility().value());
                } else if (employment.getPutCode().equals(Long.valueOf(18))) {
                    assertEquals("LIMITED Department", employment.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/employment/18", employment.getPath());
                    assertEquals("LIMITED", employment.getRoleTitle());
                    assertEquals("APP-5555555555555555", employment.getSource().retrieveSourcePath());
                    assertEquals(Visibility.LIMITED.value(), employment.getVisibility().value());
                } else if (employment.getPutCode().equals(Long.valueOf(19))) {
                    assertEquals("PRIVATE Department", employment.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/employment/19", employment.getPath());
                    assertEquals("PRIVATE", employment.getRoleTitle());
                    assertEquals("APP-5555555555555555", employment.getSource().retrieveSourcePath());
                    assertEquals(Visibility.PRIVATE.value(), employment.getVisibility().value());
                } else if (employment.getPutCode().equals(Long.valueOf(23))) {
                    assertEquals("SELF LIMITED Department", employment.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/employment/23", employment.getPath());
                    assertEquals("SELF LIMITED", employment.getRoleTitle());
                    assertEquals("0000-0000-0000-0003", employment.getSource().retrieveSourcePath());
                    assertEquals(Visibility.LIMITED.value(), employment.getVisibility().value());
                } else {
                    fail("Invalid employment found: " + employment.getPutCode());
                }
            }
        }

        assertNotNull(activities.getInvitedPositions());
        Utils.verifyLastModified(activities.getInvitedPositions().getLastModifiedDate());
        assertEquals(3, activities.getInvitedPositions().retrieveGroups().size());
        
        for (AffiliationGroup<InvitedPositionSummary> group : activities.getInvitedPositions().retrieveGroups()) {
            for (InvitedPositionSummary element : group.getActivities()) {
                Utils.verifyLastModified(element.getLastModifiedDate());
                if (element.getPutCode().equals(Long.valueOf(32))) {
                    assertEquals("PUBLIC Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/invited-position/32", element.getPath());
                    assertEquals("PUBLIC", element.getRoleTitle());
                    assertEquals("APP-5555555555555555", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.PUBLIC.value(), element.getVisibility().value());
                } else if (element.getPutCode().equals(Long.valueOf(33))) {
                    assertEquals("LIMITED Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/invited-position/33", element.getPath());
                    assertEquals("LIMITED", element.getRoleTitle());
                    assertEquals("APP-5555555555555555", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.LIMITED.value(), element.getVisibility().value());
                } else if (element.getPutCode().equals(Long.valueOf(34))) {
                    assertEquals("PRIVATE Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/invited-position/34", element.getPath());
                    assertEquals("PRIVATE", element.getRoleTitle());
                    assertEquals("APP-5555555555555555", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.PRIVATE.value(), element.getVisibility().value());
                } else if (element.getPutCode().equals(Long.valueOf(35))) {
                    assertEquals("SELF LIMITED Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/invited-position/35", element.getPath());
                    assertEquals("SELF LIMITED", element.getRoleTitle());
                    assertEquals("0000-0000-0000-0003", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.LIMITED.value(), element.getVisibility().value());
                } else {
                    fail("Invalid invited position found: " + element.getPutCode());
                }
            }
        }
        
        assertNotNull(activities.getMemberships());
        Utils.verifyLastModified(activities.getMemberships().getLastModifiedDate());
        assertEquals(3, activities.getMemberships().retrieveGroups().size());
        
        for (AffiliationGroup<MembershipSummary> group : activities.getMemberships().retrieveGroups()) {
            for (MembershipSummary element : group.getActivities()) {
                Utils.verifyLastModified(element.getLastModifiedDate());
                if (element.getPutCode().equals(Long.valueOf(37))) {
                    assertEquals("PUBLIC Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/membership/37", element.getPath());
                    assertEquals("PUBLIC", element.getRoleTitle());
                    assertEquals("APP-5555555555555555", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.PUBLIC.value(), element.getVisibility().value());
                } else if (element.getPutCode().equals(Long.valueOf(38))) {
                    assertEquals("LIMITED Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/membership/38", element.getPath());
                    assertEquals("LIMITED", element.getRoleTitle());
                    assertEquals("APP-5555555555555555", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.LIMITED.value(), element.getVisibility().value());
                } else if (element.getPutCode().equals(Long.valueOf(39))) {
                    assertEquals("PRIVATE Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/membership/39", element.getPath());
                    assertEquals("PRIVATE", element.getRoleTitle());
                    assertEquals("APP-5555555555555555", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.PRIVATE.value(), element.getVisibility().value());
                } else if (element.getPutCode().equals(Long.valueOf(40))) {
                    assertEquals("SELF LIMITED Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/membership/40", element.getPath());
                    assertEquals("SELF LIMITED", element.getRoleTitle());
                    assertEquals("0000-0000-0000-0003", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.LIMITED.value(), element.getVisibility().value());
                } else {
                    fail("Invalid invited position found: " + element.getPutCode());
                }
            }
        }
        
        assertNotNull(activities.getQualifications());
        Utils.verifyLastModified(activities.getQualifications().getLastModifiedDate());
        assertEquals(3, activities.getQualifications().retrieveGroups().size());
        
        for (AffiliationGroup<QualificationSummary> group : activities.getQualifications().retrieveGroups()) {
            for (QualificationSummary element : group.getActivities()) {
                Utils.verifyLastModified(element.getLastModifiedDate());
                if (element.getPutCode().equals(Long.valueOf(42))) {
                    assertEquals("PUBLIC Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/qualification/42", element.getPath());
                    assertEquals("PUBLIC", element.getRoleTitle());
                    assertEquals("APP-5555555555555555", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.PUBLIC.value(), element.getVisibility().value());
                } else if (element.getPutCode().equals(Long.valueOf(43))) {
                    assertEquals("LIMITED Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/qualification/43", element.getPath());
                    assertEquals("LIMITED", element.getRoleTitle());
                    assertEquals("APP-5555555555555555", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.LIMITED.value(), element.getVisibility().value());
                } else if (element.getPutCode().equals(Long.valueOf(44))) {
                    assertEquals("PRIVATE Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/qualification/44", element.getPath());
                    assertEquals("PRIVATE", element.getRoleTitle());
                    assertEquals("APP-5555555555555555", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.PRIVATE.value(), element.getVisibility().value());
                } else if (element.getPutCode().equals(Long.valueOf(45))) {
                    assertEquals("SELF LIMITED Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/qualification/45", element.getPath());
                    assertEquals("SELF LIMITED", element.getRoleTitle());
                    assertEquals("0000-0000-0000-0003", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.LIMITED.value(), element.getVisibility().value());
                } else {
                    fail("Invalid invited position found: " + element.getPutCode());
                }
            }
        }
        
        
        assertNotNull(activities.getServices());
        Utils.verifyLastModified(activities.getServices().getLastModifiedDate());
        assertEquals(3, activities.getServices().retrieveGroups().size());
        
        for (AffiliationGroup<ServiceSummary> group : activities.getServices().retrieveGroups()) {
            for (ServiceSummary element : group.getActivities()) {
                Utils.verifyLastModified(element.getLastModifiedDate());
                if (element.getPutCode().equals(Long.valueOf(47))) {
                    assertEquals("PUBLIC Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/service/47", element.getPath());
                    assertEquals("PUBLIC", element.getRoleTitle());
                    assertEquals("APP-5555555555555555", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.PUBLIC.value(), element.getVisibility().value());
                } else if (element.getPutCode().equals(Long.valueOf(48))) {
                    assertEquals("LIMITED Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/service/48", element.getPath());
                    assertEquals("LIMITED", element.getRoleTitle());
                    assertEquals("APP-5555555555555555", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.LIMITED.value(), element.getVisibility().value());
                } else if (element.getPutCode().equals(Long.valueOf(49))) {
                    assertEquals("PRIVATE Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/service/49", element.getPath());
                    assertEquals("PRIVATE", element.getRoleTitle());
                    assertEquals("APP-5555555555555555", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.PRIVATE.value(), element.getVisibility().value());
                } else if (element.getPutCode().equals(Long.valueOf(50))) {
                    assertEquals("SELF LIMITED Department", element.getDepartmentName());
                    assertEquals("/0000-0000-0000-0003/service/50", element.getPath());
                    assertEquals("SELF LIMITED", element.getRoleTitle());
                    assertEquals("0000-0000-0000-0003", element.getSource().retrieveSourcePath());
                    assertEquals(Visibility.LIMITED.value(), element.getVisibility().value());
                } else {
                    fail("Invalid invited position found: " + element.getPutCode());
                }
            }
        }
        
        assertNotNull(activities.getFundings());
        Utils.verifyLastModified(activities.getFundings().getLastModifiedDate());
        assertEquals(4, activities.getFundings().getFundingGroup().size());
        for (FundingGroup group : activities.getFundings().getFundingGroup()) {
            Utils.verifyLastModified(group.getLastModifiedDate());
            assertNotNull(group.getIdentifiers());
            assertNotNull(group.getIdentifiers().getExternalIdentifier());
            assertEquals(1, group.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(group.getFundingSummary());
            assertEquals(1, group.getFundingSummary().size());
            FundingSummary funding = group.getFundingSummary().get(0);
            Utils.verifyLastModified(funding.getLastModifiedDate());
            if (funding.getPutCode().equals(Long.valueOf(10))) {
                assertEquals("0", funding.getDisplayIndex());
                assertNotNull(funding.getExternalIdentifiers());
                assertEquals(1, funding.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("1", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("/0000-0000-0000-0003/funding/10", funding.getPath());
                assertEquals("APP-5555555555555555", funding.getSource().retrieveSourcePath());
                assertEquals("PUBLIC", funding.getTitle().getTitle().getContent());
                assertEquals(FundingType.SALARY_AWARD.value(), funding.getType().value());
                assertEquals(Visibility.PUBLIC.value(), funding.getVisibility().value());
            } else if (funding.getPutCode().equals(Long.valueOf(11))) {
                assertEquals("1", funding.getDisplayIndex());
                assertNotNull(funding.getExternalIdentifiers());
                assertEquals(1, funding.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("2", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("/0000-0000-0000-0003/funding/11", funding.getPath());
                assertEquals("APP-5555555555555555", funding.getSource().retrieveSourcePath());
                assertEquals("LIMITED", funding.getTitle().getTitle().getContent());
                assertEquals(FundingType.SALARY_AWARD.value(), funding.getType().value());
                assertEquals(Visibility.LIMITED.value(), funding.getVisibility().value());
            } else if (funding.getPutCode().equals(Long.valueOf(12))) {
                assertEquals("2", funding.getDisplayIndex());
                assertNotNull(funding.getExternalIdentifiers());
                assertEquals(1, funding.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("3", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("/0000-0000-0000-0003/funding/12", funding.getPath());
                assertEquals("APP-5555555555555555", funding.getSource().retrieveSourcePath());
                assertEquals("PRIVATE", funding.getTitle().getTitle().getContent());
                assertEquals(FundingType.SALARY_AWARD.value(), funding.getType().value());
                assertEquals(Visibility.PRIVATE.value(), funding.getVisibility().value());
            } else if (funding.getPutCode().equals(Long.valueOf(13))) {
                assertEquals("3", funding.getDisplayIndex());
                assertNotNull(funding.getExternalIdentifiers());
                assertEquals(1, funding.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("4", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("/0000-0000-0000-0003/funding/13", funding.getPath());
                assertEquals("0000-0000-0000-0003", funding.getSource().retrieveSourcePath());
                assertEquals("SELF LIMITED", funding.getTitle().getTitle().getContent());
                assertEquals(FundingType.SALARY_AWARD.value(), funding.getType().value());
                assertEquals(Visibility.LIMITED.value(), funding.getVisibility().value());
            } else {
                fail("Invalid funding found: " + funding.getPutCode());
            }
        }

        assertNotNull(activities.getPeerReviews());
        Utils.verifyLastModified(activities.getPeerReviews().getLastModifiedDate());
        assertEquals(4, activities.getPeerReviews().getPeerReviewGroup().size());
        for (PeerReviewGroup group : activities.getPeerReviews().getPeerReviewGroup()) {
            Utils.verifyLastModified(group.getLastModifiedDate());
            assertNotNull(group.getIdentifiers());
            assertNotNull(group.getIdentifiers().getExternalIdentifier());
            assertEquals(1, group.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(group.getPeerReviewGroup().get(0).getPeerReviewSummary());
            assertEquals(1, group.getPeerReviewGroup().get(0).getPeerReviewSummary().size());
            PeerReviewSummary peerReview = group.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0);
            Utils.verifyLastModified(peerReview.getLastModifiedDate());
            assertNotNull(peerReview.getCompletionDate());
            assertEquals("2016", peerReview.getCompletionDate().getYear().getValue());
            assertEquals("02", peerReview.getCompletionDate().getMonth().getValue());
            assertEquals("02", peerReview.getCompletionDate().getDay().getValue());
            assertNotNull(peerReview.getExternalIdentifiers());
            assertEquals(1, peerReview.getExternalIdentifiers().getExternalIdentifier().size());
            assertEquals("agr", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
            if (peerReview.getPutCode().equals(Long.valueOf(9))) {
                assertEquals("0", peerReview.getDisplayIndex());
                assertEquals("issn:0000009", peerReview.getGroupId());
                assertEquals("/0000-0000-0000-0003/peer-review/9", peerReview.getPath());
                assertEquals("work:external-identifier-id#1", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), peerReview.getVisibility().value());
            } else if (peerReview.getPutCode().equals(Long.valueOf(10))) {
                assertEquals("1", peerReview.getDisplayIndex());
                assertEquals("issn:0000010", peerReview.getGroupId());
                assertEquals("/0000-0000-0000-0003/peer-review/10", peerReview.getPath());
                assertEquals("work:external-identifier-id#2", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), peerReview.getVisibility().value());
            } else if (peerReview.getPutCode().equals(Long.valueOf(11))) {
                assertEquals("2", peerReview.getDisplayIndex());
                assertEquals("issn:0000011", peerReview.getGroupId());
                assertEquals("/0000-0000-0000-0003/peer-review/11", peerReview.getPath());
                assertEquals("work:external-identifier-id#3", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), peerReview.getVisibility().value());
            } else if (peerReview.getPutCode().equals(Long.valueOf(12))) {
                assertEquals("3", peerReview.getDisplayIndex());
                assertEquals("issn:0000012", peerReview.getGroupId());
                assertEquals("/0000-0000-0000-0003/peer-review/12", peerReview.getPath());
                assertEquals("work:external-identifier-id#4", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("0000-0000-0000-0003", peerReview.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), peerReview.getVisibility().value());
            } else {
                fail("Invalid peer review found: " + peerReview.getPutCode());
            }
        }

        assertNotNull(activities.getWorks());
        Utils.verifyLastModified(activities.getWorks().getLastModifiedDate());
        assertEquals(4, activities.getWorks().getWorkGroup().size());
        for (WorkGroup group : activities.getWorks().getWorkGroup()) {
            Utils.verifyLastModified(group.getLastModifiedDate());
            assertNotNull(group.getIdentifiers());
            assertNotNull(group.getIdentifiers().getExternalIdentifier());
            assertEquals(1, group.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(group.getWorkSummary());
            assertEquals(1, group.getWorkSummary().size());
            WorkSummary work = group.getWorkSummary().get(0);
            Utils.verifyLastModified(work.getLastModifiedDate());
            if (work.getPutCode().equals(Long.valueOf(11))) {
                assertEquals("0", work.getDisplayIndex());
                assertNotNull(work.getExternalIdentifiers());
                assertEquals(1, work.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("doi", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
                assertEquals("1", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("2016", work.getPublicationDate().getYear().getValue());
                assertEquals("01", work.getPublicationDate().getMonth().getValue());
                assertEquals("01", work.getPublicationDate().getDay().getValue());
                assertEquals("APP-5555555555555555", work.getSource().retrieveSourcePath());
                assertEquals("PUBLIC", work.getTitle().getTitle().getContent());
                assertEquals(WorkType.JOURNAL_ARTICLE.value(), work.getType().value());
                assertEquals(Visibility.PUBLIC.value(), work.getVisibility().value());
                assertEquals("/0000-0000-0000-0003/work/11", work.getPath());
            } else if (work.getPutCode().equals(Long.valueOf(12))) {
                assertEquals("1", work.getDisplayIndex());
                assertNotNull(work.getExternalIdentifiers());
                assertEquals(1, work.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("doi", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
                assertEquals("2", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("2016", work.getPublicationDate().getYear().getValue());
                assertEquals("01", work.getPublicationDate().getMonth().getValue());
                assertEquals("01", work.getPublicationDate().getDay().getValue());
                assertEquals("APP-5555555555555555", work.getSource().retrieveSourcePath());
                assertEquals("LIMITED", work.getTitle().getTitle().getContent());
                assertEquals(WorkType.JOURNAL_ARTICLE.value(), work.getType().value());
                assertEquals(Visibility.LIMITED.value(), work.getVisibility().value());
                assertEquals("/0000-0000-0000-0003/work/12", work.getPath());
            } else if (work.getPutCode().equals(Long.valueOf(13))) {
                assertEquals("2", work.getDisplayIndex());
                assertNotNull(work.getExternalIdentifiers());
                assertEquals(1, work.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("doi", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
                assertEquals("3", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("2016", work.getPublicationDate().getYear().getValue());
                assertEquals("01", work.getPublicationDate().getMonth().getValue());
                assertEquals("01", work.getPublicationDate().getDay().getValue());
                assertEquals("APP-5555555555555555", work.getSource().retrieveSourcePath());
                assertEquals("PRIVATE", work.getTitle().getTitle().getContent());
                assertEquals(WorkType.JOURNAL_ARTICLE.value(), work.getType().value());
                assertEquals(Visibility.PRIVATE.value(), work.getVisibility().value());
                assertEquals("/0000-0000-0000-0003/work/13", work.getPath());
            } else if (work.getPutCode().equals(Long.valueOf(14))) {
                assertEquals("3", work.getDisplayIndex());
                assertNotNull(work.getExternalIdentifiers());
                assertEquals(1, work.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("doi", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
                assertEquals("4", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("2016", work.getPublicationDate().getYear().getValue());
                assertEquals("01", work.getPublicationDate().getMonth().getValue());
                assertEquals("01", work.getPublicationDate().getDay().getValue());
                assertEquals("0000-0000-0000-0003", work.getSource().retrieveSourcePath());
                assertEquals("SELF LIMITED", work.getTitle().getTitle().getContent());
                assertEquals(WorkType.JOURNAL_ARTICLE.value(), work.getType().value());
                assertEquals(Visibility.LIMITED.value(), work.getVisibility().value());
                assertEquals("/0000-0000-0000-0003/work/14", work.getPath());
            } else {
                fail("Invalid work found: " + work.getPutCode());
            }
        }
    }

    private void testActivities(ActivitiesSummary as, String orcid) {
        boolean found1 = false, found2 = false, found3 = false;
        // This is more an utility that will work only for 0000-0000-0000-0003
        assertEquals("0000-0000-0000-0003", orcid);

        assertNotNull(as);
        assertEquals("/0000-0000-0000-0003/activities", as.getPath());
        Utils.verifyLastModified(as.getLastModifiedDate());
        assertNotNull(as.getEducations());
        assertEquals(3, as.getEducations().retrieveGroups().size());

        for (AffiliationGroup<EducationSummary> group : as.getEducations().retrieveGroups()) {
            for (EducationSummary element : group.getActivities()) {
                if (element.getPutCode().equals(Long.valueOf(20))) {
                    found1 = true;
                } else if (element.getPutCode().equals(Long.valueOf(21))) {
                    found2 = true;
                } else if (element.getPutCode().equals(Long.valueOf(22))) {
                    found3 = true;
                } else {
                    fail("Invalid put code " + element.getPutCode());
                }
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getEmployments());
        assertEquals(3, as.getEmployments().retrieveGroups().size());

        for (AffiliationGroup<EmploymentSummary> group : as.getEmployments().retrieveGroups()) {
            for (EmploymentSummary element : group.getActivities()) {
                if (element.getPutCode().equals(Long.valueOf(17))) {
                    found1 = true;
                } else if (element.getPutCode().equals(Long.valueOf(18))) {
                    found2 = true;
                } else if (element.getPutCode().equals(Long.valueOf(19))) {
                    found3 = true;
                } else {
                    fail("Invalid put code " + element.getPutCode());
                }
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getFundings());
        assertEquals(3, as.getFundings().getFundingGroup().size());

        for (FundingGroup group : as.getFundings().getFundingGroup()) {
            assertEquals(1, group.getFundingSummary().size());
            FundingSummary element = group.getFundingSummary().get(0);
            if (element.getPutCode().equals(Long.valueOf(10))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(11))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(12))) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getPeerReviews());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());

        for (PeerReviewGroup group : as.getPeerReviews().getPeerReviewGroup()) {
            assertEquals(1, group.getPeerReviewGroup().get(0).getPeerReviewSummary().size());
            PeerReviewSummary element = group.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0);
            if (element.getPutCode().equals(Long.valueOf(9))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(10))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(11))) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getWorks());
        assertEquals(3, as.getWorks().getWorkGroup().size());

        for (WorkGroup group : as.getWorks().getWorkGroup()) {
            assertEquals(1, group.getWorkSummary().size());
            WorkSummary element = group.getWorkSummary().get(0);
            if (element.getPutCode().equals(Long.valueOf(11))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(12))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(13))) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;
    }

    private void testPerson(Person p, String orcid) {
        // This is more an utility that will work only for 0000-0000-0000-0003
        assertEquals("0000-0000-0000-0003", orcid);
        assertNotNull(p);
        assertEquals("/0000-0000-0000-0003/person", p.getPath());
        Utils.verifyLastModified(p.getLastModifiedDate());
        // Address
        assertNotNull(p.getAddresses());
        Addresses a = p.getAddresses();
        assertNotNull(a);
        Utils.verifyLastModified(a.getLastModifiedDate());
        assertEquals(3, a.getAddress().size());

        boolean found1 = false, found2 = false, found3 = false;
        for (Address element : a.getAddress()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            if (element.getPutCode() == 9) {
                found1 = true;
            } else if (element.getPutCode() == 10) {
                found2 = true;
            } else if (element.getPutCode() == 11) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);

        // Biography
        assertNotNull(p.getBiography());
        Biography b = p.getBiography();
        assertNotNull(b);
        Utils.verifyLastModified(b.getLastModifiedDate());

        assertEquals("Biography for 0000-0000-0000-0003", b.getContent());

        // Email
        assertNotNull(p.getEmails());
        Emails email = p.getEmails();
        assertNotNull(email);
        Utils.verifyLastModified(email.getLastModifiedDate());
        assertEquals(3, email.getEmails().size());
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", email.getEmails().get(0).getEmail());

        found1 = false;
        found2 = false;
        found3 = false;

        for (Email element : email.getEmails()) {
            if (element.getEmail().equals("public_0000-0000-0000-0003@test.orcid.org")) {
                found1 = true;
            } else if (element.getEmail().equals("limited_0000-0000-0000-0003@test.orcid.org")) {
                found2 = true;
            } else if (element.getEmail().equals("private_0000-0000-0000-0003@test.orcid.org")) {
                found3 = true;
            } else {
                fail("Invalid email " + element.getEmail());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);

        // External identifiers
        assertNotNull(p.getExternalIdentifiers());
        PersonExternalIdentifiers extIds = p.getExternalIdentifiers();
        assertNotNull(extIds);
        Utils.verifyLastModified(extIds.getLastModifiedDate());
        assertEquals(3, extIds.getExternalIdentifiers().size());
        found1 = false;
        found2 = false;
        found3 = false;
        for (PersonExternalIdentifier element : extIds.getExternalIdentifiers()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            if (element.getPutCode() == 13) {
                found1 = true;
            } else if (element.getPutCode() == 14) {
                found2 = true;
            } else if (element.getPutCode() == 15) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);

        // Keywords
        assertNotNull(p.getKeywords());
        Keywords k = p.getKeywords();
        assertNotNull(k);
        Utils.verifyLastModified(k.getLastModifiedDate());
        assertEquals(3, k.getKeywords().size());
        found1 = false;
        found2 = false;
        found3 = false;
        for (Keyword element : k.getKeywords()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            if (element.getPutCode() == 9) {
                found1 = true;
            } else if (element.getPutCode() == 10) {
                found2 = true;
            } else if (element.getPutCode() == 11) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);

        // Name
        assertNotNull(p.getName());
        assertEquals("Credit Name", p.getName().getCreditName().getContent());
        assertEquals("Given Names", p.getName().getGivenNames().getContent());
        assertEquals("Family Name", p.getName().getFamilyName().getContent());

        // Other names
        assertNotNull(p.getOtherNames());
        OtherNames o = p.getOtherNames();
        assertNotNull(o);
        Utils.verifyLastModified(o.getLastModifiedDate());
        assertEquals(3, o.getOtherNames().size());
        found1 = false;
        found2 = false;
        found3 = false;
        for (OtherName element : o.getOtherNames()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            if (element.getPutCode() == 13) {
                found1 = true;
            } else if (element.getPutCode() == 14) {
                found2 = true;
            } else if (element.getPutCode() == 15) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);

        // Researcher urls
        assertNotNull(p.getResearcherUrls());
        ResearcherUrls ru = p.getResearcherUrls();
        assertNotNull(ru);
        Utils.verifyLastModified(ru.getLastModifiedDate());
        assertEquals(3, ru.getResearcherUrls().size());
        found1 = false;
        found2 = false;
        found3 = false;
        for (ResearcherUrl element : ru.getResearcherUrls()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            if (element.getPutCode() == 13) {
                found1 = true;
            } else if (element.getPutCode() == 14) {
                found2 = true;
            } else if (element.getPutCode() == 15) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);

        assertNotNull(p.getPath());
    }
    
    @Test
    public void testReadPrivateEmails_OtherThingsJustPublic_Record() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, "APP-5555555555555556", ScopePathType.EMAIL_READ_PRIVATE);
        Response response = serviceDelegator.viewRecord(ORCID);
        assertNotNull(response);
        assertEquals(Record.class.getName(), response.getEntity().getClass().getName());
        Record r = (Record) response.getEntity();
        assertNotNull(r);
        assertEquals("/0000-0000-0000-0003", r.getPath());
        Utils.assertIsPublicOrSource(r.getActivitiesSummary(), "APP-5555555555555556");
        Person p = r.getPerson();

        // Check email
        // Email
        assertNotNull(p.getEmails());
        Emails email = p.getEmails();
        assertNotNull(email);
        Utils.verifyLastModified(email.getLastModifiedDate());
        assertEquals(5, email.getEmails().size());

        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false;

        for (Email element : email.getEmails()) {
            if (element.getEmail().equals("public_0000-0000-0000-0003@test.orcid.org")) {
                found1 = true;
            } else if (element.getEmail().equals("limited_0000-0000-0000-0003@test.orcid.org")) {
                found2 = true;
            } else if (element.getEmail().equals("private_0000-0000-0000-0003@test.orcid.org")) {
                found3 = true;
            } else if (element.getEmail().equals("self_limited_0000-0000-0000-0003@test.orcid.org")) {
                found4 = true;
            } else if (element.getEmail().equals("self_private_0000-0000-0000-0003@test.orcid.org")) {
                found5 = true;
            } else {
                fail("Invalid email " + element.getEmail());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        assertTrue(found5);

        this.assertAllPublicButEmails(p);
    }
    
    private void assertAllPublicButEmails(Person p) {
        assertNotNull(p);
        Utils.verifyLastModified(p.getLastModifiedDate());

        // Address
        assertNotNull(p.getAddresses());
        Addresses a = p.getAddresses();
        assertNotNull(a);
        Utils.verifyLastModified(a.getLastModifiedDate());
        assertEquals(1, a.getAddress().size());
        assertEquals(Long.valueOf(9), a.getAddress().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, a.getAddress().get(0).getVisibility());

        // Biography
        assertNotNull(p.getBiography());
        Biography b = p.getBiography();
        assertNotNull(b);
        Utils.verifyLastModified(b.getLastModifiedDate());

        assertEquals("Biography for 0000-0000-0000-0003", b.getContent());

        // External identifiers
        assertNotNull(p.getExternalIdentifiers());
        PersonExternalIdentifiers extIds = p.getExternalIdentifiers();
        assertNotNull(extIds);
        Utils.verifyLastModified(extIds.getLastModifiedDate());
        assertEquals(1, extIds.getExternalIdentifiers().size());
        assertEquals(Long.valueOf(13), extIds.getExternalIdentifiers().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, extIds.getExternalIdentifiers().get(0).getVisibility());

        // Keywords
        assertNotNull(p.getKeywords());
        Keywords k = p.getKeywords();
        assertNotNull(k);
        Utils.verifyLastModified(k.getLastModifiedDate());
        assertEquals(1, k.getKeywords().size());
        assertEquals(Long.valueOf(9), k.getKeywords().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, k.getKeywords().get(0).getVisibility());

        // Name
        assertNotNull(p.getName());
        assertEquals("Credit Name", p.getName().getCreditName().getContent());
        assertEquals("Given Names", p.getName().getGivenNames().getContent());
        assertEquals("Family Name", p.getName().getFamilyName().getContent());

        // Other names
        assertNotNull(p.getOtherNames());
        OtherNames o = p.getOtherNames();
        assertNotNull(o);
        Utils.verifyLastModified(o.getLastModifiedDate());
        assertEquals(1, o.getOtherNames().size());
        assertEquals(Long.valueOf(13), o.getOtherNames().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, o.getOtherNames().get(0).getVisibility());

        // Researcher urls
        assertNotNull(p.getResearcherUrls());
        ResearcherUrls ru = p.getResearcherUrls();
        assertNotNull(ru);
        Utils.verifyLastModified(ru.getLastModifiedDate());
        assertEquals(1, ru.getResearcherUrls().size());
        assertEquals(Long.valueOf(13), ru.getResearcherUrls().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, ru.getResearcherUrls().get(0).getVisibility());
    }
}
