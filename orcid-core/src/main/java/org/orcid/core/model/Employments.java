package org.orcid.core.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "count", "employments" })
@XmlRootElement(name = "employments", namespace = "http://www.orcid.org/ns/summary")
@Schema(description = "Employments list")
public class Employments implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "count", namespace = "http://www.orcid.org/ns/summary")
    private Integer count;
    @XmlElement(name = "employment", namespace = "http://www.orcid.org/ns/summary")
    private List<Employment> employments;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Employment> getEmployments() {
        return employments;
    }

    public void setEmployments(List<Employment> employments) {
        this.employments = employments;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, employments);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Employments other = (Employments) obj;
        return Objects.equals(count, other.count) && Objects.equals(employments, other.employments);
    }
}
