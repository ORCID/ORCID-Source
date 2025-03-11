package org.orcid.frontend.spring.session.redis;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.session.FlushMode;
import org.springframework.session.SaveMode;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

import java.lang.annotation.*;

/**
 * This code is an adaptation from the original Spring Session Data Redis (https://spring.io/projects/spring-session,
 * source code https://github.com/spring-projects/spring-session/tree/main/spring-session-data-redis)
 * And has been modified to meet ORCID requirements.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  https://www.apache.org/licenses/LICENSE-2.0
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({OrcidRedisHttpSessionConfiguration.class})
@Configuration(
        proxyBeanMethods = false
)
public @interface OrcidEnableRedisHttpSession {
    int maxInactiveIntervalInSeconds() default 1800;

    String redisNamespace() default "spring:session";

    /** @deprecated */
    @Deprecated
    RedisFlushMode redisFlushMode() default RedisFlushMode.ON_SAVE;

    FlushMode flushMode() default FlushMode.ON_SAVE;

    String cleanupCron() default "0 * * * * *";

    SaveMode saveMode() default SaveMode.ON_SET_ATTRIBUTE;
}
