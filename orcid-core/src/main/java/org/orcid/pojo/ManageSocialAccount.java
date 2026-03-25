package org.orcid.pojo;

import org.orcid.persistence.jpa.entities.UserconnectionPK;

/**
 * @author Shobhit Tyagi
 */
public class ManageSocialAccount extends AuthChallenge {

    private UserconnectionPK id;

    public UserconnectionPK getId() {
        return id;
    }

    public void setId(UserconnectionPK idToManage) {
        this.id = idToManage;
    }

}
