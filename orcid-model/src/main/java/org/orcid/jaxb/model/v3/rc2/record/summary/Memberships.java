package org.orcid.jaxb.model.v3.rc2.record.summary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;

@XmlRootElement(name = "memberships", namespace = "http://www.orcid.org/ns/activities")
@ApiModel(value = "MembershipsV3_0_rc2")
public class Memberships extends Affiliations<MembershipSummary> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1045628720886435629L;

    public Memberships() {

    }
    
    public Memberships(Collection<AffiliationGroup<MembershipSummary>> groups) {
        super();
        this.groups = groups;
    }

    public Collection<AffiliationGroup<MembershipSummary>> getMembershipGroups() {
        if (this.groups == null) {
            this.groups = new ArrayList<AffiliationGroup<MembershipSummary>>();
        }
        return (Collection<AffiliationGroup<MembershipSummary>>) this.groups;
    }

    @Override
    public Collection<AffiliationGroup<MembershipSummary>> retrieveGroups() {
        return getMembershipGroups();
    }
}
