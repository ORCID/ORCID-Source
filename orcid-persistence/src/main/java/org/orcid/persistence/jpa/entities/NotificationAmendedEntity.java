/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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

import java.util.SortedSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.orcid.jaxb.model.notification.amended_v2.AmendedSection;
import org.orcid.persistence.jpa.entities.NotificationWorkEntity.ChronologicallyOrderedNotificationWorkEntityComparator;

/**
 * 
 * @author Will Simpson
 *
 */
@Entity
@DiscriminatorValue("AMENDED")
public class NotificationAmendedEntity extends NotificationEntity {

    private static final long serialVersionUID = 1L;
    private AmendedSection amendedSection = AmendedSection.UNKNOWN;
    private SortedSet<NotificationWorkEntity> notificationWorks;

    @Enumerated(EnumType.STRING)
    @Column(name = "amended_section")
    public AmendedSection getAmendedSection() {
        return amendedSection;
    }

    public void setAmendedSection(AmendedSection amendedSection) {
        this.amendedSection = amendedSection;
    }

    @OneToMany(mappedBy = "notification", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @Sort(type = SortType.COMPARATOR, comparator = ChronologicallyOrderedNotificationWorkEntityComparator.class)
    public SortedSet<NotificationWorkEntity> getNotificationWorks() {
        return notificationWorks;
    }

    public void setNotificationWorks(SortedSet<NotificationWorkEntity> notificationWorks) {
        this.notificationWorks = notificationWorks;
    }

}
