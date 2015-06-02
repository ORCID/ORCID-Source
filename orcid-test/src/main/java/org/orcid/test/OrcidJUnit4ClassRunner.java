/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.test;

import java.io.FileNotFoundException;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Log4jConfigurer;

public class OrcidJUnit4ClassRunner extends SpringJUnit4ClassRunner {

    public OrcidJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        try {
            Log4jConfigurer.initLogging("classpath:test_log4j.xml");
        } catch (FileNotFoundException ex) {
            System.err.println("Cannot Initialize test log4j");
        }
    }

}
