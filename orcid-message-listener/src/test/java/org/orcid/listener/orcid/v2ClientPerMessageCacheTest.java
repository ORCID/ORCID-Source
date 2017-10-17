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
package org.orcid.listener.orcid;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.jaxb.model.common_v2.OrcidIdentifier;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.listener.BaseMessage;
import org.orcid.utils.listener.LastModifiedMessage;
import org.springframework.test.context.ContextConfiguration;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

import org.mockito.Matchers;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-message-listener-test-context.xml" })
public class v2ClientPerMessageCacheTest {

    @Resource
    private Orcid20APIClient orcid20ApiClient;
    
    @Mock
    private Client mock_jerseyClient;
    
    @Mock WebResource mock_resource;
    
    @Mock Builder mock_builder;
    
    @Mock ClientResponse mock_response;

    Record record = new Record();
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);        
        TargetProxyHelper.injectIntoProxy(orcid20ApiClient, "jerseyClient", mock_jerseyClient);
        record.setOrcidIdentifier(new OrcidIdentifier("http://orcid.org/0000-0000-0000-0000"));
    }
    
    @Test
    public void testCache() throws LockedRecordException, DeprecatedRecordException{
        when(mock_jerseyClient.resource(Matchers.isA(URI.class))).thenReturn(mock_resource);
        when(mock_resource.path(Matchers.anyString())).thenReturn(mock_resource);
        when(mock_resource.accept(Matchers.anyString())).thenReturn(mock_builder);
        when(mock_builder.get(Matchers.isA(Class.class))).thenReturn(mock_response);
        when(mock_builder.header(Matchers.anyString(),Matchers.anyString())).thenReturn(mock_builder);
        when(mock_response.getStatus()).thenReturn(200);
        when(mock_response.getEntity(Record.class)).thenReturn(record);
        //get it once
        BaseMessage message1 = new LastModifiedMessage("0000-0000-0000-0000",new Date());        
        Record r = orcid20ApiClient.fetchPublicRecord(message1);
        
        //get it twice, if no error, then it's 
        when(mock_response.getEntity(Record.class)).thenThrow(new RuntimeException("called twice!"));
        r = orcid20ApiClient.fetchPublicRecord(message1);
        
        BaseMessage message2 = new LastModifiedMessage("0000-0000-0000-0000",new Date());  
        try{
            r = orcid20ApiClient.fetchPublicRecord(message2);
            fail("returned cached when it should get fresh");
        }catch(RuntimeException e){
            if (e.getMessage()!="called twice!")
                fail("something weird");
            
        }

    }
    
}
