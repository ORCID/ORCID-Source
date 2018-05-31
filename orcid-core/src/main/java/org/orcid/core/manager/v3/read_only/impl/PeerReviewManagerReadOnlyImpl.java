package org.orcid.core.manager.v3.read_only.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.adapter.v3.JpaJaxbPeerReviewAdapter;
import org.orcid.core.manager.v3.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.utils.v3.activities.ActivitiesGroup;
import org.orcid.core.utils.v3.activities.ActivitiesGroupGenerator;
import org.orcid.core.utils.v3.activities.GroupableActivityComparator;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.GroupAble;
import org.orcid.jaxb.model.v3.rc1.record.GroupableActivity;
import org.orcid.jaxb.model.v3.rc1.record.PeerReview;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewGroupKey;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviews;
import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;

public class PeerReviewManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements PeerReviewManagerReadOnly {
    @Resource(name = "jpaJaxbPeerReviewAdapterV3")
    protected JpaJaxbPeerReviewAdapter jpaJaxbPeerReviewAdapter;

    protected PeerReviewDao peerReviewDao;
    
    public void setPeerReviewDao(PeerReviewDao peerReviewDao) {
        this.peerReviewDao = peerReviewDao;
    }

    @Override
    public PeerReview getPeerReview(String orcid, Long peerReviewId) {
        PeerReviewEntity peerReviewEntity = peerReviewDao.getPeerReview(orcid, peerReviewId);                       
        return jpaJaxbPeerReviewAdapter.toPeerReview(peerReviewEntity);
    }

    @Override
    public PeerReviewSummary getPeerReviewSummary(String orcid, Long peerReviewId) {
        PeerReviewEntity peerReviewEntity = peerReviewDao.getPeerReview(orcid, peerReviewId);
        return jpaJaxbPeerReviewAdapter.toPeerReviewSummary(peerReviewEntity);
    }    

    @Override
    
    public List<PeerReview> findPeerReviews(String orcid) {
        List<PeerReviewEntity> peerReviewEntities = peerReviewDao.getByUser(orcid, getLastModified(orcid));
        return jpaJaxbPeerReviewAdapter.toPeerReview(peerReviewEntities);        
    }

    /**
     * Get the list of peer reivews that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *            Last modified date used to check the cache
     * @return the list of peer reviews that belongs to this user
     * */
    @Override
    public List<PeerReviewSummary> getPeerReviewSummaryList(String orcid) {
        List<PeerReviewEntity> peerReviewEntities = peerReviewDao.getByUser(orcid, getLastModified(orcid));
        return jpaJaxbPeerReviewAdapter.toPeerReviewSummary(peerReviewEntities);
    }

    /**
     * Generate a grouped list of peer reviews with the given list of peer reviews
     * 
     * @param peerReviews
     *          The list of peer reviews to group
     * @param justPublic
     *          Specify if we want to group only the public elements in the given list
     * @return PeerReviews element with the PeerReviewSummary elements grouped                  
     * */
    @Override
    public PeerReviews groupPeerReviews(List<PeerReviewSummary> peerReviews, boolean justPublic) {
        ActivitiesGroupGenerator groupGenerator = new ActivitiesGroupGenerator();
        PeerReviews result = new PeerReviews();
        for (PeerReviewSummary peerReview : peerReviews) {
            if (justPublic && !peerReview.getVisibility().equals(org.orcid.jaxb.model.v3.rc1.common.Visibility.PUBLIC)) {
                // If it is just public and the funding is not public, just
                // ignore it
            } else {
                groupGenerator.group(peerReview);
            }
        }

        List<ActivitiesGroup> groups = groupGenerator.getGroups();

        for (ActivitiesGroup group : groups) {
            Set<GroupAble> groupKeys = group.getGroupKeys();
            Set<GroupableActivity> activities = group.getActivities();
            PeerReviewGroup peerReviewGroup = new PeerReviewGroup();
            // Fill the peer review groups with the external identifiers
            if(groupKeys == null || groupKeys.isEmpty()) {
                // Initialize the ids as an empty list
                peerReviewGroup.getIdentifiers().getExternalIdentifier();
            } else {
                for (GroupAble groupKey : groupKeys) {
                    PeerReviewGroupKey key = (PeerReviewGroupKey) groupKey;
                    ExternalID id = new ExternalID();
                    id.setType(PeerReviewGroupKey.KEY_NAME);//TODO: this is not nice
                    id.setValue(key.getGroupId());
                    peerReviewGroup.getIdentifiers().getExternalIdentifier().add(id);
                }
            }            

            // Fill the peer review group with the list of activities
            for (GroupableActivity activity : activities) {
                PeerReviewSummary peerReviewSummary = (PeerReviewSummary) activity;
                peerReviewGroup.getPeerReviewSummary().add(peerReviewSummary);
            }

            // Sort the peer reviews
            Collections.sort(peerReviewGroup.getPeerReviewSummary(), new GroupableActivityComparator());

            result.getPeerReviewGroup().add(peerReviewGroup);
        }

        return result;
    }
}