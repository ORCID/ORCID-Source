/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.pojo;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceSubMember {

    private String slug;
    private SalesForceOpportunity opportunity;
    private SalesForceContact mainContact;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public SalesForceOpportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(SalesForceOpportunity opportunity) {
        this.opportunity = opportunity;
    }

    public SalesForceContact getMainContact() {
        return mainContact;
    }

    public void setMainContact(SalesForceContact mainContact) {
        this.mainContact = mainContact;
    }

    @Override
    public String toString() {
        return "SalesForceSubMember [slug=" + slug + ", opportunity=" + opportunity + ", mainContact=" + mainContact + "]";
    }

}
