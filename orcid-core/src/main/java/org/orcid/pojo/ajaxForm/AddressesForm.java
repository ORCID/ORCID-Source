package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;

public class AddressesForm implements ErrorsInterface, Serializable {    
    private static final long serialVersionUID = -5674080678098963655L;
    private List<String> errors = new ArrayList<String>();
    private List<AddressForm> addresses = new ArrayList<AddressForm>();    
    private Visibility visibility;
    
    public static AddressesForm valueOf(Addresses addresses) {
        AddressesForm form = new AddressesForm();
        if(addresses != null && addresses.getAddress() != null) {
            for(Address address : addresses.getAddress()) {
                form.getAddresses().add(AddressForm.valueOf(address));
            }
        }
        return form;
    }
    
    public Addresses toAddresses() {
        Addresses result = new Addresses();
        if(addresses != null) {
            List<Address> addressList = new ArrayList<Address>();
            for(AddressForm form : addresses) {
                addressList.add(form.toAddress());
            }
            result.setAddress(addressList);
        }
        return result;
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
