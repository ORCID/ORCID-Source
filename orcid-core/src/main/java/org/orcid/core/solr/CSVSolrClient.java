package org.orcid.core.solr;

import java.io.IOException;
import java.net.URISyntaxException;
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

public class CSVSolrClient {

    private String baseUrl;

    public CSVSolrClient(String url) {
        this.baseUrl = url;
    }

    public String findCSVByDocumentCriteria(Map<String, List<String>> queryMap) throws URISyntaxException, ClientProtocolException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            URIBuilder builder = new URIBuilder(baseUrl + "/select?");
            builder.setParameter("wt", "csv");
            builder.setParameter("q", queryMap.get("q") != null ? queryMap.get("q").get(0) : "");

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
        // never nulls
        if (value == null) {
            return false;
        }
        return true;
    }

}
