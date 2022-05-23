package org.orcid.pojo;

import org.orcid.jaxb.model.common_v2.Contributor;
import org.orcid.jaxb.model.common_v2.ContributorAttributes;

import java.util.ArrayList;
import java.util.List;

public class ContributorsRolesAndSequencesV2 extends Contributor {

    List<ContributorAttributes> rolesAndSequences = new ArrayList<>();

    public List<ContributorAttributes> getRolesAndSequences() {
        return rolesAndSequences;
    }

    public void setRolesAndSequences(List<ContributorAttributes> rolesAndSequences) {
        this.rolesAndSequences = rolesAndSequences;
    }
}
