package org.orcid.scheduler.loader.source.cli;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;

import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.core.utils.FunderIdentifierType;
import org.orcid.persistence.constants.OrganizationStatus;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.orcid.pojo.OrgDisambiguated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RorToRinggoldFundrefCSVMapping {
    private static final Logger LOGGER = LoggerFactory.getLogger(RorToRinggoldFundrefCSVMapping.class);
    private OrgDisambiguatedDao orgDisambiguatedDao;
    private OrgDisambiguatedManager orgDisambiguatedManager;

    private static final String ROR_TYPE = "ROR";

    private static final int INDEXING_CHUNK_SIZE = 200000;

    @Value("${org.orcid.core.orgs.ringgoldtororcsv:/tmp/ror_ringold.csv}")
    private String ringoldCsvFilePath;

    @Value("${org.orcid.core.orgs.fundreftororcsv:/tmp/ror_fundref.csv}")
    private String fundrefCsvFilePath;

    /**
     * Setup our spring resources
     * 
     */
    @SuppressWarnings({ "resource" })
    @PostConstruct
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-scheduler-context.xml");
        orgDisambiguatedDao = (OrgDisambiguatedDao) context.getBean("orgDisambiguatedDao");
        orgDisambiguatedManager = (OrgDisambiguatedManager) context.getBean("orgDisambiguatedManager");
    }

    public static void main(String[] args) {
        RorToRinggoldFundrefCSVMapping mappingData = new RorToRinggoldFundrefCSVMapping();
        // TODO Auto-generated method stub
        try {

            mappingData.init();
            mappingData.getCSVMapping();

        } catch (Exception e) {
            LOGGER.error("Exception when generating  csv mapping Ringgolds to Rors", e);
            System.err.println(e.getMessage());
        } finally {
            System.exit(0);
        }

    }

    public void getCSVMapping() {
        LOGGER.info("About to start ror csv mapping");
        List<OrgDisambiguatedEntity> entities = null;
        HashMap<String, String> ringgoldMap = new HashMap<String, String>();
        ringgoldMap.put("ROR", "RINGGOLD");
        HashMap<String, String> fundrefMap = new HashMap<String, String>();
        fundrefMap.put("ROR", "FUNDREF");
        HashMap<String, String> isniMap = new HashMap<String, String>();
        isniMap.put("ROR", "ISNI");
        int startIndex = 0;
        do {
            entities = orgDisambiguatedDao.findBySourceType(ROR_TYPE, startIndex, INDEXING_CHUNK_SIZE);
            LOGGER.info("Found chunk of {} disambiguated orgs for CSV mapping", entities.size());
            for (OrgDisambiguatedEntity entity : entities) {
                LOGGER.info("ROR " + entity.getSourceId() + " and ID:  " + entity.getId());
                for (OrgDisambiguatedExternalIdentifierEntity externalIdentifier : entity.getExternalIdentifiers()) {
                    if (externalIdentifier.getIdentifierType().equals(FunderIdentifierType.ISNI.value())) {
                        if (!isniMap.containsKey(entity.getSourceId())) {
                            List<OrgDisambiguated> orgsFromExternalIdentifier = orgDisambiguatedManager
                                    .findOrgDisambiguatedIdsForSameExternalIdentifier(externalIdentifier.getIdentifier(), FunderIdentifierType.ISNI.value());
                            if (orgsFromExternalIdentifier != null) {
                                orgsFromExternalIdentifier.stream().forEach((o -> {
                                    if (o.getSourceType().equals(OrgDisambiguatedSourceType.RINGGOLD.name())) {
                                        if (!ringgoldMap.containsKey(entity.getSourceId())) {
                                            ringgoldMap.put(entity.getSourceId(), o.getSourceId());
                                        }
                                    } else if (o.getSourceType().equals(OrgDisambiguatedSourceType.FUNDREF.name())) {
                                        if (!fundrefMap.containsKey(entity.getSourceId())) {
                                            fundrefMap.put(entity.getSourceId(), o.getSourceId());
                                        }
                                    }

                                }));
                            }
                            isniMap.put(entity.getSourceId(), externalIdentifier.getIdentifier());
                        }
                    }
                    if (StringUtils.equals(externalIdentifier.getIdentifierType(), OrgDisambiguatedSourceType.FUNDREF.name())) {
                        fundrefMap.put(entity.getSourceId(), externalIdentifier.getIdentifier());
                    } else if (StringUtils.equals(externalIdentifier.getIdentifierType(), OrgDisambiguatedSourceType.RINGGOLD.name())) {
                        ringgoldMap.put(entity.getSourceId(), externalIdentifier.getIdentifier());
                    }
                }
            }

            startIndex = startIndex + INDEXING_CHUNK_SIZE;
        } while (!entities.isEmpty());
        LOGGER.info("Ror to ringgold: " + ringgoldMap.size());
        generateCsv(ringgoldMap, ringoldCsvFilePath);
        LOGGER.info("Ror to fundref: " + fundrefMap.size());
        generateCsv(fundrefMap, fundrefCsvFilePath);

    }

    public void generateCsv(HashMap<String, String> csvMap, String filePath) {
        String eol = System.getProperty("line.separator");
        try (Writer writer = new FileWriter(filePath)) {
            for (HashMap.Entry<String, String> entry : csvMap.entrySet()) {
                writer.append(entry.getKey()).append(',').append(entry.getValue()).append(eol);
            }
        } catch (IOException ex) {
            LOGGER.error("Cannot write the mapping to the csv " + filePath, ex);
        }

    }

}
