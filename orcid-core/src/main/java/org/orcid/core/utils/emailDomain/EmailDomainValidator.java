package org.orcid.core.utils.emailDomain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class EmailDomainValidator {

    private static final String PUBLIC_TLDS_LIST_URL = "https://publicsuffix.org/list/public_suffix_list.dat";
    private static EmailDomainValidator instance;
    private final Set<String> validTlds;

    // Private constructor to enforce singleton
    private EmailDomainValidator() {
        validTlds = new HashSet<>();
        loadTlds();
    }

    // Public method to get the singleton instance
    public static synchronized EmailDomainValidator getInstance() {
        if (instance == null) {
            instance = new EmailDomainValidator();
        }
        return instance;
    }

    // Load TLDs from the public suffix list
    private void loadTlds() {
        try {
            URL tldUrl = new URL(PUBLIC_TLDS_LIST_URL);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(tldUrl.openStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("//")) {
                        validTlds.add(line);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load TLD list");
        }
    }

    // Check if the email domain has a valid TLD
    public boolean isValidEmailDomain(String emailDomain) {
        for (String tld : validTlds) {
            if (emailDomain.endsWith("." + tld)) {
                return true;
            }
        }
        return false;
    }
}


