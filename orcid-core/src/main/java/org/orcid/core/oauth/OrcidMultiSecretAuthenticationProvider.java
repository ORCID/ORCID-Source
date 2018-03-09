package org.orcid.core.oauth;

import javax.annotation.Resource;

import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
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
    private ClientDetailsManager clientDetailsManager;
    
    @Resource
    private EncryptionManager encryptionManager;

    @SuppressWarnings("deprecation")
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");
            throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        String presentedPassword = authentication.getCredentials().toString();
        ClientDetailsEntity clientDetailsEntity = clientDetailsManager.findByClientId(userDetails.getUsername());
        for (ClientSecretEntity clientSecretEntity : clientDetailsEntity.getClientSecrets()) {
            if (getPasswordEncoder().isPasswordValid(encryptionManager.decryptForInternalUse(clientSecretEntity.getClientSecret()), presentedPassword, null)) {
                return;
            }
        }
        logger.debug("Authentication failed: password does not match any value");
        throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
    }

}
