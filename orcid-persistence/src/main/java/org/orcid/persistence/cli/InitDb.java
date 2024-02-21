package org.orcid.persistence.cli;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Will Simpson
 * 
 */
public class InitDb {

    private static final String CONFIG_FILE_KEY = "org.orcid.config.file";

    public static void main(String[] args) {
        //String configFilePath = "/Users/camdum/OrcidDev/github/ORCID-Source/properties/development.properties"; //System.getProperty(CONFIG_FILE_KEY);
        //if (StringUtils.isBlank(configFilePath)) {
            System.setProperty(CONFIG_FILE_KEY, "file:///Users/camdum/OrcidDev/github/ORCID-Source/properties/development.properties");
        //}
        // Just bootstrap the context to get the liquibase stuff to run
        new ClassPathXmlApplicationContext("orcid-persistence-context.xml");
        System.exit(0);
    }
}
