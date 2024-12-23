package org.orcid.frontend.spring;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.orcid.core.manager.BackupCodeManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.core.togglz.Features;
import org.orcid.frontend.web.exception.Bad2FARecoveryCodeException;
import org.orcid.frontend.web.exception.Bad2FAVerificationCodeException;
import org.orcid.frontend.web.exception.VerificationCodeFor2FARequiredException;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class OrcidAuthenticationProvider extends DaoAuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidAuthenticationProvider.class);

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "emailManagerReadOnlyV3")
    protected EmailManagerReadOnly emailManagerReadOnly;

    @Resource
    private BackupCodeManager backupCodeManager;

    @Resource
    private TwoFactorAuthenticationManager twoFactorAuthenticationManager;

    @Resource
    private OrcidUserDetailsService orcidUserDetailsService;

    // minutes
    @Value("${org.orcid.core.profile.lockout.window:10}")
    private Integer lockoutWindow;

    @Value("${org.orcid.core.profile.lockout.threshhold:10}")
    private Integer lockoutThreshhold;

    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        if (auth.getCredentials() != null && auth.getCredentials().toString().length() > 256) {
            throw new BadCredentialsException("Invalid credentials: password too long");
        }
        Authentication result = null;

        ProfileEntity profile = null;
        Integer signinLockCount = null;
        Date signinLockStart = null;
        String userOrcid = null;
        boolean succesfulLoginAccountLocked = false;
        try {
            result = super.authenticate(auth);
            profile = getProfileEntity(auth.getName());
            userOrcid = profile.getId();
            if (!Features.ACCOUNT_LOCKOUT_SIMULATION.isActive()) {
                // 2.lock window active
                if (isLockThreshHoldExceeded(profile.getSigninLockCount(), profile.getSigninLockStart())) {
                    LOGGER.info("Correct sign in but threshhold exceeded for: " + userOrcid);
                    succesfulLoginAccountLocked = true;
                    throw new BadCredentialsException("Lock Threashold Exceeded for " + userOrcid);
                } else if ((profile.getSigninLockCount() == null) || (profile.getSigninLockCount() > 0 && Features.ENABLE_ACCOUNT_LOCKOUT.isActive())) {
                    LOGGER.info("Reset the signin lock after correct login outside of locked window for: " + userOrcid);
                    profileEntityManager.resetSigninLock(userOrcid);
                }
            }

        } catch (BadCredentialsException bce) {
            // update the DB for lock threshhold fields
            if (Features.ENABLE_ACCOUNT_LOCKOUT.isActive() && !succesfulLoginAccountLocked) {
                try {
                    profile = getProfileEntity(auth.getName());
                    userOrcid = profile.getId();

                    LOGGER.info("Invalid password attempt updating signin lock");
                    // get the locking info
                    signinLockCount = profile.getSigninLockCount();
                    signinLockStart = profile.getSigninLockStart();
                    if (signinLockStart == null) {
                        profileEntityManager.startSigninLock(userOrcid);
                    }

                    profileEntityManager.updateSigninLock(userOrcid, (signinLockCount == null ? 1 : (signinLockCount + 1)));
                    profileEntityCacheManager.remove(userOrcid);
                } catch (Exception ex) {
                    //TODO: This try/catch should be removed as soon as we confirm there are no more exceptions while updating the sign in lock.
                    if (!(ex instanceof javax.persistence.NoResultException)) {
                        Throwable rootCause = ExceptionUtils.getRootCause(ex);
                        if(rootCause != null) {
                            LOGGER.error("An exception has occurred processing request from user " + auth.getName() + ". " + rootCause.getClass().getSimpleName() + ": " + rootCause.getMessage());
                        } else {
                            LOGGER.error("An exception has occurred processing request from user " + auth.getName() + ". " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
                        }
                    }
                }
            }
            throw bce;
        }

        if (!result.isAuthenticated()) {
            return result;
        }
        if (Features.ACCOUNT_LOCKOUT_SIMULATION.isActive()) {
            profile = getProfileEntity(auth.getName());
            if (profile == null) {
                throw new BadCredentialsException("Invalid username or password");
            }
        }

        if (profile.getUsing2FA()) {
            String recoveryCode = ((OrcidWebAuthenticationDetails) auth.getDetails()).getRecoveryCode();
            if (recoveryCode != null && !recoveryCode.isEmpty()) {
                if (!backupCodeManager.verify(profile.getId(), recoveryCode)) {
                    throw new Bad2FARecoveryCodeException();
                }
            } else {
                String verificationCode = ((OrcidWebAuthenticationDetails) auth.getDetails()).getVerificationCode();
                if (verificationCode == null || verificationCode.isEmpty()) {
                    throw new VerificationCodeFor2FARequiredException();
                }

                if (!twoFactorAuthenticationManager.verificationCodeIsValid(verificationCode, profile)) {
                    throw new Bad2FAVerificationCodeException();
                }
            }
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(profile.getId(), result.getCredentials(), result.getAuthorities());
        authentication.setDetails(orcidUserDetailsService.loadUserByProfile(profile));
        return authentication;
    }

    private ProfileEntity getProfileEntity(String username) {
        ProfileEntity profile = null;
        if (!StringUtils.isEmpty(username)) {
            if (OrcidStringUtils.isValidOrcid(username)) {
                profile = profileEntityCacheManager.retrieve(username);
            } else {
                String orcid = emailManagerReadOnly.findOrcidIdByEmail(username);
                if (orcid != null) {
                    profile = profileEntityCacheManager.retrieve(orcid);
                }
            }
        }
        return profile;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private boolean isLockThreshHoldExceeded(Integer signinLockCount, Date signinLockStart) {
        if ((signinLockCount != null) && signinLockCount > 0 && signinLockStart != null) {
            int multiplyWaitWindow = (signinLockCount - lockoutThreshhold) + 1;
            Instant waitLock = signinLockStart.toInstant().plusSeconds(multiplyWaitWindow * lockoutWindow * 60);
            if (waitLock.isAfter(Instant.now())) {
                return true;
            }
        }
        return false;
    }
}
