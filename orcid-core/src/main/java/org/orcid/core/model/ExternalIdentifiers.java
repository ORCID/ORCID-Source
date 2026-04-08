package org.orcid.core.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "externalIdentifiers" })
@XmlRootElement(name = "external-identifiers", namespace = "http://www.orcid.org/ns/summary")
@Schema(description = "External identifiers list")
public class ExternalIdentifiers implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "external-identifier", namespace = "http://www.orcid.org/ns/summary")
    List<ExternalIdentifier> externalIdentifiers;

    public List<ExternalIdentifier> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(List<ExternalIdentifier> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalIdentifiers);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExternalIdentifiers other = (ExternalIdentifiers) obj;
        return Objects.equals(externalIdentifiers, other.externalIdentifiers);
    }        
}
