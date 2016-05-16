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
package org.orcid.persistence.jpa.entities;

import java.util.Date;

/**
 * @author Angel Montenegro
 * */
public class MinimizedSourceEntity {
    private String id;
    private String name;
    private boolean isAMember = false;
    private Date lastModified;
        
    public MinimizedSourceEntity(String id, String name, boolean isAMember) {
        this.setId(id);
        this.setName(name);
        this.setIsAMember(isAMember);
    }
        
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isAMember() {
        return isAMember;
    }
    public void setIsAMember(boolean isAMember) {
        this.isAMember = isAMember;
    }
    public Date getLastModified() {
        return lastModified;
    }
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }               
}
