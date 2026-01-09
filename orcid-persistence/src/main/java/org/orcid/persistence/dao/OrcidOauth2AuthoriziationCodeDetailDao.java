package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;

import java.util.Date;

/**
 * @author Declan Newman (declan) Date: 24/04/2012
 */
public interface OrcidOauth2AuthoriziationCodeDetailDao extends GenericDao<OrcidOauth2AuthoriziationCodeDetail, String> {

    OrcidOauth2AuthoriziationCodeDetail removeAndReturn(String code);

    boolean isPersistentToken(String code);

    boolean removeArchivedAuthorizationCodes(Date maxArchiveDate);
}
