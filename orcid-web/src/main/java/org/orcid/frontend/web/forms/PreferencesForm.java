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
package org.orcid.frontend.web.forms;

import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.SendChangeNotifications;
import org.orcid.jaxb.model.message.SendOrcidNews;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Will Simpson
 */
public class PreferencesForm {

    private String orcid;
    private List<String> sendChangeNotifications;
    private List<String> sendOrcidNews;

    public PreferencesForm() {
    }

    public PreferencesForm(OrcidProfile orcidProfile) {
        orcid = orcidProfile.getOrcidIdentifier().getPath();

        OrcidInternal orcidInternal = orcidProfile.getOrcidInternal();

        Preferences preferences = orcidInternal != null ? orcidInternal.getPreferences() : null;

        List<String> selected = Arrays.asList(new String[] { "sendMe" });
        List<String> notSelected = Collections.emptyList();

        sendChangeNotifications = (preferences == null || preferences.getSendChangeNotifications() == null) ? notSelected : selected;
        sendOrcidNews = (preferences == null || preferences.getSendOrcidNews() == null) ? notSelected : selected;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public List<String> getSendChangeNotifications() {
        return sendChangeNotifications;
    }

    public void setSendChangeNotifications(List<String> sendChangeNotifications) {
        this.sendChangeNotifications = sendChangeNotifications;
    }

    public List<String> getSendOrcidNews() {
        return sendOrcidNews;
    }

    public void setSendOrcidNews(List<String> sendOrcidNews) {
        this.sendOrcidNews = sendOrcidNews;
    }

    public OrcidProfile getOrcidProfile() {
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcidIdentifier(orcid);
        OrcidInternal internal = new OrcidInternal();
        orcidProfile.setOrcidInternal(internal);
        Preferences preferences = new Preferences();
        internal.setPreferences(preferences);
        preferences.setSendChangeNotifications((sendChangeNotifications != null && !sendChangeNotifications.isEmpty()) ? new SendChangeNotifications(true) : null);
        preferences.setSendOrcidNews((sendOrcidNews != null && !sendOrcidNews.isEmpty()) ? new SendOrcidNews(true) : null);
        return orcidProfile;
    }

}
