package org.orcid.frontend.web.controllers.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.frontend.web.controllers.BaseControllerUtil;
import org.orcid.frontend.web.controllers.RegistrationController;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.orcid.pojo.ajaxForm.ScopeInfoForm;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
public class OauthHelper {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OauthHelper.class);   
    public static final String PUBLIC_MEMBER_NAME = "PubApp";
    
    private final Pattern redirectUriPattern = Pattern.compile("redirect_uri=([^&]*)");
    private final Pattern responseTypePattern = Pattern.compile("response_type=([^&]*)");
    private final Pattern stateParamPattern = Pattern.compile("state=([^&]*)");
    private final Pattern orcidPattern = Pattern.compile("(&|\\?)orcid=([^&]*)");    
    private final Pattern noncePattern = Pattern.compile("nonce=([^&]*)");
    private final Pattern maxAgePattern = Pattern.compile("max_age=([^&]*)");
    private final Pattern clientIdPattern = Pattern.compile("client_id=([^&]*)");
    private final Pattern scopesPattern = Pattern.compile("scope=([^&]*)");
    
    private BaseControllerUtil baseControllerUtil = new BaseControllerUtil();
    
    @Resource(name = "profileEntityManagerV3")
    protected ProfileEntityManager profileEntityManager;
    
    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManagerReadOnly;
    
    @Resource
    protected LocaleManager localeManager;
    
    @Resource
    protected ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    @Resource(name = "emailManagerV3")
    protected EmailManager emailManager;      
    
    public RequestInfoForm generateRequestInfoForm(String requestUrl) throws UnsupportedEncodingException {
        RequestInfoForm infoForm = new RequestInfoForm();
        
        //If the user is logged in 
        String loggedUserOrcid = getEffectiveUserOrcid();
        if(!PojoUtil.isEmpty(loggedUserOrcid)) {
            infoForm.setUserOrcid(loggedUserOrcid);
            
            String creditName =  recordNameManagerReadOnly.fetchDisplayableCreditName(loggedUserOrcid);
            
            if(!PojoUtil.isEmpty(creditName)) {
                infoForm.setUserName(URLDecoder.decode(creditName, "UTF-8").trim());
            }                        
        }
        
        if (!PojoUtil.isEmpty(requestUrl)) {
            Matcher matcher = clientIdPattern.matcher(requestUrl);
            if (matcher.find()) {
                String clientId = matcher.group(1);
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
                    Name name = recordNameManagerReadOnly.getRecordName(clientDetails.getGroupProfileId());
                    if(name != null) {
                        memberName = name.getCreditName() != null ? name.getCreditName().getContent() : "";
                    } 
                }
                
                // If the group name is empty, use the same as the client
                // name, since it should be a SSO user
                if (StringUtils.isBlank(memberName)) {
                    memberName = clientName;
                }
                infoForm.setClientId(clientId);
                infoForm.setClientDescription(clientDescription);
                infoForm.setClientName(clientName);
                infoForm.setClientEmailRequestReason(clientEmailRequestReason);
                infoForm.setMemberName(memberName);             
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
            if(PojoUtil.isEmpty(loggedUserOrcid)) {
                infoForm.setUserOrcid(orcid);
            }
            infoForm.setUserGivenNames(givenNames);
            infoForm.setUserFamilyNames(familyNames);
            infoForm.setRedirectUrl(redirectUri);
            infoForm.setStateParam(stateParam);
            infoForm.setResponseType(responseType);
            infoForm.setNonce(nonce);
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            Matcher scopeMatcher = scopesPattern.matcher(requestUrl);
            if (scopeMatcher.find()) {
                String scopes = scopeMatcher.group(1);
                scopesString = URLDecoder.decode(scopes, "UTF-8").trim();
                scopesString = scopesString.replaceAll(" +", " ");
                for (ScopePathType theScope : ScopePathType.getScopesFromSpaceSeparatedString(scopesString)) {
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
            } else {
                throw new InvalidRequestException("Please specify the desired scopes");
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
        
        return infoForm;
    }
    
    private String getEffectiveUserOrcid() {
        OrcidProfileUserDetails currentUser = baseControllerUtil.getCurrentUser(SecurityContextHolder.getContext());
        if (currentUser == null) {
            return null;
        }
        return currentUser.getOrcid();
    }
    
    public String getMessage(String messageCode, Object... messageParams) {
        return localeManager.resolveMessage(messageCode, messageParams);
    }
}
