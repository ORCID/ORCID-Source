package org.orcid.core.manager.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.InstitutionalSignInManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.utils.JsonUtils;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.UserConnectionStatus;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;
import org.orcid.pojo.HeaderCheckResult;
import org.orcid.pojo.HeaderMismatch;
import org.orcid.pojo.RemoteUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

public class InstitutionalSignInManagerImpl implements InstitutionalSignInManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstitutionalSignInManagerImpl.class);

    private static final String SEPARATOR = ";";

    private static final Pattern ATTRIBUTE_SEPARATOR_PATTERN = Pattern.compile("(?<!\\\\)" + SEPARATOR);

    private static final Pattern ESCAPED_SEPARATOR_PATTERN = Pattern.compile("\\\\" + SEPARATOR);

    @Resource
    protected UserConnectionDao userConnectionDao;

    @Resource
    protected OrcidUrlManager orcidUrlManager;

    @Resource
    protected ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    protected OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    private final Map<String, String> institutionNames = new HashMap<>();

    public InstitutionalSignInManagerImpl(@Value("${org.orcid.shibboleth.discoFeedSource:https://orcid.org/Shibboleth.sso/DiscoFeed}") String discoFeedSource) {
        // Init the institution names map
        try {
            LOGGER.info("Populating institution names from DiscoFeed");
            populateInsitutionNames(discoFeedSource);
            LOGGER.info("Institution names populated");
        } catch (IOException | InterruptedException | JSONException e) {
            LOGGER.error("Error populating institution names from DiscoFeed, institutional linking email might not contain accurate data", e);
        }
    }

    @Override
    @Transactional
    public void createUserConnectionAndNotify(String idType, String remoteUserId, String displayName, String providerId, String userOrcid, Map<String, String> headers)
            throws UnsupportedEncodingException {
        UserconnectionEntity userConnectionEntity = userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(remoteUserId, providerId, idType);
        if (userConnectionEntity == null) {
            LOGGER.info("No user connection found for idType={}, remoteUserId={}, displayName={}, providerId={}, userOrcid={}",
                    new Object[] { idType, remoteUserId, displayName, providerId, userOrcid });
            userConnectionEntity = new UserconnectionEntity();
            String randomId = Long.toString(new Random(Calendar.getInstance().getTimeInMillis()).nextLong());
            UserconnectionPK pk = new UserconnectionPK(randomId, providerId, remoteUserId);
            userConnectionEntity.setOrcid(userOrcid);
            userConnectionEntity.setProfileurl(orcidUrlManager.getBaseUriHttp() + "/" + userOrcid);
            userConnectionEntity.setDisplayname(displayName);
            userConnectionEntity.setRank(1);
            userConnectionEntity.setId(pk);
            userConnectionEntity.setLinked(true);
            userConnectionEntity.setLastLogin(new Date());
            userConnectionEntity.setIdType(idType);
            userConnectionEntity.setConnectionSatus(UserConnectionStatus.NOTIFIED);
            userConnectionEntity.setHeadersJson(JsonUtils.convertToJsonString(headers));
            userConnectionDao.persist(userConnectionEntity);
        } else {
            LOGGER.info("Found existing user connection, {}", userConnectionEntity);
        }

        sendNotification(userOrcid, providerId);
    }

    @Override
    public void sendNotification(String userOrcid, String providerId) throws UnsupportedEncodingException {
        try {
            // Add the acknowledgement notification if the user doesn't know about the client yet
            ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieveByIdP(providerId);
            boolean clientKnowsUser = orcidOauth2TokenDetailService.doesClientKnowUser(clientDetails.getClientId(), userOrcid);
            // If the client doesn't know about the user yet, send a
            // notification
            if (!clientKnowsUser) {
                notificationManager.sendAcknowledgeMessage(userOrcid, clientDetails.getClientId());
            }
        } catch (IllegalArgumentException e) {
            // The provided IdP hasn't not been linked to any client yet.
        }
    }

    @Override
    public HeaderCheckResult checkHeaders(Map<String, String> originalHeaders, Map<String, String> currentHeaders) {
        HeaderCheckResult result = new HeaderCheckResult();
        List<String> headersToCheck = new ArrayList<>();
        headersToCheck.addAll(Arrays.asList(POSSIBLE_REMOTE_USER_HEADERS));
        headersToCheck.add(EPPN_HEADER);
        for (String headerName : headersToCheck) {
            String original = originalHeaders.get(headerName);
            String current = currentHeaders.get(headerName);
            // Only compare where both are not blank, because otherwise could
            // just be an IdP config change to add/remove the attribute
            if (StringUtils.isNoneBlank(original, current)) {
                Set<String> originalDeduped = dedupe(original);
                Set<String> currentDeduped = dedupe(current);
                if (!currentDeduped.equals(originalDeduped)) {
                    result.addMismatch(new HeaderMismatch(headerName, original, current));
                }
            }
        }
        if (!result.isSuccess()) {
            String message = String.format("Institutional sign in header check failed: %s, originalHeaders=%s", result, originalHeaders);
            LOGGER.info(message);
        }
        return result;
    }

    private Set<String> dedupe(String headerValue) {
        String[] values = ATTRIBUTE_SEPARATOR_PATTERN.split(headerValue);
        Set<String> deduped = new HashSet<>();
        for (String value : values) {
            deduped.add(value);
        }
        return deduped;
    }

    @Override
    public RemoteUser retrieveRemoteUser(Map<String, String> headers) {
        for (String possibleHeader : InstitutionalSignInManager.POSSIBLE_REMOTE_USER_HEADERS) {
            String userId = extractFirst(headers.get(possibleHeader));
            if (userId != null) {
                return new RemoteUser(userId, possibleHeader);
            }
        }
        return null;
    }

    @Override
    public String retrieveDisplayName(Map<String, String> headers) {
        String eppn = extractFirst(headers.get(InstitutionalSignInManager.EPPN_HEADER));
        if (StringUtils.isNotBlank(eppn)) {
            return eppn;
        }        
        return null;
    }
    
    @Override
    public String retrieveFirstName(Map<String, String> headers) {
        String givenName = extractFirst(headers.get(InstitutionalSignInManager.GIVEN_NAME_HEADER));
        return givenName != null ? givenName : "";
    }

    @Override
    public String retrieveLastName(Map<String, String> headers) {
        String lastName = extractFirst(headers.get(InstitutionalSignInManager.SN_HEADER));
        return lastName != null ? lastName : "";
    }

    /**
     * Shibboleth SP combines multiple values by concatenating, using semicolon
     * as the separator (the escape character is '\'). Mutliple values will be
     * provided, even if it is actually the same attribute in mace and oid
     * format.
     * 
     * @param headerValue
     * @return the first attribute value
     */
    private static String extractFirst(String headerValue) {
        if (headerValue == null) {
            return null;
        }
        String[] values = ATTRIBUTE_SEPARATOR_PATTERN.split(headerValue);
        return values.length > 0 ? ESCAPED_SEPARATOR_PATTERN.matcher(values[0]).replaceAll(SEPARATOR) : "";
    }

    public String getInstitutionName(String providerId) {
        return institutionNames.get(providerId);
    }

    private void populateInsitutionNames(String discoFeedSource) throws IOException, InterruptedException, JSONException {
        // 1. Build and send the HTTP GET request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(discoFeedSource))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Ensure we got a successful response
        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch DiscoFeed. HTTP Status: " + response.statusCode());
        }

        // 2. Parse the JSON array
        JSONArray discoFeedArray = new JSONArray(response.body());

        // 3. Iterate through the array and extract the required fields
        for (int i = 0; i < discoFeedArray.length(); i++) {
            JSONObject idp = discoFeedArray.getJSONObject(i);

            if (idp.has("entityID")) {
                String entityID = idp.getString("entityID");
                String displayName = "Unknown Institution"; // Fallback name

                // Safely navigate the DisplayNames array
                if (idp.has("DisplayNames")) {
                    JSONArray displayNamesArray = idp.getJSONArray("DisplayNames");

                    if (displayNamesArray.length() > 0) {
                        JSONObject firstDisplayName = displayNamesArray.getJSONObject(0);
                        if (firstDisplayName.has("value")) {
                            displayName = firstDisplayName.getString("value");
                        }
                    }
                }

                institutionNames.put(entityID, displayName);
            }
        }
    }
}