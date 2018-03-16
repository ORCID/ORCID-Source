package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.pojo.ajaxForm.ErrorsInterface;
import org.orcid.pojo.ajaxForm.Text;

public class AdminDelegatesRequest implements ErrorsInterface {
    private List<String> errors = new ArrayList<String>();
    
    private Text trusted;

    private Text managed;

    private String successMessage;
    
    public Text getTrusted() {
        return trusted;
    }

    public void setTrusted(Text trusted) {
        this.trusted = trusted;
    }

    public Text getManaged() {
        return managed;
    }

    public void setManaged(Text managed) {
        this.managed = managed;
    }

    @Override
    public List<String> getErrors() {
        return errors;
    }

    @Override
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }        
}
