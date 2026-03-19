package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.persistence.jpa.entities.UserconnectionPK;
import org.orcid.pojo.ajaxForm.ErrorsInterface;

/**
 * @author Shobhit Tyagi
 */
public class ManageSocialAccount extends AuthChallenge {

    private UserconnectionPK idToMange;

    public UserconnectionPK getIdToManage() {
        return idToMange;
    }

    public void setIdToManage(UserconnectionPK idToManage) {
        this.idToMange = idToManage;
    }

}
