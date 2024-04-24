package org.orcid.api.member.model;

import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "works", namespace = "http://www.orcid.org/ns/summary")
@Schema(description = "Works")
public class Works extends ItemsCount {

}
