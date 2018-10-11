package org.orcid.core.oauth;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.jena.ext.com.google.common.collect.Sets;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import com.google.common.collect.Maps;

public class IETFTokenExchangeResponse implements OAuth2AccessToken {

    private Map<String, Object> additionalInformation = Maps.newHashMap();
    private Set<String> scope = Sets.newHashSet();
    private OAuth2RefreshToken refreshToken = null;
    private String tokenType;
    private Date expiration;
    private String value;
    private int expiresIn;
    
    public static IETFTokenExchangeResponse idToken(String idToken) {
        IETFTokenExchangeResponse token = new IETFTokenExchangeResponse();
        token.additionalInformation.put("issued_token_type", OrcidOauth2Constants.IETF_EXCHANGE_ID_TOKEN );
        token.value = idToken;
        token.tokenType = "N_A";
        return token;
    }
    
    public static  IETFTokenExchangeResponse accessToken(OAuth2AccessToken accessToken) {
        IETFTokenExchangeResponse token = new IETFTokenExchangeResponse();
        token.additionalInformation.put("issued_token_type", OrcidOauth2Constants.IETF_EXCHANGE_ACCESS_TOKEN );
        token.value = accessToken.getValue();
        token.tokenType="Bearer";
        token.expiration = accessToken.getExpiration();
        token.expiresIn = accessToken.getExpiresIn();
        token.scope = accessToken.getScope();
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
