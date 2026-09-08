package org.orcid.api.memberV3.server.delegator;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the researcher-url endpoints of the member V3 API.
 *
 * <p>
 * {@code checkAndFilter} is void and filters in place, so a mocked security
 * manager filters nothing; the visibility tables these tests used to exercise
 * are proved in orcid-core by {@code OrcidSecurityManager_generalTest}. What is
 * asserted here is the delegator's own contract, plus a {@code verify} that the
 * element was handed to the security manager with the right scope.
 */
public class MemberV3ApiServiceDelegator_ResearcherUrlsTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final ScopePathType SCOPE = ScopePathType.ORCID_BIO_READ_LIMITED;

    private static final String USER_4441 = "4444-4444-4444-4441";
    private static final String USER_4443 = "4444-4444-4444-4443";
    private static final String USER_4445 = "4444-4444-4444-4445";

    private ResearcherUrl researcherUrl(long putCode, String url, String urlName, Visibility visibility, Source source) {
        ResearcherUrl element = new ResearcherUrl();
        element.setPutCode(putCode);
        element.setUrl(new Url(url));
        element.setUrlName(urlName);
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        element.setCreatedDate(created());
        return element;
    }

    private ResearcherUrls researcherUrls(ResearcherUrl... elements) {
        ResearcherUrls container = new ResearcherUrls();
        container.setResearcherUrls(new ArrayList<>(Arrays.asList(elements)));
        return container;
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewResearcherUrlWrongToken() {
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 13L))
                .thenReturn(researcherUrl(13L, "http://www.researcherurl.com?id=13", "13", Visibility.PUBLIC, clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(ResearcherUrl.class), eq(SCOPE));

        serviceDelegator.viewResearcherUrl(ORCID, 13L);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewResearcherUrlsWrongToken() {
        when(researcherUrlManagerReadOnly.getResearcherUrls(ORCID))
                .thenReturn(researcherUrls(researcherUrl(13L, "http://www.researcherurl.com?id=13", "13", Visibility.PUBLIC, clientSource(CLIENT_1))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));

        serviceDelegator.viewResearcherUrls(ORCID);
    }

    @Test
    public void testViewResearcherUrlReadPublic() {
        ResearcherUrl stored = researcherUrl(13L, "http://www.researcherurl.com?id=13", "13", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 13L)).thenReturn(stored);

        Response r = serviceDelegator.viewResearcherUrl(ORCID, 13L);
        ResearcherUrl element = (ResearcherUrl) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/researcher-urls/13", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewResearcherUrlsReadPublic() {
        when(researcherUrlManagerReadOnly.getResearcherUrls(ORCID))
                .thenReturn(researcherUrls(researcherUrl(13L, "http://www.researcherurl.com?id=13", "13", Visibility.PUBLIC, clientSource(CLIENT_1))));

        Response r = serviceDelegator.viewResearcherUrls(ORCID);
        ResearcherUrls elements = (ResearcherUrls) r.getEntity();
        assertNotNull(elements);
        assertEquals("/0000-0000-0000-0003/researcher-urls", elements.getPath());
        assertEquals("/0000-0000-0000-0003/researcher-urls/13", elements.getResearcherUrls().get(0).getPath());
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testViewResearcherUrls() {
        when(researcherUrlManagerReadOnly.getResearcherUrls(USER_4443)).thenReturn(researcherUrls(
                researcherUrl(2L, "http://www.researcherurl2.com?id=1", "443_1", Visibility.PUBLIC, userSource(USER_4443)),
                researcherUrl(3L, "http://www.researcherurl2.com?id=3", "443_2", Visibility.LIMITED, userSource(USER_4443)),
                researcherUrl(5L, "http://www.researcherurl2.com?id=5", "443_3", Visibility.PUBLIC, clientSource(CLIENT_1)),
                researcherUrl(7L, "http://www.researcherurl2.com?id=7", "443_5", Visibility.PRIVATE, clientSource(CLIENT_1)),
                researcherUrl(8L, "http://www.researcherurl2.com?id=8", "443_6", Visibility.LIMITED, userSource(USER_4443))));

        Response response = serviceDelegator.viewResearcherUrls(USER_4443);
        assertNotNull(response);
        ResearcherUrls researcherUrls = (ResearcherUrls) response.getEntity();
        assertNotNull(researcherUrls);
        Utils.verifyLastModified(researcherUrls.getLastModifiedDate());
        assertEquals("/4444-4444-4444-4443/researcher-urls", researcherUrls.getPath());
        assertNotNull(researcherUrls.getResearcherUrls());
        assertEquals(5, researcherUrls.getResearcherUrls().size());
        for (ResearcherUrl rUrl : researcherUrls.getResearcherUrls()) {
            assertThat(rUrl.getPutCode(),
                    anyOf(equalTo(Long.valueOf(2)), equalTo(Long.valueOf(3)), equalTo(Long.valueOf(5)), equalTo(Long.valueOf(7)), equalTo(Long.valueOf(8))));
            Utils.verifyLastModified(researcherUrls.getLastModifiedDate());
            assertNotNull(rUrl.getSource());
            assertFalse(PojoUtil.isEmpty(rUrl.getSource().retrieveSourcePath()));
            assertNotNull(rUrl.getUrl());
            assertNotNull(rUrl.getUrlName());
            assertNotNull(rUrl.getVisibility());
            if (rUrl.getPutCode().equals(Long.valueOf(5)) || rUrl.getPutCode().equals(Long.valueOf(7))) {
                assertEquals(CLIENT_1, rUrl.getSource().retrieveSourcePath());
            }
        }
        verify(orcidSecurityManager).checkAndFilter(eq(USER_4443), anyList(), eq(SCOPE));
    }

    @Test
    public void testViewPublicResearcherUrl() {
        when(researcherUrlManagerReadOnly.getResearcherUrl(USER_4443, 2L))
                .thenReturn(researcherUrl(2L, "http://www.researcherurl2.com?id=1", "443_1", Visibility.PUBLIC, userSource(USER_4443)));

        Response response = serviceDelegator.viewResearcherUrl(USER_4443, 2L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("/4444-4444-4444-4443/researcher-urls/2", researcherUrl.getPath());
        Utils.verifyLastModified(researcherUrl.getLastModifiedDate());
        assertEquals(USER_4443, researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.researcherurl2.com?id=1", researcherUrl.getUrl().getValue());
        assertEquals("443_1", researcherUrl.getUrlName());
        assertEquals(Visibility.PUBLIC, researcherUrl.getVisibility());
        verify(orcidSecurityManager).checkAndFilter(USER_4443, researcherUrl, SCOPE);
    }

    @Test
    public void testViewLimitedResearcherUrl() {
        when(researcherUrlManagerReadOnly.getResearcherUrl(USER_4443, 8L))
                .thenReturn(researcherUrl(8L, "http://www.researcherurl2.com?id=8", "443_6", Visibility.LIMITED, userSource(USER_4443)));

        Response response = serviceDelegator.viewResearcherUrl(USER_4443, 8L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("/4444-4444-4444-4443/researcher-urls/8", researcherUrl.getPath());
        Utils.verifyLastModified(researcherUrl.getLastModifiedDate());
        assertEquals(USER_4443, researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.researcherurl2.com?id=8", researcherUrl.getUrl().getValue());
        assertEquals("443_6", researcherUrl.getUrlName());
        assertEquals(Visibility.LIMITED, researcherUrl.getVisibility());
        verify(orcidSecurityManager).checkAndFilter(USER_4443, researcherUrl, SCOPE);
    }

    @Test
    public void testViewPrivateResearcherUrl() {
        when(researcherUrlManagerReadOnly.getResearcherUrl(USER_4443, 7L))
                .thenReturn(researcherUrl(7L, "http://www.researcherurl2.com?id=7", "443_5", Visibility.PRIVATE, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewResearcherUrl(USER_4443, 7L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("/4444-4444-4444-4443/researcher-urls/7", researcherUrl.getPath());
        Utils.verifyLastModified(researcherUrl.getLastModifiedDate());
        assertEquals(CLIENT_1, researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.researcherurl2.com?id=7", researcherUrl.getUrl().getValue());
        assertEquals("443_5", researcherUrl.getUrlName());
        assertEquals(Visibility.PRIVATE, researcherUrl.getVisibility());
        verify(orcidSecurityManager).checkAndFilter(USER_4443, researcherUrl, SCOPE);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateResearcherUrlWhereYouAreNotTheSource() {
        ResearcherUrl stored = researcherUrl(6L, "http://www.researcherurl2.com?id=6", "443_4", Visibility.PRIVATE, clientSource(CLIENT_2));
        when(researcherUrlManagerReadOnly.getResearcherUrl(USER_4443, 6L)).thenReturn(stored);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(USER_4443, stored, SCOPE);

        serviceDelegator.viewResearcherUrl(USER_4443, 6L);
        fail();
    }

    /**
     * The rule this proves -- that researcher url 1 cannot be read through record
     * 4443 -- lives in a SQL WHERE clause
     * ({@code ResearcherUrlDaoImpl.getResearcherUrl}), so with a mocked manager
     * only the pass-through survives here. The predicate itself is proved by
     * MemberV3ApiServiceDelegatorDatabaseRulesTest in the db-tests stage.
     */
    @Test(expected = NoResultException.class)
    public void testViewResearcherUrlThatDontBelongToTheUser() {
        when(researcherUrlManagerReadOnly.getResearcherUrl(USER_4443, 1L)).thenThrow(new NoResultException());

        serviceDelegator.viewResearcherUrl(USER_4443, 1L);
        fail();
    }

    @Test
    public void testAddResearcherUrl() {
        ResearcherUrl created = researcherUrl(1000L, "http://www.myRUrl.com", "My researcher Url", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(researcherUrlManager.createResearcherUrl(eq(USER_4441), any(ResearcherUrl.class), eq(true))).thenReturn(created);
        when(researcherUrlManagerReadOnly.getResearcherUrl(USER_4441, 1000L)).thenReturn(created);

        ResearcherUrl toCreate = Utils.getResearcherUrl();
        // Planted so that assertNull below proves clearSource ran, rather than
        // only proving the fixture never had a source to begin with.
        toCreate.setSource(clientSource(CLIENT_2));

        Response response = serviceDelegator.createResearcherUrl(USER_4441, toCreate);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = Utils.getPutCode(response);
        assertEquals(Long.valueOf(1000L), putCode);

        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4441, ScopePathType.ORCID_BIO_UPDATE);
        // A client supplied source must never reach the manager.
        ArgumentCaptor<ResearcherUrl> captor = ArgumentCaptor.forClass(ResearcherUrl.class);
        verify(researcherUrlManager).createResearcherUrl(eq(USER_4441), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());

        response = serviceDelegator.viewResearcherUrl(USER_4441, putCode);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        Utils.verifyLastModified(researcherUrl.getLastModifiedDate());
        assertEquals(CLIENT_1, researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.myRUrl.com", researcherUrl.getUrl().getValue());
        assertEquals("My researcher Url", researcherUrl.getUrlName());
    }

    @Test
    public void testUpdateResearcherUrl() {
        when(researcherUrlManagerReadOnly.getResearcherUrl(USER_4443, 5L))
                .thenReturn(researcherUrl(5L, "http://www.researcherurl2.com?id=5", "443_3", Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(researcherUrlManager.updateResearcherUrl(eq(USER_4443), any(ResearcherUrl.class), eq(true)))
                .thenReturn(researcherUrl(5L, "http://theNewResearcherUrl.com", "My Updated Researcher Url", Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewResearcherUrl(USER_4443, 5L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        Utils.verifyLastModified(researcherUrl.getLastModifiedDate());
        assertEquals("http://www.researcherurl2.com?id=5", researcherUrl.getUrl().getValue());
        assertEquals("443_3", researcherUrl.getUrlName());

        researcherUrl.setUrl(new Url("http://theNewResearcherUrl.com"));
        researcherUrl.setUrlName("My Updated Researcher Url");

        response = serviceDelegator.updateResearcherUrl(USER_4443, 5L, researcherUrl);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        Utils.verifyLastModified(researcherUrl.getLastModifiedDate());
        assertEquals("http://theNewResearcherUrl.com", researcherUrl.getUrl().getValue());
        assertEquals("My Updated Researcher Url", researcherUrl.getUrlName());
        assertEquals("/4444-4444-4444-4443/researcher-urls/5", researcherUrl.getPath());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4443, ScopePathType.ORCID_BIO_UPDATE);
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateResearcherUrlYouAreNotTheSourceOf() {
        when(researcherUrlManagerReadOnly.getResearcherUrl(USER_4443, 8L))
                .thenReturn(researcherUrl(8L, "http://www.researcherurl2.com?id=8", "443_6", Visibility.LIMITED, userSource(USER_4443)));
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(researcherUrlManager).updateResearcherUrl(eq(USER_4443), any(ResearcherUrl.class), eq(true));

        Response response = serviceDelegator.viewResearcherUrl(USER_4443, 8L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("http://www.researcherurl2.com?id=8", researcherUrl.getUrl().getValue());
        assertEquals("443_6", researcherUrl.getUrlName());

        researcherUrl.setUrlName("Updated " + System.currentTimeMillis());

        serviceDelegator.updateResearcherUrl(USER_4443, 8L, researcherUrl);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateResearcherUrlChangingVisibilityTest() {
        when(researcherUrlManagerReadOnly.getResearcherUrl(USER_4443, 5L))
                .thenReturn(researcherUrl(5L, "http://www.researcherurl2.com?id=5", "443_3", Visibility.PUBLIC, clientSource(CLIENT_1)));
        doThrow(new VisibilityMismatchException())
                .when(researcherUrlManager).updateResearcherUrl(eq(USER_4443), any(ResearcherUrl.class), eq(true));

        Response response = serviceDelegator.viewResearcherUrl(USER_4443, 5L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals(Visibility.PUBLIC, researcherUrl.getVisibility());

        researcherUrl.setVisibility(Visibility.PRIVATE);

        serviceDelegator.updateResearcherUrl(USER_4443, 5L, researcherUrl);
        fail();
    }

    @Test
    public void testUpdateResearcherUrlLeavingVisibilityNullTest() {
        when(researcherUrlManagerReadOnly.getResearcherUrl(USER_4443, 5L))
                .thenReturn(researcherUrl(5L, "http://www.researcherurl2.com?id=5", "443_3", Visibility.PUBLIC, clientSource(CLIENT_1)));
        // Restoring the stored visibility is the manager's job and is proved
        // there; here the delegator must simply return what it produced.
        when(researcherUrlManager.updateResearcherUrl(eq(USER_4443), any(ResearcherUrl.class), eq(true)))
                .thenReturn(researcherUrl(5L, "http://www.researcherurl2.com?id=5", "443_3", Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewResearcherUrl(USER_4443, 5L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals(Visibility.PUBLIC, researcherUrl.getVisibility());

        researcherUrl.setVisibility(null);

        response = serviceDelegator.updateResearcherUrl(USER_4443, 5L, researcherUrl);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals(Visibility.PUBLIC, researcherUrl.getVisibility());
        // Catches a delegator that sets a visibility on the element before handing it to
        // the manager: what is submitted must still carry the null the request arrived with.
        ArgumentCaptor<ResearcherUrl> submitted = ArgumentCaptor.forClass(ResearcherUrl.class);
        verify(researcherUrlManager).updateResearcherUrl(eq(USER_4443), submitted.capture(), eq(true));
        assertNull("keeping the stored visibility is the manager's job, not the delegator's", submitted.getValue().getVisibility());
    }

    @Test
    public void testDeleteResearcherUrl() {
        when(researcherUrlManagerReadOnly.getResearcherUrls(USER_4445))
                .thenReturn(researcherUrls(researcherUrl(20L, "http://www.researcherurl.com?id=20", "445_1", Visibility.PUBLIC, clientSource(CLIENT_1))))
                .thenReturn(researcherUrls());

        Response response = serviceDelegator.viewResearcherUrls(USER_4445);
        assertNotNull(response);
        ResearcherUrls researcherUrls = (ResearcherUrls) response.getEntity();
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getResearcherUrls());
        assertFalse(researcherUrls.getResearcherUrls().isEmpty());
        ResearcherUrl toDelete = null;
        for (ResearcherUrl rurl : researcherUrls.getResearcherUrls()) {
            if (rurl.getSource().retrieveSourcePath().equals(CLIENT_1)) {
                toDelete = rurl;
                break;
            }
        }
        assertNotNull(toDelete);

        response = serviceDelegator.deleteResearcherUrl(USER_4445, toDelete.getPutCode());
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4445, ScopePathType.ORCID_BIO_UPDATE);
        verify(researcherUrlManager).deleteResearcherUrl(USER_4445, 20L, true);

        response = serviceDelegator.viewResearcherUrls(USER_4445);
        assertNotNull(response);
        researcherUrls = (ResearcherUrls) response.getEntity();
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getResearcherUrls());
        assertEquals(0, researcherUrls.getResearcherUrls().size());
    }

    @Test
    public void testReadPublicScope_ResearcherUrls() {
        when(researcherUrlManagerReadOnly.getResearcherUrls(ORCID)).thenReturn(researcherUrls(
                researcherUrl(13L, "http://www.researcherurl.com?id=13", "13", Visibility.PUBLIC, clientSource(CLIENT_1)),
                researcherUrl(14L, "http://www.researcherurl.com?id=14", "14", Visibility.LIMITED, clientSource(CLIENT_1)),
                researcherUrl(15L, "http://www.researcherurl.com?id=15", "15", Visibility.PRIVATE, clientSource(CLIENT_1))));
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 13L))
                .thenReturn(researcherUrl(13L, "http://www.researcherurl.com?id=13", "13", Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 14L))
                .thenReturn(researcherUrl(14L, "http://www.researcherurl.com?id=14", "14", Visibility.LIMITED, clientSource(CLIENT_1)));
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 15L))
                .thenReturn(researcherUrl(15L, "http://www.researcherurl.com?id=15", "15", Visibility.PRIVATE, clientSource(CLIENT_1)));

        ResearcherUrl limitedOtherSource = researcherUrl(16L, "http://www.researcherurl.com?id=16", "16", Visibility.LIMITED, userSource(ORCID));
        ResearcherUrl privateOtherSource = researcherUrl(17L, "http://www.researcherurl.com?id=17", "17", Visibility.PRIVATE, userSource(ORCID));
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 16L)).thenReturn(limitedOtherSource);
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 17L)).thenReturn(privateOtherSource);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, limitedOtherSource, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, privateOtherSource, SCOPE);

        Response r = serviceDelegator.viewResearcherUrls(ORCID);
        assertNotNull(r);
        ResearcherUrls ru = (ResearcherUrls) r.getEntity();
        assertNotNull(ru);
        assertEquals("/0000-0000-0000-0003/researcher-urls", ru.getPath());
        Utils.verifyLastModified(ru.getLastModifiedDate());
        assertEquals(3, ru.getResearcherUrls().size());
        boolean found13 = false, found14 = false, found15 = false;
        for (ResearcherUrl element : ru.getResearcherUrls()) {
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

        r = serviceDelegator.viewResearcherUrl(ORCID, 13L);
        assertNotNull(r);
        assertEquals(ResearcherUrl.class.getName(), r.getEntity().getClass().getName());

        // Limited am the source of should work
        serviceDelegator.viewResearcherUrl(ORCID, 14L);
        // Limited am not the source of should fail
        try {
            serviceDelegator.viewResearcherUrl(ORCID, 16L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }
        // Private am the source of should work
        serviceDelegator.viewResearcherUrl(ORCID, 15L);
        // Private am not the source of should fail
        try {
            serviceDelegator.viewResearcherUrl(ORCID, 17L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteResearcherUrlYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(new HashMap<String, String>())).when(researcherUrlManager).deleteResearcherUrl(USER_4443, 8L, true);

        serviceDelegator.deleteResearcherUrl(USER_4443, 8L);
        fail();
    }
}
