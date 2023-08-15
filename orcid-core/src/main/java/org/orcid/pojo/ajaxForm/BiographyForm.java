package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.release.record.Biography;

public class BiographyForm extends VisibilityForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private Text biography;    

    private List<String> errors = new ArrayList<String>();

    public static BiographyForm valueOf(Biography bio) {
        BiographyForm bf = new BiographyForm();
        if(bio != null) {
            bf.setBiography(Text.valueOf(bio.getContent()));
            if(bio.getVisibility() != null) {
                bf.setVisibility(Visibility.valueOf(bio.getVisibility()));
            } 
        }
        return bf;
    }        

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getBiography() {
        return biography;
    }

    public void setBiography(Text biography) {
        this.biography = biography;
    }

    public boolean compare(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BiographyForm other = (BiographyForm) obj;

        if (!WorkForm.compareTexts(biography, other.biography, false))
            return false;
        if (visibility != null && other.visibility != null && !visibility.getVisibility().value().equals(other.visibility.getVisibility().value()))
            return false;
        return true;
    }
}
