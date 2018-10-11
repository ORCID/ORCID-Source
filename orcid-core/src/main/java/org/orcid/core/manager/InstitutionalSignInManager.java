package org.orcid.core.manager;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.orcid.pojo.HeaderCheckResult;
import org.orcid.pojo.RemoteUser;

public interface InstitutionalSignInManager {

    static final String[] POSSIBLE_REMOTE_USER_HEADERS = new String[] { "persistent-id", "edu-person-unique-id", "targeted-id-oid", "targeted-id" };

    static final String SHIB_IDENTITY_PROVIDER_HEADER = "shib-identity-provider";

    static final String EPPN_HEADER = "eppn";

    static final String DISPLAY_NAME_HEADER = "displayname";

    static final String GIVEN_NAME_HEADER = "givenname";

    static final String SN_HEADER = "sn";

    void createUserConnectionAndNotify(String idType, String remoteUserId, String displayName, String providerId, String userOrcid, Map<String, String> headers)
            throws UnsupportedEncodingException;

    void sendNotification(String userOrcid, String providerId) throws UnsupportedEncodingException;

    HeaderCheckResult checkHeaders(Map<String, String> originalHeaders, Map<String, String> currentHeaders);

    RemoteUser retrieveRemoteUser(Map<String, String> headers);

    String retrieveDisplayName(Map<String, String> headers);

    String retrieveFirstName(Map<String, String> headers);

    String retrieveLastName(Map<String, String> headers);
}
