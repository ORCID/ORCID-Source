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
package org.orcid.integration.api.helper;


/**
 * @author Angel Montenegro
 * */
public class TokenStore {
    private String clientId;
    private String user;
    private String scope;
    private String token;
    
    public TokenStore(String clientId, String user, String scope, String token) {
        super();
        this.clientId = clientId;
        this.user = user;
        this.scope = scope;
        this.token = token;
    }
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getScope() {
        return scope;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }        
}
