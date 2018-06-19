How to add a new identifier type
================================

There are five steps:
- Add the database row
- Add the liquibase script to db-master.xml
- Add the description (optional)
- Add it to a resolver for link checking (optional)
- Add a normalizer (optional)
- Add a link generator to the UI (optional)
- Add the user entered id link translation (optional)
- Update the test
- Add a normalizer if wanted

Add the database row using a liquibase script in this directory. 
(also add the primary_use column - either work or funding (for now) 
(also add the id_resolution_prefix column) 
Example: See identifier-type.pdb.xml

Update orcid-persistence/src/main/resources/db-master.xml.  
Example: `<include file="/db/updates/identifier-type-pdb.xml" />`

Add the description (optional). Add a line to orcid-core/src/main/resources/i18n/identifiers_en.properties.  
Example: `org.orcid.jaxb.model.record.WorkExternalIdentifierType.wosuid=wosuid\: Web of Science™ identifier`

Add the user entered id link translation (optional). Add a function to the typeMap in /orcid-web/src/main/webapp/static/javascript/script.js 
xample:
	typeMap['kuid'] = function (id) {
		return 'http://koreamed.org/SearchBasic.php?RID=' + encodeURIComponent(id);
	};
	
Update the org.orcid.core.manager.IdentifierTypeManagerTest test. Add the new id to the list: 
`private List<String> v2Ids = Arrays.asList(new String[]{"pdb","kuid",”your-new-type”});`

Add a Normalizer in the org.orcid.core.utils.v3.identifiers package if required

Add it to script.js (this will generate URLs in the UI when viewing if no URL has been entered for the id) (this should be changed to use server side logic!)
   typeMap['dnb'] = function (id) {
       return 'https://d-nb.info/' + encodeURIComponent(id);
   };

Solr indexing will happen automatically.





