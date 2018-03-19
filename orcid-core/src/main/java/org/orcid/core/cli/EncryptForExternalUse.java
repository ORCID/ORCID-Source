package org.orcid.core.cli;

import org.orcid.core.manager.EncryptionManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class EncryptForExternalUse {
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
            EncryptionManager encyrptionManager = (EncryptionManager) context.getBean("encryptionManager");
            System.out.println(encyrptionManager.encryptForExternalUse(args[0]));
        } catch (Throwable t) {
            System.out.println(t);
        } finally {
            System.exit(0);
        }
    }
}
