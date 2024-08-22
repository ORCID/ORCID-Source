package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.utils.OrcidStringUtils;

public class Text implements ErrorsInterface, Required, Serializable, Comparable<Text> {
    
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    private String value;
    private boolean required = true;
    private String getRequiredMessage;

    public static Text valueOf(String value) {
        Text t = new Text();
        t.setValue(value);
       return t;
    }

    public static Text valueOf(Long value) {
        Text t = new Text();
        t.setValue(String.valueOf(value));
       return t;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = OrcidStringUtils.filterInvalidXMLCharacters(value);
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getGetRequiredMessage() {
        return getRequiredMessage;
    }

    public void setGetRequiredMessage(String getRequiredMessage) {
        this.getRequiredMessage = getRequiredMessage;
    }

    @Override
    public String toString(){
        return this.value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Text other = (Text) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public int compareTo(Text other) {
        if(other == null) {
            return 1;
        }
        
        if(PojoUtil.isEmpty(this)) {
            if(!PojoUtil.isEmpty(other)) {
                return -1;
            }
        } else {
            if(PojoUtil.isEmpty(other)) {
                return 1;
            } else {
                return value.compareTo(other.getValue());
            }
        }
        
        return 0;
    }            
}
