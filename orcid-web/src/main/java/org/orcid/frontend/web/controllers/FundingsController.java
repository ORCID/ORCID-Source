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
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.jaxb.model.message.CurrencyCode;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingType;
import org.orcid.jaxb.model.message.Fundings;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.FundingExternalIdentifierDao;
import org.orcid.persistence.dao.OrgFundingRelationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.OrgFundingRelationEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.Contributor;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.FundingExternalIdentifierForm;
import org.orcid.pojo.ajaxForm.FundingForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.Visibility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Angel Montenegro
 */
@Controller("fundingsController")
@RequestMapping(value = { "/fundings" })
public class FundingsController extends BaseWorkspaceController {
	private static final Logger LOGGER = LoggerFactory.getLogger(FundingsController.class);
	private static final String FUNDING_MAP = "FUNDING_MAP";
	
	@Resource
    private ProfileDao profileDao;
	
	@Resource
	OrgFundingRelationDao orgFundingRelationDao;
	
	@Resource 
	FundingExternalIdentifierDao fundingExternalIdentifierDao;
	
	@Resource
    private Jaxb2JpaAdapter jaxb2JpaAdapter;
	
	/**
     * Returns a blank funding form
     * */
    @RequestMapping(value = "/funding.json", method = RequestMethod.GET)
    public @ResponseBody
    FundingForm getFunding(HttpServletRequest request) { 
    	FundingForm result = new FundingForm();
    	result.setAmount(new Text());    	
    	result.setCurrencyCode(new Text());
    	result.setDescription(new Text());
    	result.setDisambiguatedFundingSourceId(new Text());
    	result.setDisambiguationSource(new Text());    	    	
    	result.setFundingName(new Text());
    	result.setFundingType(new Text());
    	result.setSourceName(new String());    	
    	result.setTitle(new Text());
    	result.setUrl(new Text());    	
    	OrcidProfile profile = getEffectiveProfile();
        Visibility v = Visibility.valueOf(profile.getOrcidInternal().getPreferences().getWorkVisibilityDefault().getValue());
        result.setVisibility(v);    	
    	Date startDate = new Date();
    	result.setStartDate(startDate);
        startDate.setDay("");
        startDate.setMonth("");
        startDate.setYear("");
        Date endDate = new Date();
        result.setEndDate(endDate);
        endDate.setDay("");
        endDate.setMonth("");
        endDate.setYear("");    	
        
        List<Contributor> emptyContributors = new ArrayList<Contributor>();
        Contributor c = new Contributor();
        c.setOrcid(new Text());
        c.setEmail(new Text());
        c.setCreditName(new Text());
        c.setContributorRole(new Text());
        c.setContributorSequence(new Text());
        emptyContributors.add(c);
        result.setContributors(emptyContributors);
                
        List<FundingExternalIdentifierForm> emptyExternalIdentifiers = new ArrayList<FundingExternalIdentifierForm>();
        FundingExternalIdentifierForm f = new FundingExternalIdentifierForm();
        f.setPutCode(new Text());
        f.setType(new Text());
        f.setUrl(new Text());
        f.setValue(new Text());
        emptyExternalIdentifiers.add(f);
        result.setExternalIdentifiers(emptyExternalIdentifiers);
        
    	return result;
    }
    
    /**
     * List fundings associated with a profile
     * */
    @RequestMapping(value = "/fundingIds.json", method = RequestMethod.GET)
    public @ResponseBody
    List<String> getFundingsJson(HttpServletRequest request) {
        // Get cached profile
        List<String> fundingIds = createFundingIdList(request);
        return fundingIds;
    }

    /**
     * Create a funding id list and sorts a map associated with the list in
     * in the session
     * 
     */
    private List<String> createFundingIdList(HttpServletRequest request) {
        OrcidProfile currentProfile = getEffectiveProfile();
        Fundings fundings = currentProfile.getOrcidActivities() == null ? null : currentProfile.getOrcidActivities().getFundings();

        HashMap<String, FundingForm> fundingsMap = new HashMap<>();
        List<String> fundingIds = new ArrayList<String>();
        if (fundings != null) {
            for (Funding funding : fundings.getFundings()) {
                try {
                    FundingForm form = FundingForm.valueOf(funding);
                    if (funding.getType() != null) {
                        form.setFundingTypeForDisplay(getMessage(buildInternationalizationKey(FundingType.class, funding.getType().value())));
                    }
                    fundingsMap.put(funding.getPutCode(), form);
                    fundingIds.add(funding.getPutCode());
                } catch (Exception e) {
                    LOGGER.error("Failed to parse as Funding. Put code" + funding.getPutCode());
                }
            }
            request.getSession().setAttribute(FUNDING_MAP, fundingsMap);
        }
        return fundingIds;
    }
    
    /**
     * List fundings associated with a profile
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/fundings.json", method = RequestMethod.GET)
    public @ResponseBody
    List<FundingForm> getFundingJson(HttpServletRequest request, @RequestParam(value = "fundingIds") String fundingIdsStr) {
        List<FundingForm> fundingList = new ArrayList<>();
        FundingForm funding = null;
        String[] fundingIds = fundingIdsStr.split(",");

        if (fundingIds != null) {
            HashMap<String, FundingForm> fundingsMap = (HashMap<String, FundingForm>) request.getSession().getAttribute(FUNDING_MAP);
            // this should never happen, but just in case.
            if (fundingsMap == null) {
                createFundingIdList(request);
                fundingsMap = (HashMap<String, FundingForm>) request.getSession().getAttribute(FUNDING_MAP);
            }
            for (String fundingId : fundingIds) {
                funding = fundingsMap.get(fundingId);
                fundingList.add(funding);
            }
        }

        return fundingList;
    }
    
    
    /**
     * Persist a funding object on database
     * */
    @RequestMapping(value = "/funding.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm postFunding(HttpServletRequest request, FundingForm funding) {
    	validateAmount(funding);
    	validateCurrency(funding);
    	validateTitle(funding);
    	validateDescription(funding);   
    	validateUrl(funding);
    	validateDates(funding);
    	validateExternalIdentifiers(funding);
    	
    	copyErrors(funding.getAmount(), funding);
    	copyErrors(funding.getCurrencyCode(), funding);
    	copyErrors(funding.getTitle(), funding);
    	copyErrors(funding.getDescription(), funding);
    	copyErrors(funding.getUrl(), funding);
    	copyErrors(funding.getEndDate(), funding);
    	
    	for(FundingExternalIdentifierForm extId : funding.getExternalIdentifiers()){
    		copyErrors(extId.getType(), funding);
    		copyErrors(extId.getUrl(), funding);
    		copyErrors(extId.getValue(), funding);
    	}
    	
    	// If there are no errors, persist to DB
    	if (funding.getErrors().isEmpty()) {
    		ProfileEntity userProfile = profileDao.find(getEffectiveUserOrcid());
    		OrgFundingRelationEntity orgFundingRelationEntity = jaxb2JpaAdapter.getNewOrgFundingRelationEntity(funding.toFunding(), userProfile);
    		orgFundingRelationEntity.setSource(userProfile);
            orgFundingRelationDao.persist(orgFundingRelationEntity);
    	}
    	
    	return funding;
    }
    
    /**
     * Validators
     * */
    @RequestMapping(value = "/funding/amountValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateAmount(FundingForm funding) {    	
    	funding.getAmount().setErrors(new ArrayList<String>());
    	if(PojoUtil.isEmpty(funding.getAmount())) {
    		setError(funding.getAmount(), "NotBlank.funding.amount");
    	} else {
    		String amount = funding.getAmount().getValue();
    		long lAmount = 0;
    		try {
    			lAmount = Long.valueOf(amount);
    		} catch(NumberFormatException nfe) {
    			setError(funding.getAmount(), "Invalid.funding.amount");
    		}
    		
    		if(lAmount < 0)
    			setError(funding.getAmount(), "Invalid.funding.amount");
    	}
    	return funding;
    }
    
    @RequestMapping(value = "/funding/currencyValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateCurrency(FundingForm funding) {
    	funding.getCurrencyCode().setErrors(new ArrayList<String>());
    	if(PojoUtil.isEmpty(funding.getCurrencyCode())) {
    		setError(funding.getCurrencyCode(), "NotBlank.funding.currency");
    	} else {
    		try {
    			CurrencyCode.fromValue(funding.getCurrencyCode().getValue());
    		} catch(IllegalArgumentException iae) {
    			setError(funding.getCurrencyCode(), "NotValid.funding.currency");
    		}
    	}
    	return funding;
    }
    
    @RequestMapping(value = "/funding/titleValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateTitle(FundingForm funding) {
    	funding.getTitle().setErrors(new ArrayList<String>());
    	if(PojoUtil.isEmpty(funding.getTitle())) {
    		setError(funding.getTitle(), "NotBlank.funding.title");
    	} else {
    		if(funding.getTitle().getValue().length() > 1000)
    			setError(funding.getTitle(), "funding.length_less_1000");
    	}
    	return funding;
    }
    
    @RequestMapping(value = "/funding/descriptionValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateDescription(FundingForm funding) {
    	funding.getDescription().setErrors(new ArrayList<String>());
   		if(funding.getDescription().getValue().length() > 5000)
   			setError(funding.getDescription(), "funding.length_less_5000");
   		return funding;
    }
    
    @RequestMapping(value = "/funding/urlValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateUrl(FundingForm funding) {
    	funding.getUrl().setErrors(new ArrayList<String>());
    	if(funding.getUrl().getValue().length() > 350)
    		setError(funding.getUrl(), "funding.length_less_350");
    	return funding;
    }
    
    @RequestMapping(value = "/funding/datesValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateDates(@RequestBody FundingForm funding) {
    	funding.getStartDate().setErrors(new ArrayList<String>());
    	funding.getEndDate().setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(funding.getStartDate()) && !PojoUtil.isEmpty(funding.getEndDate())) {
            if (funding.getStartDate().toJavaDate().after(funding.getEndDate().toJavaDate()))
                setError(funding.getEndDate(), "funding.endDate.after");
        }
        return funding;
    }
    
    @RequestMapping(value = "/funding/externalIdentifiersValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    FundingForm validateExternalIdentifiers(@RequestBody FundingForm funding) {
    	if(funding.getExternalIdentifiers() != null && !funding.getExternalIdentifiers().isEmpty()) {
    		for(FundingExternalIdentifierForm extId : funding.getExternalIdentifiers()) {
    			if(!PojoUtil.isEmpty(extId.getType()) && extId.getType().getValue().length() > 255)
    				setError(extId.getType(), "funding.lenght_less_255");
    			if(!PojoUtil.isEmpty(extId.getUrl()) && extId.getUrl().getValue().length() > 350)
    				setError(extId.getUrl(), "funding.length_less_350");
    			if(!PojoUtil.isEmpty(extId.getValue()) && extId.getValue().getValue().length() > 2084)
    				setError(extId.getValue(), "funding.length_less_2084");
    		}
    	}
    	return funding;
    }
}






