package org.orcid.frontend.spring.configuration;

import org.orcid.utils.ReleaseNameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.FixedVersionStrategy;
import org.springframework.web.servlet.resource.VersionResourceResolver;

@Configuration
@EnableWebMvc
public class ResourceHandlersConfiguration extends WebMvcConfigurerAdapter {

    @Value("${org.orcid.frontend.static.resource.cache.enabled:true}")
    private boolean cacheResources;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String releaseVersion = ReleaseNameUtils.getReleaseName();
        VersionResourceResolver versionResourceResolver = new VersionResourceResolver().addVersionStrategy(new FixedVersionStrategy(releaseVersion), "/**");
        registry.addResourceHandler("/static/**").addResourceLocations("/static/").setCachePeriod(1206297260).resourceChain(cacheResources)
                .addResolver(versionResourceResolver);
    }
}
