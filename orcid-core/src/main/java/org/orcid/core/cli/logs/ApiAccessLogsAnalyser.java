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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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

    private static final String UNKNOWN_CLIENT = "Unknown";

    @Option(name = "-f", usage = "Path to directory containing logs and / or directories of logs")
    private File logsDir;

    @Option(name = "-o", usage = "Output file")
    private File outputFile;

    @Option(name = "-d", usage = "Debug", required = false)
    private boolean debug = false;

    private OrcidOauth2TokenDetailDao tokenDao;

    private ClassPathXmlApplicationContext applicationContext;

    private Map<String, String> tokenToClientDetails = new HashMap<>();

    private AnalysisResults results = new AnalysisResults();

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
    }

    private void shutdown() {
        applicationContext.close();
    }

    void analyse() {
        LOGGER.info("Analysing log files, base directory: " + logsDir.getAbsolutePath());
        analyseDir(logsDir);
        LOGGER.info("Analysis complete");
        try {
            results.outputClientStats(new FileOutputStream(outputFile));
        } catch (IOException e) {
            LOGGER.error("Error outputting results");
            System.exit(1);
        }
    }

    private void analyseDir(File dir) {
        LOGGER.info("Examining directory {}", dir.getAbsolutePath());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                analyseDir(file);
            } else {
                analyseLogFile(file);
            }
        }
    }

    private void analyseLogFile(File file) {
        LOGGER.info("Examining log file {}", file.getAbsolutePath());
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                analyseLog(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void analyseLog(String line) {
        ApiLog log = ApiLog.parse(line);
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
