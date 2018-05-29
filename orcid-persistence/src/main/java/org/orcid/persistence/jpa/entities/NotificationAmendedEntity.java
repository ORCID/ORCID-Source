package org.orcid.persistence.jpa.entities;

import java.util.SortedSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.orcid.persistence.jpa.entities.NotificationWorkEntity.ChronologicallyOrderedNotificationWorkEntityComparator;

/**
 * 
 * @author Will Simpson
 *
 */
@Entity
@DiscriminatorValue("AMENDED")
public class NotificationAmendedEntity extends NotificationEntity {

    private static final String AMENDED_SECTION_DEFAULT = "UNKNOWN";
    
    private static final long serialVersionUID = 1L;
    private String amendedSection = AMENDED_SECTION_DEFAULT;
    private SortedSet<NotificationWorkEntity> notificationWorks;

    @Column(name = "amended_section")
    public String getAmendedSection() {
        return amendedSection;
    }

    public void setAmendedSection(String amendedSection) {
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
