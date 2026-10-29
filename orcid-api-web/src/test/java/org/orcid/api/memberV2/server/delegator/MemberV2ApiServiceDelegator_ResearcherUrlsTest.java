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
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.test.helper.Utils;

/**
 * The researcher-url endpoints of the member v2 delegator, on mocks.
 *
 * <p>
 * See {@link MemberV2ApiServiceDelegatorMockBase} for why no assertion here
 * depends on {@code checkAndFilter} having filtered anything.
 */
public class MemberV2ApiServiceDelegator_ResearcherUrlsTest extends MemberV2ApiServiceDelegatorMockBase {

    private static final String OTHER_ORCID = "4444-4444-4444-4443";
    private static final String MY_ORCID = "4444-4444-4444-4441";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewResearcherUrlWrongToken() {
        ResearcherUrl researcherUrl = researcherUrl(13L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 13L)).thenReturn(researcherUrl);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, researcherUrl,
                ScopePathType.ORCID_BIO_READ_LIMITED);

        try {
            serviceDelegator.viewResearcherUrl(ORCID, 13L);
        } finally {
            assertNull("the element must not be decorated once the guard has refused", researcherUrl.getPath());
        }
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewResearcherUrlsWrongToken() {
        when(researcherUrlManagerReadOnly.getResearcherUrls(ORCID)).thenReturn(researcherUrls(researcherUrl(13L, Visibility.PUBLIC, clientSource(CLIENT_1))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(),
                eq(ScopePathType.ORCID_BIO_READ_LIMITED));

        try {
            serviceDelegator.viewResearcherUrls(ORCID);
        } finally {
            verifyNoInteractions(sourceNameCacheManager);
        }
    }

    @Test
    public void testViewResearcherUrlReadPublic() {
        ResearcherUrl researcherUrl = researcherUrl(13L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 13L)).thenReturn(researcherUrl);

        Response r = serviceDelegator.viewResearcherUrl(ORCID, 13L);

        ResearcherUrl element = (ResearcherUrl) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/researcher-urls/13", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, researcherUrl, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @Test
    public void testViewResearcherUrlsReadPublic() {
        when(researcherUrlManagerReadOnly.getResearcherUrls(ORCID)).thenReturn(researcherUrls(researcherUrl(13L, Visibility.PUBLIC, clientSource(CLIENT_1))));

        Response r = serviceDelegator.viewResearcherUrls(ORCID);

        ResearcherUrls element = (ResearcherUrls) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/researcher-urls", element.getPath());
        assertEquals("/0000-0000-0000-0003/researcher-urls/13", element.getResearcherUrls().get(0).getPath());
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(ScopePathType.ORCID_BIO_READ_LIMITED));
    }

    @Test
    public void testViewResearcherUrls() {
        ResearcherUrls stored = researcherUrls(researcherUrl(2L, Visibility.PUBLIC, userSource(OTHER_ORCID)),
                researcherUrl(7L, Visibility.LIMITED, clientSource(CLIENT_1)), researcherUrl(8L, Visibility.PRIVATE, clientSource(CLIENT_1)));
        when(researcherUrlManagerReadOnly.getResearcherUrls(OTHER_ORCID)).thenReturn(stored);

        Response response = serviceDelegator.viewResearcherUrls(OTHER_ORCID);

        assertNotNull(response);
        ResearcherUrls returned = (ResearcherUrls) response.getEntity();
        assertNotNull(returned);
        assertEquals("/4444-4444-4444-4443/researcher-urls", returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());
        assertEquals(3, returned.getResearcherUrls().size());
        for (ResearcherUrl researcherUrl : returned.getResearcherUrls()) {
            Utils.verifyLastModified(researcherUrl.getLastModifiedDate());
            assertEquals("/4444-4444-4444-4443/researcher-urls/" + researcherUrl.getPutCode(), researcherUrl.getPath());
        }
        assertEquals(CLIENT_1_NAME, returned.getResearcherUrls().get(1).getSource().getSourceName().getContent());

        // checkAndFilter edits in place, so the cached list must be copied first
        ArgumentCaptor<List<ResearcherUrl>> filtered = researcherUrlListCaptor();
        verify(orcidSecurityManager).checkAndFilter(eq(OTHER_ORCID), filtered.capture(), eq(ScopePathType.ORCID_BIO_READ_LIMITED));
        assertNotSame(stored.getResearcherUrls(), filtered.getValue());
    }

    @Test
    public void testViewPublicResearcherUrl() {
        assertViewResearcherUrlDecorated(2L, Visibility.PUBLIC, userSource(OTHER_ORCID));
    }

    @Test
    public void testViewLimitedResearcherUrl() {
        assertViewResearcherUrlDecorated(8L, Visibility.LIMITED, clientSource(CLIENT_1));
    }

    @Test
    public void testViewPrivateResearcherUrl() {
        assertViewResearcherUrlDecorated(7L, Visibility.PRIVATE, clientSource(CLIENT_1));
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateResearcherUrlWhereYouAreNotTheSource() {
        ResearcherUrl researcherUrl = researcherUrl(6L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(researcherUrlManagerReadOnly.getResearcherUrl(OTHER_ORCID, 6L)).thenReturn(researcherUrl);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(OTHER_ORCID, researcherUrl, ScopePathType.ORCID_BIO_READ_LIMITED);

        serviceDelegator.viewResearcherUrl(OTHER_ORCID, 6L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewResearcherUrlThatDontBelongToTheUser() {
        // The (orcid, id) predicate is in ResearcherUrlDaoImpl's query. What is
        // the delegator's is that it lets the miss out and never asks the guard
        // about an element it did not get.
        when(researcherUrlManagerReadOnly.getResearcherUrl(OTHER_ORCID, 1L)).thenThrow(new NoResultException());

        try {
            serviceDelegator.viewResearcherUrl(OTHER_ORCID, 1L);
            fail();
        } finally {
            verifyNoInteractions(orcidSecurityManager);
        }
    }

    @Test
    public void testAddResearcherUrl() {
        ResearcherUrl created = researcherUrl(100L, Visibility.LIMITED, clientSource(CLIENT_1));
        when(researcherUrlManager.createResearcherUrl(eq(MY_ORCID), any(ResearcherUrl.class), anyBoolean())).thenReturn(created);

        Response response = serviceDelegator.createResearcherUrl(MY_ORCID, Utils.getResearcherUrl());

        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(100), Utils.getPutCode(response));
        verify(orcidSecurityManager).checkClientAccessAndScopes(MY_ORCID, ScopePathType.ORCID_BIO_UPDATE);
        ArgumentCaptor<ResearcherUrl> submitted = ArgumentCaptor.forClass(ResearcherUrl.class);
        verify(researcherUrlManager).createResearcherUrl(eq(MY_ORCID), submitted.capture(), eq(true));
        assertNull("a client may not choose its own source", submitted.getValue().getSource());
        assertEquals(CLIENT_1_NAME, created.getSource().getSourceName().getContent());
    }

    @Test
    public void testUpdateResearcherUrl() {
        ResearcherUrl researcherUrl = researcherUrl(5L, Visibility.PUBLIC, clientSource(CLIENT_1));
        researcherUrl.setUrlName("Updated researcher url");
        ResearcherUrl updated = researcherUrl(5L, Visibility.PUBLIC, clientSource(CLIENT_1));
        updated.setUrlName("Updated researcher url");
        when(researcherUrlManager.updateResearcherUrl(eq(OTHER_ORCID), any(ResearcherUrl.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateResearcherUrl(OTHER_ORCID, 5L, researcherUrl);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ResearcherUrl returned = (ResearcherUrl) response.getEntity();
        assertEquals("Updated researcher url", returned.getUrlName());
        assertEquals("/4444-4444-4444-4443/researcher-urls/5", returned.getPath());
        verify(orcidSecurityManager).checkClientAccessAndScopes(OTHER_ORCID, ScopePathType.ORCID_BIO_UPDATE);
        ArgumentCaptor<ResearcherUrl> submitted = ArgumentCaptor.forClass(ResearcherUrl.class);
        verify(researcherUrlManager).updateResearcherUrl(eq(OTHER_ORCID), submitted.capture(), eq(true));
        assertNull(submitted.getValue().getSource());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateResearcherUrlYouAreNotTheSourceOf() {
        // ResearcherUrlManagerImpl calls orcidSecurityManager.checkSource on the
        // stored entity; the rule belongs to that manager's tests.
        ResearcherUrl researcherUrl = researcherUrl(8L, Visibility.LIMITED, clientSource(CLIENT_2));
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "researcher-url"))).when(researcherUrlManager)
                .updateResearcherUrl(eq(OTHER_ORCID), any(ResearcherUrl.class), anyBoolean());

        serviceDelegator.updateResearcherUrl(OTHER_ORCID, 8L, researcherUrl);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateResearcherUrlChangingVisibilityTest() {
        ResearcherUrl researcherUrl = researcherUrl(5L, Visibility.PRIVATE, clientSource(CLIENT_1));
        doThrow(new VisibilityMismatchException()).when(researcherUrlManager).updateResearcherUrl(eq(OTHER_ORCID), any(ResearcherUrl.class), anyBoolean());

        serviceDelegator.updateResearcherUrl(OTHER_ORCID, 5L, researcherUrl);
        fail();
    }

    @Test
    public void testUpdateResearcherUrlLeavingVisibilityNullTest() {
        ResearcherUrl researcherUrl = researcherUrl(5L, null, clientSource(CLIENT_1));
        ResearcherUrl updated = researcherUrl(5L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(researcherUrlManager.updateResearcherUrl(eq(OTHER_ORCID), any(ResearcherUrl.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateResearcherUrl(OTHER_ORCID, 5L, researcherUrl);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(Visibility.PUBLIC, ((ResearcherUrl) response.getEntity()).getVisibility());
        ArgumentCaptor<ResearcherUrl> submitted = ArgumentCaptor.forClass(ResearcherUrl.class);
        verify(researcherUrlManager).updateResearcherUrl(eq(OTHER_ORCID), submitted.capture(), eq(true));
        assertNull("keeping the stored visibility is the manager's job, not the delegator's", submitted.getValue().getVisibility());
    }

    @Test
    public void testDeleteResearcherUrl() {
        Response response = serviceDelegator.deleteResearcherUrl("4444-4444-4444-4445", 4L);

        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes("4444-4444-4444-4445", ScopePathType.ORCID_BIO_UPDATE);
        verify(researcherUrlManager).deleteResearcherUrl("4444-4444-4444-4445", 4L, true);
    }

    @Test
    public void testReadPublicScope_ResearcherUrls() {
        // Stubbed per element rather than with a blanket matcher: refusing
        // everything would also refuse 13, 14 and 15 and make the positive half
        // of this test meaningless.
        ResearcherUrl thirteen = researcherUrl(13L, Visibility.PUBLIC, clientSource(CLIENT_1));
        ResearcherUrl fourteen = researcherUrl(14L, Visibility.LIMITED, clientSource(CLIENT_1));
        ResearcherUrl fifteen = researcherUrl(15L, Visibility.PRIVATE, clientSource(CLIENT_1));
        ResearcherUrl sixteen = researcherUrl(16L, Visibility.LIMITED, clientSource(CLIENT_2));
        ResearcherUrl seventeen = researcherUrl(17L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 13L)).thenReturn(thirteen);
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 14L)).thenReturn(fourteen);
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 15L)).thenReturn(fifteen);
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 16L)).thenReturn(sixteen);
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 17L)).thenReturn(seventeen);
        when(researcherUrlManagerReadOnly.getResearcherUrls(ORCID)).thenReturn(researcherUrls(thirteen, fourteen, fifteen));
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, sixteen, ScopePathType.ORCID_BIO_READ_LIMITED);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, seventeen, ScopePathType.ORCID_BIO_READ_LIMITED);

        Response r = serviceDelegator.viewResearcherUrls(ORCID);
        assertNotNull(r);
        assertEquals(ResearcherUrls.class.getName(), r.getEntity().getClass().getName());
        ResearcherUrls urls = (ResearcherUrls) r.getEntity();
        assertEquals("/0000-0000-0000-0003/researcher-urls", urls.getPath());
        Utils.verifyLastModified(urls.getLastModifiedDate());
        assertEquals(3, urls.getResearcherUrls().size());

        r = serviceDelegator.viewResearcherUrl(ORCID, 13L);
        assertNotNull(r);
        assertEquals(ResearcherUrl.class.getName(), r.getEntity().getClass().getName());

        // Limited where am the source should work
        serviceDelegator.viewResearcherUrl(ORCID, 14L);

        try {
            // Limited am not the source should fail
            serviceDelegator.viewResearcherUrl(ORCID, 16L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private where am the source should work
        serviceDelegator.viewResearcherUrl(ORCID, 15L);
        try {
            // Private am not the source should fail
            serviceDelegator.viewResearcherUrl(ORCID, 17L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteResearcherUrlYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "researcher-url"))).when(researcherUrlManager).deleteResearcherUrl(OTHER_ORCID,
                8L, true);

        serviceDelegator.deleteResearcherUrl(OTHER_ORCID, 8L);
        fail();
    }

    // ------------------------------------------------------------- helpers

    private void assertViewResearcherUrlDecorated(long putCode, Visibility visibility, Source source) {
        ResearcherUrl researcherUrl = researcherUrl(putCode, visibility, source);
        when(researcherUrlManagerReadOnly.getResearcherUrl(OTHER_ORCID, putCode)).thenReturn(researcherUrl);

        Response response = serviceDelegator.viewResearcherUrl(OTHER_ORCID, putCode);

        assertNotNull(response);
        ResearcherUrl returned = (ResearcherUrl) response.getEntity();
        assertNotNull(returned);
        assertEquals("/4444-4444-4444-4443/researcher-urls/" + putCode, returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());
        assertEquals(visibility, returned.getVisibility());
        verify(orcidSecurityManager).checkAndFilter(OTHER_ORCID, researcherUrl, ScopePathType.ORCID_BIO_READ_LIMITED);
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<ResearcherUrl>> researcherUrlListCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    private ResearcherUrl researcherUrl(Long putCode, Visibility visibility, Source source) {
        ResearcherUrl researcherUrl = new ResearcherUrl();
        researcherUrl.setPutCode(putCode);
        researcherUrl.setUrl(new Url("http://www.researcherurl.com/" + putCode));
        researcherUrl.setUrlName("Researcher url " + putCode);
        researcherUrl.setVisibility(visibility);
        researcherUrl.setSource(source);
        researcherUrl.setCreatedDate(createdDate());
        researcherUrl.setLastModifiedDate(lastModified());
        return researcherUrl;
    }

    private ResearcherUrls researcherUrls(ResearcherUrl... elements) {
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<>(Arrays.asList(elements)));
        researcherUrls.setLastModifiedDate(lastModified());
        return researcherUrls;
    }
}
