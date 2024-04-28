package org.orcid.core.model;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "putCode", "externalIdType", "externalIdValue", "externalIdUrl", "validated" })
@XmlRootElement(name = "external-identifier", namespace = "http://www.orcid.org/ns/summary")
@Schema(description = "External identifier")
public class ExternalIdentifier {
    @XmlElement(name = "put-code", namespace = "http://www.orcid.org/ns/summary")
    private Long putCode;
    @XmlElement(name = "external-id-type", namespace = "http://www.orcid.org/ns/summary")
    private String externalIdType;
    @XmlElement(name = "external-id-value", namespace = "http://www.orcid.org/ns/summary")
    private String externalIdValue;
    @XmlElement(name = "external-id-url", namespace = "http://www.orcid.org/ns/summary")
    private String externalIdUrl;
    @XmlElement(name = "validated", namespace = "http://www.orcid.org/ns/summary")
    private boolean validated;

    public Long getPutCode() {
        return putCode;
    }

    public void setPutCode(Long putCode) {
        this.putCode = putCode;
    }

    public String getExternalIdType() {
        return externalIdType;
    }

    public void setExternalIdType(String externalIdType) {
        this.externalIdType = externalIdType;
    }

    public String getExternalIdValue() {
        return externalIdValue;
    }

    public void setExternalIdValue(String externalIdValue) {
        this.externalIdValue = externalIdValue;
    }

    public String getExternalIdUrl() {
        return externalIdUrl;
    }

    public void setExternalIdUrl(String externalIdUrl) {
        this.externalIdUrl = externalIdUrl;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalIdType, externalIdUrl, externalIdValue, putCode, validated);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExternalIdentifier other = (ExternalIdentifier) obj;
        return Objects.equals(externalIdType, other.externalIdType) && Objects.equals(externalIdUrl, other.externalIdUrl)
                && Objects.equals(externalIdValue, other.externalIdValue) && Objects.equals(putCode, other.putCode) && validated == other.validated;
    }

}
