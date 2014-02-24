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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class LoadFundRefData {

    class RDFOrganization {
        String doi, name, country, state, stateCode, city, type, subtype;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadFundRefData.class);
    private static final String FUNDREF_SOURCE_TYPE = "FUNDREF";
    private static final String STATE_NAME = "STATE";
    private static final String STATE_ABBREVIATION = "abbr";
    private static String geonamesApiUrl;
    // Params
    @Option(name = "-f", usage = "Path to RDF file containing FundRef info to load into DB")
    private File fileToLoad;

    // Resources
    private GenericDao<OrgDisambiguatedExternalIdentifierEntity, Long> orgDisambiguatedExternalIdentifierDao;
    private OrgDisambiguatedDao orgDisambiguatedDao;
    private String apiUser;
    // Cache
    private HashMap<String, String> cache = new HashMap<String, String>();
    // xPath queries
    private String conceptsExpression = "/RDF/ConceptScheme/hasTopConcept";
    private String itemExpression = "/RDF/Concept[@about='%s']";
    private String orgNameExpression = itemExpression + "/prefLabel/Label/literalForm";
    private String orgCountryExpression = itemExpression + "/country";
    private String orgStateExpression = itemExpression + "/state";
    private String orgTypeExpression = itemExpression + "/fundingBodyType";
    private String orgSubTypeExpression = itemExpression + "/fundingBodySubType";
    // xPath init
    private XPath xPath = XPathFactory.newInstance().newXPath();
    // Statistics
    private long updatedOrgs = 0;
    private long addedDisambiguatedOrgs = 0;
    private long addedExternalIdentifiers = 0;

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
        orgDisambiguatedExternalIdentifierDao = (GenericDao) context.getBean("orgDisambiguatedExternalIdentifierEntityDao");
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
                LOGGER.info("Processing organization from RDF, doi:{}, name:{}, country:{}, state:{}, stateCode:{}, type:{}, subtype:{}", new String[] {
                        rdfOrganization.doi, rdfOrganization.name, rdfOrganization.country, rdfOrganization.state, rdfOrganization.stateCode, rdfOrganization.type,
                        rdfOrganization.subtype });
                // #1: Look for an existing org
                OrgDisambiguatedEntity existingEntity = findByDetails(rdfOrganization);
                if(existingEntity != null) {
                    // #2: If the name, city or region changed, update those values
                    if(entityChanged(rdfOrganization, existingEntity)) {
                        existingEntity.setCity(rdfOrganization.city);
                        Iso3166Country country = StringUtils.isNotBlank(rdfOrganization.country) ? Iso3166Country.fromValue(rdfOrganization.country) : null;
                        existingEntity.setCountry(country);
                        existingEntity.setName(rdfOrganization.name);                        
                        String orgType = rdfOrganization.type + (StringUtils.isNotBlank(rdfOrganization.subtype) ? ('/' + rdfOrganization.subtype) : "");
                        existingEntity.setOrgType(orgType);                        
                        existingEntity.setRegion(rdfOrganization.stateCode);
                        existingEntity.setSourceId(rdfOrganization.doi);
                        existingEntity.setSourceType(FUNDREF_SOURCE_TYPE);
                        existingEntity.setSourceUrl(rdfOrganization.doi);
                        orgDisambiguatedDao.merge(existingEntity); 
                        updatedOrgs += 1;
                    } else if(idChanged(rdfOrganization, existingEntity)){
                        // #3: If the ID changed, create an external identifier
                        createExternalIdentifier(existingEntity, rdfOrganization.doi);
                        addedExternalIdentifiers += 1;
                    }
                } else {
                    // #4: Else, create the new org
                    OrgDisambiguatedEntity newOrg = createDisambiguatedOrg(rdfOrganization);                    
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
            LOGGER.info("Number new Disambiguated Orgs={}, Updated Orgs={}, new External Identifiers={}", new Object[] { addedDisambiguatedOrgs, updatedOrgs,
                    addedExternalIdentifiers, getTotal() });
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
            LOGGER.info("Processing item {}", itemDoi);
            // Get organization name
            String orgName = (String) xPath.compile(orgNameExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.STRING);
            // Get country code
            Node countryNode = (Node) xPath.compile(orgCountryExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.NODE);
            NamedNodeMap countryAttrs = countryNode.getAttributes();
            String countryGeonameUrl = countryAttrs.getNamedItem("rdf:resource").getNodeValue();
            String countryCode = fetchFromGeoNames(countryGeonameUrl, "countryCode");

            // Get state name
            Node stateNode = (Node) xPath.compile(orgStateExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.NODE);
            String stateName = null;
            String stateCode = null;
            if (stateNode != null) {
                NamedNodeMap stateAttrs = stateNode.getAttributes();
                String stateGeoNameCode = stateAttrs.getNamedItem("rdf:resource").getNodeValue();
                stateName = fetchFromGeoNames(stateGeoNameCode, "name");
                stateCode = fetchFromGeoNames(stateGeoNameCode, STATE_NAME);
            }

            // Get type
            String orgType = (String) xPath.compile(orgTypeExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.STRING);
            // Get subType
            String orgSubType = (String) xPath.compile(orgSubTypeExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.STRING);

            // Fill the organization object
            organization.doi = itemDoi;
            organization.name = orgName;
            organization.country = countryCode;
            organization.state = stateName;
            organization.stateCode = stateCode;
            // TODO: since we don't have city, we fill this with the state, this
            // should be modified soon
            organization.city = stateCode;
            organization.type = orgType;
            organization.subtype = orgSubType;
        } catch (XPathExpressionException xpe) {
            LOGGER.error("XPathExpressionException {}", xpe.getMessage());
        }

        return organization;
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
                    if (langNode != null && STATE_ABBREVIATION.equals(langNode.getTextValue())) {
                        JsonNode nameNode = altNameNode.get("name");
                        result = nameNode.getTextValue();
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

    /**
     * TODO
     * */
    private OrgDisambiguatedEntity findByDetails(RDFOrganization org) {
        Iso3166Country country = StringUtils.isBlank(org.country) ? null : Iso3166Country.valueOf(org.country);
        // Find the org by name, city, country and state
        OrgDisambiguatedEntity existingEntity = orgDisambiguatedDao.findByNameCityRegionCountryAndSourceType(org.name, org.stateCode, org.stateCode, country,
                FUNDREF_SOURCE_TYPE);
        // If no match is found, try with the doi and source type
        if (existingEntity == null) {
            existingEntity = orgDisambiguatedDao.findBySourceIdAndSourceType(org.doi, FUNDREF_SOURCE_TYPE);
        }

        return existingEntity;
    }

    /**
     * TODO
     * */
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
            if (entity.getCountry() == null || !org.country.equals(entity.getCountry().value())) {
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

        return false;
    }
    
    /**
     * TODO
     * */
    private boolean idChanged(RDFOrganization org, OrgDisambiguatedEntity entity) {
        if(org.doi.equals(entity.getSourceId()))
            return false;
        return true;
    }

    /**
     * Creates a disambiguated ORG in the org_disambiguated table
     * */
    private OrgDisambiguatedEntity createDisambiguatedOrg(RDFOrganization organization) {
        LOGGER.info("Creating disambiguated org {}", organization.name);
        String orgType = organization.type + (StringUtils.isEmpty(organization.subtype) ? "" : "/" + organization.subtype);
        Iso3166Country country = StringUtils.isNotBlank(organization.country) ? Iso3166Country.fromValue(organization.country) : null;
        OrgDisambiguatedEntity orgDisambiguatedEntity = new OrgDisambiguatedEntity();
        orgDisambiguatedEntity.setName(organization.name);
        orgDisambiguatedEntity.setCountry(country);       
        orgDisambiguatedEntity.setCity(organization.city);
        orgDisambiguatedEntity.setRegion(organization.stateCode);        
        orgDisambiguatedEntity.setOrgType(orgType);
        orgDisambiguatedEntity.setSourceId(organization.doi);
        orgDisambiguatedEntity.setSourceUrl(organization.doi);
        orgDisambiguatedEntity.setSourceType(FUNDREF_SOURCE_TYPE);
        orgDisambiguatedDao.persist(orgDisambiguatedEntity);
        return orgDisambiguatedEntity;
    }
    
    /**
     * Creates an external identifier in the
     * org_disambiguated_external_identifier table
     * */
    private boolean createExternalIdentifier(OrgDisambiguatedEntity disambiguatedOrg, String identifier) {
        LOGGER.info("Creating external identifier for {}", disambiguatedOrg.getId());
        Date creationDate = new Date();
        OrgDisambiguatedExternalIdentifierEntity externalIdentifier = new OrgDisambiguatedExternalIdentifierEntity();
        externalIdentifier.setIdentifier(identifier);
        externalIdentifier.setIdentifierType(FUNDREF_SOURCE_TYPE);
        externalIdentifier.setOrgDisambiguated(disambiguatedOrg);
        externalIdentifier.setDateCreated(creationDate);
        externalIdentifier.setLastModified(creationDate);
        orgDisambiguatedExternalIdentifierDao.persist(externalIdentifier);
        return true;
    }
    
    /**
     * STATISTICS
     * */
    /**
     * Get the total number of orgs processed
     * */
    private long getTotal() {
        return updatedOrgs + addedDisambiguatedOrgs + addedExternalIdentifiers;
    }

}
