package org.orcid.frontend.web.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.api.common.util.v3.ActivityUtils;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.AffiliationsManager;
import org.orcid.core.manager.v3.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.v3.release.record.Affiliation;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.OrgDisambiguated;
import org.orcid.pojo.ajaxForm.AffiliationForm;
import org.orcid.pojo.ajaxForm.AffiliationGroupContainer;
import org.orcid.pojo.ajaxForm.AffiliationGroupForm;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.Errors;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.Visibility;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private static final String AFFILIATIONS_MAP = "AFFILIATIONS_MAP";

    @Resource
    private OrgDisambiguatedManager orgDisambiguatedManager;

    @Resource(name = "affiliationsManagerV3")
    private AffiliationsManager affiliationsManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "affiliationsManagerReadOnlyV3")
    private AffiliationsManagerReadOnly affiliationsManagerReadOnly;

    /**
     * Removes a affiliation from a profile
     */
    @Deprecated
    @RequestMapping(value = "/affiliations.json", method = RequestMethod.DELETE)
    public @ResponseBody AffiliationForm removeAffiliationJson(HttpServletRequest request, @RequestBody AffiliationForm affiliation) {
        affiliationsManager.removeAffiliation(getCurrentUserOrcid(), Long.valueOf(affiliation.getPutCode().getValue()));
        return affiliation;
    }

    @RequestMapping(value = "/affiliation.json", method = RequestMethod.DELETE)
    public @ResponseBody Errors removeAffiliationJson(@RequestParam(value = "id") String affiliationId) {
        Errors errors = new Errors();
        boolean deleted = affiliationsManager.removeAffiliation(getCurrentUserOrcid(), Long.valueOf(affiliationId));
        if (!deleted) {
            // TODO: Log error in case the affiliation wasn't deleted
        }
        return errors;
    }

    /**
     * List affiliations associated with a profile
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/affiliations.json", method = RequestMethod.GET)
    public @ResponseBody List<AffiliationForm> getAffiliationJson(HttpServletRequest request, @RequestParam(value = "affiliationIds") String affiliationIdsStr) {
        List<AffiliationForm> affiliationList = new ArrayList<>();
        String[] affiliationIds = affiliationIdsStr.split(",");

        if (affiliationIds != null) {
            HashMap<String, AffiliationForm> affiliationsMap = (HashMap<String, AffiliationForm>) request.getSession().getAttribute(AFFILIATIONS_MAP);
            // this should never happen, but just in case.
            if (affiliationsMap == null) {
                createAffiliationsIdList(request);
                affiliationsMap = (HashMap<String, AffiliationForm>) request.getSession().getAttribute(AFFILIATIONS_MAP);
            }
            for (String affiliationId : affiliationIds) {
                affiliationList.add(affiliationsMap.get(affiliationId));
            }
        }

        return affiliationList;
    }

    /**
     * Returns a blank affiliation form
     */
    @RequestMapping(value = "/affiliation.json", method = RequestMethod.GET)
    public @ResponseBody AffiliationForm getAffiliation(HttpServletRequest request) {
        AffiliationForm affiliationForm = new AffiliationForm();

        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());

        Visibility v = null;
        org.orcid.jaxb.model.v3.release.common.Visibility defaultVis = null;
        if (profile.getActivitiesVisibilityDefault() != null) {
            defaultVis = org.orcid.jaxb.model.v3.release.common.Visibility.valueOf(profile.getActivitiesVisibilityDefault());
        } else {
            defaultVis = org.orcid.jaxb.model.v3.release.common.Visibility.valueOf(OrcidVisibilityDefaults.FUNDING_DEFAULT.getVisibility().name());
        }
        v = Visibility.valueOf(defaultVis);
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
        affiliationForm.setUrl(new Text());

        return affiliationForm;
    }

    @RequestMapping(value = "/affiliationDetails.json", method = RequestMethod.GET)
    public @ResponseBody AffiliationForm getAffiliationDetails(@RequestParam("id") Long id, @RequestParam("type") String type) {
        String orcid = getCurrentUserOrcid();

        if (type.equals("distinction")) {
            return AffiliationForm.valueOf(affiliationsManager.getDistinctionAffiliation(orcid, id));
        } else if (type.equals("education")) {
            return AffiliationForm.valueOf(affiliationsManager.getEducationAffiliation(orcid, id));
        } else if (type.equals("employment")) {
            return AffiliationForm.valueOf(affiliationsManager.getEmploymentAffiliation(orcid, id));
        } else if (type.equals("invited-position")) {
            return AffiliationForm.valueOf(affiliationsManager.getInvitedPositionAffiliation(orcid, id));
        } else if (type.equals("membership")) {
            return AffiliationForm.valueOf(affiliationsManager.getMembershipAffiliation(orcid, id));
        } else if (type.equals("qualification")) {
            return AffiliationForm.valueOf(affiliationsManager.getQualificationAffiliation(orcid, id));
        } else if (type.equals("service")) {
            return AffiliationForm.valueOf(affiliationsManager.getServiceAffiliation(orcid, id));
        } else {
            throw new IllegalArgumentException("Invalid affiliation type: " + type);
        }
    }

    @RequestMapping(value = "/affiliation.json", method = RequestMethod.POST)
    public @ResponseBody AffiliationForm postAffiliation(HttpServletRequest request, @RequestBody AffiliationForm affiliationForm) throws Exception {
        // Validate
        affiliationNameValidate(affiliationForm);
        if(!AffiliationForm.isEditorialService(affiliationForm)) {
	        cityValidate(affiliationForm);
	        regionValidate(affiliationForm);
	        countryValidate(affiliationForm);
        }
        departmentValidate(affiliationForm);
        roleTitleValidate(affiliationForm);
        datesValidate(affiliationForm);
        urlValidate(affiliationForm);
        copyErrors(affiliationForm.getAffiliationName(), affiliationForm);
        copyErrors(affiliationForm.getCity(), affiliationForm);
        copyErrors(affiliationForm.getRegion(), affiliationForm);
        copyErrors(affiliationForm.getCountry(), affiliationForm);
        copyErrors(affiliationForm.getDepartmentName(), affiliationForm);
        copyErrors(affiliationForm.getRoleTitle(), affiliationForm);
        copyErrors(affiliationForm.getUrl(), affiliationForm);
        copyErrors(affiliationForm.getStartDate(), affiliationForm);

        if (!PojoUtil.isEmpty(affiliationForm.getEndDate()))
            copyErrors(affiliationForm.getEndDate(), affiliationForm);
        
        if (affiliationForm.getErrors().isEmpty()) {
            if (PojoUtil.isEmpty(affiliationForm.getPutCode()))
                addAffiliation(affiliationForm);
            else
                editAffiliation(affiliationForm);
        }

        return affiliationForm;
    }

    /**
     * Adds a new affiliations
     * 
     * @param affiliationForm
     */
    private void addAffiliation(AffiliationForm affiliationForm) {
        Affiliation affiliation = affiliationForm.toAffiliation();
        if (affiliation instanceof Distinction) {
            affiliation = affiliationsManager.createDistinctionAffiliation(getCurrentUserOrcid(), (Distinction) affiliation, false);
        } else if (affiliation instanceof Education) {
            affiliation = affiliationsManager.createEducationAffiliation(getCurrentUserOrcid(), (Education) affiliation, false);
        } else if (affiliation instanceof Employment) {
            affiliation = affiliationsManager.createEmploymentAffiliation(getCurrentUserOrcid(), (Employment) affiliation, false);
        } else if (affiliation instanceof InvitedPosition) {
            affiliation = affiliationsManager.createInvitedPositionAffiliation(getCurrentUserOrcid(), (InvitedPosition) affiliation, false);
        } else if (affiliation instanceof Membership) {
            affiliation = affiliationsManager.createMembershipAffiliation(getCurrentUserOrcid(), (Membership) affiliation, false);
        } else if (affiliation instanceof Qualification) {
            affiliation = affiliationsManager.createQualificationAffiliation(getCurrentUserOrcid(), (Qualification) affiliation, false);
        } else if (affiliation instanceof Service) {
            affiliation = affiliationsManager.createServiceAffiliation(getCurrentUserOrcid(), (Service) affiliation, false);
        } else {
            throw new IllegalArgumentException("Invalid affiliation type: " + affiliation.getClass().getName());
        }
        affiliationForm.setPutCode(Text.valueOf(affiliation.getPutCode()));
    }

    /**
     * Updates an existing affiliation
     * 
     * @param affiliationForm
     * @throws Exception
     */
    private void editAffiliation(AffiliationForm affiliationForm) throws Exception {
        if (!getCurrentUserOrcid().equals(affiliationForm.getSource()))
            throw new Exception(getMessage("web.orcid.activity_incorrectsource.exception"));

        Affiliation affiliation = affiliationForm.toAffiliation();
        if (affiliation instanceof Distinction) {
            affiliation = affiliationsManager.updateDistinctionAffiliation(getCurrentUserOrcid(), (Distinction) affiliation, false);
        } else if (affiliation instanceof Education) {
            affiliation = affiliationsManager.updateEducationAffiliation(getCurrentUserOrcid(), (Education) affiliation, false);
        } else if (affiliation instanceof Employment) {
            affiliation = affiliationsManager.updateEmploymentAffiliation(getCurrentUserOrcid(), (Employment) affiliation, false);
        } else if (affiliation instanceof InvitedPosition) {
            affiliation = affiliationsManager.updateInvitedPositionAffiliation(getCurrentUserOrcid(), (InvitedPosition) affiliation, false);
        } else if (affiliation instanceof Membership) {
            affiliation = affiliationsManager.updateMembershipAffiliation(getCurrentUserOrcid(), (Membership) affiliation, false);
        } else if (affiliation instanceof Qualification) {
            affiliation = affiliationsManager.updateQualificationAffiliation(getCurrentUserOrcid(), (Qualification) affiliation, false);
        } else if (affiliation instanceof Service) {
            affiliation = affiliationsManager.updateServiceAffiliation(getCurrentUserOrcid(), (Service) affiliation, false);
        } else {
            throw new IllegalArgumentException("Invalid affiliation type: " + affiliation.getClass().getName());
        }
    }

    /**
     * List affiliations associated with a profile
     */
    @RequestMapping(value = "/affiliationIds.json", method = RequestMethod.GET)
    public @ResponseBody List<String> getAffiliationsJson(HttpServletRequest request) {
        // Get cached profile
        return createAffiliationsIdList(request);
    }

    @RequestMapping(value = "/employments.json", method = RequestMethod.GET)
    public @ResponseBody Employments getEmploymentSummaryList() {
        String orcid = getCurrentUserOrcid();
        List<EmploymentSummary> employmentsList = affiliationsManagerReadOnly.getEmploymentSummaryList(orcid);

        Employments employments = new Employments(affiliationsManagerReadOnly.groupAffiliations(employmentsList, false));
        ActivityUtils.setPathToAffiliations(employments, orcid);
        return employments;
    }

    /**
     * Create an affiliation id list and sorts a map associated with the list in
     * in the session
     * 
     */
    private List<String> createAffiliationsIdList(HttpServletRequest request) {
        List<Affiliation> affiliationsList = affiliationsManager.getAffiliations(getCurrentUserOrcid());
        HashMap<String, AffiliationForm> affiliationsMap = new HashMap<>();
        List<String> affiliationIds = new ArrayList<String>();
        if (affiliationsList != null) {
            for (Affiliation affiliation : affiliationsList) {
                AffiliationForm form = AffiliationForm.valueOf(affiliation);
                form.setCountryForDisplay(getMessage(buildInternationalizationKey(CountryIsoEntity.class, form.getCountry().getValue())));
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
                affiliationsMap.put(form.getPutCode().getValue(), form);
                affiliationIds.add(form.getPutCode().getValue());
            }
            request.getSession().setAttribute(AFFILIATIONS_MAP, affiliationsMap);
        }
        return affiliationIds;
    }

    /**
     * Updates an affiliation visibility
     */
    @RequestMapping(value = "/affiliation.json", method = RequestMethod.PUT)
    public @ResponseBody AffiliationForm updateAffiliationVisibility(HttpServletRequest request, @RequestBody AffiliationForm affiliation) {
        org.orcid.jaxb.model.v3.release.common.Visibility visibility = org.orcid.jaxb.model.v3.release.common.Visibility
                .fromValue(affiliation.getVisibility().getVisibility().value());
        affiliationsManager.updateVisibility(getEffectiveUserOrcid(), Long.valueOf(affiliation.getPutCode().getValue()), visibility);
        return affiliation;
    }

    /**
     * Updates visibility on multiple affiliations
     */
    @RequestMapping(value = "/{affiliationIdsStr}/visibility/{visibilityStr}", method = RequestMethod.GET)
    public @ResponseBody ArrayList<Long> updateAffiliationVisibilities(@PathVariable("affiliationIdsStr") String affiliationIdsStr,
            @PathVariable("visibilityStr") String visibilityStr) {
        String orcid = getEffectiveUserOrcid();
        ArrayList<Long> affIds = new ArrayList<Long>();
        for (String affId : affiliationIdsStr.split(","))
            affIds.add(new Long(affId));
        affiliationsManager.updateVisibilities(orcid, affIds, org.orcid.jaxb.model.v3.release.common.Visibility.fromValue(visibilityStr));
        return affIds;
    }

    /**
     * Search DB for disambiguated affiliations to suggest to user
     */
    @RequestMapping(value = "/disambiguated/name/{query}", method = RequestMethod.GET)
    public @ResponseBody List<Map<String, String>> searchDisambiguated(@PathVariable("query") String query, @RequestParam(value = "limit") int limit) {
        List<Map<String, String>> datums = new ArrayList<>();
        for (OrgDisambiguated orgDisambiguated : orgDisambiguatedManager.searchOrgsFromSolr(query.toLowerCase(), 0, limit, false)) {
            datums.add(orgDisambiguated.toMap());
        }
        return datums;
    }

    /**
     * fetch disambiguated by id
     */
    @RequestMapping(value = "/disambiguated/id/{id}", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getDisambiguated(@PathVariable("id") Long id) {
        OrgDisambiguated orgDisambiguated = orgDisambiguatedManager.findInDB(id);
        return orgDisambiguated.toMap();
    }

    @RequestMapping(value = "/affiliation/affiliationNameValidate.json", method = RequestMethod.POST)
    public @ResponseBody AffiliationForm affiliationNameValidate(@RequestBody AffiliationForm affiliationForm) {
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
    public @ResponseBody AffiliationForm cityValidate(@RequestBody AffiliationForm affiliationForm) {
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
    public @ResponseBody AffiliationForm regionValidate(@RequestBody AffiliationForm affiliationForm) {
        affiliationForm.getRegion().setErrors(new ArrayList<String>());
        if (affiliationForm.getRegion().getValue() != null && affiliationForm.getRegion().getValue().trim().length() > 1000) {
            setError(affiliationForm.getRegion(), "common.length_less_1000");
        }
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/countryValidate.json", method = RequestMethod.POST)
    public @ResponseBody AffiliationForm countryValidate(@RequestBody AffiliationForm affiliationForm) {
        affiliationForm.getCountry().setErrors(new ArrayList<String>());
        if (affiliationForm.getCountry().getValue() == null || affiliationForm.getCountry().getValue().trim().length() == 0) {
            setError(affiliationForm.getCountry(), "common.country.not_blank");
        }
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/departmentValidate.json", method = RequestMethod.POST)
    public @ResponseBody AffiliationForm departmentValidate(@RequestBody AffiliationForm affiliationForm) {
        affiliationForm.getDepartmentName().setErrors(new ArrayList<String>());
        if (affiliationForm.getDepartmentName().getValue() != null && affiliationForm.getDepartmentName().getValue().trim().length() > 1000) {
            setError(affiliationForm.getDepartmentName(), "common.length_less_1000");
        }
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/urlValidate.json", method = RequestMethod.POST)
    public @ResponseBody AffiliationForm urlValidate(@RequestBody AffiliationForm affiliationForm) {
        validateUrl(affiliationForm.getUrl());
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/roleTitleValidate.json", method = RequestMethod.POST)
    public @ResponseBody AffiliationForm roleTitleValidate(@RequestBody AffiliationForm affiliationForm) {
        affiliationForm.getRoleTitle().setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(affiliationForm.getRoleTitle()) && affiliationForm.getRoleTitle().getValue().trim().length() > 1000) {
            setError(affiliationForm.getRoleTitle(), "common.length_less_1000");
        }
        return affiliationForm;
    }

    @RequestMapping(value = "/affiliation/datesValidate.json", method = RequestMethod.POST)
    public @ResponseBody AffiliationForm datesValidate(@RequestBody AffiliationForm affiliationForm) {
        boolean primaryValidation = true;
        affiliationForm.getStartDate().setErrors(new ArrayList<String>());

        if (!PojoUtil.isEmpty(affiliationForm.getEndDate()))
            affiliationForm.getEndDate().setErrors(new ArrayList<String>());

        if (!PojoUtil.isEmpty(affiliationForm.getStartDate()) && !validDate(affiliationForm.getStartDate())) {
            primaryValidation = false;
            setError(affiliationForm.getStartDate(), "common.dates.invalid");
        }

        if (!PojoUtil.isEmpty(affiliationForm.getEndDate()) && !validDate(affiliationForm.getEndDate())) {
            primaryValidation = false;
            setError(affiliationForm.getEndDate(), "common.dates.invalid");
        }

        if (primaryValidation && (!PojoUtil.isEmpty(affiliationForm.getStartDate()) && !PojoUtil.isEmpty(affiliationForm.getEndDate()))) {
            if ("".equals(affiliationForm.getStartDate().getDay()) || "".equals(affiliationForm.getStartDate().getMonth())
                    || "".equals(affiliationForm.getEndDate().getDay()) || "".equals(affiliationForm.getEndDate().getMonth())) {
                
                boolean removeStartDateDay = false;
                boolean removeStartDateMonth = false;
                boolean removeEndDateDay = false;
                boolean removeEndDateMonth = false;
                if ("".equals(affiliationForm.getStartDate().getDay())) {
                    affiliationForm.getStartDate().setDay("01");
                    removeStartDateDay = true;
                }
                
                if ("".equals(affiliationForm.getStartDate().getMonth())) {
                    affiliationForm.getStartDate().setMonth("01");
                    removeStartDateMonth = true;
                }
                
                if ("".equals(affiliationForm.getEndDate().getDay())) {
                    affiliationForm.getEndDate().setDay("31");
                    removeEndDateDay = true;
                }
                
                if ("".equals(affiliationForm.getEndDate().getMonth())) {
                    affiliationForm.getEndDate().setMonth("12");
                    removeEndDateMonth = true;
                }            

                if (affiliationForm.getStartDate().toJavaDate().after(affiliationForm.getEndDate().toJavaDate()))
                    setError(affiliationForm.getEndDate(), "manualAffiliation.endDate.after");

                if (removeStartDateDay) {
                    affiliationForm.getStartDate().setDay("");
                }
                
                if (removeStartDateMonth) {
                    affiliationForm.getStartDate().setMonth("");
                }
                
                if (removeEndDateDay) {
                    affiliationForm.getEndDate().setDay("");
                }
                
                if (removeEndDateMonth) {
                    affiliationForm.getEndDate().setMonth("");
                }
                
            } else {
                if (affiliationForm.getStartDate().toJavaDate().after(affiliationForm.getEndDate().toJavaDate()))
                    setError(affiliationForm.getEndDate(), "manualAffiliation.endDate.after");
            }          

        }

        return affiliationForm;
    }

    @RequestMapping(value = "/affiliationGroups.json", method = RequestMethod.GET)
    public @ResponseBody AffiliationGroupContainer getGroupedAffiliations() {
        String orcid = getCurrentUserOrcid();
        AffiliationGroupContainer result = new AffiliationGroupContainer();
        Map<AffiliationType, List<AffiliationGroup<AffiliationSummary>>> affiliationsMap = affiliationsManager.getGroupedAffiliations(orcid, false);
        Long featuredId = affiliationsManagerReadOnly.getFeaturedFlag(orcid);
        for (AffiliationType type : AffiliationType.values()) {
            if (affiliationsMap.containsKey(type)) {
                List<AffiliationGroup<AffiliationSummary>> elementsList = affiliationsMap.get(type);
                List<AffiliationGroupForm> elementsFormList = new ArrayList<AffiliationGroupForm>();
                IntStream.range(0, elementsList.size()).forEach(idx -> {
                    AffiliationGroupForm groupForm = AffiliationGroupForm.valueOf(elementsList.get(idx), type.name() + '_' + idx, orcid);
                    // Fill country and org disambiguated data on the default
                    // affiliation
                    AffiliationForm defaultAffiliation = groupForm.getDefaultAffiliation();
                    if (defaultAffiliation != null) {
                        if (!PojoUtil.isEmpty(groupForm.getDefaultAffiliation().getCountry())) {
                            // Set country name
                            defaultAffiliation.setCountryForDisplay(groupForm.getDefaultAffiliation().getCountry().getValue());
                        }
                        // Set featured flag on default
                        if (defaultAffiliation.getPutCode() != null && defaultAffiliation.getPutCode().getValue() != null) {
                            try {
                                Long pc = Long.valueOf(defaultAffiliation.getPutCode().getValue());
                                if (featuredId != null && featuredId.equals(pc)) {
                                    defaultAffiliation.setFeatured(Boolean.TRUE);
                                }
                            } catch (NumberFormatException nfe) {
                                // ignore invalid putCode
                            }
                        }
                        // Set org disambiguated data
                        if (!PojoUtil.isEmpty(defaultAffiliation.getOrgDisambiguatedId())) {
                            OrgDisambiguated orgDisambiguated = orgDisambiguatedManager.findInDB(Long.parseLong(defaultAffiliation.getOrgDisambiguatedId().getValue()));
                            defaultAffiliation.setOrgDisambiguatedName(orgDisambiguated.getValue());
                            defaultAffiliation.setOrgDisambiguatedUrl(orgDisambiguated.getUrl());
                            defaultAffiliation.setOrgDisambiguatedCity(orgDisambiguated.getCity());
                            defaultAffiliation.setOrgDisambiguatedRegion(orgDisambiguated.getRegion());
                            defaultAffiliation.setOrgDisambiguatedCountry(orgDisambiguated.getCountry());
                            if (orgDisambiguated.getOrgDisambiguatedExternalIdentifiers() != null) {
                                defaultAffiliation.setOrgDisambiguatedExternalIdentifiers(orgDisambiguated.getOrgDisambiguatedExternalIdentifiers());
                            }
                        }
                    }

                    // Fill country and org disambiguated data for each
                    // affiliation
                    for (AffiliationForm aff : groupForm.getAffiliations()) {
                        if (!PojoUtil.isEmpty(aff.getCountry())) {
                            // Set country name
                            aff.setCountryForDisplay(aff.getCountry().getValue());
                        }
                        // Set featured flag on each affiliation
                        if (aff.getPutCode() != null && aff.getPutCode().getValue() != null) {
                            try {
                                Long pc = Long.valueOf(aff.getPutCode().getValue());
                                if (featuredId != null && featuredId.equals(pc)) {
                                    aff.setFeatured(Boolean.TRUE);
                                }
                            } catch (NumberFormatException nfe) {
                                // ignore invalid putCode
                            }
                        }
                        // Set org disambiguated data
                        if (!PojoUtil.isEmpty(aff.getOrgDisambiguatedId())) {
                            if (!PojoUtil.isEmpty(aff.getOrgDisambiguatedId())) {
                                OrgDisambiguated orgDisambiguated = orgDisambiguatedManager.findInDB(Long.parseLong(aff.getOrgDisambiguatedId().getValue()));
                                aff.setOrgDisambiguatedName(orgDisambiguated.getValue());
                                aff.setOrgDisambiguatedUrl(orgDisambiguated.getUrl());
                                aff.setOrgDisambiguatedCity(orgDisambiguated.getCity());
                                aff.setOrgDisambiguatedRegion(orgDisambiguated.getRegion());
                                aff.setOrgDisambiguatedCountry(orgDisambiguated.getCountry());
                                if (orgDisambiguated.getOrgDisambiguatedExternalIdentifiers() != null) {
                                    aff.setOrgDisambiguatedExternalIdentifiers(orgDisambiguated.getOrgDisambiguatedExternalIdentifiers());
                                }
                            }
                        }
                    }

                    elementsFormList.add(groupForm);
                });
                result.getAffiliationGroups().put(type, elementsFormList);
            }
        }
        return result;
    }

    @RequestMapping(value = "/updateToMaxDisplay.json", method = RequestMethod.GET)
    public @ResponseBody boolean updateToMaxDisplay(@RequestParam(value = "putCode") Long putCode) {
        String orcid = getEffectiveUserOrcid();
        return affiliationsManager.updateToMaxDisplay(orcid, putCode);
    }

    @RequestMapping(value = "/featuredAffiliation.json", method = RequestMethod.PUT)
    public @ResponseBody ResponseEntity<Map<String, Object>> setFeaturedAffiliation(@RequestBody Map<String, Long> payload) {
        String orcid = getEffectiveUserOrcid();
        Map<String, Object> body = new HashMap<String, Object>();
        Long putCode = null;
        boolean hasKey = false;
        if (payload != null) {
            if (payload.containsKey("putCode")) {
                hasKey = true;
                putCode = payload.get("putCode");
            } else if (payload.containsKey("affiliationId")) {
                hasKey = true;
                putCode = payload.get("affiliationId");
            }
        }
        // If client explicitly sent a null value, clear all featured flags
        if (hasKey && putCode == null) {
            affiliationsManager.clearFeatured(orcid);
            body.put("ok", Boolean.TRUE);
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.OK);
        }
        // If no key provided or null without explicit key, return bad request
        if (!hasKey || putCode == null) {
            body.put("ok", Boolean.FALSE);
            body.put("message", "putCode is required");
            return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
        }
        boolean updated = affiliationsManager.setOnlyFeatured(orcid, putCode);
        body.put("ok", Boolean.valueOf(updated));
        return new ResponseEntity<Map<String, Object>>(body, HttpStatus.OK);
    }
}
