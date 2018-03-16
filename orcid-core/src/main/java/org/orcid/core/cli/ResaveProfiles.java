package org.orcid.core.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.aop.ProfileLastModifiedAspect;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.utils.NullUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Will Simpson
 * 
 */
public class ResaveProfiles {

    private static Logger LOG = LoggerFactory.getLogger(ResaveProfiles.class);

    private OrcidProfileManager orcidProfileManager;
    private TransactionTemplate transactionTemplate;
    private ProfileDao profileDao;
    private ProfileLastModifiedAspect profileLastModifiedAspect;
    @Option(name = "-f", usage = "Path to file containing ORCIDs to resave")
    private File fileToLoad;
    @Option(name = "-o", usage = "ORCID to resave")
    private String orcid;
    @Option(name = "-m", usage = "Update the last modified date of the record (default = false)")
    private boolean updateLastModified;
    @Option(name = "-c", usage = "Continue to next record if there is an error (default = stop on error)")
    private boolean continueOnError;
    private int doneCount;
    private int errorCount;

    public static void main(String[] args) throws IOException {
        ResaveProfiles resaveProfiles = new ResaveProfiles();
        CmdLineParser parser = new CmdLineParser(resaveProfiles);
        try {
            parser.parseArgument(args);
            resaveProfiles.validateArgs(parser);
            resaveProfiles.init();
            resaveProfiles.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        } catch (Throwable t) {
            System.err.println(t);
            System.exit(2);
        }
        System.exit(0);
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (NullUtils.allNull(fileToLoad, orcid)) {
            throw new CmdLineException(parser, "At least one of -f | -o must be specificed");
        }
    }

    public void execute() throws IOException {
        if (fileToLoad != null) {
            processFile();
        }
        if (orcid != null) {
            processOrcid(orcid);
        }
    }

    private void processFile() throws IOException {
        long startTime = System.currentTimeMillis();
        try (BufferedReader br = new BufferedReader(new FileReader(fileToLoad))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    processOrcid(line.trim());
                }
            }
            long endTime = System.currentTimeMillis();
            String timeTaken = DurationFormatUtils.formatDurationHMS(endTime - startTime);
            LOG.info("Finished resaving profiles: doneCount={}, errorCount={}, timeTaken={} (H:m:s.S)", new Object[] { doneCount, errorCount, timeTaken });
        }
    }

    private void processOrcid(final String orcid) {
        LOG.info("Resaving profile: {}", orcid);
        try {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile(orcid);
                    Date originalLastModified = orcidProfile.getOrcidHistory().getLastModifiedDate().getValue().toGregorianCalendar().getTime();
                    IndexingStatus originalIndexingStatus = profileDao.find(orcid).getIndexingStatus();
                    // Save it straight back - it will be saved back in the
                    // new DB table automatically
                    orcidProfileManager.updateOrcidProfile(orcidProfile);
                    if (!updateLastModified) {
                        profileDao.updateLastModifiedDateAndIndexingStatusWithoutResult(orcid, originalLastModified, originalIndexingStatus);
                    }
                }
            });
        } catch (RuntimeException e) {
            errorCount++;
            if (continueOnError) {
                LOG.error("Error saving profile: orcid={}", orcid, e);
                return;
            } else {
                throw e;
            }
        }
        doneCount++;
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orcidProfileManager = (OrcidProfileManager) context.getBean("orcidProfileManager");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
        profileDao = (ProfileDao) context.getBean("profileDao");
        profileLastModifiedAspect = (ProfileLastModifiedAspect) context.getBean("profileLastModifiedAspect");
        profileLastModifiedAspect.setEnabled(updateLastModified);
    }

}
