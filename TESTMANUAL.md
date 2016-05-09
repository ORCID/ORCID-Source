# Manual Testing
Steps to be completed before each release

##User Interface

###Register/Verify/Signin
1. Visit https://qa.orcid.org/register
2. Create new account:
    * First name: ma_test
    * Last name: [DD][month][YYYY] (ex: 24feb2016)
    * Email: ma_test_[DD][month][YYYY]@mailinator.com (ex: ma_test_24feb2016@mailinator.com)
    * Password: generate random password using [LastPass generator](https://lastpass.com/generatepassword.php) or similar
    * Default privacy for new activities: Public
    * Email frequency: Weekly summary
3. Visit https://qa.orcid.org/signout
4. Visit http://mailinator.com and check ma_test_[DD][month][YYYY]@mailinator.com
5. Open message from support@verify.orcid.org with subject [ORCID] Thanks for creating an ORCID iD 
6. Click (or copy/paste) email verification link
7. When redirected to https://qa.orcid.org/signin, sign in using ma_test credentials created in previous steps

###My-ORCID
1. Visit https://qa.orcid.org/my-orcid
2. Add a published name: Published Name
3. Add an also know as name: Other Name
4. Add a country: US
5. Add a keyword: keyword
6. Add a Researcher URL: URL https://qa.orcid.org, Description: "ORCID"
7. Add a biography: Bio
8. Add an education item: Institution "ORCID" (select from dropdown list)
9. Add a funding item: type "grant", title "ma fund test", funding agency "Wellcome Trust" (select from dropdown list)
10. Add a work: type: "journal article", title "ma test work", identifier type "DOI", identifier value “9999”

###Account settings
1. Visit https://qa.orcid.org/account
2. Ensure email ma_test_[DD][month][YYYY]@mailinator.com is marked as private
3. Add new email ma_test_[DD][month][YYYY]_01@mailinator.com and mark as public

###Verify Public ORCID record
1. Visit public page (http://qa.orcid.org/[XXXX-XXXX-XXXX-XXXX])
2. Verify ma_test_[DD][month][YYYY]@mailinator.com is not visible
3. Verify ma_test_[DD][month][YYYY]_01@mailinator.com is visible
4. Verify all the fields added to my-ORCID are visible
5. Verify the source of all fields is correctly recorded as Published Name
5. Visit https://qa.orcid.org/signout

###Reset password
1. Visit https://qa.orcid.org/reset-password and reset the password for ma_test_[DD][month][YYYY]@mailinator.com
2. Visit http://mailinator.com and check ma_test_[DD][month][YYYY]@mailinator.com
3. Open message from reset@notify.orcid.org with subject [ORCID] About your password reset request 
4. Click (or copy/paste) password reset link
5. Reset password with random value generated using [LastPass generator](https://lastpass.com/generatepassword.php) or similar
6. Visit https://qa.orcid.org/signin and sign in using the new password created in the previous steps

##API
###Public Authenticate
1. Go to https://qa.orcid.org/oauth/authorize?client_id=[public client id]&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground

2. Register for a new account and grant authorization
    * First name: ma_public_test
    * Last name: [DD][month][YYYY] (ex: 24feb2016)
    * Email: ma_public_test_[DD][month][YYYY]@mailinator.com (ex: ma_test_24feb2016@mailinator.com)
    * Password: generate random password using [LastPass generator](https://lastpass.com/generatepassword.php) or similar
    * Default privacy for new activities: Private
    * Email frequency: Never

3. Exchange the authorization code: 

    ```
    curl -i -L -H 'Accept: application/json' --data 'client_id=[public client id]&client_secret=[public client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://qa.orcid.org/oauth/token' -k
    ```

### Public Read/Search 1.2
1. Generate a read-public token:

    ```
    curl -i -L -H 'Accept: application/json' -d 'client_id=[public client id]' -d 'client_secret=[public client secret]' -d 'scope=/read-public' -d 'grant_type=client_credentials' 'http://pub.qa.orcid.org/oauth/token'
    ```

2. Search for the new record you created:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [public token]' 'Accept: application/xml' 'https://pub.qa.orcid.org/v1.2/search/orcid-bio/?q=family-name:[DD][month][YYYY]'
    ```

3. Read the record:
 
    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [public token]' -X GET 'http://pub.qa.orcid.org/v1.2/[ma id2]/orcid-profile' -L -i -k
    ```

4. Read the record without a version: 

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [public token]' -X GET 'http://pub.qa.orcid.org/[ma id2]/orcid-profile' -L -i -k
    ```

5. Read the record without an access token: 

    ```
    curl -H 'Content-Type: application/xml' 'http://pub.qa.orcid.org/v1.2/[ma id2]/orcid-profile' -L -i -k
    ```

###Member 1.2
1. Go to https://qa.orcid.org/oauth/authorize?client_id=[client id]&response_type=code&scope=/orcid-works/create /orcid-works/update /affiliations/create /affiliations/update /funding/create /funding/update /orcid-profile/read-limited /orcid-bio/update&redirect_uri=https://developers.google.com/oauthplayground

2. Log into the account created for testing today and grant short lived authorization

3. Exchange the authorization code:
 
    ```
    curl -i -L -H 'Accept: application/json' --data 'client_id=[client id]&client_secret=[client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://api.qa.orcid.org/oauth/token' -k
    ```

4. Post the ma test work:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 1.2 token]' -H 'Accept: application/xml' -d '@/ma_work.xml' -X POST 'http://api.qa.orcid.org/v1.2/[ma id]/orcid-works' -L -i -k
    ```

5. Update the works with: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 1.2 token]' -H 'Accept: application/xml' -d '@/ma_work2.xml' -X PUT 'http://api.qa.orcid.org/v1.2/[ma id]/orcid-works' -L -i -k
    ```

6. Check that the work is updated and the manually added work is not affected

7. Post the ma test funding: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 1.2 token]' -H 'Accept: application/xml' -d '@/ma_fund.xml' -X POST 'http://api.qa.orcid.org/v1.2/[ma id]/funding' -L -i -k
    ```

8. Update funding with: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 1.2 token]' -H 'Accept: application/xml' -d '@/ma_fund2.xml' -X PUT 'http://api.qa.orcid.org/v1.2/[ma id]/funding' -L -i -k
    ```

9. Check that the funding item is updated and the manually added funding is not affected

10. Post the ma test education: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 1.2 token]' -H 'Accept: application/xml' -d '@/ma_edu.xml' -X POST 'http://api.qa.orcid.org/v1.2/[ma id]/affiliations' -L -i -k
    ```

11. Update education: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 1.2 token]' -H 'Accept: application/xml' -d '@/ma_edu2.xml' -X PUT 'http://api.qa.orcid.org/v1.2/[ma id]/affiliations' -L -i -k
    ```

12. Check that the education is updated and the manually added education is not affected

13. Read the biography:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [bio 1.2 token]' 'http://api.qa.orcid.org/v1.2/[ma id]/orcid-bio' -L -i -k
    ```

14. Update the biography:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [bio 1.2 token]' -H 'Accept: application/xml' -d '@/ma_bio.xml' -X PUT 'http://api.qa.orcid.org/v1.2/[ma id]/orcid-bio' -L -i -k
    ```

13. Attempt to access the wrong record:
    
    ```
     curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 1.2 token]' -H 'Accept: application/xml' -d '@/ma_work.xml' -X POST 'http://api.qa.orcid.org/[ma id 2]/orcid-works' -L -i -k
    ```
Check that this fail

14. Attempt to post to a record without a token:

```
curl -H 'Content-Type: application/orcid+xml' -H 'Accept: application/xml' -d '@/ma_work.xml' -X POST 'http://api.qa.orcid.org/v1.2/[ma id]/orcid-works' -L -i -k
```

Check that this fails

15. Wait 1 hour Run the call (or use the UI to revoke the token): 
    
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 1.2 token]' -H 'Accept: application/xml' -d '@/ma_work.xml' -X POST 'http://api.qa.orcid.org/v1.2/[ma id]/orcid-works' -L -i -k
    
    ```
This should fail

16. Visit https://qa.orcid.org/signout

###Member 2.0
1. Log into the account created for testing today 

2. Go to https://qa.orcid.org/oauth/authorize?client_id=[client id]&response_type=code&scope=/read-limited /activities/update /orcid-bio/update&redirect_uri=https://developers.google.com/oauthplayground

3. Grant long lived authorization

4. Exchange the authorization code: 

    ```
    curl -i -L -H 'Accept: application/json' --data 'client_id=[client id]&client_secret=[client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://api.qa.orcid.org/oauth/token' -k
    ```

5. Post the ma test work 2: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 2.0 token]' -H 'Accept: application/xml' -d '@/ma2_work.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[ma id]/work' -L -i -k
    ```

6. Update the work: 

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 2.0 token]' -H 'Accept: application/xml' -d '@/ma2_work2.xml' -X PUT 'https://api.qa.orcid.org/v2.0_rc2/v2.0_rc2/[ma id]/work/[put-code]' -L -i -k
    ```

7. Read the work:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 2.0 token]' -H 'Accept: application/xml' 'https://api.qa.orcid.org/v2.0_rc2/[ma id]/work/[put-code]' -L -i -k
    ```

8. Delete the work:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 2.0 token]' -H 'Accept: application/xml' -X DELETE 'https://api.qa.orcid.org/v2.0_rc2/[ma id]/work/[put-code]' -L -i -k
    ```

9. Post an employment item:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 2.0 token]' -H 'Accept: application/xml' -d '@/ma2_employ.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[ma id]/employment' -L -i -k
    ```

10. Post a funding item:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 2.0 token]' -H 'Accept: application/xml' -d '@/ma2_fund.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[ma id]/funding' -L -i -k
    ```

11. Post a peer-review item:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 2.0 token]' -H 'Accept: application/xml' -d '@/ma2_peer.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[ma id]/peer-review' -L -i -k
    ```
    
12. Attempt to post to a record without a token
	
	```
	curl -H 'Content-Type: application/orcid+xml' -H 'Accept: application/xml' -d '@/ma2_work.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[ma id]/work' -L -i -k
	```
check that this fails

13. Attempt to update and item without a token

	```
    curl -H 'Content-Type: application/orcid+xml' -H 'Accept: application/xml' -d '@/ma2_work2.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/v2.0_rc2/[ma id]/work/[put-code]' -L -i -k
    ```
    
check that this fails

14. Attempt to update an item you are not the source of

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 2.0 token]' -H 'Accept: application/xml' -d '@/ma2_work2.xml' -X PUT 'https://api.qa.orcid.org/v2.0_rc2/v2.0_rc2/[ma id]/work/[manually-added-work-put-code (usually put-code from above -1)]' -L -i -k
    ```
    
check that this fails

##Privacy Check
###Check Public record
1. Visit http://qa.orcid.org/0000-0002-3874-7658

2. Check that the following fields are shown
 * Name
 * Also known as
 * County
 * Keywords
 * Websites
 * Email
 * Other iDs
 * Biography

3. Check that there is one item visible in each of the following sections
 * Education
 * Employment
 * Funding
 * Peer-Review

4. Read with Public API 1.2:

    ```
     curl -H 'Accept: application/orcid+xml' 'http://pub.qa.orcid.org/v1.2/0000-0002-3874-7658/orcid-works' -L -i -k
    ```
Check that the bio section is returned
Check that the activities section is returned with affiliations, funding, peer-review and works

5. Read with Public API 2.0_rc2:
 
    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [token]’ -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0002-3874-7658/activities' -L -i -k
    ```
Check that affiliation, funding, peer-review and works are returned

6. Read with Member API 1.2: 

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [token]’ -X GET 'https://api.qa.orcid.org/v1.2/0000-0002-3874-7658/orcid-profile' -L -i -k
    ```
Check that the bio section is returned
Check that affiliations, funding, peer-review and work sections are returned

7. Read with Member API 2.0_rc2 record:

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [token]’ -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0002-3874-7658/record' -L -i -k
    ```
Check that personal information fields are returned

7. Read with Member API 2.0_rc2 person:

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [token]’ -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0002-3874-7658/person' -L -i -k
    ```
Check that personal information fields are returned

8. Read with Member API 2.0_rc2 activities:
 
    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 0658713c-5b6d-4fa4-a3da-73db9c7ab16c' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0002-3874-7658/activities' -L -i -k
    ```
Check that affiliations, funding, peer-review and work summaries are returned

###Check Limited record
1. Visit http://qa.orcid.org/0000-0001-7325-5491

2. Check that no information is displayed other than the ORCID iD

3. Read with Public API 1.2:

    ```
    curl -H 'Accept: application/orcid+xml' 'http://pub.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```
Check that the bio and activities section are not returned

4. Read record with Public API 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 80e4aa5a-6ccc-44b3-83bb-3d9e315cda22' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```
Check that nothing is returned

5. Read activities with Public API 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 80e4aa5a-6ccc-44b3-83bb-3d9e315cda22' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/activities' -L -i -k
    ```
Check that nothing is returned

6. Read person with Public API 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 80e4aa5a-6ccc-44b3-83bb-3d9e315cda22' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/person' -L -i -k
    ```
Check that nothing is returned

7. Read email with Public API 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 80e4aa5a-6ccc-44b3-83bb-3d9e315cda22' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/email' -L -i -k
    ```
Check that nothing is returned

3. Read record with /read-public token API 1.2:

    ```
    curl -H 'Accept: application/orcid+xml' -H 'Authorization: Bearer ba290a09-b757-4583-a5af-bd55d7087467' -X GET 'http://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```
Check that the bio and activities section are not returned

4. Read record with /read-public token API 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer ba290a09-b757-4583-a5af-bd55d7087467' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```
Check that nothing is returned

5. Read activities with /read-public token 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer ba290a09-b757-4583-a5af-bd55d7087467' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/activities' -L -i -k
    ```
Check that nothing is returned

6. Read person with /read-public token 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer ba290a09-b757-4583-a5af-bd55d7087467' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/person' -L -i -k
    ```
Check that nothing is returned

7. Read email with /read-public token API 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer ba290a09-b757-4583-a5af-bd55d7087467' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/email' -L -i -k
    ```
Check that nothing is returned

8. Read with revoked access token  1.2 (should fail):

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 63409312-5ef6-4051-988c-f33b0fcea09f' -X GET 'https://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```
Check that an error message is returned

8. Read record with revoked access token  2.0_rc2 (should fail):

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 63409312-5ef6-4051-988c-f33b0fcea09f' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```
Check that an error message is returned

9. Read with an access token for another record 1.2 (should fail):

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 2283056e-6a4a-4c80-b3a0-beaa102161d0' -X GET 'https://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```
Check that an error is returned

10. Read record with access token for another record 2.0_rc2 (should fail):

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 2283056e-6a4a-4c80-b3a0-beaa102161d0' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```
Check that an error is returned

10. Read person with access token for another record 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 2283056e-6a4a-4c80-b3a0-beaa102161d0' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/person' -L -i -k
    ```
Check that nothing is returned

11. Read activities with access token for another record 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 2283056e-6a4a-4c80-b3a0-beaa102161d0' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/activities' -L -i -k
    ```
Check that nothing is returned

12. Read email with access token for another record 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 2283056e-6a4a-4c80-b3a0-beaa102161d0' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/email' -L -i -k
    ```
Check that nothing is returned


13. Read record with access token with activities/update scope 1.2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 064e64ef-6c49-4634-b09b-a38d8d75c774' -X GET 'https://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```
Check that nothing is returned

14. Read record with access token with activities/update scope 2.0_rc2 (should fail):

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 064e64ef-6c49-4634-b09b-a38d8d75c774' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```
Check that an error is returned

14. Read person with access token with activities/update scope 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 064e64ef-6c49-4634-b09b-a38d8d75c774' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/person' -L -i -k
    ```
Check that nothing is returned

15. Read activities with access token with activities/create scope 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 064e64ef-6c49-4634-b09b-a38d8d75c774' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/activities' -L -i -k
    ```
Check that nothing is returned

16. Read email with access token with activities/create scope 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 064e64ef-6c49-4634-b09b-a38d8d75c774' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/email' -L -i -k
    ```
Check that nothing is returned

17. Read with access token with orcid-profile/create scope 1.2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 262192b4-3dac-4d29-9897-b02823ac3618' -X GET 'https://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```
Check that nothing is returned

18. Read record with access token with orcid-profile/create scope 2.0_rc2 (should fail):

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 262192b4-3dac-4d29-9897-b02823ac3618' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```
Check that an error is returned

18. Read person with access token with orcid-profile/create scope 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 262192b4-3dac-4d29-9897-b02823ac3618' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/person' -L -i -k
    ```
Check that nothing is returned

19. Read activities with access token with orcid-profile/create scope 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 262192b4-3dac-4d29-9897-b02823ac3618' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/activities' -L -i -k
    ```
Check that nothing is returned

20. Read email section with access token with orcid-profile/create scope 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 262192b4-3dac-4d29-9897-b02823ac3618' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/email' -L -i -k
    ```
Check that nothing is returned


21. Read the record with a working token 1.2

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 1fcda8a0-1af3-4b35-8825-e4c53dae8953' -X GET 'https://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i -k
    ```
Check that the bio section is returned
Check that affiliations, funding, peer-review and work sections are returned

22. Read record with a working token 2.0_rc2

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 1fcda8a0-1af3-4b35-8825-e4c53dae8953' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/record' -L -i -k
    ```
Check that all fields are returned

22. Read person with a working token 2.0_rc2

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 1fcda8a0-1af3-4b35-8825-e4c53dae8953' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/person' -L -i -k
    ```
Check that personal information fields are returned

23. Read activities with a working token 2.0_rc2

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 1fcda8a0-1af3-4b35-8825-e4c53dae8953' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/activities' -L -i -k
    ```
Check that affiliations, funding, peer-review and work summaries are returned

24. Read email with a working token 2.0_rc2

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 1fcda8a0-1af3-4b35-8825-e4c53dae8953' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/email' -L -i -k
    ```
Check that the email address is returned

###Check Private record
1. Visit http://qa.orcid.org/0000-0003-2366-2712

2. Check that no information is displayed other than the ORCID iD

3. Read with public API 1.2:

    ```
    curl -H 'Accept: application/orcid+xml' 'http://pub.qa.orcid.org/v1.2/0000-0003-2366-2712/orcid-works' -L -i -k
    ```
Check that the bio and activities section are not returned

4. Read record with public API 2.0_rc2:

    ```
    curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 80e4aa5a-6ccc-44b3-83bb-3d9e315cda22' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/record' -L -i -k
    ```
Check that nothing is returned

5. Read record with /read-limited token API 1.2:

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v1.2/0000-0003-2366-2712/orcid-profile' -L -i -k
    ```
Check that nothing is returned

6. Read record with /read-limited token API 2.0_rc2:

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/record' -L -i -k
    ```
Check that nothing is returned

6. Read person with /read-limited token API 2.0_rc2:

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/person' -L -i -k
    ```
Check that nothing is returned

7. Read activities with /read-limited token API 2.0_rc2:

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/activities' -L -i -k
    ```
Check that nothing is returned in the affiliations, funding, peer-review, and works sections

6. Read email with /read-limited token API 2.0_rc2:

    ```
    curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/email' -L -i -k
    ```
Check that nothing is returned

###Check scopes and methods

1. Attempt to get a /read-limited token with a public client

```
Go to https://qa.orcid.org/oauth/authorize?client_id=[public client id]&response_type=code&scope=/read-limited&redirect_uri=https://developers.google.com/oauthplayground
```

2. Attempt to get an /activities/update token with a public client

```
Go to https://qa.orcid.org/oauth/authorize?client_id=[public client id]&response_type=code&scope=/activities/update&redirect_uri=https://developers.google.com/oauthplayground
```

3. Attempt to get a /read-limited token via 2 step OAuth

```
curl -i -L -H 'Accept: application/json' -d '[client id]' -d 'client_secret=[client secret]' -d 'scope=/read-limited' -d 'grant_type=client_credentials' 'http://api.qa.orcid.org/oauth/token'
```

4. Attempt to get an /activities/update token via 2 step

```
curl -i -L -H 'Accept: application/json' -d '[client id]' -d 'client_secret=[client secret]' -d 'scope=/activities/update' -d 'grant_type=client_credentials' 'http://api.qa.orcid.org/oauth/token'

```

5. Attempt to get a /webhooks token with a basic client

```
curl -i -L -H 'Accept: application/json' -d '[client id]' -d 'client_secret=[client secret]' -d 'scope=/web-hook' -d 'grant_type=client_credentials' 'http://api.qa.orcid.org/oauth/token'
```

6. Attempt to get a /orcid-profile/create token with a non-institution client

```
curl -i -L -H 'Accept: application/json' -d '[client id]' -d 'client_secret=[client secret]' -d 'scope=/orcid-profile/create' -d 'grant_type=client_credentials' 'http://api.qa.orcid.org/oauth/token'
```


* Finally help out by improving these instructions!      
   
