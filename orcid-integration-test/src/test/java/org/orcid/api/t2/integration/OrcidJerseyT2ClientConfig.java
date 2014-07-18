/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.t2.integration;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.beans.factory.InitializingBean;

import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 12/04/2012
 */
public class OrcidJerseyT2ClientConfig extends DefaultClientConfig implements InitializingBean {

    private KeyStore keyStore;
    private String keyStorePassword;
    private KeyStore trustStore;

    public OrcidJerseyT2ClientConfig(Set<Class<?>> providers) {
        super(providers);
    }

    public void setProperties(Map<String, Object> properties) {
        Set<String> keyset = properties.keySet();
        for (String key : keyset) {
            getProperties().put(key, properties.get(key));
        }
    }

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied
     * (and satisfied BeanFactoryAware and ApplicationContextAware).
     * <p>
     * This method allows the bean instance to perform initialization only
     * possible when all bean properties have been set and to throw an exception
     * in the event of misconfiguration.
     * 
     * @throws Exception
     *             in the event of misconfiguration (such as failure to set an
     *             essential property) or if initialization fails.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        SSLContext ctx = createSslContext();
        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(new HostnameVerifier() {

            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        }, ctx));
    }

    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public void setTrustStore(KeyStore trustStore) {
        this.trustStore = trustStore;
    }

    private SSLContext createSslContext() {
        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, keyStorePassword.toCharArray());
            KeyManager[] keyManagers = kmf.getKeyManagers();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            // Use the trustStore if present, otherwise default to keyStore.
            if (trustStore != null) {
                tmf.init(trustStore);
            } else {
                tmf.init(keyStore);
            }
            TrustManager[] trustManagers = tmf.getTrustManagers();
            SSLContext ssl = SSLContext.getInstance("TLS");
            ssl.init(keyManagers, trustManagers, new SecureRandom());
            return ssl;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

}
