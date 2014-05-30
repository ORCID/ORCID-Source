/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.cli;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class InitDb {

    private static final String CONFIG_FILE_KEY = "org.orcid.config.file";

    public static void main(String[] args) {
        String configFilePath = System.getProperty(CONFIG_FILE_KEY);
        if (StringUtils.isBlank(configFilePath)) {
            System.setProperty(CONFIG_FILE_KEY, "classpath:staging-persistence.properties");
        }
        // Just bootstrap the context to get the liquibase stuff to run
        new ClassPathXmlApplicationContext("orcid-persistence-context.xml");
        System.exit(0);
    }

}
