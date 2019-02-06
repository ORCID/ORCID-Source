package org.orcid.core.cli;

import org.orcid.core.manager.EncryptionManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GenerateEmailHash {

    private EncryptionManager encryptionManager;
    
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("No email address supplied");
        }
        String email = args[0];
        GenerateEmailHash generateEmailHash = new GenerateEmailHash();
        generateEmailHash.init();
        System.out.println(generateEmailHash.generateEmailHash(email));
    }

    private String generateEmailHash(String email) {
        return encryptionManager.getEmailHash(email);
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        encryptionManager = (EncryptionManager) context.getBean("encryptionManager");
    }
    
}
