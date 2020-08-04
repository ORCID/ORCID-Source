# Manual Tests

* These tests use the public email inbox service https://www.mailinator.com/. Sometimes emails sent by the ORCID Registry don't arrive into mailinator. If that happens, just repeat the step to trigger a new email. Please note you do not need a mailinator account -- just enter the email address you want to check at the top in the mailinator website.

* It is recommended that you use a private browser window to complete these tests (except the axe accessibility tests), or otherwise a browser you don't normally use, to ensure you can complete all the steps.

## Register/Verify

0. Copy this script into a text document, and then Find and Replace [DD][month][YYYY] with the current day, written month, and four digit year (for example, 24feb2016)
1. Visit https://qa.orcid.org and check that the cookies banner is displayed correctly in home page
2. Visit https://qa.orcid.org/register
3. Create new account:
    * First name: ma_test
    * Last name: [DD][month][YYYY]
    * Email: ma_test_[DD][month][YYYY]@mailinator.com (ex: ma_test_24feb2016@mailinator.com)
    * Second email: 00_ma_test_[DD][month][YYYY]@mailinator.com
    * Password: test1234
    * Default privacy for new activities: Everyone
    * Subscribe to quarterly emails about new features
    * Accept terms and conditions
4. Click the button "Resend verification email" (located inside the yellow banner above the biography section)
5. Attempt to edit the biography of the record- click the link to resend the verify email in the warning that comes up
6. Visit https://qa.orcid.org/signout
7. Visit https://www.mailinator.com and check the inbox for ma_test_[DD][month][YYYY]@mailinator.com
8. Verify there are three messages -- one with the subject "Welcome to ORCID - verify your email address" and two with the subject "Reminder to verify your primary email address"
9. Open the message from support@verify.orcid.org with subject "Welcome to ORCID - verify your email address" and click the email verification link
10. When redirected to https://qa.orcid.org/signin, ensure there is a banner confirming the email address was verified successfully, and sign in using the credentials created at the start (ma_test_[DD][month][YYYY]@mailinator.com)
11. Replace [orcid id] in this document with the 16 digit iD from the record
12. Visit https://qa.orcid.org/signout

## Reset password and ORCID iD recovery

13. Click the link "Forgot your password or ORCID iD?"
14. Enter MA_test_[DD][month][YYYY]@mailinator.com in the form and click Recover account details (uppercase is intentional)
15. Reload the page
16. Click on the ORCID iD button
17. Enter MA_test_[DD][month][YYYY]@mailinator.com in the form and click Recover account details (uppercase is intentional)
18. Visit https://www.mailinator.com and check the inbox for ma_test_[DD][month][YYYY]@mailinator.com
19. Verify there is a message from reset@notify.orcid.org with subject [ORCID] Your ORCID iD
20. Open message from reset@notify.orcid.org with subject [ORCID] About your password reset request and click the password reset link
21. Reset password with [DD][month][YYYY] 
22. You will be forward to the sign in page, sign in with:
	* Email: MA_TEST_[DD][month][YYYY]@mailinator.com (uppercase is intentional)
	* Password: [DD][month][YYYY]
23. Visit https://qa.orcid.org/signout

## Institutional Login

24. Visit https://qa.orcid.org/signin and select the option "Institutional account"
25. Select "SALMtest IdP" from the list of organizations. You will then be taken to https://samltest.id/
26. Sign in using the test account with username "sheldon" and password "bazinga", after which you'll be taken to a page where you can select which "Information to be Provided to Service"
27. Select the default option "ask me again if information to be provided to this service changes" and click the "accept" button
28. Sign in with the ORCID credentials created at the start (ma_test_[DD][month][YYYY]@mailinator.com)
29. Visit https://qa.orcid.org/signout
30. Visit https://qa.orcid.org/oauth/authorize?client_id=APP-6QJHHJ6OH7I9Z5XO&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground
31. Click the button "Sign in with Google" and use a Google account that's not linked to an existing ORCID record
32. Complete the steps to link the Google account to the ORCID account created at the start (ma_test_[DD][month][YYYY]@mailinator.com)
33. Check that after linking the accounts you are taken back to the authorize page, not to https://qa.orcid.org/my-orcid
34. Click "Deny" on the authorization page -- check that you are taken to the Google OAuth Playground with a deny error (expect to see "?error=access_denied&error_description=User denied access" appended in the browser address bar)
35. Visit https://qa.orcid.org/account and follow the steps to enable two factor authentication
36. Visit https://qa.orcid.org/signout
37. Sign in, check that you are asked for a 2FA code
38. Visit https://qa.orcid.org/account, disable 2FA, and remove the Google and SAMLtest entries from the "alternate signin accounts" section

## My-ORCID

37. Visit https://qa.orcid.org/my-orcid
38. Use the language selector to change the language to Spanish -- check that the page updates to that language
39. Use the language selector to set the page back to English
40. Add a published name: "Pub Name" (Published name can be edited using the pencil icon next to the record name)
41. Add an also known as name: "Other Name"
42. Add a country: "Afghanistan"
43. Add a keyword: "keyword"
44. Add an entry in the "Websites & Social Links" section with the name "website" and the URL "https://qa.orcid.org"
45. Add an email address: 01_ma_test_[DD][month][YYYY]@mailinator.com
46. Change the visibility setting for ma_test_[DD][month][YYYY]@mailinator.com to public (select "everyone")
47. Add a biography: "Bio!"
48. Add an education item: 'ORCID' (select from dropdown list) start date '2018'
49. Add a funding item: type 'grant', title 'ma_fund_test', funding agency 'NASA Exoplanet Science Institute' (select from dropdown list)
50. Add a work by DOI: enter DOI "10.1087/20120404" and save without making change on the add manually screen
51. Add a work manually: category: "publication', type: 'journal article', title 'ma_test_work', identifier type 'DOI', identifier value '1234'. Ignore the warning about the identifier and click the button "add to list"
52. Set the work you just added to private (select "only me")
53. Visit public page (https://qa.orcid.org/[orcid id])
    * Verify information added in the steps above is visible, but not the private work or email addresses
	* Click on the public record print view, check that it displays properly
54. Visit https://qa.orcid.org/signout

## Public API & Revoke Access check

55. Go to https://qa.orcid.org/oauth/authorize?client_id=APP-6QJHHJ6OH7I9Z5XO&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground&email=pub_ma_test_[DD][month][YYYY]@mailinator.com&given_names=ma_test&family_names=[DD][month][YYYY]

56. Check that the registration screen displays and first and last names and the email address are prepopulated

57. Complete the registration form & authorize the connection
    * Leave additional email blank
    * Password: [DD][month][YYYY]
    * Default privacy for new activities: Only me

58. Check you are prompted with a window asking if the record you created earlier is you. Click Continue to registration

59. Use curl to exchange the authorization code (the 6 digit code returned with the URI, you do not need to do anything on the Google Playground page). On a Mac you can open a terminal window and run the calls there, if using Windows you will need to install curl and replace single quotes with double quotes in steps 52, 53 and 55 or use an online tool that lets you execute curl commands such as https://onlinecurl.com/:

    ```
    curl -i -L -H 'Accept: application/json' --data 'client_id=APP-6QJHHJ6OH7I9Z5XO&client_secret=[replace with client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://qa.orcid.org/oauth/token' -k
    ```

60. Use curl to read the record with the public access token:

	```
	curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [token]' -X GET 'https://pub.qa.orcid.org/v2.0/[public orcid id]/record' -L -i -k
	```

61. Visit https://qa.orcid.org/account and revoke the public API permission

62. Attempt the call in step 60 again- check that an error is returned

63. On https://qa.orcid.org/account add [orcid id] as a trusted individual

64. Visit https://qa.orcid.org/signout

## Test Self Service

For this test you need to have a Consortium Lead account on QA and a Consortium Member account. For the purposes of testing you can use the accounts 0000-0002-3646-4021 for the consortium lead account and 0000-0002-0517-4718 for the consortium member account.

**Check Consortium Lead Functionality**

65. Go to https://qa.orcid.org/signin and sign in with

		0000-0002-3646-4021
		Password: *Ask a member of the tech team!*. Check that there is a tab 'MEMBER TOOLS'
		
66. Click on the 'MEMBER TOOLS' tab, and check the page loads

67. Scroll to the bottom of the page to the Add a new consortium member section:

			Organization Name: [DD][month][YYYY]
			Website : http://www.[DD][month][YYYY].com
			**Contact Information**
			First Name : Self_service
			Last Name: [DD][month][YYYY]
			Email: [DD][month][YYYY]@mailinator.com

**Check the Salesforce staff email to check there is a notification**

68. Visit mailinator.com and enter:	

			sfqaselfservicetest@mailinator.com
			
69. Check that there is an email titled "Consortium member addition requested - [DD][month][YYYY]"

**Check that consortium member can add a contact**

70.  Visit www.qa.orcid.org/signout

		Sign in with
		0000-0002-0517-4718
		*QA password*
		
71.  Verify the language is set to Russian

72.  Go to https://qa.orcid.org/self-service

73.  Add a contact in the Add Member Contacts email field (at the bottom of the page)

		Email: ma_test_[DD][month][YYYY]@mailinator.com

## Run the automated Independent Tests

74. Go to the CI server (https://ci.orcid.org/) and select ORCID-independent-tests-step2

75. Build the ORCID Independent Tests 2 with the following parameters
	* user_login: ma_test_[DD][month][YYYY]
 	* user_pass: [DD][month][YYYY]
 	* orcid_id: [orcid id]
 	* search_value: [DD][month][YYYY]
	
76. Go back to CI Server Dashboard (https://ci.orcid.org/) and select ORCID-independent-tests-step1

77. Build the Step 1 tests

## Post ORCID Independent Tests

78. Visit https://qa.orcid.org/signin

79. Sign into the account created earlier
	* ORCID: [orcid id]
	* Password: [DD][month][YYYY]

80. Check that the information you entered earlier is present and items have been added by the Manual Testing Client
	* 6 other names
	* 6 countries
	* 6 keywords
	* 6 websites
	* 5 external id
	* 6 education items - open one to view details
	* 3 qualifications
	* 6 funding items
	* 3 research-resource
	* 16 works (8 bulk work items, 2 added via OBO, one work being a group of 2 and the rest being different api versions) - open one to view details
	* 2 peer-review items (one with two reviews and one with three reviews) - open one to view details

81. Visit https://qa.orcid.org/[orcid id] Check that the same information displays
	* Check that the group of works with doi:1234 only displays the public version

82. Attempt to merge two of the works, make sure they merge ok.

83. Go to http://qa.orcid.org/inbox check for:
	* the request for access notification
	* notifications of updates to the record

84. Archive the first notification in the list
	
85. Check there is a Member Tools tab and the page loads when you go to it

86. Use the switch user option to change to the record created during the OAuth process



87. Sign in to Browserstack.com and using IE 11 visit the follow pages and make sure everything loads
	* https://qa.orcid.org (check that blog feed loads as well as page contents)
	* https://qa.orcid.org/[orcid id]
	* https://qa.orcid.org/register
	* https://qa.orcid.org/sign-in
	* sign into [orcid id] account and check that it also looks as expected

88. Using browserstack check the following pages on one Android and one Apple device to check load times
	* https://qa.orcid.org (check that blog feed loads as well as page contents)
	* https://qa.orcid.org/[orcid id]
	* https://qa.orcid.org/register
	* https://qa.orcid.org/sign-in

## Accessibility Testing

89. Install Axe browser plugin from https://www.deque.com/axe/ if you don't have it already. 

90. Visit the following pages and check in the AXE console that their errors are less or the same as stated below. If they are more than stated below visit the [QA benchmarks](https://docs.google.com/document/d/1jSzuH9k5KeX-OqGdltj9dJx5MorczSWhFbcbYgVWWrc/edit?usp=sharing) and compare your result to the results there to see what is causing the error.
91. Visit https://qa.orcid.org and check errors are same or less than 20 errors
92. Visit https://qa.orcid.org/register and check errors are same or less than 19
93. Visit https://qa.orcid.org/signin and check errors are same or less than 15
94. Visit https://qa.orcid.org/reset-password and check errors are same or less than 5
95.	Stay on this page and sign in with

	```0000-0002-7361-1027```
	```QA password```

96. Visit https://qa.orcid.org/myorcid Check the number of errors in AXE is less than  60
97. Visit https://qa.orcid.org/signout
98. Upload your document to https://drive.google.com/drive/folders/1yG9ksNIGF9Iy7NU858cmuo7ReUbqSqcO 

* Finally help out by improving these instructions!      
