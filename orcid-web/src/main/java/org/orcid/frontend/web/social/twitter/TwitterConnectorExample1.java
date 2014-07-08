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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterConnectorExample1 {

    static String consumerKeyStr = "soCTKWWByfjq91SxuaQRh4Gnk";
    static String consumerSecretStr = "sjtMHV2myGQ6qZAoKROoKaNfvRFvyDtIuGn0cKdy5h0RQ55NPM";
    // These two should be saved on database
    static String accessTokenStr = "211510093-Vw2ex1DUOyMKr3G6VZjmad8wbYCanxmSBpMkGb6r";
    static String accessTokenSecretStr = "1VqVc9dwk3AQUkDnidpRE6q9NIEC4wkRrD4fUZqmlXQes";

    /**
     * @param args
     * @throws TwitterException
     */
    public static void main(String[] args) throws TwitterException, IOException {
        // The factory instance is re-useable and thread safe.
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer("soCTKWWByfjq91SxuaQRh4Gnk", "sjtMHV2myGQ6qZAoKROoKaNfvRFvyDtIuGn0cKdy5h0RQ55NPM");
        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (null == accessToken) {
            System.out.println("Open the following URL and grant access to your account:");
            System.out.println(requestToken.getAuthorizationURL());
            System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
            String pin = br.readLine();
            try {
                if (pin.length() > 0) {
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                } else {
                    accessToken = twitter.getOAuthAccessToken();
                }
            } catch (TwitterException te) {
                if (401 == te.getStatusCode()) {
                    System.out.println("Unable to get the access token.");
                } else {
                    te.printStackTrace();
                }
            }
        }
        // persist to the accessToken for future reference.
        System.out.println(twitter.verifyCredentials().getId() + " -> " + accessToken);
        Status status = twitter.updateStatus("Playing with twitter oauth " + System.currentTimeMillis());
        System.out.println("Successfully updated the status to [" + status.getText() + "].");

        System.out.println("--------------------------------------------------------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        System.out.println(accessToken.getToken());
        System.out.println(accessToken.getTokenSecret());
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------------------------------------------------------");

        System.exit(0);
    }

}
