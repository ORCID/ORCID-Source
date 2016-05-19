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

import org.orcid.jaxb.model.record_rc2.ExternalID;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * This class serializes a single ExternalID into a WorkExternalIdentifier. It
 * is used by peer review. Works are handled differently by
 * SingleWorkExternalIdentifierConvertor.
 * 
 * @author tom
 *
 */
public class PeerReviewWorkExternalIDConverter extends BidirectionalConverter<ExternalID, String> {

    /**
     * Uses rc1 as intermediary form
     * 
     */
    @Override
    public ExternalID convertFrom(String externalIdentifiersAsString, Type<ExternalID> arg1) {
        WorkExternalIdentifier id = WorkExternalIdentifier.fromDBJSONString(externalIdentifiersAsString);
        return id.toRecordPojo();
    }

    /**
     * Currently transforms into rc1 format
     * 
     */
    @Override
    public String convertTo(ExternalID externalID, Type<String> arg1) {
        WorkExternalIdentifier id = new WorkExternalIdentifier(externalID);
        return id.toDBJSONString();
    }

    /**
     * Transforms RC1 into RC2
     * 
     * protected ExternalID convertRC1toRC2(WorkExternalIdentifier id){
     * ExternalID result = new ExternalID(); if
     * (id.getWorkExternalIdentifierType() != null){
     * result.setType(id.getWorkExternalIdentifierType().value()); }else{
     * result.setType(WorkExternalIdentifierType.OTHER_ID.value()); } if
     * (id.getRelationship() !=null)
     * result.setRelationship(org.orcid.jaxb.model.record_rc2.Relationship.
     * fromValue(id.getRelationship().value())); if (id.getUrl() != null)
     * result.setUrl(new
     * org.orcid.jaxb.model.common_rc2.Url(id.getUrl().getValue())); if
     * (id.getWorkExternalIdentifierId() !=null)
     * result.setValue(id.getWorkExternalIdentifierId().getContent()); else
     * result.setValue(""); return result; }
     * 
     * protected WorkExternalIdentifier convertRC2toRC1(ExternalID externalID){
     * WorkExternalIdentifier id = new WorkExternalIdentifier(); try{
     * id.setWorkExternalIdentifierType(WorkExternalIdentifierType.fromValue(
     * externalID.getType())); }catch(IllegalArgumentException e){ throw new
     * ActivityIdentifierValidationException(e); } if (externalID.getValue()
     * !=null) id.setWorkExternalIdentifierId(new
     * WorkExternalIdentifierId(externalID.getValue())); else
     * id.setWorkExternalIdentifierId(new WorkExternalIdentifierId(""));
     * 
     * if (externalID.getUrl()!=null) id.setUrl(new
     * Url(externalID.getUrl().getValue())); if (externalID.getRelationship() !=
     * null) try{
     * id.setRelationship(Relationship.fromValue(externalID.getRelationship().
     * value())); }catch (IllegalArgumentException e){ } return id; }
     */

}
