package org.orcid.frontend.spring.configuration;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.orcid.frontend.web.util.SwitchUserGrantedAuthorityDeserializer;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.core.Authentication;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

@Configuration
public class OrcidBeanClassLoaderAware implements BeanClassLoaderAware {
    private ClassLoader loader;

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(objectMapper());
    }

    /**
     * Customized {@link ObjectMapper} to add mix-in for class that doesn't have default
     * constructors
     * @return the {@link ObjectMapper} to use
     */
    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(SecurityJackson2Modules.getModules(this.loader));
        mapper.registerModules(new CoreJackson2Module());
        mapper.addMixIn(String[].class, StringArrayMixin.class);
        mapper.addMixIn(SwitchUserGrantedAuthority.class, SwitchUserGrantedAuthorityMixin.class);
        return mapper;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.loader = classLoader;
    }

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.CLASS
    )
    abstract class StringArrayMixin {
        @JsonCreator
        StringArrayMixin(String[] array) {
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE,
            getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @JsonDeserialize(using = SwitchUserGrantedAuthorityDeserializer.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    abstract class SwitchUserGrantedAuthorityMixin {

        /**
         * Mixin Constructor.
         * @param role the role
         */
        @JsonCreator
        public SwitchUserGrantedAuthorityMixin(@JsonProperty("authority") String role, @JsonProperty("source") Authentication authentication) {
        }

    }
}
