package org.orcid.frontend.web.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.aop.ProfileLastModifiedAspect;
import org.orcid.core.manager.CountryManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.v3.ActivityManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.security.visibility.filter.VisibilityFilter;
import org.orcid.frontend.web.util.NumberList;
import org.orcid.frontend.web.util.YearsList;
import org.orcid.pojo.ajaxForm.Contributor;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * @author Declan Newman (declan) Date: 22/02/2012
 */
public class BaseWorkspaceController extends BaseController {

    protected static final String ORCID_ID_HASH = "orcid_hash";

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    protected CountryManager countryManager;

    @Resource(name = "profileEntityManagerV3")
    protected ProfileEntityManager profileEntityManager;

    @Resource(name = "visibilityFilter")
    protected VisibilityFilter visibilityFilter;

    @Resource
    private ProfileLastModifiedAspect profileLastModifiedAspect;

    protected long getLastModified(String orcid) {
        java.util.Date lastModified = profileLastModifiedAspect.retrieveLastModifiedDate(orcid);
        return (lastModified == null) ? 0 : lastModified.getTime();
    }

    @ModelAttribute("years")
    public Map<String, String> retrieveYearsAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = YearsList.createList(0);
        map.put("", getMessage("select.item.year"));
        for (String year : list) {
            map.put(year, year);
        }
        return map;
    }

    @ModelAttribute("affiliationYears")
    public Map<String, String> retrieveAffiliationsYearsAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = YearsList.createList(10);
        map.put("", getMessage("select.item.year"));
        for (String year : list) {
            map.put(year, year);
        }
        return map;
    }

    @ModelAttribute("fundingYears")
    public Map<String, String> retrieveFundingYearsAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = YearsList.createList(10);
        map.put("", getMessage("select.item.year"));
        for (String year : list) {
            map.put(year, year);
        }
        return map;
    }

    @ModelAttribute("months")
    public Map<String, String> retrieveMonthsAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = NumberList.createList(12);
        map.put("", getMessage("select.item.month"));
        for (String month : list) {
            map.put(month, month);
        }
        return map;
    }

    @ModelAttribute("days")
    public Map<String, String> retrieveDaysAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = NumberList.createList(31);
        map.put("", getMessage("select.item.day"));
        for (String day : list) {
            map.put(day, day);
        }
        return map;
    }

    @ModelAttribute("orcidIdHash")
    String getOrcidHash(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(false);
        String hash = session != null ? (String) session.getAttribute(ORCID_ID_HASH) : null;
        if (!PojoUtil.isEmpty(hash)) {
            return hash;
        }
        hash = profileEntityManager.getOrcidHash(getEffectiveUserOrcid());
        if (session != null) {
            request.getSession().setAttribute(ORCID_ID_HASH, hash);
        }
        return hash;
    }

    protected boolean validDate(Date date) {
        DateTimeFormatter[] formatters = {
                new DateTimeFormatterBuilder().appendPattern("yyyy").parseDefaulting(ChronoField.MONTH_OF_YEAR, 1).parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                        .toFormatter(),
                new DateTimeFormatterBuilder().appendPattern("yyyyMM").parseDefaulting(ChronoField.DAY_OF_MONTH, 1).toFormatter(),
                new DateTimeFormatterBuilder().appendPattern("yyyyMMdd").parseStrict().toFormatter() };
        String dateString = date.getYear();
        // If the month is empty and day provided is an invalid date
        if (StringUtils.isBlank(date.getMonth())) {
            if (!StringUtils.isBlank(date.getDay())) {
                return false;
            }
        }
        else if (StringUtils.isBlank(date.getYear())) {
            if (!StringUtils.isBlank(date.getDay()) && !StringUtils.isBlank(date.getMonth())) {
                return false;
            }
        }
        else {
            dateString += date.getMonth();
            if (!StringUtils.isBlank(date.getDay())) {
                dateString += date.getDay();
            }
        }

        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate localDate = LocalDate.parse(dateString, formatter);
                if (PojoUtil.isEmpty(date.getDay()) || localDate.getDayOfMonth() == Integer.parseInt(date.getDay())) {
                    // formatter will correct day to last valid day of month if
                    // it is too great
                    return true;
                }
            } catch (DateTimeParseException e) {
            }
        }
        return false;
    }

    protected List<Contributor> filterContributors(List<Contributor> contributors, ActivityManager activityManager) {
        List<Contributor> newContributorsList = new ArrayList<>();
        for (Contributor contributor : contributors) {
            if (!PojoUtil.isEmpty(contributor.getOrcid())) {
                String contributorOrcid = contributor.getOrcid().getValue();
                if (profileEntityManager.orcidExists(contributorOrcid)) {
                    String publicContributorCreditName = activityManager.getPublicCreditName(contributorOrcid);
                    contributor.setCreditName(Text.valueOf(publicContributorCreditName));
                }
                newContributorsList.add(contributor);
            } else if (!PojoUtil.isEmpty(contributor.getCreditName())) {
                newContributorsList.add(contributor);
            }
        }
        return newContributorsList;
    }
}
