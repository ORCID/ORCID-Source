package org.orcid.pojo;

import org.orcid.core.contributors.roles.works.WorkContributorRoleConverter;
import org.orcid.core.utils.v3.ContributorUtils;
import org.orcid.jaxb.model.v3.release.common.Contributor;
import org.orcid.jaxb.model.v3.release.common.ContributorAttributes;
import org.orcid.jaxb.model.v3.release.common.ContributorEmail;
import org.orcid.jaxb.model.v3.release.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.pojo.ajaxForm.WorkForm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ContributorsRolesAndSequences extends Contributor {

    List<ContributorAttributes> rolesAndSequences = new ArrayList<>();

    public List<ContributorAttributes> getRolesAndSequences() {
        return rolesAndSequences;
    }

    public void setRolesAndSequences(List<ContributorAttributes> rolesAndSequences) {
        this.rolesAndSequences = rolesAndSequences;
    }

    public boolean compare(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContributorsRolesAndSequences other = (ContributorsRolesAndSequences) obj;

        if (creditName != null && other.getCreditName() != null) {
            if (!creditName.getContent().equals(other.getCreditName().getContent())) {
                return false;
            }
        } else if (WorkForm.isAnyObjectNotNull(creditName, other.getCreditName())) {
            return false;
        }

        if (contributorOrcid != null && other.getContributorOrcid() != null) {
            if (
                    !WorkForm.isEachObjectNull(contributorOrcid.getPath(), other.getContributorOrcid().getPath()) &&
                            !contributorOrcid.getPath().equalsIgnoreCase(other.getContributorOrcid().getPath())
            ) {
                return false;
            }
        } else if (WorkForm.isAnyObjectNotNull(contributorOrcid, other.getContributorOrcid())) {
            return false;
        }

        if (rolesAndSequences != null && other.getRolesAndSequences() != null) {
            if (rolesAndSequences.size() != other.getRolesAndSequences().size()) {
                return false;
            }
            AtomicBoolean isDifferent = new AtomicBoolean(false);
            WorkContributorRoleConverter roleConverter = new WorkContributorRoleConverter();
            ContributorUtils contributorUtils = new ContributorUtils(null);
            for (int i = 0; i < rolesAndSequences.size() ; i++) {
                if (rolesAndSequences.get(i).getContributorRole() != null && other.rolesAndSequences.get(i).getContributorRole() != null) {
                    if (!WorkForm.compareStrings(rolesAndSequences.get(i).getContributorRole(), contributorUtils.getCreditRole(roleConverter.toRoleValue(other.rolesAndSequences.get(i).getContributorRole())))) {
                        isDifferent.set(true);
                        break;
                    }
                }
                if (WorkForm.isAnyObjectNotNull(rolesAndSequences.get(i).getContributorRole(), other.rolesAndSequences.get(i).getContributorRole())) {
                    isDifferent.set(true);
                    break;
                }
                if (rolesAndSequences.get(i).getContributorSequence() != null && other.rolesAndSequences.get(i).getContributorSequence() != null) {
                    if (!rolesAndSequences.get(i).getContributorSequence().equals(other.rolesAndSequences.get(i).getContributorSequence())) {
                        isDifferent.set(true);
                        break;
                    }
                }
                if (WorkForm.isAnyObjectNotNull(rolesAndSequences.get(i).getContributorSequence(), other.rolesAndSequences.get(i).getContributorSequence())) {
                    isDifferent.set(true);
                    break;
                }
            }
            if (isDifferent.get()) {
                return false;
            }
        } else if (WorkForm.isAnyObjectNotNull(rolesAndSequences, other.getRolesAndSequences())) {
            return false;
        }

        return true;
    }
}

