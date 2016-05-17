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
package org.orcid.persistence.jpa.entities;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.orcid.persistence.manager.cache.SourceEntityCacheManager;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@MappedSuperclass
@Configurable
public abstract class SourceAwareEntity<T extends Serializable> extends BaseEntity<T> {
    private static final long serialVersionUID = -5397119397438830995L;
    private String sourceId;
    private String clientSourceId;
    
    @Resource
    private SourceEntityCacheManager sourceEntityCacheManager;

    @Column(name = "source_id")
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Column(name = "client_source_id")
    public String getClientSourceId() {
        return clientSourceId;
    }

    public void setClientSourceId(String clientSourceId) {
        this.clientSourceId = clientSourceId;
    }

    @Transient
    public SourceEntity getSource() {        
        return sourceEntityCacheManager.retrieve(getElementSourceId());
    }

    @Transient
    public void setSource(SourceEntity source) {
        if(source != null) {
            if(source.getSourceClient() != null) {
                this.clientSourceId = source.getSourceClient().getClientId(); 
            } else if(source.getSourceProfile() != null) {
                this.sourceId = source.getSourceProfile().getId();
            }
        }
    }
    
    @Transient
    public String getElementSourceId() {
        if(!StringUtils.isEmpty(clientSourceId)) {
            return clientSourceId;
        } else if(!StringUtils.isEmpty(sourceId)) {
            return sourceId;
        }
        return null;
    }    
}
