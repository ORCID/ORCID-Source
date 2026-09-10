package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Declan Newman (declan) Date: 24/04/2012
 */
public interface OrcidOauth2AuthoriziationCodeDetailDao extends GenericDao<OrcidOauth2AuthoriziationCodeDetail, String> {

    @Transactional(propagation = Propagation.REQUIRED)
    OrcidOauth2AuthoriziationCodeDetail removeAndReturn(String code);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean isPersistentToken(String code);

    boolean removeArchivedAuthorizationCodes(Date maxArchiveDate);
}
