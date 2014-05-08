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

import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidProfile;

public class BiographyForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private Text biography;
    private Visibility visiblity;

    private List<String> errors = new ArrayList<String>();

    public static BiographyForm valueOf(OrcidProfile op) {
        BiographyForm bf = new BiographyForm();

        if (op.getOrcidBio() != null)
            if (op.getOrcidBio().getBiography() != null)
                if (op.getOrcidBio().getBiography().getContent() != null) {
                    bf.setBiography(Text.valueOf(op.getOrcidBio().getBiography().getContent()));
                    if (op.getOrcidBio().getBiography().getVisibility() != null) {
                        bf.setVisiblity(Visibility.valueOf(op.getOrcidBio().getBiography().getVisibility()));
                    } else {
                        bf.setVisiblity(Visibility.valueOf(OrcidVisibilityDefaults.BIOGRAPHY_DEFAULT.getVisibility()));
                    }
                }
        return bf;
    }

    public void populateProfile(OrcidProfile op) {
        if (op.getOrcidBio() == null)
            op.setOrcidBio(new OrcidBio());
        if (op.getOrcidBio().getBiography() == null)
            op.getOrcidBio().setBiography(new Biography());
        if (!PojoUtil.isEmpty(biography)) {
            op.getOrcidBio().getBiography().setContent(this.biography.toString());            
            if(this.visiblity == null)
                op.getOrcidBio().getBiography().setVisibility(OrcidVisibilityDefaults.BIOGRAPHY_DEFAULT.getVisibility());
            else
                op.getOrcidBio().getBiography().setVisibility(this.visiblity.getVisibility());
        }
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
