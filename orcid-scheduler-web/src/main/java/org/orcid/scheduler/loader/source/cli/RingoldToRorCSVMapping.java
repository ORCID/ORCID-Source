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

public class RingoldToRorCSVMapping {
    private static final Logger LOGGER = LoggerFactory.getLogger(RingoldToRorCSVMapping.class);
    private OrgDisambiguatedDao orgDisambiguatedDao;
    private OrgDisambiguatedManager orgDisambiguatedManager;

    private static final String RINGGOLD_TYPE = "RINGGOLD";

    private static final int INDEXING_CHUNK_SIZE = 10000;

    @Value("${org.orcid.core.orgs.ringgoldtororcsv:/tmp/ringgoldtoror.csv}")
    private String csvFilePath;

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
        RingoldToRorCSVMapping mappingData = new RingoldToRorCSVMapping();
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
        LOGGER.info("About to start ringgold to ror csv mapping");
        List<OrgDisambiguatedEntity> entities = null;
        HashMap<String, String> ringgoldMap = new HashMap<String, String>();
        ringgoldMap.put("Ringgold", "ROR");
        int startIndex = 0;
        do {
            entities = orgDisambiguatedDao.findBySourceType(RINGGOLD_TYPE, startIndex, INDEXING_CHUNK_SIZE);
            LOGGER.info("Found chunk of {} disambiguated orgs for indexing", entities.size());
            for (OrgDisambiguatedEntity entity : entities) {
                if (StringUtils.equals(entity.getStatus(), OrganizationStatus.PART_OF_GROUP.name())) {
                    for (OrgDisambiguatedExternalIdentifierEntity externalIdentifier : entity.getExternalIdentifiers()) {
                        if (StringUtils.equals(externalIdentifier.getIdentifierType(), FunderIdentifierType.ISNI.value())) {
                            List<OrgDisambiguated> orgsFromExternalIdentifier = orgDisambiguatedManager
                                    .findOrgDisambiguatedIdsForSameExternalIdentifier(externalIdentifier.getIdentifier(), FunderIdentifierType.ISNI.value());
                            if (orgsFromExternalIdentifier != null) {
                                orgsFromExternalIdentifier.stream().forEach((o -> {

                                    if (o.getSourceType().equals(OrgDisambiguatedSourceType.ROR.name())) {
                                        ringgoldMap.put(entity.getSourceId(), o.getSourceId());
                                    }

                                }));
                            }
                        } else if(StringUtils.equals(externalIdentifier.getIdentifierType(), OrgDisambiguatedSourceType.ROR.name())) {
                            ringgoldMap.put(entity.getSourceId(),externalIdentifier.getIdentifier());
                        }
                    }
                    
                  //TBD check if there is any ROR that has the ringgold as external identifier??
                }         
            }
            startIndex = startIndex + INDEXING_CHUNK_SIZE;
        } while (!entities.isEmpty());
        
        generateCsv(ringgoldMap);

    }

    public void generateCsv(HashMap<String, String> ringgoldMap) {
        String eol = System.getProperty("line.separator");
        try (Writer writer = new FileWriter("somefile.csv")) {
          for (HashMap.Entry<String, String> entry : ringgoldMap.entrySet()) {
            writer.append(entry.getKey())
                  .append(',')
                  .append(entry.getValue())
                  .append(eol);
          }
        } catch (IOException ex) {
          LOGGER.error("Cannot write the ringgold to ror csv", csvFilePath);
        }
    }

}
