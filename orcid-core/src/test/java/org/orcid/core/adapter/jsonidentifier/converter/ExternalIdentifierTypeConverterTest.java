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
package org.orcid.core.adapter.jsonidentifier.converter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class ExternalIdentifierTypeConverterTest {
   
    ExternalIdentifierTypeConverter converter = new ExternalIdentifierTypeConverter();

    @Test
    public void testConvertTo() {
        assertEquals("SOMETHING", converter.convertTo("something", null));
        assertEquals("GRANT_NUMBER", converter.convertTo("grant_number", null));
        assertEquals("ERM_WHAT_ELSE", converter.convertTo("erm-what-else", null));
    }

    @Test
    public void testConvertFrom() {
        assertEquals("something", converter.convertFrom("SOMETHING", null));
        assertEquals("grant_number", converter.convertFrom("GRANT_NUMBER", null));
        assertEquals("erm-what-else", converter.convertFrom("ERM_WHAT_ELSE", null));
    }

}