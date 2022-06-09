package org.orcid.core.salesforce.dao;

import java.io.IOException;

public interface SalesforceMicroserviceClient {
    String retrieveMembers() throws IOException, InterruptedException;

    String retrieveMemberDetails(String memberId) throws IOException, InterruptedException;

    String retrieveConsortiaList() throws IOException, InterruptedException;
}
