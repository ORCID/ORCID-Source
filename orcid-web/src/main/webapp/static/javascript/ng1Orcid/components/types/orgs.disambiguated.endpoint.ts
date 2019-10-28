export interface OrgDisambiguated {
  city: string
  country: string
  countryForDisplay: string
  disambiguatedAffiliationIdentifier: string
  orgDisambiguatedExternalIdentifiers: OrgDisambiguatedExternalIdentifier[]
  orgType: string
  region: string
  sourceId: string
  sourceType: string
  url: string
  value: string
}

export interface OrgDisambiguatedExternalIdentifier {
  preferred?: any
  identifierType: string
  all: string[]
}
