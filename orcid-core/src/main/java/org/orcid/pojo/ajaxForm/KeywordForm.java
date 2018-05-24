package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.v3.rc1.common.CreatedDate;
import org.orcid.jaxb.model.v3.rc1.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.rc1.common.Source;
import org.orcid.jaxb.model.v3.rc1.record.Keyword;
import org.orcid.utils.DateUtils;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class KeywordForm extends VisibilityForm implements ErrorsInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> errors = new ArrayList<String>();
    private String putCode;
    private String content;    
    private Long displayIndex;
    private Date createdDate;
    private Date lastModified;
    private String source;
    private String sourceName;

    public static KeywordForm valueOf(Keyword keyword) {
        KeywordForm form = new KeywordForm();
        if (keyword == null) {
            return form;
        }

        if (keyword.getPutCode() != null) {
            form.setPutCode(String.valueOf(keyword.getPutCode()));
        }

        if (!PojoUtil.isEmpty(keyword.getContent())) {
            form.setContent(keyword.getContent());
        }

        if (keyword.getVisibility() != null) {
            form.setVisibility(Visibility.valueOf(keyword.getVisibility()));
        } else {
            form.setVisibility(Visibility.valueOf(org.orcid.jaxb.model.common_v2.Visibility.fromValue(OrcidVisibilityDefaults.KEYWORD_DEFAULT.getVisibility().value())));
        }

        if (keyword.getCreatedDate() != null) {
            Date createdDate = new Date();
            createdDate.setYear(String.valueOf(keyword.getCreatedDate().getValue().getYear()));
            createdDate.setMonth(String.valueOf(keyword.getCreatedDate().getValue().getMonth()));
            createdDate.setDay(String.valueOf(keyword.getCreatedDate().getValue().getDay()));
            form.setCreatedDate(createdDate);
        }

        if (keyword.getLastModifiedDate() != null) {
            Date lastModifiedDate = new Date();
            lastModifiedDate.setYear(String.valueOf(keyword.getLastModifiedDate().getValue().getYear()));
            lastModifiedDate.setMonth(String.valueOf(keyword.getLastModifiedDate().getValue().getMonth()));
            lastModifiedDate.setDay(String.valueOf(keyword.getLastModifiedDate().getValue().getDay()));
            form.setLastModified(lastModifiedDate);
        }

        if (keyword.getSource() != null) {
            // Set source
            form.setSource(keyword.getSource().retrieveSourcePath());
            if (keyword.getSource().getSourceName() != null) {
                form.setSourceName(keyword.getSource().getSourceName().getContent());
            }
        }

        if (keyword.getDisplayIndex() != null) {
            form.setDisplayIndex(keyword.getDisplayIndex());
        } else {
            form.setDisplayIndex(0L);
        }

        return form;
    }

    public Keyword toKeyword() {
        Keyword keyword = new Keyword();

        if (!PojoUtil.isEmpty(putCode)) {
            keyword.setPutCode(Long.valueOf(putCode));
        }

        if (!PojoUtil.isEmpty(content)) {
            keyword.setContent(content);
        }

        if (visibility != null && visibility.getVisibility() != null) {
            keyword.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.fromValue(visibility.getVisibility().value()));
        } else {
            keyword.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.fromValue(OrcidVisibilityDefaults.KEYWORD_DEFAULT.getVisibility().value()));
        }

        if (createdDate != null) {
            keyword.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(createdDate.toCalendar())));
        }

        if (lastModified != null) {
            keyword.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(lastModified.toCalendar())));
        }

        if (displayIndex != null) {
            keyword.setDisplayIndex(displayIndex);
        } else {
            keyword.setDisplayIndex(0L);
        }

        keyword.setSource(new Source(source));
        return keyword;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getPutCode() {
        return putCode;
    }

    public void setPutCode(String putCode) {
        this.putCode = putCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Long getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(Long displayIndex) {
        this.displayIndex = displayIndex;
    }
}
