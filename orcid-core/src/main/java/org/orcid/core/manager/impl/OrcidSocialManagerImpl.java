package org.orcid.core.manager.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.ajax.JSON;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidSocialManager;
import org.orcid.persistence.dao.OrcidSocialDao;
import org.orcid.persistence.jpa.entities.OrcidSocialEntity;
import org.orcid.persistence.jpa.entities.OrcidSocialType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

@Service("orcidSocialManager")
public class OrcidSocialManagerImpl implements OrcidSocialManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidSocialManagerImpl.class);

    private static String TWITTER_USER_KEY = "twitter-key";
    private static String TWITTER_USER_SECRET = "twitter-secret";

    @Value("${org.orcid.core.baseUri:http://orcid.org}")
    private String baseUri;

    @Value("${org.orcid.core.twitter.key}")
    private String twitterKey;

    @Value("${org.orcid.core.twitter.secret}")
    private String twitterSecret;

    @Value("${org.orcid.core.twitter.enabled:true}")
    private boolean isTwitterEnabled;
    
    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private OrcidSocialDao orcidSocialDao;

    @Resource
    private LocaleManager localeManager;

    private Map<String, RequestToken> requestTokenMap = new HashMap<String, RequestToken>();

    /**
     * Get the twitter RequestToken
     * 
     * @return The twitter RequestToken
     * */
    private RequestToken getTwitterRequestToken(String orcid) throws Exception {
        // If it exists, use it once and discard it
        if (requestTokenMap.containsKey(orcid)) {
            RequestToken result = requestTokenMap.get(orcid);
            requestTokenMap.remove(orcid);
            return result;
        } else {
            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(twitterKey, twitterSecret);
            RequestToken requestToken = twitter.getOAuthRequestToken();
            requestTokenMap.put(orcid, requestToken);
            return requestToken;
        }
    }

    /**
     * Get the twitter AccessToken
     * 
     * @return The twitter AccessToken
     * */
    private AccessToken getOAuthAccessToken(String orcid, String pin) throws Exception {
        RequestToken requestToken = getTwitterRequestToken(orcid);
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(twitterKey, twitterSecret);
        return twitter.getOAuthAccessToken(requestToken, pin);
    }

    /**
     * Return the URL where the user should authorize access
     * 
     * @param orcid
     *            the user orcid
     * @return the twitter URL where the user should authorize access
     * */
    public String getTwitterAuthorizationUrl(String orcid) throws Exception {
        RequestToken token = getTwitterRequestToken(orcid);
        return token.getAuthorizationURL();
    }

    /**
     * Enables twitter on the user profile
     * 
     * @param orcid
     * @param pin
     *            oauth_verifier parameter that comes from twitter request
     * */
    @Override
    public void enableTwitter(String userOrcid, String pin) throws Exception {
        AccessToken accessToken = getOAuthAccessToken(userOrcid, pin);
        String credentials = generateEncryptedTwitterCredentials(accessToken.getToken(), accessToken.getTokenSecret());
        orcidSocialDao.save(userOrcid, OrcidSocialType.TWITTER, credentials);
    }

    /**
     * Generate a JSON string with the credentials and return the string
     * encrypted
     * 
     * @param userKey
     * @param userSecret
     * @return an encrypted string that contains the user key and secret in a
     *         json string
     * */
    private String generateEncryptedTwitterCredentials(String userKey, String userSecret) {
        Map<String, String> twitterCredentials = new HashMap<String, String>();
        twitterCredentials.put(TWITTER_USER_KEY, userKey);
        twitterCredentials.put(TWITTER_USER_SECRET, userSecret);
        String twitterJsonCredentials = JSON.toString(twitterCredentials);
        return encrypt(twitterJsonCredentials);
    }

    /**
     * Encrypts a string
     * 
     * @param unencrypted
     * @return the string encrypted
     * */
    private String encrypt(String unencrypted) {
        if (StringUtils.isNotBlank(unencrypted)) {
            return encryptionManager.encryptForInternalUse(unencrypted);
        } else {
            return null;
        }
    }

    /**
     * Decrypts a string
     * 
     * @param encrypted
     *            string
     * @return the unencrypted string
     * */
    private String decrypt(String encrypted) {
        if (StringUtils.isNotBlank(encrypted)) {
            return encryptionManager.decryptForInternalUse(encrypted);
        } else {
            return null;
        }
    }

    /**
     * Removes the twitter access from the user profile
     * 
     * @param userOrcid
     *            the profile to disable twitter
     * */
    @Override
    public void disableTwitter(String userOrcid) {
        orcidSocialDao.delete(userOrcid, OrcidSocialType.TWITTER);
    }

    /**
     * Checks if twitter is enabled in the given profile
     * 
     * @param userOrcid
     * @return true if the profile has twitter enabled
     * */
    @Override
    public boolean isTwitterEnabled(String userOrcid) {
        return orcidSocialDao.isEnabled(userOrcid, OrcidSocialType.TWITTER);
    }

    /**
     * Will be invoked by a cron job to tweet in the modified profiles
     * */
    @Override
    public void tweetLatestUpdates() {
        LOGGER.info("Start tweeting thread");
        if(isTwitterEnabled) {
            List<OrcidSocialEntity> toTweet = orcidSocialDao.getRecordsToTweet();
            Calendar cal = Calendar.getInstance();
            cal.roll(Calendar.HOUR, false);
            Date oneHourBack = cal.getTime();
            for (OrcidSocialEntity entity : toTweet) {
                Date lastTimeTweeted = entity.getLastRun();
                if (lastTimeTweeted == null || lastTimeTweeted.before(oneHourBack)) {
                    LOGGER.info("Tweeting profile {}", entity.getId().getOrcid());
                    if(lastTimeTweeted == null || entity.getProfile().getLastModified().after(lastTimeTweeted)) {
                        if (tweet(entity))
                            orcidSocialDao.updateLatestRunDate(entity.getId().getOrcid(), entity.getType());
                    }                
                }
            }
        } else {
            LOGGER.info("Twitter is disabled in this environment");
        }        
        LOGGER.info("Finished tweeting thread");
    }

    /**
     * Tweet a message to the specified profile
     * 
     * @param entity
     *            An entity containing the user information and the twitter
     *            credentials
     * @return true if it was able to tweet the updates
     * */
    @SuppressWarnings("unchecked")
    private boolean tweet(OrcidSocialEntity entity) {
        String jsonCredentials = decrypt(entity.getEncryptedCredentials());
        Map<String, String> credentials = (HashMap<String, String>) JSON.parse(jsonCredentials);
        Twitter twitter = new TwitterFactory().getInstance();

        twitter.setOAuthConsumer(twitterKey, twitterSecret);
        AccessToken accessToken = new AccessToken(credentials.get(TWITTER_USER_KEY), credentials.get(TWITTER_USER_SECRET));

        twitter.setOAuthAccessToken(accessToken);
        try {
            twitter.updateStatus(buildUpdateMessage(entity.getId().getOrcid()));
        } catch (Exception e) {
            LOGGER.error("Unable to tweet on profile {}", entity.getId().getOrcid());
            return false;
        }

        return true;
    }

    /**
     * Builds the message to be tweeted
     * 
     * @param orcid
     * @return the message the will be tweeted
     * */
    private String buildUpdateMessage(String orcid) {
        String path = baseUri + '/' + orcid;
        return localeManager.resolveMessage("orcid_social.twitter.updated_message", path);
    }
}
