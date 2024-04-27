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
@XmlType(propOrder = { "count", "professionalActivities" })
@XmlRootElement(name = "professional-activities", namespace = "http://www.orcid.org/ns/summary")
@Schema(description = "Professional activities list")
public class ProfessionalActivities implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "count", namespace = "http://www.orcid.org/ns/summary")
    private Integer count;
    @XmlElement(name = "professional-activity", namespace = "http://www.orcid.org/ns/summary")
    private List<ProfessionalActivity> professionalActivities;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<ProfessionalActivity> getProfessionalActivities() {
        return professionalActivities;
    }

    public void setProfessionalActivities(List<ProfessionalActivity> professionalActivities) {
        this.professionalActivities = professionalActivities;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, professionalActivities);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProfessionalActivities other = (ProfessionalActivities) obj;
        return Objects.equals(count, other.count) && Objects.equals(professionalActivities, other.professionalActivities);
    }

}
