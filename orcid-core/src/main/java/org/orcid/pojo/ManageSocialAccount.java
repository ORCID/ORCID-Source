package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.persistence.jpa.entities.UserconnectionPK;
import org.orcid.pojo.ajaxForm.ErrorsInterface;

/**
 * @author Shobhit Tyagi
 */
public class ManageSocialAccount implements ErrorsInterface {

    private List<String> errors = new ArrayList<String>();
    private UserconnectionPK idToMange;
    private String password;

    public UserconnectionPK getIdToManage() {
        return idToMange;
    }

    public void setIdToManage(UserconnectionPK idToManage) {
        this.idToMange = idToManage;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
