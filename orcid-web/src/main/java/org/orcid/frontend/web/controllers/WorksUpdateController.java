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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.crossref.CrossRefMetadata;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.frontend.web.forms.CurrentWork;
import org.orcid.frontend.web.forms.CurrentWorkContributor;
import org.orcid.frontend.web.forms.CurrentWorksForm;
import org.orcid.frontend.web.util.NumberList;
import org.orcid.frontend.web.util.YearsList;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 22/02/2012
 */
@Controller("worksController")
public class WorksUpdateController extends BaseWorkspaceController {

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @ModelAttribute("workTypes")
    public Map<String, String> retrieveWorkTypesAsMap() {
        Map<String, String> workTypes = new TreeMap<String, String>();
        workTypes.put("", "Pick a publication type");
        for (WorkType workType : WorkType.values()) {
            workTypes.put(workType.value(), StringUtils.capitalize(workType.value().replace('-', ' ')));
        }

        workTypes.remove(WorkType.BIBLE.value());
        return workTypes;
    }

    @ModelAttribute("citationTypes")
    public Map<String, String> retrieveTypesAsMap() {
        Map<String, String> citationTypes = new TreeMap<String, String>();
        citationTypes.put("", "Pick a citation type");
        for (CitationType citationType : CitationType.values()) {
            String value = citationType.value().replace("formatted-", "");
            citationTypes.put(citationType.value(), StringUtils.upperCase(value));
        }
        return citationTypes;
    }

    @ModelAttribute("years")
    public Map<String, String> retrieveYearsAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = YearsList.createList();
        map.put("", "Year");
        for (String year : list) {
            map.put(year, year);
        }
        return map;
    }

    @ModelAttribute("months")
    public Map<String, String> retrieveMonthsAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = NumberList.createList(12);
        map.put("", "Month");
        for (String month : list) {
            map.put(month, month);
        }
        return map;
    }

    @ModelAttribute("days")
    public Map<String, String> retrieveDaysAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = NumberList.createList(31);
        map.put("", "Day");
        for (String day : list) {
            map.put(day, day);
        }
        return map;
    }

    @ModelAttribute("idTypes")
    public Map<String, String> retrieveIdTypesAsMap() {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("", "What type of external ID?");
        for (WorkExternalIdentifierType type : WorkExternalIdentifierType.values()) {
            map.put(type.value(), type.description());
        }
        return map;
    }

    @ModelAttribute("roles")
    public Map<String, String> retrieveRolesAsMap() {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("", "What was your role?");
        for (ContributorRole contributorRole : ContributorRole.values()) {
            map.put(contributorRole.value(), StringUtils.capitalize(contributorRole.value().replaceAll("[-_]", " ")));
        }
        return map;
    }

    @ModelAttribute("sequences")
    public Map<String, String> retrieveSequencesAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (SequenceType sequenceType : SequenceType.values()) {
            map.put(sequenceType.value(), StringUtils.capitalize(sequenceType.value().replaceAll("[-]", " ")));
        }
        return map;
    }

    @RequestMapping(value = "/works-update", method = RequestMethod.GET)
    public ModelAndView viewWorks() {
        ModelAndView mav = createWorksUpdateModelAndView();
        return mav;
    }

    ModelAndView createWorksUpdateModelAndView() {
        ModelAndView mav = new ModelAndView("works_update");
        OrcidProfile currentUser = orcidProfileManager.retrieveOrcidProfile(getCurrentUserOrcid());
        getCurrentUser().setEffectiveProfile(currentUser);
        mav.addObject(new CurrentWorksForm(currentUser));
        String searchText = getSearchTerms(currentUser);
        List<CrossRefMetadata> metadatas = null;
        try {
            metadatas = crossRefManager.searchForMetadata(searchText);
            mav.addObject("searchAndAddForm", new CurrentWorksForm(metadatas));
        } catch (Exception e) {
            mav.addObject("searchAndAddFormError", "Yikes");
        }
        mav.addObject("manualWork", new CurrentWork());
        mav.addObject("workVisibilityDefault", currentUser.getOrcidInternal().getPreferences().getWorkVisibilityDefault().getValue().value());

        return mav;
    }

    private String getSearchTerms(OrcidProfile currentUser) {
        List<String> searchTerms = new ArrayList<String>(10);
        PersonalDetails personalDetails = currentUser.getOrcidBio().getPersonalDetails();
        CreditName creditName = personalDetails.getCreditName();
        if (creditName != null) {
            searchTerms.add(creditName.getContent());
        }
        GivenNames givenNames = personalDetails.getGivenNames();
        if (givenNames != null) {
            searchTerms.add(givenNames.getContent());
        }
        FamilyName familyName = personalDetails.getFamilyName();
        if (familyName != null) {
            searchTerms.add(familyName.getContent());
        }
        return StringUtils.join(searchTerms, " ");
    }

    @RequestMapping(value = "/search-and-add-works", method = RequestMethod.POST)
    public String searchAndAddWorks(HttpServletRequest request, @ModelAttribute("searchAndAddForm") CurrentWorksForm currentWorksForm) {
        OrcidProfile worksProfile = currentWorksForm.getOrcidProfileWithSelectedOnly(getCurrentUserOrcid());
        OrcidProfile updatedProfile = orcidProfileManager.addOrcidWorks(worksProfile);
        getCurrentUser().setEffectiveProfile(updatedProfile);
        request.getSession().removeAttribute(WORKS_RESULTS_ATTRIBUTE);
        request.getSession().removeAttribute(PUBLIC_WORKS_RESULTS_ATTRIBUTE);
        return "redirect:/works-update";
    }

    @RequestMapping(value = "/save-current-works", method = RequestMethod.POST)
    public String saveCurrentWorks(HttpServletRequest request, @ModelAttribute CurrentWorksForm currentWorksForm) {
        sanitizeCurrentWorks(currentWorksForm.getCurrentWorks());
        OrcidProfile worksProfile = currentWorksForm.getOrcidProfile(getCurrentUserOrcid());
        OrcidProfile updatedProfile = orcidProfileManager.updateOrcidWorks(worksProfile);
        getCurrentUser().setEffectiveProfile(updatedProfile);
        request.getSession().removeAttribute(WORKS_RESULTS_ATTRIBUTE);
        request.getSession().removeAttribute(PUBLIC_WORKS_RESULTS_ATTRIBUTE);
        return "redirect:/works-update";
    }

    /**
     * Helper for saving a work manually. Doesn't actually save the work!
     */
    @RequestMapping(value = "/save-work-manually", method = RequestMethod.POST)
    public ModelAndView saveWorkManually(HttpServletRequest request, @ModelAttribute("manualWork") @Valid CurrentWork manualWork, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("manual_work_form_contents");
            mav.addAllObjects(bindingResult.getModel());
            return mav;
        }
        OrcidProfile currentUser = getCurrentUser().getEffectiveProfile();
        String currentUserOrcid = currentUser.getOrcid().getValue();
        CreditName currentUserCreditName = currentUser.getOrcidBio().getPersonalDetails().getCreditName();
        List<CurrentWorkContributor> currentWorkContributors = manualWork.getCurrentWorkContributors();
        if (currentWorkContributors != null) {
            for (CurrentWorkContributor currentWorkContributor : currentWorkContributors) {
                currentWorkContributor.setOrcid(currentUserOrcid);
                if (currentUserCreditName != null) {
                    currentWorkContributor.setCreditName(currentUserCreditName.getContent());
                }
            }
        }
        CurrentWorksForm currentWorksForm = new CurrentWorksForm();
        List<CurrentWork> currentWorksList = new ArrayList<CurrentWork>(1);
        currentWorksForm.setCurrentWorks(currentWorksList);
        currentWorksList.add(manualWork);
        // Just contains the added work not all works in the current list!
        ModelAndView mav = new ModelAndView("current_works_list");
        mav.addObject("showPrivacy", false);
        mav.addObject(currentWorksForm);
        return mav;
    }

    private void sanitizeCurrentWorks(List<CurrentWork> currentWorks) {
        if (currentWorks != null && !currentWorks.isEmpty()) {
            Iterator<CurrentWork> iterator = currentWorks.iterator();
            while (iterator.hasNext()) {
                CurrentWork next = iterator.next();
                if (!next.hasRequiredFormFields()) {
                    iterator.remove();
                }
            }
        }
    }

}
