package org.orcid.core.oauth.openid;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class OpenIDConnectKeyService {

    private final String keyID;
    private final RSAKey publicJWK;
    private final RSAKey privateJWK;
    private final JWSAlgorithm defaultAlg = JWSAlgorithm.RS256;
    
    public static class OpenIDConnectKeyServiceConfig{
        String jwksLocation;
        public String getJwksLocation() {
            return jwksLocation;
        }
        public void setJwksLocation(String jwksLocation) {
            this.jwksLocation = jwksLocation;
        }
        public String getJsonKey() {
            return jsonKey;
        }
        public void setJsonKey(String jsonKey) {
            this.jsonKey = jsonKey;
        }
        public String getKeyName() {
            return keyName;
        }
        public void setKeyName(String keyName) {
            this.keyName = keyName;
        }
        String jsonKey;
        String keyName;
    }
    
    /** Use a configured key ${org.orcid.openid.jwksLocation} or ${org.orcid.openid.jwksTestKey} + ${org.orcid.openid.jwksKeyName}
     * 
     * New keys can be generated using this: https://mkjwk.org/ or a command line tool found here: https://connect2id.com/products/nimbus-jose-jwt/generator
     * @throws NoSuchAlgorithmException
     * @throws ParseException 
     * @throws IOException 
     * @throws URISyntaxException 
     */
    public OpenIDConnectKeyService(OpenIDConnectKeyServiceConfig config) throws NoSuchAlgorithmException, IOException, ParseException, URISyntaxException{
        if (config.jwksLocation !=null && !config.jwksLocation.isEmpty() && config.keyName!=null && !config.keyName.isEmpty()){
            //use a configured key.
            this.keyID = config.keyName;
            JWKSet keys = JWKSet.load(new File(config.jwksLocation));
            privateJWK = (RSAKey) keys.getKeyByKeyId(keyID);
            publicJWK = privateJWK.toPublicJWK();
        }else if (config.jsonKey!=null){
            //use a key embedded in the properties file
            this.keyID = config.keyName;
            JWKSet keys = JWKSet.parse(config.jsonKey);
            privateJWK = (RSAKey) keys.getKeyByKeyId(keyID);
            publicJWK = privateJWK.toPublicJWK();
        }else
            throw new RuntimeException("OpenID jwks not configured!");
    }
    
    /** Get the private key for signing
     * 
     * @return
     * @throws JOSEException 
     */
    public SignedJWT sign(JWTClaimsSet claims) throws JOSEException{
        JWSSigner signer = new RSASSASigner(privateJWK);
        JWSHeader.Builder head = new JWSHeader.Builder(defaultAlg);
        head.keyID(getDefaultKeyID());
        SignedJWT signedJWT = new SignedJWT(head.build(), claims);
        signedJWT.sign(signer);        
        return signedJWT;
        
        /* For HMAC we could do the following.  This may be useful for the implicit flow:
        ClientDetailsEntity clientEntity = clientDetailsEntityCacheManager.retrieve(authentication.getOAuth2Request().getClientId());
        JWSSigner signer = new MACSigner(StringUtils.rightPad(clientEntity.getDecryptedClientSecret(), 32, "#").getBytes());
        signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims.build());
        signedJWT.sign(signer);     
         */
    }
    
    /** Get the key ID we'll be using
     * 
     * @return
     */
    public String getDefaultKeyID(){
        return keyID;
    }
    
    /** get the Json Web Key representation of the public key
     * 
     * @return a JWK.  use .toString() to generate JSON representation.
     */
    public JWKSet getPublicJWK(){
        return new JWKSet(publicJWK);
    }
    
}
