package org.orcid.core.cli;

import org.orcid.core.manager.EncryptionManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class DecryptForInternalUse {
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        EncryptionManager encyrptionManager = (EncryptionManager) context.getBean("encryptionManager");
        System.out.println(encyrptionManager.decryptForInternalUse(args[0]));
    }
}
