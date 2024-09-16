package org.orcid.core.model;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.pojo.ajaxForm.Date;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "value","createdDate", "lastModified"})
@XmlRootElement(name = "education-qualification", namespace = "http://www.orcid.org/ns/summary")
@Schema(description = "Education Qualification")
public class EmailDomain {
    @XmlElement(name = "value", namespace = "http://www.orcid.org/ns/summary")
    protected String value;
    @XmlElement(name = "created-date", namespace = "http://www.orcid.org/ns/common")
    protected Date createdDate;
    @XmlElement(name = "last-modified", namespace = "http://www.orcid.org/ns/common")
    protected Date lastModified;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, createdDate, lastModified);
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
        return Objects.equals(createdDate, other.createdDate) && Objects.equals(lastModified, other.lastModified) && Objects.equals(value, other.value);
    }
}
