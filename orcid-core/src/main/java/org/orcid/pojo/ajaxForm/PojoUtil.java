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
package org.orcid.pojo.ajaxForm;

import org.orcid.jaxb.model.message.FuzzyDate;

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
	
	public static boolean isEmpty(Text text) {
        if (text == null || text.getValue() == null || text.getValue().trim().isEmpty()) return true;
        return false;
    }

    
    public static boolean isEmpty(String string) {
        if (string == null || string.trim().isEmpty()) return true;
        return false;
    }
    
    public static String createDateSortString(FuzzyDate start, FuzzyDate end) {
        String year = "0";
        String month = "0";
        String day = "0";
        if (start != null && start.getYear() != null && !PojoUtil.isEmpty(start.getYear().getValue())) {
            year = start.getYear().getValue();
            if (start.getMonth() != null && !PojoUtil.isEmpty(start.getMonth().getValue()))
                month = start.getMonth().getValue();
            if (start.getDay() != null && !PojoUtil.isEmpty(start.getDay().getValue()))
                day = start.getDay().getValue();
        } else if (end != null && end.getYear() != null && !PojoUtil.isEmpty(end.getYear().getValue())) {
            year = end.getYear().getValue();
            if (end.getMonth() != null && !PojoUtil.isEmpty(end.getMonth().getValue()))
                month = end.getMonth().getValue();
            if (end.getDay() != null && !PojoUtil.isEmpty(end.getDay().getValue()))
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
    
    public static boolean isEmtpy(Contributor c) {
    	return PojoUtil.areAllEmtpy(c.getContributorSequence(), c.getEmail(), c.getOrcid(), c.getUri(), c.getCreditName(), c.getContributorRole());
    }

    public static boolean isEmtpy(FundingExternalIdentifierForm g) {
    	return PojoUtil.areAllEmtpy(g.getType(), g.getValue(), g.getUrl());
    }

}
