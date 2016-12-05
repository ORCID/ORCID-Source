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

import org.orcid.jaxb.model.common_rc4.Title;
import org.orcid.jaxb.model.record_rc4.FundingTitle;

public class FundingTitleForm implements ErrorsInterface, Serializable {
    
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text title;

    private TranslatedTitleForm translatedTitle;
    
    public static FundingTitleForm valueOf(FundingTitle grantTitle) {
        FundingTitleForm gt = new FundingTitleForm(); 
        if (grantTitle != null) {
            if (grantTitle.getTitle() != null) {
            	gt.setTitle(Text.valueOf(grantTitle.getTitle().getContent()));
            }
            if(grantTitle.getTranslatedTitle() != null){
                TranslatedTitleForm translatedTitle = new TranslatedTitleForm();
                translatedTitle.setContent((grantTitle.getTranslatedTitle() == null) ? null : grantTitle.getTranslatedTitle().getContent());
                translatedTitle.setLanguageCode((grantTitle.getTranslatedTitle() == null || grantTitle.getTranslatedTitle().getLanguageCode() == null) ? null : grantTitle.getTranslatedTitle().getLanguageCode());
                gt.setTranslatedTitle(translatedTitle);
            }

        }
        return gt;

    }

    public FundingTitle toFundingTitle() {
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

    public TranslatedTitleForm getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(TranslatedTitleForm translatedTitle) {
        this.translatedTitle = translatedTitle;
    }   
}
