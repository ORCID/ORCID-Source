/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.version.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.jaxb.model.common_rc2.LastModifiedDate;
import org.orcid.jaxb.model.groupid_rc2.GroupIdRecord;
import org.orcid.jaxb.model.groupid_rc2.GroupIdRecords;
import org.orcid.jaxb.model.record_rc2.ActivitiesContainer;
import org.orcid.jaxb.model.record_rc2.Activity;
import org.orcid.jaxb.model.record_rc2.Group;
import org.orcid.jaxb.model.record_rc2.GroupableActivity;
import org.orcid.jaxb.model.record_rc2.GroupsContainer;
import org.orcid.utils.DateUtils;

public class LastModifiedDatesHelper {

    public static Date calculateLatest(ActivitiesContainer actContainerRc2) {
        Date latestAct = null;
        Collection<? extends Activity> activities = actContainerRc2.retrieveActivities();
        if (activities != null && !activities.isEmpty()) {
            Iterator<? extends Activity> activitiesIterator = activities.iterator();
            XMLGregorianCalendar latest = activitiesIterator.next().getLastModifiedDate().getValue();
            while (activitiesIterator.hasNext()) {
                Activity activity = activitiesIterator.next();
                if (latest.compare(activity.getLastModifiedDate().getValue()) == -1) {
                    latest = activity.getLastModifiedDate().getValue();
                }
            }
            latestAct = latest.toGregorianCalendar().getTime();
        }
        return latestAct;
    }

    public static Date calculateLatest(GroupsContainer groupsContainerRc2) {
        Date latestGrp = null;
        if (groupsContainerRc2.retrieveGroups() != null && !groupsContainerRc2.retrieveGroups().isEmpty()) {
            List<? extends Group> groupsRc1 = new ArrayList<>(groupsContainerRc2.retrieveGroups());
            List<org.orcid.jaxb.model.record_rc2.Group> groupsRc2 = new ArrayList<>(groupsContainerRc2.retrieveGroups());
            if (groupsRc1.get(0).getActivities() != null && !groupsRc1.get(0).getActivities().isEmpty()) {
                for (int index = 0; index < groupsRc2.size(); index++) {
                    latestGrp = calculateLatest(groupsRc2.get(index));
                }
                groupsContainerRc2.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(latestGrp)));
            }
        }
        return latestGrp;
    }

    public static Date calculateLatest(Group groupRc2) {
    	Date latestAct = null;
        Collection<? extends GroupableActivity> activities = groupRc2.getActivities();
        if (activities != null && !activities.isEmpty()) {
            Iterator<? extends GroupableActivity> activitiesIterator = activities.iterator();
            XMLGregorianCalendar latest = activitiesIterator.next().getLastModifiedDate().getValue();
            while (activitiesIterator.hasNext()) {
                GroupableActivity activity = activitiesIterator.next();
                if (latest.compare(activity.getLastModifiedDate().getValue()) == -1) {
                    latest = activity.getLastModifiedDate().getValue();
                }
            }
            latestAct = latest.toGregorianCalendar().getTime();
            groupRc2.setLastModifiedDate(new LastModifiedDate(latest));
        }
        return latestAct;
    }
    
    public static Date calculateLatest(GroupIdRecords groupIdRecords) {
    	Date latestAct = null;
        if (groupIdRecords != null && groupIdRecords.getGroupIdRecord() != null && !groupIdRecords.getGroupIdRecord().isEmpty()) {
            XMLGregorianCalendar latest = groupIdRecords.getGroupIdRecord().get(0).getLastModifiedDate().getValue();
            for(GroupIdRecord groupid : groupIdRecords.getGroupIdRecord()) {
                if (latest.compare(groupid.getLastModifiedDate().getValue()) == -1) {
                    latest = groupid.getLastModifiedDate().getValue();
                }
            }
            latestAct = latest.toGregorianCalendar().getTime();
        }
        return latestAct;
    }
}
