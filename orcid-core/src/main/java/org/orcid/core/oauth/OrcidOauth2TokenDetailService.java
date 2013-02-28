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

import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;

import java.util.List;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 19/04/2012
 */
public interface OrcidOauth2TokenDetailService {

    OrcidOauth2TokenDetail findNonDisabledByTokenValue(String token);

    OrcidOauth2TokenDetail findIgnoringDisabledByTokenValue(String token);

    List<OrcidOauth2TokenDetail> getAll();

    void remove(OrcidOauth2TokenDetail e);

    void remove(String id);

    void saveOrUpdate(OrcidOauth2TokenDetail e);

    Long getCount();

    void removeByTokenValue(String tokenValue);

    OrcidOauth2TokenDetail findByRefreshTokenValue(String refreshTokenValue);

    void removeByRefreshTokenValue(String refreshToken);

    OrcidOauth2TokenDetail findByAuthenticationKey(String authKey);

    List<OrcidOauth2TokenDetail> findByUserName(String userName);

    List<OrcidOauth2TokenDetail> findByClientId(String clientId);

    /**
     * This should NOT delete the row, but merely remove the value from it
     * 
     * @param accessToken
     *            the value to use to identify the row containing the access
     *            token
     */
    void disableAccessToken(String accessToken);

    /**
     * This should NOT delete the row, but merely remove the value from it
     * 
     * @param refreshTokenValue
     *            the value to use to identify the row containing the access
     *            token
     */
    void disableAccessTokenByRefreshToken(String refreshTokenValue);

    void removeConflictsAndCreateNew(OrcidOauth2TokenDetail detail);
}
