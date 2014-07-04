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
package org.orcid.frontend.web.social.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterConnector {

    static String consumerKeyStr = "soCTKWWByfjq91SxuaQRh4Gnk";
    static String consumerSecretStr = "sjtMHV2myGQ6qZAoKROoKaNfvRFvyDtIuGn0cKdy5h0RQ55NPM";
    // These two should be saved on database
    static String accessTokenStr = "2604224184-JEW89PiNZrEjVCyHOvOuYjilPxU2criQbQtxMaA";
    static String accessTokenSecretStr = "CaWVUpeQI8f2NMH5AKluOsPMzewlz1WMfSWtghz6t6BHa";

    
    public static void postToTwitter() {
        try {
            Twitter twitter = new TwitterFactory().getInstance();

            twitter.setOAuthConsumer(consumerKeyStr, consumerSecretStr);
            AccessToken accessToken = new AccessToken(accessTokenStr, accessTokenSecretStr);

            twitter.setOAuthAccessToken(accessToken);

            twitter.updateStatus("Post using Twitter4J Again " + System.currentTimeMillis());

            System.out.println("Successfully updated the status in Twitter.");
        } catch (TwitterException te) {
            te.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        for(int i = 0; i < 100; i++) {
            TwitterConnector.postToTwitter();
            Thread.sleep(5000);
        }        
    }

}
