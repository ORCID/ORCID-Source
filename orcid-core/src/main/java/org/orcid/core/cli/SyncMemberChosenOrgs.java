package org.orcid.core.cli;

import org.orcid.core.manager.MemberChosenOrgDisambiguatedManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SyncMemberChosenOrgs {
    
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        MemberChosenOrgDisambiguatedManager manager = (MemberChosenOrgDisambiguatedManager) context.getBean("memberChosenOrgDisambiguatedManager");
        manager.refreshMemberChosenOrgs();
    }

}
