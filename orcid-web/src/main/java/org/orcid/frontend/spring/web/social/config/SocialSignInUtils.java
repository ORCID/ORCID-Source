package org.orcid.frontend.spring.web.social.config;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.UserConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.RedirectView;

public class SocialSignInUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocialSignInUtils.class);

    @Resource
    private UserConnectionManager userConnectionManager;

    @Resource
    private UserCookieGenerator userCookieGenerator;

    private final String facebookOauthUrl;

    private final String facebookTokenExchangeUrl;

    private final String facebookUserInfoEndpoint;

    private final String googleOauthUrl;

    private final String googleUserInfoUrl;

    private final String googleTokenExchangeUrl;

    private final String googleFormParams;

    public SocialSignInUtils(@Value("${org.orcid.social.fb.key}") String fbKey, @Value("${org.orcid.social.fb.secret}") String fbSecret,
            @Value("${org.orcid.social.fb.redirectUri}") String fbRedirectUri, @Value("${org.orcid.social.gg.key}") String gKey,
            @Value("${org.orcid.social.gg.secret}") String gSecret, @Value("${org.orcid.core.baseUri}") String baseUri)
            throws MalformedURLException, IOException, JSONException {
        facebookOauthUrl = "https://www.facebook.com/v3.3/dialog/oauth?client_id=" + fbKey + "&redirect_uri=" + fbRedirectUri + "&scope=email&state={state_param}";
        facebookTokenExchangeUrl = "https://graph.facebook.com/v3.3/oauth/access_token?client_id=" + fbKey + "&redirect_uri=" + fbRedirectUri + "&client_secret="
                + fbSecret + "&code={code}";
        facebookUserInfoEndpoint = "https://graph.facebook.com/me?access_token={access-token}&fields=id,email,name,first_name,last_name";

        String googleRedirectUrl = baseUri + "/signin/google";
        String googleTokenEndpoint = "https://www.googleapis.com/oauth2/v4/token";
        String googleUserInfoEndpoint = null;
        // Find google token endpoint
        HttpURLConnection con = (HttpURLConnection) new URL("https://accounts.google.com/.well-known/openid-configuration").openConnection();
        con.setRequestProperty("User-Agent", con.getRequestProperty("User-Agent") + " (orcid.org)");
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8.name()));
            StringBuffer accessTokenResponse = new StringBuffer();
            in.lines().forEach(i -> accessTokenResponse.append(i));
            in.close();
            // Read JSON response and print
            JSONObject googleConfig = new JSONObject(accessTokenResponse.toString());
            if (googleConfig.has("token_endpoint")) {
                googleTokenEndpoint = googleConfig.getString("token_endpoint");
            } else {
                // Use not recommended default token endpoint
                LOGGER.warn("Unable to fetch google token endpoing, using default one");
            }

            if (googleConfig.has("userinfo_endpoint")) {
                googleUserInfoEndpoint = googleConfig.getString("userinfo_endpoint");
            }
        } else {
            // Use not recommended default token endpoint
            LOGGER.warn("Unable to fetch google token endpoing, using default one");
        }

        googleOauthUrl = "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + gKey + "&response_type=code&scope=openid%20email%20profile&redirect_uri="
                + googleRedirectUrl + "&state={state_param}";
        googleTokenExchangeUrl = googleTokenEndpoint;
        googleFormParams = "code={code}&client_id=" + gKey + "&client_secret=" + gSecret + "&redirect_uri=" + googleRedirectUrl + "&grant_type=authorization_code";
        googleUserInfoUrl = googleUserInfoEndpoint;
    }

    public RedirectView initFacebookLogin(String sessionState) {
        return new RedirectView(facebookOauthUrl.replace("{state_param}", sessionState));
    }

    public RedirectView initGoogleLogin(String sessionState) {
        return new RedirectView(googleOauthUrl.replace("{state_param}", sessionState));
    }

    public void setSignedInData(HttpServletRequest request, JSONObject userData) throws JSONException {
        String providerUserId = userData.getString(OrcidOauth2Constants.PROVIDER_USER_ID);
        request.getSession().setAttribute(OrcidOauth2Constants.SOCIAL_SESSION_ATT_NAME + providerUserId, userData.toString());
    }

    public Map<String, String> getSignedInData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> data = null;
        String userConnectionId = userCookieGenerator.readCookieValue(request);
        if (!StringUtils.isBlank(userConnectionId)) {
            try {
                data = userConnectionManager.getUserConnectionInfo(userConnectionId);
                data.put(OrcidOauth2Constants.USER_CONNECTION_ID, userConnectionId);
                // Set first name and last name from session when available
                String sessionStoredData = (String) request.getSession()
                        .getAttribute(OrcidOauth2Constants.SOCIAL_SESSION_ATT_NAME + data.get(OrcidOauth2Constants.PROVIDER_USER_ID));
                JSONObject json = new JSONObject(sessionStoredData);
                data.put("firstName", json.getString(OrcidOauth2Constants.FIRST_NAME));
                data.put("lastName", json.getString(OrcidOauth2Constants.LAST_NAME));
            } catch (Exception e) {
                userCookieGenerator.removeCookie(response);
            }
        }

        return data;
    }

    public JSONObject getFacebookUserData(String code) throws IOException, JSONException {
        JSONObject userInfoJson = new JSONObject();
        // Exchange the code for an access token
        HttpURLConnection con = (HttpURLConnection) new URL(facebookTokenExchangeUrl.replace("{code}", code)).openConnection();
        con.setRequestProperty("User-Agent", con.getRequestProperty("User-Agent") + " (orcid.org)");
        con.setRequestMethod("GET");
        con.setInstanceFollowRedirects(true);
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8.name()));
            StringBuffer accessTokenResponse = new StringBuffer();
            in.lines().forEach(i -> accessTokenResponse.append(i));
            in.close();
            // Read JSON response and print
            JSONObject tokenJson = new JSONObject(accessTokenResponse.toString());
            // Get user info from Facebook
            String accessToken = tokenJson.getString("access_token");
            Long expiresIn = tokenJson.getLong("expires_in");
            userInfoJson.put(OrcidOauth2Constants.ACCESS_TOKEN, accessToken);
            userInfoJson.put(OrcidOauth2Constants.EXPIRES_IN, expiresIn);
            con = (HttpURLConnection) new URL(facebookUserInfoEndpoint.replace("{access-token}", accessToken)).openConnection();
            con.setRequestProperty("User-Agent", con.getRequestProperty("User-Agent") + " (orcid.org)");
            con.setRequestMethod("GET");
            con.setInstanceFollowRedirects(true);
            int userInfoResponseCode = con.getResponseCode();
            if (userInfoResponseCode == HttpURLConnection.HTTP_OK) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8.name()));
                StringBuffer userInfoResponse = new StringBuffer();
                in.lines().forEach(i -> userInfoResponse.append(i));
                in.close();
                JSONObject userDetailsJson = new JSONObject(userInfoResponse.toString());
                userInfoJson.put(OrcidOauth2Constants.PROVIDER_USER_ID, userDetailsJson.get("id"));
                userInfoJson.put(OrcidOauth2Constants.EMAIL, userDetailsJson.get("email"));
                userInfoJson.put(OrcidOauth2Constants.DISPLAY_NAME, userDetailsJson.get("name"));
                userInfoJson.put(OrcidOauth2Constants.FIRST_NAME, userDetailsJson.get("first_name"));
                userInfoJson.put(OrcidOauth2Constants.LAST_NAME, userDetailsJson.get("last_name"));
            }
        }
        return userInfoJson;
    }

    public JSONObject getGoogleUserData(String code) throws IOException, JSONException {
        JSONObject userInfoJson = new JSONObject();
        String formParamsWithCode = googleFormParams.replace("{code}", code);
        byte[] postData = formParamsWithCode.getBytes(StandardCharsets.UTF_8);
        String length = String.valueOf(postData.length);

        HttpURLConnection con = (HttpURLConnection) new URL(googleTokenExchangeUrl).openConnection();
        con.setRequestProperty("User-Agent", con.getRequestProperty("User-Agent") + " (orcid.org)");
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Content-Length", length);
        con.setDoOutput(true);
        con.setInstanceFollowRedirects(false);
        con.setRequestProperty("charset", "utf-8");
        con.setUseCaches(false);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(postData);
        }
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8.name()));
            StringBuffer accessTokenResponse = new StringBuffer();
            in.lines().forEach(i -> accessTokenResponse.append(i));
            in.close();
            // Read JSON response and print
            JSONObject tokenJson = new JSONObject(accessTokenResponse.toString());
            String accessToken = tokenJson.getString("access_token");
            String idToken = tokenJson.getString("id_token");
            Long expiresIn = tokenJson.getLong("expires_in");
            String[] base64EncodedSegments = idToken.split("\\.");

            String base64EncodedClaims = base64EncodedSegments[1];

            String tokenClaims = new String(Base64.decodeBase64(base64EncodedClaims));
            JSONObject jsonClaims = new JSONObject(tokenClaims);
            String userEmail = jsonClaims.getString("email");
            String providerUserId = jsonClaims.getString("sub");
            String userName = jsonClaims.getString("name");
            String firstName = jsonClaims.getString("given_name");
            String lastName = jsonClaims.getString("family_name");

            userInfoJson.put(OrcidOauth2Constants.ACCESS_TOKEN, accessToken);
            userInfoJson.put(OrcidOauth2Constants.EXPIRES_IN, expiresIn);
            userInfoJson.put(OrcidOauth2Constants.PROVIDER_USER_ID, providerUserId);
            userInfoJson.put(OrcidOauth2Constants.EMAIL, userEmail);
            userInfoJson.put(OrcidOauth2Constants.DISPLAY_NAME, userName);
            if (!StringUtils.isBlank(firstName)) {
                userInfoJson.put(OrcidOauth2Constants.FIRST_NAME, firstName);
            }
            if (!StringUtils.isBlank(lastName)) {
                userInfoJson.put(OrcidOauth2Constants.LAST_NAME, lastName);
            }

            if (StringUtils.isBlank(firstName) || StringUtils.isBlank(lastName)) {
                // Fetch user's first and last name
                HttpURLConnection googleUserInfoUrlConnection = (HttpURLConnection) new URL(googleUserInfoUrl).openConnection();
                googleUserInfoUrlConnection.setRequestMethod("GET");
                googleUserInfoUrlConnection.setRequestProperty("User-Agent", con.getRequestProperty("User-Agent") + " (orcid.org)");
                googleUserInfoUrlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                googleUserInfoUrlConnection.setInstanceFollowRedirects(true);
                int googleUserInfoUrlResponseCode = googleUserInfoUrlConnection.getResponseCode();
                if (googleUserInfoUrlResponseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader googleUserInfoUrlConnectionIn = new BufferedReader(
                            new InputStreamReader(googleUserInfoUrlConnection.getInputStream(), StandardCharsets.UTF_8.name()));
                    StringBuffer googleUserInfoResponse = new StringBuffer();
                    googleUserInfoUrlConnectionIn.lines().forEach(i -> googleUserInfoResponse.append(i));
                    googleUserInfoUrlConnectionIn.close();
                    // Read JSON response
                    JSONObject googleUserInfoJson = new JSONObject(googleUserInfoResponse.toString());
                    userInfoJson.put(OrcidOauth2Constants.FIRST_NAME, googleUserInfoJson.get("given_name"));
                    userInfoJson.put(OrcidOauth2Constants.LAST_NAME, googleUserInfoJson.get("family_name"));
                }
            }
        }
        return userInfoJson;
    }
}
