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
package org.orcid.internal.server.delegator.impl;

import static org.orcid.core.api.OrcidApiConstants.STATUS_OK_MESSAGE;

import java.util.Date;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;

import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.internal.server.delegator.InternalApiServiceDelegator;
import org.orcid.jaxb.model.message.ScopePathType;

public class InternalApiServiceDelegatorImpl implements InternalApiServiceDelegator {

    @Resource
    OrcidProfileManager orcidProfileManager;
    
    @Override
    public Response viewStatusText() {
        return Response.ok(STATUS_OK_MESSAGE).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.INTERNAL_PERSON_LAST_MODIFIED, requestComesFromInternalApi = true)
    public Response viewPersonLastModified(String orcid) {
        Date lastModified = orcidProfileManager.retrieveLastModifiedDate(orcid);
        LastModifiedResponse obj = new LastModifiedResponse(orcid, lastModified.toString());        
        Response response = Response.ok(obj).build(); 
        return response;
    }
    
}

class LastModifiedResponse {
    @XmlElement(name = "orcid")
    private String orcid;
    @XmlElement(name = "last-modified")
    private String lastModified;
        
    public LastModifiedResponse(String orcid, String lastModified) {        
        this.orcid = orcid;        
        this.lastModified = lastModified;
    }
    
    public String getOrcid() {
        return orcid;
    }
    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
    public String getLastModified() {
        return lastModified;
    }
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
    
}