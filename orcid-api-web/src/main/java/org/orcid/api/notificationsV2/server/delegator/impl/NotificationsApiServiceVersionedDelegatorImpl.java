package org.orcid.api.notificationsV2.server.delegator.impl;

import static org.orcid.core.api.OrcidApiConstants.STATUS_OK_MESSAGE;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.orcid.api.memberV2.server.delegator.MemberV2ApiServiceDelegator;
import org.orcid.api.notificationsV2.server.delegator.NotificationsApiServiceDelegator;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverterChain;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Will Simpson
 *
 */
@Component
public class NotificationsApiServiceVersionedDelegatorImpl implements NotificationsApiServiceDelegator<Object> {

    @Resource
    private NotificationsApiServiceDelegator<Object> notificationsApiServiceDelegator;
    

    private String externalVersion;

    @Resource
    private V2VersionConverterChain v2VersionConverterChain;
    
    @Resource
    private V2VersionConverterChain v2_1VersionConverterChain;

    public String getExternalVersion() {
        return externalVersion;
    }

    public void setExternalVersion(String externalVersion) {
        this.externalVersion = externalVersion;
    }
    
    @Override
    public Response viewStatusText() {
        return Response.ok(STATUS_OK_MESSAGE).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response findPermissionNotifications(String orcid) {        
        Response response = notificationsApiServiceDelegator.findPermissionNotifications(orcid);
        if(externalVersion.equals("2.1")) {
            return upgradeResponse(response);
        } else {
            return downgradeResponse(response);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response findPermissionNotification(String orcid, Long id) {
        Response response = downgradeResponse(notificationsApiServiceDelegator.findPermissionNotification(orcid, id));
        if(externalVersion.equals("2.1")) {
            return upgradeResponse(response);
        } else {
            return downgradeResponse(response);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response flagNotificationAsArchived(String orcid, Long id) throws OrcidNotificationAlreadyReadException {
        return downgradeResponse(notificationsApiServiceDelegator.flagNotificationAsArchived(orcid, id));
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response addPermissionNotification(UriInfo uriInfo, String orcid, Object notification) {
        return notificationsApiServiceDelegator.addPermissionNotification(uriInfo, orcid, upgradeObject(notification));
    }

    private Response downgradeResponse(Response response) {
        Object entity = response.getEntity();
        V2Convertible result = null;
        if (entity != null) {
            result = v2VersionConverterChain.downgrade(new V2Convertible(entity, MemberV2ApiServiceDelegator.LATEST_V2_VERSION), externalVersion);
        }
        return Response.fromResponse(response).entity(result.getObjectToConvert()).build();
    }
    
    private Response upgradeResponse(Response response) {
        Object entity = response.getEntity();
        V2Convertible result = null;
        if (entity != null) {
            result = v2_1VersionConverterChain.upgrade(new V2Convertible(entity, MemberV2ApiServiceDelegator.LATEST_V2_VERSION), externalVersion);
            return Response.fromResponse(response).entity(result.getObjectToConvert()).build();
        }
        return response;
    }

    private Object upgradeObject(Object entity) {
        V2Convertible result = null;
        if (entity != null) {
            result = v2VersionConverterChain.upgrade(new V2Convertible(entity, externalVersion), MemberV2ApiServiceDelegator.LATEST_V2_VERSION);
        }
        return result.getObjectToConvert();
    }
    
}
