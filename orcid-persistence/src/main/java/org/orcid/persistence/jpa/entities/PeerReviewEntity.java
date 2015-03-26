package org.orcid.persistence.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "peer_review")
public class PeerReviewEntity extends BaseEntity<Long> {

    @Override
    public Long getId() {
        // TODO Auto-generated method stub
        return null;
    }

}
