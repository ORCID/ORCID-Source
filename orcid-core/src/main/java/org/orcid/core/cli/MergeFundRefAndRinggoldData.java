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

public class MergeFundRefAndRinggoldData {

    class FundRefOrganization {
        String id, name, altName, country, state, type, subtype;
    }

    class RingGoldOrganization {
        String id, name, country, city;
        List<RingGoldOrganizationAltNameAttributes> altNames;
    }

    class RingGoldOrganizationAltNameAttributes {
        String id, name, country, city;
    }
    
    class RingGoldNames {
        String name, id;
        boolean isPrimary;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeFundRefAndRinggoldData.class);
    private static final String RINGGOLD_CHARACTER_ENCODING = "UTF-8";
    private static final String FUNDREF_SOURCE_TYPE = "FUNDREF";
    private static final String RINGGOLD_SOURCE_TYPE = "RINGGOLD";
    private static String geonamesApiUrl;
    // Params
    @Option(name = "-f", usage = "Path to RDF file containing the FundRef organizations")
    private File fundRefFile;
    @Option(name = "-r", usage = "Path to the zip file containing the RingGold organizations")
    private File ringGoldFile;

    // xPath init
    private XPath xPath = XPathFactory.newInstance().newXPath();

    // GeoNames Cache
    private HashMap<String, String> cache = new HashMap<String, String>();

    // Resources
    private GenericDao<OrgDisambiguatedExternalIdentifierEntity, Long> genericDao;
    private OrgDisambiguatedDao orgDisambiguatedDao;
    private OrgManager orgManager;
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

    // Map with RingGold org names
    private Map<String, RingGoldNames> ringGoldNames = new HashMap<String, RingGoldNames>();

    // Map with RingGold orgs with duplicated names, this will hold the name of
    // the org and the ids with the same name
    private Map<String, List<String>> ringGoldOrgsWithDuplicatedNames = new HashMap<String, List<String>>();

    private String EXISTING_ORGS_FILE = "C:/Users/angel.montenegro/Desktop/mergeTest/latest/csv/existingOrgs.csv";
    private String NEW_ORGS_FILE = "C:/Users/angel.montenegro/Desktop/mergeTest/latest/csv/newOrgs.csv";

    private CSVWriter existingOrgsCSV = null;
    private CSVWriter newOrgsCSV = null;

    /**
     * INIT
     * */
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orgDisambiguatedDao = (OrgDisambiguatedDao) context.getBean("orgDisambiguatedDao");
        genericDao = (GenericDao) context.getBean("orgDisambiguatedExternalIdentifierEntityDao");
        orgManager = (OrgManager) context.getBean("orgManager");
        // Geonames params
        geonamesApiUrl = (String) context.getBean("geonamesApiUrl");
        apiUser = (String) context.getBean("geonamesUser");

        // Init the CSV file for existing orgs
        try {
            Writer writer1 = new FileWriter(this.EXISTING_ORGS_FILE);
            existingOrgsCSV = createCSVWriter(writer1);
            // Write headers
            String[] headers = { "ringgold-id", "fundref-id", "name", "alt-names", "fundref-name", "fundref-alt-name" };
            existingOrgsCSV.writeNext(headers);

            Writer wirter2 = new FileWriter(this.NEW_ORGS_FILE);
            newOrgsCSV = createCSVWriter(wirter2);

            headers = new String[] { "id", "name", "altName", "country", "state", "type", "subtype" };
            newOrgsCSV.writeNext(headers);

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

        if (ringGoldFile == null) {
            throw new CmdLineException(parser, "-r parameter must be specificed");
        }
    }

    public void process() {
        // Init
        init();
        // Load fundref organizations
        List<FundRefOrganization> fundRefOrgs = loadFundRefOrgs();
        // Load ringgold organizations
        Map<String, RingGoldOrganization> ringGoldOrgs = loadRinggoldOrgs();
        // Try to match fundref orgs and ringgold orgs
        System.out.println("Begin comparing orgs");
        System.out.println();
        int counter = 0;
        for (FundRefOrganization fOrg : fundRefOrgs) {
            String matchingRingGoldOrgId = findMatchesInRinggoldData(fOrg, ringGoldOrgs);
            // If no match is found, create FundRef organization into our
            // database
            if (matchingRingGoldOrgId == null) {
                createFundRefOrganization(fOrg);
            } else {
                writeMatchInformation(fOrg, ringGoldOrgs.get(matchingRingGoldOrgId));
            }
            if(counter % 100 == 0)
                System.out.println(counter);
            counter += 1;
        }
        System.out.println();
        System.out.println("Done comparing orgs");
        try {
            existingOrgsCSV.close();
            newOrgsCSV.close();
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
                if ("US".equalsIgnoreCase(organization.country))
                    organization.state = fetchFromGeoNames(stateGeoNameCode, "adminCode1");
                else
                    organization.state = fetchFromGeoNames(stateGeoNameCode, "name");
                        
                    
            }
            
        } catch (XPathExpressionException xpe) {
            LOGGER.error("XPathExpressionException {}", xpe.getMessage());
        }

        return organization;
    }

    /*****************************************************************************
     ******************************* RINGOLD FUNCTIONS ***************************
     ***************************************************************************** */

    private Reader getReader(ZipFile zip, ZipEntry entry) throws IOException, UnsupportedEncodingException {
        InputStream is = zip.getInputStream(entry);
        Reader reader = new InputStreamReader(is);
        return reader;
    }

    /**
     * Creates a CSV reader file
     * */
    private CSVReader createCSVReader(Reader reader) {
        return new CSVReader(reader, ',', '"', 1);
    }

    private CSVWriter createCSVWriter(Writer writer) {
        return new CSVWriter(writer, ',', '"');
    }

    /**
     * Process data from RingGold
     * */
    private Map<String, RingGoldOrganization> loadRinggoldOrgs() {
        Map<String, RingGoldOrganization> ringGoldOrgs = new HashMap<String, RingGoldOrganization>();
        Reader parents = null;
        Reader altNames = null;

        try (ZipFile zip = new ZipFile(ringGoldFile)) {
            for (ZipEntry entry : Collections.list(zip.entries())) {
                String entryName = entry.getName();
                if (entryName.endsWith("_parents.csv")) {
                    LOGGER.info("Found parents file: " + entryName);
                    parents = getReader(zip, entry);
                }
                if (entryName.endsWith("alt_names.csv")) {
                    LOGGER.info("Found alt names file: " + entryName);
                    altNames = getReader(zip, entry);
                }
            }

            // Process parents
            ringGoldOrgs = processParentsReader(parents);
            // Process altNames
            ringGoldOrgs = processAltNamesCsv(altNames, ringGoldOrgs);
        } catch (IOException e) {
            throw new RuntimeException("Error reading zip file", e);
        }

        return ringGoldOrgs;
    }

    private Map<String, RingGoldOrganization> processParentsReader(Reader reader) throws IOException {
        Map<String, RingGoldOrganization> ringGoldOrgs = new HashMap<String, RingGoldOrganization>();
        int orgsLoaded = 0;
        System.out.println("Loading parent orgs");
        try (CSVReader csvReader = createCSVReader(reader)) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                String id = line[1];
                RingGoldOrganization org = processParentLine(line);

                // Check if the org name already exists
                if (ringGoldNames.containsKey(org.name)) {
                    // If it does, add the name to the list of orgs with
                    // duplicated names
                    List<String> duplicatedNamesIds = new ArrayList<String>();
                    if (ringGoldOrgsWithDuplicatedNames.containsKey(org.name)) {
                        duplicatedNamesIds = ringGoldOrgsWithDuplicatedNames.get(org.name);
                    }
                    duplicatedNamesIds.add(org.id);
                    ringGoldOrgsWithDuplicatedNames.put(org.name, duplicatedNamesIds);
                } else {
                    // If not, add the name to the list of ringgold names
                    RingGoldNames rName = new RingGoldNames();
                    rName.id = org.id;
                    rName.name = org.name;
                    rName.isPrimary = true;
                    ringGoldNames.put(org.name, rName);
                }

                // Put the org into the ringgold orgs map
                ringGoldOrgs.put(id, org);
                orgsLoaded++;                
            }
        } finally {
            LOGGER.info("RingGold orgs loaded, total={}", new Object[] { orgsLoaded });
        }
        
        System.out.println("---------------------------------------");
        System.out.println("Orgs names loaded:  " + orgsLoaded);
        System.out.println("---------------------------------------");
        return ringGoldOrgs;
    }

    private RingGoldOrganization processParentLine(String[] line) {
        String pCode = line[1];
        String name = line[2];
        String extName = line[3];
        String city = line[4];
        String country = line[7];
        
        if (StringUtils.isNotBlank(extName)) {
            name = extName;
        }
        
        // To match country names, change ringgold USA to US, since US is used in fundref
        if("USA".equals(country)){
            country = "US";
        }
        RingGoldOrganization org = new RingGoldOrganization();        
        org.id = StringUtils.isBlank(pCode) ? null : pCode;
        org.name = StringUtils.isBlank(name) ? null : name;
        org.city = city;
        org.country = country;
        
        return org;
    }

    /**
     * Process the alt names csv file
     * */
    private Map<String, RingGoldOrganization> processAltNamesCsv(Reader reader, Map<String, RingGoldOrganization> parentOrgs) throws IOException {
        int altNamesLoaded = 0;
        System.out.println("Loading parent orgs");
        try (CSVReader csvReader = createCSVReader(reader)) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                String id = line[0];
                String altName = line[1];
                String altExtName = line[2];
                String city = line[4];
                String country = line[6];
                
                if (StringUtils.isNotBlank(altExtName)) {
                    altName = altExtName;
                }

                // Check if the org name already exists
                if(StringUtils.isNotBlank(altName)) {
                    if (ringGoldNames.containsKey(altName)) {
                        // If it does, add the name to the list of orgs with
                        // duplicated names
                        List<String> duplicatedNamesIds = new ArrayList<String>();
                        if (ringGoldOrgsWithDuplicatedNames.containsKey(altName)) {
                            duplicatedNamesIds = ringGoldOrgsWithDuplicatedNames.get(altName);
                        }
                        duplicatedNamesIds.add(id);
                        ringGoldOrgsWithDuplicatedNames.put(altName, duplicatedNamesIds);
                    } else {
                        // If not, add the name to the list of ringgold names
                        RingGoldNames rName = new RingGoldNames();
                        rName.id = id;
                        rName.name = altName;
                        rName.isPrimary = false;
                        ringGoldNames.put(altName, rName);
                    }
    
                    // Add the alt name to the existing parent org in the parentOrgs
                    // map
                    RingGoldOrganization parentOrg = parentOrgs.get(id);
                    List<RingGoldOrganizationAltNameAttributes> altNames = parentOrg.altNames;
                    if (altNames == null) {
                        altNames = new ArrayList<RingGoldOrganizationAltNameAttributes>();
                    }
                    
                    RingGoldOrganizationAltNameAttributes altNameAtt = new RingGoldOrganizationAltNameAttributes();
                    altNameAtt.name = altName;
                    altNameAtt.city = city;
                    altNameAtt.country = country;
                    
                    altNames.add(altNameAtt);
                    parentOrg.altNames = altNames;
                    altNamesLoaded++;                
                }
            }
        } finally {
            LOGGER.info("RingGold alt names loaded, total={}", new Object[] { altNamesLoaded });
        }
        
        System.out.println("---------------------------------------");
        System.out.println("Alt names loaded:  " + altNamesLoaded);
        System.out.println("---------------------------------------");
        
        return parentOrgs;
    }

    /*****************************************************************************
     ***************************** MATCH ORGS FUNCTIONS **************************
     ***************************************************************************** */

    /**
     * Find a Ringgold organization that matches the provided RDF organization
     * */
    private String findMatchesInRinggoldData(FundRefOrganization fOrg, Map<String, RingGoldOrganization> ringgoldOrgs) {
        String ringGoldId = null;
        if (StringUtils.isNotBlank(fOrg.name) && ringGoldNames.containsKey(fOrg.name)) {     
            RingGoldNames rName = ringGoldNames.get(fOrg.name);
            RingGoldOrganization rOrg = ringgoldOrgs.get(rName.id);
            //Compare country and city
            if(StringUtils.isNotBlank(fOrg.country) && fOrg.country.equals(rOrg.country)){
                System.out.println("Possible Match: " + rName.id + " - " + rName.name + " - " + rName.isPrimary);
                //If the state is not null, compare states
                if(StringUtils.isNotBlank(fOrg.state)){
                    if(StringUtils.isNotBlank(rOrg.city)){
                        if(fOrg.state.equals(rOrg.city)){
                            ringGoldId = rOrg.id;
                        }
                    }
                } else {
                    //If it is null, assume this is a match
                    ringGoldId = rOrg.id;
                }
            }
            
        } else if (StringUtils.isNotBlank(fOrg.altName) && ringGoldNames.containsKey(fOrg.altName)) {
            //Get the alt name object
            RingGoldNames rAltName = ringGoldNames.get(fOrg.altName);
            //Get the RingGold organization
            RingGoldOrganization rOrg = ringgoldOrgs.get(rAltName.id);                        
            System.out.println("AltName Possible Match: " + rAltName.id + " - " + rAltName.name + " - " + rAltName.isPrimary);
            //If the match is found with the FundRef alt name, check first the primary name, then check the alt names
            if(rAltName.isPrimary) {
                //Compare the alt name with the ringgold primary name
                if(fOrg.altName.equals(rOrg.name)){
                    
                  //Compare country and city
                    if(StringUtils.isNotBlank(fOrg.country) && fOrg.country.equals(rOrg.country)){
                        
                        //If the state is not null, compare states
                        if(StringUtils.isNotBlank(fOrg.state)){
                            if(StringUtils.isNotBlank(rOrg.city)){
                                if(fOrg.state.equals(rOrg.city)){
                                    ringGoldId = rOrg.id;
                                }
                            }
                        } else {
                            //If it is null, since they are in the same country, assume this is a match
                            ringGoldId = rOrg.id;
                        }
                    }
                }
            } else {
                //Look for the alt org 
                RingGoldOrganizationAltNameAttributes altOrg = null;
                //Compare the alt name with the ringgold alt names    
                if(rOrg.altNames != null) {
                    for(RingGoldOrganizationAltNameAttributes altOrgIt : rOrg.altNames) {
                        if(StringUtils.isNotBlank(altOrgIt.name) && altOrgIt.name.equals(fOrg.altName)){
                            altOrg = altOrgIt;
                            break;
                        }
                    }
                }
                
                //If the alt org was found
                if(altOrg != null){
                    //Compare country and city
                    if(StringUtils.isNotBlank(fOrg.country) && fOrg.country.equals(altOrg.country)){
                        
                        //If the state is not null, compare states
                        if(StringUtils.isNotBlank(fOrg.state)){
                            if(StringUtils.isNotBlank(altOrg.city)){
                                if(fOrg.state.equals(altOrg.city)){
                                    ringGoldId = altOrg.id;
                                }
                            }
                        } else {
                            //If it is null, assume this is a match
                            ringGoldId = altOrg.id;
                        }
                    }
                }
            }
        }
        
        return ringGoldId;
    }
    
    /*****************************************************************************
     ************* CREATING ORGS OR UPDATING MATCH LIST FUNCTIONS ****************
     ***************************************************************************** */
    
    /**
     * Create a new organization in database
     * */
    private void createFundRefOrganization(FundRefOrganization organization) {
        LOGGER.info("Creating disambiguated org {}", organization.name);
        String orgType = organization.type + (StringUtils.isEmpty(organization.subtype) ? "" : "/" + organization.subtype);

        // Get the country code
        Iso3166Country country = StringUtils.isNotBlank(organization.country) ? Iso3166Country.fromValue(organization.country) : null;
        // Fill the orgDisambiguated entity
        OrgDisambiguatedEntity orgDisambiguatedEntity = new OrgDisambiguatedEntity();
        orgDisambiguatedEntity.setName(organization.name);
        orgDisambiguatedEntity.setRegion(organization.state);
        orgDisambiguatedEntity.setCountry(country);
        orgDisambiguatedEntity.setOrgType(orgType);
        orgDisambiguatedEntity.setSourceId(organization.id);
        orgDisambiguatedEntity.setSourceUrl(organization.id);
        orgDisambiguatedEntity.setSourceType(FUNDREF_SOURCE_TYPE);
        orgDisambiguatedDao.persist(orgDisambiguatedEntity);

        writeNewOrgsInformation(organization);
    }

    private void writeNewOrgsInformation(FundRefOrganization organization) {
        // {"id", "name", "altName", "country", "state", "type", "subtype"}
        String[] newOrgLine = new String[7];
        newOrgLine[0] = organization.id;
        newOrgLine[1] = organization.name;
        newOrgLine[2] = organization.altName;
        newOrgLine[3] = organization.country;
        newOrgLine[4] = organization.state;
        newOrgLine[5] = organization.type;
        newOrgLine[6] = organization.subtype;
        newOrgsCSV.writeNext(newOrgLine);
    }

    /**
     * Writes information about a FundRef org that matches with a Ringgold org
     * */
    private void writeMatchInformation(FundRefOrganization fOrg, RingGoldOrganization rOrg) {
        // {"ringgold-id", "fundref-id", "name", "alt-names", "fundref-name",
        // "fundref-alt-name"}
        String[] organizationMatchLine = new String[6];

        organizationMatchLine[0] = rOrg.id;
        organizationMatchLine[1] = fOrg.id;
        organizationMatchLine[2] = rOrg.name;

        if (rOrg.altNames != null && rOrg.altNames.size() > 0) {
            
            List<String> altNameOrgsNames = new ArrayList<>();
            for(RingGoldOrganizationAltNameAttributes altName : rOrg.altNames) {
                altNameOrgsNames.add(altName.name);
            }
            
            Collections.sort(altNameOrgsNames);
            String altNames = StringUtils.join(altNameOrgsNames, '|');
            organizationMatchLine[3] = altNames;
        }
        organizationMatchLine[4] = fOrg.name;
        organizationMatchLine[5] = fOrg.altName;

        existingOrgsCSV.writeNext(organizationMatchLine);
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
                result = fetchValueFromJson(jsonResponse, propertyToFetch);
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

    /*****************************************************************************
     ************************************* MAIN **********************************
     ***************************************************************************** */
    public static void main(String[] args) {
        MergeFundRefAndRinggoldData mergeData = new MergeFundRefAndRinggoldData();
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
