package org.orcid.core.cli;

import org.orcid.core.manager.OrgDisambiguatedManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class IndexOrgs {
    
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        OrgDisambiguatedManager manager = (OrgDisambiguatedManager) context.getBean("orgDisambiguatedManager");
        manager.processOrgsForIndexing();
    }

}
