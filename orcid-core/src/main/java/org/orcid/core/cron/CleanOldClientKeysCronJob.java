package org.orcid.core.cron;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.orcid.core.manager.impl.ClientDetailsManagerImpl;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ClientSecretDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CleanOldClientKeysCronJob {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDetailsManagerImpl.class);
    
    @Resource
    private ClientDetailsDao clientDetailsDaoReadOnly;
    
    @Resource
    private ClientSecretDao clientSecretDao;
    
    @Resource
    private ClientDetailsDao clientDetailsDao;
        
    /**
     * Removes all non primary client secret keys
     * 
     * @param clientId
     * */
    @Transactional
    public void cleanOldClientKeys() {
        List<ClientSecretEntity> nonPrimaryKeys = clientSecretDao.getNonPrimaryKeys();
        if (nonPrimaryKeys.size() > 0) {
            int i = 0;
            StringBuilder queryCondition = new StringBuilder();
            List<String> clientIds = new ArrayList<String>();
            for (ClientSecretEntity e : nonPrimaryKeys) {
                i++;
                ClientDetailsEntity clientDetails = e.getClientDetailsEntity();
                String clientId = clientDetails.getId();
                String key = e.getClientSecret();
                String s = String.format("client_details_id = '%1$s' and client_secret = '%2$s'", clientId, key);
                queryCondition.append(s);
                clientIds.add(clientId);
                if (i != nonPrimaryKeys.size()) queryCondition.append(" or ");
            };
            String queryConditionString = queryCondition.toString();
            LOGGER.info("The following keys are going to be deleted:\n" + queryConditionString.replace(" =", ":").replace(" and", ",").replace(" or", "\n").replace("'",""));
            boolean removeNonPrimaryKeys = clientSecretDao.removeWithCustomCondition(queryConditionString);
            if (removeNonPrimaryKeys) {
                LOGGER.info("Done removing old keys");
                this.clientDetailsDao.updateLastModifiedBulk(clientIds);
            }
        }
        LOGGER.info("Cron done");
       /* LOGGER.info("Starting cron to delete non primary client keys");
        Date currentDate = new Date();
        List<ClientDetailsEntity> allClientDetails = this.clientDetailsDaoReadOnly.getAll();
        if (allClientDetails != null && allClientDetails != null) {
            for (ClientDetailsEntity clientDetails : allClientDetails) {
                String clientId = clientDetails.getClientId();
                LOGGER.info("Deleting non primary keys for client: {}", clientId);
                Set<ClientSecretEntity> clientSecrets = clientDetails.getClientSecrets();
                boolean anyRemoved = false;
                for (ClientSecretEntity clientSecret : clientSecrets) {
                    if (!clientSecret.isPrimary()) {
                        Date dateRevoked = clientSecret.getLastModified();
                        Date timeToDeleteMe = DateUtils.addHours(dateRevoked, 24);
                        // If the key have been revoked more than 24 hours ago
                        if (timeToDeleteMe.before(currentDate)) {
                            LOGGER.info("Deleting key for client {}", clientId);
                            boolean removed = clientSecretDao.removeClientSecret(clientId, clientSecret.getClientSecret());
                            if(removed) {
                                anyRemoved = true;
                            }
                        }
                    }
                }
                // Update the last modified on the client record
                if(anyRemoved) {
                    this.clientDetailsDao.updateLastModified(clientId);
                }
            }
        }*/        
    }
}
