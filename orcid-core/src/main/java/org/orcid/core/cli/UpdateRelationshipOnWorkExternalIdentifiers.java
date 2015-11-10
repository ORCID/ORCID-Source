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
package org.orcid.core.cli;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.record_rc1.Relationship;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifier;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifiers;
import org.orcid.jaxb.model.record_rc1.WorkType;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class UpdateRelationshipOnWorkExternalIdentifiers {
    private static Logger LOG = LoggerFactory.getLogger(AddRelationshipFieldToExistingActivitiesExternalIds.class);
    private static final long DEFAULT_CHUNK_SIZE = 10000;
    
    private WorkDao workDao;
    private TransactionTemplate transactionTemplate;
    
    @Option(name = "-fn", usage = "Fix null relationships")
    private Boolean fix;
    
    public static void main(String[] args) throws IOException {
        UpdateRelationshipOnWorkExternalIdentifiers obj = new UpdateRelationshipOnWorkExternalIdentifiers();
        obj.init();
        CmdLineParser parser = new CmdLineParser(obj);
        
        try {
            parser.parseArgument(args);
            obj.validateArgs(parser);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }

        if(obj.fix) {
            obj.fixNullRelationships(0, UpdateRelationshipOnWorkExternalIdentifiers.DEFAULT_CHUNK_SIZE);            
        }
        
        obj.updateRelationshipField(Relationship.PART_OF, WorkType.CONFERENCE_PAPER.name(), WorkExternalIdentifierType.ISBN.name(), 0, UpdateRelationshipOnWorkExternalIdentifiers.DEFAULT_CHUNK_SIZE);
        
        System.exit(0);
    }        
    
    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        workDao = (WorkDao) context.getBean("workDao");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }
    
    private long fixNullRelationships(long lastWorkId, long limit) {
        final List<BigInteger> idsToUpgrade = workDao.getWorksWithNullRelationship();
        if (idsToUpgrade == null || idsToUpgrade.isEmpty()) {
            LOG.info("Couldnt find more works to process");
            return -1;
        }
        long lastWorkIdProcessed = 0;
        LOG.info("Ids to upgrade: {}", idsToUpgrade.size());
        for (final BigInteger workId : idsToUpgrade) {
            System.out.println("Processing work id: " + workId);
            lastWorkIdProcessed = workId.longValue();
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {            
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {  
                    WorkEntity work = workDao.find(workId.longValue());                    
                    WorkExternalIdentifiers extIds = JsonUtils.readObjectFromJsonString(work.getExternalIdentifiersJson(),
                            WorkExternalIdentifiers.class);                    
                    if (extIds != null) {
                        for (WorkExternalIdentifier extId : extIds.getWorkExternalIdentifier()) {
                            if(extId.getRelationship() == null) {
                                if (WorkExternalIdentifierType.ISSN.equals(extId.getWorkExternalIdentifierType())) {
                                    if (WorkType.BOOK.equals(work.getWorkType())) {
                                        extId.setRelationship(Relationship.PART_OF);
                                    } else {
                                        extId.setRelationship(Relationship.SELF);
                                    }
                                } else if (WorkExternalIdentifierType.ISBN.equals(extId.getWorkExternalIdentifierType())) {
                                    if (WorkType.BOOK_CHAPTER.equals(work.getWorkType()) || WorkType.CONFERENCE_PAPER.equals(work.getWorkType())) {
                                        extId.setRelationship(Relationship.PART_OF);
                                    } else {
                                        extId.setRelationship(Relationship.SELF);
                                    }
                                } else {
                                    extId.setRelationship(Relationship.SELF);
                                }
                            }                                                                                                                
                        }
                    }
                    work.setExternalIdentifiersJson(JsonUtils.convertToJsonString(extIds));
                    workDao.merge(work);                                        
                }
            });            
        }        
        return lastWorkIdProcessed;
    }
    
    private long updateRelationshipField(final Relationship newRelationshipValue, final String workType, final String externalIdType, long lastWorkId, long limit) {
        final List<BigInteger> idsToUpgrade = workDao.getWorksByWorkTypeAndExtIdType(workType, externalIdType);
        if (idsToUpgrade == null || idsToUpgrade.isEmpty()) {
            LOG.info("Couldnt find more works to process");
            return -1;
        }
        long lastWorkIdProcessed = 0;
        LOG.info("Ids to upgrade: {}", idsToUpgrade.size());
        for (final BigInteger workId : idsToUpgrade) {
            System.out.println("Processing work id: " + workId);
            lastWorkIdProcessed = workId.longValue();
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {            
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {  
                    WorkEntity work = workDao.find(workId.longValue());                    
                    boolean updated = false;
                    if(work.getWorkType().name().equals(workType)) {
                        WorkExternalIdentifiers extIds = JsonUtils.readObjectFromJsonString(work.getExternalIdentifiersJson(),
                                WorkExternalIdentifiers.class);                    
                        if (extIds != null) {                            
                            for (WorkExternalIdentifier extId : extIds.getWorkExternalIdentifier()) {
                                if(extId.getWorkExternalIdentifierType() != null && extId.getWorkExternalIdentifierType().name().equals(externalIdType)) {
                                    extId.setRelationship(newRelationshipValue);
                                    updated = true;
                                }
                            }
                        }
                        
                        if(updated) {                        
                            work.setExternalIdentifiersJson(JsonUtils.convertToJsonString(extIds));
                            workDao.merge(work);
                        }
                    }                                                                                                  
                }
            });            
        }        
        return lastWorkIdProcessed;
    }
    
    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if(fix == null) {
            fix = false;
        }
    }
}
