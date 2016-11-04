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
package org.orcid.listener;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.jaxb.model.message.OrcidDeprecated;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.listener.clients.Orcid12APIClient;
import org.orcid.listener.clients.Orcid20APIClient;
import org.orcid.listener.clients.S3Updater;
import org.orcid.listener.common.ExceptionHandler;
import org.orcid.listener.common.LastModifiedMessageProcessor;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.listener.MessageConstants;
import org.springframework.test.context.ContextConfiguration;

import com.amazonaws.AmazonClientException;
import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-message-listener-test-context.xml" })
public class ReIndexListenerTest {

    @Resource
    private ReIndexListener reIndexListener;
    
    @Resource
    private LastModifiedMessageProcessor processor;
    
    @Mock
    private Orcid12APIClient mock_orcid12ApiClient;
    
    @Mock
    private Orcid20APIClient mock_orcid20ApiClient;
    
    @Mock
    private S3Updater mock_s3Updater; 
    
    @Mock
    private ExceptionHandler mock_exceptionHandler;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);        
        TargetProxyHelper.injectIntoProxy(processor, "orcid12ApiClient", mock_orcid12ApiClient);                
        TargetProxyHelper.injectIntoProxy(processor, "orcid20ApiClient", mock_orcid20ApiClient);
        TargetProxyHelper.injectIntoProxy(processor, "exceptionHandler", mock_exceptionHandler);
        TargetProxyHelper.injectIntoProxy(processor, "s3Updater", mock_s3Updater);
        TargetProxyHelper.injectIntoProxy(reIndexListener,"processor", processor);
    }
    
    @Test
    public void recordLockedExceptionTest() throws LockedRecordException, JsonProcessingException, AmazonClientException, JAXBException, DeprecatedRecordException {
        when(mock_orcid12ApiClient.fetchPublicProfile(Matchers.anyString())).thenThrow(new LockedRecordException(new OrcidMessage()));
        Map<String, String> map = new HashMap<String, String>();
        String orcid = "0000-0000-0000-0000";
        String date = String.valueOf(System.currentTimeMillis());
        map.put(MessageConstants.ORCID.value, orcid);
        map.put(MessageConstants.DATE.value, date);
        map.put(MessageConstants.TYPE.value, MessageConstants.TYPE_LAST_UPDATED.value);
        reIndexListener.processMessage(map);
        verify(mock_exceptionHandler, times(1)).handleLockedRecordException(Matchers.any(), Matchers.any());
    }
    
    @Test
    public void deprecatedRecordExceptionTest() throws LockedRecordException, JsonProcessingException, AmazonClientException, JAXBException, DeprecatedRecordException {
        when(mock_orcid12ApiClient.fetchPublicProfile(Matchers.anyString())).thenThrow(new DeprecatedRecordException(new OrcidDeprecated()));
        Map<String, String> map = new HashMap<String, String>();
        String orcid = "0000-0000-0000-0000";
        String date = String.valueOf(System.currentTimeMillis());
        map.put(MessageConstants.ORCID.value, orcid);
        map.put(MessageConstants.DATE.value, date);
        map.put(MessageConstants.TYPE.value, MessageConstants.TYPE_LAST_UPDATED.value);
        reIndexListener.processMessage(map);
        verify(mock_exceptionHandler, times(1)).handleDeprecatedRecordException(Matchers.any(), Matchers.any());
    }
}
