/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;

public class WorkExternalIdentifier implements ErrorsInterface, Serializable {
    
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text workExternalIdentifierId;

    private Text workExternalIdentifierType;
    
    public WorkExternalIdentifier() {
        
    }

    public WorkExternalIdentifier(org.orcid.jaxb.model.message.WorkExternalIdentifier workExternalIdentifier) {
        if (workExternalIdentifier != null) {
            if (workExternalIdentifier.getWorkExternalIdentifierId() != null)
                this.setWorkExternalIdentifierId(new Text(workExternalIdentifier.getWorkExternalIdentifierId().getContent()));
            if (workExternalIdentifier.getWorkExternalIdentifierType() != null)
                this.setWorkExternalIdentifierType(new Text(workExternalIdentifier.getWorkExternalIdentifierType().value()));
        }

    }
    
    public org.orcid.jaxb.model.message.WorkExternalIdentifier toWorkExternalIdentifier() {
        org.orcid.jaxb.model.message.WorkExternalIdentifier we = new org.orcid.jaxb.model.message.WorkExternalIdentifier();
        if (this.getWorkExternalIdentifierId() != null) 
            we.setWorkExternalIdentifierId(new WorkExternalIdentifierId(this.getWorkExternalIdentifierId().getValue()));
        if (this.getWorkExternalIdentifierType() != null)
            we.setWorkExternalIdentifierType(WorkExternalIdentifierType.fromValue(this.getWorkExternalIdentifierType().getValue()));
        return we;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getWorkExternalIdentifierId() {
        return workExternalIdentifierId;
    }

    public void setWorkExternalIdentifierId(Text workExternalIdentifierId) {
        this.workExternalIdentifierId = workExternalIdentifierId;
    }

    public Text getWorkExternalIdentifierType() {
        return workExternalIdentifierType;
    }

    public void setWorkExternalIdentifierType(Text workExternalIdentifierType) {
        this.workExternalIdentifierType = workExternalIdentifierType;
    }


}
