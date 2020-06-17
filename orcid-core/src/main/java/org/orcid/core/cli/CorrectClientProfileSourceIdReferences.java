package org.orcid.core.cli;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.jaxb.model.common.OrcidType;
import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.dao.BiographyDao;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.OrgDao;
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.dao.ResearchResourceDao;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.dao.WebhookDao;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * Script that updates all data that references profiles of deprecated type
 * CLIENT in source_id column, to instead reference their corresponding
 * client_details row via the client_source_id column.
 * 
 * Process:
 * 
 * @author georgenash
 *
 */
public class CorrectClientProfileSourceIdReferences {

    private static final Logger LOG = LoggerFactory.getLogger(CorrectClientProfileSourceIdReferences.class);

    @Resource
    private ProfileDao profileDao;

    @Resource
    private ResearchResourceDao researchResourceDao;

    @Resource
    private ClientDetailsDao clientDetailsDao;

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

    @Resource
    private WebhookDao webhookDao;

    @Resource
    private RecordNameDao recordNameDao;

    @Resource
    private BiographyDao biographyDao;
    
    @Option(name = "-b", usage = "Batch size")
    private Integer batchSize;

    private List<String> clientProfileOrcidIds = new ArrayList<>();

    public static void main(String[] args) throws CmdLineException {
        CorrectClientProfileSourceIdReferences corrector = new CorrectClientProfileSourceIdReferences();
        CmdLineParser parser = new CmdLineParser(corrector);
        parser.parseArgument(args);
        corrector.validateBatchSize();
        corrector.init();
        corrector.correctProfileReferences();
        System.exit(0);
    }

    private void validateBatchSize() {
        if (batchSize == null || batchSize.equals(0)) {
            throw new RuntimeException("Illegal batch size. Specify with -b option.");
        }
    }

    public void correctProfileReferences() {
        findProfilesOfClientType();
        if (clientProfileOrcidIds.isEmpty()) {
            LOG.info("No profiles of type CLIENT found");
        } else {
            updateAddresses();
            updateEmails();
            updateNotifications();
            updateOrgAffiliationRelations();
            updateOrgs();
            updateOtherNames();
            updateFundings();
            updateKeywords();
            updateResearcherUrls();
            updateWorks();
        }
    }

    private void updateWorks() {
        LOG.info("Updating works...");
        int corrected = 0;
        LOG.info("Fetching {} work IDs for correction", batchSize * 20);
        List<BigInteger> ids = workDao.getIdsOfWorksReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                LOG.info("Correcting client source of {} work records", subList.size());
                workDao.correctClientSource(subList);
                LOG.info("Corrected {} records", subList.size());
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            LOG.info("Fetching {} work IDs for correction", batchSize * 20);
            ids = workDao.getIdsOfWorksReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        }
        LOG.info("Updated {} records", corrected);
    }

    private void updateResearcherUrls() {
        LOG.info("Updating researcher urls...");
        int corrected = 0;
        List<BigInteger> ids = researcherUrlDao.getIdsOfResearcherUrlsReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                researcherUrlDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = researcherUrlDao.getIdsOfResearcherUrlsReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        }
        LOG.info("Updated {} records", corrected);
    }

    private void updateKeywords() {
        LOG.info("Updating keywords...");
        int corrected = 0;
        List<BigInteger> ids = profileKeywordDao.getIdsOfKeywordsReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                profileKeywordDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = profileKeywordDao.getIdsOfKeywordsReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        }
        LOG.info("Updated {} records", corrected);
    }

    private void updateFundings() {
        LOG.info("Updating fundings...");
        int corrected = 0;
        List<BigInteger> ids = profileFundingDao.getIdsOfFundingsReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                profileFundingDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = profileFundingDao.getIdsOfFundingsReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        }
        LOG.info("Updated {} records", corrected);
    }

    private void updateOtherNames() {
        LOG.info("Updating other names...");
        int corrected = 0;
        List<BigInteger> ids = otherNameDao.getIdsOfOtherNamesReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                otherNameDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = otherNameDao.getIdsOfOtherNamesReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        }
        LOG.info("Updated {} records", corrected);
    }

    private void updateOrgs() {
        LOG.info("Updating orgs...");
        int corrected = 0;
        List<BigInteger> ids = orgDao.getIdsOfOrgsReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                orgDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = orgDao.getIdsOfOrgsReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        }
        LOG.info("Updated {} records", corrected);
    }

    private void updateOrgAffiliationRelations() {
        LOG.info("Updating org affiliation relations...");
        int corrected = 0;
        List<BigInteger> ids = orgAffiliationRelationDao.getIdsOfOrgAffiliationRelationsReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                orgAffiliationRelationDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = orgAffiliationRelationDao.getIdsOfOrgAffiliationRelationsReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        }
        LOG.info("Updated {} records", corrected);
    }

    private void updateNotifications() {
        LOG.info("Updating notifications...");
        int corrected = 0;
        List<BigInteger> ids = notificationDao.getIdsOfNotificationsReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                notificationDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = notificationDao.getIdsOfNotificationsReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        }
        LOG.info("Updated {} records", corrected);
    }

    private void updateEmails() {
        LOG.info("Updating emails...");
        int corrected = 0;
        List<String> ids = emailDao.getIdsOfEmailsReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        while (!ids.isEmpty()) {
            List<String> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                emailDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = emailDao.getIdsOfEmailsReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        }
        LOG.info("Updated {} records", corrected);
    }

    private void updateAddresses() {
        LOG.info("Updating addresses...");
        int corrected = 0;
        List<BigInteger> ids = addressDao.getIdsOfAddressesReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                addressDao.correctClientSource(subList);
                corrected += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = addressDao.getIdsOfAddressesReferencingClientProfiles(batchSize * 20, clientProfileOrcidIds);
        }
        LOG.info("Updated {} records", corrected);
    }

    private <T> List<T> getNextIdSubset(List<T> ids) {
        List<T> subset = new ArrayList<>();
        for (int i = 0; i < batchSize && !ids.isEmpty(); i++) {
            subset.add(ids.remove(0));
        }
        return subset;
    }

    @SuppressWarnings("deprecation")
    private void findProfilesOfClientType() {
        List<ProfileEntity> clientProfiles = profileDao.findByOrcidType(OrcidType.CLIENT.name());
        clientProfiles.forEach(p -> {
            ClientDetailsEntity clientDetails = clientDetailsDao.find(p.getId());
            if (clientDetails == null) {
                LOG.error("No matching client details entity found for profile {}", p.getId());
                throw new RuntimeException("Missing client details entity, unable to continue");
            }
            clientProfileOrcidIds.add(p.getId());
        });
    }

    @SuppressWarnings("resource")
    private void init() {
        LOG.info("Initialising DAOs...");
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        addressDao = (AddressDao) context.getBean("addressDao");
        emailDao = (EmailDao) context.getBean("emailDao");
        notificationDao = (NotificationDao) context.getBean("notificationDao");
        orgAffiliationRelationDao = (OrgAffiliationRelationDao) context.getBean("orgAffiliationRelationDao");
        orgDao = (OrgDao) context.getBean("orgDao");
        otherNameDao = (OtherNameDao) context.getBean("otherNameDao");
        profileFundingDao = (ProfileFundingDao) context.getBean("profileFundingDao");
        profileKeywordDao = (ProfileKeywordDao) context.getBean("profileKeywordDao");
        researcherUrlDao = (ResearcherUrlDao) context.getBean("researcherUrlDao");
        workDao = (WorkDao) context.getBean("workDao");
        clientDetailsDao = (ClientDetailsDao) context.getBean("clientDetailsDao");
        profileDao = (ProfileDao) context.getBean("profileDao");
    }

}
