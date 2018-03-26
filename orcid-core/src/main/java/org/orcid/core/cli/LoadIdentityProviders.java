package org.orcid.core.cli;

import org.orcid.core.manager.IdentityProviderManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class LoadIdentityProviders {

    @SuppressWarnings("resource")
    public static void main(String args[]) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        IdentityProviderManager identityProviderManager = (IdentityProviderManager) context.getBean("identityProviderManager");
        identityProviderManager.loadIdentityProviders();
        System.exit(0);
    }

}
