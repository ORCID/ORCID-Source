# Manual Tests

## Register/Verify

0. Find and Replace [DD][month][YYYY] in this document with the current day, written month, and four digit year for example 24feb2016
1. Visit https://qa.orcid.org/register
2. Create new account:
    * First name: ma_test
    * Last name: [DD][month][YYYY]
    * Email: ma_test_[DD][month][YYYY]@mailinator.com (ex: ma_test_24feb2016@mailinator.com)
    * Second email: 00_ma_test_[DD][month][YYYY]@mailinator.com
    * Password: test1234
    * Default privacy for new activities: Everyone
    * Subscribe to quarterly emails about new features
    * Accept terms and conditions
3. Click the link to resend the verify email message
4. Attempt to edit the biography of the record- click the link to resend the verify email in the warning that comes up
5. Visit https://qa.orcid.org/signout
6. Visit https://www.mailinator.com and check the inbox for ma_test_[DD][month][YYYY]@mailinator.com
7. Verify there are three messages, Thanks for registering and two reminders to verify your email address
8. Open message from support@verify.orcid.org with subject [ORCID] Welcome to ORCID and click the email verification link
9. When redirected to https://qa.orcid.org/signin, sign in using ma_test credentials created in previous steps
10. Replace [orcid id] in this document with the 16 digit iD from the record
11. Visit https://qa.orcid.org/signout

## Reset password

12. Click the Forgotten Your Password link
13. Enter MA_test_[DD][month][YYYY]@mailinator.com in the form and click Send Reset Link
14. Visit https://www.mailinator.com and check the inbox for ma_test_[DD][month][YYYY]@mailinator.com
15. Open message from reset@notify.orcid.org with subject [ORCID] About your password reset request and click the password reset link
16. Reset password with [DD][month][YYYY] (No need to fill challenge question)
17. You will be forward to the sign in page, sign in with:
	Email: MA_TEST_[DD][month][YYYY]@mailinator.com
	Password: [DD][month][YYYY]
18. Visit https://qa.orcid.org/signout

## Institutional Login
19. Create a UnitedID account if you do not already have one at https://app.unitedid.org/signup/ and enable a way to get a security token by going to 'Manage security tokens' after signing up
20. Visit https://qa.orcid.org/signin and use the Institutional account option to sign in using "United ID" as the institution and the UnitedID account you just created. Complete steps to link it to the Individual account the account created in steps 1 and 2.
21. On the notification in the orange box at the top of the page to link the account to State University, click connect, you'll be taken to the OAuth page. Click 'Deny'  and return to the record.
22. Visit https://qa.orcid.org/signout
23. Visit https://qa.orcid.org/oauth/authorize?client_id=APP-6QJHHJ6OH7I9Z5XO&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground
24. Sign in using a Google account not linked to an existing ORCID record
25. Complete steps to link the Google account to the account created today
26. Check that after linking the account you are taken back to the authorize page not to my-orcid
27. Click Deny on the authorization- check that you are taken to the Google OAuth Playground with a Deny error
28. Visit https://qa.orcid.org/account and follow the steps to enable two factor authentication
29. Visit https://qa.orcid.org/signout
30. Sign in, check that you are asked for a 2FA code
31. Visit https://qa.orcid.org/account disable 2FA and revoke the Google and United ID access

## My-ORCID

32. Visit https://qa.orcid.org/my-orcid
33. Use the language selector to change the language to a different language- check that the page updates to that language
34. Use the language selector to set the page back to English
35. Add a published name: "Pub Name" (Published name can be edited using the pencil icon next to the record name)
36. Add an also know as name: "Other Name"
37. Add a country: "Afghanistan"
38. Add a keyword: "keyword"
39. Add a URL: name:"website" URL https://qa.orcid.org
40. Add a second email address: 01_ma_test_[DD][month][YYYY]@mailinator.com and change the visibility to public
41. Add a biography: "Bio!"
42. Add an education item: 'ORCID' (select from dropdown list) start date '2018'
43. Add a funding item: type 'grant', title 'ma_fund_test', funding agency 'NIH Clinical Center' (select from dropdown list)
44. Add a work: category: "publication', type: 'journal article', title 'ma_test_work', identifier type 'DOI', identifier value '1234' click through warning about the identifier validation
45. Set the work you just added to private
46. Visit public page (https://qa.orcid.org/[orcid id])
    * Verify information added in the steps above is visible, but not the private work or email addresses
47. Visit https://qa.orcid.org/signout

## Public API & Revoke Access check

48. Go to https://qa.orcid.org/oauth/authorize?client_id=APP-6QJHHJ6OH7I9Z5XO&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground&email=pub_ma_test_[DD][month][YYYY]@mailinator.com&given_names=ma_test&family_names=[DD][month][YYYY]

49. Check that the registration screen displays and first and last names and the email address are prepopulated

50. Complete the registration form & authorize the connection
    * Leave additional email blank
    * Password: [DD][month][YYYY]
    * Default privacy for new activities: Only me

51. Check you are prompted with a window asking if the record you created earlier is you. Click Continue to registration

52. Use curl to exchange the authorization code (the 6 digit code returned with the URI, you do not need to do anything on the Google Playground page). On a Mac you can open a terminal window and run the calls there, if using Windows you will need to install curl and replace single quotes with double quotes in steps 52, 53 and 55 or use an online tool that lets you execute curl commands such as https://onlinecurl.com/:

    ```
    curl -i -L -H 'Accept: application/json' --data 'client_id=APP-6QJHHJ6OH7I9Z5XO&client_secret=[replace with client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://qa.orcid.org/oauth/token' -k
    ```

53. Use curl to read the record with the public access token:

	```
	curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [token]' -X GET 'https://pub.qa.orcid.org/v2.0/[public orcid id]/record' -L -i -k
	```

54. Visit https://qa.orcid.org/account and revoke the public API permission

55. Attempt the call in step 53 again- check that an error is returned

56. On https://qa.orcid.org/account add [orcid id] as a trusted individual

## Test Self Service

For this test there is a fair amount of set up. You need to have a Consortium Lead account on QA and a Consortium Member account. For the purposes of testing you can use the accounts 0000-0003-0641-4661 for the consortium lead account and 0000-0001-5870-8499 for the consortium member account.

**Check Consortium Lead Functionality**

57. Go to https://qa.orcid.org/signin and sign in with

		0000-0003-0641-4661
		Password: test1234. Check that there is a tab 'MEMBER TOOLS'
		
58. Click on the 'MEMBER TOOLS' tab, and check the page loads

59. Scroll to the bottom of the page to the Add a new consortium member section:

			Organization Name: [DD][month][YYYY]
			Website : http://www.[DD][month][YYYY].com
			**Contact Information**
			First Name : Self_service
			Last Name: [DD][month][YYYY]
			Email: [DD][month][YYYY]@mailinator.com

**Check the Saleforce staff email to check there is a notification**

60. Visit mailinator.com and enter:	

			selfservicesalesforcetest@mailinator.com
			
61. Check that there is an email titled "Consortium member addition requested - [DD][month][YYYY]"

**Check that consortium member can add a contact**

62.  Visit www.qa.orcid.org/signout

		Sign in with
		0000-0001-5870-8499
		test1234
		
63. Go to https://qa.orcid.org/self-service

64.  Add a contact in the Add Member Contacts email field

		Email: ma_test_[DD][month][YYYY]@mailinator.com

64. Visit https://qa.orcid.org/signout

## Run the automated Independent Tests

64. Go to the CI server (https://ci.orcid.org/) and select ORCID-independent-tests-step2

65. Build the ORCID Independent Tests 2 with the following parameters
	* user_login: ma_test_[DD][month][YYYY]
 	* user_pass: [DD][month][YYYY]
 	* orcid_id: [orcid id]
 	* search_value: [DD][month][YYYY]

## Post ORCID Independent Tests

66. Visit https://qa.orcid.org/signin

67. Sign into the account created earlier
	* ORCID: [orcid id]
	* Password: [DD][month][YYYY]

68. Check that the information you entered earlier is present and items have been added by the Manual Testing Client
	* 3 other names
	* 3 countries
	* 4 keywords
	* 4 websites
	* 3 external id
	* 4 education items - open one to view details
	* 4 funding items
	* 3 works (one being a group of 2) - open one to view details
	* 1 peer-review item (with two reviews) - open one to view details

69. Visit https://qa.orcid.org/[orcid id] Check that the same information displays
	* Check that the group of works with doi:0000 only displays the public version

70. Go to http://qa.orcid.org/inbox check for:
	* the request for access notification
	* notifications of updates to the record

71. Archive the first notification in the list
	
70. Check there is a Member Tools tab and the page loads when you go to it

72. Use the switch user option to change to the record created during the OAuth process

73. Sign in to Browserstack.com and using IE 11 visit the follow pages and make sure everything loads
	* https://qa.orcid.org (check that blog feed loads as well as page contents)
	* https://qa.orcid.org/[orcid id]
	* https://qa.orcid.org/register
	* https://qa.orcid.org/sign-in
	* sign into [orcid id] account and check that it also looks as expected

74. Using browserstack check the following pages on one Android and one Apple device to check load times
	* https://qa.orcid.org (check that blog feed loads as well as page contents)
	* https://qa.orcid.org/[orcid id]
	* https://qa.orcid.org/register
	* https://qa.orcid.org/sign-in

* Finally help out by improving these instructions!      
