package org.orcid.core.manager.v3.read_only;

import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;

public interface ActivitiesSummaryManagerReadOnly extends ManagerReadOnlyBase {
    ActivitiesSummary getActivitiesSummary(String orcid, boolean filterVersionOfIds);

    ActivitiesSummary getPublicActivitiesSummary(String orcid, boolean filterVersionOfIdentifiers);
}
