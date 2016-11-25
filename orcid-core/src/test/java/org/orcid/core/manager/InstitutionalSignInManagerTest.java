/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.utils.JsonUtils;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
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
    private OrcidOauth2TokenDetailDao mock_orcidOauth2TokenDetailDao;

    @Resource
    private UserConnectionDao userConnectionDao;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;

    @Resource
    private InstitutionalSignInManager institutionalSignInManager;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(institutionalSignInManager, "userConnectionDao", mock_userConnectionDao);
        TargetProxyHelper.injectIntoProxy(institutionalSignInManager, "clientDetailsEntityCacheManager", mock_clientDetailsEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(institutionalSignInManager, "notificationManager", mock_notificationManager);
        TargetProxyHelper.injectIntoProxy(institutionalSignInManager, "orcidOauth2TokenDetailDao", mock_orcidOauth2TokenDetailDao);
    }

    @After
    public void after() {
        // Rollback to the original beans
        TargetProxyHelper.injectIntoProxy(institutionalSignInManager, "userConnectionDao", userConnectionDao);
        TargetProxyHelper.injectIntoProxy(institutionalSignInManager, "clientDetailsEntityCacheManager", clientDetailsEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(institutionalSignInManager, "notificationManager", notificationManager);
        TargetProxyHelper.injectIntoProxy(institutionalSignInManager, "orcidOauth2TokenDetailDao", orcidOauth2TokenDetailDao);
    }

    @Test
    public void testCreateUserConnectionAndNotify() throws UnsupportedEncodingException {
        ClientDetailsEntity testClient = new ClientDetailsEntity(clientId);

        when(mock_userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(Matchers.anyString(), Matchers.anyString(), Matchers.anyString())).thenReturn(null);
        when(mock_clientDetailsEntityCacheManager.retrieveByIdP(Matchers.anyString())).thenReturn(testClient);
        when(mock_orcidOauth2TokenDetailDao.findByClientIdAndUserName(Matchers.anyString(), Matchers.anyString())).thenReturn(null);

        institutionalSignInManager.createUserConnectionAndNotify("idType", "remoteUserId", "displayName", "providerId", userOrcid,
                Collections.<String, String> emptyMap());

        verify(mock_userConnectionDao, times(1)).persist(Matchers.any());
        verify(mock_notificationManager, times(1)).sendAcknowledgeMessage(userOrcid, clientId);
    }

    @Test
    public void testDontSendNotificationIfClientKnowUser() throws UnsupportedEncodingException {
        ClientDetailsEntity testClient = new ClientDetailsEntity(clientId);
        List<OrcidOauth2TokenDetail> existingTokens = new ArrayList<OrcidOauth2TokenDetail>();
        OrcidOauth2TokenDetail token1 = new OrcidOauth2TokenDetail();
        Date expirationDate = new Date(System.currentTimeMillis() + 5000);
        token1.setTokenExpiration(expirationDate);
        existingTokens.add(token1);

        when(mock_userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(Matchers.anyString(), Matchers.anyString(), Matchers.anyString())).thenReturn(null);
        when(mock_clientDetailsEntityCacheManager.retrieveByIdP(Matchers.anyString())).thenReturn(testClient);
        when(mock_orcidOauth2TokenDetailDao.findByClientIdAndUserName(Matchers.anyString(), Matchers.anyString())).thenReturn(existingTokens);

        institutionalSignInManager.createUserConnectionAndNotify("idType", "remoteUserId", "displayName", "providerId", userOrcid,
                Collections.<String, String> emptyMap());

        verify(mock_userConnectionDao, times(1)).persist(Matchers.any());
        verify(mock_notificationManager, never()).sendAcknowledgeMessage(userOrcid, clientId);
    }

    @Test
    public void testSendNotificationIfClientHasOnlyExpiredTokensOnUser() throws UnsupportedEncodingException {
        ClientDetailsEntity testClient = new ClientDetailsEntity(clientId);
        List<OrcidOauth2TokenDetail> existingTokens = new ArrayList<OrcidOauth2TokenDetail>();
        OrcidOauth2TokenDetail token1 = new OrcidOauth2TokenDetail();
        Date expirationDate = new Date(System.currentTimeMillis() - 1000);
        token1.setTokenExpiration(expirationDate);
        existingTokens.add(token1);

        when(mock_userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(Matchers.anyString(), Matchers.anyString(), Matchers.anyString())).thenReturn(null);
        when(mock_clientDetailsEntityCacheManager.retrieveByIdP(Matchers.anyString())).thenReturn(testClient);
        when(mock_orcidOauth2TokenDetailDao.findByClientIdAndUserName(Matchers.anyString(), Matchers.anyString())).thenReturn(existingTokens);

        institutionalSignInManager.createUserConnectionAndNotify("idType", "remoteUserId", "displayName", "providerId", userOrcid,
                Collections.<String, String> emptyMap());

        verify(mock_userConnectionDao, times(1)).persist(Matchers.any());
        verify(mock_notificationManager, times(1)).sendAcknowledgeMessage(userOrcid, clientId);
    }

    @Test
    public void testDontSendNotificationIfIdPNotLinkedToClient() throws UnsupportedEncodingException {
        when(mock_userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(Matchers.anyString(), Matchers.anyString(), Matchers.anyString())).thenReturn(null);
        when(mock_clientDetailsEntityCacheManager.retrieveByIdP(Matchers.anyString())).thenThrow(new IllegalArgumentException());
        when(mock_orcidOauth2TokenDetailDao.findByClientIdAndUserName(Matchers.anyString(), Matchers.anyString())).thenReturn(null);

        institutionalSignInManager.createUserConnectionAndNotify("idType", "remoteUserId", "displayName", "providerId", userOrcid,
                Collections.<String, String> emptyMap());

        verify(mock_userConnectionDao, times(1)).persist(Matchers.any());
        verify(mock_notificationManager, never()).sendAcknowledgeMessage(userOrcid, clientId);
    }

    @Test
    public void testDontPersistIfUserConnectionAlreadyExists() throws UnsupportedEncodingException {
        ClientDetailsEntity testClient = new ClientDetailsEntity(clientId);
        when(mock_userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(Matchers.anyString(), Matchers.anyString(), Matchers.anyString()))
                .thenReturn(new UserconnectionEntity());
        when(mock_clientDetailsEntityCacheManager.retrieveByIdP(Matchers.anyString())).thenReturn(testClient);
        when(mock_orcidOauth2TokenDetailDao.findByClientIdAndUserName(Matchers.anyString(), Matchers.anyString())).thenReturn(null);

        institutionalSignInManager.createUserConnectionAndNotify("idType", "remoteUserId", "displayName", "providerId", userOrcid,
                Collections.<String, String> emptyMap());

        verify(mock_userConnectionDao, never()).persist(Matchers.any());
        verify(mock_notificationManager, times(1)).sendAcknowledgeMessage(userOrcid, clientId);
    }

    @Test
    public void testDontPersistAndDontNotify() throws UnsupportedEncodingException {
        List<OrcidOauth2TokenDetail> existingTokens = new ArrayList<OrcidOauth2TokenDetail>();
        OrcidOauth2TokenDetail token1 = new OrcidOauth2TokenDetail();
        Date expirationDate = new Date(System.currentTimeMillis() + 5000);
        token1.setTokenExpiration(expirationDate);
        existingTokens.add(token1);
        when(mock_userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(Matchers.anyString(), Matchers.anyString(), Matchers.anyString()))
                .thenReturn(new UserconnectionEntity());
        when(mock_clientDetailsEntityCacheManager.retrieveByIdP(Matchers.anyString())).thenThrow(new IllegalArgumentException());
        when(mock_orcidOauth2TokenDetailDao.findByClientIdAndUserName(Matchers.anyString(), Matchers.anyString())).thenReturn(existingTokens);

        institutionalSignInManager.createUserConnectionAndNotify("idType", "remoteUserId", "displayName", "providerId", userOrcid,
                Collections.<String, String> emptyMap());

        verify(mock_userConnectionDao, never()).persist(Matchers.any());
        verify(mock_notificationManager, never()).sendAcknowledgeMessage(userOrcid, clientId);
    }

    @Test
    public void testDontSendNotificationIfAtLeastOneTokenIsNotExpired() throws UnsupportedEncodingException {
        ClientDetailsEntity testClient = new ClientDetailsEntity(clientId);
        List<OrcidOauth2TokenDetail> existingTokens = new ArrayList<OrcidOauth2TokenDetail>();

        // Expired 1
        OrcidOauth2TokenDetail token1 = new OrcidOauth2TokenDetail();
        token1.setTokenExpiration(new Date(System.currentTimeMillis() - 1000));

        // Expired 2
        OrcidOauth2TokenDetail token2 = new OrcidOauth2TokenDetail();
        token1.setTokenExpiration(new Date(System.currentTimeMillis() - 2000));

        // Expired 3
        OrcidOauth2TokenDetail token3 = new OrcidOauth2TokenDetail();
        token1.setTokenExpiration(new Date(System.currentTimeMillis() - 2000));

        // Live 1
        OrcidOauth2TokenDetail token4 = new OrcidOauth2TokenDetail();
        token1.setTokenExpiration(new Date(System.currentTimeMillis() + 5000));

        existingTokens.add(token1);
        existingTokens.add(token2);
        existingTokens.add(token3);
        existingTokens.add(token4);

        when(mock_userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(Matchers.anyString(), Matchers.anyString(), Matchers.anyString())).thenReturn(null);
        when(mock_clientDetailsEntityCacheManager.retrieveByIdP(Matchers.anyString())).thenReturn(testClient);
        when(mock_orcidOauth2TokenDetailDao.findByClientIdAndUserName(Matchers.anyString(), Matchers.anyString())).thenReturn(existingTokens);

        institutionalSignInManager.createUserConnectionAndNotify("idType", "remoteUserId", "displayName", "providerId", userOrcid,
                Collections.<String, String> emptyMap());

        verify(mock_userConnectionDao, times(1)).persist(Matchers.any());
        verify(mock_notificationManager, never()).sendAcknowledgeMessage(userOrcid, clientId);
    }

    @Test
    public void testSendNotificationIfAllTokensAreExpired() throws UnsupportedEncodingException {
        ClientDetailsEntity testClient = new ClientDetailsEntity(clientId);
        List<OrcidOauth2TokenDetail> existingTokens = new ArrayList<OrcidOauth2TokenDetail>();

        // Expired 1
        OrcidOauth2TokenDetail token1 = new OrcidOauth2TokenDetail();
        token1.setTokenExpiration(new Date(System.currentTimeMillis() - 1000));

        // Expired 2
        OrcidOauth2TokenDetail token2 = new OrcidOauth2TokenDetail();
        token1.setTokenExpiration(new Date(System.currentTimeMillis() - 2000));

        // Expired 3
        OrcidOauth2TokenDetail token3 = new OrcidOauth2TokenDetail();
        token1.setTokenExpiration(new Date(System.currentTimeMillis() - 2000));

        existingTokens.add(token1);
        existingTokens.add(token2);
        existingTokens.add(token3);

        when(mock_userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(Matchers.anyString(), Matchers.anyString(), Matchers.anyString())).thenReturn(null);
        when(mock_clientDetailsEntityCacheManager.retrieveByIdP(Matchers.anyString())).thenReturn(testClient);
        when(mock_orcidOauth2TokenDetailDao.findByClientIdAndUserName(Matchers.anyString(), Matchers.anyString())).thenReturn(existingTokens);

        institutionalSignInManager.createUserConnectionAndNotify("idType", "remoteUserId", "displayName", "providerId", userOrcid,
                Collections.<String, String> emptyMap());

        verify(mock_userConnectionDao, times(1)).persist(Matchers.any());
        verify(mock_notificationManager, times(1)).sendAcknowledgeMessage(userOrcid, clientId);
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
    }

}
