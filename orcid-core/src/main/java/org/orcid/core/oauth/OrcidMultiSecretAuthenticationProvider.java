/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.oauth;

import javax.annotation.Resource;

import org.orcid.core.manager.EncryptionManager;
import org.orcid.persistence.dao.ClientDetailsDao;
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
    private ClientDetailsDao clientDetailsDao;

    @Resource
    private EncryptionManager encryptionManager;

    @SuppressWarnings("deprecation")
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");
            throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"), userDetails);
        }

        String presentedPassword = authentication.getCredentials().toString();
        ClientDetailsEntity clientDetailsEntity = clientDetailsDao.find(userDetails.getUsername());
        for (ClientSecretEntity clientSecretEntity : clientDetailsEntity.getClientSecrets()) {
            if (getPasswordEncoder().isPasswordValid(encryptionManager.decryptForInternalUse(clientSecretEntity.getClientSecret()), presentedPassword, null)) {
                return;
            }
        }
        logger.debug("Authentication failed: password does not match any value");
        throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"), userDetails);
    }

}
