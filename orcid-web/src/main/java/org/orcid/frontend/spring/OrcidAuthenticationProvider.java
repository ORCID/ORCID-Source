package org.orcid.frontend.spring;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.BackupCodeManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.SlackManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.frontend.web.exception.Bad2FARecoveryCodeException;
import org.orcid.frontend.web.exception.Bad2FAVerificationCodeException;
import org.orcid.frontend.web.exception.VerificationCodeFor2FARequiredException;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class OrcidAuthenticationProvider extends DaoAuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidAuthenticationProvider.class);
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private EmailDao emailDaoReadOnly;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private BackupCodeManager backupCodeManager;
    
    @Resource
    private SlackManager slackManager;

    @Resource
    private TwoFactorAuthenticationManager twoFactorAuthenticationManager;
    
    @Resource
    private OrcidUserDetailsService orcidUserDetailsService;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        Authentication result = super.authenticate(auth);
        if (!result.isAuthenticated()) {
            return result;
        }

        ProfileEntity profile = getProfileEntity(auth.getName());
        if ((profile == null)) {
            throw new BadCredentialsException("Invalid username or password");
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
                EmailEntity emailEntity = emailDaoReadOnly.findCaseInsensitive(username);
                if (emailEntity != null) {
                    profile = emailEntity.getProfile();
                }
            }
        }
        return profile;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
