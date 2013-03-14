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

public class SponsorIdPair {

    private String sponsorOrcid;

    private String externalId;

    public SponsorIdPair() {
    }

    public SponsorIdPair(String sponsorOrcid, String externalId) {
        super();
        this.sponsorOrcid = sponsorOrcid;
        this.externalId = externalId;
    }

    public String getSponsorOrcid() {
        return sponsorOrcid;
    }

    public void setSponsorOrcid(String sponsorOrcid) {
        this.sponsorOrcid = sponsorOrcid;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

}
