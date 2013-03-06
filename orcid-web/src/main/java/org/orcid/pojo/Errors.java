package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

public class Errors implements ErrorsInterface {

	private List<String> errors = new ArrayList<String>();

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

}
