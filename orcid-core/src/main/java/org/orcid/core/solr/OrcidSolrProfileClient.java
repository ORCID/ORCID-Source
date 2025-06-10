package org.orcid.core.solr;

import static org.orcid.core.solr.SolrConstants.*;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.orcid.core.solr.OrcidSolrResult;
import org.orcid.core.solr.OrcidSolrResults;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.stereotype.Component;

@Component
public class OrcidSolrProfileClient extends OrcidSolrClient {

    @Resource(name = "solrReadOnlyProfileClient")
    private SolrClient solrReadOnlyProfileClient;

    public OrcidSolrResult findByOrcid(String orcid) {
        OrcidSolrResult orcidSolrResult = null;
        SolrQuery query = new SolrQuery();
        query.setQuery(ORCID + ":\"" + ClientUtils.escapeQueryChars(orcid) + "\"").setFields(SCORE, ORCID, PUBLIC_PROFILE);
        try {
            QueryResponse queryResponse = solrReadOnlyProfileClient.query(query);
            if (!queryResponse.getResults().isEmpty()) {
                SolrDocument solrDocument = queryResponse.getResults().get(0);
                orcidSolrResult = new OrcidSolrResult();
                orcidSolrResult.setRelevancyScore((Float) solrDocument.get(SCORE));
                orcidSolrResult.setOrcid((String) solrDocument.get(ORCID));
                orcidSolrResult.setPublicProfileMessage((String) solrDocument.getFieldValue(PUBLIC_PROFILE));
            }
        } catch (SolrServerException | IOException se) {
            String errorMessage = MessageFormat.format("Error when attempting to retrieve orcid {0}", new Object[] { orcid });
            throw new NonTransientDataAccessResourceException(errorMessage, se);
        }

        return orcidSolrResult;
    }

    public Date retrieveLastModified(String orcid) {
        SolrQuery query = new SolrQuery();
        query.setQuery(ORCID + ":\"" + ClientUtils.escapeQueryChars(orcid) + "\"");
        query.setFields(PROFILE_LAST_MODIFIED_DATE);
        try {
            QueryResponse response = solrReadOnlyProfileClient.query(query);
            List<SolrDocument> results = response.getResults();
            if (results.isEmpty()) {
                return null;
            } else {
                return (Date) results.get(0).getFieldValue(PROFILE_LAST_MODIFIED_DATE);
            }

        } catch (SolrServerException | IOException e) {
            throw new NonTransientDataAccessResourceException("Error retrieving last modified date from SOLR Server", e);
        }
    }

    public OrcidSolrResults findByDocumentCriteria(String queryString, Integer start, Integer rows) {
        SolrQuery query = new SolrQuery(queryString).setFields(SCORE, ORCID, PUBLIC_PROFILE);
        if (start != null)
            query.setStart(start);
        if (rows != null)
            query.setRows(rows);
        return querySolr(query);
    }

    public OrcidSolrResults findByDocumentCriteria(Map<String, List<String>> queryMap) {
        return findByDocumentCriteria(queryMap, new String[] { SCORE, ORCID });
    }
    
    public OrcidSolrResults findExpandedByDocumentCriteria(Map<String, List<String>> queryMap) {
        String requestedFieldList = queryMap.get("fl") != null ? queryMap.get("fl").get(0) : null;
        List<String> fieldList = new ArrayList<>();
        if (requestedFieldList != null) {
            String fields = getFieldList(requestedFieldList);
            fieldList = new ArrayList<>();
            for (String field : fields.split(",")) {
                fieldList.add(field);
            }
        } else {
            fieldList.addAll(ALLOWED_FIELDS);
        }
        fieldList.add(SCORE);
        return findByDocumentCriteria(queryMap, fieldList.toArray(new String[0]));
    }
    
    private OrcidSolrResults findByDocumentCriteria(Map<String, List<String>> queryMap, String... fieldList) {
        OrcidSolrResults orcidSolrResults = new OrcidSolrResults();
        List<OrcidSolrResult> orcidSolrResultsList = new ArrayList<>();
        orcidSolrResults.setResults(orcidSolrResultsList);
        SolrQuery solrQuery = new SolrQuery();
        for (Map.Entry<String, List<String>> entry : queryMap.entrySet()) {
            String queryKey = entry.getKey();
            List<String> escapedQueryVals = new ArrayList<>();
            for (String value : entry.getValue()) {
                escapedQueryVals.add(ClientUtils.escapeQueryChars(value));
            }
            solrQuery.add(queryKey, escapedQueryVals.get(0));
        }
        solrQuery.setFields(fieldList);
        return querySolr(solrQuery);
    }

    private OrcidSolrResults querySolr(SolrQuery query) {
        OrcidSolrResults orcidSolrResults = new OrcidSolrResults();
        List<OrcidSolrResult> orcidSolrResultsList = new ArrayList<>();
        orcidSolrResults.setResults(orcidSolrResultsList);
        try {
            QueryResponse queryResponse = solrReadOnlyProfileClient.query(query);
            for (SolrDocument solrDocument : queryResponse.getResults()) {
                OrcidSolrResult orcidSolrResult = new OrcidSolrResult();
                orcidSolrResult.setRelevancyScore((Float) solrDocument.getFieldValue(SCORE));
                orcidSolrResult.setOrcid((String) solrDocument.getFieldValue(ORCID));
                orcidSolrResult.setPublicProfileMessage((String) solrDocument.getFieldValue(PUBLIC_PROFILE));
                orcidSolrResult.setCreditName((String) solrDocument.getFieldValue(CREDIT_NAME));
                orcidSolrResult.setEmails(getStringList(solrDocument, EMAIL_ADDRESS));
                orcidSolrResult.setFamilyName((String) solrDocument.getFieldValue(FAMILY_NAME));
                orcidSolrResult.setGivenNames((String) solrDocument.getFieldValue(GIVEN_NAMES));
                orcidSolrResult.setInstitutionAffiliationNames(getInstitutionAffiliationNames(solrDocument));
                orcidSolrResult.setOtherNames(getStringList(solrDocument, OTHER_NAMES));
                orcidSolrResultsList.add(orcidSolrResult);
            }
            orcidSolrResults.setNumFound(queryResponse.getResults().getNumFound());

        } catch (SolrServerException | IOException se) {
            throw new NonTransientDataAccessResourceException("Error retrieving from SOLR Server", se);
        }
        return orcidSolrResults;
    }

    private Collection<String> getInstitutionAffiliationNames(SolrDocument solrDocument) {
        List<String> institutionNames = new ArrayList<>();
        institutionNames.addAll(getStringList(solrDocument, AFFILIATE_CURRENT_INSTITUTION_NAME));
        institutionNames.addAll(getStringList(solrDocument, AFFILIATE_PAST_INSTITUTION_NAMES));
        return institutionNames;
    }
    
    @SuppressWarnings("unchecked")
    private List<String> getStringList(SolrDocument solrDocument, String fieldName) {
        List<String> values = new ArrayList<>();
        if (solrDocument.getFieldValue(fieldName) != null) {
            if (solrDocument.getFieldValue(fieldName) instanceof String) {
                values.add((String) solrDocument.getFieldValue(fieldName)); 
            } else {
                values.addAll((List<String>) solrDocument.getFieldValue(fieldName));
            }
        }
        return values;
    }
}
