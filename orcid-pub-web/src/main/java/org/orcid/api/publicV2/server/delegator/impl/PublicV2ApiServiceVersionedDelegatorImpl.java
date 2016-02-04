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
package org.orcid.api.publicV2.server.delegator.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.api.publicV2.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverterChain;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;

public class PublicV2ApiServiceVersionedDelegatorImpl implements PublicV2ApiServiceDelegator<Object, Object, Object, Object, Object, Object, Object, Object, Object> {

    @Resource
    private PublicV2ApiServiceDelegator<Object, Object, Object, Object, Object, Object, Object, Object, Object> publicV2ApiServiceDelegator;

    private String externalVersion;

    @Resource
    private V2VersionConverterChain v2VersionConverterChain;
    
    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private ProfileEntityManager profileEntityManager;
    
    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;

    @Override
    public Response viewStatusText() {
        return downgradeResponse(publicV2ApiServiceDelegator.viewStatusText());
    }

    @Override
    public Response viewActivities(String orcid) {        
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewActivities(orcid), orcid);
    }

    @Override
    public Response viewWork(String orcid, Long putCode) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewWork(orcid, putCode), orcid);
    }

    @Override
    public Response viewWorkCitation(String orcid, Long putCode) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewWorkCitation(orcid, putCode), orcid);
    }

    @Override
    public Response viewWorkSummary(String orcid, Long putCode) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewWorkSummary(orcid, putCode), orcid);
    }

    @Override
    public Response viewFunding(String orcid, Long putCode) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewFunding(orcid, putCode), orcid);
    }

    @Override
    public Response viewFundingSummary(String orcid, Long putCode) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewFundingSummary(orcid, putCode), orcid);
    }

    @Override
    public Response viewEducation(String orcid, Long putCode) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewEducation(orcid, putCode), orcid);
    }

    @Override
    public Response viewEducationSummary(String orcid, Long putCode) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewEducationSummary(orcid, putCode), orcid);
    }

    @Override
    public Response viewEmployment(String orcid, Long putCode) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewEmployment(orcid, putCode), orcid);
    }

    @Override
    public Response viewEmploymentSummary(String orcid, Long putCode) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewEmploymentSummary(orcid, putCode), orcid);
    }

    @Override
    public Response viewPeerReview(String orcid, Long putCode) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewPeerReview(orcid, putCode), orcid);
    }

    @Override
    public Response viewPeerReviewSummary(String orcid, Long putCode) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewPeerReviewSummary(orcid, putCode), orcid);
    }

    @Override
    public Response viewGroupIdRecord(Long putCode) {
        return publicV2ApiServiceDelegator.viewGroupIdRecord(putCode);
    }

    @Override
    public Response viewGroupIdRecords(String pageSize, String pageNum) {
        return publicV2ApiServiceDelegator.viewGroupIdRecords(pageSize, pageNum);
    }

    @Override
    public Response viewResearcherUrls(String orcid) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewResearcherUrls(orcid), orcid);
    }

    @Override
    public Response viewResearcherUrl(String orcid, Long putCode) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewResearcherUrl(orcid, putCode), orcid);
    }

    @Override
    public Response viewEmails(String orcid) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewEmails(orcid), orcid);
    }

    @Override
    public Response viewOtherNames(String orcid) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewOtherNames(orcid), orcid);
    }

    @Override
    public Response viewOtherName(String orcid, Long putCode) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewOtherName(orcid, putCode), orcid);
    }

    @Override
    public Response viewPersonalDetails(String orcid) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewPersonalDetails(orcid), orcid);
    }

    @Override
    public Response viewExternalIdentifiers(String orcid) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewExternalIdentifiers(orcid), orcid);
    }

    @Override
    public Response viewExternalIdentifier(String orcid, Long putCode) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewExternalIdentifier(orcid, putCode), orcid);
    }

    @Override
    public Response viewBiography(String orcid) {       
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewBiography(orcid), orcid);
    }

    @Override
    public Response viewKeywords(String orcid) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewKeywords(orcid), orcid);
    }

    @Override
    public Response viewKeyword(String orcid, Long putCode) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewKeyword(orcid, putCode), orcid);
    }

    @Override
    public Response viewAddresses(String orcid) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewAddresses(orcid), orcid);
    }

    @Override
    public Response viewAddress(String orcid, Long putCode) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewAddress(orcid, putCode), orcid);
    }

    @Override
    public Response viewPerson(String orcid) {
        return downgradeAndValidateResponse(publicV2ApiServiceDelegator.viewPerson(orcid), orcid);
    }

    private Response downgradeAndValidateResponse(Response response, String orcid) {
        checkProfileStatus(orcid);
        Response downgradedResponse = downgradeResponse(response);
        return downgradedResponse;
    }

    private Response downgradeResponse(Response response) {
        Object entity = response.getEntity();
        V2Convertible result = null;
        if (entity != null) {
            result = v2VersionConverterChain.downgrade(new V2Convertible(entity, PublicV2ApiServiceDelegator.LATEST_V2_VERSION), externalVersion);
        }
        return Response.fromResponse(response).entity(result.getObjectToConvert()).build();
    }

    private void checkProfileStatus(String orcid) {
    	ProfileEntity entity = profileEntityManager.findByOrcid(orcid);
        if (profileDao.isProfileDeprecated(orcid)) {
            StringBuffer primary = new StringBuffer(baseUrl).append("/").append(entity.getPrimaryRecord().getId());
            Map<String, String> params = new HashMap<String, String>();
            params.put(OrcidDeprecatedException.ORCID, primary.toString());
            if (entity.getDeprecatedDate() != null) {
                XMLGregorianCalendar calendar = DateUtils.convertToXMLGregorianCalendar(entity.getDeprecatedDate());
                params.put(OrcidDeprecatedException.DEPRECATED_DATE, calendar.toString());
            }
            throw new OrcidDeprecatedException(params);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setMemberV2ApiServiceDelegator(PublicV2ApiServiceDelegator memberV2ApiServiceDelegator) {
        this.publicV2ApiServiceDelegator = memberV2ApiServiceDelegator;
    }

    public String getExternalVersion() {
        return externalVersion;
    }

    public void setExternalVersion(String externalVersion) {
        this.externalVersion = externalVersion;
    }
}
