package org.orcid.core.oauth;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import com.nimbusds.jwt.SignedJWT;

import static org.orcid.core.constants.OrcidOauth2Constants.TOKEN_DISABLED;
import static org.orcid.core.constants.OrcidOauth2Constants.IS_OBO_TOKEN;

public class IETFTokenExchangeResponse implements OAuth2AccessToken {

    private Map<String, Object> additionalInformation = new HashMap<String, Object>();
    private Set<String> scope = new HashSet<String>();
    private OAuth2RefreshToken refreshToken = null;
    private String tokenType;
    private Date expiration;
    private String value;
    private int expiresIn;
    
    public static IETFTokenExchangeResponse idToken(String idToken) throws ParseException {
        IETFTokenExchangeResponse token = new IETFTokenExchangeResponse();
        token.additionalInformation.put("issued_token_type", OrcidOauth2Constants.IETF_EXCHANGE_ID_TOKEN );
        token.value = idToken;
        token.tokenType = "N_A";
        SignedJWT claims = SignedJWT.parse(idToken);
        token.expiration = claims.getJWTClaimsSet().getExpirationTime();
        return token;
    }
    
    public static  IETFTokenExchangeResponse accessToken(OAuth2AccessToken accessToken) {
        IETFTokenExchangeResponse token = new IETFTokenExchangeResponse();
        token.additionalInformation.put("issued_token_type", OrcidOauth2Constants.IETF_EXCHANGE_ACCESS_TOKEN );
        token.value = accessToken.getValue();
        token.tokenType="bearer";
        token.expiration = accessToken.getExpiration();
        token.expiresIn = accessToken.getExpiresIn();
        token.scope = accessToken.getScope();
        if (accessToken.getAdditionalInformation().containsKey("orcid")) {
            token.additionalInformation.put("orcid",accessToken.getAdditionalInformation().get("orcid"));
        }
        if (accessToken.getAdditionalInformation().containsKey("name")) {
            token.additionalInformation.put("name",accessToken.getAdditionalInformation().get("name"));
        }
        if(accessToken.getAdditionalInformation().containsKey(TOKEN_DISABLED)) {
            token.additionalInformation.put(TOKEN_DISABLED, "true");
        }
        if(accessToken.getAdditionalInformation().containsKey(IS_OBO_TOKEN)) {
            token.additionalInformation.put(IS_OBO_TOKEN, "true");
        }

        return token;
    }
    
    private IETFTokenExchangeResponse() {
        
    }
    
    
    @Override
    public Map<String, Object> getAdditionalInformation() {
        return additionalInformation;
    }

    @Override
    public Set<String> getScope() {
        return scope;
    }

    @Override
    public OAuth2RefreshToken getRefreshToken() {
        return refreshToken;
    }

    @Override
    public String getTokenType() {
        return tokenType;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public Date getExpiration() {
        return expiration;
    }

    @Override
    public int getExpiresIn() {
        return expiresIn;
    }

    @Override
    public String getValue() {
        return value;
    }

}
