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
package org.orcid.api.t2.server.delegator.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.orcid.api.common.exception.OrcidBadRequestException;
import org.orcid.api.t2.server.delegator.T2OrcidApiServiceDelegator;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.manager.ValidationManager;
import org.orcid.core.version.OrcidMessageVersionConverterChain;
import org.orcid.jaxb.model.message.OrcidMessage;

/**
 * 
 * @author Will Simpson
 * 
 */
public class T2OrcidApiServiceVersionedDelegatorImpl implements T2OrcidApiServiceDelegator {

    @Resource(name = "orcidT2ServiceDelegator")
    private T2OrcidApiServiceDelegator t2OrcidApiServiceDelegator;

    @Resource
    private OrcidMessageVersionConverterChain orcidMessageVersionConverterChain;

    private ValidationManager incomingValidationManager;

    private ValidationManager outgoingValidationManager;

    private String externalVersion;

    public void setExternalVersion(String externalVersion) {
        this.externalVersion = externalVersion;
    }

    public void setIncomingValidationManager(ValidationManager incomingValidationManager) {
        this.incomingValidationManager = incomingValidationManager;
    }

    public void setOutgoingValidationManager(ValidationManager outgoingValidationManager) {
        this.outgoingValidationManager = outgoingValidationManager;
    }

    @Override
    public Response viewStatusText() {
        return t2OrcidApiServiceDelegator.viewStatusText();
    }

    @Override
    public Response findBioDetails(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findBioDetails(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findBioDetailsFromPublicCache(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findBioDetailsFromPublicCache(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findExternalIdentifiers(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findExternalIdentifiers(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findExternalIdentifiersFromPublicCache(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findExternalIdentifiersFromPublicCache(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findFullDetails(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findFullDetails(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findFullDetailsFromPublicCache(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findFullDetailsFromPublicCache(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findWorksDetails(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findWorksDetails(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response findWorksDetailsFromPublicCache(String orcid) {
        Response response = t2OrcidApiServiceDelegator.findWorksDetailsFromPublicCache(orcid);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response searchByQuery(Map<String, List<String>> queryMap) {
        Response response = t2OrcidApiServiceDelegator.searchByQuery(queryMap);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response createProfile(UriInfo uriInfo, OrcidMessage orcidMessage) {
        validateIncomingMessage(orcidMessage);
        OrcidMessage upgradedMessage = upgradeMessage(orcidMessage);
        return t2OrcidApiServiceDelegator.createProfile(uriInfo, upgradedMessage);
    }

    @Override
    public Response updateBioDetails(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        validateIncomingMessage(orcidMessage);
        OrcidMessage upgradedMessage = upgradeMessage(orcidMessage);
        Response response = t2OrcidApiServiceDelegator.updateBioDetails(uriInfo, orcid, upgradedMessage);
        return downgradeAndValidateResponse(response);
    }

    @Override
    public Response addWorks(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        validateIncomingMessage(orcidMessage);
        OrcidMessage upgradedMessage = upgradeMessage(orcidMessage);
        return t2OrcidApiServiceDelegator.addWorks(uriInfo, orcid, upgradedMessage);
    }

    @Override
    public Response updateWorks(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        validateIncomingMessage(orcidMessage);
        OrcidMessage upgradedMessage = upgradeMessage(orcidMessage);
        return t2OrcidApiServiceDelegator.updateWorks(uriInfo, orcid, upgradedMessage);
    }

    @Override
    public Response addExternalIdentifiers(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        validateIncomingMessage(orcidMessage);
        OrcidMessage upgradedMessage = upgradeMessage(orcidMessage);
        return t2OrcidApiServiceDelegator.addExternalIdentifiers(uriInfo, orcid, upgradedMessage);
    }

    @Override
    public Response deleteProfile(UriInfo uriInfo, String orcid) {
        return t2OrcidApiServiceDelegator.deleteProfile(uriInfo, orcid);
    }

    @Override
    public Response registerWebhook(UriInfo uriInfo, String orcid, String webhookUri) {
        return t2OrcidApiServiceDelegator.registerWebhook(uriInfo, orcid, webhookUri);
    }

    @Override
    public Response unregisterWebhook(UriInfo uriInfo, String orcid, String webhookUri) {
        return t2OrcidApiServiceDelegator.unregisterWebhook(uriInfo, orcid, webhookUri);
    }

    private void validateIncomingMessage(OrcidMessage orcidMessage) {
        try {
            incomingValidationManager.validateMessage(orcidMessage);
        } catch (OrcidValidationException e) {
            Throwable cause = e.getCause();
            if (cause == null) {
                cause = e;
            } else {
                Throwable underlyingCause = cause.getCause();
                if (underlyingCause != null) {
                    cause = underlyingCause;
                }
            }
            throw new OrcidBadRequestException("Invalid incoming message: " + cause.toString());
        }
    }

    private void validateOutgoingResponse(Response response) {
        OrcidMessage orcidMessage = (OrcidMessage) response.getEntity();
        outgoingValidationManager.validateMessage(orcidMessage);
    }

    private OrcidMessage upgradeMessage(OrcidMessage orcidMessage) {
        return orcidMessageVersionConverterChain.upgradeMessage(orcidMessage, OrcidMessage.DEFAULT_VERSION);
    }

    private Response downgradeResponse(Response response) {
        OrcidMessage orcidMessage = (OrcidMessage) response.getEntity();
        if (orcidMessage != null) {
            orcidMessageVersionConverterChain.downgradeMessage(orcidMessage, externalVersion);
        }
        return Response.fromResponse(response).entity(orcidMessage).build();
    }

    private Response downgradeAndValidateResponse(Response response) {
        Response downgradedResponse = downgradeResponse(response);
        validateOutgoingResponse(downgradedResponse);
        return downgradedResponse;
    }

    @Override
    public Response addAffiliations(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        validateIncomingMessage(orcidMessage);
        OrcidMessage upgradedMessage = upgradeMessage(orcidMessage);
        return t2OrcidApiServiceDelegator.addAffiliations(uriInfo, orcid, upgradedMessage);
    }

    @Override
    public Response updateAffiliations(UriInfo uriInfo, String orcid, OrcidMessage orcidMessage) {
        validateIncomingMessage(orcidMessage);
        OrcidMessage upgradedMessage = upgradeMessage(orcidMessage);
        return t2OrcidApiServiceDelegator.updateAffiliations(uriInfo, orcid, upgradedMessage);
    }

}
