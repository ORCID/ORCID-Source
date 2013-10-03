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

public class TranslatedTitle implements ErrorsInterface, Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    
    private String content;

    private String languageCode;
    
    public static TranslatedTitle valueOf(org.orcid.jaxb.model.message.TranslatedTitle translatedTitle){
    	if(translatedTitle == null)
    		return null;
        TranslatedTitle result = new TranslatedTitle();
        result.setContent(translatedTitle.getContent());
        result.setLanguageCode(translatedTitle.getLanguageCode());
        return result;
    }
    
    public org.orcid.jaxb.model.message.TranslatedTitle toTranslatedTitle(){
        org.orcid.jaxb.model.message.TranslatedTitle result = new org.orcid.jaxb.model.message.TranslatedTitle();
        result.setContent((content == null) ? null : content);
        result.setLanguageCode((languageCode == null) ? null : languageCode);        
        return result;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
    
    
}
