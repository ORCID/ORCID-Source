package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;

public interface OrgDisambiguatedSolrDao {

    public void persist(OrgDisambiguatedSolrDocument orgDisambiguatedSolrDocument);

    public void remove(Long id);

    public OrgDisambiguatedSolrDocument findById(Long id);

    public List<OrgDisambiguatedSolrDocument> getOrgs(String searchTerm, int firstResult, int maxResult, boolean promoteChosenOrgs);

    public List<OrgDisambiguatedSolrDocument> getOrgs(String searchTerm, int firstResult, int maxResult, boolean fundersOnly, boolean promoteChosenOrgs);

    List<OrgDisambiguatedSolrDocument> getOrgsForSelfService(String searchTerm, int firstResult, int maxResult);

}
