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
package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;

import java.util.List;

/**
 * 
 * @author Declan Newman
 * 
 */
public interface OrcidOauth2TokenDetailDao extends GenericDao<OrcidOauth2TokenDetail, Long> {

    OrcidOauth2TokenDetail findByTokenValue(String token);

    OrcidOauth2TokenDetail findNonDisabledByTokenValue(String tokenValue);

    void removeByTokenValue(String tokenValue);

    void removeByRefreshTokenValue(String refreshTokenValue);

    void removeByUserOrcidAndClientOrcid(String userOrcid, String clientOrcid);

    OrcidOauth2TokenDetail findByRefreshTokenValue(String refreshTokenValue);

    OrcidOauth2TokenDetail findByAuthenticationKey(String authKey);

    List<OrcidOauth2TokenDetail> findByUserName(String userName);

    List<OrcidOauth2TokenDetail> findByClientId(String clientId);

    void disableAccessToken(String accessToken);

    void disableAccessTokenByRefreshToken(String refreshTokenValue);

    void removeByAuthenticationKeyOrTokenValueOrRefreshTokenValue(String authKey, String tokenValue, String refreshTokenValue);
}
