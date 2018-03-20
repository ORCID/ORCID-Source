package org.orcid.core.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.constants.OrganizationStatus;
import org.orcid.persistence.dao.OrgDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/** The file can be found here: https://leidata.gleif.org/api/v1/concatenated-files/lei2/20180227/zip
 * 1) wget file, 
 * 2) unzip file
 * 3) import:  mvn exec:java -Dexec.mainClass=org.orcid.core.cli.LoadLEIData -Dorg.orcid.config.file=file:///Users/tom/git/ORCID-Source/orcid-persistence/src/main/resources/staging-persistence.properties -Dexec.args="-f path/to/extracted_file.xml"
 * 4) reindex: mvn exec:java -Dexec.mainClass=org.orcid.core.cli.ProcessDisambiguatedOrgsForIndexing -Dorg.orcid.config.file=file:///path/to/ORCID-Source/orcid-persistence/src/main/resources/staging-persistence.properties
 * 
 * @author tom
 *
 */
public class LoadLEIData {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadLEIData.class);
    private static final String LEI_SOURCE_TYPE = "LEI";
    private static final QName QNAME_TYPE = new QName("type");
    private static final String TRANSLITERATED_LEGAL_NAME_TYPE = "PREFERRED_ASCII_TRANSLITERATED_LEGAL_NAME";

    // Params
    @Option(name = "-f", usage = "Path to RDF file containing LEI data dump to load into DB")
    private File fileToLoad;

    private OrgDisambiguatedDao orgDisambiguatedDao;
    private OrgDao orgDao;

    // Statistics
    private long updatedDisambiguatedOrgs = 0;
    private long addedDisambiguatedOrgs = 0;
    private long updatedOrgs = 0;
    private long addedOrgs = 0;
    private long count = 0;

    /**
     * Grab the file, check it exists, stream the file, extract the entities,
     * update the DB.
     * 
     * @param args
     */
    public static void main(String[] args) {
        LoadLEIData element = new LoadLEIData();
        CmdLineParser parser = new CmdLineParser(element);
        try {
            parser.parseArgument(args);
            element.validateArgs(parser);
            element.init();
            element.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        } catch (XMLStreamException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(2);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(3);
        }
        System.exit(0);
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (fileToLoad == null) {
            throw new CmdLineException(parser, "-f parameter must be specificed");
        }
    }

    /**
     * Setup our spring resources
     * 
     */
    @SuppressWarnings({ "resource" })
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orgDao = (OrgDao) context.getBean("orgDao");
        orgDisambiguatedDao = (OrgDisambiguatedDao) context.getBean("orgDisambiguatedDao");
    }

    /**
     * Stream the XML using Stax. Create orgs and process them as we go
     * 
     * @throws XMLStreamException
     * @throws FileNotFoundException
     */
    public void execute() throws XMLStreamException, FileNotFoundException {
        Instant start = Instant.now();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader r = null;
        try {
            r = factory.createXMLEventReader(new FileReader(fileToLoad));
            LEIOrganization org = null;
            while (r.hasNext()) {
                XMLEvent event = r.nextEvent();
                if (event.isStartElement()) {
                    String startElement = event.asStartElement().getName().getLocalPart();
                    if (startElement.equals("LEIRecord")) {
                        org = new LEIOrganization();
                    } else if (startElement.equals("LEI")) {
                        org.id = r.getElementText();
                    } else if (startElement.equals("LegalName")) {
                        org.name = r.getElementText();
                    } else if (startElement.equals("OtherEntityName") || startElement.equals("TransliteratedOtherEntityName")) {
                        Attribute type = event.asStartElement().getAttributeByName(QNAME_TYPE);
                        if (type != null && type.getValue().equals(TRANSLITERATED_LEGAL_NAME_TYPE))
                            org.transliteratedLegalNames.add(r.getElementText());
                        else
                            org.otherNames.add(r.getElementText());
                    } else if (startElement.equals("HeadquartersAddress")) {
                        org.hqAddres = extractAddress(r);
                    } else if (startElement.equals("LegalAddress") || startElement.equals("OtherAddress") || startElement.equals("TransliteratedOtherAddress")) {
                        org.otherAddresses.add(extractAddress(r));
                    } else if (startElement.equals("EntityStatus")) {
                        org.status = r.getElementText(); // ACTIVE || INACTIVE
                    } else if (startElement.equals("SuccessorLEI")) {
                        org.successorLEI = r.getElementText(); // can be active
                                                               // and have
                                                               // successor
                    } else if (startElement.equals("OtherLegalForm")) {
                        org.type = r.getElementText(); // see
                                                       // https://www.gleif.org/en/about-lei/iso-20275-entity-legal-forms-code-list
                                                       // for vocab
                        if ("OTHER - please specify".equals(org.type))
                            org.type = "OTHER";
                    } // we also have general purpose AssociatedLEI, for active
                      // and inactive records.
                } else if (event.isEndElement()) {
                    String endElement = event.asEndElement().getName().getLocalPart();
                    if (endElement.equals("LEIRecord")) {
                        count++;
                        LOGGER.info("Processing [" + count + "] LEI:" + org.id);
                        processOrg(org);
                    }
                }
                // we also have //LastUpdateDate
            }
        } finally {
            if (r != null)
                r.close();
        }
        LOGGER.info("Updated Disambiguated orgs: {}", updatedDisambiguatedOrgs);
        LOGGER.info("New Disambiguated orgs: {}", addedDisambiguatedOrgs);
        LOGGER.info("Updated orgs: {}", updatedOrgs);
        LOGGER.info("New orgs: {}", addedOrgs);
        LOGGER.info("Time taken to process the data: {}", Duration.between(start, Instant.now()).toString());
    }

    /**
     * Process Address subtrees from the Stax Stream.
     * 
     * @param r
     * @return
     * @throws XMLStreamException
     */
    public LEIAddress extractAddress(XMLEventReader r) throws XMLStreamException {
        LEIAddress address = new LEIAddress();
        while (r.hasNext()) {
            XMLEvent event = r.nextEvent();
            if (event.isStartElement()) {
                String startElement = event.asStartElement().getName().getLocalPart();
                if (startElement.equals("City")) {
                    address.city = r.getElementText();
                } else if (startElement.equals("Region")) {
                    address.region = r.getElementText();
                } else if (startElement.equals("Country")) {
                    String c = r.getElementText();
                    address.country = StringUtils.isBlank(c) ? null : Iso3166Country.fromValue(c);
                }
            } else if (event.isEndElement()) {
                String endElement = event.asEndElement().getName().getLocalPart();
                if (endElement.equals("HeadquartersAddress") || endElement.equals("LegalAddress") || endElement.equals("OtherAddress")
                        || endElement.equals("TransliteratedOtherAddress")) {
                    return address;
                }
            }
        }
        return null;// impossible
    }

    /**
     * Add/Update Orgs in the DB
     * 
     * @param org
     */
    public void processOrg(LEIOrganization org) {
        Date now = new Date();
        OrgDisambiguatedEntity existingDO = orgDisambiguatedDao.findBySourceIdAndSourceType(org.id, LEI_SOURCE_TYPE);
        if (existingDO != null) {
            // update
            if (org.differentFrom(existingDO)) {
                existingDO.setCity(org.hqAddres.city);
                existingDO.setCountry(org.hqAddres.country);
                existingDO.setName(org.name);
                existingDO.setOrgType(org.type);
                existingDO.setRegion(org.hqAddres.region);
                existingDO.setUrl("https://www.gleif.org/lei/" + org.id);
                existingDO.setLastModified(now);
                existingDO.setIndexingStatus(IndexingStatus.PENDING);
                // Is it replaced?
                if (!PojoUtil.isEmpty(org.successorLEI)) {
                    existingDO.setSourceParentId(org.successorLEI);
                    existingDO.setStatus(OrganizationStatus.DEPRECATED.name());
                } // or is is simply gone
                else if ("INACTIVE".equals(org.status)) {
                    existingDO.setStatus(OrganizationStatus.OBSOLETE.name());
                }
                LOGGER.info("Merging LEI:" + org.id);
                existingDO = orgDisambiguatedDao.merge(existingDO);
                updatedDisambiguatedOrgs++;
            }
        } else {
            // create
            Iso3166Country country = org.hqAddres.country;
            OrgDisambiguatedEntity orgDisambiguatedEntity = new OrgDisambiguatedEntity();
            orgDisambiguatedEntity.setName(org.name);
            orgDisambiguatedEntity.setCountry(country);
            orgDisambiguatedEntity.setCity(org.hqAddres.city);
            orgDisambiguatedEntity.setRegion(org.hqAddres.region);
            orgDisambiguatedEntity.setOrgType(org.type);
            orgDisambiguatedEntity.setSourceId(org.id);
            orgDisambiguatedEntity.setSourceUrl("https://www.gleif.org/lei/" + org.id);
            // Is it replaced?
            if (!PojoUtil.isEmpty(org.successorLEI)) {
                orgDisambiguatedEntity.setSourceParentId(org.successorLEI);
                orgDisambiguatedEntity.setStatus(OrganizationStatus.DEPRECATED.name());
            } // or is is simply gone
            else if ("INACTIVE".equals(org.status)) {
                orgDisambiguatedEntity.setStatus(OrganizationStatus.OBSOLETE.name());
            }
            orgDisambiguatedEntity.setSourceType(LEI_SOURCE_TYPE);
            LOGGER.info("Creating LEI:" + org.id);
            orgDisambiguatedDao.persist(orgDisambiguatedEntity);
            existingDO = orgDisambiguatedEntity;
            addedDisambiguatedOrgs++;
        }

        // Other names
        for (LEIOrganization otherOrg : org.toOtherOrgs()) {
            OrgEntity existingOrg = orgDao.findByAddressAndDisambiguatedOrg(otherOrg.name, otherOrg.hqAddres.city, otherOrg.hqAddres.region, otherOrg.hqAddres.country,
                    existingDO);
            if (existingOrg != null) {
                // do nothing (nothing to update!)
            } else {
                OrgEntity newOrg = new OrgEntity();
                newOrg.setDateCreated(now);
                newOrg.setLastModified(now);
                newOrg.setCity(otherOrg.hqAddres.city);
                newOrg.setCountry(otherOrg.hqAddres.country);
                newOrg.setRegion(otherOrg.hqAddres.region);
                newOrg.setName(otherOrg.name);
                newOrg.setOrgDisambiguated(existingDO);
                LOGGER.info("Creating org LEI:" + org.id);
                orgDao.persist(newOrg);
                addedOrgs++;
            }
        }
    }

    // ========================================================================
    // models
    class LEIOrganization {
        public String type;
        public String successorLEI;
        String id, status, name;
        Set<String> otherNames = new HashSet<String>();
        Set<String> transliteratedLegalNames = new HashSet<String>();
        LEIAddress hqAddres;
        LEIAddress legalAddres;
        Set<LEIAddress> otherAddresses = new HashSet<LEIAddress>();

        public boolean differentFrom(OrgDisambiguatedEntity other) {
            return (!StringUtils.equals(this.name, other.getName()) || !StringUtils.equals(this.type, other.getOrgType())
                    || !StringUtils.equals(this.hqAddres.city, other.getCity()) || !StringUtils.equals(this.hqAddres.region, other.getRegion())
                    || !StringUtils.equals(this.successorLEI, other.getSourceParentId())
                    || (other.getCountry() != null && !other.getCountry().equals(this.hqAddres.country))
                    || (OrganizationStatus.OBSOLETE.name().equals(other.getStatus()) && this.status.equals("ACTIVE"))
                    || (!OrganizationStatus.OBSOLETE.name().equals(other.getStatus()) && this.status.equals("INACTIVE")));
        }

        /** Get Orgs with translated legal names */
        private Set<LEIOrganization> toOtherOrgs() {
            Set<LEIOrganization> orgs = new HashSet<LEIOrganization>();
            for (String tName : this.transliteratedLegalNames) {
                if (!this.name.equals(tName)) {
                    // add org
                    LEIOrganization tOrg = new LEIOrganization();
                    tOrg.name = tName;
                    tOrg.hqAddres = this.hqAddres;
                    tOrg.id = this.id;
                    tOrg.status = this.status;
                    tOrg.successorLEI = this.successorLEI;
                    tOrg.type = this.type;
                    orgs.add(tOrg);
                }
            }
            return orgs;
        }

        @Override
        public String toString() {
            return "LEIOrganization [type=" + type + ", successorLEI=" + successorLEI + ", id=" + id + ", status=" + status + ", name=" + name + ", otherNames="
                    + otherNames + ", transliteratedLegalNames=" + transliteratedLegalNames + ", hqAddres=" + hqAddres + ", legalAddres=" + legalAddres
                    + ", otherAddresses=" + otherAddresses + "]";
        }

    }

    // ========================================================================
    class LEIAddress {
        String region, city;
        Iso3166Country country;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((city == null) ? 0 : city.hashCode());
            result = prime * result + ((country == null) ? 0 : country.hashCode());
            result = prime * result + ((region == null) ? 0 : region.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            LEIAddress other = (LEIAddress) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (city == null) {
                if (other.city != null)
                    return false;
            } else if (!city.equals(other.city))
                return false;
            if (country == null) {
                if (other.country != null)
                    return false;
            } else if (!country.equals(other.country))
                return false;
            if (region == null) {
                if (other.region != null)
                    return false;
            } else if (!region.equals(other.region))
                return false;
            return true;
        }

        private LoadLEIData getOuterType() {
            return LoadLEIData.this;
        }

        @Override
        public String toString() {
            return "LEIAddress [country=" + country + ", region=" + region + ", city=" + city + "]";
        }
    }

    public void setFileToLoad(File testFile) {
        this.fileToLoad = testFile;
    }

}
