package org.orcid.core.cli;

import java.util.Date;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PopulateTestWorksForClientAndUser {

    static final int BATCH_SIZE = 400;

    private static final Logger LOGGER = LoggerFactory.getLogger(PopulateTestWorksForClientAndUser.class);

    @Option(name = "-c", usage = "Client ID of client source (optional)", required = false)
    private String clientDetailsId;
    
    @Option(name = "-o", usage = "ORCID iD", required = true)
    private String orcidId;
    
    @Option(name = "-n", usage = "Number of works", required = true)
    private int num;
    
    private WorkDao workDao;
    
    public static void main(String[] args) {
        PopulateTestWorksForClientAndUser populateWorkData = new PopulateTestWorksForClientAndUser();
        CmdLineParser parser = new CmdLineParser(populateWorkData);
        try {
            parser.parseArgument(args);
            populateWorkData.init();
            populateWorkData.execute();
        } catch (CmdLineException e) {
            LOGGER.error(e.getMessage(), e);
            parser.printUsage(System.err);
            System.exit(1);
        } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
            System.exit(2);
        }
        System.exit(0);
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        workDao = (WorkDao) context.getBean("workDao");
    }

    public void execute() {
        populateWorks();
    }

    private void populateWorks() {
        LOGGER.info("Creating works...");
        for (int i = 0; i < num; i++) {
            workDao.persist(getWork(i));
        }
        LOGGER.info("Created {} works", num);
    }

    private WorkEntity getWork(int i) {
        WorkEntity entity = new WorkEntity();
        entity.setTitle("test " + i);
        entity.setOrcid(orcidId);
        if (clientDetailsId != null) {
            entity.setClientSourceId(clientDetailsId);
        }
        return entity;
    }

}
