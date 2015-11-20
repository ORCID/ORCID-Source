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
package org.orcid.api.common.cerif;

import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record.summary_rc1.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc1.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc1.WorkSummary;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifier;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;

import com.google.common.collect.ImmutableSet;

import xmlns.org.eurocris.cerif_1.CERIF;
import xmlns.org.eurocris.cerif_1.CfCoreClassWithFractionType;
import xmlns.org.eurocris.cerif_1.CfFedIdEmbType;
import xmlns.org.eurocris.cerif_1.CfMLangStringType;
import xmlns.org.eurocris.cerif_1.CfPersType;
import xmlns.org.eurocris.cerif_1.CfPersType.CfPersNamePers;
import xmlns.org.eurocris.cerif_1.CfResProdType;
import xmlns.org.eurocris.cerif_1.CfResPublType;
import xmlns.org.eurocris.cerif_1.ObjectFactory;
import xmlns.org.eurocris.cerif_api.Cerifapitype;
import xmlns.org.eurocris.cerif_api.Headertype;
import xmlns.org.eurocris.cerif_api.Payloadtype;

/**
 * Builds JAXB Cerif 1.6.2 objects from ORCID records and wraps them in 1.0
 * Cerif API envelopes
 * 
 * Follows OpenAIRE semantics.
 * 
 * @see https://zenodo.org/record/17065/files/
 *      OpenAIRE_Guidelines_for_CRIS_Managers_v.1.0.pdf
 * @author tom
 *
 */
public class Cerif16Builder {

    private ObjectFactory objectFactory;
    private CerifTypeTranslator translator;
    private CERIF cerif;
    private CfPersType person;

    /**
     * Types we export to CERIF.
     * 
     * @see https://zenodo.org/record/17065/files/
     *      OpenAIRE_Guidelines_for_CRIS_Managers_v.1.0.pdf
     */
    private static Set<WorkExternalIdentifierType> exportedIDs = ImmutableSet.of(WorkExternalIdentifierType.DOI, WorkExternalIdentifierType.HANDLE,
            WorkExternalIdentifierType.URI, WorkExternalIdentifierType.URN, WorkExternalIdentifierType.PMC);

    public Cerif16Builder() {
        objectFactory = new ObjectFactory();
        cerif = objectFactory.createCERIF();
        translator = new CerifTypeTranslator();
    }

    /** Add a person entity to the underlying CERIF document
     * 
     * @param profile
     * @param orcid
     * @return
     */
    public Cerif16Builder addPerson(ProfileEntity profile) {
        person = objectFactory.createCfPersType();
        person.setCfPersId(profile.getId());
        person.getCfResIntOrCfKeywOrCfPersPers().add(buildFedID(profile.getId(), CerifClassEnum.ORCID));
        
        // add in other external ids here
        // TODO: why is this throwing JPA exceptions?                
        if (!profile.getExternalIdentifiersVisibility().isMoreRestrictiveThan(Visibility.PUBLIC)) {
            for (ExternalIdentifierEntity id : profile.getExternalIdentifiers()) {
                if (translator.translate(id) != CerifClassEnum.OTHER){
                    person.getCfResIntOrCfKeywOrCfPersPers().add(buildFedID(id.getExternalIdReference(), translator.translate(id)));                    
                }
            }
        }

        // TODO: we need to check name visibility properly here!
        CfPersNamePers name = objectFactory.createCfPersTypeCfPersNamePers();
        name.setCfFirstNames(profile.getGivenNames());
        name.setCfFamilyNames(profile.getFamilyName());
        person.getCfResIntOrCfKeywOrCfPersPers().add(objectFactory.createCfPersTypeCfPersNamePers(name));

        if (!profile.getCreditNameVisibility().isMoreRestrictiveThan(Visibility.PUBLIC)
                && StringUtils.isNotEmpty(profile.getCreditName())) {
            CfPersNamePers creditname = objectFactory.createCfPersTypeCfPersNamePers();
            creditname.setCfClassId(CerifClassEnum.PRESENTED_NAME.getUuid());
            creditname.setCfClassSchemeId(CerifClassSchemeEnum.PERSON_NAMES.getUuid());
            creditname.setCfOtherNames(profile.getCreditName());
            person.getCfResIntOrCfKeywOrCfPersPers().add(objectFactory.createCfPersTypeCfPersNamePers(creditname));
        }
        
        cerif.getCfClassOrCfClassSchemeOrCfClassSchemeDescr().add(person);
        return this;
    }

    /**
     * Add a ResultPublication to the underlying CERIF.
     * 
     * @param orcid
     * @param ws
     * @param objectFactory
     * @return
     */
    public Cerif16Builder addPublication(String orcid, WorkSummary ws) {
        // create full record for publication
        CfResPublType pub = objectFactory.createCfResPublType();
        pub.setCfResPublId(orcid + ":" + ws.getPutCode());

        // add title
        CfMLangStringType titleString = objectFactory.createCfMLangStringType();
        titleString.setValue(ws.getTitle().getTitle().getContent());
        pub.getCfTitleOrCfAbstrOrCfKeyw().add(objectFactory.createCfResPublTypeCfTitle(titleString));

        // add type info
        CfCoreClassWithFractionType type = objectFactory.createCfCoreClassWithFractionType();
        type.setCfClassSchemeId(CerifClassSchemeEnum.OUTPUT_TYPES.getUuid());
        type.setCfClassId(translator.translate(ws.getType()).getUuid());
        pub.getCfTitleOrCfAbstrOrCfKeyw().add(objectFactory.createCfResPublTypeCfResPublClass(type));

        // add external identifiers
        if (ws.getExternalIdentifiers() != null && ws.getExternalIdentifiers().getWorkExternalIdentifier() != null) {
            for (WorkExternalIdentifier id : ws.getExternalIdentifiers().getWorkExternalIdentifier()) {
                if (exportedIDs.contains(id.getWorkExternalIdentifierType()) && StringUtils.isNotEmpty(id.getWorkExternalIdentifierId().getContent())) {
                    pub.getCfTitleOrCfAbstrOrCfKeyw().add(
                        this.buildFedID(
                            id.getWorkExternalIdentifierId().getContent(), 
                            translator.translate(id.getWorkExternalIdentifierType())
                    ));
                }
            }
        }
        xmlns.org.eurocris.cerif_1.CfResPublType.CfPersResPubl persRes = objectFactory.createCfResPublTypeCfPersResPubl();
        persRes.setCfPersId(orcid);
        pub.getCfTitleOrCfAbstrOrCfKeyw().add(objectFactory.createCfResPublTypeCfPersResPubl(persRes));
        cerif.getCfClassOrCfClassSchemeOrCfClassSchemeDescr().add(pub);
        return this;
    }

    /**
     * Add a ResultProduct to the underlying CERIF document
     * 
     * @param orcid
     * @param ws
     * @param objectFactory
     * @return
     */
    public Cerif16Builder addProduct(String orcid, WorkSummary ws) {
        // create full record for publication
        CfResProdType prod = objectFactory.createCfResProdType();
        prod.setCfResProdId(orcid + ":" + ws.getPutCode());
        CfMLangStringType titleString = objectFactory.createCfMLangStringType();
        titleString.setValue(ws.getTitle().getTitle().getContent());
        prod.getCfNameOrCfDescrOrCfKeyw().add(objectFactory.createCfResProdTypeCfName((titleString)));

        // add type info
        CfCoreClassWithFractionType type = objectFactory.createCfCoreClassWithFractionType();
        type.setCfClassSchemeId(CerifClassSchemeEnum.OUTPUT_TYPES.getUuid());
        type.setCfClassId(translator.translate(ws.getType()).getUuid());
        prod.getCfNameOrCfDescrOrCfKeyw().add(objectFactory.createCfResProdTypeCfResProdClass(type));

        // add external identifiers
        if (ws.getExternalIdentifiers() != null && ws.getExternalIdentifiers().getWorkExternalIdentifier() != null) {
            for (WorkExternalIdentifier id : ws.getExternalIdentifiers().getWorkExternalIdentifier()) {
                if (exportedIDs.contains(id.getWorkExternalIdentifierType()) && StringUtils.isNotEmpty(id.getWorkExternalIdentifierId().getContent())) {
                    prod.getCfNameOrCfDescrOrCfKeyw().add(
                        this.buildFedID(
                                id.getWorkExternalIdentifierId().getContent(), 
                                translator.translate(id.getWorkExternalIdentifierType())
                    ));
                }
            }
        }
        xmlns.org.eurocris.cerif_1.CfResProdType.CfPersResProd persRes = objectFactory.createCfResProdTypeCfPersResProd();
        persRes.setCfPersId(orcid);
        prod.getCfNameOrCfDescrOrCfKeyw().add(objectFactory.createCfResProdTypeCfPersResProd(persRes));
        cerif.getCfClassOrCfClassSchemeOrCfClassSchemeDescr().add(prod);
        return this;
    }

    /**
     * For each work that is a publication, add a reference to the person object
     * 
     * NOTE: will fail if you have not already added a person via addPerson()
     * 
     * @param objectFactory
     * @param orcid
     * @param addFullPublication
     *            if true, include the complete publications in the CERIF
     */
    public Cerif16Builder concatPublications(ActivitiesSummary as, String orcid, boolean addFullPublications) {
        for (WorkGroup w : as.getWorks().getWorkGroup()) {
            // check it's a publication
            WorkSummary ws = w.getWorkSummary().iterator().next();
            if (!translator.isPublication(ws.getType()))
                continue;

            // add reference
            String resID = orcid + ":" + ws.getPutCode();
            xmlns.org.eurocris.cerif_1.CfPersType.CfPersResPubl link = objectFactory.createCfPersTypeCfPersResPubl();
            link.setCfResPublId(resID);
            person.getCfResIntOrCfKeywOrCfPersPers().add(objectFactory.createCfPersTypeCfPersResPubl(link));

            // add the full record if required
            if (addFullPublications) {
                this.addPublication(orcid, ws);
            }
        }
        return this;
    }

    /**
     * For each work that is a product, add a reference to the person object
     * 
     * NOTE: will fail if you have not already added a person via addPerson()
     * 
     * @param objectFactory
     * @param orcid
     * @param addFull
     *            if true, include the complete product in the CERIF
     */
    public Cerif16Builder concatProducts(ActivitiesSummary as, String orcid, boolean addFullProducts) {
        for (WorkGroup w : as.getWorks().getWorkGroup()) {
            // check it's a publication
            WorkSummary ws = w.getWorkSummary().iterator().next();
            if (!translator.isProduct(ws.getType()))
                continue;

            // add reference
            String resID = orcid + ":" + ws.getPutCode();
            xmlns.org.eurocris.cerif_1.CfPersType.CfPersResProd link = objectFactory.createCfPersTypeCfPersResProd();
            link.setCfResProdId(resID);
            person.getCfResIntOrCfKeywOrCfPersPers().add(objectFactory.createCfPersTypeCfPersResProd(link));

            if (addFullProducts) {
                addProduct(orcid, ws);
            }
        }
        return this;
    }

    /**
     * Wrap the CERIF in the CERIF API Envelope
     * 
     * @param cerif
     * @return
     */
    public Cerifapitype build() {
        Cerifapitype apiType = new Cerifapitype();
        Headertype header = new Headertype();
        header.setApiVersion("1.0");
        //header.setSource("http://pub.orcid.org/cerif/1_0");
        //header.setQuery("http://pub.orcid.org/cerif/1_0/blah/0000-0000-0000-0000");
        apiType.setHeader(header);
        Payloadtype payload = new Payloadtype();
        payload.setCERIF(cerif);
        apiType.setPayload(payload);
        return apiType;
    }
    
    /** Create a federated identifer element suitable for use by all entities
     * 
     * @param id
     * @param type
     * @return
     */
    private JAXBElement<CfFedIdEmbType> buildFedID(String id, CerifClassEnum type) {
        CfFedIdEmbType fedId = objectFactory.createCfFedIdEmbType();
        fedId.setCfFedId(id);
        fedId.setCfClassId(type.getUuid());
        fedId.setCfClassSchemeId(CerifClassSchemeEnum.IDENTIFIER_TYPES.getUuid());
        return objectFactory.createCfPersTypeCfFedId(fedId);
    }

}
