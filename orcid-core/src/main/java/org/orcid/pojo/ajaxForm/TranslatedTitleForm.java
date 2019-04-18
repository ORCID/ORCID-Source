package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.orcid.jaxb.model.v3.release.common.TranslatedTitle;

public class TranslatedTitleForm implements ErrorsInterface, Required, Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    
    private String content;

    private String languageCode;
    
    private String languageName;
    
    private boolean required = false;
    
    private String getRequiredMessage;
    
    public TranslatedTitleForm() {
        
    }
    
    public TranslatedTitleForm(String content, String languageCode) {        
        this.content = content;
        this.languageCode = languageCode;
    }
    
    public static TranslatedTitleForm valueOf(TranslatedTitle translatedTitle){
    	if(translatedTitle == null)
    		return null;
        TranslatedTitleForm result = new TranslatedTitleForm();
        result.setContent(translatedTitle.getContent());
        result.setLanguageCode(translatedTitle.getLanguageCode());
        return result;
    }
    
    public TranslatedTitle toTranslatedTitle(){
        TranslatedTitle result = new TranslatedTitle();
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + ((languageCode == null) ? 0 : languageCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TranslatedTitleForm other = (TranslatedTitleForm) obj;
        if (content == null) {
            if (other.content != null)
                return false;
        } else if (!content.equals(other.content))
            return false;
        if (languageCode == null) {
            if (other.languageCode != null)
                return false;
        } else if (!languageCode.equals(other.languageCode))
            return false;
        return true;
    }                
}
