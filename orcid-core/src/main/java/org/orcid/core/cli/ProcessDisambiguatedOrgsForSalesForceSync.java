package org.orcid.core.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.orcid.core.manager.MemberChosenOrgDisambiguatedManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ProcessDisambiguatedOrgsForSalesForceSync {

    private MemberChosenOrgDisambiguatedManager memberChosenOrgDisambiguatedManager;

    public static void main(String[] args) {
        ProcessDisambiguatedOrgsForSalesForceSync processDisambiguatedOrgsForIndexing = new ProcessDisambiguatedOrgsForSalesForceSync();
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
        memberChosenOrgDisambiguatedManager = (MemberChosenOrgDisambiguatedManager) context.getBean("memberChosenOrgDisambiguatedManager");
    }

    public void execute() {
        memberChosenOrgDisambiguatedManager.refreshMemberChosenOrgs();
    }

}