package org.orcid.api.t1.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "org.springdoc")
/*@Import({org.springdoc.core.SpringDocConfiguration.class,
         org.springdoc.webmvc.core.SpringDocWebMvcConfiguration.class,
         org.springdoc.webmvc.ui.SwaggerConfig.class,
         org.springdoc.core.SwaggerUiConfigProperties.class,
         org.springdoc.core.SwaggerUiOAuthProperties.class,
         org.springdoc.core.SpringDocConfigProperties.class,
         org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class})*/
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("ORCID Members API").description(
                        "ORCID Members API using OpenAPI way!!! ."));
    }
}
