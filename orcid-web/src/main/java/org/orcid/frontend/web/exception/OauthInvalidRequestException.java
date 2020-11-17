package org.orcid.frontend.web.exception;

import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;

@SuppressWarnings("serial")
public class OauthInvalidRequestException extends InvalidRequestException {

    RequestInfoForm requestInfoForm;

    public OauthInvalidRequestException(final String msg, final RequestInfoForm requestInfoForm) {
        super(msg);
        this.requestInfoForm = requestInfoForm;
    }

    public OauthInvalidRequestException(final String msg) {
        super(msg);
    }

    public OauthInvalidRequestException(final String msg, final Throwable t) {
        super(msg, t);
    }

    public RequestInfoForm getRequestInfoForm() {
        return this.requestInfoForm;
    }

}
