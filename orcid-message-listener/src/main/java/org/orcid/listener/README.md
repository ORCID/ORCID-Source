Package contents
================

Listeners configured in spring config.  You can point queues at Consumer<LastModifiedMessage> implementations.

Testing S3
==========
Have at least the following: a tomcat running withapps deployed: message-listener, activemq, scheduler, solr, orcid-api-web

update message-listener.properties
	org.orcid.message-listener.s3.accessKey=xxx
	org.orcid.message-listener.s3.secretKey=xxx
	org.orcid.message-listener.api12Enabled=true
	org.orcid.message-listener.api20Enabled=false

create a member token with /read-public scope and put it in
	org.orcid.message-listener.api.read_public_access_token=xxx
	
Set some indexing status to PENDING or modify a record via the UI and see what happens

Testing SOLR
============
update message-listener.properties
	org.orcid.persistence.messaging.solr_indexing.enabled=true
Set some indexing status to PENDING or modify something in the UI the and see what happens
