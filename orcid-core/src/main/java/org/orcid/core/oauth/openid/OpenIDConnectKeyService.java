package org.orcid.core.oauth.openid;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Component
public class OpenIDConnectKeyService {

    private final KeyPair kp;
    private final String keyID = "OpenIDConnectKey1";
    private final JWK publicJWK;
    //private final JWK privateJWK;
    private final JWSAlgorithm defaultAlg = JWSAlgorithm.RS256;
    
    
    /** Generate a random key for now
     * @throws NoSuchAlgorithmException
     */
    public OpenIDConnectKeyService() throws NoSuchAlgorithmException{
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(1024);
        kp = keyGenerator.genKeyPair();
     
        
        publicJWK = new RSAKey.Builder((RSAPublicKey)kp.getPublic())
                .keyID(getDefaultKeyID())
                .keyUse(KeyUse.SIGNATURE)
                /*We could also support HMAC*/
                .algorithm(defaultAlg)
                .build();
        
        /*privateJWK = new RSAKey.Builder((RSAPublicKey)kp.getPublic())
                .privateKey((RSAPrivateKey)kp.getPrivate())
                .keyID(getDefaultKeyID())
                .build();*/
    }
    
    /** Get the private key for signing
     * 
     * @return
     * @throws JOSEException 
     */
    public SignedJWT sign(JWTClaimsSet claims) throws JOSEException{
        JWSSigner signer = new RSASSASigner(kp.getPrivate());
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
