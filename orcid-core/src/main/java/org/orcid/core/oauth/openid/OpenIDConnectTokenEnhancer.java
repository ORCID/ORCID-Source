package org.orcid.core.oauth.openid;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;

@Component
public class OpenIDConnectTokenEnhancer implements TokenEnhancer {
    
    @Value("${org.orcid.core.token.read_validity_seconds:631138519}")
    private int readValiditySeconds;
    
    @Resource
    private ProfileEntityManager profileEntityManager;
    
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    @Resource
    private OpenIDConnectKeyService keyManager;
    
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        //We have the code at this point, but it has already been consumed and removed.
        //So instead we check for a nonce and max_age which are added back into request by OrcidClientCredentialEndPointDelegatorImpl
        Map<String,String> params = authentication.getOAuth2Request().getRequestParameters();
        
        //inject the OpenID Connect "id_token" (authn).  This is distinct from the access token (authz), so is for transporting info to the client only
        //this means we do not have to support using them for authentication purposes. Some APIs support it, but it is not part of the spec.          
        try {
            //shared secret for signing. Use HMAC as we can do it with existing keys and not certs
            Builder claims = new JWTClaimsSet.Builder();
            claims.audience(params.get(OrcidOauth2Constants.CLIENT_ID_PARAM));
            claims.subject(accessToken.getAdditionalInformation().get("orcid").toString());
            claims.issuer("https://orcid.org");
            claims.expirationTime(accessToken.getExpiration());
            claims.issueTime(new Date());
            claims.jwtID(UUID.randomUUID().toString());
            claims.claim("nonce", params.get("nonce"));
            if (params.get("max_age") != null){
                //When max_age is used, the ID Token returned MUST include an auth_time Claim Value.
                //This is a privacy leak and probably should not be implemented.
                //However, not implementing it means we cannot conform with spec.
            }                                                    
            SignedJWT signedJWT = keyManager.sign(claims.build());
            String idTok = signedJWT.serialize();
            accessToken.getAdditionalInformation().put("id_token", idTok);
        } catch (JOSEException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        
        return accessToken;
        
    }
    
        //during authn prompt=login and prompt=none must be honored
}
