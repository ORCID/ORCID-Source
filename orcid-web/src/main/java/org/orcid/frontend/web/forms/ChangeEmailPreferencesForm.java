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

public class ChangeEmailPreferencesForm {

    private boolean sendOrcidNews;

    private boolean sendOrcidChangeNotifcations;

    public boolean getSendOrcidNews() {
        return sendOrcidNews;
    }

    public void setSendOrcidNews(boolean sendOrcidNews) {
        this.sendOrcidNews = sendOrcidNews;
    }

    public boolean getSendOrcidChangeNotifcations() {
        return sendOrcidChangeNotifcations;
    }

    public void setSendOrcidChangeNotifcations(boolean sendOrcidChangeNotifcations) {
        this.sendOrcidChangeNotifcations = sendOrcidChangeNotifcations;
    }

}
