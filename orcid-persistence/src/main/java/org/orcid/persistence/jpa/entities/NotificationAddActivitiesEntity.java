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

import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

/**
 * 
 * @author Will Simpson
 *
 */
@Entity
@DiscriminatorValue("ADD_ACTIVITIES")
public class NotificationAddActivitiesEntity extends NotificationEntity {

    private static final long serialVersionUID = 1L;

    private Set<NotificationActivityEntity> notificationActivities;

    @OneToMany
    @JoinColumn(name="notification_id")
    public Set<NotificationActivityEntity> getNotificationActivities() {
        return notificationActivities;
    }

    public void setNotificationActivities(Set<NotificationActivityEntity> notificationActivities) {
        this.notificationActivities = notificationActivities;
    }
    
}
