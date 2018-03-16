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
