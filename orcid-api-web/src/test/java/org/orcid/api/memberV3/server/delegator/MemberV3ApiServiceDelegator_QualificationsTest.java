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
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Qualifications;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the qualification endpoints of the member V3 API.
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
public class MemberV3ApiServiceDelegator_QualificationsTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final ScopePathType SCOPE = ScopePathType.AFFILIATIONS_READ_LIMITED;

    private Qualification qualification(long putCode, Visibility visibility, String department, Source source) {
        Qualification element = new Qualification();
        element.setPutCode(putCode);
        element.setVisibility(visibility);
        element.setDepartmentName(department);
        element.setRoleTitle(visibility.value().toUpperCase());
        element.setOrganization(Utils.getOrganization());
        element.setLastModifiedDate(lastModified());
        element.setSource(source);
        return element;
    }

    private QualificationSummary summary(long putCode, Visibility visibility, String department, Source source) {
        QualificationSummary element = new QualificationSummary();
        element.setPutCode(putCode);
        element.setVisibility(visibility);
        element.setDepartmentName(department);
        element.setRoleTitle(visibility.value().toUpperCase());
        element.setOrganization(Utils.getOrganization());
        element.setLastModifiedDate(lastModified());
        element.setSource(source);
        return element;
    }

    private AffiliationGroup<QualificationSummary> group(QualificationSummary... elements) {
        AffiliationGroup<QualificationSummary> group = new AffiliationGroup<>();
        for (QualificationSummary element : elements) {
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
    public void testViewQualificationsWrongToken() {
        when(affiliationsManagerReadOnly.getQualificationSummaryList(ORCID))
                .thenReturn(Arrays.asList(summary(42L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));

        serviceDelegator.viewQualifications(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewQualificationWrongToken() {
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 42L))
                .thenReturn(qualification(42L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Qualification.class), eq(SCOPE));

        serviceDelegator.viewQualification(ORCID, 42L);
    }

    @Test
    public void testViewQualificationReadPublic() {
        Qualification stored = qualification(42L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 42L)).thenReturn(stored);

        Response r = serviceDelegator.viewQualification(ORCID, 42L);
        Qualification element = (Qualification) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/qualification/42", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewQualificationSummaryWrongToken() {
        when(affiliationsManagerReadOnly.getQualificationSummary(ORCID, 42L))
                .thenReturn(summary(42L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(QualificationSummary.class), eq(SCOPE));

        serviceDelegator.viewQualificationSummary(ORCID, 42L);
    }

    @Test
    public void testViewQualificationsReadPublic() {
        QualificationSummary publicSummary = summary(42L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        List<QualificationSummary> summaries = Arrays.asList(publicSummary);
        when(affiliationsManagerReadOnly.getQualificationSummaryList(ORCID)).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(summaries, false)).thenReturn(Arrays.asList(group(publicSummary)));

        Response r = serviceDelegator.viewQualifications(ORCID);
        Qualifications element = (Qualifications) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/qualifications", element.getPath());
        for (AffiliationGroup<QualificationSummary> group : element.retrieveGroups()) {
            for (QualificationSummary activity : group.getActivities()) {
                assertEquals("/0000-0000-0000-0003/qualification/42", activity.getPath());
            }
        }
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testViewQualificationSummaryReadPublic() {
        QualificationSummary stored = summary(42L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getQualificationSummary(ORCID, 42L)).thenReturn(stored);

        Response r = serviceDelegator.viewQualificationSummary(ORCID, 42L);
        QualificationSummary element = (QualificationSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/qualification/42", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewPublicQualification() {
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 42L))
                .thenReturn(qualification(42L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewQualification(ORCID, 42L);
        assertNotNull(response);
        Qualification element = (Qualification) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(42L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/qualification/42", element.getPath());
        assertEquals("PUBLIC Department", element.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewLimitedQualification() {
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 45L))
                .thenReturn(qualification(45L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID)));

        Response response = serviceDelegator.viewQualification(ORCID, 45L);
        assertNotNull(response);
        Qualification element = (Qualification) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(45L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/qualification/45", element.getPath());
        assertEquals("SELF LIMITED Department", element.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewPrivateQualification() {
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 44L))
                .thenReturn(qualification(44L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewQualification(ORCID, 44L);
        assertNotNull(response);
        Qualification element = (Qualification) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(44L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/qualification/44", element.getPath());
        assertEquals("PRIVATE Department", element.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateQualificationWhereYouAreNotTheSource() {
        Qualification stored = qualification(46L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_2));
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 46L)).thenReturn(stored);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(ORCID, stored, SCOPE);

        serviceDelegator.viewQualification(ORCID, 46L);
        fail();
    }

    /**
     * The rule this proves -- that qualification 42 cannot be read through another
     * record -- lives in a SQL WHERE clause
     * ({@code OrgAffiliationRelationDaoImpl.getOrgAffiliationRelation}), so with a
     * mocked manager all that is left here is that the delegator does not swallow
     * the exception. The predicate itself is proved by
     * MemberV3ApiServiceDelegatorDatabaseRulesTest, which carries the DBUnit
     * fixture and runs in the db-tests stage.
     */
    @Test(expected = NoResultException.class)
    public void testViewQualificationThatDontBelongToTheUser() {
        when(affiliationsManagerReadOnly.getQualificationAffiliation("4444-4444-4444-4446", 42L)).thenThrow(new NoResultException());

        serviceDelegator.viewQualification("4444-4444-4444-4446", 42L);
        fail();
    }

    @Test
    public void testViewQualifications() {
        QualificationSummary limited = summary(43L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1));
        QualificationSummary priv = summary(44L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1));
        QualificationSummary selfLimited = summary(45L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        QualificationSummary pub = summary(42L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        List<QualificationSummary> summaries = Arrays.asList(limited, priv, selfLimited, pub);
        when(affiliationsManagerReadOnly.getQualificationSummaryList(ORCID)).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(summaries, false))
                .thenReturn(Arrays.asList(group(limited), group(priv), group(selfLimited, pub)));

        Response r = serviceDelegator.viewQualifications(ORCID);
        assertNotNull(r);
        Qualifications elements = (Qualifications) r.getEntity();
        assertNotNull(elements);
        assertEquals("/0000-0000-0000-0003/qualifications", elements.getPath());
        Utils.verifyLastModified(elements.getLastModifiedDate());
        assertNotNull(elements.retrieveGroups());
        assertEquals(3, elements.retrieveGroups().size());
        boolean found1 = false, found2 = false, found3 = false;

        for (AffiliationGroup<QualificationSummary> group : elements.retrieveGroups()) {
            QualificationSummary element0 = group.getActivities().get(0);
            Utils.verifyLastModified(element0.getLastModifiedDate());
            if (Long.valueOf(43).equals(element0.getPutCode())) {
                assertEquals("LIMITED Department", element0.getDepartmentName());
                found1 = true;
            } else if (Long.valueOf(44).equals(element0.getPutCode())) {
                assertEquals("PRIVATE Department", element0.getDepartmentName());
                found2 = true;
            } else if (Long.valueOf(45).equals(element0.getPutCode())) {
                assertEquals("SELF LIMITED Department", element0.getDepartmentName());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(42), group.getActivities().get(1).getPutCode());
                assertEquals("PUBLIC Department", group.getActivities().get(1).getDepartmentName());
                found3 = true;
            } else {
                fail("Invalid qualification found: " + element0.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testReadPublicScope_Qualifications() {
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 42L)).thenReturn(qualification(42L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getQualificationSummary(ORCID, 42L)).thenReturn(summary(42L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 43L)).thenReturn(qualification(43L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getQualificationSummary(ORCID, 43L)).thenReturn(summary(43L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 44L)).thenReturn(qualification(44L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getQualificationSummary(ORCID, 44L)).thenReturn(summary(44L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));

        Qualification failing1 = qualification(45L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        QualificationSummary failingSummary1 = summary(45L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 45L)).thenReturn(failing1);
        when(affiliationsManagerReadOnly.getQualificationSummary(ORCID, 45L)).thenReturn(failingSummary1);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failing1, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failingSummary1, SCOPE);

        Response r = serviceDelegator.viewQualification(ORCID, 42L);
        assertNotNull(r);
        assertEquals(Qualification.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewQualificationSummary(ORCID, 42L);
        assertNotNull(r);
        assertEquals(QualificationSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewQualification(ORCID, 43L);
        serviceDelegator.viewQualificationSummary(ORCID, 43L);
        // Private that am the source of should work
        serviceDelegator.viewQualification(ORCID, 44L);
        serviceDelegator.viewQualificationSummary(ORCID, 44L);

        // Elements the security manager refuses must come back out of the
        // delegator unwrapped, not swallowed or remapped.
        try {
            serviceDelegator.viewQualification(ORCID, 45L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewQualificationSummary(ORCID, 45L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewQualification(ORCID, 45L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewQualificationSummary(ORCID, 45L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddQualification() {
        Qualification toCreate = (Qualification) Utils.getAffiliation(AffiliationType.QUALIFICATION);
        toCreate.setSource(clientSource(CLIENT_2));
        Qualification created = qualification(9999L, Visibility.PUBLIC, "My department name", clientSource(CLIENT_1));
        when(affiliationsManager.createQualificationAffiliation(eq(ORCID), any(Qualification.class), eq(true))).thenReturn(created);

        Response response = serviceDelegator.createQualification(ORCID, toCreate);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(9999L), Utils.getPutCode(response));

        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(orcidSecurityManager).checkClientAccessAndScopes(ORCID, ScopePathType.AFFILIATIONS_CREATE, ScopePathType.AFFILIATIONS_UPDATE);

        // A client supplied source must never reach the manager.
        ArgumentCaptor<Qualification> captor = ArgumentCaptor.forClass(Qualification.class);
        verify(affiliationsManager).createQualificationAffiliation(eq(ORCID), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());

        // Remove new element
        serviceDelegator.deleteAffiliation(ORCID, 9999L);
        verify(affiliationsManager).checkSourceAndDelete(ORCID, 9999L);
    }

    @Test
    public void testAddQualificationNoCityNoCountry() {
        Qualification toCreate = (Qualification) Utils.getAffiliationNoCityNoCountry(AffiliationType.QUALIFICATION);
        Qualification created = qualification(9998L, Visibility.PUBLIC, "My department name", clientSource(CLIENT_1));
        when(affiliationsManager.createQualificationAffiliation(eq(ORCID), any(Qualification.class), eq(true))).thenReturn(created);

        Response response = serviceDelegator.createQualification(ORCID, toCreate);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(9998L), Utils.getPutCode(response));
        verify(affiliationsManager).createQualificationAffiliation(eq(ORCID), any(Qualification.class), eq(true));
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddQualificationsDuplicateExternalIDs() {
        Qualification element = (Qualification) Utils.getAffiliation(AffiliationType.QUALIFICATION);
        element.setExternalIDs(duplicateExternalIDs());
        doThrow(new OrcidDuplicatedActivityException(new HashMap<String, String>()))
                .when(affiliationsManager).createQualificationAffiliation(eq(ORCID), any(Qualification.class), eq(true));

        serviceDelegator.createQualification(ORCID, element);
    }

    @Test
    public void testUpdateQualification() {
        Qualification stored = qualification(42L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 42L)).thenReturn(stored);

        Response response = serviceDelegator.viewQualification(ORCID, 42L);
        assertNotNull(response);
        Qualification element = (Qualification) response.getEntity();
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

        when(affiliationsManager.updateQualificationAffiliation(eq(ORCID), any(Qualification.class), eq(true))).thenReturn(element);

        response = serviceDelegator.updateQualification(ORCID, 42L, element);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Qualification updated = (Qualification) response.getEntity();
        assertEquals("Updated department name", updated.getDepartmentName());
        assertEquals("The updated role title", updated.getRoleTitle());

        verify(orcidSecurityManager).checkClientAccessAndScopes(ORCID, ScopePathType.AFFILIATIONS_UPDATE);
        ArgumentCaptor<Qualification> captor = ArgumentCaptor.forClass(Qualification.class);
        verify(affiliationsManager).updateQualificationAffiliation(eq(ORCID), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateQualificationYouAreNotTheSourceOf() {
        Qualification stored = qualification(45L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 45L)).thenReturn(stored);
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(affiliationsManager).updateQualificationAffiliation(eq(ORCID), any(Qualification.class), eq(true));

        Response response = serviceDelegator.viewQualification(ORCID, 45L);
        assertNotNull(response);
        Qualification element = (Qualification) response.getEntity();
        assertNotNull(element);
        element.setDepartmentName("Updated department name");
        element.setRoleTitle("The updated role title");
        serviceDelegator.updateQualification(ORCID, 45L, element);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateQualificationChangingVisibilityTest() {
        Qualification stored = qualification(42L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 42L)).thenReturn(stored);
        doThrow(new VisibilityMismatchException())
                .when(affiliationsManager).updateQualificationAffiliation(eq(ORCID), any(Qualification.class), eq(true));

        Response response = serviceDelegator.viewQualification(ORCID, 42L);
        assertNotNull(response);
        Qualification element = (Qualification) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());

        element.setVisibility(Visibility.PRIVATE);

        serviceDelegator.updateQualification(ORCID, 42L, element);
        fail();
    }

    @Test
    public void testUpdateQualificationLeavingVisibilityNullTest() {
        Qualification stored = qualification(42L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 42L)).thenReturn(stored);
        // The manager restores the stored visibility when the request leaves it
        // null; that rule is proved in orcid-core, here we only check the
        // delegator returns what the manager produced.
        when(affiliationsManager.updateQualificationAffiliation(eq(ORCID), any(Qualification.class), eq(true)))
                .thenReturn(qualification(42L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewQualification(ORCID, 42L);
        assertNotNull(response);
        Qualification element = (Qualification) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());

        element.setVisibility(null);

        response = serviceDelegator.updateQualification(ORCID, 42L, element);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        element = (Qualification) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testUpdateQualificationDuplicateExternalIDs() {
        Qualification stored = qualification(42L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 42L)).thenReturn(stored);
        doThrow(new OrcidDuplicatedActivityException(new HashMap<String, String>()))
                .when(affiliationsManager).updateQualificationAffiliation(eq(ORCID), any(Qualification.class), eq(true));

        Response response = serviceDelegator.viewQualification(ORCID, 42L);
        Qualification element = (Qualification) response.getEntity();
        element.setExternalIDs(duplicateExternalIDs());
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        serviceDelegator.updateQualification(ORCID, 42L, element);
        fail();
    }

    @Test
    public void testDeleteQualification() {
        Qualification stored = qualification(1005L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getQualificationAffiliation("0000-0000-0000-0002", 1005L)).thenReturn(stored).thenThrow(new NoResultException());

        Response response = serviceDelegator.viewQualification("0000-0000-0000-0002", 1005L);
        assertNotNull(response);
        assertNotNull(response.getEntity());

        response = serviceDelegator.deleteAffiliation("0000-0000-0000-0002", 1005L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(affiliationsManager).checkSourceAndDelete("0000-0000-0000-0002", 1005L);

        try {
            serviceDelegator.viewQualification("0000-0000-0000-0002", 1005L);
            fail();
        } catch (NoResultException nre) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteQualificationYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(affiliationsManager).checkSourceAndDelete(ORCID, 45L);

        serviceDelegator.deleteAffiliation(ORCID, 45L);
        fail();
    }
}
