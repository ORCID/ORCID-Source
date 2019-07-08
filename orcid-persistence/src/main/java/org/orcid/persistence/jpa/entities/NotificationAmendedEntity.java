package org.orcid.persistence.jpa.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

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
    private Set<NotificationItemEntity> notificationItems;

    @Column(name = "amended_section")
    public String getAmendedSection() {
        return amendedSection;
    }

    public void setAmendedSection(String amendedSection) {
        this.amendedSection = amendedSection;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "notification_id")  
    public Set<NotificationItemEntity> getNotificationItems() {
        return notificationItems;
    }

    public void setNotificationItems(Set<NotificationItemEntity> notificationItems) {
        this.notificationItems = notificationItems;
    }

}
