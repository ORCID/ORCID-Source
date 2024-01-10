![](https://github.com/ORCID/ORCID-Source/workflows/ORCID-Source%20CI/badge.svg)
# ORCID-Source

Welcome to ORCID Source. Here are some resources to get you started.

* [What is ORCID?](http://orcid.org/about/what-is-orcid)

* [ORCID API introduction](https://github.com/ORCID/ORCID-Source/tree/master/orcid-api-web)
  * [Content negotiation](https://github.com/ORCID/ORCID-Source/blob/master/CONTENT_NEGOTIATION.md)

* [ORCID's API XSD](https://github.com/ORCID/orcid-model/tree/master/src/main/resources)
  * [Current stable XSD](https://github.com/ORCID/orcid-model/tree/master/src/main/resources/record_2.1)

* [ORCID's Localization Reference](https://github.com/ORCID/ORCID-Source/tree/master/orcid-core/src/main/resources/i18n)

* [ORCID API user's listserv](https://groups.google.com/forum/#!forum/orcid-api-users)

* [ORCID member support center](https://members.orcid.org/)

* [Popular Client Libraries](https://github.com/ORCID/ORCID-Source/blob/master/POPULAR_CLIENT_LIBRARIES.md)

# Getting Support

If you are experiencing problems using ORCID you can check our [Support page](https://support.orcid.org/hc/en-us) or reach out on the [API user's listserv](https://groups.google.com/forum/#!forum/orcid-api-users). 

# General Application Stack

ORCID Source is set of web apps and libraries built in [Java](http://en.wikipedia.org/wiki/Java_%28programming_language%29) with [Spring Web MVC](http://www.springsource.org/) and persistence provided by [Postgres Database](http://www.postgresql.org/).  

Frontend Technologies (brief version):
On the client side we utilize [HTML](http://www.w3schools.com/html/default.asp), [AJAX](http://en.wikipedia.org/wiki/Ajax_%28programming%29), [JQuery](http://jquery.com/) and [AngularJS](http://angularjs.org/).  Server side we use [FreeMarker](http://freemarker.sourceforge.net/) for view rendering.

Backend Technologies (brief version):
[Spring Web MVC](http://www.springsource.org/) is our web framework. For security we use [Spring Security](http://www.springsource.org/). Our restful services are built with [Jersey](http://jersey.java.net/) and [JAXB](http://jaxb.java.net/). Finally we use [JPA](http://www.oracle.com/technetwork/java/javaee/tech/persistence-jsp-140049.html)/[Hibernate](http://www.hibernate.org/) to persist models to a [Postgres Database](http://www.postgresql.org/) database.  

The above is just a brief introduction. Best way to see everything used is to dig into the code, but baring that please browse our [PROJECTS](https://github.com/ORCID/ORCID-Source/blob/master/PROJECTS.md) page.

# Versioning

Version number used with releases will look to follow a similar format like [semver](http://semver.org/)

**e.g.** 1.138.2 described as next

> [release that can’t be rolled back, typically DB changes].[release sequence].[release patch]|([patches applied after pushed to production]) 

# Contributing
Pull requests are welcome to improve the Registry. See [CONTRIBUTING.md](CONTRIBUTING.md) for details on making contributions,  feature requests, and creating issue/bug reports.

# Development Environment Setup
See [DEVSETUP.md](https://github.com/ORCID/ORCID-Source/blob/master/DEVSETUP.md)

# License
See [LICENSE](https://github.com/ORCID/ORCID-Source/blob/master/LICENSE)

# Contributors
See [CREDITS.md](https://github.com/ORCID/ORCID-Source/blob/master/CREDITS.md)

# Projects
See [PROJECTS.md](https://github.com/ORCID/ORCID-Source/blob/master/PROJECTS.md)
