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
import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.pojo.ajaxForm.AffiliationForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.Work;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PublicProfileController extends BaseWorkspaceController {

	private static final String WORKS_MAP = "WORKS_MAP"; 
	
    @Resource
    private LocaleManager localeManager;
    
    @Resource
    private WorkManager workManager;
    
    @Resource
    private ProfileWorkManager profileWorkManager;

    @Resource
    private Jpa2JaxbAdapter jpa2JaxbAdapter;
    
    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}")
    public ModelAndView publicPreview(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "maxResults", defaultValue = "15") int maxResults, @PathVariable("orcid") String orcid) {
        ModelAndView mav = new ModelAndView("public_profile");
        mav.addObject("isPublicProfile", true);

        request.getSession().removeAttribute(PUBLIC_WORKS_RESULTS_ATTRIBUTE);
        OrcidProfile profile = orcidProfileManager.retrievePublicOrcidProfile(orcid);

        mav.addObject("profile", profile);

        List<Work> works = new ArrayList<Work>();
        List<String> workIds = new ArrayList<String>();
        HashMap<String, Work> worksMap = new HashMap<String, Work>();
        
        List<Affiliation> affilations = new ArrayList<Affiliation>();
        List<String> affiliationIds = new ArrayList<String>();

        if (profile.getOrcidDeprecated() != null) {
            String primaryRecord = profile.getOrcidDeprecated().getPrimaryRecord().getOrcid().getValue();
            mav.addObject("deprecated", true);
            mav.addObject("primaryRecord", primaryRecord);
        } else {
        	
        	if (profile.getOrcidActivities() != null && profile.getOrcidActivities().getOrcidWorks() != null) {
                for (OrcidWork orcidWork : profile.getOrcidActivities().getOrcidWorks().getOrcidWork()) {
                	if(Visibility.PUBLIC.equals(orcidWork.getVisibility())) {
                		String workId = orcidWork.getPutCode();
                		Work work = Work.minimizedValueOf(orcidWork); 
                		works.add(work);
                		workIds.add(workId);
                		worksMap.put(workId, work);
                	}
                }
                if (!works.isEmpty()) {
                    mav.addObject("works", works);
                    request.getSession().setAttribute(WORKS_MAP, worksMap);
                }
            }        	        	
        	
        	if (profile.getOrcidActivities() != null && profile.getOrcidActivities().getAffiliations() != null) {
                for (Affiliation affiliation : profile.getOrcidActivities().getAffiliations().getAffiliation()) {
                    affilations.add(affiliation);
                    affiliationIds.add(affiliation.getPutCode());
                }
                if (!affilations.isEmpty()) {
                    mav.addObject("affilations", affilations);
                }
            }
        }
        ObjectMapper mapper = new ObjectMapper();

        try {
            String worksIdsJson = mapper.writeValueAsString(workIds);
            String affiliationIdsJson = mapper.writeValueAsString(affiliationIds);
            mav.addObject("workIdsJson", StringEscapeUtils.escapeEcmaScript(worksIdsJson));
            mav.addObject("affiliationIdsJson", StringEscapeUtils.escapeEcmaScript(affiliationIdsJson));
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
        OrcidProfile profile = orcidProfileManager.retrievePublicOrcidProfile(orcid);
        Map<String, Affiliation> affMap = profile.getOrcidActivities().getAffiliations().retrieveAffiliationAsMap();
        String[] affIds = workIdsStr.split(",");
        for (String id: affIds) {
            Affiliation aff = affMap.get(id);
            // ONLY SHARE THE PUBLIC AFFILIATIONS! 
            if (aff != null && aff.getVisibility().equals(Visibility.PUBLIC)) {
                affs.add(AffiliationForm.valueOf(aff));
            }
        }
        return affs;
    }

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/works.json")
    public @ResponseBody
    List<Work> getWorkJson(HttpServletRequest request, @PathVariable("orcid") String orcid, @RequestParam(value = "workIds") String workIdsStr) {
        Map<String, String> countries = retrieveIsoCountries();
        Map<String, String> languages = LanguagesMap.buildLanguageMap(localeManager.getLocale(), false);
                              
        List<Work> works = new ArrayList<Work>();
        String[] workIds = workIdsStr.split(",");        
        HashMap<String, Work> worksMap = (HashMap<String, Work>) request.getSession().getAttribute(WORKS_MAP);
        
        for(String workId : workIds) {
        	if(worksMap.containsKey(workId)) {
        		Work work = worksMap.get(workId);
	        	if(Visibility.PUBLIC.equals(work.getVisibility())) {
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
	                if (!(work.getWorkTitle().getTranslatedTitle() == null) && !StringUtils.isEmpty(work.getWorkTitle().getTranslatedTitle().getLanguageCode())) {
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
     * @param workId The id of the work
     * @return the content of that work
     * */
    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/getWorkInfo.json", method = RequestMethod.GET)
    public @ResponseBody
    Work getWorkInfo(@PathVariable("orcid") String orcid, @RequestParam(value = "workId") String workId) {
    	Map<String, String> countries = retrieveIsoCountries();
        Map<String, String> languages = LanguagesMap.buildLanguageMap(localeManager.getLocale(), false);
    	if(StringUtils.isEmpty(workId))
    		return null;    	    
    	
    	ProfileWorkEntity profileWork = profileWorkManager.getProfileWork(orcid, workId);
    	
    	if(profileWork != null){    	
    		OrcidWork orcidWork = jpa2JaxbAdapter.getOrcidWork(profileWork);
    		if(orcidWork != null) {
				Work work = Work.valueOf(orcidWork);
				//Set country name
		        if(!PojoUtil.isEmpty(work.getCountryCode())) {            
		            Text countryName = Text.valueOf(countries.get(work.getCountryCode().getValue()));
		            work.setCountryName(countryName);
		        }
		        //Set language name
		        if(!PojoUtil.isEmpty(work.getLanguageCode())) {
		            Text languageName = Text.valueOf(languages.get(work.getLanguageCode().getValue()));
		            work.setLanguageName(languageName);
		        }
		        //Set translated title language name
		        if(!(work.getWorkTitle().getTranslatedTitle() == null) && !StringUtils.isEmpty(work.getWorkTitle().getTranslatedTitle().getLanguageCode())) {
		            String languageName = languages.get(work.getWorkTitle().getTranslatedTitle().getLanguageCode());
		            work.getWorkTitle().getTranslatedTitle().setLanguageName(languageName);
		        }
		        
		        return work;
    		}
        }
    	    	    	
    	return null;
    }        
}