package org.orcid.core.model;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.jaxb.model.v3.release.common.VerificationDate;
import org.orcid.pojo.ajaxForm.Date;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "value", "verificationDate"})
@XmlRootElement(name = "email-domain", namespace = "http://www.orcid.org/ns/summary")
@Schema(description = "Email Domain")
public class EmailDomain {
    @XmlElement(name = "value", namespace = "http://www.orcid.org/ns/summary")
    protected String value;

    @XmlElement(name = "verification-date", namespace = "http://www.orcid.org/ns/summary")
    protected VerificationDate verificationDate;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public VerificationDate getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(VerificationDate verificationDate) {
        this.verificationDate = verificationDate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EmailDomain other = (EmailDomain) obj;
        return Objects.equals(value, other.value);
    }
}
