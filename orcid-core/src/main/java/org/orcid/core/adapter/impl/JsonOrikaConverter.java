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