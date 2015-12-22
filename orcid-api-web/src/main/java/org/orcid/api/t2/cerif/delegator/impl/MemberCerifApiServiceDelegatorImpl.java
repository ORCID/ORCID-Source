/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.t2.cerif.delegator.impl;

import javax.ws.rs.core.Response;

import org.orcid.api.common.delegator.impl.CerifApiServiceDelegatorImpl;
import org.orcid.api.t2.cerif.delegator.MemberCerifApiServiceDelgator;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Visibility;
import org.springframework.transaction.annotation.Transactional;


/** TODO:
 * Members can read public (using two leg || three leg) or read limited using three leg
 * Need to return appropriately.
 * 
 * @author tom
 *
 */
public class MemberCerifApiServiceDelegatorImpl extends CerifApiServiceDelegatorImpl implements MemberCerifApiServiceDelgator {

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = false)
    @Transactional(readOnly=true)
    public Response getPerson(String id) {
        return super.getPerson(id);
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = false)
    public Response getPublication(String orcid, Long id) {
        return super.getPublication(orcid,id);
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = false)
    public Response getProduct(String orcid, Long id) {
        return super.getProduct(orcid,id);
    }

}
