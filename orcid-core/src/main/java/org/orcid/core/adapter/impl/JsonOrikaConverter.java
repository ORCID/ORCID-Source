package org.orcid.core.adapter.impl;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.orcid.core.utils.JsonUtils;

/**
 * 
 * @author Will Simpson
 *
 */
public final class JsonOrikaConverter<T> extends BidirectionalConverter<T, String> {

    @Override
    public String convertTo(T source, Type<String> destinationType) {
        return JsonUtils.convertToJsonString(source);
    }

    @Override
    public T convertFrom(String source, Type<T> destinationType) {
        return (T) JsonUtils.readObjectFromJsonString(source, destinationType.getRawType());
    }

}