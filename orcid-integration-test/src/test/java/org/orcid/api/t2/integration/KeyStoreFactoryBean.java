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

/*
 * Copyright 2005-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * Spring factory bean for a {@link KeyStore}.
 * <p/>
 * To load an existing key store, you must set the <code>location</code>
 * property. If this property is not set, a new, empty key store is created,
 * which is most likely not what you want.
 * 
 * Moved from spring-ws-security as it is not present in the latest version
 * 
 * @author Arjen Poutsma
 * @see #setLocation(org.springframework.core.io.Resource)
 * @see KeyStore
 * @since 1.0.0
 */
public class KeyStoreFactoryBean implements FactoryBean<KeyStore>, InitializingBean {

    private static final Log logger = LogFactory.getLog(KeyStoreFactoryBean.class);

    private KeyStore keyStore;

    private String type;

    private String provider;

    private Resource location;

    private char[] password;

    /**
     * Sets the location of the key store to use. If this is not set, a new,
     * empty key store will be used.
     * 
     * @see KeyStore#load(java.io.InputStream,char[])
     */
    public void setLocation(Resource location) {
        this.location = location;
    }

    /**
     * Sets the password to use for integrity checking. If this property is not
     * set, then integrity checking is not performed.
     */
    public void setPassword(String password) {
        if (password != null) {
            this.password = password.toCharArray();
        }
    }

    /**
     * Sets the provider of the key store to use. If this is not set, the
     * default is used.
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * Sets the type of the <code>KeyStore</code> to use. If this is not set,
     * the default is used.
     * 
     * @see KeyStore#getDefaultType()
     */
    public void setType(String type) {
        this.type = type;
    }

    public KeyStore getObject() {
        return keyStore;
    }

    public Class<KeyStore> getObjectType() {
        return KeyStore.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public final void afterPropertiesSet() throws GeneralSecurityException, IOException {
        if (StringUtils.hasLength(provider) && StringUtils.hasLength(type)) {
            keyStore = KeyStore.getInstance(type, provider);
        } else if (StringUtils.hasLength(type)) {
            keyStore = KeyStore.getInstance(type);
        } else {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        }
        InputStream is = null;
        try {
            if (location != null && location.exists()) {
                is = location.getInputStream();
                if (logger.isInfoEnabled()) {
                    logger.info("Loading key store from " + location);
                }
            } else if (logger.isWarnEnabled()) {
                logger.warn("Creating empty key store");
            }
            keyStore.load(is, password);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
