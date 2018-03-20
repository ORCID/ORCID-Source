package org.orcid.persistence.jpa.entities.keys;

import java.io.Serializable;

/**
 * orcid-entities - Dec 6, 2011 - ProfileWorkEntityPk
 * 
 * @author Declan Newman (declan)
 */
public class NotificationWorkEntityPk implements Serializable {

    private static final long serialVersionUID = -6483017414290915958L;

    private Long notification;
    private Long work;

    public Long getNotification() {
        return notification;
    }

    public void setNotification(Long notification) {
        this.notification = notification;
    }

    public Long getWork() {
        return work;
    }

    public void setWork(Long work) {
        this.work = work;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((notification == null) ? 0 : notification.hashCode());
        result = prime * result + ((work == null) ? 0 : work.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NotificationWorkEntityPk other = (NotificationWorkEntityPk) obj;
        if (notification == null) {
            if (other.notification != null)
                return false;
        } else if (!notification.equals(other.notification))
            return false;
        if (work == null) {
            if (other.work != null)
                return false;
        } else if (!work.equals(other.work))
            return false;
        return true;
    }

}
