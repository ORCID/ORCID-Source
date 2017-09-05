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
package org.orcid.core.oauth.openid;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.read_only.PersonDetailsManagerReadOnly;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;

public class OpenIDConnectTokenEnhancer implements TokenEnhancer {
    
    @Value("${org.orcid.core.token.read_validity_seconds:631138519}")
    private int readValiditySeconds;
    
    @Resource
    private ProfileEntityManager profileEntityManager;
    
    @Resource
    private PersonDetailsManagerReadOnly personDetailsManagerReadOnly;
    
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    @Resource
    private OpenIDConnectKeyService keyManager;
    
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        //We have the code at this point, but it has already been consumed and removed.
        //So instead we check for a nonce and max_age which are added back into request by OrcidClientCredentialEndPointDelegatorImpl
        Map<String,String> params = authentication.getOAuth2Request().getRequestParameters();

        //only add if we're using openid scope.
        String scopes = params.get(OrcidOauth2Constants.SCOPE_PARAM);        
        if (PojoUtil.isEmpty(scopes) || !ScopePathType.getScopesFromSpaceSeparatedString(scopes).contains(ScopePathType.OPENID) ){
            return accessToken;
        }
        
        //inject the OpenID Connect "id_token" (authn).  This is distinct from the access token (authz), so is for transporting info to the client only
        //this means we do not have to support using them for authentication purposes. Some APIs support it, but it is not part of the spec.          
        try {
            //shared secret for signing. Use HMAC as we can do it with existing keys and not certs
            Builder claims = new JWTClaimsSet.Builder();
            claims.audience(params.get(OrcidOauth2Constants.CLIENT_ID_PARAM));
            claims.subject(accessToken.getAdditionalInformation().get("orcid").toString());
            claims.issuer("https://orcid.org");
            Date now = new Date();
            claims.expirationTime(new Date(now.getTime()+600000));
            claims.issueTime(now);
            claims.jwtID(UUID.randomUUID().toString());
            if (params.get(OrcidOauth2Constants.NONCE) != null)
                claims.claim(OrcidOauth2Constants.NONCE, params.get(OrcidOauth2Constants.NONCE));
            claims.claim(OrcidOauth2Constants.AUTH_TIME, profileEntityManager.getLastLogin(accessToken.getAdditionalInformation().get("orcid").toString()));
            
            Person person = personDetailsManagerReadOnly.getPublicPersonDetails(accessToken.getAdditionalInformation().get("orcid").toString());
            if (person.getName() != null){
                if (person.getName().getCreditName() != null){
                    claims.claim("name", person.getName().getCreditName().getContent());
                }
                if (person.getName().getFamilyName() != null){
                    claims.claim("family_name", person.getName().getFamilyName().getContent());
                }
                if (person.getName().getGivenNames() != null){
                    claims.claim("given_name", person.getName().getGivenNames().getContent());
                }            
            }
            
            SignedJWT signedJWT = keyManager.sign(claims.build());
            String idTok = signedJWT.serialize();
            accessToken.getAdditionalInformation().put(OrcidOauth2Constants.ID_TOKEN, idTok);
        } catch (JOSEException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        
        return accessToken;
        
    }
}
