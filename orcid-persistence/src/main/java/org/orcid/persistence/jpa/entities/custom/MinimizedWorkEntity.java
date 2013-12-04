/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.jpa.entities.custom;

import org.orcid.jaxb.model.message.Visibility;

/**
 * An object that will contain the minimum work information needed to display
 * work in the UI.
 * 
 * @author Angel Montenegro (amontenegro)
 * */
public class MinimizedWorkEntity {
	private Long id;
	private String title;
	private String subtitle;
	private String description;
	private int publicationDay;
	private int publicationMonth;
	private int publicationYear;
	private Visibility visibility;

	public MinimizedWorkEntity() {
		super();
	}

	public MinimizedWorkEntity(Long id, String title, String subtitle,
			String description, Integer publicationDay,
			Integer publicationMonth, Integer publicationYear,
			Visibility visibility) {
		super();
		this.id = id;
		this.title = title;
		this.subtitle = subtitle;
		this.description = description;
		if (publicationDay != null)
			this.publicationDay = publicationDay;
		if (publicationMonth != null)
			this.publicationMonth = publicationMonth;
		if (publicationYear != null)
			this.publicationYear = publicationYear;
		this.visibility = visibility;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPublicationDay() {
		return publicationDay;
	}

	public void setPublicationDay(int publicationDay) {
		this.publicationDay = publicationDay;
	}

	public int getPublicationMonth() {
		return publicationMonth;
	}

	public void setPublicationMonth(int publicationMonth) {
		this.publicationMonth = publicationMonth;
	}

	public int getPublicationYear() {
		return publicationYear;
	}

	public void setPublicationYear(int publicationYear) {
		this.publicationYear = publicationYear;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}
}