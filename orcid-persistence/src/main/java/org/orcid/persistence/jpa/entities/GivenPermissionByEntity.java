/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.jpa.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * @author Will Simpson
 * 
 */
@Table(name = "given_permission_to")
@Entity
public class GivenPermissionByEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    private Long id;
    private ProfileSummaryEntity giver;
    private String receiver;
    private Date approvalDate;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "given_permission_to_seq")
    @SequenceGenerator(name = "given_permission_to_seq", sequenceName = "given_permission_to_seq")
    @Column(name = "given_permission_to_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH })
    @JoinColumn(name = "giver_orcid")
    public ProfileSummaryEntity getGiver() {
        return giver;
    }

    public void setGiver(ProfileSummaryEntity giver) {
        this.giver = giver;
    }

    @Column(name = "receiver_orcid")
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Column(name = "approval_date")
    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((giver == null) ? 0 : giver.hashCode());
        result = prime * result + ((receiver == null) ? 0 : receiver.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GivenPermissionByEntity other = (GivenPermissionByEntity) obj;
        if (giver == null) {
            if (other.giver != null)
                return false;
        } else if (!giver.equals(other.giver))
            return false;
        if (receiver == null) {
            if (other.receiver != null)
                return false;
        } else if (!receiver.equals(other.receiver))
            return false;
        return true;
    }

}
