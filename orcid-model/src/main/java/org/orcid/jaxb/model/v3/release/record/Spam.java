package org.orcid.jaxb.model.v3.release.record;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;

import io.swagger.annotations.ApiModel;

/**
 * 
 * @author Daniel Palafox
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "createdDate", "lastModifiedDate", "sourceType", "spamCounter" })
@XmlRootElement(name = "spam", namespace = "http://www.orcid.org/ns/spam")
@ApiModel(value = "SpamV3_0")
public class Spam implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "source-type", namespace = "http://www.orcid.org/ns/spam")
    protected SourceType sourceType;
    @XmlElement(name = "spam-counter", namespace = "http://www.orcid.org/ns/spam")
    protected Integer spamCounter;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "last-modified-date")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "created-date")
    protected CreatedDate createdDate;
    public SourceType getSourceType() {
        return sourceType;
    }
    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }
    public Integer getSpamCounter() {
        return spamCounter;
    }
    public void setSpamCounter(Integer spamCounter) {
        this.spamCounter = spamCounter;
    }
    public LastModifiedDate getLastModifiedDate() {
        return lastModifiedDate;
    }
    public void setLastModifiedDate(LastModifiedDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    public CreatedDate getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(CreatedDate createdDate) {
        this.createdDate = createdDate;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
        result = prime * result + ((lastModifiedDate == null) ? 0 : lastModifiedDate.hashCode());
        result = prime * result + ((sourceType == null) ? 0 : sourceType.hashCode());
        result = prime * result + ((spamCounter == null) ? 0 : spamCounter.hashCode());
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
        Spam other = (Spam) obj;
        if (createdDate == null) {
            if (other.createdDate != null)
                return false;
        } else if (!createdDate.equals(other.createdDate))
            return false;
        if (lastModifiedDate == null) {
            if (other.lastModifiedDate != null)
                return false;
        } else if (!lastModifiedDate.equals(other.lastModifiedDate))
            return false;
        if (sourceType == null) {
            if (other.sourceType != null)
                return false;
        } else if (!sourceType.equals(other.sourceType))
            return false;
        if (spamCounter == null) {
            if (other.spamCounter != null)
                return false;
        } else if (!spamCounter.equals(other.spamCounter))
            return false;
        return true;
    }

    
   
    
}
