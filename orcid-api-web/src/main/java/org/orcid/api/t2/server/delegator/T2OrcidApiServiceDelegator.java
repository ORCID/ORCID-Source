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
package org.orcid.api.t2.server.delegator;

import org.orcid.api.common.delegator.OrcidApiServiceDelegator;
import org.orcid.jaxb.model.message.OrcidMessage;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * 2011-2012 ORCID
 * <p/>
 * This class (unlike the
 * {@link org.orcid.api.common.delegator.impl.OrcidApiServiceDelegatorImpl})
 * should return the content dependent on the privileges assigned to that user
 * or client.
 * <p/>
 * Perhaps as importantly, this is the class that should know if the client is
 * that of a 'super user' that has been identified using a certificate
 * 
 * @author Declan Newman (declan) Date: 07/03/2012
 */
public interface T2OrcidApiServiceDelegator extends OrcidApiServiceDelegator {

    /**
     * Creates a new profile and returns the saved representation of it. The
     * response should include the 'location' to retrieve the newly created
     * profile from.
     * 
     * @param orcidMessage
     *            the message to be saved. If the message already contains an
     *            ORCID value a 400 Bad Request
     * @return if the creation was successful, returns a 201 along with the
     *         location of the newly created resource otherwise returns the 400
     *         Bad Request
     */
    Response createProfile(UriInfo uriInfo, OrcidMessage orcidMessage);

    /**
     * Takes the {@link OrcidMessage} and attempts to update the bio details
     * only. If there is content other than the bio in the message, it should
     * return a 400 Bad Request. Privilege checks will be performed to determine
     * if the client or user has permissions to perform the update.
     * 
     * @param orcidMessage
     *            the message containing the bio to be updated. Any elements
     *            outside of the bio will cause a 400 Bad Request to be returned
     * @return if the update was successful, a 200 response will be returned
     *         with the updated
     */
    Response updateBioDetails(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage);

    /**
     * Add works to an existing ORCID profile. If the profile already contains
     * an identifiable work with the same id a 409 Conflict should be returned.
     * 
     * @param orcidMessage
     *            the message containing the works to be added
     * @return if the works were all added successfully, a 201 with a location
     *         should be returned
     */
    Response addWorks(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage);

    /**
     * Update the works for a given ORCID profile. This will cause all content
     * to be overwritten
     * 
     * @param orcidMessage
     *            the message containing all works to overwritten. If any other
     *            elements outside of the works are present, a 400 Bad Request
     *            is returned
     * @return
     */
    Response updateWorks(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage);

    /**
     * Add new external identifiers to the profile. As with all calls, if the
     * message contains any other elements, a 400 Bad Request will be returned.
     * 
     * @param orcidMessage
     *            the message congtaining the external ids
     * @return If successful, returns a 200 OK with the updated content.
     */
    Response addExternalIdentifiers(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage);

    /**
     * Delete profile from Orcid.
     * 
     * @param uriInfo
     * @param orcid
     * @return
     */
    Response deleteProfile(UriInfo uriInfo, String orcid);

    /**
     * Register a new webhook to the profile. As with all calls, if the message
     * contains any other elements, a 400 Bad Request will be returned.
     * 
     * @param orcid
     *            the identifier of the profile to add the webhook
     * @param webhookUri
     *            uri of the webhook
     * @return If successful, returns a 200 OK.
     * */
    Response registerWebhook(String orcid, String webhookUri);

    /**
     * Unregister a webhook from a profile. As with all calls, if the message
     * contains any other elements, a 400 Bad Request will be returned.
     * 
     * @param orcid
     *            the identifier of the profile to unregister the webhook
     * @param webhookUri
     *            uri of the webhook
     * @return If successful, returns a 200 OK.
     * */
    Response unregisterWebhook(String orcid, String webhookUri);
}
