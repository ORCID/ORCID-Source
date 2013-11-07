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

import org.orcid.jaxb.model.message.Subtitle;
import org.orcid.jaxb.model.message.Title;

public class WorkTitle implements ErrorsInterface, Serializable {
    
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text title;

    private Text subtitle;
    
    private TranslatedTitle translatedTitle;
    
    public static WorkTitle valueOf(org.orcid.jaxb.model.message.WorkTitle workTitle) {
        WorkTitle wt = new WorkTitle(); 
        if (workTitle != null) {
            if (workTitle.getTitle() != null) {
                wt.setTitle(Text.valueOf(workTitle.getTitle().getContent()));
            }
            if (workTitle.getSubtitle() != null) {
                wt.setSubtitle(Text.valueOf(workTitle.getSubtitle().getContent()));
            }
            if(workTitle.getTranslatedTitle() != null){
                TranslatedTitle translatedTitle = new TranslatedTitle();
                translatedTitle.setContent((workTitle.getTranslatedTitle() == null) ? null : workTitle.getTranslatedTitle().getContent());
                translatedTitle.setLanguageCode((workTitle.getTranslatedTitle() == null || workTitle.getTranslatedTitle().getLanguageCode() == null) ? null : workTitle.getTranslatedTitle().getLanguageCode());
                wt.setTranslatedTitle(translatedTitle);
            }

        }
        return wt;

    }

    public org.orcid.jaxb.model.message.WorkTitle toWorkTitle() {
        org.orcid.jaxb.model.message.WorkTitle wt = new org.orcid.jaxb.model.message.WorkTitle();
        if (this.getTitle() != null)
            wt.setTitle(new Title(this.getTitle().getValue()));
        if (this.getSubtitle() != null)
            wt.setSubtitle(new Subtitle(this.getSubtitle().getValue()));
        if(this.getTranslatedTitle() != null)
            wt.setTranslatedTitle(this.getTranslatedTitle().toTranslatedTitle());
                
        return wt;
    }
    
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getTitle() {
        return title;
    }

    public void setTitle(Text title) {
        this.title = title;
    }

    public Text getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(Text subtitle) {
        this.subtitle = subtitle;
    }

    public TranslatedTitle getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(TranslatedTitle translatedTitle) {
        this.translatedTitle = translatedTitle;
    }   
}
