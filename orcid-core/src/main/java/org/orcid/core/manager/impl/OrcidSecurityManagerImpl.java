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
package org.orcid.core.manager.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.aop.LockedException;
import org.orcid.jaxb.model.common_rc4.VisibilityType;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary;
import org.orcid.jaxb.model.record_rc4.Person;
import org.orcid.jaxb.model.record_rc4.Record;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidSecurityManagerImpl implements OrcidSecurityManager {

    @Resource
    private SourceManager sourceManager;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailService;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Value("${org.orcid.core.token.write_validity_seconds:3600}")
    private int writeValiditySeconds;

    @Value("${org.orcid.core.claimWaitPeriodDays:10}")
    private int claimWaitPeriodDays;

    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;        
    
    @Override
    public boolean isAdmin() {
        Authentication authentication = getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof OrcidProfileUserDetails) {
                OrcidProfileUserDetails userDetails = (OrcidProfileUserDetails) principal;
                return OrcidType.ADMIN.equals(userDetails.getOrcidType());
            }
        }
        return false;
    }

    @Override
    public boolean isPasswordConfirmationRequired() {
        return sourceManager.isInDelegationMode() && !sourceManager.isDelegatedByAnAdmin();
    }

    private Authentication getAuthentication() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            return context.getAuthentication();
        }
        return null;
    }

    @Override
    public String getClientIdFromAPIRequest() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
            OAuth2Request request = oAuth2Authentication.getOAuth2Request();
            return request.getClientId();
        }
        return null;
    }

    /**
     * Checks a record status and throw an exception indicating if the profile
     * have any of the following conditions: - The record is not claimed and is
     * not old enough nor being accessed by its creator - It is locked - It is
     * deprecated - It is deactivated
     * 
     * @throws OrcidDeprecatedException
     *             in case the account is deprecated
     * @throws OrcidNotClaimedException
     *             in case the account is not claimed
     * @throws LockedException
     *             in the case the account is locked
     * */
    @Override
    public void checkProfile(String orcid) throws NoResultException, OrcidDeprecatedException, OrcidNotClaimedException, LockedException {
        ProfileEntity profile = null;

        try {
            profile = profileEntityCacheManager.retrieve(orcid);
        } catch (IllegalArgumentException e) {
            throw new NoResultException();
        }

        // Check if the user record is deprecated
        if(profile.getPrimaryRecord() != null) {
            StringBuffer primary = new StringBuffer(baseUrl).append("/").append(profile.getPrimaryRecord().getId());
            Map<String, String> params = new HashMap<String, String>();
            params.put(OrcidDeprecatedException.ORCID, primary.toString());
            if (profile.getDeprecatedDate() != null) {
                XMLGregorianCalendar calendar = DateUtils.convertToXMLGregorianCalendar(profile.getDeprecatedDate());
                params.put(OrcidDeprecatedException.DEPRECATED_DATE, calendar.toString());
            }
            throw new OrcidDeprecatedException(params);
        }
        
        //Check if the profile is not claimed and not old enough
        if((profile.getClaimed() == null || Boolean.FALSE.equals(profile.getClaimed())) && !isOldEnough(profile)) {
            //Let the creator access the profile even if it is not claimed and not old enough
            SourceEntity currentSourceEntity = sourceManager.retrieveSourceEntity();

            String profileSource = profile.getSource() == null ? null : profile.getSource().getSourceId();
            String currentSource = currentSourceEntity == null ? null : currentSourceEntity.getSourceId();

            // If the profile doesn't have source or the current source is not
            // the profile source, throw an exception
            if (profileSource == null || !Objects.equals(profileSource, currentSource)) {
                throw new OrcidNotClaimedException();
            }                        
        }                
        
        //Check if the record is locked
        if(!profile.isAccountNonLocked()) {
            LockedException lockedException = new LockedException();
            lockedException.setOrcid(profile.getId());
            throw lockedException;
        }
    }

    private boolean isOldEnough(ProfileEntity profile) {
        return DateUtils.olderThan(profile.getSubmissionDate(), claimWaitPeriodDays);
    }	

    @Override
	public void checkScopes(ScopePathType requiredScope) {
		// TODO Auto-generated method stub
		
	}
    
	@Override
	public void checkAndFilter(VisibilityType element, ScopePathType requiredScope) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkAndFilter(Collection<? extends VisibilityType> elements, ScopePathType requiredScope) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkAndFilter(ActivitiesSummary activities, ScopePathType requiredScope) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkAndFilter(Person person, ScopePathType requiredScope) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkAndFilter(Record record, ScopePathType requiredScope) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkClientAccessAndScopes(String orcid, ScopePathType requiredScope) {
		// TODO Auto-generated method stub
		
	}	
}
