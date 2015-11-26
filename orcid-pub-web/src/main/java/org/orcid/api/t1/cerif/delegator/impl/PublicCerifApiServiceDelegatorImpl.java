package org.orcid.api.t1.cerif.delegator.impl;

import javax.ws.rs.core.Response;

import org.orcid.api.common.delegator.impl.CerifApiServiceDelegatorImpl;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.transaction.annotation.Transactional;

public class PublicCerifApiServiceDelegatorImpl extends CerifApiServiceDelegatorImpl{

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    @Transactional
    public Response getPerson(String id) {
        return super.getPerson(id);
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response getPublication(String orcid, Long id) {
        return super.getPublication(orcid, id);
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response getProduct(String orcid, Long id) {
        return super.getProduct(orcid,id);
    }
}
