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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.GroupIdRecordManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.groupid_rc3.GroupIdRecord;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record_rc3.PeerReview;
import org.orcid.jaxb.model.record_rc3.PeerReviewType;
import org.orcid.jaxb.model.record_rc3.Relationship;
import org.orcid.jaxb.model.record_rc3.Role;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedSolrDao;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PeerReviewForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.TranslatedTitleForm;
import org.orcid.pojo.ajaxForm.WorkExternalIdentifier;
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
 * @author Angel Montenegro
 */
@Controller("peerReviewsController")
@RequestMapping(value = { "/peer-reviews" })
public class PeerReviewsController extends BaseWorkspaceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PeerReviewsController.class);
    private static final String PEER_REVIEW_MAP = "PEER_REVIEW_MAP";

    @Resource
    private LocaleManager localeManager;

    @Resource
    private PeerReviewManager peerReviewManager;

    @Resource
    private OrgDisambiguatedSolrDao orgDisambiguatedSolrDao;

    @Resource
    private OrgDisambiguatedDao orgDisambiguatedDao;

    @Resource(name = "languagesMap")
    private LanguagesMap lm;    
    
    @Resource
    private GroupIdRecordManager groupIdRecordManager;
    
    @Resource
    private ProfileEntityManager profileEntityManager;

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    /**
     * Returns a blank peer review form
     * */
    @RequestMapping(value = "/peer-review.json", method = RequestMethod.GET)
    public @ResponseBody PeerReviewForm getEmptyPeerReview() {
        Date emptyDate = new Date();
        emptyDate.setDay(StringUtils.EMPTY);
        emptyDate.setMonth(StringUtils.EMPTY);
        emptyDate.setYear(StringUtils.EMPTY);

        TranslatedTitleForm emptyTranslatedTitle = new TranslatedTitleForm();
        emptyTranslatedTitle.setErrors(Collections.<String> emptyList());
        emptyTranslatedTitle.setContent(StringUtils.EMPTY);
        emptyTranslatedTitle.setLanguageCode(StringUtils.EMPTY);
        emptyTranslatedTitle.setLanguageName(StringUtils.EMPTY);

        WorkExternalIdentifier emptyExternalId = new WorkExternalIdentifier();
        emptyExternalId.setErrors(new ArrayList<String>());
        emptyExternalId.setWorkExternalIdentifierId(Text.valueOf(StringUtils.EMPTY));
        emptyExternalId.getWorkExternalIdentifierId().setRequired(false);
        emptyExternalId.setWorkExternalIdentifierType(Text.valueOf(StringUtils.EMPTY));
        emptyExternalId.getWorkExternalIdentifierType().setRequired(false);
        emptyExternalId.setRelationship(Text.valueOf(Relationship.SELF.value()));
        emptyExternalId.setUrl(Text.valueOf(StringUtils.EMPTY));

        List<WorkExternalIdentifier> emptyExtIdsList = new ArrayList<WorkExternalIdentifier>();
        emptyExtIdsList.add(emptyExternalId);

        PeerReviewForm form = new PeerReviewForm();

        form.setErrors(Collections.<String> emptyList());

        form.setCompletionDate(emptyDate);
        form.setCreatedDate(emptyDate);

        form.setCity(Text.valueOf(StringUtils.EMPTY));
        form.setRegion(Text.valueOf(StringUtils.EMPTY));
        form.getRegion().setRequired(false);
        form.setCountry(Text.valueOf(StringUtils.EMPTY));
        form.setCountryForDisplay(StringUtils.EMPTY);
        form.setDisambiguationSource(Text.valueOf(StringUtils.EMPTY));
        form.getDisambiguationSource().setRequired(false);
        form.setOrgName(Text.valueOf(StringUtils.EMPTY));

        form.setPutCode(Text.valueOf(StringUtils.EMPTY));
        form.getPutCode().setRequired(false);
        form.setRole(Text.valueOf(Role.REVIEWER.value()));
        form.setType(Text.valueOf(PeerReviewType.REVIEW.value()));
        form.setUrl(Text.valueOf(StringUtils.EMPTY));
        form.getUrl().setRequired(false);
        form.setExternalIdentifiers(emptyExtIdsList);

        form.setGroupId(Text.valueOf(StringUtils.EMPTY));
        form.setSubjectContainerName(Text.valueOf(StringUtils.EMPTY));        
        form.setSubjectExternalIdentifier(emptyExternalId);
        form.setSubjectName(Text.valueOf(StringUtils.EMPTY));
        form.setSubjectType(Text.valueOf(StringUtils.EMPTY));
        form.setSubjectUrl(Text.valueOf(StringUtils.EMPTY));
        
        return form;
    }

    /**
     * List fundings associated with a profile
     * */
    @RequestMapping(value = "/peer-review-ids.json", method = RequestMethod.GET)
    public @ResponseBody List<String> getPeerReviewIdsJson(HttpServletRequest request) {
        // Get cached profile
        List<String> fundingIds = createPeerReviewIdList(request);
        return fundingIds;
    }

    /**
     * Create a funding id list and sorts a map associated with the list in in
     * the session
     * 
     */
    private List<String> createPeerReviewIdList(HttpServletRequest request) {
        String orcid = getCurrentUserOrcid();
        java.util.Date lastModified = profileEntityManager.getLastModified(orcid);
        List<PeerReview> peerReviews = peerReviewManager.findPeerReviews(orcid, lastModified.getTime());
        
        Map<String, String> languages = lm.buildLanguageMap(getUserLocale(), false);
        HashMap<Long, PeerReviewForm> peerReviewMap = new HashMap<>();
        List<String> peerReviewIds = new ArrayList<String>();

        if (peerReviews != null) {
            for (PeerReview peerReview : peerReviews) {
                try {
                    PeerReviewForm form = PeerReviewForm.valueOf(peerReview);

                    if(form.getExternalIdentifiers() != null && !form.getExternalIdentifiers().isEmpty()) {
                        for(WorkExternalIdentifier wExtId : form.getExternalIdentifiers()) {
                            if(PojoUtil.isEmpty(wExtId.getRelationship())) {
                                wExtId.setRelationship(Text.valueOf(Relationship.SELF.value()));
                            }
                        }
                    }
                    
                    if (form.getTranslatedSubjectName() != null) {
                        // Set translated title language name
                        if (!(form.getTranslatedSubjectName() == null) && !StringUtils.isEmpty(form.getTranslatedSubjectName().getLanguageCode())) {
                            String languageName = languages.get(form.getTranslatedSubjectName().getLanguageCode());
                            form.getTranslatedSubjectName().setLanguageName(languageName);
                        }
                    }

                    form.setCountryForDisplay(getMessage(buildInternationalizationKey(CountryIsoEntity.class, peerReview.getOrganization().getAddress().getCountry()
                            .name())));
                    
                    //Set the numeric id (the table id in the group_id_record table) of the group id
                    if(form.getGroupId() != null && !PojoUtil.isEmpty(form.getGroupId().getValue())) {
                        GroupIdRecord groupId = groupIdRecordManager.findByGroupId(form.getGroupId().getValue()).get();
                        form.setGroupIdPutCode(Text.valueOf(groupId.getPutCode()));
                    }                    
                    
                    peerReviewMap.put(peerReview.getPutCode(), form);
                    peerReviewIds.add(String.valueOf(peerReview.getPutCode()));
                } catch (Exception e) {
                    LOGGER.error("Failed to parse as PeerReview. Put code" + peerReview.getPutCode(), e);
                }
            }
            request.getSession().setAttribute(PEER_REVIEW_MAP, peerReviewMap);
        }
        return peerReviewIds;
    }

    /**
     * List peer reviews associated with a profile
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/get-peer-reviews.json", method = RequestMethod.GET)
    public @ResponseBody List<PeerReviewForm> getPeerReviewsJson(HttpServletRequest request, @RequestParam(value = "peerReviewIds") String peerReviewIdsStr) {
        List<PeerReviewForm> peerReviewList = new ArrayList<>();
        PeerReviewForm peerReview = null;
        String[] peerReviewIds = peerReviewIdsStr.split(",");

        if (peerReviewIds != null) {
            HashMap<Long, PeerReviewForm> peerReviewMap = (HashMap<Long, PeerReviewForm>) request.getSession().getAttribute(PEER_REVIEW_MAP);
            // this should never happen, but just in case.
            if (peerReviewMap == null) {
                createPeerReviewIdList(request);
                peerReviewMap = (HashMap<Long, PeerReviewForm>) request.getSession().getAttribute(PEER_REVIEW_MAP);
            }
            for (String peerReviewId : peerReviewIds) {
                peerReview = peerReviewMap.get(Long.valueOf(peerReviewId));
                peerReviewList.add(peerReview);
            }
        }

        return peerReviewList;
    }

    /**
     * Persist a funding object on database
     * */
    @RequestMapping(value = "/peer-review.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm postPeerReview(@RequestBody PeerReviewForm peerReview) {
        // Reset errors
        peerReview.setErrors(new ArrayList<String>());
        // Remove empty external identifiers
        removeEmptyExternalIds(peerReview);
        // Remove empty external identifiers on subject
        removeEmptyExternalIdsOnSubject(peerReview);

        validateOrgName(peerReview);
        validateCity(peerReview);
        validateRegion(peerReview);
        validateCountry(peerReview);
        validateUrl(peerReview);         
        validateExternalIdentifiers(peerReview);
        validateGroupId(peerReview);
        copyErrors(peerReview.getOrgName(), peerReview);
        copyErrors(peerReview.getCity(), peerReview);
        copyErrors(peerReview.getRegion(), peerReview);
        copyErrors(peerReview.getCountry(), peerReview);
        copyErrors(peerReview.getUrl(), peerReview);
        copyErrors(peerReview.getGroupId(), peerReview);
        if(peerReview.getExternalIdentifiers() != null) {
            for(WorkExternalIdentifier extId : peerReview.getExternalIdentifiers()) {
                copyErrors(extId.getWorkExternalIdentifierId(), peerReview);
                copyErrors(extId.getWorkExternalIdentifierType(), peerReview);
            }
        }
        
        if(peerReview.getCompletionDate() != null) {
            validateCompletionDate(peerReview);
            copyErrors(peerReview.getCompletionDate(), peerReview);
        }
                                
        validateSubjectType(peerReview);
        copyErrors(peerReview.getSubjectType(), peerReview);
        
        validateSubjectName(peerReview);
        copyErrors(peerReview.getSubjectName(), peerReview);
        
        validateSubjectUrl(peerReview);
        copyErrors(peerReview.getSubjectUrl(), peerReview);
        
        validateSubjectExternalIdentifier(peerReview);
        copyErrors(peerReview.getSubjectExternalIdentifier(), peerReview);
            
        // If there are no errors, persist to DB
        if (peerReview.getErrors().isEmpty()) {
            if (PojoUtil.isEmpty(peerReview.getPutCode())) {
                peerReview = addPeerReview(peerReview);
            } else {
                peerReview = editPeerReview(peerReview);
            }
        }

        return peerReview;
    }

    /**
     * List peer reviews associated with a profile
     * */
    @RequestMapping(value = "/get-peer-review.json", method = RequestMethod.GET)
    public @ResponseBody PeerReviewForm getPeerReviewJson(@RequestParam(value = "peerReviewId") Long peerReviewId) {
        PeerReview peerReview = peerReviewManager.getPeerReview(getEffectiveUserOrcid(), peerReviewId);
        if (peerReview != null) {
            return PeerReviewForm.valueOf(peerReview);
        }
        
        return null;
    }

    /**
     * Deletes a peer review
     * */
    @RequestMapping(value = "/peer-review.json", method = RequestMethod.DELETE)
    public @ResponseBody PeerReviewForm deletePeerReviewJson(@RequestBody PeerReviewForm peerReview) {
        if (peerReview == null || PojoUtil.isEmpty(peerReview.getPutCode())) {
            return null;
        }
        peerReviewManager.removePeerReview(getEffectiveUserOrcid(), Long.valueOf(peerReview.getPutCode().getValue()));
        return peerReview;
    }

    @RequestMapping(value = "/{peerReviewIdsStr}", method = RequestMethod.DELETE)
    public @ResponseBody List<String> removePeerReviews(@PathVariable("peerReviewIdsStr") String peerReviewIdsStr) {
        List<String> peerReviewIds = Arrays.asList(peerReviewIdsStr.split(","));
        String orcid = getEffectiveUserOrcid();

        for (String id : peerReviewIds) {
            peerReviewManager.removePeerReview(orcid, Long.valueOf(id));
        }

        return peerReviewIds;
    }

    private PeerReviewForm addPeerReview(PeerReviewForm peerReviewForm) {
        String userOrcid = getEffectiveUserOrcid();
        PeerReview peerReview = peerReviewForm.toPeerReview();
        peerReview = peerReviewManager.createPeerReview(userOrcid, peerReview, false);
        peerReviewForm = PeerReviewForm.valueOf(peerReview);
        return peerReviewForm;
    }

    private PeerReviewForm editPeerReview(PeerReviewForm peerReviewForm) {
        String userOrcid = getEffectiveUserOrcid();
        PeerReview peerReview = peerReviewForm.toPeerReview();
        peerReview = peerReviewManager.updatePeerReview(userOrcid, peerReview, false);
        return PeerReviewForm.valueOf(peerReview);
    }

    private void removeEmptyExternalIds(PeerReviewForm peerReview) {
        List<WorkExternalIdentifier> extIds = peerReview.getExternalIdentifiers();
        List<WorkExternalIdentifier> updatedExtIds = new ArrayList<WorkExternalIdentifier>();
        if (extIds != null) {
            // For all external identifiers
            for (WorkExternalIdentifier extId : extIds) {
                // Keep only the ones that contains a value or url
                if (!PojoUtil.isEmpty(extId.getWorkExternalIdentifierId())) {
                    updatedExtIds.add(extId);
                }
            }
        }

        if (updatedExtIds.isEmpty()) {
            WorkExternalIdentifier wei = new WorkExternalIdentifier();
            updatedExtIds.add(wei);
        }

        peerReview.setExternalIdentifiers(updatedExtIds);
    }

    private void removeEmptyExternalIdsOnSubject(PeerReviewForm peerReview) {
        if (peerReview.getSubjectExternalIdentifier() == null) {
            return;
        }
        if(PojoUtil.isEmpty(peerReview.getSubjectExternalIdentifier().getWorkExternalIdentifierId())) {
            peerReview.setSubjectExternalIdentifier(null);
        }                    
    }

    @RequestMapping(value = "/orgNameValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateOrgName(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getOrgName() == null) {
            peerReview.setOrgName(Text.valueOf(StringUtils.EMPTY));
        }
        peerReview.getOrgName().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(peerReview.getOrgName())) {
            setError(peerReview.getOrgName(), "org.name.not_blank");
        } else {
            if (peerReview.getOrgName().getValue().trim().length() > 1000) {
                setError(peerReview.getOrgName(), "common.length_less_1000");
            }
        }
        return peerReview;
    }

    @RequestMapping(value = "/cityValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateCity(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getCity() == null) {
            peerReview.setCity(Text.valueOf(StringUtils.EMPTY));
        }
        peerReview.getCity().setErrors(new ArrayList<String>());
        if (peerReview.getCity().getValue() == null || peerReview.getCity().getValue().trim().length() == 0) {
            setError(peerReview.getCity(), "org.city.not_blank");
        } else {
            if (peerReview.getCity().getValue().trim().length() > 1000) {
                setError(peerReview.getCity(), "common.length_less_1000");
            }
        }
        return peerReview;
    }

    @RequestMapping(value = "/regionValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateRegion(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getRegion() == null) {
           peerReview.setRegion(Text.valueOf(StringUtils.EMPTY)); 
        }
        peerReview.getRegion().setErrors(new ArrayList<String>());
        if (peerReview.getRegion().getValue() != null && peerReview.getRegion().getValue().trim().length() > 1000) {
            setError(peerReview.getRegion(), "common.length_less_1000");
        }
        return peerReview;
    }

    @RequestMapping(value = "/countryValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateCountry(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getCountry() == null) {
            peerReview.setCountry(Text.valueOf(StringUtils.EMPTY));
        }
        peerReview.getCountry().setErrors(new ArrayList<String>());
        if (peerReview.getCountry().getValue() == null || peerReview.getCountry().getValue().trim().length() == 0) {
            setError(peerReview.getCountry(), "common.country.not_blank");
        }
        return peerReview;
    }

    @RequestMapping(value = "/urlValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateUrl(@RequestBody PeerReviewForm peerReview) {        
        if(peerReview.getUrl() == null) {
            peerReview.setUrl(Text.valueOf(StringUtils.EMPTY));
        }
        peerReview.getUrl().setErrors(new ArrayList<String>());
        validateUrl(peerReview.getUrl());
        return peerReview;
    }

    @RequestMapping(value = "/completionDateValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateCompletionDate(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getCompletionDate() == null) {
            peerReview.setCompletionDate(new Date());
        }
        
        peerReview.getCompletionDate().setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(peerReview.getCompletionDate().getMonth()) && PojoUtil.isEmpty(peerReview.getCompletionDate().getYear())) {
            setError(peerReview.getCompletionDate(), "common.dates.invalid");
        }
        
        return peerReview;
    }

    @RequestMapping(value = "/subject/subjectNameValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateSubjectName(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getSubjectName() == null) {
            peerReview.setSubjectName(Text.valueOf(StringUtils.EMPTY));
        }
        peerReview.getSubjectName().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(peerReview.getSubjectName())) {
            setError(peerReview.getSubjectName(), "common.title.not_blank");
        } else if (peerReview.getSubjectName().getValue().length() > 100) {
            setError(peerReview.getSubjectName(), "common.length_less_1000");
        }
        return peerReview;
    }

    @RequestMapping(value = "/subject/typeValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateSubjectType(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getSubjectType() == null) {
            peerReview.setSubjectType(Text.valueOf(StringUtils.EMPTY)); 
        }
        peerReview.getSubjectType().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(peerReview.getSubjectType())) {
            setError(peerReview.getSubjectType(), "peer_review.subject.work_type.not_blank");
        }
        return peerReview;
    }

    @RequestMapping(value = "/subject/urlValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateSubjectUrl(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getSubjectUrl() == null) {
            peerReview.setSubjectUrl(Text.valueOf(StringUtils.EMPTY));
        }
        peerReview.getSubjectUrl().setErrors(new ArrayList<String>());
        validateUrl(peerReview.getSubjectUrl());
        return peerReview;
    }

    @RequestMapping(value = "/subject/extIdsValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateSubjectExternalIdentifier(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getSubjectExternalIdentifier() == null) {
            peerReview.setSubjectExternalIdentifier(new WorkExternalIdentifier());
        }
        validateExternalIdentifier(peerReview.getSubjectExternalIdentifier());                
        return peerReview;
    }
    
    @RequestMapping(value = "/extIdsValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateExternalIdentifiers(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getExternalIdentifiers() == null) {
            return peerReview;
        }        
        for(WorkExternalIdentifier extId : peerReview.getExternalIdentifiers()) {
            validateExternalIdentifier(extId);
        }                
        return peerReview;
    }        
    
    private void validateExternalIdentifier(WorkExternalIdentifier extId) {
        //Init fields
        if(extId.getWorkExternalIdentifierId() == null)
            extId.setWorkExternalIdentifierId(new Text());
        if(extId.getWorkExternalIdentifierType() == null)
            extId.setWorkExternalIdentifierType(new Text());
        if(extId.getRelationship() == null) 
            extId.setRelationship(new Text());
        if(extId.getUrl() == null)
            extId.setUrl(new Text());
        extId.setErrors(new ArrayList<String>());
        extId.getWorkExternalIdentifierId().setErrors(new ArrayList<String>());
        extId.getWorkExternalIdentifierType().setErrors(new ArrayList<String>());
        extId.getRelationship().setErrors(new ArrayList<String>());
        extId.getUrl().setErrors(new ArrayList<String>());
        
        if(PojoUtil.isEmpty(extId.getWorkExternalIdentifierId()) && !PojoUtil.isEmpty(extId.getWorkExternalIdentifierType())) {
            //Please select a type
            setError(extId.getWorkExternalIdentifierId(), "NotBlank.currentWorkExternalIds.id");
        } else if(!PojoUtil.isEmpty(extId.getWorkExternalIdentifierId()) && PojoUtil.isEmpty(extId.getWorkExternalIdentifierType())) {
            //Please add a value
            setError(extId.getWorkExternalIdentifierType(), "NotBlank.currentWorkExternalIds.idType");
        } 
        
        if(!PojoUtil.isEmpty(extId.getWorkExternalIdentifierId()) && extId.getWorkExternalIdentifierId().getValue().length() > 2084) {
            setError(extId.getWorkExternalIdentifierId(), "manualWork.length_less_2084");
        }
    }
    
    @RequestMapping(value = "/roleValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateRole(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getRole() == null) {
            peerReview.setRole(Text.valueOf(StringUtils.EMPTY));
        }
        peerReview.getRole().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(peerReview.getRole())) {
            setError(peerReview.getRole(), "peer_review.role.not_blank");
        }

        return peerReview;
    }       

    @RequestMapping(value = "/typeValidate.json", method = RequestMethod.POST)
    public @ResponseBody PeerReviewForm validateType(@RequestBody PeerReviewForm peerReview) {
        if(peerReview.getType() == null){
            peerReview.setType(Text.valueOf(StringUtils.EMPTY));
        }
        peerReview.getType().setErrors(new ArrayList<String>());
        if (PojoUtil.isEmpty(peerReview.getType())) {
            setError(peerReview.getType(), "peer_review.type.not_blank");
        }

        return peerReview;
    }
    
    private void validateGroupId(PeerReviewForm peerReview) {
        if(peerReview.getGroupId() == null) {
            peerReview.setGroupId(Text.valueOf(StringUtils.EMPTY));
        }        
        peerReview.getGroupId().setErrors(new ArrayList<String>());
        
        if(!PojoUtil.isEmpty(peerReview.getGroupId())) {
            if(!groupIdRecordManager.exists(peerReview.getGroupId().getValue())) {
                setError(peerReview.getGroupId(), "peer_review.group_id.not_valid");
            }
        }
    }

    /**
     * Typeahead
     * */

    /**
     * Search DB for disambiguated affiliations to suggest to user
     */
    @RequestMapping(value = "/disambiguated/name/{query}", method = RequestMethod.GET)
    public @ResponseBody List<Map<String, String>> searchDisambiguated(@PathVariable("query") String query, @RequestParam(value = "limit") int limit) {
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
        datum.put("disambiguatedOrganizationIdentifier", Long.toString(orgDisambiguatedDocument.getOrgDisambiguatedId()));
        return datum;
    }

    /**
     * fetch disambiguated by id
     */
    @RequestMapping(value = "/disambiguated/id/{id}", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getDisambiguated(@PathVariable("id") Long id) {
        OrgDisambiguatedEntity orgDisambiguatedEntity = orgDisambiguatedDao.find(id);
        Map<String, String> datum = new HashMap<>();
        datum.put("value", orgDisambiguatedEntity.getName());
        datum.put("city", orgDisambiguatedEntity.getCity());
        datum.put("region", orgDisambiguatedEntity.getRegion());
        if (orgDisambiguatedEntity.getCountry() != null)
            datum.put("country", orgDisambiguatedEntity.getCountry().value());
        datum.put("sourceId", orgDisambiguatedEntity.getSourceId());
        datum.put("sourceType", orgDisambiguatedEntity.getSourceType());
        return datum;
    }

    public Locale getUserLocale() {
        return localeManager.getLocale();
    }

    @RequestMapping(value = "/updateToMaxDisplay.json", method = RequestMethod.GET)
    public @ResponseBody boolean updateToMaxDisplay(HttpServletRequest request, @RequestParam(value = "putCode") Long putCode) {
        return peerReviewManager.updateToMaxDisplay(getEffectiveUserOrcid(), putCode);
    }

    /**
     * updates visibility of a peer review
     * */
    @RequestMapping(value = "/{peerReviewIdsStr}/visibility/{visibilityStr}", method = RequestMethod.GET)
    public @ResponseBody ArrayList<Long> updateVisibilitys(@PathVariable("peerReviewIdsStr") String peerReviewIdsStr, @PathVariable("visibilityStr") String visibilityStr) {
        ArrayList<Long> peerReviewIds = new ArrayList<Long>();
        if (PojoUtil.isEmpty(peerReviewIdsStr)) {
            return peerReviewIds;
        }
        // make sure this is a users work
        String orcid = getEffectiveUserOrcid();
        for (String peerReviewId : peerReviewIdsStr.split(","))
            peerReviewIds.add(new Long(peerReviewId));
        peerReviewManager.updateVisibilities(orcid, peerReviewIds, Visibility.fromValue(visibilityStr));
        return peerReviewIds;
    }     
}
