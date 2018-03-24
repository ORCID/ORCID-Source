package org.orcid.core.manager.read_only;

import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;

public interface ActivitiesSummaryManagerReadOnly extends ManagerReadOnlyBase {
    ActivitiesSummary getActivitiesSummary(String orcid);

    ActivitiesSummary getPublicActivitiesSummary(String orcid);
}
