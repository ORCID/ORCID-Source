/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.persistence.jpa.entities.ProfileEntity;

public class Member implements ErrorsInterface, Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    private Text type;
    private Text groupOrcid;
    private Text groupName;
    private Text email;
    private Text salesforceId;
    private List<Client> clients = new ArrayList<Client>();

    @Override
    public List<String> getErrors() {
        return errors;
    }

    @Override
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public static Member fromProfileEntity(ProfileEntity profile){
    	Member group = new Member();
    	group.setEmail(Text.valueOf(profile.getPrimaryEmail().getId()));
    	
    	if(profile.getRecordNameEntity() != null) {
    	    group.setGroupName(Text.valueOf(profile.getRecordNameEntity().getCreditName()));
    	} else {
    	    group.setGroupName(Text.valueOf(profile.getCreditName()));
    	}
    	
    	group.setGroupOrcid(Text.valueOf(profile.getId()));
    	group.setType(Text.valueOf(profile.getGroupType().value()));
    	group.setSalesforceId(Text.valueOf(profile.getSalesforeId()));
    	return group;
    }
    
    public OrcidClientGroup toOrcidClientGroup() {
        OrcidClientGroup orcidClientGroup = new OrcidClientGroup();
        orcidClientGroup.setGroupOrcid(groupOrcid == null? "" : groupOrcid.getValue());
        orcidClientGroup.setType(MemberType.fromValue(getType().getValue()));
        orcidClientGroup.setGroupName(getGroupName().getValue());
        orcidClientGroup.setEmail(getEmail().getValue());
        if(getSalesforceId() == null)
            setSalesforceId(Text.valueOf(""));
        orcidClientGroup.setSalesforceId(getSalesforceId().getValue());
        return orcidClientGroup;
    }

    public Text getType() {
        return type;
    }

    public void setType(Text type) {
        this.type = type;
    }

    public Text getGroupOrcid() {
        return groupOrcid;
    }

    public void setGroupOrcid(Text groupOrcid) {
        this.groupOrcid = groupOrcid;
    }

    public Text getGroupName() {
        return groupName;
    }

    public void setGroupName(Text groupName) {
        this.groupName = groupName;
    }

    public Text getEmail() {
        return email;
    }

    public void setEmail(Text email) {
        this.email = email;
    }

    public Text getSalesforceId() {
        return salesforceId;
    }

    public void setSalesforceId(Text salesforceId) {
        this.salesforceId = salesforceId;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }            
}
