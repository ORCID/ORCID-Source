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
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.orcid.jaxb.model.message.ContributorEmail;
import org.orcid.jaxb.model.message.ContributorOrcid;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.SequenceType;

public class Contributor implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text contributorSequence;

    private Text email;

    private Text orcid;

    private Text uri;

    private Text creditName;

    private Text contributorRole;

    private Visibility creditNameVisibility;

    public static Contributor valueOf(org.orcid.jaxb.model.message.Contributor contributor) {
        Contributor c = new Contributor();
        if (contributor != null) {
            if (contributor.getContributorAttributes() != null) {
                contributor.getContributorAttributes();
                if (contributor.getContributorAttributes().getContributorRole() != null)
                    c.setContributorRole(Text.valueOf(contributor.getContributorAttributes().getContributorRole().value()));
                if (contributor.getContributorAttributes().getContributorSequence() != null)
                    c.setContributorSequence(Text.valueOf(contributor.getContributorAttributes().getContributorSequence().value()));
            }
            if (contributor.getContributorEmail() != null)
                c.setEmail(Text.valueOf(contributor.getContributorEmail().getValue()));
            if (contributor.getContributorOrcid() != null) {
                c.setOrcid(Text.valueOf(contributor.getContributorOrcid().getPath()));
                c.setUri(Text.valueOf(contributor.getContributorOrcid().getUri()));
            }
            if (contributor.getCreditName() != null) {
                c.setCreditName(Text.valueOf(contributor.getCreditName().getContent()));
                c.setCreditNameVisibility(Visibility.valueOf(contributor.getCreditName().getVisibility()));
            }
        }
        return c;

    }

    public org.orcid.jaxb.model.message.Contributor toContributor() {
        org.orcid.jaxb.model.message.Contributor c = new org.orcid.jaxb.model.message.Contributor();
        if (this.getContributorRole() != null || this.getContributorSequence() != null) {
            org.orcid.jaxb.model.message.ContributorAttributes ca = new org.orcid.jaxb.model.message.ContributorAttributes();
            if (!PojoUtil.isEmpty(this.getContributorRole()))
                ca.setContributorRole(ContributorRole.fromValue(this.getContributorRole().getValue()));
            if (!PojoUtil.isEmpty(this.getContributorSequence()))
                ca.setContributorSequence(SequenceType.fromValue(this.getContributorSequence().getValue()));
            c.setContributorAttributes(ca);
        }
        if (this.getEmail() != null)
            c.setContributorEmail(new ContributorEmail(this.getEmail().getValue()));
        if (this.getOrcid() != null) {
            ContributorOrcid contributorOrcid = new ContributorOrcid(this.getOrcid().getValue());
            if (this.getUri() != null) {
                String uriString = this.getUri().getValue();
                if (StringUtils.isNotBlank(uriString)) {
                    try {
                        URI uri = new URI(uriString);
                        contributorOrcid.setHost(uri.getHost());
                    } catch (URISyntaxException e) {
                        throw new RuntimeException("Problem parsing contributor orcid uri", e);
                    }
                }
            }
            contributorOrcid.setUri(this.getUri().getValue());

            c.setContributorOrcid(contributorOrcid);
        }
        if (this.getCreditName() != null) {
            CreditName cn = new CreditName(this.getCreditName().getValue());
            cn.setVisibility(org.orcid.jaxb.model.message.Visibility.fromValue(this.getCreditNameVisibility().getVisibility().value()));
            c.setCreditName(cn);
        }
        return c;
    }
    
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getContributorSequence() {
        return contributorSequence;
    }

    public void setContributorSequence(Text contributorSequence) {
        this.contributorSequence = contributorSequence;
    }

    public Text getContributorRole() {
        return contributorRole;
    }

    public void setContributorRole(Text contributorRole) {
        this.contributorRole = contributorRole;
    }

    public Text getEmail() {
        return email;
    }

    public void setEmail(Text email) {
        this.email = email;
    }

    public Text getOrcid() {
        return orcid;
    }

    public void setOrcid(Text orcid) {
        this.orcid = orcid;
    }

    public Text getUri() {
        return uri;
    }

    public void setUri(Text uri) {
        this.uri = uri;
    }

    public Text getCreditName() {
        return creditName;
    }

    public void setCreditName(Text creditName) {
        this.creditName = creditName;
    }

    public Visibility getCreditNameVisibility() {
        return creditNameVisibility;
    }

    public void setCreditNameVisibility(Visibility contributorRoleVisibility) {
        this.creditNameVisibility = contributorRoleVisibility;
    }

}
