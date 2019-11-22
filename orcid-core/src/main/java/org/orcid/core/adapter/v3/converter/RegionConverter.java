package org.orcid.core.adapter.v3.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * Converter that converts empty strings to null on read and null to empty strings on write
 * @author georgenash
 *
 */
public class RegionConverter extends BidirectionalConverter<String, String> {

    @Override
    public String convertTo(String source, Type<String> destinationType) {
        return source == null ? "" : source;
    }

    @Override
    public String convertFrom(String source, Type<String> destinationType) {
        return source != null && source.isEmpty() ? null : source;
    }
}
