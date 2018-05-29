package org.orcid.core.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class ProcessDisambiguatedOrgsWithIncorrectPopularity {

    private OrgDisambiguatedManager orgDisambiguatedManager;

    public static void main(String[] args) {
        ProcessDisambiguatedOrgsWithIncorrectPopularity processDisambiguatedOrgsWithIncorrectPopularity = new ProcessDisambiguatedOrgsWithIncorrectPopularity();
        CmdLineParser parser = new CmdLineParser(processDisambiguatedOrgsWithIncorrectPopularity);
        try {
            parser.parseArgument(args);
            processDisambiguatedOrgsWithIncorrectPopularity.validateArgs(parser);
            processDisambiguatedOrgsWithIncorrectPopularity.init();
            processDisambiguatedOrgsWithIncorrectPopularity.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orgDisambiguatedManager = (OrgDisambiguatedManager) context.getBean("orgDisambiguatedManager");
    }

    public void execute() {
        orgDisambiguatedManager.processOrgsWithIncorrectPopularity();
    }

}