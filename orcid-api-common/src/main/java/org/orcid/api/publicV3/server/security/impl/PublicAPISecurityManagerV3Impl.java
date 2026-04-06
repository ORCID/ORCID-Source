package org.orcid.api.publicV3.server.security.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.api.publicV3.server.security.PublicAPISecurityManagerV3;
import org.orcid.core.exception.OrcidCoreExceptionMapper;
import org.orcid.core.exception.OrcidNoBioException;
import org.orcid.core.exception.OrcidNonPublicElementException;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.v3.release.common.Filterable;
import org.orcid.jaxb.model.v3.release.common.VisibilityType;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.record.ActivitiesContainer;
import org.orcid.jaxb.model.v3.release.record.Activity;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.Group;
import org.orcid.jaxb.model.v3.release.record.GroupableActivity;
import org.orcid.jaxb.model.v3.release.record.GroupsContainer;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.PersonalDetails;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class PublicAPISecurityManagerV3Impl implements PublicAPISecurityManagerV3 {

    @Resource
    private OrcidCoreExceptionMapper orcidCoreExceptionMapper;

    @Override
    public void checkIsPublic(VisibilityType visibilityType) {
        if (visibilityType != null && !org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC.equals(visibilityType.getVisibility())) {
            throw new OrcidNonPublicElementException();
        }
    }

    @Override
    public void checkIsPublic(Biography biography) {
        if (biography == null) {
            throw new OrcidNoBioException();
        }

        if (PojoUtil.isEmpty(biography.getContent()) && biography.getVisibility() == null) {
            return;
        }

        if (!org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC.equals(biography.getVisibility())) {
            throw new OrcidNonPublicElementException();
        }
    }

    @Override
    public void filter(ActivitiesSummary activitiesSummary) {
        if (activitiesSummary == null) {
            return;
        }
        if (activitiesSummary.getDistinctions() != null) {
            filter(activitiesSummary.getDistinctions());
        }
        if (activitiesSummary.getEmployments() != null) {
            filter(activitiesSummary.getEmployments());
        }
        if (activitiesSummary.getEducations() != null) {
            filter(activitiesSummary.getEducations());
        }
        if (activitiesSummary.getInvitedPositions() != null) {
            filter(activitiesSummary.getInvitedPositions());
        }
        if (activitiesSummary.getMemberships() != null) {
            filter(activitiesSummary.getMemberships());
        }
        if (activitiesSummary.getQualifications() != null) {
            filter(activitiesSummary.getQualifications());
        }
        if (activitiesSummary.getServices() != null) {
            filter(activitiesSummary.getServices());
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
    public void filter(PeerReviews peerReviews) {
        if (peerReviews == null || peerReviews.retrieveGroups() == null) {
            return;
        }

        Iterator<PeerReviewGroup> groupIt = peerReviews.retrieveGroups().iterator();

        while (groupIt.hasNext()) {
            PeerReviewGroup g = groupIt.next();
            for (PeerReviewDuplicateGroup duplicateGroup : g.getPeerReviewGroup()) {
                if (duplicateGroup.getActivities() != null) {
                    Iterator<? extends GroupableActivity> activityIt = duplicateGroup.getActivities().iterator();
                    while (activityIt.hasNext()) {
                        GroupableActivity activity = activityIt.next();
                        try {
                            checkIsPublic(activity);
                        } catch (OrcidNonPublicElementException e) {
                            activityIt.remove();
                        }
                    }
                    if (duplicateGroup.getActivities().isEmpty()) {
                        groupIt.remove();
                    }
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
    public void filter(WorkBulk workBulk) {
        if (workBulk != null && workBulk.getBulk() != null) {
            List<BulkElement> filtered = new ArrayList<>();
            for (int i = 0; i < workBulk.getBulk().size(); i++) {
                BulkElement bulkElement = workBulk.getBulk().get(i);
                if (bulkElement instanceof OrcidError) {
                    filtered.add(bulkElement);
                } else {
                    try {
                        checkIsPublic((Work) bulkElement);
                        filtered.add(bulkElement);
                    } catch (OrcidNonPublicElementException e) {
                        filtered.add(orcidCoreExceptionMapper.getV3OrcidError(e));
                    }
                }
            }
            workBulk.setBulk(filtered);
        }
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
