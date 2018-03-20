package org.orcid.integration.api.t2;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.orcid.integration.api.t2.OrcidJerseyT2ClientConfig;

import com.sun.jersey.client.urlconnection.HTTPSProperties;

/**
 * @author Declan Newman (declan) Date: 12/04/2012
 */
public class OrcidJerseyT2ClientOAuthConfig extends OrcidJerseyT2ClientConfig {

    public OrcidJerseyT2ClientOAuthConfig(Set<Class<?>> providers) {
        super(providers);
        allowInvalidCerts();
    }

    public OrcidJerseyT2ClientOAuthConfig() {
        allowInvalidCerts();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        
    }

    private void allowInvalidCerts() {
        SSLContext ctx = createSslContext();
        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(new HostnameVerifier() {

            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        }, ctx));
    }

    private SSLContext createSslContext() {
        try {

            SSLContext ssl = SSLContext.getInstance("TLS");
            ssl.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
            return ssl;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    private static class DefaultTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

}
