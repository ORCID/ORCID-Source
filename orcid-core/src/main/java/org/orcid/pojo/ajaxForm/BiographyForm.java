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

import org.orcid.jaxb.model.record_v2.Biography;

public class BiographyForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private Text biography;
    private Visibility visiblity;

    private List<String> errors = new ArrayList<String>();

    public static BiographyForm valueOf(Biography bio) {
        BiographyForm bf = new BiographyForm();
        if(bio != null) {
            bf.setBiography(Text.valueOf(bio.getContent()));
            if(bio.getVisibility() != null) {
                bf.setVisiblity(Visibility.valueOf(bio.getVisibility()));
            } 
        }
        return bf;
    }        

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getBiography() {
        return biography;
    }

    public void setBiography(Text biography) {
        this.biography = biography;
    }

    public Visibility getVisiblity() {
        return visiblity;
    }

    public void setVisiblity(Visibility visiblity) {
        this.visiblity = visiblity;
    }
}
