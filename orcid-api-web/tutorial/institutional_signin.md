# Institutional sign in with ORCID (SSO)

This document provides the information on setting up and troubleshooting institutional sign in with ORCID.

Institutional sign enables researchers to sign in using an existing account provided to them by their institution thereby reducing the burden of effort for the researcher. In addition, Institutional Connect can be enabled which allows an ORCID member institution to request read/write permission to the researcher's ORCID record.


## Requirements for Institutional sign in

Institutional sign-in does not require ORCID membership, however some prerequisites are necessary. Institutional Connect requires both the prerequisites listed below and ORCID membership.

### Membership of a supported access federation

ORCID is a member of eduGAIN. Members wishing to use institutional sign in must be a member of SURFconext or eduGAIN interfederation service.

ORCID is a registered as a Service Provider in the eduGAIN interfederation service and as such is not an identity provider.

ORCID gets Institutional sign in information directly from the eduGAIN, this information is collected from federations that are a member of EduGAIN. IF the IDP is not in eduGAIN then ORCID cannot use it. ORCID shows any IdP that is listed in eduGAIN on the drop down when using Institution account login.

### A Persistent Identifier

A persistent identifier must be provided in the IDP metadata sent to ORCID, any of the following will work as a persistent identifier:

* a persistent NameID (transient NameIDs will not be accepted)

* eduPersonUniqueID (ePUID)

* eduPersonTargetedID (ePTID)

Transient name identifiers such as ePPN will not be accepted

***ORCID does not accept ePPN for this attribute***, even for research and scholarship entities. This is due to the longevity of ORCID iDs/accounts, as well as the chance, albeit small, of reassignment of eduPersonPrincipalName (ePPN).

## ORCID's  Classification Details

|Term | Details|
|---------------|------------------------------------------|
|Federation(s):|SURFconext eduGAIN interfederation service|
|Entity type:| Service provider
|Entity ID:| https://orcid.org/saml2/sp/1
|ORCID metadata:| Available in theMetadata Explorer Tool (MET)|
|Supported protocols:| SAML 2.0|
|Required attributes:| ORCID requires a locally unique, persistent, non-reassignable identifier to link an institution account to an ORCID account.|
|Optional attributes: ORCID will use the following attributes if provided by the institution, but none are required for the SSO service to work.:| NAME, EMAIL|


### Optional Attributes
NAME (displayName, givenName): If a name is provided by the institution, ORCID will use it to populate the name fields on the ORCID registration form.

EMAIL (mail): If an email address is provided, ORCID will use it to populate the ORCID registration or sign-in form.

## Skip the ORCID sign in page

If a researcher has previously linked their ORCID record to their insitutional account and are currently signed into that institutional account they can go directly to their ORCID record skipping the sign in page. This is done by sending the user to the ORCID Service Provider Login Link, this link can be generated at [Switch Service Provider Login Link Composer](https://www.switch.ch/aai/guides/discovery/login-link-composer/) filling out the fields as directed:

* Service Provider Session Initiator Handler URL: search for 'orcid' and select either the Sandbox or Production environment.
* Session Initiator : Login
* Service Provider Target URL : `https://sandbox.orcid.org/shibboleth/signin` or `https://sandbox.orcid.org/shibboleth/signin`
* Identity Provider entityID : Your entityID, if you do not know it you can look it up at https://met.refeds.org/
* Initiation Type : Service Provider-initiated

## Troubleshooting

If the prerequisites of Persistent Identifier, Federation membership have been met then there should be no further problems with IdP.  However problems do arise.

### Check that ORCID is recieving a Persistent identifier
Use the SAML tracer plugin for Firefox or similar to get the 'blob' that is sent to ORCID. Search the file to see if any of the above persistent Identifiers are in the file. IF they aren't then double check what persistant identifier the Institution is sending. If there is a persistent identifier in the file then there is some other issue that needs further investigation.


### Confirm the Institution is listed in eduGAIN

 [Search eduGAIN entities database](https://technical.edugain.org/entities) for the organization’s name or their domain

 **Search by IDP name:**

* Entity filter: name or domain
* Choose: Whole entity multiword search

**Search by location:**

[Look at the eduGAIN map](https://technical.edugain.org/status.php) for countries with supporting federations
Check whether it is obvious that they would not be included (i.e. the country/territory is not coloured)

**Use the the Metadata Explorer tool:**

Use the [Metadata Explorer](https://met.refeds.org/) to find the federation (you can use Google and the eduGAIN map to figure out what to search for - some things aren't obviously named.)

**Search by IdPs:**

Filtered the list by IdPs (identity providers) only   
Check whether the federation is registered in eduGAIN.

### Check the ORCID DiscoFeed

Check the ORCID "disco feed" (discovery feed) (http://orcid.org/Shibboleth.sso/DiscoFeed)
Copy the entity ID that you found in the MET tool in the steps above
Open the ORCID disco feed and search for the entity ID in the list
OR Check eduGAIN raw metadata feed (http://mds.edugain.org) upon which the ORCID disco feed is based
Sample search: for https://shib2idp.rgu.ac.uk/idp/shibboleth
Results should have information such as Display name, logo, description.


### Check that the Institution is not using an old format name

ORCID runs the Shibboleth service provider on our servers, and that generates the disco feed.  By default, Shibboleth does not look for display names in the legacy format.


Old display name:

		<md:OrganizationDisplayName xml:lang="en">Example University</md:OrganizationDisplayName>

New display name:

		<mdui:DisplayName xml:lang="en">Example University</mdui:DisplayName>


## Institutional Connect

If Institutional Connect is enabled, when a researcher first connects their institutional account to ORCID via Institutional sign in they will receive a notification asking that they grant the institution access to their ORCID record via the standard 3 step OAuth process. To enabled Institutional Connect an organization must have a working Institutional Sign in process and a working integration using 3 step OAuth.

To enable Institutional Connect you will need to send a request to [ORCID support](https://support.orcid.org/hc/en-us/requests/new). We suggest first testing Institutional Connect using the ORCID Sandbox, if institutional sign-in is working for your organization on the sandbox. In your request including the following information:

* Your ORCID Client ID
* Your identity provider entity ID (e.g. https://idp.example.org/idp/shibboleth)
* Your redirect URI: The page within your ORCID-integrated system that users will be directed to after they authorise the connection. This can be the same redirect URI as used when researchers start the OAuth process from your site, or a different URI. [More about redirect URIs](https://support.orcid.org/hc/en-us/articles/360006973913-Register-a-member-API-client-application)
* The [scopes](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.1/#scopes) you need: These should be the same as requested when a researcher starts the OAuth process from your site.
