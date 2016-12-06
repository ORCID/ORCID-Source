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
package org.orcid.core.security.visibility.filter.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.security.visibility.filter.VisibilityFilterV2;
import org.orcid.jaxb.model.common_rc3.Filterable;
import org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc3.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc3.Educations;
import org.orcid.jaxb.model.record.summary_rc3.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc3.Employments;
import org.orcid.jaxb.model.record.summary_rc3.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc3.Fundings;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc3.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc3.Works;
import org.orcid.jaxb.model.record_rc3.Group;
import org.orcid.jaxb.model.record_rc3.GroupableActivity;
import org.orcid.jaxb.model.record_rc3.OtherName;
import org.orcid.jaxb.model.record_rc3.Person;
import org.orcid.jaxb.model.record_rc3.PersonalDetails;
import org.orcid.jaxb.model.record_rc3.Record;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Will Simpson
 * 
 */
@Component("visibilityFilterV2")
public class VisibilityFilterV2Impl implements VisibilityFilterV2 {

    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    @Override
    public ActivitiesSummary filter(ActivitiesSummary activitiesSummary, String orcid) {
        if (activitiesSummary == null) {
            return null;
        }
        Educations educations = activitiesSummary.getEducations();
        if (educations != null) {
            List<EducationSummary> summaries = educations.getSummaries();
            filter(summaries, orcid);
            if (summaries.isEmpty()) {
                activitiesSummary.setEducations(null);
            }
        }
        Employments employments = activitiesSummary.getEmployments();
        if (employments != null) {
            List<EmploymentSummary> summaries = employments.getSummaries();
            filter(summaries, orcid);
            if (summaries.isEmpty()) {
                activitiesSummary.setEmployments(null);
            }
        }
        Fundings fundings = activitiesSummary.getFundings();
        if (fundings != null) {
            List<FundingGroup> fundingGroups = fundings.getFundingGroup();
            filterGroups(fundingGroups, orcid);
            if (fundingGroups.isEmpty()) {
                activitiesSummary.setFundings(null);
            }
        }
        
        Works works = activitiesSummary.getWorks();
        if (works != null) {
            List<WorkGroup> workGroups = works.getWorkGroup();
            filterGroups(workGroups, orcid);
            if (workGroups.isEmpty()) {
                activitiesSummary.setWorks(null);
            }
        }
        
        PeerReviews peerReviews = activitiesSummary.getPeerReviews();
        if(peerReviews != null) {
            List<PeerReviewGroup> peerReviewGroups = peerReviews.getPeerReviewGroup();
            filterGroups(peerReviewGroups, orcid);
            if(peerReviewGroups.isEmpty()) {
                activitiesSummary.setPeerReviews(null);
            }
        }
        
        return activitiesSummary;
    }
    
    @Override
    public Works filter(Works works, String orcid) {
        if (works != null) {
            List<WorkGroup> workGroups = works.getWorkGroup();
            filterGroups(workGroups, orcid);            
        }
        
        return works;
    }

    @Override
    public Fundings filter(Fundings fundings, String orcid) {
        if (fundings != null) {
            List<FundingGroup> fundingGroups = fundings.getFundingGroup();
            filterGroups(fundingGroups, orcid);            
        }
        
        return fundings;
    }
    
    @Override
    public PeerReviews filter(PeerReviews peerReviews, String orcid) {
        if(peerReviews != null) {
            List<PeerReviewGroup> peerReviewGroups = peerReviews.getPeerReviewGroup();
            filterGroups(peerReviewGroups, orcid);
        }
        
        return peerReviews;
    }
    
    @Override
    public Collection<? extends Filterable> filter(Collection<? extends Filterable> filterables, String orcid) {
        if (filterables == null) {
            return null;
        }
        for (Iterator<? extends Filterable> iterator = filterables.iterator(); iterator.hasNext();) {
            try {
                orcidSecurityManager.checkVisibility(iterator.next(), orcid);
            } catch (OrcidVisibilityException | OrcidUnauthorizedException e) {
                iterator.remove();
            }
        }
        return filterables;
    }

    @Override
    public Collection<? extends Group> filterGroups(Collection<? extends Group> groups, String orcid) {
        if (groups == null) {
            return null;
        }
        for (Iterator<? extends Group> iterator = groups.iterator(); iterator.hasNext();) {
            Group group = iterator.next();
            Collection<? extends GroupableActivity> activities = group.getActivities();
            filter(activities, orcid);
            if (activities.isEmpty()) {
                iterator.remove();
            }
        }
        return groups;
    }

    @Override
    public PersonalDetails filter(PersonalDetails personalDetails, String orcid) {
        if(personalDetails.getName() != null) {
            try {
                orcidSecurityManager.checkBiographicalVisibility(personalDetails.getName(), orcid);
            } catch(OrcidVisibilityException | OrcidUnauthorizedException e) {
                personalDetails.setName(null);
            }
        }
        
        if(personalDetails.getBiography() != null) {
            try {
                orcidSecurityManager.checkBiographicalVisibility(personalDetails.getBiography(), orcid);
            } catch(OrcidVisibilityException | OrcidUnauthorizedException e) {
                personalDetails.setBiography(null);
            }
        }
        
        if(personalDetails.getOtherNames() != null) {
            if(personalDetails.getOtherNames().getOtherNames() != null) {
                List<OtherName> filteredOtherNames = new ArrayList<OtherName>();
                for(OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
                    try {
                        orcidSecurityManager.checkVisibility(otherName, orcid);
                        filteredOtherNames.add(otherName);
                    } catch(OrcidVisibilityException | OrcidUnauthorizedException e) {
                        // Client dont have permissions to see this other name
                    }
                }
                if(filteredOtherNames.isEmpty()) {
                    personalDetails.setOtherNames(null);
                } else {
                    personalDetails.getOtherNames().setOtherNames(filteredOtherNames);
                }                
            }
        }
        
        return personalDetails;
    }

    @Override
    public Person filter(Person person, String orcid) {
        if(person.getAddresses() != null) {
            filter(person.getAddresses().getAddress(), orcid);
        }
        if(person.getEmails() != null) {
            filter(person.getEmails().getEmails(), orcid);
        }
        if(person.getExternalIdentifiers() != null) {
            filter(person.getExternalIdentifiers().getExternalIdentifiers(), orcid);
        }
        if(person.getKeywords() != null) {
            filter(person.getKeywords().getKeywords(), orcid);
        }
        
        if(person.getOtherNames() != null) {
            filter(person.getOtherNames().getOtherNames(), orcid);
        }
        
        if(person.getResearcherUrls() != null) {
            filter(person.getResearcherUrls().getResearcherUrls(), orcid);
        }        

        // If it is private
        try {
            if (person.getBiography() != null) {
                orcidSecurityManager.checkBiographicalVisibility(person.getBiography(), orcid);
            }
        } catch (OrcidVisibilityException | OrcidUnauthorizedException e) {
            person.setBiography(null);
        }

        try {
            if (person.getName() != null) {
                orcidSecurityManager.checkBiographicalVisibility(person.getName(), orcid);
            }
        } catch (OrcidVisibilityException | OrcidUnauthorizedException e) {
            person.setName(null);
        }

        return person;
    }

    @Override
    public Record filter(Record record, String orcid) {
        if(record.getPerson() != null) {
            record.setPerson(filter(record.getPerson(), orcid));
        }
        
        if(record.getActivitiesSummary() != null) {
            record.setActivitiesSummary(filter(record.getActivitiesSummary(), orcid));
        }
        
        return record;
    }
    
    
}
