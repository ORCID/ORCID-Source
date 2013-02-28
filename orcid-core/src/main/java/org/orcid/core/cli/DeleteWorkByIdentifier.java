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
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.utils.NullUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DeleteWorkByIdentifier {

    @Option(name = "-w", usage = "ID of work to delete")
    private String workId;

    @Option(name = "-f", usage = "Path to file of work IDs to delete (one per line)")
    private File worksToDelete;

    public static void main(String[] args) {
        DeleteWorkByIdentifier deleteWorkByIdentifier = new DeleteWorkByIdentifier();
        CmdLineParser parser = new CmdLineParser(deleteWorkByIdentifier);
        try {
            parser.parseArgument(args);
            deleteWorkByIdentifier.validateArgs(parser);
            deleteWorkByIdentifier.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (NullUtils.allNull(workId, worksToDelete)) {
            throw new CmdLineException(parser, "At least one of -f | -o must be specificed");
        }
    }

    public void execute() {
        if (workId != null) {
            delete(workId);
        } else if (worksToDelete != null) {
            deleteAll(worksToDelete);
        }
    }

    private void delete(String workId) {
        GenericDao<WorkEntity, Long> workDao = createWorkDao();
        workDao.remove(Long.valueOf(workId));
    }

    private void deleteAll(File orcidsToDelete) {
        GenericDao<WorkEntity, Long> workDao = createWorkDao();
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
                    workDao.remove(Long.valueOf(line.trim()));
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading from: " + orcidsToDelete, e);
            }
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    @SuppressWarnings("unchecked")
    private GenericDao<WorkEntity, Long> createWorkDao() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-persistence-context.xml");
        return (GenericDao<WorkEntity, Long>) context.getBean("workDao");
    }

}
