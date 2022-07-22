package org.orcid.utils.jersey;

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

import org.orcid.utils.jersey.unmarshaller.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

public class OrcidJerseyClientHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidJerseyClientHandler.class);

    public static Client create(boolean isDevelopmentMode, Map<String, Object> properties) {
        Client client;
        ClientBuilder builder = ClientBuilder.newBuilder();
        builder.register(JacksonJaxbJsonProvider.class)
        .register(V2ActivitiesSummaryBodyReader.class)
        .register(V2EducationBodyReader.class)
        .register(V2EmploymentBodyReader.class)
        .register(V2FundingBodyReader.class)
        .register(V2OrcidErrorBodyReader.class)
        .register(V2PeerReviewBodyReader.class)
        .register(V2RecordBodyReader.class)
        .register(V2WorkBodyReader.class)
        .register(V3ActivitiesSummaryBodyReader.class)
        .register(V3DistinctionBodyReader.class)
        .register(V3EducationBodyReader.class)
        .register(V3EmploymentBodyReader.class)
        .register(V3FundingBodyReader.class)
        .register(V3InvitedPositionBodyReader.class)
        .register(V3MembershipBodyReader.class)
        .register(V3OrcidErrorBodyReader.class)
        .register(V3PeerReviewBodyReader.class)
        .register(V3QualificationBodyReader.class)
        .register(V3RecordBodyReader.class)
        .register(V3ResearchResourceBodyReader.class)
        .register(V3ResearchResourcesBodyReader.class)
        .register(V3ServiceBodyReader.class)
        .register(V3WorkBodyReader.class)
        .register(W3CDocumentBodyReader.class);
        
        if (isDevelopmentMode) {
            // DANGER!!! Trust all certs
            LOGGER.info("TRUSTING ALL SSL CERTS IN DEV MODE!!!");
            builder.hostnameVerifier(createHostnameVerifier()).sslContext(createSslContext());
        } 
        client = builder.build();
        Set<String> keyset = properties.keySet();
        for (String key : keyset) {
            client.property(key, properties.get(key));
        }
        return client;
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
