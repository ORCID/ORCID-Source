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

import java.io.InputStream;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.OrgManager;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.utils.NullUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.util.FileManager;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class LoadFundRefData {
	@Option(name = "-f", usage = "Path to RDF file containing FundRef info to load into DB")
    private String fileToLoad;
	private OrgDisambiguatedDao orgDisambiguatedDao;
    private OrgManager orgManager;
	
	public static void main(String[] args) {
		LoadFundRefData loadFundRefData = new LoadFundRefData();
        CmdLineParser parser = new CmdLineParser(loadFundRefData);
        try {
            parser.parseArgument(args);
            loadFundRefData.validateArgs(parser);
            loadFundRefData.init();
            loadFundRefData.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (NullUtils.allNull(fileToLoad)) {
            throw new CmdLineException(parser, "-f parameter must be specificed");
        }
    }
    
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orgDisambiguatedDao = (OrgDisambiguatedDao) context.getBean("orgDisambiguatedDao");
        orgManager = (OrgManager) context.getBean("orgManager");
    }
    
    private void execute() {
    	Model model = ModelFactory.createDefaultModel();
    	InputStream in = FileManager.get().open(fileToLoad);
        if (in == null) {
            throw new IllegalArgumentException( "File: " + fileToLoad + " not found");
        }
        
        // read the RDF/XML file
        model.read(in, "");
        
        model.write(System.out);
        
        
    }
}
