package org.orcid.jaxb.model.v3.release.record;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.TranslatedTitle;

import io.swagger.annotations.ApiModel;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType( propOrder = { "title", "translatedTitle" })
@XmlRootElement(name = "title", namespace = "http://www.orcid.org/ns/research-resource")
@ApiModel(value = "ResearchResourceTitleV3_0")
public class ResearchResourceTitle implements Serializable {

    private static final long serialVersionUID = 1L;
    @XmlElement(namespace = "http://www.orcid.org/ns/common")
    protected Title title;    
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "translated-title")
    protected TranslatedTitle translatedTitle;

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title value) {
        this.title = value;
    }    
    
    public TranslatedTitle getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(TranslatedTitle translatedTitle) {
        this.translatedTitle = translatedTitle;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((translatedTitle == null) ? 0 : translatedTitle.hashCode());
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
        ResearchResourceTitle other = (ResearchResourceTitle) obj;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        
        if(translatedTitle == null){
            if(other.translatedTitle != null)
                return false;
        } else if(!translatedTitle.equals(other.translatedTitle))
            return false;
        
        return true;
    }

}
