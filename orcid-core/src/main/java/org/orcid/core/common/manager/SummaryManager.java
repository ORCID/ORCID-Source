package org.orcid.core.common.manager;

import org.orcid.pojo.summary.RecordSummary;

public interface SummaryManager {
    RecordSummary getRecordSummary(String orcid);
}
