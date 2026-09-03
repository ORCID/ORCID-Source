package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jakarta.persistence.NoResultException;
import jakarta.ws.rs.core.Response;

import org.apache.hc.core5.http.HttpStatus;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.orcid.core.exception.InvalidOrgAddressException;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the education endpoints of the member V3 API.
 *
 * <p>
 * These used to run the real {@code OrcidSecurityManager} against a DBUnit
 * fixture, so a "view" test proved both the delegator's own work (look the
 * element up, set its path, resolve the source name, return 200) and the
 * security manager's visibility table. Only the first half can be proved here:
 * {@code checkAndFilter} is void and filters in place, so a mocked security
 * manager filters nothing and any assertion of the form "only public elements
 * came back" would pass without proving anything. What each test asserts now is
 * the delegator's contract, plus a {@code verify} that the element really was
 * handed to the security manager with the right scope. The visibility and scope
 * tables themselves are proved in orcid-core, by
 * {@code OrcidSecurityManager_generalTest} and its siblings.
 */
public class MemberV3ApiServiceDelegator_EducationsTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final ScopePathType SCOPE = ScopePathType.AFFILIATIONS_READ_LIMITED;

    private Education education(long putCode, Visibility visibility, String department, Source source) {
        Education element = new Education();
        element.setPutCode(putCode);
        element.setVisibility(visibility);
        element.setDepartmentName(department);
        element.setRoleTitle(visibility.value().toUpperCase());
        element.setOrganization(Utils.getOrganization());
        element.setLastModifiedDate(lastModified());
        element.setSource(source);
        return element;
    }

    private EducationSummary summary(long putCode, Visibility visibility, String department, Source source) {
        EducationSummary element = new EducationSummary();
        element.setPutCode(putCode);
        element.setVisibility(visibility);
        element.setDepartmentName(department);
        element.setRoleTitle(visibility.value().toUpperCase());
        element.setOrganization(Utils.getOrganization());
        element.setLastModifiedDate(lastModified());
        element.setSource(source);
        return element;
    }

    private AffiliationGroup<EducationSummary> group(EducationSummary... elements) {
        AffiliationGroup<EducationSummary> group = new AffiliationGroup<>();
        for (EducationSummary element : elements) {
            group.getActivities().add(element);
        }
        return group;
    }

    private ExternalIDs duplicateExternalIDs() {
        ExternalID e1 = new ExternalID();
        e1.setRelationship(Relationship.SELF);
        e1.setType("erm");
        e1.setUrl(new Url("https://orcid.org"));
        e1.setValue("err");

        ExternalID e2 = new ExternalID();
        e2.setRelationship(Relationship.SELF);
        e2.setType("err");
        e2.setUrl(new Url("http://bbc.co.uk"));
        e2.setValue("erm");

        ExternalIDs externalIDs = new ExternalIDs();
        externalIDs.getExternalIdentifier().add(e1);
        externalIDs.getExternalIdentifier().add(e2);
        return externalIDs;
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEducationsWrongToken() {
        when(affiliationsManagerReadOnly.getEducationSummaryList(ORCID))
                .thenReturn(Arrays.asList(summary(20L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));

        serviceDelegator.viewEducations(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEducationWrongToken() {
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 20L))
                .thenReturn(education(20L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Education.class), eq(SCOPE));

        serviceDelegator.viewEducation(ORCID, 20L);
    }

    @Test
    public void testViewEducationReadPublic() {
        Education stored = education(20L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 20L)).thenReturn(stored);

        Response r = serviceDelegator.viewEducation(ORCID, 20L);
        Education element = (Education) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/education/20", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEducationSummaryWrongToken() {
        when(affiliationsManagerReadOnly.getEducationSummary(ORCID, 20L))
                .thenReturn(summary(20L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(EducationSummary.class), eq(SCOPE));

        serviceDelegator.viewEducationSummary(ORCID, 20L);
    }

    @Test
    public void testViewEducationsReadPublic() {
        EducationSummary publicSummary = summary(20L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        List<EducationSummary> summaries = Arrays.asList(publicSummary);
        when(affiliationsManagerReadOnly.getEducationSummaryList(ORCID)).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(summaries, false)).thenReturn(Arrays.asList(group(publicSummary)));

        Response r = serviceDelegator.viewEducations(ORCID);
        Educations element = (Educations) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/educations", element.getPath());
        for (AffiliationGroup<EducationSummary> group : element.retrieveGroups()) {
            for (EducationSummary activity : group.getActivities()) {
                assertEquals("/0000-0000-0000-0003/education/20", activity.getPath());
            }
        }
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testViewEducationSummaryReadPublic() {
        EducationSummary stored = summary(20L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEducationSummary(ORCID, 20L)).thenReturn(stored);

        Response r = serviceDelegator.viewEducationSummary(ORCID, 20L);
        EducationSummary element = (EducationSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/education/20", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewPublicEducation() {
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 20L))
                .thenReturn(education(20L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(response);
        Education element = (Education) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(20L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/education/20", element.getPath());
        assertEquals("PUBLIC Department", element.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewLimitedEducation() {
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 21L))
                .thenReturn(education(21L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewEducation(ORCID, 21L);
        assertNotNull(response);
        Education element = (Education) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(21L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/education/21", element.getPath());
        assertEquals("LIMITED Department", element.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewPrivateEducation() {
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 22L))
                .thenReturn(education(22L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewEducation(ORCID, 22L);
        assertNotNull(response);
        Education element = (Education) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(22L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/education/22", element.getPath());
        assertEquals("PRIVATE Department", element.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateEducationWhereYouAreNotTheSource() {
        Education stored = education(26L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_2));
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 26L)).thenReturn(stored);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(ORCID, stored, SCOPE);

        serviceDelegator.viewEducation(ORCID, 26L);
        fail();
    }

    /**
     * The rule this proves -- that education 20 cannot be read through another
     * record -- lives in a SQL WHERE clause
     * ({@code OrgAffiliationRelationDaoImpl.getOrgAffiliationRelation}), so with a
     * mocked manager all that is left here is that the delegator does not swallow
     * the exception. The predicate itself is proved by
     * MemberV3ApiServiceDelegatorDatabaseRulesTest, which carries the DBUnit
     * fixture and runs in the db-tests stage.
     */
    @Test(expected = NoResultException.class)
    public void testViewEducationThatDontBelongToTheUser() {
        when(affiliationsManagerReadOnly.getEducationAffiliation("4444-4444-4444-4446", 20L)).thenThrow(new NoResultException());

        serviceDelegator.viewEducation("4444-4444-4444-4446", 20L);
        fail();
    }

    @Test
    public void testViewEducations() {
        EducationSummary limited = summary(21L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1));
        EducationSummary priv = summary(22L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1));
        EducationSummary selfLimited = summary(25L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        EducationSummary pub = summary(20L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        List<EducationSummary> summaries = Arrays.asList(limited, priv, selfLimited, pub);
        when(affiliationsManagerReadOnly.getEducationSummaryList(ORCID)).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(summaries, false))
                .thenReturn(Arrays.asList(group(limited), group(priv), group(selfLimited, pub)));

        Response r = serviceDelegator.viewEducations(ORCID);
        assertNotNull(r);
        Educations elements = (Educations) r.getEntity();
        assertNotNull(elements);
        assertEquals("/0000-0000-0000-0003/educations", elements.getPath());
        Utils.verifyLastModified(elements.getLastModifiedDate());
        assertNotNull(elements.retrieveGroups());
        assertEquals(3, elements.retrieveGroups().size());
        boolean found1 = false, found2 = false, found3 = false;

        for (AffiliationGroup<EducationSummary> group : elements.retrieveGroups()) {
            EducationSummary element0 = group.getActivities().get(0);
            Utils.verifyLastModified(element0.getLastModifiedDate());
            if (Long.valueOf(21).equals(element0.getPutCode())) {
                assertEquals("LIMITED Department", element0.getDepartmentName());
                found1 = true;
            } else if (Long.valueOf(22).equals(element0.getPutCode())) {
                assertEquals("PRIVATE Department", element0.getDepartmentName());
                found2 = true;
            } else if (Long.valueOf(25).equals(element0.getPutCode())) {
                assertEquals("SELF LIMITED Department", element0.getDepartmentName());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(20), group.getActivities().get(1).getPutCode());
                assertEquals("PUBLIC Department", group.getActivities().get(1).getDepartmentName());
                found3 = true;
            } else {
                fail("Invalid education found: " + element0.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testReadPublicScope_Educations() {
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 20L)).thenReturn(education(20L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getEducationSummary(ORCID, 20L)).thenReturn(summary(20L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 21L)).thenReturn(education(21L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getEducationSummary(ORCID, 21L)).thenReturn(summary(21L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 22L)).thenReturn(education(22L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getEducationSummary(ORCID, 22L)).thenReturn(summary(22L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));

        Education failing1 = education(25L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        EducationSummary failingSummary1 = summary(25L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 25L)).thenReturn(failing1);
        when(affiliationsManagerReadOnly.getEducationSummary(ORCID, 25L)).thenReturn(failingSummary1);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failing1, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failingSummary1, SCOPE);
        Education failing2 = education(26L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_2));
        EducationSummary failingSummary2 = summary(26L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_2));
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 26L)).thenReturn(failing2);
        when(affiliationsManagerReadOnly.getEducationSummary(ORCID, 26L)).thenReturn(failingSummary2);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failing2, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failingSummary2, SCOPE);

        Response r = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(r);
        assertEquals(Education.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewEducationSummary(ORCID, 20L);
        assertNotNull(r);
        assertEquals(EducationSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewEducation(ORCID, 21L);
        serviceDelegator.viewEducationSummary(ORCID, 21L);
        // Private that am the source of should work
        serviceDelegator.viewEducation(ORCID, 22L);
        serviceDelegator.viewEducationSummary(ORCID, 22L);

        // Elements the security manager refuses must come back out of the
        // delegator unwrapped, not swallowed or remapped.
        try {
            serviceDelegator.viewEducation(ORCID, 25L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewEducationSummary(ORCID, 25L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewEducation(ORCID, 26L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewEducationSummary(ORCID, 26L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddEducation() {
        Education toCreate = (Education) Utils.getAffiliation(AffiliationType.EDUCATION);
        toCreate.setSource(clientSource(CLIENT_2));
        Education created = education(9999L, Visibility.PUBLIC, "My department name", clientSource(CLIENT_1));
        when(affiliationsManager.createEducationAffiliation(eq(ORCID), any(Education.class), eq(true))).thenReturn(created);

        Response response = serviceDelegator.createEducation(ORCID, toCreate);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(9999L), Utils.getPutCode(response));

        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(orcidSecurityManager).checkClientAccessAndScopes(ORCID, ScopePathType.AFFILIATIONS_CREATE, ScopePathType.AFFILIATIONS_UPDATE);

        // A client supplied source must never reach the manager.
        ArgumentCaptor<Education> captor = ArgumentCaptor.forClass(Education.class);
        verify(affiliationsManager).createEducationAffiliation(eq(ORCID), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());

        // Remove new element
        serviceDelegator.deleteAffiliation(ORCID, 9999L);
        verify(affiliationsManager).checkSourceAndDelete(ORCID, 9999L);
    }

    /**
     * An education whose organization carries neither a city nor a country is
     * refused. The rule is {@code ActivityValidator.validateOrgAddress}, which
     * runs inside {@code AffiliationsManager} and is proved by orcid-core's
     * {@code ActivityValidatorTest.validateEducationNoCountryNorCity}; the half
     * that belongs here is that {@code createEducation} lets the refusal out
     * rather than answering 201. Only educations and employments are subject to
     * it -- see {@code testAddQualificationNoCityNoCountry}, which asserts the
     * opposite for a qualification on purpose.
     */
    @Test(expected = InvalidOrgAddressException.class)
    public void testAddEducationNoCityNoCountry() {
        Education toCreate = (Education) Utils.getAffiliationNoCityNoCountry(AffiliationType.EDUCATION);
        doThrow(new InvalidOrgAddressException()).when(affiliationsManager).createEducationAffiliation(eq(ORCID), any(Education.class), eq(true));

        serviceDelegator.createEducation(ORCID, toCreate);
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddEducationsDuplicateExternalIDs() {
        Education element = (Education) Utils.getAffiliation(AffiliationType.EDUCATION);
        element.setExternalIDs(duplicateExternalIDs());
        doThrow(new OrcidDuplicatedActivityException(new HashMap<String, String>()))
                .when(affiliationsManager).createEducationAffiliation(eq(ORCID), any(Education.class), eq(true));

        serviceDelegator.createEducation(ORCID, element);
    }

    @Test
    public void testUpdateEducation() {
        Education stored = education(20L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 20L)).thenReturn(stored);

        Response response = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(response);
        Education element = (Education) response.getEntity();
        assertNotNull(element);
        assertEquals("PUBLIC Department", element.getDepartmentName());
        assertEquals("PUBLIC", element.getRoleTitle());
        Utils.verifyLastModified(element.getLastModifiedDate());

        element.setDepartmentName("Updated department name");
        element.setRoleTitle("The updated role title");

        // disambiguated org is required in API v3
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("abc456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        element.getOrganization().setDisambiguatedOrganization(disambiguatedOrg);

        when(affiliationsManager.updateEducationAffiliation(eq(ORCID), any(Education.class), eq(true))).thenReturn(element);

        response = serviceDelegator.updateEducation(ORCID, 20L, element);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Education updated = (Education) response.getEntity();
        assertEquals("Updated department name", updated.getDepartmentName());
        assertEquals("The updated role title", updated.getRoleTitle());

        verify(orcidSecurityManager).checkClientAccessAndScopes(ORCID, ScopePathType.AFFILIATIONS_UPDATE);
        ArgumentCaptor<Education> captor = ArgumentCaptor.forClass(Education.class);
        verify(affiliationsManager).updateEducationAffiliation(eq(ORCID), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateEducationYouAreNotTheSourceOf() {
        Education stored = education(25L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 25L)).thenReturn(stored);
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(affiliationsManager).updateEducationAffiliation(eq(ORCID), any(Education.class), eq(true));

        Response response = serviceDelegator.viewEducation(ORCID, 25L);
        assertNotNull(response);
        Education element = (Education) response.getEntity();
        assertNotNull(element);
        element.setDepartmentName("Updated department name");
        element.setRoleTitle("The updated role title");
        serviceDelegator.updateEducation(ORCID, 25L, element);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateEducationChangingVisibilityTest() {
        Education stored = education(20L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 20L)).thenReturn(stored);
        doThrow(new VisibilityMismatchException())
                .when(affiliationsManager).updateEducationAffiliation(eq(ORCID), any(Education.class), eq(true));

        Response response = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(response);
        Education element = (Education) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());

        element.setVisibility(Visibility.PRIVATE);

        serviceDelegator.updateEducation(ORCID, 20L, element);
        fail();
    }

    @Test
    public void testUpdateEducationLeavingVisibilityNullTest() {
        Education stored = education(20L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 20L)).thenReturn(stored);
        // The manager restores the stored visibility when the request leaves it
        // null; that rule is proved in orcid-core, here we only check the
        // delegator returns what the manager produced.
        when(affiliationsManager.updateEducationAffiliation(eq(ORCID), any(Education.class), eq(true)))
                .thenReturn(education(20L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(response);
        Education element = (Education) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());

        element.setVisibility(null);

        response = serviceDelegator.updateEducation(ORCID, 20L, element);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        element = (Education) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testUpdateEducationDuplicateExternalIDs() {
        Education stored = education(20L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 20L)).thenReturn(stored);
        doThrow(new OrcidDuplicatedActivityException(new HashMap<String, String>()))
                .when(affiliationsManager).updateEducationAffiliation(eq(ORCID), any(Education.class), eq(true));

        Response response = serviceDelegator.viewEducation(ORCID, 20L);
        Education element = (Education) response.getEntity();
        element.setExternalIDs(duplicateExternalIDs());
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        serviceDelegator.updateEducation(ORCID, 20L, element);
        fail();
    }

    @Test
    public void testDeleteEducation() {
        Education stored = education(1001L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEducationAffiliation("0000-0000-0000-0002", 1001L)).thenReturn(stored).thenThrow(new NoResultException());

        Response response = serviceDelegator.viewEducation("0000-0000-0000-0002", 1001L);
        assertNotNull(response);
        assertNotNull(response.getEntity());

        response = serviceDelegator.deleteAffiliation("0000-0000-0000-0002", 1001L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(affiliationsManager).checkSourceAndDelete("0000-0000-0000-0002", 1001L);

        try {
            serviceDelegator.viewEducation("0000-0000-0000-0002", 1001L);
            fail();
        } catch (NoResultException nre) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteEducationYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(affiliationsManager).checkSourceAndDelete(ORCID, 23L);

        serviceDelegator.deleteAffiliation(ORCID, 23L);
        fail();
    }
}
