package org.orcid.scheduler.loader.cli;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.orcid.core.common.manager.EmailDomainManager;
import org.orcid.persistence.jpa.entities.EmailDomainEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;

public class EmailDomainLoader {
    
    private static final Logger LOG = LoggerFactory.getLogger(EmailDomainLoader.class);
    
    private String filePath;
    private EmailDomainManager emailDomainManager;
    List<List<String>> emailDomainData;

    List<String> invalidDomains = new ArrayList<String>();
    
    public EmailDomainLoader(String filePath) {
        this.filePath = filePath;
        init(filePath);        
    }
    
    public void execute() throws IOException {
        load(this.filePath);
        process();
    }
    
    private void init(String filePath) {
        Path path = Paths.get(filePath);
        if(!Files.exists(path)) {
            LOG.error("File does not exists: '{}'", filePath);
            System.exit(1);
        }
        
        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        emailDomainManager = (EmailDomainManager) context.getBean("emailDomainManager");
    }

    private void load(String filePath) throws IOException {
        LOG.info("Reading file {}", filePath);
        FileReader fileReader = new FileReader(filePath);       
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        MappingIterator<List<String>> it = csvMapper.readerForListOf(String.class).readValues(fileReader);

        if (it != null) {
            emailDomainData = new ArrayList<List<String>>();
            while(it.hasNext()) {
                List<String> r = it.next();
                emailDomainData.add(r);
            }                        
        }        
    }

    private void process() {
        int total = 0;
        int newEntities = 0;
        int updatedEntities = 0;
        LOG.info("Process started");
        if (emailDomainData != null) {
            for (List<String> row : emailDomainData) {
                String elementDomain = row.get(0);
                String elementCategory = row.get(1);
                List<EmailDomainEntity> ede = emailDomainManager.findByEmailDomain(elementDomain);
                EmailDomainEntity.DomainCategory category = EmailDomainEntity.DomainCategory.valueOf(elementCategory.toUpperCase());
                if(ede == null) {
                    try {
                        EmailDomainEntity newEde = emailDomainManager.createEmailDomain(elementDomain, category);
                        newEntities += 1;
                        LOG.info("New EmailDomainEntity created for domain {} with id {}", elementDomain, newEde.getId());
                    } catch(IllegalArgumentException iae) {
                        LOG.error("Invalid domain: {}", elementDomain);
                        invalidDomains.add(elementDomain);
                    }
                }
                total += 1;
            }
        }
        if(!invalidDomains.isEmpty()) {
            LOG.warn("List of invalid domains:");
            for(String invalidDomain : invalidDomains) {
                LOG.info(invalidDomain);
            }
        }
        LOG.info("Process done, total: {}, new entities: {}, updated entities: {}", total, newEntities, updatedEntities);
    }    
    
    public static void main(String[] args) throws IOException {
        String filePath = args[0]; 
        EmailDomainLoader edl = new EmailDomainLoader(filePath);
        edl.execute();
    }
}
