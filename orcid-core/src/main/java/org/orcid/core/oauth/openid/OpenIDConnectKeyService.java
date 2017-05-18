package org.orcid.core.oauth.openid;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Component
public class OpenIDConnectKeyService {

    private final String keyID = "OpenIDConnectKey1";
    private final RSAKey publicJWK;
    private final RSAKey privateJWK;
    private final JWSAlgorithm defaultAlg = JWSAlgorithm.RS256;
        
    /** Use a configured key ${org.orcid.openid.jwks_location} or generate a random key
     * 
     * New keys can be generated using this: https://mkjwk.org/ or a command line tool found here: https://connect2id.com/products/nimbus-jose-jwt/generator
     * @throws NoSuchAlgorithmException
     * @throws ParseException 
     * @throws IOException 
     */
    public OpenIDConnectKeyService(@Value("${org.orcid.openid.jwks_location}") String jwksLocation) throws NoSuchAlgorithmException, IOException, ParseException{
        if (jwksLocation !=null && !jwksLocation.isEmpty()){
            //use a configured key.
            JWKSet keys = JWKSet.load(new File(jwksLocation));
            privateJWK = (RSAKey) keys.getKeyByKeyId(keyID);
            publicJWK = privateJWK.toPublicJWK();
            
        }else{
            //generate a random key.  Fine for testing.
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
            keyGenerator.initialize(1024);
            KeyPair kp = keyGenerator.genKeyPair();     
            privateJWK = new RSAKey.Builder((RSAPublicKey)kp.getPublic())
                    .privateKey((RSAPrivateKey)kp.getPrivate())
                    .keyUse(KeyUse.SIGNATURE)
                    .keyID(getDefaultKeyID())
                    .algorithm(defaultAlg)
                    .build();
            
            publicJWK = privateJWK.toPublicJWK();
            /*new RSAKey.Builder((RSAPublicKey)kp.getPublic())
                    .keyID(getDefaultKeyID())
                    .keyUse(KeyUse.SIGNATURE)
                    .algorithm(defaultAlg)
                    .build();*/
        }

        
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
        
        /* For HMAC we could do:
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
    public JWK getPublicJWK(){
        return publicJWK;
    }
    
}
