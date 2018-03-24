package org.orcid.core.version.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.jaxb.model.common_v2.LastModifiedDate;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecords;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record_v2.ActivitiesContainer;
import org.orcid.jaxb.model.record_v2.Activity;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.Group;
import org.orcid.jaxb.model.record_v2.GroupableActivity;
import org.orcid.jaxb.model.record_v2.GroupsContainer;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.OtherNames;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.PersonalDetails;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;

public class Api2_0_LastModifiedDatesHelper {

	public static void calculateLastModified(ActivitiesSummary activitiesSummary) {
		if(activitiesSummary != null) {
			calculateLastModified(activitiesSummary.getEducations());
			calculateLastModified(activitiesSummary.getEmployments());
			calculateLastModified(activitiesSummary.getFundings());
			calculateLastModified(activitiesSummary.getPeerReviews());
			calculateLastModified(activitiesSummary.getWorks());
			
			LastModifiedDate l1 = activitiesSummary.getEducations() == null ? null : activitiesSummary.getEducations().getLastModifiedDate();
			LastModifiedDate l2= activitiesSummary.getEmployments() == null ? null : activitiesSummary.getEmployments().getLastModifiedDate();
			LastModifiedDate l3 = activitiesSummary.getFundings() == null ? null : activitiesSummary.getFundings().getLastModifiedDate();
			LastModifiedDate l4 = activitiesSummary.getPeerReviews() == null ? null : activitiesSummary.getPeerReviews().getLastModifiedDate();
			LastModifiedDate l5 = activitiesSummary.getWorks() == null ? null : activitiesSummary.getWorks().getLastModifiedDate();
			
			LastModifiedDate globalLatest = calculateLatest(l1, l2, l3, l4, l5);
			activitiesSummary.setLastModifiedDate(globalLatest);
		}		
	}
	
	public static void calculateLastModified(ActivitiesContainer actContainerV2) {
		if(actContainerV2 != null) {
	        Collection<? extends Activity> activities = actContainerV2.retrieveActivities();
	        if (activities != null && !activities.isEmpty()) {
	            Iterator<? extends Activity> activitiesIterator = activities.iterator();
	            XMLGregorianCalendar latest = activitiesIterator.next().getLastModifiedDate().getValue();
	            while (activitiesIterator.hasNext()) {
	                Activity activity = activitiesIterator.next();
	                if (latest.compare(activity.getLastModifiedDate().getValue()) == -1) {
	                    latest = activity.getLastModifiedDate().getValue();
	                }
	            }
	            actContainerV2.setLastModifiedDate(new LastModifiedDate(latest));         
	        }        
		}
    }        

    public static void calculateLastModified(GroupsContainer groupsContainerV2) {
        if (groupsContainerV2.retrieveGroups() != null && !groupsContainerV2.retrieveGroups().isEmpty()) {
            List<? extends Group> groupsRc1 = new ArrayList<>(groupsContainerV2.retrieveGroups());
            List<org.orcid.jaxb.model.record_v2.Group> groupsV2 = new ArrayList<>(groupsContainerV2.retrieveGroups());
            if (groupsRc1.get(0).getActivities() != null && !groupsRc1.get(0).getActivities().isEmpty()) {
                LastModifiedDate latest = null;
            	for (Group group : groupsV2) {
            		calculateLastModified(group);
                    if(group.getLastModifiedDate() != null && group.getLastModifiedDate().after(latest)) {
                    	latest = group.getLastModifiedDate();
                    }
                }
                groupsContainerV2.setLastModifiedDate(latest);
            }
        }
    }

    public static void calculateLastModified(Group group) {
        Collection<? extends GroupableActivity> activities = group.getActivities();
        if (activities != null && !activities.isEmpty()) {
            Iterator<? extends GroupableActivity> activitiesIterator = activities.iterator();
            LastModifiedDate latest = null;
            while (activitiesIterator.hasNext()) {
                GroupableActivity activity = activitiesIterator.next();
                if (activity.getLastModifiedDate() != null && activity.getLastModifiedDate().after(latest)) {
                    latest = activity.getLastModifiedDate();
                }
            }
            group.setLastModifiedDate(latest);
        }
    }

    public static void calculateLastModified(GroupIdRecords groupIdRecords) {
        if (groupIdRecords != null && groupIdRecords.getGroupIdRecord() != null && !groupIdRecords.getGroupIdRecord().isEmpty()) {
            LastModifiedDate latest = null;
            for (GroupIdRecord groupid : groupIdRecords.getGroupIdRecord()) {
                if (groupid.getLastModifiedDate() != null && groupid.getLastModifiedDate().after(latest)) {
                    latest = groupid.getLastModifiedDate();
                }
            }            
            groupIdRecords.setLastModifiedDate(latest);
        }        
    }

    public static void calculateLastModified(ResearcherUrls researcherUrls) {
        if (researcherUrls != null && researcherUrls.getResearcherUrls() != null && !researcherUrls.getResearcherUrls().isEmpty()) {
        	LastModifiedDate latest = null;
            for (ResearcherUrl researcherUrl : researcherUrls.getResearcherUrls()) {
                if (researcherUrl.getLastModifiedDate() != null && researcherUrl.getLastModifiedDate().after(latest)) {
                    latest = researcherUrl.getLastModifiedDate();
                }
            }
            researcherUrls.setLastModifiedDate(latest);
        }        
    }

    public static void calculateLastModified(Emails emails) {
        if (emails != null && emails.getEmails() != null && !emails.getEmails().isEmpty()) {
        	LastModifiedDate latest = null;
            for (Email email : emails.getEmails()) {
                if (email.getLastModifiedDate() != null && email.getLastModifiedDate().after(latest)) {
                    latest = email.getLastModifiedDate();
                }
            }
            emails.setLastModifiedDate(latest);
        }        
    }

    public static void calculateLastModified(OtherNames otherNames) {
        if (otherNames != null && otherNames.getOtherNames() != null && !otherNames.getOtherNames().isEmpty()) {
        	LastModifiedDate latest = null;
            for (OtherName otherName : otherNames.getOtherNames()) {
            	if (otherName.getLastModifiedDate() != null && otherName.getLastModifiedDate().after(latest)) {
                    latest = otherName.getLastModifiedDate();
                }
            }
            otherNames.setLastModifiedDate(latest);
        }        
    }

    public static void calculateLastModified(PersonExternalIdentifiers extIds) {
        if (extIds != null && extIds.getExternalIdentifiers() != null && !extIds.getExternalIdentifiers().isEmpty()) {
        	LastModifiedDate latest = null;
            for (PersonExternalIdentifier extId : extIds.getExternalIdentifiers()) {
            	if (extId.getLastModifiedDate() != null && extId.getLastModifiedDate().after(latest)) {
                    latest = extId.getLastModifiedDate();
                }
            }
            extIds.setLastModifiedDate(latest);
        }        
    }

    public static void calculateLastModified(Keywords keywords) {
        if (keywords != null && keywords.getKeywords() != null && !keywords.getKeywords().isEmpty()) {
        	LastModifiedDate latest = null;
            for (Keyword keyword : keywords.getKeywords()) {
            	if (keyword.getLastModifiedDate() != null && keyword.getLastModifiedDate().after(latest)) {
                    latest = keyword.getLastModifiedDate();
                }
            }
            keywords.setLastModifiedDate(latest);
        }        
    }

    public static void calculateLastModified(Addresses addresses) {
        if (addresses != null && addresses.getAddress() != null && !addresses.getAddress().isEmpty()) {
        	LastModifiedDate latest = null;
            for (Address address : addresses.getAddress()) {
            	if (address.getLastModifiedDate() != null && address.getLastModifiedDate().after(latest)) {
                    latest = address.getLastModifiedDate();
                }
            }
            addresses.setLastModifiedDate(latest);
        }        
    }
    
	public static void calculateLastModified(PersonalDetails personalDetails) {
		if (personalDetails != null) {
			calculateLastModified(personalDetails.getOtherNames());
			LastModifiedDate l1 = personalDetails.getBiography() == null ? null
					: personalDetails.getBiography().getLastModifiedDate();
			LastModifiedDate l2 = personalDetails.getName() == null ? null
					: personalDetails.getName().getLastModifiedDate();
			LastModifiedDate l3 = personalDetails.getOtherNames() == null ? null
					: personalDetails.getOtherNames().getLastModifiedDate();
			LastModifiedDate globalLatest = calculateLatest(l1, l2, l3);
			personalDetails.setLastModifiedDate(globalLatest);
		}
	}

	public static void calculateLastModified(Person person) {
		if (person != null) {
			calculateLastModified(person.getAddresses());
			calculateLastModified(person.getEmails());
			calculateLastModified(person.getExternalIdentifiers());
			calculateLastModified(person.getKeywords());
			calculateLastModified(person.getOtherNames());
			calculateLastModified(person.getResearcherUrls());

			LastModifiedDate l1 = person.getAddresses() == null ? null : person.getAddresses().getLastModifiedDate();
			LastModifiedDate l2 = person.getEmails() == null ? null : person.getEmails().getLastModifiedDate();
			LastModifiedDate l3 = person.getExternalIdentifiers() == null ? null
					: person.getExternalIdentifiers().getLastModifiedDate();
			LastModifiedDate l4 = person.getKeywords() == null ? null : person.getKeywords().getLastModifiedDate();
			LastModifiedDate l5 = person.getOtherNames() == null ? null : person.getOtherNames().getLastModifiedDate();
			LastModifiedDate l6 = person.getResearcherUrls() == null ? null
					: person.getResearcherUrls().getLastModifiedDate();

			LastModifiedDate globalLatest = calculateLatest(l1, l2, l3, l4, l5, l6);
			person.setLastModifiedDate(globalLatest);
		}
	}
	
	public static void calculateLastModified(Record record) {
		if (record != null) {
			calculateLastModified(record.getPerson());
			calculateLastModified(record.getActivitiesSummary());			
		}
	}
    
    public static LastModifiedDate calculateLatest(LastModifiedDate ... dates) {
        LastModifiedDate latest = null;
        for(LastModifiedDate obj : dates) {
            if(obj != null) {
                if(obj.after(latest)) {
                	latest = obj;
                }
            }            
        }
        return latest;
    }
}
