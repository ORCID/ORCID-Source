package org.orcid.integration.api.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.orcid.integration.blackbox.api.v2.rc1.BlackBoxBaseRC1;

/**
 * 
 * @author Angel Montenegro
 */
public class SystemPropertiesHelper {
    public static Properties getProperties() {
        Properties prop = new Properties();
        try {
            // Read the names of the property files
            String propertyFiles = System.getProperty("org.orcid.config.file");
            String[] files = propertyFiles.split(",");

            for (String file : files) {
                file = file.replace("classpath:", "");
                // For each config file, iterate and load the properties
                InputStream inputStream = BlackBoxBaseRC1.class.getClassLoader().getResourceAsStream(file);
                prop.load(inputStream);
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}
