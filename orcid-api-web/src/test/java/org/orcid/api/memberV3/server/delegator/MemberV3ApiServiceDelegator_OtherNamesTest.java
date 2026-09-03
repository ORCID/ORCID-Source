package org.orcid.api.memberV3.server.delegator;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import jakarta.persistence.NoResultException;
import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the other-name endpoints of the member V3 API.
 *
 * <p>
 * {@code checkAndFilter} is void and filters in place, so with a mocked security
 * manager nothing is filtered; assertions that used to prove "only the public
 * other names came back" would now hold vacuously. They are replaced by a
 * {@code verify} that the collection really was handed to the security manager
 * with the right scope. The visibility tables themselves live in orcid-core's
 * {@code OrcidSecurityManager_generalTest}.
 */
public class MemberV3ApiServiceDelegator_OtherNamesTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final ScopePathType SCOPE = ScopePathType.ORCID_BIO_READ_LIMITED;

    private static final String USER_4441 = "4444-4444-4444-4441";
    private static final String USER_4443 = "4444-4444-4444-4443";
    private static final String USER_4446 = "4444-4444-4444-4446";
    private static final String USER_4447 = "4444-4444-4444-4447";

    private OtherName otherName(long putCode, String content, Visibility visibility, Source source) {
        OtherName element = new OtherName();
        element.setPutCode(putCode);
        element.setContent(content);
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        element.setCreatedDate(created());
        return element;
    }

    private OtherNames otherNames(OtherName... elements) {
        OtherNames container = new OtherNames();
        container.setOtherNames(new ArrayList<>(Arrays.asList(elements)));
        return container;
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewOtherNamesWrongToken() {
        when(otherNameManagerReadOnly.getOtherNames(ORCID))
                .thenReturn(otherNames(otherName(13L, "Other Name PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));

        serviceDelegator.viewOtherNames(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewOtherNameWrongToken() {
        when(otherNameManagerReadOnly.getOtherName(ORCID, 13L)).thenReturn(otherName(13L, "Other Name PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(OtherName.class), eq(SCOPE));

        serviceDelegator.viewOtherName(ORCID, 13L);
    }

    @Test
    public void testViewOtherNameReadPublic() {
        OtherName stored = otherName(13L, "Other Name PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(otherNameManagerReadOnly.getOtherName(ORCID, 13L)).thenReturn(stored);

        Response r = serviceDelegator.viewOtherName(ORCID, 13L);
        OtherName element = (OtherName) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/other-names/13", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewOtherNamesReadPublic() {
        when(otherNameManagerReadOnly.getOtherNames(ORCID))
                .thenReturn(otherNames(otherName(13L, "Other Name PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1))));

        Response r = serviceDelegator.viewOtherNames(ORCID);
        OtherNames element = (OtherNames) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/other-names", element.getPath());
        assertEquals("/0000-0000-0000-0003/other-names/13", element.getOtherNames().get(0).getPath());
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testViewOtherNames() {
        when(otherNameManagerReadOnly.getOtherNames(USER_4446)).thenReturn(otherNames(
                otherName(5L, "Other Name # 1", Visibility.PUBLIC, clientSource(CLIENT_1)),
                otherName(6L, "Other Name # 2", Visibility.LIMITED, userSource(USER_4446)),
                otherName(8L, "Other Name # 4", Visibility.PRIVATE, clientSource(CLIENT_1))));

        Response response = serviceDelegator.viewOtherNames(USER_4446);
        assertNotNull(response);
        OtherNames otherNames = (OtherNames) response.getEntity();
        assertNotNull(otherNames);
        assertEquals("/4444-4444-4444-4446/other-names", otherNames.getPath());
        Utils.verifyLastModified(otherNames.getLastModifiedDate());
        assertNotNull(otherNames.getOtherNames());
        assertEquals(3, otherNames.getOtherNames().size());

        for (OtherName otherName : otherNames.getOtherNames()) {
            Utils.verifyLastModified(otherName.getLastModifiedDate());
            assertThat(otherName.getPutCode(), anyOf(is(5L), is(6L), is(8L)));
            assertThat(otherName.getContent(), anyOf(is("Other Name # 1"), is("Other Name # 2"), is("Other Name # 4")));
            if (otherName.getPutCode() == 5L) {
                assertEquals(Visibility.PUBLIC, otherName.getVisibility());
                assertEquals(CLIENT_1, otherName.getSource().retrieveSourcePath());
            } else if (otherName.getPutCode() == 6L) {
                assertEquals(Visibility.LIMITED, otherName.getVisibility());
                assertEquals(USER_4446, otherName.getSource().retrieveSourcePath());
            } else {
                assertEquals(Visibility.PRIVATE, otherName.getVisibility());
                assertEquals(CLIENT_1, otherName.getSource().retrieveSourcePath());
            }
        }
        verify(orcidSecurityManager).checkAndFilter(eq(USER_4446), anyList(), eq(SCOPE));
    }

    @Test
    public void testViewPublicOtherName() {
        when(otherNameManagerReadOnly.getOtherName(USER_4446, 5L)).thenReturn(otherName(5L, "Other Name # 1", Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewOtherName(USER_4446, 5L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals("/4444-4444-4444-4446/other-names/5", otherName.getPath());
        Utils.verifyLastModified(otherName.getLastModifiedDate());
        assertEquals("Other Name # 1", otherName.getContent());
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());
        assertEquals(CLIENT_1, otherName.getSource().retrieveSourcePath());
        verify(orcidSecurityManager).checkAndFilter(USER_4446, otherName, SCOPE);
    }

    @Test
    public void testViewLimitedOtherName() {
        when(otherNameManagerReadOnly.getOtherName(USER_4446, 6L)).thenReturn(otherName(6L, "Other Name # 2", Visibility.LIMITED, userSource(USER_4446)));

        Response response = serviceDelegator.viewOtherName(USER_4446, 6L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals("/4444-4444-4444-4446/other-names/6", otherName.getPath());
        Utils.verifyLastModified(otherName.getLastModifiedDate());
        assertEquals("Other Name # 2", otherName.getContent());
        assertEquals(Visibility.LIMITED, otherName.getVisibility());
        assertEquals(USER_4446, otherName.getSource().retrieveSourcePath());
        verify(orcidSecurityManager).checkAndFilter(USER_4446, otherName, SCOPE);
    }

    @Test
    public void testViewPrivateOtherName() {
        when(otherNameManagerReadOnly.getOtherName(USER_4446, 8L)).thenReturn(otherName(8L, "Other Name # 4", Visibility.PRIVATE, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewOtherName(USER_4446, 8L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals("/4444-4444-4444-4446/other-names/8", otherName.getPath());
        Utils.verifyLastModified(otherName.getLastModifiedDate());
        assertEquals("Other Name # 4", otherName.getContent());
        assertEquals(Visibility.PRIVATE, otherName.getVisibility());
        assertEquals(CLIENT_1, otherName.getSource().retrieveSourcePath());
        verify(orcidSecurityManager).checkAndFilter(USER_4446, otherName, SCOPE);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateOtherNameWhereYouAreNotTheSource() {
        OtherName stored = otherName(7L, "Other Name # 3", Visibility.PRIVATE, clientSource(CLIENT_2));
        when(otherNameManagerReadOnly.getOtherName(USER_4446, 7L)).thenReturn(stored);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(USER_4446, stored, SCOPE);

        serviceDelegator.viewOtherName(USER_4446, 7L);
        fail();
    }

    /**
     * The rule this proves -- that other name 1 cannot be read through record
     * 4446 -- lives in a SQL WHERE clause ({@code OtherNameDaoImpl.getOtherName}),
     * so with a mocked manager only the pass-through survives here. The predicate
     * itself is proved by MemberV3ApiServiceDelegatorDatabaseRulesTest, which
     * runs in the db-tests stage.
     */
    @Test(expected = NoResultException.class)
    public void testViewOtherNameThatDontBelongToTheUser() {
        when(otherNameManagerReadOnly.getOtherName(USER_4446, 1L)).thenThrow(new NoResultException());

        serviceDelegator.viewOtherName(USER_4446, 1L);
        fail();
    }

    @Test
    public void testAddOtherName() {
        OtherName created = otherName(1000L, "New Other Name", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(otherNameManager.createOtherName(eq(USER_4441), any(OtherName.class), eq(true))).thenReturn(created);
        when(otherNameManagerReadOnly.getOtherName(USER_4441, 1000L)).thenReturn(created);

        OtherName toCreate = Utils.getOtherName();
        // Planted so that assertNull below proves clearSource ran, rather than
        // only proving the fixture never had a source to begin with.
        toCreate.setSource(clientSource(CLIENT_2));

        Response response = serviceDelegator.createOtherName(USER_4441, toCreate);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = Utils.getPutCode(response);
        assertEquals(Long.valueOf(1000L), putCode);

        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4441, ScopePathType.ORCID_BIO_UPDATE);
        // A client supplied source must never reach the manager.
        ArgumentCaptor<OtherName> captor = ArgumentCaptor.forClass(OtherName.class);
        verify(otherNameManager).createOtherName(eq(USER_4441), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());

        response = serviceDelegator.viewOtherName(USER_4441, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        OtherName newOtherName = (OtherName) response.getEntity();
        assertNotNull(newOtherName);
        Utils.verifyLastModified(newOtherName.getLastModifiedDate());
        assertEquals("New Other Name", newOtherName.getContent());
        assertEquals(Visibility.PUBLIC, newOtherName.getVisibility());
        assertNotNull(newOtherName.getSource());
        assertEquals(CLIENT_1, newOtherName.getSource().retrieveSourcePath());
        assertNotNull(newOtherName.getCreatedDate());
    }

    @Test
    public void testUpdateOtherName() {
        when(otherNameManagerReadOnly.getOtherName(USER_4443, 1L)).thenReturn(otherName(1L, "Slibberdy Slabinah", Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(otherNameManager.updateOtherName(eq(USER_4443), eq(1L), any(OtherName.class), eq(true)))
                .thenReturn(otherName(1L, "Updated Other Name", Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewOtherName(USER_4443, 1L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        Utils.verifyLastModified(otherName.getLastModifiedDate());
        assertEquals("Slibberdy Slabinah", otherName.getContent());
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());

        otherName.setContent("Updated Other Name");

        response = serviceDelegator.updateOtherName(USER_4443, 1L, otherName);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        OtherName updatedOtherName = (OtherName) response.getEntity();
        assertNotNull(updatedOtherName);
        Utils.verifyLastModified(updatedOtherName.getLastModifiedDate());
        assertEquals("Updated Other Name", updatedOtherName.getContent());
        assertEquals(Visibility.PUBLIC, updatedOtherName.getVisibility());
        assertEquals("/4444-4444-4444-4443/other-names/1", updatedOtherName.getPath());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4443, ScopePathType.ORCID_BIO_UPDATE);
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateOtherNameYouAreNotTheSourceOf() {
        when(otherNameManagerReadOnly.getOtherName(USER_4443, 2L)).thenReturn(otherName(2L, "Flibberdy Flabinah", Visibility.PUBLIC, clientSource(CLIENT_2)));
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(otherNameManager).updateOtherName(eq(USER_4443), eq(2L), any(OtherName.class), eq(true));

        Response response = serviceDelegator.viewOtherName(USER_4443, 2L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals("Flibberdy Flabinah", otherName.getContent());
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());

        otherName.setContent("Updated Other Name " + System.currentTimeMillis());

        serviceDelegator.updateOtherName(USER_4443, 2L, otherName);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateOtherNameChangingVisibilityTest() {
        when(otherNameManagerReadOnly.getOtherName(USER_4443, 1L)).thenReturn(otherName(1L, "Slibberdy Slabinah", Visibility.PUBLIC, clientSource(CLIENT_1)));
        doThrow(new VisibilityMismatchException())
                .when(otherNameManager).updateOtherName(eq(USER_4443), eq(1L), any(OtherName.class), eq(true));

        Response response = serviceDelegator.viewOtherName(USER_4443, 1L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());

        otherName.setVisibility(Visibility.PRIVATE);

        serviceDelegator.updateOtherName(USER_4443, 1L, otherName);
        fail();
    }

    @Test
    public void testUpdateOtherNameLeavingVisibilityNullTest() {
        when(otherNameManagerReadOnly.getOtherName(USER_4443, 1L)).thenReturn(otherName(1L, "Slibberdy Slabinah", Visibility.PUBLIC, clientSource(CLIENT_1)));
        // Restoring the stored visibility is the manager's job and is proved
        // there; here the delegator must simply return what it produced.
        when(otherNameManager.updateOtherName(eq(USER_4443), eq(1L), any(OtherName.class), eq(true)))
                .thenReturn(otherName(1L, "Slibberdy Slabinah", Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewOtherName(USER_4443, 1L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());

        otherName.setVisibility(null);

        response = serviceDelegator.updateOtherName(USER_4443, 1L, otherName);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());
    }

    @Test
    public void testDeleteOtherName() {
        when(otherNameManagerReadOnly.getOtherNames(USER_4447))
                .thenReturn(otherNames(otherName(9L, "the only other name", Visibility.PUBLIC, clientSource(CLIENT_1))))
                .thenReturn(otherNames());

        Response response = serviceDelegator.viewOtherNames(USER_4447);
        assertNotNull(response);
        OtherNames otherNames = (OtherNames) response.getEntity();
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());
        assertEquals(1, otherNames.getOtherNames().size());

        response = serviceDelegator.deleteOtherName(USER_4447, 9L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4447, ScopePathType.ORCID_BIO_UPDATE);
        verify(otherNameManager).deleteOtherName(USER_4447, 9L, true);

        response = serviceDelegator.viewOtherNames(USER_4447);
        assertNotNull(response);
        otherNames = (OtherNames) response.getEntity();
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());
        assertTrue(otherNames.getOtherNames().isEmpty());
    }

    @Test
    public void testReadPublicScope_OtherNames() {
        when(otherNameManagerReadOnly.getOtherNames(ORCID)).thenReturn(otherNames(
                otherName(13L, "Other Name PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1)),
                otherName(14L, "Other Name LIMITED", Visibility.LIMITED, clientSource(CLIENT_1)),
                otherName(15L, "Other Name PRIVATE", Visibility.PRIVATE, clientSource(CLIENT_1))));
        when(otherNameManagerReadOnly.getOtherName(ORCID, 13L)).thenReturn(otherName(13L, "Other Name PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(otherNameManagerReadOnly.getOtherName(ORCID, 14L)).thenReturn(otherName(14L, "Other Name LIMITED", Visibility.LIMITED, clientSource(CLIENT_1)));
        when(otherNameManagerReadOnly.getOtherName(ORCID, 15L)).thenReturn(otherName(15L, "Other Name PRIVATE", Visibility.PRIVATE, clientSource(CLIENT_1)));

        OtherName limitedOtherSource = otherName(16L, "Other Name SELF LIMITED", Visibility.LIMITED, userSource(ORCID));
        OtherName privateOtherSource = otherName(17L, "Other Name SELF PRIVATE", Visibility.PRIVATE, userSource(ORCID));
        when(otherNameManagerReadOnly.getOtherName(ORCID, 16L)).thenReturn(limitedOtherSource);
        when(otherNameManagerReadOnly.getOtherName(ORCID, 17L)).thenReturn(privateOtherSource);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, limitedOtherSource, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, privateOtherSource, SCOPE);

        Response r = serviceDelegator.viewOtherNames(ORCID);
        assertNotNull(r);
        assertEquals(OtherNames.class.getName(), r.getEntity().getClass().getName());
        OtherNames o = (OtherNames) r.getEntity();
        assertNotNull(o);
        Utils.verifyLastModified(o.getLastModifiedDate());
        assertEquals(3, o.getOtherNames().size());
        boolean found1 = false, found2 = false, found3 = false;
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

        r = serviceDelegator.viewOtherName(ORCID, 13L);
        assertNotNull(r);
        assertEquals(OtherName.class.getName(), r.getEntity().getClass().getName());

        // Limited where am the source should work
        serviceDelegator.viewOtherName(ORCID, 14L);
        // Limited where am not the source of should fail
        try {
            serviceDelegator.viewOtherName(ORCID, 16L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }
        // Private where am the source should work
        serviceDelegator.viewOtherName(ORCID, 15L);
        // Private where am not the source should fail
        try {
            serviceDelegator.viewOtherName(ORCID, 17L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteOtherNameYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(new HashMap<String, String>())).when(otherNameManager).deleteOtherName(USER_4446, 6L, true);

        serviceDelegator.deleteOtherName(USER_4446, 6L);
        fail();
    }
}
