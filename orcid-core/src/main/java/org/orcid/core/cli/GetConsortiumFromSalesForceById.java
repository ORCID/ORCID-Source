package org.orcid.core.cli;

import org.orcid.core.manager.SalesForceManagerLegacy;
import org.orcid.core.salesforce.model.Consortium;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class GetConsortiumFromSalesForceById {

    @SuppressWarnings("resource")
    public static void main(String args[]) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        SalesForceManagerLegacy salesForceManager = (SalesForceManagerLegacy) context.getBean("salesForceManager");
        Consortium consortium = salesForceManager.retrieveConsortium(args[0]);
        System.out.println(consortium);
        System.exit(0);
    }

}
