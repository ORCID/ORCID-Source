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

* Public API v2.0

### Fetch identifier type map.  Defaults to English descriptions
```
GET /v2.0/identifiers
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|QueryParameter|locale||false|string||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200||No Content|


#### Produces

* application/json
* application/xml

#### Tags

* Identifier API v2.0

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

* Public API v2.0

### Check the server status
```
GET /v2.0/status
```

#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|


#### Produces

* text/plain

#### Tags

* Public API v2.0

### Fetch all Activities
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

* Public API v2.0

### Fetch all addresses
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

* Public API v2.0

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

* Public API v2.0

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

* Public API v2.0

### Fetch an Education Summary
```
GET /v2.0/{orcid}/education/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0

### Fetch an Education
```
GET /v2.0/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Education|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Public API v2.0

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

* Public API v2.0

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

* Public API v2.0

### Fetch an Employment Summary
```
GET /v2.0/{orcid}/employment/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0

### Fetch an Employment
```
GET /v2.0/{orcid}/employment/{putCode}
```

#### Description

Retrive a specific education representation

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Employment found|Employment|
|404|Employment not found|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Public API v2.0

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
|200|successful operation|Fundings|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Public API v2.0

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

* Public API v2.0

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

* Public API v2.0

### Fetch a Funding Summary
```
GET /v2.0/{orcid}/funding/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0

### Fetch a Funding
```
GET /v2.0/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0

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

* Public API v2.0

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

* Public API v2.0

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

* Public API v2.0

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

* Public API v2.0

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

* Public API v2.0

### Fetch a Peer Review Summary
```
GET /v2.0/{orcid}/peer-review/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0

### Fetch a Peer Review
```
GET /v2.0/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0

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

* Public API v2.0

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

* Public API v2.0

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

* Public API v2.0

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

* Public API v2.0

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


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Public API v2.0

### Fetch a Work Summary
```
GET /v2.0/{orcid}/work/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0

### Fetch a Work
```
GET /v2.0/{orcid}/work/{putCode}
```

#### Description

More notes about this method

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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
* application/vnd.citationstyles.csl+json

#### Tags

* Public API v2.0

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

* Public API v2.0

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

* Public API v2.0

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

* Public API v2.0

### Fetch latest statistics summary
```
GET /v2.0_rc1/statistics
```

#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Statistic found|StatisticsSummary|
|404|Statistic not found|No Content|


#### Produces

* application/json
* application/xml

#### Tags

* Statistics API v2.0_rc1

### Fetch a time series for all statistics
```
GET /v2.0_rc1/statistics/all
```

#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Statistic found|StatsTimelineList|
|404|Statistic not found|No Content|


#### Produces

* application/json
* text/csv

#### Tags

* Statistics API v2.0_rc1

### Fetch a time series for a given statistic
```
GET /v2.0_rc1/statistics/{type}
```

#### Description

Valid statistic types can be inferred from the /statistics resource.  e.g. 'works'

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|type||true|enum (liveIds, idsWithVerifiedEmail, idsWithWorks, works, worksWithDois, uniqueDois, employment, employmentUniqueOrg, education, educationUniqueOrg, funding, fundingUniqueOrg)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Statistic found|StatisticsTimeline|
|404|Statistic not found|No Content|


#### Produces

* application/json
* application/xml

#### Tags

* Statistics API v2.0_rc1

### Check the server status
```
GET /v2.0_rc1/status
```

#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|


#### Produces

* text/plain

#### Tags

* Public API v2.0_rc1

### Fetch all Activities
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

* Public API v2.0_rc1

### Fetch an Education Summary
```
GET /v2.0_rc1/{orcid}/education/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc1

### Fetch an Education
```
GET /v2.0_rc1/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Education|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Public API v2.0_rc1

### Fetch an Employment Summary
```
GET /v2.0_rc1/{orcid}/employment/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc1

### Fetch an Employment
```
GET /v2.0_rc1/{orcid}/employment/{putCode}
```

#### Description

Retrive a specific education representation

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Employment found|Employment|
|404|Employment not found|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Public API v2.0_rc1

### Fetch a Funding Summary
```
GET /v2.0_rc1/{orcid}/funding/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc1

### Fetch a Funding
```
GET /v2.0_rc1/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc1

### Fetch a Peer Review Summary
```
GET /v2.0_rc1/{orcid}/peer-review/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc1

### Fetch a Peer Review
```
GET /v2.0_rc1/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc1

### Fetch a Work Summary
```
GET /v2.0_rc1/{orcid}/work/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc1

### Fetch a Work
```
GET /v2.0_rc1/{orcid}/work/{putCode}
```

#### Description

More notes about this method

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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
* application/vnd.citationstyles.csl+json

#### Tags

* Public API v2.0_rc1

### Check the server status
```
GET /v2.0_rc2/status
```

#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|


#### Produces

* text/plain

#### Tags

* Public API v2.0_rc2

### Fetch all Activities
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

* Public API v2.0_rc2

### Fetch all addresses
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

* Public API v2.0_rc2

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

* Public API v2.0_rc2

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

* Public API v2.0_rc2

### Fetch an Education Summary
```
GET /v2.0_rc2/{orcid}/education/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc2

### Fetch an Education
```
GET /v2.0_rc2/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Education|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Public API v2.0_rc2

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

* Public API v2.0_rc2

### Fetch an Employment Summary
```
GET /v2.0_rc2/{orcid}/employment/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc2

### Fetch an Employment
```
GET /v2.0_rc2/{orcid}/employment/{putCode}
```

#### Description

Retrive a specific education representation

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Employment found|Employment|
|404|Employment not found|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Public API v2.0_rc2

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

* Public API v2.0_rc2

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

* Public API v2.0_rc2

### Fetch a Funding Summary
```
GET /v2.0_rc2/{orcid}/funding/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc2

### Fetch a Funding
```
GET /v2.0_rc2/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc2

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

* Public API v2.0_rc2

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

* Public API v2.0_rc2

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

* Public API v2.0_rc2

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

* Public API v2.0_rc2

### Fetch a Peer Review Summary
```
GET /v2.0_rc2/{orcid}/peer-review/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc2

### Fetch a Peer Review
```
GET /v2.0_rc2/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc2

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

* Public API v2.0_rc2

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

* Public API v2.0_rc2

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

* Public API v2.0_rc2

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


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Public API v2.0_rc2

### Fetch a Work Summary
```
GET /v2.0_rc2/{orcid}/work/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc2

### Fetch a Work
```
GET /v2.0_rc2/{orcid}/work/{putCode}
```

#### Description

More notes about this method

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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
* application/vnd.citationstyles.csl+json

#### Tags

* Public API v2.0_rc2

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

* Public API v2.0_rc2

### Check the server status
```
GET /v2.0_rc3/status
```

#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|


#### Produces

* text/plain

#### Tags

* Public API v2.0_rc3

### Fetch all Activities
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

* Public API v2.0_rc3

### Fetch all addresses
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

* Public API v2.0_rc3

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

* Public API v2.0_rc3

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

* Public API v2.0_rc3

### Fetch an Education Summary
```
GET /v2.0_rc3/{orcid}/education/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc3

### Fetch an Education
```
GET /v2.0_rc3/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Education|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Public API v2.0_rc3

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

* Public API v2.0_rc3

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

* Public API v2.0_rc3

### Fetch an Employment Summary
```
GET /v2.0_rc3/{orcid}/employment/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc3

### Fetch an Employment
```
GET /v2.0_rc3/{orcid}/employment/{putCode}
```

#### Description

Retrive a specific education representation

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Employment found|Employment|
|404|Employment not found|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Public API v2.0_rc3

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
|200|successful operation|Fundings|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Public API v2.0_rc3

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

* Public API v2.0_rc3

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

* Public API v2.0_rc3

### Fetch a Funding Summary
```
GET /v2.0_rc3/{orcid}/funding/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc3

### Fetch a Funding
```
GET /v2.0_rc3/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc3

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

* Public API v2.0_rc3

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

* Public API v2.0_rc3

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

* Public API v2.0_rc3

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

* Public API v2.0_rc3

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

* Public API v2.0_rc3

### Fetch a Peer Review Summary
```
GET /v2.0_rc3/{orcid}/peer-review/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc3

### Fetch a Peer Review
```
GET /v2.0_rc3/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc3

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

* Public API v2.0_rc3

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

* Public API v2.0_rc3

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

* Public API v2.0_rc3

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

* Public API v2.0_rc3

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


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Public API v2.0_rc3

### Fetch a Work Summary
```
GET /v2.0_rc3/{orcid}/work/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc3

### Fetch a Work
```
GET /v2.0_rc3/{orcid}/work/{putCode}
```

#### Description

More notes about this method

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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
* application/vnd.citationstyles.csl+json

#### Tags

* Public API v2.0_rc3

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

* Public API v2.0_rc3

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

* Public API v2.0_rc3

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

* Public API v2.0_rc4

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

* Public API v2.0_rc4

### Check the server status
```
GET /v2.0_rc4/status
```

#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|string|


#### Produces

* text/plain

#### Tags

* Public API v2.0_rc4

### Fetch all Activities
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

* Public API v2.0_rc4

### Fetch all addresses
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

* Public API v2.0_rc4

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

* Public API v2.0_rc4

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

* Public API v2.0_rc4

### Fetch an Education Summary
```
GET /v2.0_rc4/{orcid}/education/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc4

### Fetch an Education
```
GET /v2.0_rc4/{orcid}/education/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|successful operation|Education|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Public API v2.0_rc4

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

* Public API v2.0_rc4

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

* Public API v2.0_rc4

### Fetch an Employment Summary
```
GET /v2.0_rc4/{orcid}/employment/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc4

### Fetch an Employment
```
GET /v2.0_rc4/{orcid}/employment/{putCode}
```

#### Description

Retrive a specific education representation

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


#### Responses
|HTTP Code|Description|Schema|
|----|----|----|
|200|Employment found|Employment|
|404|Employment not found|No Content|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Public API v2.0_rc4

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
|200|successful operation|Fundings|


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Public API v2.0_rc4

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

* Public API v2.0_rc4

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

* Public API v2.0_rc4

### Fetch a Funding Summary
```
GET /v2.0_rc4/{orcid}/funding/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc4

### Fetch a Funding
```
GET /v2.0_rc4/{orcid}/funding/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc4

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

* Public API v2.0_rc4

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

* Public API v2.0_rc4

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

* Public API v2.0_rc4

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

* Public API v2.0_rc4

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

* Public API v2.0_rc4

### Fetch a Peer Review Summary
```
GET /v2.0_rc4/{orcid}/peer-review/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc4

### Fetch a Peer Review
```
GET /v2.0_rc4/{orcid}/peer-review/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc4

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

* Public API v2.0_rc4

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

* Public API v2.0_rc4

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

* Public API v2.0_rc4

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

* Public API v2.0_rc4

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


#### Produces

* application/vnd.orcid+xml; qs=5
* application/orcid+xml; qs=3
* application/xml
* application/vnd.orcid+json; qs=4
* application/orcid+json; qs=2
* application/json

#### Tags

* Public API v2.0_rc4

### Fetch a Work Summary
```
GET /v2.0_rc4/{orcid}/work/summary/{putCode}
```

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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

* Public API v2.0_rc4

### Fetch a Work
```
GET /v2.0_rc4/{orcid}/work/{putCode}
```

#### Description

More notes about this method

#### Parameters
|Type|Name|Description|Required|Schema|Default|
|----|----|----|----|----|----|
|PathParameter|orcid||true|string||
|PathParameter|putCode||true|integer (int64)||


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
* application/vnd.citationstyles.csl+json

#### Tags

* Public API v2.0_rc4

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

* Public API v2.0_rc4

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

* Public API v2.0_rc4

