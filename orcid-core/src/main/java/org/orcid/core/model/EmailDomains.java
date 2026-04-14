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
@XmlType(propOrder = { "count", "emailDomains" })
@XmlRootElement(name = "email-domains", namespace = "http://www.orcid.org/ns/summary")
@Schema(description = "Email domains list")
public class EmailDomains implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "count", namespace = "http://www.orcid.org/ns/summary")
    private Integer count;
    @XmlElement(name = "email-domain", namespace = "http://www.orcid.org/ns/summary")
    private List<EmailDomain> emailDomains;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<EmailDomain> getEmailDomains() {
        return emailDomains;
    }

    public void setEmailDomains(List<EmailDomain> emailDomains ) {
        this.emailDomains = emailDomains ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, emailDomains);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EmailDomains other = (EmailDomains) obj;
        return Objects.equals(count, other.count) && Objects.equals(emailDomains, other.emailDomains);
    }

}
