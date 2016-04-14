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

import java.util.Collections;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.persistence.dao.BiographyDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
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
public class MigrateNamesAndBioToTheirOwnTables {
    private static Logger LOG = LoggerFactory.getLogger(MigrateAddressData.class);
    private TransactionTemplate transactionTemplate;
    private ProfileDao profileDao;
    private RecordNameDao recordNameDao;
    private BiographyDao biographyDao;

    @Option(name = "-s", usage = "Batch size", required = false)
    private int batchSize;
    
    @Option(name = "-n", usage = "Number of batches to run", required = false)
    private int numberOfBatches;
    
    
    public static void main(String [] args) throws CmdLineException {
        MigrateNamesAndBioToTheirOwnTables migrate = new MigrateNamesAndBioToTheirOwnTables();
        migrate.init(args);
        migrate.migrateData();
        System.exit(0);
    }
    
    private void migrateData() {
        LOG.debug("Starting migration process");
        List<Object[]> profileElements = Collections.emptyList();
        int counter = 0;
        int batchCount = 0;
        do {
            LOG.debug("About to fetch a batch from DB");
            profileElements = profileDao.findProfilesWhereNamesAreNotMigrated(batchSize);            
            LOG.debug("Procesing batch, profiles processed so far: " + counter);
            for(final Object[] profileElement : profileElements) {
                String orcid = (String) profileElement[0];
                String givenNames = (String) profileElement[1];
                String familyName = (String) profileElement[2];
                String creditName = (String) profileElement[3];
                String namesVisibility = (String) profileElement[4];
                String biography = (String) profileElement[5];
                String biographyVisibility = (String) profileElement[6];
                String defaultVisibility = (String) profileElement[7];
                
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        LOG.info("Migrating names for profile: {}", orcid);                        
                        if(!recordNameDao.exists(orcid)) {
                            ProfileEntity profile = new ProfileEntity(orcid);
                            RecordNameEntity recordName = new RecordNameEntity();
                            recordName.setProfile(profile);
                            recordName.setCreditName(creditName);
                            recordName.setFamilyName(familyName);
                            recordName.setGivenNames(givenNames);
                            if(PojoUtil.isEmpty(namesVisibility)) {
                                recordName.setVisibility(Visibility.fromValue(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().value()));
                            } else {
                                recordName.setVisibility(Visibility.fromValue(namesVisibility));
                            }                            
                            recordNameDao.createRecordName(recordName);
                        }
                        
                        LOG.info("Migrating biography for profile: {}", orcid);
                        if(!biographyDao.exists(orcid)) {
                            Visibility visibility = Visibility.fromValue(OrcidVisibilityDefaults.BIOGRAPHY_DEFAULT.getVisibility().value()); 
                            if(!PojoUtil.isEmpty(biographyVisibility)) {
                                visibility = Visibility.fromValue(biographyVisibility);                                
                            } else if(!PojoUtil.isEmpty(defaultVisibility)) {
                                visibility = Visibility.fromValue(defaultVisibility);
                            }
                            
                            biographyDao.createBiography(orcid, biography, visibility);
                        }
                    }
                });
                counter += 1;
                batchCount += 1;
                //Stop if we ran the number of batches
                if(numberOfBatches > 0) {
                    if(batchCount >= numberOfBatches) {
                        profileElements = null;
                    }
                }
            }
        } while (profileElements != null && !profileElements.isEmpty());
        
        LOG.debug("Finished migration process");
    }
    
    @SuppressWarnings("resource")
    private void init(String [] args) throws CmdLineException {
        CmdLineParser parser = new CmdLineParser(this);
        parser.parseArgument(args);
        if(batchSize == 0) {
            batchSize = 10000;
        }
        
        if(numberOfBatches == 0) {
            numberOfBatches = -1;
        }
        
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-persistence-context.xml");
        profileDao = (ProfileDao) context.getBean("profileDao");
        recordNameDao = (RecordNameDao) context.getBean("recordNameDao");
        biographyDao = (BiographyDao) context.getBean("biographyDao");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }
}
