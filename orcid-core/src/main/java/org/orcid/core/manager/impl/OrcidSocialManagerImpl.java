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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.ajax.JSON;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidSocialManager;
import org.orcid.persistence.dao.OrcidSocialDao;
import org.orcid.persistence.jpa.entities.OrcidSocialEntity;
import org.orcid.persistence.jpa.entities.OrcidSocialType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class OrcidSocialManagerImpl implements OrcidSocialManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidSocialManagerImpl.class);
    
    private static String TWITTER_USER_KEY = "twitter-key";
    private static String TWITTER_USER_SECRET = "twitter-secret";
    
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
        twitterCredentials.put(TWITTER_USER_KEY, token);
        twitterCredentials.put(TWITTER_USER_SECRET, verifier);
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
    private String decrypt(String unencrypted) {
        if (StringUtils.isNotBlank(unencrypted)) {
            return encryptionManager.decryptForInternalUse(unencrypted);
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
    
    /**
     * TODO
     * */
    @Override
    public void tweetLatestUpdates() {
        LOGGER.info("Start tweeting thread");
        List<OrcidSocialEntity> toTweet = orcidSocialDao.getRecordsToTweet();
        Calendar cal = Calendar.getInstance();
        cal.roll(Calendar.HOUR, false);
        Date oneHourBack = cal.getTime();
        for(OrcidSocialEntity entity : toTweet) {
            Date lastTimeTweeted = entity.getLastRun();
            if(lastTimeTweeted == null || lastTimeTweeted.before(oneHourBack)) {
                LOGGER.info("Tweeting profile {}", entity.getId().getOrcid());
                if (tweet(entity))
                    orcidSocialDao.updateLatestRunDate(entity.getId().getOrcid(), entity.getType());
            }             
        }
        LOGGER.info("Finished tweeting thread");
    }
    
    /**
     * TODO
     * */
    private boolean tweet(OrcidSocialEntity entity) {
        String jsonCredentials = decrypt(entity.getEncryptedCredentials());
        Map<String, String> credentials = (HashMap<String, String>)JSON.parse(jsonCredentials);
        Twitter twitter = new TwitterFactory().getInstance();

        twitter.setOAuthConsumer(twitterKey, twitterSecret);
        AccessToken accessToken = new AccessToken(credentials.get(TWITTER_USER_KEY), credentials.get(TWITTER_USER_SECRET));

        twitter.setOAuthAccessToken(accessToken);
        try {
            twitter.updateStatus("Post using Twitter4J Again " + System.currentTimeMillis());
        } catch(Exception e) {
            LOGGER.error("Unable to tweet on profile {}", entity.getId().getOrcid());
            return false;
        }
        
        return true;
    }
}
