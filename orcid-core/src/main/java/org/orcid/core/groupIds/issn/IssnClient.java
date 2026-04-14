package org.orcid.core.groupIds.issn;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.eclipse.jetty.http.HttpStatus;
import org.orcid.core.exception.BannedException;
import org.orcid.core.exception.TooManyRequestsException;
import org.orcid.core.exception.UnexpectedResponseCodeException;
import org.orcid.core.utils.http.HttpRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IssnClient {

    private static final Logger LOG = LoggerFactory.getLogger(IssnClient.class);

    private static final String START_OF_STRING = "\u0098";
    private static final String STRING_TERMINATOR = "\u009C";

    private static final String RESOURCE_MAIN = "resource/ISSN/%issn";
    private static final String RESOURCE_KEY_TITLE = "resource/ISSN/%issn#KeyTitle";    
    
    @Resource
    private IssnPortalUrlBuilder issnPortalUrlBuilder;

    @Resource
    private HttpRequestUtils httpRequestUtils;

    public IssnData getIssnData(String issn) throws BannedException, TooManyRequestsException, UnexpectedResponseCodeException, IOException, URISyntaxException, InterruptedException, JSONException {
        if(StringUtils.isEmpty(issn)) {
            return null;
        }
        LOG.debug("Extracting ISSN for " +  issn);
        String json = getJsonDataFromIssnPortal(issn.toUpperCase());
        try {
            IssnData data = extractIssnData(issn.toUpperCase(), json);
            data.setIssn(issn);
            return data;
        } catch (JSONException e) {
            LOG.warn("Error extracting issn data from json returned from issn portal "+ issn);
            if(json == null) {
                return null;
            } else if(json.contains("you have been banned")) {
                throw new BannedException();
            } else {
                throw e;
            }
        } catch (Exception e) {
            LOG.warn("Error extracting issn data from json returned from issn portal "+ issn);
            return null;
        }
    }

    private IssnData extractIssnData(String issn, String json) throws JSONException {
        LOG.info("Extracting json data from " + issn);
        JSONObject rootObj = new JSONObject(json);
        IssnData issnData = new IssnData();

        // 1. Try to get mainTitle directly from the root
        if (rootObj.has("mainTitle")) {
            String title = rootObj.getString("mainTitle");
            String cleanTitle = cleanText(title);
            issnData.setMainTitle(cleanTitle);
            LOG.debug("Found mainTitle for '" + issn + "' " + cleanTitle);
            return issnData;
        }

        // 2. Try to get the KeyTitle from the identifiedBy block
        if (rootObj.has("identifiedBy")) {
            JSONObject identifiedBy = rootObj.getJSONObject("identifiedBy");
            if (identifiedBy.has("#KeyTitle")) {
                JSONObject keyTitleObj = identifiedBy.getJSONObject("#KeyTitle");
                if (keyTitleObj.has("value")) {
                    String title = keyTitleObj.getString("value");
                    String cleanTitle = cleanText(title);
                    issnData.setMainTitle(cleanTitle);
                    LOG.debug("Found KeyTitle for '" + issn + "' " + cleanTitle);
                    return issnData;
                }
            }
        }

        // 3. Fallback to the 'name' attribute (which can be a String or an Array)
        if (rootObj.has("name")) {
            Object nameObject = rootObj.get("name");
            String title = null;

            if (nameObject instanceof JSONArray) {
                // Grab the first element of the array
                JSONArray nameArray = (JSONArray) nameObject;
                if (nameArray.length() > 0) {
                    title = nameArray.getString(0);
                }
            } else if (nameObject instanceof String) {
                title = (String) nameObject;
            }

            if (title != null && !title.isEmpty()) {
                String cleanTitle = cleanText(title);
                issnData.setMainTitle(cleanTitle);
                LOG.debug("Found name for '" + issn + "' " + cleanTitle);
                return issnData;
            }
        }

        LOG.warn("Unable to extract name, couldn't find the mainTitle, KeyTitle, or name array for " + issn);
        throw new IllegalArgumentException("Unable to extract name from JSON for " + issn);
    }

    private String getJsonDataFromIssnPortal(String issn) throws TooManyRequestsException, UnexpectedResponseCodeException, IOException, InterruptedException, URISyntaxException {
        String issnUrl = issnPortalUrlBuilder.buildIssnPortalUrlForIssn(issn);
        HttpResponse<String> response = httpRequestUtils.doGet(issnUrl, "application/ld+json, application/json", HttpClient.Redirect.NORMAL);
        if (response.statusCode() != HttpStatus.OK_200) {
            if(response.statusCode() == HttpStatus.TOO_MANY_REQUESTS_429) {
                throw new TooManyRequestsException();
            } else {
                LOG.warn(issnUrl + " returned status code " + response.statusCode());
                throw new UnexpectedResponseCodeException(response.statusCode());
            }
        }
        return response.body();
    }

    private String cleanText(String text) {
        return text.replaceAll("\\p{C}", "").replaceAll(START_OF_STRING, "").replaceAll(STRING_TERMINATOR, "");
    }

}
