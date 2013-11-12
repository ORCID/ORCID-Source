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

import org.apache.commons.lang3.StringUtils;

public class TranslatedTitle implements ErrorsInterface, Required, Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    
    private String content;

    private String languageCode;
    
    private String languageName;
    
    private boolean required = false;
    
    private String getRequiredMessage;
    
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
        result.setContent(StringUtils.isEmpty(content) ? null : content);
        result.setLanguageCode(StringUtils.isEmpty(languageCode) ? null : languageCode);        
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
    
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getGetRequiredMessage() {
        return getRequiredMessage;
    }

    public void setGetRequiredMessage(String getRequiredMessage) {
        this.getRequiredMessage = getRequiredMessage;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }        
}
