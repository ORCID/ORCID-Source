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
package org.orcid.core.manager.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.ajax.JSON;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidSocialManager;
import org.orcid.persistence.dao.OrcidSocialDao;
import org.orcid.persistence.jpa.entities.OrcidSocialType;
import org.springframework.beans.factory.annotation.Value;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

public class OrcidSocialManagerImpl implements OrcidSocialManager {

    private static String TWITTER_KEY = "twitter-key";
    private static String TWITTER_SECRET = "twitter-secret";
    
    @Value("${org.orcid.core.twitter.key}")
    private String twitterKey;
    
    @Value("${org.orcid.core.twitter.secret}")
    private String twitterSecret;
    
    @Resource
    private EncryptionManager encryptionManager;
    
    @Resource
    private OrcidSocialDao orcidSocialDao;
    
    /**
     * Get the authorization URL for twitter
     * @return the url where the user should authorize twitter
     * */
    @Override
    public String getTwitterAuthorizationUrl() throws Exception {
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(twitterKey, twitterSecret);
        RequestToken requestToken = twitter.getOAuthRequestToken();
        return requestToken.getAuthorizationURL();
    }        
    
    /**
     * TODO
     * */
    @Override
    public void enableTwitter(String userOrcid, String token, String verifier) {
        String credentials = generateEncryptedTwitterCredentials(token, verifier);
        orcidSocialDao.save(userOrcid, OrcidSocialType.TWITTER, credentials);
    }
    
    /**
     * TODO
     * */
    private String generateEncryptedTwitterCredentials(String token, String verifier) {
        Map<String, String> twitterCredentials = new HashMap<String, String>();
        twitterCredentials.put(TWITTER_KEY, token);
        twitterCredentials.put(TWITTER_SECRET, verifier);
        String twitterJsonCredentials = JSON.toString(twitterCredentials);
        return encrypt(twitterJsonCredentials);
    }
        
    /**
     * TODO
     * */
    private String encrypt(String unencrypted) {
        if (StringUtils.isNotBlank(unencrypted)) {
            return encryptionManager.encryptForInternalUse(unencrypted);
        } else {
            return null;
        }
    }
    
    /**
     * TODO
     * */
    @Override
    public void disableTwitter(String userOrcid) {
        orcidSocialDao.delete(userOrcid, OrcidSocialType.TWITTER);
    }
    
    /**
     * TODO
     * */
    @Override
    public boolean isTwitterEnabled(String userOrcid) {
        return orcidSocialDao.isEnabled(userOrcid, OrcidSocialType.TWITTER);
    }
}
