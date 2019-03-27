package T2OrcidApiServiceImplBase;

import static org.orcid.api.common.T2OrcidApiService.OAUTH_TOKEN;
import static org.orcid.api.common.T2OrcidApiService.OAUTH_REVOKE;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_XML;

import java.net.URI;
import java.util.Date;

import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.ORCID_XML;
import static org.orcid.core.api.OrcidApiConstants.PROFILE_POST_PATH;
import static org.orcid.core.api.OrcidApiConstants.PROFILE_GET_PATH;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.oauth.OrcidClientCredentialEndPointDelegator;
import org.orcid.core.security.visibility.aop.AccessControl;
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
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SourceClientId;
import org.orcid.jaxb.model.message.SourceName;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.Work;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.DateUtils;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.OrcidGenerationManager;
import org.orcid.core.manager.v3.NotificationManager;

@Path("/")
public class OrcidApiCommonEndpoints {

    @Context
    private UriInfo uriInfo;

    @Resource
    private OrcidClientCredentialEndPointDelegator orcidClientCredentialEndPointDelegator;

    @Resource
    private LocaleManager localeManager;

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Resource
    private OrcidGenerationManager orcidGenerationManager;

    @Resource
    private Jaxb2JpaAdapter jaxb2JpaAdapter;
    
    /**
     * 
     * @param formParams
     * @return
     */
    @POST
    @Path(OAUTH_TOKEN)
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response obtainOauth2TokenPost(@HeaderParam("Authorization") @DefaultValue(StringUtils.EMPTY) String authorization, @FormParam("grant_type") String grantType,
            MultivaluedMap<String, String> formParams) {
        return orcidClientCredentialEndPointDelegator.obtainOauth2Token(authorization, formParams);
    }

    /**
     * POST an XML representation of the entire ORCID profile
     * 
     * @return the XML representation of the ORCID record including the added
     *         work(s)
     */
    @POST
    @Produces(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Consumes(value = { VND_ORCID_XML, ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD, VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
    @Path("/v1.2" + PROFILE_POST_PATH)
    @AccessControl(requiredScope = ScopePathType.ORCID_PROFILE_CREATE)
    public Response createProfile(OrcidMessage orcidMessage) {
        // TODO: UGLY UGLY!!!! we should remove this ASAP
        return createProfile(uriInfo, orcidMessage);
    }

    private Response createProfile(UriInfo uriInfo, OrcidMessage orcidMessage) {
        OrcidProfile orcidProfile = orcidMessage.getOrcidProfile();
        try {
            OrcidHistory orcidHistory = new OrcidHistory();
            orcidHistory.setCreationMethod(CreationMethod.API);
            orcidHistory.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
            orcidHistory.setSource(getSource());
            orcidProfile.setOrcidHistory(orcidHistory);
            orcidProfile = createOrcidProfileAndNotify(orcidProfile);
            return getCreatedResponse(uriInfo, PROFILE_GET_PATH, orcidProfile);
        } catch (DataAccessException e) {
            if (e.getCause() != null && ConstraintViolationException.class.isAssignableFrom(e.getCause().getClass())) {
                throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_email_exists.exception"));
            }
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_createorcid.exception"), e);
        }
    }

    private Response getCreatedResponse(UriInfo uriInfo, String requested, OrcidProfile profile) {
        if (profile != null && profile.getOrcidIdentifier() != null) {
            URI uri = uriInfo.getBaseUriBuilder().path("/").path(requested).build(profile.getOrcidIdentifier().getPath());
            return Response.created(uri).build();
        } else {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_findorcid.exception"));
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
        encryptAndMapFieldsForProfileEntityPersistence(orcidProfile, profileEntity);
        profileEntity.setAuthorities(getGrantedAuthorities(profileEntity));
        setDefaultVisibility(profileEntity, createdByMember, defaultVisibility);

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
    
}
