# ORCID XSD Information

The current version of the ORCID XSD is [Version 1.0.22](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.0.22.xsd).

Read the [changelog from the previous version](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/orcid-message-1.0.22.md).

## Key Notices

While most versions of the XSD are backward compatible, a deprecated version is expected during Summer 2013. See the [notice of the change](http://support.orcid.org/knowledgebase/articles/167943-xsd-1-0-10-planning-for-the-update).

## Staying Informed

You can stay informed of updates to the XSD and API by subscribing to our [API Users Group](https://groups.google.com/forum/?fromgroups#!forum/orcid-api-users) mailing list and forum.

## Our Guidelines for XSD Updates

### BACKWARD COMPATIBLE CHANGES

All updates to the XSD will be backward compatible unless otherwise specified.

### NON-BACKWARD COMPATIBLE CHANGES

* _ADVANCED COMMUNICATION_: In the instance where something will not be backward compatible, we communicate in advance what the planned change will be, and provide an approximate timeline for this change. (note, it is possible that there will be additional backward compatible updates during this interval.)
* _TESTING PERIOD_: For at least two weeks before a change that isn't backward compatible is in place, we will support both versions. While we are allowing members to code to changes during the advance communication period, we also allow for this testing period against the final XSD.
* _ALL ON BOARD_: We are committed to our member organizations to ensure that uses of our APIs do not break when updates are made. In the rare instance that the planned change does not work as expected, we will delay the cutover timeline to provide time to ensure a successful transition both for the ORCID website, and those integrations with our member partners.

## Getting help

Your best first resource for getting help is our [ORCID-Source Wiki](https://github.com/ORCID/ORCID-Source/wiki). We also invite you to submit questinos to the [API Users Group](https://groups.google.com/forum/?fromgroups#!forum/orcid-api-users) mailing list and forum, or to contact our member support help desk at support@orcid.org.
