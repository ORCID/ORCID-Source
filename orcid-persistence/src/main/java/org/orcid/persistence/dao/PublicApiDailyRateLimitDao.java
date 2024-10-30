package org.orcid.persistence.dao;

import java.time.LocalDate;

import org.orcid.persistence.jpa.entities.PublicApiDailyRateLimitEntity;

public interface PublicApiDailyRateLimitDao extends GenericDao<PublicApiDailyRateLimitEntity, Long> {
    
    PublicApiDailyRateLimitEntity findByClientIdAndRequestDate(String clientId, LocalDate requestDate);
    PublicApiDailyRateLimitEntity findByIpAddressAndRequestDate(String ipAddress, LocalDate requestDate);
    int countClientRequestsWithLimitExceeded(LocalDate requestDate, int limit);
    int countAnonymousRequestsWithLimitExceeded(LocalDate requestDate, int limit);
    boolean updatePublicApiDailyRateLimit(PublicApiDailyRateLimitEntity papiRateLimitingEntity, boolean isClient);

}
