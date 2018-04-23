package org.orcid.jaxb.model.v3.rc1.record.summary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "services", namespace = "http://www.orcid.org/ns/activities")
public class Services extends Affiliations<ServiceSummary> implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 5138824260612086887L;

    public Services() {

    }

    public Services(List<ServiceSummary> summaries) {
        super();
        this.summaries = summaries;
    }

    @Override
    public List<ServiceSummary> getSummaries() {
        if (this.summaries == null) {
            this.summaries = new ArrayList<ServiceSummary>();
        }
        return this.summaries;
    }
}
