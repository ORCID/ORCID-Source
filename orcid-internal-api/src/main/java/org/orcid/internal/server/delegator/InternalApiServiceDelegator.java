package org.orcid.internal.server.delegator;

import jakarta.ws.rs.core.Response;

import org.orcid.internal.util.AccountRecoveryMatchRequest;
import org.orcid.internal.util.AccountRecoveryResetLinkRequest;

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
    Response findOrcidByEmail(String email);
    Response accountRecoveryMatch(AccountRecoveryMatchRequest request);
    Response accountRecoveryResetLink(AccountRecoveryResetLinkRequest request);
}
