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
package org.orcid.core.cli.logs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.NoResultException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApiAccessLogsAnalyser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiAccessLogsAnalyser.class);

    private static final int BEARER_TOKEN_LENGTH = 36;

    static final String UNKNOWN_CLIENT = "Unknown";

    @Option(name = "-f", usage = "Path to directory containing logs and / or directories of logs")
    private File logsDir;

    @Option(name = "-o", usage = "Output file")
    private File outputFile;

    @Option(name = "-d", usage = "Debug", required = false)
    private boolean debug = false;

    private OrcidOauth2TokenDetailDao tokenDao;

    private ClassPathXmlApplicationContext applicationContext;

    private Map<String, String> tokenToClientDetails = new HashMap<>();

    private AnalysisResults results;

    private LogReader logReader;

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
        logReader = new LogReader();
        logReader.init(logsDir);
        results = new AnalysisResults();
        try {
            results.setOutputStream(new FileOutputStream(outputFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error creating output stream to file " + outputFile.getAbsolutePath(), e);
        }
    }

    private void shutdown() {
        applicationContext.close();
    }

    void analyse() {
        LOGGER.info("Analysing log files, base directory: " + logsDir.getAbsolutePath());
        String line = logReader.getNextLine();
        while (line != null) {
            analyseLog(line);
            line = logReader.getNextLine();
        }
        LOGGER.info("Analysis complete");
        try {
            results.outputClientStats();
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
                tokenToClientDetails.put(token, UNKNOWN_CLIENT);
            }
        }
        return tokenToClientDetails.get(token);
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (logsDir == null) {
            throw new CmdLineException(parser, "-f parameter must be specificed");
        }
        if (!logsDir.exists()) {
            throw new CmdLineException(parser, "Logs dir " + logsDir.getAbsolutePath() + " does not exist");
        }
        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                throw new CmdLineException(parser, "Invalid output file " + outputFile.getAbsolutePath());
            }
        }
    }

}
