ORCID - ActiveMQ
================

Simple module that fires up an ActiveMQ instance.  Can be run alongside other modules or stand alone.

Configuration
-------------
The broker is started by the spring context, which is started by the web.xml
The spring context uses the configuration in src/main/resources/activemq.xml to configure the broker.