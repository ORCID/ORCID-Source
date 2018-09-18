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
    private Text realGivenNames;
    private Text realFamilyName;
    private Text realCreditName;    
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
    public static NamesForm valueOf(Name name, Name realName) {
       NamesForm nf = valueOf (name);
       if (realName != null) {
           if (realName.getGivenNames() != null) {
               nf.setRealGivenNames((Text.valueOf(realName.getGivenNames().getContent())));
           }

           if (realName.getFamilyName() != null) {
               nf.setRealFamilyName(Text.valueOf(realName.getFamilyName().getContent()));
           }

           if (realName.getCreditName() != null) {
               nf.setRealCreditName(Text.valueOf(realName.getCreditName().getContent()));
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
	public Text getRealGivenNames() {
		return realGivenNames;
	}
	public void setRealGivenNames(Text realGivenNames) {
		this.realGivenNames = realGivenNames;
	}
	public Text getRealFamilyName() {
		return realFamilyName;
	}
	public void setRealFamilyName(Text realFamilyName) {
		this.realFamilyName = realFamilyName;
	}
	public Text getRealCreditName() {
		return realCreditName;
	}
	public void setRealCreditName(Text realCreditName) {
		this.realCreditName = realCreditName;
	}
}
