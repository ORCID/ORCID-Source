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
