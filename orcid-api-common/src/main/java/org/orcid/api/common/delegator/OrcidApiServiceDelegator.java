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
package org.orcid.api.common.delegator;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.orcid.persistence.dao.SolrDao;

/**
 * 2011-2012 ORCID
 * <p/>
 * Interface that outlines the interactions between the API and core. However,
 * this is not interested in the actual content-type of the returned objects
 * <p/>
 * <p/>
 * It may be the case that this will be extended for use with the tier 2's needs
 * <p/>
 * 
 * @author Declan Newman (declan) Date: 02/03/2012
 */
public interface OrcidApiServiceDelegator {

    /**
     * @return Plain text message indicating health of service
     */
    Response viewStatusText();

    /**
     * finds and returns the {@link org.orcid.jaxb.model.message.OrcidMessage}
     * wrapped in a {@link Response} with only the profile's bio details
     * 
     * @param orcid
     *            the ORCID to be used to identify the record
     * @return the {@link Response} with the
     *         {@link org.orcid.jaxb.model.message.OrcidMessage} within it
     */
    Response findBioDetails(String orcid);

    Response findBioDetailsFromPublicCache(String orcid);

    /**
     * finds and returns the {@link org.orcid.jaxb.model.message.OrcidMessage}
     * wrapped in a {@link Response} with only the profile's external identifier
     * details
     * 
     * @param orcid
     *            the ORCID to be used to identify the record
     * @return the {@link Response} with the
     *         {@link org.orcid.jaxb.model.message.OrcidMessage} within it
     */
    Response findExternalIdentifiers(String orcid);

    Response findExternalIdentifiersFromPublicCache(String orcid);

    /**
     * finds and returns the {@link org.orcid.jaxb.model.message.OrcidMessage}
     * wrapped in a {@link Response} with all of the profile's details
     * 
     * @param orcid
     *            the ORCID to be used to identify the record
     * @return the {@link Response} with the
     *         {@link org.orcid.jaxb.model.message.OrcidMessage} within it
     */
    Response findFullDetails(String orcid);

    Response findFullDetailsFromPublicCache(String orcid);

    /**
     * finds and returns the {@link org.orcid.jaxb.model.message.OrcidMessage}
     * wrapped in a {@link Response} with only the affiliation details
     * 
     * @param orcid
     *            the ORCID to be used to identify the record
     * @return the {@link Response} with the
     *         {@link org.orcid.jaxb.model.message.OrcidMessage} within it
     */
    Response findAffiliationsDetails(String orcid);

    Response findAffiliationsDetailsFromPublicCache(String orcid);

    
    /**
     * finds and returns the {@link org.orcid.jaxb.model.message.OrcidMessage}
     * wrapped in a {@link Response} with only the grants details
     * 
     * @param orcid
     *            the ORCID to be used to identify the record
     * @return the {@link Response} with the
     *         {@link org.orcid.jaxb.model.message.OrcidMessage} within it
     */
    Response findGrantsDetails(String orcid);

    Response findGrantsDetailsFromPublicCache(String orcid);
    
    /**
     * finds and returns the {@link org.orcid.jaxb.model.message.OrcidMessage}
     * wrapped in a {@link Response} with only the work details
     * 
     * @param orcid
     *            the ORCID to be used to identify the record
     * @return the {@link Response} with the
     *         {@link org.orcid.jaxb.model.message.OrcidMessage} within it
     */
    Response findWorksDetails(String orcid);

    Response findWorksDetailsFromPublicCache(String orcid);

    /**
     * finds and returns the
     * {@link org.orcid.jaxb.model.message.OrcidSearchResult} wrapped in a
     * {@link Response} with only bio details returned.
     * 
     * @param queryMap
     *            any set of query params accepted by SOLR. *NB* currently the
     *            SOLRDAO only applies the first value for each key in the map
     *            when building a query for SOLR.
     * @See {@link SolrDao}
     * 
     * @return the {@link Response} with the
     *         {@link org.orcid.jaxb.model.message.OrcidSearchResult} within it.
     */
    Response searchByQuery(Map<String, List<String>> queryMap);

}
