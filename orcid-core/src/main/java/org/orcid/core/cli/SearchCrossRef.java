package org.orcid.core.cli;

import org.orcid.core.manager.CrossRefManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class SearchCrossRef {

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        CrossRefManager manager = (CrossRefManager) context.getBean("crossRefManager");
        String response = manager.searchForMetadataAsString(args[0]);
        System.out.println(response);
    }

}
