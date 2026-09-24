package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.NoResultException;
import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.OtherNames;
import org.orcid.test.helper.Utils;

/**
 * The other-name endpoints of the member v2 delegator, on mocks.
 *
 * <p>
 * See {@link MemberV2ApiServiceDelegatorMockBase} for why no assertion here
 * depends on {@code checkAndFilter} having filtered anything.
 */
public class MemberV2ApiServiceDelegator_OtherNamesTest extends MemberV2ApiServiceDelegatorMockBase {

    private static final String OTHER_ORCID = "4444-4444-4444-4446";
    private static final String MY_ORCID = "4444-4444-4444-4441";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewOtherNamesWrongToken() {
        when(otherNameManagerReadOnly.getOtherNames(ORCID)).thenReturn(otherNames(otherName(9L, Visibility.PUBLIC, clientSource(CLIENT_1))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(),
                eq(ScopePathType.ORCID_BIO_READ_LIMITED));

        try {
            serviceDelegator.viewOtherNames(ORCID);
        } finally {
            verifyNoInteractions(sourceNameCacheManager);
        }
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewOtherNameWrongToken() {
        OtherName otherName = otherName(9L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(otherNameManagerReadOnly.getOtherName(ORCID, 9L)).thenReturn(otherName);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, otherName,
                ScopePathType.ORCID_BIO_READ_LIMITED);

        try {
            serviceDelegator.viewOtherName(ORCID, 9L);
        } finally {
            assertNull("the element must not be decorated once the guard has refused", otherName.getPath());
        }
    }

    @Test
    public void testViewOtherNameReadPublic() {
        OtherName otherName = otherName(9L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(otherNameManagerReadOnly.getOtherName(ORCID, 9L)).thenReturn(otherName);

        Response r = serviceDelegator.viewOtherName(ORCID, 9L);

        OtherName element = (OtherName) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/other-names/9", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, otherName, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @Test
    public void testViewOtherNamesReadPublic() {
        when(otherNameManagerReadOnly.getOtherNames(ORCID)).thenReturn(otherNames(otherName(9L, Visibility.PUBLIC, clientSource(CLIENT_1))));

        Response r = serviceDelegator.viewOtherNames(ORCID);

        OtherNames element = (OtherNames) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/other-names", element.getPath());
        assertEquals("/0000-0000-0000-0003/other-names/9", element.getOtherNames().get(0).getPath());
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(ScopePathType.ORCID_BIO_READ_LIMITED));
    }

    @Test
    public void testViewOtherNames() {
        OtherNames stored = otherNames(otherName(1L, Visibility.PUBLIC, userSource(OTHER_ORCID)), otherName(2L, Visibility.LIMITED, clientSource(CLIENT_1)),
                otherName(4L, Visibility.PRIVATE, clientSource(CLIENT_1)));
        when(otherNameManagerReadOnly.getOtherNames(OTHER_ORCID)).thenReturn(stored);

        Response response = serviceDelegator.viewOtherNames(OTHER_ORCID);

        assertNotNull(response);
        OtherNames returned = (OtherNames) response.getEntity();
        assertNotNull(returned);
        assertEquals("/4444-4444-4444-4446/other-names", returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());
        assertEquals(3, returned.getOtherNames().size());
        for (OtherName otherName : returned.getOtherNames()) {
            Utils.verifyLastModified(otherName.getLastModifiedDate());
            assertEquals("/4444-4444-4444-4446/other-names/" + otherName.getPutCode(), otherName.getPath());
        }
        assertEquals(CLIENT_1_NAME, returned.getOtherNames().get(1).getSource().getSourceName().getContent());

        // checkAndFilter edits in place, so the cached list must be copied first
        ArgumentCaptor<List<OtherName>> filtered = otherNameListCaptor();
        verify(orcidSecurityManager).checkAndFilter(eq(OTHER_ORCID), filtered.capture(), eq(ScopePathType.ORCID_BIO_READ_LIMITED));
        assertNotSame(stored.getOtherNames(), filtered.getValue());
    }

    @Test
    public void testViewPublicOtherName() {
        assertViewOtherNameDecorated(5L, Visibility.PUBLIC, userSource(OTHER_ORCID));
    }

    @Test
    public void testViewLimitedOtherName() {
        assertViewOtherNameDecorated(6L, Visibility.LIMITED, clientSource(CLIENT_1));
    }

    @Test
    public void testViewPrivateOtherName() {
        assertViewOtherNameDecorated(8L, Visibility.PRIVATE, clientSource(CLIENT_1));
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateOtherNameWhereYouAreNotTheSource() {
        OtherName otherName = otherName(7L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(otherNameManagerReadOnly.getOtherName(OTHER_ORCID, 7L)).thenReturn(otherName);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(OTHER_ORCID, otherName, ScopePathType.ORCID_BIO_READ_LIMITED);

        serviceDelegator.viewOtherName(OTHER_ORCID, 7L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewOtherNameThatDontBelongToTheUser() {
        // The (orcid, id) predicate is in OtherNameDaoImpl's query. What is
        // the delegator's is that it lets the miss out and never asks the guard
        // about an element it did not get.
        when(otherNameManagerReadOnly.getOtherName(OTHER_ORCID, 1L)).thenThrow(new NoResultException());

        try {
            serviceDelegator.viewOtherName(OTHER_ORCID, 1L);
            fail();
        } finally {
            verifyNoInteractions(orcidSecurityManager);
        }
    }

    @Test
    public void testAddOtherName() {
        OtherName created = otherName(100L, Visibility.LIMITED, clientSource(CLIENT_1));
        when(otherNameManager.createOtherName(eq(MY_ORCID), any(OtherName.class), anyBoolean())).thenReturn(created);

        Response response = serviceDelegator.createOtherName(MY_ORCID, Utils.getOtherName());

        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(100), Utils.getPutCode(response));
        verify(orcidSecurityManager).checkClientAccessAndScopes(MY_ORCID, ScopePathType.ORCID_BIO_UPDATE);
        ArgumentCaptor<OtherName> submitted = ArgumentCaptor.forClass(OtherName.class);
        verify(otherNameManager).createOtherName(eq(MY_ORCID), submitted.capture(), eq(true));
        assertNull("a client may not choose its own source", submitted.getValue().getSource());
        assertEquals(CLIENT_1_NAME, created.getSource().getSourceName().getContent());
    }

    @Test
    public void testUpdateOtherName() {
        OtherName otherName = otherName(6L, Visibility.PUBLIC, clientSource(CLIENT_1));
        otherName.setContent("Updated other name");
        OtherName updated = otherName(6L, Visibility.PUBLIC, clientSource(CLIENT_1));
        updated.setContent("Updated other name");
        when(otherNameManager.updateOtherName(eq(MY_ORCID), eq(6L), any(OtherName.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateOtherName(MY_ORCID, 6L, otherName);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        OtherName returned = (OtherName) response.getEntity();
        assertEquals("Updated other name", returned.getContent());
        assertEquals("/4444-4444-4444-4441/other-names/6", returned.getPath());
        verify(orcidSecurityManager).checkClientAccessAndScopes(MY_ORCID, ScopePathType.ORCID_BIO_UPDATE);
        ArgumentCaptor<OtherName> submitted = ArgumentCaptor.forClass(OtherName.class);
        verify(otherNameManager).updateOtherName(eq(MY_ORCID), eq(6L), submitted.capture(), eq(true));
        assertNull(submitted.getValue().getSource());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateOtherNameYouAreNotTheSourceOf() {
        // OtherNameManagerImpl calls orcidSecurityManager.checkSource on the
        // stored entity; the rule belongs to that manager's tests.
        OtherName otherName = otherName(2L, Visibility.LIMITED, clientSource(CLIENT_2));
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "other-name"))).when(otherNameManager).updateOtherName(eq(OTHER_ORCID), eq(2L),
                any(OtherName.class), anyBoolean());

        serviceDelegator.updateOtherName(OTHER_ORCID, 2L, otherName);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateOtherNameChangingVisibilityTest() {
        OtherName otherName = otherName(6L, Visibility.PRIVATE, clientSource(CLIENT_1));
        doThrow(new VisibilityMismatchException()).when(otherNameManager).updateOtherName(eq(MY_ORCID), eq(6L), any(OtherName.class), anyBoolean());

        serviceDelegator.updateOtherName(MY_ORCID, 6L, otherName);
        fail();
    }

    @Test
    public void testUpdateOtherNameLeavingVisibilityNullTest() {
        OtherName otherName = otherName(6L, null, clientSource(CLIENT_1));
        OtherName updated = otherName(6L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(otherNameManager.updateOtherName(eq(MY_ORCID), eq(6L), any(OtherName.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateOtherName(MY_ORCID, 6L, otherName);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(Visibility.PUBLIC, ((OtherName) response.getEntity()).getVisibility());
        ArgumentCaptor<OtherName> submitted = ArgumentCaptor.forClass(OtherName.class);
        verify(otherNameManager).updateOtherName(eq(MY_ORCID), eq(6L), submitted.capture(), eq(true));
        assertNull("keeping the stored visibility is the manager's job, not the delegator's", submitted.getValue().getVisibility());
    }

    @Test
    public void testDeleteOtherName() {
        Response response = serviceDelegator.deleteOtherName("4444-4444-4444-4499", 8L);

        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes("4444-4444-4444-4499", ScopePathType.ORCID_BIO_UPDATE);
        verify(otherNameManager).deleteOtherName("4444-4444-4444-4499", 8L, true);
    }

    @Test
    public void testReadPublicScope_OtherNames() {
        // Stubbed per element rather than with a blanket matcher: refusing
        // everything would also refuse 9, 10 and 11 and make the positive half of
        // this test meaningless.
        OtherName nine = otherName(9L, Visibility.PUBLIC, clientSource(CLIENT_1));
        OtherName ten = otherName(10L, Visibility.LIMITED, clientSource(CLIENT_1));
        OtherName eleven = otherName(11L, Visibility.PRIVATE, clientSource(CLIENT_1));
        OtherName twelve = otherName(12L, Visibility.LIMITED, clientSource(CLIENT_2));
        OtherName thirteen = otherName(13L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(otherNameManagerReadOnly.getOtherName(ORCID, 9L)).thenReturn(nine);
        when(otherNameManagerReadOnly.getOtherName(ORCID, 10L)).thenReturn(ten);
        when(otherNameManagerReadOnly.getOtherName(ORCID, 11L)).thenReturn(eleven);
        when(otherNameManagerReadOnly.getOtherName(ORCID, 12L)).thenReturn(twelve);
        when(otherNameManagerReadOnly.getOtherName(ORCID, 13L)).thenReturn(thirteen);
        when(otherNameManagerReadOnly.getOtherNames(ORCID)).thenReturn(otherNames(nine, ten, eleven));
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, twelve, ScopePathType.ORCID_BIO_READ_LIMITED);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, thirteen, ScopePathType.ORCID_BIO_READ_LIMITED);

        Response r = serviceDelegator.viewOtherNames(ORCID);
        assertNotNull(r);
        assertEquals(OtherNames.class.getName(), r.getEntity().getClass().getName());
        OtherNames k = (OtherNames) r.getEntity();
        assertEquals("/0000-0000-0000-0003/other-names", k.getPath());
        Utils.verifyLastModified(k.getLastModifiedDate());
        assertEquals(3, k.getOtherNames().size());

        r = serviceDelegator.viewOtherName(ORCID, 9L);
        assertNotNull(r);
        assertEquals(OtherName.class.getName(), r.getEntity().getClass().getName());

        // Limited where am the source should work
        serviceDelegator.viewOtherName(ORCID, 10L);

        try {
            // Limited am not the source should fail
            serviceDelegator.viewOtherName(ORCID, 12L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private where am the source should work
        serviceDelegator.viewOtherName(ORCID, 11L);
        try {
            // Private am not the source should fail
            serviceDelegator.viewOtherName(ORCID, 13L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteOtherNameYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "other-name"))).when(otherNameManager).deleteOtherName(OTHER_ORCID, 3L, true);

        serviceDelegator.deleteOtherName(OTHER_ORCID, 3L);
        fail();
    }

    // ------------------------------------------------------------- helpers

    private void assertViewOtherNameDecorated(long putCode, Visibility visibility, Source source) {
        OtherName otherName = otherName(putCode, visibility, source);
        when(otherNameManagerReadOnly.getOtherName(OTHER_ORCID, putCode)).thenReturn(otherName);

        Response response = serviceDelegator.viewOtherName(OTHER_ORCID, putCode);

        assertNotNull(response);
        OtherName returned = (OtherName) response.getEntity();
        assertNotNull(returned);
        assertEquals("/4444-4444-4444-4446/other-names/" + putCode, returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());
        assertEquals(visibility, returned.getVisibility());
        verify(orcidSecurityManager).checkAndFilter(OTHER_ORCID, otherName, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<OtherName>> otherNameListCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    private OtherName otherName(Long putCode, Visibility visibility, Source source) {
        OtherName otherName = new OtherName();
        otherName.setPutCode(putCode);
        otherName.setContent("Other name " + putCode);
        otherName.setVisibility(visibility);
        otherName.setSource(source);
        otherName.setCreatedDate(createdDate());
        otherName.setLastModifiedDate(lastModified());
        return otherName;
    }

    private OtherNames otherNames(OtherName... elements) {
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<>(Arrays.asList(elements)));
        otherNames.setLastModifiedDate(lastModified());
        return otherNames;
    }
}
