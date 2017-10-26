/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.List;

public class ImportWizzardForm implements Serializable {
    private static final long serialVersionUID = -8888090231363714695L;
    private List<Client> clients;
    private List<Text> geoAreas;
    private List<Text> types;
    private Text defaultArea;
    private Text defaultType;
    private Text type;

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public List<Text> getGeoAreas() {
        return geoAreas;
    }

    public void setGeoAreas(List<Text> geoAreas) {
        this.geoAreas = geoAreas;
    }

    public List<Text> getTypes() {
        return types;
    }

    public void setTypes(List<Text> types) {
        this.types = types;
    }

    public Text getDefaultArea() {
        return defaultArea;
    }

    public void setDefaultArea(Text defaultArea) {
        this.defaultArea = defaultArea;
    }

    public Text getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(Text defaultType) {
        this.defaultType = defaultType;
    }

    public Text getType() {
        return type;
    }

    public void setType(Text type) {
        this.type = type;
    }
}