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
import org.orcid.jaxb.model.v3.dev1.notification.amended.AmendedSection;
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
