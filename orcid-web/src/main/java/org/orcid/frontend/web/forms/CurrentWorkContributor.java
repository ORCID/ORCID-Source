/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.web.forms;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.ContributorAttributes;
import org.orcid.jaxb.model.message.ContributorEmail;
import org.orcid.jaxb.model.message.ContributorOrcid;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.SequenceType;

public class CurrentWorkContributor {

    private String orcid;

    private String creditName;

    private String email;

    private String role;

    private String sequence;

    public CurrentWorkContributor() {
    }

    public CurrentWorkContributor(Contributor contributor) {
        ContributorOrcid contributorOrcid = contributor.getContributorOrcid();
        if (contributorOrcid != null) {
            orcid = contributorOrcid.getValue();
        }
        if (contributor != null && contributor.getCreditName() != null) {
            creditName = contributor.getCreditName().getContent();
        }

        ContributorAttributes contributorAttributes = contributor.getContributorAttributes();
        if (contributorAttributes != null) {
            ContributorRole contributorRole = contributorAttributes.getContributorRole();
            if (contributorRole != null) {
                role = contributorRole.value();
            }
            SequenceType sequenceType = contributorAttributes.getContributorSequence();
            if (sequenceType != null) {
                sequence = sequenceType.value();
            }
        }
    }

    public Contributor getContributor() {
        Contributor contributor = new Contributor();
        if (StringUtils.isNotBlank(orcid)) {
            contributor.setContributorOrcid(new ContributorOrcid(orcid));
        }
        if (StringUtils.isNotBlank(creditName)) {
            contributor.setCreditName(new CreditName(creditName));
        }
        if (StringUtils.isNotBlank(email)) {
            contributor.setContributorEmail(new ContributorEmail(email));
        }
        if (StringUtils.isNotBlank(role)) {
            ContributorAttributes attributes = retrieveContributorAttributes(contributor);
            ContributorRole contributorRole = ContributorRole.fromValue(role);
            attributes.setContributorRole(contributorRole);
        }
        if (StringUtils.isNotBlank(sequence)) {
            ContributorAttributes attributes = retrieveContributorAttributes(contributor);
            SequenceType sequenceType = SequenceType.fromValue(sequence);
            attributes.setContributorSequence(sequenceType);
        }
        return contributor;
    }

    private ContributorAttributes retrieveContributorAttributes(Contributor contributor) {
        ContributorAttributes attributes = contributor.getContributorAttributes();
        if (attributes == null) {
            attributes = new ContributorAttributes();
            contributor.setContributorAttributes(attributes);
        }
        return attributes;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getCreditName() {
        return creditName;
    }

    public void setCreditName(String creditName) {
        this.creditName = creditName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

}
