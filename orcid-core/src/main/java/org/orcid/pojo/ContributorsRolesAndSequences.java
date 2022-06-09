package org.orcid.pojo;

import org.orcid.jaxb.model.v3.release.common.Contributor;
import org.orcid.jaxb.model.v3.release.common.ContributorAttributes;
import org.orcid.jaxb.model.v3.release.common.ContributorEmail;
import org.orcid.jaxb.model.v3.release.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.release.common.CreditName;

import java.util.ArrayList;
import java.util.List;

public class ContributorsRolesAndSequences extends Contributor {
 
    List<ContributorAttributes> rolesAndSequences = new ArrayList<>();

    public List<ContributorAttributes> getRolesAndSequences() {
        return rolesAndSequences;
    }

    public void setRolesAndSequences(List<ContributorAttributes> rolesAndSequences) {
        this.rolesAndSequences = rolesAndSequences;
    }
}

