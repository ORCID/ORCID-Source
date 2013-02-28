/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.utils.NullUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DeleteOrcidByIdentifier {

    @Option(name = "-o", usage = "ORCID to delete")
    private String orcid;

    @Option(name = "-f", usage = "Path to file of ORCIDs to delete (one per line)")
    private File orcidsToDelete;

    public static void main(String[] args) {
        DeleteOrcidByIdentifier deleteOrcidByIdentifier = new DeleteOrcidByIdentifier();
        CmdLineParser parser = new CmdLineParser(deleteOrcidByIdentifier);
        try {
            parser.parseArgument(args);
            deleteOrcidByIdentifier.validateArgs(parser);
            deleteOrcidByIdentifier.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (NullUtils.allNull(orcid, orcidsToDelete)) {
            throw new CmdLineException(parser, "At least one of -f | -o must be specificed");
        }
    }

    public void execute() {
        if (orcid != null) {
            delete(orcid);
        } else if (orcidsToDelete != null) {
            deleteAll(orcidsToDelete);
        }
    }

    private void delete(String orcid) {
        OrcidProfileManager orcidProfileManager = createOrcidProfileManager();
        orcidProfileManager.deleteProfile(orcid);
    }

    private void deleteAll(File orcidsToDelete) {
        OrcidProfileManager orcidProfileManager = createOrcidProfileManager();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(orcidsToDelete);
        } catch (FileNotFoundException e) {
            if (!orcidsToDelete.exists()) {
                System.err.println("Input file does not exist: " + orcidsToDelete);
                return;
            }
            if (!orcidsToDelete.canRead()) {
                System.err.println("Input exists, but can't read: " + orcidsToDelete);
                return;
            }
            System.err.println("Unable to read input file: " + orcidsToDelete + "\n" + e);
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            try {
                while ((line = br.readLine()) != null) {
                    String orcid = line.trim();
                    System.out.println("About to delete profile: " + orcid);
                    orcidProfileManager.deleteProfile(orcid);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading from: " + orcidsToDelete, e);
            }
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    private OrcidProfileManager createOrcidProfileManager() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        return (OrcidProfileManager) context.getBean("orcidProfileManager");
    }

}
