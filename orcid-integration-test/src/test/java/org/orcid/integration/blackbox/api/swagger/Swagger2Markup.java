/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.integration.blackbox.api.swagger;

import static org.junit.Assert.assertEquals;

import java.io.File;
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
    public void convertMemberSwaggerToAsciiDoc() throws IOException {
        Swagger2MarkupConverter.from("http://localhost:8080/orcid-api-web/resources/swagger.json").build()
            .intoFolder("src/docs/member/generated/asciidoc");

        // Then validate that three AsciiDoc files have been created
        String[] files = new File("src/docs/member/generated/asciidoc").list();
        assertEquals(files.length,3);
    }

    @Test
    public void convertPublicSwaggerToAsciiDoc() throws IOException {
        Swagger2MarkupConverter.from("http://localhost:8080/orcid-pub-web/resources/swagger.json").build()
            .intoFolder("src/docs/public/generated/asciidoc");

        // Then validate that three AsciiDoc files have been created
        String[] files = new File("src/docs/public/generated/asciidoc").list();
        assertEquals(files.length,3);
    }

    @Test
    public void convertMemberSwaggerToMarkdown() throws IOException {
        Swagger2MarkupConverter.from("http://localhost:8080/orcid-api-web/resources/swagger.json")
            .withMarkupLanguage(MarkupLanguage.MARKDOWN).build()
            .intoFolder("src/docs/member/generated/markdown");
        // Then validate that three Markdown files have been created
        String[] files = new File("src/docs/member/generated/markdown").list();
        assertEquals(files.length,3);
    }

    @Test
    public void convertPublicSwaggerToMarkdown() throws IOException {
        Swagger2MarkupConverter.from("http://localhost:8080/orcid-pub-web/resources/swagger.json")
            .withMarkupLanguage(MarkupLanguage.MARKDOWN).build()
            .intoFolder("src/docs/public/generated/markdown");
        // Then validate that three Markdown files have been created
        String[] files = new File("src/docs/public/generated/markdown").list();
        assertEquals(files.length,3);
    }

}
