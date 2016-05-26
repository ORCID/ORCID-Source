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
package org.orcid.core.adapter.impl.jsonidentifiers;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * Translates the API representation of identifier types (including
 * relationships) into the DB representation. e.g. PART-OF becomes part_of,
 * OTHER-ID becomes other_id
 * 
 * 
 * @author Will Simpson
 *
 */
public final class ExternalIdentifierTypeConverter extends BidirectionalConverter<String, String> {

    @Override
    public String convertTo(String source, Type<String> destinationType) {
        return source.toUpperCase().replace("-", "_");
    }

    @Override
    public String convertFrom(String source, Type<String> destinationType) {
        if (source == null)
            return null;
        // annoying hack because grant_number does it different.
        if (source.equals("GRANT_NUMBER"))
            return "grant_number";
        return source.toLowerCase().replace("_", "-");
    }

}