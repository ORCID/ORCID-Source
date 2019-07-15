package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ActivityManager;
import org.orcid.core.manager.v3.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.GroupIdRecordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PersonalDetailsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ResearchResourceManagerReadOnly;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.security.aop.LockedException;
import org.orcid.core.utils.v3.SourceUtils;
import org.orcid.core.utils.v3.activities.FundingComparators;
import org.orcid.core.utils.v3.activities.PeerReviewGroupComparator;
import org.orcid.frontend.web.pagination.Page;
import org.orcid.frontend.web.pagination.ResearchResourcePaginator;
import org.orcid.frontend.web.pagination.WorksPaginator;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.release.record.Affiliation;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.PersonalDetails;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.OrgDisambiguated;
import org.orcid.pojo.PublicRecordPersonDetails;
import org.orcid.pojo.ResearchResource;
import org.orcid.pojo.ResearchResourceGroupPojo;
import org.orcid.pojo.ajaxForm.AffiliationForm;
import org.orcid.pojo.ajaxForm.AffiliationGroupContainer;
import org.orcid.pojo.ajaxForm.AffiliationGroupForm;
import org.orcid.pojo.ajaxForm.Contributor;
import org.orcid.pojo.ajaxForm.FundingForm;
import org.orcid.pojo.ajaxForm.PeerReviewForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.WorkForm;
import org.orcid.pojo.grouping.FundingGroup;
import org.orcid.pojo.grouping.PeerReviewDuplicateGroup;
import org.orcid.pojo.grouping.PeerReviewGroup;
import org.orcid.pojo.grouping.WorkGroup;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class PublicProfileController extends BaseWorkspaceController {

    @Resource(name = "workManagerReadOnlyV3")
    private WorkManagerReadOnly workManagerReadOnly;

    @Resource(name = "peerReviewManagerReadOnlyV3")
    private PeerReviewManagerReadOnly peerReviewManagerReadOnly;
    
    @Resource(name = "profileFundingManagerReadOnlyV3")
    private ProfileFundingManagerReadOnly profileFundingManagerReadOnly;

    @Resource
    private WorksPaginator worksPaginator;

    @Resource(name = "activityManagerV3")
    private ActivityManager activityManager;

    @Resource(name = "languagesMap")
    private LanguagesMap lm;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;    

    @Resource(name = "groupIdRecordManagerReadOnlyV3")
    private GroupIdRecordManagerReadOnly groupIdRecordManagerReadOnly;

    @Resource(name = "personalDetailsManagerReadOnlyV3")
    private PersonalDetailsManagerReadOnly personalDetailsManagerReadOnly;

    @Resource
    private OrgDisambiguatedManager orgDisambiguatedManager;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenService;

    @Resource(name = "sourceUtilsV3")
    private SourceUtils sourceUtils;

    @Resource(name = "affiliationsManagerReadOnlyV3")
    private AffiliationsManagerReadOnly affiliationsManagerReadOnly;

    @Resource
    private ResearchResourcePaginator researchResourcePaginator;

    @Resource(name = "researchResourceManagerReadOnlyV3")
    private ResearchResourceManagerReadOnly researchResourceManagerReadOnly;

    public static int ORCID_HASH_LENGTH = 8;

    private Long getLastModifiedTime(String orcid) {
        return profileEntityManager.getLastModified(orcid);
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[x]}")
    public ModelAndView publicPreviewRedir(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "maxResults", defaultValue = "15") int maxResults, @PathVariable("orcid") String orcid) {
        RedirectView rv = new RedirectView();
        rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        rv.setUrl(getBasePath() + orcid.toUpperCase());
        return new ModelAndView(rv);
    }

    @RequestMapping(value = { "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}", "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/print" })
    public ModelAndView publicPreview(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "v", defaultValue = "0") int v, @RequestParam(value = "maxResults", defaultValue = "15") int maxResults,
            @PathVariable("orcid") String orcid) {

        ProfileEntity profile = null;

        try {
            profile = profileEntityCacheManager.retrieve(orcid);
        } catch (Exception e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return new ModelAndView("error-404");
        }

        Long lastModifiedTime = getLastModifiedTime(orcid);
        
        ModelAndView mav = null;
        if (request.getRequestURI().contains("/print")) {
            mav = new ModelAndView("print_public_record");
            mav.addObject("hideSupportWidget", true);
            mav.addObject("noIndex", true);
        } else {
            mav = new ModelAndView("public_profile_v3");
        }
        mav.addObject("isPublicProfile", true);
        mav.addObject("effectiveUserOrcid", orcid);
        mav.addObject("lastModifiedTime",  new java.util.Date(lastModifiedTime));

        if (!workManagerReadOnly.hasPublicWorks(orcid)) {
            mav.addObject("worksEmpty", true);
        }
        if (!researchResourceManagerReadOnly.hasPublicResearchResources(orcid)) {
            mav.addObject("researchResourcesEmpty", true);
        }

        if (!affiliationsManagerReadOnly.hasPublicAffiliations(orcid)) {
            mav.addObject("affiliationsEmpty", true);
        }

        if (!profileFundingManagerReadOnly.hasPublicFunding(orcid)) {
            mav.addObject("fundingEmpty", true);
        }
        
        if(!peerReviewManagerReadOnly.hasPublicPeerReviews(orcid)) {
            mav.addObject("peerReviewEmpty", true);
        }
        
        if (!profile.isReviewed()) {
            if (!orcidOauth2TokenService.hasToken(orcid, lastModifiedTime)) {
                mav.addObject("noIndex", true);
            }
        } 
        return mav;
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/userInfo.json", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getUserInfo( @PathVariable("orcid") String orcid) {
        Map<String, String> info = new HashMap<String, String>();
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        info.put("EFFECTIVE_USER_ORCID", orcid);
        info.put("IS_LOCKED", String.valueOf(!profile.isAccountNonLocked()));
        info.put("IS_DEACTIVATED", String.valueOf(!(profile.getDeactivationDate() == null)));
        if(profile.getPrimaryRecord() != null) {
            info.put("PRIMARY_RECORD", profile.getPrimaryRecord().getId()); 
        }
        return info;
    }
    
    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/person.json", method = RequestMethod.GET)
    public @ResponseBody PublicRecordPersonDetails getPersonDetails(@PathVariable("orcid") String orcid) {
        PublicRecordPersonDetails publicRecordPersonDetails = new PublicRecordPersonDetails();
        Boolean isDeprecated = false;
        
        try {
	            // Check if the profile is deprecated or locked
	        orcidSecurityManager.checkProfile(orcid);
        } catch (LockedException | DeactivatedException e) {
        	publicRecordPersonDetails.setDisplayName(
        			localeManager.resolveMessage("public_profile.deactivated.given_names") + " " + localeManager.resolveMessage("public_profile.deactivated.family_name")
        			);
        	return publicRecordPersonDetails;
        } catch (OrcidNotClaimedException e) {
        	publicRecordPersonDetails.setDisplayName( localeManager.resolveMessage("orcid.reserved_for_claim"));
        	return publicRecordPersonDetails;
        } catch (OrcidDeprecatedException e ) {
        	isDeprecated = true;
        	
        }
	
        publicRecordPersonDetails = getPersonDetails(orcid, true);
        if(isDeprecated) {
            // If deprecated be sure to remove all fields
            publicRecordPersonDetails.setBiography(null);
            publicRecordPersonDetails.setPublicGroupedOtherNames(null);
            publicRecordPersonDetails.setCountryNames(null);
            publicRecordPersonDetails.setPublicGroupedKeywords(null);
            publicRecordPersonDetails.setPublicGroupedEmails(null);
            publicRecordPersonDetails.setPublicGroupedResearcherUrls(null);
            publicRecordPersonDetails.setPublicGroupedPersonExternalIdentifiers(null);
        }
        return publicRecordPersonDetails;
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/affiliations.json")
    public @ResponseBody List<AffiliationForm> getAffiliationsJson(HttpServletRequest request, @PathVariable("orcid") String orcid,
            @RequestParam(value = "affiliationIds") String workIdsStr) {
        List<AffiliationForm> affs = new ArrayList<AffiliationForm>();
        Map<Long, Affiliation> affMap = activityManager.affiliationMap(orcid);
        String[] affIds = workIdsStr.split(",");
        for (String id : affIds) {
            Affiliation aff = affMap.get(Long.valueOf(id));
            validateVisibility(aff.getVisibility());
            AffiliationForm form = AffiliationForm.valueOf(aff);
            form.setCountryForDisplay(getMessage(buildInternationalizationKey(CountryIsoEntity.class, aff.getOrganization().getAddress().getCountry().name())));
            if (form.getOrgDisambiguatedId() != null) {
                OrgDisambiguated orgDisambiguated = orgDisambiguatedManager.findInDB(Long.parseLong(form.getOrgDisambiguatedId().getValue()));
                form.setOrgDisambiguatedName(orgDisambiguated.getValue());
                form.setOrgDisambiguatedUrl(orgDisambiguated.getUrl());
                form.setOrgDisambiguatedCity(orgDisambiguated.getCity());
                form.setOrgDisambiguatedRegion(orgDisambiguated.getRegion());
                form.setOrgDisambiguatedCountry(orgDisambiguated.getCountry());
                if (orgDisambiguated.getOrgDisambiguatedExternalIdentifiers() != null) {
                    form.setOrgDisambiguatedExternalIdentifiers(orgDisambiguated.getOrgDisambiguatedExternalIdentifiers());
                }
            }
            affs.add(form);
        }

        return affs;
    }
    
    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/fundingDetails.json", method = RequestMethod.GET)
    public @ResponseBody FundingForm getFundingDetails(@PathVariable("orcid") String orcid, @RequestParam("id") Long id) {        
        if (id == null)
            return null;        
        Map<String, String> languages = lm.buildLanguageMap(localeManager.getLocale(), false);
        Funding funding = profileFundingManagerReadOnly.getFunding(orcid, id);
        if (funding != null) {
            validateVisibility(funding.getVisibility());
            sourceUtils.setSourceName(funding);
            FundingForm form = FundingForm.valueOf(funding);
                   
            if (funding.getType() != null) {
                form.setFundingTypeForDisplay(getMessage(buildInternationalizationKey(org.orcid.jaxb.model.message.FundingType.class, funding.getType().value())));
            }
            // Set translated title language name
            if (!(funding.getTitle().getTranslatedTitle() == null) && !StringUtils.isEmpty(funding.getTitle().getTranslatedTitle().getLanguageCode())) {
                String languageName = languages.get(funding.getTitle().getTranslatedTitle().getLanguageCode());
                form.getFundingTitle().getTranslatedTitle().setLanguageName(languageName);
            }
    
            // Set the formatted amount
            if (funding.getAmount() != null && StringUtils.isNotBlank(funding.getAmount().getContent())) {
                BigDecimal bigDecimal = new BigDecimal(funding.getAmount().getContent());
                String formattedAmount = formatAmountString(bigDecimal);
                form.setAmount(Text.valueOf(formattedAmount));
            }
    
            form.setCountryForDisplay(getMessage(buildInternationalizationKey(CountryIsoEntity.class, funding.getOrganization().getAddress().getCountry().name())));
            return form;
        }
        return null;
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/fundingGroups.json")
    public @ResponseBody List<FundingGroup> getFundingsJson(HttpServletRequest request, @PathVariable("orcid") String orcid,
            @RequestParam("sort") String sort, @RequestParam("sortAsc") boolean sortAsc) {
        List<FundingGroup> fundingGroups = new ArrayList<>();
        List<FundingSummary> summaries = profileFundingManagerReadOnly.getFundingSummaryList(orcid);
        Fundings fundings = profileFundingManagerReadOnly.groupFundings(summaries, true);
        for (org.orcid.jaxb.model.v3.release.record.summary.FundingGroup group : fundings.getFundingGroup()) {
            FundingGroup fundingGroup = FundingGroup.valueOf(group);
            for(org.orcid.jaxb.model.v3.release.record.summary.FundingSummary summary : summaries) {
                if(summary.getSource().retrieveSourcePath().equals(orcid)) {
                    fundingGroup.setUserVersionPresent(true);
                    break;
                }
            }
            for(FundingForm summaryForm : fundingGroup.getFundings()) {
                if(summaryForm.getFundingType().getValue() != null) {
                    summaryForm.setFundingTypeForDisplay(getMessage(buildInternationalizationKey(org.orcid.jaxb.model.message.FundingType.class, summaryForm.getFundingType().getValue())));
                }         
            }
            fundingGroups.add(fundingGroup);
        }

        fundingGroups.sort(FundingComparators.getInstance(sort, sortAsc));
        return fundingGroups;
    }
    
    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/allWorks.json", method = RequestMethod.GET)
    public @ResponseBody Page<WorkGroup> getAllWorkGroupsJson(@PathVariable("orcid") String orcid, @RequestParam("sort") String sort,
            @RequestParam("sortAsc") boolean sortAsc) {
        return worksPaginator.getAllWorks(orcid, true, sort, sortAsc);
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/worksPage.json", method = RequestMethod.GET)
    public @ResponseBody Page<WorkGroup> getWorkGroupsJson(@PathVariable("orcid") String orcid, @RequestParam("offset") int offset, @RequestParam("sort") String sort,
            @RequestParam("sortAsc") boolean sortAsc) {
        return worksPaginator.getWorksPage(orcid, offset, true, sort, sortAsc);
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/researchResourcePage.json", method = RequestMethod.GET)
    public @ResponseBody Page<ResearchResourceGroupPojo> getResearchResourceGroupsJson(@PathVariable("orcid") String orcid, @RequestParam("offset") int offset,
            @RequestParam("sort") String sort, @RequestParam("sortAsc") boolean sortAsc) {
        return researchResourcePaginator.getPage(orcid, offset, true, sort, sortAsc);
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/researchResource.json", method = RequestMethod.GET)
    public @ResponseBody ResearchResource getResearchResource(@PathVariable("orcid") String orcid, @RequestParam("id") int id) {
        org.orcid.jaxb.model.v3.release.record.ResearchResource r = researchResourceManagerReadOnly.getResearchResource(orcid, Long.valueOf(id));
        validateVisibility(r.getVisibility());
        sourceUtils.setSourceName(r);
        return ResearchResource.fromValue(r);
    }

    /**
     * Returns the work info for a given work id
     * 
     * @param workId
     *            The id of the work
     * @return the content of that work
     */
    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/getWorkInfo.json", method = RequestMethod.GET)
    public @ResponseBody WorkForm getWorkInfo(@PathVariable("orcid") String orcid, @RequestParam(value = "workId") Long workId) {
        Map<String, String> languages = lm.buildLanguageMap(localeManager.getLocale(), false);
        if (workId == null)
            return null;

        Work workObj = workManagerReadOnly.getWork(orcid, workId);
        if (workObj != null) {
            validateVisibility(workObj.getVisibility());
            sourceUtils.setSourceName(workObj);
            WorkForm work = WorkForm.valueOf(workObj);
            // Set country name
            if (!PojoUtil.isEmpty(work.getCountryCode())) {
                Text countryName = Text.valueOf(retrieveIsoCountries().get(work.getCountryCode().getValue()));
                work.setCountryName(countryName);
            }
            // Set language name
            if (!PojoUtil.isEmpty(work.getLanguageCode())) {
                Text languageName = Text.valueOf(languages.get(work.getLanguageCode().getValue()));
                work.setLanguageName(languageName);
            }
            // Set translated title language name
            if (work.getTranslatedTitle() != null && !StringUtils.isEmpty(work.getTranslatedTitle().getLanguageCode())) {
                String languageName = languages.get(work.getTranslatedTitle().getLanguageCode());
                work.getTranslatedTitle().setLanguageName(languageName);
            }

            if (work.getContributors() != null) {
                for (Contributor contributor : work.getContributors()) {
                    if (!PojoUtil.isEmpty(contributor.getOrcid())) {
                        String contributorOrcid = contributor.getOrcid().getValue();
                        if (profileEntityManager.orcidExists(contributorOrcid)) {
                            ProfileEntity profileEntity = profileEntityCacheManager.retrieve(contributorOrcid);
                            String publicContributorCreditName = activityManager.getPublicCreditName(profileEntity);
                            contributor.setCreditName(Text.valueOf(publicContributorCreditName));
                        }
                    }
                }
            }

            return work;
        }
        return null;
    }
    
    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/peer-review.json", method = RequestMethod.GET)
    public @ResponseBody PeerReviewForm getPeerReview(@PathVariable("orcid") String orcid, @RequestParam("putCode") Long putCode) {
        PeerReview peerReview = peerReviewManagerReadOnly.getPeerReview(orcid, putCode);
        validateVisibility(peerReview.getVisibility());
        sourceUtils.setSourceName(peerReview);
        return PeerReviewForm.valueOf(peerReview);
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/peer-reviews.json")
    public @ResponseBody List<PeerReviewGroup> getPeerReviewsJson(@PathVariable("orcid") String orcid, @RequestParam("sortAsc") boolean sortAsc) {
        List<PeerReviewGroup> peerReviewGroups = new ArrayList<>();
        List<PeerReviewSummary> summaries = peerReviewManagerReadOnly.getPeerReviewSummaryList(orcid);
        PeerReviews peerReviews = peerReviewManagerReadOnly.groupPeerReviews(summaries, true);
        for (org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup group : peerReviews.getPeerReviewGroup()) {
            Optional<GroupIdRecord> groupIdRecord = groupIdRecordManagerReadOnly.findByGroupId(group.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getGroupId());
            PeerReviewGroup peerReviewGroup = PeerReviewGroup.getInstance(group, groupIdRecord.get());
            for (PeerReviewDuplicateGroup duplicateGroup : peerReviewGroup.getPeerReviewDuplicateGroups()) {
                for (PeerReviewForm peerReviewForm : duplicateGroup.getPeerReviews()) {
                    if (peerReviewForm.getCountry() != null) {
                        peerReviewForm.setCountryForDisplay(getMessage(buildInternationalizationKey(CountryIsoEntity.class, peerReviewForm.getCountry().getValue())));
                    }
                }
            }
            peerReviewGroups.add(peerReviewGroup);
        }
        peerReviewGroups.sort(new PeerReviewGroupComparator(!sortAsc));
        return peerReviewGroups;
    }

    /**
     * Get group information based on the group id
     * 
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/public/group/{groupId}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody GroupIdRecord getGroupInformation(@PathVariable("groupId") Long groupId) {
        return groupIdRecordManagerReadOnly.getGroupIdRecord(groupId);
    }

    /**
     * Format a big decimal based on a locale
     * 
     * @param bigDecimal
     * @param currencyCode
     * @return a string with the number formatted based on the locale
     */
    private String formatAmountString(BigDecimal bigDecimal) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(localeManager.getLocale());
        return numberFormat.format(bigDecimal);
    }

    private void validateVisibility(Visibility v) {
        if (!Visibility.PUBLIC.equals(v)) {
            throw new IllegalArgumentException("Invalid request");
        }
    }
    
    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/affiliationDetails.json", method = RequestMethod.GET)
    public @ResponseBody AffiliationForm getAffiliationDetails(@PathVariable("orcid") String orcid, @RequestParam("id") Long id, @RequestParam("type") String type) {        
        Affiliation aff; 
        if (type.equals("distinction")) {
            aff = affiliationsManagerReadOnly.getDistinctionAffiliation(orcid, id);
        } else if (type.equals("education")) {
            aff = affiliationsManagerReadOnly.getEducationAffiliation(orcid, id);
        } else if (type.equals("employment")) {
            aff = affiliationsManagerReadOnly.getEmploymentAffiliation(orcid, id);
        } else if (type.equals("invited-position")) {
            aff = affiliationsManagerReadOnly.getInvitedPositionAffiliation(orcid, id);
        } else if (type.equals("membership")) {
            aff = affiliationsManagerReadOnly.getMembershipAffiliation(orcid, id);
        } else if (type.equals("qualification")) {
            aff = affiliationsManagerReadOnly.getQualificationAffiliation(orcid, id);
        } else if (type.equals("service")) {
            aff = affiliationsManagerReadOnly.getServiceAffiliation(orcid, id);
        } else {
            throw new IllegalArgumentException("Invalid affiliation type: " + type);
        }
        
        validateVisibility(aff.getVisibility());
        sourceUtils.setSourceName(aff);
        return AffiliationForm.valueOf(aff);
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/affiliationGroups.json", method = RequestMethod.GET)
    public @ResponseBody AffiliationGroupContainer getGroupedAffiliations(@PathVariable("orcid") String orcid) {        
        AffiliationGroupContainer result = new AffiliationGroupContainer();
        Map<AffiliationType, List<AffiliationGroup<AffiliationSummary>>> affiliationsMap = affiliationsManagerReadOnly.getGroupedAffiliations(orcid, true);
        for (AffiliationType type : AffiliationType.values()) {
            if (affiliationsMap.containsKey(type)) {
                List<AffiliationGroup<AffiliationSummary>> elementsList = affiliationsMap.get(type);
                List<AffiliationGroupForm> elementsFormList = new ArrayList<AffiliationGroupForm>();
                IntStream.range(0, elementsList.size()).forEach(idx -> {
                    AffiliationGroupForm groupForm = AffiliationGroupForm.valueOf(elementsList.get(idx), type.name() + '_' + idx, orcid);
                    // Fill country on the default affiliation
                    AffiliationForm defaultAffiliation = groupForm.getDefaultAffiliation();
                    if (defaultAffiliation != null) {
                        // Set country name
                        defaultAffiliation.setCountryForDisplay(groupForm.getDefaultAffiliation().getCountry().getValue());
                    }

                    // Fill country for each affiliation
                    for (AffiliationForm aff : groupForm.getAffiliations()) {
                        // Set country name
                        aff.setCountryForDisplay(aff.getCountry().getValue());
                    }

                    elementsFormList.add(groupForm);
                });
                result.getAffiliationGroups().put(type, elementsFormList);
            }
        }
        return result;
    }
}