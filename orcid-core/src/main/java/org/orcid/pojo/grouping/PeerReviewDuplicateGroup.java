package org.orcid.pojo.grouping;

import java.io.Serializable;
import java.util.List;

import org.orcid.pojo.ajaxForm.PeerReviewForm;

public class PeerReviewDuplicateGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<PeerReviewForm> peerReviews;

    private long activePutCode;
    
    private long id;
    
    public List<PeerReviewForm> getPeerReviews() {
        return peerReviews;
    }

    public void setPeerReviews(List<PeerReviewForm> peerReviews) {
        this.peerReviews = peerReviews;
    }

    public long getActivePutCode() {
        return activePutCode;
    }

    public void setActivePutCode(long activePutCode) {
        this.activePutCode = activePutCode;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
