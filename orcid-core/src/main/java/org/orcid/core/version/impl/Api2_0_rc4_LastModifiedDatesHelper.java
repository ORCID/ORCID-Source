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

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.jaxb.model.record_rc4.Address;
import org.orcid.jaxb.model.common_rc4.LastModifiedDate;
import org.orcid.jaxb.model.groupid_rc4.GroupIdRecord;
import org.orcid.jaxb.model.groupid_rc4.GroupIdRecords;
import org.orcid.jaxb.model.record_rc4.ActivitiesContainer;
import org.orcid.jaxb.model.record_rc4.Activity;
import org.orcid.jaxb.model.record_rc4.Addresses;
import org.orcid.jaxb.model.record_rc4.Email;
import org.orcid.jaxb.model.record_rc4.Emails;
import org.orcid.jaxb.model.record_rc4.Group;
import org.orcid.jaxb.model.record_rc4.GroupableActivity;
import org.orcid.jaxb.model.record_rc4.GroupsContainer;
import org.orcid.jaxb.model.record_rc4.Keyword;
import org.orcid.jaxb.model.record_rc4.Keywords;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.jaxb.model.record_rc4.OtherNames;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc4.ResearcherUrl;
import org.orcid.jaxb.model.record_rc4.ResearcherUrls;
import org.orcid.utils.DateUtils;

public class Api2_0_rc4_LastModifiedDatesHelper {

    public static Date calculateLatest(ActivitiesContainer actContainerRc4) {
        Date latestAct = null;
        Collection<? extends Activity> activities = actContainerRc4.retrieveActivities();
        if (activities != null && !activities.isEmpty()) {
            Iterator<? extends Activity> activitiesIterator = activities.iterator();
            XMLGregorianCalendar latest = activitiesIterator.next().getLastModifiedDate().getValue();
            while (activitiesIterator.hasNext()) {
                Activity activity = activitiesIterator.next();
                if (latest.compare(activity.getLastModifiedDate().getValue()) == -1) {
                    latest = activity.getLastModifiedDate().getValue();
                }
            }
            actContainerRc4.setLastModifiedDate(new LastModifiedDate(latest));
            latestAct = latest.toGregorianCalendar().getTime();
        }
        return latestAct;
    }        

    public static Date calculateLatest(GroupsContainer groupsContainerRc4) {
        Date latestGrp = null;
        if (groupsContainerRc4.retrieveGroups() != null && !groupsContainerRc4.retrieveGroups().isEmpty()) {
            List<? extends Group> groupsRc1 = new ArrayList<>(groupsContainerRc4.retrieveGroups());
            List<org.orcid.jaxb.model.record_rc4.Group> groupsRc4 = new ArrayList<>(groupsContainerRc4.retrieveGroups());
            if (groupsRc1.get(0).getActivities() != null && !groupsRc1.get(0).getActivities().isEmpty()) {
                for (int index = 0; index < groupsRc4.size(); index++) {
                    latestGrp = calculateLatest(groupsRc4.get(index));
                }
                groupsContainerRc4.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(latestGrp)));
            }
        }
        return latestGrp;
    }

    public static Date calculateLatest(Group group) {
        Date latestAct = null;
        Collection<? extends GroupableActivity> activities = group.getActivities();
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
            group.setLastModifiedDate(new LastModifiedDate(latest));
        }
        return latestAct;
    }

    public static Date calculateLatest(GroupIdRecords groupIdRecords) {
        Date latestAct = null;
        if (groupIdRecords != null && groupIdRecords.getGroupIdRecord() != null && !groupIdRecords.getGroupIdRecord().isEmpty()) {
            XMLGregorianCalendar latest = groupIdRecords.getGroupIdRecord().get(0).getLastModifiedDate().getValue();
            for (GroupIdRecord groupid : groupIdRecords.getGroupIdRecord()) {
                if (latest.compare(groupid.getLastModifiedDate().getValue()) == -1) {
                    latest = groupid.getLastModifiedDate().getValue();
                }
            }
            latestAct = latest.toGregorianCalendar().getTime();
            groupIdRecords.setLastModifiedDate(new LastModifiedDate(latest));
        }
        return latestAct;
    }

    public static Date calculateLatest(ResearcherUrls researcherUrls) {
        Date latestAct = null;
        if (researcherUrls != null && researcherUrls.getResearcherUrls() != null && !researcherUrls.getResearcherUrls().isEmpty()) {
            XMLGregorianCalendar latest = researcherUrls.getResearcherUrls().get(0).getLastModifiedDate().getValue();
            for (ResearcherUrl researcherUrl : researcherUrls.getResearcherUrls()) {
                if (latest.compare(researcherUrl.getLastModifiedDate().getValue()) == -1) {
                    latest = researcherUrl.getLastModifiedDate().getValue();
                }
            }
            latestAct = latest.toGregorianCalendar().getTime();
            researcherUrls.setLastModifiedDate(new LastModifiedDate(latest));
        }
        return latestAct;
    }

    public static Date calculateLatest(Emails emails) {
        Date latestAct = null;
        if (emails != null && emails.getEmails() != null && !emails.getEmails().isEmpty()) {
            XMLGregorianCalendar latest = emails.getEmails().get(0).getLastModifiedDate().getValue();
            for (Email email : emails.getEmails()) {
                if (latest.compare(email.getLastModifiedDate().getValue()) == -1) {
                    latest = email.getLastModifiedDate().getValue();
                }
            }
            latestAct = latest.toGregorianCalendar().getTime();
            emails.setLastModifiedDate(new LastModifiedDate(latest));
        }
        return latestAct;
    }

    public static XMLGregorianCalendar calculateLatest(OtherNames otherNames) {
        XMLGregorianCalendar latest = null;
        if (otherNames != null && otherNames.getOtherNames() != null && !otherNames.getOtherNames().isEmpty()) {
            latest = otherNames.getOtherNames().get(0).getLastModifiedDate().getValue();
            for (OtherName otherName : otherNames.getOtherNames()) {
                if (latest.compare(otherName.getLastModifiedDate().getValue()) == -1) {
                    latest = otherName.getLastModifiedDate().getValue();
                }
            }
            otherNames.setLastModifiedDate(new LastModifiedDate(latest));
        }
        return latest;
    }

    public static Date calculateLatest(PersonExternalIdentifiers extIds) {
        Date latestAct = null;
        if (extIds != null && extIds.getExternalIdentifiers() != null && !extIds.getExternalIdentifiers().isEmpty()) {
            XMLGregorianCalendar latest = extIds.getExternalIdentifiers().get(0).getLastModifiedDate().getValue();
            for (PersonExternalIdentifier extId : extIds.getExternalIdentifiers()) {
                if (latest.compare(extId.getLastModifiedDate().getValue()) == -1) {
                    latest = extId.getLastModifiedDate().getValue();
                }
            }
            latestAct = latest.toGregorianCalendar().getTime();
            extIds.setLastModifiedDate(new LastModifiedDate(latest));
        }
        return latestAct;
    }

    public static Date calculateLatest(Keywords keywords) {
        Date latestAct = null;
        if (keywords != null && keywords.getKeywords() != null && !keywords.getKeywords().isEmpty()) {
            XMLGregorianCalendar latest = keywords.getKeywords().get(0).getLastModifiedDate().getValue();
            for (Keyword keyword : keywords.getKeywords()) {
                if (latest.compare(keyword.getLastModifiedDate().getValue()) == -1) {
                    latest = keyword.getLastModifiedDate().getValue();
                }
            }
            latestAct = latest.toGregorianCalendar().getTime();
            keywords.setLastModifiedDate(new LastModifiedDate(latest));
        }
        return latestAct;
    }

    public static Date calculateLatest(Addresses addresses) {
        Date latestAct = null;
        if (addresses != null && addresses.getAddress() != null && !addresses.getAddress().isEmpty()) {
            XMLGregorianCalendar latest = addresses.getAddress().get(0).getLastModifiedDate().getValue();
            for (Address address : addresses.getAddress()) {
                if (latest.compare(address.getLastModifiedDate().getValue()) == -1) {
                    latest = address.getLastModifiedDate().getValue();
                }
            }
            latestAct = latest.toGregorianCalendar().getTime();
            addresses.setLastModifiedDate(new LastModifiedDate(latest));
        }
        return latestAct;
    }

    public static LastModifiedDate returnLatestLastModifiedDate(LastModifiedDate latest, LastModifiedDate temp) {
        if (temp == null) {
            return latest;
        }
        if (latest == null) {
            return temp;
        }
        if (latest.getValue().compare(temp.getValue()) == -1) {
            return temp;
        }
        return latest;
    }
    
    public static XMLGregorianCalendar calculateLatest(XMLGregorianCalendar ... dates) {
        XMLGregorianCalendar latest = null;
        for(XMLGregorianCalendar obj : dates) {
            if(obj != null) {
                if(latest == null) {
                    latest = obj;
                } else {
                    if(latest.compare(obj) == DatatypeConstants.LESSER) {
                        latest = obj;
                    }
                }
            }            
        }
        return latest;
    }
}
