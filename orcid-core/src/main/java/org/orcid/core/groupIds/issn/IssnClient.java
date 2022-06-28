package org.orcid.core.groupIds.issn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.utils.rest.RESTHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.core.Response;

@Component
public class IssnClient {

    private static final Logger LOG = LoggerFactory.getLogger(IssnClient.class);

    @Resource
    private RESTHelper httpHelper;

    @Resource
    private IssnPortalUrlBuilder issnPortalUrlBuilder;

    public IssnData getIssnData(String issn) {
        String json = null;
        try {
            // ensure any lower case x is X otherwise issn portal won't work
            json = getJsonDataFromIssnPortal(issn.toUpperCase());
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
                String title = jsonArray.getJSONObject(i).getString("mainTitle");
                String cleanTitle = cleanText(title);
                issnData.setMainTitle(cleanTitle);
                issnData.setIssn(jsonArray.getJSONObject(i).getString("issn"));
                return issnData;
            } else if (jsonArray.getJSONObject(i).has("name")) {
                // name and mainTitle always in same object - therefore if no mainTitle but name present, no mainTitle in data
                issnData.setIssn(jsonArray.getJSONObject(i).getString("issn"));
                
                try {
                    issnData.setMainTitle(jsonArray.getJSONObject(i).getJSONArray("name").getString(0));
                } catch (JSONException e) {
                    // may not be an array
                    issnData.setMainTitle(jsonArray.getJSONObject(i).getString("name"));
                }
                return issnData;
            }
        }
        return null;
    }

    private String getJsonDataFromIssnPortal(String issn) throws IOException {
        String issnUrl = issnPortalUrlBuilder.buildJsonIssnPortalUrlForIssn(issn);
        
        Response response = httpHelper.executeGetRequest(issnUrl);
        int status = response.getStatus();
        if (status != 200) {
            return null;
        }

        return response.reade
        
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
    
    private String cleanText(String text) {
        return text.replaceAll("\\p{C}", "");
    }
    
}
