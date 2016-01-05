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
import org.orcid.jaxb.model.common.Url;
import org.orcid.jaxb.model.record_rc2.Relationship;
import org.orcid.jaxb.model.record_rc2.WorkExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.WorkExternalIdentifiers;
import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.FundingExternalIdentifier;
import org.orcid.pojo.FundingExternalIdentifiers;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class AddRelationshipFieldToExistingActivitiesExternalIds {

    private static Logger LOG = LoggerFactory.getLogger(AddRelationshipFieldToExistingActivitiesExternalIds.class);
    private final long DEFAULT_CHUNK_SIZE = 1000;

    private PeerReviewDao peerReviewDao;
    private ProfileFundingDao profileFundingDao;
    private WorkDao workDao;
    private TransactionTemplate transactionTemplate;

    @Option(name = "-wid", usage = "Last work id processed")
    private Long lastWorkId;
    
    @Option(name = "-s", usage = "Chunk size")
    private Long chunkSize;

    @Option(name = "-n", usage = "Number of batches to run")
    private Long batchesToRun;

    @Option(name = "-w", usage = "Process works")
    boolean processWorks;
    
    @Option(name = "-p", usage = "Process peer reviews")
    boolean processPeerReviews;
    
    @Option(name = "-f", usage = "Process fundings")
    boolean processFundings;
    
    public static void main(String[] args) throws IOException {
        AddRelationshipFieldToExistingActivitiesExternalIds obj = new AddRelationshipFieldToExistingActivitiesExternalIds();
        obj.init();
        CmdLineParser parser = new CmdLineParser(obj);
        try {
            parser.parseArgument(args);
            obj.validateArgs(parser);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }

        long counter = 0;
        long start = System.currentTimeMillis();
        boolean haveMoreWorks = true;
        boolean haveMoreFundings = true;
        boolean haveMorePeerReviews = true;
        do {
            if(!obj.processFundings && !obj.processPeerReviews && !obj.processWorks) {
                LOG.info("Nothing to process");
                break;
            }
            // First migrate works
            if(obj.processWorks) {
                if (haveMoreWorks) {
                    haveMoreWorks = obj.upgradeWorks(obj.lastWorkId, obj.chunkSize);
                }
            } 

            // Migrate funding
            if(obj.processFundings) {
                if (haveMoreFundings) {
                    haveMoreFundings = obj.upgradeFunding(obj.chunkSize);
                }
            }
            
            // Migrate peer review
            if(obj.processPeerReviews) {
                if (haveMorePeerReviews) {
                    haveMorePeerReviews = obj.upgradePeerReview(obj.chunkSize);
                }
            }            

            long time = System.currentTimeMillis();
            LOG.info("{} batches have run so far in {} secs", (++counter), ((time - start) / 1000));

            if (!haveMoreWorks && !haveMoreFundings && !haveMorePeerReviews) {
                LOG.info("All data has been migrated");
                System.exit(0);
            }
            if (obj.batchesToRun > 0) {
                if (counter >= obj.batchesToRun) {
                    break;
                }
            }
        } while (true);

        System.exit(0);
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        workDao = (WorkDao) context.getBean("workDao");
        peerReviewDao = (PeerReviewDao) context.getBean("peerReviewDao");
        profileFundingDao = (ProfileFundingDao) context.getBean("profileFundingDao");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }

    private boolean upgradeWorks(long lastWorkId, long limit) {
        final List<BigInteger> idsToUpgrade = workDao.getWorksWithOldExtIds(lastWorkId, limit);
        if (idsToUpgrade == null || idsToUpgrade.isEmpty()) {
            LOG.info("Couldnt find more works to process");
            return false;
        }
        LOG.info("Ids to upgrade: {}", idsToUpgrade.size());
        for (final BigInteger workId : idsToUpgrade) {
            System.out.println("Processing work id: " + workId);
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {            
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {                
                    WorkEntity work = workDao.find(workId.longValue());
                    org.orcid.jaxb.model.message.WorkExternalIdentifiers oldExtIds = JsonUtils.readObjectFromJsonString(work.getExternalIdentifiersJson(),
                            org.orcid.jaxb.model.message.WorkExternalIdentifiers.class);
                    org.orcid.jaxb.model.record_rc2.WorkExternalIdentifiers newExtIds = new org.orcid.jaxb.model.record_rc2.WorkExternalIdentifiers();
                    if (oldExtIds != null) {
                        for (org.orcid.jaxb.model.message.WorkExternalIdentifier oldExtId : oldExtIds.getWorkExternalIdentifier()) {
                            org.orcid.jaxb.model.record_rc2.WorkExternalIdentifier newExtId = org.orcid.jaxb.model.record_rc2.WorkExternalIdentifier.fromMessageExtId(oldExtId);
                            // Set the part_of field
                            if (org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISSN.equals(oldExtId.getWorkExternalIdentifierType())) {
                                if (org.orcid.jaxb.model.message.WorkType.BOOK.equals(work.getWorkType())) {
                                    newExtId.setRelationship(Relationship.PART_OF);
                                } else {
                                    newExtId.setRelationship(Relationship.SELF);
                                }
                            } else if (org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISBN.equals(oldExtId.getWorkExternalIdentifierType())) {
                                if (org.orcid.jaxb.model.message.WorkType.BOOK_CHAPTER.equals(work.getWorkType())) {
                                    newExtId.setRelationship(Relationship.PART_OF);
                                } else {
                                    newExtId.setRelationship(Relationship.SELF);
                                }
                            } else {
                                newExtId.setRelationship(Relationship.SELF);
                            }
                            // Set an empty url
                            if(PojoUtil.isEmpty(newExtId.getUrl())) {
                                newExtId.setUrl(new Url(""));
                            }
                            newExtIds.getWorkExternalIdentifier().add(newExtId);
                        }
                    }
                    work.setExternalIdentifiersJson(JsonUtils.convertToJsonString(newExtIds));
                    workDao.merge(work);
                }
            });
        }
        return true;
    }

    private boolean upgradeFunding(long limit) {
        final List<BigInteger> idsToUpgrade = profileFundingDao.getFundingWithOldExtIds(limit);
        if (idsToUpgrade == null || idsToUpgrade.isEmpty()) {
            LOG.info("Couldnt find more funding to process");
            return false;
        }
        LOG.info("Funding ids to upgrade: {}", idsToUpgrade.size());
        for (final BigInteger fundingId : idsToUpgrade) {
            System.out.println("Processing funding id: " + fundingId);
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {                
                    ProfileFundingEntity fundingEntity = profileFundingDao.find(fundingId.longValue());
                    FundingExternalIdentifiers extIdsPojo = JsonUtils.readObjectFromJsonString(fundingEntity.getExternalIdentifiersJson(),
                            FundingExternalIdentifiers.class);
                    if (extIdsPojo != null && !extIdsPojo.getFundingExternalIdentifier().isEmpty()) {
                        for (FundingExternalIdentifier extId : extIdsPojo.getFundingExternalIdentifier()) {
                            if (extId.getRelationship() == null) {
                                extId.setRelationship(Relationship.SELF);
                            }
                        }
                    }

                    fundingEntity.setExternalIdentifiersJson(JsonUtils.convertToJsonString(extIdsPojo));
                    profileFundingDao.merge(fundingEntity);
                }
            });
        }

        return true;
    }

    private boolean upgradePeerReview(long limit) {
        final List<BigInteger> idsToUpgrade = peerReviewDao.getPeerReviewWithOldExtIds(limit);
        if (idsToUpgrade == null || idsToUpgrade.isEmpty()) {
            return false;
        }
        LOG.info("Peer review ids to upgrade: {}", idsToUpgrade.size());
        
        for (BigInteger peerReviewId : idsToUpgrade) {
            System.out.println("Processing peer review id: " + peerReviewId);
            PeerReviewEntity peerReviewEntity = peerReviewDao.find(peerReviewId.longValue());
            
            //Update peer review ext ids
            WorkExternalIdentifiers extIds = JsonUtils.readObjectFromJsonString(peerReviewEntity.getExternalIdentifiersJson(),
                    WorkExternalIdentifiers.class);
            if (extIds != null && !extIds.getExternalIdentifier().isEmpty()) {
                for (WorkExternalIdentifier extId : extIds.getExternalIdentifier()) {
                    if (extId.getRelationship() == null) {
                        extId.setRelationship(Relationship.SELF);
                    }
                    if(PojoUtil.isEmpty(extId.getUrl())) {
                        extId.setUrl(new Url(""));
                    }
                }
            }
            peerReviewEntity.setExternalIdentifiersJson(JsonUtils.convertToJsonString(extIds));

            //Update peer review subject ext ids
            
            if(!PojoUtil.isEmpty(peerReviewEntity.getSubjectExternalIdentifiersJson())) {
                WorkExternalIdentifiers subjectExtIds = JsonUtils.readObjectFromJsonString(peerReviewEntity.getSubjectExternalIdentifiersJson(), WorkExternalIdentifiers.class);
                if (subjectExtIds != null && !subjectExtIds.getExternalIdentifier().isEmpty()) {
                    for (WorkExternalIdentifier subjectExtId : subjectExtIds.getExternalIdentifier()) {
                        if (subjectExtId.getRelationship() == null) {
                            subjectExtId.setRelationship(Relationship.SELF);
                        }
                        if(PojoUtil.isEmpty(subjectExtId.getUrl())) {
                            subjectExtId.setUrl(new Url(""));
                        }
                    }
                }
                peerReviewEntity.setSubjectExternalIdentifiersJson(JsonUtils.convertToJsonString(subjectExtIds));
            }
                        
                        
            peerReviewDao.merge(peerReviewEntity);
        }
        
        return true;
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (chunkSize == null) {
            chunkSize = DEFAULT_CHUNK_SIZE;
        }

        if (batchesToRun == null) {
            batchesToRun = Long.valueOf(-1);
        }
        
        if(lastWorkId == null) {
            lastWorkId = 0L;
        }
    }
}
