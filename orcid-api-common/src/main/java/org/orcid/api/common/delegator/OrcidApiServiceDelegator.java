package org.orcid.api.common.delegator;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

/**
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
     * wrapped in a {@link Response} with only the funding details
     * 
     * @param orcid
     *            the ORCID to be used to identify the record
     * @return the {@link Response} with the
     *         {@link org.orcid.jaxb.model.message.OrcidMessage} within it
     */
    Response findFundingDetails(String orcid);

    Response findFundingDetailsFromPublicCache(String orcid);
    
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
     * Sends a redirect from the client URI to the group URI
     * 
     * @param clientId
     *            the client ID that corresponds to the client
     * @return a redirect to the ORCID record for the client's group
     */
    Response redirectClientToGroup(String clientId);

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

    /**
     * NOTE:
     * Helper method used during the transition of public searches with and without bearer authentication 
     * When we finish the transition and all public searches require the bearer, we must remove this one 
     * 
     * 
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
     * */
    Response publicSearchByQuery(Map<String, List<String>> queryMap);
}
