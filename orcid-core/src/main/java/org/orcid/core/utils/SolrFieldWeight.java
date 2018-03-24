package org.orcid.core.utils;

/**
 * 
 * @author Will Simpson
 * 
 */
public class SolrFieldWeight {

    private String field;
    private float weight;

    public SolrFieldWeight() {
    }

    public SolrFieldWeight(String field, float weight) {
        this.field = field;
        this.weight = weight;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
