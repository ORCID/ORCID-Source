package org.orcid.core.manager.impl;

import java.util.*;

import javax.annotation.Resource;

import org.orcid.core.constants.OrcidOauth2Constants;
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
        List<UserconnectionEntity> userConnections = userConnectionDao.findByOrcid(orcid);
        userConnections.removeIf(userConnection -> userConnection.getId().getProviderid().equals("google") || userConnection.getId().getProviderid().equals("facebook"));
        return userConnections;
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
    public void update(UserconnectionEntity userConnectionEntity) {
        userConnectionDao.merge(userConnectionEntity);
    }

    @Override
    @Transactional
    public void update(String providerUserId, String providerId, String accessToken, Long expireTime) {
        UserconnectionEntity userConnection = userConnectionDao.findByProviderIdAndProviderUserId(providerUserId, providerId);
        if (userConnection != null) {
            Date now = new Date();
            userConnection.setLastLogin(now);
            userConnection.setAccesstoken(accessToken);
            userConnection.setExpiretime(expireTime);
            userConnectionDao.merge(userConnection);
        }
    }

    @Override
    public String create(String providerUserId, String providerId, String email, String userName, String accessToken, Long expireTime) {
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
        return randomId;
    }

    @Override
    public Map<String, String> getUserConnectionInfo(String userConnectionId) {
        UserconnectionEntity entity = userConnectionDao.findByUserConnectionId(userConnectionId);
        Map<String, String> data = new HashMap<String, String>();
        data.put(OrcidOauth2Constants.PROVIDER_ID, entity.getId().getProviderid());
        data.put(OrcidOauth2Constants.PROVIDER_USER_ID, entity.getId().getProvideruserid());
        data.put(OrcidOauth2Constants.DISPLAY_NAME, entity.getDisplayname());
        data.put(OrcidOauth2Constants.EMAIL, entity.getEmail());
        data.put(OrcidOauth2Constants.ORCID, entity.getOrcid());
        data.put(OrcidOauth2Constants.IS_LINKED, String.valueOf(entity.isLinked()));
        return data;        
    }

}
