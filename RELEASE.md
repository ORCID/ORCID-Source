# Release notes and highlights
We release updates to the Registry regularly. Below is a list of the improvements and enhancements that have been made so far. You can see more details about our releases on our Release boards:

* [2013 Releases](https://trello.com/b/ZgYVAlel)
* [2012 Releases](https://trello.com/b/vyCDYdvR)


## release-1.1.50.5
(2014-05-15)

* Move APIs to 1.1

* Push out ability to have multiple client secrets allowing us to rotate secrets when needed

* Update how we handle PMC identifiers



## release-1.1.49.11
(2014-04-10)

* Tons of improvements to delegation

* Tons of improvements to public client

* Move client details out of profile to it's own table

* Other small improvements



## release-1.1.48.23
(2014-04-01)

* Improve search with phrases (help find names with Chinese characters)

* Fix Current/past email selector

* Add delegates by email (hidden)

* Refactor import wizard clear cache to work across load balanced machines

* Delegates trusted list improvements (hidden)

* Public Client Authentication Hidden

* UI Improvements



## release-1.1.47.14
(2014-03-18)

* Clean up Authorization code (remove used metadata)

* Update close modal behavior 

* Add privacy policy link to emails

* Admin group fixes

* RC4 - Add new information to ORCID History: verified email and verified primary email

* RC4 - Add new information to ORCID Internal: referred-by



## release-1.1.46.2
(2014-03-7)

* Fix API call (with PUT) deletes private data

* Add group name to GA tracking for revoke

* DATE: Date listing sorting: use end date if no start date



## release-1.1.45.17
(2014-02-28)

* Release funding

* Added fundref data

* Added group name to google analytics tracking.

* Admin page beautification.

* Removed Patents code

* Delegates (hidden)  

* Prevent webhooks notification until solr indexing

* Fix quicksearch results when we have zero results

* Update bootstrap

* Add Add group id to ORCID Internal



## release-1.1.44.6
(2014-02-7)

* Another go at the fonts.

* Require one primary email address in 1.2 APIs.

* Fix password popup layout.

* Add client ID to event labels for OAuth sign-in/registration.

* Fix issue where common first name no Last name was breaking duplicate check.



## release-1.1.43.18 
(2014-01-29)

* Fix issue with grants scope breaking scope authorization.

* Add disk caching 

* Fix message that says 1.1, but has an <orcid> element in it when it should only have an <orcid-id>

* Prevent invalid scopes from being requested and granted.

* Caching of activities in public profiles

* Caching of internalized languages and country names

* Change date format

* Uservoice widget updates

* Next round of funding improvements

* Other small fixes

* Language code enumerated in the XSD


 
## release-1.1.42.1
(2014-01-07)

* Place work duplicate check after adding source information

* Make external id source behavior consistent with other source ids 



## release-1.1.41.12
(2014-01-06)

* Change language list in add work modal to include all ISO_639-1_codes
  two letter codes

* Various bug fixes



## release-1.1.40.6
(2013-12-19)

* rc2 added granularity to creation method

* Lot of UI changes including using full popup width.

* Added Korean / hidden beta https://qa.orcid.org/register?lang=ko

* Remove institutions from advance search

* Make privacy help and works more info ipad friendly(ish)

* Prevent chars not allowed in xml1.0 from being outputted

* Fix issue with education and employment deduping each other 



## 1.1.39.11
(2013-10-10)

* 1.2_rc1 - Affiliation's API

* Don't load works more info until mouse over

* General UI fixes



## release-1.1.38.5
(2013-10-5)

* New ORCID iD structure changed in 1.1!

* Removing the undefined work type form the UI and from the latest orcid-message (1.1)

* Release responsive design

* Add RIS to citiations types in new XSDs. 1.0.23 and 1.1

* Create orcid-message-1.0.23.xsd which only allows education and employment affiliations. Modify orcid-message-1.1.xsd to only allow education and employment.

* Change multi-scope token have expiration of matching longest scope, scopes with shorter scopes are removed past their expirations.

* Updated to bootstrap 3.0.2 

* Migrating works to works metadata.

* Add plus sign next to ID to allow user to add more than one ID



## release-1.1.36.12
(2013-10-24)

* Admin page for deactivation.

* XSD changes to insure given-names is not empty.

* Make works info popover unhidden. Also added to public view.

* Remove concept of single use token.

* Index other work identifiers in Solr.

* Migrate pubmed links.

* Forced hibernate to refresh when a profile transitions to "claimed".



## release-1.1.35.42 
(2013-10-18)

* Stop importing duplicated works

* Added abbreviations to external identifiers types

* Reorders the new works ui screen

* Refactor webhooks to also have a profile_last_modified column

* Journal title to work

* Add Language to work

* Add Translated title to work

* Affiliations API - Beta

* Update to show all work info screen - feature hidden

* New page to help load balancers detect tomcat outage



## release-1.1.39.9
(2013-09-27)

* Fix for issue with solr record not being found in Postgres.

* Confirm message for revoking client authorization.

* Make our website more XSS resistant.

* Make our email hibernate mappings reuse cached emails objects.

* Update to newest stable version of AngularJs.

* Update to remove user voice from OAuth page.

* Sync Java and XSD work types

* Display line breaks in bio & work descriptions



## release-1.1.33.3
(2013-09-19)

* Add a page where group owners can create their own client credentials.

## release-1.1.32.9
(2013-09-16)

* Refactor "Is this you" to open the public profile in a new window.

* Refactor work contributor Database persistence.

* Release profile depreciation.

* Autolink external work ids.

* Release email verification reminder, 7 days after adding email.



## release-1.1.31.8
(2013-08-27)

* Speed up logins by not loading works

* Allow CORS json request to public API

* Display other external ids in user interface

* Increase bio field



## release-1.1.30.11
(2013-08-19)

* Release Simplified Chinese, Traditional Chinese, Spanish and French



## release-1.1.29.2
(2013-08-13)

* Upgrade spring security

* Remove modernizr.js (except the old style personal info page)

* Add ORCID researcher id count caching.

* Require password to change security question.



## release-1.1.28.6
(2013-08-09)

* Show better error message for incomplete verify links

* Remove external ids and biography when profile is deactivated

* Fix missing trash can on website urls

* Save an individual work, instead of all works when adding a work

* Do not display bowser version error messages in modals

* Persist locale when user registers or claims a profile 



## release-1.1.27.4
(2013-07-31)

* Make new add works modal live

* Fix revoke token list to display newly added clients



## release-1.1.26.23
(2013-07-26)

* Versioned API

* Locale is persisted

* Improved work-external-identifier-id validation

* Rework statistics page

* Put email text in properties files

* Public launch of statistics page



## release-1.1.24.10
(2013-06-27)

* Fix email's with spaces being allowed into the db

* Quick Search is AJAX and paginates

* Push out property files for Chinese Simplified, Chinese Traditional, Spanish, French and English.

* Fix registration redirecting non-existent pages issue.

* Push out statistics page. /statistics 



## release-1.1.23.5
(2013-06-12)

* Improve Solr quick search results.

* Send out one-off verify email via MailGun. Also first html email.

* Remove legancy email code.

* Upgrade java-bibtex.

* Release experimental RDF.



## release-1.1.22.24
(2013-06-05)

* Added in defensive threading to kill jbibtex thread locks

* Caching public records with Solr

* Works delete and privacy settings are ajaxed on the /my-orcid 

* "View Public Record" button displays white text when hover

* When adding works manually, the last digits of the year are now visible

* Import wizzard popup now have a header, a footer and a close button.


## release-1.1.21.3
(2013-05-17)

* Caching ORCID Profiles!

* Migrated unknown bootstrap to 2.3.1

* Fix read public profile issue

* User is asked to verify email on login if primary isn't verified

* Add source to works

* Added jsonp user logout feature


## release-1.1.19.5
(2013-05-02)

* Upgrade 1.0.5 java-bibtex

* Use solar instead of postgres for public search results

* Enable ajax registration


## releaes-1.1.18.2 
(2013-04-30)

* DB optimisations for externtal identifiers

* Ajax registration form - hidden feature /register?ajax

* Store fields in solar

* IE 7 External ID Display Issues


## release-1.1.17.2

* Implement timestamp session caching for Profile

* Implement caching for static data in db (like select box values)


## release-1.1.16.2
(2013-04-24)

* Email specific DAOs

* Expand external identifier length

* Fixed fonts on http:// pages

* Fixed token validation logic


## release-1.1.15.17
(2013-04-23)

* Refactor works to reuse db table rows

* Upgraded EhCache

* Terms of use in blank window

* Prototype statistics page 

* Member Webhooks 

* Enabled CDN 

* New script to create releases

## release-1.1.14.17
(2013-04-16)

* Added Delete External ID button.

* Ajaxed Email Preferences

* Ajaxed Deactivate Account

* Ajaxed Change Password

* Ajaxed Privacy Preferences

* Changed delete links to trash cans

* Updated font awesome

* XML's orcid-id now reflect environment

* Order Work Chronologically

* Releasing new privacy toggle on account setting page

* Completed account settings translation

* Add missing links on java web app footer and header 


## release-1.1.14.5
 
* Order works chronologically

* Add Disengagement GA Tracking

* New Privacy Selector used in Multi Email


## release-1.1.13.4 
(2013-04-02)

* Import Wizard opens in new tab one window sooner

* Fixed JavaScript Internet Explorer race condition with our new gaEvent Tracking  


## release-1.1.12.12
(2013-03-28)

* April Fools internationalized ORC/Troll


## release-1.1.11.3
(2013-03-25)

* Multiple emails -MPV (minium viable product)


## release-1.1.10.12
(2013-03-21)

* GA event tracking, with custom page change extensions

* Import wizard now has descriptions

* Tons of orc translations

* RDF content request now return 406

* You can now add contributor works via API

* Import wizard now opens 3rd parties into new window after Authorize


## release-1.1.9.7
(2013-03-12)

* Migrate java web-app about links to point to orcid.org 
* Search by ORCID iD (love it Will!)
* Claim email is now only good for one use
* Fix IE caching issue for login check (I Hope!)
* Multiple email backend release
* Last name is now not required (Way to go Angel)
* Fix login error formatting. 
* Email is now case insensitive on login screen.
* Email is validated on OAuth Create
* Formatting improvement to Personal Info Window (Brian's making us look nice)
 

## Earlier Release Notes

Looking for release note from January 2013 and earlier? See our [knowledge base article](http://support.orcid.org/knowledgebase/articles/137550)
