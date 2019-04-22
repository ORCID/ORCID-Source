package org.orcid.jaxb.model.v3.release.record.summary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.annotations.ApiModel;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "lastModifiedDate", "identifiers", "summaries" })
@XmlRootElement(name = "affiliation-group", namespace = "http://www.orcid.org/ns/activities")
@ApiModel(value = "AffiliationGroupV3_0")
public class AffiliationGroup<T extends AffiliationSummary> extends ActivityGroup implements Serializable {
    
    private static final long serialVersionUID = -8293559217646416864L;
    
    @XmlElements({ @XmlElement(namespace = "http://www.orcid.org/ns/distinction", name = "distinction-summary", type = DistinctionSummary.class),
            @XmlElement(namespace = "http://www.orcid.org/ns/invited-position", name = "invited-position-summary", type = InvitedPositionSummary.class),
            @XmlElement(namespace = "http://www.orcid.org/ns/education", name = "education-summary", type = EducationSummary.class),
            @XmlElement(namespace = "http://www.orcid.org/ns/employment", name = "employment-summary", type = EmploymentSummary.class),
            @XmlElement(namespace = "http://www.orcid.org/ns/membership", name = "membership-summary", type = MembershipSummary.class),
            @XmlElement(namespace = "http://www.orcid.org/ns/qualification", name = "qualification-summary", type = QualificationSummary.class),
            @XmlElement(namespace = "http://www.orcid.org/ns/service", name = "service-summary", type = ServiceSummary.class) })
    protected List<T> summaries = new ArrayList<>();

    public List<T> getActivities() {
        return summaries;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((summaries == null) ? 0 : summaries.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AffiliationGroup<T> other = (AffiliationGroup<T>) obj;
        if (summaries == null) {
            if (other.summaries != null)
                return false;
        } else if (!summaries.equals(other.summaries))
            return false;
        return true;
    }
}
