package org.orcid.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.pojo.ajaxForm.ErrorsInterface;

public class GroupedWorks implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    
    private List<Long> workIds;
    
    @Override
    public List<String> getErrors() {
        return errors;
    }

    @Override
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<Long> getWorkIds() {
        return workIds;
    }

    public void setWorkIds(List<Long> workIds) {
        this.workIds = workIds;
    }

}
