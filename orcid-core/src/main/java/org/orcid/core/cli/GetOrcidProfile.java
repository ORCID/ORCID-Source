package org.orcid.core.cli;

import org.orcid.core.manager.OrcidProfileManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GetOrcidProfile {

    /**
     * @param args
     */
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        OrcidProfileManager orcidProfileManager = (OrcidProfileManager) context.getBean("orcidProfileManager");
        System.out.println(orcidProfileManager.retrieveOrcidProfile(args[0]));
        System.exit(0);
    }

}
