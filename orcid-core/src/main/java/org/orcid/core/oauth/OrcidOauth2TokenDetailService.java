package org.orcid.core.oauth;

import java.util.List;

import org.orcid.core.constants.RevokeReason;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;

/**
 * @author Declan Newman (declan) Date: 19/04/2012
 */
public interface OrcidOauth2TokenDetailService {

    void setOrcidOauth2TokenDetailDao(OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao);
    
    OrcidOauth2TokenDetail findNonDisabledByTokenValue(String token);

    OrcidOauth2TokenDetail findIgnoringDisabledByTokenValue(String token);

    List<OrcidOauth2TokenDetail> getAll();

    void saveOrUpdate(OrcidOauth2TokenDetail e);

    Long getCount();    

    OrcidOauth2TokenDetail findByRefreshTokenValue(String refreshTokenValue);

    void removeByRefreshTokenValue(String refreshToken);

    List<OrcidOauth2TokenDetail> findByAuthenticationKey(String authKey);

    List<OrcidOauth2TokenDetail> findByUserName(String userName);

    List<OrcidOauth2TokenDetail> findByClientId(String clientId);
    
    List<OrcidOauth2TokenDetail> findByClientIdAndUserName(String clientId, String userName);
    
    boolean doesClientKnowUser(String clientId, String userName);
    
    /**
     * This should NOT delete the row, but merely set it as disabled
     * 
     * @param accessToken
     *            the value to use to identify the row containing the access
     *            token
     */
    void disableAccessToken(String accessToken);

    /**
     * This should NOT delete the row, but merely set it as disabled and store the revoke date
     * 
     * @param accessToken
     *            the value to use to identify the row containing the access
     *            token
     */
    void revokeAccessToken(String accessToken);
    
    void disableAccessTokenByRefreshToken(String refreshTokenValue);
    
    /**
     * This should NOT delete the row, but merely set it as disabled
     * 
     * @param tokenId
     *            the id of the token that should be disabled
     * @param userOrcid
     *            the id of the user owner of the token
     */
    void disableAccessToken(Long tokenId, String userOrcid);        

    void createNew(OrcidOauth2TokenDetail detail);
    
    int findCountByUserName(String userName, long lastModified);

    int disableAccessTokenByCodeAndClient(String authorizationCode, String clientID, RevokeReason reason);
    
    /**
     * This should NOT delete the row, but merely set it as disabled
     * 
     * @param userOrcid
     *            the id of the user owner of the token
     */
    void disableAccessTokenByUserOrcid(String userOrcid, RevokeReason reason);
    
}
