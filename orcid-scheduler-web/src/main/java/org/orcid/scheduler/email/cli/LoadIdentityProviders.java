package org.orcid.scheduler.email.cli;

import org.orcid.scheduler.tasks.IdentityProviderLoader;
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
        IdentityProviderLoader loader = (IdentityProviderLoader) context.getBean("identityProviderLoader");
        loader.loadIdentityProviders();
        System.exit(0);
    }

}
