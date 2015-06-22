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
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.persistence.dao.ProfileWorkDao;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class MigrateDataFromProfileWorkToWorkTable {

    private static Logger LOG = LoggerFactory.getLogger(MigrateDataFromProfileWorkToWorkTable.class);
    private final long DEFAULT_CHUNK_SIZE = 1000;
    private ProfileWorkDao profileWorkDao;
    private WorkDao workDao;
    private TransactionTemplate transactionTemplate;

    @Option(name = "-s", usage = "Chunk size")
    private Long chunkSize;
    
    @Option(name = "-n", usage = "Number of batches to run")
    private Long batchesToRun;
    
    public static void main(String[] args) throws IOException {
        MigrateDataFromProfileWorkToWorkTable m = new MigrateDataFromProfileWorkToWorkTable();
        CmdLineParser parser = new CmdLineParser(m);
        try {
            parser.parseArgument(args);
            m.validateArgs(parser);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
        m.init();        
        long counter = 0;
        long start = System.currentTimeMillis();
        do {
            List<ProfileWorkEntity> toProcess = m.getBatchToProcess();
            if(toProcess == null || toProcess.isEmpty()) {
                LOG.info("All data has been migrated");
                System.exit(0);
            }                
            if(toProcess != null && !toProcess.isEmpty()) {
                m.processBatch(toProcess);
            } else {
                break;
            }            
            long time = System.currentTimeMillis();
            LOG.info("{} batches have run so far in {} secs", (++counter), ((time - start)/1000));
            if(m.batchesToRun > 0) {
                if (counter >= m.batchesToRun){
                    break;
                }
            }
        } while(true);
        
        System.exit(0);
    }
    
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        profileWorkDao = (ProfileWorkDao) context.getBean("profileWorkDao");
        workDao = (WorkDao) context.getBean("workDao");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }

    private List<ProfileWorkEntity> getBatchToProcess() {
        return profileWorkDao.getNonMigratedProfileWorks(chunkSize.intValue());
    }
    
    private void processBatch(final List<ProfileWorkEntity> profileWorks) {
        LOG.info("Processing batch of {} elements", profileWorks.size());

        for (final ProfileWorkEntity profileWork : profileWorks) {
            final String orcid = profileWork.getProfile().getId();
            final Long workId = profileWork.getWork().getId();
            LOG.debug("Migrating work id {} for profile {}", workId, orcid);
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    // Migrate the data to the work table
                    boolean copied = workDao.copyDataFromProfileWork(profileWork.getWork().getId(), profileWork);
                    if (!copied) {
                        LOG.error("Unable to migrate profile work with orcid: {}, and work id: {}", orcid, workId);
                        System.exit(1);
                    }
                    // Set the profile_work as migrated
                    boolean updated = profileWorkDao.setProfileWorkAsMigrated(orcid, workId);
                    if (!updated) {
                        LOG.error("Unable to set profile work as updated orcid: {} work id: {}", orcid, workId);
                        System.exit(1);
                    }
                }
            });
        }        
    }
    
    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (chunkSize == null) {
            chunkSize = DEFAULT_CHUNK_SIZE;            
        }
        
        if(batchesToRun == null) {
            batchesToRun = Long.valueOf(-1);
        }
    }
}
