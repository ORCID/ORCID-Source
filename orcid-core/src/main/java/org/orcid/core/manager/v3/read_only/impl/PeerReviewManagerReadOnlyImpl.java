package org.orcid.core.manager.v3.read_only.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.orcid.core.adapter.v3.JpaJaxbPeerReviewAdapter;
import org.orcid.core.manager.v3.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.utils.v3.activities.ActivitiesGroup;
import org.orcid.core.utils.v3.activities.ActivitiesGroupGenerator;
import org.orcid.core.utils.v3.activities.PeerReviewDuplicateGroupComparator;
import org.orcid.core.utils.v3.activities.PeerReviewGroupGenerator;
import org.orcid.core.utils.v3.activities.PeerReviewGroupKey;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.GroupAble;
import org.orcid.jaxb.model.v3.release.record.GroupableActivity;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.pojo.PeerReviewMinimizedSummary;

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
     */
    @Override
    public List<PeerReviewSummary> getPeerReviewSummaryList(String orcid) {
        List<PeerReviewEntity> peerReviewEntities = peerReviewDao.getByUser(orcid, getLastModified(orcid));
        return jpaJaxbPeerReviewAdapter.toPeerReviewSummary(peerReviewEntities);
    }

    /**
     * Get the list of peer reivews that belongs to a user
     *
     * @param orcid
     * @param justPublic
     *
     * @return the list of peer reviews that belongs to this user
     */
    @Override
    public List<PeerReviewMinimizedSummary> getPeerReviewMinimizedSummaryList(String orcid, boolean justPublic) {
        List<PeerReviewMinimizedSummary> peerReviewMinimizedSummaryList = new ArrayList<>();
        List<Object[]> list = peerReviewDao.getPeerReviewsByOrcid(orcid, justPublic);
        for(Object[] q1 : list){
            BigInteger groupId = (BigInteger) q1[0];
            String groupIdValue = q1[1].toString();
            BigInteger putCode = (BigInteger) q1[2];
            String visibility = q1[3].toString();
            String groupName = q1[4].toString();
            String sourceId = (q1[5] == null) ? null : q1[5].toString();
            String clientSourceId = (q1[6] == null) ? null : q1[6].toString();
            String assertionOriginSourceId = (q1[7] == null) ? null : q1[7].toString();
            if (peerReviewMinimizedSummaryList.size() > 0) {
                List<PeerReviewMinimizedSummary> peerReviews = peerReviewMinimizedSummaryList
                        .stream()
                        .filter(peerReviewMinimizedSummary -> groupId.equals(peerReviewMinimizedSummary.getGroupId()))
                        .collect(Collectors.toList());
                if (peerReviews.size() > 0) {
                    peerReviews.get(0).addPutCode(putCode);
                    peerReviews.get(0).setDuplicated(peerReviews.get(0).getDuplicated() + 1);
                    if (!Visibility.valueOf(visibility).equals(peerReviews.get(0).getVisibility())) {
                        peerReviews.get(0).setVisibilityError(true);
                    }
                } else {
                    PeerReviewMinimizedSummary ps = new PeerReviewMinimizedSummary(orcid, groupId, groupIdValue, putCode, Visibility.fromValue(visibility), groupName, 1);
                    ps.setAssertionOriginSourceId(assertionOriginSourceId);
                    ps.setClientSourceId(clientSourceId);
                    ps.setSourceId(sourceId);
                    peerReviewMinimizedSummaryList.add(ps);
                }
            } else {
                PeerReviewMinimizedSummary ps = new PeerReviewMinimizedSummary(orcid, groupId, groupIdValue, putCode, Visibility.fromValue(visibility), groupName, 1);
                ps.setAssertionOriginSourceId(assertionOriginSourceId);
                ps.setClientSourceId(clientSourceId);
                ps.setSourceId(sourceId);                
                peerReviewMinimizedSummaryList.add(ps);
            }
        }
        return peerReviewMinimizedSummaryList;
    }

    /**
     * Get the list of peer reivews that belongs to a user
     *
     * @param orcid
     * @param groupId
     *
     * @return the list of peer reviews that belongs to this user
     */
    @Override
    public List<PeerReviewSummary> getPeerReviewSummaryListByGroupId(String orcid, String groupId, boolean justPublic) {
        List<PeerReviewEntity> peerReviewEntities = peerReviewDao.getPeerReviewsByOrcidAndGroupId(orcid, groupId, justPublic);
        return jpaJaxbPeerReviewAdapter.toPeerReviewSummary(peerReviewEntities);
    }
    /**
     * Generate a grouped list of peer reviews with the given list of peer
     * reviews
     * 
     * @param peerReviews
     *            The list of peer reviews to group
     * @param justPublic
     *            Specify if we want to group only the public elements in the
     *            given list
     * @return PeerReviews element with the PeerReviewSummary elements grouped
     */
    @Override
    public PeerReviews groupPeerReviews(List<PeerReviewSummary> peerReviews, boolean justPublic) {
        PeerReviewGroupGenerator groupGenerator = new PeerReviewGroupGenerator();
        PeerReviews result = new PeerReviews();
        for (PeerReviewSummary peerReview : peerReviews) {
            if (justPublic && !peerReview.getVisibility().equals(org.orcid.jaxb.model.v3.release.common.Visibility.PUBLIC)) {
                // If it is just public and the funding is not public, just
                // ignore it
            } else {
                groupGenerator.group(peerReview);
            }
        }

        List<org.orcid.core.utils.v3.activities.PeerReviewGroup> groups = groupGenerator.getGroups();

        for (org.orcid.core.utils.v3.activities.PeerReviewGroup group : groups) {
            Set<PeerReviewGroupKey> groupKeys = group.getGroupKeys();
            Set<PeerReviewSummary> activities = group.getActivities();
            PeerReviewGroup peerReviewGroup = new PeerReviewGroup();
            // Fill the peer review groups with the external identifiers
            if (groupKeys == null || groupKeys.isEmpty()) {
                // Initialize the ids as an empty list
                peerReviewGroup.getIdentifiers().getExternalIdentifier();
            } else {
                for (PeerReviewGroupKey key : groupKeys) {
                    ExternalID id = new ExternalID();
                    id.setType(PeerReviewGroupKey.KEY_NAME);
                    id.setValue(key.getGroupId());
                    peerReviewGroup.getIdentifiers().getExternalIdentifier().add(id);
                }
            }

            List<PeerReviewDuplicateGroup> groupedDuplicates = groupDuplicates(activities);
            peerReviewGroup.getPeerReviewGroup().addAll(groupedDuplicates);

            // Sort the peer reviews
            Collections.sort(peerReviewGroup.getPeerReviewGroup(), new PeerReviewDuplicateGroupComparator());
            result.getPeerReviewGroup().add(peerReviewGroup);
        }

        return result;
    }

    private List<PeerReviewDuplicateGroup> groupDuplicates(Set<PeerReviewSummary> peerReviews) {
        ActivitiesGroupGenerator groupGenerator = new ActivitiesGroupGenerator();
        List<PeerReviewDuplicateGroup> groupedDuplicates = new ArrayList<>();
        for (PeerReviewSummary peerReview : peerReviews) {
            groupGenerator.group(peerReview);
        }

        List<ActivitiesGroup> groups = groupGenerator.getGroups();

        for (ActivitiesGroup group : groups) {
            Set<GroupAble> externalIdentifiers = group.getGroupKeys();
            Set<GroupableActivity> activities = group.getActivities();
            PeerReviewDuplicateGroup peerReviewDuplicateGroup = new PeerReviewDuplicateGroup();
            // Fill the peer review groups with the external identifiers
            if (externalIdentifiers == null || externalIdentifiers.isEmpty()) {
                // Initialize the ids as an empty list
                peerReviewDuplicateGroup.getIdentifiers().getExternalIdentifier();
            } else {
                for (GroupAble extId : externalIdentifiers) {
                    ExternalID peerReviewExtId = (ExternalID) extId;
                    peerReviewDuplicateGroup.getIdentifiers().getExternalIdentifier().add(peerReviewExtId);
                }
            }

            // Fill the peer review group with the list of activities
            for (GroupableActivity activity : activities) {
                PeerReviewSummary peerReviewSummary = (PeerReviewSummary) activity;
                peerReviewDuplicateGroup.getPeerReviewSummary().add(peerReviewSummary);
            }
            groupedDuplicates.add(peerReviewDuplicateGroup);
        }

        return groupedDuplicates;
    }

    @Override
    public Boolean hasPublicPeerReviews(String orcid) {
        return peerReviewDao.hasPublicPeerReviews(orcid);
    }

}
