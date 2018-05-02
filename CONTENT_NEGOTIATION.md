# Content Negotiation

The ORCID registry supports content negotiation for ORCID record metadata.  This means that you can request the PUBLIC JSON, XML or JSON-LD metadata directly from the URI version of an ORCID identifier by specifying the content type in a HTTP Accept header.  

Data is returned using the most recent stable version of the API, which is version 2.1 at the time of writing.  The data returned is the equivalent of calling https://pub.orcid.org/v2.1/{orcid}/record with the corresponding content type.  See the [v2.1 documentation](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/README.md) for a description of the format.

ORCID support for content negotiation is similar to that of DOIs - [Documentation here](https://citation.crosscite.org/docs.html). 

# What is content negotiation?

"Content negotiation refers to mechanisms defined as a part of HTTP that make it possible to serve different versions of a document (or more generally, representations of a resource) at the same URI, so that user agents can specify which version fits their capabilities the best. " - [Wikipedia](https://en.wikipedia.org/wiki/Content_negotiation)

It is defined by
[RFC7231 section 5.3.2](https://tools.ietf.org/html/rfc7231#section-5.3.2) and 
[RFC7231 section 3.4](https://tools.ietf.org/html/rfc7231#section-3.4)

# Supported content types

ORCID supports the following types:

| Type  | Accept Header | 
| ------------- | ------------- | 
| [ORCID XML](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/record-2.1.xsd)  | application/xml  |
| [ORCID XML](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/record-2.1.xsd)  | application/orcid+xml  |
| [ORCID XML](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/record-2.1.xsd)  | application/vnd.orcid+xml  |
| ORCID JSON  | application/json  |
| ORCID JSON  | application/json  |
| ORCID JSON  | application/vnd.orcid+json  |
| [Schema.org](https://schema.org) [JSON LD](https://json-ld.org/spec/latest/json-ld/) | application/ld+json |
| [RDF Turtle](https://www.w3.org/TR/turtle/) | text/turtle |
| [RDF N-Triples](https://www.w3.org/TR/n-triples/) | application/n-triples |
| [RDF/XML](https://www.w3.org/TR/rdf-syntax-grammar/) | application/rdf+xml |

# Example

```
curl -vLH'Accept: application/json' https://orcid.org/0000-0003-0902-4386
```






