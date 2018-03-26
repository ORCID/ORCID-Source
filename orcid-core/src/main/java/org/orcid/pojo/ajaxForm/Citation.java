package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.message.CitationType;

public class Citation implements ErrorsInterface, Required, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    private Text citation;
    private Text citationType;
    private boolean required = true;
    private String getRequiredMessage;

    public Citation() {

    }

    public Citation(String value, String type) {
        this.citation = Text.valueOf(value);
        this.citationType = Text.valueOf(type);
    }

    public static Citation valueOf(org.orcid.jaxb.model.message.Citation citation) {
        Citation c = new Citation();
        if (citation.getCitation() != null) {
            Text cText = new Text();
            cText.setValue(citation.getCitation());
            c.setCitation(cText);
        }
        if (citation.getWorkCitationType() != null) {
            Text ctText = new Text();
            ctText.setValue(citation.getWorkCitationType().value());
            c.setCitationType(ctText);
        }
        return c;
    }

    public org.orcid.jaxb.model.message.Citation toCitiation() {
        org.orcid.jaxb.model.message.Citation c = new org.orcid.jaxb.model.message.Citation();
        if (this.getCitation() != null)
            c.setCitation(this.getCitation().getValue());
        if (!PojoUtil.isEmpty(this.getCitationType()))
            c.setWorkCitationType(CitationType.fromValue(this.getCitationType().getValue()));
        return c;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
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

    public Text getCitation() {
        return citation;
    }

    public void setCitation(Text citation) {
        this.citation = citation;
    }

    public Text getCitationType() {
        return citationType;
    }

    public void setCitationType(Text citationType) {
        this.citationType = citationType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((citation == null) ? 0 : citation.hashCode());
        result = prime * result + ((citationType == null) ? 0 : citationType.hashCode());
        result = prime * result + ((errors == null) ? 0 : errors.hashCode());
        result = prime * result + ((getRequiredMessage == null) ? 0 : getRequiredMessage.hashCode());
        result = prime * result + (required ? 1231 : 1237);
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
        Citation other = (Citation) obj;
        if (citation == null) {
            if (other.citation != null)
                return false;
        } else if (!citation.equals(other.citation))
            return false;
        if (citationType == null) {
            if (other.citationType != null)
                return false;
        } else if (!citationType.equals(other.citationType))
            return false;
        if (errors == null) {
            if (other.errors != null)
                return false;
        } else if (!errors.equals(other.errors))
            return false;
        if (getRequiredMessage == null) {
            if (other.getRequiredMessage != null)
                return false;
        } else if (!getRequiredMessage.equals(other.getRequiredMessage))
            return false;
        if (required != other.required)
            return false;
        return true;
    }
}
