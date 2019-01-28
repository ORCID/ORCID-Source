package org.orcid.api.common.delegator;

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

    /**
     * Sends a redirect from the client URI to the group URI
     * 
     * @param clientId
     *            the client ID that corresponds to the client
     * @return a redirect to the ORCID record for the client's group
     */
    Response redirectClientToGroup(String clientId);

}
