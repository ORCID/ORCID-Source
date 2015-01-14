## ORCID Message (XSD) Readme 

**Contents**

* [XSD Information](#orcid-xsd-information)
    * [Active Versions & Base URLs](#active-xsd-versions-and-base-urls)
        * [Release Version](#xsd-release-versions)
        * [Release Candidate(s)](#xsd-release-candidates)
        * [Deprecated Versions](#deprecated-versions)
        * [Sunset Versions](#sunset-versions-unavailable-unsupported-should-not-be-used)
    * [Key Notices](#key-notices)
    * [Staying Informed](#staying-informed)
* [About XSD Updates and Versioning](#about-xsd-updates-and-versioning)
    * [Using XSD Versions](#using-xsd-versions)
* [ORCID-Message XSD Versions Table](#orcid-message-xsd-versions)
* [Getting Help](#getting-help)

---

# ORCID XSD information

##Active XSD versions and base URLs

_NOTE:<br />API calls for member to ORCID iD holder authorization using OAuth are not affected by XSD versions. Regardless of the XSD version used, the OAuth endpoints are:_

* _[http://orcid.org/oauth/authorize](http://support.orcid.org/knowledgebase/articles/120107)_
* _[https://orcid.org/oauth/token](http://support.orcid.org/knowledgebase/articles/119985)_

#### XSD release version

* **[ORCID Message 1.2](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2.xsd)** <br>_Read the [change log from the previous version (ORCID Message 1.1)](http://support.orcid.org/knowledgebase/articles/481386-xsd-1-2-update)_
    * API Base URLs - PRODUCTION
        * Public API:   http://pub.orcid.org/v1.2/
        * Member API:   https://api.orcid.org/v1.2/
    * API Base URLs - SANDBOX
        * Public API:   http://pub.sandbox.orcid.org/v1.2/
        * Member API:   http://api.sandbox.orcid.org/v1.2/

#### XSD release candidate(s)

_None available_

#### XSD deprecated versions


* **[ORCID Message 1.1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.1.xsd)** <br>_Read the [change log from the previous version (ORCID Message 1.0.23)](http://support.orcid.org/knowledgebase/articles/268952-xsd-1-1-update)_
    * API Base URLs - PRODUCTION
        * Public API:   http://pub.orcid.org/v1.1/
        * Member API:   https://api.orcid.org/v1.1/
    * API Base URLs - SANDBOX
        * Public API:   http://pub.sandbox.orcid.org/v1.1/
        * Member API:   http://api.sandbox.orcid.org/v1.1/


* **ORCID Message 1.2 Release Candidates**<br/>**[ORCID Message 1.2_rc7](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc7.xsd)** - _Contains: Adds Web of Science IDs_ <br/> **[ORCID Message 1.2_rc6](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc6.xsd)** - _Contains: Migrates external-id-orcid to external-id-source_ <br/> **[ORCID Message 1.2_rc5](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc5.xsd)** - _Contains: Migrates external-id-orcid to external-id-source_ <br/> **[ORCID Message 1.2_rc4](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc4.xsd)** - _Contains: Verified Email, Verified Primary Email and Referred By in ORCID History_ <br/> **[ORCID Message 1.2_rc3](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc3.xsd)** - _Contains: XSD for Funding_ <br/> **[ORCID Message 1.2_rc2](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc2.xsd)** - _Contains: updates to creation-method field_ <br/> **[ORCID Message 1.2_rc1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc1.xsd)** - _Contains: XSD for Organizational Affiliations_
    * API Base URLs - PRODUCTION (replace 'X' in the URL with the rc version)
        * Public API:   http://pub.orcid.org/v1.2_rcX/
        * Member API:   https://api.orcid.org/v1.2_rcX/
    * API Base URLs - SANDBOX
        * Public API:   http://pub.sandbox.orcid.org/v1.2_rcX/
        * Member API:   http://api.sandbox.orcid.org/v1.2_rcX/

#### Sunset versions (unavailable, unsupported, should not be used)

* **Version 1.0.x (includes versions 1.0.1 through 1.0.23)** <br>_Sunset Date: March 1, 2014_


## Key notices

In November 2013, ORCID released is new XSD versioning scheme and release process. This scheme was created to allow for flexible development of future enhancements, while providing stability for those incorporating changes. Please read the following notice carefully to familiarize yourself with these changes.

**Who is affected?** All users of the ORCID public or member API will be affected by this change.

## Staying informed

You can stay informed of updates to the XSD and API by subscribing to our [API Users Group](https://groups.google.com/forum/?fromgroups#!forum/orcid-api-users) mailing list and forum.

# About XSD Updates and Versioning

### High-level description

At any one time there may be several available versions of the XSD available for use:

* _Deprecated Version(s)_ - past version(s) of the XSD. While deprecated versions may still work, developers should be migrating to a current release version
* _Release Version(s)_ - the XSD version you should be actively migrating to in order to take advantage of either improvements or new functionality
* _One or more Release Candidates_ - these versions of the XSD contain in-progress improvements and functionality. They are made available to enable API users to take advantage of new functionality faster than waiting for the next Release Version. These versions are subject to change at any time.

### Which XSD version should I be using?

The XSD version you should use depends on what you are trying to do. Most developers will use a current Release Version, or be actively migrating to one. However, if you are developing an integration that takes advantage of new functionality or improvement, you likely will need to use one of the Release Candidates.

## Using XSD versions

### <a name="deprecatedVersion"></a>Deprecated version(s)

The deprecated version(s) of the XSD are former versions of the XSD. If you are using a deprecated version, you should be planning a migration to move to a release version. Other things of note about deprecated versions:

* An XSD that has changed to deprecated status will not be removed immediately, though we are actively encouraging all developers to move to the current release version. 
* Developers on deprecated versions should refer to the migration notes to assist in moving to the latest release version.
* Future functionality will not be supported in deprecated versions.
* Future versions may not be backward compatible with deprecated versions.
* Deprecated versions will have a “sunset date”, after which they will no longer be supported. Developers experiencing problems with deprecated versions will be directed to move to the release version.

### <a name="releaseVersion"></a>Release version(s)

Release versions of the XSD are fully documented, tested, supported and encouraged for use. Of note about release versions:

* Our intent is for release versions of the XSD to have longer lifespans than the very fast pace of ORCID new development.
* When feasible, we will provide estimates of when the next release version is likely to be available, and what it is likely to contain.
* While more than one release version may be available at any time, we highly recommend that developers update to the latest release version at least once per year to enjoy optimal XSD stability.

### <a name="releaseCandidate"></a>Release candidate(s)

Release candidate versions of the XSD contain elements and data structures for future functionality, potential changes and other enhancements. Of note about release candidates:

* The version number of release candidates will contain ‘rc’ to indicate that they are release candidates (for example, version 1.2_rc1). The first two numbers indicate the target release version for the candidate. For example, the changes and enhancements in version 1.2_rc1 are being considered for inclusion with release version 1.2.
* Release candidates are generally unsupported, and have limited documentation.
* Elements and data structure within a release candidate XSD could change at any time based on development needs and feedback from early adopters.
* We welcome feedback from any developers working with current release candidates.


# ORCID-message XSD versions

| Version/link | Release Date | Sunset date | Notes | Who should be using this version |
| ------------ | ------------ | ----------- | ----- | -------------------------------- |
| [ORCID XSD 1.2](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2.xsd) | Jan 12, 2014 | | **[RELEASE VERSION](#releaseVersion)**<br><br>CURRENT SUPPORTED VERSION<br/><br/>See the [ORCID knowledge base](http://support.orcid.org/knowledgebase/articles/481386-xsd-1-2-update) for migration details. | Fully-supported XSD. Preferred version.|
| XSD 1.2_rcX | throughout 2014 | Apr 1, 2015 | **[DEPRECATED VERSIONS](#deprecatedVersion)**<br/><br/>[XSD 1.2_rc1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc1.xsd): _adds elements for education or employment affiliations_<br/>[XSD 1.2_rc2](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc2.xsd): _updates the options for the creation-method field, and adds elements needed for funding_<br/>[XSD 1.2_rc3](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc3.xsd): _restricts the valid language codes to those supported by ORCID, and removes patents as a separate element (included under the works element)_<br/>[XSD 1.2_rc4](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc4.xsd): _provides more details about existing elements, email verification info, organization details that create an ORCID record, and funding agency details for funding element_<br/>[XSD 1.2_rc5](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc5.xsd): _clarifies elements for data provenance - the data source, creation date and modification date; work external IDs are required_<br/>[XSD 1.2_rc6](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc6.xsd): _adds more work types_ | This version has a sunset date of Apr 1, 2015, and won't be supported after this date. |
| [ORCID XSD 1.1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.1.xsd) | Nov 25, 2013 | Apr 1, 2015 | **[DEPRECATED VERSION](#deprecatedVersion)**<br><br> | This version has a sunset date of Apr 1, 2015, and won't be supported after this date. |
| [ORCID XSD 1.0.x](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources) | various | Mar 1, 2014 | **SUNSET VERSIONS** | These versions were sunset as of March 1, 2014. They are no longer available, and should not be used. _(Versions 1.0.1 - 1.0.23)_ |

# Getting help

Your best first resource for getting help is our [ORCID Support Site](http://support.orcid.org), where you may access our complete documentation, tutorials and guides, as well as contact our support team. We also invite you to submit questions to the [API Users Group](https://groups.google.com/forum/?fromgroups#!forum/orcid-api-users) mailing list and forum.
