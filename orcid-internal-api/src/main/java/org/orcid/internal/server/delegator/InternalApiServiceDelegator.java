package org.orcid.internal.server.delegator;

import javax.ws.rs.core.Response;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface InternalApiServiceDelegator {
    Response viewStatusText();
    Response viewPersonLastModified(String orcid);
    Response viewMemberInfo(String memberIdOrName);
    Response viewTogglz();
}
