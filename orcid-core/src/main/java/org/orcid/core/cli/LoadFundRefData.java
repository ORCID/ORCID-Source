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
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class LoadFundRefData {
	@Option(name = "-f", usage = "Path to RDF file containing FundRef info to load into DB")
    private File fileToLoad;
	private static String geonamesApiUrl="http://api.geonames.org/getJSON";
	private OrgDisambiguatedDao orgDisambiguatedDao;
    private OrgManager orgManager;
    private String apiUser;
    private Client jerseyClient;
	
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
        apiUser = (String)context.getBean("geonamesUser");
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
			String orgCountryExpression = itemExpression + "/country";
			NodeList nodeList = (NodeList) xPath.compile(conceptsExpression).evaluate(xmlDocument, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {
				NamedNodeMap attrs = nodeList.item(i).getAttributes();
				Node node = attrs.getNamedItem("rdf:resource");
				String itemDoi = node.getNodeValue();
				//Get organization name								
				String orgName = (String)xPath.compile(orgNameExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.STRING);
				//Get country name
				Node countryNode = (Node)xPath.compile(orgCountryExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.NODE);
				NamedNodeMap countryAttrs = countryNode.getAttributes();
				String countryCode = countryAttrs.getNamedItem("rdf:resource").getNodeValue();
				String countryName = fetchCountryNameFromGeoNames(countryCode);
				
				
			}
    	} catch(Exception e) {
    		
    	}               
    }
    
    private String fetchFromGeoNames(String geoNameUri, String propertyToFetch){
    	String result = null;
    	String geoNameId = geoNameUri.replaceAll( "[^\\d]", "" );    	
    	
    	String jsonResponse = fetchFromGeoNames(geoNameId);
    	result = fetchValueFromJson(jsonResponse, propertyToFetch);
    	
    	return result;
    }
    
    
    private String fetchFromGeoNames(String geoNameId) {
    	Client c = Client.create();    	
    	WebResource r = c.resource(geonamesApiUrl);
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("geonameId", geoNameId);
        params.add("username", apiUser);
        
        return r.queryParams(params).get(String.class);    	        
    }
    
    /**
     * It only fetches properties in the first level
     * */
    private String fetchValueFromJson(String jsonString, String propetyName){
    	String result = null;
    	try {
        	ObjectMapper m = new ObjectMapper();
        	JsonNode rootNode = m.readTree(jsonString);
        	JsonNode nameNode = rootNode.path("countryName");
        	result = nameNode.getTextValue();
        } catch (Exception e) {
        	
        }
    	return result;
    }
    
    private URI resolveUri(URI uri) {
        try {
            return new URI(uri.toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Calculated URI is invalid. Please check the settings.", e);
        }
    }
}





























