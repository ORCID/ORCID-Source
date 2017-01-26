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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedSolrDao;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
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

    //TODO: Stop using the DAO, use the 'affiliationsManager' instead
    @Resource
    private OrgAffiliationRelationDao orgAffiliationRelationDao;

    @Resource
    private OrgDisambiguatedSolrDao orgDisambiguatedSolrDao;

    @Resource
    private OrgDisambiguatedDao orgDisambiguatedDao;
    
    @Resource
    private AffiliationsManager affiliationsManager;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

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
            orgAffiliationRelationDao.removeOrgAffiliationRelation(currentProfile.getOrcidIdentifier().getPath(), Long.valueOf(affiliation.getPutCode().getValue()));
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
                
                if(affiliation.getStartDate() == null) {
                	initializeStartDate(affiliation);
                } else {
                	if(affiliation.getStartDate().getDay() == null) {
                		affiliation.getStartDate().setDay(new String());
                	}
                	if(affiliation.getStartDate().getMonth() == null) {
                		affiliation.getStartDate().setMonth(new String());
                	}
                	if(affiliation.getStartDate().getYear() == null) {
                		affiliation.getStartDate().setYear(new String());
                	}
                }
                
                if(affiliation.getEndDate() == null) {
                	initializeEndDate(affiliation);
                } else {
                	if(affiliation.getEndDate().getDay() == null) {
                		affiliation.getEndDate().setDay(new String());
                	}
                	if(affiliation.getEndDate().getMonth() == null) {
                		affiliation.getEndDate().setMonth(new String());
                	}
                	if(affiliation.getEndDate().getYear() == null) {
                		affiliation.getEndDate().setYear(new String());
                	}
                }
                
                affiliationList.add(affiliation);
            }
        }

        return affiliationList;
    }

    private void initializeStartDate(AffiliationForm affiliation) {
    	if (affiliation.getStartDate() == null) {
            affiliation.setStartDate(getEmptyDate());
        }
	}
    
    private void initializeEndDate(AffiliationForm affiliation) {
    	if (affiliation.getEndDate() == null) {
            affiliation.setEndDate(getEmptyDate());
        }
	}
    
    private Date getEmptyDate() {
    	Date date = new Date();
        date.setDay(new String());
        date.setMonth(new String());
        date.setYear(new String());
        return date;
    }

	/**
     * Returns a blank affiliation form
     * */
    @RequestMapping(value = "/affiliation.json", method = RequestMethod.GET)
    public @ResponseBody
    AffiliationForm getAffiliation(HttpServletRequest request) {
        AffiliationForm affiliationForm = new AffiliationForm();

        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        Visibility v = Visibility.valueOf(profile.getActivitiesVisibilityDefault() == null ? OrcidVisibilityDefaults.FUNDING_DEFAULT.getVisibility() : profile.getActivitiesVisibilityDefault());
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
        
        affiliationForm.setOrgDisambiguatedId(new Text());

        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation.json", method = RequestMethod.POST)
    public @ResponseBody
    AffiliationForm postAffiliation(HttpServletRequest request, @RequestBody AffiliationForm affiliationForm) throws Exception {
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
        if(!PojoUtil.isEmpty(affiliationForm.getStartDate()))
            copyErrors(affiliationForm.getStartDate(), affiliationForm);
        if(!PojoUtil.isEmpty(affiliationForm.getEndDate()))
            copyErrors(affiliationForm.getEndDate(), affiliationForm);
        if (affiliationForm.getErrors().isEmpty()) {
            if(PojoUtil.isEmpty(affiliationForm.getPutCode()))
                addAffiliation(affiliationForm);
            else
                editAffiliation(affiliationForm);
        }

        return affiliationForm;
    }

    /**
     * Adds a new affiliations
     * @param affiliationForm
     * */
    private void addAffiliation(AffiliationForm affiliationForm) {
        // Persist to DB
        ProfileEntity userProfile = new ProfileEntity(getEffectiveUserOrcid());
        OrgAffiliationRelationEntity orgAffiliationRelationEntity = jaxb2JpaAdapter.getNewOrgAffiliationRelationEntity(affiliationForm.toAffiliation(), userProfile);
        orgAffiliationRelationEntity.setSourceId(getEffectiveUserOrcid());
        orgAffiliationRelationDao.persist(orgAffiliationRelationEntity);
        affiliationForm.setPutCode(Text.valueOf(orgAffiliationRelationEntity.getId().toString()));
    }
    
    /**
     * Updates an existing affiliation
     * @param affiliationForm
     * @throws Exception 
     * */
    private void editAffiliation(AffiliationForm affiliationForm) throws Exception {
    	Object params[] = {};
        if(PojoUtil.isEmpty(affiliationForm.getPutCode())) {
            throw new IllegalArgumentException(getMessage("web.orcid.affiliation_noputcode.exception", params));
        }
        OrcidProfile currentProfile = getEffectiveProfile();
        if (!currentProfile.getOrcidIdentifier().getPath().equals(affiliationForm.getSource()))
            throw new Exception(getMessage("web.orcid.activity_incorrectsource.exception"));

        OrgAffiliationRelationEntity orgAffiliationRelationEntity = jaxb2JpaAdapter.getUpdatedAffiliationRelationEntity(affiliationForm.toAffiliation());
        orgAffiliationRelationEntity.setSourceId(getEffectiveUserOrcid());
        orgAffiliationRelationDao.updateOrgAffiliationRelationEntity(orgAffiliationRelationEntity);
        affiliationForm.setPutCode(Text.valueOf(orgAffiliationRelationEntity.getId().toString()));                
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
        List<AffiliationForm> affiliationsList = affiliationsManager.getAffiliations(getCurrentUserOrcid());
        HashMap<String, AffiliationForm> affiliationsMap = new HashMap<>();
        List<String> affiliationIds = new ArrayList<String>();
        if (affiliationsList != null) {
            for (AffiliationForm form : affiliationsList) {
                try {
                    if (form.getAffiliationType() != null) {
                        form.setAffiliationTypeForDisplay(getMessage(buildInternationalizationKey(org.orcid.jaxb.model.message.AffiliationType.class, form.getAffiliationType().getValue())));
                    }
                    form.setCountryForDisplay(getMessage(buildInternationalizationKey(CountryIsoEntity.class, form.getCountry().getValue())));
                    affiliationsMap.put(form.getPutCode().getValue(), form);
                    affiliationIds.add(form.getPutCode().getValue());
                } catch (Exception e) {
                    LOGGER.error("Failed to parse as Affiliation. Put code" + form.getPutCode().getValue(), e);
                }
            }
            request.getSession().setAttribute(AFFILIATIONS_MAP, affiliationsMap);
        }
        return affiliationIds;
    }

    /**
     * Updates an affiliation visibility
     * */
    @RequestMapping(value = "/affiliation.json", method = RequestMethod.PUT)
    public @ResponseBody
    AffiliationForm updateAffiliationVisibility(HttpServletRequest request, @RequestBody AffiliationForm affiliation) {
        // Get cached profile
        String userOrcid = getEffectiveUserOrcid();
        affiliationsManager.updateVisibility(userOrcid, Long.valueOf(affiliation.getPutCode().getValue()), affiliation
                                .getVisibility().getVisibility());
        return affiliation;
    }

    /**
     * Search DB for disambiguated affiliations to suggest to user
     */
    @RequestMapping(value = "/disambiguated/name/{query}", method = RequestMethod.GET)
    public @ResponseBody
    List<Map<String, String>> searchDisambiguated(@PathVariable("query") String query, @RequestParam(value = "limit") int limit) {
        List<Map<String, String>> datums = new ArrayList<>();
        for (OrgDisambiguatedSolrDocument orgDisambiguatedDocument : orgDisambiguatedSolrDao.getOrgs(query, 0, limit)) {
            Map<String, String> datum = createDatumFromOrgDisambiguated(orgDisambiguatedDocument);
            datums.add(datum);
        }
        return datums;
    }

    private Map<String, String> createDatumFromOrgDisambiguated(OrgDisambiguatedSolrDocument orgDisambiguatedDocument) {
        Map<String, String> datum = new HashMap<>();
        datum.put("value", orgDisambiguatedDocument.getOrgDisambiguatedName());
        datum.put("city", orgDisambiguatedDocument.getOrgDisambiguatedCity());
        datum.put("region", orgDisambiguatedDocument.getOrgDisambiguatedRegion());
        datum.put("country", orgDisambiguatedDocument.getOrgDisambiguatedCountry());
        datum.put("orgType", orgDisambiguatedDocument.getOrgDisambiguatedType());
        datum.put("disambiguatedAffiliationIdentifier", Long.toString(orgDisambiguatedDocument.getOrgDisambiguatedId()));
        datum.put("countryForDisplay", getMessage(buildInternationalizationKey(CountryIsoEntity.class, orgDisambiguatedDocument.getOrgDisambiguatedCountry())));
        return datum;
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
                setError(affiliationForm.getAffiliationName(), "common.length_less_1000");
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
                setError(affiliationForm.getCity(), "common.length_less_1000");
            }
        }
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/regionValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    AffiliationForm regionValidate(@RequestBody AffiliationForm affiliationForm) {
        affiliationForm.getRegion().setErrors(new ArrayList<String>());
        if (affiliationForm.getRegion().getValue() != null && affiliationForm.getRegion().getValue().trim().length() > 1000) {
            setError(affiliationForm.getRegion(), "common.length_less_1000");
        }
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/countryValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    AffiliationForm countryValidate(@RequestBody AffiliationForm affiliationForm) {
        affiliationForm.getCountry().setErrors(new ArrayList<String>());
        if (affiliationForm.getCountry().getValue() == null || affiliationForm.getCountry().getValue().trim().length() == 0) {
            setError(affiliationForm.getCountry(), "common.country.not_blank");
        }
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/departmentValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    AffiliationForm departmentValidate(@RequestBody AffiliationForm affiliationForm) {
        affiliationForm.getDepartmentName().setErrors(new ArrayList<String>());
        if (affiliationForm.getDepartmentName().getValue() != null && affiliationForm.getDepartmentName().getValue().trim().length() > 1000) {
            setError(affiliationForm.getDepartmentName(), "common.length_less_1000");
        }
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/roleTitleValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    AffiliationForm roleTitleValidate(@RequestBody AffiliationForm affiliationForm) {
        affiliationForm.getRoleTitle().setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(affiliationForm.getRoleTitle()) && affiliationForm.getRoleTitle().getValue().trim().length() > 1000) {
            setError(affiliationForm.getRoleTitle(), "common.length_less_1000");
        }
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/datesValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    AffiliationForm datesValidate(@RequestBody AffiliationForm affiliationForm) {
        boolean primaryValidation = true;
    	
        if(!PojoUtil.isEmpty(affiliationForm.getStartDate()))
            affiliationForm.getStartDate().setErrors(new ArrayList<String>());
        if(!PojoUtil.isEmpty(affiliationForm.getEndDate()))
            affiliationForm.getEndDate().setErrors(new ArrayList<String>());
        if((PojoUtil.isEmpty(affiliationForm.getStartDate().getYear()) && (!PojoUtil.isEmpty(affiliationForm.getStartDate().getMonth()) || !PojoUtil.isEmpty(affiliationForm.getStartDate().getDay()))) ||
        		(!PojoUtil.isEmpty(affiliationForm.getStartDate().getYear()) && !PojoUtil.isEmpty(affiliationForm.getStartDate().getDay()) && PojoUtil.isEmpty(affiliationForm.getStartDate().getMonth()))) {
        	primaryValidation = false;
        	setError(affiliationForm.getStartDate(), "common.dates.invalid");
        } 
        if((PojoUtil.isEmpty(affiliationForm.getEndDate().getYear()) && (!PojoUtil.isEmpty(affiliationForm.getEndDate().getMonth()) || !PojoUtil.isEmpty(affiliationForm.getEndDate().getDay()))) ||
		(!PojoUtil.isEmpty(affiliationForm.getEndDate().getYear()) && !PojoUtil.isEmpty(affiliationForm.getEndDate().getDay()) && PojoUtil.isEmpty(affiliationForm.getEndDate().getMonth()))) {
        	primaryValidation = false;
        	setError(affiliationForm.getEndDate(), "common.dates.invalid");
        }
        if (primaryValidation && (!PojoUtil.isEmpty(affiliationForm.getStartDate()) && !PojoUtil.isEmpty(affiliationForm.getEndDate()))) {
            if (affiliationForm.getStartDate().toJavaDate().after(affiliationForm.getEndDate().toJavaDate()))
                setError(affiliationForm.getEndDate(), "manualAffiliation.endDate.after");
        }
       
        return affiliationForm;
    }

}