package org.orcid.core.salesforce.model;

import java.io.Serializable;

/**
 * 
 * @author Will Simpson
 *
 */
public class SubMember implements Serializable {

    private static final long serialVersionUID = 1L;

    private String parentAccountId;
    private String slug;
    private Opportunity opportunity;
    private Contact mainContact;

    public String getParentAccountId() {
        return parentAccountId;
    }

    public void setParentAccountId(String parentAccountId) {
        this.parentAccountId = parentAccountId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Opportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(Opportunity opportunity) {
        this.opportunity = opportunity;
    }

    public Contact getMainContact() {
        return mainContact;
    }

    public void setMainContact(Contact mainContact) {
        this.mainContact = mainContact;
    }

    @Override
    public String toString() {
        return "SalesForceSubMember [slug=" + slug + ", opportunity=" + opportunity + ", mainContact=" + mainContact + "]";
    }

}
