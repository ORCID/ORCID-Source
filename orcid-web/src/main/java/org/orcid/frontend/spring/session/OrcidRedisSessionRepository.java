package org.orcid.frontend.spring.session;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.session.data.redis.RedisSessionRepository;

public class OrcidRedisSessionRepository extends RedisSessionRepository {

    public OrcidRedisSessionRepository(RedisOperations<String, Object> sessionRedisOperations) {
        super(sessionRedisOperations);
    }

}
