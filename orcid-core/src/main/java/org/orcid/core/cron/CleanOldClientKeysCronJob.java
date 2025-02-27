package org.orcid.core.cron;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ClientSecretDao;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class CleanOldClientKeysCronJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(CleanOldClientKeysCronJob.class);

    @Resource
    private ClientDetailsDao clientDetailsDaoReadOnly;

    @Resource
    private ClientSecretDao clientSecretDao;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    /**
     * Removes all non primary client secret keys
     *
     */
    @Transactional
    public void cleanOldClientKeys() {
        // fetch nonprimary keys
        List<ClientSecretEntity> nonPrimaryKeys = clientSecretDao.getNonPrimaryKeys(100);
        if (nonPrimaryKeys.size() > 0) {
            int i = 0;
            StringBuilder queryCondition = new StringBuilder();
            List<String> clientIds = new ArrayList<String>();
            for (ClientSecretEntity e : nonPrimaryKeys) {
                i++;
                String clientId = e.getId().getClientId();
                String key = e.getId().getClientSecret();
                // build string for the condition in the db delete query
                String s = String.format("(client_details_id = '%1$s' and client_secret = '%2$s')", clientId, key);
                queryCondition.append(s);
                clientIds.add(clientId);
                if (i != nonPrimaryKeys.size())
                    queryCondition.append(" or ");
            }

            String queryConditionString = queryCondition.toString();
            LOGGER.warn("The following keys are going to be deleted:\n"
                    + queryConditionString.replace(" =", ":").replace(" and", ",").replace(" or ", "\n").replace("'", ""));
            // remove fetched keys
            boolean removeNonPrimaryKeys = clientSecretDao.removeWithCustomCondition(queryConditionString);
            if (removeNonPrimaryKeys) {
                LOGGER.info("Done removing old keys");
                // update last_modified if successful
                this.clientDetailsDao.updateLastModifiedBulk(clientIds);
            }
        }
        LOGGER.info("cleanOldClientKeys() done");
    }
}
