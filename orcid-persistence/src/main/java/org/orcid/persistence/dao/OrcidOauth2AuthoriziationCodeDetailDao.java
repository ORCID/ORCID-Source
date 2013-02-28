/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 24/04/2012
 */
public interface OrcidOauth2AuthoriziationCodeDetailDao extends GenericDao<OrcidOauth2AuthoriziationCodeDetail, String> {

    OrcidOauth2AuthoriziationCodeDetail removeAndReturn(String code);

}
