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

import java.util.ArrayList;
import java.util.List;

public class WorkTitle implements ErrorsInterface {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text title;

    private Text subtitle;

    public WorkTitle(org.orcid.jaxb.model.message.WorkTitle workTitle) {
        if (workTitle != null) {
            if (workTitle.getTitle() != null) {
                this.setTitle(new Text(workTitle.getTitle().getContent()));
            }
            if (workTitle.getSubtitle() != null) {
                this.setSubtitle(new Text(workTitle.getSubtitle().getContent()));
            }

        }

    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getTitle() {
        return title;
    }

    public void setTitle(Text title) {
        this.title = title;
    }

    public Text getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(Text subtitle) {
        this.subtitle = subtitle;
    }

}
