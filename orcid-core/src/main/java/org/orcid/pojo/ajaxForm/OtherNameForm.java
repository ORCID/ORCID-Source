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

import org.orcid.jaxb.model.common_rc4.Source;
import org.orcid.jaxb.model.record_rc4.OtherName;

public class OtherNameForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;
    private List<String> errors = new ArrayList<String>();
    private String content;
    private String putCode;
    private Visibility visibility;
    private Long displayIndex;
    private Date createdDate;
    private Date lastModified;
    private String source;
    private String sourceName;

    public static OtherNameForm valueOf(OtherName otherName) {
        OtherNameForm form = new OtherNameForm();
        if (otherName != null) {
            if (!PojoUtil.isEmpty(otherName.getContent())) {
                form.setContent(otherName.getContent());
            }

            if (otherName.getVisibility() != null) {
                form.setVisibility(Visibility.valueOf(otherName.getVisibility()));
            }

            if (otherName.getPutCode() != null) {
                form.setPutCode(String.valueOf(otherName.getPutCode()));
            }

            if (otherName.getCreatedDate() != null) {
                Date createdDate = new Date();
                createdDate.setYear(String.valueOf(otherName.getCreatedDate().getValue().getYear()));
                createdDate.setMonth(String.valueOf(otherName.getCreatedDate().getValue().getMonth()));
                createdDate.setDay(String.valueOf(otherName.getCreatedDate().getValue().getDay()));
                form.setCreatedDate(createdDate);
            }

            if (otherName.getLastModifiedDate() != null) {
                Date lastModifiedDate = new Date();
                lastModifiedDate.setYear(String.valueOf(otherName.getLastModifiedDate().getValue().getYear()));
                lastModifiedDate.setMonth(String.valueOf(otherName.getLastModifiedDate().getValue().getMonth()));
                lastModifiedDate.setDay(String.valueOf(otherName.getLastModifiedDate().getValue().getDay()));
                form.setLastModified(lastModifiedDate);
            }

            if (otherName.getSource() != null) {
                // Set source
                form.setSource(otherName.getSource().retrieveSourcePath());
                if (otherName.getSource().getSourceName() != null) {
                    form.setSourceName(otherName.getSource().getSourceName().getContent());
                }
            }

            if (otherName.getDisplayIndex() != null) {
                form.setDisplayIndex(otherName.getDisplayIndex());
            } else {
                form.setDisplayIndex(0L);
            }
        }
        return form;
    }

    public OtherName toOtherName() {
        OtherName otherName = new OtherName();
        if (!PojoUtil.isEmpty(this.getContent())) {
            otherName.setContent(this.getContent());
        }

        if (this.visibility != null && this.visibility.getVisibility() != null) {
            otherName.setVisibility(org.orcid.jaxb.model.common_rc4.Visibility.fromValue(this.getVisibility().getVisibility().value()));
        }

        if (!PojoUtil.isEmpty(this.getPutCode())) {
            otherName.setPutCode(Long.valueOf(this.getPutCode()));
        }

        if (displayIndex != null) {
            otherName.setDisplayIndex(displayIndex);
        } else {
            otherName.setDisplayIndex(0L);
        }

        otherName.setSource(new Source(source));

        return otherName;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPutCode() {
        return putCode;
    }

    public void setPutCode(String putCode) {
        this.putCode = putCode;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Long getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(Long displayIndex) {
        this.displayIndex = displayIndex;
    }
}
