package org.orcid.core.utils.v3.activities;

import java.util.Comparator;

import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;

public class PeerReviewDuplicateGroupComparator implements Comparator<PeerReviewDuplicateGroup> {

    @Override
    public int compare(PeerReviewDuplicateGroup x, PeerReviewDuplicateGroup y) {
        PeerReviewSummary summaryX = (PeerReviewSummary) x.getActivities().iterator().next();
        PeerReviewSummary summaryY = (PeerReviewSummary) y.getActivities().iterator().next();
        if (summaryX.getCompletionDate() == null && summaryY.getCompletionDate() == null) {
            return 0;
        }
        
        if (summaryY.getCompletionDate() == null) {
            return -1;
        }
        
        if (summaryX.getCompletionDate() == null) {
            return 1;
        }
        return summaryY.getCompletionDate().compareTo(summaryX.getCompletionDate());
    }
    
}
