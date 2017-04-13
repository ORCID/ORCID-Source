## Paths
### Fetch client details
```
GET /v2.0/client/{client_id}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|client_id||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch Groups
```
GET /v2.0/group-id-record
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|QueryParameter|page-size||false|string|100|
|QueryParameter|page||false|string|1|
|QueryParameter|name||false|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|GroupIdRecords|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Create a Group
```
POST /v2.0/group-id-record
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|BodyParameter|body||false|GroupIdRecord||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|201|Group created, see HTTP Location header for URI|No Content|
|400|Invalid Group representation|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch a Group
```
GET /v2.0/group-id-record/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|GroupIdRecord|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Update a Group
```
PUT /v2.0/group-id-record/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|putCode||true|string||
|BodyParameter|body||false|GroupIdRecord||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Peer Review updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Delete a Group
```
DELETE /v2.0/group-id-record/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Group deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Search records
```
GET /v2.0/search
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|QueryParameter|q||false|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml

#### Tags

* Member API v2.0

### Fetch all activities
```
GET /v2.0/{orcid}/activities
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|ActivitiesSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch all addresses of a profile
```
GET /v2.0/{orcid}/address
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0

### Add an address
```
POST /v2.0/{orcid}/address
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Address||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch an address
```
GET /v2.0/{orcid}/address/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0

### Edit an address
```
PUT /v2.0/{orcid}/address/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Address||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Delete an address
```
DELETE /v2.0/{orcid}/address/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Get biography details
```
GET /v2.0/{orcid}/biography
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Create an Education
```
POST /v2.0/{orcid}/education
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Education||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Education created, see HTTP Location header for URI|No Content|
|400|Invalid Education representation|string|
|500|Invalid Education representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch an Education summary
```
GET /v2.0/{orcid}/education/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|EducationSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch an Education
```
GET /v2.0/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|OK|Education|
|400|Invalid putCode or ORCID ID|string|
|404|putCode not found|string|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Update an Education
```
PUT /v2.0/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Education||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Education updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Delete an Education
```
DELETE /v2.0/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Education deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch all educations
```
GET /v2.0/{orcid}/educations
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Educations|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch all emails for an ORCID ID
```
GET /v2.0/{orcid}/email
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Create an Employment
```
POST /v2.0/{orcid}/employment
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Employment||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Employment created, see HTTP Location header for URI|No Content|
|400|Invalid Employment representation|string|
|500|Invalid Employment representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch an Employment Summary
```
GET /v2.0/{orcid}/employment/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|EmploymentSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch an Employment
```
GET /v2.0/{orcid}/employment/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Employment|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Update an Employment
```
PUT /v2.0/{orcid}/employment/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Employment||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Employment updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Delete an Employment
```
DELETE /v2.0/{orcid}/employment/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Employment deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch all employments
```
GET /v2.0/{orcid}/employments
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Employments|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch external identifiers
```
GET /v2.0/{orcid}/external-identifiers
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0

### Add external identifier
```
POST /v2.0/{orcid}/external-identifiers
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|PersonExternalIdentifier||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch external identifier
```
GET /v2.0/{orcid}/external-identifiers/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0

### Edit external identifier
```
PUT /v2.0/{orcid}/external-identifiers/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|PersonExternalIdentifier||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Delete external identifier
```
DELETE /v2.0/{orcid}/external-identifiers/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Create a Funding
```
POST /v2.0/{orcid}/funding
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Funding||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Funding created, see HTTP Location header for URI|No Content|
|400|Invalid Funding representation|string|
|500|Invalid Funding representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch a Funding Summary
```
GET /v2.0/{orcid}/funding/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|FundingSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch a Funding
```
GET /v2.0/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Funding|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Update a Funding
```
PUT /v2.0/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Funding||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Funding updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Delete a Funding
```
DELETE /v2.0/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Funding deleted|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch all fundings
```
GET /v2.0/{orcid}/fundings
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Fundings|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch keywords
```
GET /v2.0/{orcid}/keywords
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0

### Add keyword
```
POST /v2.0/{orcid}/keywords
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Keyword||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch keyword
```
GET /v2.0/{orcid}/keywords/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0

### Edit keyword
```
PUT /v2.0/{orcid}/keywords/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Keyword||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Delete keyword
```
DELETE /v2.0/{orcid}/keywords/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Add a notification
```
POST /v2.0/{orcid}/notification-permission
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|NotificationPermission||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Notification added, see HTTP Location header for URI|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch a notification by id
```
GET /v2.0/{orcid}/notification-permission/{id}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|id||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Notification found|Notification|
|401|Access denied, this is not your notification|string|
|404|Notification not found|string|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Archive a notification
```
DELETE /v2.0/{orcid}/notification-permission/{id}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|id||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Notification archived|Notification|
|401|Access denied, this is not your notification|string|
|404|Notification not found|string|


#### Consumes

* */*

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch Other names
```
GET /v2.0/{orcid}/other-names
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0

### Add other name
```
POST /v2.0/{orcid}/other-names
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|OtherName||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch Other name
```
GET /v2.0/{orcid}/other-names/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0

### Edit other name
```
PUT /v2.0/{orcid}/other-names/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|OtherName||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Delete other name
```
DELETE /v2.0/{orcid}/other-names/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Create a Peer Review
```
POST /v2.0/{orcid}/peer-review
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|PeerReview||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Peer Review created, see HTTP Location header for URI|No Content|
|400|Invalid Peer Review representation|string|
|500|Invalid Peer Review representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch a Peer Review Summary
```
GET /v2.0/{orcid}/peer-review/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|PeerReviewSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch a Peer Review
```
GET /v2.0/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|PeerReview|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Update a Peer Review
```
PUT /v2.0/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|PeerReview||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Peer Review updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Delete a Peer Review
```
DELETE /v2.0/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Peer Review deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch all peer reviews
```
GET /v2.0/{orcid}/peer-reviews
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|PeerReviews|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch person details
```
GET /v2.0/{orcid}/person
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0

### Fetch personal details for an ORCID ID
```
GET /v2.0/{orcid}/personal-details
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch all researcher urls for an ORCID ID
```
GET /v2.0/{orcid}/researcher-urls
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Add a new researcher url for an ORCID ID
```
POST /v2.0/{orcid}/researcher-urls
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|ResearcherUrl||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch one researcher url for an ORCID ID
```
GET /v2.0/{orcid}/researcher-urls/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0

### Edits researcher url for an ORCID ID
```
PUT /v2.0/{orcid}/researcher-urls/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|ResearcherUrl||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Delete one researcher url from an ORCID ID
```
DELETE /v2.0/{orcid}/researcher-urls/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Create a Work
```
POST /v2.0/{orcid}/work
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Work||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Work created, see HTTP Location header for URI|No Content|
|400|Invalid Work representation|string|
|500|Invalid Work representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch a Work Summary
```
GET /v2.0/{orcid}/work/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|WorkSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch a Work
```
GET /v2.0/{orcid}/work/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Work|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Update a Work
```
PUT /v2.0/{orcid}/work/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Work||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Work updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Delete a Work
```
DELETE /v2.0/{orcid}/work/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Work deleted|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch all works
```
GET /v2.0/{orcid}/works
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Works|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Create a listo of Work
```
POST /v2.0/{orcid}/works
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|WorkBulk||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|At least one of the works was created|No Content|
|400|Invalid Work representation|string|
|500|Invalid Work representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch specified works
```
GET /v2.0/{orcid}/works/{putCodes}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCodes||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|WorkBulk|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0

### Fetch record details
```
GET /v2.0/{orcid}{ignore}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0

### Fetch Groups
```
GET /v2.0_rc1/group-id-record
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|QueryParameter|page-size||false|string||
|QueryParameter|page||false|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|GroupIdRecords|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Create a Group
```
POST /v2.0_rc1/group-id-record
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|BodyParameter|body||false|GroupIdRecord||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|201|Group created, see HTTP Location header for URI|No Content|
|400|Invalid Group representation|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Fetch a Group
```
GET /v2.0_rc1/group-id-record/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|GroupIdRecord|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Update a Group
```
PUT /v2.0_rc1/group-id-record/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|putCode||true|string||
|BodyParameter|body||false|GroupIdRecord||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Peer Review updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Delete a Group
```
DELETE /v2.0_rc1/group-id-record/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Group deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Fetch all activities
```
GET /v2.0_rc1/{orcid}/activities
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|ActivitiesSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Create an Education
```
POST /v2.0_rc1/{orcid}/education
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Education||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Education created, see HTTP Location header for URI|No Content|
|400|Invalid Education representation|string|
|500|Invalid Education representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Fetch an Education summary
```
GET /v2.0_rc1/{orcid}/education/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|EducationSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Fetch an Education
```
GET /v2.0_rc1/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|OK|Education|
|400|Invalid putCode or ORCID ID|string|
|404|putCode not found|string|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Update an Education
```
PUT /v2.0_rc1/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Education||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Education updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Delete an Education
```
DELETE /v2.0_rc1/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Education deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Create an Employment
```
POST /v2.0_rc1/{orcid}/employment
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Employment||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Employment created, see HTTP Location header for URI|No Content|
|400|Invalid Employment representation|string|
|500|Invalid Employment representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Fetch an Employment Summary
```
GET /v2.0_rc1/{orcid}/employment/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|EmploymentSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Fetch an Employment
```
GET /v2.0_rc1/{orcid}/employment/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Employment|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Update an Employment
```
PUT /v2.0_rc1/{orcid}/employment/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Employment||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Employment updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Delete an Employment
```
DELETE /v2.0_rc1/{orcid}/employment/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Employment deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Create a Funding
```
POST /v2.0_rc1/{orcid}/funding
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Funding||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Funding created, see HTTP Location header for URI|No Content|
|400|Invalid Funding representation|string|
|500|Invalid Funding representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Fetch a Funding Summary
```
GET /v2.0_rc1/{orcid}/funding/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|FundingSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Fetch a Funding
```
GET /v2.0_rc1/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Funding|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Update a Funding
```
PUT /v2.0_rc1/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Funding||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Funding updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Delete a Funding
```
DELETE /v2.0_rc1/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Funding deleted|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Add a notification
```
POST /v2.0_rc1/{orcid}/notification-permission
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|NotificationPermission||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Notification added, see HTTP Location header for URI|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Fetch a notification by id
```
GET /v2.0_rc1/{orcid}/notification-permission/{id}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|id||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Notification found|Notification|
|401|Access denied, this is not your notification|string|
|404|Notification not found|string|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Archive a notification
```
DELETE /v2.0_rc1/{orcid}/notification-permission/{id}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|id||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Notification archived|Notification|
|401|Access denied, this is not your notification|string|
|404|Notification not found|string|


#### Consumes

* */*

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Create a Peer Review
```
POST /v2.0_rc1/{orcid}/peer-review
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|PeerReview||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Peer Review created, see HTTP Location header for URI|No Content|
|400|Invalid Peer Review representation|string|
|500|Invalid Peer Review representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Fetch a Peer Review Summary
```
GET /v2.0_rc1/{orcid}/peer-review/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|PeerReviewSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Fetch a Peer Review
```
GET /v2.0_rc1/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|PeerReview|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Update a Peer Review
```
PUT /v2.0_rc1/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|PeerReview||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Peer Review updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Delete a Peer Review
```
DELETE /v2.0_rc1/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Peer Review deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Create a Work
```
POST /v2.0_rc1/{orcid}/work
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Work||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Work created, see HTTP Location header for URI|No Content|
|400|Invalid Work representation|string|
|500|Invalid Work representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Fetch a Work Summary
```
GET /v2.0_rc1/{orcid}/work/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|WorkSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Fetch a Work
```
GET /v2.0_rc1/{orcid}/work/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Work|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Update a Work
```
PUT /v2.0_rc1/{orcid}/work/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Work||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Work updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Delete a Work
```
DELETE /v2.0_rc1/{orcid}/work/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Work deleted|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc1

### Fetch Groups
```
GET /v2.0_rc2/group-id-record
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|QueryParameter|page-size||false|string||
|QueryParameter|page||false|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|GroupIdRecords|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Create a Group
```
POST /v2.0_rc2/group-id-record
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|BodyParameter|body||false|GroupIdRecord||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|201|Group created, see HTTP Location header for URI|No Content|
|400|Invalid Group representation|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch a Group
```
GET /v2.0_rc2/group-id-record/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|GroupIdRecord|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Update a Group
```
PUT /v2.0_rc2/group-id-record/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|putCode||true|string||
|BodyParameter|body||false|GroupIdRecord||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Peer Review updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Delete a Group
```
DELETE /v2.0_rc2/group-id-record/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Group deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch all activities
```
GET /v2.0_rc2/{orcid}/activities
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|ActivitiesSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch all addresses of a profile
```
GET /v2.0_rc2/{orcid}/address
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc2

### Add an address
```
POST /v2.0_rc2/{orcid}/address
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Address||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch an address
```
GET /v2.0_rc2/{orcid}/address/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc2

### Edit an address
```
PUT /v2.0_rc2/{orcid}/address/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Address||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Delete an address
```
DELETE /v2.0_rc2/{orcid}/address/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Get biography details
```
GET /v2.0_rc2/{orcid}/biography
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Create an Education
```
POST /v2.0_rc2/{orcid}/education
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Education||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Education created, see HTTP Location header for URI|No Content|
|400|Invalid Education representation|string|
|500|Invalid Education representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch an Education summary
```
GET /v2.0_rc2/{orcid}/education/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|EducationSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch an Education
```
GET /v2.0_rc2/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|OK|Education|
|400|Invalid putCode or ORCID ID|string|
|404|putCode not found|string|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Update an Education
```
PUT /v2.0_rc2/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Education||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Education updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Delete an Education
```
DELETE /v2.0_rc2/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Education deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch all emails for an ORCID ID
```
GET /v2.0_rc2/{orcid}/email
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Create an Employment
```
POST /v2.0_rc2/{orcid}/employment
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Employment||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Employment created, see HTTP Location header for URI|No Content|
|400|Invalid Employment representation|string|
|500|Invalid Employment representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch an Employment Summary
```
GET /v2.0_rc2/{orcid}/employment/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|EmploymentSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch an Employment
```
GET /v2.0_rc2/{orcid}/employment/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Employment|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Update an Employment
```
PUT /v2.0_rc2/{orcid}/employment/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Employment||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Employment updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Delete an Employment
```
DELETE /v2.0_rc2/{orcid}/employment/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Employment deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch external identifiers
```
GET /v2.0_rc2/{orcid}/external-identifiers
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc2

### Add external identifier
```
POST /v2.0_rc2/{orcid}/external-identifiers
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|PersonExternalIdentifier||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch external identifier
```
GET /v2.0_rc2/{orcid}/external-identifiers/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc2

### Edit external identifier
```
PUT /v2.0_rc2/{orcid}/external-identifiers/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|PersonExternalIdentifier||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Delete external identifier
```
DELETE /v2.0_rc2/{orcid}/external-identifiers/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Create a Funding
```
POST /v2.0_rc2/{orcid}/funding
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Funding||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Funding created, see HTTP Location header for URI|No Content|
|400|Invalid Funding representation|string|
|500|Invalid Funding representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch a Funding Summary
```
GET /v2.0_rc2/{orcid}/funding/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|FundingSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch a Funding
```
GET /v2.0_rc2/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Funding|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Update a Funding
```
PUT /v2.0_rc2/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Funding||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Funding updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Delete a Funding
```
DELETE /v2.0_rc2/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Funding deleted|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch keywords
```
GET /v2.0_rc2/{orcid}/keywords
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc2

### Add keyword
```
POST /v2.0_rc2/{orcid}/keywords
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Keyword||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch keyword
```
GET /v2.0_rc2/{orcid}/keywords/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc2

### Edit keyword
```
PUT /v2.0_rc2/{orcid}/keywords/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Keyword||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Delete keyword
```
DELETE /v2.0_rc2/{orcid}/keywords/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Add a notification
```
POST /v2.0_rc2/{orcid}/notification-permission
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|NotificationPermission||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Notification added, see HTTP Location header for URI|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch a notification by id
```
GET /v2.0_rc2/{orcid}/notification-permission/{id}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|id||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Notification found|Notification|
|401|Access denied, this is not your notification|string|
|404|Notification not found|string|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Archive a notification
```
DELETE /v2.0_rc2/{orcid}/notification-permission/{id}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|id||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Notification archived|Notification|
|401|Access denied, this is not your notification|string|
|404|Notification not found|string|


#### Consumes

* */*

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch Other names
```
GET /v2.0_rc2/{orcid}/other-names
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc2

### Add other name
```
POST /v2.0_rc2/{orcid}/other-names
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|OtherName||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch Other name
```
GET /v2.0_rc2/{orcid}/other-names/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc2

### Edit other name
```
PUT /v2.0_rc2/{orcid}/other-names/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|OtherName||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Delete other name
```
DELETE /v2.0_rc2/{orcid}/other-names/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Create a Peer Review
```
POST /v2.0_rc2/{orcid}/peer-review
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|PeerReview||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Peer Review created, see HTTP Location header for URI|No Content|
|400|Invalid Peer Review representation|string|
|500|Invalid Peer Review representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch a Peer Review Summary
```
GET /v2.0_rc2/{orcid}/peer-review/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|PeerReviewSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch a Peer Review
```
GET /v2.0_rc2/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|PeerReview|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Update a Peer Review
```
PUT /v2.0_rc2/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|PeerReview||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Peer Review updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Delete a Peer Review
```
DELETE /v2.0_rc2/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Peer Review deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch person details
```
GET /v2.0_rc2/{orcid}/person
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc2

### Fetch personal details for an ORCID ID
```
GET /v2.0_rc2/{orcid}/personal-details
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch all researcher urls for an ORCID ID
```
GET /v2.0_rc2/{orcid}/researcher-urls
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Add a new researcher url for an ORCID ID
```
POST /v2.0_rc2/{orcid}/researcher-urls
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|ResearcherUrl||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch one researcher url for an ORCID ID
```
GET /v2.0_rc2/{orcid}/researcher-urls/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc2

### Edits researcher url for an ORCID ID
```
PUT /v2.0_rc2/{orcid}/researcher-urls/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|ResearcherUrl||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Delete one researcher url from an ORCID ID
```
DELETE /v2.0_rc2/{orcid}/researcher-urls/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Create a Work
```
POST /v2.0_rc2/{orcid}/work
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Work||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Work created, see HTTP Location header for URI|No Content|
|400|Invalid Work representation|string|
|500|Invalid Work representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch a Work Summary
```
GET /v2.0_rc2/{orcid}/work/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|WorkSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch a Work
```
GET /v2.0_rc2/{orcid}/work/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Work|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Update a Work
```
PUT /v2.0_rc2/{orcid}/work/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Work||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Work updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Delete a Work
```
DELETE /v2.0_rc2/{orcid}/work/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Work deleted|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc2

### Fetch record details
```
GET /v2.0_rc2/{orcid}{ignore}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc2

### Fetch Groups
```
GET /v2.0_rc3/group-id-record
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|QueryParameter|page-size||false|string|100|
|QueryParameter|page||false|string|1|
|QueryParameter|name||false|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|GroupIdRecords|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Create a Group
```
POST /v2.0_rc3/group-id-record
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|BodyParameter|body||false|GroupIdRecord||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|201|Group created, see HTTP Location header for URI|No Content|
|400|Invalid Group representation|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch a Group
```
GET /v2.0_rc3/group-id-record/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|GroupIdRecord|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Update a Group
```
PUT /v2.0_rc3/group-id-record/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|putCode||true|string||
|BodyParameter|body||false|GroupIdRecord||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Peer Review updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Delete a Group
```
DELETE /v2.0_rc3/group-id-record/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Group deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch all activities
```
GET /v2.0_rc3/{orcid}/activities
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|ActivitiesSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch all addresses of a profile
```
GET /v2.0_rc3/{orcid}/address
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc3

### Add an address
```
POST /v2.0_rc3/{orcid}/address
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Address||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch an address
```
GET /v2.0_rc3/{orcid}/address/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc3

### Edit an address
```
PUT /v2.0_rc3/{orcid}/address/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Address||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Delete an address
```
DELETE /v2.0_rc3/{orcid}/address/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Get biography details
```
GET /v2.0_rc3/{orcid}/biography
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Create an Education
```
POST /v2.0_rc3/{orcid}/education
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Education||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Education created, see HTTP Location header for URI|No Content|
|400|Invalid Education representation|string|
|500|Invalid Education representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch an Education summary
```
GET /v2.0_rc3/{orcid}/education/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|EducationSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch an Education
```
GET /v2.0_rc3/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|OK|Education|
|400|Invalid putCode or ORCID ID|string|
|404|putCode not found|string|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Update an Education
```
PUT /v2.0_rc3/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Education||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Education updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Delete an Education
```
DELETE /v2.0_rc3/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Education deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch all educations
```
GET /v2.0_rc3/{orcid}/educations
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Educations|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch all emails for an ORCID ID
```
GET /v2.0_rc3/{orcid}/email
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Create an Employment
```
POST /v2.0_rc3/{orcid}/employment
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Employment||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Employment created, see HTTP Location header for URI|No Content|
|400|Invalid Employment representation|string|
|500|Invalid Employment representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch an Employment Summary
```
GET /v2.0_rc3/{orcid}/employment/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|EmploymentSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch an Employment
```
GET /v2.0_rc3/{orcid}/employment/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Employment|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Update an Employment
```
PUT /v2.0_rc3/{orcid}/employment/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Employment||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Employment updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Delete an Employment
```
DELETE /v2.0_rc3/{orcid}/employment/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Employment deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch all employments
```
GET /v2.0_rc3/{orcid}/employments
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Employments|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch external identifiers
```
GET /v2.0_rc3/{orcid}/external-identifiers
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc3

### Add external identifier
```
POST /v2.0_rc3/{orcid}/external-identifiers
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|PersonExternalIdentifier||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch external identifier
```
GET /v2.0_rc3/{orcid}/external-identifiers/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc3

### Edit external identifier
```
PUT /v2.0_rc3/{orcid}/external-identifiers/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|PersonExternalIdentifier||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Delete external identifier
```
DELETE /v2.0_rc3/{orcid}/external-identifiers/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Create a Funding
```
POST /v2.0_rc3/{orcid}/funding
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Funding||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Funding created, see HTTP Location header for URI|No Content|
|400|Invalid Funding representation|string|
|500|Invalid Funding representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch a Funding Summary
```
GET /v2.0_rc3/{orcid}/funding/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|FundingSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch a Funding
```
GET /v2.0_rc3/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Funding|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Update a Funding
```
PUT /v2.0_rc3/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Funding||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Funding updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Delete a Funding
```
DELETE /v2.0_rc3/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Funding deleted|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch all fundings
```
GET /v2.0_rc3/{orcid}/fundings
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Fundings|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch keywords
```
GET /v2.0_rc3/{orcid}/keywords
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc3

### Add keyword
```
POST /v2.0_rc3/{orcid}/keywords
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Keyword||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch keyword
```
GET /v2.0_rc3/{orcid}/keywords/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc3

### Edit keyword
```
PUT /v2.0_rc3/{orcid}/keywords/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Keyword||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Delete keyword
```
DELETE /v2.0_rc3/{orcid}/keywords/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Add a notification
```
POST /v2.0_rc3/{orcid}/notification-permission
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|NotificationPermission||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Notification added, see HTTP Location header for URI|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch a notification by id
```
GET /v2.0_rc3/{orcid}/notification-permission/{id}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|id||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Notification found|Notification|
|401|Access denied, this is not your notification|string|
|404|Notification not found|string|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Archive a notification
```
DELETE /v2.0_rc3/{orcid}/notification-permission/{id}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|id||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Notification archived|Notification|
|401|Access denied, this is not your notification|string|
|404|Notification not found|string|


#### Consumes

* */*

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch Other names
```
GET /v2.0_rc3/{orcid}/other-names
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc3

### Add other name
```
POST /v2.0_rc3/{orcid}/other-names
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|OtherName||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch Other name
```
GET /v2.0_rc3/{orcid}/other-names/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc3

### Edit other name
```
PUT /v2.0_rc3/{orcid}/other-names/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|OtherName||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Delete other name
```
DELETE /v2.0_rc3/{orcid}/other-names/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Create a Peer Review
```
POST /v2.0_rc3/{orcid}/peer-review
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|PeerReview||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Peer Review created, see HTTP Location header for URI|No Content|
|400|Invalid Peer Review representation|string|
|500|Invalid Peer Review representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch a Peer Review Summary
```
GET /v2.0_rc3/{orcid}/peer-review/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|PeerReviewSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch a Peer Review
```
GET /v2.0_rc3/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|PeerReview|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Update a Peer Review
```
PUT /v2.0_rc3/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|PeerReview||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Peer Review updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Delete a Peer Review
```
DELETE /v2.0_rc3/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Peer Review deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch all peer reviews
```
GET /v2.0_rc3/{orcid}/peer-reviews
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|PeerReviews|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch person details
```
GET /v2.0_rc3/{orcid}/person
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc3

### Fetch personal details for an ORCID ID
```
GET /v2.0_rc3/{orcid}/personal-details
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch all researcher urls for an ORCID ID
```
GET /v2.0_rc3/{orcid}/researcher-urls
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Add a new researcher url for an ORCID ID
```
POST /v2.0_rc3/{orcid}/researcher-urls
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|ResearcherUrl||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch one researcher url for an ORCID ID
```
GET /v2.0_rc3/{orcid}/researcher-urls/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc3

### Edits researcher url for an ORCID ID
```
PUT /v2.0_rc3/{orcid}/researcher-urls/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|ResearcherUrl||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Delete one researcher url from an ORCID ID
```
DELETE /v2.0_rc3/{orcid}/researcher-urls/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Create a Work
```
POST /v2.0_rc3/{orcid}/work
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Work||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Work created, see HTTP Location header for URI|No Content|
|400|Invalid Work representation|string|
|500|Invalid Work representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch a Work Summary
```
GET /v2.0_rc3/{orcid}/work/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|WorkSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch a Work
```
GET /v2.0_rc3/{orcid}/work/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Work|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Update a Work
```
PUT /v2.0_rc3/{orcid}/work/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Work||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Work updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Delete a Work
```
DELETE /v2.0_rc3/{orcid}/work/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Work deleted|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch all works
```
GET /v2.0_rc3/{orcid}/works
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Works|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Create a listo of Work
```
POST /v2.0_rc3/{orcid}/works
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|WorkBulk||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|At least one of the works was created|No Content|
|400|Invalid Work representation|string|
|500|Invalid Work representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc3

### Fetch record details
```
GET /v2.0_rc3/{orcid}{ignore}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc3

### Fetch client details
```
GET /v2.0_rc4/client/{client_id}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|client_id||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch Groups
```
GET /v2.0_rc4/group-id-record
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|QueryParameter|page-size||false|string||
|QueryParameter|page||false|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|GroupIdRecords|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Create a Group
```
POST /v2.0_rc4/group-id-record
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|BodyParameter|body||false|GroupIdRecord||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|201|Group created, see HTTP Location header for URI|No Content|
|400|Invalid Group representation|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch a Group
```
GET /v2.0_rc4/group-id-record/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|GroupIdRecord|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Update a Group
```
PUT /v2.0_rc4/group-id-record/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|putCode||true|string||
|BodyParameter|body||false|GroupIdRecord||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Peer Review updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Delete a Group
```
DELETE /v2.0_rc4/group-id-record/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Group deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Search records
```
GET /v2.0_rc4/search
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|QueryParameter|q||false|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml

#### Tags

* Member API v2.0_rc4

### Fetch all activities
```
GET /v2.0_rc4/{orcid}/activities
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|ActivitiesSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch all addresses of a profile
```
GET /v2.0_rc4/{orcid}/address
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc4

### Add an address
```
POST /v2.0_rc4/{orcid}/address
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Address||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch an address
```
GET /v2.0_rc4/{orcid}/address/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc4

### Edit an address
```
PUT /v2.0_rc4/{orcid}/address/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Address||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Delete an address
```
DELETE /v2.0_rc4/{orcid}/address/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Get biography details
```
GET /v2.0_rc4/{orcid}/biography
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Create an Education
```
POST /v2.0_rc4/{orcid}/education
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Education||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Education created, see HTTP Location header for URI|No Content|
|400|Invalid Education representation|string|
|500|Invalid Education representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch an Education summary
```
GET /v2.0_rc4/{orcid}/education/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|EducationSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch an Education
```
GET /v2.0_rc4/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|OK|Education|
|400|Invalid putCode or ORCID ID|string|
|404|putCode not found|string|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Update an Education
```
PUT /v2.0_rc4/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Education||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Education updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Delete an Education
```
DELETE /v2.0_rc4/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Education deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch all educations
```
GET /v2.0_rc4/{orcid}/educations
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Educations|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch all emails for an ORCID ID
```
GET /v2.0_rc4/{orcid}/email
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Create an Employment
```
POST /v2.0_rc4/{orcid}/employment
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Employment||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Employment created, see HTTP Location header for URI|No Content|
|400|Invalid Employment representation|string|
|500|Invalid Employment representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch an Employment Summary
```
GET /v2.0_rc4/{orcid}/employment/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|EmploymentSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch an Employment
```
GET /v2.0_rc4/{orcid}/employment/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Employment|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Update an Employment
```
PUT /v2.0_rc4/{orcid}/employment/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Employment||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Employment updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Delete an Employment
```
DELETE /v2.0_rc4/{orcid}/employment/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Employment deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch all employments
```
GET /v2.0_rc4/{orcid}/employments
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Employments|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch external identifiers
```
GET /v2.0_rc4/{orcid}/external-identifiers
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc4

### Add external identifier
```
POST /v2.0_rc4/{orcid}/external-identifiers
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|PersonExternalIdentifier||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch external identifier
```
GET /v2.0_rc4/{orcid}/external-identifiers/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc4

### Edit external identifier
```
PUT /v2.0_rc4/{orcid}/external-identifiers/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|PersonExternalIdentifier||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Delete external identifier
```
DELETE /v2.0_rc4/{orcid}/external-identifiers/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Create a Funding
```
POST /v2.0_rc4/{orcid}/funding
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Funding||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Funding created, see HTTP Location header for URI|No Content|
|400|Invalid Funding representation|string|
|500|Invalid Funding representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch a Funding Summary
```
GET /v2.0_rc4/{orcid}/funding/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|FundingSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch a Funding
```
GET /v2.0_rc4/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Funding|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Update a Funding
```
PUT /v2.0_rc4/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Funding||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Funding updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Delete a Funding
```
DELETE /v2.0_rc4/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Funding deleted|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch all fundings
```
GET /v2.0_rc4/{orcid}/fundings
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Fundings|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch keywords
```
GET /v2.0_rc4/{orcid}/keywords
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc4

### Add keyword
```
POST /v2.0_rc4/{orcid}/keywords
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Keyword||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch keyword
```
GET /v2.0_rc4/{orcid}/keywords/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc4

### Edit keyword
```
PUT /v2.0_rc4/{orcid}/keywords/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Keyword||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Delete keyword
```
DELETE /v2.0_rc4/{orcid}/keywords/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Add a notification
```
POST /v2.0_rc4/{orcid}/notification-permission
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|NotificationPermission||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Notification added, see HTTP Location header for URI|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch a notification by id
```
GET /v2.0_rc4/{orcid}/notification-permission/{id}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|id||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Notification found|Notification|
|401|Access denied, this is not your notification|string|
|404|Notification not found|string|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Archive a notification
```
DELETE /v2.0_rc4/{orcid}/notification-permission/{id}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|id||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Notification archived|Notification|
|401|Access denied, this is not your notification|string|
|404|Notification not found|string|


#### Consumes

* */*

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch Other names
```
GET /v2.0_rc4/{orcid}/other-names
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc4

### Add other name
```
POST /v2.0_rc4/{orcid}/other-names
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|OtherName||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch Other name
```
GET /v2.0_rc4/{orcid}/other-names/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc4

### Edit other name
```
PUT /v2.0_rc4/{orcid}/other-names/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|OtherName||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Delete other name
```
DELETE /v2.0_rc4/{orcid}/other-names/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Create a Peer Review
```
POST /v2.0_rc4/{orcid}/peer-review
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|PeerReview||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Peer Review created, see HTTP Location header for URI|No Content|
|400|Invalid Peer Review representation|string|
|500|Invalid Peer Review representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch a Peer Review Summary
```
GET /v2.0_rc4/{orcid}/peer-review/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|PeerReviewSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch a Peer Review
```
GET /v2.0_rc4/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|PeerReview|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Update a Peer Review
```
PUT /v2.0_rc4/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|PeerReview||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Peer Review updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Delete a Peer Review
```
DELETE /v2.0_rc4/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Peer Review deleted|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch all peer reviews
```
GET /v2.0_rc4/{orcid}/peer-reviews
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|PeerReviews|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch person details
```
GET /v2.0_rc4/{orcid}/person
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc4

### Fetch personal details for an ORCID ID
```
GET /v2.0_rc4/{orcid}/personal-details
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch all researcher urls for an ORCID ID
```
GET /v2.0_rc4/{orcid}/researcher-urls
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Add a new researcher url for an ORCID ID
```
POST /v2.0_rc4/{orcid}/researcher-urls
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|ResearcherUrl||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch one researcher url for an ORCID ID
```
GET /v2.0_rc4/{orcid}/researcher-urls/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc4

### Edits researcher url for an ORCID ID
```
PUT /v2.0_rc4/{orcid}/researcher-urls/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|ResearcherUrl||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Delete one researcher url from an ORCID ID
```
DELETE /v2.0_rc4/{orcid}/researcher-urls/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Create a Work
```
POST /v2.0_rc4/{orcid}/work
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|Work||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|Work created, see HTTP Location header for URI|No Content|
|400|Invalid Work representation|string|
|500|Invalid Work representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch a Work Summary
```
GET /v2.0_rc4/{orcid}/work/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|WorkSummary|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch a Work
```
GET /v2.0_rc4/{orcid}/work/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Work|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Update a Work
```
PUT /v2.0_rc4/{orcid}/work/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||
|BodyParameter|body||false|Work||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Work updated|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Delete a Work
```
DELETE /v2.0_rc4/{orcid}/work/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|204|Work deleted|No Content|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch all works
```
GET /v2.0_rc4/{orcid}/works
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Works|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Create a listo of Work
```
POST /v2.0_rc4/{orcid}/works
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|BodyParameter|body||false|WorkBulk||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|
|201|At least one of the works was created|No Content|
|400|Invalid Work representation|string|
|500|Invalid Work representation that wasn't trapped (bad fuzzy date or you tried to add a put code)|string|


#### Consumes

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Member API v2.0_rc4

### Fetch record details
```
GET /v2.0_rc4/{orcid}{ignore}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|default|successful operation|No Content|


#### Tags

* Member API v2.0_rc4

