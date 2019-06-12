package org.orcid.core.manager.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.UserConnectionManager;
import org.orcid.jaxb.model.notification_v2.Notification;
import org.orcid.model.notification.institutional_sign_in_v2.NotificationInstitutionalConnection;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.UserConnectionStatus;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public UserconnectionEntity findByProviderIdAndProviderUserId(String providerUserId, String providerId) {        
        return userConnectionDao.findByProviderIdAndProviderUserId(providerUserId, providerId);
    }

    @Override
    public void updateLoginInformation(UserconnectionPK pk) {
        userConnectionDao.updateLoginInformation(pk);
    }

    @Override
    public UserconnectionEntity findByProviderIdAndProviderUserIdAndIdType(String userId, String shibIdentityProvider, String idType) {
        return userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(userId, shibIdentityProvider, idType);
    }

    @Override
    @Transactional
    public void update(String providerUserId, String providerId, String accessToken, Long expireTime) {
        UserconnectionEntity userConnection = userConnectionDao.findByProviderIdAndProviderUserId(providerUserId, providerId);
        if(userConnection != null) {
            Date now = new Date();
            userConnection.setLastLogin(now);
            userConnection.setLastModified(now);
            userConnection.setAccesstoken(accessToken);
            userConnection.setExpiretime(expireTime);
            userConnectionDao.merge(userConnection);
        }        
    }

    @Override
    public void create(String providerUserId, String providerId, String email, String userName, String accessToken, Long expireTime) {        
        UserconnectionEntity userConnectionEntity = new UserconnectionEntity();
        String randomId = Long.toString(new Random(Calendar.getInstance().getTimeInMillis()).nextLong());
        UserconnectionPK pk = new UserconnectionPK(randomId, providerId, providerUserId);
        userConnectionEntity.setDisplayname(userName);
        userConnectionEntity.setRank(1);
        userConnectionEntity.setId(pk);
        userConnectionEntity.setLinked(false);
        userConnectionEntity.setLastLogin(new Date());
        userConnectionEntity.setEmail(email);
        userConnectionEntity.setAccesstoken(accessToken);
        userConnectionEntity.setExpiretime(expireTime);
        userConnectionEntity.setConnectionSatus(UserConnectionStatus.STARTED);
        userConnectionDao.persist(userConnectionEntity);
    }

}
