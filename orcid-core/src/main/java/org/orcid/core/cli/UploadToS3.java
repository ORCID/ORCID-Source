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

import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.messaging.JmsMessageSender;
import org.orcid.persistence.messaging.JmsMessageSender.JmsDestination;
import org.orcid.utils.listener.LastModifiedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UploadToS3 {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadToS3.class);
    
    // Params
    @Option(name = "-s", usage = "Number of elements to fetch per batch")
    private Integer batchSize;
    
    @Option(name = "-i", usage = "Number of iterations (batches) to process")
    private Integer iterations;
        
    JmsMessageSender messaging;
    
    ProfileDao profileDao;
    
    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getIterations() {
        return iterations;
    }

    public void setIterations(Integer iterations) {
        this.iterations = iterations;
    }

    public static void main(String[] args) {
        UploadToS3 s3 = new UploadToS3();
        CmdLineParser parser = new CmdLineParser(s3);
        try {
            parser.parseArgument(args);
            if(s3.getBatchSize() == null || s3.getBatchSize() < 1) {
                s3.setBatchSize(500);
            }
            
            if(s3.getIterations() == null || s3.getIterations() < 1) {
                s3.setIterations(100);
            }
            s3.init();
            s3.start();
            
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
        System.exit(0);
    }
    
    @SuppressWarnings({ "resource" })
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-message-listener-web-context.xml");        
        profileDao = (ProfileDao) context.getBean("profileDao");
        messaging = (JmsMessageSender)context.getBean("messaging");
    }
    
    public void start() {
        IntStream.range(0, iterations).forEach(i -> {LOGGER.info("Processing batch: " + i); processBatch(); LOGGER.info("Batch done: " + i);});
    }
    
    private void processBatch() {
        List<String> idsToProcess = profileDao.getIdsNotDumpedYet(batchSize);
        if(idsToProcess == null) {
            LOGGER.info("There are no elements to process");
        }
        
        LOGGER.info("Number of profiles to process " + idsToProcess.size());
        for(String orcid : idsToProcess) {
            LastModifiedMessage mess = new LastModifiedMessage(orcid, new Date());
            //Send to the message queue
            if(messaging.send(mess, JmsDestination.S3_ONLY)) {
                //Set profile as processed
                profileDao.setDumpedStatusAsSent(orcid);
            }               
        }
    }
}
