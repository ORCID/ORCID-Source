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
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.Address;
import org.orcid.jaxb.model.v3.rc2.record.Distinction;
import org.orcid.jaxb.model.v3.rc2.record.Education;
import org.orcid.jaxb.model.v3.rc2.record.Employment;
import org.orcid.jaxb.model.v3.rc2.record.Funding;
import org.orcid.jaxb.model.v3.rc2.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc2.record.Keyword;
import org.orcid.jaxb.model.v3.rc2.record.Keywords;
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
public class MemberV3ApiServiceDelegator_KeywordsTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml",
            "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV3ApiServiceDelegatorV3_0")
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
    public void testViewKeywordsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewKeywords(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewKeywordWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewKeyword(ORCID, 9L);
    }

    @Test
    public void testViewKeywordReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewKeyword(ORCID, 9L);
        Keyword element = (Keyword) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/keywords/9", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewKeywordsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewKeywords(ORCID);
        Keywords element = (Keywords) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/keywords", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewKeywords() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewKeywords("4444-4444-4444-4443");
        assertNotNull(response);
        Keywords keywords = (Keywords) response.getEntity();
        assertNotNull(keywords);
        assertEquals("/4444-4444-4444-4443/keywords", keywords.getPath());
        Utils.verifyLastModified(keywords.getLastModifiedDate());
        assertNotNull(keywords.getKeywords());
        assertEquals(3, keywords.getKeywords().size());

        for (Keyword keyword : keywords.getKeywords()) {
            Utils.verifyLastModified(keyword.getLastModifiedDate());
            assertThat(keyword.getPutCode(), anyOf(is(1L), is(2L), is(4L)));
            assertThat(keyword.getContent(), anyOf(is("tea making"), is("coffee making"), is("what else can we make?")));
            if (keyword.getPutCode() == 1L) {
                assertEquals(Visibility.PUBLIC, keyword.getVisibility());
                assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
            } else if (keyword.getPutCode() == 2L) {
                assertEquals(Visibility.LIMITED, keyword.getVisibility());
                assertEquals("4444-4444-4444-4443", keyword.getSource().retrieveSourcePath());
            } else {
                assertEquals(Visibility.PRIVATE, keyword.getVisibility());
                assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
            }
        }
    }

    @Test
    public void testViewPublicKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4443", 1L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("/4444-4444-4444-4443/keywords/1", keyword.getPath());
        Utils.verifyLastModified(keyword.getLastModifiedDate());
        assertEquals("tea making", keyword.getContent());
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());
        assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewLimitedKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4443", 2L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("/4444-4444-4444-4443/keywords/2", keyword.getPath());
        Utils.verifyLastModified(keyword.getLastModifiedDate());
        assertEquals("coffee making", keyword.getContent());
        assertEquals(Visibility.LIMITED, keyword.getVisibility());
        assertEquals("4444-4444-4444-4443", keyword.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewPrivateKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4443", 4L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("/4444-4444-4444-4443/keywords/4", keyword.getPath());
        Utils.verifyLastModified(keyword.getLastModifiedDate());
        assertEquals("what else can we make?", keyword.getContent());
        assertEquals(Visibility.PRIVATE, keyword.getVisibility());
        assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateKeywordWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewKeyword("4444-4444-4444-4443", 3L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewKeywordThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewOtherName("4444-4444-4444-4443", 5L);
        fail();
    }

    @Test
    public void testAddKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.createKeyword("4444-4444-4444-4441", Utils.getKeyword());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewKeyword("4444-4444-4444-4441", putCode);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Keyword newKeyword = (Keyword) response.getEntity();
        assertNotNull(newKeyword);
        Utils.verifyLastModified(newKeyword.getLastModifiedDate());
        assertEquals("New keyword", newKeyword.getContent());
        assertEquals(Visibility.PUBLIC, newKeyword.getVisibility());
        assertNotNull(newKeyword.getSource());
        assertEquals("APP-5555555555555555", newKeyword.getSource().retrieveSourcePath());
        assertNotNull(newKeyword.getCreatedDate());
        Utils.verifyLastModified(newKeyword.getLastModifiedDate());
    }

    @Test
    public void testUpdateKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4441", 6L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        Utils.verifyLastModified(keyword.getLastModifiedDate());
        LastModifiedDate before = keyword.getLastModifiedDate();
        assertEquals("keyword-2", keyword.getContent());
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());

        keyword.setContent("Updated keyword");

        response = serviceDelegator.updateKeyword("4444-4444-4444-4441", 6L, keyword);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewKeyword("4444-4444-4444-4441", 6L);
        assertNotNull(response);
        keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        Utils.verifyLastModified(keyword.getLastModifiedDate());
        assertTrue(keyword.getLastModifiedDate().after(before));
        assertEquals("Updated keyword", keyword.getContent());
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateKeywordYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4443", 2L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("coffee making", keyword.getContent());
        assertEquals(Visibility.LIMITED, keyword.getVisibility());
        assertNotNull(keyword.getSource());
        assertEquals("4444-4444-4444-4443", keyword.getSource().retrieveSourcePath());

        keyword.setContent("Updated Keyword " + System.currentTimeMillis());

        serviceDelegator.updateKeyword("4444-4444-4444-4443", 2L, keyword);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateKeywordChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4441", 6L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());

        keyword.setVisibility(Visibility.PRIVATE);

        response = serviceDelegator.updateKeyword("4444-4444-4444-4441", 6L, keyword);
        fail();
    }

    @Test
    public void testUpdateKeywordLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4441", 6L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());

        keyword.setVisibility(null);

        response = serviceDelegator.updateKeyword("4444-4444-4444-4441", 6L, keyword);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());
    }

    @Test
    public void testDeleteKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4499", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewKeywords("4444-4444-4444-4499");
        assertNotNull(response);
        Keywords keywords = (Keywords) response.getEntity();
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());
        assertEquals(1, keywords.getKeywords().size());
        response = serviceDelegator.deleteKeyword("4444-4444-4444-4499", 8L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        response = serviceDelegator.viewKeywords("4444-4444-4444-4499");
        assertNotNull(response);
        keywords = (Keywords) response.getEntity();
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());
        assertTrue(keywords.getKeywords().isEmpty());
    }

    @Test
    public void testReadPublicScope_Keywords() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        // Public works
        Response r = serviceDelegator.viewKeywords(ORCID);
        assertNotNull(r);
        assertEquals(Keywords.class.getName(), r.getEntity().getClass().getName());
        Keywords k = (Keywords) r.getEntity();
        assertNotNull(k);
        Utils.verifyLastModified(k.getLastModifiedDate());
        assertEquals(3, k.getKeywords().size());
        boolean found1 = false, found2 = false, found3 = false;
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

        r = serviceDelegator.viewKeyword(ORCID, 9L);
        assertNotNull(r);
        assertEquals(Keyword.class.getName(), r.getEntity().getClass().getName());

        // Limited where am the source of should work
        serviceDelegator.viewKeyword(ORCID, 10L);
        // Limited where am not the source of should fail
        try {
            serviceDelegator.viewKeyword(ORCID, 12L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private where am the source of should work
        serviceDelegator.viewKeyword(ORCID, 11L);
        // Private where am not the source of should fail
        try {
            serviceDelegator.viewKeyword(ORCID, 13L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteKeywordYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_UPDATE);
        serviceDelegator.deleteKeyword("4444-4444-4444-4443", 3L);
        fail();
    }
}
