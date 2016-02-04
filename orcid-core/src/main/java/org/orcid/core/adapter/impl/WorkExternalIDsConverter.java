package org.orcid.core.adapter.impl;

import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.common_rc1.Url;
import org.orcid.jaxb.model.record_rc1.Relationship;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifier;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierId;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.ExternalIDs;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class WorkExternalIDsConverter extends BidirectionalConverter<ExternalIDs, String> {

    /** Uses rc1 as middle stage
     * 
     */
    @Override
    public ExternalIDs convertFrom(String externalIdentifiersAsString, Type<ExternalIDs> arg1) {
        ExternalIDs result = new ExternalIDs();
        WorkExternalIdentifiers ids = JsonUtils.readObjectFromJsonString(externalIdentifiersAsString, WorkExternalIdentifiers.class);        
        for (WorkExternalIdentifier id : ids.getWorkExternalIdentifier()){
            ExternalID exid = new ExternalID();
            exid.setType(id.getWorkExternalIdentifierType().value());
            exid.setRelationship(org.orcid.jaxb.model.record_rc2.Relationship.valueOf(id.getRelationship().value()));
            exid.setUrl(new org.orcid.jaxb.model.common_rc2.Url(id.getUrl().getValue()));
            exid.setValue(id.getWorkExternalIdentifierId().getContent());
            result.getExternalIdentifiers().add(exid);
        }
        return result;
    }

    @Override
    public String convertTo(ExternalIDs externalIDs, Type<String> arg1) {
        WorkExternalIdentifiers ids = new WorkExternalIdentifiers();
        for (ExternalID externalID : externalIDs.getExternalIdentifiers()){
            WorkExternalIdentifier id = new WorkExternalIdentifier();
            try{
                id.setWorkExternalIdentifierType(WorkExternalIdentifierType.fromValue(externalID.getType()));            
            }catch(IllegalArgumentException e){
                id.setWorkExternalIdentifierType(WorkExternalIdentifierType.OTHER_ID); 
            }
            id.setWorkExternalIdentifierId(new WorkExternalIdentifierId(externalID.getValue()));
            id.setUrl(new Url(externalID.getUrl().getValue()));
            try{
                id.setRelationship(Relationship.fromValue(externalID.getRelationship().value()));
            }catch (IllegalArgumentException e){
                //?
            }
            ids.getExternalIdentifier().add(id);
        }        
        return JsonUtils.convertToJsonString(ids);
    }

}
