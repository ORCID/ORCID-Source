package org.orcid.jaxb.model.v3.rc2.record.summary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "employments", namespace = "http://www.orcid.org/ns/activities")
public class Employments extends Affiliations<EmploymentSummary> implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 2620166422482125404L;

    public Employments() {

    }

    public Employments(Collection<AffiliationGroup<EmploymentSummary>> groups) {
        super();
        this.groups = groups;
    }

    public Collection<AffiliationGroup<EmploymentSummary>> getEmploymentGroups() {
        if (this.groups == null) {
            this.groups = new ArrayList<AffiliationGroup<EmploymentSummary>>();
        }
        return (Collection<AffiliationGroup<EmploymentSummary>>) this.groups;
    }

    @Override
    public Collection<AffiliationGroup<EmploymentSummary>> retrieveGroups() {
        return getEmploymentGroups();
    }
}
