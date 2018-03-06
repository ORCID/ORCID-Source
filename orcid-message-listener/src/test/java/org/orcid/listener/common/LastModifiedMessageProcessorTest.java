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
package org.orcid.listener.common;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.message.OrcidDeprecated;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.listener.orcid.Orcid12APIClient;
import org.orcid.listener.orcid.Orcid20APIClient;
import org.orcid.listener.persistence.managers.RecordStatusManager;
import org.orcid.listener.persistence.util.AvailableBroker;
import org.orcid.listener.s3.ExceptionHandler;
import org.orcid.listener.s3.S3MessageProcessor;
import org.orcid.listener.s3.S3Manager;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.listener.LastModifiedMessage;
import org.orcid.utils.listener.MessageConstants;
import org.springframework.test.context.ContextConfiguration;

import com.amazonaws.AmazonClientException;
import com.amazonaws.util.IOUtils;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-message-listener-test-context.xml" })
public class LastModifiedMessageProcessorTest {

    @Resource
    private S3MessageProcessor processor;

    @Mock
    private Orcid12APIClient mock_orcid12ApiClient;

    @Mock
    private Orcid20APIClient mock_orcid20ApiClient;

    @Mock
    private S3Manager mock_s3Manager;

    @Mock
    private ExceptionHandler mock_exceptionHandler;

    @Mock
    private RecordStatusManager mock_recordStatusManager;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(processor, "orcid12ApiClient", mock_orcid12ApiClient);
        TargetProxyHelper.injectIntoProxy(processor, "orcid20ApiClient", mock_orcid20ApiClient);
        TargetProxyHelper.injectIntoProxy(processor, "exceptionHandler", mock_exceptionHandler);
        TargetProxyHelper.injectIntoProxy(processor, "recordStatusManager", mock_recordStatusManager);
        TargetProxyHelper.injectIntoProxy(processor, "s3Manager", mock_s3Manager);
    }

    @Test
    public void recordLockedExceptionTest() throws LockedRecordException, AmazonClientException, JAXBException, DeprecatedRecordException, IOException {
        when(mock_orcid12ApiClient.fetchPublicProfile(anyString(), anyString())).thenThrow(new LockedRecordException(new OrcidMessage()));
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(null);
        String orcid = "0000-0000-0000-0000";
        execute(orcid);
        verify(mock_exceptionHandler, times(1)).handle12LockedRecordException(any(), any());
    }

    @Test
    public void deprecatedRecordExceptionTest() throws LockedRecordException, AmazonClientException, JAXBException, DeprecatedRecordException, IOException {
        when(mock_orcid12ApiClient.fetchPublicProfile(anyString(), anyString())).thenThrow(new DeprecatedRecordException(new OrcidDeprecated()));
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(null);
        String orcid = "0000-0000-0000-0000";
        execute(orcid);
        verify(mock_exceptionHandler, times(1)).handle12DeprecatedRecordException(any(), any());
    }

    @Test
    public void recordLocked20DeprecatedExceptionTest() throws LockedRecordException, AmazonClientException, JAXBException, DeprecatedRecordException, IOException {
        when(mock_orcid12ApiClient.fetchPublicProfile(anyString(), anyString())).thenReturn(null);
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenThrow(new DeprecatedRecordException(new OrcidDeprecated()));
        String orcid = "0000-0000-0000-0000";
        execute(orcid);
        verify(mock_exceptionHandler, times(1)).handle20Exception(any(), any());
    }

    @Test
    public void recordLocked20LockedExceptionTest() throws LockedRecordException, AmazonClientException, JAXBException, DeprecatedRecordException, IOException {
        when(mock_orcid12ApiClient.fetchPublicProfile(anyString(), anyString())).thenReturn(null);
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenThrow(new LockedRecordException(new OrcidError()));
        String orcid = "0000-0000-0000-0000";
        execute(orcid);
        verify(mock_exceptionHandler, times(1)).handle20Exception(any(), any());
    }

    @Test
    public void recordStatusMarkAsSentFor12Test() throws LockedRecordException, DeprecatedRecordException, IOException {
        byte[] bytes = IOUtils.toByteArray(new ByteArrayInputStream(SerializationUtils.serialize(new OrcidMessage())));
        when(mock_orcid12ApiClient.fetchPublicProfile(anyString(), anyString())).thenReturn(bytes);
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(null);
        String orcid = "0000-0000-0000-0000";
        execute(orcid);
        verify(mock_recordStatusManager, times(1)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
        verify(mock_recordStatusManager, times(0)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
        verify(mock_recordStatusManager, times(0)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
        verify(mock_recordStatusManager, times(0)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
    }

    @Test
    public void recordStatusMarkAsFailedFor12Test() throws LockedRecordException, DeprecatedRecordException, IOException {
        when(mock_orcid12ApiClient.fetchPublicProfile(anyString(), anyString())).thenThrow(new RuntimeException());
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(null);
        String orcid = "0000-0000-0000-0000";
        execute(orcid);
        verify(mock_recordStatusManager, times(0)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
        verify(mock_recordStatusManager, times(1)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
        verify(mock_recordStatusManager, times(0)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
        verify(mock_recordStatusManager, times(0)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
    }

    @Test
    public void recordStatusMarkAsSentFor20Test() throws LockedRecordException, DeprecatedRecordException, IOException {
        when(mock_orcid12ApiClient.fetchPublicProfile(anyString(), anyString())).thenReturn(null);
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(new Record());
        String orcid = "0000-0000-0000-0000";
        execute(orcid);
        verify(mock_recordStatusManager, times(0)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
        verify(mock_recordStatusManager, times(1)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
        verify(mock_recordStatusManager, times(1)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
        verify(mock_recordStatusManager, times(0)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
    }

    @Test
    public void recordStatusMarkAsFailedFor20Test() throws LockedRecordException, DeprecatedRecordException, IOException {
        when(mock_orcid12ApiClient.fetchPublicProfile(anyString(), anyString())).thenReturn(null);
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenThrow(new RuntimeException());
        String orcid = "0000-0000-0000-0000";
        execute(orcid);
        verify(mock_recordStatusManager, times(0)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
        verify(mock_recordStatusManager, times(1)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
        verify(mock_recordStatusManager, times(0)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
        verify(mock_recordStatusManager, times(1)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
    }

    @Test
    public void recrodStatusMarkAsSentForLockedRecordException12Test() throws LockedRecordException, DeprecatedRecordException, IOException {
        when(mock_orcid12ApiClient.fetchPublicProfile(anyString(), anyString())).thenThrow(new LockedRecordException(new OrcidMessage()));
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(null);
        String orcid = "0000-0000-0000-0000";
        execute(orcid);
        verify(mock_recordStatusManager, times(1)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
        verify(mock_recordStatusManager, times(0)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
        verify(mock_recordStatusManager, times(0)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
        verify(mock_recordStatusManager, times(0)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
    }

    @Test
    public void recrodStatusMarkAsSentForLockedRecordException20Test() throws LockedRecordException, DeprecatedRecordException, IOException {
        when(mock_orcid12ApiClient.fetchPublicProfile(anyString(), anyString())).thenReturn(null);
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenThrow(new LockedRecordException(new OrcidMessage()));
        String orcid = "0000-0000-0000-0000";
        execute(orcid);
        verify(mock_recordStatusManager, times(0)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
        verify(mock_recordStatusManager, times(1)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
        verify(mock_recordStatusManager, times(1)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
        verify(mock_recordStatusManager, times(0)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
    }

    @Test
    public void recrodStatusMarkAsSentForDeprecatedRecordException12Test() throws LockedRecordException, DeprecatedRecordException, IOException {
        when(mock_orcid12ApiClient.fetchPublicProfile(anyString(), anyString())).thenThrow(new DeprecatedRecordException(new OrcidDeprecated()));
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenReturn(null);
        String orcid = "0000-0000-0000-0000";
        execute(orcid);
        verify(mock_recordStatusManager, times(1)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
        verify(mock_recordStatusManager, times(0)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
        verify(mock_recordStatusManager, times(0)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
        verify(mock_recordStatusManager, times(0)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
    }

    @Test
    public void recrodStatusMarkAsSentForDeprecatedRecordException20Test() throws LockedRecordException, DeprecatedRecordException, IOException {
        when(mock_orcid12ApiClient.fetchPublicProfile(anyString(), anyString())).thenReturn(null);
        when(mock_orcid20ApiClient.fetchPublicRecord(any())).thenThrow(new DeprecatedRecordException(new OrcidDeprecated()));
        String orcid = "0000-0000-0000-0000";
        execute(orcid);
        verify(mock_recordStatusManager, times(0)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
        verify(mock_recordStatusManager, times(1)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
        verify(mock_recordStatusManager, times(1)).markAsSent(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
        verify(mock_recordStatusManager, times(0)).markAsFailed(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
    }

    private void execute(String orcid) {
        Map<String, String> map = new HashMap<String, String>();
        String date = String.valueOf(System.currentTimeMillis());
        map.put(MessageConstants.ORCID.value, orcid);
        map.put(MessageConstants.DATE.value, date);
        map.put(MessageConstants.TYPE.value, MessageConstants.TYPE_LAST_UPDATED.value);
        LastModifiedMessage message = new LastModifiedMessage(map);
        processor.accept(message);
    }
}