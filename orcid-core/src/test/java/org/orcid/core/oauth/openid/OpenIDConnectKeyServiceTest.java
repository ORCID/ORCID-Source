package org.orcid.core.oauth.openid;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.orcid.core.oauth.openid.OpenIDConnectKeyService.OpenIDConnectKeyServiceConfig;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class OpenIDConnectKeyServiceTest {

    private String testKey = "{\"keys\":[{\"kty\":\"RSA\",\"d\":\"JMSIq2mASGud93H9C6uBCd3hLnerIFwFj36oDodW2jb5kx6b-R1IAA8GD0mP-cRuNOle9S7xWhXeyZfo0eiZVh-CybyWU33HsuaHxC985mcOOjd2eEvr7dTvH5CrloQI_y-S3qlWQGHi0525xwDJh_fc3patnIGQtlG9mVHIS1GkmU_jjkR_e4-spa9v3sPZqf957LHuEYlcGoR3-oCTOhdIGpW1PPEgJYD7BfEMYwna8Y3k1ShpgfdQu4v-VwNX73SVRJqmEOdLIYE02QnrRjZm87pcX8AS0N1iyoRjwN917DgUsloT5sViYA5h1psUJLnmGDiPnilW1bG2FGvmwQ\",\"e\":\"AQAB\",\"use\":\"sig\",\"kid\":\"IntTestKey1\",\"alg\":\"RS256\",\"n\":\"jZPJIRAel6xY2VBET8lSNJFIcXupe92syQNNY676qzINv78_1JSAtkimgo0ihjuuzE_06HGwW48GMFpIdyWm0SUDzyMTrMtIoPk91G3_Pw0-5BvtOZ0IY6U54sHG86kdeAlqpSeaG2_k424fPIEAMR1-QxOd3AJdX6Nn1G58fzkQxRpf4XwZ4WxWAPJwQ-G2NbWzHQSINBaU5gL83Ggc3-DDfX2N5M8hRcOlcTeIWCWoboPfVTqeZe2T525Jtdjcim0syWocMCrgEC8Q7PmibaTvrQkX7AKatKS-8iKFlYfZvIEmDUVp6skV28Hc7TY3xLgC7GjCg0o4Gf1glQiHbw\"}]}";
    @Test
    public void testKeyGenAndSigning() throws JOSEException, NoSuchAlgorithmException, IOException, ParseException, URISyntaxException{
        OpenIDConnectKeyService.OpenIDConnectKeyServiceConfig config = new OpenIDConnectKeyServiceConfig();
        config.keyName = "IntTestKey1";
        config.jsonKey = testKey;
        OpenIDConnectKeyService service = new OpenIDConnectKeyService(config);
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("test", "abcd1234");
        JWTClaimsSet claims = new JWTClaimsSet.Builder().issuer("me").build();
        SignedJWT signed = service.sign(claims);
        Assert.assertTrue(service.verify(signed));

        JWSVerifier verifier = new RSASSAVerifier(((RSAKey)service.getPublicJWK().getKeyByKeyId(signed.getHeader().getKeyID())));
        Assert.assertTrue(signed.verify(verifier));
    }
}
