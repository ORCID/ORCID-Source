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
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.AffiliationForm;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.Visibility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Resource
    private OrgDisambiguatedDao orgDisambiguatedDao;

    /**
     * Removes a affiliation from a profile
     * */
    @RequestMapping(value = "/affiliations.json", method = RequestMethod.DELETE)
    public @ResponseBody
    AffiliationForm removeAffiliationJson(HttpServletRequest request, @RequestBody AffiliationForm affiliation) {

        // Get cached profile
        OrcidProfile currentProfile = getEffectiveProfile();
        Affiliations affiliations = currentProfile.getOrcidActivities() == null ? null : currentProfile.getOrcidActivities().getAffiliations();
        if (affiliations != null) {
            List<Affiliation> affiliationList = affiliations.getAffiliation();
            Iterator<Affiliation> affiliationIterator = affiliationList.iterator();
            while (affiliationIterator.hasNext()) {
                Affiliation orcidAffiliation = affiliationIterator.next();
                if (affiliation.getPutCode().getValue().equals(orcidAffiliation.getPutCode())) {
                    affiliationIterator.remove();
                }
            }
            currentProfile.getOrcidActivities().setAffiliations(affiliations);
            orgRelationAffiliationDao.removeOrgAffiliationRelation(currentProfile.getOrcid().getValue(), affiliation.getPutCode().getValue());
        }

        return affiliation;
    }

    /**
     * List affiliations associated with a profile
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/affiliations.json", method = RequestMethod.GET)
    public @ResponseBody
    List<AffiliationForm> getAffiliationJson(HttpServletRequest request, @RequestParam(value = "affiliationIds") String affiliationIdsStr) {
        List<AffiliationForm> affiliationList = new ArrayList<>();
        AffiliationForm affiliation = null;
        String[] affiliationIds = affiliationIdsStr.split(",");

        if (affiliationIds != null) {
            HashMap<String, AffiliationForm> affiliationsMap = (HashMap<String, AffiliationForm>) request.getSession().getAttribute(AFFILIATIONS_MAP);
            // this should never happen, but just in case.
            if (affiliationsMap == null) {
                createAffiliationsIdList(request);
                affiliationsMap = (HashMap<String, AffiliationForm>) request.getSession().getAttribute(AFFILIATIONS_MAP);
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
    AffiliationForm getAffiliation(HttpServletRequest request) {
        AffiliationForm affiliationForm = new AffiliationForm();

        OrcidProfile profile = getEffectiveProfile();
        Visibility v = Visibility.valueOf(profile.getOrcidInternal().getPreferences().getWorkVisibilityDefault().getValue());
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
        affiliationForm.setDepartmentName(department);

        Text roleTitle = new Text();
        affiliationForm.setRoleTitle(roleTitle);

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
    AffiliationForm postAffiliation(HttpServletRequest request, @RequestBody AffiliationForm affiliationForm) {
        // Validate
        affiliationNameValidate(affiliationForm);
        cityValidate(affiliationForm);
        regionValidate(affiliationForm);
        countryValidate(affiliationForm);
        departmentValidate(affiliationForm);
        roleTitleValidate(affiliationForm);
        datesValidate(affiliationForm);

        copyErrors(affiliationForm.getAffiliationName(), affiliationForm);
        copyErrors(affiliationForm.getCity(), affiliationForm);
        copyErrors(affiliationForm.getRegion(), affiliationForm);
        copyErrors(affiliationForm.getCountry(), affiliationForm);
        copyErrors(affiliationForm.getDepartmentName(), affiliationForm);
        copyErrors(affiliationForm.getRoleTitle(), affiliationForm);
        copyErrors(affiliationForm.getEndDate(), affiliationForm);

        if (affiliationForm.getErrors().isEmpty()) {
            // Persist to DB
            ProfileEntity userProfile = profileDao.find(getEffectiveUserOrcid());
            OrgAffiliationRelationEntity orgAffiliationRelationEntity = jaxb2JpaAdapter.getNewOrgAffiliationRelationEntity(affiliationForm.toAffiliation(), userProfile);
            orgAffiliationRelationEntity.setSource(userProfile);
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
        Affiliations affiliations = currentProfile.getOrcidActivities() == null ? null : currentProfile.getOrcidActivities().getAffiliations();

        HashMap<String, AffiliationForm> affiliationsMap = new HashMap<>();
        List<String> affiliationIds = new ArrayList<String>();
        if (affiliations != null) {
            for (Affiliation affiliation : affiliations.getAffiliation()) {
                try {
                    AffiliationForm form = AffiliationForm.valueOf(affiliation);
                    if (affiliation.getAffiliationType() != null) {
                        form.setAffiliationTypeForDisplay(getMessage(buildInternationalizationKey(AffiliationType.class, affiliation.getAffiliationType().value())));
                    }
                    form.setCountryForDisplay(getMessage(buildInternationalizationKey(CountryIsoEntity.class, affiliation.getAffiliationAddress().getAffiliationCountry()
                            .getValue().name())));
                    affiliationsMap.put(affiliation.getPutCode(), form);
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
    AffiliationForm updateProfileAffiliationJson(HttpServletRequest request, @RequestBody AffiliationForm affiliation) {
        // Get cached profile
        OrcidProfile currentProfile = getEffectiveProfile();
        Affiliations orcidAffiliations = currentProfile.getOrcidActivities() == null ? null : currentProfile.getOrcidActivities().getAffiliations();
        if (orcidAffiliations != null) {
            List<Affiliation> orcidAffiliationsList = orcidAffiliations.getAffiliation();
            if (orcidAffiliationsList != null) {
                for (Affiliation orcidAffiliation : orcidAffiliationsList) {
                    // If the put codes are equal, we know that they are the
                    // same affiliation
                    if (orcidAffiliation.getPutCode().equals(affiliation.getPutCode().getValue())) {
                        // Update the privacy of the affiliation
                        orgRelationAffiliationDao.updateOrgAffiliationRelation(currentProfile.getOrcid().getValue(), affiliation.getPutCode().getValue(), affiliation
                                .getVisibility().getVisibility());
                    }
                }
            }
        }
        return affiliation;
    }

    /**
     * Search DB for disambiguated affiliations to suggest to user
     */
    @RequestMapping(value = "/disambiguated/name/{query}", method = RequestMethod.GET)
    public @ResponseBody
    List<Map<String, String>> searchDisambiguated(@PathVariable("query") String query, @RequestParam(value = "limit") int limit) {
        List<Map<String, String>> datums = new ArrayList<>();
        for (OrgDisambiguatedEntity orgDisambiguatedEntity : orgDisambiguatedDao.getOrgs(query, 0, limit)) {
            Map<String, String> datum = new HashMap<>();
            datum.put("value", orgDisambiguatedEntity.getName());
            datum.put("city", orgDisambiguatedEntity.getCity());
            datum.put("region", orgDisambiguatedEntity.getRegion());
            datum.put("country", orgDisambiguatedEntity.getCountry().value());
            datum.put("orgType", orgDisambiguatedEntity.getOrgType());
            datum.put("disambiguatedAffiliationIdentifier", String.valueOf(orgDisambiguatedEntity.getId()));
            datum.put("countryForDisplay", getMessage(buildInternationalizationKey(CountryIsoEntity.class, orgDisambiguatedEntity.getCountry().name())));
            datums.add(datum);
        }
        return datums;
    }

    /**
     * fetch disambiguated by id
     */
    @RequestMapping(value = "/disambiguated/id/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> getDisambiguated(@PathVariable("id") Long id) {
        OrgDisambiguatedEntity orgDisambiguatedEntity = orgDisambiguatedDao.find(id);
        Map<String, String> datum = new HashMap<>();
        datum.put("value", orgDisambiguatedEntity.getName());
        datum.put("city", orgDisambiguatedEntity.getCity());
        datum.put("region", orgDisambiguatedEntity.getRegion());
        datum.put("country", orgDisambiguatedEntity.getCountry().value());
        datum.put("orgType", orgDisambiguatedEntity.getOrgType());
        datum.put("sourceId", orgDisambiguatedEntity.getSourceId());
        datum.put("sourceType", orgDisambiguatedEntity.getSourceType());
        datum.put("countryForDisplay", getMessage(buildInternationalizationKey(CountryIsoEntity.class, orgDisambiguatedEntity.getCountry().name())));
        return datum;
    }

    @RequestMapping(value = "/affiliation/affiliationNameValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    AffiliationForm affiliationNameValidate(@RequestBody AffiliationForm affiliationForm) {
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
    AffiliationForm cityValidate(@RequestBody AffiliationForm affiliationForm) {
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
    AffiliationForm regionValidate(@RequestBody AffiliationForm affiliationForm) {
        affiliationForm.getRegion().setErrors(new ArrayList<String>());
        if (affiliationForm.getRegion().getValue() != null && affiliationForm.getRegion().getValue().trim().length() > 1000) {
            setError(affiliationForm.getRegion(), "manualAffiliation.length_less_1000");
        }
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/countryValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    AffiliationForm countryValidate(@RequestBody AffiliationForm affiliationForm) {
        affiliationForm.getCountry().setErrors(new ArrayList<String>());
        if (affiliationForm.getCountry().getValue() == null || affiliationForm.getCountry().getValue().trim().length() == 0) {
            setError(affiliationForm.getCountry(), "NotBlank.manualAffiliation.country");
        }
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/departmentValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    AffiliationForm departmentValidate(@RequestBody AffiliationForm affiliationForm) {
        affiliationForm.getDepartmentName().setErrors(new ArrayList<String>());
        if (affiliationForm.getDepartmentName().getValue() != null && affiliationForm.getDepartmentName().getValue().trim().length() > 1000) {
            setError(affiliationForm.getDepartmentName(), "manualAffiliation.length_less_1000");
        }
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/roleTitleValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    AffiliationForm roleTitleValidate(@RequestBody AffiliationForm affiliationForm) {
        affiliationForm.getRoleTitle().setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(affiliationForm.getRoleTitle()) && affiliationForm.getRoleTitle().getValue().trim().length() > 1000) {
            setError(affiliationForm.getRoleTitle(), "manualAffiliation.length_less_1000");
        }
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/datesValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    AffiliationForm datesValidate(@RequestBody AffiliationForm affiliationForm) {
        affiliationForm.getStartDate().setErrors(new ArrayList<String>());
        affiliationForm.getEndDate().setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmply(affiliationForm.getStartDate()) && !PojoUtil.isEmply(affiliationForm.getEndDate())) {
            if (affiliationForm.getStartDate().toJavaDate().after(affiliationForm.getEndDate().toJavaDate()))
                setError(affiliationForm.getEndDate(), "manualAffiliation.endDate.after");
        }
        return affiliationForm;
    }

}