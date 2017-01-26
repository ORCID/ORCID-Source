package org.orcid.core.manager.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.UserConnectionManager;
import org.orcid.jaxb.model.notification_v2.Notification;
import org.orcid.model.notification.institutional_sign_in_v2.NotificationInstitutionalConnection;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;

/**
 * 
 * @author Will Simpson
 *
 */
public class UserConnectionManagerImpl implements UserConnectionManager {

    @Resource
    private UserConnectionDao userConnectionDao;

    @Resource
    private NotificationManager notificationManager;

    @Override
    public List<UserconnectionEntity> findByOrcid(String orcid) {
        return userConnectionDao.findByOrcid(orcid);
    }

    @Override
    public void remove(String orcid, UserconnectionPK userConnectionPK) {
        List<Notification> notifications = notificationManager.findNotificationAlertsByOrcid(orcid);
        notifications.forEach(n -> {
            if (n instanceof NotificationInstitutionalConnection) {
                NotificationInstitutionalConnection nic = (NotificationInstitutionalConnection) n;
                if (userConnectionPK.getProviderid().equals(nic.getAuthenticationProviderId())) {
                    notificationManager.flagAsArchived(orcid, n.getPutCode(), false);
                }
            }
        });
        userConnectionDao.remove(userConnectionPK);
    }

}
