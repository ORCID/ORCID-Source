package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.pojo.ajaxForm.ErrorsInterface;

public class Redirect implements ErrorsInterface {
    /**
     * 
     */

    public Redirect() {

    }

    public Redirect(String url) {
        this.url = url;
    }

    private String url;

    private List<String> errors = new ArrayList<String>();

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
