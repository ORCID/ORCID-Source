package org.orcid.core.groupIds.issn;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.utils.http.HttpRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IssnClient {

    private static final Logger LOG = LoggerFactory.getLogger(IssnClient.class);
    
    private static final String START_OF_STRING="\u0098";
    private static final String STRING_TERMINATOR = "\u009C";
    
    @Resource
    private IssnPortalUrlBuilder issnPortalUrlBuilder;
    
    @Resource
    private HttpRequestUtils httpRequestUtils;

    public IssnData getIssnData(String issn) {
        String json = null;
        try {
            // ensure any lower case x is X otherwise issn portal won't work
            json = getJsonDataFromIssnPortal(issn.toUpperCase());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException("Error extracting json from issn portal response", e);
        } 
        try {
            IssnData data = extractIssnData(json);
            data.setIssn(issn);
            return data;
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
                return issnData;
            } else if (jsonArray.getJSONObject(i).has("name")) {
                // name and mainTitle always in same object - therefore if no mainTitle but name present, no mainTitle in data
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

    private String getJsonDataFromIssnPortal(String issn) throws IOException, InterruptedException, URISyntaxException {
        String issnUrl = issnPortalUrlBuilder.buildJsonIssnPortalUrlForIssn(issn);        
        HttpResponse<String> response = httpRequestUtils.doGet(issnUrl);
        if(response.statusCode() != 200) {
            return null;
        }
        return response.body();
    }
    
    private String cleanText(String text) {
        return text.replaceAll("\\p{C}", "")
                .replaceAll(START_OF_STRING,"")
                .replaceAll(STRING_TERMINATOR, "");
    }
    
}
