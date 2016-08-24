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
package org.orcid.utils.solr.entities;

import org.apache.solr.client.solrj.beans.Field;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class OrgDefinedFundingTypeSolrDocument {

    @Field(SolrConstants.ORG_DEFINED_FUNDING_TYPE)
    private String orgDefinedFundingType;

    public String getOrgDefinedFundingType() {
        return orgDefinedFundingType;
    }

    public void setOrgDefinedFundingType(String orgDefinedFundingType) {
        this.orgDefinedFundingType = orgDefinedFundingType;
    }      
}
