package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.v3.rc1.common.CreditName;
import org.orcid.jaxb.model.v3.rc1.record.FamilyName;
import org.orcid.jaxb.model.v3.rc1.record.GivenNames;
import org.orcid.jaxb.model.v3.rc1.record.Name;

public class NamesForm extends VisibilityForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text givenNames;
    private Text familyName;
    private Text creditName;    

    public static NamesForm valueOf(Name name) {
        NamesForm nf = new NamesForm();

        if (name != null) {
            if (name.getGivenNames() != null) {
                nf.setGivenNames(Text.valueOf(name.getGivenNames().getContent()));
            }

            if (name.getFamilyName() != null) {
                nf.setFamilyName(Text.valueOf(name.getFamilyName().getContent()));
            }

            if (name.getCreditName() != null) {
                nf.setCreditName(Text.valueOf(name.getCreditName().getContent()));
            }

            if (name.getVisibility() != null) {
                nf.setVisibility(Visibility.valueOf(name.getVisibility()));
            } else {
                org.orcid.jaxb.model.common_v2.Visibility v = org.orcid.jaxb.model.common_v2.Visibility.fromValue(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().value());
                nf.setVisibility(Visibility.valueOf(v));
            }
        }

        return nf;
    }
    
    public Name toName() {
        Name name = new Name();
        if(!PojoUtil.isEmpty(givenNames)) {
            name.setGivenNames(new GivenNames(givenNames.getValue()));
        }
        
        if(!PojoUtil.isEmpty(familyName)) {
            name.setFamilyName(new FamilyName(familyName.getValue()));
        }

        if(!PojoUtil.isEmpty(creditName)) {
            name.setCreditName(new CreditName(creditName.getValue()));
        }
        
        if(visibility != null && visibility.getVisibility() != null) {
            name.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.fromValue(visibility.getVisibility().value()));
        } else {
            name.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.fromValue(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().value()));
        }
        
        return name;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(Text givenNames) {
        this.givenNames = givenNames;
    }

    public Text getFamilyName() {
        return familyName;
    }

    public void setFamilyName(Text familyName) {
        this.familyName = familyName;
    }

    public Text getCreditName() {
        return creditName;
    }

    public void setCreditName(Text creditName) {
        this.creditName = creditName;
    }
}
