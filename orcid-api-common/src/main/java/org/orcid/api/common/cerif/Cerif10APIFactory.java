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

import java.io.IOException;
import java.io.InputStream;

import xmlns.org.eurocris.cerif_api.Cerifentitytype;
import xmlns.org.eurocris.cerif_api.Entitylisttype;
import xmlns.org.eurocris.cerif_api.Entitytype;

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
    
    private static String semanticsXML = null;
    private static Entitylisttype entities = null;
    
    /** The list of supported entities
     * 
     * @return
     */
    public Entitylisttype getEntities(){
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
            entities = list;
        }
        return entities;
    }
    
    /** The semantic mapping.  Hard coded to OpenAIRE.
     * 
     * @return
     */
    public String getSemantics(){
        if (semanticsXML == null)
            try {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("cerif/OpenAIRE_CERIF_Semantics_v.1.0.xml");
                semanticsXML = org.apache.commons.io.IOUtils.toString(is);
            } catch (IOException e) {
                return "";
            }
        return semanticsXML;
    }
}
