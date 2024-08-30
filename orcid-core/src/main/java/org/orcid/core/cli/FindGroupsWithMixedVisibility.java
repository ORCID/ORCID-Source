package org.orcid.core.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.v3.WorkManager;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.utils.NullUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class FindGroupsWithMixedVisibility {

    private static Logger LOG = LoggerFactory.getLogger(FindGroupsWithMixedVisibility.class);

    private WorkManager workManager;
    @Option(name = "-f", usage = "Path to file containing ORCIDs to check")
    private File fileToLoad;
    @Option(name = "-o", usage = "ORCID to check")
    private String orcid;
    private int doneCount;

    public static void main(String[] args) throws IOException {
        FindGroupsWithMixedVisibility resaveProfiles = new FindGroupsWithMixedVisibility();
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
            LOG.info("Finished checking records: doneCount={}, timeTaken={} (H:m:s.S)", new Object[] { doneCount, timeTaken });
        }
    }

    private void processOrcid(final String orcid) {
        LOG.info("Checking record: {}", orcid);
        Works works = workManager.getWorksAsGroups(orcid);
        for (WorkGroup workGroup : works.getWorkGroup()) {
            if (Visibility.PUBLIC.equals(workGroup.getWorkSummary().get(0).getVisibility())) {
                List<String> nonPublicIds = new ArrayList<>();
                for (WorkSummary summary : workGroup.getWorkSummary()) {
                    if (!Visibility.PUBLIC.equals(summary.getVisibility())) {
                        nonPublicIds.add(String.valueOf(summary.getPutCode()));
                    }
                }
                if (!nonPublicIds.isEmpty()) {
                    LOG.info("Found mixed group for orcid={}, primaryWorkId={}, nonPublicWorkIds={}",
                            new Object[] { orcid, workGroup.getWorkSummary().get(0).getPutCode(), String.join(",", nonPublicIds) });
                }
            }
        }
        doneCount++;
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        workManager = (WorkManager) context.getBean("workManagerV3");
    }

}
