# Institutional sign in with ORCID (SSO)

This document provides the information on setting up and troubleshooting institutional sign in with ORCID.

Institutional sign enables researchers to sign in using email that they already have as part of being part of an institution thereby reducing the burden of effort for the researcher while enabling the Institution to collect any tokens they might need to read or write to the researchers record.


## Requirements for IdP

For institutional sign in to work with ORCID some prerequisites are necessary.

 ### A Persistent Identifier

Any of the following as the persistent identifiers:

* a persistent NameID (transient NameIDs will not be accepted)

* eduPersonUniqueID (ePUID)

* eduPersonTargetedID (ePTID)

Transient name identifiers such as ePPN will not be accepted

ORCID requires a persistent ID for IDP to work/

***ORCID does not accept ePPN for this attribute***, even for research and scholarship entities. This is due to the longevity of ORCID iDs/accounts, as well as the chance, albeit small, of reassignment of eduPersonPrincipalName (ePPN).

### Membership of a supported access federation

ORCID is a member of eduGAIN. Members wishing to use IDp must be a member of SURFconext or eduGAIN interfederation service.

ORCID is a registered as a Service Provider in the eduGAIN infederation service and as such is not an identity provider.

ORCID gets IDP information directly from the eduGAIN this information is collected from federatrions that are a member of EduGAIN. IF the IDP is not in eduGAIN then ORCID cannot use it. ORCID shows any IdP that is listed in eduGAIN on the drop down when using Institution account login.

### Correctly Configured Client

You will need to request that we update your ORCID Member API credentials to support Institutional Connect. We suggest first updating your Sandbox credentials -- but note that institutional sign-in may not work on the ORCID sandbox.

Submit a request to update your Sandbox Member API client credentials to support Institutional Connect, including the following information in the notes section:

Client ID
Your identity provider entity ID (e.g. https://idp.example.org/idp/shibboleth)
Your redirect URI: The page within your ORCID-integrated system that users will be directed to after they authorize the connection
The permission scope(s) you need: These should be the same as required for your system:
/activities/update (required): add/update an affiliation with your institution. Can also be used to add/update research works, funding, and peer review activities.
/read-limited: read limited-access data on the ORCID record and obtain the authenticated ORCID iD.
/person/update: add/update a unique identifier for your institution, a link to the user's faculty webpage, and other personal data in the biographical section of the of the ORCID record.

## ORCID's  Classification Details

|Term | Details|
|---------------------------------------------------------|
|Federation(s):|SURFconext eduGAIN interfederation service|
|Entity type:| Service provider
|Entity ID:|https://orcid.org/saml2/sp/1
|ORCID metadata:|Available in theMetadata Explorer Tool (MET)|
|Supported protocols:|SAML 2.0|
|Required attributes:|ORCID requires a locally unique, persistent, non-reassignable identifier to link an institution account to an ORCID account.|
|Optional attributes: ORCID will use the following attributes if provided by the institution, but none are required for the SSO service to work.:|NAME, FUTURE, EMAIL More information see below. |


### Optional Attributes
NAME (displayName, givenName, sn): If a name is provided by the institution, ORCID will use it to
personalize the greeting to the user when they have signed in and are about to link the institutional and ORCID accounts.

FUTURE: Add the name to the researcher’s ORCID record as an “also known as” name(s) by the researcher (i.e. the researcher is listed as the source).

EMAIL (mail): If an email address is provided, ORCID will use it in the following way:
FUTURE: Add the email address to the ORCID record.

Note: The visibility of items added to ORCID records is determined by the individual researcher on the ORCID site. The researcher may delete added items at any time.

## Troubleshooting

If the prerequisites of Persistent Identifier, Federation membership have been met then there should be no further problems with IdP.  However problems do arise.

#### Check that ORCID is recieving a Persistent identifier
Use the SAML tracer plugin for Firefox or similar to get the 'blob' that is sent to ORCID. Search the file to see if any of the above persistent Identifiers are in the file. IF they aren't then double check what persistant identifier the Institution is sending. If there is a persistent identifier in the file then there is some other issue that needs further investigation.


#### Confirm the Institution is listed in eduGAIN

 [Search eduGAIN entities database](https://technical.edugain.org/entities) for the organization’s name or their domain

 Search by IDP name:

* Entity filter: name or domain
* Choose: Whole entity multiword search


Search by location:

[Look at the eduGAIN map](https://technical.edugain.org/status.php) for countries with supporting federations
Check whether it is obvious that they would not be included (i.e. the country/territory is not coloured)

Use the the Metadata Explorer tool:

Use the [Metadata Explorer](https://met.refeds.org/) to find the federation (you can use Google and the eduGAIN map to figure out what to search for - some things aren't obviously named.)

Search by IdPs

Filtered the list by IdPs (identity providers) only   
Check whether the federation is registered in eduGAIN.

#### Check the ORCID DiscoFeed

Check the ORCID "disco feed" (discovery feed) (http://orcid.org/Shibboleth.sso/DiscoFeed)
Copy the entity ID that you found in the MET tool in the steps above
Open the ORCID disco feed and search for the entity ID in the list
OR Check eduGAIN raw metadata feed (http://mds.edugain.org) upon which the ORCID disco feed is based
Sample search: for https://shib2idp.rgu.ac.uk/idp/shibboleth
Results should have information such as Display name, logo, description.


### Check that the Institution is not using an old format

ORCID runs the Shibboleth service provider on our servers, and that generates the disco feed.  By default, Shibboleth does not look for display names in the legacy format.


Old display name:

		<md:OrganizationDisplayName xml:lang="en">Example University</md:OrganizationDisplayName>

New display name:

		<mdui:DisplayName xml:lang="en">UCL (Example University)</mdui:DisplayName>
