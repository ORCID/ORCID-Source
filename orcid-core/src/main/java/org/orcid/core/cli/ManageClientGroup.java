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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.IOUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.utils.NullUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

/**
 * 
 * @author Will Simpson
 * 
 */
public class ManageClientGroup {

    @Option(name = "-r", usage = "ORCID for which to read client group from DB")
    private String orcid;

    @Option(name = "-s", usage = "Path to file to validate against the client group schema")
    private File fileToValidate;

    @Option(name = "-f", usage = "Path to file to load into DB")
    private File fileToLoad;

    public static void main(String[] args) {
        ManageClientGroup manageClientGroup = new ManageClientGroup();
        CmdLineParser parser = new CmdLineParser(manageClientGroup);
        try {
            parser.parseArgument(args);
            manageClientGroup.validateArgs(parser);
            manageClientGroup.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (NullUtils.allNull(orcid, fileToValidate, fileToLoad)) {
            throw new CmdLineException(parser, "At least one of -f | -r | -s must be specificed");
        }        
    }

    public void execute() {
        if (orcid != null) {
            read(orcid);
        } else if (fileToValidate != null) {
            isValidAgainstSchema(fileToValidate);
        } else if (fileToLoad != null) {
            load(fileToLoad);
        }
    }

    private void load(File fileToLoad) {
        if (isValidAgainstSchema(fileToLoad)) {
            OrcidClientGroupManager manager = createOrcidClientGroupManager();
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(fileToLoad);
            } catch (FileNotFoundException e) {
                if (!fileToLoad.exists()) {
                    System.err.println("Input file does not exist: " + fileToLoad);
                    return;
                }
                if (!fileToLoad.canRead()) {
                    System.err.println("Input exists, but can't read: " + fileToLoad);
                    return;
                }
                System.err.println("Unable to read input file: " + fileToLoad + "\n" + e);
            }
            try {
                OrcidClientGroup result = manager.createOrUpdateOrcidClientGroupForAPIRequest(OrcidClientGroup.unmarshall(fis));
                System.out.println(result);
            } finally {
                IOUtils.closeQuietly(fis);
            }
        }
    }

    private boolean isValidAgainstSchema(File fileToValidate) {
        Validator validator = createValidator();
        Source source = new StreamSource(fileToValidate);
        try {
            validator.validate(source);
            System.out.println(fileToValidate + " is valid");
            return true;
        } catch (SAXException e) {
            System.out.println(fileToValidate + " is invalid");
            System.out.println(e);
        } catch (IOException e) {
            System.out.println("Unable to read file " + fileToValidate);
        }
        return false;
    }

    private Validator createValidator() {
        try {
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = factory.newSchema(getClass().getResource("/orcid-client-group-1.3.xsd"));
            return schema.newValidator();
        } catch (SAXException e) {
            throw new RuntimeException("Error reading ORCID client group schema", e);
        }
    }

    private void read(String orcid) {
        OrcidClientGroupManager manager = createOrcidClientGroupManager();
        OrcidClientGroup group = manager.retrieveOrcidClientGroup(orcid);
        System.out.println(group);
    }

    private OrcidClientGroupManager createOrcidClientGroupManager() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        OrcidClientGroupManager manager = (OrcidClientGroupManager) context.getBean("orcidClientGroupManager");
        return manager;
    }

}
