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
package org.orcid.core.manager.v3.read_only;

import java.util.List;

import org.orcid.pojo.DelegateForm;

public interface GivenPermissionToManagerReadOnly {

    List<DelegateForm> findByGiver(String giverOrcid);

    List<DelegateForm> findByReceiver(String receiverOrcid);

    DelegateForm findByGiverAndReceiverOrcid(String giverOrcid, String receiverOrcid);
}
