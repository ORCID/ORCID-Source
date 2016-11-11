How to add a new identifier type
================================

There are five steps:
- Add the database row
- Add the liquibase script to db-master.xml
- Add the description (optional)
- Add the user entered id link translation (optional)
- Update the test

Add the database row using a liquibase script in this directory.  
Example: See identifier-type.pdb.xml

Update orcid-persistence/src/main/resources/db-master.xml.  
Example: `<include file="/db/updates/identifier-type-pdb.xml" />`

Add the description (optional). Add a line to orcid-core/src/main/resources/i18n/messages_en.properties.  
Example: `org.orcid.jaxb.model.record.WorkExternalIdentifierType.wosuid=wosuid\: Web of Science™ identifier`

Add the user entered id link translation (optional). Add a function to the typeMap in /orcid-web/src/main/webapp/static/javascript/script.js 
xample:
	typeMap['kuid'] = function (id) {
		return 'http://koreamed.org/SearchBasic.php?RID=' + encodeURIComponent(id);
	};
	
Update the test. Add the new id to the list: 
`private List<String> v2Ids = Arrays.asList(new String[]{"pdb","kuid",”your-new-type”});`




