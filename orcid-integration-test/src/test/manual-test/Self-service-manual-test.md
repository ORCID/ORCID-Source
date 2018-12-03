## Test Self Service

### Before you start

For this test there is a fair amount of set up. You need to have a Consortium Lead account on QA and a Consortium Member account . For the purposes of testing you can use the accounts 0000-0003-0641-4661 for the consortium lead account and 0000-0001-5870-8499 for the consortium member account. For additional accounts USe the MA testing accounts you created when running throught the MA tests


Find and Replace [DD][month][YYYY] in this document with the current day, written month, and four digit year for example 24feb2016.

### Create an account to add as a contact
1. Visit https://qa.orcid.org/register create an account

2. Create new account:
		 First name: self-service
		 Last name: [DD][month][YYYY]
		 Email: self-service_[DD][month][YYYY]@mailinator.com (ex: self-service_2feb2016@mailinator.com)
		 Password: test1234
		 Default privacy for new activities: Everyone
		 Subscribe to quarterly emails about new features
		 Accept terms and conditions

### Test that Self-service is live and check consortium lead functionality

3. Go to https://qa.orcid.org/signin and sign in with
		0000-0001-5870-8499
		Password: test1234
4. Check that there is a tab 'MEMBER TOOLS'

5. Click on the tab

6. Check the page loads

7. Click on the consortium contact field and add the Account you created above by adding the email self-service_[DD][month][YYYY]@mailinator.com

8. Visit https://test.salesforce.com/ and sign in with your credentials

9. Visit the the org Rob_b Self Service Test Org by going to this url
 https://orcid--consortia.lightning.force.com/lightning/r/Account/0013D00000ZjwqYQAR/view

10. Check that the account you added above has been added to the Membership Contact Roles section for the Consortia Lead Org 'ROb_b Self Service Test Org'.

### Check adding a consortium member
11. Visit https://qa.orcid.org/self-service
12. Add a new consortium member:
			Organization Name: [DD][month][YYYY]
			First Name : Self_service
			Last Name: [DD][month][YYYY]
			Website : www.[DD][month][YYYY].com
			Email: consortium_member_[DD][month][YYYY]@mailinator.com

13. Visit mailinator.com and enter consortium_member_[DD][month][YYYY]@mailinator.com
		Check that there is an email titled "Consortium member addition requested - 20nov2018"

22. Visit www.qa.orcid.org/signout


### Check that consortium contact can  add, edit and delete  a contact and it updates in salesforce

**Create an account to add as a contact**

23. Visit https://qa.orcid.org/register create an account
		Create new account:
		First name: self-service
		Last name: [DD][month][YYYY]
		Email: self-service-contact_[DD][month][YYYY]@mailinator.com (ex: self-service-contact_21nov2018@mailinator.com)
		Password: test1234
		Default privacy for new activities: Everyone
		Subscribe to quarterly emails about new features
		Accept terms and conditions

24. Visit https://qa.orcid.org/signout
25. Visit https://qa.orcid.org/signin
		Sign in with
		0000-0001-5870-8499
		test1234
26. Go to https://qa.orcid.org/self-service
27. Check the page loads

28. Add a contact in the Add Member Contacts email field using the email you just created above. Click add.

29. Visit New Org 1 in Salesforce at https://orcid--consortia.lightning.force.com/lightning/r/Account/0013D00000dndAVQAY/view

30. Check that the contact you just added is added to Membership contact roles and Contacts.
31. Go to https://qa.orcid.org/self-service
32. Update the Contact you just added to a Technical Contact.
33. Visit 		https://orcid--consortia.lightning.force.com/lightning/r/Account/0013D00000dndAVQAY/view  and check the role you just changed has updated.


34. Go back to https://qa.orcid.org/self-service

35. Delete the role you just added by clicking on the trash can

36. Visit https://orcid--consortia.lightning.force.com/lightning/r/Account/0013D00000dndAVQAY/view and check the role you just changed is updated.

37. C'est Fini
