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
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.claims.AccessTokenHash;

/**
 * This class creates and appends JWT id_tokens to the response.
 * 
 * @author tom
 *
 */
public class OpenIDConnectTokenEnhancer implements TokenEnhancer {

    @Value("${org.orcid.core.baseUri}")
    private String path;

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
        // We check for a nonce and max_age which are added back into request by
        // OrcidClientCredentialEndPointDelegatorImpl
        Map<String, String> params = authentication.getOAuth2Request().getRequestParameters();

        // only add if we're using openid scope.
        // only add in implicit flow if response_type id_token is present
        String scopes = params.get(OrcidOauth2Constants.SCOPE_PARAM);
        if (PojoUtil.isEmpty(scopes) || !ScopePathType.getScopesFromSpaceSeparatedString(scopes).contains(ScopePathType.OPENID)) {
            return accessToken;
        }
        // inject the OpenID Connect "id_token" (authn). This is distinct from
        // the access token (authz), so is for transporting info to the client
        // only
        // this means we do not have to support using them for authentication
        // purposes. Some APIs support it, but it is not part of the spec.
        try {
            String orcid = authentication.getName();
            Builder claims = new JWTClaimsSet.Builder();
            claims.audience(params.get(OrcidOauth2Constants.CLIENT_ID_PARAM));
            claims.issuer(path);
            claims.subject("https://orcid.org"+"/"+orcid);
            claims.claim("id_path", orcid);
            claims.claim("at_hash", createAccessTokenHash(accessToken.getValue()));
            Date now = new Date();
            claims.expirationTime(new Date(now.getTime() + 600000));
            claims.issueTime(now);
            claims.jwtID(UUID.randomUUID().toString());
            if (params.get(OrcidOauth2Constants.NONCE) != null)
                claims.claim(OrcidOauth2Constants.NONCE, params.get(OrcidOauth2Constants.NONCE));
            claims.claim(OrcidOauth2Constants.AUTH_TIME, profileEntityManager.getLastLogin(orcid));

            Person person = personDetailsManagerReadOnly.getPublicPersonDetails(orcid);
            if (person.getName() != null) {
                if (person.getName().getCreditName() != null) {
                    claims.claim("name", person.getName().getCreditName().getContent());
                }
                if (person.getName().getFamilyName() != null) {
                    claims.claim("family_name", person.getName().getFamilyName().getContent());
                }
                if (person.getName().getGivenNames() != null) {
                    claims.claim("given_name", person.getName().getGivenNames().getContent());
                }
            }

            SignedJWT signedJWT = keyManager.sign(claims.build());
            String idTok = signedJWT.serialize();
            accessToken.getAdditionalInformation().put(OrcidOauth2Constants.ID_TOKEN, idTok);
        } catch (JOSEException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return accessToken;

    }

    /**
     * Access Token hash value. If the ID Token is issued with an access_token
     * in an Implicit Flow, this is REQUIRED, which is the case for this subset
     * of OpenID Connect. Its value is the base64url encoding of the left-most
     * half of the hash of the octets of the ASCII representation of the
     * access_token value, where the hash algorithm used is the hash algorithm
     * used in the alg Header Parameter of the ID Token's JOSE Header. For
     * instance, if the alg is RS256, hash the access_token value with SHA-256,
     * then take the left-most 128 bits and base64url-encode them. The at_hash
     * value is a case-sensitive string.
     * 
     * @param accessToken
     * @return
     */
    private String createAccessTokenHash(String accessToken) {
        return AccessTokenHash.compute(new BearerAccessToken(accessToken), JWSAlgorithm.RS256).toString();
    }
}
