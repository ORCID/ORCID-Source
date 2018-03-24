package org.orcid.jaxb.model.v3.dev1.record.summary;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.dev1.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.dev1.record.ActivitiesContainer;
import org.orcid.jaxb.model.v3.dev1.record.Activity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "lastModifiedDate", "summaries" })
public abstract class Affiliations<T extends AffiliationSummary> implements ActivitiesContainer {
    @XmlElement(name = "last-modified-date", namespace = "http://www.orcid.org/ns/common")
    protected LastModifiedDate lastModifiedDate;
    @XmlElements({ @XmlElement(namespace = "http://www.orcid.org/ns/distinction", name = "distinction-summary", type = DistinctionSummary.class),
            @XmlElement(namespace = "http://www.orcid.org/ns/invited-position", name = "invited-position-summary", type = InvitedPositionSummary.class),
            @XmlElement(namespace = "http://www.orcid.org/ns/education", name = "education-summary", type = EducationSummary.class),
            @XmlElement(namespace = "http://www.orcid.org/ns/employment", name = "employment-summary", type = EmploymentSummary.class),
            @XmlElement(namespace = "http://www.orcid.org/ns/membership", name = "membership-summary", type = MembershipSummary.class),
            @XmlElement(namespace = "http://www.orcid.org/ns/qualification", name = "qualification-summary", type = QualificationSummary.class),
            @XmlElement(namespace = "http://www.orcid.org/ns/service", name = "service-summary", type = ServiceSummary.class) })
    protected List<T> summaries;
    @XmlAttribute
    protected String path;

    public abstract List<T> getSummaries();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((summaries == null) ? 0 : summaries.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Affiliations other = (Affiliations) obj;
        if (summaries == null) {
            if (other.summaries != null)
                return false;
        } else if (!summaries.equals(other.summaries))
            return false;
        return true;
    }

    public LastModifiedDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LastModifiedDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public Map<Long, ? extends Activity> retrieveActivitiesAsMap() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Collection<? extends Activity> retrieveActivities() {
        return (Collection<? extends Activity>) summaries;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
