package org.orcid.email.trickle.manager.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.orcid.email.trickle.error.TrickleTooHeavyException;
import org.orcid.email.trickle.manager.TrickleManager;
import org.orcid.persistence.dao.EmailScheduleDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TrickleManagerImpl implements TrickleManager {

    private static final Logger LOG = LoggerFactory.getLogger(TrickleManagerImpl.class);

    @Resource(name = "emailScheduleDaoReadOnly")
    private EmailScheduleDao emailScheduleDaoReadOnly;

    @Resource
    private EmailScheduleDao emailScheduleDao;

    @Override
    public synchronized void attemptSend() {
        Long scheduleId = emailScheduleDaoReadOnly.currentScheduleAllows();
        if (scheduleId == -1L) {
            throw new TrickleTooHeavyException();
        }
        Date now = new Date();
        LOG.info("Cleared to send at {}", now);
        emailScheduleDao.updateLatestSent(scheduleId, now);
    }
    
}
