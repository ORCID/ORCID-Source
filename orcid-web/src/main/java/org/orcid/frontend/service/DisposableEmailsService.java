package org.orcid.frontend.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.orcid.core.utils.cache.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

@Service
public class DisposableEmailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisposableEmailsService.class);

    private static final String REDIS_LIST_KEY = "disposable-domains";
    private static final long REFRESH_INTERVAL_MS = 6L * 60 * 60 * 1000;

    @Resource(name = "redisClient")
    private RedisClient redisClient;

    private volatile Set<String> disposableDomains = Collections.emptySet();

    @PostConstruct
    public void init() {
        refreshCache();
    }

    @Scheduled(initialDelay = REFRESH_INTERVAL_MS, fixedDelay = REFRESH_INTERVAL_MS)
    public void refreshCache() {
        try {
            List<String> domains = redisClient.getList(REDIS_LIST_KEY);
            if (domains == null || domains.isEmpty()) {
                LOGGER.warn("No disposable domains found in redis list {}, keeping the current cache ({} entries)", REDIS_LIST_KEY, disposableDomains.size());
                return;
            }
            Set<String> newCache = new HashSet<>();
            for (String domain : domains) {
                if (domain != null && !domain.trim().isEmpty()) {
                    newCache.add(domain.trim().toLowerCase(Locale.ROOT));
                }
            }
            disposableDomains = Collections.unmodifiableSet(newCache);
            LOGGER.info("Disposable domains cache refreshed with {} entries", newCache.size());
        } catch (Exception e) {
            LOGGER.error("Unable to refresh the disposable domains cache, keeping the current one", e);
        }
    }

    public boolean isDisposableEmail(String email) {
        if (email == null) {
            return false;
        }
        int at = email.lastIndexOf('@');
        if (at < 0 || at == email.length() - 1) {
            return false;
        }
        String domain = email.substring(at + 1).trim().toLowerCase(Locale.ROOT);
        Set<String> cache = disposableDomains;
        // Match the domain itself or any parent domain, so sub.mailinator.com matches mailinator.com
        String candidate = domain;
        while (true) {
            if (cache.contains(candidate)) {
                LOGGER.info("Email with domain {} is a disposable email", domain);
                return true;
            }
            int dot = candidate.indexOf('.');
            if (dot < 0) {
                return false;
            }
            candidate = candidate.substring(dot + 1);
        }
    }
}
