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
package org.orcid.core.adapter.impl.jsonidentifiers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.ExternalIDs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties("scope")
public class WorkExternalIdentifiers implements Serializable, JSONIdentifierAdapter<org.orcid.jaxb.model.message.WorkExternalIdentifiers, ExternalIDs> {

    private static final long serialVersionUID = 1L;
    protected List<WorkExternalIdentifier> workExternalIdentifier;
    
    public WorkExternalIdentifiers(){
        
    }
    
    public WorkExternalIdentifiers(ExternalIDs recordPojo){
        if (recordPojo!=null && !recordPojo.getExternalIdentifier().isEmpty()) {
            for (ExternalID recordEi : recordPojo.getExternalIdentifier()) {                               
                this.getWorkExternalIdentifier().add(new WorkExternalIdentifier(recordEi));
            }
        }
    }
    
    public WorkExternalIdentifiers (org.orcid.jaxb.model.message.WorkExternalIdentifiers messagePojo){
        if (messagePojo!=null && !messagePojo.getWorkExternalIdentifier().isEmpty()) {
            for (org.orcid.jaxb.model.message.WorkExternalIdentifier messageFei : messagePojo.getWorkExternalIdentifier()) {
                this.getWorkExternalIdentifier().add(new WorkExternalIdentifier(messageFei));
            }
        }
    }
    
    public static WorkExternalIdentifiers fromDBJSONString(String dbJSON){
        return JsonUtils.readObjectFromJsonString(dbJSON, WorkExternalIdentifiers.class);
    }
    
    @Override
    public String toDBJSONString() {
        return JsonUtils.convertToJsonString(this);
    }

    @Override
    public org.orcid.jaxb.model.message.WorkExternalIdentifiers toMessagePojo() {
        org.orcid.jaxb.model.message.WorkExternalIdentifiers result = new org.orcid.jaxb.model.message.WorkExternalIdentifiers();
        if (!this.getWorkExternalIdentifier().isEmpty()) {
            for (WorkExternalIdentifier fei : this.getWorkExternalIdentifier()) {
                result.getWorkExternalIdentifier().add(fei.toMessagePojo());
            }
        }
        return result;
    }

    @Override
    public ExternalIDs toRecordPojo() {
        ExternalIDs result = new ExternalIDs();
        for (WorkExternalIdentifier fei : this.getWorkExternalIdentifier()) {
            result.getExternalIdentifier().add(fei.toRecordPojo());
        }
        return result;
    }
    
    public List<WorkExternalIdentifier> getWorkExternalIdentifier() {
        if (this.workExternalIdentifier == null)
            workExternalIdentifier = new ArrayList<WorkExternalIdentifier>();
        return workExternalIdentifier;
    }

    public void setWorkExternalIdentifier(List<WorkExternalIdentifier> workExternalIdentifier) {
        this.workExternalIdentifier = workExternalIdentifier;
    }


}
