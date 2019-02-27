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
import org.orcid.jaxb.model.v3.rc2.common.Url;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.Address;
import org.orcid.jaxb.model.v3.rc2.record.Distinction;
import org.orcid.jaxb.model.v3.rc2.record.Education;
import org.orcid.jaxb.model.v3.rc2.record.Employment;
import org.orcid.jaxb.model.v3.rc2.record.Funding;
import org.orcid.jaxb.model.v3.rc2.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc2.record.Keyword;
import org.orcid.jaxb.model.v3.rc2.record.Membership;
import org.orcid.jaxb.model.v3.rc2.record.OtherName;
import org.orcid.jaxb.model.v3.rc2.record.PeerReview;
import org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifiers;
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
public class MemberV3ApiServiceDelegator_ExternalIdentifiersTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

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
    public void testViewExternalIdentifiersWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewExternalIdentifiers(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewExternalIdentifierWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewExternalIdentifier(ORCID, 13L);
    }

    @Test
    public void testViewExternalIdentifierReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewExternalIdentifier(ORCID, 13L);
        PersonExternalIdentifier element = (PersonExternalIdentifier) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/external-identifiers/13", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewExternalIdentifiersReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewExternalIdentifiers(ORCID);
        PersonExternalIdentifiers element = (PersonExternalIdentifiers) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/external-identifiers", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewExternalIdentifiers() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewExternalIdentifiers("4444-4444-4444-4442");
        assertNotNull(response);
        PersonExternalIdentifiers extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertEquals("/4444-4444-4444-4442/external-identifiers", extIds.getPath());
        Utils.verifyLastModified(extIds.getLastModifiedDate());
        List<PersonExternalIdentifier> extIdsList = extIds.getExternalIdentifiers();
        assertNotNull(extIdsList);
        assertEquals(3, extIdsList.size());

        for (PersonExternalIdentifier extId : extIdsList) {
            Utils.verifyLastModified(extId.getLastModifiedDate());
            assertThat(extId.getPutCode(), anyOf(is(2L), is(3L), is(5L)));
            assertThat(extId.getValue(), anyOf(is("abc123"), is("abc456"), is("abc012")));
            assertNotNull(extId.getUrl());
            assertThat(extId.getUrl().getValue(),
                    anyOf(is("http://www.facebook.com/abc123"), is("http://www.facebook.com/abc456"), is("http://www.facebook.com/abc012")));
            assertEquals("Facebook", extId.getType());
            assertNotNull(extId.getSource());
            if (extId.getPutCode().equals(2L)) {
                assertEquals(Visibility.PUBLIC, extId.getVisibility());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
            } else if (extId.getPutCode().equals(3L)) {
                assertEquals(Visibility.LIMITED, extId.getVisibility());
                assertEquals("4444-4444-4444-4442", extId.getSource().retrieveSourcePath());
            } else {
                assertEquals(Visibility.PRIVATE, extId.getVisibility());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
            }
        }
    }

    @Test
    public void testViewPublicExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 2L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("/4444-4444-4444-4442/external-identifiers/2", extId.getPath());
        Utils.verifyLastModified(extId.getLastModifiedDate());
        assertEquals("Facebook", extId.getType());
        assertEquals(Long.valueOf(2), extId.getPutCode());
        assertEquals("abc123", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc123", extId.getUrl().getValue());
        assertEquals(Visibility.PUBLIC, extId.getVisibility());
        assertNotNull(extId.getSource());
        assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
        assertNotNull(extId.getCreatedDate());
        Utils.verifyLastModified(extId.getLastModifiedDate());
    }

    @Test
    public void testViewLimitedExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 3L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("/4444-4444-4444-4442/external-identifiers/3", extId.getPath());
        Utils.verifyLastModified(extId.getLastModifiedDate());
        assertEquals("Facebook", extId.getType());
        assertEquals(Long.valueOf(3), extId.getPutCode());
        assertEquals("abc456", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc456", extId.getUrl().getValue());
        assertEquals(Visibility.LIMITED, extId.getVisibility());
        assertNotNull(extId.getSource());
        assertEquals("4444-4444-4444-4442", extId.getSource().retrieveSourcePath());
        assertNotNull(extId.getCreatedDate());
        Utils.verifyLastModified(extId.getLastModifiedDate());
    }

    @Test
    public void testViewPrivateExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 5L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("/4444-4444-4444-4442/external-identifiers/5", extId.getPath());
        Utils.verifyLastModified(extId.getLastModifiedDate());
        assertEquals("Facebook", extId.getType());
        assertEquals(Long.valueOf(5), extId.getPutCode());
        assertEquals("abc012", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc012", extId.getUrl().getValue());
        assertEquals(Visibility.PRIVATE, extId.getVisibility());
        assertNotNull(extId.getSource());
        assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
        assertNotNull(extId.getCreatedDate());
        Utils.verifyLastModified(extId.getLastModifiedDate());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateExternalIdentifierWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 4L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewExternalIdentifierThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 1L);
        fail();
    }

    @Test
    public void testAddExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifiers("4444-4444-4444-4443");
        assertNotNull(response);
        PersonExternalIdentifiers extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertNotNull(extIds.getExternalIdentifiers());
        assertEquals(1, extIds.getExternalIdentifiers().size());
        assertEquals(Long.valueOf(1), extIds.getExternalIdentifiers().get(0).getPutCode());
        assertNotNull(extIds.getExternalIdentifiers().get(0).getUrl());
        assertEquals("http://www.facebook.com/d3clan", extIds.getExternalIdentifiers().get(0).getUrl().getValue());
        assertEquals("d3clan", extIds.getExternalIdentifiers().get(0).getValue());
        assertEquals(Visibility.PUBLIC, extIds.getExternalIdentifiers().get(0).getVisibility());

        response = serviceDelegator.createExternalIdentifier("4444-4444-4444-4443", Utils.getPersonExternalIdentifier());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewExternalIdentifiers("4444-4444-4444-4443");
        assertNotNull(response);
        extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        Utils.verifyLastModified(extIds.getLastModifiedDate());
        assertNotNull(extIds.getExternalIdentifiers());
        assertEquals(2, extIds.getExternalIdentifiers().size());

        for (PersonExternalIdentifier extId : extIds.getExternalIdentifiers()) {
            Utils.verifyLastModified(extId.getLastModifiedDate());
            assertNotNull(extId.getUrl());
            if (extId.getPutCode() != 1L) {
                assertEquals(Visibility.PUBLIC, extId.getVisibility());
                assertEquals("new-common-name", extId.getType());
                assertEquals("new-reference", extId.getValue());
                assertEquals("http://newUrl.com", extId.getUrl().getValue());
                assertEquals(putCode, extId.getPutCode());
            } else {
                assertEquals(Visibility.PUBLIC, extId.getVisibility());
                assertEquals("Facebook", extId.getType());
                assertEquals("d3clan", extId.getValue());
                assertEquals("http://www.facebook.com/d3clan", extId.getUrl().getValue());
            }
        }
    }

    @Test
    public void testUpdateExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 2L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        Utils.verifyLastModified(extId.getLastModifiedDate());
        LastModifiedDate before = extId.getLastModifiedDate();
        assertEquals("Facebook", extId.getType());
        assertEquals("abc123", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc123", extId.getUrl().getValue());
        extId.setType("updated-common-name");
        extId.setValue("updated-reference");
        extId.setUrl(new Url("http://updatedUrl.com"));
        response = serviceDelegator.updateExternalIdentifier("4444-4444-4444-4442", 2L, extId);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 2L);
        assertNotNull(response);
        PersonExternalIdentifier updatedExtId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(updatedExtId);
        Utils.verifyLastModified(updatedExtId.getLastModifiedDate());
        assertTrue(updatedExtId.getLastModifiedDate().after(before));
        assertEquals("updated-common-name", updatedExtId.getType());
        assertEquals("updated-reference", updatedExtId.getValue());
        assertNotNull(updatedExtId.getUrl());
        assertEquals("http://updatedUrl.com", updatedExtId.getUrl().getValue());
        // Revert changes so other tests still works
        extId.setType("Facebook");
        extId.setValue("abc123");
        extId.setUrl(new Url("http://www.facebook.com/abc123"));
        response = serviceDelegator.updateExternalIdentifier("4444-4444-4444-4442", 2L, extId);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateExaternalIdentifierYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 3L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("Facebook", extId.getType());
        assertEquals("abc456", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc456", extId.getUrl().getValue());
        extId.setType("other-common-name");
        extId.setValue("other-reference");
        extId.setUrl(new Url("http://otherUrl.com"));
        serviceDelegator.updateExternalIdentifier("4444-4444-4444-4442", 3L, extId);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateExternalIdentifierChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 2L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals(Visibility.PUBLIC, extId.getVisibility());

        extId.setVisibility(Visibility.PRIVATE);

        response = serviceDelegator.updateExternalIdentifier("4444-4444-4444-4442", 2L, extId);
        fail();
    }

    @Test
    public void testUpdateExternalIdentifierLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 2L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals(Visibility.PUBLIC, extId.getVisibility());

        extId.setVisibility(null);

        response = serviceDelegator.updateExternalIdentifier("4444-4444-4444-4442", 2L, extId);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals(Visibility.PUBLIC, extId.getVisibility());
    }

    @Test
    public void testDeleteExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4444", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifiers("4444-4444-4444-4444");
        assertNotNull(response);
        PersonExternalIdentifiers extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertNotNull(extIds.getExternalIdentifiers());
        assertEquals(1, extIds.getExternalIdentifiers().size());
        assertEquals(Long.valueOf(6), extIds.getExternalIdentifiers().get(0).getPutCode());

        response = serviceDelegator.deleteExternalIdentifier("4444-4444-4444-4444", 6L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewExternalIdentifiers("4444-4444-4444-4444");
        assertNotNull(response);
        extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertNotNull(extIds.getExternalIdentifiers());
        assertTrue(extIds.getExternalIdentifiers().isEmpty());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteExternalIdentifierYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 3L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("Facebook", extId.getType());
        assertEquals("abc456", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc456", extId.getUrl().getValue());

        serviceDelegator.deleteExternalIdentifier("4444-4444-4444-4442", 3L);
        fail();
    }

    @Test
    public void testReadPublicScope_ExternalIdentifiers() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        // Public works
        Response r = serviceDelegator.viewExternalIdentifiers(ORCID);
        assertNotNull(r);
        assertEquals(PersonExternalIdentifiers.class.getName(), r.getEntity().getClass().getName());
        PersonExternalIdentifiers p = (PersonExternalIdentifiers) r.getEntity();
        assertNotNull(p);
        assertEquals("/0000-0000-0000-0003/external-identifiers", p.getPath());
        Utils.verifyLastModified(p.getLastModifiedDate());
        assertEquals(3, p.getExternalIdentifiers().size());
        boolean found13 = false, found14 = false, found15 = false;
        for (PersonExternalIdentifier element : p.getExternalIdentifiers()) {
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

        r = serviceDelegator.viewExternalIdentifier(ORCID, 13L);
        assertNotNull(r);
        assertEquals(PersonExternalIdentifier.class.getName(), r.getEntity().getClass().getName());

        // Limited am the source of should work
        serviceDelegator.viewExternalIdentifier(ORCID, 14L);

        // Limited fail
        try {
            serviceDelegator.viewExternalIdentifier(ORCID, 16L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private am the source of should work
        serviceDelegator.viewExternalIdentifier(ORCID, 15L);
        // Private fail
        try {
            serviceDelegator.viewExternalIdentifier(ORCID, 17L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }
}
