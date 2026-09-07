package org.orcid.persistence.dao;

import java.time.LocalDate;

import org.orcid.persistence.jpa.entities.PublicApiDailyRateLimitEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface PublicApiDailyRateLimitDao extends GenericDao<PublicApiDailyRateLimitEntity, Long> {
    
    @Transactional(readOnly = true)
    PublicApiDailyRateLimitEntity findByClientIdAndRequestDate(String clientId, LocalDate requestDate);
    
    @Transactional(readOnly = true)
    PublicApiDailyRateLimitEntity findByIpAddressAndRequestDate(String ipAddress, LocalDate requestDate);
    
    @Transactional(readOnly = true)
    int countClientRequestsWithLimitExceeded(LocalDate requestDate, int limit);
    
    @Transactional(readOnly = true)
    int countAnonymousRequestsWithLimitExceeded(LocalDate requestDate, int limit);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean updatePublicApiDailyRateLimit(PublicApiDailyRateLimitEntity papiRateLimitingEntity, boolean isClient);
    
    @Transactional(propagation = Propagation.REQUIRED)
    int cleanup(int daysToKeep);

}
