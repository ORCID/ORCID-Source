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
package org.orcid.core.manager.read_only;

import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;

public interface ActivitiesSummaryManagerReadOnly extends ManagerReadOnlyBase {
    ActivitiesSummary getActivitiesSummary(String orcid);

    ActivitiesSummary getPublicActivitiesSummary(String orcid);
}
