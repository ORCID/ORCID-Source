package org.orcid.core.cli;

import java.util.List;

import org.orcid.core.manager.SalesForceManagerLegacy;
import org.orcid.core.salesforce.model.Contact;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class GetContactsFromSalesForceByAccountId {

    @SuppressWarnings("resource")
    public static void main(String args[]) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        SalesForceManagerLegacy salesForceManager = (SalesForceManagerLegacy) context.getBean("salesForceManager");
        List<Contact> contacts = salesForceManager.retrieveContactsByAccountId(args[0]);
        System.out.println(contacts);
        System.exit(0);
    }

}
