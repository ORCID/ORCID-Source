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
package org.orcid.frontend.web.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ActivityCacheManager;
import org.orcid.core.manager.AddressManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.GroupIdRecordManager;
import org.orcid.core.manager.OrcidProfileCacheManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.PersonalDetailsManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.security.aop.LockedException;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.groupid_rc2.GroupIdRecord;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingType;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Addresses;
import org.orcid.jaxb.model.record_rc2.Biography;
import org.orcid.jaxb.model.record_rc2.Emails;
import org.orcid.jaxb.model.record_rc2.Keywords;
import org.orcid.jaxb.model.record_rc2.Name;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;
import org.orcid.jaxb.model.record_rc2.Work;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.AffiliationForm;
import org.orcid.pojo.ajaxForm.Contributor;
import org.orcid.pojo.ajaxForm.FundingForm;
import org.orcid.pojo.ajaxForm.PeerReviewForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.WorkForm;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class PublicProfileController extends BaseWorkspaceController {

    @Resource
    private LocaleManager localeManager;

    @Resource
    private WorkManager workManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private ActivityCacheManager activityCacheManager;

    @Resource
    private ProfileEntityManager profileEntManager;

    @Resource
    private Jpa2JaxbAdapter jpa2JaxbAdapter;

    @Resource(name = "languagesMap")
    private LanguagesMap lm;

    @Resource
    private OrcidProfileCacheManager orcidProfileCacheManager;

    @Resource
    private ActivityCacheManager cacheManager;

    @Resource
    private PeerReviewManager peerReviewManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private GroupIdRecordManager groupIdRecordManager;
    
    @Resource
    private AddressManager addressManager;

    @Resource
    private PersonalDetailsManager personalDetailsManager;
    
    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenService;
    
    @Resource
    private OtherNameManager otherNameManager;
    
    @Resource
    private ProfileKeywordManager keywordManager;
    
    @Resource
    private ResearcherUrlManager researcherUrlManager;
    
    @Resource
    private EmailManager emailManager;
    
    @Resource
    private ExternalIdentifierManager externalIdentifierManager;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;
    
    public static int ORCID_HASH_LENGTH = 8;

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[x]}")
    public ModelAndView publicPreviewRedir(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "maxResults", defaultValue = "15") int maxResults, @PathVariable("orcid") String orcid) {
        RedirectView rv = new RedirectView();
        rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        rv.setUrl(getBasePath() + orcid.toUpperCase());
        return new ModelAndView(rv);
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}")
    public ModelAndView publicPreview(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "v", defaultValue = "0") int v, @RequestParam(value = "maxResults", defaultValue = "15") int maxResults,
            @PathVariable("orcid") String orcid) {

        if (!profileEntManager.orcidExists(orcid)) {
            return new ModelAndView("error-404");
        }        
               
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        
        try {
            //Check if the profile is deprecated, non claimed or locked
            orcidSecurityManager.checkProfile(orcid);
        } catch(OrcidDeprecatedException | OrcidNotClaimedException | LockedException e) {
            ModelAndView mav = new ModelAndView("public_profile_unavailable");
            mav.addObject("orcidId", orcid);
            String displayName = "";
            
            if(e instanceof OrcidDeprecatedException) {
                PersonalDetails publicPersonalDetails = personalDetailsManager.getPublicPersonalDetails(orcid);
                if(publicPersonalDetails.getName() != null) {
                    Name name = publicPersonalDetails.getName();
                    if(name.getVisibility().equals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC)) {
                        if(name.getCreditName() != null && !PojoUtil.isEmpty(name.getCreditName().getContent())) {
                            displayName = name.getCreditName().getContent();
                        } else {
                            if(name.getGivenNames() != null && !PojoUtil.isEmpty(name.getGivenNames().getContent())) {
                                displayName = name.getGivenNames().getContent() + " ";
                            }
                            if(name.getFamilyName() != null && !PojoUtil.isEmpty(name.getFamilyName().getContent())) {
                                displayName += name.getFamilyName().getContent();
                            }
                        }
                    }
                }
                mav.addObject("deprecated", true);
                mav.addObject("primaryRecord", profile.getPrimaryRecord().getId());
            } else if(e instanceof OrcidNotClaimedException) {
                displayName = localeManager.resolveMessage("orcid.reserved_for_claim");
            } else {
                mav.addObject("locked", true);
                displayName = localeManager.resolveMessage("public_profile.deactivated.given_names") + " " + localeManager.resolveMessage("public_profile.deactivated.family_name");
            }
            
            if(!PojoUtil.isEmpty(displayName)) {
                mav.addObject("title", getMessage("layout.public-layout.title", displayName, orcid));
                mav.addObject("displayName", displayName);
            }            
            return mav;
        }                
        
        Date lastModified = profile.getLastModified();
        long lastModifiedTime = 0;
        if(lastModified != null) {
            lastModifiedTime = lastModified.getTime();
        }
        
        ModelAndView mav = null;
        mav = new ModelAndView("public_profile_v3");
        mav.addObject("isPublicProfile", true);
        mav.addObject("orcidId", orcid);
        
        OrcidProfile orcidProfile = orcidProfileCacheManager.retrievePublic(orcid);
        mav.addObject("profile", orcidProfile);
        
        boolean isProfileEmtpy = true;

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(PUBLIC_WORKS_RESULTS_ATTRIBUTE);
        }
        
        PersonalDetails publicPersonalDetails = personalDetailsManager.getPublicPersonalDetails(orcid);
        
        //Fill personal details
        if(publicPersonalDetails != null) {
            // Get display name
            String displayName = "";
            
            if(publicPersonalDetails.getName() != null) {
                Name name = publicPersonalDetails.getName();
                if(name.getVisibility().equals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC)) {
                    if(name.getCreditName() != null && !PojoUtil.isEmpty(name.getCreditName().getContent())) {
                        displayName = name.getCreditName().getContent();
                    } else {
                        if(name.getGivenNames() != null && !PojoUtil.isEmpty(name.getGivenNames().getContent())) {
                            displayName = name.getGivenNames().getContent() + " ";
                        }
                        if(name.getFamilyName() != null && !PojoUtil.isEmpty(name.getFamilyName().getContent())) {
                            displayName += name.getFamilyName().getContent();
                        }
                    }
                }
            }
                        
            if (!PojoUtil.isEmpty(displayName)) {
                // <Published Name> (<ORCID iD>) - ORCID | Connecting Research
                // and Researchers
                mav.addObject("title", getMessage("layout.public-layout.title", displayName.trim(), orcid));
                mav.addObject("displayName", displayName);
            }
                        
            // Get biography
            if (publicPersonalDetails.getBiography() != null) {
                Biography bio = publicPersonalDetails.getBiography();
                if(bio.getVisibility().equals(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC) && !PojoUtil.isEmpty(bio.getContent())) {
                    isProfileEmtpy = false;
                }            
            }
            
            //Fill other names
            OtherNames publicOtherNames = publicPersonalDetails.getOtherNames();
            if(publicOtherNames != null && publicOtherNames.getOtherNames() != null) {
                Iterator<OtherName> it = publicOtherNames.getOtherNames().iterator();
                while(it.hasNext()) {
                    OtherName otherName = it.next();
                    if(!org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC.equals(otherName.getVisibility())) {
                        it.remove();
                    }
                }
            }
            mav.addObject("publicOtherNames", publicOtherNames);
        }
        
        //Fill biography elements
        //Fill country
        Addresses publicAddresses = addressManager.getPublicAddresses(orcid, lastModifiedTime);
        if(publicAddresses != null && publicAddresses.getAddress() != null) {
            for(Address publicAddress : publicAddresses.getAddress()) {
                if(Boolean.TRUE.equals(publicAddress.getPrimary())) {
                    mav.addObject("publicAddresses", publicAddress);
                    mav.addObject("countryName", getcountryName(publicAddress.getCountry().getValue().value()));
                    break;
                }
            }
        }
        
        //Fill keywords
        Keywords publicKeywords = keywordManager.getPublicKeywords(orcid, lastModifiedTime);
        mav.addObject("publicKeywords", publicKeywords);
        
        //Fill researcher urls
        ResearcherUrls publicResearcherUrls = researcherUrlManager.getPublicResearcherUrls(orcid, lastModifiedTime);
        mav.addObject("publicResearcherUrls", publicResearcherUrls);
        
        //Fill emails
        Emails publicEmails = emailManager.getPublicEmails(orcid, lastModifiedTime);
        mav.addObject("publicEmails", publicEmails);
        
        //Fill external identifiers
        PersonExternalIdentifiers publicPersonExternalIdentifiers = externalIdentifierManager.getPublicExternalIdentifiers(orcid, lastModifiedTime);
        mav.addObject("publicPersonExternalIdentifiers", publicPersonExternalIdentifiers);
                
        LinkedHashMap<Long, WorkForm> minimizedWorksMap = new LinkedHashMap<>();
        LinkedHashMap<Long, Affiliation> affiliationMap = new LinkedHashMap<>();
        LinkedHashMap<Long, Funding> fundingMap = new LinkedHashMap<>();
        LinkedHashMap<Long, PeerReview> peerReviewMap = new LinkedHashMap<>();
        
        minimizedWorksMap = minimizedWorksMap(orcid);
        if (minimizedWorksMap.size() > 0) {
            isProfileEmtpy = false;
        } else {
            mav.addObject("worksEmpty", true);
        }

        affiliationMap = affiliationMap(orcid);
        if (affiliationMap.size() > 0) {
            isProfileEmtpy = false;
        } else {
            mav.addObject("affiliationsEmpty", true);
        }

        fundingMap = fundingMap(orcid);
        if (fundingMap.size() > 0)
            isProfileEmtpy = false;
        else {
            mav.addObject("fundingEmpty", true);
        }

        peerReviewMap = peerReviewMap(orcid);
        if (peerReviewMap.size() > 0) {
            isProfileEmtpy = false;
        } else {
            mav.addObject("peerReviewsEmpty", true);
        }
        
        ObjectMapper mapper = new ObjectMapper();

        try {
            String worksIdsJson = mapper.writeValueAsString(minimizedWorksMap.keySet());
            String affiliationIdsJson = mapper.writeValueAsString(affiliationMap.keySet());
            String fundingIdsJson = mapper.writeValueAsString(fundingMap.keySet());
            String peerReviewIdsJson = mapper.writeValueAsString(peerReviewMap.keySet());
            mav.addObject("workIdsJson", StringEscapeUtils.escapeEcmaScript(worksIdsJson));
            mav.addObject("affiliationIdsJson", StringEscapeUtils.escapeEcmaScript(affiliationIdsJson));
            mav.addObject("fundingIdsJson", StringEscapeUtils.escapeEcmaScript(fundingIdsJson));
            mav.addObject("peerReviewIdsJson", StringEscapeUtils.escapeEcmaScript(peerReviewIdsJson));
            mav.addObject("isProfileEmpty", isProfileEmtpy);            
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!profile.isReviewed()) {
            if (isProfileValidForIndex(profile)) {
                int countTokens = orcidOauth2TokenService.findCountByUserName(orcid, lastModifiedTime);
                if (!profile.isAccountNonLocked() || countTokens == 0 || (!CreationMethod.WEBSITE.value().equals(profile.getCreationMethod())
                        && !CreationMethod.DIRECT.value().equals(profile.getCreationMethod()))) {
                    mav.addObject("noIndex", true);
                }
            } else {
                mav.addObject("noIndex", true);
            }
        }

        return mav;
    }

    private boolean isProfileValidForIndex(ProfileEntity profile) {
        String orcid = profile.getId();
        if (orcid != null) {
            int validAge = 3 + (Character.getNumericValue(orcid.charAt(orcid.length() - 2))) / 2;
            if (profile.getSubmissionDate() != null) {
                Date profileCreationDate = profile.getSubmissionDate();
                Date currentDate = new Date();

                Calendar temp = Calendar.getInstance();
                temp.setTime(profileCreationDate);
                temp.add(Calendar.DATE, validAge);
                profileCreationDate = temp.getTime();
                if (profileCreationDate.before(currentDate)) {
                    return true;
                }
            }
        }
        return false;
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/affiliations.json")
    public @ResponseBody List<AffiliationForm> getAffiliationsJson(HttpServletRequest request, @PathVariable("orcid") String orcid,
            @RequestParam(value = "affiliationIds") String workIdsStr) {
        List<AffiliationForm> affs = new ArrayList<AffiliationForm>();
        Map<Long, Affiliation> affMap = affiliationMap(orcid);
        String[] affIds = workIdsStr.split(",");
        for (String id : affIds) {
            Affiliation aff = affMap.get(Long.valueOf(id));
            // ONLY SHARE THE PUBLIC AFFILIATIONS!
            if (aff != null && aff.getVisibility().equals(Visibility.PUBLIC)) {
                AffiliationForm form = AffiliationForm.valueOf(aff);
                form.setCountryForDisplay(getMessage(buildInternationalizationKey(CountryIsoEntity.class, aff.getOrganization().getAddress().getCountry().name())));
                affs.add(form);
            }
        }

        return affs;
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/fundings.json")
    public @ResponseBody List<FundingForm> getFundingsJson(HttpServletRequest request, @PathVariable("orcid") String orcid,
            @RequestParam(value = "fundingIds") String fundingIdsStr) {
        Map<String, String> languages = lm.buildLanguageMap(localeManager.getLocale(), false);
        List<FundingForm> fundings = new ArrayList<FundingForm>();
        Map<Long, Funding> fundingMap = fundingMap(orcid);
        String[] fundingIds = fundingIdsStr.split(",");
        for (String id : fundingIds) {
            Funding funding = fundingMap.get(Long.valueOf(id));
            FundingForm form = FundingForm.valueOf(funding);
            // Set type name
            if (funding.getType() != null) {
                form.setFundingTypeForDisplay(getMessage(buildInternationalizationKey(FundingType.class, funding.getType().value())));
            }
            // Set translated title language name
            if (!(funding.getTitle().getTranslatedTitle() == null) && !StringUtils.isEmpty(funding.getTitle().getTranslatedTitle().getLanguageCode())) {
                String languageName = languages.get(funding.getTitle().getTranslatedTitle().getLanguageCode());
                form.getFundingTitle().getTranslatedTitle().setLanguageName(languageName);
            }
            // Set formatted amount
            if (funding.getAmount() != null && StringUtils.isNotBlank(funding.getAmount().getContent())) {
                BigDecimal bigDecimal = new BigDecimal(funding.getAmount().getContent());
                String formattedAmount = formatAmountString(bigDecimal);
                form.setAmount(Text.valueOf(formattedAmount));
            }

            // Set country name
            form.setCountryForDisplay(getMessage(buildInternationalizationKey(CountryIsoEntity.class, funding.getOrganization().getAddress().getCountry().name())));
            fundings.add(form);
        }
        return fundings;
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/works.json")
    public @ResponseBody List<WorkForm> getWorkJson(HttpServletRequest request, @PathVariable("orcid") String orcid, @RequestParam(value = "workIds") String workIdsStr) {
        Map<String, String> countries = retrieveIsoCountries();
        Map<String, String> languages = lm.buildLanguageMap(localeManager.getLocale(), false);

        HashMap<Long, WorkForm> minimizedWorksMap = minimizedWorksMap(orcid);

        List<WorkForm> works = new ArrayList<WorkForm>();
        String[] workIds = workIdsStr.split(",");

        for (String workId : workIds) {
            if (minimizedWorksMap.containsKey(Long.valueOf(workId))) {
                WorkForm work = minimizedWorksMap.get(Long.valueOf(workId));
                if (Visibility.PUBLIC.equals(work.getVisibility())) {
                    if (!PojoUtil.isEmpty(work.getCountryCode())) {
                        Text countryName = Text.valueOf(countries.get(work.getCountryCode().getValue()));
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
                    works.add(work);
                }
            }
        }

        return works;
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
        Map<String, String> countries = retrieveIsoCountries();
        Map<String, String> languages = lm.buildLanguageMap(localeManager.getLocale(), false);
        if (workId == null)
            return null;

        OrcidProfile userPubProfile = orcidProfileCacheManager.retrievePublic(orcid);
        java.util.Date lastModified = userPubProfile.getOrcidHistory().getLastModifiedDate().getValue().toGregorianCalendar().getTime();
        long lastModifiedTime = (lastModified == null) ? 0 : lastModified.getTime();
        
        Work workObj = workManager.getWork(orcid, workId, lastModifiedTime);

        if (workObj != null) {
            WorkForm work = WorkForm.valueOf(workObj);
            // Set country name
            if (!PojoUtil.isEmpty(work.getCountryCode())) {
                Text countryName = Text.valueOf(countries.get(work.getCountryCode().getValue()));
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
                        if (profileEntManager.orcidExists(contributorOrcid)) {
                            ProfileEntity profileEntity = profileEntityCacheManager.retrieve(contributorOrcid);
                            String publicContributorCreditName = cacheManager.getPublicCreditName(profileEntity);
                            contributor.setCreditName(Text.valueOf(publicContributorCreditName));                            
                            if (profileEntity.getRecordNameEntity() != null && profileEntity.getRecordNameEntity().getVisibility() != null) {                                
                                contributor.setCreditNameVisibility(org.orcid.pojo.ajaxForm.Visibility.valueOf(profileEntity.getRecordNameEntity().getVisibility()));
                            } else {
                                contributor.setCreditNameVisibility(org.orcid.pojo.ajaxForm.Visibility.valueOf(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility()));
                            }
                        } else {
                            if (contributor.getCreditNameVisibility() == null) {
                                contributor.setCreditNameVisibility(org.orcid.pojo.ajaxForm.Visibility.valueOf(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility()));
                            }
                        }
                    }
                }
            }

            return work;
        }

        return null;
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/peer-reviews.json")
    public @ResponseBody List<PeerReviewForm> getPeerReviewsJson(HttpServletRequest request, @PathVariable("orcid") String orcid,
            @RequestParam(value = "peerReviewIds") String peerReviewIdsStr) {
        Map<String, String> languages = lm.buildLanguageMap(localeManager.getLocale(), false);
        List<PeerReviewForm> peerReviews = new ArrayList<PeerReviewForm>();
        Map<Long, PeerReview> peerReviewMap = peerReviewMap(orcid);
        String[] peerReviewIds = peerReviewIdsStr.split(",");
        for (String id : peerReviewIds) {
            PeerReview peerReview = peerReviewMap.get(Long.valueOf(id));
            PeerReviewForm form = PeerReviewForm.valueOf(peerReview);
            // Set language name
            form.setCountryForDisplay(getMessage(buildInternationalizationKey(CountryIsoEntity.class, peerReview.getOrganization().getAddress().getCountry().name())));

            if (!PojoUtil.isEmpty(form.getTranslatedSubjectName())) {
                // Set translated title language name
                if (!StringUtils.isEmpty(form.getTranslatedSubjectName().getLanguageCode())) {
                    String languageName = languages.get(form.getTranslatedSubjectName().getLanguageCode());
                    form.getTranslatedSubjectName().setLanguageName(languageName);
                }
            }

            //Set the numeric id (the table id in the group_id_record table) of the group id
            if(form.getGroupId() != null && !PojoUtil.isEmpty(form.getGroupId().getValue())) {
                GroupIdRecord groupId = groupIdRecordManager.findByGroupId(form.getGroupId().getValue());
                form.setGroupIdPutCode(Text.valueOf(groupId.getPutCode()));
            }
            
            peerReviews.add(form);
        }
        return peerReviews;
    }

    @RequestMapping(value = "/public_widgets/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/{orcidHash}/info.json")
    public @ResponseBody OrcidInfo getInfo(@PathVariable("orcid") String orcid, @PathVariable("orcidHash") String orcidHash,
            @RequestParam(value = "locale", required = false) String localeParam) throws Exception {
        // Light weight security check. To keep copy and paste from easily
        // generating
        // the widget with out the user being logged in. Anyone that figures out
        // this is
        // a hash should be smart enough to just use the API.
        if (orcidHash.length() > 5 && !encryptionManager.sha256Hash(orcid).startsWith(orcidHash))
            throw new Exception(getMessage("web.orcid.securityhash.exception"));
        OrcidInfo result = new OrcidInfo();
        OrcidProfile profile = orcidProfileCacheManager.retrievePublic(orcid);
        result.setOrcid(orcid);

        if (profile.getOrcidBio().getPersonalDetails().getCreditName() != null
                && !PojoUtil.isEmpty(profile.getOrcidBio().getPersonalDetails().getCreditName().getContent())
                && profile.getOrcidBio().getPersonalDetails().getCreditName().getVisibility().equals(Visibility.PUBLIC)) {
            result.setName(profile.getOrcidBio().getPersonalDetails().getCreditName().getContent());
        } else {
            String name = "";

            if (profile.getOrcidBio().getPersonalDetails().getGivenNames() != null
                    && !PojoUtil.isEmpty(profile.getOrcidBio().getPersonalDetails().getGivenNames().getContent())) {
                name += profile.getOrcidBio().getPersonalDetails().getGivenNames().getContent();
            }

            if (profile.getOrcidBio().getPersonalDetails().getFamilyName() != null
                    && !PojoUtil.isEmpty(profile.getOrcidBio().getPersonalDetails().getFamilyName().getContent())) {
                name += " " + profile.getOrcidBio().getPersonalDetails().getFamilyName().getContent();
            }
            result.setName(name);
        }

        Locale locale = null;
        if (!StringUtil.isBlank(localeParam)) {
            locale = new Locale(localeParam);
        } else {
            locale = Locale.US;
        }

        ActivitiesSummary actSummary = profileEntManager.getPublicActivitiesSummary(orcid);

        boolean haveActivities = false;

        if (actSummary != null) {
            if (actSummary.getFundings() != null && actSummary.getFundings().getFundingGroup() != null && actSummary.getFundings().getFundingGroup().size() > 0) {
                String fundingsLabel = localeManager.resolveMessage("widget.labels.funding", locale, new Object() {
                });
                result.setValue(fundingsLabel, String.valueOf(actSummary.getFundings().getFundingGroup().size()));
                haveActivities = true;
            } else {
                String fundingsLabel = localeManager.resolveMessage("widget.labels.funding", locale, new Object() {
                });
                result.setValue(fundingsLabel, String.valueOf(0));
            }

            if (actSummary.getWorks() != null && actSummary.getWorks().getWorkGroup() != null && actSummary.getWorks().getWorkGroup().size() > 0) {
                String worksLabel = localeManager.resolveMessage("widget.labels.works", locale, new Object() {
                });
                result.setValue(worksLabel, String.valueOf(actSummary.getWorks().getWorkGroup().size()));
                haveActivities = true;
            } else {
                String worksLabel = localeManager.resolveMessage("widget.labels.works", locale, new Object() {
                });
                result.setValue(worksLabel, String.valueOf(0));
            }

            if (actSummary.getPeerReviews() != null && actSummary.getPeerReviews().getPeerReviewGroup() != null
                    && actSummary.getPeerReviews().getPeerReviewGroup().size() > 0) {
                String peerReviewsLabel = localeManager.resolveMessage("widget.labels.peer_review", locale, new Object() {
                });
                result.setValue(peerReviewsLabel, String.valueOf(actSummary.getPeerReviews().getPeerReviewGroup().size()));
                haveActivities = true;
            } else {
                String peerReviewsLabel = localeManager.resolveMessage("widget.labels.peer_review", locale, new Object() {
                });
                result.setValue(peerReviewsLabel, String.valueOf(0));
            }

            if (actSummary.getEducations() != null && actSummary.getEducations().getSummaries() != null && actSummary.getEducations().getSummaries().size() > 0) {
                String educationsLabel = localeManager.resolveMessage("widget.labels.educations", locale, new Object() {
                });
                result.setValue(educationsLabel, String.valueOf(actSummary.getEducations().getSummaries().size()));
                haveActivities = true;
            } else {
                String educationsLabel = localeManager.resolveMessage("widget.labels.educations", locale, new Object() {
                });
                result.setValue(educationsLabel, String.valueOf(0));
            }

            if (actSummary.getEmployments() != null && actSummary.getEmployments().getSummaries() != null && actSummary.getEmployments().getSummaries().size() > 0) {
                String employmentsLabel = localeManager.resolveMessage("widget.labels.employments", locale, new Object() {
                });
                result.setValue(employmentsLabel, String.valueOf(actSummary.getEmployments().getSummaries().size()));
                haveActivities = true;
            } else {
                String employmentsLabel = localeManager.resolveMessage("widget.labels.employments", locale, new Object() {
                });
                result.setValue(employmentsLabel, String.valueOf(0));
            }
        }

        if (!haveActivities) {
            // Set the no activities label
            result.setValue("no_activities", localeManager.resolveMessage("widget.labels.no_activties", locale, new Object() {
            }));
        }

        // Set the what is label
        result.setValue("what_is", localeManager.resolveMessage("widget.labels.what_is", locale, new Object() {
        }));

        return result;
    }

    /**
     * Get group information based on the group id
     * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/public/group/{groupId}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody GroupIdRecord getGroupInformation(@PathVariable("groupId") Long groupId) {
        return groupIdRecordManager.getGroupIdRecord(groupId);
    }

    public LinkedHashMap<Long, Funding> fundingMap(String orcid) {
        OrcidProfile profile = orcidProfileCacheManager.retrievePublic(orcid);
        if (profile == null)
            return null;
        return activityCacheManager.fundingMap(profile);
    }

    public LinkedHashMap<Long, Affiliation> affiliationMap(String orcid) {
        OrcidProfile profile = orcidProfileCacheManager.retrievePublic(orcid);
        if (profile == null)
            return null;
        return activityCacheManager.affiliationMap(profile);
    }

    public LinkedHashMap<Long, WorkForm> minimizedWorksMap(String orcid) {
        OrcidProfile profile = orcidProfileCacheManager.retrievePublic(orcid);
        if (profile == null)
            return null;
        return activityCacheManager.pubMinWorksMap(profile);
    }

    public LinkedHashMap<Long, PeerReview> peerReviewMap(String orcid) {
        OrcidProfile userPubProfile = orcidProfileCacheManager.retrievePublic(orcid);
        java.util.Date lastModified = userPubProfile.getOrcidHistory().getLastModifiedDate().getValue().toGregorianCalendar().getTime();
        return activityCacheManager.pubPeerReviewsMap(orcid, lastModified.getTime());
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
}

class OrcidInfo {
    public String orcid = "";
    public String name = "";
    public HashMap<String, String> values = new HashMap<String, String>();

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String name, String value) {
        values.put(name, value);
    }

    public String getValue(String name) {
        return values.get(name);
    }
}