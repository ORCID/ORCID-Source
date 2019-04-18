package org.orcid.core.manager.v3;

import java.util.ArrayList;

import org.orcid.core.manager.v3.read_only.AffiliationsManagerReadOnly;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.Service;

public interface AffiliationsManager extends AffiliationsManagerReadOnly {
    /**
     * Add a new education to the given user
     * 
     * @param orcid
     *            The user to add the education
     * @param education
     *            The education to add
     * @return the added education
     */
    Education createEducationAffiliation(String orcid, Education education, boolean isApiRequest);

    /**
     * Updates a education that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param education
     *            The education to update
     * @return the updated education
     */
    Education updateEducationAffiliation(String orcid, Education education, boolean isApiRequest);

    /**
     * Add a new employment to the given user
     * 
     * @param orcid
     *            The user to add the employment
     * @param employment
     *            The employment to add
     * @return the added employment
     */
    Employment createEmploymentAffiliation(String orcid, Employment employment, boolean isApiRequest);

    /**
     * Updates a employment that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param employment
     *            The employment to update
     * @return the updated employment
     */
    Employment updateEmploymentAffiliation(String orcid, Employment employment, boolean isApiRequest);

    /**
     * Add a new distinction to the given user
     * 
     * @param orcid
     *            The user to add the distinction
     * @param distinction
     *            The distinction to add
     * @return the added distinction
     */
    Distinction createDistinctionAffiliation(String orcid, Distinction distinction, boolean isApiRequest);

    /**
     * Updates a distinction that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param distinction
     *            The distinction to update
     * @return the updated distinction
     */
    Distinction updateDistinctionAffiliation(String orcid, Distinction distinction, boolean isApiRequest);

    /**
     * Add a new invitedPosition to the given user
     * 
     * @param orcid
     *            The user to add the invitedPosition
     * @param invitedPosition
     *            The invitedPosition to add
     * @return the added invitedPosition
     */
    InvitedPosition createInvitedPositionAffiliation(String orcid, InvitedPosition invitedPosition, boolean isApiRequest);

    /**
     * Updates a invitedPosition that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param invitedPosition
     *            The invitedPosition to update
     * @return the updated invitedPosition
     */
    InvitedPosition updateInvitedPositionAffiliation(String orcid, InvitedPosition invitedPosition, boolean isApiRequest);

    /**
     * Add a new membership to the given user
     * 
     * @param orcid
     *            The user to add the membership
     * @param membership
     *            The membership to add
     * @return the added membership
     */
    Membership createMembershipAffiliation(String orcid, Membership membership, boolean isApiRequest);

    /**
     * Updates a membership that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param membership
     *            The membership to update
     * @return the updated membership
     */
    Membership updateMembershipAffiliation(String orcid, Membership membership, boolean isApiRequest);

    /**
     * Add a new qualification to the given user
     * 
     * @param orcid
     *            The user to add the qualification
     * @param qualification
     *            The qualification to add
     * @return the added membership
     */
    Qualification createQualificationAffiliation(String orcid, Qualification qualification, boolean isApiRequest);

    /**
     * Updates a qualification that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param qualification
     *            The qualification to update
     * @return the updated qualification
     */
    Qualification updateQualificationAffiliation(String orcid, Qualification qualification, boolean isApiRequest);

    /**
     * Add a new service to the given user
     * 
     * @param orcid
     *            The user to add the service
     * @param service
     *            The service to add
     * @return the added service
     */
    Service createServiceAffiliation(String orcid, Service service, boolean isApiRequest);

    /**
     * Updates a service that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param service
     *            The service to update
     * @return the updated service
     */
    Service updateServiceAffiliation(String orcid, Service service, boolean isApiRequest);

    /**
     * Deletes a given affiliation, if and only if, the client that requested
     * the delete is the source of the affiliation
     * 
     * @param orcid
     *            the affiliation owner
     * @param affiliationId
     *            The affiliation id
     * @return true if the affiliation was deleted, false otherwise
     */
    boolean checkSourceAndDelete(String orcid, Long affiliationId);
    
    /**
     * Updates visibility of a single affiliation, if and only if, the client that requested
     * the delete is the source of the affiliation
     * 
     * @param orcid
     *            the affiliation owner
     * @param affiliationId
     *            The affiliation id
     * @return true if the affiliation visibility was updated, false otherwise
     */
    boolean updateVisibility(String orcid, Long affiliationId, Visibility visibility);
    
    /**
     * Updates visibility of multiple affiliations, if and only if, the client that requested
     * the delete is the source of the affiliation
     * 
     * @param orcid
     *            the affiliation owner
     * @param affiliationIds
     *            List of affiliation ids to update
     * @return true if the visibility of each affiliation in the list was updated, false otherwise
     */
    boolean updateVisibilities(String orcid, ArrayList<Long> affiliationIds, Visibility visibility);

    /**
     * Deletes an affiliation.
     * 
     * It doesn't check the source of the element before delete it, so, it is
     * intended to be used only by the user from the UI
     * 
     * @param userOrcid
     *            The client orcid
     *
     * @param affiliationId
     *            The affiliation id in the DB
     * @return true if the relationship was deleted
     */
    boolean removeAffiliation(String userOrcid, Long affiliationId);

    /**
     * Removes all affiliations that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all affiliations will be
     *            removed.
     */
    void removeAllAffiliations(String orcid);
    
    /**
     * Updates the display index of a given affiliation
     * 
     * @param orcid
     *            The affiliation owner
     * @param putCode
     *            The affiliation id
     * @return true if it was able to update the display index
     * */
    Boolean updateToMaxDisplay(String orcid, Long putCode);
}
