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
package org.orcid.api.memberV2.server.delegator;

import javax.ws.rs.core.Response;

import org.orcid.jaxb.model.groupid.GroupIdRecord;
import org.orcid.jaxb.model.record_rc1.Education;
import org.orcid.jaxb.model.record_rc1.Employment;
import org.orcid.jaxb.model.record_rc1.Funding;
import org.orcid.jaxb.model.record_rc1.PeerReview;
import org.orcid.jaxb.model.record_rc1.Work;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface MemberV2ApiServiceDelegator {

    Response viewStatusText();

    Response viewActivities(String orcid);

    Response viewWork(String orcid, Long putCode);

    Response viewWorkSummary(String orcid, Long putCode);

    Response createWork(String orcid, Work work);

    Response updateWork(String orcid, Long putCode, Work work);

    Response deleteWork(String orcid, Long putCode);

    Response viewFunding(String orcid, Long putCode);

    Response viewFundingSummary(String orcid, Long putCode);

    Response createFunding(String orcid, Funding funding);

    Response updateFunding(String orcid, Long putCode, Funding funding);

    Response deleteFunding(String orcid, Long putCode);

    Response viewEducation(String orcid, Long putCode);

    Response viewEducationSummary(String orcid, Long putCode);

    Response createEducation(String orcid, Education education);

    Response updateEducation(String orcid, Long putCode, Education education);

    Response viewEmployment(String orcid, Long putCode);

    Response viewEmploymentSummary(String orcid, Long putCode);

    Response createEmployment(String orcid, Employment employment);

    Response updateEmployment(String orcid, Long putCode, Employment employment);

    Response deleteAffiliation(String orcid, Long putCode);

    Response viewPeerReview(String orcid, Long putCode);

    Response viewPeerReviewSummary(String orcid, Long putCode);

    Response createPeerReview(String orcid, PeerReview peerReview);

    Response updatePeerReview(String orcid, Long putCode, PeerReview peerReview);

    Response deletePeerReview(String orcid, Long putCode);

    Response viewGroupIdRecord(Long putCode);

    Response createGroupIdRecord(GroupIdRecord groupIdRecord);

    Response updateGroupIdRecord(GroupIdRecord groupIdRecord, Long putCode);

    Response deleteGroupIdRecord(Long putCode);

    Response viewGroupIdRecords(String pageSize, String pageNum);

}
