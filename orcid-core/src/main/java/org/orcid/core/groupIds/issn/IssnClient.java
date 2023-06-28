package org.orcid.core.groupIds.issn;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
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

    private static final String START_OF_STRING = "\u0098";
    private static final String STRING_TERMINATOR = "\u009C";

    @Resource
    private IssnPortalUrlBuilder issnPortalUrlBuilder;

    @Resource
    private HttpRequestUtils httpRequestUtils;

    public IssnData getIssnData(String issn) {
        if(StringUtils.isEmpty(issn)) {
            return null;
        }
        String json = null;
        try {
            LOG.debug("Extracting ISSN for " +  issn);
            // ensure any lower case x is X otherwise issn portal won't work
            json = getJsonDataFromIssnPortal(issn.toUpperCase());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            LOG.error("Error when getting the issn data from issn portal " + issn, e);
            return null;
        }
        try {
            if (json != null) {
                IssnData data = extractIssnData(json);
                data.setIssn(issn);
                return data;
            } else {
                return null;
            }
        } catch (Exception e) {
            LOG.warn("Error extracting issn data from json returned from issn portal "+ issn, e);
            return null;
        }
    }

    private IssnData extractIssnData(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("@graph");
        IssnData issnData = new IssnData();
        if (issnData != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).has("mainTitle")) {
                    String title = jsonArray.getJSONObject(i).getString("mainTitle");
                    String cleanTitle = cleanText(title);
                    issnData.setMainTitle(cleanTitle);
                    return issnData;
                } 
            }
            // If we reach this point it means the mainTitle was not available.
            // Lets iterate again now looking for key
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).has("name")) {
                    try {
                        String title = jsonArray.getJSONObject(i).getString("name");                        
                        issnData.setMainTitle(cleanText(title));
                    } catch (JSONException e) {
                        // may be an array
                        try {
                            String title = jsonArray.getJSONObject(i).getJSONArray("name").getString(0);
                            issnData.setMainTitle(cleanText(title));
                        } catch(Exception ee) {
                            // Nothing else to try, propagate the exception
                            throw ee;
                        }
                    }
                    return issnData;
                }
            }
        }
        return null;
    }

    private String getJsonDataFromIssnPortal(String issn) throws IOException, InterruptedException, URISyntaxException {
        String issnUrl = issnPortalUrlBuilder.buildJsonIssnPortalUrlForIssn(issn);
        HttpResponse<String> response = httpRequestUtils.doGet(issnUrl);
        if (response.statusCode() != 200) {
            return null;
        }
        return response.body();
    }

    private String cleanText(String text) {
        return text.replaceAll("\\p{C}", "").replaceAll(START_OF_STRING, "").replaceAll(STRING_TERMINATOR, "");
    }

}
