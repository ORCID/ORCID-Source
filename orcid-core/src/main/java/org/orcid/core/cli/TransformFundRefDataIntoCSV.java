package org.orcid.core.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.OrgManager;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class TransformFundRefDataIntoCSV {

    class FundRefOrganization {
        String id, name, altName, country, state, type, subtype;
    }    

    private static final Logger LOGGER = LoggerFactory.getLogger(TransformFundRefDataIntoCSV.class);   
    private static String geonamesApiUrl;
    // Params
    @Option(name = "-f", usage = "Path to RDF file containing the FundRef organizations")
    private File fundRefFile;
    
    // xPath init
    private XPath xPath = XPathFactory.newInstance().newXPath();

    // GeoNames Cache
    private HashMap<String, String> cache = new HashMap<String, String>();

    // Resources
    private GenericDao<OrgDisambiguatedExternalIdentifierEntity, Long> genericDao;
    private String apiUser;

    // xPath queries
    private String conceptsExpression = "/RDF/ConceptScheme/hasTopConcept";
    private String itemExpression = "/RDF/Concept[@about='%s']";
    private String orgNameExpression = itemExpression + "/prefLabel/Label/literalForm";
    private String orgAltNameExpression = itemExpression + "/altLabel/Label/literalForm";
    private String orgCountryExpression = itemExpression + "/country";
    private String orgStateExpression = itemExpression + "/state";
    private String orgTypeExpression = itemExpression + "/fundingBodyType";
    private String orgSubTypeExpression = itemExpression + "/fundingBodySubType";
    
    private String FUNDREF_CSV = "C:/Users/angel.montenegro/Desktop/fundref/crossref_complete.csv";
    private CSVWriter fundrefCSV = null;
    
    /**
     * INIT
     * */
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        // Geonames params
        geonamesApiUrl = (String) context.getBean("geonamesApiUrl");
        apiUser = (String) context.getBean("geonamesUser");

        // Init the CSV file for existing orgs
        try {
            Writer writer1 = new FileWriter(this.FUNDREF_CSV);
            fundrefCSV = createCSVWriter(writer1);
            // Write headers
            String[] headers = { "id","name","altName","country","state","type","subtype" };
            fundrefCSV.writeNext(headers);
        } catch (IOException ioe) {
            // TODO
        }
    }

    /**
     * Validate cmd arguments
     * */
    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (fundRefFile == null) {
            throw new CmdLineException(parser, "-f parameter must be specificed");
        }
    }

    public void process() {
        // Init
        init();
        // Load fundref organizations
        List<FundRefOrganization> fundRefOrgs = loadFundRefOrgs();
        
        for(FundRefOrganization fOrg : fundRefOrgs) {
            writeFundRefOrg(fOrg);
        }
        
        try {
            fundrefCSV.close();
        } catch (IOException ioe) {
            System.out.println("ERROR CLOSING CSV FILES");
        }
        // TODO Write duplicates names into a csv

        
    }

    /*****************************************************************************
     ******************************* FUNDREF FUNCTIONS ***************************
     ***************************************************************************** */

    /**
     * Load data from FundRef
     * */
    private List<FundRefOrganization> loadFundRefOrgs() {
        List<FundRefOrganization> fundRefOrgs = new ArrayList<FundRefOrganization>();
        System.out.println("Begin loading FundRef orgs");
        try {
            FileInputStream file = new FileInputStream(fundRefFile);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(file);
            // Parent node
            NodeList nodeList = (NodeList) xPath.compile(conceptsExpression).evaluate(xmlDocument, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                FundRefOrganization fOrg = getFundrefOrganization(xmlDocument, nodeList.item(i).getAttributes());
                fundRefOrgs.add(fOrg);               
            }
        } catch (FileNotFoundException fne) {
            LOGGER.error("Unable to read file {}", fundRefFile);
        } catch (ParserConfigurationException pce) {
            LOGGER.error("Unable to initialize the DocumentBuilder");
        } catch (IOException ioe) {
            LOGGER.error("Unable to parse document {}", fundRefFile);
        } catch (SAXException se) {
            LOGGER.error("Unable to parse document {}", fundRefFile);
        } catch (XPathExpressionException xpe) {
            LOGGER.error("XPathExpressionException {}", xpe.getMessage());
        }
        return fundRefOrgs;
    }

    /**
     * Parse a RDF node and convert it into a FundRefOrganization object
     * */
    private FundRefOrganization getFundrefOrganization(Document xmlDocument, NamedNodeMap attrs) {
        FundRefOrganization organization = new FundRefOrganization();
        try {
            Node node = attrs.getNamedItem("rdf:resource");
            String itemDoi = node.getNodeValue();
            LOGGER.info("Processing item {}", itemDoi);
            // Get organization name
            String orgName = (String) xPath.compile(orgNameExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.STRING);
            
            //Replace "U.S." with "US" to match RingGold info
            orgName = orgName.replace("U.S.", "US");
            
            // Get organization alt name
            String orgAltName = (String) xPath.compile(orgAltNameExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.STRING);
            // Get country geoname id
            Node countryNode = (Node) xPath.compile(orgCountryExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.NODE);
            NamedNodeMap countryAttrs = countryNode.getAttributes();
            String countryGeonameUrl = countryAttrs.getNamedItem("rdf:resource").getNodeValue();

            // Get state geoname id
            Node stateNode = (Node) xPath.compile(orgStateExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.NODE);
            String stateGeoNameCode = null;
            if (stateNode != null) {
                NamedNodeMap stateAttrs = stateNode.getAttributes();
                stateGeoNameCode = stateAttrs.getNamedItem("rdf:resource").getNodeValue();
            }
            
            // Get type
            String orgType = (String) xPath.compile(orgTypeExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.STRING);
            // Get subType
            String orgSubType = (String) xPath.compile(orgSubTypeExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.STRING);

            // Fill the organization object
            organization.type = StringUtils.isBlank(orgType) ? null : orgType;
            organization.id = StringUtils.isBlank(itemDoi) ? null : itemDoi;
            organization.name = StringUtils.isBlank(orgName) ? null : orgName;
            organization.altName = StringUtils.isBlank(orgAltName) ? null : orgAltName;                        
            organization.subtype = StringUtils.isBlank(orgSubType) ? null : orgSubType;
            
            // By this moment the geonames uris hasnt been resolved, so, resolve them
            // Fetch country code from geonames            
            if (StringUtils.isNotBlank(countryGeonameUrl))
                organization.country = fetchFromGeoNames(countryGeonameUrl, "countryCode");
            
            // Fetch state from geonames
            if (StringUtils.isNotBlank(stateGeoNameCode)) {                            
                organization.state = fetchFromGeoNames(stateGeoNameCode, "STATE");
                    
            }
            
        } catch (XPathExpressionException xpe) {
            LOGGER.error("XPathExpressionException {}", xpe.getMessage());
        }

        return organization;
    }        

    /*****************************************************************************
     ******************************* RINGOLD FUNCTIONS ***************************
     ***************************************************************************** */
    private CSVWriter createCSVWriter(Writer writer) {
        return new CSVWriter(writer, ',', '"');
    }
    
    private void writeFundRefOrg(FundRefOrganization organization) {
        // { "id","name","altName","country","state","type","subtype" }
        String[] newOrgLine = new String[7];
        newOrgLine[0] = organization.id;
        newOrgLine[1] = organization.name;
        newOrgLine[2] = organization.altName;
        newOrgLine[3] = organization.country;
        newOrgLine[4] = organization.state;
        newOrgLine[5] = organization.type;
        newOrgLine[6] = organization.subtype;
        fundrefCSV.writeNext(newOrgLine);
    }
        

    /*****************************************************************************
     ***************************** GEONAMES FUNCTIONS ****************************
     ***************************************************************************** */

    /**
     * Fetch a property from geonames
     * */
    private String fetchFromGeoNames(String geoNameUri, String propertyToFetch) {
        String result = null;
        String geoNameId = geoNameUri.replaceAll("[^\\d]", "");
        if (StringUtils.isNotBlank(geoNameId)) {
            String cacheKey = propertyToFetch + '_' + geoNameId;
            if (cache.containsKey(cacheKey)) {
                result = cache.get(cacheKey);
            } else {
                String jsonResponse = fetchJsonFromGeoNames(geoNameId);
                
                if(propertyToFetch.equals("STATE")){
                    result = fetchStateAbbrev(jsonResponse);
                } else {
                    result = fetchValueFromJson(jsonResponse, propertyToFetch);
                }
                                
                cache.put(cacheKey, result);
            }
        }

        return result;
    }

    /**
     * Queries GeoNames API for a given geonameId and return the JSON string
     * */
    private String fetchJsonFromGeoNames(String geoNameId) {
        String result = null;
        if (cache.containsKey("geoname_json_" + geoNameId)) {
            return cache.get("geoname_json_" + geoNameId);
        } else {
            Client c = Client.create();
            WebResource r = c.resource(geonamesApiUrl);
            MultivaluedMap<String, String> params = new MultivaluedMapImpl();
            params.add("geonameId", geoNameId);
            params.add("username", apiUser);
            result = r.queryParams(params).get(String.class);
            cache.put("geoname_json_" + geoNameId, result);
        }
        return result;
    }

    /**
     * It only fetches properties in the first level
     * */
    private String fetchValueFromJson(String jsonString, String propetyName) {
        String result = null;
        try {
            ObjectMapper m = new ObjectMapper();
            JsonNode rootNode = m.readTree(jsonString);
            JsonNode nameNode = rootNode.path(propetyName);
            if (nameNode != null)
                result = nameNode.getTextValue();
        } catch (Exception e) {

        }
        return result;
    }
    
    private String fetchStateAbbrev(String jsonString){
        String result = null;
        try {
            ObjectMapper m = new ObjectMapper();
            JsonNode rootNode = m.readTree(jsonString);
            JsonNode altNameNode = rootNode.path("alternateNames");
            if (altNameNode != null && altNameNode.isArray()){
                for(JsonNode node : altNameNode){
                    JsonNode type = node.path("lang"); 
                    if(type != null && "abbr".equalsIgnoreCase(type.getTextValue())){
                        JsonNode state = node.path("name");
                        result = state.getTextValue();
                        break;
                    }
                }
            }                
        } catch (Exception e) {

        }
        return result;
    }

    /*****************************************************************************
     ************************************* MAIN **********************************
     ***************************************************************************** */
    public static void main(String[] args) {
        TransformFundRefDataIntoCSV mergeData = new TransformFundRefDataIntoCSV();
        CmdLineParser parser = new CmdLineParser(mergeData);
        try {
            parser.parseArgument(args);
            mergeData.validateArgs(parser);
            mergeData.process();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }

    }
}
