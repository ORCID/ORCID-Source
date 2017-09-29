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

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;


@RunWith(OrcidJUnit4ClassRunner.class)
@Ignore
public final class ExternalIdentifierTypeConverterTest {

    @Test
    public void testConvertTo(String source, Type<String> destinationType) {
//        return source.toUpperCase().replace("-", "_");
    }

    @Test
    public void testConvertFrom(String source, Type<String> destinationType) {
//        if (source == null)
//            return null;
//        // annoying hack because grant_number does it different.
//        if (source.equals("GRANT_NUMBER"))
//            return "grant_number";
//        return source.toLowerCase().replace("_", "-");
    }

}