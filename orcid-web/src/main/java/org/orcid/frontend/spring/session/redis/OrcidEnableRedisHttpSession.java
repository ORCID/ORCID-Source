package org.orcid.frontend.spring.session.redis;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.session.FlushMode;
import org.springframework.session.SaveMode;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

import java.lang.annotation.*;

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
