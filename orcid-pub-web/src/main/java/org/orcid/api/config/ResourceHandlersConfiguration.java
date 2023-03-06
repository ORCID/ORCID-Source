package org.orcid.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
@EnableWebMvc
public class ResourceHandlersConfiguration implements WebMvcConfigurer {

    @Value("${org.orcid.frontend.static.resource.cache.enabled:true}")
    private boolean cacheResources;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {        
        registry.addResourceHandler("/static/**").addResourceLocations("/static/").setCachePeriod(1206297260).resourceChain(cacheResources)
                .addResolver(new PathResourceResolver());
    }
}
