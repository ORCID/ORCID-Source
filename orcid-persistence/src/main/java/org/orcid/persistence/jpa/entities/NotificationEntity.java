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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.orcid.jaxb.model.notification.NotificationType;

/**
 * @author Will Simpson
 */
@Entity
@Table(name = "notification")
public class NotificationEntity extends BaseEntity<Long> implements ProfileAware {

    private static final long serialVersionUID = 1L;

    private Long id;
    private ProfileEntity profile;
    private NotificationType notificationType;
    private String subject;
    private String bodyText;
    private String bodyHtml;
    private Date sentDate;
    private Date readDate;
    private Date archivedDate;
    private boolean sendable;
    private ProfileEntity source;

    @Override
    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orcid", nullable = false, updatable = false, insertable = false)
    @Override
    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Column(name = "body_text")
    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    @Column(name = "body_html")
    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    @Column(name = "sent_date")
    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    @Column(name = "read_date")
    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    @Column(name = "archived_date")
    public Date getArchivedDate() {
        return archivedDate;
    }

    public void setArchivedDate(Date archivedDate) {
        this.archivedDate = archivedDate;
    }

    public boolean isSendable() {
        return sendable;
    }

    public void setSendable(boolean sendable) {
        this.sendable = sendable;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_id")
    public ProfileEntity getSource() {
        return source;
    }

    public void setSource(ProfileEntity source) {
        this.source = source;
    }

}
