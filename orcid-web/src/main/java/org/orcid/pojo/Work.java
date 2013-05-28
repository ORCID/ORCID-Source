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
package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.pojo.ajaxForm.ErrorsInterface;

public class Work extends OrcidWork implements ErrorsInterface {
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    public Work () {
        
    }
    
    public Work(OrcidWork orcidWork){
        this.setPublicationDate(orcidWork.getPublicationDate());
        this.setPutCode(orcidWork.getPutCode());
        this.setShortDescription(orcidWork.getShortDescription());
        this.setUrl(orcidWork.getUrl());
        this.setVisibility(orcidWork.getVisibility());
        this.setWorkCitation(orcidWork.getWorkCitation());
        this.setWorkContributors(orcidWork.getWorkContributors());
        this.setWorkExternalIdentifiers(orcidWork.getWorkExternalIdentifiers());
        this.setWorkSource(orcidWork.getWorkSource());
        this.setWorkTitle(orcidWork.getWorkTitle());
        this.setWorkType(orcidWork.getWorkType());
    }
    
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
