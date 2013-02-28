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
package org.orcid.core.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.orcid.utils.DateUtils;
import org.orcid.utils.NullUtils;

public class PasswordResetToken {

  //  public static final String RESET_TOKEN_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private static final String EMAIL_PARAM_KEY = "email";
    private static final String ISSUE_DATE_PARAM_KEY = "issueDate";
    private static final String EQUALS = "=";
    private static final String SEPARATOR = "&";

    private String email;
    private XMLGregorianCalendar issueDate;

    public PasswordResetToken() {

    }

    public PasswordResetToken(String paramsString) {
        String[] pairs = StringUtils.split(paramsString, SEPARATOR);
        Map<String, String> params = new HashMap<String, String>();
        for (String pair : pairs) {
            String[] keyValue = StringUtils.split(pair, EQUALS);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        email = params.get(EMAIL_PARAM_KEY);       
        issueDate=DateUtils.convertToXMLGregorianCalendar(params.get(ISSUE_DATE_PARAM_KEY));
        
    }   
    
    public String getEmail() {     
        return email;
    }
    
    public Date getIssueDate() {
        return issueDate.toGregorianCalendar().getTime();
    }

    /**
     * 
     * @return The params encoded as a single string, as if in a URL query. The string is not url encoded because will
     *         be encrypted by a manager first. email=?&gNames=?&fName=?&sponsor=?&identifier=?&institution=?
     */
    public String toParamsString() {
        List<Pair<String, String>> pairs = new ArrayList<Pair<String, String>>();
        pairs.add(new ImmutablePair<String, String>(EMAIL_PARAM_KEY, email));
        pairs.add(new ImmutablePair<String, String>(ISSUE_DATE_PARAM_KEY, String.valueOf(issueDate)));

        List<String> items = new ArrayList<String>(pairs.size());
        for (Pair<String, String> pair : pairs) {
            items.add(pair.getLeft() + EQUALS + NullUtils.blankIfNull(pair.getRight()));
        }

        return StringUtils.join(items, SEPARATOR);
    }

    @Override
    public String toString() {
        return toParamsString();
    }

}
