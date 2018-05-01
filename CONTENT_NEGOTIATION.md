# Content Negotiation

The ORCID registry supports content negotiation for ORCID record metadata.  This means that you can request the PUBLIC JSON, XML or JSON-LD metadata directly from the URI version of an ORCID identifier by specifying the content type in a HTTP Accept header.  

Data is returned using the most recent stable version of the API, which is version 2.1 at the time of writing.  The data returned is the equivalent of calling https://pub.orcid.org/v2.1/{orcid}/record with the corresponding content type.  See the [v2.1 documentation](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/README.md) for a description of the format.

ORCID support for content negotiation is similar to that of DOIs - [Documentation here](https://citation.crosscite.org/docs.html). 

# What is content negotiation?

"Content negotiation refers to mechanisms defined as a part of HTTP that make it possible to serve different versions of a document (or more generally, representations of a resource) at the same URI, so that user agents can specify which version fits their capabilities the best. " - [Wikipedia](https://en.wikipedia.org/wiki/Content_negotiation)

It is defined in [RFC2616, section 12](https://www.w3.org/Protocols/rfc2616/rfc2616-sec12.html).

# Supported content types

ORCID supports the following types:

| Type  | Accept Header | 
| ------------- | ------------- | 
| ORCID XML  | application/xml  |
| ORCID XML  | application/orcid+xml  |
| ORCID XML  | application/vnd.orcid+xml  |
| ORCID JSON  | application/json  |
| ORCID JSON  | application/json  |
| ORCID JSON  | application/vnd.orcid+json  |
| Schema.org JSON-LD | application/ld+json |

# Example

```
curl -vLH'Accept: application/json' https://orcid.org/0000-0003-0902-4386
```






