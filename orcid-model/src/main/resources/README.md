# ORCID XSD Information

The current version of the ORCID XSD is [Version 1.0.22](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.0.22.xsd).

Read the [changelog from the previous version](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.0.22.md).

## Key Notices

While most versions of the XSD are backward compatible, a deprecated version is expected during Summer 2013. See the [notice of the change](http://support.orcid.org/knowledgebase/articles/167943-xsd-1-0-10-planning-for-the-update).

## Staying Informed

You can stay informed of updates to the XSD and API by subscribing to our [API Users Group](https://groups.google.com/forum/?fromgroups#!forum/orcid-api-users) mailing list and forum.

## About XSD Updates and Versioning

In November 2013, ORCID released is new XSD versioning scheme. This scheme was created to allow for flexible development of future enhancements, while providing stability for those incorporating changes.

### Who is affected?

All users of the ORCID public or member API will be affected by this change.

### High-level description

At any one time there may be several available versions of the XSD available for use:

* _Deprecated Version(s)_ - past version(s) of the XSD. While deprecated versions may still work, developers should be migrating to a current release version
* _Release Version(s)_ - the XSD version you should be actively migrating to in order to take advantage of either improvements or new functionality
* _One or more Release Candidates_ - these versions of the XSD contain in-progress improvements and functionality. They are made available to enable API users to take advantage of new functionality faster than waiting for the next Release Version. These versions are subject to change at any time.

### Which XSD version should I be using?

The XSD version you should use depends on what you are trying to do. Most developers will use a current Release Version, or be actively migrating to one. However, if you are developing an integration that takes advantage of new functionality or improvement, you likely will need to use one of the Release Candidates.

## Using XSD Versions

### Deprecated Version(s)

The deprecated version(s) of the XSD are former versions of the XSD. If you are using a deprecated version, you should be planning a migration to move to a release version. Other things of note about deprecated versions:

* An XSD that has changed to deprecated status will not be removed immediately, though we are actively encouraging all developers to move to the current release version. 
* Developers on deprecated versions should refer to the migration notes to assist in moving to the latest release version.
* Future functionality will not be supported in deprecated versions.
* Future versions may not be backward compatible with deprecated versions.
* Deprecated versions will have a “sunset date”, after which they will no longer be supported. developers experiencing problems with deprecated versions will be directed to move to the release version.

### Release Version(s)

Release versions of the XSD are fully documented, tested, supported and encouraged for use. Of note about release versions:

* Our intent is for release versions of the XSD to have longer lifespans than the very fast pace of ORCID new development.
* When feasible, we will provide estimates of when the next release version is likely to be available.
* While more than one release version may be available at any time, we highly recommend that developers update to the latest release version at least once per year to enjoy optimal XSD stability.

### Release Candidate(s)

Release candidate versions of the XSD contain elements and data structures for future functionality, potential changes and other enhancements. Of note about release candidates:

* The version number of release candidates will contain ‘rc’ to indicate that they are release candidates (for example, version 1.2_rc1). The first two numbers indicate the target release version for the candidate. For example, the changes and enhancements in version 1.2_rc1 are being considered for inclusion with release version 1.2.
* Release candidates are generally unsupported, and have limited documentation.
* Elements and data structure within a release candidate XSD could change at any time based on development needs and feedback from early adopters.
* We welcome feedback from any developers working with current release candidates.


## ORCID-Message XSD Versions

| Version/link | Release Date | Sunset date | Notes | Who should be using this version |
| ------------ | ------------ | ----------- | ----- | -------------------------------- |
| ORCID XSD 1.0.x | various | Mar 1, 2014 | DEPRECATED VERSIONS <br><br> Currently versions 1.0.1 - 1.0.23 | _All users should immediately begin their migrations to version 1.1._ <br><br>These versions have a sunset date of March 1, 2014, and won't be supported after this date. |
| ORCID XSD 1.1 | Nov 25, 2013 | | **RELEASE VERSION**<br><br>CURRENT SUPPORTED VERSION | ALL API users should migrate to this version to ensure future API compatibility. |
| XSD 1.2_rc1 | | | RELEASE CANDIDATE: Affiliations | This unsupported release candidate provides elements needed for API calls involving Education or Employment Affiliations. |
| | | | _other release candidates are likely prior to version ORCID XSD 1.2_ | |
| ORCID XSD 1.2 | TBD - target: Mar 2014 | | Expected to include support for:<br>* Affiliations<br>* Funding<br>* Account Delegation | First fully-supported XSD for this new functionality |

## Getting help

Your best first resource for getting help is our [ORCID Support Site](http://support.orcid.org). We also invite you to submit questions to the [API Users Group](https://groups.google.com/forum/?fromgroups#!forum/orcid-api-users) mailing list and forum, or to contact our member support help desk at support@orcid.org.
