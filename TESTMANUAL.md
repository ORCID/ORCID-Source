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

###Account settings
1. Visit https://qa.orcid.org/account
2. Ensure email ma_test_[DD][month][YYYY]@mailinator.com is marked as private
3. Add new email ma_test_[DD][month][YYYY]_01@mailinator.com and mark as public
4. Visit public page (http://qa.orcid.org/[XXXX-XXXX-XXXX-XXXX])
      * Verify ma_test_[DD][month][YYYY]@mailinator.com is not visible
      * Verify ma_test_[DD][month][YYYY]_01@mailinator.com is visible
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
3. Exchange the authorization code with 
```
curl -i -L -H 'Accept: application/json' --data 'client_id=[public client id]&client_secret=[public client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://qa.orcid.org/oauth/token' -k
```

### Public Read/Search 1.2
1. Generate a read-public token with ```curl -i -L -H 'Accept: application/json' -d 'client_id=[public client id]' -d 'client_secret=[public client secret]' -d 'scope=/read-public' -d 'grant_type=client_credentials' 'http://pub.qa.orcid.org/oauth/token'
```
2. Search for the new record you created with 
	```curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [public token]' 'Accept: application/xml' 'https://api.qa.orcid.org/v1.2/search/orcid-bio/?q=family-name:[DD][month][YYYY]'```
3. Read the record with: 
```
curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [public token]' -X GET 'http://api.qa.orcid.org/v1.2/[ma id 2]/orcid-profile' -L -i
```
4. Read the record without a version: curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [public token]' -X GET 'http://api.qa.orcid.org/[ma id 2]/orcid-profile' -L -i
5. Read the record without an access token: curl -H 'Content-Type: application/xml' 'http://api.qa.orcid.org/v1.2/[ma id 2]/orcid-profile' -L -i
###Member


###Member Activities 1.2
1. Go to https://qa.orcid.org/oauth/authorize?client_id=[member client id]&response_type=code&scope=/orcid-works/create /orcid-works/update /affiliations/create /affiliations/update /funding/create /funding/update /orcid-profile/read-limited&redirect_uri=https://developers.google.com/oauthplayground
2. Log into the account created for testing today and grant short lived authorization
3. Exchange the authorization code with: curl -i -L -H 'Accept: application/json' --data 'client_id=[client id]&client_secret=[client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://api.qa.orcid.org/oauth/token'
3. Post the ma test work: curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 1.2 token]' -H 'Accept: application/xml' -d '@/ma_test_work.xml' -X POST 'http://api.qa.orcid.org/v1.2/[ma id]/orcid-works' -L -i
4. Update the works with: curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 1.2 token]' -H 'Accept: application/xml' -d '@/ma_test_work2.xml' -X POST 'http://api.qa.orcid.org/v1.2/[ma id]/orcid-works' -L -i
5. Check that the work is updated and the manually added work is not affected
6. Post the ma test funding: curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 1.2 token]' -H 'Accept: application/xml' -d '@/ma_test_fund.xml' -X POST 'http://api.qa.orcid.org/v1.2/[ma id]/funding' -L -i
7. Update funding with: curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 1.2 token]' -H 'Accept: application/xml' -d '@/ma_test_fund2.xml' -X POST 'http://api.qa.orcid.org/v1.2/[ma id]/funding' -L -i
8. Check that the funding item is updated and the manually added funding is not affected
9. Post the ma test education: curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 1.2 token]' -H 'Accept: application/xml' -d '@/ma_test_edu.xml' -X POST 'http://api.qa.orcid.org/v1.2/[ma id]/affiliations' -L -i
7. Update education with: curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 1.2 token]' -H 'Accept: application/xml' -d '@/ma_test_edu2.xml' -X POST 'http://api.qa.orcid.org/v1.2/[ma id]/affiliations' -L -i
8. Check that the education is updated and the manually added education is not affected
9. Attempt to access the wrong record with: curl curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 1.2 token]' -H 'Accept: application/xml' -d '@/ma_test_work.xml' -X POST 'http://api.qa.orcid.org/[ma id 2]/orcid-works' -L -i This should fail
10. Wait 1 hour. Run the call: curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 1.2 token]' -H 'Accept: application/xml' -d '@/ma_test_work.xml' -X POST 'http://api.qa.orcid.org/v1.2/[ma id]/orcid-works' -L -i This should fail

###Member Bio 1.2
1. Go to https://qa.orcid.org/oauth/authorize?client_id=[public client id]&response_type=code&scope=/orcid-bio/update&redirect_uri=https://developers.google.com/oauthplayground
2. Log into the account created for testing today and grant short lived authorization
3. Exchange the authorization code with: curl -i -L -H 'Accept: application/json' --data 'client_id=[client id]&client_secret=[client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://api.qa.orcid.org/oauth/token'
4. Read the biography with: curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [bio 1.2 token]' 'http://api.qa.orcid.org/v1.2/[ma id]/orcid-bio' -L -i
5. Update the biography with: curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [bio 1.2 token]' -H 'Accept: application/xml' -d '@/ma_test_bio.xml' -X POST 'http://api.qa.orcid.org/v1.2/[ma id]/orcid-bio' -L -i

###Member 2.0
1. Log into the account created for testing today 
2. Go to https://qa.orcid.org/oauth/authorize?client_id=[public client id]&response_type=code&scope=/read-limited /activities/update /orcid-bio/update&redirect_uri=https://developers.google.com/oauthplayground
2. Grant long lived authorization
3. Exchange the authorization code with: curl -i -L -H 'Accept: application/json' --data 'client_id=[client id]&client_secret=[client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://api.qa.orcid.org/oauth/token'
3. Post the ma test work 2: curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 2.0 token]' -H 'Accept: application/xml' -d '@/ma2.0_test_work.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[ma id]/work' -L -i
4. Update the work with: curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 2.0 token]' -H 'Accept: application/xml' -d '@/ma2.0_test_work2.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/v2.0_rc2/[ma id]/work/[put-code]' -L -i
5. Read the work with: curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 2.0 token]' -H 'Accept: application/xml' 'https://api.qa.orcid.org/v2.0_rc2/[ma id]/work/[put-code]' -L -i
6. Delete with work with curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 2.0 token]' -H 'Accept: application/xml' -X DELETE 'https://api.qa.orcid.org/v2.0_rc2/[ma id]/work/[put-code]' -L -i
7. Post an employment item with: curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 2.0 token]' -H 'Accept: application/xml' -d '@/ma2.0_test_employ.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[ma id]/employment' -L -i
8. Post a funding item with: curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 2.0 token]' -H 'Accept: application/xml' -d '@/ma2.0_test_fund.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[ma id]/funding' -L -i
9. Post a peer-review item with: curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [activities 2.0 token]' -H 'Accept: application/xml' -d '@/ma2.0_test_peer.xml' -X POST 'https://api.qa.orcid.org/v2.0_rc2/[ma id]/peer-review' -L -i

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
4. Call curl -H 'Accept: application/orcid+xml' 'http://pub.qa.orcid.org/v1.2/0000-0002-3874-7658/orcid-works' -L -i
 * Check that the bio section is returned
 * Check that the activities section is returned with affiliations, funding, peer-review and works
5. Call curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 80e4aa5a-6ccc-44b3-83bb-3d9e315cda22' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0002-3874-7658/activities' -L -i
 * Check that affiliation, funding, peer-review and works are returned
6. Call curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 0658713c-5b6d-4fa4-a3da-73db9c7ab16c' -X GET 'https://api.qa.orcid.org/v1.2/0000-0002-3874-7658/orcid-profile' -L -i
 * Check that the bio section is returned
 * Check that affiliations, funding, peer-review and work sections are returned
7. Call curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 0658713c-5b6d-4fa4-a3da-73db9c7ab16c' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0002-3874-7658/person' -L -i
 * Check that personal information fields are returned
8. Call curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 0658713c-5b6d-4fa4-a3da-73db9c7ab16c' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0002-3874-7658/activities' -L -i
 * Check that affiliations, funding, peer-review and work summaries are returned

###Check Limited record
1. Visit http://qa.orcid.org/0000-0001-7325-5491
2. Check that no information is displayed other than the ORCID iD
3. Call curl -H 'Accept: application/orcid+xml' 'http://pub.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-works' -L -i
 * Check that the bio and activities section are not returned
4. Call curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 80e4aa5a-6ccc-44b3-83bb-3d9e315cda22' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/activities' -L -i
 * Check that nothing is returned
5. Call curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 1fcda8a0-1af3-4b35-8825-e4c53dae8953' -X GET 'https://api.qa.orcid.org/v1.2/0000-0001-7325-5491/orcid-profile' -L -i
 * Check that the bio section is returned
 * Check that affiliations, funding, peer-review and work sections are returned
7. Call curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 1fcda8a0-1af3-4b35-8825-e4c53dae8953' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/person' -L -i
 * Check that personal information fields are returned
8. Call curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 1fcda8a0-1af3-4b35-8825-e4c53dae8953' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0001-7325-5491/activities' -L -i
 * Check that affiliations, funding, peer-review and work summaries are returned

###Check Private record
1. Visit http://qa.orcid.org/0000-0003-2366-2712
2. Check that no information is displayed other than the ORCID iD
3. Call curl -H 'Accept: application/orcid+xml' 'http://pub.qa.orcid.org/v1.2/0000-0003-2366-2712/orcid-works' -L -i
 * Check that the bio and activities section are not returned
4. Call curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 80e4aa5a-6ccc-44b3-83bb-3d9e315cda22' -X GET 'https://pub.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/activities' -L -i
 * Check that nothing is returned
5. Call curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v1.2/0000-0003-2366-2712/orcid-profile' -L -i
 * Check that nothing is returned
7. Call curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/person' -L -i
 * Check that nothing is returned
8. Call curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer 6ae41a5b-abf9-4922-bbb4-08ed8508b4ce' -X GET 'https://api.qa.orcid.org/v2.0_rc2/0000-0003-2366-2712/activities' -L -i
 * Check that nothing is returned in the affiliations, funding, peer-review, and works sections


* Finally help out by improving these instructions!      
   
