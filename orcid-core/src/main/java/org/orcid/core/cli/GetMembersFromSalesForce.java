package org.orcid.core.cli;

import java.util.List;

import org.orcid.core.manager.SalesForceManager;
import org.orcid.core.salesforce.model.Member;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class GetMembersFromSalesForce {

    @SuppressWarnings("resource")
    public static void main(String args[]) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        SalesForceManager salesForceManager = (SalesForceManager) context.getBean("salesForceManager");
        List<Member> members = salesForceManager.retrieveMembers();
        members.stream().forEach(e -> System.out.println(e));
        System.exit(0);
    }

}
