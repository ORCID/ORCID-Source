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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.core.manager.ThirdPartyImportManager;
import org.orcid.frontend.web.forms.CurrentWork;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.SourceOrcid;
import org.orcid.persistence.adapter.Jpa2JaxbAdapter;
import org.orcid.pojo.ThirdPartyRedirect;
import org.orcid.pojo.ajaxForm.Work;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

/**
 * @author Will Simpson
 */
@Controller("workspaceController")
@RequestMapping(value = { "/my-orcid", "/workspace" })
public class WorkspaceController extends BaseWorkspaceController {

    private static final String WORKS_MAP = "WORKS_MAP";

    @Resource
    private ThirdPartyImportManager thirdPartyImportManager;

    @Resource
    private ExternalIdentifierManager externalIdentifierManager;

    @Resource
    private ProfileWorkManager profileWorkManager;

    @Resource
    private Jpa2JaxbAdapter jpa2JaxbAdapter;

    @ModelAttribute("thirdPartiesForImport")
    public List<OrcidClient> retrieveThirdPartiesForImport() {
        return thirdPartyImportManager.findOrcidClientsWithPredefinedOauthScopeWorksImport();
    }

    @RequestMapping
    public ModelAndView viewWorkspace(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "maxResults", defaultValue = "200") int maxResults) {
        ModelAndView mav = new ModelAndView("workspace");
        mav.addObject("showPrivacy", true);

        OrcidProfile profile = getCurrentUserAndRefreshIfNecessary().getEffectiveProfile();
        getCurrentUser().setEffectiveProfile(profile);
        List<CurrentWork> currentWorks = getCurrentWorksFromProfile(profile);
        if (currentWorks != null && !currentWorks.isEmpty()) {
            mav.addObject("currentWorks", currentWorks);
        }
        mav.addObject("profile", profile);
        return mav;
    }

    @RequestMapping(value = { "/public", "/preview" })
    public ModelAndView preview(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "maxResults", defaultValue = "200") int maxResults) {
        ModelAndView mav = new ModelAndView("public_profile");
        mav.addObject("isPreview", true);

        OrcidProfile profile = orcidProfileManager.retrievePublicOrcidProfile(getCurrentUserOrcid());
        request.getSession().removeAttribute(PUBLIC_WORKS_RESULTS_ATTRIBUTE);
        List<CurrentWork> currentWorks = getCurrentWorksFromProfile(profile);
        if (currentWorks != null && !currentWorks.isEmpty()) {
            mav.addObject("currentWorks", currentWorks);
        }
        mav.addObject("profile", profile);
        return mav;
    }

    /**
     * Retrieve all external identifiers as a json string
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/externalIdentifiers.json", method = RequestMethod.GET)
    public @ResponseBody
    org.orcid.pojo.ExternalIdentifiers getExternalIdentifiersJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
        org.orcid.pojo.ExternalIdentifiers externalIdentifiers = new org.orcid.pojo.ExternalIdentifiers();
        externalIdentifiers.setExternalIdentifiers((List<org.orcid.pojo.ExternalIdentifier>) (Object) currentProfile.getOrcidBio().getExternalIdentifiers()
                .getExternalIdentifier());
        return externalIdentifiers;
    }

    @RequestMapping(value = "/sourceGrantReadWizard.json", method = RequestMethod.GET)
    public @ResponseBody
    ThirdPartyRedirect getSourceGrantReadWizard() {
        ThirdPartyRedirect tpr = new ThirdPartyRedirect();

        OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
        if (currentProfile.getOrcidHistory().getSource() == null)
            return tpr;
        SourceOrcid sourceOrcid = currentProfile.getOrcidHistory().getSource().getSourceOrcid();
        String sourcStr = sourceOrcid.getValue();
        List<OrcidClient> orcidClients = thirdPartyImportManager.findOrcidClientsWithPredefinedOauthScopeReadAccess();
        for (OrcidClient orcidClient : orcidClients) {
            if (sourcStr.equals(orcidClient.getClientId())) {
                RedirectUri ru = orcidClient.getRedirectUris().getRedirectUri().get(0);
                String redirect = getBaseUri() + "/oauth/authorize?client_id=" + orcidClient.getClientId() + "&response_type=code&scope=" + ru.getScopeAsSingleString()
                        + "&redirect_uri=" + ru.getValue();
                tpr.setUrl(redirect);
                tpr.setDisplayName(orcidClient.getDisplayName());
                tpr.setShortDescription(orcidClient.getShortDescription());
                return tpr;
            }
        }
        return tpr;
    }

    /**
     * Updates the list of external identifiers assigned to a user
     * */
    @RequestMapping(value = "/externalIdentifiers.json", method = RequestMethod.DELETE)
    public @ResponseBody
    org.orcid.pojo.ExternalIdentifier removeExternalIdentifierJson(HttpServletRequest request, @RequestBody org.orcid.pojo.ExternalIdentifier externalIdentifier) {
        List<String> errors = new ArrayList<String>();

        // If the orcid is blank, add an error
        if (externalIdentifier.getOrcid() == null || StringUtils.isBlank(externalIdentifier.getOrcid().getValue())) {
            errors.add(getMessage("ExternalIdentifier.orcid"));
        }

        // If the external identifier is blank, add an error
        if (externalIdentifier.getExternalIdReference() == null || StringUtils.isBlank(externalIdentifier.getExternalIdReference().getContent())) {
            errors.add(getMessage("ExternalIdentifier.externalIdReference"));
        }
        // Set errors to the external 
        externalIdentifier.setErrors(errors);

        if (errors.isEmpty()) {
            //Get cached profile
            OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
            ExternalIdentifiers externalIdentifiers = currentProfile.getOrcidBio().getExternalIdentifiers();
            List<ExternalIdentifier> externalIdentifiersList = externalIdentifiers.getExternalIdentifier();
            Iterator<ExternalIdentifier> externalIdentifierIterator = externalIdentifiersList.iterator();
            //Remove external identifier from the cached profile
            while (externalIdentifierIterator.hasNext()) {
                ExternalIdentifier existingExternalIdentifier = externalIdentifierIterator.next();
                if (existingExternalIdentifier.equals(externalIdentifier)) {
                    externalIdentifierIterator.remove();
                }
            }
            //Update cached profile
            currentProfile.getOrcidBio().setExternalIdentifiers(externalIdentifiers);
            //Remove external identifier
            externalIdentifierManager.removeExternalIdentifier(externalIdentifier.getOrcid().getValue(), externalIdentifier.getExternalIdReference().getContent());
        }

        return externalIdentifier;
    }

    /**
     * Removes a work from a profile
     * */
    @RequestMapping(value = "/works.json", method = RequestMethod.DELETE)
    public @ResponseBody
    Work removeWorkJson(HttpServletRequest request, @RequestBody Work work) {
        //Get cached profile
        OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
        OrcidWorks works = currentProfile.getOrcidActivities() == null ? null : currentProfile.getOrcidActivities().getOrcidWorks();
        Work deletedWork = new Work();
        if (works != null) {
            List<OrcidWork> workList = works.getOrcidWork();
            Iterator<OrcidWork> workIterator = workList.iterator();
            while (workIterator.hasNext()) {
                OrcidWork orcidWork = workIterator.next();
                if (work.equals(orcidWork)) {
                    workIterator.remove();
                    deletedWork = work;
                }
            }
            works.setOrcidWork(workList);
            currentProfile.getOrcidActivities().setOrcidWorks(works);
            profileWorkManager.removeWork(currentProfile.getOrcid().getValue(), work.getPutCode().getValue());
        }

        return deletedWork;
    }

    /**
     * List works associated with a profile
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/work.json", method = RequestMethod.GET)
    public @ResponseBody List<Work> getWorkJson(HttpServletRequest request, @RequestParam(value = "workIds") String workIdsStr) {
        List<Work> workList = new ArrayList<>();
        Work work = null;
        String[] workIds = workIdsStr.split(",");
        
        if(workIds != null){
            HashMap<String,Work> worksMap = (HashMap<String,Work>)request.getSession().getAttribute(WORKS_MAP);
            // this should never happen, but just in case.
            if (worksMap == null) {
                createWorksIdList(request);
                worksMap = (HashMap<String,Work>)request.getSession().getAttribute(WORKS_MAP);
            }
            for (String workId: workIds) {
                work = worksMap.get(workId);
                workList.add(work);
            }
        }
        
        return workList;
    }

    /**
     * List works associated with a profile
     * */
    @RequestMapping(value = "/works.json", method = RequestMethod.GET)
    public @ResponseBody
    List<String> getWorksJson(HttpServletRequest request) {
        //Get cached profile
        List<String> workIds = createWorksIdList(request);
        return workIds;
    }

    /**
     * created a work id list and sorts a map associated with the list in
     * in the session
     *
     */
    private List<String> createWorksIdList(HttpServletRequest request) {
        OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
        OrcidWorks orcidWorks = currentProfile.getOrcidActivities() == null ? null : currentProfile.getOrcidActivities().getOrcidWorks();

        HashMap<String, Work> worksMap = new HashMap<String, Work>();
        List<String> workIds = new ArrayList<String>();
        if (orcidWorks != null) {
            for (OrcidWork work : orcidWorks.getOrcidWork()) {
                worksMap.put(work.getPutCode(), new Work(work));
                workIds.add(work.getPutCode());
            }
            request.getSession().setAttribute(WORKS_MAP, worksMap);
        }
        return workIds;
    }

    /**
     * List works associated with a profile
     * */
    @RequestMapping(value = "/profileWork.json", method = RequestMethod.PUT)
    public @ResponseBody
    Work updateProfileWorkJson(HttpServletRequest request, @RequestBody Work work) {
        //Get cached profile
        OrcidWork ow = work.toOrcidWork();
        OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
        OrcidWorks orcidWorks = currentProfile.getOrcidActivities() == null ? null : currentProfile.getOrcidActivities().getOrcidWorks();
        if (orcidWorks != null) {
            List<OrcidWork> orcidWorksList = orcidWorks.getOrcidWork();
            if (orcidWorksList != null) {
                for (OrcidWork orcidWork : orcidWorksList) {
                    //If the put codes are equal, we know that they are the same work
                    if (orcidWork.getPutCode().equals(ow.getPutCode())) {
                        //Update the privacy of the work
                        profileWorkManager.updateWork(currentProfile.getOrcid().getValue(), ow.getPutCode(), ow.getVisibility());
                    }
                }
            }
        }
        return work;
    }
}