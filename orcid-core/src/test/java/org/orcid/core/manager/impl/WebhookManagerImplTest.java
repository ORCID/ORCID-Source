package org.orcid.core.manager.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.test.TargetProxyHelper;
import org.orcid.core.utils.http.HttpRequestUtils;
import org.orcid.persistence.dao.WebhookDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.WebhookEntity;
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
    HttpResponse<String> mockResponseOk;
    
    @Mock 
    HttpResponse<String> mockResponseNotFound;

    @Mock
    HttpResponse<String> mockResponseRateLimited;
    
    private ClientDetailsEntity clientDetails;
    
    private String orcid;
    
    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        orcid = "4444-4444-4444-4444";
        
        clientDetails = new ClientDetailsEntity();
        clientDetails.setGroupProfileId(orcid);
        clientDetails.setId("123456789");
        
        webhookManager = new WebhookManagerImpl();
        webhookManager.setWebhookDao(webhookDaoMock);
        TargetProxyHelper.injectIntoProxy(webhookManager, "webhookDaoReadOnly", webhookDaoReadOnlyMock);
        TargetProxyHelper.injectIntoProxy(webhookManager, "transactionTemplate", transactionTemplateMock);
        TargetProxyHelper.injectIntoProxy(webhookManager, "httpRequestUtils", httpRequestUtilsMock);

        webhookManager.setMaxJobsPerClient(5);
        webhookManager.setNumberOfWebhookThreads(2);
        webhookManager.setRetryDelayMinutes(5);
        webhookManager.setMaxPerRun(10);
        TargetProxyHelper.injectIntoProxy(webhookManager, "webhooksBatchSize", 100);
        
        // Mock transaction template to execute the callback
        doAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        }).when(transactionTemplateMock).execute(any());

        when(mockResponseOk.statusCode()).thenReturn(200);
        when(mockResponseNotFound.statusCode()).thenReturn(404);
        when(mockResponseRateLimited.statusCode()).thenReturn(429);
        
        when(httpRequestUtilsMock.doPost(anyString())).thenThrow(new HttpConnectTimeoutException("Error"));
        when(httpRequestUtilsMock.doPost(eq("http://qa-1.orcid.org"))).thenReturn(mockResponseOk);
        when(httpRequestUtilsMock.doPost(eq("http://unexisting.orcid.com"))).thenReturn(mockResponseNotFound);   
        when(httpRequestUtilsMock.doPost(eq("http://nowhere.com/orcid/4444-4444-4444-4443"))).thenReturn(mockResponseOk);
        when(httpRequestUtilsMock.doPost(eq("http://ratelimited.orcid.org"))).thenReturn(mockResponseRateLimited);
    }
    
    @Test
    public void testValidUriOnWebhook() {
        WebhookEntity webhook = new WebhookEntity();
        webhook.setClientDetailsId(clientDetails.getId());
        webhook.setUri("http://qa-1.orcid.org");
        webhook.setProfile(orcid);
        for(int i = 0; i < 4; i++) {
            webhookManager.processWebhook(webhook);
        }
        verify(webhookDaoMock, times(4)).markAsSent(eq(orcid), eq("http://qa-1.orcid.org"));
    }

    @Test
    public void testUnexsistingUriOnWebhook() {
        WebhookEntity webhook = new WebhookEntity();
        webhook.setClientDetailsId(clientDetails.getId());
        webhook.setUri("http://unexisting.orcid.com");
        webhook.setProfile(orcid);
        webhookManager.processWebhook(webhook);
        verify(webhookDaoMock, times(1)).markAsFailed(eq(orcid), eq("http://unexisting.orcid.com"));
        // Should have retried only once in this call, letting the scheduler handle retries later
        try {
            verify(httpRequestUtilsMock, times(1)).doPost(eq("http://unexisting.orcid.com"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testRateLimitedWebhook() {
        WebhookEntity webhook = new WebhookEntity();
        webhook.setClientDetailsId(clientDetails.getId());
        webhook.setUri("http://ratelimited.orcid.org");
        webhook.setProfile(orcid);
        
        webhookManager.processWebhook(webhook);
        
        verify(webhookDaoMock, times(1)).markAsFailed(eq(orcid), eq("http://ratelimited.orcid.org"));
        try {
            // Should NOT retry 429
            verify(httpRequestUtilsMock, times(1)).doPost(eq("http://ratelimited.orcid.org"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        // Subsequent webhooks for same client should be skipped
        WebhookEntity webhook2 = new WebhookEntity();
        webhook2.setClientDetailsId(clientDetails.getId());
        webhook2.setUri("http://qa-1.orcid.org");
        webhook2.setProfile(orcid);
        
        webhookManager.processWebhook(webhook2);
        // Should NOT have called doPost for webhook2 because client is blacklisted
        try {
            verify(httpRequestUtilsMock, times(0)).doPost(eq("http://qa-1.orcid.org"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testHandleExceptions() {
        WebhookEntity webhook = new WebhookEntity();
        webhook.setClientDetailsId(clientDetails.getId());
        webhook.setUri("InvalidUrl");
        webhook.setProfile(orcid);
        webhookManager.processWebhook(webhook);
        verify(webhookDaoMock, times(1)).markAsFailed(orcid, "InvalidUrl");
    }
    
    @Test
    public void testProcessWebhooks() {
        WebhookEntity webhook = new WebhookEntity();
        webhook.setClientDetailsId(clientDetails.getId());
        webhook.setUri("http://qa-1.orcid.org");
        webhook.setProfile(orcid);
        
        List<WebhookEntity> webhooks = new ArrayList<>();
        webhooks.add(webhook);
        
        when(webhookDaoReadOnlyMock.findWebhooksReadyToProcess(any(Date.class), anyInt(), anyInt()))
            .thenReturn(webhooks)
            .thenReturn(new ArrayList<WebhookEntity>());
        
        when(webhookDaoReadOnlyMock.countWebhooksReadyToProcess(any(Date.class), anyInt()))
            .thenReturn(1L);
            
        webhookManager.processWebhooks();
        
        verify(webhookDaoReadOnlyMock, times(2)).findWebhooksReadyToProcess(any(Date.class), anyInt(), anyInt());
        verify(webhookDaoMock, times(1)).markAsSent(eq(orcid), eq("http://qa-1.orcid.org"));
    }
    
}
