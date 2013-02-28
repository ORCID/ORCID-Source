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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.orcid.utils.NullUtils;

/**
 * 
 * @author Will Simpson
 * 
 */
public class VerifyRegistrationToken {

    private static final String EMAIL_PARAM_KEY = "email";
    private static final String FAMILY_NAME_PARAM_KEY = "fName";
    private static final String GIVEN_NAMES_PARAM_KEY = "gNames";
    private static final String VOCATIVE_NAME_PARAM_KEY = "vName";
    private static final String SPONSOR_NAME_PARAM_KEY = "sponsor";
    private static final String SPONSOR_ID_PARAM_KEY = "identifier";
    private static final String INSTITUTION_NAME_PARAM_KEY = "institution";
    private static final String EQUALS = "=";
    private static final String SEPARATOR = "&";

    private String email;
    private String familyName;
    private String givenNames;
    private String vocativeName;
    private String sponsorName;
    private String sponsorId;
    private String institutionName;

    public VerifyRegistrationToken() {
    }

    public VerifyRegistrationToken(String paramsString) {
        String[] pairs = StringUtils.split(paramsString, SEPARATOR);
        Map<String, String> params = new HashMap<String, String>();
        for (String pair : pairs) {
            String[] keyValue = StringUtils.split(pair, EQUALS);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        email = params.get(EMAIL_PARAM_KEY);
        familyName = params.get(FAMILY_NAME_PARAM_KEY);
        givenNames = params.get(GIVEN_NAMES_PARAM_KEY);
        vocativeName = params.get(VOCATIVE_NAME_PARAM_KEY);
        sponsorName = params.get(SPONSOR_NAME_PARAM_KEY);
        sponsorId = params.get(SPONSOR_ID_PARAM_KEY);
        institutionName = params.get(INSTITUTION_NAME_PARAM_KEY);
    }   

    public String getEmail() {
        return email;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getGivenNames() {
        return givenNames;
    }

    public String getVocativeName() {
        return vocativeName;
    }

    public void setVocativeName(String vocativeName) {
        this.vocativeName = vocativeName;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public String getSponsorId() {
        return sponsorId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    /**
     * 
     * @return The params encoded as a single string, as if in a URL query. The
     *         string is not url encoded because will be encrypted by a manager
     *         first.
     *         email=?&gNames=?&fName=?&sponsor=?&identifier=?&institution=?
     */
    public String toParamsString() {
        List<Pair<String, String>> pairs = new ArrayList<Pair<String, String>>();
        pairs.add(new ImmutablePair<String, String>(EMAIL_PARAM_KEY, email));
        pairs.add(new ImmutablePair<String, String>(GIVEN_NAMES_PARAM_KEY, givenNames));
        pairs.add(new ImmutablePair<String, String>(FAMILY_NAME_PARAM_KEY, familyName));
        pairs.add(new ImmutablePair<String, String>(VOCATIVE_NAME_PARAM_KEY, vocativeName));
        pairs.add(new ImmutablePair<String, String>(SPONSOR_NAME_PARAM_KEY, sponsorName));
        pairs.add(new ImmutablePair<String, String>(SPONSOR_ID_PARAM_KEY, sponsorId));
        pairs.add(new ImmutablePair<String, String>(INSTITUTION_NAME_PARAM_KEY, institutionName));

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
