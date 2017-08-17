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
package org.orcid.core.adapter.v3.impl.jsonidentifiers;

import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.jaxb.model.v3.dev1.common.SourceName;
import org.orcid.pojo.ajaxForm.PojoUtil;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class SourceNameConverter extends CustomConverter<String, SourceName> {

    private SourceNameCacheManager sourceNameCacheManager;    
    
    public SourceNameConverter(SourceNameCacheManager sourceNameCacheManager) {
        this.sourceNameCacheManager = sourceNameCacheManager;
    }
    
    @Override
    public SourceName convert(String source, Type<? extends SourceName> destinationType) {
        if(PojoUtil.isEmpty(source)) {
            return null;
        }
        
        String sourceName = sourceNameCacheManager.retrieve(source);
        
        if(PojoUtil.isEmpty(sourceName)) {
            return null;
        }
        
        SourceName result = new SourceName();
        result.setContent(sourceName);
        return result;
    }

}
