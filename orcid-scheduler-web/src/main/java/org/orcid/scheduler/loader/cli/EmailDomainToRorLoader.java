package org.orcid.scheduler.loader.cli;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orcid.core.common.manager.EmailDomainManager;
import org.orcid.core.common.manager.impl.EmailDomainManagerImpl.STATUS;
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
    private List<List<String>> csvData;
    private Map<String, DomainToRorMap> map = new HashMap<String, DomainToRorMap>();
    
    private int updatedEntries = 0;
    private int createdEntries = 0;
    private int invalidEntires = 0;
    
    public EmailDomainToRorLoader(String filePath) {
        this.filePath = filePath;
        init(filePath);        
    }
    
    public void execute() throws IOException {
        load(this.filePath);
        processCsvData();
        storeDomainToRorMap();
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
        csvMapper.enable(CsvParser.Feature.TRIM_SPACES);
        
        MappingIterator<List<String>> it = csvMapper.readerForListOf(String.class).readValues(fileReader);

        if (it != null) {
            csvData = new ArrayList<List<String>>();
            while(it.hasNext()) {
                List<String> r = it.next();
                // Hack to avoid adding empty lines if they are present, we need at least 2 columns, the domain and the ror id
                if(r.size() > 1)
                    csvData.add(r);
            }                        
        }
        fileReader.close();
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
                map.put(domain, dtrm);
            } else {
                DomainToRorMap dtrm = map.get(domain);
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
            LOG.debug("Processing domain {}", element.getDomain());
            // If the domain has only one entry with no parent, store that one
            if(element.getIdsWithNoParent().size() == 1) {
                STATUS s = emailDomainManager.createOrUpdateEmailDomain(element.getDomain(), element.getIdsWithNoParent().get(0));
                if(STATUS.CREATED.equals(s)) {
                    createdEntries++;
                } else if (STATUS.UPDATED.equals(s)) {
                    updatedEntries++;
                }
            } else if(element.getIdsWithNoParent().isEmpty() && element.getIdsWithParent().size() == 1) {
                // Else, if the domain doesn't have an org with no parents and only have one entry with parent, store that one
                STATUS s = emailDomainManager.createOrUpdateEmailDomain(element.getDomain(), element.getIdsWithParent().get(0));
                if(STATUS.CREATED.equals(s)) {
                    createdEntries++;
                } else if (STATUS.UPDATED.equals(s)) {
                    updatedEntries++;
                }
            } else {            
                // Else log a warning because there is no way to provide a suggestion
                LOG.warn("Domain {} couldnt be mapped, it have {} rows with parent and {} rows with no parent", element.getDomain(), element.getIdsWithParent().size(), element.getIdsWithNoParent().size());
                invalidEntires++;
            }
        }
        
        LOG.info("Created entries: {}, updated entries: {}, invalid entries {}", createdEntries, updatedEntries, invalidEntires);
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
            LOG.debug("Domain {} adding {} with parent flag", this.domain, rorId);
            idsWithParent.add(rorId);
        }
        
        public List<String> getIdsWithParent() {
            return this.idsWithParent;
        }
        
        public void addIdWithNoParent(String rorId) {
            LOG.debug("Domain {} adding {} with NO parent flag", this.domain, rorId);
            idsWithNoParent.add(rorId);
        }
        
        public List<String> getIdsWithNoParent() {
            return this.idsWithNoParent;
        }
    }
    
    public static void main(String[] args) throws IOException {
        String filePath = args[0]; 
        EmailDomainToRorLoader edl = new EmailDomainToRorLoader(filePath);
        edl.execute();
    }
}
