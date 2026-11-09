package org.orcid.scheduler.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

import org.orcid.core.utils.cache.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class DisposableDomainLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisposableDomainLoader.class);

    private static final String REDIS_LIST_KEY = "disposable-domains";
    private static final int BATCH_SIZE = 1000;

    @Autowired
    private RedisClient redisClient;

    @Value("${org.orcid.scheduler.disposableDomains.url:https://raw.githubusercontent.com/disposable/disposable-email-domains/master/domains_strict.txt}")
    private String domainsUrl;

    public void execute() throws IOException {
        LOGGER.info("Process to load disposable email domains started");
        Set<String> domains = readDomains();
        storeDomains(domains);
        LOGGER.info("Process to load disposable email domains ended. Entries processed: {}", domains.size());
    }

    private Set<String> readDomains() throws IOException {
        Set<String> domains = new LinkedHashSet<>();
        URLConnection connection = new URL(domainsUrl).openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String domain = line.trim();
                if (domain.isEmpty()) {
                    LOGGER.warn("Skipping empty line {}", lineNumber);
                    continue;
                }
                if (!domains.add(domain)) {
                    LOGGER.warn("Skipping duplicate domain '{}' at line {}", domain, lineNumber);
                }
            }
        }
        return domains;
    }

    private void storeDomains(Set<String> domains) {
        if (domains.isEmpty()) {
            throw new IllegalStateException("No disposable domains found, existing redis list left untouched");
        }
        if (!redisClient.replaceList(REDIS_LIST_KEY, domains, BATCH_SIZE)) {
            throw new IllegalStateException("Redis client is not enabled, existing redis list left untouched");
        }
    }
}
