package org.orcid.frontend.web.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.BackupCodeManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.core.manager.UserConnectionManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.frontend.spring.web.social.GoogleSignIn;
import org.orcid.frontend.spring.web.social.config.SocialContext;
import org.orcid.frontend.spring.web.social.config.SocialType;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;
import org.orcid.pojo.TwoFactorAuthenticationCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.google.api.plus.Person;
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

    @Autowired
    private SocialContext socialContext;

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

    @RequestMapping(value = { "/2FA/authenticationCode.json" }, method = RequestMethod.GET)
    public @ResponseBody TwoFactorAuthenticationCodes getTwoFactorCodeWrapper() {
        return new TwoFactorAuthenticationCodes();
    }

    @RequestMapping(value = { "/access" }, method = RequestMethod.GET)
    public ModelAndView signinHandler(HttpServletRequest request, HttpServletResponse response) {
        SocialType connectionType = socialContext.isSignedIn(request, response);
        if (connectionType != null) {
            Map<String, String> userMap = retrieveUserDetails(connectionType);

            String providerId = connectionType.value();
            String userId = socialContext.getUserId();
            UserconnectionEntity userConnectionEntity = userConnectionManager.findByProviderIdAndProviderUserId(userMap.get("providerUserId"), providerId);
            if (userConnectionEntity != null) {
                if (userConnectionEntity.isLinked()) {
                    ProfileEntity profile = profileEntityCacheManager.retrieve(userConnectionEntity.getOrcid());
                    if (profile.getUsing2FA()) {
                        return new ModelAndView("social_2FA");
                    }

                    UserconnectionPK pk = new UserconnectionPK(userId, providerId, userMap.get("providerUserId"));
                    String aCredentials = new StringBuffer(providerId).append(":").append(userMap.get("providerUserId")).toString();
                    PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(userConnectionEntity.getOrcid(), aCredentials);
                    token.setDetails(getOrcidProfileUserDetails(userConnectionEntity.getOrcid()));
                    Authentication authentication = authenticationManager.authenticate(token);
                    userConnectionManager.updateLoginInformation(pk);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    return new ModelAndView("redirect:" + calculateRedirectUrl(request, response));
                } else {
                    ModelAndView mav = new ModelAndView();
                    mav.setViewName("social_link_signin");
                    mav.addObject("providerId", providerId);
                    mav.addObject("accountId", getAccountIdForDisplay(userMap));
                    mav.addObject("linkType", "social");
                    mav.addObject("emailId", (userMap.get("email") == null) ? "" : userMap.get("email"));
                    mav.addObject("firstName", (userMap.get("firstName") == null) ? "" : userMap.get("firstName"));
                    mav.addObject("lastName", (userMap.get("lastName") == null) ? "" : userMap.get("lastName"));
                    return mav;
                }
            } else {
                throw new UsernameNotFoundException("Could not find an orcid account associated with the email id.");
            }
        } else {
            throw new UsernameNotFoundException("Could not find an orcid account associated with the email id.");
        }
    }

    @RequestMapping(value = { "/2FA/submitCode.json" }, method = RequestMethod.POST)
    public @ResponseBody TwoFactorAuthenticationCodes post2FAVerificationCode(@RequestBody TwoFactorAuthenticationCodes codes, HttpServletRequest request,
            HttpServletResponse response) {
        SocialType connectionType = socialContext.isSignedIn(request, response);
        if (connectionType != null) {
            Map<String, String> userMap = retrieveUserDetails(connectionType);

            String providerId = connectionType.value();
            String userId = socialContext.getUserId();
            UserconnectionEntity userConnectionEntity = userConnectionManager.findByProviderIdAndProviderUserId(userMap.get("providerUserId"), providerId);
            if (userConnectionEntity != null) {
                if (userConnectionEntity.isLinked()) {
                    validate2FACodes(userConnectionEntity.getOrcid(), codes);
                    if (!codes.getErrors().isEmpty()) {
                        return codes;
                    }

                    UserconnectionPK pk = new UserconnectionPK(userId, providerId, userMap.get("providerUserId"));
                    String aCredentials = new StringBuffer(providerId).append(":").append(userMap.get("providerUserId")).toString();
                    PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(userConnectionEntity.getOrcid(), aCredentials);
                    token.setDetails(getOrcidProfileUserDetails(userConnectionEntity.getOrcid()));
                    Authentication authentication = authenticationManager.authenticate(token);
                    userConnectionManager.updateLoginInformation(pk);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    codes.setRedirectUrl(calculateRedirectUrl(request, response));
                } else {
                    codes.setRedirectUrl(orcidUrlManager.getBaseUrl() + "/social/access");
                }
            } else {
                throw new UsernameNotFoundException("Could not find an orcid account associated with the email id.");
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

    private Map<String, String> retrieveUserDetails(SocialType connectionType) {

        Map<String, String> userMap = new HashMap<String, String>();
        if (SocialType.FACEBOOK.equals(connectionType)) {
            Facebook facebook = socialContext.getFacebook();
            User user = facebook.fetchObject("me", User.class, "id", "email", "name", "first_name", "last_name");
            userMap.put("providerUserId", user.getId());
            userMap.put("userName", user.getName());
            userMap.put("email", user.getEmail());
            userMap.put("firstName", user.getFirstName());
            userMap.put("lastName", user.getLastName());
        } else if (SocialType.GOOGLE.equals(connectionType)) {
            GoogleSignIn google = socialContext.getGoogle();
            System.out.println("socialContext.getUserId(): " + socialContext.getUserId());
            Person person = google.plusOperations().getGoogleProfile();
            userMap.put("providerUserId", person.getId());
            System.out.println("person.getId(): " + person.getId());
            userMap.put("userName", person.getDisplayName());
            userMap.put("email", person.getAccountEmail());
            userMap.put("firstName", person.getGivenName());
            userMap.put("lastName", person.getFamilyName());
            
            
            google.getJWTInfo();
        }

        return userMap;
    }

    private String getAccountIdForDisplay(Map<String, String> userMap) {
        if (userMap.get("email") != null) {
            return userMap.get("email");
        }
        if (userMap.get("userName") != null) {
            return userMap.get("userName");
        }
        return userMap.get("providerUserId");
    }
}
