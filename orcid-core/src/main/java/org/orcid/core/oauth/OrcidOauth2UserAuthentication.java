/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

/**
 * @author Declan Newman (declan) Date: 17/04/2012
 */
public class OrcidOauth2UserAuthentication implements Authentication {

    /**
     * 
     */
    private static final long serialVersionUID = 2499121955477502667L;
    private ProfileEntity profileEntity;
    private boolean authenticated = false;

    public OrcidOauth2UserAuthentication(ProfileEntity profileEntity, boolean authenticated) {
        Assert.notNull(profileEntity, "Cannot instantiate an OrcidOauth2UserAuthentication will a null profile");
        this.profileEntity = profileEntity;
        this.authenticated = authenticated;
    }

    /**
     * Set by an <code>AuthenticationManager</code> to indicate the authorities
     * that the principal has been granted. Note that classes should not rely on
     * this value as being valid unless it has been set by a trusted
     * <code>AuthenticationManager</code>.
     * <p>
     * Implementations should ensure that modifications to the returned
     * collection array do not affect the state of the Authentication object, or
     * use an unmodifiable instance.
     * </p>
     * 
     * @return the authorities granted to the principal, or an empty collection
     *         if the token has not been authenticated. Never null.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return profileEntity.getAuthorities();
    }

    /**
     * The credentials that prove the principal is correct. This is usually a
     * password, but could be anything relevant to the
     * <code>AuthenticationManager</code>. Callers are expected to populate the
     * credentials.
     * 
     * @return the credentials that prove the identity of the
     *         <code>Principal</code>
     */
    @Override
    public Object getCredentials() {
        return profileEntity.getEncryptedPassword();
    }

    /**
     * Stores additional details about the authentication request. These might
     * be an IP address, certificate serial number etc.
     * 
     * @return additional details about the authentication request, or
     *         <code>null</code> if not used
     */
    @Override
    public Object getDetails() {
        return profileEntity.getOrcidType();
    }

    /**
     * The identity of the principal being authenticated. In the case of an
     * authentication request with username and password, this would be the
     * username. Callers are expected to populate the principal for an
     * authentication request.
     * <p/>
     * The <tt>AuthenticationManager</tt> implementation will often return an
     * <tt>Authentication</tt> containing richer information as the principal
     * for use by the application. Many of the authentication providers will
     * create a {@code UserDetails} object as the principal.
     * 
     * @return the <code>Principal</code> being authenticated or the
     *         authenticated principal after authentication.
     */
    @Override
    public Object getPrincipal() {
        return profileEntity;
    }

    /**
     * Used to indicate to {@code AbstractSecurityInterceptor} whether it should
     * present the authentication token to the
     * <code>AuthenticationManager</code>. Typically an
     * <code>AuthenticationManager</code> (or, more often, one of its
     * <code>AuthenticationProvider</code>s) will return an immutable
     * authentication token after successful authentication, in which case that
     * token can safely return <code>true</code> to this method. Returning
     * <code>true</code> will improve performance, as calling the
     * <code>AuthenticationManager</code> for every request will no longer be
     * necessary.
     * <p/>
     * For security reasons, implementations of this interface should be very
     * careful about returning <code>true</code> from this method unless they
     * are either immutable, or have some way of ensuring the properties have
     * not been changed since original creation.
     * 
     * @return true if the token has been authenticated and the
     *         <code>AbstractSecurityInterceptor</code> does not need to present
     *         the token to the <code>AuthenticationManager</code> again for
     *         re-authentication.
     */
    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * See {@link #isAuthenticated()} for a full description.
     * <p/>
     * Implementations should <b>always</b> allow this method to be called with
     * a <code>false</code> parameter, as this is used by various classes to
     * specify the authentication token should not be trusted. If an
     * implementation wishes to reject an invocation with a <code>true</code>
     * parameter (which would indicate the authentication token is trusted - a
     * potential security risk) the implementation should throw an
     * {@link IllegalArgumentException}.
     * 
     * @param isAuthenticated
     *            <code>true</code> if the token should be trusted (which may
     *            result in an exception) or <code>false</code> if the token
     *            should not be trusted
     * @throws IllegalArgumentException
     *             if an attempt to make the authentication token trusted (by
     *             passing <code>true</code> as the argument) is rejected due to
     *             the implementation being immutable or implementing its own
     *             alternative approach to {@link #isAuthenticated()}
     */
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        RecordNameEntity recordName = profileEntity.getRecordNameEntity();
        if(recordName == null) {
            return StringUtils.EMPTY;
        }
        
        if(Visibility.PUBLIC.value().equals(recordName.getVisibility())) {
            if(!PojoUtil.isEmpty(recordName.getCreditName())) {
                return recordName.getCreditName();
            } else {
                if(!PojoUtil.isEmpty(recordName.getFamilyName())) {
                    return recordName.getGivenNames() + " " + recordName.getFamilyName();
                }
            }
        }
        return StringUtils.EMPTY;
    }
}
