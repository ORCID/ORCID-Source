package org.orcid.core.utils.v3.activities;

import java.util.Comparator;

import org.orcid.pojo.grouping.PeerReviewGroup;

public class PeerReviewGroupComparator implements Comparator<PeerReviewGroup> {

    private boolean reverse;

    public PeerReviewGroupComparator(boolean reverse) {
        this.reverse = reverse;
    }
    
    @Override
    public int compare(PeerReviewGroup x, PeerReviewGroup y) {
        if (reverse) {
            return y.getName().toLowerCase().compareTo(x.getName().toLowerCase());
        }
        return x.getName().toLowerCase().compareTo(y.getName().toLowerCase());
    }
    
}
