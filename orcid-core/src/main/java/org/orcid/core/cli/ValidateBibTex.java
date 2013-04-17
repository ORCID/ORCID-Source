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

import org.apache.commons.io.IOUtils;
import org.jbibtex.ParseException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.utils.BibtexException;
import org.orcid.utils.BibtexUtils;
import org.orcid.utils.NullUtils;

/**
 * 
 * @author Will Simpson
 * 
 */
public class ValidateBibTex {

    @Option(name = "-b", usage = "BibTeX supplied as a command line arg")
    private String bibTex;;

    @Option(name = "-f", usage = "A file containing BibTeX")
    private File fileToLoad;

    public static void main(String[] args) throws FileNotFoundException, IOException, BibtexException, ParseException {
        ValidateBibTex manageClientGroup = new ValidateBibTex();
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
        if (NullUtils.allNull(bibTex, fileToLoad)) {
            throw new CmdLineException(parser, "At least one of -b | -f must be specificed");
        }
    }

    public void execute() throws FileNotFoundException, IOException, BibtexException, ParseException {
        if (bibTex == null) {
            bibTex = IOUtils.toString(new FileInputStream(fileToLoad));
        }
        System.out.println("Valid: " + BibtexUtils.isValid((bibTex)));
        System.out.println(BibtexUtils.toCitation(bibTex));
    }

}
