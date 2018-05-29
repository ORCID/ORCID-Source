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
