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

import org.orcid.core.salesforce.model.MemberDetails;

public class ConsortiumForm implements ErrorsInterface, Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    private Text name;
    private Text website;

    @Override
    public List<String> getErrors() {
        return errors;
    }

    @Override
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getName() {
        return name;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public Text getWebsite() {
        return website;
    }

    public void setWebsite(Text website) {
        this.website = website;
    }

    public static ConsortiumForm fromMemberDetails(MemberDetails memberDetails) {
        ConsortiumForm form = new ConsortiumForm();
        form.setName(Text.valueOf(memberDetails.getMember().getName()));
        form.setWebsite(Text.valueOf(memberDetails.getMember().getWebsiteUrl().toString()));
        return form;
    }

    
}
