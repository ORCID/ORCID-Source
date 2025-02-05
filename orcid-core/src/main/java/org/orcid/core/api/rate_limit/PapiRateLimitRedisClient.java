package org.orcid.core.api.rate_limit;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.persistence.dao.PublicApiDailyRateLimitDao;
import org.orcid.persistence.jpa.entities.PublicApiDailyRateLimitEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Component
public class PapiRateLimitRedisClient {
    private static final Logger LOG = LoggerFactory.getLogger(PapiRateLimitRedisClient.class);

    @Resource(name = "redisClientPapi")
    private RedisClient redisClient;

    @Value("${org.orcid.papi.rate.limit.redisCacheExpiryInSec:172800}")
    private int CASH_EXPIRY_IN_SECONDS; // caching for 2 days to have time to
                                        // synch with DB

    @Autowired
    private PublicApiDailyRateLimitDao papiRateLimitingDao;

    public static final String KEY_REQUEST_COUNT = "reqCount";
    public static final String KEY_REQUEST_DATE = "reqDate";
    public static final String KEY_LAST_MODIFIED = "lastModified";
    public static final String KEY_DATE_CREATED = "dateCreated";
    public static final String KEY_IS_ANONYMOUS = "isAnonymous";
    public static final String KEY_REQUEST_CLIENT = "reqClient";

    public static final String KEY_DELIMITATOR = "||";

    public String getTodayKeyByClient(String client) {
        return getRequestDateKeyByClient(client, LocalDate.now());
    }

    public String getRequestDateKeyByClient(String client, LocalDate requestDate) {
        return client + KEY_DELIMITATOR + requestDate.toString()  ;
    }

    public JSONObject getDailyLimitsForClient(String client, LocalDate requestDate) {

        String metaData = redisClient.get(getRequestDateKeyByClient(client, requestDate));
        try {
            return StringUtils.isNotBlank(metaData) ? new JSONObject(metaData) : null;
        } catch (Exception ex) {
            return null;
        }
    }

    public JSONObject getTodayDailyLimitsForClient(String client) {
        return getDailyLimitsForClient(client, LocalDate.now());
    }

    public void setTodayLimitsForClient(String client, JSONObject metaData) {
        String limitKey = getTodayKeyByClient(client);
        redisClient.set(limitKey, metaData.toString(), CASH_EXPIRY_IN_SECONDS);
    }

    public void saveRedisPapiLimitDateToDB(LocalDate requestDate) throws JSONException {
        // returns all the keys for requestDate
        HashMap<String, JSONObject> allValuesForKey = redisClient.getAllValuesForKeyPattern("*" + requestDate.toString());
        for (String key : allValuesForKey.keySet()) {
            PublicApiDailyRateLimitEntity redisRateLimitEntity = redisObjJsonToEntity(allValuesForKey.get(key));
            PublicApiDailyRateLimitEntity pgRateLimitEntity = null;
            boolean isClient = false;
            if(StringUtils.isNotEmpty(redisRateLimitEntity.getIpAddress())) {
                pgRateLimitEntity  = papiRateLimitingDao.findByIpAddressAndRequestDate(redisRateLimitEntity.getIpAddress(), requestDate);
            }
            else if(StringUtils.isNotEmpty(redisRateLimitEntity.getClientId())){
                pgRateLimitEntity  = papiRateLimitingDao.findByClientIdAndRequestDate(redisRateLimitEntity.getClientId(), requestDate);
                isClient = true;
            }
            if(pgRateLimitEntity != null) {
                papiRateLimitingDao.updatePublicApiDailyRateLimit(pgRateLimitEntity, isClient);
            } else {
                papiRateLimitingDao.persist(redisObjJsonToEntity(allValuesForKey.get(key)));
            }
            redisClient.remove(key);
        }
    }

    private PublicApiDailyRateLimitEntity redisObjJsonToEntity(JSONObject redisObj) throws JSONException {
        PublicApiDailyRateLimitEntity rateLimitEntity = new PublicApiDailyRateLimitEntity();
        if (!redisObj.getBoolean(KEY_IS_ANONYMOUS)) {
            rateLimitEntity.setClientId(redisObj.getString(KEY_REQUEST_CLIENT));
        } else {
            rateLimitEntity.setIpAddress(redisObj.getString(KEY_REQUEST_CLIENT));
        }
        rateLimitEntity.setRequestCount(redisObj.getLong(KEY_REQUEST_COUNT));
        rateLimitEntity.setRequestDate(LocalDate.parse(redisObj.getString(KEY_REQUEST_DATE)));
        rateLimitEntity.setDateCreated(new Date(redisObj.getInt(KEY_DATE_CREATED)));
        rateLimitEntity.setLastModified(new Date(redisObj.getInt(KEY_LAST_MODIFIED)));
        papiRateLimitingDao.persist(rateLimitEntity);
        return rateLimitEntity;
    }

}
