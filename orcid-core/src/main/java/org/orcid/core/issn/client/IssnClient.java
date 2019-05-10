package org.orcid.core.issn.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.issn.IssnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@Component
public class IssnClient {

    @Value("${org.orcid.core.issn.portal.url:https://portal.issn.org/resource/ISSN/%s?format=json}")
    private String url;

    private Client client = Client.create();

    private static final Logger LOG = LoggerFactory.getLogger(IssnClient.class);

    public IssnData getIssnData(String issn) {
        String json = null;
        try {
            json = getJsonDataFromIssnPortal(issn);
        } catch (IOException e) {
            throw new RuntimeException("Error extracting json from issn portal response", e);
        }
        try {
            return extractIssnData(json);
        } catch (JSONException e) {
            LOG.warn("Error extracting issn data from json returned from issn portal", e);
            return null;
        }
    }

    private IssnData extractIssnData(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("@graph");
        IssnData issnData = new IssnData();
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.getJSONObject(i).has("mainTitle")) {
                issnData.setMainTitle(jsonArray.getJSONObject(i).getString("mainTitle"));
                issnData.setIssn(jsonArray.getJSONObject(i).getString("issn"));
                return issnData;
            }
        }
        return null;
    }

    private String getJsonDataFromIssnPortal(String issn) throws IOException {
        String issnUrl = String.format(url, issn);
        WebResource resource = client.resource(issnUrl);
        ClientResponse response = resource.get(ClientResponse.class);
        int status = response.getStatus();
        if (status != 200) {
            return null;
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        InputStream input = response.getEntityInputStream();

        try {
            IOUtils.copy(response.getEntityInputStream(), output);
            return output.toString("UTF-8");
        } finally {
            input.close();
            output.close();
        }
    }

}
