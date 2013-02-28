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
package org.orcid.core.security;

import java.util.Arrays;

import javax.annotation.Resource;

import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.SecurityDetails;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 09/02/2012
 */
public class OrcidUsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private EncryptionManager encryptionManager;

    /**
     * Performs authentication with the same contract as
     * {@link org.springframework.security.authentication.AuthenticationManager#authenticate(org.springframework.security.core.Authentication)}
     * .
     * 
     * @param authentication
     *            the authentication request object.
     * @return a fully authenticated object including credentials. May return
     *         <code>null</code> if the <code>AuthenticationProvider</code> is
     *         unable to support authentication of the passed
     *         <code>Authentication</code> object. In such a case, the next
     *         <code>AuthenticationProvider</code> that supports the presented
     *         <code>Authentication</code> class will be tried.
     * @throws org.springframework.security.core.AuthenticationException
     *             if authentication fails.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return doAuthentication((UsernamePasswordAuthenticationToken) authentication);
        } else {
            return null;
        }
    }

    /**
     * Returns <code>true</code> if this <Code>AuthenticationProvider</code>
     * supports the indicated <Code>Authentication</code> object.
     * <p>
     * Returning <code>true</code> does not guarantee an
     * <code>AuthenticationProvider</code> will be able to authenticate the
     * presented instance of the <code>Authentication</code> class. It simply
     * indicates it can support closer evaluation of it. An
     * <code>AuthenticationProvider</code> can still return <code>null</code>
     * from the
     * {@link #authenticate(org.springframework.security.core.Authentication)}
     * method to indicate another <code>AuthenticationProvider</code> should be
     * tried.
     * </p>
     * <p>
     * Selection of an <code>AuthenticationProvider</code> capable of performing
     * authentication is conducted at runtime the <code>ProviderManager</code>.
     * </p>
     * 
     * @param authentication
     *            the Authentication type that will be supported by this
     *            AuthenticationProvider
     * @return <code>true</code> if the implementation can more closely evaluate
     *         the <code>Authentication</code> class presented
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private Authentication doAuthentication(UsernamePasswordAuthenticationToken authentication) {
        Assert.isInstanceOf(String.class, authentication.getPrincipal(), "Cannot attempt user/pass authentication with a null or non-string userId.");
        Assert.isInstanceOf(String.class, authentication.getCredentials(), "Cannot attempt user/pass authentication with a null or non-string password.");
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        OrcidProfile profile;

        if (OrcidStringUtils.isValidOrcid(username)) {
            profile = orcidProfileManager.retrieveOrcidProfile(username);
        } else {
            profile = orcidProfileManager.retrieveOrcidProfileByEmail(username);
        }
        
        if (profileDeactivated(profile)){
            throw new BadCredentialsException("orcid.frontend.security.orcid_deactivated");
        }
        
        if (passwordsMatch(profile, password)) {
            return new UsernamePasswordAuthenticationToken(profile, username, Arrays.asList(new SimpleGrantedAuthority("USER_ROLE")));
        } else {
            throw new BadCredentialsException("orcid.frontend.security.bad_credentials");
        }
    }

    private boolean passwordsMatch(OrcidProfile orcidProfile, String password) {
        if (orcidProfile != null) {
            OrcidInternal orcidInternal = orcidProfile.getOrcidInternal();
            if (orcidInternal != null && orcidInternal.getSecurityDetails() != null && orcidInternal.getSecurityDetails().getEncryptedPassword() != null) {
                SecurityDetails securityDetails = orcidInternal.getSecurityDetails();
                return encryptionManager.hashMatches(password, securityDetails.getEncryptedPassword().getContent());
            }
        }
        return false;
    }
    
    private boolean profileDeactivated(OrcidProfile orcidProfile) {
        return orcidProfile!=null && orcidProfile.isDeactivated();
    }

}
