package org.orcid.core.contributors.roles;

import java.util.Map;

import org.orcid.core.exception.ApplicationException;

public class InvalidContributorRoleException extends ApplicationException {

    private static final long serialVersionUID = 1L;
    
    public InvalidContributorRoleException(Map<String, String> params) {
        super(params);
    }
}
