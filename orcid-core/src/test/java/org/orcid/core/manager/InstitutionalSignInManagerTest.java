package org.orcid.core.manager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.impl.InstitutionalSignInManagerImpl;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.utils.JsonUtils;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.pojo.HeaderCheckResult;
import org.orcid.pojo.HeaderMismatch;
import org.orcid.pojo.RemoteUser;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Mock
    private OrcidUrlManager mock_orcidUrlManager;

    private InstitutionalSignInManagerImpl institutionalSignInManager;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        // Use a dummy discoFeedSource to avoid network calls in constructor
        institutionalSignInManager = new InstitutionalSignInManagerImpl("http://localhost/dummy");
        
        ReflectionTestUtils.setField(institutionalSignInManager, "userConnectionDao", mock_userConnectionDao);
        ReflectionTestUtils.setField(institutionalSignInManager, "clientDetailsEntityCacheManager", mock_clientDetailsEntityCacheManager);
        ReflectionTestUtils.setField(institutionalSignInManager, "notificationManager", mock_notificationManager);
        ReflectionTestUtils.setField(institutionalSignInManager, "orcidOauth2TokenDetailService", mock_orcidOauth2TokenDetailService);
        ReflectionTestUtils.setField(institutionalSignInManager, "orcidUrlManager", mock_orcidUrlManager);
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

    @Test
    public void testRetrieveRemoteUser() {
        Map<String, String> headers = new HashMap<>();
        headers.put("persistent-id", "user1");
        RemoteUser remoteUser = institutionalSignInManager.retrieveRemoteUser(headers);
        assertNotNull(remoteUser);
        assertEquals("user1", remoteUser.getUserId());
        assertEquals("persistent-id", remoteUser.getIdType());

        headers.clear();
        headers.put("edu-person-unique-id", "user2");
        remoteUser = institutionalSignInManager.retrieveRemoteUser(headers);
        assertNotNull(remoteUser);
        assertEquals("user2", remoteUser.getUserId());
        assertEquals("edu-person-unique-id", remoteUser.getIdType());

        headers.clear();
        headers.put("targeted-id-oid", "user3");
        remoteUser = institutionalSignInManager.retrieveRemoteUser(headers);
        assertNotNull(remoteUser);
        assertEquals("user3", remoteUser.getUserId());
        assertEquals("targeted-id-oid", remoteUser.getIdType());

        headers.clear();
        headers.put("targeted-id", "user4");
        remoteUser = institutionalSignInManager.retrieveRemoteUser(headers);
        assertNotNull(remoteUser);
        assertEquals("user4", remoteUser.getUserId());
        assertEquals("targeted-id", remoteUser.getIdType());

        headers.clear();
        headers.put("subject-id", "user5");
        remoteUser = institutionalSignInManager.retrieveRemoteUser(headers);
        assertNotNull(remoteUser);
        assertEquals("user5", remoteUser.getUserId());
        assertEquals("subject-id", remoteUser.getIdType());

        headers.clear();
        assertNull(institutionalSignInManager.retrieveRemoteUser(headers));
    }

    @Test
    public void testRetrieveDisplayName() {
        Map<String, String> headers = new HashMap<>();
        headers.put("eppn", "test@orcid.org");
        assertEquals("test@orcid.org", institutionalSignInManager.retrieveDisplayName(headers));

        headers.clear();
        assertNull(institutionalSignInManager.retrieveDisplayName(headers));
    }

    @Test
    public void testRetrieveFirstName() {
        Map<String, String> headers = new HashMap<>();
        headers.put("givenname", "John");
        assertEquals("John", institutionalSignInManager.retrieveFirstName(headers));

        headers.clear();
        assertEquals("", institutionalSignInManager.retrieveFirstName(headers));
    }

    @Test
    public void testRetrieveLastName() {
        Map<String, String> headers = new HashMap<>();
        headers.put("sn", "Doe");
        assertEquals("Doe", institutionalSignInManager.retrieveLastName(headers));

        headers.clear();
        assertEquals("", institutionalSignInManager.retrieveLastName(headers));
    }

    @Test
    public void testExtractFirstWithEscapedSemicolon() {
        Map<String, String> headers = new HashMap<>();
        // Shibboleth escapes semicolons with backslash
        headers.put("persistent-id", "user\\;name;anotheruser");
        RemoteUser remoteUser = institutionalSignInManager.retrieveRemoteUser(headers);
        assertNotNull(remoteUser);
        assertEquals("user;name", remoteUser.getUserId());
    }

    @Test
    public void testGetInstitutionName() {
        // Since we can't easily populate the map without network, we can use reflection to set it for testing
        Map<String, String> institutionNames = new HashMap<>();
        institutionNames.put("provider1", "Institution 1");
        ReflectionTestUtils.setField(institutionalSignInManager, "institutionNames", institutionNames);

        assertEquals("Institution 1", institutionalSignInManager.getInstitutionName("provider1"));
        assertNull(institutionalSignInManager.getInstitutionName("unknown"));
    }
}
