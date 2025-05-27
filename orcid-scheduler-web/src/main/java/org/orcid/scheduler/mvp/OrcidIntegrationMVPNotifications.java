package org.orcid.scheduler.mvp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ProfileEmailDomainDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class OrcidIntegrationMVPNotifications {

    private static final Logger LOG = LoggerFactory.getLogger(OrcidIntegrationMVPNotifications.class);

    @Autowired
    private ClientDetailsDao clientDetailsDaoReadOnly;

    @Autowired
    private ProfileEmailDomainDao profileEmailDomainDaoReadOnly;

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    public static final String ORCID_INTEGRATION_NOTIFICATION_FAMILY = "ORCID_INTEGRATION";

    public void createOrcidIntegrationNotifications() {
        // Get clients eligible for mvp
        List<ClientDetailsEntity> clientsWithMVP = clientDetailsDaoReadOnly.findMVPEnabled();
        if (clientsWithMVP != null && !clientsWithMVP.isEmpty()) {
            for (ClientDetailsEntity clientDetails : clientsWithMVP) {
                if (StringUtils.isNotBlank(clientDetails.getNotificationWebpageUrl()) && StringUtils.isNotBlank(clientDetails.getNotificationDomains())) {
                    try {
                        JSONArray jsonDomainArr = new JSONArray(clientDetails.getNotificationDomains());
                        Set<ProfileEmailDomainEntity> profileDomainSet = new HashSet<ProfileEmailDomainEntity>();
                        for (int i = 0; i < jsonDomainArr.length(); i++) {
                            String domain = jsonDomainArr.getString(i);
                            List<ProfileEmailDomainEntity> emailDomainList = profileEmailDomainDaoReadOnly.findByEmailDomain(domain);
                            if (emailDomainList != null && emailDomainList.size() > 0) {
                                profileDomainSet.addAll(emailDomainList);
                            }
                        }
                        if (profileDomainSet.size() > 0) {
                            for (ProfileEmailDomainEntity pe : profileDomainSet) {
                                List<NotificationEntity> orcidIntegrationNotifications = notificationManager.findByOrcidAndClientAndNotificationFamilyNoClientToken(
                                        pe.getOrcid(), clientDetails.getClientId(), ORCID_INTEGRATION_NOTIFICATION_FAMILY);
                                if (orcidIntegrationNotifications == null || orcidIntegrationNotifications.isEmpty()) {
                                    notificationManager.sendOrcidIntegrationNotificationToUser(pe.getOrcid(), clientDetails);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        LOG.error("Cannot set the mvp notifications for client with the id: " + clientDetails.getClientId(), ex);
                    }

                } else {
                    LOG.warn("Check the mvp notifications fields for client with the id: " + clientDetails.getClientId()
                            + " at least one of required notification field is empty, therefore the notification cannot be sent.");
                }
            }
        }
    }

}
