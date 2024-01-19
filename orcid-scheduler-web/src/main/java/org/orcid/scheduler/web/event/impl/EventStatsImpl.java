package org.orcid.scheduler.web.event.impl;

import org.orcid.core.togglz.Features;
import org.orcid.persistence.dao.EventDao;
import org.orcid.persistence.dao.EventStatsDao;
import org.orcid.scheduler.web.event.EventStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.time.LocalDate;

public class EventStatsImpl implements EventStats {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventStatsImpl.class);

    @Resource(name = "eventDao")
    private EventDao eventDao;

    @Resource(name = "eventStatsDao")
    private EventStatsDao eventStatsDao;

    @Value("${org.orcid.scheduler.event.deleteEvents.numberOfDays:90}")
    private int DELETE_EVENTS_OLDER_THAN_DAYS;
    
    @Override
    public void saveEventStats() {
        LocalDate date = LocalDate.now().minusDays(1);
        String currentDate = date.getDayOfMonth() + "/" + date.getMonth() + "/" + date.getYear();
        LOGGER.info("Storing aggregate data to event_stats table of the day" + currentDate);
        eventStatsDao.createEventStats();
    }

    @Override
    public void deleteEvents() {
        if (Features.DELETE_EVENTS.isActive()) {
            LOGGER.info("Deleting events older than "+ DELETE_EVENTS_OLDER_THAN_DAYS +" days");
            eventDao.deleteEventsByDate(DELETE_EVENTS_OLDER_THAN_DAYS);
        }
    }
}
