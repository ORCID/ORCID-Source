package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.solr.entities.OrgDefinedFundingTypeSolrDocument;

public interface FundingSubTypeSolrDao {
    public void persist(OrgDefinedFundingTypeSolrDocument fundingType);
    public List<OrgDefinedFundingTypeSolrDocument> getFundingTypes(String searchTerm, int firstResult, int maxResult);
}
