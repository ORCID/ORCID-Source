package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.record_rc2.Addresses;

public class AddressesForm implements ErrorsInterface, Serializable {    
    private static final long serialVersionUID = -5674080678098963655L;
    private List<String> errors = new ArrayList<String>();
    private List<AddressForm> addresses = new ArrayList<AddressForm>();    
    private Visibility visibility;
    
    public static AddressForm valueOf(Addresses addresses) {
        AddressForm form = new AddressForm();
        return form;
    }
    
    public Addresses toAddresses() {
        Addresses addresses = new Addresses();
        return addresses;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    public List<AddressForm> getAddresses() {
        return addresses;
    }
    public void setAddresses(List<AddressForm> addresses) {
        this.addresses = addresses;
    }
    public Visibility getVisibility() {
        return visibility;
    }
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }    
}
