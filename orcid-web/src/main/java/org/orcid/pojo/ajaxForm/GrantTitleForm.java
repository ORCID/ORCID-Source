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

import org.orcid.jaxb.model.message.FundingTitle;
import org.orcid.jaxb.model.message.Title;

public class GrantTitleForm implements ErrorsInterface, Serializable {
    
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text title;

    private TranslatedTitle translatedTitle;
    
    public static GrantTitleForm valueOf(FundingTitle grantTitle) {
        GrantTitleForm gt = new GrantTitleForm(); 
        if (grantTitle != null) {
            if (grantTitle.getTitle() != null) {
            	gt.setTitle(Text.valueOf(grantTitle.getTitle().getContent()));
            }
            if(grantTitle.getTranslatedTitle() != null){
                TranslatedTitle translatedTitle = new TranslatedTitle();
                translatedTitle.setContent((grantTitle.getTranslatedTitle() == null) ? null : grantTitle.getTranslatedTitle().getContent());
                translatedTitle.setLanguageCode((grantTitle.getTranslatedTitle() == null || grantTitle.getTranslatedTitle().getLanguageCode() == null) ? null : grantTitle.getTranslatedTitle().getLanguageCode());
                gt.setTranslatedTitle(translatedTitle);
            }

        }
        return gt;

    }

    public FundingTitle toGrantTitle() {
        FundingTitle gt = new FundingTitle();
        if (this.getTitle() != null)
        	gt.setTitle(new Title(this.getTitle().getValue()));       
        if(this.getTranslatedTitle() != null)
        	gt.setTranslatedTitle(this.getTranslatedTitle().toTranslatedTitle());
                
        return gt;
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

    public TranslatedTitle getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(TranslatedTitle translatedTitle) {
        this.translatedTitle = translatedTitle;
    }   
}
