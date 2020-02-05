package org.orcid.core.solr;

import static org.orcid.utils.solr.entities.SolrConstants.AFFILIATE_CURRENT_INSTITUTION_NAME;
import static org.orcid.utils.solr.entities.SolrConstants.AFFILIATE_PAST_INSTITUTION_NAMES;
import static org.orcid.utils.solr.entities.SolrConstants.CREDIT_NAME;
import static org.orcid.utils.solr.entities.SolrConstants.EMAIL_ADDRESS;
import static org.orcid.utils.solr.entities.SolrConstants.FAMILY_NAME;
import static org.orcid.utils.solr.entities.SolrConstants.GIVEN_NAMES;
import static org.orcid.utils.solr.entities.SolrConstants.ORCID;
import static org.orcid.utils.solr.entities.SolrConstants.OTHER_NAMES;
import static org.orcid.utils.solr.entities.SolrConstants.PROFILE_LAST_MODIFIED_DATE;
import static org.orcid.utils.solr.entities.SolrConstants.PUBLIC_PROFILE;
import static org.orcid.utils.solr.entities.SolrConstants.SCORE;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.orcid.utils.solr.entities.OrcidSolrResult;
import org.orcid.utils.solr.entities.OrcidSolrResults;
import org.orcid.utils.solr.entities.SolrConstants;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.stereotype.Component;

@Component
public class OrcidSolrProfileClient extends OrcidSolrClient {

    @Resource(name = "solrReadOnlyProfileClient")
    private SolrClient solrReadOnlyProfileClient;

    public OrcidSolrResult findByOrcid(String orcid) {
        OrcidSolrResult orcidSolrResult = null;
        SolrQuery query = new SolrQuery();
        query.setQuery(ORCID + ":\"" + orcid + "\"").setFields(SCORE, ORCID, PUBLIC_PROFILE);
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
        query.setQuery(ORCID + ":\"" + orcid + "\"");
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
            fieldList.addAll(SolrConstants.ALLOWED_FIELDS);
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
            List<String> queryVals = entry.getValue();
            solrQuery.add(queryKey, queryVals.get(0));
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
                orcidSolrResult.setEmail((String) solrDocument.getFieldValue(EMAIL_ADDRESS));
                orcidSolrResult.setFamilyName((String) solrDocument.getFieldValue(FAMILY_NAME));
                orcidSolrResult.setGivenNames((String) solrDocument.getFieldValue(GIVEN_NAMES));
                
                List<String> pastInstitutionNames = (List<String>) solrDocument.getFieldValue(AFFILIATE_PAST_INSTITUTION_NAMES);
                List<String> currentInstitutionNames = (List<String>) solrDocument.getFieldValue(AFFILIATE_CURRENT_INSTITUTION_NAME);
                
                List<String> institutionAffiliationNames = new ArrayList<>();
                if (currentInstitutionNames != null) {
                    institutionAffiliationNames.addAll(currentInstitutionNames);
                }
                if (pastInstitutionNames != null) {
                    institutionAffiliationNames.addAll(pastInstitutionNames);
                }
                orcidSolrResult.setInstitutionAffiliationNames(institutionAffiliationNames);
                
                String[] otherNames = (String[]) solrDocument.getFieldValue(OTHER_NAMES);
                if (otherNames != null) {
                    orcidSolrResult.setOtherNames(Arrays.asList(otherNames));
                }
                orcidSolrResultsList.add(orcidSolrResult);
            }
            orcidSolrResults.setNumFound(queryResponse.getResults().getNumFound());

        } catch (SolrServerException | IOException se) {
            throw new NonTransientDataAccessResourceException("Error retrieving from SOLR Server", se);
        }
        return orcidSolrResults;
    }
}
