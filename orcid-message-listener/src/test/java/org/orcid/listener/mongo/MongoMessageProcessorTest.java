package org.orcid.listener.mongo;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.jaxb.model.common_v2.OrcidIdentifier;
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.CreditName;
import org.orcid.jaxb.model.record_v2.DeactivationDate;
import org.orcid.jaxb.model.record_v2.History;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.WorkTitle;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.listener.orcid.Orcid20APIClient;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.listener.LastModifiedMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

@SuppressWarnings("deprecation")
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-message-listener-test-context.xml" })
public class MongoMessageProcessorTest {

    @Resource
    MongoMessageProcessor mongo;
    
    @Mock
    private Orcid20APIClient mock_orcid20ApiClient;
    
    @Resource
    private MongoClient mongoClient;
        
    Record record = new Record();
    
    @Value("${org.orcid.message-listener.mongo.database}")
    private String mongoDatabase;
    @Value("${org.orcid.message-listener.mongo.collection}")
    private String mongoCollection;
    private MongoCollection<Document> col;
    
    private final String orcid = "1000-0000-0000-0000";
    private final String orcid2 = "2000-0000-0000-0000";
    
    /** Sets up a FONGO instance and injects it into the MessageProcessor.
     * 
     * @throws LockedRecordException
     * @throws DeprecatedRecordException
     */
    @Before
    public void before() throws LockedRecordException, DeprecatedRecordException {
        MockitoAnnotations.initMocks(this);        
        TargetProxyHelper.injectIntoProxy(mongo, "orcid20ApiClient", mock_orcid20ApiClient);
        col = mongoClient.getDatabase(mongoDatabase).getCollection(mongoCollection);
    }
    
    @Test
    public void testInsertAndUpsert() throws LockedRecordException, DeprecatedRecordException{
        Record record = new Record();
        record.setOrcidIdentifier(new OrcidIdentifier("http://orcid.org/"+this.orcid));
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
        when(mock_orcid20ApiClient.fetchPublicRecord(Matchers.any())).thenReturn(record);
        
        LastModifiedMessage m = new LastModifiedMessage(this.orcid, new Date());
        mongo.accept(m);
        //test record inserted (has work, no name)
        Document d = new Document();
        d.put("_id", this.orcid);
        assertEquals(col.count(d),1);
        FindIterable<Document> it = col.find(d);
        Document found = it.first();
        assertEquals(found.get("orcid-identifier", Document.class).get("path"),"http://orcid.org/"+this.orcid);
        List<Document> groups = (List<Document>) found.get("activities-summary", Document.class).get("works", Document.class).get("group");
        List<Document> sums = (List<Document>) groups.get(0).get("work-summary");
        assertEquals(sums.get(0).get("title",Document.class)
                .get("title",Document.class)
                .get("value"),"blah");        
        
        record.setPerson(new Person());
        record.getPerson().setName(new Name());
        record.getPerson().getName().setCreditName(new CreditName());
        record.getPerson().getName().getCreditName().setContent("name");;
        LastModifiedMessage m2 = new LastModifiedMessage(this.orcid, new Date());
        mongo.accept(m2);
        it = col.find(d);
        found = it.first();
        assertEquals(found.get("person", Document.class).get("name",Document.class).get("credit-name",Document.class).get("value"),"name");
    }
    
    @Test
    public void testLocked() throws LockedRecordException, DeprecatedRecordException{
        when(mock_orcid20ApiClient.fetchPublicRecord(Matchers.any())).thenThrow(new LockedRecordException(new OrcidError()));
        LastModifiedMessage m = new LastModifiedMessage(this.orcid, new Date());
        mongo.accept(m);
        //test db has entry for locked record
        Document d = new Document();
        d.put("_id", this.orcid);
        assertEquals(col.count(d),1);
        FindIterable<Document> it = col.find(d);
        assertEquals(it.first().get("status"),"locked");
    }

    @Test
    public void testDeprecated() throws LockedRecordException, DeprecatedRecordException{
        when(mock_orcid20ApiClient.fetchPublicRecord(Matchers.any())).thenThrow(new DeprecatedRecordException(new OrcidError()));
        LastModifiedMessage m = new LastModifiedMessage(this.orcid, new Date());
        mongo.accept(m);
        //test db has entry for depreciated record
        Document d = new Document();
        d.put("_id", this.orcid);
        assertEquals(col.count(d),1);
        FindIterable<Document> it = col.find(d);
        assertEquals(it.first().get("status"),"deprecated");
    }

    @Test
    public void testDeactivated() throws LockedRecordException, DeprecatedRecordException, DatatypeConfigurationException{  
        Record deactivatedRecord = new Record();
        deactivatedRecord.setOrcidIdentifier(new OrcidIdentifier("http://orcid.org/"+this.orcid2));
        History h = new History();
        h.setDeactivationDate(new DeactivationDate());
        GregorianCalendar c = new GregorianCalendar();
        XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        h.getDeactivationDate().setValue(date);
        deactivatedRecord.setHistory(h);
        when(mock_orcid20ApiClient.fetchPublicRecord(Matchers.any())).thenReturn(deactivatedRecord);
        LastModifiedMessage m = new LastModifiedMessage(this.orcid2, new Date());
        mongo.accept(m);
        //test db has entry for deactivated record
        Document d = new Document();
        d.put("_id", this.orcid2);
        assertEquals(col.count(d),1);
        FindIterable<Document> it = col.find(d);
        assertEquals(it.first().get("status"),"deactivated");
    }

}
