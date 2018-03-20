package org.orcid.core.exception;

import javax.ws.rs.core.Response;

import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidMessage;

/**
 * @author Declan Newman (declan) Date: 01/03/2012
 */
public abstract class OrcidApiException extends RuntimeException {

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
        super(message, t);
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        ErrorDesc errorDesc = new ErrorDesc();
        errorDesc.setContent(message + (t != null ? " " + t.getMessage() : ""));
        orcidMessage.setErrorDesc(errorDesc);
        response = Response.status(status).entity(orcidMessage).build();
    }

    public Response getResponse() {
        return response;
    }

}
