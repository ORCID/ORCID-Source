package org.orcid.cron;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.listener.persistence.entities.RecordStatusEntity;
import org.orcid.listener.persistence.managers.RecordStatusManager;
import org.orcid.test.TargetProxyHelper;
import org.springframework.jms.core.JmsTemplate;

import com.sun.jersey.api.client.Client;

public class HandleFailedMessagesTest {

    @Mock
    private RecordStatusManager mock_manager;
    
    @Mock
    private JmsTemplate mock_jmsTemplate;
    
    @Mock
    private Client mock_client;
    
    private HandleFailedMessages h = new HandleFailedMessages(); 
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(h, "manager", mock_manager);
        TargetProxyHelper.injectIntoProxy(h, "jmsTemplate", mock_jmsTemplate);
        TargetProxyHelper.injectIntoProxy(h, "maxFailuresBeforeNotify", 3);
        
        List<RecordStatusEntity> elements = new ArrayList<RecordStatusEntity>();
        for(int i = 0; i < 10; i++) {
            RecordStatusEntity r = new RecordStatusEntity();
            r.setDateCreated(new Date());
            r.setDumpStatus12Api(i);
            r.setDumpStatus20Api(0);
            r.setSolrStatus20Api(0);
            r.setId("0000-0000-0000-000" + i);
            elements.add(r);
        }
        
        when(mock_manager.getFailedElements(Matchers.anyInt())).thenReturn(elements);
    }
    
    @Test
    public void resendFailedElementsTest() {
        h.resendFailedElements();
        verify(mock_jmsTemplate, times(9)).convertAndSend(Matchers.anyString(), Matchers.anyMapOf(String.class, String.class));
    }
}
