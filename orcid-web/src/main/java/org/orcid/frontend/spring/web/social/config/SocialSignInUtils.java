package org.orcid.frontend.spring.web.social.config;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.UserConnectionManager;

public class SocialSignInUtils {

    @Resource
    private UserConnectionManager userConnectionManager;

    @Resource
    private UserCookieGenerator userCookieGenerator;
    
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
}
