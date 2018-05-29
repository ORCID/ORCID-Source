package org.orcid.frontend.web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.orcid.core.manager.impl.RegistrationManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonPasswords {
    
    private static final String COMMON_PASSWORDS_FILENAME = "common_passwords.txt";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonPasswords.class);
    
    private static List<String> commonPasswords;
    
    static {
        LOGGER.info("Building common passwords list...");
        commonPasswords = new ArrayList<>();
        InputStream inputStream = RegistrationManagerImpl.class.getResourceAsStream(COMMON_PASSWORDS_FILENAME);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = reader.readLine();
            while (line != null) {
                commonPasswords.add(line.trim());
                line = reader.readLine();
            }
        } catch (IOException e) {
            LOGGER.error("Error building list of common passwords", e);
            throw new RuntimeException(e);
        }
        LOGGER.info("Built list of " + commonPasswords.size() + " common passwords");
    }
    
    public static boolean passwordIsCommon(String password) {
        return commonPasswords.contains(password);
    }

}
