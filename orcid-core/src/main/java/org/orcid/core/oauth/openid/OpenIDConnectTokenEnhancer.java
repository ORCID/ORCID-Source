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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
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
        //We check for a nonce and max_age which are added back into request by OrcidClientCredentialEndPointDelegatorImpl
        Map<String,String> params = authentication.getOAuth2Request().getRequestParameters();

        //only add if we're using openid scope.
        String scopes = params.get(OrcidOauth2Constants.SCOPE_PARAM);        
        if (PojoUtil.isEmpty(scopes) || !ScopePathType.getScopesFromSpaceSeparatedString(scopes).contains(ScopePathType.OPENID) ){
            return accessToken;
        }
        //TODO check persistentToken? (additionalinfo.get("persistent") is always false (set by OrcidTokenEnhancer).  Request grantPersistentToken is always true (set by spring).  LOL
        
        //inject the OpenID Connect "id_token" (authn).  This is distinct from the access token (authz), so is for transporting info to the client only
        //this means we do not have to support using them for authentication purposes. Some APIs support it, but it is not part of the spec.          
        try {
            String orcid = authentication.getName();
            Builder claims = new JWTClaimsSet.Builder();
            claims.audience(params.get(OrcidOauth2Constants.CLIENT_ID_PARAM));
            claims.subject(orcid);
            claims.claim("at_hash", createAccessTokenHash(accessToken.getValue()));
            //claims.subject(accessToken.getAdditionalInformation().get("orcid").toString());
            claims.issuer("https://orcid.org");
            Date now = new Date();
            claims.expirationTime(new Date(now.getTime()+600000));
            claims.issueTime(now);
            claims.jwtID(UUID.randomUUID().toString());
            if (params.get(OrcidOauth2Constants.NONCE) != null)
                claims.claim(OrcidOauth2Constants.NONCE, params.get(OrcidOauth2Constants.NONCE));
            claims.claim(OrcidOauth2Constants.AUTH_TIME, profileEntityManager.getLastLogin(orcid));
            
            Person person = personDetailsManagerReadOnly.getPublicPersonDetails(orcid);
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
    
    /** Access Token hash value. 
     * If the ID Token is issued with an access_token in an Implicit Flow, this is REQUIRED, 
     * which is the case for this subset of OpenID Connect. 
     * Its value is the base64url encoding of the left-most half of the hash of the octets of the ASCII 
     * representation of the access_token value, where the hash algorithm used is the hash algorithm 
     * used in the alg Header Parameter of the ID Token's JOSE Header. For instance, if the alg is RS256, 
     * hash the access_token value with SHA-256, then take the left-most 128 bits and base64url-encode them. 
     * The at_hash value is a case-sensitive string.
     * 
     * @param accessToken
     * @return
     */
    private String createAccessTokenHash(String accessToken){
        try {
            byte[] bytes = accessToken.getBytes("UTF-8");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();
            bytes = Arrays.copyOfRange(bytes, 0, 127);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }
}
