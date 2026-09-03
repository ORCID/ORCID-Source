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
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Services;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the service endpoints of the member V3 API.
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
public class MemberV3ApiServiceDelegator_ServicesTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final ScopePathType SCOPE = ScopePathType.AFFILIATIONS_READ_LIMITED;

    private Service service(long putCode, Visibility visibility, String department, Source source) {
        Service element = new Service();
        element.setPutCode(putCode);
        element.setVisibility(visibility);
        element.setDepartmentName(department);
        element.setRoleTitle(visibility.value().toUpperCase());
        element.setOrganization(Utils.getOrganization());
        element.setLastModifiedDate(lastModified());
        element.setSource(source);
        return element;
    }

    private ServiceSummary summary(long putCode, Visibility visibility, String department, Source source) {
        ServiceSummary element = new ServiceSummary();
        element.setPutCode(putCode);
        element.setVisibility(visibility);
        element.setDepartmentName(department);
        element.setRoleTitle(visibility.value().toUpperCase());
        element.setOrganization(Utils.getOrganization());
        element.setLastModifiedDate(lastModified());
        element.setSource(source);
        return element;
    }

    private AffiliationGroup<ServiceSummary> group(ServiceSummary... elements) {
        AffiliationGroup<ServiceSummary> group = new AffiliationGroup<>();
        for (ServiceSummary element : elements) {
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
    public void testViewServicesWrongToken() {
        when(affiliationsManagerReadOnly.getServiceSummaryList(ORCID))
                .thenReturn(Arrays.asList(summary(47L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));

        serviceDelegator.viewServices(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewServiceWrongToken() {
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 47L))
                .thenReturn(service(47L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Service.class), eq(SCOPE));

        serviceDelegator.viewService(ORCID, 47L);
    }

    @Test
    public void testViewServiceReadPublic() {
        Service stored = service(47L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 47L)).thenReturn(stored);

        Response r = serviceDelegator.viewService(ORCID, 47L);
        Service element = (Service) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/service/47", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewServiceSummaryWrongToken() {
        when(affiliationsManagerReadOnly.getServiceSummary(ORCID, 47L))
                .thenReturn(summary(47L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(ServiceSummary.class), eq(SCOPE));

        serviceDelegator.viewServiceSummary(ORCID, 47L);
    }

    @Test
    public void testViewServicesReadPublic() {
        ServiceSummary publicSummary = summary(47L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        List<ServiceSummary> summaries = Arrays.asList(publicSummary);
        when(affiliationsManagerReadOnly.getServiceSummaryList(ORCID)).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(summaries, false)).thenReturn(Arrays.asList(group(publicSummary)));

        Response r = serviceDelegator.viewServices(ORCID);
        Services element = (Services) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/services", element.getPath());
        for (AffiliationGroup<ServiceSummary> group : element.retrieveGroups()) {
            for (ServiceSummary activity : group.getActivities()) {
                assertEquals("/0000-0000-0000-0003/service/47", activity.getPath());
            }
        }
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testViewServiceSummaryReadPublic() {
        ServiceSummary stored = summary(47L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getServiceSummary(ORCID, 47L)).thenReturn(stored);

        Response r = serviceDelegator.viewServiceSummary(ORCID, 47L);
        ServiceSummary element = (ServiceSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/service/47", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewPublicService() {
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 47L))
                .thenReturn(service(47L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewService(ORCID, 47L);
        assertNotNull(response);
        Service element = (Service) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(47L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/service/47", element.getPath());
        assertEquals("PUBLIC Department", element.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewLimitedService() {
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 50L))
                .thenReturn(service(50L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID)));

        Response response = serviceDelegator.viewService(ORCID, 50L);
        assertNotNull(response);
        Service element = (Service) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(50L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/service/50", element.getPath());
        assertEquals("SELF LIMITED Department", element.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewPrivateService() {
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 49L))
                .thenReturn(service(49L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewService(ORCID, 49L);
        assertNotNull(response);
        Service element = (Service) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(49L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/service/49", element.getPath());
        assertEquals("PRIVATE Department", element.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateServiceWhereYouAreNotTheSource() {
        Service stored = service(51L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_2));
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 51L)).thenReturn(stored);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(ORCID, stored, SCOPE);

        serviceDelegator.viewService(ORCID, 51L);
        fail();
    }

    /**
     * The rule this proves -- that service 47 cannot be read through another
     * record -- lives in a SQL WHERE clause
     * ({@code OrgAffiliationRelationDaoImpl.getOrgAffiliationRelation}), so with a
     * mocked manager all that is left here is that the delegator does not swallow
     * the exception. The predicate itself is proved by
     * MemberV3ApiServiceDelegatorDatabaseRulesTest, which carries the DBUnit
     * fixture and runs in the db-tests stage.
     */
    @Test(expected = NoResultException.class)
    public void testViewServiceThatDontBelongToTheUser() {
        when(affiliationsManagerReadOnly.getServiceAffiliation("4444-4444-4444-4446", 47L)).thenThrow(new NoResultException());

        serviceDelegator.viewService("4444-4444-4444-4446", 47L);
        fail();
    }

    @Test
    public void testViewServices() {
        ServiceSummary limited = summary(48L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1));
        ServiceSummary priv = summary(49L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1));
        ServiceSummary selfLimited = summary(50L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        ServiceSummary pub = summary(47L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        List<ServiceSummary> summaries = Arrays.asList(limited, priv, selfLimited, pub);
        when(affiliationsManagerReadOnly.getServiceSummaryList(ORCID)).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(summaries, false))
                .thenReturn(Arrays.asList(group(limited), group(priv), group(selfLimited, pub)));

        Response r = serviceDelegator.viewServices(ORCID);
        assertNotNull(r);
        Services elements = (Services) r.getEntity();
        assertNotNull(elements);
        assertEquals("/0000-0000-0000-0003/services", elements.getPath());
        Utils.verifyLastModified(elements.getLastModifiedDate());
        assertNotNull(elements.retrieveGroups());
        assertEquals(3, elements.retrieveGroups().size());
        boolean found1 = false, found2 = false, found3 = false;

        for (AffiliationGroup<ServiceSummary> group : elements.retrieveGroups()) {
            ServiceSummary element0 = group.getActivities().get(0);
            Utils.verifyLastModified(element0.getLastModifiedDate());
            if (Long.valueOf(48).equals(element0.getPutCode())) {
                assertEquals("LIMITED Department", element0.getDepartmentName());
                found1 = true;
            } else if (Long.valueOf(49).equals(element0.getPutCode())) {
                assertEquals("PRIVATE Department", element0.getDepartmentName());
                found2 = true;
            } else if (Long.valueOf(50).equals(element0.getPutCode())) {
                assertEquals("SELF LIMITED Department", element0.getDepartmentName());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(47), group.getActivities().get(1).getPutCode());
                assertEquals("PUBLIC Department", group.getActivities().get(1).getDepartmentName());
                found3 = true;
            } else {
                fail("Invalid service found: " + element0.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testReadPublicScope_Services() {
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 47L)).thenReturn(service(47L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getServiceSummary(ORCID, 47L)).thenReturn(summary(47L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 48L)).thenReturn(service(48L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getServiceSummary(ORCID, 48L)).thenReturn(summary(48L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 49L)).thenReturn(service(49L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getServiceSummary(ORCID, 49L)).thenReturn(summary(49L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));

        Service failing1 = service(50L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        ServiceSummary failingSummary1 = summary(50L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 50L)).thenReturn(failing1);
        when(affiliationsManagerReadOnly.getServiceSummary(ORCID, 50L)).thenReturn(failingSummary1);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failing1, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failingSummary1, SCOPE);

        Response r = serviceDelegator.viewService(ORCID, 47L);
        assertNotNull(r);
        assertEquals(Service.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewServiceSummary(ORCID, 47L);
        assertNotNull(r);
        assertEquals(ServiceSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewService(ORCID, 48L);
        serviceDelegator.viewServiceSummary(ORCID, 48L);
        // Private that am the source of should work
        serviceDelegator.viewService(ORCID, 49L);
        serviceDelegator.viewServiceSummary(ORCID, 49L);

        // Elements the security manager refuses must come back out of the
        // delegator unwrapped, not swallowed or remapped.
        try {
            serviceDelegator.viewService(ORCID, 50L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewServiceSummary(ORCID, 50L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewService(ORCID, 50L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewServiceSummary(ORCID, 50L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddService() {
        Service toCreate = (Service) Utils.getAffiliation(AffiliationType.SERVICE);
        toCreate.setSource(clientSource(CLIENT_2));
        Service created = service(9999L, Visibility.PUBLIC, "My department name", clientSource(CLIENT_1));
        when(affiliationsManager.createServiceAffiliation(eq(ORCID), any(Service.class), eq(true))).thenReturn(created);

        Response response = serviceDelegator.createService(ORCID, toCreate);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(9999L), Utils.getPutCode(response));

        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(orcidSecurityManager).checkClientAccessAndScopes(ORCID, ScopePathType.AFFILIATIONS_CREATE, ScopePathType.AFFILIATIONS_UPDATE);

        // A client supplied source must never reach the manager.
        ArgumentCaptor<Service> captor = ArgumentCaptor.forClass(Service.class);
        verify(affiliationsManager).createServiceAffiliation(eq(ORCID), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());

        // Remove new element
        serviceDelegator.deleteAffiliation(ORCID, 9999L);
        verify(affiliationsManager).checkSourceAndDelete(ORCID, 9999L);
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddServicesDuplicateExternalIDs() {
        Service element = (Service) Utils.getAffiliation(AffiliationType.SERVICE);
        element.setExternalIDs(duplicateExternalIDs());
        doThrow(new OrcidDuplicatedActivityException(new HashMap<String, String>()))
                .when(affiliationsManager).createServiceAffiliation(eq(ORCID), any(Service.class), eq(true));

        serviceDelegator.createService(ORCID, element);
    }

    @Test
    public void testUpdateService() {
        Service stored = service(47L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 47L)).thenReturn(stored);

        Response response = serviceDelegator.viewService(ORCID, 47L);
        assertNotNull(response);
        Service element = (Service) response.getEntity();
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

        when(affiliationsManager.updateServiceAffiliation(eq(ORCID), any(Service.class), eq(true))).thenReturn(element);

        response = serviceDelegator.updateService(ORCID, 47L, element);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Service updated = (Service) response.getEntity();
        assertEquals("Updated department name", updated.getDepartmentName());
        assertEquals("The updated role title", updated.getRoleTitle());

        verify(orcidSecurityManager).checkClientAccessAndScopes(ORCID, ScopePathType.AFFILIATIONS_UPDATE);
        ArgumentCaptor<Service> captor = ArgumentCaptor.forClass(Service.class);
        verify(affiliationsManager).updateServiceAffiliation(eq(ORCID), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateServiceYouAreNotTheSourceOf() {
        Service stored = service(50L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 50L)).thenReturn(stored);
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(affiliationsManager).updateServiceAffiliation(eq(ORCID), any(Service.class), eq(true));

        Response response = serviceDelegator.viewService(ORCID, 50L);
        assertNotNull(response);
        Service element = (Service) response.getEntity();
        assertNotNull(element);
        element.setDepartmentName("Updated department name");
        element.setRoleTitle("The updated role title");
        serviceDelegator.updateService(ORCID, 50L, element);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateServiceChangingVisibilityTest() {
        Service stored = service(47L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 47L)).thenReturn(stored);
        doThrow(new VisibilityMismatchException())
                .when(affiliationsManager).updateServiceAffiliation(eq(ORCID), any(Service.class), eq(true));

        Response response = serviceDelegator.viewService(ORCID, 47L);
        assertNotNull(response);
        Service element = (Service) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());

        element.setVisibility(Visibility.PRIVATE);

        serviceDelegator.updateService(ORCID, 47L, element);
        fail();
    }

    @Test
    public void testUpdateServiceLeavingVisibilityNullTest() {
        Service stored = service(47L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 47L)).thenReturn(stored);
        // The manager restores the stored visibility when the request leaves it
        // null; that rule is proved in orcid-core, here we only check the
        // delegator returns what the manager produced.
        when(affiliationsManager.updateServiceAffiliation(eq(ORCID), any(Service.class), eq(true)))
                .thenReturn(service(47L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewService(ORCID, 47L);
        assertNotNull(response);
        Service element = (Service) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());

        element.setVisibility(null);

        response = serviceDelegator.updateService(ORCID, 47L, element);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        element = (Service) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testUpdateServiceDuplicateExternalIDs() {
        Service stored = service(47L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 47L)).thenReturn(stored);
        doThrow(new OrcidDuplicatedActivityException(new HashMap<String, String>()))
                .when(affiliationsManager).updateServiceAffiliation(eq(ORCID), any(Service.class), eq(true));

        Response response = serviceDelegator.viewService(ORCID, 47L);
        Service element = (Service) response.getEntity();
        element.setExternalIDs(duplicateExternalIDs());
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        serviceDelegator.updateService(ORCID, 47L, element);
        fail();
    }

    @Test
    public void testDeleteService() {
        Service stored = service(1006L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getServiceAffiliation("0000-0000-0000-0002", 1006L)).thenReturn(stored).thenThrow(new NoResultException());

        Response response = serviceDelegator.viewService("0000-0000-0000-0002", 1006L);
        assertNotNull(response);
        assertNotNull(response.getEntity());

        response = serviceDelegator.deleteAffiliation("0000-0000-0000-0002", 1006L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(affiliationsManager).checkSourceAndDelete("0000-0000-0000-0002", 1006L);

        try {
            serviceDelegator.viewService("0000-0000-0000-0002", 1006L);
            fail();
        } catch (NoResultException nre) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteServiceYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(affiliationsManager).checkSourceAndDelete(ORCID, 50L);

        serviceDelegator.deleteAffiliation(ORCID, 50L);
        fail();
    }
}
