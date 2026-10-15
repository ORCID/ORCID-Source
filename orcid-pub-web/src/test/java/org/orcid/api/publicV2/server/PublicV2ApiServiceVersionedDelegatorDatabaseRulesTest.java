package org.orcid.api.publicV2.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.orcid.api.publicV2.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.test.DBUnitTest;
import org.orcid.test.DatabaseTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;

/**
 * The rules of the versioned public v2 delegators that only the real beans can prove.
 *
 * <p>
 * {@code PublicV2ApiServiceVersionedDelegatorTest} runs on mocks, with the version
 * converter chain stubbed as a pass-through, so it cannot see what the real 2.0 to 2.1
 * conversion does. The converter copies a property only when it has a bean read method,
 * which is how the email flags were once dropped on /v2.1 while every test of the
 * non-versioned bean stayed green. These run through both versioned beans, against
 * fixtures.
 */
@Category(DatabaseTest.class)
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-t1-web-context.xml" })
public class PublicV2ApiServiceVersionedDelegatorDatabaseRulesTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml",
            "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml", "/data/BiographyEntityData.xml",
            "/data/RecordNameEntityData.xml");

    private static final String USER_WITH_PUBLIC_EMAILS = "0000-0000-0000-0003";

    @Resource(name = "publicV2ApiServiceDelegatorV2")
    private PublicV2ApiServiceDelegator<?, ?, ?, ?, ?, ?, ?, ?, ?> serviceDelegator;

    @Resource(name = "publicV2ApiServiceDelegatorV2_1")
    private PublicV2ApiServiceDelegator<?, ?, ?, ?, ?, ?, ?, ?, ?> serviceDelegatorV2_1;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        ArrayList<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("anonymous", "anonymous", roles));
    }

    @After
    public void after() {
        SecurityContextHolder.clearContext();
    }

    /**
     * The versioned beans serve /v2.0 and /v2.1, and both run every response through the
     * 2.0 <-> 2.1 converter, which can only copy a property that has a bean read method.
     * PublicV2ApiServiceDelegatorTest.testViewEmails asserts the same values on the
     * non-versioned bean, which never converts, so it passes even when the flags are dropped.
     */
    @Test
    public void testViewEmailsKeepsVerifiedAndPrimaryOnV2_0AndV2_1() {
        assertEmailFlags(((Emails) serviceDelegator.viewEmails(USER_WITH_PUBLIC_EMAILS).getEntity()).getEmails());
        assertEmailFlags(((Emails) serviceDelegatorV2_1.viewEmails(USER_WITH_PUBLIC_EMAILS).getEntity()).getEmails());
    }

    @Test
    public void testViewPersonAndRecordKeepVerifiedAndPrimaryOnV2_1() {
        Person person = (Person) serviceDelegatorV2_1.viewPerson(USER_WITH_PUBLIC_EMAILS).getEntity();
        assertEmailFlags(person.getEmails().getEmails());

        Record record = (Record) serviceDelegatorV2_1.viewRecord(USER_WITH_PUBLIC_EMAILS).getEntity();
        assertEmailFlags(record.getPerson().getEmails().getEmails());
    }

    /**
     * The contributor email is write-only. The versioned beans run every work through the
     * 2.0 <-> 2.1 converter, which copies every bean property, so check it stays out on both.
     */
    @Test
    public void testViewWorkOmitsContributorEmailOnV2_0AndV2_1() {
        assertNoContributorEmail((Work) serviceDelegator.viewWork(USER_WITH_PUBLIC_EMAILS, 11L).getEntity());
        assertNoContributorEmail((Work) serviceDelegatorV2_1.viewWork(USER_WITH_PUBLIC_EMAILS, 11L).getEntity());
        assertNoContributorEmail((Work) ((WorkBulk) serviceDelegator.viewBulkWorks(USER_WITH_PUBLIC_EMAILS, "11").getEntity()).getBulk().get(0));
        assertNoContributorEmail((Work) ((WorkBulk) serviceDelegatorV2_1.viewBulkWorks(USER_WITH_PUBLIC_EMAILS, "11").getEntity()).getBulk().get(0));
    }

    private void assertNoContributorEmail(Work work) {
        assertEquals(Long.valueOf(11), work.getPutCode());
        assertEquals(1, work.getWorkContributors().getContributor().size());
        assertEquals("0000-0000-0000-0000", work.getWorkContributors().getContributor().get(0).getContributorOrcid().getPath());
        assertNull(work.getWorkContributors().getContributor().get(0).getContributorEmail());
    }

    /**
     * Compares values rather than asserting true: a dropped flag is null, and false has to stay
     * false rather than collapse to null.
     */
    private void assertEmailFlags(List<Email> emails) {
        assertEquals(2, emails.size());
        Email primary = emails.get(0);
        Email secondary = emails.get(1);
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", primary.getEmail());
        assertEquals(Boolean.TRUE, primary.isVerified());
        assertEquals(Boolean.TRUE, primary.isPrimary());
        assertEquals("public_0000-0000-0000-0003@orcid.org", secondary.getEmail());
        assertEquals(Boolean.TRUE, secondary.isVerified());
        assertEquals(Boolean.FALSE, secondary.isPrimary());
    }
}
