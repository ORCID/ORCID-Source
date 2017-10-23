package org.orcid.listener.mongo;

import java.util.function.Consumer;

import javax.annotation.Resource;

import org.bson.Document;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.listener.orcid.Orcid20APIClient;
import org.orcid.listener.persistence.managers.RecordStatusManager;
import org.orcid.listener.persistence.util.AvailableBroker;
import org.orcid.utils.listener.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

@Component
public class MongoMessageProcessor implements Consumer<BaseMessage> {

    Logger LOG = LoggerFactory.getLogger(MongoMessageProcessor.class);

    @Resource
    private RecordStatusManager recordStatusManager;

    private final boolean isMongoEnabled;

    @Resource
    private Orcid20APIClient orcid20ApiClient;

    private final MongoClient mongoClient;
    private final ObjectMapper mapper;
    private final MongoCollection<Document> col;
    private final UpdateOptions upsert = new UpdateOptions().upsert(true);

    MongoMessageProcessor(@Value("${org.orcid.message-listener.mongo.enabled}") boolean isMongoEnabled,
            @Value("${org.orcid.message-listener.mongo.database}") String mongoDatabase,
            @Value("${org.orcid.message-listener.mongo.collection}") String mongoCollection) {
        this.isMongoEnabled = isMongoEnabled;
        if (isMongoEnabled){
            mapper = new ObjectMapper();
            JaxbAnnotationModule module = new JaxbAnnotationModule();
            mapper.registerModule(module);
            //check db available
            mongoClient = new MongoClient();
            MongoDatabase db = mongoClient.getDatabase(mongoDatabase);
            col = db.getCollection(mongoCollection);        
        } else {
            col = null;
            mapper = null;
            mongoClient = null;
            LOG.info("Mongo disabled");
        }

    }

    @Override
    public void accept(BaseMessage message) {
        if (isMongoEnabled)
            updateMongo(message);
    }

    private void updateMongo(BaseMessage message) {
        String orcid = message.getOrcid();
        LOG.info("Updating using Record " + orcid + " in Mongo");
        try {
            Record record = orcid20ApiClient.fetchPublicRecord(message);
            // Remove deactivated records from Mongo
            if (record.getHistory() != null && record.getHistory().getDeactivationDate() != null && record.getHistory().getDeactivationDate().getValue() != null) {
                delete(message.getOrcid(), "deactivated");
                return;
            }
            Document d = Document.parse(mapper.writeValueAsString(record));
            d.put("_id", message.getOrcid());
            Document index = new Document();
            index.put("_id", message.getOrcid());
            col.replaceOne(index, d, upsert);
            recordStatusManager.markAsSent(orcid, AvailableBroker.MONGO);
            LOG.info("inserted " + d.toJson());
        } catch (LockedRecordException lre) {
            LOG.error("Record " + orcid + " is locked");
            delete(message.getOrcid(), "locked");
        } catch (DeprecatedRecordException dre) {
            LOG.error("Record " + orcid + " is deprecated");
            delete(message.getOrcid(), "deprecated");
        } catch (Exception e) {
            LOG.error("pants", e);
            recordStatusManager.markAsFailed(message.getOrcid(), AvailableBroker.MONGO);
        }
    }

    private void delete(String orcid, String status) {
        Document o = new Document();
        o.put("_id", orcid);
        o.put("status", status);
        Document index = new Document();
        index.put("_id", orcid);
        col.replaceOne(index, o, upsert);
        recordStatusManager.markAsSent(orcid, AvailableBroker.MONGO);
    }

}
