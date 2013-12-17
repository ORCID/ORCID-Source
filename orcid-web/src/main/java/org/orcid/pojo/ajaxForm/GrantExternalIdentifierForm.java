package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hsqldb.lib.StringUtil;
import org.orcid.jaxb.model.message.GrantExternalIdentifier;
import org.orcid.jaxb.model.message.Url;

public class GrantExternalIdentifierForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text putCode;    
    private Text type;
    private Text value;
    private Text url;
    
	@Override
	public List<String> getErrors() {
		return this.errors;
	}
	@Override
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	public Text getPutCode() {
		return putCode;
	}
	public void setPutCode(Text putCode) {
		this.putCode = putCode;
	}
	public Text getType() {
		return type;
	}
	public void setType(Text type) {
		this.type = type;
	}
	public Text getValue() {
		return value;
	}
	public void setValue(Text value) {
		this.value = value;
	}
	public Text getUrl() {
		return url;
	}
	public void setUrl(Text url) {
		this.url = url;
	}

	public static GrantExternalIdentifierForm valueOf(GrantExternalIdentifier grantExternalIdentifier){
		GrantExternalIdentifierForm result = new GrantExternalIdentifierForm();
		if(!StringUtil.isEmpty(grantExternalIdentifier.getPutCode()))
			result.setPutCode(Text.valueOf(grantExternalIdentifier.getPutCode()));
		if(!StringUtil.isEmpty(grantExternalIdentifier.getType()))
			result.setType(Text.valueOf(grantExternalIdentifier.getType()));
		if(grantExternalIdentifier.getUrl() != null && !StringUtil.isEmpty(grantExternalIdentifier.getUrl().getValue()))
			result.setUrl(Text.valueOf(grantExternalIdentifier.getUrl().getValue()));
		if(!StringUtil.isEmpty(grantExternalIdentifier.getValue()))
			result.setValue(Text.valueOf(grantExternalIdentifier.getValue()));
		return result;
	}
	
	public GrantExternalIdentifier toGrantExternalIdentifier() {
		GrantExternalIdentifier result = new GrantExternalIdentifier();
		if(!PojoUtil.isEmpty(putCode))
			result.setPutCode(putCode.getValue());
		if(!PojoUtil.isEmpty(type))
			result.setType(type.getValue());
		if(!PojoUtil.isEmpty(url))
			result.setUrl(new Url(url.getValue()));
		if(!PojoUtil.isEmpty(value))
			result.setValue(value.getValue());
		return result;
	}
}
