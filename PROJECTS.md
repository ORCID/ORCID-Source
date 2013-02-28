Many have contributed to ORCID's Open Source effort, from direct contributions of code, to contributions of projects through sub-licensed code and binaries.

* [Projects](#Projects)
    * [Sub-licensed code](#sublicense)
    * [Packages](#package) (unmodified binaries)
    * [Other](#other)

Also see our active contributors: [CREDITS.md](https://github.com/ORCID/ORCID-Work-in-Progress/blob/master/CREDITS.md)

> ORCID (R) Open Source    
> http://orcid.org/OpenSource
>
> Copyright (c) 2013 ORCID, Inc.    
> Licensed under an MIT-Style License (MIT)    
> https://github.com/ORCID/ORCID-Source/blob/master/LICENSE.md    
> http://orcid.org/OpenSource/license
> 
> This copyright and license information (including a link to the full license) shall be included in its entirety in all copies or substantial portion of  the software.

--
<a id="Projects"></a>
## Projects

<a id="sublicense"></a>
### Sub-licensed code

The following code has been incorporated into the ORCID code:

**jQUERY**
* **[jQuery JavaScript Library](http://jquery.com/)** _(LICENSE: [MIT or GPL Version 2 licenses](http://jquery.org/license))_

<!--jQuery is a fast, small, and feature-rich JavaScript library. It makes things like HTML document traversal and manipulation, event handling, animation, and Ajax much simpler with an easy-to-use API that works across a multitude of browsers. With a combination of versatility and extensbility, jQuery has changed the way that millions of people write JavaScript. <i>(List of plugins may not be exhaustive)</i>-->

* **[jQuery UI CSS Framework](http://docs.jquery.com/UI/Theming/API)** _(LICENSE: [MIT](http://www.opensource.org/licenses/mit-license.php), [GPL Version 2](http://www.gnu.org/licenses/gpl-2.0.html))_
    
<!--<br>a robust CSS Framework designed for building custom jQuery widgets.<br>-->

* **jQuery UI Plugins** _(LICENSE: [MIT or GPL Version 2 licenses](http://jquery.org/license))_
    * [jQuery UI Resizable](http://docs.jquery.com/UI/Resizable#theming)
    * [jQuery UI Selectable](http://docs.jquery.com/UI/Selectable#theming)
    * [jQuery UI Accordion](http://docs.jquery.com/UI/Accordion#theming)
    * [jQuery UI Autocomplete](http://docs.jquery.com/UI/Autocomplete#theming)
    * [jQuery UI Menu](http://docs.jquery.com/UI/Menu#theming)
    * [jQuery UI Button](http://docs.jquery.com/UI/Button#theming)
    * [jQuery UI Dialog](http://docs.jquery.com/UI/Dialog#theming)
    * [jQuery UI Slider](http://docs.jquery.com/UI/Slider#theming)
    * [jQuery UI Tabs](http://docs.jquery.com/UI/Tabs#theming)
    * [jQuery UI Datepicker](http://docs.jquery.com/UI/Datepicker#theming)
    * [jQuery UI Progressbar](http://docs.jquery.com/UI/Progressbar#theming)


<!--<br>The jQuery UI plugins use the jQuery UI CSS Framework to style its look and feel, including colors and background textures.<br>-->


**OTHER CODE**
* **[Metadata](http://docs.jquery.com/Plugins/Metadata)** _(LICENSE: [MIT](http://www.opensource.org/licenses/mit-license.php), [GPL](http://www.gnu.org/licenses/gpl.html))_

<!--<br>jQuery plugin for parsing metadata from elements<br>-->

* **[jQuery Validation Plugin](https://github.com/jzaefferer/jquery-validation)** _(LICENSE: [MIT](http://www.opensource.org/licenses/mit-license.php), [GPL](http://www.gnu.org/licenses/gpl.html))_

<!--<br>The jQuery Validation Plugin provides drop-in validation for your existing forms, while making all kinds of customizations to fit your application really easy.<br>-->

* **[Sizzle.js](http://sizzlejs.com/)** _(LICENSE: MIT, BSD, and GPL Licenses)_

<!--<br>A pure-JavaScript CSS selector engine designed to be easily dropped in to a host library.<br>-->

* **[ColorBox](http://www.jacklmoore.com/colorbox)** _(LICENSE: [Standard MIT License](http://www.opensource.org/licenses/mit-license.php))_

<!--<br>a lightweight, customizable lightbox plugin for jQuery<br>-->

* **[password_strength_plugin.js](www.mypocket-technologies.com)** *modified version* _(LICENSE: [MIT License](https://github.com/ORCID/ORCID-Work-in-Progress/blob/master/orcid-frontend-web/src/main/webapp/static/javascript/plugins.js))_

<!--<br>Password Strength Meter is a jQuery plug-in provide you smart algorithm to detect a password strength. Based on [Firas Kassem orginal plugin](http://phiras.wordpress.com/2007/04/08/password-strength-meter-a-jquery-plugin/)<br>-->

* **[effects.js](http://script.aculo.us)** _(LICENSE: [MIT-style License](http://madrobby.github.com/scriptaculous/license/))_

<!--<br>User interface effects<br>-->

* **[Prototype JavaScript framework](http://www.prototypejs.org/)** _(LICENSE: [MIT-style license](http://prototypejs.org/license.html))_

<!--<br>Prototype takes the complexity out of client-side web programming. Built to solve real-world problems, it adds useful extensions to the browser scripting environment and provides elegant APIs around the clumsy interfaces of Ajax and the Document Object Model.<br>-->

* **[Files related to SOLR Configuration](http://wiki.apache.org/solr/#Installation_and_Configuration)** _(LICENSE: [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0))_

<!--<br>SolrTM is the popular, blazing fast open source enterprise search platform from the Apache LuceneTM project.<br>-->

* **[DTD Web Application](http://java.sun.com/dtd/web-app_2_3.dtd)** _(LICENSE: [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0))_

<!--<br>This is the XML DTD for the Servlet 2.3 deployment descriptor.<br>-->

* **[KeyStoreFactoryBean](http://static.springsource.org/spring-ws/sites/1.5/apidocs/org/springframework/ws/soap/security/support/KeyStoreFactoryBean.html)** _(LICENSE: [Apache License Version 2.0](http://static.springsource.org/spring-ws/sites/2.0/license.html))_

<!--<br>Spring factory bean for a [KeyStore](http://docs.oracle.com/javase/6/docs/api/java/security/KeyStore.html?is-external=true).<br>-->


<a id="package"></a>
### Packages 

**SECURITY**
* **[Semantico Spring Security OAuth Library](https://github.com/semantico/spring-security-oauth)** 

<!--<br>This repository was forked the the main spring-security-oath project to allow us to support multiple redirect URLs. These changes have subsequently been incorporated into the core product and should be used in preference to this unless you are developing against the ORCID codebase.</i> | **[Apache License V2.0](https://github.com/semantico/spring-security-oauth/blob/master/license.txt)-->

* **[Spring OAuth Security](http://static.springsource.org/spring-security/oauth/)**<i></i>

<!--<br>This project provides support for using Spring Security with OAuth (1a) and OAuth2. It provides features for implementing both consumers and providers of these protocols using standard Spring and Spring Security programming models and configuration idioms. | **[Apache License V 2.0](https://github.com/SpringSource/spring-security-oauth/blob/master/license.txt)**-->

* **[Spring Framework](http://www.springsource.org/spring-framework)** <i>org.springframework</i> 

<!--<br>The Spring Framework provides a comprehensive programming and configuration model for modern Java-based enterprise applications - on any kind of deployment platform. A key element of Spring is infrastructural support at the application level: Spring focuses on the "plumbing" of enterprise applications so that teams can focus on application-level business logic, without unnecessary ties to specific deployment environments. | **[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)** |-->

* **[jasypt](http://www.jasypt.org/)** - <i>org.jasypt</i> 

<!--<br>Jasypt is a java library which allows the developer to add basic encryption capabilities to his/her projects with minimum effort, and without the need of having deep knowledge on how cryptography works. | **[]()** |-->

<br>
**CODE & DATABASE**
* **[Java JDK](http://docs.oracle.com/javase/7/docs/)** 

<!--<br>Java Platform, Standard Edition (Java SE) lets you develop and deploy Java applications on desktops and servers, as well as in today's demanding embedded environments. Java offers the rich user interface, performance, versatility, portability, and security that today’s applications require. Java Platform, Enterprise Edition (Java EE) 6 is the industry standard for enterprise Java computing.   | **[Oracle Binary Code License Agreement for the Java SE Platform Products and JavaFX ](http://www.oracle.com/technetwork/java/javase/terms/license/index.html)** |-->

* **[Hibernate](http://www.hibernate.org/)** - <i>org.hibernate</i>

<!--<br>Hibernate is a collection of related projects enabling developers to utilize POJO-style domain models in their applications in ways extending well beyond Object/Relational Mapping. | **[]()** |-->

* **[Liquibase](http://www.liquibase.org/)** - <i>liquibase.database</i>

<!--<br>Liquibase is an open source (Apache 2.0 Licensed), database-independent library for tracking, managing and applying database changes. It is built on a simple premise: All database changes are stored in a human readable yet trackable form and checked into source control. | **[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)** |-->

* **[Apache Solr](http://lucene.apache.org/solr/)** - <i>schema.constants.SolrConstants</i> 

<!--<br>SolrTM is the popular, blazing fast open source enterprise search platform from the Apache LuceneTM project. Its major features include powerful full-text search, hit highlighting, faceted search, near real-time indexing, dynamic clustering, database integration, rich document (e.g., Word, PDF) handling, and geospatial search. Solr is highly reliable, scalable and fault tolerant, providing distributed indexing, replication and load-balanced querying, automated failover and recovery, centralized configuration and more. Solr powers the search and navigation features of many of the world's largest internet sites. | **[]()** |-->

<br>
**API SUPPORT**
* **[Jersey](http://jersey.java.net/)** <i>com.sun.jersey.api</i>

<!--<br>Jersey is the open source, production quality, JAX-RS (JSR 311) Reference Implementation for building RESTful Web services, with an API for developers to extend Jersey to suit their needs. | **[CDDL, Version 1.1](http://glassfish.java.net/public/CDDL+GPL_1_1.html)** |-->

* **[c3p0 - JDBC3 Connection and Statement Pooling](http://www.mchange.com/projects/c3p0/)** <i>com.mchange.v2.c3p0</i>

<!--<br>c3p0 is an easy-to-use library for making traditional JDBC drivers "enterprise-ready" by augmenting them with functionality defined by the jdbc3 spec and the optional extensions to jdbc2. | **[Lesser GNU Public License (LGPL)](http://www.gnu.org/copyleft/lesser.html)** |-->

* **[Jackson JSON Processor](http://wiki.fasterxml.com/JacksonHome)** - <i>org.codehaus.jackson</i>

<!--<br>Inspired by the quality and variety of XML tooling available for the Java platform (StAX, JAXB, etc.), the Jackson is a multi-purpose Java library for processing JSON data format. Jackson aims to be the best possible combination of fast, correct, lightweight, and ergonomic for developers. | **[Apache License (AL) 2.0](http://www.apache.org/licenses/LICENSE-2.0)** |-->

* **[args4j](http://args4j.kohsuke.org/)** - <i>org.kohsuke.args4j</i>

<!--<br>args4j is a small Java class library that makes it easy to parse command line options/arguments in your CUI application. | **[]()** |-->

<br>
**UTILITIES & TOOLS**
* **[java-bibtex](http://code.google.com/p/java-bibtex/)** <i>org.jbibtex</i>

<!--<br>Java BibTeX Parser and Formatter. | **[BSD 3](http://opensource.org/licenses/BSD-3-Clause)** |-->

* **[Yammer Metrics](http://metrics.codahale.com/)** <i>com.yammer.metrics</i>

<!--<br>Developed by Yammer to instrument their JVM-based backend services, Metrics provides a powerful toolkit of ways to measure the behavior of critical components in your production environment. | **[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)** |-->

* **[Apache Commons](http://commons.apache.org/)** - <i>org.apache.commons</i>

<!--<br>The Commons Proper is dedicated to one principal goal: creating and maintaining reusable Java components. The Commons Proper is a place for collaboration and sharing, where developers from throughout the Apache community can work together on projects to be shared by the Apache projects and Apache users. | **[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)** |-->

* **[aspectj](http://eclipse.org/aspectj/)** - <i>org.aspectj</i>

<!--<br> An extension to the Java(tm) programming language that enables clean modularization of crosscutting concerns, such as error checking and handling, synchronization, context-sensitive behavior, performance optimizations, monitoring and logging, debugging support, and multi-object protocols. | **[Eclipse Public License](http://www.eclipse.org/org/documents/epl-v10.php)** |-->

* **[FreeMarker](http://freemarker.sourceforge.net/)** - <i>freemarker.templat</i>

<!--<br>FreeMarker is a "template engine"; a generic tool to generate text output (anything from HTML to autogenerated source code) based on templates. It's a Java package, a class library for Java programmers. It's not an application for end-users in itself, but something that programmers can embed into their products. | **[]()** |-->

* **[Simple Logging Facade for Java - SLF4J](http://www.slf4j.org/)** - <i>org.slf4j</i>

<!--<br>The Simple Logging Facade for Java or (SLF4J) serves as a simple facade or abstraction for various logging frameworks, e.g. java.util.logging, log4j and logback, allowing the end user to plug in the desired logging framework at deployment time. | **[]()** |-->

* **[SAX](http://www.saxproject.org/)** - <i>org.xml.sax</i>

<!--<br>SAX is the Simple API for XML, originally a Java-only API. SAX was the first widely adopted API for XML in Java, and is a “de facto” standard. The current version is SAX 2.0.1, and there are versions for several programming language environments other than Java. | **[]()** |-->

<br>
**TESTING**
* **[JUnit](http://en.wikipedia.org/wiki/JUnit)** <i>org.junit</i>

<!--<br>JUnit is a unit testing framework for the Java programming language. | **[Common Public License (CPL)](http://opensource.org/licenses/cpl1.0.php)** |-->

* **[DbUnit](http://www.dbunit.org/)** - <i>org.dbunit</i>

<!--<br>DbUnit is a JUnit extension (also usable with Ant) targeted at database-driven projects that, among other things, puts your database into a known state between test runs. This is an excellent way to avoid the myriad of problems that can occur when one test case corrupts the database and causes subsequent tests to fail or exacerbate the damage. | **[]()** |-->

* **[XMLUnit](http://xmlunit.sourceforge.net/api/overview-summary.html)** - <i>org.custommonkey.xmlunit</i>

<!--<br>XMLUnit provides extensions to the JUnit framework to allow assertions to be made about XML content. | **[]()** |-->

* **[Hamcrest](http://hamcrest.org/)** - </i>org.hamcrest</i>

<!--<br>Hamcrest is a framework for creating matchers ('Hamcrest' is an anagram of 'matchers'), allowing match rules to be defined declaratively. These matchers have uses in unit testing frameworks such as JUnit [2] and jMock. | **[]()** |-->

* **[Mockito](http://code.google.com/p/mockito/)** - <i>org.mockito</i>

<!--<br>Mockito is a mocking framework that tastes really good. It lets you write beautiful tests with clean & simple API. Mockito doesn't give you hangover because the tests are very readable and they produce clean verification errors. Read more about features & motivations. | **[]()** |-->

* **[SeleniumHQ](http://docs.seleniumhq.org/)** - <i>org.openqa.selenium</i>

<!--<br>Selenium automates browsers. That's it. What you do with that power is entirely up to you. Primarily it is for automating web applications for testing purposes, but is certainly not limited to just that. Boring web-based administration tasks can (and should!) also be automated as well. | **[]()** |-->

<a id="other"></a>
### OTHER

The font used for the ORCID website is **Gill Sans**.


