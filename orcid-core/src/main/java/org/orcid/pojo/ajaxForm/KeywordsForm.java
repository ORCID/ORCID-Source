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

import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;

public class KeywordsForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private List<KeywordForm> keywords = new ArrayList<KeywordForm>();

    private Visibility visibility;

    public static KeywordsForm valueOf(Keywords keywords) {
        KeywordsForm kf = new KeywordsForm();
        
        if (keywords.getKeywords() != null) {
            for (Keyword keyword : keywords.getKeywords()) {
                if (keyword.getContent() != null) {
                    kf.getKeywords().add(KeywordForm.valueOf(keyword));
                }
            }
        }

        return kf;
    }

    public Keywords toKeywords() {
        Keywords keywords = new Keywords();
        List<Keyword> kList = new ArrayList<Keyword>();
        for (KeywordForm form : this.keywords) {
            kList.add(form.toKeyword());
        }
        keywords.setKeywords(kList);
        return keywords;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<KeywordForm> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<KeywordForm> keywords) {
        this.keywords = keywords;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

}
