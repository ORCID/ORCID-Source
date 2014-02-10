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
        String doi, name, country, state, stateCode, type, subtype;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadFundRefData.class);
    private static final String FUNDREF_SOURCE_TYPE = "FUNDREF";
    private static String geonamesApiUrl;
    // Params
    @Option(name = "-f", usage = "Path to RDF file containing FundRef info to load into DB")
    private File fileToLoad;
    // Resources
    private GenericDao<OrgDisambiguatedExternalIdentifierEntity, Long> genericDao;
    private OrgDisambiguatedDao orgDisambiguatedDao;
    private OrgManager orgManager;
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
    private long addedOrgs = 0;
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
        genericDao = (GenericDao) context.getBean("orgDisambiguatedExternalIdentifierEntityDao");
        orgManager = (OrgManager) context.getBean("orgManager");
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
                // Now look an exact match into the disambiguated orgs
                OrgDisambiguatedEntity existingDisambiguatedOrg = getMatchingDisambiguatedOrg(rdfOrganization);
                // If exists add an external identifier
                if (existingDisambiguatedOrg != null) {
                    LOGGER.info("Organization {} - {} already exists on database with id {}",
                            new String[] { rdfOrganization.doi, rdfOrganization.name, String.valueOf(existingDisambiguatedOrg.getId()) });
                    if (!existsExternalIdentifier(existingDisambiguatedOrg, rdfOrganization.doi)) {
                        createExternalIdentifier(existingDisambiguatedOrg, rdfOrganization.doi);
                    }
                } else {
                    // Find an exact match in the list of orgs
                    OrgEntity existingOrg = getMatchingOrg(rdfOrganization);
                    Iso3166Country country = StringUtils.isNotBlank(rdfOrganization.country) ? Iso3166Country.fromValue(rdfOrganization.country) : null;
                    if (existingOrg != null) {
                        // If the disambiguated org exists, just create an
                        // external identifier for it
                        if (existingOrg.getOrgDisambiguated() != null) {
                            LOGGER.info("Creating external identifier for {} - {}", new String[] { rdfOrganization.doi, rdfOrganization.name });
                            createExternalIdentifier(existingOrg.getOrgDisambiguated(), rdfOrganization.doi);
                            addedExternalIdentifiers++;
                        } else {
                            // Else create the disambiguated org and assign it
                            // to the existing org
                            LOGGER.info("Creating disambiguated org for {} - {}", new String[] { rdfOrganization.doi, rdfOrganization.name });
                            OrgDisambiguatedEntity disambiguatedOrg = createDisambiguatedOrg(rdfOrganization);
                            addedDisambiguatedOrgs++;
                            LOGGER.info("Assiging the new disambiguated org to {} - {}", new String[] { String.valueOf(existingOrg.getId()), existingOrg.getName() });
                            createOrUpdateOrg(existingOrg.getName(), existingOrg.getCity(), existingOrg.getCountry(), existingOrg.getRegion(), disambiguatedOrg.getId());
                        }
                    } else {
                        LOGGER.info("A new disambiguated organization and organization will be created for: {} - {}", new String[] { rdfOrganization.doi,
                                rdfOrganization.name });
                        // Create disambiguated organization
                        OrgDisambiguatedEntity disambiguatedOrg = createDisambiguatedOrg(rdfOrganization);
                        addedDisambiguatedOrgs++;
                        // Create organization
                        createOrUpdateOrg(rdfOrganization.name, null, country, rdfOrganization.state, disambiguatedOrg.getId());
                        addedOrgs++;
                    }
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
            LOGGER.info("Number new Disambiguated Orgs={}, new Orgs={}, new External Identifiers={}", new Object[] { addedDisambiguatedOrgs, addedOrgs,
                    addedExternalIdentifiers, getTotal() });
        }

    }

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
                // Get state code for US states
                if (countryCode != null && countryCode.equals("US")) {
                    stateCode = fetchFromGeoNames(stateGeoNameCode, "adminCode1");
                }
            }

            // Get type
            String orgType = (String) xPath.compile(orgTypeExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.STRING);
            // Get subType
            String orgSubType = (String) xPath.compile(orgSubTypeExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.STRING);

            // Fill the organization object
            organization.type = orgType;
            organization.doi = itemDoi;
            organization.name = orgName;
            organization.country = countryCode;
            organization.state = stateName;
            organization.stateCode = stateCode;
            organization.subtype = orgSubType;
        } catch (XPathExpressionException xpe) {
            LOGGER.error("XPathExpressionException {}", xpe.getMessage());
        }

        return organization;
    }

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

    /**
     * Get the disambiguated org that have the same name, the same country, and,
     * for US, the same state
     * */
    private OrgDisambiguatedEntity getMatchingDisambiguatedOrg(RDFOrganization rdfOrg) {
        List<OrgDisambiguatedEntity> orgs = getExistingDisambiguatedOrgs(rdfOrg.name);
        if (orgs == null || orgs.size() == 0)
            return null;
        boolean compareRegion = (rdfOrg.country != null && rdfOrg.country.equals("US")) ? true : false;
        for (OrgDisambiguatedEntity disambiguatedOrg : orgs) {
            if (attributesMatches(disambiguatedOrg, rdfOrg, compareRegion)) {
                return disambiguatedOrg;
            }
        }
        return null;
    }

    /**
     * Get the org that have the same name, the same country, and, for US, the
     * same state
     * */
    private OrgEntity getMatchingOrg(RDFOrganization rdfOrg) {
        List<OrgEntity> orgs = getExistingOrgs(rdfOrg.name);
        if (orgs == null || orgs.size() == 0)
            return null;
        boolean compareRegion = (rdfOrg.country != null && rdfOrg.country.equals("US")) ? true : false;
        for (OrgEntity org : orgs) {
            if (attributesMatches(org, rdfOrg, compareRegion)) {
                return org;
            }
        }
        return null;
    }

    /**
     * Get the disambiguated orgs that have the given orgName
     * */
    private List<OrgDisambiguatedEntity> getExistingDisambiguatedOrgs(String orgName) {
        List<OrgDisambiguatedEntity> orgs = orgDisambiguatedDao.findByName(orgName);
        if (orgs == null || orgs.size() == 0)
            return null;
        return orgs;
    }

    /**
     * Get the orgs that have the given orgName
     * */
    private List<OrgEntity> getExistingOrgs(String orgName) {
        List<OrgEntity> orgs = orgManager.getOrgsByName(orgName);
        if (orgs == null || orgs.size() == 0)
            return null;
        return orgs;
    }

    /**
     * Checks if the country (and region for US) matches in the given
     * disambiguated organization and the organization that comes from the RDF
     * */
    private boolean attributesMatches(OrgDisambiguatedEntity org, RDFOrganization rdfOrg, boolean compareRegion) {
        if (org.getCountry() == null) {
            if (rdfOrg.country != null)
                return false;
        } else {
            if (rdfOrg.country == null)
                return false;
            if (!org.getCountry().equals(Iso3166Country.fromValue(rdfOrg.country)))
                return false;
        }

        // Compare against the region only for US organizations
        if (compareRegion) {
            if (org.getRegion() == null) {
                if (rdfOrg.stateCode != null)
                    return false;
            } else {
                if (!org.getRegion().equals(rdfOrg.stateCode))
                    return false;
            }
        }
        return true;
    }

    /**
     * Checks if the country (and region for US) matches in the given
     * organization and the organization that comes from the RDF
     * */
    private boolean attributesMatches(OrgEntity org, RDFOrganization rdfOrg, boolean compareRegion) {
        if (org.getCountry() == null) {
            if (rdfOrg.country != null)
                return false;
        } else {
            if (rdfOrg.country == null)
                return false;
            if (!org.getCountry().equals(Iso3166Country.fromValue(rdfOrg.country)))
                return false;
        }

        // Compare against the region only for US organizations
        if (compareRegion) {
            if (org.getRegion() == null) {
                if (rdfOrg.stateCode != null)
                    return false;
            } else {
                if (!org.getRegion().equals(rdfOrg.stateCode))
                    return false;
            }
        }
        return true;
    }

    /**
     * Checks if the disambiguated org already contains an external identifier
     * of type FUNDREF and associated with the given id
     * */
    private boolean existsExternalIdentifier(OrgDisambiguatedEntity disambiguatedOrg, String id) {
        Set<OrgDisambiguatedExternalIdentifierEntity> extIds = disambiguatedOrg.getExternalIdentifiers();
        if (extIds == null || extIds.size() == 0)
            return false;
        for (OrgDisambiguatedExternalIdentifierEntity extId : extIds) {
            if (extId.getIdentifierType().equals(FUNDREF_SOURCE_TYPE)) {
                if (extId.getIdentifier() != null && extId.getIdentifier().equals(id))
                    return true;
            }
        }
        return false;
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
        genericDao.persist(externalIdentifier);
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
        if (country != null && country.equals(Iso3166Country.US))
            orgDisambiguatedEntity.setRegion(organization.stateCode);
        else
            orgDisambiguatedEntity.setRegion(organization.state);
        orgDisambiguatedEntity.setCountry(country);
        orgDisambiguatedEntity.setOrgType(orgType);
        orgDisambiguatedEntity.setSourceId(organization.doi);
        orgDisambiguatedEntity.setSourceUrl(organization.doi);
        orgDisambiguatedEntity.setSourceType(FUNDREF_SOURCE_TYPE);
        orgDisambiguatedDao.persist(orgDisambiguatedEntity);
        return orgDisambiguatedEntity;
    }

    /**
     * Creates or updates an Org in the org table
     * */
    private void createOrUpdateOrg(String name, String city, Iso3166Country country, String state, Long orgDisambiguatedId) {
        LOGGER.info("Adding or updating organization {} to disambiguated org {}", name, orgDisambiguatedId);
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setName(name);
        orgEntity.setRegion(state);
        orgEntity.setCity(city);
        orgEntity.setCountry(country);
        orgManager.createUpdate(orgEntity, orgDisambiguatedId);
    }

    private long getTotal() {
        return addedOrgs + addedDisambiguatedOrgs + addedExternalIdentifiers;
    }

}
