package org.orcid.jaxb.model.v3.dev1.record.summary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "distinctions", namespace = "http://www.orcid.org/ns/activities")
public class Distinctions extends Affiliations<DistinctionSummary> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3281485714665085184L;

    public Distinctions() {

    }

    public Distinctions(List<DistinctionSummary> summaries) {
        super();
        this.summaries = summaries;
    }

    @Override
    public List<DistinctionSummary> getSummaries() {
        if (this.summaries == null) {
            this.summaries = new ArrayList<DistinctionSummary>();
        }
        return this.summaries;
    }

}
