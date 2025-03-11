package org.orcid.core.exception;

/**
 * This exception will be thrown when the scope provided by the user doesn't
 * match any of the scopes available in
 * org.orcid.jaxb.model.message.ScopePathType enum
 * 
 * @author Angel Montenegro(amontenegro)
 */
public class OrcidInvalidScopeException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    private String clientId;
    private String scope;

    public OrcidInvalidScopeException() {
        super();
    }

    public OrcidInvalidScopeException(String message) {
        super(message);
    }

    public OrcidInvalidScopeException(String message, String clientId, String scope) {
        super(message);
        this.clientId = clientId;
        this.scope = scope;
    }

    public String getClientId() {
        return clientId;
    }

    public String getScope() {
        return scope;
    }
}
