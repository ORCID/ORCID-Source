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

import java.util.List;

import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record.Relationship;
import org.orcid.jaxb.model.record.WorkExternalIdentifier;
import org.orcid.jaxb.model.record.WorkExternalIdentifiers;
import org.orcid.jaxb.model.record.WorkType;
import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class AddRelationshipFieldToExistingActivitiesExternalIds {
    
    private PeerReviewDao peerReviewDao;
    private ProfileFundingDao profileFundingDao;
    private WorkDao workDao;    
    private TransactionTemplate transactionTemplate;
    
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        workDao = (WorkDao) context.getBean("workDao");
        peerReviewDao = (PeerReviewDao) context.getBean("peerReviewDao");
        profileFundingDao = (ProfileFundingDao) context.getBean("profileFundingDao");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
    }
    
    private void upgradeWorks(long limit) {
        List<String> idsToUpgrade = workDao.getWorksWithOldExtIds(limit);
        for(String workId : idsToUpgrade) {
            WorkEntity work = workDao.find(Long.valueOf(workId));
            WorkExternalIdentifiers extIds = JsonUtils.readObjectFromJsonString(work.getExternalIdentifiersJson(), WorkExternalIdentifiers.class);
            if(extIds != null) {
                for(WorkExternalIdentifier extId : extIds.getWorkExternalIdentifier()) {
                    if(extId.getRelationship() == null) {
                        if(WorkExternalIdentifierType.ISSN.equals(extId.getWorkExternalIdentifierType())) {
                            if(WorkType.BOOK.equals(work.getWorkType())) {
                                extId.setRelationship(Relationship.PART_OF);
                            } else {
                                extId.setRelationship(Relationship.SELF);
                            }
                        } else if(WorkExternalIdentifierType.ISBN.equals(extId.getWorkExternalIdentifierType())) {
                            if(WorkType.BOOK_CHAPTER.equals(work.getWorkType())) {                                
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
        }
    }
}
