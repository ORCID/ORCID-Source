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
package org.orcid.persistence.solr.entities;

public class OrcidSolrResult {
    private String orcid;
    private float relevancyScore;

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public float getRelevancyScore() {
        return relevancyScore;
    }

    public void setRelevancyScore(float relevancyScore) {
        this.relevancyScore = relevancyScore;
    }

}
