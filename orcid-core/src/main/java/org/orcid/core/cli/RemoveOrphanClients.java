package org.orcid.core.cli;

import java.io.IOException;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class RemoveOrphanClients {

    private static Logger LOG = LoggerFactory.getLogger(RemoveOrphanClients.class);

    private ClientDetailsDao clientDetailsDao;

    @Option(name = "-n", usage = "do a dry run")
    private boolean dryRun;

    public static void main(String[] args) throws IOException {
        RemoveOrphanClients resaveProfiles = new RemoveOrphanClients();
        CmdLineParser parser = new CmdLineParser(resaveProfiles);
        try {
            parser.parseArgument(args);
            resaveProfiles.init();
            resaveProfiles.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        } catch (Throwable t) {
            System.err.println(t);
            System.exit(2);
        }
        System.exit(0);
    }

    public void execute() throws IOException {
        List<ClientDetailsEntity> clientDetailsList = clientDetailsDao.getAll();
        for (ClientDetailsEntity clientDetailsEntity : clientDetailsList) {
            LOG.info("Checking client: {}", clientDetailsEntity.getId());
            if (PojoUtil.isEmpty(clientDetailsEntity.getGroupProfileId())) {
                LOG.info("Found orphan client: {}", clientDetailsEntity.getId());
                if (!dryRun) {
                    // Remove the client
                    LOG.info("Removing orphan client: {}", clientDetailsEntity.getId());
                    clientDetailsDao.remove(clientDetailsEntity.getId());
                }
            }
        }
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        clientDetailsDao = (ClientDetailsDao) context.getBean("clientDetailsDao");
    }

}
