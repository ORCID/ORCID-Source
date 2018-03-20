package org.orcid.core.manager.v3.read_only;

import org.orcid.jaxb.model.v3.dev1.record.summary.ActivitiesSummary;

public interface ActivitiesSummaryManagerReadOnly extends ManagerReadOnlyBase {
    ActivitiesSummary getActivitiesSummary(String orcid);

    ActivitiesSummary getPublicActivitiesSummary(String orcid);
}
