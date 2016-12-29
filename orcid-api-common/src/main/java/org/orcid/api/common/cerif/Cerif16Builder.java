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

import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;

import com.google.common.base.Optional;
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
    private Cerif10APIFactory apifactory;

    /**
     * Types we export to CERIF.
     * 
     * @see https://zenodo.org/record/17065/files/
     *      OpenAIRE_Guidelines_for_CRIS_Managers_v.1.0.pdf
     */
    private static Set<String> exportedIDs = ImmutableSet.of(WorkExternalIdentifierType.DOI.name(), WorkExternalIdentifierType.HANDLE.name(),
            WorkExternalIdentifierType.URI.name(), WorkExternalIdentifierType.URN.name(), WorkExternalIdentifierType.PMC.name());

    public Cerif16Builder() {
        objectFactory = new ObjectFactory();
        cerif = objectFactory.createCERIF();
        translator = new CerifTypeTranslator();
        apifactory = new Cerif10APIFactory();
    }

    /**
     * Add a person entity to the underlying CERIF document
     * 
     * @param orcid
     * @param given
     * @param family
     * @param creditname
     * @param externalIDs
     * @return
     */
    public Cerif16Builder addPerson(String orcid, Optional<String> given, Optional<String> family, Optional<String> creditname, List<PersonExternalIdentifier> externalIDs) {
        person = objectFactory.createCfPersType();
        person.setCfPersId(orcid);
        person.getCfResIntOrCfKeywOrCfPersPers().add(buildFedID(orcid, CerifClassEnum.ORCID));

        // add in other external ids here
        for (PersonExternalIdentifier id : externalIDs) {
            if (translator.translate(id) != CerifClassEnum.OTHER) {
                person.getCfResIntOrCfKeywOrCfPersPers().add(buildFedID(id.getValue(), translator.translate(id)));
            }
        }

        if (given.isPresent() || family.isPresent()) {
            CfPersNamePers name = objectFactory.createCfPersTypeCfPersNamePers();
            if (given.isPresent())
                name.setCfFirstNames(given.get());
            if (family.isPresent())
                name.setCfFamilyNames(family.get());
            name.setCfClassId(CerifClassEnum.PASSPORT_NAME.getUuid());
            name.setCfClassSchemeId(CerifClassSchemeEnum.PERSON_NAMES.getUuid());
            person.getCfResIntOrCfKeywOrCfPersPers().add(objectFactory.createCfPersTypeCfPersNamePers(name));
        }

        if (creditname.isPresent()) {
            CfPersNamePers cn = objectFactory.createCfPersTypeCfPersNamePers();
            cn.setCfClassId(CerifClassEnum.PRESENTED_NAME.getUuid());
            cn.setCfClassSchemeId(CerifClassSchemeEnum.PERSON_NAMES.getUuid());
            cn.setCfOtherNames(creditname.get());
            person.getCfResIntOrCfKeywOrCfPersPers().add(objectFactory.createCfPersTypeCfPersNamePers(cn));
        }

        cerif.getCfClassOrCfClassSchemeOrCfClassSchemeDescr().add(person);
        return this;
    }

    /**
     * Add a ResultPublication to the underlying CERIF.
     * 
     * @param orcid
     * @param ws
     *            please ensure this has been filtered for visibility before
     *            passing in
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
        titleString.setCfLangCode("en");
        pub.getCfTitleOrCfAbstrOrCfKeyw().add(objectFactory.createCfResPublTypeCfTitle(titleString));

        if (ws.getTitle().getTranslatedTitle() != null) {
            org.orcid.jaxb.model.common_v2.TranslatedTitle trans = ws.getTitle().getTranslatedTitle();
            CfMLangStringType transTitle = objectFactory.createCfMLangStringType();
            titleString.setValue(trans.getContent());
            titleString.setCfLangCode(trans.getLanguageCode());
            pub.getCfTitleOrCfAbstrOrCfKeyw().add(objectFactory.createCfResProdTypeCfName(transTitle));
        }

        // add type info
        CfCoreClassWithFractionType type = objectFactory.createCfCoreClassWithFractionType();
        type.setCfClassSchemeId(CerifClassSchemeEnum.OUTPUT_TYPES.getUuid());
        type.setCfClassId(translator.translate(ws.getType()).getUuid());
        pub.getCfTitleOrCfAbstrOrCfKeyw().add(objectFactory.createCfResPublTypeCfResPublClass(type));

        // add external identifiers
        if (ws.getExternalIdentifiers() != null && ws.getExternalIdentifiers().getExternalIdentifier() != null) {
            for (ExternalID id : ws.getExternalIdentifiers().getExternalIdentifier()) {
                if (exportedIDs.contains(id.getType()) && StringUtils.isNotEmpty(id.getValue())) {
                    pub.getCfTitleOrCfAbstrOrCfKeyw()
                            .add(this.buildFedID(id.getValue(), translator.translate(id.getType())));
                }
            }
        }
        xmlns.org.eurocris.cerif_1.CfResPublType.CfPersResPubl persRes = objectFactory.createCfResPublTypeCfPersResPubl();
        persRes.setCfPersId(orcid);
        persRes.setCfClassId(CerifClassEnum.CONTRIBUTOR.getUuid());
        persRes.setCfClassSchemeId(CerifClassSchemeEnum.PERSON_OUTPUT_CONTRIBUTIONS.getUuid());
        pub.getCfTitleOrCfAbstrOrCfKeyw().add(objectFactory.createCfResPublTypeCfPersResPubl(persRes));
        cerif.getCfClassOrCfClassSchemeOrCfClassSchemeDescr().add(pub);
        return this;
    }

    /**
     * Add a ResultProduct to the underlying CERIF document
     * 
     * @param orcid
     * @param ws
     *            please ensure this has been filtered for visibility before
     *            passing in
     * @param objectFactory
     * @return
     */
    public Cerif16Builder addProduct(String orcid, WorkSummary ws) {
        // create full record for publication
        CfResProdType prod = objectFactory.createCfResProdType();
        prod.setCfResProdId(orcid + ":" + ws.getPutCode());
        CfMLangStringType titleString = objectFactory.createCfMLangStringType();
        titleString.setValue(ws.getTitle().getTitle().getContent());
        titleString.setCfLangCode("en");
        prod.getCfNameOrCfDescrOrCfKeyw().add(objectFactory.createCfResProdTypeCfName(titleString));

        if (ws.getTitle().getTranslatedTitle() != null) {
            org.orcid.jaxb.model.common_v2.TranslatedTitle trans = ws.getTitle().getTranslatedTitle();
            CfMLangStringType transTitle = objectFactory.createCfMLangStringType();
            titleString.setValue(trans.getContent());
            titleString.setCfLangCode(trans.getLanguageCode());
            prod.getCfNameOrCfDescrOrCfKeyw().add(objectFactory.createCfResProdTypeCfName(transTitle));
        }

        // add type info
        CfCoreClassWithFractionType type = objectFactory.createCfCoreClassWithFractionType();
        type.setCfClassSchemeId(CerifClassSchemeEnum.OUTPUT_TYPES.getUuid());
        type.setCfClassId(translator.translate(ws.getType()).getUuid());
        prod.getCfNameOrCfDescrOrCfKeyw().add(objectFactory.createCfResProdTypeCfResProdClass(type));

        // add external identifiers
        if (ws.getExternalIdentifiers() != null && ws.getExternalIdentifiers().getExternalIdentifier() != null) {
            for (ExternalID id : ws.getExternalIdentifiers().getExternalIdentifier()) {
                if (exportedIDs.contains(id.getType()) && StringUtils.isNotEmpty(id.getValue())) {
                    prod.getCfNameOrCfDescrOrCfKeyw()
                            .add(this.buildFedID(id.getValue(), translator.translate(id.getType())));
                }
            }
        }
        xmlns.org.eurocris.cerif_1.CfResProdType.CfPersResProd persRes = objectFactory.createCfResProdTypeCfPersResProd();
        persRes.setCfPersId(orcid);
        persRes.setCfClassId(CerifClassEnum.CONTRIBUTOR.getUuid());
        persRes.setCfClassSchemeId(CerifClassSchemeEnum.PERSON_OUTPUT_CONTRIBUTIONS.getUuid());
        prod.getCfNameOrCfDescrOrCfKeyw().add(objectFactory.createCfResProdTypeCfPersResProd(persRes));
        cerif.getCfClassOrCfClassSchemeOrCfClassSchemeDescr().add(prod);
        return this;
    }

    /**
     * For each work that is a publication, add a reference to the person object
     * 
     * NOTE: will fail if you have not already added a person via addPerson()
     * 
     * @param as
     *            please ensure this has been filtered for visibility before
     *            passing in
     * @param orcid
     * @param addFullPublications
     * @return
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
            link.setCfClassId(CerifClassEnum.CONTRIBUTOR.getUuid());
            link.setCfClassSchemeId(CerifClassSchemeEnum.PERSON_OUTPUT_CONTRIBUTIONS.getUuid());
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
     * @param as
     *            please ensure this has been filtered for visibility before
     *            passing in
     * @param orcid
     * @param addFullPublications
     * @return
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
            link.setCfClassId(CerifClassEnum.CONTRIBUTOR.getUuid());
            link.setCfClassSchemeId(CerifClassSchemeEnum.PERSON_OUTPUT_CONTRIBUTIONS.getUuid());
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
        return apifactory.wrap(cerif);
    }

    /**
     * Create a federated identifer element suitable for use by all entities
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
