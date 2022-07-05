package org.orcid.listener.orcid;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.jaxb.model.common_v2.OrcidIdentifier;
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.WorkTitle;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.jersey.JerseyClientHelper;
import org.orcid.utils.jersey.JerseyClientResponse;
import org.orcid.utils.listener.BaseMessage;
import org.orcid.utils.listener.LastModifiedMessage;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-message-listener-test-context.xml" })
public class v2ClientPerMessageCacheTest {

    @Resource
    private Orcid20Manager orcid20ApiClient;
    
    @Mock
    private JerseyClientHelper jerseyClientHelperMock;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);        
        TargetProxyHelper.injectIntoProxy(orcid20ApiClient, "jerseyClientHelper", jerseyClientHelperMock);

        Record record = new Record();
        //build test record
        record.setOrcidIdentifier(new OrcidIdentifier("http://orcid.org/0000-0000-0000-0000"));
        ActivitiesSummary sum = new ActivitiesSummary();
        Works works = new Works();
        WorkGroup group = new WorkGroup();
        WorkSummary work = new WorkSummary();
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("blah"));
        work.setTitle(title);
        group.getWorkSummary().add(work);
        works.getWorkGroup().add(group);
        sum.setWorks(works);
        record.setActivitiesSummary(sum);
        
        JerseyClientResponse<Record, OrcidError> responseMock = new JerseyClientResponse<Record, OrcidError>(200, record, null);
        
        //mock methods
        when(jerseyClientHelperMock.executeGetRequest(any(), any(), any(), eq(Record.class), eq(OrcidError.class))).thenReturn(responseMock);                        
    }
    
    @Test
    public void testRecordCache() throws ExecutionException, LockedRecordException, DeprecatedRecordException{
        Date date = new Date();
        date.setYear(1900);
        BaseMessage message1 = new LastModifiedMessage("0000-0000-0000-0000", date);        

        Record r = orcid20ApiClient.fetchPublicRecord(message1);
        assertEquals("http://orcid.org/0000-0000-0000-0000",r.getOrcidIdentifier().getPath());
        verify(jerseyClientHelperMock, times(1)).executeGetRequest(any(), any(), any(), eq(Record.class), eq(OrcidError.class));
        
        r = orcid20ApiClient.fetchPublicRecord(message1);
        assertEquals("http://orcid.org/0000-0000-0000-0000",r.getOrcidIdentifier().getPath());
        // Verify the mock was called only once
        verify(jerseyClientHelperMock, times(1)).executeGetRequest(any(), any(), any(), eq(Record.class), eq(OrcidError.class));
        
        //get it with a different message, should return fresh
        BaseMessage message2 = new LastModifiedMessage("0000-0000-0000-0000",new Date());
        
        r = orcid20ApiClient.fetchPublicRecord(message2);
        verify(jerseyClientHelperMock, times(2)).executeGetRequest(any(), any(), any(), eq(Record.class), eq(OrcidError.class));        
    }           
}
