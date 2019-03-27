package org.orcid.core.manager.impl;

import static org.orcid.core.api.OrcidApiConstants.PROFILE_GET_PATH;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.hibernate.exception.ConstraintViolationException;
import org.orcid.core.adapter.JpaJaxbEntityAdapter;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.APIRecordCreateManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.OrcidGenerationManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.core.togglz.Features;
import org.orcid.jaxb.model.common.OrcidType;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SourceClientId;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.OrcidGrantedAuthority;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.DateUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

public class APIRecordCreateManagerImpl implements APIRecordCreateManager {
    
    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Resource
    private OrcidGenerationManager orcidGenerationManager;

    @Resource
    protected JpaJaxbEntityAdapter adapter;
    
    @Resource
    private EncryptionManager encryptionManager;
    
    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private LocaleManager localeManager;
    
    public OrcidProfile createProfile(OrcidMessage orcidMessage) {
        if(Features.ENABLE_RECORD_CREATE_ENDPOINT.isActive()) {
            OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
            try {
                OrcidHistory orcidHistory = new OrcidHistory();
                orcidHistory.setCreationMethod(CreationMethod.API);
                orcidHistory.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
                orcidHistory.setSource(getSource());
                orcidProfile.setOrcidHistory(orcidHistory);
                return createOrcidProfileAndNotify(orcidProfile);                
            } catch (DataAccessException e) {
                if (e.getCause() != null && ConstraintViolationException.class.isAssignableFrom(e.getCause().getClass())) {
                    throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_email_exists.exception"));
                }
                throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_createorcid.exception"), e);
            }
        } else {
            throw new IllegalAccessError("This endpoint is not enabled, turn on ENABLE_RECORD_CREATE_ENDPOINT to enable it");
        }       
    }

    

    private OrcidProfile createOrcidProfileAndNotify(OrcidProfile orcidProfile) {
        OrcidProfile createdOrcidProfile = createOrcidProfile(orcidProfile, true, false);
        notificationManager.sendApiRecordCreationEmail(orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue(),
                orcidProfile.getOrcidIdentifier().getPath());
        return createdOrcidProfile;
    }

    private Source getSource() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            OAuth2Request authorizationRequest = ((OAuth2Authentication) authentication).getOAuth2Request();
            Source source = new Source();
            String sourceId = authorizationRequest.getClientId();
            source.setSourceClientId(new SourceClientId(sourceId));
            return source;
        }
        return null;
    }

    private OrcidProfile createOrcidProfile(OrcidProfile orcidProfile, boolean createdByMember, boolean usedCaptcha) {
        if (orcidProfile.getOrcidIdentifier() == null) {
            orcidProfile.setOrcidIdentifier(orcidGenerationManager.createNewOrcid());
        }

        Visibility defaultVisibility = Visibility.PRIVATE;
        if (orcidProfile.getOrcidInternal() != null && orcidProfile.getOrcidInternal().getPreferences() != null
                && orcidProfile.getOrcidInternal().getPreferences().getActivitiesVisibilityDefault() != null) {
            defaultVisibility = orcidProfile.getOrcidInternal().getPreferences().getActivitiesVisibilityDefault().getValue();
        }
        // If it is created by member, it is not claimed
        addSourceAndVisibilityToBioElements(orcidProfile, defaultVisibility);

        // Add source to emails, works and affiliations
        addSourceAndVisibilityToEmails(orcidProfile, defaultVisibility);
        
        if (orcidProfile.getOrcidActivities() != null) {
            addSourceToNewActivities(orcidProfile.getOrcidActivities(), defaultVisibility);
        }
        
        ProfileEntity profileEntity = adapter.toProfileEntity(orcidProfile);
        profileEntity.setUsedRecaptchaOnRegistration(usedCaptcha);
        profileEntity.setEncryptedPassword(orcidProfile.getPassword() == null ? null : encryptionManager.hashForInternalUse(orcidProfile.getPassword()));
        OrcidType userType = (profileEntity.getOrcidType() == null) ? OrcidType.USER : OrcidType.valueOf(profileEntity.getOrcidType());
        OrcidGrantedAuthority authority = new OrcidGrantedAuthority();
        authority.setProfileEntity(profileEntity);
        if (userType.equals(OrcidType.USER))
            authority.setAuthority(OrcidWebRole.ROLE_USER.getAuthority());
        else if (userType.equals(OrcidType.ADMIN))
            authority.setAuthority(OrcidWebRole.ROLE_ADMIN.getAuthority());
        Set<OrcidGrantedAuthority> authorities = new HashSet<OrcidGrantedAuthority>();
        authorities.add(authority);
        
        profileEntity.setAuthorities(authorities);
        setDefaultVisibility(profileEntity);

        try {
            profileEntity.setHashedOrcid(encryptionManager.sha256Hash(orcidProfile.getOrcidIdentifier().getPath()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        // Persist the profile
        profileDao.persist(profileEntity);
        profileDao.flush();

        // Then persist the works
        if (orcidProfile.getOrcidActivities() != null && orcidProfile.getOrcidActivities().getOrcidWorks() != null) {
            adapter.setWorks(profileEntity, orcidProfile.getOrcidActivities().getOrcidWorks());
        }

        OrcidProfile updatedTranslatedOrcid = adapter.toOrcidProfile(profileEntity, LoadOptions.ALL);
        return updatedTranslatedOrcid;
    }

    private void addSourceAndVisibilityToBioElements(OrcidProfile orcidProfile, Visibility defaultVisibility) {
        Source source = getSource();
        if (orcidProfile != null && orcidProfile.getOrcidBio() != null && source != null) {
            OrcidBio bio = orcidProfile.getOrcidBio();
            // Other names
            if (bio.getPersonalDetails() != null && bio.getPersonalDetails().getOtherNames() != null && bio.getPersonalDetails().getOtherNames().getOtherName() != null
                    && !bio.getPersonalDetails().getOtherNames().getOtherName().isEmpty()) {
                for (OtherName otherName : bio.getPersonalDetails().getOtherNames().getOtherName()) {
                    otherName.setSource(source);
                    otherName.setVisibility(defaultVisibility);
                }
            }

            // Address
            if (bio.getContactDetails() != null && bio.getContactDetails().getAddress() != null && bio.getContactDetails().getAddress().getCountry() != null) {
                bio.getContactDetails().getAddress().getCountry().setSource(source);
                bio.getContactDetails().getAddress().getCountry().setVisibility(defaultVisibility);
            }

            // Keywords
            if (bio.getKeywords() != null && bio.getKeywords().getKeyword() != null && !bio.getKeywords().getKeyword().isEmpty()) {
                Keywords keywords = bio.getKeywords();
                for (Keyword keyword : keywords.getKeyword()) {
                    keyword.setSource(source);
                    keyword.setVisibility(defaultVisibility);
                }
            }

            // Researcher urls
            if (bio.getResearcherUrls() != null && bio.getResearcherUrls().getResearcherUrl() != null && !bio.getResearcherUrls().getResearcherUrl().isEmpty()) {
                ResearcherUrls rUrls = bio.getResearcherUrls();
                for (ResearcherUrl rUrl : rUrls.getResearcherUrl()) {
                    rUrl.setSource(source);
                    rUrl.setVisibility(defaultVisibility);
                }
            }

            // External identifiers
            if (bio.getExternalIdentifiers() != null && bio.getExternalIdentifiers().getExternalIdentifier() != null
                    && !bio.getExternalIdentifiers().getExternalIdentifier().isEmpty()) {
                for (ExternalIdentifier extId : bio.getExternalIdentifiers().getExternalIdentifier()) {
                    extId.setSource(source);
                    extId.setVisibility(defaultVisibility);
                }
            }
        }
    }

    private void addSourceAndVisibilityToEmails(OrcidProfile orcidProfile, Visibility defaultVisibility) {
        Source source = getSource();
        if (orcidProfile != null && orcidProfile.getOrcidBio() != null && orcidProfile.getOrcidBio().getContactDetails() != null
                && orcidProfile.getOrcidBio().getContactDetails().getEmail() != null && source != null) {
            for (Email email : orcidProfile.getOrcidBio().getContactDetails().getEmail()) {
                email.setSourceClientId(source.retrieveSourcePath());
                email.setVisibility(defaultVisibility);
            }
        }
    }

    private void addSourceToNewActivities(OrcidActivities activities, Visibility defaultVisibility) {
        Source source = getSource();
        if(activities != null) {
            if(activities.getAffiliations() != null) {
                for (Affiliation affiliation : activities.getAffiliations().getAffiliation()) {
                    affiliation.setSource(source);
                    affiliation.setVisibility(defaultVisibility);
                }
            }
            
            if(activities.getFundings() != null) {
                for(Funding funding : activities.getFundings().getFundings()) {
                    funding.setSource(source);
                    funding.setVisibility(defaultVisibility);
                }
            }
            
            if(activities.getOrcidWorks() != null) {
                for(OrcidWork work : activities.getOrcidWorks().getOrcidWork()) {
                    work.setSource(source);
                    work.setVisibility(defaultVisibility);
                }
            }
        }
    }
    
    private void setDefaultVisibility(ProfileEntity profileEntity) {
        if (profileEntity != null) {
            // Names should be public by default
            if (profileEntity.getRecordNameEntity() != null && profileEntity.getRecordNameEntity().getVisibility() == null) {
                profileEntity.getRecordNameEntity()
                        .setVisibility(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().name());
            }

            if (profileEntity.getActivitiesVisibilityDefault() == null) {
                profileEntity.setActivitiesVisibilityDefault(OrcidVisibilityDefaults.CREATED_BY_MEMBER_DEFAULT.getVisibility().name());                
            }

            if (profileEntity.getRecordNameEntity() != null) {
                if (profileEntity.getRecordNameEntity().getVisibility() == null) {
                    profileEntity.getRecordNameEntity()
                            .setVisibility(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().name());
                }
            }

            if (profileEntity.getBiographyEntity() != null) {
                if (profileEntity.getBiographyEntity().getVisibility() == null) {
                    profileEntity.getBiographyEntity().setVisibility(OrcidVisibilityDefaults.CREATED_BY_MEMBER_DEFAULT.getVisibility().name());                    
                }
            }
        }
    }
}
