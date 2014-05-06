/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;

public class KeywordsForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private List<Text> keywords = new ArrayList<Text>();
    
    private Visibility visibility;

    public static KeywordsForm valueOf(Keywords keywords) {
        KeywordsForm kf = new KeywordsForm();
        if (keywords.getKeyword() != null) {
            for (Keyword keyword:keywords.getKeyword())
                if (keyword.getContent() != null)
                    kf.getKeywords().add(Text.valueOf(keyword.getContent()));
        }
        kf.setVisibility(Visibility.valueOf(keywords.getVisibility()));
        return kf;
    }

    public Keywords toKeywords() {
        Keywords keywords = new Keywords();
        List<Keyword> kList = new ArrayList<Keyword>();
        for (Text text : this.keywords) 
            kList.add(text.toKeyword());
        keywords.setKeyword(kList);
        keywords.setVisibility(this.getVisibility().getVisibility());
        return keywords;
    }

    
    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<Text> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Text> keywords) {
        this.keywords = keywords;
    }
    
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

}
