package org.orcid.pojo.ajaxForm;

import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.jaxb.model.message.Day;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.Month;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.UrlName;
import org.orcid.jaxb.model.message.Year;
import org.orcid.jaxb.model.v3.release.common.PublicationDate;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.pojo.WorkSummaryExtended;

public class PojoUtil {
	
    public static boolean anyIsEmtpy(Text ...texts) {
    	for(Text text : texts){
    		if(isEmpty(text)){
    			return true;
    		}
    	}
    	return false;
    }
    
    public static boolean areAllEmtpy(Text ...texts) {
    	for(Text text : texts){
    		if(!isEmpty(text)){
    			return false;
    		}
    	}
    	return true;
    }
    
    public static boolean areAllEmpty(String ... strings) {
        for(String s : strings) {
            if(!isEmpty(s)) {
                return false;
            }
        }
        return true;
    }
	
    public static boolean isEmpty(Text text) {
        if (text == null || text.getValue() == null || text.getValue().trim().isEmpty()) return true;
        return false;
    }

    public static boolean isEmpty(Url url) {
        if (url == null || url.getValue() == null || url.getValue().trim().isEmpty()) return true;
        return false;
    }

    public static boolean isEmpty(org.orcid.jaxb.model.common_v2.Url url) {
        if (url == null || url.getValue() == null || url.getValue().trim().isEmpty()) return true;
        return false;
    }
    
    public static boolean isEmpty(org.orcid.jaxb.model.v3.release.common.Url url) {
        if (url == null || url.getValue() == null || url.getValue().trim().isEmpty()) return true;
        return false;
    }
    
    public static boolean isEmpty(UrlName urlName) {
        if (urlName == null || urlName.getContent() == null) return true;
        return false;
    }

    public static boolean isEmpty(String string) {
        if (string == null || string.trim().isEmpty()) return true;
        return false;
    }
    
    @Deprecated
    public static String createDateSortString(FuzzyDate start, FuzzyDate end) {
        String year = "0";
        String month = "0";
        String day = "0";
        if (!isEmpty(start) && !isEmpty(start.getYear())) {
            year = start.getYear().getValue();
            if (!PojoUtil.isEmpty(start.getMonth()))
                month = start.getMonth().getValue();
            if (!PojoUtil.isEmpty(start.getDay()))
                day = start.getDay().getValue();
        } else if (!isEmpty(end) && !isEmpty(end.getYear())) {
            year = end.getYear().getValue();
            if (!PojoUtil.isEmpty(end.getMonth()))
                month = end.getMonth().getValue();
            if (!PojoUtil.isEmpty(end.getDay()))
                day = end.getDay().getValue();
        }
        return year + "-" + month + '-' + day;
    }
    
    public static String createDateSortString(org.orcid.jaxb.model.common_v2.FuzzyDate start, org.orcid.jaxb.model.common_v2.FuzzyDate end) {
        String year = "0";
        String month = "0";
        String day = "0";
        if (!isEmpty(start) && !isEmpty(start.getYear())) {
            year = start.getYear().getValue();
            if (!PojoUtil.isEmpty(start.getMonth()))
                month = start.getMonth().getValue();
            if (!PojoUtil.isEmpty(start.getDay()))
                day = start.getDay().getValue();
        } else if (!isEmpty(end) && !isEmpty(end.getYear())) {
            year = end.getYear().getValue();
            if (!PojoUtil.isEmpty(end.getMonth()))
                month = end.getMonth().getValue();
            if (!PojoUtil.isEmpty(end.getDay()))
                day = end.getDay().getValue();
        }
        return year + "-" + month + '-' + day;
    }       
    
    public static String createDateSortString(org.orcid.jaxb.model.v3.release.common.FuzzyDate start, org.orcid.jaxb.model.v3.release.common.FuzzyDate end) {
        String year = "0";
        String month = "0";
        String day = "0";
        if (!isEmpty(start) && !isEmpty(start.getYear())) {
            year = start.getYear().getValue();
            if (!PojoUtil.isEmpty(start.getMonth()))
                month = start.getMonth().getValue();
            if (!PojoUtil.isEmpty(start.getDay()))
                day = start.getDay().getValue();
        } else if (!isEmpty(end) && !isEmpty(end.getYear())) {
            year = end.getYear().getValue();
            if (!PojoUtil.isEmpty(end.getMonth()))
                month = end.getMonth().getValue();
            if (!PojoUtil.isEmpty(end.getDay()))
                day = end.getDay().getValue();
        }
        return year + "-" + month + '-' + day;
    }
    
    // Date sort string for 2.0 API
    public static String createDateSortString(org.orcid.jaxb.model.record.summary_v2.EducationSummary education) {
        return createDateSortStringForAffiliations(education.getStartDate(), education.getEndDate(), education.getCreatedDate());
    }

    public static String createDateSortString(org.orcid.jaxb.model.record.summary_v2.EmploymentSummary employment) {
        return createDateSortStringForAffiliations(employment.getStartDate(), employment.getEndDate(), employment.getCreatedDate());
    }
    
    public static String createDateSortString(org.orcid.jaxb.model.record_v2.Affiliation affiliation) {
        return createDateSortStringForAffiliations(affiliation.getStartDate(), affiliation.getEndDate(), affiliation.getCreatedDate());
    }

    private static String createDateSortStringForAffiliations(org.orcid.jaxb.model.common_v2.FuzzyDate startDate, org.orcid.jaxb.model.common_v2.FuzzyDate endDate,
            org.orcid.jaxb.model.common_v2.CreatedDate createdDate) {
        String dateSortString = "";
        if (startDate == null && endDate == null) {
            XMLGregorianCalendar date = createdDate.getValue();
            dateSortString = "Z-" + date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
        } else {
            if (endDate == null) {
                dateSortString = "Y-";
                dateSortString += startDate.getYear() == null ? "NaN" : startDate.getYear().getValue();
                if (!PojoUtil.isEmpty(startDate.getMonth())) {
                    dateSortString += "-" + startDate.getMonth().getValue();
                } else {
                    dateSortString += "-00";
                }
                if (!PojoUtil.isEmpty(startDate.getDay())) {
                    dateSortString += "-" + startDate.getDay().getValue();
                } else {
                    dateSortString += "-00";
                }
            } else {
                dateSortString = "X-";
                dateSortString += endDate.getYear() == null ? "NaN" : endDate.getYear().getValue();
                if (!PojoUtil.isEmpty(endDate.getMonth())) {
                    dateSortString += "-" + endDate.getMonth().getValue();
                } else {
                    dateSortString += "-00";
                }

                if (!PojoUtil.isEmpty(endDate.getDay())) {
                    dateSortString += "-" + endDate.getDay().getValue();
                } else {
                    dateSortString += "-00";
                }

                if (startDate != null) {
                    dateSortString += startDate.getYear() == null ? "-NaN" : "-" + startDate.getYear().getValue();
                    if (!PojoUtil.isEmpty(startDate.getMonth())) {
                        dateSortString += "-" + startDate.getMonth().getValue();
                    } else {
                        dateSortString += "-00";
                    }

                    if (!PojoUtil.isEmpty(startDate.getDay())) {
                        dateSortString += "-" + startDate.getDay().getValue();
                    } else {
                        dateSortString += "-00";
                    }
                }
            }
        }

        return dateSortString;
    }

    // Date sort string for v3.rc2 API
    public static String createDateSortString(AffiliationSummary affiliation) {
        return createDateSortStringForAffiliations(affiliation.getStartDate(), affiliation.getEndDate(), affiliation.getCreatedDate());
    }

    public static String createDateSortString(org.orcid.jaxb.model.v3.release.record.Affiliation affiliation) {
        return createDateSortStringForAffiliations(affiliation.getStartDate(), affiliation.getEndDate(), affiliation.getCreatedDate());
    }

    private static String createDateSortStringForAffiliations(org.orcid.jaxb.model.v3.release.common.FuzzyDate startDate,
            org.orcid.jaxb.model.v3.release.common.FuzzyDate endDate, org.orcid.jaxb.model.v3.release.common.CreatedDate createdDate) {
        String dateSortString = "";
        if (startDate == null && endDate == null) {
            XMLGregorianCalendar date = createdDate.getValue();
            dateSortString = "Z-" + date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
        } else {
            if (endDate == null) {
                dateSortString = "Y-";
                dateSortString += startDate.getYear() == null ? "NaN" : startDate.getYear().getValue();
                if (!PojoUtil.isEmpty(startDate.getMonth())) {
                    dateSortString += "-" + startDate.getMonth().getValue();
                } else {
                    dateSortString += "-00";
                }

                if (!PojoUtil.isEmpty(startDate.getDay())) {
                    dateSortString += "-" + startDate.getDay().getValue();
                } else {
                    dateSortString += "-00";
                }
            } else {
                dateSortString = "X-";
                dateSortString += endDate.getYear() == null ? "NaN" : endDate.getYear().getValue();
                if (!PojoUtil.isEmpty(endDate.getMonth())) {
                    dateSortString += "-" + endDate.getMonth().getValue();
                } else {
                    dateSortString += "-00";
                }
                if (!PojoUtil.isEmpty(endDate.getDay())) {
                    dateSortString += "-" + endDate.getDay().getValue();
                } else {
                    dateSortString += "-00";
                }
                if (startDate != null) {
                    dateSortString += startDate.getYear() == null ? "-NaN" : "-" + startDate.getYear().getValue();
                    if (!PojoUtil.isEmpty(startDate.getMonth())) {
                        dateSortString += "-" + startDate.getMonth().getValue();
                    } else {
                        dateSortString += "-00";
                    }
                    if (!PojoUtil.isEmpty(startDate.getDay())) {
                        dateSortString += "-" + startDate.getDay().getValue();
                    } else {
                        dateSortString += "-00";
                    }
                }
            }
        }

        return dateSortString;
    }
    
    public static boolean isEmpty(Date date) {
        if (date == null) return true;
        if (!PojoUtil.isEmpty(date.getDay()))
            return false;
        if (!PojoUtil.isEmpty(date.getMonth()))
            return false;
        if (!PojoUtil.isEmpty(date.getYear()))
            return false;
        return true;
    }
    
    public static boolean isEmpty(FuzzyDate date) {
        if (date == null) return true;
        if (!isEmpty(date.getDay()))
            return false;
        if (!isEmpty(date.getMonth()))
            return false;
        if (!isEmpty(date.getYear()))
            return false;
        return true;
    }
    
    public static boolean isEmpty(org.orcid.jaxb.model.common_v2.FuzzyDate date) {
        if (date == null) return true;
        if (!isEmpty(date.getDay()))
            return false;
        if (!isEmpty(date.getMonth()))
            return false;
        if (!isEmpty(date.getYear()))
            return false;
        return true;
    }
    
    public static boolean isEmpty(org.orcid.jaxb.model.v3.release.common.FuzzyDate date) {
        if (date == null) return true;
        if (!isEmpty(date.getDay()))
            return false;
        if (!isEmpty(date.getMonth()))
            return false;
        if (!isEmpty(date.getYear()))
            return false;
        return true;
    }
    
    public static boolean isEmpty(Year year) {
        if (year==null) return true;
        return isEmpty(year.getValue());
    }
    
    public static boolean isEmpty(Day day) {
        if (day==null) return true;
        return isEmpty(day.getValue());
    }

    public static boolean isEmpty(Month month) {
        if (month==null) return true;
        return isEmpty(month.getValue());
    }

    public static boolean isEmpty(org.orcid.jaxb.model.common_v2.Year year) {
        if (year==null) return true;
        return isEmpty(year.getValue());
    }
    
    public static boolean isEmpty(org.orcid.jaxb.model.common_v2.Day day) {
        if (day==null) return true;
        return isEmpty(day.getValue());
    }

    public static boolean isEmpty(org.orcid.jaxb.model.common_v2.Month month) {
        if (month==null) return true;
        return isEmpty(month.getValue());
    }
    
    public static boolean isEmpty(org.orcid.jaxb.model.v3.release.common.Year year) {
        if (year==null) return true;
        return isEmpty(year.getValue());
    }
    
    public static boolean isEmpty(org.orcid.jaxb.model.v3.release.common.Day day) {
        if (day==null) return true;
        return isEmpty(day.getValue());
    }

    public static boolean isEmpty(org.orcid.jaxb.model.v3.release.common.Month month) {
        if (month==null) return true;
        return isEmpty(month.getValue());
    }
    
    public static boolean isEmtpy(Contributor c) {
    	return PojoUtil.areAllEmtpy(c.getContributorSequence(), c.getEmail(), c.getOrcid(), c.getUri(), c.getCreditName(), c.getContributorRole());
    }

    public static boolean isEmtpy(FundingExternalIdentifierForm g) {
    	return PojoUtil.areAllEmtpy(g.getType(), g.getValue(), g.getUrl());
    }
    
    public static boolean isEmpty(org.orcid.jaxb.model.message.ContributorOrcid contributorOrcid) {
        if(contributorOrcid == null) return true;
        return isEmpty(contributorOrcid.getPath());
    }

    public static boolean isEmpty(org.orcid.jaxb.model.common_v2.ContributorOrcid contributorOrcid) {
        if(contributorOrcid == null) return true;
        return isEmpty(contributorOrcid.getPath());
    }
    
    public static boolean isEmpty(org.orcid.jaxb.model.v3.release.common.ContributorOrcid contributorOrcid) {
        if(contributorOrcid == null) return true;
        return isEmpty(contributorOrcid.getPath());
    }
    
    public static boolean isEmpty(ActivityExternalIdentifier workExternalId) {
        if(workExternalId == null) return true;
        return areAllEmtpy(workExternalId.getRelationship(), workExternalId.getUrl(), workExternalId.getExternalIdentifierId(), workExternalId.getExternalIdentifierType());
    }
    
    public static boolean isEmpty(TranslatedTitleForm translatedTitle) {
        if(translatedTitle == null) return true;
        return areAllEmpty(translatedTitle.getContent(), translatedTitle.getLanguageCode());
    }
    
    public static Date convertDate(org.orcid.jaxb.model.v3.release.common.FuzzyDate fuzzyDate) {
        if (fuzzyDate != null) {
            Integer year = PojoUtil.isEmpty(fuzzyDate.getYear()) ? null : Integer.valueOf(fuzzyDate.getYear().getValue());
            Integer month = PojoUtil.isEmpty(fuzzyDate.getMonth()) ? null : Integer.valueOf(fuzzyDate.getMonth().getValue());
            Integer day = PojoUtil.isEmpty(fuzzyDate.getDay()) ? null : Integer.valueOf(fuzzyDate.getDay().getValue());
            if (year != null && year == 0) {
                year = null;
            }
            if (month != null && month == 0) {
                month = null;
            }
            if (day != null && day == 0) {
                day = null;
            }
            return Date.valueOf(org.orcid.jaxb.model.v3.release.common.FuzzyDate.valueOf(year, month, day));
        }
        return null;
    }

    public static WorkForm getWorkForm(WorkSummary workSummary) {
        WorkForm workForm = new WorkForm();
        workForm.setPutCode(Text.valueOf(workSummary.getPutCode()));

        String title = workSummary.getTitle() != null && workSummary.getTitle().getTitle() != null ? workSummary.getTitle().getTitle().getContent() : "";
        workForm.setTitle(Text.valueOf(title));

        if (workSummary.getJournalTitle() != null) {
            workForm.setJournalTitle(Text.valueOf(workSummary.getJournalTitle().getContent()));
        }

        if (workSummary.getPublicationDate() != null) {
            workForm.setPublicationDate(getPublicationDate(workSummary.getPublicationDate()));
        }

        workForm.setSource(workSummary.getSource().retrieveSourcePath());
        if (workSummary.getSource().getSourceName() != null) {
            workForm.setSourceName(workSummary.getSource().getSourceName().getContent());
        }

        if (workSummary.getSource().getAssertionOriginClientId() != null) {
            workForm.setAssertionOriginClientId(workSummary.getSource().getAssertionOriginClientId().getPath());
        }

        if (workSummary.getSource().getAssertionOriginOrcid() != null) {
            workForm.setAssertionOriginOrcid(workSummary.getSource().getAssertionOriginOrcid().getPath());
        }

        if (workSummary.getSource().getAssertionOriginName() != null) {
            workForm.setAssertionOriginName(workSummary.getSource().getAssertionOriginName().getContent());
        }

        workForm.setWorkType(Text.valueOf(workSummary.getType().value()));
        workForm.setVisibility(org.orcid.pojo.ajaxForm.Visibility.valueOf(workSummary.getVisibility()));
        WorkForm.populateExternalIdentifiers(workSummary.getExternalIdentifiers(), workForm, workSummary.getType());
        workForm.setCreatedDate(Date.valueOf(workSummary.getCreatedDate()));
        workForm.setLastModified(Date.valueOf(workSummary.getLastModifiedDate()));
        return workForm;
    }

    public static WorkForm getWorkForm(WorkSummaryExtended workSummary) {
        WorkForm workForm = new WorkForm();
        workForm.setPutCode(Text.valueOf(workSummary.getPutCode()));

        String title = workSummary.getTitle() != null && workSummary.getTitle().getTitle() != null ? workSummary.getTitle().getTitle().getContent() : "";
        workForm.setTitle(Text.valueOf(title));

        if (workSummary.getJournalTitle() != null) {
            workForm.setJournalTitle(Text.valueOf(workSummary.getJournalTitle().getContent()));
        }

        if (workSummary.getPublicationDate() != null) {
            workForm.setPublicationDate(getPublicationDate(workSummary.getPublicationDate()));
        }

        if (workSummary.getSource() != null) {
            workForm.setSource(workSummary.getSource().retrieveSourcePath());
            if (workSummary.getSource().getSourceName() != null) {
                workForm.setSourceName(workSummary.getSource().getSourceName().getContent());
            }

            if (workSummary.getSource().getAssertionOriginClientId() != null) {
                workForm.setAssertionOriginClientId(workSummary.getSource().getAssertionOriginClientId().getPath());
            }

            if (workSummary.getSource().getAssertionOriginOrcid() != null) {
                workForm.setAssertionOriginOrcid(workSummary.getSource().getAssertionOriginOrcid().getPath());
            }

            if (workSummary.getSource().getAssertionOriginName() != null) {
                workForm.setAssertionOriginName(workSummary.getSource().getAssertionOriginName().getContent());
            }
        }

        workForm.setWorkType(Text.valueOf(workSummary.getType().value()));
        workForm.setVisibility(org.orcid.pojo.ajaxForm.Visibility.valueOf(workSummary.getVisibility()));
        WorkForm.populateExternalIdentifiers(workSummary.getExternalIdentifiers(), workForm, workSummary.getType());
        workForm.setCreatedDate(Date.valueOf(workSummary.getCreatedDate()));
        workForm.setLastModified(Date.valueOf(workSummary.getLastModifiedDate()));
        workForm.setContributorsGroupedByOrcid(workSummary.getContributorsGroupedByOrcid());
        workForm.setNumberOfContributors(workSummary.getNumberOfContributors());
        workForm.setFeaturedDisplayIndex(workSummary.getFeaturedDisplayIndex());
        return workForm;
    }

    private static Date getPublicationDate(PublicationDate publicationDate) {
        return PojoUtil.convertDate(publicationDate);
    }
}
