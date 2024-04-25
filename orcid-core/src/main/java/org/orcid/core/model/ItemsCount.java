package org.orcid.core.model;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "selfAssertedCount", "validatedCount" })
@Schema(description = "Items count abstract class")
public abstract class ItemsCount {
    @XmlElement(name = "self-asserted-count", namespace = "http://www.orcid.org/ns/summary")
    private Integer selfAssertedCount;
    @XmlElement(name = "validated-count", namespace = "http://www.orcid.org/ns/summary")
    private Integer validatedCount;

    public Integer getSelfAssertedCount() {
        return selfAssertedCount;
    }

    public void setSelfAssertedCount(Integer selfAssertedCount) {
        this.selfAssertedCount = selfAssertedCount;
    }

    public Integer getValidatedCount() {
        return validatedCount;
    }

    public void setValidatedCount(Integer validatedCount) {
        this.validatedCount = validatedCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(selfAssertedCount, validatedCount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ItemsCount other = (ItemsCount) obj;
        return Objects.equals(selfAssertedCount, other.selfAssertedCount) && Objects.equals(validatedCount, other.validatedCount);
    }
}
