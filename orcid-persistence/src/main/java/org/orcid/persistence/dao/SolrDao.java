package org.orcid.persistence.dao;

import java.io.Reader;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.orcid.utils.solr.entities.*;

public interface SolrDao {

    /**
     * Method to persist a OrcidSolrDocument and any fields that it has set,
     * into SOLR.
     * 
     * @param orcidSolrDocument
     */
    void persist(OrcidSolrDocument orcidSolrDocument);

    /**
     * Method to remove any orcids with the given string from SOLR. Non-existent
     * orcids will be ignored.
     * 
     * @param orcids
     */
    void removeOrcids(List<String> orcids);

    /**
     * Method to retrieve a single OrcidSolrResult based on the unique Orcid
     * Field.
     * 
     * @param orcid
     * @return null if a record does't exist for that Orcid or a single
     *         OrcidSolrResult otherwise
     */
    OrcidSolrResult findByOrcid(String orcid);

    /**
     * Method to retrieve a single record based on the unique Orcid Field.
     * 
     * Please close the reader when you have finished with it. Thanks!
     * 
     * @param orcid
     * @return null if a record does't exist for that Orcid or a reader for the
     *         record XML
     */
    Reader findByOrcidAsReader(String orcid);

    /**
     * Method to retrieve a List of OrcidSolrResult wrapped in an
     * OrcidSolrResults object. Since this is for internal (NOT exposed via a
     * REST API and only used by the Orcid web app) this currently expects only
     * the query values representing the 'q' query field.
     * 
     * @param solrQuery
     * @param start
     *            row to start query at
     * @param rows
     *            number of row to query
     * @return
     * @See {@link SolrDaoTest} for examples of this usage
     */
    OrcidSolrResults findByDocumentCriteria(String solrQuery, Integer start, Integer rows);

    /**
     * /** Method to retrieve a List of OrcidSolrResult wrapped in an
     * OrcidSolrResults object. Since this is exposed this uses a set of Map
     * values to build up the different params of a SOLR query. NB currently
     * only a single value is accepted per key for this map.
     * 
     * @param solrMap
     * @return
     * @See {@link T2OrcidApiClientIntegrationTest} for example usage of the
     *      query string
     */
    OrcidSolrResults findByDocumentCriteria(Map<String, List<String>> solrMap);

    Date retrieveLastModified(String orcid);
}
