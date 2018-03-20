package org.orcid.listener.mongo;

import java.util.function.Consumer;

import javax.annotation.PostConstruct;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;

@Component
public class MongoMessageProcessor implements Consumer<BaseMessage> {

    Logger LOG = LoggerFactory.getLogger(MongoMessageProcessor.class);

    @Resource
    private RecordStatusManager recordStatusManager;

    private boolean isMongoEnabled;

    @Resource
    private Orcid20APIClient orcid20ApiClient;

    @Resource
    @Lazy
    private MongoClient mongoClient;

    @Value("${org.orcid.message-listener.mongo.database:messagelistener}")
    private String mongoDatabase;

    @Value("${org.orcid.message-listener.mongo.collection:dump}")
    private String mongoCollection;

    private final ObjectMapper mapper;
    private MongoCollection<Document> col;
    private final UpdateOptions upsert = new UpdateOptions().upsert(true);

    MongoMessageProcessor(@Value("${org.orcid.message-listener.mongo.enabled:false}") boolean isMongoEnabled) {
        if(!isMongoEnabled) {
            LOG.warn("Mongo is disabled");
        }
        this.isMongoEnabled = isMongoEnabled;
        mapper = new ObjectMapper();
        JaxbAnnotationModule module = new JaxbAnnotationModule();
        mapper.registerModule(module);
    }

    @PostConstruct
    public void init() {
        if (isMongoEnabled) {
            Document ping = new Document("ping", "1");
            try {
                mongoClient.getDatabase(mongoDatabase).runCommand(ping);
            } catch (MongoException e) {
                LOG.error("Cannot connect to MongoDB");
                isMongoEnabled = false;
                throw e;
            }
            col = mongoClient.getDatabase(mongoDatabase).getCollection(mongoCollection);
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
