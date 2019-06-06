package org.orcid.core.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.messaging.JmsMessageSender;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class ProcessDisambiguatedOrgsForIndexing {

    private OrgDisambiguatedManager orgDisambiguatedManager;

    public static void main(String[] args) {
        ProcessDisambiguatedOrgsForIndexing processDisambiguatedOrgsForIndexing = new ProcessDisambiguatedOrgsForIndexing();
        CmdLineParser parser = new CmdLineParser(processDisambiguatedOrgsForIndexing);
        try {
            parser.parseArgument(args);
            processDisambiguatedOrgsForIndexing.validateArgs(parser);
            processDisambiguatedOrgsForIndexing.init();
            processDisambiguatedOrgsForIndexing.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
        
        System.exit(0);
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orgDisambiguatedManager = (OrgDisambiguatedManager) context.getBean("orgDisambiguatedManager");
        JmsMessageSender jmsMessageSender = (JmsMessageSender) context.getBean("jmsMessageSender");
        jmsMessageSender.setEnabled(true);        
    }

    public void execute() {
        orgDisambiguatedManager.processOrgsForIndexing();
    }

}