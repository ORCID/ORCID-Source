package org.orcid.core.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.adapter.JpaJaxbWorkAdapter;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.SourceName;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

public class LoadWorksToDB {

    private Works works;

    private List<WorkEntity> worksList = new ArrayList<>();
    
    private Map<String, String> sourcesMap = new HashMap<>();   

    private ClientDetailsDao clientDetailsDao;

    private WorkDao workDao;
    
    private JpaJaxbWorkAdapter workAdapter;

    @Option(name = "-f", usage = "Works file")
    private File worksFile;

    @Option(name = "-o", usage = "ORCID iD for which works are to be generated")
    private String orcid;

    @Option(name = "-i", usage = "Orcidinal ORCID iD from which works from other system were taken (used to identify self source)")
    private String originalOrcid;

    private ClassPathXmlApplicationContext applicationContext;

    public static void main(String[] args) {
        LoadWorksToDB loadWorksToDB = new LoadWorksToDB();
        CmdLineParser parser = new CmdLineParser(loadWorksToDB);
        try {
            parser.parseArgument(args);
            loadWorksToDB.validateArgs(parser);
            loadWorksToDB.init();
            loadWorksToDB.readWorks();
            loadWorksToDB.createWorkList();
            loadWorksToDB.writeWorks();
            loadWorksToDB.shutdown();
        } catch (CmdLineException e) {
            parser.printUsage(System.err);
            System.exit(1);
        }
        System.exit(0);
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (orcid == null || orcid.isEmpty()) {
            throw new CmdLineException(parser, "Invalid orcid");
        }

        if (originalOrcid == null || originalOrcid.isEmpty()) {
            throw new CmdLineException(parser, "Invalid original orcid");
        }

        if (worksFile == null || !worksFile.exists()) {
            throw new CmdLineException(parser, "Invalid works file");
        }
    }

    private void init() {
        applicationContext = new ClassPathXmlApplicationContext("orcid-persistence-context.xml", "orcid-core-context.xml");
        clientDetailsDao = (ClientDetailsDao) applicationContext.getBean("clientDetailsDaoReadOnly");
        workDao = (WorkDao) applicationContext.getBean("workDao");
        workAdapter = (JpaJaxbWorkAdapter) applicationContext.getBean("jpaJaxbWorkAdapter"); 
    }

    private void readWorks() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(worksFile));
            StringBuilder fileContents = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                fileContents.append(line).append("\n");
                line = reader.readLine();
            }
            reader.close();
            ObjectMapper mapper = new ObjectMapper();
            JaxbAnnotationModule module = new JaxbAnnotationModule();
            mapper.registerModule(module);
            works = mapper.readValue(fileContents.toString(), Works.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createWorkList() {
        for (WorkGroup group : works.getWorkGroup()) {
            for (WorkSummary summary : group.getWorkSummary()) {
                Work work = getWorkFromSummary(summary);
                WorkEntity workEntity = workAdapter.toWorkEntity(work);
                workEntity.setOrcid(orcid);
                worksList.add(workEntity);
            }
        }
    }

    private Source getUpdatedSource(Source source) {
        String sourceId = source.retrieveSourcePath();
        String updatedSourceId = null;
        if (sourceId.equals(originalOrcid)) {
            updatedSourceId = orcid;
        } else {
            updatedSourceId = sourcesMap.get(sourceId);
            if (updatedSourceId == null) {
                updatedSourceId = getNewClientDetailsSource();
                sourcesMap.put(sourceId, updatedSourceId);
            }
        }
        Source updatedSource = new Source(updatedSourceId);
        updatedSource.setSourceName(new SourceName(updatedSourceId));
        return updatedSource;
    }

    private String getNewClientDetailsSource() {
        List<ClientDetailsEntity> clientDetailsList = clientDetailsDao.getAll();
        for (ClientDetailsEntity client : clientDetailsList) {
            if (!ClientType.PUBLIC_CLIENT.name().equals(client.getClientType()) && !sourcesMap.containsKey(client.getId())) {
                return client.getId();
            }
        }
        throw new RuntimeException("Greater number of different work sources than number of clients in local db!");
    }

    private Work getWorkFromSummary(WorkSummary summary) {
        Work work = new Work();
        work.setWorkTitle(summary.getTitle());
        work.setWorkType(summary.getType());
        work.setPublicationDate(summary.getPublicationDate());
        work.setWorkExternalIdentifiers(summary.getExternalIdentifiers());
        work.setVisibility(summary.getVisibility());
        work.setSource(getUpdatedSource(summary.getSource()));
        return work;
    }

    private void writeWorks() {
        for (WorkEntity workEntity : worksList) {
            workDao.persist(workEntity);
        }
    }
    
    private void shutdown() {
        applicationContext.close();
    }


}
