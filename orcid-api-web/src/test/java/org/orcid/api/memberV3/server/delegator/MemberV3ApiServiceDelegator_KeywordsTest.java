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
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the keyword endpoints of the member V3 API.
 *
 * <p>
 * {@code OrcidSecurityManager.checkAndFilter} is void and filters the collection
 * in place, so with a mocked security manager nothing is filtered and an
 * assertion of the form "only the public keywords came back" would hold without
 * proving anything. Those tables are proved in orcid-core by
 * {@code OrcidSecurityManager_generalTest}; what is asserted here is the
 * delegator's own contract -- which manager it asks, what it hands the security
 * manager, the path it stamps on the result, and the status it returns.
 */
public class MemberV3ApiServiceDelegator_KeywordsTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final ScopePathType SCOPE = ScopePathType.ORCID_BIO_READ_LIMITED;

    private static final String USER_4443 = "4444-4444-4444-4443";
    private static final String USER_4441 = "4444-4444-4444-4441";
    private static final String USER_4499 = "4444-4444-4444-4499";

    private Keyword keyword(long putCode, String content, Visibility visibility, Source source) {
        Keyword keyword = new Keyword();
        keyword.setPutCode(putCode);
        keyword.setContent(content);
        keyword.setVisibility(visibility);
        keyword.setSource(source);
        keyword.setLastModifiedDate(lastModified());
        return keyword;
    }

    private Keywords keywords(Keyword... elements) {
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<>(Arrays.asList(elements)));
        return keywords;
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewKeywordsWrongToken() {
        when(profileKeywordManagerReadOnly.getKeywords(ORCID))
                .thenReturn(keywords(keyword(9L, "keyword-9", Visibility.PUBLIC, clientSource(CLIENT_1))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));

        serviceDelegator.viewKeywords(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewKeywordWrongToken() {
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 9L)).thenReturn(keyword(9L, "keyword-9", Visibility.PUBLIC, clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Keyword.class), eq(SCOPE));

        serviceDelegator.viewKeyword(ORCID, 9L);
    }

    @Test
    public void testViewKeywordReadPublic() {
        Keyword stored = keyword(9L, "keyword-9", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 9L)).thenReturn(stored);

        Response r = serviceDelegator.viewKeyword(ORCID, 9L);
        Keyword element = (Keyword) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/keywords/9", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewKeywordsReadPublic() {
        Keywords stored = keywords(keyword(9L, "keyword-9", Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(profileKeywordManagerReadOnly.getKeywords(ORCID)).thenReturn(stored);

        Response r = serviceDelegator.viewKeywords(ORCID);
        Keywords element = (Keywords) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/keywords", element.getPath());
        assertEquals("/0000-0000-0000-0003/keywords/9", element.getKeywords().get(0).getPath());
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testViewKeywords() {
        Keywords stored = keywords(keyword(1L, "tea making", Visibility.PUBLIC, clientSource(CLIENT_1)),
                keyword(2L, "coffee making", Visibility.LIMITED, userSource(USER_4443)),
                keyword(4L, "what else can we make?", Visibility.PRIVATE, clientSource(CLIENT_1)));
        when(profileKeywordManagerReadOnly.getKeywords(USER_4443)).thenReturn(stored);

        Response response = serviceDelegator.viewKeywords(USER_4443);
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
                assertEquals(CLIENT_1, keyword.getSource().retrieveSourcePath());
            } else if (keyword.getPutCode() == 2L) {
                assertEquals(Visibility.LIMITED, keyword.getVisibility());
                assertEquals(USER_4443, keyword.getSource().retrieveSourcePath());
            } else {
                assertEquals(Visibility.PRIVATE, keyword.getVisibility());
                assertEquals(CLIENT_1, keyword.getSource().retrieveSourcePath());
            }
        }
        verify(orcidSecurityManager).checkAndFilter(eq(USER_4443), anyList(), eq(SCOPE));
    }

    @Test
    public void testViewPublicKeyword() {
        when(profileKeywordManagerReadOnly.getKeyword(USER_4443, 1L)).thenReturn(keyword(1L, "tea making", Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewKeyword(USER_4443, 1L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("/4444-4444-4444-4443/keywords/1", keyword.getPath());
        Utils.verifyLastModified(keyword.getLastModifiedDate());
        assertEquals("tea making", keyword.getContent());
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());
        assertEquals(CLIENT_1, keyword.getSource().retrieveSourcePath());
        verify(orcidSecurityManager).checkAndFilter(USER_4443, keyword, SCOPE);
    }

    @Test
    public void testViewLimitedKeyword() {
        when(profileKeywordManagerReadOnly.getKeyword(USER_4443, 2L)).thenReturn(keyword(2L, "coffee making", Visibility.LIMITED, userSource(USER_4443)));

        Response response = serviceDelegator.viewKeyword(USER_4443, 2L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("/4444-4444-4444-4443/keywords/2", keyword.getPath());
        Utils.verifyLastModified(keyword.getLastModifiedDate());
        assertEquals("coffee making", keyword.getContent());
        assertEquals(Visibility.LIMITED, keyword.getVisibility());
        assertEquals(USER_4443, keyword.getSource().retrieveSourcePath());
        verify(orcidSecurityManager).checkAndFilter(USER_4443, keyword, SCOPE);
    }

    @Test
    public void testViewPrivateKeyword() {
        when(profileKeywordManagerReadOnly.getKeyword(USER_4443, 4L))
                .thenReturn(keyword(4L, "what else can we make?", Visibility.PRIVATE, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewKeyword(USER_4443, 4L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("/4444-4444-4444-4443/keywords/4", keyword.getPath());
        Utils.verifyLastModified(keyword.getLastModifiedDate());
        assertEquals("what else can we make?", keyword.getContent());
        assertEquals(Visibility.PRIVATE, keyword.getVisibility());
        assertEquals(CLIENT_1, keyword.getSource().retrieveSourcePath());
        verify(orcidSecurityManager).checkAndFilter(USER_4443, keyword, SCOPE);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateKeywordWhereYouAreNotTheSource() {
        Keyword stored = keyword(3L, "chocolat making", Visibility.PRIVATE, userSource(USER_4443));
        when(profileKeywordManagerReadOnly.getKeyword(USER_4443, 3L)).thenReturn(stored);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(USER_4443, stored, SCOPE);

        serviceDelegator.viewKeyword(USER_4443, 3L);
        fail();
    }

    /**
     * Despite the name this drives viewOtherName, and the rule it proves -- that
     * other name 5 cannot be read through record 4443 -- lives in a SQL WHERE
     * clause ({@code OtherNameDaoImpl.getOtherName}). With a mocked manager only
     * the pass-through survives here; the predicate itself is proved by
     * MemberV3ApiServiceDelegatorDatabaseRulesTest in the db-tests stage.
     */
    @Test(expected = NoResultException.class)
    public void testViewKeywordThatDontBelongToTheUser() {
        when(otherNameManagerReadOnly.getOtherName(USER_4443, 5L)).thenThrow(new NoResultException());

        serviceDelegator.viewOtherName(USER_4443, 5L);
        fail();
    }

    @Test
    public void testAddKeyword() {
        Keyword created = keyword(1000L, "New keyword", Visibility.PUBLIC, clientSource(CLIENT_1));
        created.setCreatedDate(created());
        when(profileKeywordManager.createKeyword(eq(USER_4441), any(Keyword.class), eq(true))).thenReturn(created);
        when(profileKeywordManagerReadOnly.getKeyword(USER_4441, 1000L)).thenReturn(created);

        Keyword toCreate = Utils.getKeyword();
        // Planted so that assertNull below proves clearSource ran, rather than
        // only proving the fixture never had a source to begin with.
        toCreate.setSource(clientSource(CLIENT_2));

        Response response = serviceDelegator.createKeyword(USER_4441, toCreate);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = Utils.getPutCode(response);
        assertEquals(Long.valueOf(1000L), putCode);

        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4441, ScopePathType.ORCID_BIO_UPDATE);
        // A client supplied source must never reach the manager.
        ArgumentCaptor<Keyword> captor = ArgumentCaptor.forClass(Keyword.class);
        verify(profileKeywordManager).createKeyword(eq(USER_4441), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());

        response = serviceDelegator.viewKeyword(USER_4441, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Keyword newKeyword = (Keyword) response.getEntity();
        assertNotNull(newKeyword);
        Utils.verifyLastModified(newKeyword.getLastModifiedDate());
        assertEquals("New keyword", newKeyword.getContent());
        assertEquals(Visibility.PUBLIC, newKeyword.getVisibility());
        assertNotNull(newKeyword.getSource());
        assertEquals(CLIENT_1, newKeyword.getSource().retrieveSourcePath());
        assertNotNull(newKeyword.getCreatedDate());
    }

    @Test
    public void testUpdateKeyword() {
        Keyword stored = keyword(6L, "keyword-2", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(profileKeywordManagerReadOnly.getKeyword(USER_4441, 6L)).thenReturn(stored);

        Response response = serviceDelegator.viewKeyword(USER_4441, 6L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        Utils.verifyLastModified(keyword.getLastModifiedDate());
        assertEquals("keyword-2", keyword.getContent());
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());

        keyword.setContent("Updated keyword");
        Keyword updated = keyword(6L, "Updated keyword", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(profileKeywordManager.updateKeyword(eq(USER_4441), eq(6L), any(Keyword.class), eq(true))).thenReturn(updated);

        response = serviceDelegator.updateKeyword(USER_4441, 6L, keyword);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        Utils.verifyLastModified(keyword.getLastModifiedDate());
        assertEquals("Updated keyword", keyword.getContent());
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());
        assertEquals("/4444-4444-4444-4441/keywords/6", keyword.getPath());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4441, ScopePathType.ORCID_BIO_UPDATE);
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateKeywordYouAreNotTheSourceOf() {
        Keyword stored = keyword(2L, "coffee making", Visibility.LIMITED, userSource(USER_4443));
        when(profileKeywordManagerReadOnly.getKeyword(USER_4443, 2L)).thenReturn(stored);
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(profileKeywordManager).updateKeyword(eq(USER_4443), eq(2L), any(Keyword.class), eq(true));

        Response response = serviceDelegator.viewKeyword(USER_4443, 2L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("coffee making", keyword.getContent());
        assertEquals(Visibility.LIMITED, keyword.getVisibility());
        assertNotNull(keyword.getSource());
        assertEquals(USER_4443, keyword.getSource().retrieveSourcePath());

        keyword.setContent("Updated Keyword " + System.currentTimeMillis());

        serviceDelegator.updateKeyword(USER_4443, 2L, keyword);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateKeywordChangingVisibilityTest() {
        when(profileKeywordManagerReadOnly.getKeyword(USER_4441, 6L)).thenReturn(keyword(6L, "keyword-2", Visibility.PUBLIC, clientSource(CLIENT_1)));
        doThrow(new VisibilityMismatchException())
                .when(profileKeywordManager).updateKeyword(eq(USER_4441), eq(6L), any(Keyword.class), eq(true));

        Response response = serviceDelegator.viewKeyword(USER_4441, 6L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());

        keyword.setVisibility(Visibility.PRIVATE);

        serviceDelegator.updateKeyword(USER_4441, 6L, keyword);
        fail();
    }

    @Test
    public void testUpdateKeywordLeavingVisibilityNullTest() {
        when(profileKeywordManagerReadOnly.getKeyword(USER_4441, 6L)).thenReturn(keyword(6L, "keyword-2", Visibility.PUBLIC, clientSource(CLIENT_1)));
        // The manager restores the stored visibility when the request leaves it
        // null; that rule belongs to ProfileKeywordManager and is proved there.
        when(profileKeywordManager.updateKeyword(eq(USER_4441), eq(6L), any(Keyword.class), eq(true)))
                .thenReturn(keyword(6L, "keyword-2", Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewKeyword(USER_4441, 6L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());

        keyword.setVisibility(null);

        response = serviceDelegator.updateKeyword(USER_4441, 6L, keyword);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());
        // Catches a delegator that sets a visibility on the element before handing it to
        // the manager: what is submitted must still carry the null the request arrived with.
        ArgumentCaptor<Keyword> submitted = ArgumentCaptor.forClass(Keyword.class);
        verify(profileKeywordManager).updateKeyword(eq(USER_4441), eq(6L), submitted.capture(), eq(true));
        assertNull("keeping the stored visibility is the manager's job, not the delegator's", submitted.getValue().getVisibility());
    }

    @Test
    public void testDeleteKeyword() {
        when(profileKeywordManagerReadOnly.getKeywords(USER_4499))
                .thenReturn(keywords(keyword(8L, "the only keyword", Visibility.PUBLIC, clientSource(CLIENT_1))))
                .thenReturn(keywords());

        Response response = serviceDelegator.viewKeywords(USER_4499);
        assertNotNull(response);
        Keywords keywords = (Keywords) response.getEntity();
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());
        assertEquals(1, keywords.getKeywords().size());

        response = serviceDelegator.deleteKeyword(USER_4499, 8L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4499, ScopePathType.ORCID_BIO_UPDATE);
        verify(profileKeywordManager).deleteKeyword(USER_4499, 8L, true);

        response = serviceDelegator.viewKeywords(USER_4499);
        assertNotNull(response);
        keywords = (Keywords) response.getEntity();
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());
        assertTrue(keywords.getKeywords().isEmpty());
    }

    @Test
    public void testReadPublicScope_Keywords() {
        Keywords stored = keywords(keyword(9L, "keyword-9", Visibility.PUBLIC, clientSource(CLIENT_1)),
                keyword(10L, "keyword-10", Visibility.LIMITED, clientSource(CLIENT_1)),
                keyword(11L, "keyword-11", Visibility.PRIVATE, clientSource(CLIENT_1)));
        when(profileKeywordManagerReadOnly.getKeywords(ORCID)).thenReturn(stored);
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 9L)).thenReturn(keyword(9L, "keyword-9", Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 10L)).thenReturn(keyword(10L, "keyword-10", Visibility.LIMITED, clientSource(CLIENT_1)));
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 11L)).thenReturn(keyword(11L, "keyword-11", Visibility.PRIVATE, clientSource(CLIENT_1)));

        Keyword limitedOtherSource = keyword(12L, "keyword-12", Visibility.LIMITED, userSource(ORCID));
        Keyword privateOtherSource = keyword(13L, "keyword-13", Visibility.PRIVATE, userSource(ORCID));
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 12L)).thenReturn(limitedOtherSource);
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 13L)).thenReturn(privateOtherSource);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, limitedOtherSource, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, privateOtherSource, SCOPE);

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
        doThrow(new WrongSourceException(new HashMap<String, String>())).when(profileKeywordManager).deleteKeyword(USER_4443, 3L, true);

        serviceDelegator.deleteKeyword(USER_4443, 3L);
        fail();
    }
}
