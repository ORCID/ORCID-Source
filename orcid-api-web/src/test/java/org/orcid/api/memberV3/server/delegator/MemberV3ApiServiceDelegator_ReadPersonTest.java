package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.v3.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV3ApiServiceDelegator_ReadPersonTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml",
            "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV3ApiServiceDelegator")
    protected MemberV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work, WorkBulk, Address, Keyword, ResearchResource> serviceDelegator;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPersonWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPerson(ORCID);
    }

    @Test
    public void testViewPersonReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewPerson(ORCID);
        Person element = (Person) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/person", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testReadPublicScope_Person() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewPerson(ORCID);
        assertNotNull(r);
        assertEquals(Person.class.getName(), r.getEntity().getClass().getName());
        Person p = (Person) r.getEntity();
        testPerson(p, ORCID);
    }

    @Test
    public void testViewPerson() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewPerson(ORCID);
        assertNotNull(response);
        assertEquals(Person.class.getName(), response.getEntity().getClass().getName());
        Person p = (Person) response.getEntity();
        assertNotNull(p);
        assertEquals("/0000-0000-0000-0003/person", p.getPath());
        Utils.verifyLastModified(p.getLastModifiedDate());

        // Address
        assertNotNull(p.getAddresses());
        Addresses a = p.getAddresses();
        assertNotNull(a);
        Utils.verifyLastModified(a.getLastModifiedDate());
        assertEquals(4, a.getAddress().size());

        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        for (Address element : a.getAddress()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            if (element.getPutCode() == 9) {
                found1 = true;
            } else if (element.getPutCode() == 10) {
                found2 = true;
            } else if (element.getPutCode() == 11) {
                found3 = true;
            } else if (element.getPutCode() == 12) {
                found4 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);

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
        assertEquals(4, email.getEmails().size());

        found1 = false;
        found2 = false;
        found3 = false;
        found4 = false;

        for (Email element : email.getEmails()) {
            if (element.getEmail().equals("public_0000-0000-0000-0003@test.orcid.org")) {
                found1 = true;
            } else if (element.getEmail().equals("limited_0000-0000-0000-0003@test.orcid.org")) {
                found2 = true;
            } else if (element.getEmail().equals("private_0000-0000-0000-0003@test.orcid.org")) {
                found3 = true;
            } else if (element.getEmail().equals("self_limited_0000-0000-0000-0003@test.orcid.org")) {
                found4 = true;
            } else {
                fail("Invalid email " + element.getEmail());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);

        // External identifiers
        assertNotNull(p.getExternalIdentifiers());
        PersonExternalIdentifiers extIds = p.getExternalIdentifiers();
        assertNotNull(extIds);
        Utils.verifyLastModified(extIds.getLastModifiedDate());
        assertEquals(4, extIds.getExternalIdentifiers().size());
        found1 = false;
        found2 = false;
        found3 = false;
        found4 = false;

        for (PersonExternalIdentifier element : extIds.getExternalIdentifiers()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            if (element.getPutCode() == 13) {
                found1 = true;
            } else if (element.getPutCode() == 14) {
                found2 = true;
            } else if (element.getPutCode() == 15) {
                found3 = true;
            } else if (element.getPutCode() == 16) {
                found4 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);

        // Keywords
        assertNotNull(p.getKeywords());
        Keywords k = p.getKeywords();
        assertNotNull(k);
        Utils.verifyLastModified(k.getLastModifiedDate());
        assertEquals(4, k.getKeywords().size());
        found1 = false;
        found2 = false;
        found3 = false;
        found4 = false;
        for (Keyword element : k.getKeywords()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            if (element.getPutCode() == 9) {
                found1 = true;
            } else if (element.getPutCode() == 10) {
                found2 = true;
            } else if (element.getPutCode() == 11) {
                found3 = true;
            } else if (element.getPutCode() == 12) {
                found4 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);

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
        assertEquals(4, o.getOtherNames().size());
        found1 = false;
        found2 = false;
        found3 = false;
        found4 = false;
        for (OtherName element : o.getOtherNames()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            if (element.getPutCode() == 13) {
                found1 = true;
            } else if (element.getPutCode() == 14) {
                found2 = true;
            } else if (element.getPutCode() == 15) {
                found3 = true;
            } else if (element.getPutCode() == 16) {
                found4 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);

        // Researcher urls
        assertNotNull(p.getResearcherUrls());
        ResearcherUrls ru = p.getResearcherUrls();
        assertNotNull(ru);
        Utils.verifyLastModified(ru.getLastModifiedDate());
        assertEquals(4, ru.getResearcherUrls().size());
        found1 = false;
        found2 = false;
        found3 = false;
        found4 = false;
        for (ResearcherUrl element : ru.getResearcherUrls()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            if (element.getPutCode() == 13) {
                found1 = true;
            } else if (element.getPutCode() == 14) {
                found2 = true;
            } else if (element.getPutCode() == 15) {
                found3 = true;
            } else if (element.getPutCode() == 16) {
                found4 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);

        assertNotNull(p.getPath());
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
    public void testReadPrivateEmails_OtherThingsJustPublic_Person() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, "APP-5555555555555556", ScopePathType.EMAIL_READ_PRIVATE);
        Response response = serviceDelegator.viewPerson(ORCID);
        assertNotNull(response);
        assertEquals(Person.class.getName(), response.getEntity().getClass().getName());
        Person p = (Person) response.getEntity();
        assertEquals("/0000-0000-0000-0003/person", p.getPath());
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
