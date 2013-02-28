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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "registration")
public class RegistrationEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String email;

    private String givenNames;

    private String familyName;

    private String vocativeName;

    private String institutionName;

    private String sponsorId;

    private String sponsorName;

    private HearAboutEntity hearAbout;

    private String emailSendStatus;

    private String ipAddress;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "registration_seq")
    @SequenceGenerator(name = "registration_seq", sequenceName = "registration_seq")
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Transient
    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }

    @Transient
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @Transient
    public String getVocativeName() {
        return vocativeName;
    }

    public void setVocativeName(String vocativeName) {
        this.vocativeName = vocativeName;
    }

    @Transient
    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    @Transient
    public String getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(String sponsorId) {
        this.sponsorId = sponsorId;
    }

    @Transient
    // XXX needs to be sponsor entity instead of just string.
    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    @ManyToOne
    public HearAboutEntity getHearAbout() {
        return hearAbout;
    }

    public void setHearAbout(HearAboutEntity hearAbout) {
        this.hearAbout = hearAbout;
    }

    @Column(name = "email_send_status")
    public String getEmailSendStatus() {
        return emailSendStatus;
    }

    public void setEmailSendStatus(String emailSendStatus) {
        this.emailSendStatus = emailSendStatus;
    }

    @Column(name = "ip_address")
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

}
