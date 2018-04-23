package org.orcid.jaxb.model.v3.rc1.record.summary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "employments", namespace = "http://www.orcid.org/ns/activities")
public class Employments extends Affiliations<EmploymentSummary> implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 2620166422482125404L;

    public Employments() {

    }

    public Employments(List<EmploymentSummary> summaries) {
        super();
        this.summaries = summaries;
    }

    @Override
    public List<EmploymentSummary> getSummaries() {
        if (this.summaries == null) {
            this.summaries = new ArrayList<EmploymentSummary>();
        }
        return this.summaries;
    }
}
