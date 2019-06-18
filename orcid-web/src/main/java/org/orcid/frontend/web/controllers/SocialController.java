package org.orcid.frontend.web.controllers;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.BackupCodeManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.core.manager.UserConnectionManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.frontend.spring.web.social.config.SocialSignInUtils;
import org.orcid.frontend.spring.web.social.config.UserCookieGenerator;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;
import org.orcid.pojo.OAuthSigninData;
import org.orcid.pojo.TwoFactorAuthenticationCodes;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Shobhit Tyagi
 */
@Controller
@RequestMapping("/social")
public class SocialController extends BaseController {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private UserConnectionManager userConnectionManager;

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private TwoFactorAuthenticationManager twoFactorAuthenticationManager;

    @Resource
    private BackupCodeManager backupCodeManager;

    @Resource
    private OrcidUserDetailsService orcidUserDetailsService;

    @Resource
    private UserCookieGenerator userCookieGenerator;

    @Resource
    private SocialSignInUtils socialSignInUtils;
    
    @RequestMapping(value = { "/2FA/authenticationCode.json" }, method = RequestMethod.GET)
    public @ResponseBody TwoFactorAuthenticationCodes getTwoFactorCodeWrapper() {
        return new TwoFactorAuthenticationCodes();
    }

    @RequestMapping(value = { "/signinData.json" }, method = RequestMethod.GET)
    public @ResponseBody OAuthSigninData getSigninData(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> signedInData = socialSignInUtils.getSignedInData(request, response);
        OAuthSigninData data = new OAuthSigninData();
        if (signedInData != null) {
            data.setAccountId(getAccountIdForDisplay(signedInData));
            data.setLinkType("social");
            data.setProviderId(signedInData.get(OrcidOauth2Constants.PROVIDER_ID));            
            data.setEmail(signedInData.containsKey(OrcidOauth2Constants.EMAIL) ? signedInData.get(OrcidOauth2Constants.EMAIL) : "");
            data.setFirstName(signedInData.containsKey(OrcidOauth2Constants.FIRST_NAME) ?  signedInData.get(OrcidOauth2Constants.FIRST_NAME) : "");
            data.setLastName(signedInData.containsKey(OrcidOauth2Constants.LAST_NAME) ?  signedInData.get(OrcidOauth2Constants.LAST_NAME) : "");
            data.setAccountId(getAccountIdForDisplay(signedInData));
        }
        return data;
    }

    @RequestMapping(value = { "/access" }, method = RequestMethod.GET)
    public ModelAndView signinHandler(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> signedInData = socialSignInUtils.getSignedInData(request, response);
        if (signedInData != null) {            
            String userConnectionId = signedInData.get(OrcidOauth2Constants.USER_CONNECTION_ID);
            String providerId = signedInData.get(OrcidOauth2Constants.PROVIDER_ID);
            String providerUserId = signedInData.get(OrcidOauth2Constants.PROVIDER_USER_ID);
            String orcid = signedInData.get(OrcidOauth2Constants.ORCID);
            Boolean isLinked = Boolean.valueOf(signedInData.get(OrcidOauth2Constants.IS_LINKED));
            if (isLinked) {
                ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
                if (profile.getUsing2FA()) {
                    return new ModelAndView("social_2FA");
                }
                updateUserConnectionLoginAndLogUserIn(userConnectionId, providerId, providerUserId, orcid);
                return new ModelAndView("redirect:" + calculateRedirectUrl(request, response, false));
            } else {
                return new ModelAndView("social_link_signin");
            } 
        } else {
            throw new UsernameNotFoundException("Could not find social account");
        }
    }

    @RequestMapping(value = { "/2FA/submitCode.json" }, method = RequestMethod.POST)
    public @ResponseBody TwoFactorAuthenticationCodes post2FAVerificationCode(@RequestBody TwoFactorAuthenticationCodes codes, HttpServletRequest request,
            HttpServletResponse response) {
        Map<String, String> signedInData = socialSignInUtils.getSignedInData(request, response);
        if (signedInData != null) {
            String userConnectionId = signedInData.get(OrcidOauth2Constants.USER_CONNECTION_ID);
            String providerId = signedInData.get(OrcidOauth2Constants.PROVIDER_ID);
            String providerUserId = signedInData.get(OrcidOauth2Constants.PROVIDER_USER_ID);
            String orcid = signedInData.get(OrcidOauth2Constants.ORCID);
            Boolean isLinked = Boolean.valueOf(signedInData.get(OrcidOauth2Constants.IS_LINKED));
            if (isLinked) {
                validate2FACodes(orcid, codes);
                if (!codes.getErrors().isEmpty()) {
                    return codes;
                }
                updateUserConnectionLoginAndLogUserIn(userConnectionId, providerId, providerUserId, orcid);
                codes.setRedirectUrl(calculateRedirectUrl(request, response, false));
            } else {
                codes.setRedirectUrl(orcidUrlManager.getBaseUrl() + "/social/access");
            }
            
        } else {
            throw new UsernameNotFoundException("Could not find an orcid account associated with the email id.");
        }
        return codes;
    }

    private void validate2FACodes(String orcid, TwoFactorAuthenticationCodes codes) {
        codes.setErrors(new ArrayList<>());
        if (codes.getRecoveryCode() != null && !codes.getRecoveryCode().isEmpty()) {
            if (!backupCodeManager.verify(orcid, codes.getRecoveryCode())) {
                codes.getErrors().add(getMessage("2FA.recoveryCode.invalid"));
            }
            return;
        }

        if (codes.getVerificationCode() == null || codes.getVerificationCode().isEmpty()
                || !twoFactorAuthenticationManager.verificationCodeIsValid(codes.getVerificationCode(), orcid)) {
            codes.getErrors().add(getMessage("2FA.verificationCode.invalid"));
        }
    }

    private OrcidProfileUserDetails getOrcidProfileUserDetails(String orcid) {
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);
        return orcidUserDetailsService.loadUserByProfile(profileEntity);
    }

    private String getAccountIdForDisplay(Map<String, String> userMap) {
        if (userMap.containsKey(OrcidOauth2Constants.EMAIL)) {
            return userMap.get(OrcidOauth2Constants.EMAIL);
        }
        if (userMap.containsKey(OrcidOauth2Constants.DISPLAY_NAME)) {
            return userMap.get(OrcidOauth2Constants.DISPLAY_NAME);
        }
        return userMap.get(OrcidOauth2Constants.PROVIDER_USER_ID);
    }    
    
    private void updateUserConnectionLoginAndLogUserIn(String userConnectionId, String providerId, String providerUserId, String orcid) {
        // Update userConnection login status
        UserconnectionPK pk = new UserconnectionPK(userConnectionId, providerId, providerUserId);
        userConnectionManager.updateLoginInformation(pk);
        
        // Log user in
        String aCredentials = providerId + ':' + providerUserId;
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(orcid, aCredentials);
        token.setDetails(getOrcidProfileUserDetails(orcid));
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
