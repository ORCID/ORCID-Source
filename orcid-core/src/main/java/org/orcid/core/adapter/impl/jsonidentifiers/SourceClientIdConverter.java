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

import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.common_v2.SourceClientId;
import org.orcid.pojo.ajaxForm.PojoUtil;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class SourceClientIdConverter extends BidirectionalConverter<SourceClientId, String> {

    private OrcidUrlManager orcidUrlManager;
    
    public SourceClientIdConverter(OrcidUrlManager orcidUrlManager) {
        this.orcidUrlManager = orcidUrlManager;
    }
    
    @Override
    public String convertTo(SourceClientId source, Type<String> destinationType) {
        if(source == null){
            return null;
        }
        
        return source.getPath();
    }

    @Override
    public SourceClientId convertFrom(String source, Type<SourceClientId> destinationType) {
        if(PojoUtil.isEmpty(source)) {
            return null;
        }
        
        SourceClientId sourceClientId = new SourceClientId();
        sourceClientId.setHost(orcidUrlManager.getBaseHost());
        sourceClientId.setUri(orcidUrlManager.getBaseUriHttp() + "/client/" + source);
        sourceClientId.setPath(source);
        return sourceClientId;
    }

}
