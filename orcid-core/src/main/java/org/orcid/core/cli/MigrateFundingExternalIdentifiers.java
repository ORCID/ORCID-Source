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

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.message.FundingExternalIdentifier;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.message.FundingExternalIdentifiers;
import org.orcid.jaxb.model.message.Url;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.jpa.entities.FundingExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MigrateFundingExternalIdentifiers {    
    private ProfileFundingDao profileFundingDao;
    private ProfileDao profileDao;
    private static Logger LOG = LoggerFactory.getLogger(MigrateFundingExternalIdentifiers.class);
    private static final int CHUNK_SIZE = 1000;

    public static void main(String... args) {
        new MigrateFundingExternalIdentifiers().migrate();
    }

    private void migrate() {
        init();
        migrateProfiles();
        System.exit(0);
    }

    private void migrateProfiles() {
        long startTime = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        List<BigInteger> fundingIds = Collections.EMPTY_LIST;
        Set<String> profilesToReindex = new HashSet<String>();
        int doneCount = 0;
        do {
            fundingIds = profileFundingDao.findFundingNeedingExternalIdentifiersMigration(CHUNK_SIZE);
            for (final BigInteger fundingId : fundingIds) {
                LOG.info("Migrating external identifiers for funding: {}", fundingId);
                
                ProfileFundingEntity profileFunding = profileFundingDao.find(fundingId.longValue());
                FundingExternalIdentifiers extIds = getFundingExternalIdentifiers(profileFunding);
                if (extIds != null && !extIds.getFundingExternalIdentifier().isEmpty()) {
                    String extIdsJson = JsonUtils.convertToJsonString(extIds);
                    BigInteger numericFundingId = BigInteger.valueOf(profileFunding.getId());
                    // Update funding
                    profileFundingDao.setFundingExternalIdentifiersInJson(numericFundingId, extIdsJson);
                    profilesToReindex.add(profileFunding.getProfile().getId());                    
                }
                
                doneCount++;                               
            }            
        } while (!fundingIds.isEmpty());
        
        for(String profileToReindex : profilesToReindex) {
            profileDao.updateIndexingStatus(profileToReindex, IndexingStatus.REINDEX);
        }
        
        
        long endTime = System.currentTimeMillis();
        String timeTaken = DurationFormatUtils.formatDurationHMS(endTime - startTime);
        LOG.info("Finished migrating funding external ids: doneCount={}, timeTaken={} (H:m:s.S)", doneCount, timeTaken);
    }

    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        profileFundingDao = (ProfileFundingDao) context.getBean("profileFundingDao");
        profileDao = (ProfileDao) context.getBean("profileDao");
    }

    private FundingExternalIdentifiers getFundingExternalIdentifiers(ProfileFundingEntity profileFundingEntity) {
        if (profileFundingEntity == null || profileFundingEntity.getExternalIdentifiers() == null || profileFundingEntity.getExternalIdentifiers().isEmpty()) {
            return null;
        }
        SortedSet<FundingExternalIdentifierEntity> fundingExternalIdentifierEntitys = profileFundingEntity.getExternalIdentifiers();
        FundingExternalIdentifiers fundingExternalIdentifiers = new FundingExternalIdentifiers();

        for (FundingExternalIdentifierEntity fundingExternalIdentifierEntity : fundingExternalIdentifierEntitys) {
            FundingExternalIdentifier fundingExternalIdentifier = getFundingExternalIdentifier(fundingExternalIdentifierEntity);
            if (fundingExternalIdentifier != null) {
                fundingExternalIdentifiers.getFundingExternalIdentifier().add(fundingExternalIdentifier);
            }
        }

        return fundingExternalIdentifiers;
    }

    private FundingExternalIdentifier getFundingExternalIdentifier(FundingExternalIdentifierEntity fundingExternalIdentifierEntity) {
        if (fundingExternalIdentifierEntity == null) {
            return null;
        }
        FundingExternalIdentifier fundingExternalIdentifier = new FundingExternalIdentifier();

        fundingExternalIdentifier.setType(FundingExternalIdentifierType.fromValue(fundingExternalIdentifierEntity.getType()));
        fundingExternalIdentifier.setUrl(new Url(fundingExternalIdentifierEntity.getUrl()));
        fundingExternalIdentifier.setValue(fundingExternalIdentifierEntity.getValue());

        return fundingExternalIdentifier;
    }
}
