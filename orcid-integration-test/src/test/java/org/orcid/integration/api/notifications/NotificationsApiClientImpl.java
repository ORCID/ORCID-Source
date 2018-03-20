package org.orcid.integration.api.notifications;

import static org.orcid.core.api.OrcidApiConstants.PERMISSIONS_PATH;
import static org.orcid.core.api.OrcidApiConstants.PERMISSIONS_VIEW_PATH;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriBuilder;

import org.orcid.api.common.OrcidClientHelper;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Will Simpson
 *
 */
public class NotificationsApiClientImpl<NOTIFICATION> {

    private OrcidClientHelper orcidClientHelper;

    public NotificationsApiClientImpl(URI baseUri, Client c) throws URISyntaxException {
        orcidClientHelper = new OrcidClientHelper(baseUri, c);
    }

    public ClientResponse viewPermissionNotificationsHtml(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    public ClientResponse viewPermissionNotificationsXml(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    public ClientResponse viewPermissionNotificationsJson(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    public ClientResponse viewPermissionNotificationXml(String orcid, Long id, String accessToken) {
        return orcidClientHelper.getClientResponseWithToken(UriBuilder.fromPath(PERMISSIONS_VIEW_PATH).build(orcid, id), VND_ORCID_XML, accessToken);
    }

    public ClientResponse viewPermissionNotificationJson(String orcid, Long id, String accessToken) {
        return orcidClientHelper.getClientResponseWithToken(UriBuilder.fromPath(PERMISSIONS_VIEW_PATH).build(orcid, id), VND_ORCID_JSON, accessToken);
    }

    public ClientResponse flagAsArchivedPermissionNotificationXml(String orcid, Long id, String accessToken) throws OrcidNotificationAlreadyReadException {
        return orcidClientHelper.deleteClientResponseWithToken(UriBuilder.fromPath(PERMISSIONS_VIEW_PATH).build(orcid, id), VND_ORCID_XML, accessToken);
    }

    public ClientResponse flagAsArchivedPermissionNotificationJson(String orcid, Long id, String accessToken) throws OrcidNotificationAlreadyReadException {
        return orcidClientHelper.deleteClientResponseWithToken(UriBuilder.fromPath(PERMISSIONS_VIEW_PATH).build(orcid, id), VND_ORCID_JSON, accessToken);
    }

    public ClientResponse addPermissionNotificationXml(String orcid, NOTIFICATION notification, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(PERMISSIONS_PATH).build(orcid), VND_ORCID_XML, notification, accessToken);
    }

    public ClientResponse addPermissionNotificationJson(String orcid, NOTIFICATION notification, String accessToken) {
        return orcidClientHelper.postClientResponseWithToken(UriBuilder.fromPath(PERMISSIONS_PATH).build(orcid), VND_ORCID_JSON, notification, accessToken);
    }

}
