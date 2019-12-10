package org.orcid.core.solr;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.orcid.utils.solr.entities.SolrConstants;

public class CSVSolrClient {

    private String baseUrl;

    private static final List<String> ALLOWED_FIELDS = Arrays.asList(SolrConstants.ORCID, SolrConstants.EMAIL_ADDRESS, SolrConstants.GIVEN_NAMES,
            SolrConstants.FAMILY_NAME, SolrConstants.GIVEN_AND_FAMILY_NAMES, SolrConstants.AFFILIATE_CURRENT_INSTITUTION_NAME,
            SolrConstants.AFFILIATE_PAST_INSTITUTION_NAMES, SolrConstants.CREDIT_NAME, SolrConstants.OTHER_NAMES);

    public CSVSolrClient(String url) {
        this.baseUrl = url;
    }

    public String findCSVByDocumentCriteria(Map<String, List<String>> queryMap) throws URISyntaxException, ClientProtocolException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            URIBuilder builder = new URIBuilder(baseUrl + "/select?");
            builder.setParameter("wt", "csv");
            builder.setParameter("q", queryMap.get("q") != null ? queryMap.get("q").get(0) : "");
            builder.setParameter("fl", getFieldList(queryMap.get("fl") != null ? queryMap.get("fl").get(0) : null));

            // add and filter other allowed params
            for (String key : queryMap.keySet()) {
                if (allowedParam(key, queryMap.get(key) != null ? queryMap.get(key).get(0) : null)) {
                    builder.setParameter(key, queryMap.get(key).get(0));
                }
            }

            HttpGet get = new HttpGet(builder.build());
            CloseableHttpResponse response = httpClient.execute(get);

            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    String result = EntityUtils.toString(entity);
                    return result;
                }
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }

        return null;
    }

    private boolean allowedParam(String key, String value) {
        // fl param not copied (see getFieldList filtering method)
        if ("fl".equals(key)) {
            return false;
        }
        
        // never nulls
        if (value == null) {
            return false;
        }
        return true;
    }

    // generate or filter specified fl param to only include allowed values
    private String getFieldList(String requestedFieldList) {
        String[] specifiedFields = new String[0];
        StringBuilder fl = new StringBuilder();
        if (requestedFieldList != null) {
            specifiedFields = requestedFieldList.split(",");
            for (String specifiedField : specifiedFields) {
                if (ALLOWED_FIELDS.contains(specifiedField)) {
                    fl.append(specifiedField).append(",");
                }
            }
            return fl.toString();
        } else {
            ALLOWED_FIELDS.forEach(s -> fl.append(s).append(","));
            return fl.toString();
        }

    }

}
