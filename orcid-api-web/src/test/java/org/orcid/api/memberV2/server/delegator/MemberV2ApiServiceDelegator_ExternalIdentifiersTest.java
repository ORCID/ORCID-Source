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
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.test.helper.Utils;

/**
 * The person external identifier endpoints of the member v2 delegator, on mocks.
 *
 * <p>
 * See {@link MemberV2ApiServiceDelegatorMockBase} for why no assertion here
 * depends on {@code checkAndFilter} having filtered anything.
 *
 * <p>
 * Note the scope asymmetry that the delegator actually implements: creating and
 * updating an external identifier is guarded by
 * {@code ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE}, while deleting one is guarded
 * by the broader {@code ORCID_BIO_UPDATE}.
 */
public class MemberV2ApiServiceDelegator_ExternalIdentifiersTest extends MemberV2ApiServiceDelegatorMockBase {

    private static final String OTHER_ORCID = "4444-4444-4444-4442";
    private static final String MY_ORCID = "4444-4444-4444-4443";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewExternalIdentifiersWrongToken() {
        when(externalIdentifierManagerReadOnly.getExternalIdentifiers(ORCID)).thenReturn(extIds(extId(13L, Visibility.PUBLIC, clientSource(CLIENT_1))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(),
                eq(ScopePathType.ORCID_BIO_READ_LIMITED));

        try {
            serviceDelegator.viewExternalIdentifiers(ORCID);
        } finally {
            verifyNoInteractions(sourceNameCacheManager);
        }
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewExternalIdentifierWrongToken() {
        PersonExternalIdentifier extId = extId(13L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 13L)).thenReturn(extId);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, extId,
                ScopePathType.ORCID_BIO_READ_LIMITED);

        try {
            serviceDelegator.viewExternalIdentifier(ORCID, 13L);
        } finally {
            assertNull("the element must not be decorated once the guard has refused", extId.getPath());
        }
    }

    @Test
    public void testViewExternalIdentifierReadPublic() {
        PersonExternalIdentifier extId = extId(13L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 13L)).thenReturn(extId);

        Response r = serviceDelegator.viewExternalIdentifier(ORCID, 13L);

        PersonExternalIdentifier element = (PersonExternalIdentifier) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/external-identifiers/13", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, extId, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @Test
    public void testViewExternalIdentifiersReadPublic() {
        when(externalIdentifierManagerReadOnly.getExternalIdentifiers(ORCID)).thenReturn(extIds(extId(13L, Visibility.PUBLIC, clientSource(CLIENT_1))));

        Response r = serviceDelegator.viewExternalIdentifiers(ORCID);

        PersonExternalIdentifiers element = (PersonExternalIdentifiers) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/external-identifiers", element.getPath());
        assertEquals("/0000-0000-0000-0003/external-identifiers/13", element.getExternalIdentifiers().get(0).getPath());
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(ScopePathType.ORCID_BIO_READ_LIMITED));
    }

    @Test
    public void testViewExternalIdentifiers() {
        PersonExternalIdentifiers stored = extIds(extId(2L, Visibility.PUBLIC, userSource(OTHER_ORCID)), extId(3L, Visibility.LIMITED, clientSource(CLIENT_1)),
                extId(5L, Visibility.PRIVATE, clientSource(CLIENT_1)));
        when(externalIdentifierManagerReadOnly.getExternalIdentifiers(OTHER_ORCID)).thenReturn(stored);

        Response response = serviceDelegator.viewExternalIdentifiers(OTHER_ORCID);

        assertNotNull(response);
        PersonExternalIdentifiers returned = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(returned);
        assertEquals("/4444-4444-4444-4442/external-identifiers", returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());
        assertEquals(3, returned.getExternalIdentifiers().size());
        for (PersonExternalIdentifier extId : returned.getExternalIdentifiers()) {
            Utils.verifyLastModified(extId.getLastModifiedDate());
            assertEquals("/4444-4444-4444-4442/external-identifiers/" + extId.getPutCode(), extId.getPath());
        }
        assertEquals(CLIENT_1_NAME, returned.getExternalIdentifiers().get(1).getSource().getSourceName().getContent());

        // checkAndFilter edits in place, so the cached list must be copied first
        ArgumentCaptor<List<PersonExternalIdentifier>> filtered = extIdListCaptor();
        verify(orcidSecurityManager).checkAndFilter(eq(OTHER_ORCID), filtered.capture(), eq(ScopePathType.ORCID_BIO_READ_LIMITED));
        assertNotSame(stored.getExternalIdentifiers(), filtered.getValue());
    }

    @Test
    public void testViewPublicExternalIdentifier() {
        assertViewExternalIdentifierDecorated(2L, Visibility.PUBLIC, userSource(OTHER_ORCID));
    }

    @Test
    public void testViewLimitedExternalIdentifier() {
        assertViewExternalIdentifierDecorated(3L, Visibility.LIMITED, clientSource(CLIENT_1));
    }

    @Test
    public void testViewPrivateExternalIdentifier() {
        assertViewExternalIdentifierDecorated(5L, Visibility.PRIVATE, clientSource(CLIENT_1));
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateExternalIdentifierWhereYouAreNotTheSource() {
        PersonExternalIdentifier extId = extId(4L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(OTHER_ORCID, 4L)).thenReturn(extId);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(OTHER_ORCID, extId, ScopePathType.ORCID_BIO_READ_LIMITED);

        serviceDelegator.viewExternalIdentifier(OTHER_ORCID, 4L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewExternalIdentifierThatDontBelongToTheUser() {
        // The (orcid, id) predicate is in ExternalIdentifierDaoImpl's query. What
        // is the delegator's is that it lets the miss out and never asks the
        // guard about an element it did not get.
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(OTHER_ORCID, 1L)).thenThrow(new NoResultException());

        try {
            serviceDelegator.viewExternalIdentifier(OTHER_ORCID, 1L);
            fail();
        } finally {
            verifyNoInteractions(orcidSecurityManager);
        }
    }

    @Test
    public void testAddExternalIdentifier() {
        PersonExternalIdentifier created = extId(100L, Visibility.LIMITED, clientSource(CLIENT_1));
        when(externalIdentifierManager.createExternalIdentifier(eq(MY_ORCID), any(PersonExternalIdentifier.class), anyBoolean())).thenReturn(created);

        Response response = serviceDelegator.createExternalIdentifier(MY_ORCID, Utils.getPersonExternalIdentifier());

        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(100), Utils.getPutCode(response));
        verify(orcidSecurityManager).checkClientAccessAndScopes(MY_ORCID, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
        ArgumentCaptor<PersonExternalIdentifier> submitted = ArgumentCaptor.forClass(PersonExternalIdentifier.class);
        verify(externalIdentifierManager).createExternalIdentifier(eq(MY_ORCID), submitted.capture(), eq(true));
        assertNull("a client may not choose its own source", submitted.getValue().getSource());
    }

    @Test
    public void testUpdateExternalIdentifier() {
        PersonExternalIdentifier extId = extId(2L, Visibility.PUBLIC, clientSource(CLIENT_1));
        extId.setValue("updated-value");
        PersonExternalIdentifier updated = extId(2L, Visibility.PUBLIC, clientSource(CLIENT_1));
        updated.setValue("updated-value");
        when(externalIdentifierManager.updateExternalIdentifier(eq(OTHER_ORCID), any(PersonExternalIdentifier.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateExternalIdentifier(OTHER_ORCID, 2L, extId);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        PersonExternalIdentifier returned = (PersonExternalIdentifier) response.getEntity();
        assertEquals("updated-value", returned.getValue());
        assertEquals("/4444-4444-4444-4442/external-identifiers/2", returned.getPath());
        verify(orcidSecurityManager).checkClientAccessAndScopes(OTHER_ORCID, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
        ArgumentCaptor<PersonExternalIdentifier> submitted = ArgumentCaptor.forClass(PersonExternalIdentifier.class);
        verify(externalIdentifierManager).updateExternalIdentifier(eq(OTHER_ORCID), submitted.capture(), eq(true));
        assertNull(submitted.getValue().getSource());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateExaternalIdentifierYouAreNotTheSourceOf() {
        // ExternalIdentifierManagerImpl calls orcidSecurityManager.checkSource on
        // the stored entity; the rule belongs to that manager's tests.
        PersonExternalIdentifier extId = extId(3L, Visibility.LIMITED, clientSource(CLIENT_2));
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "external-identifier"))).when(externalIdentifierManager)
                .updateExternalIdentifier(eq(OTHER_ORCID), any(PersonExternalIdentifier.class), anyBoolean());

        serviceDelegator.updateExternalIdentifier(OTHER_ORCID, 3L, extId);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateExternalIdentifierChangingVisibilityTest() {
        PersonExternalIdentifier extId = extId(2L, Visibility.PRIVATE, clientSource(CLIENT_1));
        doThrow(new VisibilityMismatchException()).when(externalIdentifierManager).updateExternalIdentifier(eq(OTHER_ORCID),
                any(PersonExternalIdentifier.class), anyBoolean());

        serviceDelegator.updateExternalIdentifier(OTHER_ORCID, 2L, extId);
        fail();
    }

    @Test
    public void testUpdateExternalIdentifierLeavingVisibilityNullTest() {
        PersonExternalIdentifier extId = extId(2L, null, clientSource(CLIENT_1));
        PersonExternalIdentifier updated = extId(2L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(externalIdentifierManager.updateExternalIdentifier(eq(OTHER_ORCID), any(PersonExternalIdentifier.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateExternalIdentifier(OTHER_ORCID, 2L, extId);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(Visibility.PUBLIC, ((PersonExternalIdentifier) response.getEntity()).getVisibility());
        ArgumentCaptor<PersonExternalIdentifier> submitted = ArgumentCaptor.forClass(PersonExternalIdentifier.class);
        verify(externalIdentifierManager).updateExternalIdentifier(eq(OTHER_ORCID), submitted.capture(), eq(true));
        assertNull("keeping the stored visibility is the manager's job, not the delegator's", submitted.getValue().getVisibility());
    }

    @Test
    public void testDeleteExternalIdentifier() {
        Response response = serviceDelegator.deleteExternalIdentifier("4444-4444-4444-4444", 6L);

        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes("4444-4444-4444-4444", ScopePathType.ORCID_BIO_UPDATE);
        verify(externalIdentifierManager).deleteExternalIdentifier("4444-4444-4444-4444", 6L, true);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteExternalIdentifierYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "external-identifier"))).when(externalIdentifierManager)
                .deleteExternalIdentifier(OTHER_ORCID, 3L, true);

        serviceDelegator.deleteExternalIdentifier(OTHER_ORCID, 3L);
        fail();
    }

    @Test
    public void testReadPublicScope_ExternalIdentifiers() {
        // Stubbed per element rather than with a blanket matcher: refusing
        // everything would also refuse 13, 14 and 15 and make the positive half
        // of this test meaningless.
        PersonExternalIdentifier thirteen = extId(13L, Visibility.PUBLIC, clientSource(CLIENT_1));
        PersonExternalIdentifier fourteen = extId(14L, Visibility.LIMITED, clientSource(CLIENT_1));
        PersonExternalIdentifier fifteen = extId(15L, Visibility.PRIVATE, clientSource(CLIENT_1));
        PersonExternalIdentifier sixteen = extId(16L, Visibility.LIMITED, clientSource(CLIENT_2));
        PersonExternalIdentifier seventeen = extId(17L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 13L)).thenReturn(thirteen);
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 14L)).thenReturn(fourteen);
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 15L)).thenReturn(fifteen);
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 16L)).thenReturn(sixteen);
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 17L)).thenReturn(seventeen);
        when(externalIdentifierManagerReadOnly.getExternalIdentifiers(ORCID)).thenReturn(extIds(thirteen, fourteen, fifteen));
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, sixteen, ScopePathType.ORCID_BIO_READ_LIMITED);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, seventeen, ScopePathType.ORCID_BIO_READ_LIMITED);

        Response r = serviceDelegator.viewExternalIdentifiers(ORCID);
        assertNotNull(r);
        assertEquals(PersonExternalIdentifiers.class.getName(), r.getEntity().getClass().getName());
        PersonExternalIdentifiers extIds = (PersonExternalIdentifiers) r.getEntity();
        assertEquals("/0000-0000-0000-0003/external-identifiers", extIds.getPath());
        Utils.verifyLastModified(extIds.getLastModifiedDate());
        assertEquals(3, extIds.getExternalIdentifiers().size());

        r = serviceDelegator.viewExternalIdentifier(ORCID, 13L);
        assertNotNull(r);
        assertEquals(PersonExternalIdentifier.class.getName(), r.getEntity().getClass().getName());

        // Limited where am the source should work
        serviceDelegator.viewExternalIdentifier(ORCID, 14L);

        try {
            // Limited am not the source should fail
            serviceDelegator.viewExternalIdentifier(ORCID, 16L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private where am the source should work
        serviceDelegator.viewExternalIdentifier(ORCID, 15L);
        try {
            // Private am not the source should fail
            serviceDelegator.viewExternalIdentifier(ORCID, 17L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    // ------------------------------------------------------------- helpers

    private void assertViewExternalIdentifierDecorated(long putCode, Visibility visibility, Source source) {
        PersonExternalIdentifier extId = extId(putCode, visibility, source);
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(OTHER_ORCID, putCode)).thenReturn(extId);

        Response response = serviceDelegator.viewExternalIdentifier(OTHER_ORCID, putCode);

        assertNotNull(response);
        PersonExternalIdentifier returned = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(returned);
        assertEquals("/4444-4444-4444-4442/external-identifiers/" + putCode, returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());
        assertEquals(visibility, returned.getVisibility());
        verify(orcidSecurityManager).checkAndFilter(OTHER_ORCID, extId, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<PersonExternalIdentifier>> extIdListCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    private PersonExternalIdentifier extId(Long putCode, Visibility visibility, Source source) {
        PersonExternalIdentifier extId = new PersonExternalIdentifier();
        extId.setPutCode(putCode);
        extId.setType("type-" + putCode);
        extId.setValue("value-" + putCode);
        extId.setUrl(new Url("http://extId.com/" + putCode));
        extId.setRelationship(Relationship.SELF);
        extId.setVisibility(visibility);
        extId.setSource(source);
        extId.setCreatedDate(createdDate());
        extId.setLastModifiedDate(lastModified());
        return extId;
    }

    private PersonExternalIdentifiers extIds(PersonExternalIdentifier... elements) {
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<>(Arrays.asList(elements)));
        extIds.setLastModifiedDate(lastModified());
        return extIds;
    }
}
