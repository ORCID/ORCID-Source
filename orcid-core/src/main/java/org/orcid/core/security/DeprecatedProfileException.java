package org.orcid.core.security;

import java.util.Map;

import org.orcid.core.exception.ApplicationException;

/**
 * 
 * @author Angel Montenegro
 *
 */
public class DeprecatedProfileException extends ApplicationException {
    private static final long serialVersionUID = 1L;
    private String primary;
    private String deprecated;

    public DeprecatedProfileException() {
    }
    
    public DeprecatedProfileException(String msg) {
        super(msg);
    }

    public DeprecatedProfileException(String msg, Throwable t) {
        super(msg, t);
    }

    public DeprecatedProfileException(String msg, String primary, String deprecated) {
        super(msg);
        this.primary = primary;
        this.deprecated = deprecated;
    }

    public DeprecatedProfileException(String msg, Throwable t, String primary, String deprecated) {
        super(msg, t);
        this.primary = primary;
        this.deprecated = deprecated;
    }

    public DeprecatedProfileException(Map<String, String> params) {
		super(params);
	}

	public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(String deprecated) {
        this.deprecated = deprecated;
    }
}
