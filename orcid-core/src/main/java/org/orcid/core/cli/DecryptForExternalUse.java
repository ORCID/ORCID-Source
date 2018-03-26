package org.orcid.core.cli;

import org.apache.commons.codec.binary.Base64;
import org.orcid.core.manager.EncryptionManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class DecryptForExternalUse {
    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
            EncryptionManager encyrptionManager = (EncryptionManager) context.getBean("encryptionManager");
            System.out.println("'" + encyrptionManager.decryptForExternalUse(new String(Base64.decodeBase64(args[0]), "UTF-8")) + "'");
        } catch (Throwable t) {
            System.out.println(t);
        } finally {
            System.exit(0);
        }
    }
}
