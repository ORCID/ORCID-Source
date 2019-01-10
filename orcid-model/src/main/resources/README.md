## ORCID Message (XSD) Readme

**Contents**

* [XSD Information](#orcid-xsd-information)
    * [Active Versions & Base URLs](#active-xsd-versions-and-base-urls)
* [About XSD Updates and Versioning](#about-xsd-updates-and-versioning)
    * [Staying Informed](#staying-informed)
    * [Using XSD Versions](#using-xsd-versions)
* [ORCID-Message XSD Versions Table](#orcid-message-xsd-versions)
* [Getting Help](#getting-help)

---

# ORCID XSD information

## Active XSD versions and base URLs

#### XSD release version

* **[ORCID Message 2.1](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.1)** - [Documentation](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/README.md)
    * API Base URLs - PRODUCTION
        * Public API:   https://pub.orcid.org/v2.1/
        * Member API:   https://api.orcid.org/v2.1/
    * API Base URLs - SANDBOX
        * Public API:   https://pub.sandbox.orcid.org/v2.1/
        * Member API:   https://api.sandbox.orcid.org/v2.1/

* **[ORCID Message 2.0](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.0)** - [Documentation](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/README.md), [Change log from 1.2](https://members.orcid.org/api/news/xsd-20-update)
    * API Base URLs - PRODUCTION
        * Public API:   https://pub.orcid.org/v2.0/
        * Member API:   https://api.orcid.org/v2.0/
    * API Base URLs - SANDBOX
        * Public API:   https://pub.sandbox.orcid.org/v2.0/
        * Member API:   https://api.sandbox.orcid.org/v2.0/

#### XSD release candidate(s)

#### XSD deprecated versions

* **[ORCID Message 2.0_rc4](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.0_rc4)** - _Contains: Minor corrections and updates_ <br/>
* **[ORCID Message 2.0_rc3](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.0_rc3)** - _Contains: bulk works, activities section endpoints_ <br/>
* **[ORCID Message 2.0_rc2](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.0_rc2)** - _Contains: Person section, migrates all identifiers to external-identifier_ <br/>
* **[ORCID Message 2.0_rc1](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.0_rc1)** - _Contains: Peer-review, new format XSD for activities_ <br/><br/>
    * API Base URLs - PRODUCTION (replace 'X' in the URL with the rc version)
        * Public API:   https://pub.orcid.org/v2.0_rcX/
        * Member API:   https://api.orcid.org/v2.0_rcX/
    * API Base URLs - SANDBOX
        * Public API:   https://pub.sandbox.orcid.org/v2.0_rcX/
        * Member API:   https://api.sandbox.orcid.org/v2.0_rcX/

* **[ORCID Message 1.2](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2.xsd)**
    * API Base URLs - PRODUCTION
        * Public API:   https://pub.orcid.org/v1.2/
        * Member API:   https://api.orcid.org/v1.2/
    * API Base URLs - SANDBOX
        * Public API:   https://pub.sandbox.orcid.org/v1.2/
        * Member API:   https://api.sandbox.orcid.org/v1.2/


#### Sunset versions (unavailable, unsupported, should not be used)

* **Version 1.0.x (includes versions 1.0.1 through 1.0.23)** <br>_Sunset Date: March 1, 2014_
* **[ORCID Message 1.1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.1.xsd)** <br>_Sunset Date: April 1, 2015_
* **Version 1.2.x ** <br>_Sunset Date: Aug 1, 2018_

_NOTE:<br />API calls for member to ORCID iD holder authorization using OAuth are not affected by XSD versions. Regardless of the XSD version used, the OAuth endpoints are:_

* _[http://orcid.org/oauth/authorize](https://members.orcid.org/api/oauth/get-oauthauthorize)_
* _[https://orcid.org/oauth/token](https://members.orcid.org/api/oauth/3legged-oauth)_


# About XSD Updates and Versioning


At any one time there may be several available versions of the XSD available for use:

* _Deprecated Version(s)_ - past version(s) of the XSD. While deprecated versions may still work, developers should be migrating to a current release version. New development should not be done against deprecated versions.
* _Release Version(s)_ - the XSD version you should be actively migrating to in order to take advantage of either improvements or new functionality.
* _One or more Release Candidates_ - these versions of the XSD contain in-progress improvements and functionality. They are made available to enable API users to take advantage of new functionality faster than waiting for the next Release Version. These versions are subject to change at any time.

_Which XSD version should I be using?_

The XSD version you should use depends on what you are trying to do. Most developers will use a current Release Version, or be actively migrating to one. However, if you are developing an integration that takes advantage of new functionality or improvement, you likely will need to use one of the Release Candidates.

## Staying informed

You can stay informed of updates to the XSD and API by subscribing to our [API Users Group](https://groups.google.com/forum/?fromgroups#!forum/orcid-api-users) mailing list and forum.

## Using XSD versions

### <a name="deprecatedVersion"></a>Deprecated version(s)

The deprecated version(s) of the XSD are former versions of the XSD. If you are using a deprecated version, you should be planning a migration to move to a release version. Other things of note about deprecated versions:

* An XSD that has changed to deprecated status will not be removed immediately, though we are actively encouraging all developers to move to the current release version.
* Developers on deprecated versions should refer to the migration notes to assist in moving to the latest release version.
* Future functionality will not be supported in deprecated versions.
* Future versions may not be backward compatible with deprecated versions.
* Deprecated versions will have a "sunset date", after which they will no longer be supported. Developers experiencing problems with deprecated versions will be directed to move to the release version.

### <a name="releaseVersion"></a>Release version(s)

Release versions of the XSD are fully documented, tested, supported and encouraged for use. Of note about release versions:

* Our intent is for release versions of the XSD to have longer lifespans than the very fast pace of ORCID new development.
* When feasible, we will provide estimates of when the next release version is likely to be available, and what it is likely to contain.
* While more than one release version may be available at any time, we highly recommend that developers update to the latest release version at least once per year to enjoy optimal XSD stability.

### <a name="releaseCandidate"></a>Release candidate(s)

Release candidate versions of the XSD contain elements and data structures for future functionality, potential changes and other enhancements. Of note about release candidates:

* The version number of release candidates will contain "rc" to indicate that they are release candidates (for example, version 1.2_rc1). The first two numbers indicate the target release version for the candidate. For example, the changes and enhancements in version 1.2_rc1 are being considered for inclusion with release version 1.2.
* Release candidates are generally unsupported, and have limited documentation.
* Elements and data structure within a release candidate XSD could change at any time based on development needs and feedback from early adopters.
* We welcome feedback from any developers working with current release candidates.


# ORCID-message XSD versions

| Version/link | Release Date | Sunset date | Notes | Who should be using this version |
| ------------ | ------------ | ----------- | ----- | -------------------------------- |
| [ORCID XSD 2.1](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.1) | Nov 17, 2017 | | **[Release version](#releaseVersion)**<br><br>CURRENT SUPPORTED VERSION<br/>*ORCID iDs are expressed in the canonical form with the https prefix* |This is a preferred version. All developers should be using or migrating to this version or 2.0. [Documentation](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/README.md)|
| [ORCID XSD 2.0](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.0) | Feb 13, 2017 | | **[Release version](#releaseVersion)**<br><br>CURRENT SUPPORTED VERSION<br/>[Change log from 1.2](https://members.orcid.org/api/news/xsd-20-update) |This is a preferred version. All developers should be using or migrating to this version or 2.1. [Documentation](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/README.md)|
| [ORCID XSD 2.0_rc4](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.0_rc4) | Jan 24, 2017 | Spring 2018 | **[Release candidate version](#releaseCandidate)**<br><br/>_minor fixes and corrections_ | |
| [ORCID XSD 2.0_rc3](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.0_rc3) | Nov 18, 2016 | Spring 2018 | **[Release candidate version](#releaseCandidate)**<br><br/>_adds bulk works_ |  |
| [ORCID XSD 2.0_rc2](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.0_rc2) | Jul 20, 2016 | Spring 2018 | **[Release candidate version](#releaseCandidate)**<br><br/>_adds person section_ | |
| [ORCID XSD 2.0_rc1](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.0_rc1) | Jul 01, 2015 | Spring 2018 | **[Release candidate version](#releaseCandidate)**<br><br/>_adds peer-review section, reformats XSD for activities_ | |
| [ORCID XSD 1.2](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2.xsd) | Jan 12, 2015 | August 1st 2018 | **[Deprecated version](#deprecatedVersion)**<br/><br/> | All developers on this version should migrate to version 2.0 |
| XSD 1.2_rcX | throughout 2014 | Apr 1, 2015 | **[Deprecated versions](#deprecatedVersion)**<br/><br/>[XSD 1.2_rc1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc1.xsd): _adds elements for education or employment affiliations_<br/>[XSD 1.2_rc2](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc2.xsd): _updates the options for the creation-method field, and adds elements needed for funding_<br/>[XSD 1.2_rc3](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc3.xsd): _restricts the valid language codes to those supported by ORCID, and removes patents as a separate element (included under the works element)_<br/>[XSD 1.2_rc4](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc4.xsd): _provides more details about existing elements, email verification info, organization details that create an ORCID record, and funding agency details for funding element_<br/>[XSD 1.2_rc5](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc5.xsd): _clarifies elements for data provenance - the data source, creation date and modification date; work external IDs are required_<br/>[XSD 1.2_rc6](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.2_rc6.xsd): _adds more work types_ | These versions were sunset as of Apr 1, 2015. They are no longer available and should not be used. |
| [ORCID XSD 1.1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.1.xsd) | Nov 25, 2013 | Apr 1, 2015 | **[Deprecated version](#deprecatedVersion)**<br><br> | This version was sunset as of Apr 1, 2015. It is no longer available and should not be used. |
| [ORCID XSD 1.0.x](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources) | various | Mar 1, 2014 | **Sunset version** | These versions were sunset as of March 1, 2014. They are no longer available and should not be used. _(Versions 1.0.1 - 1.0.23)_ |

# Getting help

Technical documentation is included with the read-me file for each version. [Current release documentation](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/README.md)

The [ORCID Member Support Site](https://members.orcid.org) has additional documentation, tutorials and guides, as well as contact for our support team.

We also invite you to submit questions to the [API Users Group](https://groups.google.com/forum/?fromgroups#!forum/orcid-api-users) mailing list and forum.
