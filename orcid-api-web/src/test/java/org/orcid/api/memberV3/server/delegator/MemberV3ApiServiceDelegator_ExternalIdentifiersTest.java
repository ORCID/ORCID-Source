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
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the external-identifier endpoints of the member V3
 * API.
 *
 * <p>
 * {@code checkAndFilter} is void and filters in place, so a mocked security
 * manager filters nothing; the visibility tables these tests used to exercise
 * are proved in orcid-core by {@code OrcidSecurityManager_generalTest}. What is
 * asserted here is the delegator's own contract, plus a {@code verify} that the
 * element was handed to the security manager with the right scope.
 */
public class MemberV3ApiServiceDelegator_ExternalIdentifiersTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final ScopePathType SCOPE = ScopePathType.ORCID_BIO_READ_LIMITED;

    private static final String USER_4442 = "4444-4444-4444-4442";
    private static final String USER_4443 = "4444-4444-4444-4443";
    private static final String USER_4444 = "4444-4444-4444-4444";

    private PersonExternalIdentifier extId(long putCode, String type, String value, Visibility visibility, Source source) {
        PersonExternalIdentifier element = new PersonExternalIdentifier();
        element.setPutCode(putCode);
        element.setType(type);
        element.setValue(value);
        element.setUrl(new Url("http://www.facebook.com/" + value));
        element.setRelationship(Relationship.SELF);
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        element.setCreatedDate(created());
        return element;
    }

    private PersonExternalIdentifiers extIds(PersonExternalIdentifier... elements) {
        PersonExternalIdentifiers container = new PersonExternalIdentifiers();
        container.setExternalIdentifiers(new ArrayList<>(Arrays.asList(elements)));
        return container;
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewExternalIdentifiersWrongToken() {
        when(externalIdentifierManagerReadOnly.getExternalIdentifiers(ORCID))
                .thenReturn(extIds(extId(13L, "Facebook", "abc123", Visibility.PUBLIC, clientSource(CLIENT_1))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));

        serviceDelegator.viewExternalIdentifiers(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewExternalIdentifierWrongToken() {
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 13L))
                .thenReturn(extId(13L, "Facebook", "abc123", Visibility.PUBLIC, clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(PersonExternalIdentifier.class), eq(SCOPE));

        serviceDelegator.viewExternalIdentifier(ORCID, 13L);
    }

    @Test
    public void testViewExternalIdentifierReadPublic() {
        PersonExternalIdentifier stored = extId(13L, "Facebook", "abc123", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 13L)).thenReturn(stored);

        Response r = serviceDelegator.viewExternalIdentifier(ORCID, 13L);
        PersonExternalIdentifier element = (PersonExternalIdentifier) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/external-identifiers/13", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewExternalIdentifiersReadPublic() {
        when(externalIdentifierManagerReadOnly.getExternalIdentifiers(ORCID))
                .thenReturn(extIds(extId(13L, "Facebook", "abc123", Visibility.PUBLIC, clientSource(CLIENT_1))));

        Response r = serviceDelegator.viewExternalIdentifiers(ORCID);
        PersonExternalIdentifiers element = (PersonExternalIdentifiers) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/external-identifiers", element.getPath());
        assertEquals("/0000-0000-0000-0003/external-identifiers/13", element.getExternalIdentifiers().get(0).getPath());
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testViewExternalIdentifiers() {
        when(externalIdentifierManagerReadOnly.getExternalIdentifiers(USER_4442)).thenReturn(extIds(
                extId(2L, "Facebook", "abc123", Visibility.PUBLIC, clientSource(CLIENT_1)),
                extId(3L, "Facebook", "abc456", Visibility.LIMITED, userSource(USER_4442)),
                extId(5L, "Facebook", "abc012", Visibility.PRIVATE, clientSource(CLIENT_1))));

        Response response = serviceDelegator.viewExternalIdentifiers(USER_4442);
        assertNotNull(response);
        PersonExternalIdentifiers extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertEquals("/4444-4444-4444-4442/external-identifiers", extIds.getPath());
        Utils.verifyLastModified(extIds.getLastModifiedDate());
        assertEquals(3, extIds.getExternalIdentifiers().size());

        for (PersonExternalIdentifier extId : extIds.getExternalIdentifiers()) {
            Utils.verifyLastModified(extId.getLastModifiedDate());
            assertEquals("Facebook", extId.getType());
            if (extId.getPutCode() == 2L) {
                assertEquals(Visibility.PUBLIC, extId.getVisibility());
                assertEquals(CLIENT_1, extId.getSource().retrieveSourcePath());
            } else if (extId.getPutCode() == 3L) {
                assertEquals(Visibility.LIMITED, extId.getVisibility());
                assertEquals(USER_4442, extId.getSource().retrieveSourcePath());
            } else {
                assertEquals(Visibility.PRIVATE, extId.getVisibility());
                assertEquals(CLIENT_1, extId.getSource().retrieveSourcePath());
            }
        }
        verify(orcidSecurityManager).checkAndFilter(eq(USER_4442), anyList(), eq(SCOPE));
    }

    @Test
    public void testViewPublicExternalIdentifier() {
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(USER_4442, 2L))
                .thenReturn(extId(2L, "Facebook", "abc123", Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewExternalIdentifier(USER_4442, 2L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("/4444-4444-4444-4442/external-identifiers/2", extId.getPath());
        Utils.verifyLastModified(extId.getLastModifiedDate());
        assertEquals("Facebook", extId.getType());
        assertEquals("abc123", extId.getValue());
        assertEquals("http://www.facebook.com/abc123", extId.getUrl().getValue());
        assertEquals(Visibility.PUBLIC, extId.getVisibility());
        assertEquals(CLIENT_1, extId.getSource().retrieveSourcePath());
        verify(orcidSecurityManager).checkAndFilter(USER_4442, extId, SCOPE);
    }

    @Test
    public void testViewLimitedExternalIdentifier() {
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(USER_4442, 3L))
                .thenReturn(extId(3L, "Facebook", "abc456", Visibility.LIMITED, userSource(USER_4442)));

        Response response = serviceDelegator.viewExternalIdentifier(USER_4442, 3L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("/4444-4444-4444-4442/external-identifiers/3", extId.getPath());
        Utils.verifyLastModified(extId.getLastModifiedDate());
        assertEquals("Facebook", extId.getType());
        assertEquals("abc456", extId.getValue());
        assertEquals("http://www.facebook.com/abc456", extId.getUrl().getValue());
        assertEquals(Visibility.LIMITED, extId.getVisibility());
        assertEquals(USER_4442, extId.getSource().retrieveSourcePath());
        verify(orcidSecurityManager).checkAndFilter(USER_4442, extId, SCOPE);
    }

    @Test
    public void testViewPrivateExternalIdentifier() {
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(USER_4442, 5L))
                .thenReturn(extId(5L, "Facebook", "abc012", Visibility.PRIVATE, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewExternalIdentifier(USER_4442, 5L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("/4444-4444-4444-4442/external-identifiers/5", extId.getPath());
        Utils.verifyLastModified(extId.getLastModifiedDate());
        assertEquals("Facebook", extId.getType());
        assertEquals("abc012", extId.getValue());
        assertEquals("http://www.facebook.com/abc012", extId.getUrl().getValue());
        assertEquals(Visibility.PRIVATE, extId.getVisibility());
        assertEquals(CLIENT_1, extId.getSource().retrieveSourcePath());
        verify(orcidSecurityManager).checkAndFilter(USER_4442, extId, SCOPE);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateExternalIdentifierWhereYouAreNotTheSource() {
        PersonExternalIdentifier stored = extId(4L, "Facebook", "abc789", Visibility.PRIVATE, clientSource(CLIENT_2));
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(USER_4442, 4L)).thenReturn(stored);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(USER_4442, stored, SCOPE);

        serviceDelegator.viewExternalIdentifier(USER_4442, 4L);
        fail();
    }

    /**
     * The rule this proves -- that external identifier 1 cannot be read through
     * record 4442 -- lives in a SQL WHERE clause
     * ({@code ExternalIdentifierDaoImpl.getExternalIdentifier}), so with a mocked
     * manager only the pass-through survives here. The predicate itself is proved
     * by MemberV3ApiServiceDelegatorDatabaseRulesTest in the db-tests stage.
     */
    @Test(expected = NoResultException.class)
    public void testViewExternalIdentifierThatDontBelongToTheUser() {
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(USER_4442, 1L)).thenThrow(new NoResultException());

        serviceDelegator.viewExternalIdentifier(USER_4442, 1L);
        fail();
    }

    @Test
    public void testAddExternalIdentifier() {
        PersonExternalIdentifier existing = extId(10L, "Facebook", "d3clan", Visibility.PUBLIC, clientSource(CLIENT_1));
        PersonExternalIdentifier created = extId(1000L, "new-common-name", "new-reference", Visibility.LIMITED, clientSource(CLIENT_1));
        created.setUrl(new Url("http://newUrl.com"));
        when(externalIdentifierManagerReadOnly.getExternalIdentifiers(USER_4443)).thenReturn(extIds(existing)).thenReturn(extIds(existing, created));
        when(externalIdentifierManager.createExternalIdentifier(eq(USER_4443), any(PersonExternalIdentifier.class), eq(true))).thenReturn(created);

        Response response = serviceDelegator.viewExternalIdentifiers(USER_4443);
        assertNotNull(response);
        PersonExternalIdentifiers extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertEquals(1, extIds.getExternalIdentifiers().size());
        assertEquals("http://www.facebook.com/d3clan", extIds.getExternalIdentifiers().get(0).getUrl().getValue());
        assertEquals("d3clan", extIds.getExternalIdentifiers().get(0).getValue());

        PersonExternalIdentifier toCreate = Utils.getPersonExternalIdentifier();
        // Planted so that assertNull below proves clearSource ran, rather than
        // only proving the fixture never had a source to begin with.
        toCreate.setSource(clientSource(CLIENT_2));

        response = serviceDelegator.createExternalIdentifier(USER_4443, toCreate);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(1000L), Utils.getPutCode(response));

        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4443, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
        // A client supplied source must never reach the manager.
        ArgumentCaptor<PersonExternalIdentifier> captor = ArgumentCaptor.forClass(PersonExternalIdentifier.class);
        verify(externalIdentifierManager).createExternalIdentifier(eq(USER_4443), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());

        response = serviceDelegator.viewExternalIdentifiers(USER_4443);
        assertNotNull(response);
        extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertEquals(2, extIds.getExternalIdentifiers().size());
        boolean foundNew = false, foundExisting = false;
        for (PersonExternalIdentifier extId : extIds.getExternalIdentifiers()) {
            if (extId.getPutCode() == 1000L) {
                assertEquals("new-common-name", extId.getType());
                assertEquals("new-reference", extId.getValue());
                assertEquals("http://newUrl.com", extId.getUrl().getValue());
                foundNew = true;
            } else {
                assertEquals("Facebook", extId.getType());
                assertEquals("d3clan", extId.getValue());
                assertEquals("http://www.facebook.com/d3clan", extId.getUrl().getValue());
                foundExisting = true;
            }
        }
        assertTrue(foundNew);
        assertTrue(foundExisting);
    }

    @Test
    public void testUpdateExternalIdentifier() {
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(USER_4442, 2L))
                .thenReturn(extId(2L, "Facebook", "abc123", Visibility.PUBLIC, clientSource(CLIENT_1)));
        PersonExternalIdentifier updated = extId(2L, "updated-common-name", "updated-reference", Visibility.PUBLIC, clientSource(CLIENT_1));
        updated.setUrl(new Url("http://updatedUrl.com"));
        when(externalIdentifierManager.updateExternalIdentifier(eq(USER_4442), any(PersonExternalIdentifier.class), eq(true))).thenReturn(updated);

        Response response = serviceDelegator.viewExternalIdentifier(USER_4442, 2L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("Facebook", extId.getType());
        assertEquals("abc123", extId.getValue());
        assertEquals("http://www.facebook.com/abc123", extId.getUrl().getValue());

        extId.setType("updated-common-name");
        extId.setValue("updated-reference");
        extId.setUrl(new Url("http://updatedUrl.com"));

        response = serviceDelegator.updateExternalIdentifier(USER_4442, 2L, extId);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        PersonExternalIdentifier updatedExtId = (PersonExternalIdentifier) response.getEntity();
        assertEquals("updated-common-name", updatedExtId.getType());
        assertEquals("updated-reference", updatedExtId.getValue());
        assertEquals("http://updatedUrl.com", updatedExtId.getUrl().getValue());
        assertEquals("/4444-4444-4444-4442/external-identifiers/2", updatedExtId.getPath());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4442, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateExaternalIdentifierYouAreNotTheSourceOf() {
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(USER_4442, 3L))
                .thenReturn(extId(3L, "Facebook", "abc456", Visibility.LIMITED, userSource(USER_4442)));
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(externalIdentifierManager).updateExternalIdentifier(eq(USER_4442), any(PersonExternalIdentifier.class), eq(true));

        Response response = serviceDelegator.viewExternalIdentifier(USER_4442, 3L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("Facebook", extId.getType());
        assertEquals("abc456", extId.getValue());
        assertEquals("http://www.facebook.com/abc456", extId.getUrl().getValue());

        extId.setValue("updated-reference");

        serviceDelegator.updateExternalIdentifier(USER_4442, 3L, extId);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateExternalIdentifierChangingVisibilityTest() {
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(USER_4442, 2L))
                .thenReturn(extId(2L, "Facebook", "abc123", Visibility.PUBLIC, clientSource(CLIENT_1)));
        doThrow(new VisibilityMismatchException())
                .when(externalIdentifierManager).updateExternalIdentifier(eq(USER_4442), any(PersonExternalIdentifier.class), eq(true));

        Response response = serviceDelegator.viewExternalIdentifier(USER_4442, 2L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals(Visibility.PUBLIC, extId.getVisibility());

        extId.setVisibility(Visibility.PRIVATE);

        serviceDelegator.updateExternalIdentifier(USER_4442, 2L, extId);
        fail();
    }

    @Test
    public void testUpdateExternalIdentifierLeavingVisibilityNullTest() {
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(USER_4442, 2L))
                .thenReturn(extId(2L, "Facebook", "abc123", Visibility.PUBLIC, clientSource(CLIENT_1)));
        // Restoring the stored visibility is the manager's job and is proved
        // there; here the delegator must simply return what it produced.
        when(externalIdentifierManager.updateExternalIdentifier(eq(USER_4442), any(PersonExternalIdentifier.class), eq(true)))
                .thenReturn(extId(2L, "Facebook", "abc123", Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewExternalIdentifier(USER_4442, 2L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals(Visibility.PUBLIC, extId.getVisibility());

        extId.setVisibility(null);

        response = serviceDelegator.updateExternalIdentifier(USER_4442, 2L, extId);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals(Visibility.PUBLIC, extId.getVisibility());
        // Catches a delegator that sets a visibility on the element before handing it to
        // the manager: what is submitted must still carry the null the request arrived with.
        ArgumentCaptor<PersonExternalIdentifier> submitted = ArgumentCaptor.forClass(PersonExternalIdentifier.class);
        verify(externalIdentifierManager).updateExternalIdentifier(eq(USER_4442), submitted.capture(), eq(true));
        assertNull("keeping the stored visibility is the manager's job, not the delegator's", submitted.getValue().getVisibility());
    }

    @Test
    public void testDeleteExternalIdentifier() {
        when(externalIdentifierManagerReadOnly.getExternalIdentifiers(USER_4444))
                .thenReturn(extIds(extId(6L, "Facebook", "the-only-one", Visibility.PUBLIC, clientSource(CLIENT_1)))).thenReturn(extIds());

        Response response = serviceDelegator.viewExternalIdentifiers(USER_4444);
        assertNotNull(response);
        PersonExternalIdentifiers extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertEquals(1, extIds.getExternalIdentifiers().size());

        response = serviceDelegator.deleteExternalIdentifier(USER_4444, 6L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4444, ScopePathType.ORCID_BIO_UPDATE);
        verify(externalIdentifierManager).deleteExternalIdentifier(USER_4444, 6L, true);

        response = serviceDelegator.viewExternalIdentifiers(USER_4444);
        assertNotNull(response);
        extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertTrue(extIds.getExternalIdentifiers().isEmpty());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteExternalIdentifierYouAreNotTheSourceOf() {
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(USER_4442, 3L))
                .thenReturn(extId(3L, "Facebook", "abc456", Visibility.LIMITED, userSource(USER_4442)));
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(externalIdentifierManager).deleteExternalIdentifier(USER_4442, 3L, true);

        Response response = serviceDelegator.viewExternalIdentifier(USER_4442, 3L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("Facebook", extId.getType());
        assertEquals("abc456", extId.getValue());
        assertEquals("http://www.facebook.com/abc456", extId.getUrl().getValue());

        serviceDelegator.deleteExternalIdentifier(USER_4442, 3L);
        fail();
    }

    @Test
    public void testReadPublicScope_ExternalIdentifiers() {
        when(externalIdentifierManagerReadOnly.getExternalIdentifiers(ORCID)).thenReturn(extIds(
                extId(13L, "Facebook", "abc123", Visibility.PUBLIC, clientSource(CLIENT_1)),
                extId(14L, "Facebook", "abc456", Visibility.LIMITED, clientSource(CLIENT_1)),
                extId(15L, "Facebook", "abc789", Visibility.PRIVATE, clientSource(CLIENT_1))));
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 13L))
                .thenReturn(extId(13L, "Facebook", "abc123", Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 14L))
                .thenReturn(extId(14L, "Facebook", "abc456", Visibility.LIMITED, clientSource(CLIENT_1)));
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 15L))
                .thenReturn(extId(15L, "Facebook", "abc789", Visibility.PRIVATE, clientSource(CLIENT_1)));

        PersonExternalIdentifier limitedOtherSource = extId(16L, "Facebook", "self-limited", Visibility.LIMITED, userSource(ORCID));
        PersonExternalIdentifier privateOtherSource = extId(17L, "Facebook", "self-private", Visibility.PRIVATE, userSource(ORCID));
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 16L)).thenReturn(limitedOtherSource);
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 17L)).thenReturn(privateOtherSource);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, limitedOtherSource, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, privateOtherSource, SCOPE);

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
            Utils.verifyLastModified(element.getLastModifiedDate());
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

        // Limited where am the source of should work
        serviceDelegator.viewExternalIdentifier(ORCID, 14L);
        // Limited where am not the source of should fail
        try {
            serviceDelegator.viewExternalIdentifier(ORCID, 16L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }
        // Private where am the source of should work
        serviceDelegator.viewExternalIdentifier(ORCID, 15L);
        // Private where am not the source of should fail
        try {
            serviceDelegator.viewExternalIdentifier(ORCID, 17L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }
    }
}
