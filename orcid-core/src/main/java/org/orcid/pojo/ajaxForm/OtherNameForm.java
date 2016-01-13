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

import org.orcid.jaxb.model.record_rc2.OtherName;

public class OtherNameForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;
    private List<String> errors = new ArrayList<String>();
    private String content;    
    private String putCode;    
    private Visibility visibility;

    public static OtherNameForm valueOf(OtherName otherName) {
        OtherNameForm form = new OtherNameForm();
        if(otherName != null) {
            if(!PojoUtil.isEmpty(otherName.getContent())) {
                form.setContent(otherName.getContent());
            }
            
            if(otherName.getVisibility() != null) {
                form.setVisibility(Visibility.valueOf(otherName.getVisibility()));
            }
            
            if(otherName.getPutCode() != null) {
                form.setPutCode(String.valueOf(otherName.getPutCode()));
            }
        }
        return form;
    }
    
    public OtherName toOtherName() {
        OtherName otherName = new OtherName();
        if(!PojoUtil.isEmpty(this.getContent())) {
            otherName.setContent(this.getContent());
        }
        
        if(this.visibility != null && this.visibility.getVisibility() != null) {
            otherName.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(this.getVisibility().getVisibility().value()));
        }
        
        if(!PojoUtil.isEmpty(this.getPutCode())) {
            otherName.setPutCode(Long.valueOf(this.getPutCode()));
        }
        
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
}
