package org.orcid.listener.jersey;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

public class OrcidJerseyClientHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidJerseyClientHandler.class);

    public static Client create(boolean isDevelopmentMode, Map<String, Object> properties) {
        Client c;
        if (isDevelopmentMode) {
            // DANGER!!! Trust all certs
            LOGGER.info("TRUSTING ALL SSL CERTS IN DEV MODE!!!");
            c = ClientBuilder.newBuilder().register(JacksonJaxbJsonProvider.class).hostnameVerifier(createHostnameVerifier()).sslContext(createSslContext()).build();
        } else {
            c = ClientBuilder.newClient().register(JacksonJaxbJsonProvider.class);
        }
        Set<String> keyset = properties.keySet();
        for (String key : keyset) {
            c.property(key, properties.get(key));
        }
        return c;
    }

    private static HostnameVerifier createHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession sslSession) {
                if (hostname.equals("localhost")) {
                    return true;
                }
                return false;
            }
        };
    }

    private static SSLContext createSslContext() {
        try {
            // DANGER!!! Accepts all certs!
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            } };
            SSLContext ssl = SSLContext.getInstance("TLS");
            ssl.init(null, trustAllCerts, new SecureRandom());
            return ssl;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }
}
