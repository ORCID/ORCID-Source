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
package org.orcid.jaxb.model.statistics;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a timeline of statistics.
 * 
 * @author tom
 *
 */
@XmlRootElement(name = "statistics-timeline")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatisticsTimeline implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "statistic-name")
    protected String statisticName;
    protected Map<Long, Long> timeline;

    public String getStatisticName() {
        return statisticName;
    }

    public void setStatisticName(String name) {
        this.statisticName = name;
    }

    public Map<Long, Long> getTimeline() {
        return timeline;
    }

    public void setTimeline(Map<Long, Long> timeline) {
        this.timeline = timeline;
    }
}
