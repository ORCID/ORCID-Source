package org.orcid.core.manager.v3.read_only;

import java.util.List;

import org.orcid.pojo.DelegateForm;

public interface GivenPermissionToManagerReadOnly {

    List<DelegateForm> findByGiver(String giverOrcid, long lastModified);

    List<DelegateForm> findByReceiver(String receiverOrcid, long lastModified);

    DelegateForm findByGiverAndReceiverOrcid(String giverOrcid, String receiverOrcid);
}
