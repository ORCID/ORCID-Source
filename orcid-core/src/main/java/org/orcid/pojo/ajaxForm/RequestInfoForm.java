/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RequestInfoForm implements ErrorsInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> errors = new ArrayList<String>();
    private Set<ScopeInfoForm> scopes = new HashSet<ScopeInfoForm>();
    private String redirectUrl = null;
    private boolean userPersistentTokens = false;
    
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Set<ScopeInfoForm> getScopes() {
        return scopes;
    }

    public void setScopes(Set<ScopeInfoForm> scopes) {
        this.scopes = scopes;
    }

    public boolean getUserPersistentTokens() {
        return userPersistentTokens;
    }

    public void setUserPersistentTokens(boolean userPersistentTokens) {
        this.userPersistentTokens = userPersistentTokens;
    }
    
    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getScopesAsString() {
        String result = new String();        
        for(ScopeInfoForm form : scopes) {
            result += form.getValue() + " ";
        }                        
        return result.trim();
    }
}
