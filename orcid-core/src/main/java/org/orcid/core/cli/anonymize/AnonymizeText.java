package org.orcid.core.cli.anonymize;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.release.common.Contributor;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.WorkContributors;
import org.orcid.pojo.ajaxForm.ActivityExternalIdentifier;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;
import org.orcid.core.contributors.roles.works.WorkContributorRoleConverter;

public class AnonymizeText {

    private static String protocolRegex = "^(?i)(https?|ftp)://.*$";
    private static final String KEY_RELATIONSHIP = "relationship";
    private static final String KEY_URL = "url";
    private static final String KEY_VALUE = "value";
    private static final String KEY_WORK_EXTERNAL_IDENTIFIER_TYPE = "workExternalIdentifierType";
    private static final String KEY_WORK_EXTERNAL_IDENTIFIER_ID = "workExternalIdentifierId";
    private static final String KEY_CONTENT = "content";

    private static final String KEY_CONTRIBUTOR_ORCID = "contributorOrcid";
    private static final String KEY_URI = "uri";
    private static final String KEY_PATH = "path";
    private static final String KEY_HOST = "host";
    private static final String KEY_CREDIT_NAME = "creditName";
    private static final String KEY_CONTRIBUTOR_EMAIL = "contributorEmail";
    private static final String KEY_CONTRIBUTOR_ATTRIBUTES = "contributorAttributes";
    private static final String KEY_CONTRIBUTOR_SEQUENCE = "contributorSequence";
    private static final String KEY_CONTRIBUTOR_ROLE = "contributorRole";

    private static final HashMap<String, ExternalID> extIdsAnonymized = new HashMap<String, ExternalID>();
    private static final HashMap<String, String> contributorNamesAnonymized = new HashMap<String, String>();
    private static final HashMap<String, String> contributorOrcidsAnonymized = new HashMap<String, String>();
    private static final HashMap<String, String> contributorEmailsAnonymized = new HashMap<String, String>();
    private static final String KEY_SEPARATOR_EXT_ID = "::";

    public String anonymizeString(String s) {
        if (StringUtils.isBlank(s)) {
            return "";
        }

        List<Character> l = new ArrayList<Character>();
        for (char c : s.toCharArray())
            l.add(c);
        Collections.shuffle(l); // shuffle the list
        StringBuilder sb = new StringBuilder();
        for (char c : l)
            sb.append(c);
        return sb.toString();

    }

    public String anonymizeURL(String url) throws MalformedURLException {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        // just in case invalid URL anonymize it as string
        if (!isValidURL(url)) {
            return anonymizeString(url);
        }

        String filePath = getFileFromURL(url);

        String domain = url.substring(0, url.length() - filePath.length());
        if (StringUtils.isNotBlank(filePath)) {
            filePath = filePath.substring(1);
            domain = domain + "/";
        }
        return domain + anonymizeString(filePath);
    }

    public ExternalID anonymizeWorkExternalIdentifier(JSONObject original) throws MalformedURLException, JSONException {
        if (original == null) {
            return null;
        }

        String workIdentifierType = null;
        String workIdentifierId = null;
        if (original.has(KEY_WORK_EXTERNAL_IDENTIFIER_TYPE)) {
            workIdentifierType = original.getString(KEY_WORK_EXTERNAL_IDENTIFIER_TYPE);
        }
        if (original.has(KEY_WORK_EXTERNAL_IDENTIFIER_ID) && !original.isNull(KEY_WORK_EXTERNAL_IDENTIFIER_ID)) {
            JSONObject extIdObj = original.getJSONObject(KEY_WORK_EXTERNAL_IDENTIFIER_ID);
            if (extIdObj.has(KEY_CONTENT)) {
                workIdentifierId = extIdObj.getString(KEY_CONTENT);
            }
        }

        String extIdKey = null;
        if (workIdentifierType != null && workIdentifierId != null) {
            extIdKey = workIdentifierType + KEY_SEPARATOR_EXT_ID + workIdentifierId;
        }

        ExternalID wExtId = new ExternalID();
        if (extIdKey != null) {
            if (extIdsAnonymized.get(extIdKey) != null) {
                wExtId = extIdsAnonymized.get(extIdKey);
                if (original.has(KEY_RELATIONSHIP)) {
                    wExtId.setRelationship(Relationship.valueOf(original.getString(KEY_RELATIONSHIP)));
                }
                return wExtId;
            }
        }
        if (workIdentifierType != null) {
            wExtId.setType(workIdentifierType);
        }

        if (original.has(KEY_WORK_EXTERNAL_IDENTIFIER_ID) && !original.isNull(KEY_WORK_EXTERNAL_IDENTIFIER_ID)) {
            JSONObject extIdObj = original.getJSONObject(KEY_WORK_EXTERNAL_IDENTIFIER_ID);
            if (extIdObj.has(KEY_CONTENT)) {
                wExtId.setValue(anonymizeURL(workIdentifierId));
            }

        }

        if (original.has(KEY_RELATIONSHIP)) {
            wExtId.setRelationship(Relationship.valueOf(original.getString(KEY_RELATIONSHIP)));
        }
        if (original.has(KEY_URL)) {

            if (!original.isNull(KEY_URL)) {

                JSONObject urlObj = original.getJSONObject(KEY_URL);
                if (urlObj.has(KEY_VALUE)) {
                    wExtId.setUrl(new org.orcid.jaxb.model.v3.release.common.Url(anonymizeURL(urlObj.getString(KEY_VALUE))));
                }
            }
        }

        if (extIdKey != null) {
            extIdsAnonymized.put(extIdKey, wExtId);
        }

        return wExtId;
    }

    public ExternalID anonymizeWorkExternalIdentifier(ExternalID original) throws MalformedURLException, JSONException {
        if (original == null) {
            return null;
        }
        String extIdKey = null;
        if (original.getType() != null && original.getValue() != null) {
            extIdKey = original.getType() + KEY_SEPARATOR_EXT_ID + original.getValue();
        }
        ExternalID wExtId = new ExternalID();
        if (extIdKey != null) {
            if (extIdsAnonymized.get(extIdKey) != null) {
                wExtId = extIdsAnonymized.get(extIdKey);
                if (original.getRelationship() != null) {
                    wExtId.setRelationship(original.getRelationship());
                }
                return wExtId;
            }
        }

        if (original.getType() != null) {
            wExtId.setType(original.getType());
        }

        if (original.getValue() != null) {
            wExtId.setValue(anonymizeURL(original.getValue()));

        }

        if (original.getRelationship() != null) {
            wExtId.setRelationship(original.getRelationship());
        }
        if (original.getUrl() != null) {

            wExtId.setUrl(new org.orcid.jaxb.model.v3.release.common.Url(anonymizeURL(original.getUrl().getValue())));
        }

        if (extIdKey != null) {
            extIdsAnonymized.put(extIdKey, wExtId);
        }

        return wExtId;
    }

    public ExternalIDs anonymizeWorkExternalIdentifiers(ExternalIDs extIds) throws MalformedURLException, JSONException {
        ExternalIDs workExternalIds = new ExternalIDs();
        if (extIds != null) {

            for (ExternalID extId : workExternalIds.getExternalIdentifier()) {
                workExternalIds.getExternalIdentifier().add(anonymizeWorkExternalIdentifier(extId));
            }
        }
        return workExternalIds;

    }

    public ExternalIDs anonymizeWorkExternalIdentifiers(JSONArray extArray) throws MalformedURLException, JSONException {
        ExternalIDs workExternalIds = new ExternalIDs();
        if (extArray != null) {

            for (int i = 0; i < extArray.length(); i++) {
                if (extArray.getJSONObject(i) != null) {
                    workExternalIds.getExternalIdentifier().add(anonymizeWorkExternalIdentifier(extArray.getJSONObject(i)));
                }
            }
        }
        return workExternalIds;

    }

    public Contributor anonymizeWorkContributor(JSONObject original) throws JSONException, MalformedURLException {
        if (original == null) {
            return null;
        }

        Contributor newContributor = new Contributor();
        if (original.has(KEY_CONTRIBUTOR_ORCID) && !original.isNull(KEY_CONTRIBUTOR_ORCID)) {

            org.orcid.jaxb.model.v3.release.common.ContributorOrcid contributorOrcid = new org.orcid.jaxb.model.v3.release.common.ContributorOrcid();

            JSONObject origContributorOrcid = original.getJSONObject(KEY_CONTRIBUTOR_ORCID);

            if (origContributorOrcid.has(KEY_URI) && !origContributorOrcid.isNull(KEY_URI)) {

                contributorOrcid.setUri(anonymizeURL(origContributorOrcid.getString(KEY_URI)));

            }

            if (origContributorOrcid.has(KEY_PATH) && !origContributorOrcid.isNull(KEY_PATH)) {
                if(!contributorOrcidsAnonymized.containsKey(origContributorOrcid.get(KEY_PATH))) {
                    contributorOrcidsAnonymized.put(origContributorOrcid.getString(KEY_PATH), anonymizeString(origContributorOrcid.getString(KEY_PATH)));
                }

                contributorOrcid.setPath(contributorOrcidsAnonymized.get(origContributorOrcid.getString(KEY_PATH)));

            }

            if (origContributorOrcid.has(KEY_HOST) && !origContributorOrcid.isNull(KEY_HOST)) {

                contributorOrcid.setHost(anonymizeString(origContributorOrcid.getString(KEY_HOST)));

            }

            newContributor.setContributorOrcid(contributorOrcid);

        }

        if (original.has(KEY_CONTRIBUTOR_EMAIL)) {
            if (!original.isNull(KEY_CONTRIBUTOR_EMAIL)) {
                org.orcid.jaxb.model.v3.release.common.ContributorEmail email = new org.orcid.jaxb.model.v3.release.common.ContributorEmail();
                if(!contributorEmailsAnonymized.containsKey(original.getString(KEY_CONTRIBUTOR_EMAIL))) {
                    
                    contributorEmailsAnonymized.put(original.getString(KEY_CONTRIBUTOR_EMAIL), anonymizeString(original.getString(KEY_CONTRIBUTOR_EMAIL)));
                }
                email.setValue(contributorEmailsAnonymized.get(original.getString(KEY_CONTRIBUTOR_EMAIL)));
                newContributor.setContributorEmail(email);
            }
        }

        if (original.has(KEY_CREDIT_NAME)) {
            if (!original.isNull(KEY_CREDIT_NAME)) {
                JSONObject creditNameObj = original.getJSONObject(KEY_CREDIT_NAME);
                if (creditNameObj.has(KEY_CONTENT)) {

                    org.orcid.jaxb.model.v3.release.common.CreditName creditName = new org.orcid.jaxb.model.v3.release.common.CreditName();
                    if(!contributorNamesAnonymized.containsKey(creditNameObj.getString(KEY_CONTENT))) {
                        contributorNamesAnonymized.put(creditNameObj.getString(KEY_CONTENT), anonymizeString(creditNameObj.getString(KEY_CONTENT)));
                    }
                    
                    creditName.setContent(contributorNamesAnonymized.get(creditNameObj.getString(KEY_CONTENT))); 
                    newContributor.setCreditName(creditName);
                }
            }
        }

        // no need to anonymize attributes
        org.orcid.jaxb.model.v3.release.common.ContributorAttributes contributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        if (original.has(KEY_CONTRIBUTOR_ATTRIBUTES)) {

            if (!original.isNull(KEY_CONTRIBUTOR_ATTRIBUTES)) {
                if (original.getJSONObject(KEY_CONTRIBUTOR_ATTRIBUTES).has(KEY_CONTRIBUTOR_SEQUENCE)) {
                    if (!original.getJSONObject(KEY_CONTRIBUTOR_ATTRIBUTES).isNull(KEY_CONTRIBUTOR_SEQUENCE)) {
                        contributorAttributes.setContributorSequence(
                                org.orcid.jaxb.model.common.SequenceType.valueOf(original.getJSONObject(KEY_CONTRIBUTOR_ATTRIBUTES).getString(KEY_CONTRIBUTOR_SEQUENCE)));
                    }
                }

                if (original.getJSONObject(KEY_CONTRIBUTOR_ATTRIBUTES).has(KEY_CONTRIBUTOR_ROLE)) {
                    if (!original.getJSONObject(KEY_CONTRIBUTOR_ATTRIBUTES).isNull(KEY_CONTRIBUTOR_ROLE)) {
                        String dbRole = original.getJSONObject(KEY_CONTRIBUTOR_ATTRIBUTES).getString(KEY_CONTRIBUTOR_ROLE);
                        WorkContributorRoleConverter roleConverter = new WorkContributorRoleConverter();
                        contributorAttributes.setContributorRole(roleConverter.toRoleValue(dbRole));
                    }
                }
            }
            newContributor.setContributorAttributes(contributorAttributes);
        }

        return newContributor;
    }

    public Contributor anonymizeWorkContributor(Contributor original) throws JSONException, MalformedURLException {
        if (original == null) {
            return null;
        }

        Contributor newContributor = new Contributor();
        if (original.getContributorOrcid() != null) {

            org.orcid.jaxb.model.v3.release.common.ContributorOrcid contributorOrcid = new org.orcid.jaxb.model.v3.release.common.ContributorOrcid();

            if (!PojoUtil.isEmpty(original.getContributorOrcid().getUri())) {

                contributorOrcid.setUri(anonymizeURL(original.getContributorOrcid().getUri()));

            }

            if (!PojoUtil.isEmpty(original.getContributorOrcid().getPath())) {
                if(!contributorOrcidsAnonymized.containsKey(original.getContributorOrcid().getPath())) {
                    
                    contributorOrcidsAnonymized.put(original.getContributorOrcid().getPath(), anonymizeString(original.getContributorOrcid().getPath()));
                }

                contributorOrcid.setPath(contributorOrcidsAnonymized.get(original.getContributorOrcid().getPath()));

            }

            if (!PojoUtil.isEmpty(original.getContributorOrcid().getHost())) {

                contributorOrcid.setHost(anonymizeString(original.getContributorOrcid().getHost()));

            }

            newContributor.setContributorOrcid(contributorOrcid);

        }

        if (original.getContributorEmail() != null && !PojoUtil.isEmpty(original.getContributorEmail().getValue())) {

            org.orcid.jaxb.model.v3.release.common.ContributorEmail email = new org.orcid.jaxb.model.v3.release.common.ContributorEmail();
            if(!contributorEmailsAnonymized.containsKey(original.getContributorEmail().getValue())) {
                
                contributorEmailsAnonymized.put(original.getContributorEmail().getValue(), anonymizeString(original.getContributorEmail().getValue()));
            }
            email.setValue(contributorEmailsAnonymized.get(original.getContributorEmail().getValue()));
            newContributor.setContributorEmail(email);

        }

        if (original.getCreditName() != null && !PojoUtil.isEmpty(original.getCreditName().getContent())) {
            org.orcid.jaxb.model.v3.release.common.CreditName creditName = new org.orcid.jaxb.model.v3.release.common.CreditName();
            if(!contributorNamesAnonymized.containsKey(original.getCreditName().getContent())) {
                
                contributorNamesAnonymized.put(original.getCreditName().getContent(), anonymizeString(original.getCreditName().getContent()));
            }
            creditName.setContent(contributorNamesAnonymized.get(original.getCreditName().getContent()));
            newContributor.setCreditName(creditName);

        }

        // no need to anonymize attributes
        org.orcid.jaxb.model.v3.release.common.ContributorAttributes contributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
        if (original.getContributorAttributes() != null) {
            if (original.getContributorAttributes().getContributorSequence() != null)
                contributorAttributes.setContributorSequence(original.getContributorAttributes().getContributorSequence());

            if (original.getContributorAttributes().getContributorRole() != null)
                contributorAttributes.setContributorRole(original.getContributorAttributes().getContributorRole());

            newContributor.setContributorAttributes(contributorAttributes);
        }

        return newContributor;
    }

    public WorkContributors anonymizeWorkContributors(WorkContributors originalContributors) throws JSONException, MalformedURLException {
        org.orcid.jaxb.model.v3.release.record.WorkContributors contributors = new org.orcid.jaxb.model.v3.release.record.WorkContributors();

        if (originalContributors != null) {

            for (Contributor contributor : originalContributors.getContributor()) {

                contributors.getContributor().add(anonymizeWorkContributor(contributor));

            }
        }
        return contributors;
    }

    public WorkContributors anonymizeWorkContributors(JSONArray contrArray) throws JSONException, MalformedURLException {
        org.orcid.jaxb.model.v3.release.record.WorkContributors contributors = new org.orcid.jaxb.model.v3.release.record.WorkContributors();

        if (contrArray != null) {

            for (int i = 0; i < contrArray.length(); i++) {
                if (contrArray.getJSONObject(i) != null) {
                    contributors.getContributor().add(anonymizeWorkContributor(contrArray.getJSONObject(i)));
                }
            }
        }

        return contributors;
    }

    private String getFileFromURL(String url) throws MalformedURLException {
        if (!url.matches(protocolRegex)) {
            url = "http://" + url;
        }

        URL aURL = new URL(url);
        if (url.contains("#"))
            return aURL.getFile() + "#" + aURL.getRef();
        else
            return aURL.getFile();
    }

    private static boolean isValidURL(String url) {
        if (StringUtils.isNotBlank(url)) {
            return url.matches(OrcidStringUtils.URL_REGEXP);
        } else {
            return false;
        }
    }

}
