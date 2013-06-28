# ORCID Emails to Users

This directory contains the emails that are sent to ORCID Users.

## Additional Information

The specification for the emails can be found in the [specification document](https://docs.google.com/a/orcid.org/document/d/1eK7mNZCPAtcUTeB-JKCBNW956KdWtw0SlI4jYN5-DUY/edit#) _(internal document)_

The managers for sending out the emails can be found:

* /ORCID-Source/orcid-core/src/main/java/org/orcid/core/profileEvent/CrossRefEmail.java
	* verification_email_w_crossref.ftl
	* verification_email_w_crossref_html.ftl
* /ORCID-Source/orcid-core/src/main/java/org/orcid/core/manager/impl/NotificationManagerImpl.java
	* All other emails

## The Emails

### ACCOUNT CREATION / CLAIM

### Claim your newly-created ORCID Account
_Email asking a user to claim their ORCID record created by a member organization._

* [api\_record\_creation\_email.ftl](https://github.com/ORCID/ORCID-Source/blob/master/orcid-core/src/main/resources/org/orcid/core/template/api_record_creation_email.ftl)
* [Internal Spec](https://docs.google.com/a/orcid.org/document/d/1eK7mNZCPAtcUTeB-JKCBNW956KdWtw0SlI4jYN5-DUY/edit#heading=h.3q1mf7owcij)
* Variables:
	* ${emailName} - the name of the recipient
	* ${orcid} - the ORCID iD of the recipient
	* ${creatorName} - the name of the member organization creating the account
	* ${baseUri} - the base URI of the sending site
	* ${verificationUrl} - the personalized URL generated to claim the record

### Reminder to claim your ORCID Account
_Email reminding the user to claim their ORCID Record before the Account goes live._

* [claim\_reminder\_email.ftl](https://github.com/ORCID/ORCID-Source/blob/master/orcid-core/src/main/resources/org/orcid/core/template/claim_reminder_email.ftl)
* [Internal Spec](https://docs.google.com/a/orcid.org/document/d/1eK7mNZCPAtcUTeB-JKCBNW956KdWtw0SlI4jYN5-DUY/edit#heading=h.5x68m06t1fhu)
* Variables:
	* ${emailName} - the name of the recipient
	* ${orcid} - the ORCID iD of the recipient
	* ${creatorName} - the name of the member organization creating the account
	* ${baseUri} - the base URI of the sending site
	* ${verificationUrl} - the personalized URL generated to claim the record
	* ${daysUntilActivation} - the number of days until the account will go live

### EMAIL VERIFICATION

### Email verification required 
_Email to verify any email address added to an ORCID account. The email requests that the record holder verify his/her email address by clicking on the verification link presented in the email._

* [verification\_email.ftl](https://github.com/ORCID/ORCID-Source/blob/master/orcid-core/src/main/resources/org/orcid/core/template/verification_email.ftl)
* [Internal Spec](https://docs.google.com/a/orcid.org/document/d/1eK7mNZCPAtcUTeB-JKCBNW956KdWtw0SlI4jYN5-DUY/edit#heading=h.ypwpsmhn2f36)
* Variables:
	* ${emailName} - the name of the recipient
	* ${verificationUrl} - the personalized URL generated to verify the email address
	* ${orcid} - the ORCID iD of the recipient
	* ${baseUri} - the base URI of the sending site

### Reminder to verify your email address
_Sent to the user one week after setting a primary email address if the account is active and the address has not yet been verified._

* [verification\_reminder\_email.ftl](https://github.com/ORCID/ORCID-Source/blob/master/orcid-core/src/main/resources/org/orcid/core/template/verification_reminder_email.ftl)
* [Internal Spec]()
* Variables:
	* ${emailName} - the name of the recipient
	* ${verificationUrl} - the personalized URL generated to verify the email address
	* ${orcid} - the ORCID iD of the recipient
	* ${baseUri} - the base URI of the sending site

### Semi-annual email verification reminder
_Email sent twice yearly to all active accounts that do not have a verified primary email address. Used to encourage people who still haven't verified their primary email address to do so._

* [verification\_email\_w\_crossref.ftl](https://github.com/ORCID/ORCID-Source/blob/master/orcid-core/src/main/resources/org/orcid/core/template/verification_email_w_crossref.ftl) 
* [verification\_email\_w\_crossref\_html.ftl](https://github.com/ORCID/ORCID-Source/blob/master/orcid-core/src/main/resources/org/orcid/core/template/verification_email_w_crossref_html.ftl) _(HTML Version)
* SENT:
	* June 12, 2013

### ACCOUNT SECURITY

### Your email has been successfully changed 
_Email sent to the former primary email address when the primary address on the account is changed._

* [email\_removed.ftl](https://github.com/ORCID/ORCID-Source/blob/master/orcid-core/src/main/resources/org/orcid/core/template/email_removed.ftl)
* [Internal Spec](https://docs.google.com/a/orcid.org/document/d/1eK7mNZCPAtcUTeB-JKCBNW956KdWtw0SlI4jYN5-DUY/edit#heading=h.8nvu0k18r0u8)
* Variables:
	* ${emailName} - the name of the recipient
	* ${verificationUrl} - the personalized URL to verify the new email (not used)
	* ${oldEmail} - the previous primary email
	* ${newEmail} - the new primary email (not used)
	* ${orcid} - the ORCID iD of the recipient
	* ${baseUri} - the base URI of the sending site

### About your password reset request
_Email sent to the user when they are having problems logging in so that they can log in an alternate way. Related to the Forgotten Password functionality._

* [reset\_password\_email.ftl](https://github.com/ORCID/ORCID-Source/blob/master/orcid-core/src/main/resources/org/orcid/core/template/reset_password_email.ftl)
* [Internal Spec](https://docs.google.com/a/orcid.org/document/d/1eK7mNZCPAtcUTeB-JKCBNW956KdWtw0SlI4jYN5-DUY/edit#heading=h.npmh2wggac7m)
* Variables:
	* ${emailName} - the name of the recipient
	* ${orcid} - the ORCID iD of the recipient
	* ${baseUri} - the base URI of the sending site
	* ${passwordResetUrl} - the personalized URL to reset the user’s password

### Confirm deactivation of your ORCID Account
_This email is sent to a user who has decided to deactivate their account. A link is provided to confirm the deactivation._

* [deactivate\_orcid\_email.ftl](https://github.com/ORCID/ORCID-Source/blob/master/orcid-core/src/main/resources/org/orcid/core/template/deactivate_orcid_email.ftl)
* [Internal Spec](https://docs.google.com/a/orcid.org/document/d/1eK7mNZCPAtcUTeB-JKCBNW956KdWtw0SlI4jYN5-DUY/edit#heading=h.k479y1b22gn8)
* Variables:
	* ${emailName} - the name of the recipient
	* ${orcid} - the ORCID iD of the recipient
	* ${baseUri} - the base URI of the sending site
	* ${deactivateUrlEndpoint} - the link used to confirm the deactivation of the account


### RECORD ACTIONS

### Your ORCID Record was amended
_Email to the ORCID user when an organization has edited their record. This email is subject to the opt out rules (send change notifications)_

* [amend\_email.ftl](https://github.com/ORCID/ORCID-Source/blob/master/orcid-core/src/main/resources/org/orcid/core/template/amend_email.ftl)
* [Internal Spec](https://docs.google.com/a/orcid.org/document/d/1eK7mNZCPAtcUTeB-JKCBNW956KdWtw0SlI4jYN5-DUY/edit#heading=h.aphbwgthy0cy)
* Variables:
	* ${emailName} - Name of the email recipient. Either 1)  Credit name, 2) First_Name Last_Name, or 3) “ORCID Registry User”
	* ${orcid} - Numeric portion of the ORCID iD of the email recipient
	* ${amenderName} - Name of the person or organization who amended the ORCID Record
	* ${baseUri} - The Base URI of the site sending the email


### FUTURE

### You've been made an Account Delegate!
_Email to the Account Delegate when someone grants them access to their account. This email is considered a Service Message, and is not affected by Opt-out settings._

* [added\_as\_delegate\_email.ftl](https://github.com/ORCID/ORCID-Source/blob/master/orcid-core/src/main/resources/org/orcid/core/template/added_as_delegate_email.ftl)
* [Internal Spec](https://docs.google.com/a/orcid.org/document/d/1eK7mNZCPAtcUTeB-JKCBNW956KdWtw0SlI4jYN5-DUY/edit#heading=h.r9ka6n639epv)
* Variables:
	* ${emailNameForDeletate} - the name of the Account Delegate
	* ${grantingOrcidValue} - the numerical portion of the ORCID iD of the person granting Delegate permissions
	* ${grantingOrcidName} - the name of the person granting Delegate permissions
	* ${baseUri} - the base URI of the sending site

### LEGACY EMAILS

### Registration Complete - NOT USED

* legacy\_verification\_email.ftl

### Invitation to register for an ORCID Account - NOT USED

* registration\_email.ftl
