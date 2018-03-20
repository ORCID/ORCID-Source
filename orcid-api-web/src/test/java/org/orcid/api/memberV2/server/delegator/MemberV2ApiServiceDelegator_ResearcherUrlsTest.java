package org.orcid.api.memberV2.server.delegator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import org.orcid.jaxb.model.common_v2.LastModifiedDate;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV2ApiServiceDelegator_ResearcherUrlsTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml",
            "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV2ApiServiceDelegator")
    protected MemberV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, WorkBulk, Address, Keyword> serviceDelegator;

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
    public void testViewResearcherUrlWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewResearcherUrl(ORCID, 13L);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewResearcherUrlsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewResearcherUrls(ORCID);
    }

    @Test
    public void testViewResearcherUrlReadPublic() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewResearcherUrl(ORCID, 13L);
        ResearcherUrl element = (ResearcherUrl) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/researcher-urls/13", element.getPath());
        Utils.assertIsPublicOrSource(element, SecurityContextTestUtils.DEFAULT_CLIENT_ID);
    }

    @Test
    public void testViewResearcherUrlsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewResearcherUrls(ORCID);
        ResearcherUrls elements = (ResearcherUrls) r.getEntity();
        assertNotNull(elements);
        assertEquals("/0000-0000-0000-0003/researcher-urls", elements.getPath());
        for (ResearcherUrl element : elements.getResearcherUrls()) {
            if (!element.retrieveSourcePath().equals("APP-5555555555555555") && !Visibility.PUBLIC.equals(element.getVisibility())) {
                fail("Element " + element.getPutCode() + " is not source of APP-5555555555555555 and is not public");
            }
        }
    }

    @Test
    public void testViewResearcherUrls() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrls("4444-4444-4444-4443");
        assertNotNull(response);
        ResearcherUrls researcherUrls = (ResearcherUrls) response.getEntity();
        assertNotNull(researcherUrls);        
        Utils.verifyLastModified(researcherUrls.getLastModifiedDate());
        assertEquals("/4444-4444-4444-4443/researcher-urls", researcherUrls.getPath());
        assertNotNull(researcherUrls.getResearcherUrls());
        assertEquals(5, researcherUrls.getResearcherUrls().size());
        for (ResearcherUrl rUrl : researcherUrls.getResearcherUrls()) {
            assertThat(rUrl.getPutCode(),
                    anyOf(equalTo(Long.valueOf(2)), equalTo(Long.valueOf(3)), equalTo(Long.valueOf(5)), equalTo(Long.valueOf(7)), equalTo(Long.valueOf(8))));
            Utils.verifyLastModified(researcherUrls.getLastModifiedDate());
            assertNotNull(rUrl.getSource());
            assertFalse(PojoUtil.isEmpty(rUrl.getSource().retrieveSourcePath()));
            assertNotNull(rUrl.getUrl());
            assertNotNull(rUrl.getUrlName());
            assertNotNull(rUrl.getVisibility());
            if (rUrl.getPutCode().equals(Long.valueOf(5)) || rUrl.getPutCode().equals(Long.valueOf(7))) {
                assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
            }
        }
    }

    @Test
    public void testViewPublicResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 2L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("/4444-4444-4444-4443/researcher-urls/2", researcherUrl.getPath());
        Utils.verifyLastModified(researcherUrl.getLastModifiedDate());
        assertEquals("4444-4444-4444-4443", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.researcherurl2.com?id=1", researcherUrl.getUrl().getValue());
        assertEquals("443_1", researcherUrl.getUrlName());
        assertEquals(Visibility.PUBLIC, researcherUrl.getVisibility());
    }

    @Test
    public void testViewLimitedResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 8L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("/4444-4444-4444-4443/researcher-urls/8", researcherUrl.getPath());
        Utils.verifyLastModified(researcherUrl.getLastModifiedDate());
        assertEquals("4444-4444-4444-4443", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.researcherurl2.com?id=8", researcherUrl.getUrl().getValue());
        assertEquals("443_6", researcherUrl.getUrlName());
        assertEquals(Visibility.LIMITED, researcherUrl.getVisibility());
    }

    @Test
    public void testViewPrivateResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 7L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("/4444-4444-4444-4443/researcher-urls/7", researcherUrl.getPath());
        Utils.verifyLastModified(researcherUrl.getLastModifiedDate());
        assertEquals("APP-5555555555555555", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.researcherurl2.com?id=7", researcherUrl.getUrl().getValue());
        assertEquals("443_5", researcherUrl.getUrlName());
        assertEquals(Visibility.PRIVATE, researcherUrl.getVisibility());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateResearcherUrlWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 6L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewResearcherUrlThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 1L);
        fail();
    }

    @Test
    public void testAddResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.createResearcherUrl("4444-4444-4444-4441", Utils.getResearcherUrl());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4441", putCode);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        Utils.verifyLastModified(researcherUrl.getLastModifiedDate());
        assertEquals("APP-5555555555555555", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.myRUrl.com", researcherUrl.getUrl().getValue());
        assertEquals("My researcher Url", researcherUrl.getUrlName());
        assertEquals(Visibility.PUBLIC, researcherUrl.getVisibility());
    }

    @Test
    public void testUpdateResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 5L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        Utils.verifyLastModified(researcherUrl.getLastModifiedDate());
        LastModifiedDate before = researcherUrl.getLastModifiedDate();
        assertNotNull(researcherUrl.getUrl());
        assertEquals("http://www.researcherurl2.com?id=5", researcherUrl.getUrl().getValue());
        assertEquals("443_3", researcherUrl.getUrlName());

        researcherUrl.setUrl(new Url("http://theNewResearcherUrl.com"));
        researcherUrl.setUrlName("My Updated Researcher Url");

        response = serviceDelegator.updateResearcherUrl("4444-4444-4444-4443", 5L, researcherUrl);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 5L);
        assertNotNull(response);
        researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        Utils.verifyLastModified(researcherUrl.getLastModifiedDate());
        assertTrue(researcherUrl.getLastModifiedDate().after(before));
        assertNotNull(researcherUrl.getUrl());
        assertEquals("http://theNewResearcherUrl.com", researcherUrl.getUrl().getValue());
        assertEquals("My Updated Researcher Url", researcherUrl.getUrlName());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateResearcherUrlYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_UPDATE, ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 8L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertNotNull(researcherUrl.getUrl());
        assertEquals("http://www.researcherurl2.com?id=8", researcherUrl.getUrl().getValue());
        assertEquals("443_6", researcherUrl.getUrlName());

        researcherUrl.setUrlName("Updated Name");
        serviceDelegator.updateResearcherUrl("4444-4444-4444-4443", 8L, researcherUrl);
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateResearcherUrlChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 5L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertEquals(Visibility.LIMITED, researcherUrl.getVisibility());

        researcherUrl.setVisibility(Visibility.PRIVATE);

        response = serviceDelegator.updateResearcherUrl("4444-4444-4444-4443", 5L, researcherUrl);
        fail();
    }

    @Test
    public void testUpdateResearcherUrlLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 5L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertEquals(Visibility.LIMITED, researcherUrl.getVisibility());

        researcherUrl.setVisibility(null);

        response = serviceDelegator.updateResearcherUrl("4444-4444-4444-4443", 5L, researcherUrl);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        researcherUrl = (ResearcherUrl) response.getEntity();
        assertEquals(Visibility.LIMITED, researcherUrl.getVisibility());
    }

    @Test
    public void testDeleteResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4445", ScopePathType.PERSON_UPDATE, ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrls("4444-4444-4444-4445");
        assertNotNull(response);
        ResearcherUrls researcherUrls = (ResearcherUrls) response.getEntity();
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getResearcherUrls());
        assertFalse(researcherUrls.getResearcherUrls().isEmpty());
        ResearcherUrl toDelete = null;

        for (ResearcherUrl rurl : researcherUrls.getResearcherUrls()) {
            if (rurl.getSource().retrieveSourcePath().equals("APP-5555555555555555")) {
                toDelete = rurl;
                break;
            }
        }

        assertNotNull(toDelete);

        response = serviceDelegator.deleteResearcherUrl("4444-4444-4444-4445", toDelete.getPutCode());
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewResearcherUrls("4444-4444-4444-4445");
        assertNotNull(response);
        researcherUrls = (ResearcherUrls) response.getEntity();
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getResearcherUrls());
        assertEquals(0, researcherUrls.getResearcherUrls().size());
    }

    @Test
    public void testReadPublicScope_ResearcherUrls() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        // Public works
        Response r = serviceDelegator.viewResearcherUrls(ORCID);
        assertNotNull(r);
        ResearcherUrls ru = (ResearcherUrls) r.getEntity();
        assertNotNull(ru);
        assertEquals("/0000-0000-0000-0003/researcher-urls", ru.getPath());
        Utils.verifyLastModified(ru.getLastModifiedDate());
        assertEquals(3, ru.getResearcherUrls().size());
        boolean found13 = false, found14 = false, found15 = false;
        for (ResearcherUrl element : ru.getResearcherUrls()) {
            if (element.getPutCode() == 13) {
                found13 = true;
            } else if (element.getPutCode() == 14) {
                found14 = true;
            } else if (element.getPutCode() == 15) {
                found15 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found13);
        assertTrue(found14);
        assertTrue(found15);

        r = serviceDelegator.viewResearcherUrl(ORCID, 13L);
        assertNotNull(r);
        assertEquals(ResearcherUrl.class.getName(), r.getEntity().getClass().getName());
        // Limited am the source of should work
        serviceDelegator.viewResearcherUrl(ORCID, 14L);
        // Limited am not the source of should fail
        try {
            serviceDelegator.viewResearcherUrl(ORCID, 16L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private am the source of should work
        serviceDelegator.viewResearcherUrl(ORCID, 15L);
        // Private am not the source of should fail
        try {
            serviceDelegator.viewResearcherUrl(ORCID, 17L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteResearcherUrlYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        serviceDelegator.deleteResearcherUrl("4444-4444-4444-4443", 8L);
        fail();
    }
}
