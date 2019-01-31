package org.orcid.jaxb.model.v3.rc2.common;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.common.LanguageCode;
import org.orcid.jaxb.model.common.adapters.LanguageCodeAdapter;

import io.swagger.annotations.ApiModel;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType( propOrder = { "content" })
@XmlRootElement(name = "translatedTitle")
@ApiModel(value = "TranslatedTitleV3_0_rc2")
public class TranslatedTitle implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @XmlValue
    protected String content;
    @XmlJavaTypeAdapter(LanguageCodeAdapter.class)
    @XmlAttribute(name="language-code", required = true)
    protected LanguageCode languageCode;

    public TranslatedTitle() {

    }

    public TranslatedTitle(String content) {
        this.content = content;
    }

    public TranslatedTitle(String content, String languageCode) {
        this.content = content;
        this.languageCode = (languageCode == null) ? null : LanguageCode.valueOf(languageCode);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguageCode() {
        return (languageCode == null) ? null : languageCode.name();
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = (languageCode == null) ? null : LanguageCode.valueOf(languageCode);               
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 1;
        result = prime * result + (StringUtils.isEmpty(this.content) ? 0 : this.content.hashCode());
        result = prime * result + (this.languageCode == null ? 0 : this.languageCode.hashCode());
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
        TranslatedTitle other = (TranslatedTitle) obj;
        if (this.content == null) {
            if (other.content != null)
                return false;
        } else if (!this.content.equals(other.content))
            return false;

        if (this.languageCode == null) {
            if (other.languageCode != null)
                return false;
        } else if (!this.languageCode.equals(other.languageCode))
            return false;

        return true;
    }
}
