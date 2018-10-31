package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.BackupCodeManager;
import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.core.manager.InstitutionalSignInManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.core.manager.UserConnectionManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.core.utils.JsonUtils;
import org.orcid.frontend.web.exception.FeatureDisabledException;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.UserConnectionStatus;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.pojo.HeaderCheckResult;
import org.orcid.pojo.RemoteUser;
import org.orcid.pojo.TwoFactorAuthenticationCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Will Simpson
 *
 */
@Controller
@RequestMapping("/shibboleth")
public class ShibbolethController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShibbolethController.class);

    @Resource
    private UserConnectionManager userConnectionManager;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private IdentityProviderManager identityProviderManager;

    @Resource
    private InstitutionalSignInManager institutionalSignInManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;
    
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

    @RequestMapping(value = { "/signin" }, method = RequestMethod.GET)
    public ModelAndView signinHandler(HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> headers, ModelAndView mav) {
        LOGGER.info("Headers for shibboleth sign in: {}", headers);
        checkEnabled();
        mav.setViewName("social_link_signin");
        String shibIdentityProvider = headers.get(InstitutionalSignInManager.SHIB_IDENTITY_PROVIDER_HEADER);
        mav.addObject("providerId", shibIdentityProvider);
        String displayName = institutionalSignInManager.retrieveDisplayName(headers);
        mav.addObject("accountId", displayName);
        RemoteUser remoteUser = institutionalSignInManager.retrieveRemoteUser(headers);
        if (remoteUser == null) {
            LOGGER.info("Failed federated log in for {}", shibIdentityProvider);
            identityProviderManager.incrementFailedCount(shibIdentityProvider);
            mav.addObject("unsupportedInstitution", true);
            mav.addObject("institutionContactEmail", identityProviderManager.retrieveContactEmailByProviderid(shibIdentityProvider));
            return mav;
        }

        // Check if the Shibboleth user is already linked to an ORCID account.
        // If so sign them in automatically.
        UserconnectionEntity userConnectionEntity = userConnectionManager.findByProviderIdAndProviderUserIdAndIdType(remoteUser.getUserId(), shibIdentityProvider,
                remoteUser.getIdType());
        if (userConnectionEntity != null) {
            LOGGER.info("Found existing user connection: {}", userConnectionEntity);
            HeaderCheckResult checkHeadersResult = institutionalSignInManager.checkHeaders(parseOriginalHeaders(userConnectionEntity.getHeadersJson()), headers);
            if (!checkHeadersResult.isSuccess()) {
                mav.addObject("headerCheckFailed", true);
                return mav;
            }
            
            ProfileEntity profile = profileEntityCacheManager.retrieve(userConnectionEntity.getOrcid());
            if (profile.getUsing2FA()) {
                return new ModelAndView("institutional_2FA");
            }
            
            try {
                notifyUser(shibIdentityProvider, userConnectionEntity);
                processAuthentication(remoteUser, userConnectionEntity);
            } catch (AuthenticationException e) {
                // this should never happen
                SecurityContextHolder.getContext().setAuthentication(null);
                LOGGER.warn("User {0} should have been logged-in via Shibboleth, but was unable to due to a problem", remoteUser, e);
            }
            return new ModelAndView("redirect:" + calculateRedirectUrl(request, response));
        } else {
            // To avoid confusion, force the user to login to ORCID again
            mav.addObject("linkType", "shibboleth");
            mav.addObject("firstName", institutionalSignInManager.retrieveFirstName(headers));
            mav.addObject("lastName", institutionalSignInManager.retrieveLastName(headers));
        }
        return mav;
    }

    @RequestMapping(value = { "/2FA/submitCode.json" }, method = RequestMethod.POST)
    public @ResponseBody TwoFactorAuthenticationCodes post2FAVerificationCode(@RequestBody TwoFactorAuthenticationCodes codes, HttpServletRequest request,
            HttpServletResponse response, @RequestHeader Map<String, String> headers) {
        
        checkEnabled();
        String shibIdentityProvider = headers.get(InstitutionalSignInManager.SHIB_IDENTITY_PROVIDER_HEADER);
        RemoteUser remoteUser = institutionalSignInManager.retrieveRemoteUser(headers);
        if (remoteUser == null) {
            LOGGER.info("Failed federated log in for {}", shibIdentityProvider);
            identityProviderManager.incrementFailedCount(shibIdentityProvider);
            codes.setRedirectUrl(orcidUrlManager.getBaseUrl() + "/shibboleth/signin");
            return codes;
        }
        
        UserconnectionEntity userConnectionEntity = userConnectionManager.findByProviderIdAndProviderUserIdAndIdType(remoteUser.getUserId(), shibIdentityProvider,
                remoteUser.getIdType());
        if (userConnectionEntity != null) {
            LOGGER.info("Found existing user connection: {}", userConnectionEntity);
            HeaderCheckResult checkHeadersResult = institutionalSignInManager.checkHeaders(parseOriginalHeaders(userConnectionEntity.getHeadersJson()), headers);
            if (!checkHeadersResult.isSuccess()) {
                codes.setRedirectUrl(orcidUrlManager.getBaseUrl() + "/shibboleth/signin");
                return codes;
            }
            
            validate2FACodes(userConnectionEntity.getOrcid(), codes);
            if (!codes.getErrors().isEmpty()) {
                return codes;
            }
            
            try {
                notifyUser(shibIdentityProvider, userConnectionEntity);
                processAuthentication(remoteUser, userConnectionEntity);
            } catch (AuthenticationException e) {
                // this should never happen
                SecurityContextHolder.getContext().setAuthentication(null);
                LOGGER.warn("User {0} should have been logged-in via Shibboleth, but was unable to due to a problem", remoteUser, e);
            }
            codes.setRedirectUrl(calculateRedirectUrl(request, response));
            return codes;
        } else {
            codes.setRedirectUrl(orcidUrlManager.getBaseUrl() + "/shibboleth/signin");
            return codes;
        }
    }

    private void notifyUser(String shibIdentityProvider, UserconnectionEntity userConnectionEntity) {
        if (!UserConnectionStatus.NOTIFIED.equals(userConnectionEntity.getConnectionSatus())) {
            try {
                institutionalSignInManager.sendNotification(userConnectionEntity.getOrcid(), shibIdentityProvider);
                userConnectionEntity.setConnectionSatus(UserConnectionStatus.NOTIFIED);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Unable to send institutional sign in notification to user " + userConnectionEntity.getOrcid(), e);
            }
        }
    }
    
    private void processAuthentication(RemoteUser remoteUser, UserconnectionEntity userConnectionEntity) {
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(userConnectionEntity.getOrcid(), remoteUser.getUserId());
        token.setDetails(getOrcidProfileUserDetails(userConnectionEntity.getOrcid()));
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        userConnectionEntity.setLastLogin(new Date());
        userConnectionManager.update(userConnectionEntity);
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

    private Map<String, String> parseOriginalHeaders(String originalHeadersJson) {
        @SuppressWarnings("unchecked")
        Map<String, String> originalHeaders = originalHeadersJson != null ? JsonUtils.readObjectFromJsonString(originalHeadersJson, Map.class)
                : Collections.<String, String> emptyMap();
        return originalHeaders;
    }

    private void checkEnabled() {
        if (!isShibbolethEnabled()) {
            throw new FeatureDisabledException();
        }
    }

}
