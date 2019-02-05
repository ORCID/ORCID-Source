package org.orcid.jaxb.model.v3.rc2.record;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.common.adapters.AvailableLocalesAdapter;

import io.swagger.annotations.ApiModel;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType( propOrder = { "locale" })
@XmlRootElement(name = "preferences", namespace = "http://www.orcid.org/ns/preferences")
@ApiModel(value = "PreferencesV3_0_rc2")
public class Preferences implements Serializable {    
    private static final long serialVersionUID = -2143886440930470817L;
    @XmlJavaTypeAdapter(AvailableLocalesAdapter.class)
    @XmlElement(namespace = "http://www.orcid.org/ns/preferences")
    AvailableLocales locale;

    public AvailableLocales getLocale() {
        return locale;
    }

    public void setLocale(AvailableLocales locale) {
        this.locale = locale;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((locale == null) ? 0 : locale.hashCode());
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
        Preferences other = (Preferences) obj;
        if (locale != other.locale)
            return false;
        return true;
    }
    
    
}
