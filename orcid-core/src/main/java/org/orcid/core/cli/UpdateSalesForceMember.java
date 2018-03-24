package org.orcid.core.cli;

import java.io.IOException;
import java.net.URL;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.SalesForceManager;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class UpdateSalesForceMember {

    private static Logger LOGGER = LoggerFactory.getLogger(UpdateSalesForceMember.class);

    private SalesForceManager salesForceManager;
    @Option(name = "-a", usage = "SalesForce account ID of the member", required = true)
    private String accountId;
    @Option(name = "-n", usage = "The new name of the member")
    private String name;
    @Option(name = "-w", usage = "The new website of the member")
    private String website;

    public static void main(String[] args) throws IOException {
        UpdateSalesForceMember thisObject = new UpdateSalesForceMember();
        CmdLineParser parser = new CmdLineParser(thisObject);
        try {
            parser.parseArgument(args);
            thisObject.validateArgs(parser);
            thisObject.init();
            thisObject.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            System.exit(2);
        }
        System.exit(0);
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
    }

    public void execute() throws IOException {
        MemberDetails memberDetails = salesForceManager.retrieveDetails(accountId);
        Member member = memberDetails.getMember();
        member.setName(name);
        member.setWebsiteUrl(new URL(website));
        salesForceManager.updateMember(member);

        MemberDetails updatedDetails = salesForceManager.retrieveDetails(accountId);
        LOGGER.info("Update member info: {}", updatedDetails.getMember());

    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        salesForceManager = (SalesForceManager) context.getBean("salesForceManager");
    }

}
