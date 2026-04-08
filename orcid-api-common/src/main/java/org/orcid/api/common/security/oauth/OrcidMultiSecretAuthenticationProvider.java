package org.orcid.api.common.security.oauth;

import jakarta.annotation.Resource;

import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.userDetails.MultiSecretClient;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Will Simpson
 *
 */
public class OrcidMultiSecretAuthenticationProvider extends DaoAuthenticationProvider {

    @Resource
    private EncryptionManager encryptionManager;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");
            throw new BadCredentialsException("No credentials provided for " + userDetails.getUsername());
        }

        String presentedPassword = authentication.getCredentials().toString();
        if(!presentedPassword.startsWith("{noop}")){
            presentedPassword = "{noop}" + presentedPassword;
        }

        MultiSecretClient clientDetails = (MultiSecretClient) this.getUserDetailsService().loadUserByUsername(userDetails.getUsername());
        for (MultiSecretClient.Secret secret : clientDetails.getSecrets()) {
            if (getPasswordEncoder().matches(encryptionManager.decryptForInternalUse(secret.getEncryptedSecret()), presentedPassword)) {
                return;
            }
        }
        logger.debug("Authentication failed: password does not match any value");
        throw new BadCredentialsException("Invalid client credentials provided for " + userDetails.getUsername());
    }

}