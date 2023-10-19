package org.orcid.scheduler.loader.cli;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orcid.core.common.manager.EmailDomainManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;

public class EmailDomainToRorLoader {

    private static final Logger LOG = LoggerFactory.getLogger(EmailDomainToRorLoader.class);
    
    private String filePath;
    private EmailDomainManager emailDomainManager;
    List<List<String>> csvData;

    List<String> invalidDomains = new ArrayList<String>();  
    
    Map<String, DomainToRorMap> map = new HashMap<String, DomainToRorMap>();
    
    public EmailDomainToRorLoader(String filePath) {
        this.filePath = filePath;
        init(filePath);        
    }
    
    public void execute() throws IOException {
        load(this.filePath);
        processCsvData();
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
            csvData = new ArrayList<List<String>>();
            while(it.hasNext()) {
                List<String> r = it.next();
                csvData.add(r);
            }                        
        }        
    }
    
    private void processCsvData() {
        for (List<String> row : csvData) {
            String domain = row.get(0);
            String rorId = row.get(1);
            boolean hasParent = false;
            try {
                String hasParentField = row.get(2);
                hasParent = hasParentField == null ? false : Boolean.valueOf(hasParentField);
            } catch(IndexOutOfBoundsException eoob) {
                // Leave the hasParent as false
            }
            
            if(!map.containsKey(domain)) {
                DomainToRorMap dtrm = new DomainToRorMap();
                dtrm.setDomain(domain);
                if(hasParent) {
                    dtrm.addIdWithParent(rorId);
                } else {
                    dtrm.addIdWithNoParent(rorId);
                }
                map.put(rorId, dtrm);
            } else {
                DomainToRorMap dtrm = map.get(rorId);
                if(hasParent) {
                    dtrm.addIdWithParent(rorId);
                } else {
                    dtrm.addIdWithNoParent(rorId);
                }
            }
        }
    }
    
    private void storeDomainToRorMap() {
        for(DomainToRorMap element : map.values()) {
            // If the domain has only one entry with no parent, store that one
            if(element.getIdsWithNoParent().size() == 1) {
                
            } else if(element.getIdsWithParent().size() == 1) {
                // Else, if the domain has only one entry with parent, store that one
            } else {            
                // Else log a warning because there is no way to provide a suggestion
            }
        }
    }
    
    private class DomainToRorMap {
        private String domain;
        private List<String> idsWithParent = new ArrayList<String>();
        private List<String> idsWithNoParent = new ArrayList<String>();
        
        public void setDomain(String domain) {
            this.domain = domain;
        }
        
        public String getDomain() {
            return this.domain;
        }
        
        public void addIdWithParent(String rorId) {
            idsWithParent.add(rorId);
        }
        
        public List<String> getIdsWithParent() {
            return this.idsWithParent;
        }
        
        public void addIdWithNoParent(String rorId) {
            idsWithNoParent.add(rorId);
        }
        
        public List<String> getIdsWithNoParent() {
            return this.idsWithNoParent;
        }
    }
}
