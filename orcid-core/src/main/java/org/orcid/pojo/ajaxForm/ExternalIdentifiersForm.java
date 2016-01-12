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
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.record_rc2.ExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifiers;

public class ExternalIdentifiersForm implements ErrorsInterface, Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    private List<ExternalIdentifierForm> externalIdentifiers = new ArrayList<ExternalIdentifierForm>();

    public static ExternalIdentifiersForm valueOf(ExternalIdentifiers extIds) {
        if(extIds == null) {
            return null;
        }
        ExternalIdentifiersForm form = new ExternalIdentifiersForm();
        for(ExternalIdentifier extId : extIds.getExternalIdentifier()) {
            form.getExternalIdentifiers().add(ExternalIdentifierForm.valueOf(extId));
        }
            
        return form;
    }
    
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<ExternalIdentifierForm> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(List<ExternalIdentifierForm> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }
}
