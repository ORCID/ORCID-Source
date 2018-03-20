package org.orcid.core.utils.activities;

import java.util.Comparator;

import org.orcid.jaxb.model.record_v2.GroupableActivity;

public class GroupableActivityComparator implements Comparator<GroupableActivity> {

    @Override
    public int compare(GroupableActivity o1, GroupableActivity o2) {
        return o1.compareTo(o2);
    }
}
