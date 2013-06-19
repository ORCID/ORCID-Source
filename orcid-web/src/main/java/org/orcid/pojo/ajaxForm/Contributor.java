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
import java.util.ArrayList;
import java.util.List;

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
    
    private Text creditName;
    
    private Text contributorRole;
    
    private Visibility creditNameVisibility;

    public Contributor() {
        
    }
    
    public Contributor(org.orcid.jaxb.model.message.Contributor contributor) {
       if (contributor != null) {
           if (contributor.getContributorAttributes() != null) {
               contributor.getContributorAttributes();
               if (contributor.getContributorAttributes().getContributorRole() != null) 
                   this.setContributorRole( new Text(contributor.getContributorAttributes().getContributorRole().value()));
               if (contributor.getContributorAttributes().getContributorRole() != null)
                this.setContributorSequence(new Text(contributor.getContributorAttributes().getContributorSequence().value()));
           }
           if (contributor.getContributorEmail() != null)
               this.setEmail(new Text(contributor.getContributorEmail().getValue()));
           if (contributor.getContributorOrcid() != null)
               this.setOrcid(new Text(contributor.getContributorOrcid().getValue()));
           if (contributor.getCreditName() != null) {
               this.setCreditName(new  Text(contributor.getCreditName().getContent()));
               this.setCreditNameVisibility(new Visibility(contributor.getCreditName().getVisibility()));
           }       
       }
        
    }
    
    public org.orcid.jaxb.model.message.Contributor toContributor() {
        org.orcid.jaxb.model.message.Contributor c = new org.orcid.jaxb.model.message.Contributor();
        if (this.getContributorRole() != null || this.getContributorSequence() != null) {
            org.orcid.jaxb.model.message.ContributorAttributes ca = new org.orcid.jaxb.model.message.ContributorAttributes();
            if (this.getContributorRole() != null)
                ca.setContributorRole(ContributorRole.fromValue(this.getContributorRole().getValue()));
            if (this.getContributorSequence() != null)
                ca.setContributorSequence(SequenceType.fromValue(this.getContributorSequence().getValue()));
            c.setContributorAttributes(ca);
        }
        if (this.getEmail() != null)
            c.setContributorEmail(new ContributorEmail(this.getEmail().getValue()));
        if (this.getOrcid() != null)
            c.setContributorOrcid(new ContributorOrcid(this.getOrcid().getValue()));
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
