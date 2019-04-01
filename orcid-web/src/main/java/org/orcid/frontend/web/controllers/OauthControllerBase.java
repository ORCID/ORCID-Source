package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.oauth.service.OrcidAuthorizationEndpoint;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.frontend.spring.OrcidWebAuthenticationDetails;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.OrcidStringUtils;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.orcid.pojo.ajaxForm.ScopeInfoForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.web.bind.annotation.ResponseBody;

@Deprecated
public class OauthControllerBase extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OauthControllerBase.class);    
    protected Pattern clientIdPattern = Pattern.compile("client_id=([^&]*)");
    protected Pattern scopesPattern = Pattern.compile("scope=([^&]*)");
    private Pattern redirectUriPattern = Pattern.compile("redirect_uri=([^&]*)");
    private Pattern responseTypePattern = Pattern.compile("response_type=([^&]*)");
    private Pattern stateParamPattern = Pattern.compile("state=([^&]*)");
    private Pattern orcidPattern = Pattern.compile("(&|\\?)orcid=([^&]*)");    
    private Pattern noncePattern = Pattern.compile("nonce=([^&]*)");
    private Pattern maxAgePattern = Pattern.compile("max_age=([^&]*)");
    protected static String PUBLIC_MEMBER_NAME = "PubApp";
    protected static String REDIRECT_URI_ERROR = "/oauth/error/redirect-uri-mismatch?client_id={0}";
            
    protected static String REQUEST_INFO_FORM = "requestInfoForm";

    @Resource
    protected ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    protected OrcidOAuth2RequestValidator orcidOAuth2RequestValidator;

    @Resource
    protected ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    protected AuthenticationManager authenticationManager;

    @Resource
    protected OrcidAuthorizationEndpoint authorizationEndpoint;
    
    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public OrcidAuthorizationEndpoint getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public void setAuthorizationEndpoint(OrcidAuthorizationEndpoint authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }

    protected @ResponseBody RequestInfoForm generateRequestInfoForm(HttpServletRequest request) throws UnsupportedEncodingException {
        String clientId = request.getParameter("client_id");
        String scopesString = request.getParameter("scope");
        String redirectUri = request.getParameter("redirect_uri");
        String responseType = request.getParameter("response_type");
        String stateParam = request.getParameter("state");
        String email = request.getParameter("email");
        String orcid = request.getParameter("orcid");
        String givenNames = request.getParameter("given_names");
        String familyNames = request.getParameter("family_names");
        String nonce = request.getParameter("nonce");
        String maxAge = request.getParameter("max_age");        
        return generateRequestInfoForm(clientId, scopesString, redirectUri, responseType, stateParam, email, orcid, givenNames, familyNames, nonce, maxAge);
    }
    
    protected @ResponseBody RequestInfoForm generateRequestInfoForm(String requestUrl) throws UnsupportedEncodingException {
        String clientId = "";
        String scopesString = "";
        String redirectUri = "";
        String responseType = "";
        String stateParam = "";
        String email = "";
        String orcid = "";
        String givenNames = "";
        String familyNames = "";
        
        String nonce = "";
        String maxAge = "";

        if (!PojoUtil.isEmpty(requestUrl)) {
            Matcher matcher = clientIdPattern.matcher(requestUrl);
            if (matcher.find()) {
                clientId = matcher.group(1);
            }
            Matcher scopeMatcher = scopesPattern.matcher(requestUrl);
            if (scopeMatcher.find()) {
                String scopes = scopeMatcher.group(1);
                scopesString = URLDecoder.decode(scopes, "UTF-8").trim();
                scopesString = scopesString.replaceAll(" +", " ");
            }
            Matcher redirectUriMatcher = redirectUriPattern.matcher(requestUrl);
            if (redirectUriMatcher.find()) {
                try {
                    redirectUri = OrcidStringUtils.stripHtml(URLDecoder.decode(redirectUriMatcher.group(1), "UTF-8").trim());
                } catch (UnsupportedEncodingException e) {
                }
            }

            Matcher responseTypeMatcher = responseTypePattern.matcher(requestUrl);
            if (responseTypeMatcher.find()) {
                try {
                    responseType = OrcidStringUtils.stripHtml(URLDecoder.decode(responseTypeMatcher.group(1), "UTF-8").trim());
                } catch (UnsupportedEncodingException e) {
                }
            }

            Matcher stateParamMatcher = stateParamPattern.matcher(requestUrl);
            if (stateParamMatcher.find()) {
                try {
                    stateParam = OrcidStringUtils.stripHtml(URLDecoder.decode(stateParamMatcher.group(1), "UTF-8").trim());
                } catch (UnsupportedEncodingException e) {}
            }
            
            Matcher emailMatcher = RegistrationController.emailPattern.matcher(requestUrl);
            if (emailMatcher.find()) {
                String tempEmail = emailMatcher.group(1);
                try {
                    tempEmail = OrcidStringUtils.stripHtml(URLDecoder.decode(tempEmail, "UTF-8").trim());
                } catch (UnsupportedEncodingException e) {
                }
                if (!PojoUtil.isEmpty(tempEmail)) {
                    email = tempEmail;
                }
            }

            Matcher orcidMatcher = orcidPattern.matcher(requestUrl);
            if (orcidMatcher.find()) {
                String tempOrcid = orcidMatcher.group(2);
                try {
                    tempOrcid = OrcidStringUtils.stripHtml(URLDecoder.decode(tempOrcid, "UTF-8").trim());
                } catch (UnsupportedEncodingException e) {
                }
                if (profileEntityManager.orcidExists(tempOrcid)) {
                    orcid = tempOrcid;
                }
            }
            
            Matcher givenNamesMatcher = RegistrationController.givenNamesPattern.matcher(requestUrl);
            if(givenNamesMatcher.find()) {
                givenNames = OrcidStringUtils.stripHtml(URLDecoder.decode(givenNamesMatcher.group(1), "UTF-8").trim());
            }
            
            Matcher familyNamesMatcher = RegistrationController.familyNamesPattern.matcher(requestUrl);
            if(familyNamesMatcher.find()) {
                familyNames = OrcidStringUtils.stripHtml(URLDecoder.decode(familyNamesMatcher.group(1), "UTF-8").trim());
            }
            
            Matcher nonceMatcher = noncePattern.matcher(requestUrl);
            if(nonceMatcher.find()) {
                nonce = OrcidStringUtils.stripHtml(URLDecoder.decode(nonceMatcher.group(1), "UTF-8").trim());
            }
            
            Matcher maxAgeMatcher = maxAgePattern.matcher(requestUrl);
            if(maxAgeMatcher.find()) {
                maxAge = OrcidStringUtils.stripHtml(URLDecoder.decode(maxAgeMatcher.group(1), "UTF-8").trim());
            }
            
            
        }        
        return generateRequestInfoForm(clientId, scopesString, redirectUri, responseType, stateParam, email, orcid, givenNames, familyNames, nonce, maxAge);
    }
    
    private RequestInfoForm generateRequestInfoForm(String clientId, String scopesString, String redirectUri, String responseType, String stateParam, String email, String orcid, String givenNames, String familyNames, String nonce, String maxAge) throws UnsupportedEncodingException {
        RequestInfoForm infoForm = new RequestInfoForm();
        
        //If the user is logged in 
        String loggedUserOrcid = getEffectiveUserOrcid();
        if(!PojoUtil.isEmpty(loggedUserOrcid)) {
            infoForm.setUserOrcid(loggedUserOrcid);
            
            ProfileEntity profile = profileEntityCacheManager.retrieve(loggedUserOrcid);
            String creditName = "";
            
            RecordNameEntity recordName = profile.getRecordNameEntity();
            if(recordName != null) {
                if (!PojoUtil.isEmpty(profile.getRecordNameEntity().getCreditName())) {
                    creditName = profile.getRecordNameEntity().getCreditName();
                } else {
                	    creditName = PojoUtil.isEmpty(profile.getRecordNameEntity().getGivenNames()) ? "": profile.getRecordNameEntity().getGivenNames();
                	    creditName += PojoUtil.isEmpty(profile.getRecordNameEntity().getFamilyName()) ? "": " " + profile.getRecordNameEntity().getFamilyName();
                	    creditName = creditName.trim();
                }
            } 
                                    
            if(!PojoUtil.isEmpty(creditName)) {
                infoForm.setUserName(URLDecoder.decode(creditName, "UTF-8").trim());
            }                        
        }        
        
        Set<ScopePathType> scopes = new HashSet<ScopePathType>();

        if (!PojoUtil.isEmpty(clientId) && !PojoUtil.isEmpty(scopesString)) {
            scopesString = URLDecoder.decode(scopesString, "UTF-8").trim();
            scopesString = scopesString.replaceAll(" +", " ");
            scopes = ScopePathType.getScopesFromSpaceSeparatedString(scopesString);
        } else {
            throw new InvalidRequestException("Unable to find parameters");
        }

        for (ScopePathType theScope : scopes) {
            ScopeInfoForm scopeInfoForm = new ScopeInfoForm();
            scopeInfoForm.setValue(theScope.value());
            scopeInfoForm.setName(theScope.name());
            try {
                scopeInfoForm.setDescription(getMessage(ScopePathType.class.getName() + '.' + theScope.name()));
                scopeInfoForm.setLongDescription(getMessage(ScopePathType.class.getName() + '.' + theScope.name() + ".longDesc"));
            } catch(NoSuchMessageException e) {
                LOGGER.warn("Unable to find key message for scope: " + theScope.name() + " " + theScope.value());
            }
            infoForm.getScopes().add(scopeInfoForm);
        }

        // Check if the client has persistent tokens enabled
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);
        if (clientDetails.isPersistentTokensEnabled()) {
            infoForm.setClientHavePersistentTokens(true);
        }

        // If client details is ok, continue
        String clientName = clientDetails.getClientName() == null ? "" : clientDetails.getClientName();
        String clientEmailRequestReason = clientDetails.getEmailAccessReason() == null ? "" : clientDetails.getEmailAccessReason();
        String clientDescription = clientDetails.getClientDescription() == null ? "" : clientDetails.getClientDescription();
        String memberName = "";

        // If client type is null it means it is a public client
        if (ClientType.PUBLIC_CLIENT.equals(clientDetails.getClientType())) {
            memberName = PUBLIC_MEMBER_NAME;
        } else if (!PojoUtil.isEmpty(clientDetails.getGroupProfileId())) {
            ProfileEntity groupProfile = profileEntityCacheManager.retrieve(clientDetails.getGroupProfileId());
            if(groupProfile.getRecordNameEntity() != null) {
                memberName = groupProfile.getRecordNameEntity().getCreditName();
            } 
        }
        // If the group name is empty, use the same as the client
        // name, since it should be a SSO user
        if (StringUtils.isBlank(memberName)) {
            memberName = clientName;
        }

        if(!PojoUtil.isEmpty(email) || !PojoUtil.isEmpty(orcid)) {                        
            // Check if orcid exists, if so, show login screen
            if(!PojoUtil.isEmpty(orcid)) {
                orcid = orcid.trim();
                if(profileEntityManager.orcidExists(orcid)) {
                    infoForm.setUserId(orcid);
                }
            } else {
                // Check if email exists, if so, show login screen
                if(!PojoUtil.isEmpty(email)) {
                    email = email.trim();
                    if(emailManager.emailExists(email)) {
                        infoForm.setUserId(email);
                    }
                }
            }
        }  
        
        infoForm.setUserEmail(email);
        if(PojoUtil.isEmpty(loggedUserOrcid))
            infoForm.setUserOrcid(orcid);
        infoForm.setUserGivenNames(givenNames);
        infoForm.setUserFamilyNames(familyNames);
        infoForm.setClientId(clientId);
        infoForm.setClientDescription(clientDescription);
        infoForm.setClientName(clientName);
        infoForm.setClientEmailRequestReason(clientEmailRequestReason);
        infoForm.setMemberName(memberName);
        infoForm.setRedirectUrl(redirectUri);
        infoForm.setStateParam(stateParam);
        infoForm.setResponseType(responseType);
        infoForm.setNonce(nonce);
        
        return infoForm;
    }
    
    protected void fillOauthParams(RequestInfoForm requestInfoForm, Map<String, String> params, Map<String, String> approvalParams, boolean userEnabledPersistentTokens, boolean allowEmailAccess) {
        if (requestInfoForm.containsEmailReadPrivateScope() && !allowEmailAccess) {
            requestInfoForm.removeEmailReadPrivateScope();
        }
        
        if (!PojoUtil.isEmpty(requestInfoForm.getScopesAsString())) {
            params.put(OrcidOauth2Constants.SCOPE_PARAM, requestInfoForm.getScopesAsString());
        }
        
        params.put(OrcidOauth2Constants.TOKEN_VERSION, OrcidOauth2Constants.PERSISTENT_TOKEN);                    
        params.put(OrcidOauth2Constants.CLIENT_ID_PARAM, requestInfoForm.getClientId());
        
        // Redirect URI
        if (!PojoUtil.isEmpty(requestInfoForm.getRedirectUrl())) {
            params.put(OrcidOauth2Constants.REDIRECT_URI_PARAM, requestInfoForm.getRedirectUrl());
        } else {
            params.put(OrcidOauth2Constants.REDIRECT_URI_PARAM, new String());
        }
        
        // Response type
        if (!PojoUtil.isEmpty(requestInfoForm.getResponseType())) {
            params.put(OrcidOauth2Constants.RESPONSE_TYPE_PARAM, requestInfoForm.getResponseType());
        }
        // State param
        if (!PojoUtil.isEmpty(requestInfoForm.getStateParam())) {
            params.put(OrcidOauth2Constants.STATE_PARAM, requestInfoForm.getStateParam());
        }
        
        // Set approval params               
        params.put(OAuth2Utils.USER_OAUTH_APPROVAL, "true");
        approvalParams.put(OAuth2Utils.USER_OAUTH_APPROVAL, "true");
        
        // Set persistent token flag
        if(requestInfoForm.getClientHavePersistentTokens() && userEnabledPersistentTokens) {
            params.put(OrcidOauth2Constants.GRANT_PERSISTENT_TOKEN, "true");
        } else {
            params.put(OrcidOauth2Constants.GRANT_PERSISTENT_TOKEN, "false");
        }
        
        //OpenID connect
        if (!PojoUtil.isEmpty(requestInfoForm.getNonce())){
            params.put(OrcidOauth2Constants.NONCE, requestInfoForm.getNonce());
        }
    }
    
    /**
     * Builds the redirect uri string to use when the user deny the request
     * 
     * @param redirectUri
     *            Redirect uri
     * @return the redirect uri string with the deny params
     */
    protected String buildDenyRedirectUri(String redirectUri, String stateParam) {
        if (!PojoUtil.isEmpty(redirectUri)) {
            if (redirectUri.contains("?")) {
                redirectUri = redirectUri.concat("&error=access_denied&error_description=User denied access");
            } else {
                redirectUri = redirectUri.concat("?error=access_denied&error_description=User denied access");
            }
        }
        if (!PojoUtil.isEmpty(stateParam))
            redirectUri += "&state=" + stateParam;
        return redirectUri;
    }

    protected void copy(Map<String, String[]> savedParams, Map<String, String> params) {
        if (savedParams != null && !savedParams.isEmpty()) {
            for (String key : savedParams.keySet()) {
                String[] values = savedParams.get(key);
                if (values != null && values.length > 0)
                    params.put(key, values[0]);
            }
        }
    }

    /*****************************
     * Authenticate user methods
     ****************************/
    protected Authentication authenticateUser(HttpServletRequest request, String email, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        token.setDetails(new OrcidWebAuthenticationDetails(request));
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    /**
     * Checks if the client has the persistent tokens enabled
     * 
     * @return true if the persistent tokens are enabled for that client
     * @throws IllegalArgumentException
     */
    protected boolean hasPersistenTokensEnabled(String clientId) throws IllegalArgumentException {
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);
        if (clientDetails == null)
            throw new IllegalArgumentException(getMessage("web.orcid.oauth_invalid_client.exception"));
        return clientDetails.isPersistentTokensEnabled();
    }
    
    protected String removeQueryStringParams(String queryString, String... params) {
        for (String param : params) {
            String keyValue = param + "=[^&]*?";
            queryString = queryString.replaceAll("(&" + keyValue + "(?=(&|$))|^" + keyValue + "(&|$))", "");
        }
        return queryString;
    }
}
