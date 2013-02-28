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

import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Visibility;
import org.springframework.security.core.Authentication;

import java.util.Set;

/**
 * 2011-2012 ORCID
 * <p/>
 * Checks permissions for the user current registered as the
 * {@link java.security.Principal} in this session.
 * <p/>
 * This was designed to be used in conjunction with AOP giving access to the
 * {@link org.orcid.core.security .visibility.aop.AccessControl#requiredScopes()}
 * properties. However, to make this more versitile it takes the array of
 * {@link ScopePathType} available when using the annotation.
 * 
 * @author Declan Newman (declan) Date: 27/04/2012
 */
public interface PermissionChecker {

    /**
     * Check the permissions for the given {@link Authentication} object and the
     * scopes defined in the required scopes
     * 
     * @param authentication
     *            The authentication object associated with this session
     * @param requiredScope
     *            the scope required to perform the requested operation
     * @param orcid
     *            the orcid passed into the request. This is for requests, such
     *            as a GET /1234-1234-1234-1234/orcid-bio
     * @param orcidMessage
     *            the {@link OrcidMessage} that has been sent as part of this
     *            request. This will only apply to PUTs and POSTs
     */
    void checkPermissions(Authentication authentication, ScopePathType requiredScope, String orcid, OrcidMessage orcidMessage);

    /**
     * Check the permissions for the given {@link Authentication} object and the
     * scopes defined in the required scopes
     * 
     * @param authentication
     *            The authentication object associated with this session
     * @param requiredScope
     *            the scope required to perform the requested operation
     * @param orcidMessage
     *            the {@link OrcidMessage} that has been sent as part of this
     *            request. This will only apply to PUTs and POSTs
     */
    void checkPermissions(Authentication authentication, ScopePathType requiredScope, OrcidMessage orcidMessage);

    /**
     * Check the permissions for the given {@link Authentication} object and the
     * scopes defined in the required scopes
     * 
     * @param authentication
     *            The authentication object associated with this session
     * @param requiredScope
     *            the scope required to perform the requested operation
     * @param orcid
     *            the orcid passed into the request. This is for requests, such
     *            as a GET /1234-1234-1234-1234/orcid-bio
     */
    void checkPermissions(Authentication authentication, ScopePathType requiredScope, String orcid);

    /**
     * Obtain the current users' permission and return the {@link Visibility}
     * array containing those
     * 
     * @param authentication
     *            the object containing the user's security information
     * @return the {@alink Visibility} array of the current user
     */
    Set<Visibility> obtainVisibilitiesForAuthentication(Authentication authentication, ScopePathType requiredScope, OrcidMessage orcidMessage);
}
