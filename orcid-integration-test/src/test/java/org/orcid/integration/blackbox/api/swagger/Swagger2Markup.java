package org.orcid.integration.blackbox.api.swagger;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.github.robwin.markup.builder.MarkupLanguage;
import io.github.robwin.swagger2markup.Swagger2MarkupConverter;

/** Generates markup from swagger docs during integration testing
 * Confirms valid swagger.json
 * @author tom
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class Swagger2Markup {

    @Test
    public void convertMemberSwaggerToMarkdown() throws IOException {
        Swagger2MarkupConverter.from("https://localhost:8443/orcid-api-web/resources/swagger.json")
            .withMarkupLanguage(MarkupLanguage.MARKDOWN).build();
    }

}
