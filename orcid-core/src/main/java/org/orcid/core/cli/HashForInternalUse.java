package org.orcid.core.cli;

import org.orcid.core.manager.EncryptionManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class HashForInternalUse {

    @SuppressWarnings("resource")
    public static void main(String args[]) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        EncryptionManager encyrptionManager = (EncryptionManager) context.getBean("encryptionManager");
        long startTime = System.currentTimeMillis();
        String encrypted = encyrptionManager.hashForInternalUse(args[0]);
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Took: " + duration);
        System.out.println(encrypted);
    }

}
