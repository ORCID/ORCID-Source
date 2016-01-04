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
import org.orcid.jaxb.model.common.Filterable;
import org.orcid.jaxb.model.record.summary_rc1.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc1.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc1.Educations;
import org.orcid.jaxb.model.record.summary_rc1.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc1.Employments;
import org.orcid.jaxb.model.record.summary_rc1.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc1.Fundings;
import org.orcid.jaxb.model.record.summary_rc1.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc1.Works;
import org.orcid.jaxb.model.record_rc1.Group;
import org.orcid.jaxb.model.record_rc1.GroupableActivity;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.Person;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;
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
    public ActivitiesSummary filter(ActivitiesSummary activitiesSummary) {
        if (activitiesSummary == null) {
            return null;
        }
        Educations educations = activitiesSummary.getEducations();
        if (educations != null) {
            List<EducationSummary> summaries = educations.getSummaries();
            filter(summaries);
            if (summaries.isEmpty()) {
                activitiesSummary.setEducations(null);
            }
        }
        Employments employments = activitiesSummary.getEmployments();
        if (employments != null) {
            List<EmploymentSummary> summaries = employments.getSummaries();
            filter(summaries);
            if (summaries.isEmpty()) {
                activitiesSummary.setEmployments(null);
            }
        }
        Fundings fundings = activitiesSummary.getFundings();
        if (fundings != null) {
            List<FundingGroup> fundingGroups = fundings.getFundingGroup();
            filterGroups(fundingGroups);
            if (fundingGroups.isEmpty()) {
                activitiesSummary.setFundings(null);
            }
        }
        Works works = activitiesSummary.getWorks();
        if (works != null) {
            List<WorkGroup> workGroups = works.getWorkGroup();
            filterGroups(workGroups);
            if (workGroups.isEmpty()) {
                activitiesSummary.setWorks(null);
            }
        }
        return activitiesSummary;
    }

    @Override
    public Collection<? extends Filterable> filter(Collection<? extends Filterable> filterables) {
        if (filterables == null) {
            return null;
        }
        for (Iterator<? extends Filterable> iterator = filterables.iterator(); iterator.hasNext();) {
            try {
                orcidSecurityManager.checkVisibility(iterator.next());
            } catch (OrcidVisibilityException | OrcidUnauthorizedException e) {
                iterator.remove();
            }
        }
        return filterables;
    }

    @Override
    public Collection<? extends Group> filterGroups(Collection<? extends Group> groups) {
        if (groups == null) {
            return null;
        }
        for (Iterator<? extends Group> iterator = groups.iterator(); iterator.hasNext();) {
            Group group = iterator.next();
            Collection<? extends GroupableActivity> activities = group.getActivities();
            filter(activities);
            if (activities.isEmpty()) {
                iterator.remove();
            }
        }
        return groups;
    }

    @Override
    public PersonalDetails filter(PersonalDetails personalDetails) {
        if(personalDetails.getName() != null) {
            try {
                orcidSecurityManager.checkVisibility(personalDetails.getName());
            } catch(OrcidVisibilityException | OrcidUnauthorizedException e) {
                personalDetails.setName(null);
            }
        }
        
        if(personalDetails.getBiography() != null) {
            try {
                orcidSecurityManager.checkVisibility(personalDetails.getBiography());
            } catch(OrcidVisibilityException | OrcidUnauthorizedException e) {
                personalDetails.setBiography(null);
            }
        }
        
        if(personalDetails.getOtherNames() != null) {
            if(personalDetails.getOtherNames().getOtherNames() != null) {
                List<OtherName> filteredOtherNames = new ArrayList<OtherName>();
                for(OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
                    try {
                        orcidSecurityManager.checkVisibility(otherName);
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
    public Person filter(Person person) {
        filter(person.getAddresses().getAddress());
        filter(person.getEmails().getEmails());
        filter(person.getExternalIdentifiers().getExternalIdentifier());
        filter(person.getKeywords().getKeywords());
        filter(person.getOtherNames().getOtherNames());
        filter(person.getResearcherUrls().getResearcherUrls());

        // If it is private
        try {
            if (person.getBiography() != null) {
                orcidSecurityManager.checkVisibility(person.getBiography());
            }
        } catch (OrcidVisibilityException | OrcidUnauthorizedException e) {
            person.setBiography(null);
        }

        try {
            if (person.getName() != null) {
                orcidSecurityManager.checkVisibility(person.getName());
            }
        } catch (OrcidVisibilityException | OrcidUnauthorizedException e) {
            person.setName(null);
        }

        return person;
    }
}
