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

import org.orcid.pojo.ajaxForm.ErrorsInterface;

public class GivenNames extends org.orcid.jaxb.model.message.GivenNames implements ErrorsInterface {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    public GivenNames() {

    }

    public GivenNames(org.orcid.jaxb.model.message.GivenNames givenNames) {
        this.setContent(givenNames.getContent());
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

}
