package org.orcid.core.utils.v3.activities;

import java.util.Comparator;

import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewSummary;

public class PeerReviewSummaryComparator implements Comparator<PeerReviewSummary> {

    @Override
    public int compare(PeerReviewSummary x, PeerReviewSummary y) {
        return x.getCompletionDate().compareTo(y.getCompletionDate());
    }
    
}
