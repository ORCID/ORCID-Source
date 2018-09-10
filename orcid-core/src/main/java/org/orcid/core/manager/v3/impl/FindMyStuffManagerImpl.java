package org.orcid.core.manager.v3.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.orcid.core.adapter.v3.JpaJaxbNotificationAdapter;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.FindMyStuffManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.utils.v3.identifiers.finders.Finder;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.v3.rc1.notification.Notification;
import org.orcid.jaxb.model.v3.rc1.notification.NotificationType;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.model.v3.rc1.notification.internal.NotificationFindMyStuff;
import org.orcid.persistence.dao.FindMyStuffHistoryDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.FindMyStuffHistoryEntity;
import org.orcid.persistence.jpa.entities.NotificationFindMyStuffEntity;
import org.orcid.pojo.FindMyStuffResult;

import com.google.common.collect.Maps;

public class FindMyStuffManagerImpl implements FindMyStuffManager {

    private static final String AUTHORIZATION_END_POINT = "{0}/oauth/authorize?response_type=code&client_id={1}&scope={2}&redirect_uri={3}";

    @Resource(name = "workManagerReadOnlyV3")
    WorkManagerReadOnly workManagerReadOnly;

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;

    @Resource(name = "jpaJaxbNotificationAdapterV3")
    private JpaJaxbNotificationAdapter notificationAdapter;

    @Resource
    private FindMyStuffHistoryDao findMyStuffHistoryDao;

    @Resource
    List<Finder> finders = new ArrayList<Finder>();

    /**
     * Invokes all finders for a given ORCID
     * 
     * @param orcid
     * @return a map of serviceName->result for all finders that found something
     */
    @Override
    public Map<String, FindMyStuffResult> find(String orcid) {
        Map<String, FindMyStuffResult> result = new HashMap<String, FindMyStuffResult>();
        ExternalIDs existingIDs = workManagerReadOnly.getAllExternalIDs(orcid);
        for (Finder f : finders) {
            if (f.isEnabled())
                result.put(f.getFinderName(), f.find(orcid, existingIDs));
        }
        return result;
    }

    /**
     * For each service, invoke finder if:
     * 
     * 1. user does not have existing permissions with SP 2. user has not opted
     * out of find my stuff
     * 
     * For each result, create a notification (if there are no find my stuff
     * notifications in the first X (50)) and return the details.
     * 
     * @param orcid
     * @return a map of serviceName->result for all finders that found something
     */
    @Override
    public List<FindMyStuffResult> findIfAppropriate(String orcid) {
        // get history and check for optOuts
        List<FindMyStuffHistoryEntity> history = getHistory(orcid);
        Set<String> skipServices = Sets.newHashSet();
        Map<String, FindMyStuffHistoryEntity> existingHistories = Maps.newHashMap();
        if (history != null){
            for (FindMyStuffHistoryEntity h : history) {
                existingHistories.put(h.getFinderName(), h);
                // opted out
                if (h.getOptOut()) {
                    skipServices.add(h.getFinderName());
                }
                // note we don't check for actioned here - instead we look at
                // permissions below (action may have been initiated but not
                // followed through)
            }            
        }
        // check for existing permissions
        for (Finder f : finders) {
            if (f.isEnabled() && orcidOauth2TokenDetailService.doesClientKnowUser(f.getRelatedClientId(), orcid))
                skipServices.add(f.getFinderName());
        }

        // find for non-skip services
        List<FindMyStuffResult> result = Lists.newArrayList();
        ExternalIDs existingIDs = workManagerReadOnly.getAllExternalIDs(orcid);
        for (Finder f : finders) {
            if (f.isEnabled() && !skipServices.contains(f.getFinderName())) {
                FindMyStuffResult r = f.find(orcid, existingIDs);
                // if found, update history, create notification, return details
                // & notification
                if (!r.getResults().isEmpty()) {
                    r.setNotification(getOrCreateNotification(orcid, f));
                    result.add(r);
                    if (existingHistories.containsKey(r.getFinderName())) {
                        // update history
                        existingHistories.get(r.getFinderName()).setLastCount(r.getResults().size());
                        findMyStuffHistoryDao.merge(existingHistories.get(r.getFinderName()));
                    } else {
                        // create history
                        FindMyStuffHistoryEntity e = new FindMyStuffHistoryEntity();
                        e.setFinderName(r.getFinderName());
                        e.setOptOut(false);
                        e.setLastCount(r.getResults().size());
                        e.setOrcid(orcid);
                        findMyStuffHistoryDao.persist(e);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Retrieve existing notification if it's in the top 50, otherwise create a
     * new one.
     * 
     * @param orcid
     * @param f
     * @return
     */
    private NotificationFindMyStuff getOrCreateNotification(String orcid, Finder finder) {
        // check for existing notifications (last 50)
        List<Notification> notifications = notificationManager.findByOrcid(orcid, false, 0, 50);
        for (Notification n : notifications)
            if (NotificationType.FIND_MY_STUFF.equals(n.getNotificationType()) && n instanceof NotificationFindMyStuff) {
                NotificationFindMyStuff nfms = (NotificationFindMyStuff) n;
                for (Finder f : finders) {
                    if (nfms.getSource().retrieveSourcePath().equals(f.getRelatedClientId()))
                        return nfms;
                }
            }
        NotificationFindMyStuffEntity entity = notificationManager.createFindMyStuffNotification(orcid, finder.getFinderName(),
                buildAuthorizationUrl(finder.getRelatedClientId()));
        return (NotificationFindMyStuff) notificationAdapter.toNotification(entity);
    }

    /**
     * Creates the actionable URL for the client using current client config.
     * 
     */
    @Override
    public String buildAuthorizationUrl(String clientId) {
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);
        ClientRedirectUriEntity result = null;
        // found, return the first DEFAULT one
        for (ClientRedirectUriEntity redirectUri : clientDetails.getClientRegisteredRedirectUris()) {
            if (RedirectUriType.IMPORT_WORKS_WIZARD.value().equals(redirectUri.getRedirectUriType())) {
                result = redirectUri;
                break;
            }
        }
        try {
            String urlEncodedScopes = URLEncoder.encode(result.getPredefinedClientScope(), "UTF-8");
            String urlEncodedRedirectUri = URLEncoder.encode(result.getRedirectUri(), "UTF-8");
            return MessageFormat.format(AUTHORIZATION_END_POINT, orcidUrlManager.getBaseUrl(), clientDetails.getClientId(), urlEncodedScopes, urlEncodedRedirectUri);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks to see if user has opted out of find my stuff, when the find was
     * run, and other historical details.
     * 
     * @param orcid
     * @return one entry per SP (if present)
     */
    @Override
    public List<FindMyStuffHistoryEntity> getHistory(String orcid) {
        return findMyStuffHistoryDao.findAll(orcid);
    }

    @Override
    public void markAsActioned(String orcid, String finderName) {
        findMyStuffHistoryDao.markActioned(orcid, finderName);
    }

}