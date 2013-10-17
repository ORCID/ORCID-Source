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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.orcid.frontend.web.forms.CurrentWork;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
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

    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}")
    public ModelAndView publicPreview(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "maxResults", defaultValue = "15") int maxResults, @PathVariable("orcid") String orcid) {
        ModelAndView mav = new ModelAndView("public_profile");
        mav.addObject("isPublicProfile", true);
        
        request.getSession().removeAttribute(PUBLIC_WORKS_RESULTS_ATTRIBUTE);
        OrcidProfile profile = orcidProfileManager.retrievePublicOrcidProfile(orcid);
        
        mav.addObject("profile", profile);
        
        if(profile.getOrcidDeprecated() != null){
            String primaryRecord = profile.getOrcidDeprecated().getPrimaryRecord().getOrcid().getValue();
            mav.addObject("deprecated", true);
            mav.addObject("primaryRecord", primaryRecord);
        } else if (profile.getOrcidActivities()!= null && profile.getOrcidActivities().getOrcidWorks() != null) {             
            List<Work> works = new ArrayList<Work>();
            for (OrcidWork orcidWork: profile.getOrcidActivities().getOrcidWorks().getOrcidWork()) {
                works.add(Work.valueOf(orcidWork));
            }
            if (!works.isEmpty()) {
                mav.addObject("works", works);
                ObjectMapper mapper = new ObjectMapper();
                try {
                    String worksJson = mapper.writeValueAsString(works);
                    mav.addObject("worksJson", StringEscapeUtils.escapeEcmaScript(worksJson));
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
            }
        }        
        
        return mav;
    }
    
    
//    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/works.json")
//    public @ResponseBody
//    List<Work> getWorkJson(HttpServletRequest request, @PathVariable("orcid") String orcid, @RequestParam(value = "workIds") String workIdsStr) {
//        OrcidProfile profile = orcidProfileManager.retrievePublicOrcidProfile(orcid);
//        List<Work> workList = new ArrayList<>();
//        List<String> workIds = Arrays.asList(workIdsStr.split(","));
//        for (OrcidWork orcidWork: profile.getOrcidActivities().getOrcidWorks().getOrcidWork()) {
//            if (workIds.contains(orcidWork.getPutCode())) workList.add(Work.valueOf(orcidWork));
//        }        
//        return workList;
//    }
//    
//    /**
//     * List works ids associated with a profile
//     * */
//    @RequestMapping(value = "/{orcid:(?:\\d{4}-){3,}\\d{3}[\\dX]}/workIds.json", method = RequestMethod.GET)
//    public @ResponseBody
//    List<String> getWorksJson(HttpServletRequest request, @PathVariable("orcid") String orcid) {
//        // Get cached profile
//        OrcidProfile profile = orcidProfileManager.retrievePublicOrcidProfile(orcid);
//
//        List<String> workIds = new ArrayList<>();
//        for (OrcidWork orcidWork: profile.getOrcidActivities().getOrcidWorks().getOrcidWork()) {
//            workIds.add(orcidWork.getPutCode());
//        }        
//        return workIds;
//    }

}
