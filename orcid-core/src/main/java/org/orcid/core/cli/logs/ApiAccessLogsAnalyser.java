package org.orcid.core.cli.logs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApiAccessLogsAnalyser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiAccessLogsAnalyser.class);

    private static final int BEARER_TOKEN_LENGTH = 36;

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Option(name = "-l", usage = "Comma delimited list of directories of logs")
    private String logDirsArg;

    @Option(name = "-o", usage = "Output file")
    private File outputFile;

    @Option(name = "-z", usage = "Output summary")
    private File summaryFile;

    @Option(name = "-d", usage = "Debug", required = false)
    private boolean debug = false;

    @Option(name = "-n", usage = "Number of days to analyse")
    private int numberOfDaysToAnalyse;

    @Option(name = "-s", usage = "Start date (yyyy-MM-dd)")
    private String startDateArg;

    private List<File> logDirs;

    private OrcidOauth2TokenDetailDao tokenDao;

    private ClassPathXmlApplicationContext applicationContext;

    private Map<String, String> tokenToClientDetails = new HashMap<>();

    private AnalysisResults results;

    private LogReader logReader;

    private ClientDetailsDao clientDetailsDao;

    private LocalDate startDate;

    public static void main(String[] args) {
        ApiAccessLogsAnalyser analyser = new ApiAccessLogsAnalyser();
        CmdLineParser parser = new CmdLineParser(analyser);
        try {
            parser.parseArgument(args);
            analyser.validateArgs(parser);
            analyser.init();
            analyser.analyse();
            analyser.shutdown();
        } catch (CmdLineException e) {
            parser.printUsage(System.err);
            System.exit(1);
        }
        System.exit(0);
    }

    private void init() {
        LOGGER.info("Initialising Api access logs analysis...");
        applicationContext = new ClassPathXmlApplicationContext("orcid-persistence-context.xml");
        tokenDao = (OrcidOauth2TokenDetailDao) applicationContext.getBean("orcidOauth2TokenDetailDao");
        clientDetailsDao = (ClientDetailsDao) applicationContext.getBean("clientDetailsDaoReadOnly");
        LocalDate endDate = startDate.plusDays(numberOfDaysToAnalyse - 1);
        logReader = new LogReader();
        logReader.init(logDirs, startDate, endDate);
        results = new AnalysisResults();
        results.setClientDetailsDao(clientDetailsDao);
        try {
            results.setOutputStream(new FileOutputStream(outputFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error creating output stream to file " + outputFile.getAbsolutePath(), e);
        }

        try {
            results.setSummaryOutputStream(new FileOutputStream(summaryFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error creating output stream to summary file " + summaryFile.getAbsolutePath(), e);
        }
    }

    private void shutdown() {
        applicationContext.close();
    }

    void analyse() {
        LOGGER.info("Analysing log files...");
        String line = logReader.getNextLine();
        while (line != null) {
            analyseLog(line);
            line = logReader.getNextLine();
        }
        LOGGER.info("Analysis complete");
        try {
            results.outputResults();
        } catch (IOException e) {
            LOGGER.error("Error outputting results");
            System.exit(1);
        }
    }

    private void analyseLog(String line) {
        ApiLog log = ApiLog.parse(line);
        if (log != null) {
            if (debug) {
                LOGGER.info("Found log data {}", log.toString());
            }

            if (log.getBearerToken() != null && log.getBearerToken().length() == BEARER_TOKEN_LENGTH) {
                String client = getClientDetailsId(log.getBearerToken());
                if (debug) {
                    LOGGER.info("Found client {}", client);
                }
                if (client != null) { // discard if no client token
                    results.record(client, log);
                }
            }
        }
    }

    private String getClientDetailsId(String token) {
        if (!tokenToClientDetails.containsKey(token)) {
            OrcidOauth2TokenDetail tokenDetail = null;
            try {
                tokenDetail = tokenDao.findByTokenValue(token);
                tokenToClientDetails.put(token, tokenDetail.getClientDetailsId());
            } catch (NoResultException e) {
                if (debug) {
                    LOGGER.info("Couldn't find client for token {}", token);
                }
                return null;
            }
        }
        return tokenToClientDetails.get(token);
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        logDirs = new ArrayList<>();
        for (String logsDir : logDirsArg.split(",")) {
            if (logsDir == null) {
                throw new CmdLineException(parser, "Invalid list of log dirs");
            }
            File dir = new File(logsDir);
            if (!dir.exists()) {
                throw new CmdLineException(parser, "Logs dir " + dir.getAbsolutePath() + " does not exist");
            }
            if (!dir.isDirectory()) {
                throw new CmdLineException(parser, "Logs dir " + dir.getAbsolutePath() + " is not a directory");
            }
            logDirs.add(dir);
        }

        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                throw new CmdLineException(parser, "Invalid output file " + outputFile.getAbsolutePath());
            }
        }

        try {
            startDate = LocalDate.parse(startDateArg, FORMAT);
        } catch (DateTimeParseException e) {
            throw new CmdLineException(parser, "Invalid startDate " + startDateArg);
        }
    }

}
