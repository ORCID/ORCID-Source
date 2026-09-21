package org.orcid.core.manager.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.utils.http.HttpRequestUtils;
import org.orcid.persistence.dao.WebhookDao;
import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.orcid.test.TargetProxyHelper;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class WebhookManagerImplTest {

    @InjectMocks
    private WebhookManagerImpl webhookManager;

    @Mock
    private WebhookDao webhookDaoMock;

    @Mock
    private WebhookDao webhookDaoReadOnlyMock;

    @Mock
    private TransactionTemplate transactionTemplateMock;

    @Mock
    private HttpRequestUtils httpRequestUtilsMock;

    @Mock
    private HttpResponse<String> mockResponseOk;

    @Mock
    private HttpResponse<String> mockResponseNotFound;

    @Mock
    private HttpResponse<String> mockResponseRateLimited;

    private static final String ORCID = "0000-0000-0000-0001";
    private static final String CLIENT_ID = "CLIENT-123";
    private static final String VALID_URI = "http://example.com/webhook";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        // Inject dependencies and values
        TargetProxyHelper.injectIntoProxy(webhookManager, "webhookDao", webhookDaoMock);
        TargetProxyHelper.injectIntoProxy(webhookManager, "webhookDaoReadOnly", webhookDaoReadOnlyMock);
        TargetProxyHelper.injectIntoProxy(webhookManager, "transactionTemplate", transactionTemplateMock);
        TargetProxyHelper.injectIntoProxy(webhookManager, "httpRequestUtils", httpRequestUtilsMock);
        TargetProxyHelper.injectIntoProxy(webhookManager, "webhooksBatchSize", 100);
        TargetProxyHelper.injectIntoProxy(webhookManager, "retryDelayMinutes", 5);
        TargetProxyHelper.injectIntoProxy(webhookManager, "maxIterations", 3);

        // Mock transaction template to execute the callback
        doAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        }).when(transactionTemplateMock).execute(any());

        when(mockResponseOk.statusCode()).thenReturn(200);
        when(mockResponseNotFound.statusCode()).thenReturn(404);
        when(mockResponseRateLimited.statusCode()).thenReturn(429);
    }

    @Test
    public void testProcessWebhooks_Success() throws Exception {
        WebhookEntity webhook = createWebhook(VALID_URI, ORCID, CLIENT_ID);
        
        when(webhookDaoReadOnlyMock.findWebhooksReadyToProcess(any(Date.class), anyInt(), anyInt(), anySet()))
            .thenReturn(new ArrayList<>(Collections.singletonList(webhook)))
            .thenReturn(new ArrayList<>());
        
        when(httpRequestUtilsMock.doPost(VALID_URI)).thenReturn(mockResponseOk);
        
        webhookManager.processWebhooks();
        
        verify(httpRequestUtilsMock, times(1)).doPost(VALID_URI);
        verify(webhookDaoMock, times(1)).markAsSent(ORCID, VALID_URI);
        verify(webhookDaoReadOnlyMock, atLeastOnce()).findWebhooksReadyToProcess(any(Date.class), anyInt(), anyInt(), anySet());
    }

    @Test
    public void testProcessWebhooks_FailedStatus() throws Exception {
        WebhookEntity webhook = createWebhook(VALID_URI, ORCID, CLIENT_ID);
        
        when(webhookDaoReadOnlyMock.findWebhooksReadyToProcess(any(Date.class), anyInt(), anyInt(), anySet()))
            .thenReturn(new ArrayList<>(Collections.singletonList(webhook)))
            .thenReturn(new ArrayList<>());
        
        when(httpRequestUtilsMock.doPost(VALID_URI)).thenReturn(mockResponseNotFound);
        
        webhookManager.processWebhooks();
        
        verify(httpRequestUtilsMock, times(1)).doPost(VALID_URI);
        verify(webhookDaoMock, times(1)).markAsFailed(ORCID, VALID_URI);
    }

    @Test
    public void testProcessWebhooks_RateLimited_Blacklisting() throws Exception {
        WebhookEntity webhook1 = createWebhook("http://uri1.com", ORCID, CLIENT_ID);
        WebhookEntity webhook2 = createWebhook("http://uri2.com", ORCID, "OTHER_CLIENT");
        WebhookEntity webhook3 = createWebhook("http://uri3.com", ORCID, CLIENT_ID);
        
        List<WebhookEntity> batch1 = new ArrayList<>();
        batch1.add(webhook1);
        batch1.add(webhook2);

        List<WebhookEntity> batch2 = new ArrayList<>();
        batch2.add(webhook3);
        
        // Return batch1, then batch2, then empty
        // In reality, findWebhooksReadyToProcess takes blacklistedClients as an argument,
        // so we should verify that it is populated.
        when(webhookDaoReadOnlyMock.findWebhooksReadyToProcess(any(Date.class), anyInt(), anyInt(), anySet()))
            .thenReturn(new ArrayList<>(batch1))
            .thenReturn(new ArrayList<>(batch2))
            .thenReturn(new ArrayList<>());
        
        // First one is rate limited
        when(httpRequestUtilsMock.doPost("http://uri1.com")).thenReturn(mockResponseRateLimited);
        when(httpRequestUtilsMock.doPost("http://uri2.com")).thenReturn(mockResponseOk);
        when(httpRequestUtilsMock.doPost("http://uri3.com")).thenReturn(mockResponseOk);
        
        webhookManager.processWebhooks();
        
        verify(webhookDaoMock).markAsFailed(ORCID, "http://uri1.com");
        verify(webhookDaoMock).markAsSent(ORCID, "http://uri2.com");
        
        // webhook3 might be skipped if it's in the same run but after CLIENT_ID is blacklisted.
        // If it was in the SECOND batch, it definitely should be skipped because WebhookManagerImpl
        // calls isBlacklisted(clientId) before processing.
        
        // Verify that findWebhooksReadyToProcess was called with blacklisted client in later calls
        verify(webhookDaoReadOnlyMock, atLeastOnce()).findWebhooksReadyToProcess(any(Date.class), anyInt(), anyInt(), argThat(set -> set.contains(CLIENT_ID)));
    }

    @Test
    public void testProcessWebhooks_IterationLimit() throws Exception {
        TargetProxyHelper.injectIntoProxy(webhookManager, "maxIterations", 2);
        
        when(webhookDaoReadOnlyMock.findWebhooksReadyToProcess(any(Date.class), anyInt(), anyInt(), anySet()))
            .thenAnswer(invocation -> {
                return new ArrayList<>(Collections.singletonList(createWebhook(VALID_URI, ORCID, CLIENT_ID)));
            });
            
        when(httpRequestUtilsMock.doPost(anyString())).thenReturn(mockResponseOk);
        
        webhookManager.processWebhooks();
        
        // Should stop after 2 iterations
        verify(webhookDaoReadOnlyMock, times(2)).findWebhooksReadyToProcess(any(Date.class), anyInt(), anyInt(), anySet());
    }

    @Test
    public void testProcessWebhooks_ExceptionHandling() throws Exception {
        WebhookEntity webhook = createWebhook(VALID_URI, ORCID, CLIENT_ID);
        
        when(webhookDaoReadOnlyMock.findWebhooksReadyToProcess(any(Date.class), anyInt(), anyInt(), anySet()))
            .thenReturn(new ArrayList<>(Collections.singletonList(webhook)))
            .thenReturn(new ArrayList<>());
        
        when(httpRequestUtilsMock.doPost(VALID_URI)).thenThrow(new RuntimeException("Network error"));
        
        webhookManager.processWebhooks();
        
        verify(webhookDaoMock, times(1)).markAsFailed(ORCID, VALID_URI);
    }

    @Test
    public void testProcessWebhooks_InvalidUriHandling() throws Exception {
        // Implementation prepends http:// if missing
        WebhookEntity webhook = createWebhook("example.com", ORCID, CLIENT_ID);
        
        when(webhookDaoReadOnlyMock.findWebhooksReadyToProcess(any(Date.class), anyInt(), anyInt(), anySet()))
            .thenReturn(new ArrayList<>(Collections.singletonList(webhook)))
            .thenReturn(new ArrayList<>());
        
        when(httpRequestUtilsMock.doPost("http://example.com")).thenReturn(mockResponseOk);
        
        webhookManager.processWebhooks();
        
        verify(httpRequestUtilsMock).doPost("http://example.com");
        verify(webhookDaoMock).markAsSent(ORCID, "example.com");
    }

    private WebhookEntity createWebhook(String uri, String orcid, String clientId) {
        WebhookEntity webhook = new WebhookEntity();
        webhook.setUri(uri);
        webhook.setProfile(orcid);
        webhook.setClientDetailsId(clientId);
        return webhook;
    }
}
