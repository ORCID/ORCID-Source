package org.orcid.jaxb.model.v3.dev1.record.summary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "qualifications", namespace = "http://www.orcid.org/ns/activities")
public class Qualifications extends Affiliations<QualificationSummary> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4227026264367214274L;

    public Qualifications() {

    }

    public Qualifications(List<QualificationSummary> summaries) {
        super();
        this.summaries = summaries;
    }

    @Override
    public List<QualificationSummary> getSummaries() {
        if (this.summaries == null) {
            this.summaries = new ArrayList<QualificationSummary>();
        }
        return this.summaries;
    }
}
