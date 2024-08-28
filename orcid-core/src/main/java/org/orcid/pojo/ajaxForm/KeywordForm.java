package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.record.Keyword;
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
    private String assertionOriginOrcid;
    private String assertionOriginClientId;
    private String assertionOriginName;

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
            
            if (keyword.getSource().getAssertionOriginClientId() != null) {
                form.setAssertionOriginClientId(keyword.getSource().getAssertionOriginClientId().getPath());
            }
            
            if (keyword.getSource().getAssertionOriginOrcid() != null) {
                form.setAssertionOriginOrcid(keyword.getSource().getAssertionOriginOrcid().getPath());
            }
            
            if (keyword.getSource().getAssertionOriginName() != null) {
                form.setAssertionOriginName(keyword.getSource().getAssertionOriginName().getContent());
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
            keyword.setVisibility(org.orcid.jaxb.model.v3.release.common.Visibility.fromValue(visibility.getVisibility().value()));
        } else {
            keyword.setVisibility(org.orcid.jaxb.model.v3.release.common.Visibility.fromValue(OrcidVisibilityDefaults.KEYWORD_DEFAULT.getVisibility().value()));
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
    
    public String getAssertionOriginOrcid() {
        return assertionOriginOrcid;
    }

    public void setAssertionOriginOrcid(String assertionOriginOrcid) {
        this.assertionOriginOrcid = assertionOriginOrcid;
    }

    public String getAssertionOriginClientId() {
        return assertionOriginClientId;
    }

    public void setAssertionOriginClientId(String assertionOriginClientId) {
        this.assertionOriginClientId = assertionOriginClientId;
    }

    public String getAssertionOriginName() {
        return assertionOriginName;
    }

    public void setAssertionOriginName(String assertionOriginName) {
        this.assertionOriginName = assertionOriginName;
    }

    public Long getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(Long displayIndex) {
        this.displayIndex = displayIndex;
    }

    public boolean compare(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KeywordForm other = (KeywordForm) obj;

        if (!WorkForm.compareStrings(content, other.getContent()))
            return false;
        if (visibility != null && other.visibility != null && !visibility.getVisibility().value().equals(other.visibility.getVisibility().value()))
            return false;
        return true;
    }
}
