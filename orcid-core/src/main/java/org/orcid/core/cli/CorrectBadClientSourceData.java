package org.orcid.core.cli;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.OrgDao;
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.dao.WorkDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CorrectBadClientSourceData {
    
    private static final Logger LOG = LoggerFactory.getLogger(CorrectBadClientSourceData.class);
    
    private static final int BATCH_SIZE = 100;
    
    @Resource
    private ResearcherUrlDao researcherUrlDao;
    
    @Resource
    private WorkDao workDao;
    
    @Resource
    private ProfileKeywordDao profileKeywordDao;
    
    @Resource
    private ProfileFundingDao profileFundingDao;
    
    @Resource
    private OtherNameDao otherNameDao;
    
    @Resource
    private NotificationDao notificationDao;
    
    @Resource
    private EmailDao emailDao;
    
    @Resource
    private AddressDao addressDao;
    
    @Resource
    private OrgAffiliationRelationDao orgAffiliationRelationDao;
    
    @Resource
    private OrgDao orgDao;
    
    public static void main(String[] args) {
        CorrectBadClientSourceData corrector = new CorrectBadClientSourceData();
        corrector.init();
        corrector.correct();
    }

    void correct() {
        correctAddresses();
        correctEmails();
        correctNotifications();
        correctOrgAffiliationRelations();
        correctOrgs();
        correctOtherNames();
        correctFundings();
        correctKeywords();
        correctResearcherUrls();
        correctWorks();
    }

    private void correctAddresses() {
        LOG.info("Correcting addresses...");
        int corrected = 0;
        List<BigInteger> ids = addressDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                addressDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = addressDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        }
        
        ids = addressDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                addressDao.correctUserSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = addressDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        }
        LOG.info("Corrected {} records", corrected);
    }
    
    private void correctEmails() {
        LOG.info("Correcting emails...");
        int corrected = 0;
        List<String> ids = emailDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<String> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                emailDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = emailDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        }
        
        ids = emailDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<String> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                emailDao.correctUserSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = emailDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        }
        LOG.info("Corrected {} records", corrected);
    }

    private void correctNotifications() {
        LOG.info("Correcting notifications...");
        int corrected = 0;
        List<BigInteger> ids = notificationDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                notificationDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = notificationDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        }
        
        ids = notificationDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                notificationDao.correctUserSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = notificationDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        }
        LOG.info("Corrected {} records", corrected);
    }

    private void correctOrgAffiliationRelations() {
        LOG.info("Correcting org affiliation relations...");
        int corrected = 0;
        List<BigInteger> ids = orgAffiliationRelationDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                orgAffiliationRelationDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = orgAffiliationRelationDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        }
        
        ids = orgAffiliationRelationDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                orgAffiliationRelationDao.correctUserSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = orgAffiliationRelationDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        }
        LOG.info("Corrected {} records", corrected);
    }

    private void correctOrgs() {
        LOG.info("Correcting orgs...");
        int corrected = 0;
        List<BigInteger> ids = orgDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                orgDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = orgDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        }
        
        ids = orgDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                orgDao.correctUserSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = orgDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        }
        LOG.info("Corrected {} records", corrected);
    }

    private void correctOtherNames() {
        LOG.info("Correcting other names...");
        int corrected = 0;
        List<BigInteger> ids = otherNameDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                otherNameDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = otherNameDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        }
        
        ids = otherNameDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                otherNameDao.correctUserSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = otherNameDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        }
        LOG.info("Corrected {} records", corrected);
    }

    private void correctFundings() {
        LOG.info("Correcting fundings...");
        int corrected = 0;
        List<BigInteger> ids = profileFundingDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                profileFundingDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = profileFundingDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        }
        
        ids = profileFundingDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                profileFundingDao.correctUserSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = profileFundingDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        }
        LOG.info("Corrected {} records", corrected);
    }

    private void correctKeywords() {
        LOG.info("Correcting keywords...");
        int corrected = 0;
        List<BigInteger> ids = profileKeywordDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                profileKeywordDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = profileKeywordDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        }
        
        ids = profileKeywordDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                profileKeywordDao.correctUserSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = profileKeywordDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        }
        LOG.info("Corrected {} records", corrected);
    }

    private void correctResearcherUrls() {
        LOG.info("Correcting researcher urls...");
        int corrected = 0;
        List<BigInteger> ids = researcherUrlDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                researcherUrlDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = researcherUrlDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        }
        
        ids = researcherUrlDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                researcherUrlDao.correctUserSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = researcherUrlDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        }
        LOG.info("Corrected {} records", corrected);
    }

    private void correctWorks() {
        LOG.info("Correcting works...");
        int corrected = 0;
        List<BigInteger> ids = workDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                workDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = workDao.getIdsForClientSourceCorrection(BATCH_SIZE * 20);
        }
        
        ids = workDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                workDao.correctUserSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = workDao.getIdsForUserSourceCorrection(BATCH_SIZE * 20);
        }
        LOG.info("Corrected {} records", corrected);
    }
    
    private <T> List<T> getNextIdSubset(List<T> ids) {
        List<T> subset = new ArrayList<>();
        for (int i = 0; i < BATCH_SIZE && !ids.isEmpty(); i++) {
            subset.add(ids.remove(0));
        }
        return subset;
    }

    @SuppressWarnings("resource")
    private void init() {
        LOG.info("Initialising DAOs...");
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        addressDao  = (AddressDao) context.getBean("addressDao");
        emailDao  = (EmailDao) context.getBean("emailDao");
        notificationDao  = (NotificationDao) context.getBean("notificationDao");
        orgAffiliationRelationDao  = (OrgAffiliationRelationDao) context.getBean("orgAffiliationRelationDao");
        orgDao  = (OrgDao) context.getBean("orgDao");
        otherNameDao  = (OtherNameDao) context.getBean("otherNameDao");
        profileFundingDao  = (ProfileFundingDao) context.getBean("profileFundingDao");
        profileKeywordDao  = (ProfileKeywordDao) context.getBean("profileKeywordDao");
        researcherUrlDao  = (ResearcherUrlDao) context.getBean("researcherUrlDao");
        workDao  = (WorkDao) context.getBean("workDao");
    }

}
