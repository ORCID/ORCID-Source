package org.orcid.core.adapter.jsonidentifier.converter;

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