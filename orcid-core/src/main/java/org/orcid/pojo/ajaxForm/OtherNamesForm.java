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

import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.OtherNames;

public class OtherNamesForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private List<Text> otherNames = new ArrayList<Text>();
    
    private Visibility visibility;

    public static OtherNamesForm valueOf(OtherNames otherNames) {
        OtherNamesForm on = new OtherNamesForm();
        if (otherNames ==  null) {
            on.setVisibility(new Visibility());
            return on;
        }
        if (otherNames.getOtherName() != null) {
            for (OtherName otherName:otherNames.getOtherName())
                if (otherName.getContent() != null)
                    on.getOtherNames().add(Text.valueOf(otherName.getContent()));
        }
        on.setVisibility(Visibility.valueOf(otherNames.getVisibility()));
        return on;
    }

    public OtherNames toOtherNames() {
        OtherNames otherName = new OtherNames();
        List<OtherName> kList = new ArrayList<OtherName>();
        for (Text text : this.otherNames) 
            kList.add(text.toOtherName());
        otherName.setOtherName(kList);
        otherName.setVisibility(this.getVisibility().getVisibility());
        return otherName;
    }

    
    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<Text> getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(List<Text> otherNames) {
        this.otherNames = otherNames;
    }
    
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

}
