package org.orcid.scheduler.cleanup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class RedisExpiredMainIndexCleanup {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisExpiredMainIndexCleanup.class);

    public JedisPool pool;

    @Autowired
    private JedisPoolBuilder poolBuilder;

    @Value("${org.orcid.core.utils.cache.session.redis.pool.max:100}")
    private int jedisMaxPoolSize;

    public void execute() throws InterruptedException, ExecutionException {
        LOGGER.info("Process to clean old session indexes started");
        // Init a new pool for this execution
        pool = poolBuilder.build();
        // Get a resource
        Jedis j = pool.getResource();
        Set<String> activeSessions = j.keys("spring:session:sessions:*");
        LOGGER.info("Active sessions found: " + activeSessions.size());
        Set<String> activeSessionIds = new HashSet<>();
        for(String s : activeSessions) {
            String activeSessionId = s.substring(s.lastIndexOf(":") + 1);
            activeSessionIds.add(activeSessionId);
        }
        Set<String> mainIndexEntries = j.keys("spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:*");
        LOGGER.info("Main Index Entries to process: " + mainIndexEntries.size());
        ExecutorService executorService = Executors.newFixedThreadPool(jedisMaxPoolSize);
        List<Future<?>> futures = new ArrayList<>();
        for(String mainIndexEntry : mainIndexEntries) {
            Future<?> f = executorService.submit(() -> processMainIndex(mainIndexEntry, activeSessionIds));
            futures.add(f);
        }

        LOGGER.info("All tasks submitted to process");
        for(Future<?> f : futures) {
            f.get();
        }

        LOGGER.info("All tasks done, closing the process");
        executorService.shutdown();
        j.close();
        pool.close();
    }

    private void processMainIndex(String mainIndexEntry, Set<String> activeSessionIds) {
        Jedis j = pool.getResource();
        Set<String> sessionIds = j.smembers(mainIndexEntry);
        String orcid = mainIndexEntry.substring(mainIndexEntry.lastIndexOf(":") + 1);
        LOGGER.info("Checking main index for " + orcid + " Elements: " + sessionIds.size());
        if(sessionIds == null || sessionIds.isEmpty()) {
            removeMainIndex(mainIndexEntry, j);
        } else {
            LOGGER.trace("Iterating over main index entries in " + orcid);
            for (String sessionId : sessionIds) {
                if (!activeSessionIds.contains(sessionId)) {
                    removeSessionIdFromIndex(mainIndexEntry, sessionId, j);
                }
            }
            LOGGER.trace("DONE Iterating over main index entries in " + orcid);
        }
        // Check again the main index, if it is emtpy, remove it
        LOGGER.info("Checking main index again for " + mainIndexEntry);
        Set<String> sessionIdsAgain = j.smembers(mainIndexEntry);
        if (sessionIdsAgain == null || sessionIdsAgain.size() == 0) {
            removeMainIndex(mainIndexEntry, j);
        }

        j.close();
        LOGGER.trace("Done with main index entry: " + mainIndexEntry);
    }

    private void removeSessionIdFromIndex(String mainIndexName, String sessionId, Jedis j) {
        LOGGER.trace("Removing entry " + sessionId + " from index " + mainIndexName);
        j.srem(mainIndexName, sessionId);
    }

    private void removeMainIndex(String mainIndexName, Jedis j) {
        LOGGER.info("Removing main index: " + mainIndexName);
        j.del(mainIndexName);
    }

}
