import { MonthDayYearDate, Value } from './common.endpoint'

export interface Works {
  nextOffset: number
  totalGroups: number
  groups: WorkGroup[]
}

export interface WorkGroup {
  activePutCode: number
  defaultPutCode: number
  groupId: number
  activeVisibility: string
  userVersionPresent: boolean
  externalIdentifiers: ExternalIdentifier[]
  works: Work[]
}

export interface ExternalIdentifier {
  errors: any[]
  externalIdentifierId: ExternalIdentifierId
  externalIdentifierType: Value
  url: Value
  relationship: Value
  normalized: Value
  normalizedUrl: Value
}

export interface ExternalIdentifierId {
  errors: any[]
  value: string
  required: boolean
  getRequiredMessage?: any
}

export interface Citation {
  citation: Value
  citationType: Value
  errors: any[]
  getRequiredMessage?: any
  required: boolean
}

export interface WorkExternalIdentifier {
  errors: any[]
  externalIdentifierId: Value
  externalIdentifierType: Value
  url: Value
  relationship: Value
  normalized: Value
  normalizedUrl: Value
}

export interface Work {
  visibility: Value
  errors: any[] // TODO is this always empty?
  publicationDate: MonthDayYearDate
  putCode: Value
  shortDescription?: Value
  url?: Value
  journalTitle?: Value
  languageCode?: Value
  languageName?: Value
  citation?: Citation
  countryCode?: Value
  countryName?: Value
  contributors?: [any] // TODO is this always empty?
  workExternalIdentifiers: WorkExternalIdentifier[]
  source: string
  sourceName: string
  title: Value
  subtitle?: Value
  translatedTitle?: any // TODO is this always empty?
  workCategory?: Value
  workType: Value
  dateSortString?: string
  createdDate?: MonthDayYearDate
  lastModified?: MonthDayYearDate
  userSource: boolean
}
