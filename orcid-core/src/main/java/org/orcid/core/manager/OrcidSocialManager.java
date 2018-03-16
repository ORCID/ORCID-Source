package org.orcid.core.manager;

public interface OrcidSocialManager {

    String getTwitterAuthorizationUrl(String orcid) throws Exception;

    void enableTwitter(String userOrcid, String pin) throws Exception;

    void disableTwitter(String userOrcid);

    boolean isTwitterEnabled(String userOrcid);

    void tweetLatestUpdates();
}
