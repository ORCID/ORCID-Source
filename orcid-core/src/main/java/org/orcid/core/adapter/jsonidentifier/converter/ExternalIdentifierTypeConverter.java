package org.orcid.core.adapter.jsonidentifier.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.util.HashMap;

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

    private final HashMap<String, String> fromMap = new HashMap<String, String>();

    @Override
    public String convertTo(String source, Type<String> destinationType) {
        return source.toUpperCase().replace("-", "_");
    }

    @Override
    public String convertFrom(String source, Type<String> destinationType) {
        if (source == null)
            return null;
        if(fromMap.containsKey(source)) {
            return fromMap.get(source);
        }
        // annoying hack because grant_number does it differently.
        if (source.equals("GRANT_NUMBER"))
            return "grant_number";
        String result = source.toLowerCase().replace("_", "-");
        fromMap.put(source, result);
        return result;
    }

}