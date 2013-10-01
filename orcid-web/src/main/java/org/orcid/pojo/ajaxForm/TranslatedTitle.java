package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TranslatedTitle implements ErrorsInterface, Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    
    private Text content;

    private Text languageCode;
    
    public static TranslatedTitle valueOf(org.orcid.jaxb.model.message.TranslatedTitle translatedTitle){
        TranslatedTitle result = new TranslatedTitle();
        result.setContent((translatedTitle.getContent() == null) ? null : Text.valueOf(translatedTitle.getContent()));
        result.setLanguageCode((translatedTitle.getLanguageCode() == null) ? null : Text.valueOf(translatedTitle.getLanguageCode()));
        return result;
    }
    
    public org.orcid.jaxb.model.message.TranslatedTitle toTranslatedTitle(){
        org.orcid.jaxb.model.message.TranslatedTitle result = new org.orcid.jaxb.model.message.TranslatedTitle();
        result.setContent((content == null) ? null : content.getValue());
        result.setLanguageCode((languageCode == null) ? null : languageCode.getValue());        
        return result;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getContent() {
        return content;
    }

    public void setContent(Text content) {
        this.content = content;
    }

    public Text getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(Text languageCode) {
        this.languageCode = languageCode;
    }
    
    
}
