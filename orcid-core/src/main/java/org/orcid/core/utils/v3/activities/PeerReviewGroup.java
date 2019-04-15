package org.orcid.core.utils.v3.activities;

import java.util.HashSet;
import java.util.Set;

import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;

public class PeerReviewGroup {
    private Set<PeerReviewGroupKey> groupKeys;
    private Set<PeerReviewSummary> activities;

    public PeerReviewGroup(PeerReviewSummary summary) {
        groupKeys = new HashSet<PeerReviewGroupKey>();
        activities = new HashSet<PeerReviewSummary>();

        PeerReviewSummary peerReviewSummary = (PeerReviewSummary) summary;
        PeerReviewGroupKey prgk = new PeerReviewGroupKey();
        prgk.setGroupId(peerReviewSummary.getGroupId());
        groupKeys.add(prgk);

        activities.add(summary);
    }

    public Set<PeerReviewGroupKey> getGroupKeys() {
        if (groupKeys == null)
            groupKeys = new HashSet<PeerReviewGroupKey>();
        return groupKeys;
    }

    public Set<PeerReviewSummary> getActivities() {
        if (activities == null)
            activities = new HashSet<PeerReviewSummary>();
        return activities;
    }

    public void add(PeerReviewSummary activity) {
        activities.add(activity);
    }

    public void merge(PeerReviewGroup group) {
        Set<PeerReviewSummary> otherActivities = group.getActivities();
        Set<PeerReviewGroupKey> otherKeys = group.getGroupKeys();

        // The incoming groups should always contain at least one key, we should
        // not merge activities without keys
        if (otherKeys.isEmpty())
            throw new IllegalArgumentException("Unable to merge a group without external identifiers");

        // The incoming group should always contains at least one activity, we
        // should not merge empty activities
        // Merge group keys
        for (PeerReviewGroupKey otherKey : otherKeys) {
            if (!groupKeys.contains(otherKey))
                groupKeys.add(otherKey);
        }

        // Merge activities
        for (PeerReviewSummary activity : otherActivities) {
            // We assume the activity is not already there, anyway it is a set
            activities.add(activity);
        }
    }

}
