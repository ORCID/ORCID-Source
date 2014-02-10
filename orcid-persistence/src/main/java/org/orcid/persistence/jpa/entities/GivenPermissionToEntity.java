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
import javax.persistence.Transient;

/**
 * 
 * @author Will Simpson
 * 
 */
@Table(name = "given_permission_to")
@Entity
public class GivenPermissionToEntity extends BaseEntity<Long> implements ProfileAware {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String giver;
    private ProfileSummaryEntity receiver;
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

    @Column(name = "giver_orcid")
    public String getGiver() {
        return giver;
    }

    public void setGiver(String giver) {
        this.giver = giver;
    }

    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH })
    @JoinColumn(name = "receiver_orcid")
    public ProfileSummaryEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(ProfileSummaryEntity receiver) {
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
    @Transient
    public ProfileEntity getProfile(){
        return new ProfileEntity(giver);
    }

}
