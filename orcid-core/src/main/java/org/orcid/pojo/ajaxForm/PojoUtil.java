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
package org.orcid.pojo.ajaxForm;

import org.orcid.jaxb.model.message.Day;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.Month;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.UrlName;
import org.orcid.jaxb.model.message.Year;

public class PojoUtil {
	
    public static boolean anyIsEmtpy(Text ...texts) {
    	for(Text text : texts){
    		if(isEmpty(text)){
    			return true;
    		}
    	}
    	return false;
    }
    
    public static boolean areAllEmtpy(Text ...texts) {
    	for(Text text : texts){
    		if(!isEmpty(text)){
    			return false;
    		}
    	}
    	return true;
    }
    
    public static boolean areAllEmpty(String ... strings) {
        for(String s : strings) {
            if(!isEmpty(s)) {
                return false;
            }
        }
        return true;
    }
	
    public static boolean isEmpty(Text text) {
        if (text == null || text.getValue() == null || text.getValue().trim().isEmpty()) return true;
        return false;
    }

    public static boolean isEmpty(Url url) {
        if (url == null || url.getValue() == null || url.getValue().trim().isEmpty()) return true;
        return false;
    }

    public static boolean isEmpty(org.orcid.jaxb.model.common_rc4.Url url) {
        if (url == null || url.getValue() == null || url.getValue().trim().isEmpty()) return true;
        return false;
    }
    
    public static boolean isEmpty(UrlName urlName) {
        if (urlName == null || urlName.getContent() == null) return true;
        return false;
    }

    public static boolean isEmpty(String string) {
        if (string == null || string.trim().isEmpty()) return true;
        return false;
    }
    
    @Deprecated
    public static String createDateSortString(FuzzyDate start, FuzzyDate end) {
        String year = "0";
        String month = "0";
        String day = "0";
        if (!isEmpty(start) && !isEmpty(start.getYear())) {
            year = start.getYear().getValue();
            if (!PojoUtil.isEmpty(start.getMonth()))
                month = start.getMonth().getValue();
            if (!PojoUtil.isEmpty(start.getDay()))
                day = start.getDay().getValue();
        } else if (!isEmpty(end) && !isEmpty(end.getYear())) {
            year = end.getYear().getValue();
            if (!PojoUtil.isEmpty(end.getMonth()))
                month = end.getMonth().getValue();
            if (!PojoUtil.isEmpty(end.getDay()))
                day = end.getDay().getValue();
        }
        return year + "-" + month + '-' + day;
    }
    
    public static String createDateSortString(org.orcid.jaxb.model.common_rc4.FuzzyDate start, org.orcid.jaxb.model.common_rc4.FuzzyDate end) {
        String year = "0";
        String month = "0";
        String day = "0";
        if (!isEmpty(start) && !isEmpty(start.getYear())) {
            year = start.getYear().getValue();
            if (!PojoUtil.isEmpty(start.getMonth()))
                month = start.getMonth().getValue();
            if (!PojoUtil.isEmpty(start.getDay()))
                day = start.getDay().getValue();
        } else if (!isEmpty(end) && !isEmpty(end.getYear())) {
            year = end.getYear().getValue();
            if (!PojoUtil.isEmpty(end.getMonth()))
                month = end.getMonth().getValue();
            if (!PojoUtil.isEmpty(end.getDay()))
                day = end.getDay().getValue();
        }
        return year + "-" + month + '-' + day;
    }        
    
    public static boolean isEmpty(Date date) {
        if (date == null) return true;
        if (!PojoUtil.isEmpty(date.getDay()))
            return false;
        if (!PojoUtil.isEmpty(date.getMonth()))
            return false;
        if (!PojoUtil.isEmpty(date.getYear()))
            return false;
        return true;
    }
    
    public static boolean isEmpty(FuzzyDate date) {
        if (date == null) return true;
        if (!isEmpty(date.getDay()))
            return false;
        if (!isEmpty(date.getMonth()))
            return false;
        if (!isEmpty(date.getYear()))
            return false;
        return true;
    }
    
    public static boolean isEmpty(org.orcid.jaxb.model.common_rc4.FuzzyDate date) {
        if (date == null) return true;
        if (!isEmpty(date.getDay()))
            return false;
        if (!isEmpty(date.getMonth()))
            return false;
        if (!isEmpty(date.getYear()))
            return false;
        return true;
    }
    
    public static boolean isEmpty(Year year) {
        if (year==null) return true;
        return isEmpty(year.getValue());
    }
    
    public static boolean isEmpty(Day day) {
        if (day==null) return true;
        return isEmpty(day.getValue());
    }

    public static boolean isEmpty(Month month) {
        if (month==null) return true;
        return isEmpty(month.getValue());
    }

    public static boolean isEmpty(org.orcid.jaxb.model.common_rc4.Year year) {
        if (year==null) return true;
        return isEmpty(year.getValue());
    }
    
    public static boolean isEmpty(org.orcid.jaxb.model.common_rc4.Day day) {
        if (day==null) return true;
        return isEmpty(day.getValue());
    }

    public static boolean isEmpty(org.orcid.jaxb.model.common_rc4.Month month) {
        if (month==null) return true;
        return isEmpty(month.getValue());
    }
    
    public static boolean isEmtpy(Contributor c) {
    	return PojoUtil.areAllEmtpy(c.getContributorSequence(), c.getEmail(), c.getOrcid(), c.getUri(), c.getCreditName(), c.getContributorRole());
    }

    public static boolean isEmtpy(FundingExternalIdentifierForm g) {
    	return PojoUtil.areAllEmtpy(g.getType(), g.getValue(), g.getUrl());
    }
    
    public static boolean isEmpty(org.orcid.jaxb.model.message.ContributorOrcid contributorOrcid) {
        if(contributorOrcid == null) return true;
        return isEmpty(contributorOrcid.getPath());
    }

    public static boolean isEmpty(org.orcid.jaxb.model.common_rc4.ContributorOrcid contributorOrcid) {
        if(contributorOrcid == null) return true;
        return isEmpty(contributorOrcid.getPath());
    }
    
    public static boolean isEmpty(WorkExternalIdentifier workExternalId) {
        if(workExternalId == null) return true;
        return areAllEmtpy(workExternalId.getRelationship(), workExternalId.getUrl(), workExternalId.getWorkExternalIdentifierId(), workExternalId.getWorkExternalIdentifierType());
    }
    
    public static boolean isEmpty(TranslatedTitleForm translatedTitle) {
        if(translatedTitle == null) return true;
        return areAllEmpty(translatedTitle.getContent(), translatedTitle.getLanguageCode());
    }
}
