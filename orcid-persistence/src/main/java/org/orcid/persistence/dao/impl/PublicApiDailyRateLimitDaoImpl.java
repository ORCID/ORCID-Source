package org.orcid.persistence.dao.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.orcid.persistence.dao.PublicApiDailyRateLimitDao;
import org.orcid.persistence.jpa.entities.PublicApiDailyRateLimitEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class PublicApiDailyRateLimitDaoImpl extends GenericDaoImpl<PublicApiDailyRateLimitEntity, Long> implements PublicApiDailyRateLimitDao {
    private static final Logger LOG = LoggerFactory.getLogger(PublicApiDailyRateLimitDaoImpl.class);

    public PublicApiDailyRateLimitDaoImpl() {
        super(PublicApiDailyRateLimitEntity.class);
    }

    @Override
    public PublicApiDailyRateLimitEntity findByClientIdAndRequestDate(String clientId, LocalDate requestDate) {
        Query nativeQuery = entityManager.createNativeQuery("SELECT * FROM public_api_daily_rate_limit p where p.client_id=:clientId and p.request_date=:requestDate",
                PublicApiDailyRateLimitEntity.class);
        nativeQuery.setParameter("clientId", clientId);
        nativeQuery.setParameter("requestDate", requestDate);
        List<PublicApiDailyRateLimitEntity> papiRateList = (List<PublicApiDailyRateLimitEntity>) nativeQuery.getResultList();
        if (papiRateList != null && papiRateList.size() > 0) {
            if (papiRateList.size() > 1) {
                LOG.warn("Found more than one entry for the daily papi rate limiting the client: " + clientId + " and request date: " + requestDate.toString());
            }
            return (PublicApiDailyRateLimitEntity) papiRateList.get(0);
        }
        return null;
    }

    @Override
    public PublicApiDailyRateLimitEntity findByIpAddressAndRequestDate(String ipAddress, LocalDate requestDate) {
        String baseQuery = "SELECT * FROM public_api_daily_rate_limit p where p.ip_address=:ipAddress and p.request_date=:requestDate";

        Query nativeQuery = entityManager.createNativeQuery(baseQuery, PublicApiDailyRateLimitEntity.class);
        nativeQuery.setParameter("ipAddress", ipAddress);
        nativeQuery.setParameter("requestDate", requestDate);

        List<PublicApiDailyRateLimitEntity> papiRateList = (List<PublicApiDailyRateLimitEntity>) nativeQuery.getResultList();
        if (papiRateList != null && papiRateList.size() > 0) {
            LOG.debug("found results ....");
            if (papiRateList.size() > 1) {
                LOG.warn("Found more than one entry for the daily papi rate limiting, the IP Address: " + ipAddress + " and request date: " + requestDate.toString());
            }
            return (PublicApiDailyRateLimitEntity) papiRateList.get(0);
        }
        return null;
    }

    public int countClientRequestsWithLimitExceeded(LocalDate requestDate, int limit) {
        Query nativeQuery = entityManager.createNativeQuery(
                "SELECT count(*) FROM public_api_daily_rate_limit p WHERE NOT ((p.client_id = '' OR p.client_id IS NULL)) and p.request_date=:requestDate and p.request_count >=:requestCount");
        nativeQuery.setParameter("requestDate", requestDate);
        nativeQuery.setParameter("requestCount", limit);
        List<java.math.BigInteger>  tsList = nativeQuery.getResultList();
        if (tsList != null && !tsList.isEmpty()) {
            return tsList.get(0).intValue();
        }
        return 0;

    }

    public int countAnonymousRequestsWithLimitExceeded(LocalDate requestDate, int limit) {
        Query nativeQuery = entityManager.createNativeQuery(
                "SELECT count(*) FROM public_api_daily_rate_limit p WHERE ((p.client_id = '' OR p.client_id IS NULL)) and p.request_date=:requestDate and p.request_count >=:requestCount");
        nativeQuery.setParameter("requestDate", requestDate);
        nativeQuery.setParameter("requestCount", limit);
        List<java.math.BigInteger> tsList = nativeQuery.getResultList();
        if (tsList != null && !tsList.isEmpty()) {
            return tsList.get(0).intValue();
        }
        return 0;
    }

    @Override
    @Transactional
    public boolean updatePublicApiDailyRateLimit(PublicApiDailyRateLimitEntity papiRateLimitingEntity, boolean isClient) {
        Query query;
        if (isClient) {
            query = entityManager.createNativeQuery("update public_api_daily_rate_limit set request_count = :requestCount, last_modified = now() where "
                    + "client_id = :clientId and request_date =:requestDate");
            query.setParameter("clientId", papiRateLimitingEntity.getClientId());
        } else {
            query = entityManager.createNativeQuery("update public_api_daily_rate_limit set request_count = :requestCount, last_modified = now() where "
                    + "ip_address = :ipAddress and request_date =:requestDate");
            query.setParameter("ipAddress", papiRateLimitingEntity.getIpAddress());
        }
        query.setParameter("requestCount", papiRateLimitingEntity.getRequestCount());
        query.setParameter("requestDate", papiRateLimitingEntity.getRequestDate());
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public void persist(PublicApiDailyRateLimitEntity papiRateLimitingEntity) {
        String insertQuery = "INSERT INTO public_api_daily_rate_limit " + "(id, client_id, ip_address, request_count, request_date, date_created, last_modified)"
                + " VALUES ( NEXTVAL('papi_daily_limit_seq'), :clientId , :ipAddress, :requestCount," + " :requestDate, now(), now())";

        Query query = entityManager.createNativeQuery(insertQuery);
        query.setParameter("clientId", papiRateLimitingEntity.getClientId());
        query.setParameter("ipAddress", papiRateLimitingEntity.getIpAddress());
        query.setParameter("requestCount", papiRateLimitingEntity.getRequestCount());
        query.setParameter("requestDate", papiRateLimitingEntity.getRequestDate());
        query.executeUpdate();
        return;
    }

    private static String logQueryWithParams(String baseQuery, Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String paramPlaceholder = ":" + entry.getKey();
            String paramValue = (entry.getValue() instanceof String) ? "'" + entry.getValue() + "'" : entry.getValue().toString();
            baseQuery = baseQuery.replace(paramPlaceholder, paramValue);
        }
        return baseQuery;
    }

}
