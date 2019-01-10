package org.orcid.core.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.constants.OrganizationStatus;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class LoadFundRefData {

    class RDFOrganization {
        String doi, name, country, state, stateCode, city, type, subtype, status, isReplacedBy;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadFundRefData.class);
    private static final String STATE_NAME = "STATE";
    private static final String STATE_ABBREVIATION = "abbr";
    private static final String DEPRECATED_INDICATOR = "http://data.crossref.org/fundingdata/vocabulary/Deprecated";
    
    
    private static String geonamesApiUrl;
    // Params
    @Option(name = "-f", usage = "Path to RDF file containing FundRef info to load into DB")
    private File fileToLoad;

    // Resources    
    private OrgDisambiguatedDao orgDisambiguatedDao;
    private String apiUser;
    // Cache
    private HashMap<String, String> cache = new HashMap<String, String>();
    // xPath queries
    private String conceptsExpression = "/RDF/ConceptScheme/hasTopConcept";
    private String itemExpression = "/RDF/Concept[@about='%s']";
    private String orgNameExpression = "prefLabel/Label/literalForm";
    private String orgCountryExpression = "country";
    private String orgStateExpression = "state";
    private String orgTypeExpression = "fundingBodyType";
    private String orgSubTypeExpression = "fundingBodySubType";
    private String statusExpression = "status";
    private String isReplacedByExpression = "isReplacedBy";
    // xPath init
    private XPath xPath = XPathFactory.newInstance().newXPath();
    // Statistics
    private long updatedOrgs = 0;
    private long addedDisambiguatedOrgs = 0;    
    private long depreciatedOrgs = 0;

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
        System.exit(0);
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (fileToLoad == null) {
            throw new CmdLineException(parser, "-f parameter must be specificed");
        }
    }

    private void init() {
        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orgDisambiguatedDao = (OrgDisambiguatedDao) context.getBean("orgDisambiguatedDao");        
        // Geonames params
        geonamesApiUrl = (String) context.getBean("geonamesApiUrl");
        apiUser = (String) context.getBean("geonamesUser");
    }

    /**
     * Executes the import process
     * */
    private void execute() {
        try {
            long start = System.currentTimeMillis();
            FileInputStream file = new FileInputStream(fileToLoad);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(file);
            // Parent node
            NodeList nodeList = (NodeList) xPath.compile(conceptsExpression).evaluate(xmlDocument, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                RDFOrganization rdfOrganization = getOrganization(xmlDocument, nodeList.item(i).getAttributes());
                LOGGER.info("Processing organization from RDF, doi:{}", new String[] {
                        rdfOrganization.doi });
                // #1: Look for an existing org
                OrgDisambiguatedEntity existingEntity = findById(rdfOrganization);
                if(existingEntity != null) {
                    // #2: If the name, city or region changed, update those values
                    if(entityChanged(rdfOrganization, existingEntity)) {
                        existingEntity.setCity(rdfOrganization.city);
                        Iso3166Country country = StringUtils.isNotBlank(rdfOrganization.country) ? Iso3166Country.fromValue(rdfOrganization.country) : null;
                        existingEntity.setCountry(country == null ? null : country.name());
                        existingEntity.setName(rdfOrganization.name);                        
                        String orgType = getOrgType(rdfOrganization);
                        existingEntity.setOrgType(orgType);                        
                        existingEntity.setRegion(rdfOrganization.stateCode);
                        existingEntity.setSourceId(rdfOrganization.doi);
                        existingEntity.setSourceType(OrgDisambiguatedSourceType.FUNDREF.name());
                        existingEntity.setSourceUrl(rdfOrganization.doi);
                        existingEntity.setLastModified(new Date());
                        existingEntity.setIndexingStatus(IndexingStatus.PENDING);
                        existingEntity.setStatus(rdfOrganization.status);
                        orgDisambiguatedDao.merge(existingEntity); 
                        updatedOrgs += 1;
                    } else if(statusChanged(rdfOrganization, existingEntity)) {
                        //If the status changed, update the status
                        existingEntity.setStatus(rdfOrganization.status);
                        existingEntity.setLastModified(new Date());
                        existingEntity.setIndexingStatus(IndexingStatus.PENDING);
                        orgDisambiguatedDao.merge(existingEntity); 
                        depreciatedOrgs += 1;
                    } else {
                        // Check if it is depreciated
                        if(StringUtils.isNotBlank(rdfOrganization.isReplacedBy)) {
                            if(!rdfOrganization.isReplacedBy.equals(existingEntity.getSourceParentId())) {
                                existingEntity.setSourceParentId(rdfOrganization.isReplacedBy);
                                existingEntity.setStatus(OrganizationStatus.DEPRECATED.name());
                                existingEntity.setLastModified(new Date());
                                existingEntity.setIndexingStatus(IndexingStatus.PENDING);
                                orgDisambiguatedDao.merge(existingEntity); 
                                depreciatedOrgs += 1;
                            }
                        } 
                    }
                } else {                    
                    // If it doesn't exists, create the new org
                    createDisambiguatedOrg(rdfOrganization);                    
                    addedDisambiguatedOrgs += 1;                                                             
                }
            }
            long end = System.currentTimeMillis();
            LOGGER.info("Time taken to process the files: {}", (end - start));
        } catch (FileNotFoundException fne) {
            LOGGER.error("Unable to read file {}", fileToLoad);
        } catch (ParserConfigurationException pce) {
            LOGGER.error("Unable to initialize the DocumentBuilder");
        } catch (IOException ioe) {
            LOGGER.error("Unable to parse document {}", fileToLoad);
        } catch (SAXException se) {
            LOGGER.error("Unable to parse document {}", fileToLoad);
        } catch (XPathExpressionException xpe) {
            LOGGER.error("XPathExpressionException {}", xpe.getMessage());
        } finally {
            LOGGER.info("Number new Disambiguated Orgs={}, Updated Orgs={}, Depreciated Orgs={}", new Object[] { addedDisambiguatedOrgs, updatedOrgs, depreciatedOrgs});
        }

    }

    /**
     * FUNDREF FUNCTIONS
     * */

    /**
     * Get an RDF organization from the given RDF file
     * */
    private RDFOrganization getOrganization(Document xmlDocument, NamedNodeMap attrs) {
        RDFOrganization organization = new RDFOrganization();
        try {
            Node node = attrs.getNamedItem("rdf:resource");
            String itemDoi = node.getNodeValue();
            //Get item node
            Node organizationNode = (Node) xPath.compile(itemExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.NODE);
            
            // Get organization name
            String orgName = (String) xPath.compile(orgNameExpression).evaluate(organizationNode, XPathConstants.STRING);
            
            // Get status indicator
            Node statusNode = (Node) xPath.compile(statusExpression).evaluate(organizationNode, XPathConstants.NODE);
            String status = null;
            if(statusNode != null) {
                NamedNodeMap statusAttrs = statusNode.getAttributes();
                if(statusAttrs != null) {
                    String statusAttribute = statusAttrs.getNamedItem("rdf:resource").getNodeValue();
                    if(isDeprecatedStatus(statusAttribute)) {
                        status = OrganizationStatus.DEPRECATED.name();
                    }
                }
            }
                        
            // Get country code
            Node countryNode = (Node) xPath.compile(orgCountryExpression).evaluate(organizationNode, XPathConstants.NODE);
            NamedNodeMap countryAttrs = countryNode.getAttributes();
            String countryGeonameUrl = countryAttrs.getNamedItem("rdf:resource").getNodeValue();
            String countryCode = fetchFromGeoNames(countryGeonameUrl, "countryCode");

            // Get state name
            Node stateNode = (Node) xPath.compile(orgStateExpression).evaluate(organizationNode, XPathConstants.NODE);
            String stateName = null;
            String stateCode = null;
            if (stateNode != null) {
                NamedNodeMap stateAttrs = stateNode.getAttributes();
                String stateGeoNameCode = stateAttrs.getNamedItem("rdf:resource").getNodeValue();
                stateName = fetchFromGeoNames(stateGeoNameCode, "name");
                stateCode = fetchFromGeoNames(stateGeoNameCode, STATE_NAME);
            }

            // Get type
            String orgType = (String) xPath.compile(orgTypeExpression).evaluate(organizationNode, XPathConstants.STRING);
            // Get subType
            String orgSubType = (String) xPath.compile(orgSubTypeExpression).evaluate(organizationNode, XPathConstants.STRING);

            // Get parent id
            Node isReplacedByNode = (Node) xPath.compile(isReplacedByExpression).evaluate(organizationNode, XPathConstants.NODE);
            String isReplacedBy = null;
            if(isReplacedByNode != null) {
                isReplacedBy = isReplacedByNode.getAttributes().getNamedItem("rdf:resource").getNodeValue();
            }
            
            // Fill the organization object
            organization.doi = itemDoi;
            organization.name = orgName;
            organization.country = countryCode;
            organization.state = stateName;
            organization.stateCode = stateCode;
            organization.city = stateCode;
            organization.type = orgType;
            organization.subtype = orgSubType;
            organization.status = status;
            organization.isReplacedBy = isReplacedBy;
        } catch (XPathExpressionException xpe) {
            LOGGER.error("XPathExpressionException {}", xpe.getMessage());
        }

        return organization;
    }

    /**
     * Indicates if an organization has been marked as deprecated
     */
    private boolean isDeprecatedStatus(String statusAttribute) {
        return DEPRECATED_INDICATOR.equalsIgnoreCase(statusAttribute);
    }
    
    /**
     * GEONAMES FUNCTIONS
     * */
    
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
                if (STATE_NAME.equals(propertyToFetch)) {
                    result = fetchStateAbbreviationFromJson(jsonResponse);
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
            MultivaluedMap<String, String> params = new MultivaluedMapImpl();
            params.add("geonameId", geoNameId);
            params.add("username", apiUser);
            WebResource r = c.resource(geonamesApiUrl).queryParams(params);
            ClientResponse response = r.get(ClientResponse.class);
            int status = response.getStatus();
            if (status == 200) {
                result = response.getEntity(String.class);
            } else {
                LOGGER.warn("Got error status from geonames: {}", status);
                try {
                    LOGGER.info("Waiting before retrying geonames...");
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                ClientResponse retryResponse = r.get(ClientResponse.class);
                int retryStatus = retryResponse.getStatus();
                if (retryStatus == 200) {
                    result = retryResponse.getEntity(String.class);
                } else {
                    String message = "Geonames failed after retry with status: " + retryStatus;
                    LOGGER.error(message);
                    throw new RuntimeException(message);
                }
            }
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
                result = nameNode.asText();
        } catch (Exception e) {

        }
        return result;
    }

    /**
     * Fetch the state abbreviation from a geonames response
     * */
    private String fetchStateAbbreviationFromJson(String jsonString) {
        String result = null;
        try {
            ObjectMapper m = new ObjectMapper();
            JsonNode rootNode = m.readTree(jsonString);
            JsonNode arrayNode = rootNode.get("alternateNames");
            if (arrayNode != null && arrayNode.isArray()) {
                for (final JsonNode altNameNode : arrayNode) {
                    JsonNode langNode = altNameNode.get("lang");
                    if (langNode != null && STATE_ABBREVIATION.equals(langNode.asText())) {
                        JsonNode nameNode = altNameNode.get("name");
                        result = nameNode.asText();
                        break;
                    }
                }
            }
        } catch (Exception e) {

        }
        return result;
    }

    /**
     * DATABASE FUNCTIONS
     * */
    private OrgDisambiguatedEntity findById(RDFOrganization org) {         
        return  orgDisambiguatedDao.findBySourceIdAndSourceType(org.doi, OrgDisambiguatedSourceType.FUNDREF.name());              
    }

    /**
     * Indicates if an entity changed his name, country, state or city
     * 
     * @param org
     *            The organization with the new values
     * @param entity
     *            The organization we have stored in the database
     * 
     * @return true if the entity has changed.
     */
    private boolean entityChanged(RDFOrganization org, OrgDisambiguatedEntity entity) {
        // Check name
        if (StringUtils.isNotBlank(org.name)) {
            if (!org.name.equalsIgnoreCase(entity.getName()))
                return true;
        } else if (StringUtils.isNotBlank(entity.getName())) {
            return true;
        }
        // Check country
        if (StringUtils.isNotBlank(org.country)) {
            if (entity.getCountry() == null || !org.country.equals(entity.getCountry())) {
                return true;
            }
        } else if (entity.getCountry() != null) {
            return true;
        }
        // Check state
        if (StringUtils.isNotBlank(org.stateCode)) {
            if (entity.getRegion() == null || !org.stateCode.equals(entity.getRegion())) {
                return true;
            }
        } else if (StringUtils.isNotBlank(entity.getRegion())) {
            return true;
        }
        // Check city
        if (StringUtils.isNotBlank(org.city)) {
            if (entity.getCity() == null || !org.city.equals(entity.getCity())) {
                return true;
            }
        } else if (StringUtils.isNotBlank(entity.getCity())) {
            return true;
        }
        
        // Check org type
        String orgType = getOrgType(org);
        
        if(StringUtils.isNotBlank(org.type)) {
            if(entity.getOrgType() == null || !entity.getOrgType().equals(orgType)) {
                return true;
            }
        } 
        
        return false;
    }
    
    private String getOrgType(RDFOrganization org) {        
        return org.type + (StringUtils.isEmpty(org.subtype) ? "" : '/' + org.subtype);
    }
    
    /**
     * Indicates if an entity status has changed
     * 
     * @param org
     *            The organization with the new values
     * @param entity
     *            The organization we have stored in the database
     * 
     * @return true if the entity status has changed.
     */
    private boolean statusChanged(RDFOrganization org, OrgDisambiguatedEntity entity) {
        if(!PojoUtil.isEmpty(org.status)) {
            if(!org.status.equalsIgnoreCase(entity.getStatus())) {
                return true;
            }
        } else if(!PojoUtil.isEmpty(entity.getStatus())) {
            //If for some reason, the status of the updated organization is removed, remove it also from our data
            return true;
        }
        return false;
    }
    
    /**
     * Creates a disambiguated ORG in the org_disambiguated table
     * */
    private OrgDisambiguatedEntity createDisambiguatedOrg(RDFOrganization organization) {
        LOGGER.info("Creating disambiguated org {}", organization.name);
        String orgType = getOrgType(organization);
        Iso3166Country country = StringUtils.isNotBlank(organization.country) ? Iso3166Country.fromValue(organization.country) : null;
        OrgDisambiguatedEntity orgDisambiguatedEntity = new OrgDisambiguatedEntity();
        orgDisambiguatedEntity.setName(organization.name);
        orgDisambiguatedEntity.setCountry(country == null ? null : country.name());       
        orgDisambiguatedEntity.setCity(organization.city);
        orgDisambiguatedEntity.setRegion(organization.stateCode);        
        orgDisambiguatedEntity.setOrgType(orgType);
        orgDisambiguatedEntity.setSourceId(organization.doi);
        orgDisambiguatedEntity.setSourceUrl(organization.doi);                
        // Is it deprecated?
        if(!PojoUtil.isEmpty(organization.status)) {            
            orgDisambiguatedEntity.setStatus(OrganizationStatus.DEPRECATED.name());            
        }        
        // Is it replaced?
        if(!PojoUtil.isEmpty(organization.isReplacedBy)) {
            orgDisambiguatedEntity.setSourceParentId(organization.isReplacedBy);
            orgDisambiguatedEntity.setStatus(OrganizationStatus.DEPRECATED.name());
        }
        orgDisambiguatedEntity.setSourceType(OrgDisambiguatedSourceType.FUNDREF.name());       
        
        orgDisambiguatedDao.persist(orgDisambiguatedEntity);
        return orgDisambiguatedEntity;
    }
}
