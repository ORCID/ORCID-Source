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

/**
 * Script to revert changes made by UpdateHistoricalDataForUserOBOClient
 * 
 * @author georgenash
 *
 */
public class RevertUserOBODataForUser {

    private static final Logger LOGGER = LoggerFactory.getLogger(RevertUserOBODataForUser.class);

    @Option(name = "-c", usage = "Client ID", required = true)
    private String clientDetailsId;
    
    @Option(name = "-b", usage = "Batch size", required = true)
    private int batchSize;

    private ResearcherUrlDao researcherUrlDao;

    private WorkDao workDao;

    private ProfileKeywordDao profileKeywordDao;

    private ProfileFundingDao profileFundingDao;

    private OtherNameDao otherNameDao;

    private EmailDao emailDao;

    private AddressDao addressDao;

    private OrgAffiliationRelationDao orgAffiliationRelationDao;

    public static void main(String[] args) {
        RevertUserOBODataForUser revertUserOBODataForUser = new RevertUserOBODataForUser();
        CmdLineParser parser = new CmdLineParser(revertUserOBODataForUser);
        try {
            parser.parseArgument(args);
            revertUserOBODataForUser.init();
            revertUserOBODataForUser.execute();
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
        revertResearcherUrls();
        revertWorks();
        revertKeywords();
        revertFundings();
        revertOtherNames();
        revertEmails();
        revertAddresses();
        revertOrgAffiliations();
    }

    private void revertOrgAffiliations() {
        LOGGER.info("Updating org affiliations...");
        int updated = 0;
        List<BigInteger> ids = orgAffiliationRelationDao.getIdsForUserOBORecords(clientDetailsId, batchSize * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                orgAffiliationRelationDao.revertUserOBODetails(subList);
                updated += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = orgAffiliationRelationDao.getIdsForUserOBORecords(clientDetailsId, batchSize * 20);
        }
        LOGGER.info("Updated {} records", updated);
    }

    private void revertAddresses() {
        LOGGER.info("Updating addresses...");
        int updated = 0;
        List<BigInteger> ids = addressDao.getIdsForUserOBORecords(clientDetailsId, batchSize * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                addressDao.revertUserOBODetails(subList);
                updated += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = addressDao.getIdsForUserOBORecords(clientDetailsId, batchSize * 20);
        }
        LOGGER.info("Updated {} records", updated);
    }

    private void revertEmails() {
        LOGGER.info("Updating emails...");
        int updated = 0;
        List<String> ids = emailDao.getIdsForUserOBORecords(clientDetailsId, batchSize * 20);
        while (!ids.isEmpty()) {
            List<String> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                emailDao.revertUserOBODetails(subList);
                updated += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = emailDao.getIdsForUserOBORecords(clientDetailsId, batchSize * 20);
        }
        LOGGER.info("Updated {} records", updated);
    }

    private void revertOtherNames() {
        LOGGER.info("Updating other names...");
        int updated = 0;
        List<BigInteger> ids = otherNameDao.getIdsForUserOBORecords(clientDetailsId, batchSize * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                otherNameDao.revertUserOBODetails(subList);
                updated += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = otherNameDao.getIdsForUserOBORecords(clientDetailsId, batchSize * 20);
        }
        LOGGER.info("Updated {} records", updated);
    }

    private void revertFundings() {
        LOGGER.info("Updating fundings...");
        int updated = 0;
        List<BigInteger> ids = profileFundingDao.getIdsForUserOBORecords(clientDetailsId, batchSize * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                profileFundingDao.revertUserOBODetails(subList);
                updated += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = profileFundingDao.getIdsForUserOBORecords(clientDetailsId, batchSize * 20);
        }
        LOGGER.info("Updated {} records", updated);
    }

    private void revertKeywords() {
        LOGGER.info("Updating keywords...");
        int updated = 0;
        List<BigInteger> ids = profileKeywordDao.getIdsForUserOBORecords(clientDetailsId, batchSize * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                profileKeywordDao.revertUserOBODetails(subList);
                updated += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = profileKeywordDao.getIdsForUserOBORecords(clientDetailsId, batchSize * 20);
        }
        LOGGER.info("Updated {} records", updated);
    }

    private void revertWorks() {
        LOGGER.info("Updating works...");
        int updated = 0;
        List<BigInteger> ids = workDao.getIdsForUserOBORecords(clientDetailsId, batchSize * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                workDao.revertUserOBODetails(subList);
                updated += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = workDao.getIdsForUserOBORecords(clientDetailsId, batchSize * 20);
        }
        LOGGER.info("Updated {} records", updated);
    }

    private void revertResearcherUrls() {
        LOGGER.info("Updating researcher urls...");
        int updated = 0;
        List<BigInteger> ids = researcherUrlDao.getIdsForUserOBORecords(clientDetailsId, batchSize * 20);
        while (!ids.isEmpty()) {
            List<BigInteger> subList = getNextIdSubset(ids);
            while (!subList.isEmpty()) {
                researcherUrlDao.revertUserOBODetails(subList);
                updated += subList.size();
                subList = getNextIdSubset(ids);
            }
            ids = researcherUrlDao.getIdsForUserOBORecords(clientDetailsId, batchSize * 20);
        }
        LOGGER.info("Updated {} records", updated);
    }
    
    private <T> List<T> getNextIdSubset(List<T> ids) {
        List<T> subset = new ArrayList<>();
        for (int i = 0; i < batchSize && !ids.isEmpty(); i++) {
            subset.add(ids.remove(0));
        }
        return subset;
    }

}
