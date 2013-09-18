# Release notes and highlights
We release updates to the Registry regularly. Below is a list of the improvements and enhancements that have been made so far. You can see more details about our releases on our Release boards:

* [2013 Releases](https://trello.com/b/ZgYVAlel)
* [2012 Releases](https://trello.com/b/vyCDYdvR)


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
