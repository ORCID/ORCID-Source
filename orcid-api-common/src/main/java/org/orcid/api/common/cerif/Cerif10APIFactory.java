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

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import xmlns.org.eurocris.cerif_1.CERIF;
import xmlns.org.eurocris.cerif_1.CfClassSchemeType;
import xmlns.org.eurocris.cerif_1.CfClassSchemeType.CfClass;
import xmlns.org.eurocris.cerif_1.CfMLangStringType;
import xmlns.org.eurocris.cerif_1.ObjectFactory;
import xmlns.org.eurocris.cerif_api.Cerifapitype;
import xmlns.org.eurocris.cerif_api.Cerifentitytype;
import xmlns.org.eurocris.cerif_api.Entitylisttype;
import xmlns.org.eurocris.cerif_api.Entitytype;
import xmlns.org.eurocris.cerif_api.Headertype;
import xmlns.org.eurocris.cerif_api.Payloadtype;

/**
 * Builds JAXB Cerif 1.0 API objects
 * 
 * Follows OpenAIRE semantics.
 * 
 * @see https://zenodo.org/record/17065/files/
 *      OpenAIRE_Guidelines_for_CRIS_Managers_v.1.0.pdf
 * @author tom
 *
 */
public class Cerif10APIFactory {

    private static Cerifapitype semanticsCERIF = null;
    private static Cerifapitype entities = null;

    //@formatter:off
    public static final Set<CerifClassEnum> nameSet = Sets.immutableEnumSet(
            CerifClassEnum.PASSPORT_NAME,
            CerifClassEnum.PRESENTED_NAME);
    //@formatter:off
    public static final  Set<CerifClassEnum> roleSet = Sets.immutableEnumSet(               
            CerifClassEnum.CONTRIBUTOR);
    //@formatter:off
    public static final  Set<CerifClassEnum> idSet = Sets.immutableEnumSet(                       
            CerifClassEnum.DOI,
            CerifClassEnum.HANDLE,
            CerifClassEnum.PMCID,
            CerifClassEnum.URL,
            CerifClassEnum.URI,
            CerifClassEnum.ISSN,
            CerifClassEnum.ISBN,
            CerifClassEnum.ORCID,
            CerifClassEnum.ISNI,
            CerifClassEnum.SCOPUSAUTHORID);
    //@formatter:off
    public static final Set<CerifClassEnum> outSet = Sets.immutableEnumSet(                              
            CerifClassEnum.PRODUCT_DATASET,
            CerifClassEnum.RESEARCHERID,
            CerifClassEnum.BOOK,
            CerifClassEnum.CHAPTER_IN_BOOK,
            CerifClassEnum.BOOK_REVIEW,
            CerifClassEnum.DICTIONARY_ENTRY,
            CerifClassEnum.DOCTORAL_THESIS,
            CerifClassEnum.ENCYCLOPEDIA_ENTRY,
            CerifClassEnum.EDITED_BOOK,
            CerifClassEnum.JOURNAL_ARTICLE,
            CerifClassEnum.JOURNAL_ISSUE,
            CerifClassEnum.MAGAZINE_ARTICLE,
            CerifClassEnum.MANUAL,
            CerifClassEnum.ONLINE_RESOURCE,
            CerifClassEnum.NEWSCLIPPING,
            CerifClassEnum.REPORT,
            CerifClassEnum.RESEARCH_TOOL,
            CerifClassEnum.SUPERVISED_STUDENT_PUBLICATIONS,
            CerifClassEnum.TEST,
            CerifClassEnum.TRANSLATION,
            CerifClassEnum.ONLINE_RESOURCE,
            CerifClassEnum.WORKING_PAPER,
            CerifClassEnum.CONFERENCE_PROCEEDINGS_ARTICLE,
            CerifClassEnum.CONFERENCE_ABSTRACT,
            CerifClassEnum.CONFERENCE_POSTER,
            CerifClassEnum.STANDARD_AND_POLICY,
            CerifClassEnum.OTHER
            );

    private ObjectFactory objectFactory;

    public Cerif10APIFactory(){
        objectFactory = new ObjectFactory();
    }
    
    /** The list of supported entities
     * 
     * @return
     */
    public Cerifapitype getEntities(){
        if (entities == null){
            Entitylisttype list = new Entitylisttype();
            Entitytype person = new Entitytype();
            person.setValue(Cerifentitytype.PERSONS);
            list.getEntity().add(person);
            Entitytype pubs = new Entitytype();
            pubs.setValue(Cerifentitytype.PUBLICATIONS);
            list.getEntity().add(pubs);
            Entitytype prods = new Entitytype();
            prods.setValue(Cerifentitytype.PRODUCTS);
            list.getEntity().add(prods);         
            entities = wrap(list);
        }
        return entities;
    }
    
    /** Build the semantics
     * 
     * @return
     */
    public Cerifapitype getSemantics(){
        if (semanticsCERIF != null)
            return semanticsCERIF;
        
        Map<CerifClassSchemeEnum,Set<CerifClassEnum>> map = Maps.newHashMap();        
        map.put(CerifClassSchemeEnum.PERSON_NAMES, nameSet);
        map.put(CerifClassSchemeEnum.OUTPUT_TYPES, outSet);
        map.put(CerifClassSchemeEnum.IDENTIFIER_TYPES, idSet);
        map.put(CerifClassSchemeEnum.PERSON_OUTPUT_CONTRIBUTIONS, roleSet);
        
        CERIF cerif = objectFactory.createCERIF();
        cerif.setSourceDatabase("http://orcid.org");

        //Enumerate the semantic classes
        for (CerifClassSchemeEnum k : map.keySet()){
            CfClassSchemeType c = objectFactory.createCfClassSchemeType();            
            c.setCfClassSchemeId(k.getUuid());
            for (CerifClassEnum kk : map.get(k)){
                CfClass cc = objectFactory.createCfClassSchemeTypeCfClass();
                cc.setCfClassId(kk.getUuid());
                //term
                CfMLangStringType term = objectFactory.createCfMLangStringType();
                term.setValue(kk.getName());
                term.setCfLangCode("en");
                cc.getCfDescrOrCfDescrSrcOrCfTerm().add(objectFactory.createCfClassSchemeTypeCfClassCfTerm(term));
                //term source
                CfMLangStringType termSrc = objectFactory.createCfMLangStringType();
                termSrc.setValue(kk.getSource());
                termSrc.setCfLangCode("en");
                cc.getCfDescrOrCfDescrSrcOrCfTerm().add(objectFactory.createCfClassSchemeTypeCfClassCfTermSrc(termSrc));
                c.getCfDescrOrCfDescrSrcOrCfName().add(objectFactory.createCfClassSchemeTypeCfClass(cc));
            }
            cerif.getCfClassOrCfClassSchemeOrCfClassSchemeDescr().add(c);
        }        
        semanticsCERIF = wrap(cerif);
        return semanticsCERIF;
    }
    
    public Cerifapitype wrap(Entitylisttype list){
        Cerifapitype apiType = new Cerifapitype();
        Headertype header = new Headertype();
        header.setApiVersion("1.0");
        apiType.setHeader(header);
        Payloadtype payload = new Payloadtype();
        payload.setEntities(list);
        apiType.setPayload(payload);
        return apiType;
    }
    
    public Cerifapitype wrap(CERIF cerif){
        Cerifapitype apiType = new Cerifapitype();
        Headertype header = new Headertype();
        header.setApiVersion("1.0");
        apiType.setHeader(header);
        Payloadtype payload = new Payloadtype();
        payload.setCERIF(cerif);
        apiType.setPayload(payload);
        return apiType;
    }
}
