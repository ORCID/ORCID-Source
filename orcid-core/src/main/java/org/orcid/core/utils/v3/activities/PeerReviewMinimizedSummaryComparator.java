package org.orcid.core.utils.v3.activities;

import org.orcid.pojo.PeerReviewMinimizedSummary;

import java.util.Comparator;

public class PeerReviewMinimizedSummaryComparator implements Comparator<PeerReviewMinimizedSummary> {

    private boolean reverse;

    public PeerReviewMinimizedSummaryComparator(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public int compare(PeerReviewMinimizedSummary x, PeerReviewMinimizedSummary y) {
        if (reverse) {
            return y.getName().toLowerCase().compareTo(x.getName().toLowerCase());
        }
        return x.getName().toLowerCase().compareTo(y.getName().toLowerCase());
    }

}
