package org.orcid.frontend.spring;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.BackupCodeManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
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
        boolean succesfulLoginAccountLocked = false;
        try {

            result = super.authenticate(auth);
            // 1.retrieve the existing signin lock info
            profile = getProfileEntity(auth.getName());
            if (profile == null) {
                throw new BadCredentialsException("Invalid username or password");
            }

            if (!Features.ACCOUNT_LOCKOUT_SIMULATION.isActive()) {
                // 2.lock window active
                if (isLockThreshHoldExceeded(profile.getSigninLockCount(), profile.getSigninLockStart())) {
                    LOGGER.info("Correct sign in but threshhold exceeded for: " + profile.getId());
                    succesfulLoginAccountLocked = true;
                    throw new BadCredentialsException("Lock Threashold Exceeded for " + profile.getId());
                } else if ((profile.getSigninLockCount() == null) || (profile.getSigninLockCount() > 0 && Features.ENABLE_ACCOUNT_LOCKOUT.isActive())) {
                    LOGGER.info("Reset the signin lock after correct login outside of locked window for: " + profile.getId());
                    profileEntityManager.resetSigninLock(profile.getId());
                }
            }

        } catch (BadCredentialsException bce) {
            // update the DB for lock threshhold fields
            try {
                if (Features.ENABLE_ACCOUNT_LOCKOUT.isActive() && !succesfulLoginAccountLocked) {
                    LOGGER.info("Invalid password attempt updating signin lock");
                    if (profile == null) {
                        profile = getProfileEntity(auth.getName());
                    }
                    // get the locking info
                    List<Object[]> lockInfoList = profileEntityManager.getSigninLock(profile.getId());
                    signinLockCount = (Integer) lockInfoList.get(0)[2];
                    signinLockStart = (Date) lockInfoList.get(0)[0];
                    if (signinLockStart == null) {
                        profileEntityManager.startSigninLock(profile.getId());
                    }

                    profileEntityManager.updateSigninLock(profile.getId(), signinLockCount + 1);
                    profileEntityCacheManager.remove(profile.getId());
                }

            } catch (Exception ex) {
                if (!(ex instanceof javax.persistence.NoResultException)) {
                    LOGGER.error("Exception while saving sign in lock.", ex);
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
