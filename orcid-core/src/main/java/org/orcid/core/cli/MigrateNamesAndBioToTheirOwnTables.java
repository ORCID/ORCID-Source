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

    public static void main(String... args) {
        new MigrateNamesAndBioToTheirOwnTables().migrate();
    }
    
    private void migrate() {
        init();
        migrateData();
        System.exit(0);
    }
    
    private void migrateData() {
        LOG.debug("Starting migration process");
        List<Object[]> profileElements = Collections.emptyList();
        
        do {
            profileElements = profileDao.findProfilesWhereNamesAreNotMigrated(10000);
            
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
            }
        } while (profileElements != null && !profileElements.isEmpty());
        
        LOG.debug("Finished migration process");
    }
    
    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-persistence-context.xml");
        profileDao = (ProfileDao) context.getBean("profileDao");
        recordNameDao = (RecordNameDao) context.getBean("recordNameDao");
        biographyDao = (BiographyDao) context.getBean("biographyDao");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }
}
