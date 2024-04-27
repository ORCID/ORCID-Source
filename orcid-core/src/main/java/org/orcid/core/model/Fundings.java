package org.orcid.core.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "fundings", namespace = "http://www.orcid.org/ns/summary")
@Schema(description = "Fundings")
public class Fundings extends ItemsCount implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
       
}
