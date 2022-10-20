package org.orcid.core.exception;

import javax.servlet.http.HttpServletResponse;

import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidMessage;

/**
 * @author Declan Newman (declan) Date: 01/03/2012
 */
public abstract class OrcidApiException extends RuntimeException {

    private static final long serialVersionUID = 498312805134452270L;

    private int httpStatus;
    
    public OrcidApiException(String message, int status) {
        this(message, status, null);
    }

    public OrcidApiException(String message, Throwable t) {
        this(message, t != null ? HttpServletResponse.SC_INTERNAL_SERVER_ERROR : HttpServletResponse.SC_BAD_REQUEST, t);
    }

    public OrcidApiException(String message, int status, Throwable t) {
        super(message, t);
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        ErrorDesc errorDesc = new ErrorDesc();
        errorDesc.setContent(message + (t != null ? " " + t.getMessage() : ""));
        orcidMessage.setErrorDesc(errorDesc);
        this.httpStatus = status;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

}
