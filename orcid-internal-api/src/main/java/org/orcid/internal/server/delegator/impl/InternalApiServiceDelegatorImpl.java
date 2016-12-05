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

import org.orcid.core.manager.MembersManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.internal.server.delegator.InternalApiServiceDelegator;
import org.orcid.internal.util.LastModifiedResponse;
import org.orcid.internal.util.MemberInfo;
import org.orcid.jaxb.model.error_rc4.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.ajaxForm.Member;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class InternalApiServiceDelegatorImpl implements InternalApiServiceDelegator {

    @Resource
    private OrcidProfileManager orcidProfileManager;
    
    @Resource
    private MembersManager memberManager;
    
    
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
    
    @Override
    public Response viewMemberInfo(String memberIdOrName){
        Member member = memberManager.getMember(memberIdOrName); 
        if(member == null || (member.getErrors() != null && !member.getErrors().isEmpty())) {
            OrcidError orcidError = new OrcidError();
            orcidError.setResponseCode(404);
            orcidError.setErrorCode(0);
            orcidError.setMoreInfo("Unable to find member info for: " + memberIdOrName);
            orcidError.setDeveloperMessage("Member id or name not found for: " + memberIdOrName);
            orcidError.setUserMessage("Unable to find member info for: " + memberIdOrName);
            return Response.status(Response.Status.NOT_FOUND).entity(orcidError).build();
        }
        MemberInfo memberInfo = MemberInfo.fromMember(member);
        return Response.ok(memberInfo).build();        
    }        
    
    
}