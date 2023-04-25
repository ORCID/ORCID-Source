package org.orcid.core.manager.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.WebhookManager;
import org.orcid.core.utils.http.HttpRequestUtils;
import org.orcid.persistence.dao.WebhookDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.orcid.test.TargetProxyHelper;

public class WebhookManagerImplTest extends BaseTest {

    @Resource
    private WebhookManager webhookManager;
    
    @Resource
    private WebhookDao webhookDao;
    
    @Mock
    private WebhookDao webhookDaoMock;
    
    @Mock
    private HttpRequestUtils httpRequestUtilsMock;
    
    @Mock 
    HttpResponse<String> mockResponseOk;
    
    @Mock 
    HttpResponse<String> mockResponseNotFound;
    
    private static final List<String> DATA_FILES = Arrays.asList("/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/WebhookEntityData.xml");

    private ClientDetailsEntity clientDetails;
    
    private String orcid;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }
    
    @Before
    public void before() throws Exception {
        orcid = "4444-4444-4444-4444";
        
        clientDetails = new ClientDetailsEntity();
        clientDetails.setGroupProfileId(orcid);
        clientDetails.setId("123456789");
        List<String> headers = new ArrayList<>();
        headers.add("Content-Length: 0");
        
        TargetProxyHelper.injectIntoProxy(webhookManager, "httpRequestUtils", httpRequestUtilsMock);
        TargetProxyHelper.injectIntoProxy(webhookManager, "webhookDao", webhookDaoMock);
        
        when(mockResponseOk.statusCode()).thenReturn(200);
        when(mockResponseNotFound.statusCode()).thenReturn(404);
        
        when(httpRequestUtilsMock.doPost(anyString(), headers )).thenThrow(new HttpConnectTimeoutException("Error"));
        when(httpRequestUtilsMock.doPost(eq("http://qa-1.orcid.org"),headers ) ).thenReturn(mockResponseOk);
        when(httpRequestUtilsMock.doPost(eq("http://unexisting.orcid.com"),headers )).thenReturn(mockResponseNotFound);   
        when(httpRequestUtilsMock.doPost(eq("http://nowhere.com/orcid/4444-4444-4444-4443"),headers )).thenReturn(mockResponseOk);        
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
        for (int i = 0; i < 4; i++) {
            webhookManager.processWebhook(webhook);
        }
        verify(webhookDaoMock, times(4)).markAsFailed(eq(orcid), eq("http://unexisting.orcid.com"));
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
        TargetProxyHelper.injectIntoProxy(webhookManager, "webhookDao", webhookDao);
        
        Date now = new Date();
        List<WebhookEntity> webhooks = webhookDao.findWebhooksReadyToProcess(now, 5, 10);
        assertEquals(1, webhooks.size());
        webhookManager.processWebhooks();
        webhooks = webhookDao.findWebhooksReadyToProcess(now, 5, 10);
        assertEquals(0, webhooks.size());
        
        TargetProxyHelper.injectIntoProxy(webhookManager, "webhookDao", webhookDaoMock);        
    }
    
}
