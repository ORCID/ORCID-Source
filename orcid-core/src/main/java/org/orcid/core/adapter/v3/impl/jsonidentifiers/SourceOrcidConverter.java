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

import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.v3.dev1.common.SourceOrcid;
import org.orcid.pojo.ajaxForm.PojoUtil;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class SourceOrcidConverter extends BidirectionalConverter<SourceOrcid, String> {

    private OrcidUrlManager orcidUrlManager;    
    
    public SourceOrcidConverter(OrcidUrlManager orcidUrlManager) {
        this.orcidUrlManager = orcidUrlManager;
    }
    
    @Override
    public String convertTo(SourceOrcid source, Type<String> destinationType) {
        if(source == null){
            return null;
        }
        
        return source.getPath();
    }

    @Override
    public SourceOrcid convertFrom(String source, Type<SourceOrcid> destinationType) {
        if(PojoUtil.isEmpty(source)) {
            return null;
        }
        
        SourceOrcid sourceOrcid = new SourceOrcid();
        sourceOrcid.setHost(orcidUrlManager.getBaseHost());
        sourceOrcid.setUri(orcidUrlManager.getBaseUrl() + "/" + source);
        sourceOrcid.setPath(source);
        return sourceOrcid;
    }

}
