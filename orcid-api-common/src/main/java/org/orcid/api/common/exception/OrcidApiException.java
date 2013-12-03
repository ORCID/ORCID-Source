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
package org.orcid.api.common.exception;

import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidMessage;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Copyright 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 01/03/2012
 */
public abstract class OrcidApiException extends WebApplicationException {

    private static final long serialVersionUID = 498312805134452270L;

    private Response response;

    public OrcidApiException(String message, int status) {
        this(message, status, null);
    }

    public OrcidApiException(String message, int status, Throwable t) {
        this(message, Response.Status.fromStatusCode(status), t);
    }

    public OrcidApiException(String message, Throwable t) {
        this(message, t != null ? Response.Status.INTERNAL_SERVER_ERROR : Response.Status.BAD_REQUEST, t);
    }

    public OrcidApiException(String message, Response.Status status) {
        this(message, status, null);
    }

    public OrcidApiException(String message, Response.Status status, Throwable t) {
        super(t);
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        ErrorDesc errorDesc = new ErrorDesc();
        errorDesc.setContent(message + (t != null ? " " + t.getMessage() : ""));
        orcidMessage.setErrorDesc(errorDesc);
        response = Response.status(status).entity(orcidMessage).build();
    }

    @Override
    public Response getResponse() {
        return response;
    }

}
