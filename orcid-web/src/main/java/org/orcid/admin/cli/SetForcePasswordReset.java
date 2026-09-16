package org.orcid.admin.cli;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.frontend.spring.session.redis.OrcidRedisIndexedSessionRepository;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.session.FindByIndexNameSessionRepository;

public class SetForcePasswordReset {

    private static final Logger LOG = LoggerFactory.getLogger(SetForcePasswordReset.class);
    private static final int FORCE_PASSWORD_RESET_BATCH_SIZE = 10000;

    @Option(name = "-o", usage = "Comma-separated list of ORCID iDs", required = true)
    private String orcidIds;

    private ProfileDao profileDao;
    private OrcidRedisIndexedSessionRepository sessionRepository;
    private ClassPathXmlApplicationContext context;

    public static void main(String[] args) {
        int exitCode = 0;
        SetForcePasswordReset setForcePasswordReset = new SetForcePasswordReset();
        CmdLineParser parser = new CmdLineParser(setForcePasswordReset);

        try {
            parser.parseArgument(args);
            setForcePasswordReset.validateArgs(parser);
            setForcePasswordReset.init();
            setForcePasswordReset.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            exitCode = 1;
        } catch (Throwable t) {
            LOG.error("Error forcing password reset", t);
            System.err.println(t.getMessage());
            exitCode = 2;
        } finally {
            setForcePasswordReset.close();
            System.exit(exitCode);
        }
    }

    void execute() {
        List<String> ids = parseOrcidIds();
        List<String> existingOrcidIds = new ArrayList<>();
        List<String> missingOrcidIds = new ArrayList<>();

        for (String orcidId : ids) {
            if (profileDao.orcidExists(orcidId)) {
                existingOrcidIds.add(orcidId);
            } else {
                missingOrcidIds.add(orcidId);
            }
        }

        if (existingOrcidIds.isEmpty()) {
            LOG.warn("No matching profiles found for the provided ORCID iDs");
            if (!missingOrcidIds.isEmpty()) {
                LOG.warn("ORCID iDs not found: {}", missingOrcidIds);
            }
            return;
        }

        Date executionDate = new Date();
        int updatedProfiles = updateForcePasswordResetInBatches(existingOrcidIds, executionDate);
        int evictedSessions = evictSessions(existingOrcidIds);

        LOG.info("Updated force_password_reset for {} profiles with execution date {}", updatedProfiles, executionDate);
        LOG.info("Evicted {} active sessions linked to the provided ORCID iDs", evictedSessions);
        if (!missingOrcidIds.isEmpty()) {
            LOG.warn("Skipped {} ORCID iDs that do not exist: {}", missingOrcidIds.size(), missingOrcidIds);
        }
    }

    void validateArgs(CmdLineParser parser) throws CmdLineException {
        List<String> ids = parseOrcidIds();
        if (ids.isEmpty()) {
            throw new CmdLineException(parser, "-o must include at least one ORCID iD");
        }

        List<String> invalidOrcidIds = new ArrayList<>();
        for (String orcidId : ids) {
            if (!OrcidStringUtils.isValidOrcid(orcidId)) {
                invalidOrcidIds.add(orcidId);
            }
        }

        if (!invalidOrcidIds.isEmpty()) {
            throw new CmdLineException(parser, "Invalid ORCID iD(s): " + StringUtils.join(invalidOrcidIds, ", "));
        }
    }

    @SuppressWarnings("resource")
    private void init() {
        context = new ClassPathXmlApplicationContext("orcid-frontend-web-servlet.xml");
        profileDao = context.getBean(ProfileDao.class);
        sessionRepository = context.getBean(OrcidRedisIndexedSessionRepository.class);
    }

    private void close() {
        if (context != null) {
            context.close();
        }
    }

    private List<String> parseOrcidIds() {
        Set<String> uniqueOrcidIds = new LinkedHashSet<>();
        if (StringUtils.isBlank(orcidIds)) {
            return new ArrayList<>();
        }

        String[] splitOrcidIds = StringUtils.split(orcidIds, ',');
        if (splitOrcidIds == null) {
            return new ArrayList<>();
        }

        for (String value : splitOrcidIds) {
            String trimmedValue = StringUtils.trimToEmpty(value);
            if (StringUtils.isNotBlank(trimmedValue)) {
                uniqueOrcidIds.add(trimmedValue);
            }
        }

        return new ArrayList<>(uniqueOrcidIds);
    }

    private int evictSessions(List<String> orcidIds) {
        int evictedSessions = 0;
        for (String orcidId : orcidIds) {
            Map<String, ?> activeSessions = sessionRepository.findByIndexNameAndIndexValue(
                    FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, orcidId);
            for (String sessionId : activeSessions.keySet()) {
                sessionRepository.deleteById(sessionId);
                evictedSessions++;
            }
        }
        return evictedSessions;
    }

    int updateForcePasswordResetInBatches(List<String> orcidIds, Date executionDate) {
        int updatedProfiles = 0;
        for (int i = 0; i < orcidIds.size(); i += FORCE_PASSWORD_RESET_BATCH_SIZE) {
            int batchEnd = Math.min(i + FORCE_PASSWORD_RESET_BATCH_SIZE, orcidIds.size());
            updatedProfiles += profileDao.updateForcePasswordReset(orcidIds.subList(i, batchEnd), executionDate);
        }
        return updatedProfiles;
    }
}
