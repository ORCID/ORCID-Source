package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.utils.JsonUtils;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.pojo.HeaderCheckResult;
import org.orcid.pojo.HeaderMismatch;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class InstitutionalSignInManagerTest {

    private final String userOrcid = "0000-0000-0000-0001";
    private final String clientId = "APP-00000000001";

    @Mock
    private UserConnectionDao mock_userConnectionDao;

    @Mock
    private ClientDetailsEntityCacheManager mock_clientDetailsEntityCacheManager;

    @Mock
    private NotificationManager mock_notificationManager;

    @Mock
    private OrcidOauth2TokenDetailService mock_orcidOauth2TokenDetailService;

    @Resource
    private UserConnectionDao userConnectionDao;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;

    @Resource
    private InstitutionalSignInManager institutionalSignInManager;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(institutionalSignInManager, "userConnectionDao", mock_userConnectionDao);
        TargetProxyHelper.injectIntoProxy(institutionalSignInManager, "clientDetailsEntityCacheManager", mock_clientDetailsEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(institutionalSignInManager, "notificationManager", mock_notificationManager);
        TargetProxyHelper.injectIntoProxy(institutionalSignInManager, "orcidOauth2TokenDetailService", mock_orcidOauth2TokenDetailService);
    }

    @After
    public void after() {
        // Rollback to the original beans
        TargetProxyHelper.injectIntoProxy(institutionalSignInManager, "userConnectionDao", userConnectionDao);
        TargetProxyHelper.injectIntoProxy(institutionalSignInManager, "clientDetailsEntityCacheManager", clientDetailsEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(institutionalSignInManager, "notificationManager", notificationManager);
        TargetProxyHelper.injectIntoProxy(institutionalSignInManager, "orcidOauth2TokenDetailService", orcidOauth2TokenDetailService);
    }

    @Test
    public void testCreateUserConnectionAndNotify() throws UnsupportedEncodingException {
        ClientDetailsEntity testClient = new ClientDetailsEntity(clientId);

        when(mock_userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(anyString(), anyString(), anyString())).thenReturn(null);
        when(mock_clientDetailsEntityCacheManager.retrieveByIdP(anyString())).thenReturn(testClient);
        when(mock_orcidOauth2TokenDetailService.doesClientKnowUser(anyString(), anyString())).thenReturn(false);

        institutionalSignInManager.createUserConnectionAndNotify("idType", "remoteUserId", "displayName", "providerId", userOrcid,
                Collections.<String, String> emptyMap());

        verify(mock_userConnectionDao, times(1)).persist(any());
        verify(mock_notificationManager, times(1)).sendAcknowledgeMessage(userOrcid, clientId);
    }

    @Test
    public void testDontSendNotificationIfClientKnowUser() throws UnsupportedEncodingException {
        ClientDetailsEntity testClient = new ClientDetailsEntity(clientId);

        when(mock_userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(anyString(), anyString(), anyString())).thenReturn(null);
        when(mock_clientDetailsEntityCacheManager.retrieveByIdP(anyString())).thenReturn(testClient);
        when(mock_orcidOauth2TokenDetailService.doesClientKnowUser(anyString(), anyString())).thenReturn(true);

        institutionalSignInManager.createUserConnectionAndNotify("idType", "remoteUserId", "displayName", "providerId", userOrcid,
                Collections.<String, String> emptyMap());

        verify(mock_userConnectionDao, times(1)).persist(any());
        verify(mock_notificationManager, never()).sendAcknowledgeMessage(userOrcid, clientId);
    }

    @Test
    public void testDontSendNotificationIfIdPNotLinkedToClient() throws UnsupportedEncodingException {
        when(mock_userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(anyString(), anyString(), anyString())).thenReturn(null);
        when(mock_clientDetailsEntityCacheManager.retrieveByIdP(anyString())).thenThrow(new IllegalArgumentException());
        when(mock_orcidOauth2TokenDetailService.doesClientKnowUser(anyString(), anyString())).thenReturn(false);

        institutionalSignInManager.createUserConnectionAndNotify("idType", "remoteUserId", "displayName", "providerId", userOrcid,
                Collections.<String, String> emptyMap());

        verify(mock_userConnectionDao, times(1)).persist(any());
        verify(mock_notificationManager, never()).sendAcknowledgeMessage(userOrcid, clientId);
    }

    @Test
    public void testDontPersistIfUserConnectionAlreadyExists() throws UnsupportedEncodingException {
        ClientDetailsEntity testClient = new ClientDetailsEntity(clientId);
        when(mock_userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(anyString(), anyString(), anyString())).thenReturn(new UserconnectionEntity());
        when(mock_clientDetailsEntityCacheManager.retrieveByIdP(anyString())).thenReturn(testClient);
        when(mock_orcidOauth2TokenDetailService.doesClientKnowUser(anyString(), anyString())).thenReturn(false);

        institutionalSignInManager.createUserConnectionAndNotify("idType", "remoteUserId", "displayName", "providerId", userOrcid,
                Collections.<String, String> emptyMap());

        verify(mock_userConnectionDao, never()).persist(any());
        verify(mock_notificationManager, times(1)).sendAcknowledgeMessage(userOrcid, clientId);
    }

    @Test
    public void testDontPersistAndDontNotify() throws UnsupportedEncodingException {
        when(mock_userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(anyString(), anyString(), anyString())).thenReturn(new UserconnectionEntity());
        when(mock_clientDetailsEntityCacheManager.retrieveByIdP(anyString())).thenThrow(new IllegalArgumentException());
        when(mock_orcidOauth2TokenDetailService.doesClientKnowUser(anyString(), anyString())).thenReturn(true);

        institutionalSignInManager.createUserConnectionAndNotify("idType", "remoteUserId", "displayName", "providerId", userOrcid,
                Collections.<String, String> emptyMap());

        verify(mock_userConnectionDao, never()).persist(any());
        verify(mock_notificationManager, never()).sendAcknowledgeMessage(userOrcid, clientId);
    }

    @Test
    public void testCheckHeaders() throws IOException {
        @SuppressWarnings("unchecked")
        Map<String, String> originalHeaders = JsonUtils.readObjectFromJsonString(IOUtils.toString(getClass().getResource("shibboleth_headers_original.json")), Map.class);
        Map<String, String> currentHeaders = new HashMap<>(originalHeaders);

        // When all headers are the same
        HeaderCheckResult result = institutionalSignInManager.checkHeaders(originalHeaders, currentHeaders);
        assertTrue(result.isSuccess());
        assertEquals(0, result.getMismatches().size());

        // When eppn is different
        currentHeaders.put("eppn", "someoneelse@testshib.org");
        result = institutionalSignInManager.checkHeaders(originalHeaders, currentHeaders);
        assertFalse(result.isSuccess());
        assertEquals(1, result.getMismatches().size());
        HeaderMismatch mismatch = result.getMismatches().get(0);
        assertEquals("eppn", mismatch.getHeaderName());
        assertEquals("myself@testshib.org", mismatch.getOriginalValue());
        assertEquals("someoneelse@testshib.org", mismatch.getCurrentValue());

        // When eppn was originally there, but is not now
        currentHeaders.remove("eppn");
        result = institutionalSignInManager.checkHeaders(originalHeaders, currentHeaders);
        assertTrue(result.isSuccess());
        assertEquals(0, result.getMismatches().size());

        // When eppn is duplicated but unchanged
        currentHeaders.put("eppn", "myself@testshib.org;myself@testshib.org");
        result = institutionalSignInManager.checkHeaders(originalHeaders, currentHeaders);
        assertTrue(result.isSuccess());
        assertEquals(0, result.getMismatches().size());

        // When eppn is duplicated and changed
        currentHeaders.put("eppn", "someoneelse@testshib.org;someoneelse@testshib.org");
        result = institutionalSignInManager.checkHeaders(originalHeaders, currentHeaders);
        assertFalse(result.isSuccess());
        assertEquals(1, result.getMismatches().size());
        mismatch = result.getMismatches().get(0);
        assertEquals("eppn", mismatch.getHeaderName());
        assertEquals("myself@testshib.org", mismatch.getOriginalValue());
        assertEquals("someoneelse@testshib.org;someoneelse@testshib.org", mismatch.getCurrentValue());

        // When eppn is duplicated and one of values changed
        currentHeaders.put("eppn", "myself@testshib.org;someoneelse@testshib.org");
        result = institutionalSignInManager.checkHeaders(originalHeaders, currentHeaders);
        assertFalse(result.isSuccess());
        assertEquals(1, result.getMismatches().size());
        mismatch = result.getMismatches().get(0);
        assertEquals("eppn", mismatch.getHeaderName());
        assertEquals("myself@testshib.org", mismatch.getOriginalValue());
        assertEquals("myself@testshib.org;someoneelse@testshib.org", mismatch.getCurrentValue());
    }

}
