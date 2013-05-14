# Creating a new OAuth Client for Development

Within the application exists a CLI tool to create new Orcid Clients, in fact the same tool that is used on dev sandbox and production servers to set up client credentials for use.

## Creating clients based on an XML input
The command line utility org.orcid.core.cli.ManageClientGroup provides the means to set up a new client, given a valid XML input file.

The class provides the following features:

* Displaying the xml for an existing client data group given an ORCID identifier.

     	-r <ORCID Identifier> 
     (where ORCID identifier exists in the profile table as orcid_type='GROUP')
     

* Validating a client data xml file against the orcid-client-group-version-x.xsd 

	 	-s  <input xml file> 
	 (the exact schema version is specified within the class file so needs to be kept in step with latest deployed client group schema)

* Adding a client group and generating an ORCID id, client id and secret, given an xml for a client data group given an ORCID identifier. 

	 	-t <Creator or updater client type> -f <input xml file> 
	The 'updater' client type will be the method used most often in development. A client app belong to a group of type updater can only alter existing Orcid records (although they can add elements to it).

* If you are creating a client that can in turn create orcid users, then the group must be of the creator type. See [this guide] (https://github.com/ORCID/ORCID-Internal/wiki/Orcid-Creation-via-OAuth) once you've run the ManageClientGroup util.
	 	
## Example:
	
This file shows an example of a valid Orcid Client Request:	
	
	<orcid-client-group xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 	xsi:schemaLocation="http://www.orcid.org/ns/orcid ../../../../orcid-model/src/main/resources/orcid-client-group-1.2.xsd"
 	xmlns="http://www.orcid.org/ns/orcid">
    	<group-name>Elsevier</group-name>
    	<email>orcid-admin@elsevier.com</email>
    	<orcid-client>
        	<display-name>Ecological Complexity</display-name>
        	<website>http://www.journals.elsevier.com/ecological-complexity</website>
        	<short-description>An International Journal on Biocomplexity in the Environment and Theoretical Ecology</short-description>
        	<redirect-uris>
            	<redirect-uri scope="/orcid-profile/create /orcid-bio/read-limited" type="default">http://www.journals.elsevier.com/ecological-complexity/orcid-callback</redirect-uri>
        	</redirect-uris>
    	</orcid-client>
    	<orcid-client>
        	<display-name>Ecological Economics</display-name>
        	<website>http://www.journals.elsevier.com/ecological-economics</website>
        	<short-description>The Transdisciplinary Journal of the International Society for Ecological Economics (ISEE)</short-description>
        	<redirect-uris>
            	<redirect-uri scope="/orcid-bio/read-limited" type="default">http://www.journals.elsevier.com/ecological-economics/orcid-callback</redirect-uri>
        	</redirect-uris>
    	</orcid-client>
	</orcid-client-group>
	
We can use the example that we use for integration testing as our reference. This lives at:

	/orcid-test/src/main/resources/orcid-client-group-request.xml

Which ultimately conforms to the client group xml schema at:
	
	/orcid-model/src/main/resources/orcid-client-group-1.2.xsd
	
With the client group sample we are adding:

	* A group name of 'Elsevier'
	* An email address for the administrator of the entire group
	* Two Orcid clients falling under the 'Elsevier' group. (We could have added as many as we wanted).
	
Consider the clients to be client apps, e.g. journals, publications, as opposed to an organisation or end users. 
Each client has its own, independent set of credentials:

Taking just the first element, we are attempting to create:

   * A display name of 'Ecological Complexity' - this will show in the web app as the third party requesting access on a users Orcid.
	* A website associated with the client
	* One or more redirect uris - where to return to once the user has granted access to the 3rd party.
	
Note that as per the OAuth 2 spec:

  * A client may set up than one redirect uri.
  * If a client has specified multiple redirect uris, then the 3rd party 'OAuth link' into Orcid must specify the redirect URI to return to (otherwise how would Orcid know which one to choose?)
  * If the client has set up only a single redirect, Orcid can retrieve the (only) uri itself.
  * In either scenario, the redirect URI must exist within the Orcid client_redirect-uri table against that client.  
		
		
A further note on the redirect uri - there can be zero or more pre-defined scopes associated with a client redirect:

 * When pre-defined scopes are specified as an attribute of a redirect uri, we are effectively jumping straight into the 'Create authorisation code' stage of the OAuth flow. 
 * Normally the user would be taken FROM the third party app, authenticate against Orcid and then choosing whether or to grant whichever scope(s) the third party app requested.
 * What this means in practice is that we can embed links to a third party client within (secured) pages within the Orcid webapp. By clicking the link, the user is given the option within Orcid to allow a third party access to parts of their profile.

##Using Maven to execute ManageClientGroup
1) export maven opts with path the properties files (contains db info)

      export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=256m -Dorg.orcid.config.file=file:///usr/local/orcid/webapps/conf/orcid.properties"

* cd into orcid-web

* mvn install

* run mvn exec:exec with arguments

      mvn -X exec:java -Dexec.mainClass="org.orcid.core.cli.ManageClientGroup" -Dexec.args="-f /home/rcpeters/tr_QA1_clients_local.xml -t CREATOR"s

## Client Response
An example response is given here, which provides the credentials you can set up the OAuth playground with (https://github.com/ORCID/ORCID-Internal/wiki/OAuth-Playground), or provide to an end user (https://github.com/ORCID/ORCID-Internal/wiki/Oauth-admin.md?.mdown).

	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <orcid-client-group xmlns="http://www.orcid.org/ns/orcid">
    <group-orcid>0000-0002-8005-9197</group-orcid>
    <group-name>Elsevier</group-name>
    <email>orcid-admin@elsevier.com</email>
    <orcid-client>
        <display-name>Ecological Economics</display-name>
        <website>http://www.journals.elsevier.com/ecological-economics</website>
        <short-description>The Transdisciplinary Journal of the International Society for Ecological Economics (ISEE)</short-description>
        <redirect-uris>
            <redirect-uri scope="/orcid-bio/read-limited" type="default">http://www.journals.elsevier.com/ecological-economics/orcid-callback</redirect-uri>
            <redirect-uri type="default">https://developers.google.com/oauthplayground</redirect-uri>
        </redirect-uris>
        <client-id>0000-0001-8306-1105</client-id>
        <client-secret>afbb50fc-1b3c-4f5a-ae9a-a3d9e8cb2946</client-secret>
    </orcid-client>
    <orcid-client>
        <display-name>Ecological Complexity</display-name>
        <website>http://www.journals.elsevier.com/ecological-complexity</website>
        <short-description>An International Journal on Biocomplexity in the Environment and Theoretical Ecology</short-description>
        <redirect-uris>
            <redirect-uri scope="/orcid-bio/read-limited /orcid-profile/create" type="default">http://www.journals.elsevier.com/ecological-complexity/orcid-callback</redirect-uri>
            <redirect-uri type="default">https://developers.google.com/oauthplayground</redirect-uri>
        </redirect-uris>
        <client-id>0000-0003-4680-8112</client-id>
        <client-secret>c2647373-52e2-4a24-aa40-90444e6f92f7</client-secret>
    </orcid-client>
</orcid-client-group>

## Add new clients to an existing group

Adding a new group to the client is as simple as adding an additional (valid) <orcid-client> element without a client id and secret to the file and re-running the ManageClientGroup util. The additional elements will have a client id and secret added, and the existing elements will remain unchanged. The entire file will be returned in the response.

E.g, here I add a third client app, Ecological Complexity 2, to the group with no client id and secret:

           <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <orcid-client-group xmlns="http://www.orcid.org/ns/orcid">
    <group-orcid>0000-0002-8144-4089</group-orcid>
    <group-name>Elsevier</group-name>
    <email>orcid-admin@elsevier.com</email>
    <orcid-client>
        <display-name>Ecological Economics</display-name>
        <website>http://www.journals.elsevier.com/ecological-economics</website>
        <short-description>The Transdisciplinary Journal of the International Society for Ecological Economics (ISEE)</short-description>
        <redirect-uris>
            <redirect-uri type="default">https://developers.google.com/oauthplayground</redirect-uri>
            <redirect-uri scope="/orcid-bio/read-limited" type="default">http://www.journals.elsevier.com/ecological-economics/orcid-callback</redirect-uri>
        </redirect-uris>
        <client-id>0000-0003-0501-3882</client-id>
        <client-secret>e0173f69-e26c-4c1f-83cc-526baf223b0f</client-secret>
    </orcid-client>
    <orcid-client>
        <display-name>Ecological Complexity</display-name>
        <website>http://www.journals.elsevier.com/ecological-complexity</website>
        <short-description>An International Journal on Biocomplexity in the Environment and Theoretical Ecology</short-description>
        <redirect-uris>
            <redirect-uri type="default">https://developers.google.com/oauthplayground</redirect-uri>
            <redirect-uri scope="/orcid-profile/create /orcid-bio/read-limited" type="default">http://www.journals.elsevier.com/ecological-complexity/orcid-callback</redirect-uri>
        </redirect-uris>
        <client-id>0000-0003-3664-1216</client-id>
        <client-secret>c8dde1e9-3e1d-4720-aec1-0830eae6cea4</client-secret>
    </orcid-client>
     <orcid-client>
            <display-name>Ecological Complexity 2</display-name>
            <website>http://www.journals.elsevier.com/ecological-complexity</website>
            <short-description>An International Journal on Biocomplexity in the Environment and Theoretical Ecology</short-description>
            <redirect-uris>                
                <redirect-uri scope="/orcid-profile/create /orcid-bio/read-limited" type="default">http://www.journals.elsevier.com/ecological-complexity/orcid-callback2</redirect-uri>
            </redirect-uris>            
    </orcid-client>
</orcid-client-group>

And I am returned the full listing as a response from running the util:

         <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
         <orcid-client-group xmlns="http://www.orcid.org/ns/orcid">
    <group-orcid>0000-0002-8144-4089</group-orcid>
    <group-name>Elsevier</group-name>
    <email>orcid-admin@elsevier.com</email>
    <orcid-client>
        <display-name>Ecological Complexity 2</display-name>
        <website>http://www.journals.elsevier.com/ecological-complexity</website>
        <short-description>An International Journal on Biocomplexity in the Environment and Theoretical Ecology</short-description>
        <redirect-uris>
            <redirect-uri type="default">https://developers.google.com/oauthplayground</redirect-uri>
            <redirect-uri scope="/orcid-bio/read-limited /orcid-profile/create" type="default">http://www.journals.elsevier.com/ecological-complexity/orcid-callback2</redirect-uri>
        </redirect-uris>
        <client-id>0000-0002-0274-8923</client-id>
        <client-secret>1cfdf792-159a-4ce5-a7fe-93cfc0842373</client-secret>
    </orcid-client>
    <orcid-client>
        <display-name>Ecological Economics</display-name>
        <website>http://www.journals.elsevier.com/ecological-economics</website>
        <short-description>The Transdisciplinary Journal of the International Society for Ecological Economics (ISEE)</short-description>
        <redirect-uris>
            <redirect-uri type="default">https://developers.google.com/oauthplayground</redirect-uri>
            <redirect-uri scope="/orcid-bio/read-limited" type="default">http://www.journals.elsevier.com/ecological-economics/orcid-callback</redirect-uri>
        </redirect-uris>
        <client-id>0000-0003-0501-3882</client-id>
        <client-secret>e0173f69-e26c-4c1f-83cc-526baf223b0f</client-secret>
    </orcid-client>
    <orcid-client>
        <display-name>Ecological Complexity</display-name>
        <website>http://www.journals.elsevier.com/ecological-complexity</website>
        <short-description>An International Journal on Biocomplexity in the Environment and Theoretical Ecology</short-description>
        <redirect-uris>
             <redirect-uri type="default">https://developers.google.com/oauthplayground</redirect-uri>
            <redirect-uri scope="/orcid-bio/read-limited /orcid-profile/create" type="default">http://www.journals.elsevier.com/ecological-complexity/orcid-callback</redirect-uri>
        </redirect-uris>
        <client-id>0000-0003-3664-1216</client-id>
        <client-secret>c8dde1e9-3e1d-4720-aec1-0830eae6cea4</client-secret>
    </orcid-client>
</orcid-client-group>

