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
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.test.helper.Utils;

/**
 * The keyword endpoints of the member v2 delegator, on mocks.
 *
 * <p>
 * See {@link MemberV2ApiServiceDelegatorMockBase} for why no assertion here
 * depends on {@code checkAndFilter} having filtered anything.
 */
public class MemberV2ApiServiceDelegator_KeywordsTest extends MemberV2ApiServiceDelegatorMockBase {

    private static final String OTHER_ORCID = "4444-4444-4444-4443";
    private static final String MY_ORCID = "4444-4444-4444-4441";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewKeywordsWrongToken() {
        when(profileKeywordManagerReadOnly.getKeywords(ORCID)).thenReturn(keywords(keyword(9L, Visibility.PUBLIC, clientSource(CLIENT_1))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(),
                eq(ScopePathType.ORCID_BIO_READ_LIMITED));

        try {
            serviceDelegator.viewKeywords(ORCID);
        } finally {
            verifyNoInteractions(sourceNameCacheManager);
        }
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewKeywordWrongToken() {
        Keyword keyword = keyword(9L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 9L)).thenReturn(keyword);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, keyword,
                ScopePathType.ORCID_BIO_READ_LIMITED);

        try {
            serviceDelegator.viewKeyword(ORCID, 9L);
        } finally {
            assertNull("the element must not be decorated once the guard has refused", keyword.getPath());
        }
    }

    @Test
    public void testViewKeywordReadPublic() {
        Keyword keyword = keyword(9L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 9L)).thenReturn(keyword);

        Response r = serviceDelegator.viewKeyword(ORCID, 9L);

        Keyword element = (Keyword) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/keywords/9", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, keyword, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @Test
    public void testViewKeywordsReadPublic() {
        when(profileKeywordManagerReadOnly.getKeywords(ORCID)).thenReturn(keywords(keyword(9L, Visibility.PUBLIC, clientSource(CLIENT_1))));

        Response r = serviceDelegator.viewKeywords(ORCID);

        Keywords element = (Keywords) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/keywords", element.getPath());
        assertEquals("/0000-0000-0000-0003/keywords/9", element.getKeywords().get(0).getPath());
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(ScopePathType.ORCID_BIO_READ_LIMITED));
    }

    @Test
    public void testViewKeywords() {
        Keywords stored = keywords(keyword(1L, Visibility.PUBLIC, userSource(OTHER_ORCID)), keyword(2L, Visibility.LIMITED, clientSource(CLIENT_1)),
                keyword(4L, Visibility.PRIVATE, clientSource(CLIENT_1)));
        when(profileKeywordManagerReadOnly.getKeywords(OTHER_ORCID)).thenReturn(stored);

        Response response = serviceDelegator.viewKeywords(OTHER_ORCID);

        assertNotNull(response);
        Keywords returned = (Keywords) response.getEntity();
        assertNotNull(returned);
        assertEquals("/4444-4444-4444-4443/keywords", returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());
        assertEquals(3, returned.getKeywords().size());
        for (Keyword keyword : returned.getKeywords()) {
            Utils.verifyLastModified(keyword.getLastModifiedDate());
            assertEquals("/4444-4444-4444-4443/keywords/" + keyword.getPutCode(), keyword.getPath());
        }
        assertEquals(CLIENT_1_NAME, returned.getKeywords().get(1).getSource().getSourceName().getContent());

        // checkAndFilter edits in place, so the cached list must be copied first
        ArgumentCaptor<List<Keyword>> filtered = keywordListCaptor();
        verify(orcidSecurityManager).checkAndFilter(eq(OTHER_ORCID), filtered.capture(), eq(ScopePathType.ORCID_BIO_READ_LIMITED));
        assertNotSame(stored.getKeywords(), filtered.getValue());
    }

    @Test
    public void testViewPublicKeyword() {
        assertViewKeywordDecorated(1L, Visibility.PUBLIC, userSource(OTHER_ORCID));
    }

    @Test
    public void testViewLimitedKeyword() {
        assertViewKeywordDecorated(2L, Visibility.LIMITED, clientSource(CLIENT_1));
    }

    @Test
    public void testViewPrivateKeyword() {
        assertViewKeywordDecorated(4L, Visibility.PRIVATE, clientSource(CLIENT_1));
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateKeywordWhereYouAreNotTheSource() {
        Keyword keyword = keyword(3L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(profileKeywordManagerReadOnly.getKeyword(OTHER_ORCID, 3L)).thenReturn(keyword);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(OTHER_ORCID, keyword, ScopePathType.ORCID_BIO_READ_LIMITED);

        serviceDelegator.viewKeyword(OTHER_ORCID, 3L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewKeywordThatDontBelongToTheUser() {
        // The (orcid, id) predicate is in ProfileKeywordDaoImpl's query. What is
        // the delegator's is that it lets the miss out and never asks the guard
        // about an element it did not get.
        when(otherNameManagerReadOnly.getOtherName(OTHER_ORCID, 5L)).thenThrow(new NoResultException());

        try {
            serviceDelegator.viewOtherName(OTHER_ORCID, 5L);
            fail();
        } finally {
            verifyNoInteractions(orcidSecurityManager);
        }
    }

    @Test
    public void testAddKeyword() {
        Keyword created = keyword(100L, Visibility.LIMITED, clientSource(CLIENT_1));
        when(profileKeywordManager.createKeyword(eq(MY_ORCID), any(Keyword.class), anyBoolean())).thenReturn(created);

        Response response = serviceDelegator.createKeyword(MY_ORCID, Utils.getKeyword());

        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(100), Utils.getPutCode(response));
        verify(orcidSecurityManager).checkClientAccessAndScopes(MY_ORCID, ScopePathType.ORCID_BIO_UPDATE);
        ArgumentCaptor<Keyword> submitted = ArgumentCaptor.forClass(Keyword.class);
        verify(profileKeywordManager).createKeyword(eq(MY_ORCID), submitted.capture(), eq(true));
        assertNull("a client may not choose its own source", submitted.getValue().getSource());
        assertEquals(CLIENT_1_NAME, created.getSource().getSourceName().getContent());
    }

    @Test
    public void testUpdateKeyword() {
        Keyword keyword = keyword(6L, Visibility.PUBLIC, clientSource(CLIENT_1));
        keyword.setContent("Updated keyword");
        Keyword updated = keyword(6L, Visibility.PUBLIC, clientSource(CLIENT_1));
        updated.setContent("Updated keyword");
        when(profileKeywordManager.updateKeyword(eq(MY_ORCID), eq(6L), any(Keyword.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateKeyword(MY_ORCID, 6L, keyword);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Keyword returned = (Keyword) response.getEntity();
        assertEquals("Updated keyword", returned.getContent());
        assertEquals("/4444-4444-4444-4441/keywords/6", returned.getPath());
        verify(orcidSecurityManager).checkClientAccessAndScopes(MY_ORCID, ScopePathType.ORCID_BIO_UPDATE);
        ArgumentCaptor<Keyword> submitted = ArgumentCaptor.forClass(Keyword.class);
        verify(profileKeywordManager).updateKeyword(eq(MY_ORCID), eq(6L), submitted.capture(), eq(true));
        assertNull(submitted.getValue().getSource());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateKeywordYouAreNotTheSourceOf() {
        // ProfileKeywordManagerImpl calls orcidSecurityManager.checkSource on the
        // stored entity; the rule belongs to that manager's tests.
        Keyword keyword = keyword(2L, Visibility.LIMITED, clientSource(CLIENT_2));
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "keyword"))).when(profileKeywordManager).updateKeyword(eq(OTHER_ORCID), eq(2L),
                any(Keyword.class), anyBoolean());

        serviceDelegator.updateKeyword(OTHER_ORCID, 2L, keyword);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateKeywordChangingVisibilityTest() {
        Keyword keyword = keyword(6L, Visibility.PRIVATE, clientSource(CLIENT_1));
        doThrow(new VisibilityMismatchException()).when(profileKeywordManager).updateKeyword(eq(MY_ORCID), eq(6L), any(Keyword.class), anyBoolean());

        serviceDelegator.updateKeyword(MY_ORCID, 6L, keyword);
        fail();
    }

    @Test
    public void testUpdateKeywordLeavingVisibilityNullTest() {
        Keyword keyword = keyword(6L, null, clientSource(CLIENT_1));
        Keyword updated = keyword(6L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(profileKeywordManager.updateKeyword(eq(MY_ORCID), eq(6L), any(Keyword.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateKeyword(MY_ORCID, 6L, keyword);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(Visibility.PUBLIC, ((Keyword) response.getEntity()).getVisibility());
        ArgumentCaptor<Keyword> submitted = ArgumentCaptor.forClass(Keyword.class);
        verify(profileKeywordManager).updateKeyword(eq(MY_ORCID), eq(6L), submitted.capture(), eq(true));
        assertNull("keeping the stored visibility is the manager's job, not the delegator's", submitted.getValue().getVisibility());
    }

    @Test
    public void testDeleteKeyword() {
        Response response = serviceDelegator.deleteKeyword("4444-4444-4444-4499", 8L);

        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes("4444-4444-4444-4499", ScopePathType.ORCID_BIO_UPDATE);
        verify(profileKeywordManager).deleteKeyword("4444-4444-4444-4499", 8L, true);
    }

    @Test
    public void testReadPublicScope_Keywords() {
        // Stubbed per element rather than with a blanket matcher: refusing
        // everything would also refuse 9, 10 and 11 and make the positive half of
        // this test meaningless.
        Keyword nine = keyword(9L, Visibility.PUBLIC, clientSource(CLIENT_1));
        Keyword ten = keyword(10L, Visibility.LIMITED, clientSource(CLIENT_1));
        Keyword eleven = keyword(11L, Visibility.PRIVATE, clientSource(CLIENT_1));
        Keyword twelve = keyword(12L, Visibility.LIMITED, clientSource(CLIENT_2));
        Keyword thirteen = keyword(13L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 9L)).thenReturn(nine);
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 10L)).thenReturn(ten);
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 11L)).thenReturn(eleven);
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 12L)).thenReturn(twelve);
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 13L)).thenReturn(thirteen);
        when(profileKeywordManagerReadOnly.getKeywords(ORCID)).thenReturn(keywords(nine, ten, eleven));
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, twelve, ScopePathType.ORCID_BIO_READ_LIMITED);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, thirteen, ScopePathType.ORCID_BIO_READ_LIMITED);

        Response r = serviceDelegator.viewKeywords(ORCID);
        assertNotNull(r);
        assertEquals(Keywords.class.getName(), r.getEntity().getClass().getName());
        Keywords k = (Keywords) r.getEntity();
        assertEquals("/0000-0000-0000-0003/keywords", k.getPath());
        Utils.verifyLastModified(k.getLastModifiedDate());
        assertEquals(3, k.getKeywords().size());

        r = serviceDelegator.viewKeyword(ORCID, 9L);
        assertNotNull(r);
        assertEquals(Keyword.class.getName(), r.getEntity().getClass().getName());

        // Limited where am the source should work
        serviceDelegator.viewKeyword(ORCID, 10L);

        try {
            // Limited am not the source should fail
            serviceDelegator.viewKeyword(ORCID, 12L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private where am the source should work
        serviceDelegator.viewKeyword(ORCID, 11L);
        try {
            // Private am not the source should fail
            serviceDelegator.viewKeyword(ORCID, 13L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteKeywordYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "keyword"))).when(profileKeywordManager).deleteKeyword(OTHER_ORCID, 3L, true);

        serviceDelegator.deleteKeyword(OTHER_ORCID, 3L);
        fail();
    }

    // ------------------------------------------------------------- helpers

    private void assertViewKeywordDecorated(long putCode, Visibility visibility, Source source) {
        Keyword keyword = keyword(putCode, visibility, source);
        when(profileKeywordManagerReadOnly.getKeyword(OTHER_ORCID, putCode)).thenReturn(keyword);

        Response response = serviceDelegator.viewKeyword(OTHER_ORCID, putCode);

        assertNotNull(response);
        Keyword returned = (Keyword) response.getEntity();
        assertNotNull(returned);
        assertEquals("/4444-4444-4444-4443/keywords/" + putCode, returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());
        assertEquals(visibility, returned.getVisibility());
        verify(orcidSecurityManager).checkAndFilter(OTHER_ORCID, keyword, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<Keyword>> keywordListCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    private Keyword keyword(Long putCode, Visibility visibility, Source source) {
        Keyword keyword = new Keyword();
        keyword.setPutCode(putCode);
        keyword.setContent("Keyword " + putCode);
        keyword.setVisibility(visibility);
        keyword.setSource(source);
        keyword.setCreatedDate(createdDate());
        keyword.setLastModifiedDate(lastModified());
        return keyword;
    }

    private Keywords keywords(Keyword... elements) {
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<>(Arrays.asList(elements)));
        keywords.setLastModifiedDate(lastModified());
        return keywords;
    }
}
