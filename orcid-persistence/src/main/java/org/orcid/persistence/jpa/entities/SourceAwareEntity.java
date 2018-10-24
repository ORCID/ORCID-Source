package org.orcid.persistence.jpa.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@MappedSuperclass
public abstract class SourceAwareEntity<T extends Serializable> extends BaseEntity<T> {
    private static final long serialVersionUID = -5397119397438830995L;
    protected String sourceId;
    protected String clientSourceId;   
    protected String assertionOriginSourceId;
    protected String assertionOriginClientSourceId;    

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

    @Column(name = "assertion_origin_source_id")
    public String getAssertionOriginSourceId() {
        return assertionOriginSourceId;
    }

    public void setAssertionOriginSourceId(String assertionOriginSourceId) {
        this.assertionOriginSourceId = assertionOriginSourceId;
    }

    @Column(name = "assertion_origin_client_source_id")
    public String getAssertionOriginClientSourceId() {
        return assertionOriginClientSourceId;
    }

    public void setAssertionOriginClientSourceId(String assertionOriginClientSourceId) {
        this.assertionOriginClientSourceId = assertionOriginClientSourceId;
    }
    
    /**
     * Get the element source id, helpful when we just need the id, not the complete source element
     * */
    @Transient
    public String getElementSourceId() {
        if(!StringUtils.isEmpty(clientSourceId)) {
            return clientSourceId;
        } else if(!StringUtils.isEmpty(sourceId)) {
            return sourceId;
        }
        return null;
    }    
    
    /**
     * Get the element assertion origin source id, helpful when we just need the id, not the complete source element
     * */
    @Transient
    public String getElementAssertionOriginSourceId() {
        if(!StringUtils.isEmpty(assertionOriginClientSourceId)) {
            return assertionOriginClientSourceId;
        } else if(!StringUtils.isEmpty(assertionOriginSourceId)) {
            return assertionOriginSourceId;
        }
        return null;
    }  
}
