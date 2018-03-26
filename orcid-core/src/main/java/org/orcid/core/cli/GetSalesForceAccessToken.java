package org.orcid.core.cli;

import org.orcid.core.salesforce.dao.SalesForceDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class GetSalesForceAccessToken {

    @SuppressWarnings("resource")
    public static void main(String args[]) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        SalesForceDao salesForceDao = (SalesForceDao) context.getBean("salesForceDao");
        String accessToken = salesForceDao.getAccessToken();
        System.out.println(accessToken);
        System.exit(0);
    }

}
