package org.orcid.frontend.salesforce.client;

import java.io.IOException;

public interface SalesforceMicroserviceClient {
    String retrieveMembers() throws IOException, InterruptedException;

    String retrieveMemberDetails(String memberId) throws IOException, InterruptedException;

    String retrieveConsortiaList() throws IOException, InterruptedException;
}
