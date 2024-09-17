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
@XmlType(propOrder = { "count", "educationQualifications" })
@XmlRootElement(name = "education-qualifications", namespace = "http://www.orcid.org/ns/summary")
@Schema(description = "Education qualifications list")
public class EducationQualifications implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "count", namespace = "http://www.orcid.org/ns/summary")
    private Integer count;
    @XmlElement(name = "education-qualification", namespace = "http://www.orcid.org/ns/summary")
    private List<EducationQualification> educationQualifications;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<EducationQualification> getEducationQualifications() {
        return educationQualifications;
    }

    public void setEducationQualifications(List<EducationQualification> educationQualifications ) {
        this.educationQualifications = educationQualifications ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, educationQualifications);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EducationQualifications other = (EducationQualifications) obj;
        return Objects.equals(count, other.count) && Objects.equals(educationQualifications, other.educationQualifications);
    }

}
