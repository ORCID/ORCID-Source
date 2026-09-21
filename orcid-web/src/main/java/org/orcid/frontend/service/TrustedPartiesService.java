package org.orcid.frontend.service;

public interface TrustedPartiesService {

    public void disableClientAccess(String clientDetailsId, String userOrcid);
}
