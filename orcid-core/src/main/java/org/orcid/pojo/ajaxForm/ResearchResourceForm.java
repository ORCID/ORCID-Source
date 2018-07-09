package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.List;

import org.orcid.jaxb.model.v3.rc1.record.ResearchResource;

public class ResearchResourceForm implements ErrorsInterface, Serializable{

    private static final long serialVersionUID = 1L;

    @Override
    public List<String> getErrors() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setErrors(List<String> errors) {
        // TODO Auto-generated method stub
        
    }
    
    public ResearchResource toResearchResource(){
        return null;
    }
    
    public static ResearchResourceForm valueOf(ResearchResource r){
        return null;
    }

}
