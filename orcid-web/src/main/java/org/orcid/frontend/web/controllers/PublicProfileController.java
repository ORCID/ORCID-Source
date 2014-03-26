/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ActivityCacheManager;
import org.orcid.core.manager.OrcidProfileCacheManager;
import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingType;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.pojo.ajaxForm.AffiliationForm;
import org.orcid.pojo.ajaxForm.Contributor;
import org.orcid.pojo.ajaxForm.FundingForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.Work;
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

    @Resource
    private LocaleManager localeManager;

    @Resource
    private WorkManager workManager;

    @Resource
    private ActivityCacheManager activityCacheManager;

    @Resource
    private ProfileWorkManager profileWorkManager;

    @Resource
    private Jpa2JaxbAdapter jpa2JaxbAdapter;
    
    @Resource(name="languagesMap")
    private LanguagesMap lm;

    @Resource
    private OrcidProfileCacheManager orcidProfileCacheManager;

    @Resource
    private ActivityCacheManager cacheManager;
    
    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dx]}")
    public ModelAndView publicPreviewRedir(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "maxResults", defaultValue = "15") int maxResults, @PathVariable("orcid") String orcid) {
        RedirectView rv = new RedirectView();
        rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        rv.setUrl(getBasePath() + orcid.toUpperCase());
        return new ModelAndView(rv);
    }
    
    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}")
    public ModelAndView publicPreview(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "maxResults", defaultValue = "15") int maxResults, @PathVariable("orcid") String orcid) {
        ModelAndView mav = new ModelAndView("public_profile");
        mav.addObject("isPublicProfile", true);

        boolean isProfileEmtpy = true;
        
        request.getSession().removeAttribute(PUBLIC_WORKS_RESULTS_ATTRIBUTE);
        OrcidProfile profile = orcidProfileCacheManager.retrievePublicOrcidProfile(orcid);

        mav.addObject("profile", profile);

        HashMap<String, Work> minimizedWorksMap = new HashMap<String, Work>();
        HashMap<String, Affiliation> affiliationMap = new HashMap<String, Affiliation>();
        HashMap<String, Funding> fundingMap = new HashMap<String, Funding>();

        if(profile != null && profile.getOrcidBio() != null && profile.getOrcidBio().getBiography() != null && StringUtils.isNotBlank(profile.getOrcidBio().getBiography().getContent())){
            isProfileEmtpy = false;
        }
        
        if (profile.getOrcidDeprecated() != null) {
            String primaryRecord = profile.getOrcidDeprecated().getPrimaryRecord().getOrcidIdentifier().getPath();
            mav.addObject("deprecated", true);
            mav.addObject("primaryRecord", primaryRecord);
        } else {
            minimizedWorksMap = minimizedWorksMap(orcid);
            if (minimizedWorksMap.size() > 0) {
                mav.addObject("works", minimizedWorksMap.values());
                isProfileEmtpy = false;
            } else {
                mav.addObject("worksEmpty", true);
            }

            affiliationMap = affiliationMap(orcid);
            if (affiliationMap.size() > 0) {
                mav.addObject("affilations", affiliationMap.values());
                isProfileEmtpy = false;
            } else {
                mav.addObject("affiliationsEmpty", true);
            }
            
            fundingMap = fundingMap(orcid);            
            if(fundingMap.size() > 0)
                isProfileEmtpy = false;
            else {
                mav.addObject("fundingEmpty", true);
            }

        }
        ObjectMapper mapper = new ObjectMapper();

        try {
            String worksIdsJson = mapper.writeValueAsString(minimizedWorksMap.keySet());
            String affiliationIdsJson = mapper.writeValueAsString(affiliationMap.keySet());
            String fundingIdsJson = mapper.writeValueAsString(fundingMap.keySet());
            mav.addObject("workIdsJson", StringEscapeUtils.escapeEcmaScript(worksIdsJson));
            mav.addObject("affiliationIdsJson", StringEscapeUtils.escapeEcmaScript(affiliationIdsJson));
            mav.addObject("fundingIdsJson", StringEscapeUtils.escapeEcmaScript(fundingIdsJson));
            mav.addObject("isProfileEmpty", isProfileEmtpy);
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return mav;
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/affiliations.json")
    public @ResponseBody
    List<AffiliationForm> getAffiliationsJson(HttpServletRequest request, @PathVariable("orcid") String orcid, @RequestParam(value = "affiliationIds") String workIdsStr) {
        List<AffiliationForm> affs = new ArrayList<AffiliationForm>();
        Map<String, Affiliation> affMap = affiliationMap(orcid);
        String[] affIds = workIdsStr.split(",");
        for (String id : affIds) {
            Affiliation aff = affMap.get(id);
            // ONLY SHARE THE PUBLIC AFFILIATIONS!
            if (aff != null && aff.getVisibility().equals(Visibility.PUBLIC)) {
                affs.add(AffiliationForm.valueOf(aff));
            }
        }
        return affs;
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/fundings.json")
    public @ResponseBody
    List<FundingForm> getFundingsJson(HttpServletRequest request, @PathVariable("orcid") String orcid, @RequestParam(value = "fundingIds") String fundingIdsStr) {
        Map<String, String> languages = lm.buildLanguageMap(localeManager.getLocale(), false);
        List<FundingForm> fundings = new ArrayList<FundingForm>();
        Map<String, Funding> fundingMap = fundingMap(orcid);
        String[] fundingIds = fundingIdsStr.split(",");
        for (String id : fundingIds) {
            Funding funding = fundingMap.get(id);
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
            // Set country name
            form.setCountryForDisplay(getMessage(buildInternationalizationKey(CountryIsoEntity.class, funding.getOrganization().getAddress().getCountry().name())));
            fundings.add(form);
        }
        return fundings;
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/works.json")
    public @ResponseBody
    List<Work> getWorkJson(HttpServletRequest request, @PathVariable("orcid") String orcid, @RequestParam(value = "workIds") String workIdsStr) {
        Map<String, String> countries = retrieveIsoCountries();
        Map<String, String> languages = lm.buildLanguageMap(localeManager.getLocale(), false);

        HashMap<String, Work> minimizedWorksMap = minimizedWorksMap(orcid);

        List<Work> works = new ArrayList<Work>();
        String[] workIds = workIdsStr.split(",");

        for (String workId : workIds) {
            if (minimizedWorksMap.containsKey(workId)) {
                Work work = minimizedWorksMap.get(workId);
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
                    if (work.getWorkTitle() != null && work.getWorkTitle().getTranslatedTitle() != null
                            && !StringUtils.isEmpty(work.getWorkTitle().getTranslatedTitle().getLanguageCode())) {
                        String languageName = languages.get(work.getWorkTitle().getTranslatedTitle().getLanguageCode());
                        work.getWorkTitle().getTranslatedTitle().setLanguageName(languageName);
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
     * */
    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/getWorkInfo.json", method = RequestMethod.GET)
    public @ResponseBody
    Work getWorkInfo(@PathVariable("orcid") String orcid, @RequestParam(value = "workId") String workId) {
        Map<String, String> countries = retrieveIsoCountries();
        Map<String, String> languages = lm.buildLanguageMap(localeManager.getLocale(), false);
        if (StringUtils.isEmpty(workId))
            return null;

        ProfileWorkEntity profileWork = profileWorkManager.getProfileWork(orcid, workId);

        if (profileWork != null) {
            OrcidWork orcidWork = jpa2JaxbAdapter.getOrcidWork(profileWork);
            if (orcidWork != null) {
                Work work = Work.valueOf(orcidWork);
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
                if (work.getWorkTitle() != null && work.getWorkTitle().getTranslatedTitle() != null
                        && !StringUtils.isEmpty(work.getWorkTitle().getTranslatedTitle().getLanguageCode())) {
                    String languageName = languages.get(work.getWorkTitle().getTranslatedTitle().getLanguageCode());
                    work.getWorkTitle().getTranslatedTitle().setLanguageName(languageName);
                }

                //If the work source is the user himself, fill the work source name
                if(!PojoUtil.isEmpty(work.getWorkSource()) && profileWork.getProfile().getId().equals(work.getWorkSource().getValue())){
                    List<Contributor> contributors = work.getContributors();
                    if(work.getContributors() != null) {
                        for(Contributor contributor : contributors){  
                            //If it is not an empty contributor
                            if(!PojoUtil.isEmpty(contributor.getContributorRole()) || !PojoUtil.isEmpty(contributor.getContributorSequence())) {
                                ProfileEntity profile = profileWork.getProfile();
                                String creditNameString = cacheManager.getPublicCreditName(profile);   
                                Text creditName = Text.valueOf(creditNameString);
                                contributor.setCreditName(creditName);
                            }                            
                        }
                    }
                }
                return work;
            }
        }

        return null;
    }

    public HashMap<String, Funding> fundingMap(String orcid) {
        OrcidProfile profile = orcidProfileCacheManager.retrievePublicOrcidProfile(orcid);
        if (profile == null)
            return null;
        return activityCacheManager.fundingMap(profile);
    }

    public HashMap<String, Affiliation> affiliationMap(String orcid) {
        OrcidProfile profile = orcidProfileCacheManager.retrievePublicOrcidProfile(orcid);
        if (profile == null)
            return null;
        return activityCacheManager.affiliationMap(profile);
    }

    public HashMap<String, Work> minimizedWorksMap(String orcid) {
        OrcidProfile profile = orcidProfileCacheManager.retrievePublicOrcidProfile(orcid);
        if (profile == null)
            return null;
        return activityCacheManager.pubMinWorksMap(profile);
    }
}