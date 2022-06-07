package org.orcid.listener.orcid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.jaxb.model.common_v2.OrcidIdentifier;
import org.orcid.jaxb.model.common_v2.Title;
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
import org.orcid.utils.listener.BaseMessage;
import org.orcid.utils.listener.LastModifiedMessage;
import org.orcid.utils.rest.RESTHelper;
import org.springframework.test.context.ContextConfiguration;

import jakarta.ws.rs.core.Response;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-message-listener-test-context.xml" })
public class v2ClientPerMessageCacheTest {

    @Resource
    private Orcid20Manager orcid20ApiClient;
    
    @Mock
    RESTHelper httpHelperMock;

    @Mock
    Response responseMock;

    Record record = new Record();
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);        
        TargetProxyHelper.injectIntoProxy(orcid20ApiClient, "httpHelper", httpHelperMock);

        //mock methods
        when(httpHelperMock.executeGetRequest(any(URI.class), anyString(), anyString())).thenReturn(responseMock);
        when(responseMock.getStatus()).thenReturn(200);
        when(responseMock.readEntity(Record.class)).thenReturn(record);
        
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
    }
    
    @Test
    public void testRecordCache() throws ExecutionException, LockedRecordException, DeprecatedRecordException{
        Date date = new Date();
        date.setYear(1900);
        BaseMessage message1 = new LastModifiedMessage("0000-0000-0000-0000",date);        

        Record r = orcid20ApiClient.fetchPublicRecord(message1);
        assertEquals("http://orcid.org/0000-0000-0000-0000",r.getOrcidIdentifier().getPath());
        
        //get it twice, if no error, then it's the cached version
        when(responseMock.readEntity(Record.class)).thenThrow(new RuntimeException("called twice!"));
        r = orcid20ApiClient.fetchPublicRecord(message1);
        assertEquals("http://orcid.org/0000-0000-0000-0000",r.getOrcidIdentifier().getPath());
        
        //get it with a different message, should return fresh
        BaseMessage message2 = new LastModifiedMessage("0000-0000-0000-0000",new Date());  
        try{
            r = orcid20ApiClient.fetchPublicRecord(message2);
            fail("returned cached when it should get fresh");
        }catch(RuntimeException e){
            if (e.getCause().getMessage()!="called twice!")
                fail("something weird"+e.getLocalizedMessage());
        }

    }           
}
