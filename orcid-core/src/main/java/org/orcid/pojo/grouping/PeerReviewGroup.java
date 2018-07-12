package org.orcid.pojo.grouping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.rc1.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewSummary;
import org.orcid.pojo.ajaxForm.PeerReviewForm;

public class PeerReviewGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<PeerReviewForm> peerReviews;
    
    private String description;
    
    private String name;
    
    private String type;
    
    private long id;
    
    public List<PeerReviewForm> getPeerReviews() {
        return peerReviews;
    }

    public void setPeerReviews(List<PeerReviewForm> peerReviews) {
        this.peerReviews = peerReviews;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static PeerReviewGroup valueOf(org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewGroup peerReviewGroup, GroupIdRecord groupIdRecord) {
        String groupName = groupIdRecord != null && groupIdRecord.getName() != null ? groupIdRecord.getName() : "";
        String groupDescription = groupIdRecord != null && groupIdRecord.getDescription() != null ? groupIdRecord.getDescription() : "";
        String type = groupIdRecord != null && groupIdRecord.getType() != null ? groupIdRecord.getType() : "";
        
        PeerReviewGroup group = new PeerReviewGroup();
        group.setName(groupName);
        group.setType(type);
        group.setDescription(groupDescription);
        group.setId(groupIdRecord.getPutCode());
        group.setPeerReviews(new ArrayList<>());

        for (PeerReviewSummary peerReviewSummary : peerReviewGroup.getPeerReviewSummary()) {
            PeerReviewForm peerReviewForm = PeerReviewForm.valueOf(peerReviewSummary);
            group.getPeerReviews().add(peerReviewForm);
        }
        return group;
    }

}
