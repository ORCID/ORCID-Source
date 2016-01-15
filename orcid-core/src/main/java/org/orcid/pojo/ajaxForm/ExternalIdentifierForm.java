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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.record_rc2.ExternalIdentifier;

public class ExternalIdentifierForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 8715304593954651166L;
    private List<String> errors = new ArrayList<String>();
    private String commonName;
    private String reference;
    private String url;
    private String source;
    private String sourceName;
    private Visibility visibility;
    private String displayIndex;
    private String putCode;

    public static ExternalIdentifierForm valueOf(ExternalIdentifier extId) {
        if (extId == null)
            return null;
        ExternalIdentifierForm form = new ExternalIdentifierForm();
        form.setPutCode(String.valueOf(extId.getPutCode()));
        form.setCommonName(extId.getCommonName());
        form.setReference(extId.getReference());
        if (extId.getVisibility() != null) {
            form.setVisibility(Visibility.valueOf(extId.getVisibility()));
        }
        if (extId.getUrl() != null) {
            form.setUrl(extId.getUrl().getValue());
        }

        if (extId.getSource() != null) {
            form.setSource(extId.getSource().retrieveSourcePath());
            if (extId.getSource().getSourceName() != null) {
                form.setSourceName(extId.getSource().getSourceName().getContent());
            }
        }

        if (!PojoUtil.isEmpty(extId.getDisplayIndex())) {
            form.setDisplayIndex(extId.getDisplayIndex());
        } else {
            form.setDisplayIndex("0");
        }
        return form;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public String getPutCode() {
        return putCode;
    }

    public void setPutCode(String putCode) {
        this.putCode = putCode;
    }

    public String getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(String displayIndex) {
        this.displayIndex = displayIndex;
    }
}
