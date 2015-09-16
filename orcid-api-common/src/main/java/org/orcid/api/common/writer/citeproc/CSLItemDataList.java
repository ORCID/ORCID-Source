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

import java.util.ArrayList;
import java.util.List;

import de.undercouch.citeproc.csl.CSLItemData;

@Deprecated
public class CSLItemDataList {

    private List<CSLItemData> data = new ArrayList<CSLItemData>();

    public List<CSLItemData> getData() {
        return data;
    }

    public void setData(List<CSLItemData> data) {
        this.data = data;
    }
    
    public void addItem(CSLItemData item){
        if (item != null)
            data.add(item);
    }
}
