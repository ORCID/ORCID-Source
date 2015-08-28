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
package org.orcid.api.common.writer.citeproc;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.ParseException;
import org.orcid.jaxb.model.record.CitationType;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.summary.ActivitiesSummary;
import org.springframework.util.ReflectionUtils;

import de.undercouch.citeproc.bibtex.BibTeXConverter;
import de.undercouch.citeproc.csl.CSLItemData;
import de.undercouch.citeproc.csl.CSLName;
import de.undercouch.citeproc.csl.CSLNameBuilder;

public class WorkToCiteprocTranslator {

    private final Field authorField = ReflectionUtils.findField(CSLItemData.class, "author");
    private final Field literalField = ReflectionUtils.findField(CSLName.class, "literal");

    /** Turn a work into Citeproc JSON 
     * Horrible use of reflection to shorten hyperauthorship.  It will strip anything above 20 authors down to the primary author and 'et all'.
     * 
     * @param work
     * @return the JSON as a String.
     */
    public CSLItemData toCiteproc(Work work) {
        //if we have bibtex, use it
        if (work.getWorkCitation() != null && work.getWorkCitation().getWorkCitationType() != null
                && work.getWorkCitation().getWorkCitationType().equals(CitationType.BIBTEX)) {
            try {
                BibTeXConverter conv = new BibTeXConverter();
                BibTeXDatabase db = conv.loadDatabase(IOUtils.toInputStream(work.getWorkCitation().getCitation()));
                Map<String, CSLItemData> cids = conv.toItemData(db);
                if (cids.size() == 1) {
                    CSLItemData item = cids.values().iterator().next();
                    
                    if (item.getAuthor().length > 20){
                        CSLName [] abrev = Arrays.copyOf(item.getAuthor(), 1);
                        //this wrong but better than nothing... CSL only supports this in style definitions.  Could add a literal.
                        abrev[0] = new CSLNameBuilder().literal(abrev[0].getGiven()+" "+abrev[0].getFamily()+" "+"et all.").build();
                        ReflectionUtils.makeAccessible(authorField);
                        ReflectionUtils.setField(authorField, item , abrev);//so dangerous!
                    }
                    
                    for (int i=0;i< item.getAuthor().length;i++){
                        if (item.getAuthor()[i].getLiteral()!=null && item.getAuthor()[i].getLiteral().length()>200){
                            ReflectionUtils.makeAccessible(literalField);
                            ReflectionUtils.setField(literalField, item.getAuthor()[i] , StringUtils.abbreviate(item.getAuthor()[i].getLiteral(), 200));//so dangerous!
                        }
                    }
                    return item;
                }
            } catch (IOException | ParseException e) {
                return null;
            }
        }
        //otherwise use ORCID metadata
        return null;
    }

    public String toCiteproc(ActivitiesSummary work) {
        return "{blah:[\"yup\",\"nope\"}";
    }

    // how do we manage content negotiation using suffixes? or query params?
    // like this! https://gist.github.com/sathish06/6587432
    // https://jersey.java.net/apidocs/2.21/jersey/org/glassfish/jersey/server/filter/UriConnegFilter.html

}