package org.orcid.core.utils.v3.activities;

import java.util.Comparator;

import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewSummary;

public class PeerReviewSummaryComparator implements Comparator<PeerReviewSummary> {

    @Override
    public int compare(PeerReviewSummary x, PeerReviewSummary y) {
        if (x.getCompletionDate() == null && y.getCompletionDate() == null) {
            return 0;
        }
        
        if (y.getCompletionDate() == null) {
            return -1;
        }
        
        if (x.getCompletionDate() == null) {
            return 1;
        }
        return y.getCompletionDate().compareTo(x.getCompletionDate());
    }
    
}
