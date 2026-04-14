package org.orcid.persistence.jpa.entities;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

/**
 * 
 * @author Will Simpson
 *
 */
@Entity
@DiscriminatorValue("PERMISSION")
public class NotificationAddItemsEntity extends NotificationEntity implements ActionableNotificationEntity {

    private static final long serialVersionUID = 1L;

    private String authorizationUrl;
    private Set<NotificationItemEntity> notificationItems;

    @Column(name = "authorization_url")
    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
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
