package org.orcid.frontend.spring;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.jboss.aerogear.security.otp.Totp;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class OrcidAuthenticationProvider extends DaoAuthenticationProvider {

    @Resource
    private ProfileDao profileDao;

    @Resource
    private EmailDao emailDao;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        Authentication result = super.authenticate(auth);
        if (!result.isAuthenticated()) {
            return result;
        }
        
        ProfileEntity profile = obtainEntity(auth.getName());
        if ((profile == null)) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if (profile.isUsing2FA()) {
            String verificationCode = ((OrcidWebAuthenticationDetails) auth.getDetails()).getVerificationCode();
            if (verificationCode == null || verificationCode.isEmpty()) {
                throw new VerificationCodeFor2FARequiredException();
            }

            Totp totp = new Totp(profile.getSecretFor2FA());
            if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
                throw new Bad2FAVerificationCodeException();
            }
        }
        return new UsernamePasswordAuthenticationToken(profile, result.getCredentials(), result.getAuthorities());
    }

    private ProfileEntity obtainEntity(String username) {
        ProfileEntity profile = null;
        if (!StringUtils.isEmpty(username)) {
            if (OrcidStringUtils.isValidOrcid(username)) {
                profile = profileDao.find(username);
            } else {
                EmailEntity emailEntity = emailDao.findCaseInsensitive(username);
                if (emailEntity != null) {
                    profile = emailEntity.getProfile();
                }
            }
        }
        return profile;
    }

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
