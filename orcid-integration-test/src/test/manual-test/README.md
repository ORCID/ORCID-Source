# Manual Test

## If working with windows

Known Issues:

Git bash present issues with the format of the scope, i.e. Instruction 43. 

Solution (easiest one):

Download Vagrant (https://www.vagrantup.com/docs/installation/) and virtual box (https://www.virtualbox.org/).

Follow the instructions on vagrant getting started guide to get your basic environment.

Known problems:
Once Vagrant Up has been run, you must run "vagrant ssh". It is possible that ssh is not added to your environment variables and this will cause an error that will be reported in the console.

To add ssh you need to install git. After this, proceed to add the following path to your PATH environment variable: C:\Program Files\Git\usr\bin (or wherever git is installed on your system).

After this you should be able to enter the vagrant enviroment. There you will need to install curl ($ sudo apt-get install curl).

Now you are ready to start your tests.

NOTE: After instruction 58 you will need to use some files located on your system.

Please read this guide on how to do this: https://www.vagrantup.com/docs/getting-started/synced_folders.html

Short story: copy your folder with the tests inside your vagrant folder. I.e: C:\_work\vagrant\manual-test. Update the path inside the curl command with "@/vagrant/manual-test/ma_work.xml" matching the name of your folder.

## Register/Verify

0. Get the list of QA testing clients from ORCID-Internal, find and replace the member and public API client iDs and secrets with the ones listed in that document
0. Find and Replace [DD][month][YYYY] in this document with the current day, written month, and four digit year for example 24feb2016
1. Visit https://qa.orcid.org/register
2. Create new account:
    * First name: ma_test
    * Last name: [DD][month][YYYY]
    * Email: ma_test_[DD][month][YYYY]@mailinator.com (ex: ma_test_24feb2016@mailinator.com)
    * Password: generate random password using [LastPass generator](https://lastpass.com/generatepassword.php) or similar
    * Default privacy for new activities: Public
    * Email frequency: Weekly summary
3. Visit https://qa.orcid.org/signout
4. Visit http://mailinator.com and check ma_test_[DD][month][YYYY]@mailinator.com
5. Open message from support@verify.orcid.org with subject [ORCID] Thanks for creating an ORCID iD 
6. Click (or copy/paste) email verification link
7. When redirected to https://qa.orcid.org/signin, sign in using ma_test credentials created in previous steps
8. Visit https://qa.orcid.org/signout

## Reset password

9. Visit https://qa.orcid.org/reset-password and reset the password for ma_test_[DD][month][YYYY]@mailinator.com
10. Visit http://mailinator.com and check ma_test_[DD][month][YYYY]@mailinator.com
11. Open message from reset@notify.orcid.org with subject [ORCID] About your password reset request 
12. Click (or copy/paste) password reset link
13. Reset password with [DD][month][YYYY]
14. Visit https://qa.orcid.org/signin and sign in using the new password
15. Visit https://qa.orcid.org/signout

## Sign In

16. Create a UnitedID account if you do not already have one at https://app.unitedid.org/signup/ and enable a way to get a security token by going to 'Manage security tokens' after signing up
17. Visit https://qa.orcid.org/signin and sign in using a UnitedID account and complete steps to link it to the account created today.
18. Check that the notification to link the account to State University displays on the record
19. Visit https://qa.orcid.org/signout
20. Visit https://qa.orcid.org/oauth/authorize?client_id=[public client id]&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground&show_login=true
21. Sign in using a Google account not linked to an existing ORCID record
22. Complete steps to link the Google account to the account created today
23. Check that after linking the account you are taken back to the authorize page not to my-orcid (you do not need to complete the authorization)
24. Visit https://qa.orcid.org/account and revoke Google and UnitedID account access

## My-ORCID

25. Visit https://qa.orcid.org/my-orcid
26. Use the language selector to change the language to Spanish- check that the page updates into Spanish
27. Use the language selector to set the page back to English
28. Add a published name: Published Name (Published name can be edited using the pencil icon next to the record name)
29. Add an also know as name: Other Name
30. Add a country: Afghanistan
31. Add a keyword: keyword
32. Add a URL: website/https://qa.orcid.org
33. Add a biography: Bio!
34. Add an education item: Institution 'ORCID' (select from dropdown list)
35. Add a funding item: type 'grant', title 'ma fund test', funding agency 'Wellcome Trust' (select from dropdown list)
36. Add a work: category: "publication', type: 'journal article', title 'ma test work', identifier type 'DOI', identifier value “9999”
37. Add a second email address: 01_ma_test_[DD][month][YYYY]@mailinator.com and change the visibility to public
38. Visit public page (http://qa.orcid.org/[XXXX-XXXX-XXXX-XXXX])
    * Verify information added in the steps above is visible
    * Verify email address ma_test_[DD][month][YYYY]@mailinator.com is not visible
39. Visit https://qa.orcid.org/signout

## Public API Authenticate

40. Go to https://qa.orcid.org/oauth/authorize?client_id=[public client id]&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground

41. Register for a new account and grant authorization
    * First name: ma_public_test
    * Last name: [DD][month][YYYY] (ex: 24feb2016)
    * Email: pub_ma_test_[DD][month][YYYY]@mailinator.com (ex: pub_ma_test_24feb2016@mailinator.com)
    * Password: generate random password using [LastPass generator](https://lastpass.com/generatepassword.php) or similar
    * Default privacy for new activities: Private
    * Email frequency: Never

42. Exchange the authorization code (the 6 digit code returned with the URI, you do not need to do anything on the Google Playground page): 

    ```
    curl -i -L -H 'Accept: application/json' --data 'client_id=[public client id]&client_secret=[public client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://qa.orcid.org/oauth/token' -k
    ```

## Public API Read/Search

43. Generate a read-public token:

    ```
    curl -i -L -H 'Accept: application/json' -d 'client_id=[public client id]' -d 'client_secret=[public client secret]' -d 'scope=/read-public' -d 'grant_type=client_credentials' 'http://pub.qa.orcid.org/oauth/token'
    ```

44. Find and replace [public token] in this document with the token

45. Search for the records you created:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [public token]' -H 'Accept: application/xml' 'https://pub.qa.orcid.org/v1.2/search/orcid-bio/?q=family-name:[DD][month][YYYY]' -k
    ```
46. Check that both the ma_test and ma_public_test records are returned in the search results

47. Find and replace [orcid id] in this document with iD of the record created at the start of this document

48. Read the record with 1.2, check that it is returned:
 
    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [public token]' -X GET 'http://pub.qa.orcid.org/v1.2/[orcid id]/orcid-profile' -L -i -k
    ```
    
49. Read the record with 2.0, check that it is returned:
 
    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [public token]' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/[orcid id]/record' -L -i -k
    ```

50. Read the record without a version, check that it is returned: 

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [public token]' -X GET 'http://pub.qa.orcid.org/[orcid id]/orcid-profile' -L -i -k
    ```

51. Read the record without an access token, check that it is returned: 

    ```
    curl -H 'Content-Type: application/xml' 'http://pub.qa.orcid.org/v1.2/[orcid id]/orcid-profile' -L -i -k
    ```
    
52. Visit https://qa.orcid.org/signout

## Member API 1.2 Post/Update

53. Go to https://qa.orcid.org/oauth/authorize?client_id=[client id]&response_type=code&scope=/orcid-bio/update /orcid-works/create /orcid-works/update /affiliations/create /affiliations/update /funding/create /funding/update /orcid-profile/read-limited&redirect_uri=https://developers.google.com/oauthplayground&email=ma_test_[DD][month][YYYY]@mailinator.com

53. Log into the account created for testing today and grant short lived authorization. After granting authorization you will be taken to the Google OAuth playground- you do not need to do anything on this page, but retrieve the access code included with the URL

54. Exchange the authorization code (the 6 digit code returned in the URI):
 
    ```
    curl -i -L -H 'Accept: application/json' --data 'client_id=[client id]&client_secret=[client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://qa.orcid.org/oauth/token' -k
    ```

55. Find and replace [1.2 token] in this document with the access token

56. Find and replace [1.2 refresh] in this document with the refresh token

57. Update the files paths in this document to point to the local copy of the manual-test files or change directories to where they are stored (ie cd to /src/test/manual-test)

58. Post the ma test work:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_work.xml' -X POST 'http://api.qa.orcid.org/v1.2/[orcid id]/orcid-works' -L -i -k
    ```
59. Check that the work appears at https://qa.orcid.org/my-orcid

60. Update the ma test work: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_work2.xml' -X PUT 'http://api.qa.orcid.org/v1.2/[orcid id]/orcid-works' -L -i -k
    ```

61. Check that the work is updated and the manually added work is not affected at https://qa.orcid.org/my-orcid

62. Post the ma test funding: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_fund.xml' -X POST 'http://api.qa.orcid.org/v1.2/[orcid id]/funding' -L -i -k
    ```
63. Check that the funding appears at https://qa.orcid.org/my-orcid

64. Update the ma test funding: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_fund2.xml' -X PUT 'http://api.qa.orcid.org/v1.2/[orcid id]/funding' -L -i -k
    ```

65. Check that the funding item is updated and the manually added funding is not affected at https://qa.orcid.org/my-orcid

70. Post the ma test education: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_edu.xml' -X POST 'http://api.qa.orcid.org/v1.2/[orcid id]/affiliations' -L -i -k
    ```

71. Check that the education appears at https://qa.orcid.org/my-orcid


72. Update the ma test education: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_edu2.xml' -X PUT 'http://api.qa.orcid.org/v1.2/[orcid id]/affiliations' -L -i -k
    ```

73. Check that the education is updated and the manually added education is not affected at https://qa.orcid.org/my-orcid

74. Update the biography:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_bio.xml' -X PUT 'http://api.qa.orcid.org/v1.2/[orcid id]/orcid-bio' -L -i -k
    ```
75. Check that the researcher URL, keyword, and identifier are added and the manually added biography items are not affected at https://qa.orcid.org/my-orcid

76. Go to https://qa.orcid.org/account and revoke the permission

77. Check that the token no longer works by attempting to post a work. Check that you get a 401 Unauthorized error with the message "Invalid access token..."
    
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_work.xml' -X POST 'http://api.qa.orcid.org/v1.2/[orcid id]/orcid-works' -L -i -k
    ```

78. Create a webhook for this record. Check that a 201 Created message is returned.

    ```
    curl -i -H 'Accept: application/json' -H 'Authorization: Bearer ce0fc237-1f27-4e6e-8007-20b51ad595b9' -H "Content-Length: 0" -X PUT 'http://api.qa.orcid.org/[orcid id]/webhook/http%3A%2F%2Fnowhere2.com%2Fupdated' -k
    ```
            
## Member API 2.0 Post/Update/Notifications

79. Log into the account created for testing today if you are not already

80. Go to https://qa.orcid.org/oauth/authorize?client_id=[client id]&response_type=code&scope=/read-limited /activities/update /orcid-bio/update&redirect_uri=https://developers.google.com/oauthplayground

81. Grant long lived authorization

82. Exchange the authorization code: 

    ```
    curl -i -L -H 'Accept: application/json' --data 'client_id=[client id]&client_secret=[client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://qa.orcid.org/oauth/token' -k
    ```
    
83. Find and replace [2.0 token] in this document with the access token

84. Find and replace [2.0 refresh] in this document with the refresh token

### Browse to your local copy of https://github.com/ORCID/ORCID-Source/

**Sample xml files**

* orcid-integration-test/src/test/manual-test
* orcid-model/src/main/resources/record_2.0_rc2/samples

85. Post the ma test work 2: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' -d '@ma2_work.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work' -L -i -k
    ```
    
86. Check that the work appears at https://qa.orcid.org/my-orcid and is grouped with the manually added work. (You will need to click on the sources section of the manually added to work to see the version that was just posted)

87. Copy the put-code from the location header returned when the work was posted. Find and replace [put-code] in this document with the put-code

88. Update the work with JSON: 

    ```
    curl -H 'Content-Type: application/orcid+json' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/json' -X PUT 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work/[put-code]' -d '{
"put-code":"[put-code]",
"title": {"title": "API Test Title"},
"type": "JOURNAL_ARTICLE",
"external-ids": {
"external-id": [{
"external-id-value": "1234",
"external-id-type": "doi",
"external-id-relationship": "SELF"}]}}}' -L -i -k
    ```

89. Check that the work is updated and no longer grouped with the manually added work at https://qa.orcid.org/my-orcid

90. Delete the work:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' -X DELETE 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work/[put-code]' -L -i -k
    ```
91. Check that the work is no longer listed at https://qa.orcid.org/my-orcid

92. Post an education item:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' -d '@ma2_edu.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/education' -L -i -k
    ```

93. Post a funding item:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' -d '@ma2_fund2.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/funding' -L -i -k
    ```

94. Post a peer-review item:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' -d '@ma2_peer2.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/peer-review' -L -i -k
    ```

95. Check that the education, funding and peer-review item appear at https://qa.orcid.org/my-orcid
    
96. Post a keyword:
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' -d '@ma2_keyword.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/keywords' -L -i -k
    ```

97. Post a personal external identifier:
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' -d '@ma2_identifier.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/external-identifiers' -L -i -k
    ```
98. Check that the keyword and external identifier appear at https://qa.orcid.org/my-orcid and no other personal information was changed

99. Post a notification

    ```
    curl -i -H 'Authorization: Bearer eafafe49-b5bf-41db-9fb5-ad3a6cba575b' -H 'Content-Type: application/orcid+xml' -X POST -d '@notify.xml' https://api.qa.orcid.org/v2.0_rc2/[orcid id]/notification-permission -k
    ```
    
100. Go to https://qa.orcid.org/inbox

* Check that notification to add a work has posted
* Check that notifications from the previous updates have posted

100. Visit https://qa.orcid.org/signout	

## Member API 1.2 Creating/Claiming

101. Get a token to create records

    ```
    curl -i -L -H 'Accept: application/json' -d 'client_id=[client id]' -d 'client_secret=[client secret]' -d 'scope=/orcid-profile/create' -d 'grant_type=client_credentials' 'http://api.qa.orcid.org/oauth/token'
    ```
102. Find and replace [create token] in this document with the token you just generated

103. Post a new record with the 1.2 API

    ```
    curl -H 'Accept: application/xml' -H 'Content-Type: application/vdn.orcid+json' -H 'Authorization: Bearer [create token]'  'http://api.qa.orcid.org/v1.2/orcid-profile' -X POST -d '{
"message-version" : "1.2",
  "orcid-profile" : 
{"orcid-bio" : 
{"personal-details" : {
"given-names" : {"value" : "API created"},
"family-name" : {"value" : "[DD][month][YYYY]"}},
"biography" : {"value" : "Bio"},
"researcher-urls" : null,
"contact-details" : {
"email" : [ {"value" : "api_[DD][month][YYYY]@mailinator.com",
"primary" : true,
"current" : true}]},
"keywords" : {
"keyword" : [ {"value" : "keyword"}]},
"external-identifiers" : {
"external-identifier" : [ {
"external-id-common-name" : {"value" : "API added"},
"external-id-reference" : {"value" : "5"},
"external-id-url" : {"value" : "www.myid.com"}}]}},
"orcid-activities" : {
"orcid-works" : {
"orcid-work" : [ {
"work-title" : {
"title" : {"value" : "ma_test_work"}},
"work-type" : "JOURNAL_ARTICLE",
"work-external-identifiers" : {
"work-external-identifier" : [ {
"work-external-identifier-type" : "DOI",
"work-external-identifier-id" : {"value" : "9999"}}]}}]}}}}' -k -i
    ```

104. Find the ORCID iD of the new record in the response. Find and replace [new id] in this documente with that iD

105. View the newly created record in the UI at http://qa.orcid.org/[new id]. Check that no information is public and the record reads 'Reserved for claim' 

106. Read the newly created record with the API and check that no information is returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [public token]' -X GET 'http://pub.qa.orcid.org/v1.2/[new id]/orcid-profile' -L -i -k
    ```
107. Check the email inbox used when creating the record, api_[DD][month][YYYY]@mailinator.com, and follow the link to claim the record

108. Complete the steps to claim the record
    * Password: [DD][month][YYYY]
    * Default privacy for new activities: Limited
    
109. Check that a pop-up comes up asking you to grant permission to the client that created the record. Click Deny

109. Check the record is populated with personal information that is set to limited, and there is one work on the record

110. Try to post to the record using the create token. You should get a 401 Forbidden error with the message "Security problem : You cannot update this profile as it has been claimed, or you are not the owner."

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [create token]' -H 'Accept: application/xml' -d '@ma_work.xml' -X POST 'http://api.qa.orcid.org/v1.2/[new id]/orcid-works' -L -i -k
    ```
    
111. Try to read the record with the create token. Check that the personal information set to limited visibility is not returned.

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [create token]' -H 'Accept: application/xml' 'http://api.qa.orcid.org/v1.2/[new id]/orcid-profile' -L -i -k
    ```

### Check that errors are returned when expected

In this section all calls are expected to fail.

112. Attempt to access the wrong record in 1.2. Check that an 401 Unauthorized error is returned with the message "Invalid access token..."
    
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_work.xml' -X POST 'http://api.qa.orcid.org/v1.2/0000-0002-2619-0514/orcid-works' -L -i -k
    ```
    
113. Attempt to access the wrong record in 2.0_rc2. Check that an 401 Unauthorized error is returned with the message "Invalid access token..."
    
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [1.2 token]' -H 'Accept: application/xml' -d '@ma_work.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/0000-0002-2619-0514/orcid-works' -L -i -k
    ```


114. Attempt to post to a record without a token. Check that a 403 Forbidden error is returned.

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Accept: application/xml' -d '@ma2_work.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work' -L -i -k
    ```

115. Attempt to update an item without a token.  Check that a 403 Forbidden error is returned.

    ```
    curl -H 'Content-Type: application/orcid+json' -H 'Accept: application/json' -X PUT 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work/[put-code]' -d '{
"put-code":"[put-code]",
"title": {"title": "API Test Title"},
"type": "JOURNAL_ARTICLE",
"external-ids": {
"external-id": [{
"external-id-value": "1234",
"external-id-type": "doi",
"external-id-relationship": "SELF"}]}}}' -L -i -k
```

116. Attempt to update an item you are not the source of. (If these directions have been followed exactly you can replace the given put-code in both the call and the JSON with one that is three less and it will be for a work that was created manually). Check that a 403 Forbidden error is returned.

    ```
    curl -H 'Content-Type: application/orcid+json' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/json' -X PUT 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work/[put-code]-3' -d '{
"put-code":"[put-code]-3",
"title": {"title": "API Test Title"},
"type": "JOURNAL_ARTICLE",
"external-ids": {
"external-id": [{
"external-id-value": "1234",
"external-id-type": "doi",
"external-id-relationship": "SELF"}]}}}' -L -i -k
    ```
    
117. Check the deny access process, by going to the following URL and clicking Deny.
https://qa.orcid.org/oauth/authorize?client_id=[client id]&response_type=code&scope=/read-limited /activities/update /orcid-bio/update&redirect_uri=https://developers.google.com/oauthplayground
Check that you are sent to https://developers.google.com/oauthplayground with the error 'access_denied'

## Refresh tokens

118. Generate a new token with the same scopes and expiration as the 2.0 token and do not revoke the original token

    ```
    curl -i -L -k -H 'Authorization: Bearer [2.0 token]' -d 'refresh_token=[2.0 refresh]' -d 'grant_type=refresh_token' -d 'client_id=[client id]' -d 'client_secret=[client secret]' -d 'revoke_old=false' https://qa.orcid.org/oauth/token
    ```

119. Use the newly generated refreshed token to post the test work

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [refreshed token]' -H 'Accept: application/xml' -d '@ma2_work.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work' -L -i -k
    ```

120. Check that the original token still works by reading the record

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/record' -L -i -k
    ```

121. Generate a new token with a short lifespan, only /read-limited scope and revoke the original token    

    ```
    curl -i -L -k -H 'Authorization: Bearer [2.0 token]' -d 'refresh_token=[2.0 refresh]' -d 'grant_type=refresh_token' -d 'client_id=[client id]' -d 'client_secret=[client secret]' -d 'revoke_old=true' -d 'scope=/read-limited' -d 'expires_in=600' https://qa.orcid.org/oauth/token
    ```

122. Find and replace [refreshed2] in this document with the token you just generated

123. Check the new token can't be used to post items by attempting to post the test work. A 403 Forbidden error with the message "Insufficient or wrong scope [/read-limited]" should be returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [refreshed2]' -H 'Accept: application/xml' -d '@ma2_work.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work' -L -i -k
    ```

124. Check the original token was revoked by attempting to post the test work. A 401 Unauthorized error with the message "Invalid access token...: should be returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [2.0 token]' -H 'Accept: application/xml' -d '@ma2_work.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/work' -L -i -k
    ```

125. Check that the new token can be used to read the record. The full record should be returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [refreshed2]' -H 'Accept: application/xml' 'https://api.qa.orcid.org/v2.0_rc2/[orcid id]/record' -L -i -k
    ```

126. Check that you can't generate a refresh token using a revoked token. You should get an error message "Parent token is disabled"

    ```
    curl -i -L -k -H 'Authorization: Bearer [1.2 token]' -d 'refresh_token=[1.2 refresh]' -d 'grant_type=refresh_token' -d 'client_id=[client id]' -d 'client_secret=[client secret]' -d 'revoke_old=false' https://qa.orcid.org/oauth/token
    ```

## Privacy Check

### Public Record

This record has every field set to public. Access this record via the API and UI to check that all fields are returned.

127. Visit http://qa.orcid.org/0000-0002-3874-7658

128. Check that the following fields are shown
 * Name
 * Also known as
 * County
 * Keywords
 * Websites
 * Email
 * Other iDs
 * Biography

129. Check that there is one item visible in each of the following sections
 * Education
 * Employment
 * Funding
 * Peer-Review

130. Read with Public API 1.2. Check that the record is returned.

    ```
    curl -H 'Accept: application/orcid+xml' 'http://pub.qa.orcid.org/v1.2/0000-0002-3874-7658/orcid-profile' -L -i -k
    ```

131. Read with Public API 2.0_rc2. Check that the record is returned
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer a8ac4d85-df2b-4de2-9411-1b94491f463b' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0002-3874-7658/record' -L -i -k
    ```

132. Read with Member API 1.2. Check that the record is returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer eba7892b-4f4a-4651-9c47-f0c74fae61c5' -X GET 'https://api.qa.orcid.org/v1.2/0000-0002-3874-7658/orcid-profile' -L -i -k
    ```

133. Read with Member API 2.0_rc2 record. Check that all fields are returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer eba7892b-4f4a-4651-9c47-f0c74fae61c5' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0002-3874-7658/record' -L -i -k
    ```


134. Read with Member API 2.0_rc2 person. Check that personal information fields are returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer eba7892b-4f4a-4651-9c47-f0c74fae61c5' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0002-3874-7658/person' -L -i -k
    ```


135. Read with Member API 2.0_rc2 activities. Check that affiliations, funding, peer-review and work summaries are returned
 
    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 0658713c-5b6d-4fa4-a3da-73db9c7ab16c' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0002-3874-7658/activities' -L -i -k
    ```


### Limited Record

This record has ever field set to limited, check that nothing is visible in the UI and that it can only be read on the API with a /read-limited token.

136. Visit http://qa.orcid.org/0000-0001-7325-5491

137. Check that no information is displayed other than the ORCID iD

138. Read with Public API 1.2. Check that the bio and activities sections are not returned

    ```
    curl -H 'Accept: application/orcid+xml' 'http://pub.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```


139. Read the record with Public API 2.0_rc2. Check that the bio and activities sections are not returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 80e4aa5a-6ccc-44b3-83bb-3d9e315cda22' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```


140. Read a single work with Public API 2.0_rc2. Check that you get a 401 Unauthorized error with the message "The activity is not public"

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 80e4aa5a-6ccc-44b3-83bb-3d9e315cda22' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/work/142043' -L -i -k
    ```
    
141. Read email with Public API 2.0_rc2. Check that no content is retruned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 80e4aa5a-6ccc-44b3-83bb-3d9e315cda22' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/email' -L -i -k
    ```


142. Read the record with /read-public token API 1.2. Check that the bio and activities sections are not returned

    ```
    curl -H 'Accept: application/orcid+xml' -H 'Authorization: Bearer ba290a09-b757-4583-a5af-bd55d7087467' -X GET 'http://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```

143. Read record with /read-public token API 2.0_rc2. Check that the bio and activities sections are not returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer ba290a09-b757-4583-a5af-bd55d7087467' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```

144. Read a single work with /read-public token 2.0_rc2. Check that you get a 401 Unauthorized error with the message "The activity is not public"

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer ba290a09-b757-4583-a5af-bd55d7087467' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/work/142043' -L -i -k
    ```

145. Read email with /read-public token API 2.0_rc2. Check that no content is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer ba290a09-b757-4583-a5af-bd55d7087467' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/email' -L -i -k
    ```

146. Read with revoked access token  1.2. Check that an 401 Unauthorized error is returned with the message "Invalid access token..."

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 63409312-5ef6-4051-988c-f33b0fcea09f' -X GET 'https://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```

147. Read with revoked access token  2.0_rc2. Check that an 401 Unauthorized error is returned with the message "Invalid access token..." 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 63409312-5ef6-4051-988c-f33b0fcea09f' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```

148. Read with an access token for another record 1.2. Check that an 403 Forbidden error is returned with the message "Security problem : You do not have the required permissions." 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 2283056e-6a4a-4c80-b3a0-beaa102161d0' -X GET 'https://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```

149. Read record with access token for another record 2.0_rc2. Check that a 401 Unauthorized error is returned with the message "Access token is for a different record"

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 2283056e-6a4a-4c80-b3a0-beaa102161d0' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```

150. Read a single work with an access token for another record 2.0_rc2. Check that you get a 401 Unauthorized error with the message "The activity is not public"

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 2283056e-6a4a-4c80-b3a0-beaa102161d0' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/work/142043' -L -i -k
    ```

151. Read email with access token for another record 2.0_rc2. Check that nothing is returned.

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 2283056e-6a4a-4c80-b3a0-beaa102161d0' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/email' -L -i -k
    ```

152. Read record with access token with activities/update scope 1.2. Check that no content is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 064e64ef-6c49-4634-b09b-a38d8d75c774' -X GET 'https://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```

153. Read record with access token with activities/update scope 2.0_rc2. Check that no content is returned.

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 064e64ef-6c49-4634-b09b-a38d8d75c774' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```

154. Read a single work with activities/update 2.0_rc2. Check that you get a 401 Unauthorized error with the message "The activity is not public" (Currently broken - information is returned)

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 064e64ef-6c49-4634-b09b-a38d8d75c774' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/work/142043' -L -i -k
    ```

155. Read with access token with orcid-profile/create scope 1.2. Check that no content is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 262192b4-3dac-4d29-9897-b02823ac3618' -X GET 'https://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```

156. Read record with access token with orcid-profile/create scope 2.0_rc2. Check that you get a 401 Unauthorized error with the message "Incorrect token for claimed record"

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 262192b4-3dac-4d29-9897-b02823ac3618' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```

157. Read a single work with access token with orcid-profile/create scope 2.0_rc2. Check that you get a 401 Unauthorized error with the message "Incorrect token for claimed record"

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 262192b4-3dac-4d29-9897-b02823ac3618' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/work/142043' -L -i -k
    ```

158. Read email section with access token with orcid-profile/create scope 2.0_rc2. Check that you get a 401 Unauthorized error with the message "Incorrect token for claimed record"

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 262192b4-3dac-4d29-9897-b02823ac3618' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/email' -L -i -k
    ```

159. Read the record with a working token 1.2. Check that all fields are returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 1fcda8a0-1af3-4b35-8825-e4c53dae8953' -X GET 'https://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```

160. Read record with a working token 2.0_rc2. Check that all fields are returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 1fcda8a0-1af3-4b35-8825-e4c53dae8953' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```

161. Read a single work with a working token 2.0_rc2. Check that the work is returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 1fcda8a0-1af3-4b35-8825-e4c53dae8953' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/person' -L -i -k
    ```

162. Read email with a working token 2.0_rc2. Check that the email address is returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 1fcda8a0-1af3-4b35-8825-e4c53dae8953' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/email' -L -i -k
    ```	

### Private Record

This record has every fields set to private. Check that no information is displayed in the UI or via the API when accessing it.

163. Visit http://qa.orcid.org/0000-0003-2366-2712

164. Check that no information is displayed other than the ORCID iD

165. Read with public API 1.2. Check that no content is returned

    ```
    curl -H 'Accept: application/orcid+xml' 'http://pub.qa.orcid.org/v1.2/0000-0003-2366-2712/orcid-works' -L -i -k
    ```

166. Read record with public API 2.0_rc2. Check that no content is returned

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 80e4aa5a-6ccc-44b3-83bb-3d9e315cda22' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/record' -L -i -k
    ```

167. Read record with /read-limited token API 1.2. Check that no content is returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v1.2/0000-0003-2366-2712/orcid-profile' -L -i -k
    ```

168. Read record with /read-limited token API 2.0_rc2. Check that no content is returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/record' -L -i -k
    ```

169. Read person with /read-limited token API 2.0_rc2. Check that no content is returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/person' -L -i -k
    ```

170. Read activities with /read-limited token API 2.0_rc2. Check that no content is returned 

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/activities' -L -i -k
    ```

171. Read email with /read-limited token API 2.0_rc2. Check that no content is returned

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/email' -L -i -k
    ```

## Scopes/Methods

This section checks that clients can only get access based on the allowed scopes for that client type.

172. Attempt to get a /read-limited token with a public client at this should take you to the redirect URI with an Invalid scope error
  https://qa.orcid.org/oauth/authorize?client_id=[public client id]&response_type=code&scope=/read-limited&redirect_uri=https://developers.google.com/oauthplayground


173. Attempt to get an /read-limited token via 2 step OAuth. Check a 401 Unauthorized error is returned

    ```
    curl -i -L -H 'Accept: application/json' -d 'client_id=[public client id]' -d 'client_secret=[public client secret]' -d 'scope=/read-limited' -d 'grant_type=client_credentials' 'http://pub.qa.orcid.org/oauth/token'
    ```

174. Attempt to get an /activities/update token via 2 step. Check a 401 Unauthorized error is returned

    ```
    curl -i -L -H 'Accept: application/json' -d '[client id]' -d 'client_secret=[client secret]' -d 'scope=/activities/update' -d 'grant_type=client_credentials' 'http://api.qa.orcid.org/oauth/token'

    ```

175. Attempt to get a /webhooks token with a basic client. Check a 401 Unauthorized error is returned

    ```
    curl -i -L -H 'Accept: application/json' -d '[client id]' -d 'client_secret=[client secret]' -d 'scope=/web-hook' -d 'grant_type=client_credentials' 'http://api.qa.orcid.org/oauth/token'
    ```

176. Attempt to get a /orcid-profile/create token with a non-institution client. Check a 401 Unauthorized error is returned

    ```
    curl -i -L -H 'Accept: application/json' -d '[client id]' -d 'client_secret=[client secret]' -d 'scope=/orcid-profile/create' -d 'grant_type=client_credentials' 'http://api.qa.orcid.org/oauth/token'
    ```


* Finally help out by improving these instructions!      
   
