## v2.39.7 - 2023-09-07

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.39.6...v2.39.7)

- [#6890](https://github.com/ORCID/ORCID-Source/pull/6890): 8831 tech send spam notification to the spam channel collab spam reports

## v2.39.6 - 2023-09-05

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.39.4...v2.39.6)

- [#6889](https://github.com/ORCID/ORCID-Source/pull/6889): Update support url
- [#6888](https://github.com/ORCID/ORCID-Source/pull/6888): Stage name is NONE_PLANNED
- [#6886](https://github.com/ORCID/ORCID-Source/pull/6886): Added the code to send the slack alert to dedicated channel, increased the number of threads for the sched task pool
- [#6887](https://github.com/ORCID/ORCID-Source/pull/6887): If a record was member created and the member added activities to it …

## v2.39.4 - 2023-09-01

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.39.3...v2.39.4)

- [#6885](https://github.com/ORCID/ORCID-Source/pull/6885): Remove the integration if it is not in one of the desiered stages

## v2.39.3 - 2023-08-31

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.39.2...v2.39.3)

- [#6884](https://github.com/ORCID/ORCID-Source/pull/6884): Leave only the following status: In Development, Complete, Certified …

## v2.39.2 - 2023-08-31

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.39.1...v2.39.2)

- [#6883](https://github.com/ORCID/ORCID-Source/pull/6883): fix: Use distinction start date as an end data for sorting if exists

### Fix

- Use distinction start date as an end data for sorting if exists

## v2.39.1 - 2023-08-30

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.39.0...v2.39.1)

- [#6875](https://github.com/ORCID/ORCID-Source/pull/6875): Added user_obo_enabled column to dw_client_details view

## v2.39.0 - 2023-08-30

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.38.10...v2.39.0)

- [#6882](https://github.com/ORCID/ORCID-Source/pull/6882): Add url to summary
- [#6880](https://github.com/ORCID/ORCID-Source/pull/6880): Add url info to affiliations form

## v2.38.10 - 2023-08-23

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.38.9...v2.38.10)

- [#6878](https://github.com/ORCID/ORCID-Source/pull/6878): Deactivate endpoint should return the email in a json object

## v2.38.9 - 2023-08-23

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.38.8...v2.38.9)

- [#6877](https://github.com/ORCID/ORCID-Source/pull/6877): Remove tokens from cache

## v2.38.8 - 2023-08-22

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.38.7...v2.38.8)

- [#6876](https://github.com/ORCID/ORCID-Source/pull/6876): Remove token from cache when it is revoked

## v2.38.7 - 2023-08-18

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.38.6...v2.38.7)

## v2.38.6 - 2023-08-18

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.38.5...v2.38.6)

- [#6872](https://github.com/ORCID/ORCID-Source/pull/6872): fix: Sort professional activities by end date in summary endpoint
- [#6874](https://github.com/ORCID/ORCID-Source/pull/6874): Trim org data

### Fix

- Sort professional activities by end date in summary endpoint

## v2.38.5 - 2023-08-18

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.38.4...v2.38.5)

- [#6873](https://github.com/ORCID/ORCID-Source/pull/6873): send-deactivate-account.json should be a POST action, since we are as…

## v2.38.4 - 2023-08-17

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.38.3...v2.38.4)

- [#6871](https://github.com/ORCID/ORCID-Source/pull/6871): Fixed the s3 auth , code formatting

## v2.38.3 - 2023-08-16

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.38.2...v2.38.3)

- [#6870](https://github.com/ORCID/ORCID-Source/pull/6870): fix: Sort affiliations by created date

### Fix

- Fix test errors caused by adding more affiliations in test data
- Sort affiliations by created date

## v2.38.2 - 2023-08-16

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.38.1...v2.38.2)

- [#6837](https://github.com/ORCID/ORCID-Source/pull/6837): Add Redis cache to store the tokens

## v2.38.1 - 2023-08-16

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.38.0...v2.38.1)

- [#6847](https://github.com/ORCID/ORCID-Source/pull/6847): notify.orcid.org and verify.orcid.org should be not reply addresses

## v2.38.0 - 2023-08-15

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.37.14...v2.38.0)

## v2.37.14 - 2023-08-15

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.37.13...v2.37.14)

- [#6859](https://github.com/ORCID/ORCID-Source/pull/6859): Changed the translation for email.common.warm_regards key

## v2.37.13 - 2023-08-15

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.37.12...v2.37.13)

- [#6846](https://github.com/ORCID/ORCID-Source/pull/6846): Ror to fundref ringgold mapping

## v2.37.12 - 2023-08-15

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.37.11...v2.37.12)

- [#6757](https://github.com/ORCID/ORCID-Source/pull/6757): Bump jettison from 1.5.1 to 1.5.4 in /orcid-core
- [#6863](https://github.com/ORCID/ORCID-Source/pull/6863): fix: Add commonName and reference in ExternalIdentifiersSummary
- [#6867](https://github.com/ORCID/ORCID-Source/pull/6867): Bump socket.io-parser from 4.2.2 to 4.2.4 in /orcid-web/src/main/webapp/static/javascript/ng1Orcid
- [#6853](https://github.com/ORCID/ORCID-Source/pull/6853): Fixed the bug when the  grouping is undone for an existing Fundref that  has been updated in Fundref registry

### Fix

- Add commonName and reference in ExternalIdentifiersSummary

## v2.37.11 - 2023-08-15

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.37.10...v2.37.11)

- [#6869](https://github.com/ORCID/ORCID-Source/pull/6869): fix: Remove duplicated email in claim reminder email

### Fix

- Remove duplicated email in claim reminder email

## v2.37.10 - 2023-08-11

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.37.9...v2.37.10)

- [#6866](https://github.com/ORCID/ORCID-Source/pull/6866): Refactoring of the autospamcli so it can be run as scheduled or manual cli

## v2.37.9 - 2023-08-11

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.37.8...v2.37.9)

- [#6865](https://github.com/ORCID/ORCID-Source/pull/6865): Added pre tag to fix the new lines in the email footer

## v2.37.8 - 2023-08-10

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.37.7...v2.37.8)

- [#6864](https://github.com/ORCID/ORCID-Source/pull/6864): Transifex

## v2.37.7 - 2023-08-09

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.37.6...v2.37.7)

- [#6861](https://github.com/ORCID/ORCID-Source/pull/6861): Fix jackson problem

## v2.37.6 - 2023-08-09

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.37.5...v2.37.6)

- [#6860](https://github.com/ORCID/ORCID-Source/pull/6860): fix: Remove indentation added in ftl email locked

### Fix

- Remove indentation added in ftl email locked

## v2.37.5 - 2023-08-09

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.37.4...v2.37.5)

- [#6857](https://github.com/ORCID/ORCID-Source/pull/6857): fix: Count only default affiliation group

### Fix

- Count only default affiliation group

## v2.37.4 - 2023-08-09

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.37.3...v2.37.4)

- [#6858](https://github.com/ORCID/ORCID-Source/pull/6858): Remove the extra space between the Orcid team and the link

## v2.37.3 - 2023-08-09

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.37.2...v2.37.3)

- [#6856](https://github.com/ORCID/ORCID-Source/pull/6856): fix: Remove field from response if its private

### Fix

- Remove field from response if its private

## v2.37.2 - 2023-08-08

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.37.1...v2.37.2)

- [#6855](https://github.com/ORCID/ORCID-Source/pull/6855): fix: Fix summary peer reviews count

### Fix

- Fix summary peer reviews count

## v2.37.1 - 2023-08-08

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.37.0...v2.37.1)

- [#6854](https://github.com/ORCID/ORCID-Source/pull/6854): Added a line break in the emails footer

## v2.37.0 - 2023-08-08

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.36.16...v2.37.0)

- [#6852](https://github.com/ORCID/ORCID-Source/pull/6852): feature: Add production script one trust

### Feature

- Add production script one trust

## v2.36.16 - 2023-08-07

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.36.15...v2.36.16)

- [#6851](https://github.com/ORCID/ORCID-Source/pull/6851): fix: Rename variables in summary endpoint

### Fix

- Rename variable in summary endpoint

## v2.36.15 - 2023-08-07

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.36.14...v2.36.15)

- [#6850](https://github.com/ORCID/ORCID-Source/pull/6850): fix: Remove duplicated footer in locked email

### Fix

- Remove duplicated email

## v2.36.14 - 2023-08-04

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.36.13...v2.36.14)

- [#6849](https://github.com/ORCID/ORCID-Source/pull/6849): Fix more tests
- [#6848](https://github.com/ORCID/ORCID-Source/pull/6848): Check the source of the element based on the source ids and not the name

## v2.36.13 - 2023-08-03

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.36.12...v2.36.13)

- [#6845](https://github.com/ORCID/ORCID-Source/pull/6845): fix: Remove extra content set to one trust link

### Fix

- Remove extra content set to one trust link

## v2.36.12 - 2023-08-01

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.36.11...v2.36.12)

- [#6844](https://github.com/ORCID/ORCID-Source/pull/6844): fix: Add status and fix organization name

### Fix

- Add status and fix organization name

## v2.36.11 - 2023-07-31

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.36.10...v2.36.11)

- [#6838](https://github.com/ORCID/ORCID-Source/pull/6838): 8734 update the registry emails to remove links to the support team emails and forms

### Fix

- Remove empty white space
- Fix typos and add missing paragraphs
- Rollback loced orcid email

## v2.36.10 - 2023-07-31

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.36.9...v2.36.10)

- [#6842](https://github.com/ORCID/ORCID-Source/pull/6842): For ISSN loading, use the table id to iterate over the existing list …

## v2.36.9 - 2023-07-31

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.36.8...v2.36.9)

## v2.36.8 - 2023-07-31

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.36.7...v2.36.8)

- [#6841](https://github.com/ORCID/ORCID-Source/pull/6841): EThOS description had a typo and said 'Peristent' instead of 'Persist…
- [#6840](https://github.com/ORCID/ORCID-Source/pull/6840): fix: Remove duplicated attribute and fix organization name

### Fix

- Remove duplicated attribute and fix organization name

## v2.36.7 - 2023-07-31

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.36.6...v2.36.7)

- [#6839](https://github.com/ORCID/ORCID-Source/pull/6839): style: fix a tags color

## v2.36.6 - 2023-07-26

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.36.5...v2.36.6)

- [#6835](https://github.com/ORCID/ORCID-Source/pull/6835): Update emails notifications

### Fix

- Refactor deactivate email

### Feature

- Update emails notifications

## v2.36.5 - 2023-07-26

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.36.4...v2.36.5)

- [#6836](https://github.com/ORCID/ORCID-Source/pull/6836): fix: One trust style on print preview

### Fix

- One trust style on print preview

## v2.36.4 - 2023-07-20

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.36.3...v2.36.4)

- [#6834](https://github.com/ORCID/ORCID-Source/pull/6834): ETHOS to be case sensitive

## v2.36.3 - 2023-07-20

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.36.2...v2.36.3)

- [#6833](https://github.com/ORCID/ORCID-Source/pull/6833): fix: NPE and compare if its self asserted or validated work or funding

### Fix

- Replace incorrect variables
- NPE and compare if its self asserted or validated work or funding

## v2.36.2 - 2023-07-19

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.36.1...v2.36.2)

- [#6831](https://github.com/ORCID/ORCID-Source/pull/6831): 8682 trust summary json format

### Fix

- Only get summary if record is not deprecated
- Update dates format

### Feature

- Add new endpoint `summary.json`

## v2.36.1 - 2023-07-18

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.36.0...v2.36.1)

- [#6832](https://github.com/ORCID/ORCID-Source/pull/6832): fix: Rollback ot

### Fix

- Rollback ot

## v2.35.5 - 2023-07-13

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.35.4...v2.35.5)

- [#6830](https://github.com/ORCID/ORCID-Source/pull/6830): fix: Temporarily remove onetrust

### Fix

- Temporarily remove onetrust

## v2.35.4 - 2023-07-06

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.35.3...v2.35.4)

- [#6829](https://github.com/ORCID/ORCID-Source/pull/6829): 8659 access the onetrust widget through a link in the site footer

### Fix

- Remove duplicated links
- Add link back and fix styles

## v2.35.3 - 2023-06-30

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.35.2...v2.35.3)

- [#6828](https://github.com/ORCID/ORCID-Source/pull/6828): Added the config to group/re-index all ROR, removed ringgold references

## v2.35.2 - 2023-06-30

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.35.1...v2.35.2)

- [#6827](https://github.com/ORCID/ORCID-Source/pull/6827): Use mainTitle and fallback to name, do not update if nothing changes

## v2.35.1 - 2023-06-28

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.34.10...v2.35.1)

- [#6826](https://github.com/ORCID/ORCID-Source/pull/6826): feature: Enable language detection in onetrust

### Feature

- Enable language detection in onetrust

## v2.34.10 - 2023-06-28

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.34.9...v2.34.10)

- [#6825](https://github.com/ORCID/ORCID-Source/pull/6825): fix: Temporarily remove onetrust
- [#6824](https://github.com/ORCID/ORCID-Source/pull/6824): fix: Temporarily remove onetrust link from footer
- [#6821](https://github.com/ORCID/ORCID-Source/pull/6821): leo/8661-account-developer-tools

### Fix

- Temporarily remove onetrust
- Temporarily remove onetrust link from footer

## 2.35.0 - 2023-06-26

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.34.7...2.35.0)

- [#6824](https://github.com/ORCID/ORCID-Source/pull/6824): fix: Temporarily remove onetrust link from footer

### Fix

- Temporarily remove onetrust link from footer

## v2.34.7 - 2023-06-26

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.34.6...v2.34.7)

- [#6821](https://github.com/ORCID/ORCID-Source/pull/6821): leo/8661-account-developer-tools

## v2.34.6 - 2023-06-21

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.34.5...v2.34.6)

- [#6823](https://github.com/ORCID/ORCID-Source/pull/6823): Make the orgs indexing batch configurable

## v2.34.5 - 2023-06-19

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.34.4...v2.34.5)

- [#6820](https://github.com/ORCID/ORCID-Source/pull/6820): Added the check for affiliations before locking records
- [#6819](https://github.com/ORCID/ORCID-Source/pull/6819): feature: Remove old banner and add one trus to index and link to footer

### Feature

- Remove old banner and add one trus to index and link to footer

## v2.34.4 - 2023-06-19

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.34.3...v2.34.4)

- [#6816](https://github.com/ORCID/ORCID-Source/pull/6816): Use REMOVED org status to remove RINGGOLD orgs from the index

## v2.34.3 - 2023-06-19

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.34.2...v2.34.3)

- [#6822](https://github.com/ORCID/ORCID-Source/pull/6822): Add a property to indicate if a record should have the noindex metatag

## v2.34.2 - 2023-06-14

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.34.1...v2.34.2)

- [#6817](https://github.com/ORCID/ORCID-Source/pull/6817): fix: Rollback clientId in ApplicationSummary

### Fix

- Rollback clientId in ApplicationSummary

## v2.34.1 - 2023-06-13

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.34.0...v2.34.1)

- [#6815](https://github.com/ORCID/ORCID-Source/pull/6815): Create a csv file with the Ringgold to RORs ids

## v2.34.0 - 2023-06-13

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.33.1...v2.34.0)

- [#6814](https://github.com/ORCID/ORCID-Source/pull/6814): feature: Update `Contact us` to `Contact Support` in footer

### Feature

- Update `Contact us` to `Contact Support` in footer

## v2.33.1 - 2023-06-13

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.33.0...v2.33.1)

- [#6813](https://github.com/ORCID/ORCID-Source/pull/6813): Do not include ringgold in ORG grouping

## v2.33.0 - 2023-06-12

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.32.2...v2.33.0)

- [#6812](https://github.com/ORCID/ORCID-Source/pull/6812): feature: Refactor `get-trusted-orgs.json` endpoint and remove unneces…

### Feature

- Replace label for scope type
- Refactor `get-trusted-orgs.json` endpoint and remove unnecessary data

## v2.32.2 - 2023-06-09

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.32.1...v2.32.2)

- [#6810](https://github.com/ORCID/ORCID-Source/pull/6810): Remove RINGGOLD from the orgs query

## v2.32.1 - 2023-06-08

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.32.0...v2.32.1)

- [#6811](https://github.com/ORCID/ORCID-Source/pull/6811): check if the profile has an auth token

## v2.32.0 - 2023-06-08

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.31.11...v2.32.0)

- [#6809](https://github.com/ORCID/ORCID-Source/pull/6809): feature: Add professional activities togglz

### Feature

- Add professional activities togglz

## v2.31.11 - 2023-06-06

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.31.10...v2.31.11)

- [#6794](https://github.com/ORCID/ORCID-Source/pull/6794): Changes to cli to support sending notification emails and only lock unverified, unlocked records

## v2.31.10 - 2023-06-01

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.31.9...v2.31.10)

- [#6807](https://github.com/ORCID/ORCID-Source/pull/6807): Redirect to the root endpoint with path /oauth/token

## v2.31.9 - 2023-05-31

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.31.8...v2.31.9)

## v2.31.8 - 2023-05-31

[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.31.7...v2.31.8)

- [#6805](https://github.com/ORCID/ORCID-Source/pull/6805): Jersey does not remove .json from the path anymore
- [#6806](https://github.com/ORCID/ORCID-Source/pull/6806): Search link

## v2.31.7 - 2023-05-29
[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.31.6...v2.31.7)

- [#6803](https://github.com/ORCID/ORCID-Source/pull/6803): Transifex

## v2.31.6 - 2023-05-25
[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.31.5...v2.31.6)

- [#6801](https://github.com/ORCID/ORCID-Source/pull/6801): fix/update-node-version

## v2.31.5 - 2023-05-25
[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.31.4...v2.31.5)

- [#6798](https://github.com/ORCID/ORCID-Source/pull/6798): 8625 awstechtogglz redirect token endpoint in puborcidorg to the root endpoint

## v2.31.4 - 2023-05-25
[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.31.3...v2.31.4)

- [#6800](https://github.com/ORCID/ORCID-Source/pull/6800): Remove all togglz that have to do with the angular pages

### Fix

-  all multiple prs to be merged at the same time

## v2.31.3 - 2023-05-23
[Full Changelog](https://github.com/ORCID/ORCID-Source/compare/v2.31.2...v2.31.3)

- [#6796](https://github.com/ORCID/ORCID-Source/pull/6796): Remove ORCID_ANGULAR_PUBLIC_PAGE

### Fix

-  inject version into cache key to allow for only tagging changes

