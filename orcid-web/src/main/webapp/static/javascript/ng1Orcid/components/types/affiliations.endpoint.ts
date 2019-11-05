import { MonthDayYearDate, Value, Visibility } from './common.endpoint'

export enum AffiliationGroupsTypes {
  EMPLOYMENT = 'EMPLOYMENT',
  EDUCATION = 'EDUCATION',
  QUALIFICATION = 'QUALIFICATION',
  INVITED_POSITION = 'INVITED_POSITION',
  DISTINCTION = 'DISTINCTION',
  MEMBERSHIP = 'MEMBERSHIP',
  SERVICE = 'SERVICE',
}

export interface Affiliations {
  affiliationGroups: {
    INVITED_POSITION?: AffiliationGroup[]
    EMPLOYMENT?: AffiliationGroup[]
    EDUCATION?: AffiliationGroup[]
    SERVICE?: AffiliationGroup[]
    DISTINCTION?: AffiliationGroup[]
    MEMBERSHIP?: AffiliationGroup[]
    QUALIFICATION?: AffiliationGroup[]
  }
}

export enum AffiliationUIGroupsTypes {
  EMPLOYMENT = 'EMPLOYMENT',
  EDUCATION_AND_QUALIFICATION = 'EDUCATION_AND_QUALIFICATION',
  INVITED_POSITION_AND_DISTINCTION = 'INVITED_POSITION_AND_DISTINCTION',
  MEMBERSHIP_AND_SERVICE = 'MEMBERSHIP_AND_SERVICE',
}

export interface AffiliationUIGroup {
  type: string
  affiliationGroup: AffiliationGroup[]
}

export interface AffiliationGroup {
  affiliations: [Affiliation]
  activePutCode: number
  defaultAffiliation: Affiliation
  groupId: string
  activeVisibility: string // TODO is this always empty?
  userVersionPresent: boolean
  externalIdentifiers: [any] // TODO is this always empty?
  affiliationType: string // Todo make an enum
}

export interface Affiliation {
  visibility: Visibility
  errors: any[]
  putCode: Value
  affiliationName: Value
  city: Value
  region: Value
  country: Value
  roleTitle: Value
  countryForDisplay?: any // TODO is this always empty?
  departmentName: Value
  affiliationType: Value
  disambiguatedAffiliationSourceId: Value
  disambiguationSource: Value
  orgDisambiguatedCity?: any // TODO is this always empty?
  orgDisambiguatedCountry?: any // TODO is this always empty?
  orgDisambiguatedId: Value
  orgDisambiguatedName?: any // TODO is this always empty?
  orgDisambiguatedRegion?: any // TODO is this always empty?
  orgDisambiguatedUrl?: any // TODO is this always empty?
  affiliationTypeForDisplay?: any // TODO is this always empty?
  startDate: MonthDayYearDate
  endDate: MonthDayYearDate
  sourceName: string
  source: string
  dateSortString: string
  createdDate: MonthDayYearDate
  lastModified: MonthDayYearDate
  url: Value
  orgDisambiguatedExternalIdentifiers?: any // TODO is this always empty?
  affiliationExternalIdentifiers?: any // TODO is this always empty?
}
