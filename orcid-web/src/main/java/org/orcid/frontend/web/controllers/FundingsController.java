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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.pojo.ajaxForm.Contributor;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.FundingExternalIdentifierForm;
import org.orcid.pojo.ajaxForm.FundingForm;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.Visibility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Angel Montenegro
 */
@Controller("fundingsController")
@RequestMapping(value = { "/fundings" })
public class FundingsController extends BaseWorkspaceController {
	private static final Logger LOGGER = LoggerFactory.getLogger(FundingsController.class);
	
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
}
