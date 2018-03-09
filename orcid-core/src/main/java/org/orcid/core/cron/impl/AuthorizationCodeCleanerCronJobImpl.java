package org.orcid.core.cron.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.cron.AuthorizationCodeCleanerCronJob;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class AuthorizationCodeCleanerCronJobImpl implements AuthorizationCodeCleanerCronJob {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationCodeCleanerCronJobImpl.class);
    @Resource(name = "orcidOauth2AuthoriziationCodeDetailDao")
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;
    @Value("${org.orcid.core.oauth.auth_code.expiration_minutes:1440}")
    private int authorizationCodeExpiration;

    @Transactional
    public void cleanExpiredAuthorizationCodes() {
        List<OrcidOauth2AuthoriziationCodeDetail> allAuthorizationCodes = orcidOauth2AuthoriziationCodeDetailDao.getAll();
        Date now = new Date();
        for(OrcidOauth2AuthoriziationCodeDetail authorizationCode : allAuthorizationCodes) {
            Date creationDate = authorizationCode.getDateCreated();
            Calendar c = Calendar.getInstance();
            c.setTime(creationDate);
            c.add(Calendar.MINUTE, authorizationCodeExpiration);
            Date expirationDate = c.getTime();
            if(expirationDate.before(now)) {
                LOG.info("Authorization code is expired and will be deleted: " + authorizationCode.getId());
                orcidOauth2AuthoriziationCodeDetailDao.remove(authorizationCode.getId());
            }
        }
    }
}
