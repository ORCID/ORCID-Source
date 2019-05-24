package org.orcid.core.cli;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.dao.WorkDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UpdateHistoricalDataForUserOBOClient {

    private static final int BATCH_SIZE = 400;

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateHistoricalDataForUserOBOClient.class);

    @Option(name = "-c", usage = "Client ID")
    private String clientDetailsId;

    private ResearcherUrlDao researcherUrlDao;

    private WorkDao workDao;

    private ProfileKeywordDao profileKeywordDao;

    private ProfileFundingDao profileFundingDao;

    private OtherNameDao otherNameDao;

    private EmailDao emailDao;

    private AddressDao addressDao;

    private OrgAffiliationRelationDao orgAffiliationRelationDao;

    public static void main(String[] args) {
        UpdateHistoricalDataForUserOBOClient updateHistoricalDataForUserOBOClient = new UpdateHistoricalDataForUserOBOClient();
        CmdLineParser parser = new CmdLineParser(updateHistoricalDataForUserOBOClient);
        try {
            parser.parseArgument(args);
            updateHistoricalDataForUserOBOClient.init();
            updateHistoricalDataForUserOBOClient.execute();
        } catch (CmdLineException e) {
            LOGGER.error(e.getMessage(), e);
            parser.printUsage(System.err);
            System.exit(1);
        } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
            System.exit(2);
        }
        System.exit(0);
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        addressDao = (AddressDao) context.getBean("addressDao");
        orgAffiliationRelationDao = (OrgAffiliationRelationDao) context.getBean("orgAffiliationRelationDao");
        researcherUrlDao = (ResearcherUrlDao) context.getBean("researcherUrlDao");
        emailDao = (EmailDao) context.getBean("emailDao");
        otherNameDao = (OtherNameDao) context.getBean("otherNameDao");
        profileFundingDao = (ProfileFundingDao) context.getBean("profileFundingDao");
        profileKeywordDao = (ProfileKeywordDao) context.getBean("profileKeywordDao");
        workDao = (WorkDao) context.getBean("workDao");
    }

    public void execute() {
        updateResearcherUrls();
        updateWorks();
        updateKeywords();
        updateFundings();
        updateOtherNames();
        updateEmails();
        updateAddresses();
        updateOrgAffiliations();
    }

    private void updateOrgAffiliations() {
        LOGGER.info("Updating org affiliations...");
        int updated = 0;
        List<BigInteger> ids = orgAffiliationRelationDao.getIdsForUserOBOUpdate(clientDetailsId, BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                orgAffiliationRelationDao.updateUserOBODetails(subList);
                updated += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = orgAffiliationRelationDao.getIdsForUserOBOUpdate(clientDetailsId, BATCH_SIZE * 20);
        }
        LOGGER.info("Updated {} records", updated);
    }

    private void updateAddresses() {
        LOGGER.info("Updating addresses...");
        int updated = 0;
        List<BigInteger> ids = addressDao.getIdsForUserOBOUpdate(clientDetailsId, BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                addressDao.updateUserOBODetails(subList);
                updated += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = addressDao.getIdsForUserOBOUpdate(clientDetailsId, BATCH_SIZE * 20);
        }
        LOGGER.info("Updated {} records", updated);
    }

    private void updateEmails() {
        LOGGER.info("Updating emails...");
        int updated = 0;
        List<String> ids = emailDao.getIdsForUserOBOUpdate(clientDetailsId, BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<String> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                emailDao.updateUserOBODetails(subList);
                updated += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = emailDao.getIdsForUserOBOUpdate(clientDetailsId, BATCH_SIZE * 20);
        }
        LOGGER.info("Updated {} records", updated);
    }

    private void updateOtherNames() {
        LOGGER.info("Updating other names...");
        int updated = 0;
        List<BigInteger> ids = otherNameDao.getIdsForUserOBOUpdate(clientDetailsId, BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                otherNameDao.updateUserOBODetails(subList);
                updated += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = otherNameDao.getIdsForUserOBOUpdate(clientDetailsId, BATCH_SIZE * 20);
        }
        LOGGER.info("Updated {} records", updated);
    }

    private void updateFundings() {
        LOGGER.info("Updating fundings...");
        int updated = 0;
        List<BigInteger> ids = profileFundingDao.getIdsForUserOBOUpdate(clientDetailsId, BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                profileFundingDao.updateUserOBODetails(subList);
                updated += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = profileFundingDao.getIdsForUserOBOUpdate(clientDetailsId, BATCH_SIZE * 20);
        }
        LOGGER.info("Updated {} records", updated);
    }

    private void updateKeywords() {
        LOGGER.info("Updating keywords...");
        int updated = 0;
        List<BigInteger> ids = profileKeywordDao.getIdsForUserOBOUpdate(clientDetailsId, BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                profileKeywordDao.updateUserOBODetails(subList);
                updated += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = profileKeywordDao.getIdsForUserOBOUpdate(clientDetailsId, BATCH_SIZE * 20);
        }
        LOGGER.info("Updated {} records", updated);
    }

    private void updateWorks() {
        LOGGER.info("Updating works...");
        int updated = 0;
        List<BigInteger> ids = workDao.getIdsForUserOBOUpdate(clientDetailsId, BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                workDao.updateUserOBODetails(subList);
                updated += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = workDao.getIdsForUserOBOUpdate(clientDetailsId, BATCH_SIZE * 20);
        }
        LOGGER.info("Updated {} records", updated);
    }

    private void updateResearcherUrls() {
        LOGGER.info("Updating researcher urls...");
        int updated = 0;
        List<BigInteger> ids = researcherUrlDao.getIdsForUserOBOUpdate(clientDetailsId, BATCH_SIZE * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                researcherUrlDao.updateUserOBODetails(subList);
                updated += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = researcherUrlDao.getIdsForUserOBOUpdate(clientDetailsId, BATCH_SIZE * 20);
        }
        LOGGER.info("Updated {} records", updated);
    }
    
    private <T> List<T> getNextIdSubset(List<T> ids) {
        List<T> subset = new ArrayList<>();
        for (int i = 0; i < BATCH_SIZE && !ids.isEmpty(); i++) {
            subset.add(ids.remove(0));
        }
        return subset;
    }

}
