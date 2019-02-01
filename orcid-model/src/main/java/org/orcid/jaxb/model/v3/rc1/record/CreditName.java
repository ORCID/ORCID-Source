package org.orcid.jaxb.model.v3.rc1.record;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModel;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlRootElement(name = "credit-name", namespace = "http://www.orcid.org/ns/personal-details")
@ApiModel(value = "CreditNameV3_0_rc1")
public class CreditName extends org.orcid.jaxb.model.v3.rc1.common.CreditName implements Serializable {
    private static final long serialVersionUID = -4407704518314072926L;

}
