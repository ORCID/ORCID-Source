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

import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author rcpeters
 */
@Controller("affiliationsController")
@RequestMapping(value = { "/affiliations" })
public class AffiliationsController extends BaseWorkspaceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AffiliationsController.class);

    private static final String AFFILIATIONS_MAP = "AFFILIATIONS_MAP";

    @Resource
    private Jpa2JaxbAdapter jpa2JaxbAdapter;

    @Resource
    private Jaxb2JpaAdapter jaxb2JpaAdapter;

    @Resource
    private OrgAffiliationRelationDao orgRelationAffiliationDao;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private OrgAffiliationRelationDao orgAffiliationRelationDao;

    /**
     * Removes a affiliation from a profile
     * */
    @RequestMapping(value = "/affiliations.json", method = RequestMethod.DELETE)
    public @ResponseBody
    Affiliation removeAffiliationJson(HttpServletRequest request, @RequestBody Affiliation affiliation) {

        // Get cached profile
        OrcidProfile currentProfile = getEffectiveProfile();
        Affiliations affiliations = currentProfile.getOrcidActivities() == null ? null : currentProfile.getOrcidActivities().getAffiliations();
        Affiliation deletedAffiliation = new Affiliation();
        if (affiliations != null) {
            List<Affiliation> affiliationList = affiliations.getAffiliation();
            Iterator<Affiliation> affiliationIterator = affiliationList.iterator();
            while (affiliationIterator.hasNext()) {
                Affiliation orcidAffiliation = affiliationIterator.next();
                if (affiliation.equals(orcidAffiliation)) {
                    affiliationIterator.remove();
                    deletedAffiliation = affiliation;
                }
            }
            currentProfile.getOrcidActivities().setAffiliations(affiliations);
            orgRelationAffiliationDao.removeOrgAffiliationRelation(currentProfile.getOrcid().getValue(), affiliation.getPutCode());
        }

        return deletedAffiliation;
    }

    /**
     * List affiliations associated with a profile
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/affiliations.json", method = RequestMethod.GET)
    public @ResponseBody
    List<Affiliation> getAffiliationJson(HttpServletRequest request, @RequestParam(value = "affiliationIds") String affiliationIdsStr) {
        List<Affiliation> affiliationList = new ArrayList<>();
        Affiliation affiliation = null;
        String[] affiliationIds = affiliationIdsStr.split(",");

        if (affiliationIds != null) {
            HashMap<String, Affiliation> affiliationsMap = (HashMap<String, Affiliation>) request.getSession().getAttribute(AFFILIATIONS_MAP);
            // this should never happen, but just in case.
            if (affiliationsMap == null) {
                createAffiliationsIdList(request);
                affiliationsMap = (HashMap<String, Affiliation>) request.getSession().getAttribute(AFFILIATIONS_MAP);
            }
            for (String affiliationId : affiliationIds) {
                affiliation = affiliationsMap.get(affiliationId);
                affiliationList.add(affiliation);
            }
        }

        return affiliationList;
    }

    /**
     * Returns a blank affiliation form
     * */
    @RequestMapping(value = "/affiliation.json", method = RequestMethod.GET)
    public @ResponseBody
    org.orcid.pojo.ajaxForm.Affiliation getAffiliation(HttpServletRequest request) {
        org.orcid.pojo.ajaxForm.Affiliation affiliationForm = new org.orcid.pojo.ajaxForm.Affiliation();

        OrcidProfile profile = getEffectiveProfile();
        org.orcid.pojo.ajaxForm.Visibility v = org.orcid.pojo.ajaxForm.Visibility.valueOf(profile.getOrcidInternal().getPreferences().getWorkVisibilityDefault()
                .getValue());
        affiliationForm.setVisibility(v);

        Text affiliationName = new Text();
        affiliationForm.setAffiliationName(affiliationName);
        affiliationName.setRequired(true);

        Text city = new Text();
        affiliationForm.setCity(city);

        Text region = new Text();
        affiliationForm.setRegion(region);

        Text country = new Text();
        affiliationForm.setCountry(country);
        country.setValue("");
        country.setRequired(true);

        Text department = new Text();
        affiliationForm.setDepartment(department);

        Text affiliationType = new Text();
        affiliationForm.setAffiliationType(affiliationType);
        affiliationType.setValue("");

        Date startDate = new Date();
        affiliationForm.setStartDate(startDate);
        startDate.setDay("");
        startDate.setMonth("");
        startDate.setYear("");

        Date endDate = new Date();
        affiliationForm.setEndDate(endDate);
        endDate.setDay("");
        endDate.setMonth("");
        endDate.setYear("");

        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation.json", method = RequestMethod.POST)
    public @ResponseBody
    org.orcid.pojo.ajaxForm.Affiliation postAffiliation(HttpServletRequest request, @RequestBody org.orcid.pojo.ajaxForm.Affiliation affiliationForm) {
        // Validate
        affiliationNameValidate(affiliationForm);
        cityValidate(affiliationForm);
        regionValidate(affiliationForm);
        countryValidate(affiliationForm);
        departmentValidate(affiliationForm);

        copyErrors(affiliationForm.getAffiliationName(), affiliationForm);
        copyErrors(affiliationForm.getCity(), affiliationForm);
        copyErrors(affiliationForm.getRegion(), affiliationForm);
        copyErrors(affiliationForm.getCountry(), affiliationForm);
        copyErrors(affiliationForm.getDepartment(), affiliationForm);

        if (affiliationForm.getErrors().isEmpty()) {
            // Persist to DB
            OrgAffiliationRelationEntity orgAffiliationRelationEntity = jaxb2JpaAdapter.getNewOrgAffiliationRelationEntity(affiliationForm.toAffiliation(),
                    profileDao.find(getEffectiveUserOrcid()));
            orgAffiliationRelationDao.persist(orgAffiliationRelationEntity);
        }

        return affiliationForm;
    }

    /**
     * List affiliations associated with a profile
     * */
    @RequestMapping(value = "/affiliationIds.json", method = RequestMethod.GET)
    public @ResponseBody
    List<String> getAffiliationsJson(HttpServletRequest request) {
        // Get cached profile
        List<String> affiliationIds = createAffiliationsIdList(request);
        return affiliationIds;
    }

    /**
     * Create an affiliation id list and sorts a map associated with the list in
     * in the session
     * 
     */
    private List<String> createAffiliationsIdList(HttpServletRequest request) {
        OrcidProfile currentProfile = getEffectiveProfile();
        Affiliations orcidAffiliations = currentProfile.getOrcidActivities() == null ? null : currentProfile.getOrcidActivities().getAffiliations();

        HashMap<String, Affiliation> affiliationsMap = new HashMap<String, Affiliation>();
        List<String> affiliationIds = new ArrayList<String>();
        if (orcidAffiliations != null) {
            for (Affiliation affiliation : orcidAffiliations.getAffiliation()) {
                try {
                    affiliationsMap.put(affiliation.getPutCode(), affiliation);
                    affiliationIds.add(affiliation.getPutCode());
                } catch (Exception e) {
                    LOGGER.error("Failed to parse as Affiliation. Put code" + affiliation.getPutCode());
                }
            }
            request.getSession().setAttribute(AFFILIATIONS_MAP, affiliationsMap);
        }
        return affiliationIds;
    }

    /**
     * Saves an affiliation
     * */
    @RequestMapping(value = "/affiliation.json", method = RequestMethod.PUT)
    public @ResponseBody
    Affiliation updateProfileAffiliationJson(HttpServletRequest request, @RequestBody Affiliation affiliation) {
        // Get cached profile
        OrcidProfile currentProfile = getEffectiveProfile();
        Affiliations orcidAffiliations = currentProfile.getOrcidActivities() == null ? null : currentProfile.getOrcidActivities().getAffiliations();
        if (orcidAffiliations != null) {
            List<Affiliation> orcidAffiliationsList = orcidAffiliations.getAffiliation();
            if (orcidAffiliationsList != null) {
                for (Affiliation orcidAffiliation : orcidAffiliationsList) {
                    // If the put codes are equal, we know that they are the
                    // same affiliation
                    if (orcidAffiliation.getPutCode().equals(affiliation.getPutCode())) {
                        // Update the privacy of the affiliation
                        orgRelationAffiliationDao.updateOrgAffiliationRelation(currentProfile.getOrcid().getValue(), affiliation.getPutCode(),
                                affiliation.getVisibility());
                    }
                }
            }
        }
        return affiliation;
    }

    @RequestMapping(value = "/affiliation/affiliationNameValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    org.orcid.pojo.ajaxForm.Affiliation affiliationNameValidate(@RequestBody org.orcid.pojo.ajaxForm.Affiliation affiliationForm) {
        affiliationForm.getAffiliationName().setErrors(new ArrayList<String>());
        if (affiliationForm.getAffiliationName().getValue() == null || affiliationForm.getAffiliationName().getValue().trim().length() == 0) {
            setError(affiliationForm.getAffiliationName(), "NotBlank.manualAffiliation.name");
        } else {
            if (affiliationForm.getAffiliationName().getValue().trim().length() > 1000) {
                setError(affiliationForm.getAffiliationName(), "manualAffiliation.length_less_1000");
            }
        }
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/cityValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    org.orcid.pojo.ajaxForm.Affiliation cityValidate(@RequestBody org.orcid.pojo.ajaxForm.Affiliation affiliationForm) {
        affiliationForm.getCity().setErrors(new ArrayList<String>());
        if (affiliationForm.getCity().getValue() == null || affiliationForm.getCity().getValue().trim().length() == 0) {
            setError(affiliationForm.getCity(), "NotBlank.manualAffiliation.city");
        } else {
            if (affiliationForm.getCity().getValue().trim().length() > 1000) {
                setError(affiliationForm.getCity(), "manualAffiliation.length_less_1000");
            }
        }
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/regionValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    org.orcid.pojo.ajaxForm.Affiliation regionValidate(@RequestBody org.orcid.pojo.ajaxForm.Affiliation affiliationForm) {
        affiliationForm.getRegion().setErrors(new ArrayList<String>());
        if (affiliationForm.getRegion().getValue() != null && affiliationForm.getRegion().getValue().trim().length() > 1000) {
            setError(affiliationForm.getRegion(), "manualAffiliation.length_less_1000");
        }
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/countryValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    org.orcid.pojo.ajaxForm.Affiliation countryValidate(@RequestBody org.orcid.pojo.ajaxForm.Affiliation affiliationForm) {
        affiliationForm.getCountry().setErrors(new ArrayList<String>());
        if (affiliationForm.getCountry().getValue() == null || affiliationForm.getCountry().getValue().trim().length() == 0) {
            setError(affiliationForm.getCountry(), "NotBlank.manualAffiliation.country");
        }
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/departmentValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    org.orcid.pojo.ajaxForm.Affiliation departmentValidate(@RequestBody org.orcid.pojo.ajaxForm.Affiliation affiliationForm) {
        affiliationForm.getDepartment().setErrors(new ArrayList<String>());
        if (affiliationForm.getDepartment().getValue() != null && affiliationForm.getDepartment().getValue().trim().length() > 1000) {
            setError(affiliationForm.getDepartment(), "manualAffiliation.length_less_1000");
        }
        return affiliationForm;
    }

}