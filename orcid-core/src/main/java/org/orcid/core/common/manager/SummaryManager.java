package org.orcid.core.common.manager;

import org.orcid.core.model.RecordSummary;
import org.orcid.pojo.summary.RecordSummaryPojo;

public interface SummaryManager {
    RecordSummary getRecordSummary(String orcid);

    RecordSummaryPojo getRecordSummaryPojo(String orcid);
}
