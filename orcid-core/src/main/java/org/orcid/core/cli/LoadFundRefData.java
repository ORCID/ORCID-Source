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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.OrgManager;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class LoadFundRefData {
	@Option(name = "-f", usage = "Path to RDF file containing FundRef info to load into DB")
    private File fileToLoad;
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
        if (fileToLoad == null) {
            throw new CmdLineException(parser, "-f parameter must be specificed");
        }
    }
    
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orgDisambiguatedDao = (OrgDisambiguatedDao) context.getBean("orgDisambiguatedDao");
        orgManager = (OrgManager) context.getBean("orgManager");
    }
    
    private void execute() {
    	try {
			FileInputStream file = new FileInputStream(fileToLoad);			
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();			
			DocumentBuilder builder =  builderFactory.newDocumentBuilder();			
			Document xmlDocument = builder.parse(file);		
			XPath xPath =  XPathFactory.newInstance().newXPath();
			// Get the list of resources
			String conceptsExpression = "/RDF/ConceptScheme/hasTopConcept";
			String itemExpression = "/RDF/Concept[@about='%s']";
			String orgNameExpression = itemExpression + "/prefLabel/Label/literalForm";
			NodeList nodeList = (NodeList) xPath.compile(conceptsExpression).evaluate(xmlDocument, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {
				NamedNodeMap attrs = nodeList.item(i).getAttributes();
				Node node = attrs.getNamedItem("rdf:resource");
				String itemDoi = node.getNodeValue();
				//Get organization name								
				String orgName = (String)xPath.compile(orgNameExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.STRING);
				System.out.println("---------------------------------------------------------------------------------------------------------------");
				System.out.println(orgName);
				System.out.println("---------------------------------------------------------------------------------------------------------------");
				
			}
    	} catch(Exception e) {
    		
    	}
        
        
    }
}





























