package org.orcid.frontend.web.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.orcid.core.manager.CountryManager;
import org.orcid.core.manager.CrossRefManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.SecurityQuestionManager;
import org.orcid.core.security.visibility.filter.VisibilityFilter;
import org.orcid.frontend.web.util.NumberList;
import org.orcid.frontend.web.util.YearsList;
import org.orcid.persistence.aop.ProfileLastModifiedAspect;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PojoUtil;
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

    @Resource
    protected CrossRefManager crossRefManager;

    @Resource
    protected SecurityQuestionManager securityQuestionManager;

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
        List<String> list = YearsList.createList();
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
    
    /**
     * Use {@link #retrieveIsoCountries()} instead.
     */

    @ModelAttribute("countries")
    @Deprecated
    public Map<String, String> retrieveCountries() {
        Map<String, String> countriesWithId = new LinkedHashMap<String, String>();
        List<String> countries = countryManager.retrieveCountries();
        countriesWithId.put("", "Select a country");
        for (String countryName : countries) {
            countriesWithId.put(countryName, countryName);
        }
        return countriesWithId;
    }

    @ModelAttribute("allDates")
    public Map<String, String> getAllDates() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = YearsList.createList();
        map.put("", "Select date");
        for (String year : list) {
            map.put(year, year);
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
                new DateTimeFormatterBuilder().appendPattern("yyyy")
                        .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                        .toFormatter(),
                new DateTimeFormatterBuilder().appendPattern("yyyyMM")
                        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                        .toFormatter(),
                new DateTimeFormatterBuilder().appendPattern("yyyyMMdd")
                        .parseStrict().toFormatter() };
        String dateString = date.getYear();
        if (date.getMonth() != null) {
            dateString += date.getMonth();
            if (date.getDay() != null) {
                dateString += date.getDay();
            }
        }
        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate localDate = LocalDate.parse(dateString, formatter);
                if (PojoUtil.isEmpty(date.getDay()) || localDate.getDayOfMonth() == Integer.parseInt(date.getDay())) {
                    // formatter will correct day to last valid day of month if it is too great
                    return true;
                }
            } catch (DateTimeParseException e) {
            }
        }
        return false;
    }
    
}
