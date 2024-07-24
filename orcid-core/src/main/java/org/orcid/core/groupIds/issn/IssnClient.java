package org.orcid.core.groupIds.issn;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.Map;

import javax.annotation.Resource;

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
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("@graph");        
        if (jsonArray != null) {
            IssnData issnData = new IssnData();
            String name0 = null;
            // Look for mainTitle first
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (obj.has("@id")) {
                    String idName = obj.getString("@id");
                    if (idName.equals(RESOURCE_MAIN.replace("%issn", issn))) {
                        LOG.debug("Found main resource for " + issn);
                        // Look for the mainTitle
                        if (obj.has("mainTitle")) {                            
                            String title = obj.getString("mainTitle");
                            String cleanTitle = cleanText(title);
                            issnData.setMainTitle(cleanTitle);
                            LOG.debug("Found mainTitle for '" + issn + "' " + cleanTitle);
                            return issnData;
                        } else if (obj.has("name")) {
                            LOG.debug("Found name array for " + issn);
                            // If the mainTitle is not available, look for the
                            // name array
                            Object nameObject = jsonArray.getJSONObject(i).get("name");
                            if (nameObject instanceof JSONArray) {
                                String title = jsonArray.getJSONObject(i).getJSONArray("name").getString(0);
                                String cleanTitle = cleanText(title);
                                issnData.setMainTitle(cleanTitle);
                                LOG.debug("Found name[0] for '" + issn + "' " + cleanTitle);
                                // Save the name[0] in case we can't find the KeyTitle
                                name0 = cleanTitle;
                            } else if (nameObject instanceof String) {
                                String title = jsonArray.getJSONObject(i).getString("name");
                                String cleanTitle = cleanText(title);
                                issnData.setMainTitle(cleanTitle);
                                LOG.debug("Found name[0] for '" + issn + "' " + cleanTitle);
                                // Save the name[0] in case we can't find the KeyTitle
                                name0 = cleanTitle;
                            } else {
                                LOG.warn("Unable to extract name[0], it is not a string nor an array for " + issn);
                                throw new IllegalArgumentException("Unable to extract name[0], it is not a string nor an array for " + issn);
                            }
                        } else {
                            LOG.warn("Unable to extract name, couldn't find the mainTitle nor the name[0] for " + issn);
                            throw new IllegalArgumentException("Unable to extract name, couldn't find the mainTitle nor the name[0] for " + issn);
                        }
                    }
                }
            }
            
            // If mainTitle is not found, Look for the KeyTitle element
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (obj.has("@id")) {
                    String idName = obj.getString("@id");
                    if (idName.equals(RESOURCE_KEY_TITLE.replace("%issn", issn))) {                        
                        String title = obj.getString("value");
                        String cleanTitle = cleanText(title);
                        issnData.setMainTitle(cleanTitle);
                        LOG.debug("Found KeyTitle for '" + issn + "' " + cleanTitle);
                        return issnData;
                    }
                }
            }

            // If mainTitle and keyTitle are not available, return the name[0]
            if(StringUtils.isNotEmpty(name0)) {
                String cleanTitle = cleanText(name0);
                issnData.setMainTitle(cleanTitle);
                LOG.debug("Found name[0] for '" + issn + "' " + cleanTitle);
                return issnData;
            }
            
        }
        throw new IllegalArgumentException("Unable to extract name, couldn't find the Key Title nor the main resource for " + issn);
    }

    private String getJsonDataFromIssnPortal(String issn) throws TooManyRequestsException, UnexpectedResponseCodeException, IOException, InterruptedException, URISyntaxException {
        String issnUrl = issnPortalUrlBuilder.buildJsonIssnPortalUrlForIssn(issn);
        HttpResponse<String> response = httpRequestUtils.doGet(issnUrl);
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
