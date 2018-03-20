package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;

/**
 * 
 * @author Declan Newman
 * 
 */
public interface OrcidOauth2TokenDetailDao extends GenericDao<OrcidOauth2TokenDetail, Long> {

    OrcidOauth2TokenDetail findByTokenValue(String token);

    OrcidOauth2TokenDetail findNonDisabledByTokenValue(String tokenValue);    

    void removeByRefreshTokenValue(String refreshTokenValue);

    OrcidOauth2TokenDetail findByRefreshTokenValue(String refreshTokenValue);

    List<OrcidOauth2TokenDetail> findByAuthenticationKey(String authKey);

    List<OrcidOauth2TokenDetail> findByUserName(String userName);

    List<OrcidOauth2TokenDetail> findByClientId(String clientId);
    
    List<OrcidOauth2TokenDetail> findByClientIdAndUserName(String clientId, String userName);

    void disableAccessToken(String accessToken);

    void disableAccessTokenById(Long tokenId, String userOrcid);
    
    void disableAccessTokenByRefreshToken(String refreshTokenValue);    
    
    List<String> findAvailableScopesByUserAndClientId(String clientId, String userName);

    int findCountByUserName(String userName);

    int disableAccessTokenByCodeAndClient(String authorizationCode, String clientID, String reason);

    void disableAccessTokenByUserOrcid(String userOrcid, String reason);
    
    void revokeAccessToken(String accessToken);
}
