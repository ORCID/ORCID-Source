package org.orcid.api.memberV3.server.delegator;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
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
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.Address;
import org.orcid.jaxb.model.v3.rc2.record.Distinction;
import org.orcid.jaxb.model.v3.rc2.record.Education;
import org.orcid.jaxb.model.v3.rc2.record.Email;
import org.orcid.jaxb.model.v3.rc2.record.Emails;
import org.orcid.jaxb.model.v3.rc2.record.Employment;
import org.orcid.jaxb.model.v3.rc2.record.Funding;
import org.orcid.jaxb.model.v3.rc2.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc2.record.Keyword;
import org.orcid.jaxb.model.v3.rc2.record.Membership;
import org.orcid.jaxb.model.v3.rc2.record.OtherName;
import org.orcid.jaxb.model.v3.rc2.record.PeerReview;
import org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc2.record.Qualification;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc2.record.Service;
import org.orcid.jaxb.model.v3.rc2.record.Work;
import org.orcid.jaxb.model.v3.rc2.record.WorkBulk;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.v3.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV3ApiServiceDelegator_EmailsTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml",
            "/data/BiographyEntityData.xml");

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
    public void testViewEmailsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmails(ORCID);
    }

    @Test
    public void testViewEmailsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEmails(ORCID);
        Emails element = (Emails) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/email", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testReadPublicScope_Emails() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewEmails(ORCID);
        assertNotNull(r);
        assertEquals(Emails.class.getName(), r.getEntity().getClass().getName());
        Emails email = (Emails) r.getEntity();
        assertNotNull(email);
        assertEquals("/0000-0000-0000-0003/email", email.getPath());
        Utils.verifyLastModified(email.getLastModifiedDate());
        assertEquals(3, email.getEmails().size());
        boolean found1 = false;
        boolean found2 = false;
        boolean found3 = false;
        for (Email element : email.getEmails()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            if (element.getEmail().equals("public_0000-0000-0000-0003@test.orcid.org")) {
                found1 = true;
            } else if (element.getEmail().equals("limited_0000-0000-0000-0003@test.orcid.org")) {
                found2 = true;
            } else if (element.getEmail().equals("private_0000-0000-0000-0003@test.orcid.org")) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
    }

    @Test
    public void testReadEmailPrivate() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4497", ScopePathType.EMAIL_READ_PRIVATE);
        Response r = serviceDelegator.viewEmails("4444-4444-4444-4497");
        assertNotNull(r);
        assertEquals(Emails.class.getName(), r.getEntity().getClass().getName());
        Emails email = (Emails) r.getEntity();
        assertNotNull(email);
        assertEquals("/4444-4444-4444-4497/email", email.getPath());
        assertNotNull(email.getLastModifiedDate());
        assertEquals(3, email.getEmails().size());
        assertEquals("public_4444-4444-4444-4497@test.orcid.org", email.getEmails().get(0).getEmail());
        assertEquals(Visibility.PUBLIC, email.getEmails().get(0).getVisibility());
        assertEquals("limited_4444-4444-4444-4497@test.orcid.org", email.getEmails().get(1).getEmail());
        assertEquals(Visibility.LIMITED, email.getEmails().get(1).getVisibility());
        assertEquals("private_4444-4444-4444-4497@test.orcid.org", email.getEmails().get(2).getEmail());
        assertEquals(Visibility.PRIVATE, email.getEmails().get(2).getVisibility());
    }

    @Test
    public void testViewEmails() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewEmails("4444-4444-4444-4443");
        assertNotNull(response);
        Emails emails = (Emails) response.getEntity();
        assertNotNull(emails);
        assertEquals("/4444-4444-4444-4443/email", emails.getPath());
        Utils.verifyLastModified(emails.getLastModifiedDate());
        assertNotNull(emails.getEmails());
        assertEquals(3, emails.getEmails().size());
        for (Email email : emails.getEmails()) {
            Utils.verifyLastModified(email.getLastModifiedDate());
            assertThat(email.getEmail(), anyOf(is("teddybass2@semantico.com"), is("teddybass3public@semantico.com"), is("teddybass3private@semantico.com")));
            switch (email.getEmail()) {
            case "teddybass2@semantico.com":
                assertEquals(Visibility.LIMITED, email.getVisibility());
                assertEquals("4444-4444-4444-4443", email.retrieveSourcePath());
                assertEquals(false, email.isVerified());
                assertEquals(false, email.isPrimary());
                break;
            case "teddybass3public@semantico.com":
                assertEquals(Visibility.PUBLIC, email.getVisibility());
                assertEquals("4444-4444-4444-4443", email.retrieveSourcePath());
                assertEquals(false, email.isVerified());
                assertEquals(false, email.isPrimary());
                break;
            case "teddybass3private@semantico.com":
                assertEquals(Visibility.PRIVATE, email.getVisibility());
                assertEquals("APP-5555555555555555", email.retrieveSourcePath());
                assertEquals(true, email.isVerified());
                assertEquals(true, email.isPrimary());
                break;
            }
        }
    }
}
