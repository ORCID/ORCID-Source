package org.orcid.jaxb.model.v3.rc2.record.summary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "distinctions", namespace = "http://www.orcid.org/ns/activities")
public class Distinctions extends Affiliations<DistinctionSummary> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3281485714665085184L;

    public Distinctions() {

    }

    public Distinctions(Collection<AffiliationGroup<DistinctionSummary>> groups) {
        super();
        this.groups = groups;
    }
    
    public Collection<AffiliationGroup<DistinctionSummary>> getDistinctionGroups() {
        if (this.groups == null) {
            this.groups = new ArrayList<AffiliationGroup<DistinctionSummary>>();
        }
        return (Collection<AffiliationGroup<DistinctionSummary>>) this.groups;
    }

    @Override
    public Collection<AffiliationGroup<DistinctionSummary>> retrieveGroups() {
        return getDistinctionGroups();
    }

}
