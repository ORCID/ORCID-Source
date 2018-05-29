package org.orcid.core.manager.v3;

public interface GivenPermissionToManager {

    void remove(String giverOrcid, String receiverOrcid);

    void create(String userOrcid, String delegateOrcid);

}
