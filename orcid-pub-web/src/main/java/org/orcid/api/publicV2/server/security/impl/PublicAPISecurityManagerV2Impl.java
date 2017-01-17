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
package org.orcid.api.publicV2.server.security.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.orcid.api.publicV2.server.security.PublicAPISecurityManagerV2;
import org.orcid.core.exception.OrcidNonPublicElementException;
import org.orcid.jaxb.model.common_rc4.Filterable;
import org.orcid.jaxb.model.common_rc4.VisibilityType;
import org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary;
import org.orcid.jaxb.model.record_rc4.ActivitiesContainer;
import org.orcid.jaxb.model.record_rc4.Activity;
import org.orcid.jaxb.model.record_rc4.Addresses;
import org.orcid.jaxb.model.record_rc4.Biography;
import org.orcid.jaxb.model.record_rc4.Emails;
import org.orcid.jaxb.model.record_rc4.Group;
import org.orcid.jaxb.model.record_rc4.GroupableActivity;
import org.orcid.jaxb.model.record_rc4.GroupsContainer;
import org.orcid.jaxb.model.record_rc4.Keywords;
import org.orcid.jaxb.model.record_rc4.Name;
import org.orcid.jaxb.model.record_rc4.OtherNames;
import org.orcid.jaxb.model.record_rc4.Person;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc4.PersonalDetails;
import org.orcid.jaxb.model.record_rc4.Record;
import org.orcid.jaxb.model.record_rc4.ResearcherUrls;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class PublicAPISecurityManagerV2Impl implements PublicAPISecurityManagerV2 {
    
    @Override
    public void checkIsPublic(VisibilityType visibilityType) {
        if (visibilityType != null && !org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC.equals(visibilityType.getVisibility())) {
            throw new OrcidNonPublicElementException();
        }
    }

    @Override
    public void checkIsPublic(Biography biography) {
        if (biography != null && !PojoUtil.isEmpty(biography.getContent()) && !org.orcid.jaxb.model.common_rc4.Visibility.PUBLIC.equals(biography.getVisibility())) {
            throw new OrcidNonPublicElementException();
        }
    }
    
    @Override
    public void filter(ActivitiesSummary activitiesSummary) {
        if (activitiesSummary == null) {
            return;
        }
        if (activitiesSummary.getEmployments() != null) {
            filter(activitiesSummary.getEmployments());            
        }
        if (activitiesSummary.getEducations() != null) {
            filter(activitiesSummary.getEducations());            
        }

        if (activitiesSummary.getFundings() != null) {
            filter(activitiesSummary.getFundings());            
        }
        if (activitiesSummary.getWorks() != null) {
            filter(activitiesSummary.getWorks());            
        }
        if (activitiesSummary.getPeerReviews() != null) {
            filter(activitiesSummary.getPeerReviews());            
        }
    }

    @Override
    public void filter(ActivitiesContainer container) {
        if (container == null || container.retrieveActivities() == null) {
            return;
        }
        Collection<? extends Activity> list = container.retrieveActivities();
        list.removeIf(e -> {
            try {
                checkIsPublic(e);
                return false;
            } catch (OrcidNonPublicElementException ex) {
                return true;
            }
        });
    }

    @Override
    public void filter(GroupsContainer container) {
        if (container == null || container.retrieveGroups() == null) {
            return;
        }

        Iterator<? extends Group> groupIt = container.retrieveGroups().iterator();

        while (groupIt.hasNext()) {
            Group g = groupIt.next();
            if (g.getActivities() != null) {
                Iterator<? extends GroupableActivity> activityIt = g.getActivities().iterator();
                while (activityIt.hasNext()) {
                    GroupableActivity activity = activityIt.next();
                    try {
                        checkIsPublic(activity);
                    } catch (OrcidNonPublicElementException e) {
                        activityIt.remove();
                    }
                }
                if (g.getActivities().isEmpty()) {
                    groupIt.remove();
                }
            }
        }
    }

    @Override
    public void filter(PersonalDetails personalDetails) {
        if (personalDetails == null) {
            return;
        }
        if (personalDetails.getName() != null) {
            try {
                checkIsPublic(personalDetails.getName());
            } catch (OrcidNonPublicElementException e) {
                personalDetails.setName(null);
            }
        }

        if (personalDetails.getBiography() != null) {
            try {
                checkIsPublic(personalDetails.getBiography());
            } catch (OrcidNonPublicElementException e) {
                personalDetails.setBiography(null);
            }
        }

        if (personalDetails.getOtherNames() != null && personalDetails.getOtherNames().getOtherNames() != null) {
            personalDetails.getOtherNames().getOtherNames().removeIf(e -> {
                try {
                    checkIsPublic(e);
                    return false;
                } catch (OrcidNonPublicElementException ex) {   
                    return true;
                }
            });            
        }
    }

    @Override
    public void filter(Addresses addresses) {
        if (addresses == null) {
            return;
        }
        filter(addresses.getAddress());
    }

    @Override
    public void filter(Emails emails) {
        if (emails == null) {
            return;
        }
        filter(emails.getEmails());
    }

    @Override
    public void filter(Keywords keywords) {
        if (keywords == null) {
            return;
        }
        filter(keywords.getKeywords());
    }

    @Override
    public void filter(OtherNames otherNames) {
        if (otherNames == null) {
            return;
        }
        filter(otherNames.getOtherNames());
    }

    @Override
    public void filter(PersonExternalIdentifiers extIds) {
        if (extIds == null) {
            return;
        }
        filter(extIds.getExternalIdentifiers());

    }

    @Override
    public void filter(ResearcherUrls researcherUrls) {
        if (researcherUrls == null) {
            return;
        }
        filter(researcherUrls.getResearcherUrls());
    }

    private void filter(List<? extends Filterable> filterable) {
        if (filterable == null) {
            return;
        }

        filterable.removeIf(e -> {
            try {
                checkIsPublic(e);
                return false;
            } catch (OrcidNonPublicElementException ex) {
                return true;
            }
        });
    }

    @Override
    public void filter(Person person) {
        if (person == null) {
            return;
        }

        if (person.getAddresses() != null) {
            filter(person.getAddresses());
        }

        if (person.getEmails() != null) {
            filter(person.getEmails());
        }

        if (person.getExternalIdentifiers() != null) {
            filter(person.getExternalIdentifiers());
        }

        if (person.getKeywords() != null) {
            filter(person.getKeywords());
        }

        if (person.getOtherNames() != null) {
            filter(person.getOtherNames());
        }
        
        if (person.getResearcherUrls() != null) {
            filter(person.getResearcherUrls());
        }
        
        Name name = person.getName();
        if (name != null) {
            try {
                checkIsPublic(name);
            } catch (OrcidNonPublicElementException ex) {
                person.setName(null);
            }
        }

        Biography bio = person.getBiography();
        if (bio != null) {
            try {
                checkIsPublic(bio);
            } catch (OrcidNonPublicElementException ex) {
                person.setBiography(null);
            }

        }
    }

    @Override
    public void filter(Record record) {
        if (record == null) {
            return;
        }

        filter(record.getActivitiesSummary());
        filter(record.getPerson());
    }
}
