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
package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.message.ActivitiesVisibilityDefault;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.SendChangeNotifications;
import org.orcid.jaxb.model.message.SendOrcidNews;
import org.orcid.jaxb.model.message.WorkVisibilityDefault;
import org.orcid.pojo.ajaxForm.ErrorsInterface;
import org.springframework.validation.ObjectError;

public class Preferences extends org.orcid.jaxb.model.message.Preferences implements ErrorsInterface {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    public Preferences() {
        this.setSendChangeNotifications(new SendChangeNotifications());
        this.setSendOrcidNews(new SendOrcidNews());
        this.setWorkVisibilityDefault(new WorkVisibilityDefault());
        this.setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault());
    }

    public Preferences(org.orcid.jaxb.model.message.Preferences castPreferences) {
        this.setSendChangeNotifications(castPreferences.getSendChangeNotifications());
        this.setSendOrcidNews(castPreferences.getSendOrcidNews());
        this.setWorkVisibilityDefault(castPreferences.getWorkVisibilityDefault());
        this.setActivitiesVisibilityDefault(castPreferences.getActivitiesVisibilityDefault());
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

}
